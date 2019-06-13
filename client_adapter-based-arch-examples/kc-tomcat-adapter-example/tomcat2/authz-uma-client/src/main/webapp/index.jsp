<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
  response.setHeader("Expires", "-1");
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
%>

<html>

<head>
<title>Authz UMA Client</title>

<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<script src="https://sso.example.com/auth/js/keycloak.js"></script>
<script src="https://sso.example.com/auth/js/keycloak-authz.js"></script>
<script src="js/jquery-3.3.1.min.js" type="text/javascript" ></script>
<script src="js/jwt-decode.min.js" type="text/javascript" ></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/uma-client.js"type="text/javascript" ></script>


</head>

<body>
<div class="container-fluid">

	<h3>ログインユーザー情報</h3>
	<div class="container-fluid">
	<table class="table table-striped">
		<tr>
			<th align="left">内部ID</th>
			<td><data id="subject"></data></td>
		</tr>
		<tr>
			<th align="left">ユーザID</th>
			<td><data id="username"></data></td>
		</tr>
		<tr>
			<th align="left">メールアドレス</th>
			<td><data id="email"></data></td>
		</tr>
	</table>
	</div>

	<h3>Keycloak 操作</h3>
	<div class="container-fluid">
		<a href="javascript: location.href=keycloak.createAccountUrl().replace('account','account/resource');"  class="btn btn-primary btn-default active" role="button">マイリソース</a>
		<a href="javascript: keycloak.logout();" class="btn btn-default active" role="button">ログアウト</a>
	</div>

	<h3>UMA 操作</h3>
	<div class="container-fluid">
		<a href="#" onClick="create()" class="btn btn-primary btn-default active" role="button">'<data id="itemName"></data>' の作成</a>
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
