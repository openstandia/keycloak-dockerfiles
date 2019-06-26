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

<div class="panel panel-primary">
	<div class="panel-heading">リソース新規作成</div>
	<div class="panel-body">
		ログインユーザID: <label id="username"></label><br>

		<!-- タブ・メニュー -->
		<ul class="nav nav-tabs">
			<li class="active"><a href="#listResources" data-toggle="tab">リソース作成</a></li>
			<li><a href="#detailConsole" data-toggle="tab">詳細コンソール</a></li>
		</ul>

		<!-- タブ内容 -->
		<div class="tab-content">
			<div class="tab-pane active" id="listResources">
				<br>
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
				<label for="resultMessage">結果メッセージ</label><input type="text" class="form-control" id="resultMessage" readonly>
			</div>

			<div class="tab-pane" id="detailConsole">
				<br>
				<div class="container-fluid">
					<textarea class="resultConsole" cols="100" rows="15" wrap="off"></textarea>
				</div>
			</div>
		</div>
	</div>

	<div class="panel-footer"><a href="https://uma.example.com/authz-uma-client/">リソース一覧に戻る</a></div>

</body>
</html>
