<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style type="text/css">

</style>
<script>
    var approveSharersTemplate;
    var approveOrganizationsTemplate;
    var communityId = "${communityId}";

    // Загрузить канидатов - физ лиц
    function loadUserCandidates(callBack, errorCallBack){
        $.radomJsonPost(
            "/group/" + communityId + "/get_sharers_candidates_to_join_in_community.json",
            {},
            callBack,
            errorCallBack
        );
    }
    // Загрузить канидатов - юр лиц
    function loadOrganizationCandidates(callBack, errorCallBack){
        $.radomJsonPost(
            "/group/" + communityId + "/get_organizations_candidates_join_in_community.json",
            {},
            callBack,
            errorCallBack
        );
    }

    //
    function drawSharerCandidates(){
        $("#candidatesContainer").empty();
        loadUserCandidates(function(response){
            for (var userId in response.documentLinks) {
                var documentLink = response.documentLinks[userId];
                for (var memberIndex in response.members) {
                    var member = response.members[memberIndex];
                    if (member.user.id == userId) {
                        member.documentLink = documentLink;
                        break;
                    }
                }
            }
            for (var index in response.createdProtocols) {
                var document = response.createdProtocols[index];
                document.createDate = new Date(document.createdDate).format("dd.mm.yyyy HH:MM");
            }
            var jqContent = $(Mustache.render(approveSharersTemplate, response));
            $("#candidatesContainer").append(jqContent);
        });
    }
    //
    function drawOrganizationCandidates(){
        $("#candidatesContainer").empty();
        loadOrganizationCandidates(function(response){
            var jqContent = $(Mustache.render(approveOrganizationsTemplate, response));
            $("#candidatesContainer").append(jqContent);
        });
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
        initApproveUserCooperativePage();
    });

    function initApproveUserCooperativePage() {
        approveSharersTemplate = $("#approveSharersTemplate").html();
        approveOrganizationsTemplate = $("#approveOrganizationsTemplate").html();
        Mustache.parse(approveSharersTemplate);
        Mustache.parse(approveOrganizationsTemplate);

        $("body").on("click", "#createProtocolForJoinSharersToCooperative", function(){
            var selectCandidateToMembersIds = [];
            $(".selectCandidateToMember:checked").each(function(){
                selectCandidateToMembersIds.push($(this).attr("candidate_id"));
            });

            $.radomJsonPostWithWaiter("/group/" + communityId + "/accept_requests.json",
                JSON.stringify(selectCandidateToMembersIds)
                ,null, null,
                {
                    "neededParameters" : ["documentName", "documentLink"],
                    "content" : "Документ {documentName} создан!<br/>" +
                    "<a href='{documentLink}' class='btn btn-primary'>Перейти к подписанию документа</a>",
                    contentType: "application/json"
                }
            );
        });
        $("body").on("click", "#createProtocolForJoinOrganizationsToCooperative", function(){
            var selectCandidateToMembersIds = [];
            $(".selectCandidateToMember:checked").each(function(){
                selectCandidateToMembersIds.push($(this).attr("candidate_id"));
            });
            CommunityFunctions.acceptOrganizationRequests(communityId, selectCandidateToMembersIds, null, null, {
                "neededParameters" : ["documentName", "documentLink"],
                "content" : "Документ {documentName} создан!<br/>" +
                "<a href='{documentLink}' class='btn btn-primary'>Перейти к подписанию документа</a>"
            });
        });


        var selectFromChild = false;

        // Выбрать\Отменить выбор всех
        $("body").on("click", "#selectAll", function(){
            if (!selectFromChild) {
                var checked = $(this).prop("checked");
                $(".selectCandidateToMember").prop("checked", checked);
            }
        });

        $("body").on("click", ".selectCandidateToMember", function(){
            selectFromChild = true;
            if ($(".selectCandidateToMember").length == $(".selectCandidateToMember:checked").length) {
                $("#selectAll").prop("checked", true);
            } else {
                $("#selectAll").prop("checked", false);
            }
            selectFromChild = false;
        });

        $("#memberType").css("visibility", "visible");
        $("#memberType").selectpicker("refresh");
        $("#memberType").change(function(){
            var memberType = $(this).val();
            if (memberType == "sharer_members") {
                drawSharerCandidates();
            } else if (memberType == "organization_members") {
                drawOrganizationCandidates();
            }
        });

        // По умолчанию загружаем - физ лиц
        drawSharerCandidates();
    }
</script>
<!-- Список участников, которых нужно принять -->

<t:insertAttribute name="communityHeader" />
<h2>Принятие пайщиков</h2>
<hr/>

<div class="pageContainer">
    <div class="form-group">
        <select id="memberType" class="selectpicker" data-hide-disabled="true" data-width="100%" style="visibility: hidden;">
            <option value="sharer_members" selected="selected">Участники - Физические лица</option>
            <option value="organization_members">Участники - Юридические лица</option>
        </select>
    </div>
    <div id="candidatesContainer"></div>
</div>

<script id="approveSharersTemplate" type="x-tmpl-mustache">
    <h4>Заявления от кандидатов в пайщики физ лиц</h4>
    <div class="form-group">
        <table class="standardTable">
            <tr>
                <th>Заявление</th>
                <th>ФИО кандидата в пайщики</th>
                <th>
                    <label>Выбрать всех
                        <input type="checkbox" id="selectAll" />
                    </label>
                </th>
            </tr>
            {{#members}}
                <tr>
                    <td><a href="{{documentLink}}">Заявление</a></td>
                    <td>{{user.fullName}}</td>
                    <td>
                        <input type="checkbox" class="selectCandidateToMember" candidate_id="{{id}}" id="selectCandidateToMember{{user.id}}" />
                    </td>
                </tr>
            {{/members}}
        </table>
    </div>
    <div class="form-group">
        <a href="javascript:void(0)" style="float: right;" class="btn btn-primary" id="createProtocolForJoinSharersToCooperative">Создать протокол принятия пайщиков</a>
    </div>
    <div style="clear: both;"></div>
    <hr/>
    <h4>Не подписанные протоколы приёма пайщиков физ лиц</h4>
    <div class="form-group">
        <table class="standardTable">
            <tr>
                <th>Ссылка на протокол</th>
                <th>Дата создания протокола</th>
            </tr>
            {{#createdProtocols}}
                <tr>
                    <td><a href="{{link}}">{{{shortName}}}</a></td>
                    <td>{{createDate}}</td>
                </tr>
            {{/createdProtocols}}
        </table>
    </div>
</script>
<script id="approveOrganizationsTemplate" type="x-tmpl-mustache">
    <h4>Заявления от кандидатов в пайщики юр лиц</h4>
    <div class="form-group">
        <table class="standardTable">
            <tr>
                <th>Заявление</th>
                <th>Наименование юр лица</th>
                <th>
                    <label>Выбрать всех
                        <input type="checkbox" id="selectAll" />
                    </label>
                </th>
            </tr>
            {{#members}}
                <tr>
                    <td><a href="{{document.link}}">Заявление</a></td>
                    <td>{{organizationName}}</td>
                    <td>
                        <input type="checkbox" class="selectCandidateToMember" candidate_id="{{id}}" id="selectCandidateToMember{{organizationId}}" />
                    </td>
                </tr>
            {{/members}}
        </table>
    </div>
    <div class="form-group">
        <a href="javascript:void(0)" style="float: right;" class="btn btn-primary" id="createProtocolForJoinOrganizationsToCooperative">Создать протокол принятия пайщиков</a>
    </div>
</script>