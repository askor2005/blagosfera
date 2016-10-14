<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style type="text/css">

</style>

<h1>Мои обсуждения</h1>


<%--<ul class="nav nav-tabs" role="tablist">--%>
    <%--<li role="presentation" class="active"><a href="#">Созданные мной</a></li>--%>
    <%--<li role="presentation"><a href="#">Подписки</a></li>--%>
    <%--<li role="presentation"><a href="#">Просмотренные</a></li>--%>
<%--</ul>--%>
<%--<br/>--%>

<div class="list-group">
    <c:forEach items="${discussions}" var="d">
        <a href="/discuss/view/${d.id}" class="list-group-item">
            <h4 class="list-group-item-heading">${d.title}</h4>
            <p class="list-group-item-text pull-right" style="padding-left: 10px;">
                Рейтинг: <c:if test="${not empty ratingsSums[d.root.id]}"><fmt:formatNumber value="${ratingsSums[d.root.id]}" maxFractionDigits="0" /></c:if>
                <c:if test="${empty ratingsSums[d.root.id]}">0</c:if></p>
            <p class="list-group-item-text pull-right">Создано: <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${d.createdAt}"/></p>
            <p class="list-group-item-text">Автор: ${d.owner}</p>
        </a>
    </c:forEach>
</div>

