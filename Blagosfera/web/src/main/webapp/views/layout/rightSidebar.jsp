<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>


<c:if test="${radom:isSharer()}">
	<t:insertAttribute name="sharerRightSidebar" />
</c:if>

<c:if test="${not radom:isSharer()}">
	<%--<t:insertAttribute name="anonymousRightSidebar" />--%>
</c:if>

<script>
	$(document).ready(function(){

		$('.right-menu').attr('standart-width', $('.right-menu').width());
		$('.right-menu').css('width', getCookie('right-menu-width'));

		function getCookie(name) {
			var matches = document.cookie.match(new RegExp(
					"(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
			));
			return matches ? decodeURIComponent(matches[1]) : undefined;
		}
	});
</script>