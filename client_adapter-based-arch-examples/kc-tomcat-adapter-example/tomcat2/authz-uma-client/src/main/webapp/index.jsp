<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="org.keycloak.KeycloakSecurityContext" %>
<%@ page import="org.keycloak.adapters.RefreshableKeycloakSecurityContext" %>
<%@ page import="org.keycloak.authorization.client.ClientAuthorizationContext" %>
<%@ page import="org.keycloak.representations.IDToken" %>

<html>

<head>
<title>Authz UMA Client</title>

<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<script src="js/jquery-3.3.1.min.js" type="text/javascript" ></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/uma-client.js"type="text/javascript" ></script>

</head>

<%
	response.setHeader("Expires", "-1");
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-Control","no-cache");

	RefreshableKeycloakSecurityContext keycloakContext = (RefreshableKeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
	IDToken idToken = keycloakContext.getIdToken();

	String authServerBaseUrl = keycloakContext.getDeployment().getAuthServerBaseUrl();
	String realm = keycloakContext.getRealm();
	String logoutEndpoint = authServerBaseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
%>

<body onLoad="displayResources()">
<div class="container-fluid">

	<h3>ログインユーザ情報</h3>
	<div class="container-fluid">
	<table class="table table-striped">
		<tr>
			<th align="left">内部ID</th>
			<td><%=idToken.getSubject()%></td>
		</tr>
		<tr>
			<th align="left">ユーザID</th>
			<td><%=idToken.getPreferredUsername()%></td>
		</tr>
		<tr>
			<th align="left">メールアドレス</th>
			<td><%=idToken.getEmail()%></td>
		</tr>
	</table>
	</div>

	<h3>Keycloak 操作</h3>
	<div class="container-fluid">
		<a href="<%=authServerBaseUrl%>/realms/<%=realm%>/account/resource?referrer=authz-uma-client&referrer_uri=https%3A%2F%2Fuma.example.com%2Fauthz-uma-client%2F"  class="btn btn-primary btn-default active" role="button">マイリソース</a>
		<a href="<%=logoutEndpoint%>?redirect_uri=https%3A%2F%2Fuma.example.com%2Fauthz-uma-client%2F" class="btn btn-default active" role="button">ログアウト</a>
	</div>

	<h3>UMA 操作</h3>
	<div class="container-fluid">
		<a href="#" onClick="submit('?name=<%=idToken.getPreferredUsername()%>', 'POST')" class="btn btn-primary btn-default active" role="button">'<%=idToken.getPreferredUsername()%> Item' の作成</a>
		<a href="#" onClick="introspectRPT()" class="btn btn-primary btn-default active" role="button">現在のアクセス権の確認</a>
	</div>

	<h3>UMA リソースアクセス</h3>
	<div class="container-fluid">
		<div class="resourcesTable" ></div>
	</div>

	<h3>結果コンソール</h3>
	<div class="container-fluid">
		<textarea class="resultConsole" cols="100" rows="10" wrap="off"></textarea>
	</div>

	<h3>別の認可サービス</h3>
	<div class="container-fluid">
	<a href="https://authz.example.com/authz-app/"  class="btn btn-primary btn-default active" role="button">集中管理方式</a>
	</div>

</div>
</body>
</html>
