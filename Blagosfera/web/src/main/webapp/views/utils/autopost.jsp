<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<script type="text/javascript" src="/js/jquery.js"></script>
		<t:insertAttribute name="favicon" />
		
		<script type="text/javascript">
			$(document).ready(function() {
				$("form#autopost").submit();
			});
		</script>
		
	</head>
	<body>
		<h1>Ожидайте перенаправления</h1>
		<form id="autopost" method="post" action="${autopostParameters.action}" style="width : 1px; height : 1px; overflow : hidden;">
			<c:forEach items="${autopostParameters.map.entrySet()}" var="e">
				<input type="hidden" name="${e.key}" value='${e.value}' />
			</c:forEach>
		</form>
	</body>
</html>
