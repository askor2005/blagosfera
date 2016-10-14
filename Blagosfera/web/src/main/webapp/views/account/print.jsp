<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Печать</title>
<t:insertAttribute name="favicon" />

<style type="text/css">
table {
	border-width: 1px;
	border-spacing: 0px;
	border-style: outset;
	border-color: gray;
	border-collapse: separate;
	background-color: white;
}

table th {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	text-align : center;
}

table td {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
	text-align : center;
}
</style>

</head>
<body style="width: 21cm;">
	<h1>
		История операция с
		<fmt:formatDate pattern="dd.MM.yyyy" value="${startDate}" />
		по
		<fmt:formatDate pattern="dd.MM.yyyy" value="${endDate}" />
	</h1>
	<table id="table">
		<thead>
			<tr>
				<th width="100">Номер<br />транзакции
				</th>
				<th width="90">Дата</th>
				<th width="90">Время</th>
				<th>Комментарий</th>
				<th width="90">Сумма</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${transactions}" var="t">
				<tr>
					<td>${t.id}</td>
					<td><fmt:formatDate pattern="dd.MM.yyyy" value="${t.date}" /></td>
					<td><fmt:formatDate pattern="HH:mm:ss" value="${t.date}" /></td>
					<td>${t.comment}</td>
					<td>${t.amount}</td>
				</tr>
			</c:forEach>
		</tbody>
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
<script type="text/javascript">
	$(document).ready(function() {
		$("table#table").fixMe();
	});
</script>