var contextPath = location.pathname.substr(0, location.pathname.length - 1);
var apiContextPath = "/authz-uma-api";
var uriPrefix = "/api/items/";
var accessToken;

function checkToken() {

	var requestUri = contextPath + "/checkToken.jsp";
	$.ajax({
		type: 'GET',
		url: requestUri,
		async: false,
		dataType: 'text',
		statusCode: {
			200: function(responseText, statusText, response) {
				newAccessToken = responseText.replace(/\r?\n/g, '');
				if (accessToken != newAccessToken) {
					console.log("accessToken refreshed!")
					accessToken = newAccessToken;
				}
			}
		}
	}).fail(function(responseText){
		console.log("refreshToken expired!");
		location.href = contextPath;
	});

}

function submit(id, method) {

	checkToken();
	var requestUri = apiContextPath + uriPrefix + id;
	$.ajax({
		type: method,
		url: requestUri,
		headers: {
			"Authorization": "Bearer " + accessToken
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
			403: function(response, statusText) {
				displayResources();
				$(".resultConsole").text("＜HTTPリクエスト＞\n");
				$(".resultConsole").append(method + " " + requestUri + "\n\n");
				$(".resultConsole").append("＜HTTPレスポンス＞\n");
				$(".resultConsole").append("ステータス : " + response.status + " " + statusText + "\n");
			}
		}
	}).fail(function(text){
		$('.resultConsole').text("リクエスト失敗！");
	});

}

function requestScope(id, scope) {

	uri = "requestScope?id=" + id + "&scope=" + scope;
	submit(uri, 'GET');

}

function introspectRPT() {

	checkToken();
	requestUri = apiContextPath + uriPrefix + "introspectRPT";
	$.ajax({
		type: 'GET',
		url: requestUri,
		headers: {
			"Authorization": "Bearer " + accessToken
		},
		dataType: 'json',
	}).done(function(permissions, statusText, response){
		$(".resultConsole").text("＜HTTPリクエスト＞\n");
		$(".resultConsole").append("GET " + requestUri + "\n\n");
		$(".resultConsole").append("＜HTTPレスポンス＞\n");
		$(".resultConsole").append("ステータス : " + response.status + " " + statusText + "\n");
		$(".resultConsole").append("API 応答   : \n" + JSON.stringify(permissions, null, 4));
		displayResources();
	}).fail(function(permissions){
		$('.resultConsole').text("リクエスト失敗！");
	});

}

function displayResources() {

	checkToken();

	$.ajax({
		type: 'GET',
		url: apiContextPath + uriPrefix,
		headers: {
			"Authorization": "Bearer " + accessToken
		},
		dataType: 'json'
	}).done(function(resources){
		$('.resourcesTable').text("");
		var tableHtml = "<table  class='table table-striped'><tr><th>オーナー</th><th>リソース名</th><th>API アクセスチェック</th><th>UMA 操作<br>(アクセス権申請)</th></tr>";
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
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'view')\" class='btn btn-primary btn-default active' role='button'> view </a>";
					}
					if (!resources[i].updatable) {
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'update')\" class='btn btn-primary btn-default active' role='button'>update</a>";
					}
					if (!resources[i].deletable) {
						tableHtml += " <a href='#' onClick=\"requestScope('" + resources[i].subject + "', 'delete')\" class='btn btn-primary btn-default active' role='button'>delete</a>";
					}
				}
				tableHtml += "</td>";
			tableHtml += "</tr>";
		}
		tableHtml += "</table>";
		$('.resourcesTable').append(tableHtml);
	}).fail(function(resources){
		$('.resourcesTable').append("リクエスト失敗！");
	});
}