<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<h1 style="font-size: 30px;">${community.name}</h1>
<h2>
	${radom:communityLabel("Объединение удалено", "Группа удалена")}
</h2>

<hr/>

<c:if test="${not empty community.deleteComment}">
	<p>
		<strong>Причина удаления: </strong> ${community.deleteComment}
	</p>
</c:if>
