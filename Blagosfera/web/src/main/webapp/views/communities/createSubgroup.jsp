<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<jsp:include page="../fields/fileField.jsp" />
<jsp:include page="../fields/addressFields.jsp" />
<jsp:include page="../fields/commonFields.jsp" />
<script>
	var withOrganization = false;
	var rootCommunity = false;
	var parentCommunityId = "${communityId}";

	function loadAnyPageData(communityId, callBack) {
		$.radomJsonPost(
				"/communities/any_page_data.json",
				{
					community_id : communityId
				},
				callBack
		);
	}

	$(document).ready(function() {
		loadAnyPageData(parentCommunityId, function(communityAnyPageData) {
			initCommunityHead(communityAnyPageData.community);
			initCommunityMenu(communityAnyPageData.community);
		});
	});
</script>
<t:insertAttribute name="communityHeader" />
<jsp:include page="createCommunity.jsp" />