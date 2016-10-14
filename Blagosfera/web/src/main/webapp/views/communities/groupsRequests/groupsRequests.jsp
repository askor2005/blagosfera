<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="groupsRequestsGrid.jsp" />
<script>
    function removeRequest(id, callBack) {
        $.radomJsonPostWithWaiter("/groups/documentrequests/remove_request.json", {
            id : id
        }, callBack);
    }
    $(document).ready(function(){
        initGroupsRequestsGrid();

        $("body").on('click', '.removeRequest', function (e) {
            var requestId = $(this).attr("request_id");
            bootbox.confirm("Удалить запрос на вступление в объединение?", function(result) {
                if (result) {
                    removeRequest(requestId, function(){
                        groupsRequestsStore.reload();
                    });
                }
            });
        });

    });
</script>
<div>
    <h1>Пакеты документов для вступления в объединения</h1>
</div>
<hr/>
<div id="groupsRequests-grid"></div>
<hr/>