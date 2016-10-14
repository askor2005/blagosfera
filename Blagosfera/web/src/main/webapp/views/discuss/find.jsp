<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<h1>Найти обсужения</h1>

<div class="list-group">
    <c:forEach items="${discussions}" var="d">
        <a href="/discuss/view/${d.id}" class="list-group-item">
            <h4 class="list-group-item-heading">${d.title}</h4>
            <p class="list-group-item-text pull-right">Создано: <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${d.createdAt}"/></p>
            <p class="list-group-item-text">Автор: ${d.owner}</p>
        </a>
    </c:forEach>
</div>

