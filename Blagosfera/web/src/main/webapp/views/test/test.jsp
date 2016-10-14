<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	<title>Тестовая страница</title>
	<link rel="stylesheet" type="text/css" href="/css/bootstrap.css" />
</head>

<body>

	<table class="table">
		<tr>
			<td>available processors</td>
			<td>${availableProcessors}</td>
		</tr>
		<tr>
			<td>free memory</td>
			<td>${freeMemory}</td>
		</tr>
		<tr>
			<td>max memory</td>
			<td>${maxMemory}</td>
		</tr>
		<tr>
			<td>total memory</td>
			<td>${totalMemory}</td>
		</tr>						
	</table>
	<!-- BEGIN JIVOSITE CODE {literal} -->
	<script type='text/javascript'>
		var jivositeKey = "${jivositeKey}";
		function jivo_onLoadCallback() {
			$.radomJsonGet(
					"/jivosite/info.json",
					null,
					function (response) {
						clearJivositeInfo();
						if (response.userInfo) {
							var date = new Date(0);
							jivo_api.setContactInfo(
									{
										name: response.userInfo.name,
										email: response.userInfo.email,
										phone: response.userInfo.phone,
										description : ""

									}
							);
							//jivo_api.setUserToken(response.userToken);
						}
					});
		}
		function clearJivositeInfo() {
			delete_cookie('jv_client_name_'+jivositeKey);
			delete_cookie('jv_email_'+jivositeKey);
			delete_cookie('jv_phone_'+jivositeKey);
		}
		function delete_cookie ( cookie_name )

		{
			var cookie_date = new Date ( );
			cookie_date.setTime ( 0 );
			document.cookie = cookie_name += "=; path=/ ; expires=" +
					cookie_date.toUTCString();

		}

		(function(){ var widget_id = jivositeKey;var d=document;var w=window;function l(){
			var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);}if(d.readyState=='complete'){l();}else{if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})();
	</script>
	<!-- {/literal} END JIVOSITE CODE -->
</body>

<hr/>

<h2>Remote address: ${remoteAddress}</h2>

<hr/>

<h2>Request headers</h2>

<c:forEach var="entry" items="${headers}">
	<dl class="dl-horizontal">
		<dt>${entry.key}</dt>
		<dd>${entry.value}</dd>
	</dl>
</c:forEach>
<script type="text/javascript">
	$(document).ready(function() {
		$("table.table").fixMe();
	});
</script>
</html>
