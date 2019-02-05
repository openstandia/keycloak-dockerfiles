package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PermissionRequest;
import org.keycloak.representations.idm.authorization.PermissionResponse;
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


    @Context
    private HttpServletRequest servletRequest;

    @Context
    private ServletContext servletContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDisplayBean> getResources() {

    	String[] resourceIds = getAuthzClient().protection().resource().findAll();
    	Map<String, Permission> permissionMap = getPermissionMap();

    	List<ItemDisplayBean> resourceList = new ArrayList<ItemDisplayBean>();
    	for (String rid : resourceIds) {

    		ResourceRepresentation resource = getAuthzClient().protection().resource().findById(rid);

    		if (resource.getOwnerManagedAccess()) {

    			Iterator<String> it = resource.getUris().iterator();
    			while(it.hasNext()) {
    				ItemDisplayBean itemDisplayBean = new ItemDisplayBean(resource.getName(), it.next());

    				// リソースのオーナーかどうかチェック
        			itemDisplayBean.setOwner(resource.getOwner().getId().equals(servletRequest.getUserPrincipal().getName()));

        			Permission permission = permissionMap.get(resource.getName());
        			if (permission != null) {
            			// 当該リソースに対して、各スコープのアクセス権があるかチェック
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
    @Produces(MediaType.TEXT_PLAIN)
    public String createResource(@QueryParam("name") String name) {

    	String uuid = UUID.randomUUID().toString();
    	String subject = servletRequest.getUserPrincipal().getName();

    	// リソースで利用できる全てのスコープを HashSet に追加
    	HashSet<ScopeRepresentation> scopes = new HashSet<ScopeRepresentation>();
    	scopes.add(new ScopeRepresentation(SCOPE_ITEM_VIEW));
    	scopes.add(new ScopeRepresentation(SCOPE_ITEM_UPDATE));
    	scopes.add(new ScopeRepresentation(SCOPE_ITEM_DELETE));

    	// リソース定義
    	String uri = URI_PREFIX + uuid;
    	String resourceName = name + " Item";
    	ResourceRepresentation newResource =
    			new ResourceRepresentation(
    					resourceName,
    					scopes,
    					uri ,
    					"urn:authz-uma-api:resources:item");
    	newResource.setOwner(subject);
    	newResource.setOwnerManagedAccess(true);

    	// 既に同一のリソースが作成されていないかチェック
    	ResourceRepresentation resource = getResourceRepresentationByName(resourceName, subject);
    	if ( resource != null) {
            return "'" + resourceName + "' は既に作成済みです！";
    	}

    	// リソース作成(Authorization Client API 経由)
    	getAuthzClient().protection().resource().create(newResource);

    	return "'" + resourceName + "' が作成されました！";

    }

    @GET @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String viewResource(@PathParam("id") String id) {

    	ResourceRepresentation resource = getResourceRepresentation(id);

    	return "'" + resource.getName() + "' が参照されました！";

    }

    @PUT @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateResource(@PathParam("id") String id) {

    	ResourceRepresentation resource = getResourceRepresentation(id);

    	return "'" + resource.getName() + "' が更新されました！";

    }

    @DELETE @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteResource(@PathParam("id") String id) {

    	// リソース検索(Authorization Client API 経由)
    	List<ResourceRepresentation> search = getResourceRepresentations(id);

    	// 既にリソースが削除されていないかチェック
    	if ( search.size() == 0 ) {
            return "このリソースは既に削除されています！";
    	}

    	ResourceRepresentation resource = search.get(0);

    	// リソース削除(Authorization Client API 経由)
    	getAuthzClient().protection().resource().delete(resource.getId());
    	return "'" + resource.getName() + "' が削除されました！";

    }

    private ResourceRepresentation getResourceRepresentation(String id) {

    	if (getResourceRepresentations(id).size() == 0) {
    		return null;
    	}
    	return getResourceRepresentations(id).get(0);

    }

    private List<ResourceRepresentation> getResourceRepresentations(String id) {

    	// リソース検索(Authorization Client API 経由)
    	List<ResourceRepresentation> search = getAuthzClient().protection().resource().findByUri(URI_PREFIX + id);

    	return search;
    }

    private ResourceRepresentation getResourceRepresentationByName(String name, String ownerId) {

    	// リソース検索(Authorization Client API 経由)
    	ResourceRepresentation resource = getAuthzClient().protection().resource().findByName(name, ownerId);

    	return resource;
    }

    @GET @Path("/requestScope")
    @Produces(MediaType.TEXT_PLAIN)
    public String requestScope(@QueryParam("id") String id, @QueryParam("scope") String scope) {

    	// アクセス権要求チケット取得(Authorization Client API 経由)
    	ResourceRepresentation resource = getResourceRepresentation(id);
    	PermissionRequest permissionReq = new PermissionRequest(resource.getId(), "item:" + scope);
    	PermissionResponse permissionRes = getAuthzClient().protection().permission().create(permissionReq);

    	boolean requested = false;
    	AuthorizationRequest authorizationReq = new AuthorizationRequest(permissionRes.getTicket());
    	requested = false;
    	try {
        	// 認可リクエスト送信(Authorization Client API 経由)
    		getAuthzClient().authorization(getKeycloakSecurityContext().getTokenString()).authorize(authorizationReq);
    		requested = true;
    	} catch(AuthorizationDeniedException e) {
    		Throwable t = e.getCause();
    		if (t instanceof HttpResponseException) {
    			if (((HttpResponseException)t).getStatusCode() == 403) {
    				requested = true;
    			}
    		}
    	}

    	if (requested) {
    		return "'" + resource.getName() + "' に対して、'item:" + scope + "' スコープを要求しました！";
    	} else {
    		return "リクエスト失敗！";
    	}

    }

    @GET @Path("/introspectRPT")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Permission> introspectRPT() {

    	// RPT 取得(ユーザーのアクセストークンで認可レスポンスの取得)
    	AuthorizationResponse response = getAuthzClient().authorization(getKeycloakSecurityContext().getTokenString()).authorize();
    	String rpt = response.getToken();

    	// RPT 検証
    	TokenIntrospectionResponse requestingPartyToken = getAuthzClient().protection().introspectRequestingPartyToken(rpt);

    	return requestingPartyToken.getPermissions();

    }

    private Map<String, Permission> getPermissionMap() {

    	Map<String, Permission> permissionMap = new HashMap<String, Permission>();
    	// RPT のうちアクセス権のリストのみ JSON で送信
    	for (Permission permission : introspectRPT()) {
    		permissionMap.put(permission.getResourceName(), permission);
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
}
