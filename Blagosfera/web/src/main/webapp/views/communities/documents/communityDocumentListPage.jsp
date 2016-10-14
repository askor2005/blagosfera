<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<script>
    var communityId = ${communityId};
    function loadAnyPageData(communityId, callBack) {
        $.radomJsonPost(
                "/communities/any_page_data.json",
                {
                    community_id : communityId
                },
                callBack
        );
    }

    $(document).ready(function(){
        loadAnyPageData(communityId, function(communityAnyPageData) {
            initCommunityMenu(communityAnyPageData.community);
        });
    })
</script>
<%@include file="../../testDocuments/documentListPage.jsp" %>