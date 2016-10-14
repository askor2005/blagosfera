<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../fields/fileField.jsp" />
<jsp:include page="../fields/addressFields.jsp" />
<jsp:include page="../fields/commonFields.jsp" />
<script>
	var withOrganization = false;
	var parentCommunityId = null;
	var rootCommunity = true;
</script>
<jsp:include page="createCommunity.jsp" />