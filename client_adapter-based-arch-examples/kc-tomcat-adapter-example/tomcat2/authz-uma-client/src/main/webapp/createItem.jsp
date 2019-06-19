<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
  response.setHeader("Expires", "-1");
  response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
%>

<html>

<head>
<title>Authz UMA Client - リソース作成</title>

<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<script src="https://sso.example.com/auth/js/keycloak.js"></script>
<script src="https://sso.example.com/auth/js/keycloak-authz.js"></script>
<script src="js/jquery-3.3.1.min.js" type="text/javascript" ></script>
<script src="js/jwt-decode.min.js" type="text/javascript" ></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/uma-client.js"type="text/javascript" ></script>


</head>

<%-- ログイン前に画面が見えるのを抑止 --%>
<body style="visibility:hidden">
<div class="container-fluid">

	<h3>ログインユーザー情報</h3>
	<div class="container-fluid">
	<table class="table table-striped">
		<tr>
			<th align="left">ユーザID</th>
			<td><div id="username"></div></td>
		</tr>
		<tr>
			<th align="left">メールアドレス</th>
			<td><div id="email"></div></td>
		</tr>
	</table>
	</div>

	<h3>UMA リソース作成</h3>
	<div class="container-fluid">
		<form class="form-inline">
			<div class="form-group">
				<label for="itemName">リソース名</label>
				<input type="text" class="form-control" id="name" placeholder="リソース名を入力してください。">
				<label for="itemMemo">メモ</label>
				<input type="text" class="form-control" id="memo" placeholder="メモを入力してください。">
			</div>
			<a href="#" onClick="create()" class="btn btn-primary btn-default active" role="button">リソースの作成</a>
		</form>
	</div>

	<h3>詳細コンソール</h3>
	<div class="container-fluid">
		<textarea class="resultConsole" cols="100" rows="10" wrap="off"></textarea>
	</div>

	<div class="container-fluid">
		<a href="https://uma.example.com/authz-uma-client/"  class="btn btn-primary btn-default active" role="button">トップに戻る</a>
	</div>

</div>
</body>
</html>
