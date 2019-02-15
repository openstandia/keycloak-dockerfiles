<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
	response.setHeader("Expires", "-1");
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-Control","no-cache");
%>

<%@ page import="org.keycloak.KeycloakSecurityContext" %>
<%@ page import="org.keycloak.adapters.RefreshableKeycloakSecurityContext" %>
<%@ page import="org.keycloak.representations.IDToken" %>

<html>

<head>
<title>Authz App</title>

<link rel="stylesheet" href="/authz-app/css/bootstrap.min.css" >
<link rel="stylesheet" href="/authz-app/css/bootstrap-theme.min.css">
<script src="/authz-app/js/jquery-3.3.1.min.js" type="text/javascript" ></script>
<script src="/authz-app/js/bootstrap.min.js"></script>

</head>

<%
	RefreshableKeycloakSecurityContext keycloakContext = (RefreshableKeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
	IDToken idToken = keycloakContext.getIdToken();

	String authServerBaseUrl = keycloakContext.getDeployment().getAuthServerBaseUrl();
	String realm = keycloakContext.getRealm();
	String logoutEndpoint = authServerBaseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
%>

<body>
<div class="container-fluid">
	<h3>ログインユーザー情報</h3>
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
		<a href="<%=logoutEndpoint%>?redirect_uri=https%3A%2F%2Fauthz.example.com%2Fauthz-app%2F" class="btn btn-default active" role="button">ログアウト</a>
	</div>

	<h3>アクセス制限チェック</h3>
	<div class="container-fluid">
	現在のアクセスしているURI : <b><%=request.getRequestURI()%></b>

	<table class="table table-striped table-hover">
		<tr>
			<th>パス</th>
			<th>条件判定</th>
			<th>アクセス</th>
		</tr>
		<tr>
			<td>/authz-app/</td>
			<td>・認証済み</td>
			<td><a href="/authz-app/" class="btn btn-primary btn-default active" role="button">view</a></td>
		</tr>
		<tr>
			<td>/authz-app/role/admin/</td>
			<td>・"admin" ロールのみ</td>
			<td><a href="/authz-app/role/admin/" class="btn btn-primary btn-default active" role="button">view</a></td>
		</tr>
		<tr>
			<td>/authz-app/user/user001/</td>
			<td>・"user001" のみ</td>
			<td><a href="/authz-app/user/user001/" class="btn btn-primary btn-default active" role="button">view</a></td>
		</tr>
		<tr>
			<td>/authz-app/group/openstandia/</td>
			<td>・"openstandia" グループのみ</td>
			<td><a href="/authz-app/group/openstandia/" class="btn btn-primary btn-default active" role="button">view</a></td>
		</tr>
		<tr>
			<td>/authz-app/attr/test/</td>
			<td>・メールのドメインが "@test.example.com" のユーザのみ</td>
			<td><a href="/authz-app/attr/test/" class="btn btn-primary btn-default active" role="button" >view</a></td>
		</tr>
		<tr>
			<td>/authz-app/time/lunch/</td>
			<td>・ランチタイム(12:00 - 12:59)のみ</td>
			<td><a href="/authz-app/time/lunch/" class="btn btn-primary btn-default active" role="button">view</a></td>
		</tr>
	</table>
	</div>

	<h3>別の認可サービス</h3>
	<div class="container-fluid">
		<a href="https://uma.example.com/authz-uma-client/"  class="btn btn-primary btn-default active" role="button">UMA 方式</a>
	</div>

</div>
</body>
</html>
