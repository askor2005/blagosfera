<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="../fields/fileField.jsp" />
<jsp:include page="../fields/addressFields.jsp" />
<jsp:include page="../fields/commonFields.jsp" />
<script>
    var withOrganization = true;
    var parentCommunityId = null;
    var rootCommunity = true;
</script>
<jsp:include page="createCommunity.jsp" />