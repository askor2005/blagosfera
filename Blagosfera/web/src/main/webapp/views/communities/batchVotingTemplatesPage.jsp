<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ include file="voting/batchVotingTemplatesGrid.jsp"  %>
<%@ include file="voting/batchVotingListGrid.jsp"  %>
<style type="text/css">
    #batchVotingTemplateGridSearchResult {
        display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;
    }
</style>
<script>
    var communityId = "${communityId}";

    var templatesPageTemplate;
    var selectedTemplateId = null;

    function deleteBatchVotingTemplate(templateId, callBack) {
        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/deleteBatchVotingTemplate.json",
                {
                    template_id: templateId
                },
                callBack
        );
    }

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
        loadAnyPageData(communityId, function(communityAnyPageData) {
            initCommunityHead(communityAnyPageData.community);
            initCommunityMenu(communityAnyPageData.community);
        });
        initTemplatesPage();
    });

    function initTemplatesPage() {
        templatesPageTemplate = $("#templatesPageTemplate").html();
        Mustache.parse(templatesPageTemplate);

        var markup = Mustache.render(templatesPageTemplate, {communityId: communityId});

        $("#templatesPageBlock").append(markup);

        initBatchVotingTemplatesGrid(communityId);
        // Удалить шаблон
        $("body").on("click", ".removeBatchVotingTemplate", function(){
            var templateId = $(this).attr("template_id");
            deleteBatchVotingTemplate(templateId, function(){
                batchVotingTemplatesStore.load();
            })
        });
        // Редактировать шаблон
        $("body").on("click", ".editBatchVotingTemplate", function(){
            var templateId = $(this).attr("template_id");
            window.location.href = '/group/' + communityId + '/batchVotingConstructor.html?templateId=' + templateId;
        });
        // Список собраний по шаблону
        $("body").on("click", ".batchVotingList", function(){
            selectedTemplateId = $(this).attr("template_id");
            $("#batchVotingListWindow").modal("show");
            return false;
        });

        $("#batchVotingListWindow").on("shown.bs.modal", function() {
            initBatchVotingListGrid(communityId, selectedTemplateId);
        });
        $("#batchVotingListWindow").on("hidden.bs.modal", function () {
            clearBatchVotingListGrid();
        });
    }
</script>

<t:insertAttribute name="communityHeader" />
<h2>Шаблоны собраний</h2>
<hr/>
<div id="templatesPageBlock"></div>

<script id="templatesPageTemplate" type="x-tmpl-mustache">
    <div>
        <div class="form-group">
            <a href="/group/{{communityId}}/batchVotingConstructor.html" class="btn btn-primary" id="addPossibleVoterButton">Создать шаблон</a>
            <hr/>
            <div id="batchVotingTemplates-grid"></div>
            <div id="batchVotingTemplateGridSearchResult"></div>
        </div>
    </div>
</script>

<!-- Модальное окно списока собраний по шаблону-->
<div class="modal fade" role="dialog" id="batchVotingListWindow" aria-labelledby="batchVotingListWindowTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="batchVotingListWindowTextLabel">Собрания созданные по шаблону</h4>
            </div>
            <div class="modal-body" style="min-height: 100px;">
                <div class="form-group">
                    <div id="batchVotingList-grid"></div>
                    <div id="batchVotingListGridSearchResult"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->