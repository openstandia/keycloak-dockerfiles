var contextPath = location.pathname.substr(0, location.pathname.length - 1);
var apiContextPath = "/authz-uma-api";
var uriPrefix = "/api/items/";
var accessToken;

var keycloak = new Keycloak();
var authorization;

// JavaScriptアダプターの初期化(ログイン必須ページ)
keycloak.init({ onLoad: 'login-required' }).success(function() {

	// 認可クライアントインスタンスの生成
	authorization = new KeycloakAuthorization(keycloak);

	// profile 取得処理
	keycloak.loadUserProfile().success(function() {

		// 画面ヘッダーの値設定
		document.getElementById('subject').innerText = keycloak.subject;
		document.getElementById('email').innerText = keycloak.profile.email;
		document.getElementById('username').innerText = keycloak.profile.username;
		document.getElementById('itemName').innerText = keycloak.profile.username + ' Item';

		// アクセストークンの取得
		accessToken = keycloak.token;

		// リソース表示
		displayResources();
	}).error(function() {
		alert('Failed to load user profile');
	});

}).error(function() {
	alert('failed to initialize');
});


// アクセストークン期限切れ時の処理
keycloak.onTokenExpired = function() { 
	console.log('token expired');

	keycloak.updateToken(5).success(function(refreshed) {
		if (refreshed) {
			console.log('Token was successfully refreshed');
			// アクセストークンの更新
			accessToken = keycloak.token;
		} else {
			console.log('Token is still valid');
		}
	}).error(function() {
		console.log('Failed to refresh the token, or the session has expired');
		// 画面再ロードして、ログイン画面に遷移させる
		window.location.reload();
	});
}

function create() {
	id = "?name=" + keycloak.profile.username;
	submit(id, "POST");
}

function submit(id, method, token, submitRequest) {

	// token パラメータ(RPT)がないときは、アクセストークンを利用
	if (!token) {
		token = accessToken;
	}

	var requestUri = apiContextPath + uriPrefix + id;
	$.ajax({
		type: method,
		url: requestUri,
		headers: {
			"Authorization": "Bearer " + token
		},
		dataType: 'text',
		statusCode: {
			200: function(responseText, statusText, response) {
				displayResources();
				$(".resultConsole").text("＜HTTPリクエスト＞\n");
				$(".resultConsole").append(method + " " + requestUri + "\n\n");
				$(".resultConsole").append("＜HTTPレスポンス＞\n");
				$(".resultConsole").append("ステータス : " + response.status + " " + statusText + "\n");
				$(".resultConsole").append("API 応答   : " + responseText);
			},
			401: function(response, statusText) {
				displayResources();

				// HTTPレスポンスヘッダーに WWW-Authenticate : UMA ... が返ってきている場合は、以降の処理継続
				var wwwAuthenticateHeader = response.getResponseHeader('WWW-Authenticate');
				if (wwwAuthenticateHeader.indexOf('UMA') >= 0) {
					var params = wwwAuthenticateHeader.split(',');
					var ticket;

					// WWW-Authenticate ヘッダー内の ticket パラメータを取得
					for (i = 0; i < params.length; i++) {
						var param = params[i].split('=');

						if (param[0] == 'ticket') {
							ticket = param[1].substring(1, param[1].length - 1).trim();
							break;
						}
					}

					// 認可リクエストインスタンスの生成し、取得した ticket を設定
					var authorizationRequest = {};
					authorizationRequest.ticket = ticket;

					// パーミッション申請の有無を設定
					if (submitRequest) {
						authorizationRequest.submitRequest = submitRequest;
					} else {
						authorizationRequest.submitRequest = false;
					}

					// 認可サーバーへ認可リクエスト送信
					authorization.authorize(authorizationRequest).then(function (rpt) {

						// パーミッション申請でなければ
						if (!submitRequest) {
							// 取得した RPT を使って当初のリクエストをリトライ
							submit(id, method, rpt);
						}
					}, function () {
						// RPT が取得できない場合はアクセス権限がないので、401 エラーをそのまま返す
						if (!submitRequest) {
							$(".resultConsole").text("＜HTTPリクエスト＞\n");
							$(".resultConsole").append(method + " " + requestUri + "\n\n");
							$(".resultConsole").append("＜HTTPレスポンス＞\n");
							$(".resultConsole").append("ステータス : 401 error\n");
						}
					}, function () {
						$('.resultConsole').text("リクエスト失敗！");
					});

				}
			}
		}
	}).fail(function(response){
		// パーミッション申請のリクエストはエラーとして捕捉されるが、申請自体は実施されている
		if (submitRequest) {
			$(".resultConsole").text("＜HTTPリクエスト＞\n");
			$(".resultConsole").append(method + " " + requestUri + "\n\n");
			$(".resultConsole").append("＜HTTPレスポンス＞\n");
			$(".resultConsole").append("ステータス : " + response.status + " " + response.statusText + "\n");
			$(".resultConsole").append("パーミッション申請 : " + method + " 権限を申請しました。");
		} else {
			$('.resultConsole').text("リクエスト失敗！");
		}

	});

}

function requestScope(id, method) {

	submit(id, method, null, true);

}

function introspectRPT() {

	displayResources();

	// 認可クライアントからエンタイトルメントを取得し、現在のアクセス権を表示
	authorization.entitlement("authz-uma-api").then(function (rpt) {
		$(".resultConsole").text("＜HTTPレスポンス＞\n");
		$(".resultConsole").append("ステータス : 200 OK\n");
		$(".resultConsole").append("API 応答   : \n" + JSON.stringify(jwt_decode(rpt).authorization, null, 4));
	}, function () {
		$('.resultConsole').text("リクエスト失敗！");
	}, function () {
		$('.resultConsole').text("リクエスト失敗！");
	});

}

function displayResources() {

	$.ajax({
		type: 'GET',
		url: apiContextPath + uriPrefix,
		headers: {
			"Authorization": "Bearer " + accessToken
		},
		dataType: 'json'
	}).done(function(resources){
		$('.resourcesTable').text("");
		var tableHtml = "<table  class='table table-striped'><tr><th>オーナー</th><th>リソース名</th><th>API アクセスチェック</th><th>UMA 操作<br>(パーミッション申請)</th></tr>";
		for (var i in resources) {
			tableHtml += "<tr>";
				tableHtml += "<td align='center'>" + (resources[i].isOwner ? "○" : "") + "</td>";
				tableHtml += "<td>"+ resources[i].name+ "</td>";
				tableHtml += "<td>";
					tableHtml += " <a href='#' onClick=\"submit('" + resources[i].subject + "', 'GET')\" class='btn btn-primary btn-default active' role='button'>view</a>";
					tableHtml += " <a href='#' onClick=\"submit('" + resources[i].subject + "', 'PUT')\" class='btn btn-primary btn-default active' role='button'>update</a>";
					tableHtml += " <a href='#' onClick=\"submit('" + resources[i].subject + "', 'DELETE')\" class='btn btn-primary btn-default active' role='button'>delete</a>";
				tableHtml += "</td>";
				tableHtml += "<td>";
				if ( !resources[i].isOwner ) {
					if (!resources[i].viewable) {
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'GET')\" class='btn btn-primary btn-default active' role='button'> view </a>";
					}
					if (!resources[i].updatable) {
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'PUT')\" class='btn btn-primary btn-default active' role='button'>update</a>";
					}
					if (!resources[i].deletable) {
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'DELETE')\" class='btn btn-primary btn-default active' role='button'>delete</a>";
					}
				}
				tableHtml += "</td>";
			tableHtml += "</tr>";
		}
		tableHtml += "</table>";
		$('.resourcesTable').append(tableHtml);
	}).fail(function(resources){
		$('.resourcesTable').text("リクエスト失敗！");
		// 再ログイン要求のため、ページをリロード
		window.location.reload();
	});
}