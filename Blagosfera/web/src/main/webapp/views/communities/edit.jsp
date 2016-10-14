<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:include page="../fields/fileField.jsp" />
<jsp:include page="../fields/addressFields.jsp" />
<jsp:include page="../fields/commonFields.jsp" />
<jsp:include page="fields/documentFileSourceForField.jsp" />
<script type="text/javascript" src="/js/community/group-create.js"></script>
<script type="text/javascript">
    var communityId = ${communityId};
    var currentCommunity = null;
    var $mainOkved = null;
    var $additionalOkveds = null;

    function saveCommunity() {
        return save("/group/" + communityId + "/edit.json", currentCommunity, $mainOkved, $additionalOkveds);
    }

    function checkBlockInputs($div) {
        var $input = $div.find("input, select, textarea"),
                changedInput = false,
                button = '<a class="btn btn-primary btn-sm block-save-link" href="#" onclick="return saveCommunity();">Сохранить изменения</a>';

        if($div.find('.form-group').hasClass('changed-form-group')) {
            changedInput = true;
        } else {
            changedInput = false;
        }
        $.each($input, function (key, input) {
            if ($(input).attr('data-field-value') != undefined &&
                    $(input).attr('data-field-value') !== $(input).val() &&
                    !$(input).attr('data-changes-checker-ignore') &&
                    $(input).attr('type') != 'hidden') {
                /*if ($(input).attr('placeholder') == '') {
                    changedInput = true;
                }*/
                /*if ($(input).attr('placeholder') === $(input).val() && $(input).attr('data-field-value') != '' || $(input).val() == '' || $(input).val() != '' && $(input).attr('placeholder') != $(input).val()) {
                    changedInput = true;
                }*/
                changedInput = true;
            }
            if ($(input).attr('data-field-changed')) {
                changedInput = true;
            }
        });

        if(changedInput) {
            if($div.find('.block-save-link').length == 0){
                $div.append(button);
            }
        } else {
            $div.find('.block-save-link').remove();
        }
    }

    function copyСommuinityAddressRegistrationFromActual() {
        $('[data-field-name="COMMUNITY_LEGAL_REGION"]').val($('[data-field-name="COMMUNITY_LEGAL_F_REGION"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_AREA"]').val($('[data-field-name="COMMUNITY_LEGAL_F_AREA"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_LOCALITY"]').val($('[data-field-name="COMMUNITY_LEGAL_F_LOCALITY"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_STREET"]').val($('[data-field-name="COMMUNITY_LEGAL_F_STREET"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_HOUSE"]').val($('[data-field-name="COMMUNITY_LEGAL_F_HOUSE"]').val()).change();
        var $block = $('[data-field-name="POSTAL_CODE"]').closest('.panel-body');
        checkBlockInputs($block);
        return false;
    }

    function copyСommuinityAddressActualFromRegistration() {
        $('[data-field-name="COMMUNITY_LEGAL_F_REGION"]').val($('[data-field-name="COMMUNITY_LEGAL_REGION"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_F_AREA"]').val($('[data-field-name="COMMUNITY_LEGAL_AREA"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_F_LOCALITY"]').val($('[data-field-name="COMMUNITY_LEGAL_LOCALITY"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_F_STREET"]').val($('[data-field-name="COMMUNITY_LEGAL_STREET"]').val()).change();
        $('[data-field-name="COMMUNITY_LEGAL_F_HOUSE"]').val($('[data-field-name="COMMUNITY_LEGAL_HOUSE"]').val()).change();
        var $block = $('[data-field-name="FPOSTAL_CODE"]').closest('.panel-body');
        checkBlockInputs($block);
        return false;
    }

    function loadEditPageData(communityId, callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/edit_page_data.json",
                {},
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

    function initEditPageModel(editPageData) {
        var model = editPageData;

        model.locationOrigin = window.location.origin;

        var associationFormInfo = "";
        var associationFormId = null;
        var accessTypeName = "";
        var isWithOrganization = false;
        switch (model.community.type) {
            case "COMMUNITY_WITH_ORGANIZATION":
                isWithOrganization = true;
                break;
            case "COMMUNITY_WITHOUT_ORGANIZATION":
                isWithOrganization = false;
                break;
        }

        if (model.community.associationForm != null) {
            associationFormInfo = model.community.associationForm.text;
            associationFormId = model.community.associationForm.id;
        }

        var additionalOkvedIds = [];
        if (model.community.additionalOkveds != null) {
            for (var i in model.community.additionalOkveds) {
                var additionalOkved = model.community.additionalOkveds[i];
                additionalOkvedIds.push(additionalOkved.id);
            }
        }

        model.community.mainOkvedId = model.community.mainOkved != null ? model.community.mainOkved.id : "";
        model.community.additionalOkvedIds = additionalOkvedIds.join(";");

        var isRoot = model.community.root;
        var isOpen = false;
        var isClose = false;
        var isRestricted = false;
        switch (model.community.accessType) {
            case 'OPEN':
                accessTypeName = isRoot ? "открытое" : "открытая";
                isOpen = true;
                break;
            case 'CLOSE':
                accessTypeName = isRoot ? "закрытое" : "закрытая";
                isClose = true;
                break;
            case 'RESTRICTED':
                accessTypeName = isRoot ? "ограниченное" : "ограниченная";
                isRestricted = true;
                break;
        }

        model.community.isOpen = isOpen;
        model.community.isClose = isClose;
        model.community.isRestricted = isRestricted;

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
        var participantToFieldMap = {};
        var universalListIds = [];
        var participantIdsMap = {};
        var universalListIdsMap = {};
        for (var i in fieldGroups) {
            var fieldGroup = fieldGroups[i];

            fieldGroup.isAddressBlock =
                    fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS' ||
                    fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS' ||
                    fieldGroup.internalName == 'COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION';

            fieldGroup.isRegistrationAddressBlock = fieldGroup.internalName == "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS";
            fieldGroup.isFactAddressBlock = fieldGroup.internalName == "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS";

            var associationFormVisible = true;
            fieldGroup.withAssociationForms = fieldGroup.associationForms != null && fieldGroup.associationForms.length > 0
            if (fieldGroup.withAssociationForms) {
                associationFormVisible = false;
                for (var i in fieldGroup.associationForms) {
                    var fieldGroupAssociationForm = fieldGroup.associationForms[i];
                    if (fieldGroupAssociationForm.id == associationFormId) {
                        associationFormVisible = true;
                        break;
                    }
                }
            }
            fieldGroup.associationFormVisible = associationFormVisible;

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

                    field.hasPoints = field.points > 0;

                    field.otherField = true;
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
                            field.type != 'SYSTEM' && field.type != 'SYSTEM_IMAGE';
                    if (field.showField) {
                        allFieldEmpty = false;
                    }

                    field.lowerType = field.type.toLowerCase();

                    // Адресное поле
                    field.addressField = field.type == "REGION" || field.type == "DISTRICT" || field.type == "CITY" || field.type == "STREET" || field.type == "BUILDING";
                    //
                    field.countryField = field.type == "COUNTRY";
                    //
                    field.sharerField = field.type == "SHARER";
                    //
                    field.participantsListField = field.type == "PARTICIPANTS_LIST";
                    //
                    field.htmlTextField = field.type == "HTML_TEXT";
                    //
                    field.seoLinkField = field.internalName == "COMMUNITY_SHORT_LINK_NAME";
                    //
                    field.readOnlyField = field.type == "GEO_POSITION" || field.type == "GEO_LOCATION";
                    //
                    field.universalListField = field.type == "UNIVERSAL_LIST";
                    //
                    field.otherField =
                            !field.addressField && !field.countryField && !field.sharerField && !field.participantsListField &&
                            !field.htmlTextField && !field.seoLinkField && !field.readOnlyField && !field.universalListField;

                    //
                    field.countCharsCheck = field.internalName == "COMMUNITY_BRIEF_DESCRIPTION";
                    field.valueText = field.countCharsCheck;


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
                            }
                            participantIdsMap[id] = id;
                            participants.push({
                                id : id
                            });
                            participantToFieldMap[id] = participantToFieldMap[id] == null ? [] : participantToFieldMap[id];
                            participantToFieldMap[id].push(field);
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
                /*if (isWithOrganization) {
                    if (
                            fieldGroup.internalName != 'COMMUNITY_COMMON' &&
                            !fieldGroup.internalName.startsWith('COMMUNITY_WITH_ORGANIZATION_') &&
                            !fieldGroup.internalName.startsWith('COMMUNITY_ADDITIONAL_GROUP_')
                    ) {
                        fieldGroup.showGroup = false;
                    }
                } else {
                    if (
                            fieldGroup.internalName == 'COMMUNITY_COMMON' ||
                            fieldGroup.internalName.startsWith('COMMUNITY_WITH_ORGANIZATION_') ||
                            fieldGroup.internalName.startsWith('COMMUNITY_ADDITIONAL_GROUP_')
                    ) {
                        fieldGroup.showGroup = false;
                    }
                }*/

            }
        }
        console.log(model);
        model.participantIds = participantIds;
        model.participantToFieldMap = participantToFieldMap;
        model.universalListIds = universalListIds;
        return model;
    }

    // Инициализация формы объединения
    function initAssociationForm(community) {
        var associationFormId = community.associationForm == null ? null : community.associationForm.id;

        RameraListEditorModule.init(
                $("#associationForm"),
                {
                    labelClasses: ["checkbox-inline"],
                    labelStyle: "margin-left: 10px;",
                    selectedItems: [associationFormId],
                    selectClasses: ["form-control"]
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        community.associationForm = {
                            id : data.value
                        };
                        $("[data-has-association-forms=true]").hide();
                        $("[data-association-form-" + data.value + "]").show();
                    }
                }
        );
    }

    // Инициализация полей
    function initFields(currentUser) {
        //$("[data-field-name=COMMUNITY_DIRECTOR_NAME_ID]").attr('data-changes-checker-ignore', true);

        var $div = $('.panel-body'),
        $select = $div.find('select');

        $.each($select, function (key, select) {
            $(select).attr('data-field-value', $(select, "option:selected").val());
        });

        $.each($("div.form-group"), function(index, div) {
            var $div = $(div);
            var $block = $div.closest('.panel-body');
            var $input = $div.find("input, select, textarea");
            //checkFormGroup($div);
            $input.keyup(function() {
                //checkFormGroup($div);
                checkBlockInputs($block);
            });
            $input.click(function() {
                //checkFormGroup($div);
                checkBlockInputs($block);
            });
            $input.change(function() {
                //checkFormGroup($div);
                checkBlockInputs($block);
            });
            $input.blur(function() {
                //checkFormGroup($div);
                checkBlockInputs($block);
            });
        });
        initAllFields(currentUser);
    }

    function onChangeCountryField(listEditorItemData) {
        // TODO
        /*var $div = listEditorItemData.domNode.closest("div.form-group");
        var $block = $div.closest('.panel-body');
        var $input = $div.find("select");
        checkFormGroup($div, $input, listEditorItemData.text);
        if($block.length > 0) {
            checkBlockInputs($block);
        }*/
    }

    function initAddressFields() {
        // инициализация компонентов универсальных списков (страна)
        initCountryField(
                "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS", "COMMUNITY_LEGAL_F_COUNTRY", "COMMUNITY_LEGAL_F_POST_CODE",
                onChangeCountryField, null,
                RoomTypes.OFFICE, "COMMUNITY_LEGAL_F_OFFICE",
                "COMMUNITY_LEGAL_F_REGION_CODE", "COMMUNITY_LEGAL_F_STREET_DESCRIPTION_SHORT",
                "COMMUNITY_LEGAL_F_DISTRICT_DESCRIPTION_SHORT", "COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT");
        initCountryField(
                "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS", "COMMUNITY_LEGAL_COUNTRY", "COMMUNITY_LEGAL_POST_CODE",
                onChangeCountryField, null,
                RoomTypes.OFFICE, "COMMUNITY_LEGAL_OFFICE",
                "COMMUNITY_LEGAL_REGION_CODE", "COMMUNITY_LEGAL_STREET_DESCRIPTION_SHORT",
                "COMMUNITY_LEGAL_DISTRICT_DESCRIPTION_SHORT", "COMMUNITY_LEGAL_CITY_DESCRIPTION_SHORT");

        initCountryField(
                "COMMUNITY_WITHOUT_ORGANIZATION_GEOGRAPHICAL_POSITION", "COMMUNITY_COUNTRY", "COMMUNITY_POST_CODE",
                onChangeCountryField, null,
                RoomTypes.OFFICE, "COMMUNITY_OFFICE",
                null, null,
                null, null);



        $("[data-field-name=OFFICE_RENT_PERIOD]").closest(".row").hide();
        // При изменении значения универсального списка
        $(universalListListener).bind("OFFICE_OWNERSHIP_TYPE", function(event, officeOwnerShipTypeData) {
            switch(officeOwnerShipTypeData.code) {
                case "rent_apartment": // Арендованные площади
                    $("[data-field-name=OFFICE_RENT_PERIOD]").closest(".row").show();
                    break;
                case "own_apartment": // Свои площади
                    $("[data-field-name=OFFICE_RENT_PERIOD]").closest(".row").hide();
                    break;
            }
        });
        $("[data-field-name=FACT_OFFICE_RENT_PERIOD]").closest(".row").hide();
        // При изменении значения универсального списка
        $(universalListListener).bind("FACT_OFFICE_OWNERSHIP_TYPE", function(event, officeOwnerShipTypeData) {
            switch(officeOwnerShipTypeData.code) {
                case "fact_rent_apartment": // Арендованные площади
                    $("[data-field-name=FACT_OFFICE_RENT_PERIOD]").closest(".row").show();
                    break;
                case "fact_own_apartment": // Свои площади
                    $("[data-field-name=FACT_OFFICE_RENT_PERIOD]").closest(".row").hide();
                    break;
            }
        });
    }

    function initActivityScopes(community) {
        var activityScopeIds = [];
        if (community.activityScopes != null) {
            for (var i in community.activityScopes) {
                var activityScope = community.activityScopes[i];
                activityScopeIds.push(activityScope.id);
            }
        }

        // Инициализация сфер деятельности
        RameraListEditorModule.init($("#activityScopes"),
                {
                    labelClasses : ["checkbox-inline"],
                    labelStyle : "margin-left: 10px;",
                    selectedItems : activityScopeIds,
                    selectClasses: ["form-control"]
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        community.activityScopes = [{id:data.value}];
                    }
                }
        );
    }

    function initOkveds() {
        $mainOkved = $("input#mainOkved").okvedInput({
            singleSelect: true,
            title: "Выбор основного вида деятельности"
        });
        $additionalOkveds = $("input#additionalOkveds").okvedInput({
            title: "Выбор дополнительных видов деятельности"
        });
    }

    function initHints() {
        //$("form#create-community-form").changesChecker();
        //$("input[name=seo_link]").filteredInput("/?");

        $('a.hidden-field-eye').radomTooltip();
        $('a.hidden-group-eye').radomTooltip();
    }

    $(document).ready(function() {
        $(communityEventsManager).bind("community_saved", function(event, response){ // Данные объединения были сохранены
            $.each($("div.form-group"), function(index, div) {
                var $div = $(div);
                var $block = $div.closest('.panel-body');
                checkBlockInputs($block);
            });
        });

        var pageTemplate = $("#editPageTemplate").html();
        Mustache.parse(pageTemplate);

        var participantListTemplate = $("#participantListTemplate").html();
        Mustache.parse(participantListTemplate);

        $(eventManager).bind("inited", function(event, currentUser) {
            loadEditPageData(communityId, function(editPageData){
                currentCommunity = editPageData.community;
                initCommunityHead(editPageData.community);
                initCommunityMenu(editPageData.community);
                var model = initEditPageModel(editPageData);
                var markup = Mustache.render(pageTemplate, model);
                $("#editPageDataBlock").append(markup);

                // Загрузка участников для полей типа PARTICIPANT_LIST
                if (model.participantIds != null && model.participantIds.length > 0) {
                    loadParticipantsList(model.participantIds, function (participants) {
                        for (var i in participants) {
                            var participant = participants[i];
                            var fields = model.participantToFieldMap[participant.id];
                            for (var j in fields) {
                                var field = fields[j];
                                var participantsMarkup = Mustache.render(participantListTemplate, {
                                    participant: participant,
                                    field: field
                                });
                                $("#editPageDataBlock")
                                        .find("[data-participants-list-field=" + field.id + "]")
                                        .find(".participantList" + participant.id).html(participantsMarkup);
                            }
                        }
                    });
                }

                // Инициализация формы объединения
                initAssociationForm(editPageData.community);
                // Общая инициализация полей
                initFields(currentUser);
                // Инициализация адресных полей
                initAddressFields();
                // Инициализация сфер деятельности объединения
                initActivityScopes(editPageData.community);
                //
                initOkveds();
                //
                initHints();
            });
        });
    });
</script>

<script id="participantListTemplate" type="x-tmpl-mustache">
    {{participant.name}}
    <a
        data-{{field.internalName}}-id='{{participant.id}}'
        data-input-id="{{field.internalName}}"
        class='{{field.internalName}}-delete-link
        glyphicon glyphicon-remove'
        href='javascript:void(0)'></a>
</script>

<script id="editPageTemplate" type="x-tmpl-mustache">
    <h2><small>редактирование</small></h2>
    {{#community.verified}}
        <h4><small>Объединение сертифицировано, редактирование запрещено</small></h4>
    {{/community.verified}}
    <hr/>

    {{#community.isWithOrganization}}
        <div class="panel-body radio-block">
            <div class="form-group">
                <label>Форма объединения</label>
                <div data-field-name='COMMUNITY_ASSOCIATION_FORM' id="associationForm" rameraListEditorName="community_association_forms_groups"></div>
                <span class="help-block help-block-info">Укажите одну из доступных форм объединения.</span>
                <span style="display : none;" class="help-block help-block-error"></span>
            </div>
        </div>
    {{/community.isWithOrganization}}

    {{#community.fieldGroups}}
        {{#showGroup}}
            <div
                class="panel panel-default"
                id="fields-group-panel-{{id}}"
                data-group-name="{{internalName}}"
                {{#associationForms}}
                data-association-form-{{id}}="true"
                {{/associationForms}}
                data-has-association-forms="{{withAssociationForms}}"
                style="{{^associationFormVisible}}display:none;{{/associationFormVisible}}"
                >
                <div class="panel-heading">
                    <h4 class="panel-title">
                        {{name}}
                        <a
                            data-toggle="tooltip"
                            data-placement="left"
                            title='Скрыть блок'
                            href="#"
                            onclick="return slideBlock($(this));"
                            class="glyphicon glyphicon-arrow-up hidden-group-eye"></a>

                        {{#isRegistrationAddressBlock}}
                            <a class="pull-right collapse-group-control small" id="hideAll" href="#" onclick="return copyСommuinityAddressRegistrationFromActual();">Скопировать из фактического адреса</a>
                        {{/isRegistrationAddressBlock}}
                        {{#isFactAddressBlock}}
                            <a class="pull-right collapse-group-control small" id="hideAll" href="#" onclick="return copyСommuinityAddressActualFromRegistration();">Скопировать из юридического адреса</a>
                        {{/isFactAddressBlock}}

                        <a data-toggle="tooltip" data-placement="left"
                        title='Скрыть все поля в этой группе' href="#"
                        onclick="return communityChangeGroupHidden($(this), '{{community.id}}', '{{id}}', true);"
                        class="glyphicon glyphicon-eye-close hidden-group-eye"></a>
                        <a data-toggle="tooltip" data-placement="left"
                        title='Показать все поля в этой группе' href="#"
                        onclick="return communityChangeGroupHidden($(this), '{{community.id}}', '{{id}}', false);"
                        class="glyphicon glyphicon-eye-open hidden-group-eye"></a>
                    </h4>
                </div>
                <div id="collapse-profile-{{id}}" class="panel-collapse in">
                    <div class="panel-body">
                        {{#fields}}
                            {{#showField}}
                                <div class="row">
                                    <div class="{{#hideable}}col-xs-11{{/hideable}}{{^hideable}}col-xs-12{{/hideable}}">
                                        <div class="form-group{{#hideable}} hideable{{/hideable}}" data-required="{{required}}" data-has-points="{{hasPoints}}">
                                        <label>{{name}}</label>
                                        <div class="fieldContainer" id="fieldContainer_{{internalName}}">
                                            {{#addressField}}
                                                <input type="text" class="form-control" data-kladr-object-type='{{lowerType}}'
                                                value='{{value}}'
                                                name='f:{{id}}' data-field-name='{{internalName}}'
                                                data-field-type='{{type}}' data-field-id='{{id}}'
                                                data-field-value='{{value}}'
                                                placeholder='{{example}}'
                                                mask='{{mask}}'
                                                mask_placeholder='{{placeholder}}'/>
                                            {{/addressField}}
                                            {{#sharerField}}
                                                <div style="width: 100%; position: relative; height: 34px;">
                                                    <div style="position: absolute; left: 0px; right: 0px;">
                                                        <input type="text" class="form-control"
                                                        id="{{internalName}}"
                                                        value='{{value}}'
                                                        name='f:{{id}}' data-field-name='{{internalName}}'
                                                        data-field-type='{{type}}' data-field-id='{{id}}'
                                                        data-field-value='{{value}}'
                                                        data-sharer-url='/group/{{community.id}}/search_members.json'
                                                        placeholder='{{example}}' />
                                                    </div>
                                                </div>
                                            {{/sharerField}}
                                            {{#participantsListField}}
                                                <div style="width: 100%; position: relative; height: 34px;">
                                                    <div style="position: absolute; left: 0px; right: 0px;">
                                                        <input type="text" class="form-control"
                                                        id="{{internalName}}" value=''
                                                        name='f:{{id}}' data-field-name='{{internalName}}'
                                                        data-field-type='{{type}}' data-field-id='{{id}}'
                                                        data-field-value='{{value}}'
                                                        data-source-value='{{value}}'
                                                        data-sharer-url='/group/{{community.id}}/search_members.json'
                                                        placeholder='{{example}}' />
                                                    </div>
                                                </div>
                                                <div style="margin-top: 5px;">
                                                    <ul id="{{internalName}}_{{type}}" data-participants-list-field="{{id}}">
                                                        {{#participants}}
                                                            <li class="participantList{{id}}">Загрузка...</li>
                                                        {{/participants}}
                                                    </ul>
                                                </div>
                                            {{/participantsListField}}
                                            {{#htmlTextField}}
                                                <textarea class="form-control" rows="10"
                                                name='f:{{id}}' data-field-name='{{internalName}}'
                                                data-field-type='{{type}}' data-field-id='{{id}}'
                                                data-field-value='{{value}}'
                                                placeholder='{{example}}' data-count-chars-check="{{countCharsCheck}}"
                                                data-value-text="{{valueText}}" >{{value}}</textarea>
                                            {{/htmlTextField}}
                                            {{#seoLinkField}}
                                                <div class="input-group">
                                                <div class="input-group-addon community-seo-link-addon">
                                                <span class="community-seo-link-base">{{locationOrigin}}/group/
                                                </div>
                                                <input type="text" class="form-control"
                                                       value='{{value}}'
                                                       name='f:{{id}}' data-field-name='{{internalName}}'
                                                       data-field-type='{{type}}' data-field-id='{{id}}'
                                                       data-field-value='{{value}}'
                                                       placeholder='{{example}}'
                                                       mask='{{mask}}'
                                                       mask_placeholder='{{placeholder}}'
                                                       />
                                                </div>
                                            {{/seoLinkField}}
                                            {{#countryField}}
                                                <input type="hidden" class="form-control"
                                                       data-field-type='{{type}}'
                                                       data-field-value='{{value}}'
                                                       data-field-id='{{id}}'
                                                       name='f:{{id}}'
                                                       data-field-internal-name="{{internalName}}"
                                                       value='{{value}}' />
                                                <input type="hidden" class="country-control" data-field-internal-name="{{internalName}}_NAME" />
                                                <div id="{{internalName}}" rameraListEditorName="country_id"></div>
                                            {{/countryField}}
                                            {{#readOnlyField}}
                                                <input type="text" class="form-control" readonly="readonly"
                                                       value='{{value}}'
                                                       name='f:{{id}}' data-field-name='{{internalName}}'
                                                       data-field-type='{{type}}' data-field-id='{{id}}'
                                                       data-field-value='{{value}}'
                                                       placeholder='{{example}}' />
                                            {{/readOnlyField}}
                                            {{#universalListField}}
                                                <div class="universalList" rameraListEditorName="{{internalName}}"></div>
                                                <input type="hidden"
                                                       value='{{value}}'
                                                       name='f:{{id}}' data-field-name='{{internalName}}'
                                                       data-field-type='{{type}}' data-field-id='{{id}}'
                                                       data-field-value='{{value}}'
                                                        />
                                            {{/universalListField}}
                                            {{#otherField}}
                                                <input type="text" class="form-control"
                                                value='{{value}}'
                                                name='f:{{id}}' data-field-name='{{internalName}}'
                                                data-field-type='{{type}}' data-field-id='{{id}}'
                                                data-field-value='{{value}}'
                                                placeholder='{{example}}'
                                                mask='{{mask}}'
                                                mask_placeholder='{{placeholder}}'/>
                                            {{/otherField}}

                                            {{#attachedFile}}
                                                <div class="fieldFileContainer" id="fieldFileContainer_{{internalName}}">
                                                    <a class="browseFieldFile"
                                                       has_rights_to_edit="true"
                                                       file_limit="-1"
                                                       title="Просмотреть прикреплённые файлы"
                                                       field_id="{{id}}"
                                                       field_files_url="/group/{{community.id}}/{{id}}/fieldFiles.json"
                                                       field_files_save_url="/group/{{community.id}}/{{id}}/saveFieldFiles.json"
                                                    ></a>
                                                </div>
                                            {{/attachedFile}}
                                        </div>
                                        <span class="help-block">{{comment}}</span>
                                        <span style="display : none;" class="help-block help-block-error"></span>
                                        </div>
                                    </div>
                                    {{#hideable}}
                                        <div class="col-xs-1">
                                            <a href="#"
                                               tabindex="-1"
                                               data-toggle="tooltip"
                                               data-placement="right"
                                               title='{{#hidden}}Сейчас это поле скрыто{{/hidden}}{{^hidden}}Сейчас это поле видно всем{{/hidden}}'
                                               onclick="return changeHidden($(this), '{{community.id}}')"
                                               class="glyphicon hidden-field-eye{{^hidden}} glyphicon-eye-open{{/hidden}}{{#hidden}} glyphicon-eye-close{{/hidden}}"
                                               data-field-id="{{id}}"
                                               data-hidden="{{hidden}}"></a>
                                        </div>
                                    {{/hideable}}
                                </div>
                            {{/showField}}
                            {{^showField}}
                                <input type="hidden" class="form-control"
                                       id="{{internalName}}"
                                       value='{{value}}'
                                       name='f:{{id}}'
                                       data-field-name='{{internalName}}'
                                       data-field-type='{{type}}' data-field-id='{{id}}'
                                       data-field-value='{{value}}'/>
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

    <div class="panel-body radio-block" id="access-form-group">
        <div class="form-group">
            <div data-field-name='access_type'>
                <label>Доступ к {{#community.root}}объединению{{/community.root}}{{^community.root}}группе{{/community.root}} сторонним лицам</label>
                <br/>
                <label class="checkbox-inline">
                    <input type="radio" name="access_type" value="OPEN" {{#community.isOpen}}checked="checked"{{/community.isOpen}} > {{#community.root}}Открытое{{/community.root}}{{^community.root}}Открытая{{/community.root}}
                </label>
                <label class="checkbox-inline">
                    <input type="radio" name="access_type" value="CLOSE" {{#community.isClose}}checked="checked"{{/community.isClose}} > {{#community.root}}Закрытое{{/community.root}}{{^community.root}}Закрытая{{/community.root}}
                </label>
                <label class="checkbox-inline">
                    <input type="radio" name="access_type" value="RESTRICTED" {{#community.isRestricted}}checked="checked"{{/community.isRestricted}} > {{#community.root}}Ограниченное{{/community.root}}{{^community.root}}Ограниченная{{/community.root}}
                </label>
                <label class="checkbox-inline">
                    <input type="checkbox" name="invisible" value="true" {{^community.visible}}checked="checked"{{/community.visible}} > {{#community.root}}Невидимое{{/community.root}}{{^community.root}}Невидимая{{/community.root}}
                </label>
                <span class="help-block help-block-info">
                    {{#community.root}}
                        Укажите каким будет объединение.
                        Открытое - это значит в него может вступить любой желающий.
                        Закрытое - в объединение можно вступить только с одобрения организатора объединения.
                        Ограниченное - указаны строгие правила, по которым система сама ограничивает доступ к объединению.
                        Если желаете, чтобы объединение не присутствовало в поиске, то поставьте галочку на пункте Невидимое.
                    {{/community.root}}
                    {{^community.root}}
                        Укажите какой будет группа.
                        Открытая - это значит в нее может вступить любой желающий.
                        Закрытая - в группу можно вступить только с одобрения организатора группы.
                        Ограниченная - указаны строгие правила, по которым система сама ограничивает доступ к группе.
                        Если желаете, чтобы группа не присутствовало в поиске, то поставьте галочку на пункте Невидимая.
                    {{/community.root}}
                </span>
                <span style="display : none;" class="help-block help-block-error"></span>
            </div>
        </div>
    </div>

    {{#community.root}}
        <div class="form-group">
            <label>Сфера деятельности</label>
            <br/>
            <div id="activityScopes" data-field-name='activity_scope' rameraListEditorName="activity_scope_id"></div>
            <span class="help-block help-block-info">Укажите одну или несколько сфер деятельности объединения.</span>
            <span style="display : none;" class="help-block help-block-error"></span>
        </div>

        <div class="form-group">
            <label>Основной вид деятельности объединения</label>
            <input type="text" class="form-control" id="mainOkved" name="mainOkved" excluded_values="additionalOkveds" value="{{community.mainOkvedId}}" />
            <span class="help-block help-block-info">Укажите один вид деятельности объединения.</span>
        </div>

        <div class="form-group">
            <label>Дополнительные виды деятельности объединения</label>
            <input type="text" class="form-control" id="additionalOkveds" name="additionalOkveds" excluded_values="mainOkved" value="{{community.additionalOkvedIds}}" />
            <span class="help-block help-block-info">Укажите один или несколько видов деятельности объединения. Максимальное количество: 56.</span>
        </div>
    {{/community.root}}

    <div class="alert alert-danger" role="alert" style="display : none;"></div>

    <div class="form-group">
        <a class="btn btn-primary btn-sm" href="#" onclick="return saveCommunity();" id="create-button">Сохранить изменения</a>
    </div>
</script>

<t:insertAttribute name="communityHeader" />
<t:insertAttribute name="menu" />
<div id="testTemplates"></div>
<div id="editPageDataBlock"></div>
<hr/>