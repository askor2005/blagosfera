<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@include file="myLetterOfAuthoritiesGrid.jsp" %>
<style>
    #letterOfAuthorityMyGridSearchResult {
        display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
    });
</script>
<h2>${currentPageTitle}</h2>
<hr/>
<div id="letterOfAuthorityMy-grid"></div>
<div id="letterOfAuthorityMyGridSearchResult"></div>
<hr/>