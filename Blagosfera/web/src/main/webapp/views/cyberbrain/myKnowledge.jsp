<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="cyberbrainCommunitySelector.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        $("a#search-button").click(function() {
            storeThesaurus.load();
            storeJournalAttention.load();
            storeKnowledgeRepository.load();
            return false;
        });

        $('#communitySelectorModalWindow').find("#community-btn-add").click(function() {
            var communityId = $("#communitySelectorModalWindow").find("#combobox-communities option:selected").attr("data-community-id");

            if (communityId != -1 && communityId != "undefined") {
                $('#communitySelectorModalWindow').modal('hide');

                $.ajax({
                    type: "post",
                    dataType: "json",
                    data: "{community_id: \"" + communityId + "\"}",
                    url: "/cyberbrain/myKnowledge/startReplication",
                    success: function (response) {
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            } else {
                bootbox.alert("Для запуска репликации нужно выбрать для какого объединения будет запущенна репликация.");
            }
        });

        $("#button-start-replication").click(function() {
            $("#communitySelectorModalWindow").modal({backdrop:false, keyboard:false});
            $("#communitySelectorModalWindow").find(".modal-title").text("Выберите объединение для которого будет запущенна репликация");
            $("#communitySelectorModalWindow").find("#community-btn-add").html("Запустить");

            return false;
        })
    });
</script>

<%@include file="cyberbrainSections.jsp" %>

<h1>Мои знания</h1>

<hr/>

<form role="form" method="post" enctype="multipart/form-data">
    <div class="row">
        <div class="col-xs-9">
            <div class="form-group">
                <label>Найти</label>
                <input id="search" type="text" autocomplete="off" class="form-control" />
            </div>
        </div>

        <div class="col-xs-3">
            <div class="form-group">
                <label>&nbsp;</label>
                <a href="#" class="btn btn-default btn-block" id="search-button">Обновить</a>
            </div>
        </div>
    </div>

    <hr/>

    <%@include file="myKnowledgeThesaurusGrid.jsp" %>

    <hr/>

    <%@include file="myKnowledgeJournalAttentionGrid.jsp" %>

    <hr/>

    <%@include file="myKnowledgeKnowledgeRepositoryGrid.jsp" %>

    <hr/>

    <button type="submit" class="btn btn-primary" id="button-start-replication">Запустить репликацию данных</button>

    <hr/>
</form>