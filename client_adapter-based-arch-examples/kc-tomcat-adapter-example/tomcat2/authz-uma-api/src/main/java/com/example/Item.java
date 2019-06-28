package com.example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

/**
 * Root resource (exposed at "items" path)
 */
@Path("items")
public class Item {

	public static final String URI_PREFIX = "/api/items/";

	public static final String SCOPE_ITEM_UPDATE = "item:update";
	public static final String SCOPE_ITEM_VIEW   = "item:view";
	public static final String SCOPE_ITEM_DELETE = "item:delete";

	private static final String MESSAGE_RESOURCE_NOT_EXIST = "リソース（'%s）' が存在しません！";

	@Context
	private HttpServletRequest servletRequest;

	@Context
	private ServletContext servletContext;


	private static Map<String, ItemDetail> database = new HashMap<>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ItemDisplayBean> getResources() {

		String[] resourceIds = getAuthzClient().protection().resource().findAll();
		Map<String, Permission> permissionMap = getPermissionMap();

		List<ItemDisplayBean> resourceList = new ArrayList<>();
		for (String rid : resourceIds) {

			ResourceRepresentation resource = getAuthzClient().protection().resource().findById(rid);

			if (resource.getOwnerManagedAccess()) {

				Set<String> uris = resource.getUris();
				for (String url : uris) {
					ItemDisplayBean itemDisplayBean = new ItemDisplayBean(resource.getName(), url);

					// リソースのオーナーかどうかチェック
					itemDisplayBean
							.setOwner(resource.getOwner().getId().equals(servletRequest.getUserPrincipal().getName()));

					ItemDetail detail = database.get(itemDisplayBean.subject);
					if (detail != null) {
						// introspectRPT の応答にはユーザーIDが含まれていないため、DBからユーザーIDを取得
						itemDisplayBean.setOwnerName(detail.createUserId);
					}

					Permission permission = permissionMap.get(resource.getId());
					if (permission != null) {
						// 当該リソースに対して、各スコープのパーミッションがあるかチェック
						itemDisplayBean.setViewable(permission.getScopes().contains(SCOPE_ITEM_VIEW));
						itemDisplayBean.setUpdatable(permission.getScopes().contains(SCOPE_ITEM_UPDATE));
						itemDisplayBean.setDeletable(permission.getScopes().contains(SCOPE_ITEM_DELETE));
					} else {
						itemDisplayBean.setViewable(false);
						itemDisplayBean.setUpdatable(false);
						itemDisplayBean.setDeletable(false);
					}

					resourceList.add(itemDisplayBean);
				}

			}
		}

		return resourceList;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createResource(@QueryParam("name") String name, @QueryParam("memo") String memo) {

		String uuid = UUID.randomUUID().toString();
		String subject = servletRequest.getUserPrincipal().getName();
		String createUserId = getKeycloakSecurityContext().getToken().getPreferredUsername();

		ItemDetail item = getItemDetail(uuid);
		item.id = uuid;
		item.name = name;
		item.memo = memo;
		SimpleDateFormat tokyoSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		tokyoSdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		item.createDate = tokyoSdf.format(new Date(System.currentTimeMillis()));
		item.createUserId = createUserId;
		item.resultMessage = "リソースが作成されました！";
		database.put(uuid, item);

		// (1) リソースで利用できる全てのスコープを HashSet に追加
		HashSet<ScopeRepresentation> scopes = new HashSet<>();
		scopes.add(new ScopeRepresentation(SCOPE_ITEM_VIEW));
		scopes.add(new ScopeRepresentation(SCOPE_ITEM_UPDATE));
		scopes.add(new ScopeRepresentation(SCOPE_ITEM_DELETE));


		// (2) ResourceRepresentation インスタンスの生成
		String uri = URI_PREFIX + uuid;
		ResourceRepresentation newResource = new ResourceRepresentation(name, scopes, uri,
						"urn:authz-uma-api:resources:item");
		newResource.setOwner(subject);
		newResource.setOwnerManagedAccess(true);

		// (3) 既に同一のリソースが作成されていないかチェック
		ResourceRepresentation resource = getResourceRepresentationByName(name, subject);
		if ( resource != null) {
			item.resultMessage = "'" + name + "' は既に作成済みです！";
			return Response.status(Response.Status.CONFLICT).entity(item).build();
		}

		// (4) リソースの作成(Protection API 経由)
		getAuthzClient().protection().resource().create(newResource);

		return Response.status(Response.Status.CREATED).entity(item).build();

	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewResource(@PathParam("id") String id) {

		ItemDetail item = getItemDetail(id);

		ResourceRepresentation resource = getResourceRepresentation(id);
		if (resource == null) {
			item.resultMessage = String.format(MESSAGE_RESOURCE_NOT_EXIST, id);
			return Response.status(Response.Status.NOT_FOUND).entity(item).build();
		}
		item.resultMessage = "'" + item.name + "' が参照されました！";

		return Response.ok(item).build();

	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateResource(@PathParam("id") String id, @QueryParam("detailMemo") String detailMemo) {

		String updateUserId = getKeycloakSecurityContext().getToken().getPreferredUsername();

		ItemDetail item = getItemDetail(id);
		if (detailMemo != null) {
			item.memo = detailMemo;
			SimpleDateFormat tokyoSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			tokyoSdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			item.updateDate = tokyoSdf.format(new Date(System.currentTimeMillis()));
			item.updateUserId = updateUserId;
			item.resultMessage = "'" + item.name + "' が更新されました！";
			database.put(id, item);
		}

		ResourceRepresentation resource = getResourceRepresentation(id);
		if (resource == null) {
			item.resultMessage = String.format(MESSAGE_RESOURCE_NOT_EXIST, id);
			return Response.status(Response.Status.NOT_FOUND).entity(item).build();
		}

		return Response.ok(item).build();

	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteResource(@PathParam("id") String id) {

		ItemDetail item = getItemDetail(id);
		item.resultMessage = "'" + item.name + "' が削除されました！";
		database.remove(id);

		// (1) リソース検索(Protection API 経由)
		List<ResourceRepresentation> search = getResourceRepresentations(id);

		// (2) 既にリソースが削除されていないかチェック
		if (search.isEmpty()) {
			item.resultMessage = "このリソースは既に削除されています！";
			return Response.status(Response.Status.NOT_FOUND).entity(item).build();
		}

		// (3) リソースの削除(AProtection API 経由)
		ResourceRepresentation resource = search.get(0);
		getAuthzClient().protection().resource().delete(resource.getId());

		return Response.ok(item).build();

	}

	private ResourceRepresentation getResourceRepresentation(String id) {

		List<ResourceRepresentation> search = getResourceRepresentations(id);
		if (search.isEmpty()) {
			return null;
		}
		return search.get(0);

	}

	private List<ResourceRepresentation> getResourceRepresentations(String id) {

		// リソース検索(Authorization Client API 経由)
		return getAuthzClient().protection().resource().findByUri(URI_PREFIX + id);
	}

	private ResourceRepresentation getResourceRepresentationByName(String name, String ownerId) {

		// リソース検索(Authorization Client API 経由)
		return getAuthzClient().protection().resource().findByName(name, ownerId);
	}

	private List<Permission> introspectRPT() {

		// RPT 取得(ユーザーのアクセストークンで認可レスポンスの取得)
		AuthorizationResponse response = getAuthzClient().authorization(getKeycloakSecurityContext().getTokenString())
				.authorize();
		String rpt = response.getToken();

		// RPT 検証
		TokenIntrospectionResponse requestingPartyToken = getAuthzClient().protection()
				.introspectRequestingPartyToken(rpt);

		return requestingPartyToken.getPermissions();

	}

	private Map<String, Permission> getPermissionMap() {

		Map<String, Permission> permissionMap = new HashMap<>();
		// RPT のうちパーミッションのリストのみ JSON で送信
		for (Permission permission : introspectRPT()) {
			permissionMap.put(permission.getResourceId(), permission);
		}
		return permissionMap;

	}

	private AuthzClient getAuthzClient() {
		return getAuthorizationContext().getClient();
	}

	private ClientAuthorizationContext getAuthorizationContext() {
		return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext().getAuthorizationContext());
	}

	private KeycloakSecurityContext getKeycloakSecurityContext() {
		return KeycloakSecurityContext.class.cast(servletRequest.getAttribute(KeycloakSecurityContext.class.getName()));
	}

	private ItemDetail getItemDetail(String id) {

		ItemDetail item = database.get(id);
		if (item == null) {
			item = new ItemDetail();
		}
		return item;

	}
}
