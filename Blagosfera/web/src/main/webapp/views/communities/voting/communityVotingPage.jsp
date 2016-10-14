<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${communityErrorMessage != null}" >
        <h1>${communityErrorMessage}</h1>
    </c:when>
    <c:otherwise>
        <%@ include file="../../votingSystem/votingPage.jsp"%>
    </c:otherwise>
</c:choose>