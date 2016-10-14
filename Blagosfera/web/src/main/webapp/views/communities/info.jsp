<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../fields/fileField.jsp" />
<style type="text/css">
    .dl-horizontal dt {
        width: 165px;
    }

    .dl-horizontal dt {
        width: 175px;
        margin-bottom : 10px;
    }

    .dl-horizontal dd {
        margin-bottom : 10px;
    }

    .activity-scope {
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;

        border : 1px solid #bce8f1;
        color: #31708f;
        background-color: #d9edf7;

        margin-right: 4px;
        padding: 2px 5px;
        white-space: nowrap;
        margin-bottom: 10px;
        margin-top: -3px;
        display: inline-block;

    }

    div.field-value {
        max-height : ${maxFieldValueHeight}px;
        overflow : hidden;
    }

    div.shadow {
        background-image: url(/i/bottom-shadow.png);
        background-repeat: repeat-x;
        background-color: transparent;
        height: 50px;
        position: relative;
        top: -50px;
        margin-bottom: -50px;
        display: none;
    }

    div.text-right {
        display: none;
    }

    pre.fieldPreview {
        min-height: 39px;
        font-family: helvetica,arial,verdana,sans-serif !important;
        word-break: normal !important;
        white-space: pre-line !important;
    }

</style>
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
<script type="text/javascript" src="/js/community/group-create.js"></script>
<script type="text/javascript">
    var communityId = ${communityId};

    // Скрыть блок с группой полей
    function slideBlock($link) {
        $panel = $link.closest(".panel-heading").next(".panel-collapse");
        if($link.hasClass("glyphicon-arrow-up")) {
            $panel.slideUp();
            $link.removeClass("glyphicon-arrow-up").addClass("glyphicon-arrow-down");
        } else if ($link.hasClass("glyphicon-arrow-down")) {
            $panel.slideDown();
            $link.removeClass("glyphicon-arrow-down").addClass("glyphicon-arrow-up");
        }
        return false;
    }
    // Инициализация блока с картой
    function initAddressBlock(groupInternalName) {
        var $block = $("[data-group-name=" + groupInternalName + "]");
        var jqGeoPosition = $("[data-field-type=GEO_POSITION]", $block);
        var $map = $(".panel-map", $block);

        ymaps.ready(function () {
            var position = [55.76, 37.64];
            var	placemark = null;
            if (jqGeoPosition && jqGeoPosition.length > 0) {
                var geoPositionValue = jqGeoPosition.val();
                if (geoPositionValue) {
                    position = geoPositionValue.split(",");
                    placemark = new ymaps.Placemark(position, {}, {});
                }
            }
            var map = new ymaps.Map($map.attr("id"), {
                center: position,
                zoom: 16,
                controls: []
            });

            map.controls.add('zoomControl', {
                position: {
                    right: 10,
                    top: 10
                }
            });

            if (placemark) {
                map.geoObjects.add(placemark);
            }
        });
    }

    // Инициализация скрытия длинных полей
    function checkFieldValueHeight() {
        var jqFields = $(".field-node");
        jqFields.each(function(){
            try {
                var jqFieldDiv = $(this);
                var jqFieldValueDiv = $(".field-value", jqFieldDiv);
                var jqShowButtonDiv = $(".show-field-button", jqFieldDiv).parent();
                var jqShadowDiv = $(".shadow", jqFieldDiv);
                if (jqFieldValueDiv.length > 0 && jqFieldValueDiv[0].offsetHeight < jqFieldValueDiv[0].scrollHeight) {
                    jqFieldValueDiv.css("max-height", "${maxFieldValueHeight}px");
                    jqShowButtonDiv.show();
                    jqShadowDiv.show();
                } else {
                    jqShowButtonDiv.hide();
                    jqShadowDiv.hide();
                }
            } catch (e) {
                // do nothing
            }
        });
    }

    // Клик по кнопке развертывания значения поля
    function showFieldValue(jqButton) {
        var jqShowButtonDiv = jqButton.parent();
        var jqFieldDiv = jqButton.closest(".field-node");
        var jqFieldValueDiv = $(".field-value", jqFieldDiv);
        var jqShadowDiv = $(".shadow", jqFieldDiv);

        jqFieldValueDiv.css("height", jqFieldValueDiv.height() + "px");
        jqFieldValueDiv.css("max-height", "none");
        jqFieldValueDiv.animateAuto("height", 300);
        jqShowButtonDiv.remove();
        jqShadowDiv.remove();
        return false;
    }

    function loadInfoPageData(communityId, callBack) {
        $.radomJsonPost(
                "/communities/info_page_data.json",
                {
                    community_id : communityId
                },
                callBack
        );
    }

    function loadParticipantsList(participantIds, callBack) {
        $.radomJsonPost(
                "/communities/participants_list.json",
                {
                    "participant_ids[]" : participantIds
                },
                callBack
        );
    }

    function loadUniversalList(universalListIds, callBack) {
        $.radomJsonPost(
                "/communities/universal_list.json",
                {
                    "list_item_ids[]" : universalListIds
                },
                callBack
        );
    }

    function initInfoPageModel(infoPageData) {
        var model = infoPageData;
        var associationFormInfo = "";
        var accessTypeName = "";
        var isWithOrganization = false;
        switch (infoPageData.community.type) {
            case "COMMUNITY_WITH_ORGANIZATION":
                isWithOrganization = true;
                break;
            case "COMMUNITY_WITHOUT_ORGANIZATION":
                isWithOrganization = false;
                break;
        }

        if (infoPageData.community.associationForm != null) {
            associationFormInfo = infoPageData.community.associationForm.text;
        }

        var isRoot = infoPageData.community.root;
        switch (infoPageData.community.accessType) {
            case 'OPEN':
                accessTypeName = isRoot ? "открытое" : "открытая";
                break;
            case 'CLOSE':
                accessTypeName = isRoot ? "закрытое" : "закрытая";
                break;
            case 'RESTRICTED':
                accessTypeName = isRoot ? "ограниченное" : "ограниченная";
                break;
        }

        model.community.isWithOrganization = isWithOrganization;
        model.community.associationFormInfo = associationFormInfo;
        model.community.accessTypeName = accessTypeName;
        model.community.fieldGroups.sort(function(a, b){
            var result = -1;
            if (parseInt(a.position) > parseInt(b.position)) {
                result = 1;
            }
            return result;
        });

        var fieldGroups = model.community.fieldGroups;
        var participantIds = [];
        var universalListIds = [];
        var participantIdsMap = {};
        var universalListIdsMap = {};
        for (var i in fieldGroups) {
            var fieldGroup = fieldGroups[i];

            fieldGroup.isAddressBlock =
                    fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS' ||
                    fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS' ||
                    fieldGroup.internalName == 'COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION';

            if (fieldGroup.fields != null) {
                var allFieldEmpty = true;
                fieldGroup.fields.sort(function(a, b){
                    var result = -1;
                    if (parseInt(a.position) > parseInt(b.position)) {
                        result = 1;
                    }
                    return result;
                });

                for (var j in fieldGroup.fields) {
                    var field = fieldGroup.fields[j];
                    field.participantsList = field.type == 'PARTICIPANTS_LIST';
                    field.universalList = field.type == 'UNIVERSAL_LIST' || field.type == 'COUNTRY';
                    field.otherType = !field.participantsList && !field.universalList;

                    field.showField =
                    field.internalName != 'COMMUNITY_DIRECTOR_NAME_ID' && field.internalName != 'COMMUNITY_CHIEF_ACCOUNTANT_NAME_ID' &&
                    field.internalName != 'COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID' && field.internalName != 'COMMUNITY_CHAIRMAN_OF_THE_BOARD2_ID' &&
                    field.internalName != 'PRESIDENT_OF_COOPERATIVE_PLOT_ID' && field.internalName != 'REVISOR_OF_COOPERATIVE_PLOT_ID' &&
                    field.internalName != 'COMMUNITY_CHAIRMAN_REVISOR_COMMITTEE_ID' && field.internalName != 'COMMUNITY_TYPE' &&
                    field.internalName != 'COMMUNITY_ASSOCIATION_FORM' &&
                    field.type != 'HIDDEN_TEXT' && field.type != 'ADDRESS_FIELD_DESCRIPTION' &&
                    field.value != null && field.value != '';
                    if (field.showField) {
                        allFieldEmpty = false;
                    }

                    if (field.participantsList && field.showField) {
                        var participants = [];
                        var ids = [];
                        if (field.value != null) {
                            ids = field.value.split(";")
                        }
                        for (var i in ids) {
                            var id = parseInt(ids[i]);
                            if (participantIdsMap[id] == null) {
                                participantIds.push(id);
                                participantIdsMap[id] = id;
                                participants.push({
                                    id : id
                                });
                            }
                        }
                        field.participants = participants;
                    }

                    if (field.universalList && field.showField) {
                        if (field.value != null && field.value != '') {
                            var id = parseInt(field.value);
                            if (universalListIdsMap[id] == null) {
                                universalListIds.push(id);
                            }
                        }
                    }
                }
                fieldGroup.showGroup = !allFieldEmpty;
            }
        }
        model.participantIds = participantIds;
        model.universalListIds = universalListIds;
        return model;
    }

    $(document).ready(function() {
        checkFieldValueHeight();

        $(".show-field-button").click(function(){
            showFieldValue($(this));
        });

        var infoPageTemplate = $("#newsPageTemplate").html();
        Mustache.parse(infoPageTemplate);

        loadInfoPageData(communityId, function(infoPageData){
            initCommunityHead(infoPageData.community);
            initCommunityMenu(infoPageData.community);
            var model = initInfoPageModel(infoPageData);
            var markup = Mustache.render(infoPageTemplate, model);
            //memberId = infoPageData.community.selfMember != null ? newsPageData.community.selfMember.id : null;
            //maxFieldValueHeight = newsPageData.maxFieldValueHeight;
            $("#infoPageDataBlock").append(markup);

            // Загрузка участников для полей типа PARTICIPANT_LIST
            if (model.participantIds != null && model.participantIds.length > 0) {
                loadParticipantsList(model.participantIds, function (participants) {
                    for (var i in participants) {
                        var participant = participants[i];
                        $("#infoPageDataBlock").find(".participantList" + participant.id).text(participant.name);
                    }
                });
            }

            loadUniversalList(model.universalListIds, function(universalLists){
                for (var i in universalLists) {
                    var universalList = universalLists[i];
                    $("#infoPageDataBlock").find(".universalList" + universalList.id).text(universalList.text);
                }
            });


            // Инициализация блоков адресов юр лица
            if ($("[data-group-name=COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS]").length > 0) {
                initAddressBlock("COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS");
            }
            if ($("[data-group-name=COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS]").length > 0) {
                initAddressBlock("COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS");
            }
            // Инициализация блока с картой объекдинения без юр лица.
            if ($("[data-group-name=COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION]").length > 0) {
                initAddressBlock("COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION");
            }

        });

    });
</script>

<script id="newsPageTemplate" type="x-tmpl-mustache">
    <div id="wall-block">
        <dl class="dl-horizontal" style="margin-bottom : 10px;">
            <dt>Тип объединения</dt>
            <dd>
				{{#community.isWithOrganization}}
					Объединение в рамках юридического лица
				{{/community.isWithOrganization}}
				{{^community.isWithOrganization}}
					Объединение вне рамок юридического лица
				{{/community.isWithOrganization}}
			</dd>

            <dt>Организатор</dt>
            <dd>
                <a href="{{community.creatorLink}}">{{community.creatorFullName}}</a>
            </dd>

            <dt>Сфера деятельности</dt>
            <dd>
                {{#community.activityScopes}}
                    <a class="activity-scope" style="white-space: normal;" href="/groups/all?activity_scope_id={{id}}">{{text}}</a>
                {{/community.activityScopes}}
            </dd>

            <dt title="Основоной вид деятельности">Основоной вид деятельности</dt>
            <dd>{{community.mainOkved.code}} {{community.mainOkved.longName}}</dd>

            <dt title="Дополнительные виды деятельности">Дополнительные виды деятельности</dt>
			<dd>
				{{#community.additionalOkveds}}
					{{code}} {{longName}}<br/>
				{{/community.additionalOkveds}}
			</dd>

			<dt>Форма объединения</dt>
			<dd>
				{{community.associationFormInfo}}
			</dd>

            <dt>Доступ к {{#community.root}}объединению{{/community.root}}{{^community.root}}группе{{/community.root}}</dt>
			<dd>
				{{community.accessTypeName}}
			</dd>

			{{#community.isWithOrganization}}
				<dt>Выписка из ЕГРЮЛ</dt>
				<dd>
					<a href="{{community.link}}/ulinfo">Смотреть выписку из ЕГРЮЛ</a>
				</dd>
			{{/community.isWithOrganization}}
        </dl>
    </div>

    <div>
        {{#community.fieldGroups}}
            {{#showGroup}}
                <div class="panel panel-default" id="fields-group-panel-{{id}}" data-group-name="{{internalName}}">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            {{name}}
                            <a data-toggle="tooltip" data-placement="left" title='Скрыть блок' href="#"
                                onclick="return slideBlock($(this));"
                                class="glyphicon glyphicon-arrow-up hidden-group-eye"></a>
                        </h4>
                    </div>
                    <div id="collapse-profile-{{id}}" class="panel-collapse in">
                        <div class="panel-body">
                            {{#fields}}
                                {{#showField}}
                                    <div class="row">
                                        <div class="col-xs-12 field-node">
                                            <label>{{name}}</label>
                                            {{#participantsList}}
                                                <ul>
                                                    {{#participants}}
                                                        <li class="participantList{{id}}">Загрузка...</li>
                                                    {{/participants}}
                                                </ul>
                                            {{/participantsList}}
                                            {{#universalList}}
                                                <div class="field-value fieldContainer">
                                                    <pre
                                                        class="fieldPreview universalList{{value}}"
                                                        data-field-name="{{internalName}}"
                                                        data-field-type="{{type}}"
                                                    >Загрузка...</pre>
                                                    {{#attachedFile}}
                                                        <div class="fieldFileContainer">
                                                            <a class="browseFieldFile"
                                                                has_rights_to_edit="false"
                                                                file_limit="-1"
                                                                title="Просмотреть прикреплённые файлы"
                                                                field_id="{{id}}"
                                                                field_files_url="/group/{{community.id}}/{{id}}/fieldFiles.json"
                                                            ></a>
                                                        </div>
                                                    {{/attachedFile}}
                                                </div>
                                            {{/universalList}}
                                            {{#otherType}}
                                                <div class="field-value fieldContainer">
                                                    <pre
                                                        class="fieldPreview"
                                                        data-field-name="{{internalName}}"
                                                        data-field-type="{{type}}"
                                                    >{{{value}}}</pre>
                                                    {{#attachedFile}}
                                                        <div class="fieldFileContainer">
                                                            <a class="browseFieldFile"
                                                                has_rights_to_edit="false"
                                                                file_limit="-1"
                                                                title="Просмотреть прикреплённые файлы"
                                                                field_id="{{id}}"
                                                                field_files_url="/group/{{community.id}}/{{id}}/fieldFiles.json"
                                                            ></a>
                                                        </div>
                                                    {{/attachedFile}}
                                                </div>
                                            {{/otherType}}

                                            <div class="form-group shadow"></div>
                                            <div class="form-group text-right">
                                                <a href="javascript:void(0)" class="show-field-button btn btn-xs btn-default">Развернуть</a>
                                            </div>
                                        </div>
                                    </div>
                                {{/showField}}
                            {{/fields}}
                            {{#isAddressBlock}}
                                <div class="form-group" name="geo-block">
                                    <br/>
                                    <div id="{{internalName}}_MAP" class="panel-map" style="height : 300px;"></div>
                                    <hr/>
                                </div>
                            {{/isAddressBlock}}
                        </div>
                    </div>
                </div>
            {{/showGroup}}
        {{/community.fieldGroups}}
    </div>
</script>

<t:insertAttribute name="communityHeader" />
<hr/>
<t:insertAttribute name="menu" />

<div id="infoPageDataBlock"></div>
<hr/>