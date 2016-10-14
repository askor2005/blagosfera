<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<script>
    var communityId = ${communityId};

    function searchUser(searchString, callBack) {
        $.radomJsonPost(
                "/sharer/searchActive.json",
                {
                    query : searchString
                },
                function(response) {
                    if (response != null && response.length > 0) {
                        var result = [];
                        for (var index in response) {
                            var user = response[index];
                            result.push({
                                id : user.id,
                                name : user.fullName
                            });
                        }
                        callBack(result);
                    } else {
                        callBack([]);
                    }
                }
        );
    }

    function searchCommunity(searchString, callBack) {
        $.radomJsonPost(
                "/communities/search.json",
                {
                    query : searchString
                },
                function(response) {
                    if (response != null && response.list != null && response.list.length > 0) {
                        var result = [];
                        for (var index in response.list) {
                            var community = response.list[index];
                            result.push({
                                id : community.id,
                                name : community.name
                            });
                        }
                        callBack(result);
                    } else {
                        callBack([]);
                    }
                }
        );
    }

    var searchMembersForTemplate = function(searchString, participantType, callBack) {
        if (participantType == "INDIVIDUAL" ||
                participantType == "REGISTRATOR") {
            searchUser(searchString, callBack);
        } else if (participantType == "COMMUNITY_WITH_ORGANIZATION" ||
                participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
            searchCommunity(searchString, callBack);
        }
    };

    var sourceNamesForInputMembersForOrganization = {
        "INDIVIDUAL": [{sourceCode : "user", name : "Принимаемый участник"}],
        "COMMUNITY_WITH_ORGANIZATION": [{sourceCode : "community", name : "Объединение"}]
    };
    var sourceNamesForInputMembersForGroup = {
        "INDIVIDUAL": [{sourceCode : "user", name : "Принимаемый участник"}],
        "COMMUNITY_WITHOUT_ORGANIZATION": [{sourceCode : "community", name : "Объединение"}]
    };

    var saveDocumentTemplateSetting = function(documentTemplateSettings, callBack) {
        var needCreateDocuments = $("[name=inputNewMembersType]").filter(":checked").attr("id") == "inputWithDocuments";
        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/save_document_templates_settings.json",
                JSON.stringify({
                    documentTemplateSettings : documentTemplateSettings,
                    needCreateDocuments : needCreateDocuments
                }),
                callBack,
                null,
                {
                    contentType: "application/json"
                }
        );
    };

    var loadDocumentTemplateSettings = function(callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/load_document_templates_settings_page_data.json",
                {},
                callBack,
                function(error){
                    console.log(error);
                }
        );
    }

    function initInputMembersPage(pageData) {
        /*$("[name=inputNewMembersType]").click(function(){
            if ($(this).attr("id") == "inputWithDocuments") {
                $("#inputMemberSettingsBlock").show();
            } else {

            }
        });*/

        if (pageData.needCreateDocuments) {
            $("#inputWithDocuments").prop("checked", true).trigger("click");
        } else {
            $("#inputWithoutDocuments").prop("checked", true).trigger("click");
        }
    }

    $(document).ready(function() {
        loadDocumentTemplateSettings(function(pageData){
            console.log(pageData.community);
            var sourceNamesForInputMembers = null;
            if (pageData.community.type == "COMMUNITY_WITH_ORGANIZATION") {
                sourceNamesForInputMembers = sourceNamesForInputMembersForOrganization;
            } else if (pageData.community.type == "COMMUNITY_WITHOUT_ORGANIZATION") {
                sourceNamesForInputMembers = sourceNamesForInputMembersForGroup;
            }


            initDocumentTemplateSettings(
                    $("#inputMemberSettingsBlock"),
                    pageData.documentTemplateSettings,
                    searchMembersForTemplate,
                    saveDocumentTemplateSetting,
                    sourceNamesForInputMembers
            );
            initCommunityHead(pageData.community);
            initCommunityMenu(pageData.community);
            initInputMembersPage(pageData);
        });
    });
</script>

<t:insertAttribute name="communityHeader" />
<hr/>
<h2>Настройки приёма и вывода участников</h2>
<hr/>
<div>
    <div class="form-group">
        <label>
            <input type="radio" name="inputNewMembersType" id="inputWithDocuments"/>
            Создавать документы для подписания на основе шаблонов для приёма новых членов объединения
        </label>
    </div>
    <div class="form-group">
        <label>
            <input type="radio" name="inputNewMembersType" id="inputWithoutDocuments"/>
            Приём новых членов в объединение без подписания документов
        </label>
    </div>
</div>
<hr/>
<div id="inputMemberSettingsBlock"></div>
<hr/>