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
<script src="http://sso.example.com/auth/js/keycloak.js"></script>
<script src="http://sso.example.com/auth/js/keycloak-authz.js"></script>
<script src="js/jquery-3.3.1.min.js" type="text/javascript" ></script>
<script src="js/jwt-decode.min.js" type="text/javascript" ></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/uma-client.js"type="text/javascript" ></script>


</head>

<%-- ログイン前に画面が見えるのを抑止 --%>
<body style="visibility:hidden">

<div class="panel panel-primary">
	<div class="panel-heading">リソース一覧
	</div>
	<div class="panel-body">
		ログインユーザID: <label id="username"></label><br>

		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
				</div>

				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav navbar-left">
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">リソース管理 <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="javascript: displayResources(true);">リソース一覧の更新</a></li>
								<li><a href="createItem.jsp">リソースの新規作成</a></li>
								<li><a href="http://sso.example.com/auth/realms/demo-authz/account/resource?referrer=authz-uma-client&referrer_uri=http%3A%2F%2Fuma.example.com%2Fauthz-uma-client%2F">リソースの共有／申請の確認・承認</a></li>
								<li><a href="#" onClick="getEntitlement()">現在のパーミッション確認</a></li>
							</ul>
						</li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="javascript: keycloak.logout();">ログアウト</a></li>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>

		<!-- タブ・メニュー -->
		<ul class="nav nav-tabs">
			<li class="active"><a href="#listResources" data-toggle="tab">リソース一覧</a></li>
			<li><a href="#detailConsole" data-toggle="tab">詳細コンソール</a></li>
		</ul>

		<!-- タブ内容 -->
		<div class="tab-content">
			<div class="tab-pane active" id="listResources">
				<br>
				<div class="resourcesTable" ></div>
				<label for="resultMessage">簡易メッセージ</label><input type="text" class="form-control" id="resultMessage" readonly>
			</div>
			<div class="tab-pane" id="detailConsole">
				<br>
				<div class="container-fluid">
					<textarea class="resultConsole" cols="100" rows="15" wrap="off"></textarea>
				</div>
			</div>
		</div>
	</div>

	<div class="panel-footer"><a href="https://authz.example.com/authz-app/">集中管理方式へ</a></div>

</body>
</html>
