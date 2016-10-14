<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<jsp:include page="../../fields/fileField.jsp" />
<jsp:include page="../../fields/addressFields.jsp" />
<jsp:include page="../../fields/commonFields.jsp" />
<style>
    .pageContainer {margin-top: 15px;}
    .participantContainer {position: relative; height: 34px;}
    .participantsSelectContainer {display: inline-block; vertical-align: top; position: absolute; left: 0px; right: 91px;}
    .participantsSelectContainer .bootstrap-select  {width: 100% !important;}
    .participantsButtonsContainer {display: inline-block; vertical-align: top; position: absolute; right: 0px; width: 89px;}

    #tableContainer table {
        width: 100%;
        border: 1px solid #000;
        border-collapse: collapse;
    }
    #tableContainer th {
        text-align: left;
        background: #ccc;
        padding: 5px;
        border: 1px solid black;
        text-align: center;
    }
    #tableContainer td {
        padding: 5px;
        border: 1px solid black;
    }
</style>
<script type="text/javascript">
    var communityId = "${communityId}";
    var minCountParticipants = 0;
    var nowDate = new Date();
    var defaultInputValues = {};

    function loadCreateKuchPageData(callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/create_kuch_page_data.json",
                {},
                callBack
        );
    }

    $(document).ready(function() {
        $(eventManager).bind("inited", function(event, currentUser) {
            loadCreateKuchPageData(function (createKuchPageData) {
                initCommunityHead(createKuchPageData.community);
                initCommunityMenu(createKuchPageData.community);
                initCreateKuchPage(createKuchPageData, currentUser);
            });
        });
    });

    function initCreateKuchPage(createKuchPageData, user) {
        minCountParticipants = createKuchPageData.minCountParticipants;
        if (createKuchPageData.requirementsForCreateMeeting == null) {
            initPage(createKuchPageData, user);
        } else {
            initErrorPage(createKuchPageData, user);
        }
    }

    function initErrorPage(createKuchPageData, user) {
        var requirementsTemplate = $("#requirementsTemplate").html();
        Mustache.parse(requirementsTemplate);

        var model = createKuchPageData;
        model.user = user;
        var jqContent = $(Mustache.render(requirementsTemplate, model));
        $("#createKuchBlock").append(jqContent);
    }

    function initPage(createKuchPageData, user) {
        var votingTypesArr = [];
        for (var typeName in createKuchPageData.votingTypes) {
            var votingType = {
                name : typeName,
                value : createKuchPageData.votingTypes[typeName]
            };
            votingTypesArr.push(votingType);
        }
        createKuchPageData.votingTypesArr = votingTypesArr;


        var createKuchPageTemplate = $("#createKuchPageTemplate").html();
        Mustache.parse(createKuchPageTemplate);

        var model = createKuchPageData;
        model.user = user;
        model.userPostNames = "";
        for (var index in createKuchPageData.communityMembers) {
            var member = createKuchPageData.communityMembers[index];
            var memberPosts = "";
            member.isCurrentUser = false;
            if (member.postNames != null) {
                memberPosts = member.postNames.join(", ");
            }
            if (user.id == member.user.id) {
                member.isCurrentUser = true;
                model.userPostNames = memberPosts;
            }
        }

        model.addressKuch = initEditFieldsModel(createKuchPageData);

        var jqContent = $(Mustache.render(createKuchPageTemplate, model));
        $("#createKuchBlock").append(jqContent);

        initAllFields();

        var jqAddressOverlayDiv = $("<div style='position: absolute; top: 0px; right: 0px; bottom: 0px; left: 0px; background-color: #000000;'></div>");
        $("input[data-field-name]").each(function(){
            var internalName = $(this).attr("data-field-name");
            var value = $(this).val();
            defaultInputValues[internalName] = value;
        });
        defaultInputValues['COMMUNITY_LEGAL_F_COUNTRY'] = $("[data-field-internal-name='COMMUNITY_LEGAL_F_COUNTRY']").val();

        // Лочим блок с адресом
        $("#blockAddress").click(function(){
            clickBlockAddressBlock($(this), jqAddressOverlayDiv);
        });
        clickBlockAddressBlock($("#blockAddress"), jqAddressOverlayDiv);



        $("#meetingTargets").css("height", "500px").radomTinyMCE({});

        $("#participants").attr("title", "Никто не выбран");
        $("#participants").selectpicker("refresh");
        $("#participants").selectpicker("val", null);

        $(".addParticipant").click(function(){
            var jqOption = $("#participants").find("option:selected");
            if (jqOption.length == 0 || jqOption.prop("disabled")) {
                bootbox.alert("Необходимо выбрать участника!");
            } else {
                jqOption.prop("disabled", true);
                $("#participants").selectpicker("refresh");
                var participantId = jqOption.val();
                var participantName = jqOption.text();
                var participantPostName = jqOption.attr("post");

                var index = $("tr", "#participantList").length + 1;

                $("#participantList").append(
                        "<tr id='" + participantId + "'>" +
                            "<td class='memberIndex'>" + index + "</td>" +
                            "<td>" + participantName + "</td>" +
                            "<td>" + participantPostName + "</td>" +
                            "<td><a href='javascript:void(0)' id='deleteParticipant" + participantId + "' participant_id='" + participantId + "' class='btn btn-danger'>Удалить</a></td>" +
                        "</tr>"
                );
                initDeleteButton($("#deleteParticipant" + participantId));
            }
        });

        // Количество дней между стартом и окончанием
        var periodDateHoursDelta = parseInt('${radom:systemParameter("kuch.period.hours.delta", "3")}');

        var defaultStartDate = new Date(nowDate.getTime());
        var defaultEndDate = new Date(nowDate.getTime());
        defaultEndDate.setTime(defaultEndDate.getTime() + periodDateHoursDelta * 60 * 60 * 1000);

        $('#dateRange').radomDateRangeInput({
            startDate: defaultStartDate.format("dd.mm.yyyy HH:MM"),
            endDate: defaultEndDate.format("dd.mm.yyyy HH:MM"),
            minDate: defaultStartDate.format("dd.mm.yyyy HH:MM")
        }, function(startDate, endDate) {
            setPeriodText(startDate, endDate);
        });

        var dateStart = $('#dateRange').data().daterangepicker.startDate.toDate();
        var dateEnd = $('#dateRange').data().daterangepicker.endDate.toDate();
        setPeriodText(dateStart, dateEnd);

        // Количество часов на регистрацию
        var registrationEndDateHoursAdd = parseInt('${radom:systemParameter("kuch.registration.hours.add", "24")}');

        var defaultRegistrationEndDate = new Date(nowDate.getTime());
        defaultRegistrationEndDate.setTime(defaultRegistrationEndDate.getTime() + registrationEndDateHoursAdd * 60 * 60 * 1000);
        $("#votersRegistrationEndDate").radomDateTimeInput({
            defaultDate : defaultRegistrationEndDate
        });

        $("#saveMeeting").click(function(){
            saveMeeting();
        })
    }

    function setPeriodText(startDate, endDate){
        var secconds = (endDate.getTime() - startDate.getTime()) / 1000;
        var days = parseInt(secconds / (60 * 60 * 24));
        var seccondsDays = days * 60 * 60 * 24;
        var seccondsHours = secconds - seccondsDays;
        var hours = parseInt(seccondsHours / (60 * 60));
        seccondsHours = hours * 60 * 60;
        var seccondsMinutes = secconds - seccondsDays - seccondsHours;
        var minutes = parseInt(seccondsMinutes / 60);

        var dayForm = stringForms(days, "день", "дня", "дней");
        var hourForm = stringForms(hours, "час", "часа", "часов");
        var minutForm = stringForms(minutes, "минута", "минуты", "минут");

        $("#datePeriodText").text("Собрание будет длиться " + days + " " + dayForm + " " + hours + " " + hourForm + " " + minutes + " " + minutForm);
    }

    function initDeleteButton(jqButton){
        jqButton.click(function(){
            var participantId = jqButton.attr("participant_id");
            $("#" + participantId, "#participantList").remove();
            $("[value=" + participantId + "]","#participants").prop("disabled", false);
            $("#participants").selectpicker("refresh");

            // Обновляем номера участников в таблице
            var index = 1;
            $(".memberIndex").each(function(){
                $(this).text(index++);
            })
        });
    }

    function saveMeeting() {
        if ($("#meetingName").val() == '') {
            bootbox.alert("Укажите имя Кооперативного участка!");
            return false;
        }
        if ($("#meetingTargets").val() == '') {
            bootbox.alert("Укажите цели и задачи Кооперативного участка!");
            return false;
        }
        if ($(".memberIndex").length < minCountParticipants) {
            bootbox.alert("Участников создания Кооперативного участка должно быть не меньше " + minCountParticipants + "!");
            return false;
        }
        var dateStart = $('#dateRange').data().daterangepicker.startDate.toDate();
        var dateEnd = $('#dateRange').data().daterangepicker.endDate.toDate();
        if (dateStart != null && dateEnd != null && dateStart.getTime() > dateEnd.getTime()) {
            bootbox.alert("Дата окончания собрания должна больше даты начала собрания!");
            return false;
        }

        var votersRegistrationEndDate = $("#votersRegistrationEndDate").data().DateTimePicker.date().toDate();
        if (votersRegistrationEndDate.getTime() < dateStart.getTime() || votersRegistrationEndDate.getTime() > dateEnd.getTime()) {
            bootbox.alert("Дата окончания регистрации в собрании должна больше даты начала собрания и меньше даты окончания собрания!");
            return false;
        }

        var votersArr = [];
        $("#participantList").find("tr").each(function(){
            votersArr.push($(this).attr("id"));
        });
        var voters = votersArr.join(",");

        var dateStart = $('#dateRange').data().daterangepicker.startDate.toDate();
        var dateEnd = $('#dateRange').data().daterangepicker.endDate.toDate();
        // Изменяем время на то, что без учёта тайм зоны
        var dateStartForm = new Date(dateStart.getTime() + dateStart.getTimezoneOffset() * 60 * 1000);
        var dateEndForm = new Date(dateEnd.getTime() + dateEnd.getTimezoneOffset() * 60 * 1000);
        var votersRegistrationEndDateForm = new Date(votersRegistrationEndDate.getTime() + votersRegistrationEndDate.getTimezoneOffset() * 60 * 1000);

        var dateStartValue = dateStartForm.format("dd.mm.yyyy HH:MM");
        var dateEndValue = dateEndForm.format("dd.mm.yyyy HH:MM");
        var votersRegistrationEndDateValue = votersRegistrationEndDateForm.format("dd.mm.yyyy HH:MM");

        var isAddVotingItemsAllowed = false; // TODO

        // Собираем данные по адресу
        var addressFields = "";
        $("[data-field-id]").each(function(){
            var fieldId = $(this).attr("data-field-id");
            var fieldValue = $(this).val();
            if (addressFields == "") {
                addressFields += fieldId + ":" +  fieldValue;
            } else {
                addressFields += ";" + fieldId + ":" +  fieldValue;
            }
        });
        addressFields = encodeURIComponent(addressFields);

        // Тип голосования
        if ($(".votingType:checked").length == 0) {
            bootbox.alert("Необходимо выбрать тип голосования!");
            return;
        }
        var votingType = $(".votingType:checked").attr("voting_type");

        var data = {
            name : $("#meetingName").val(),
            meetingTargets : $("#meetingTargets").val(),
            meetingTheme : $("#meetingTheme").val(),
            dateStartValue : dateStartValue,
            dateEndValue : dateEndValue,
            votersRegistrationEndDate : votersRegistrationEndDateValue,
            isAddVotingItemsAllowed : isAddVotingItemsAllowed,
            voters : voters,
            addressFields : addressFields,
            votingType : votingType
        };

        $.radomJsonPostWithWaiter("save_meeting.json", data, null, null, {
            "neededParameters" : ["batchVotingId"],
            "content" : "Запрос на создание Кооперативного Участка выполнен успешно. " +
            "<a href='/votingsystem/registrationInVoting.html?batchVotingId={batchVotingId}' class='btn btn-primary'>Перейти к регистрации в собрании</a>"
        });
    }

    function initAddressFields(inputValues) {
        var addressInputValues = {
            countryId : inputValues["COMMUNITY_LEGAL_F_COUNTRY"],
            postalCode : inputValues["COMMUNITY_LEGAL_F_POST_CODE"],
            region : inputValues["COMMUNITY_LEGAL_F_REGION"],
            district : inputValues["COMMUNITY_LEGAL_F_AREA"],
            city : inputValues["COMMUNITY_LEGAL_F_LOCALITY"],
            street : inputValues["COMMUNITY_LEGAL_F_STREET"],
            building : inputValues["COMMUNITY_LEGAL_F_HOUSE"],
            room : inputValues["COMMUNITY_LEGAL_F_OFFICE"],
            geoLocation : inputValues["COMMUNITY_LEGAL_F_GEO_LOCATION"],
            geoPosition : inputValues["COMMUNITY_LEGAL_F_GEO_POSITION"],
            regionCode :  inputValues["COMMUNITY_LEGAL_F_REGION_CODE"],
            streetDescriptionShort : inputValues["COMMUNITY_LEGAL_F_STREET_DESCRIPTION_SHORT"],
            districtDescriptionShort : inputValues["COMMUNITY_LEGAL_F_DISTRICT_DESCRIPTION_SHORT"],
            cityDescriptionShort :  inputValues["COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT"],
            buildingDescription : inputValues["COMMUNITY_LEGAL_F_HOUSE_DESCRIPTION"],
            officeDescription : inputValues["COMMUNITY_LEGAL_F_OFFICE_DESCRIPTION"]
        };

        // инициализация компонентов универсальных списков (страна)
        initCountryField(
                "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS", "COMMUNITY_LEGAL_F_COUNTRY", "COMMUNITY_LEGAL_F_POST_CODE",
                null, null,
                RoomTypes.OFFICE, "COMMUNITY_LEGAL_F_OFFICE",
                "COMMUNITY_LEGAL_F_REGION_CODE", "COMMUNITY_LEGAL_F_STREET_DESCRIPTION_SHORT",
                "COMMUNITY_LEGAL_F_DISTRICT_DESCRIPTION_SHORT", "COMMUNITY_LEGAL_F_CITY_DESCRIPTION_SHORT", addressInputValues);

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

    function initEditFieldsModel(createKuchPageData) {
        var model = createKuchPageData;

        model.community.fieldGroups.sort(function(a, b){
            var result = -1;
            if (parseInt(a.position) > parseInt(b.position)) {
                result = 1;
            }
            return result;
        });

        var fieldGroups = model.community.fieldGroups;
        for (var i in fieldGroups) {
            var fieldGroup = fieldGroups[i];

            fieldGroup.isAddressBlock =
                    fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS';

            if (fieldGroup.fields != null) {
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

                    field.lowerType = field.type.toLowerCase();

                    // Адресное поле
                    field.addressField = field.type == "REGION" || field.type == "DISTRICT" || field.type == "CITY" || field.type == "STREET" || field.type == "BUILDING";
                    //
                    field.countryField = field.type == "COUNTRY";
                    //
                    field.readOnlyField = field.type == "GEO_POSITION" || field.type == "GEO_LOCATION";
                    //
                    field.universalListField = field.type == "UNIVERSAL_LIST";
                    //
                    field.otherField =
                            !field.addressField && !field.countryField && !field.sharerField && !field.participantsListField &&
                            !field.htmlTextField && !field.seoLinkField && !field.readOnlyField && !field.universalListField;


                }
                fieldGroup.showGroup = fieldGroup.internalName == 'COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS';
            }
        }
        return model;
    }

    function clickBlockAddressBlock(jqCheckBox, jqAddressOverlayDiv) {
        if (jqCheckBox.prop("checked")) {
            jqAddressOverlayDiv.remove();
            jqAddressOverlayDiv.css("opacity", "0.1");
            $("#addressBlock").append(jqAddressOverlayDiv);
            initDefaultAddressBlock();
        } else {
            jqAddressOverlayDiv.remove();
        }
    }

    function initDefaultAddressBlock() {
        // Инициализация адресных полей
        initAddressFields(defaultInputValues);
    }
</script>

<t:insertAttribute name="communityHeader" />
<div id="createKuchBlock"></div>

<script id="createKuchPageTemplate" type="x-tmpl-mustache">
    <h2>Создать кооперативный участок</h2>
    <h3>в Потребительском Обществе {{community.shortRuName}}</h3>
    <hr/>
    <div class="pageContainer">
        <div class="form-group">
            <label>Инициатор собрания</label>
            <input type="text" class="form-control" id="meetingCreator" name="meetingCreator" value="{{user.fullName}}" placeholder="" disabled="disabled">
        </div>
        <div class="form-group">
            <label>Тема собрания</label>
            <input type="text" class="form-control" id="meetingTheme" name="meetingTheme" value="Создание Кооперативного участка" placeholder="" disabled="disabled">
        </div>
        <div class="form-group">
            <label>Имя создаваемого Кооперативного Участка (КУч)</label>
            <input type="text" class="form-control" id="meetingName" name="meetingName" placeholder="">
        </div>
        <div class="form-group">
            <label>Цели и задачи создаваемого Кооперативного Участка (КУч)</label>
            <textarea id="meetingTargets" name="meetingTargets" ></textarea>
        </div>
        <hr/>
        <div class="form-group">
            <label>Участники создания Кооперативного Участка (минимум {{minCountParticipants}} чел){{#votersNeedBeVerified}} (доступны только идентифицированные участники){{/votersNeedBeVerified}}</label>
            <div class="participantContainer">
                <div class="participantsSelectContainer">
                    <select id="participants" name="participants" data-live-search="true" data-hide-disabled="true">
                        {{#communityMembers}}
                            {{^isCurrentUser}}
                                <option value="{{user.id}}" post="{{postNames}}" {{#votersNeedBeVerified}}{{^user.verified}}disabled="disabled"{{/user.verified}}{{/votersNeedBeVerified}}>{{user.fullName}}</option>
                            {{/isCurrentUser}}
                        {{/communityMembers}}
                    </select>
                </div>
                <div class="participantsButtonsContainer">
                    <a href="javascript:void(0)" class="btn btn-primary addParticipant">Добавить</a>
                </div>
            </div>
        </div>
        <div class="form-group" id="tableContainer">
            <table>
                <thead>
                    <th>#</th>
                    <th>ФИО</th>
                    <th>Должность</th>
                    <th style="width: 91px;"></th>
                </thead>
                <tbody id="participantList">
                    <tr id='{{user.id}}'>
                        <td class='memberIndex'>1</td>
                        <td>{{user.fullName}}</td>
                        <td>{{userPostNames}}</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="form-group">
            <label>Период проведения собрания по теме: создание Кооперативного участка Потребительского Общества</label>
            <input type="text" class="dateInput form-control" id="dateRange" name="dateRange"/>
        </div>

        <div class="form-group" style="position: relative;">
            <label>Дата окончания регистрации участников собрания КУч</label>
            <input type='text' class="form-control" id='votersRegistrationEndDate' />
        </div>

        <div class="form-group">
            <label id="datePeriodText"></label>
        </div>
        <hr/>
        <div class="form-group">
            <label>Вид голосования</label>
            <div>
                {{#votingTypesArr}}
                    <label>
                        <input type="radio" name="votingType" class="votingType" id="votingType_{{name}}" voting_type="{{name}}" />
                        {{value}}
                    </label><br/>
                {{/votingTypesArr}}
            </div>
        </div>
        <hr/>
        <div class="form-group">

            {{#addressKuch.community.fieldGroups}}
                {{#showGroup}}
                    <div
                        class="panel panel-default"
                        id="fields-group-panel-{{id}}"
                        data-group-name="{{internalName}}"
                        >

                        <div class="panel-heading">
                            <div style="display: inline-block"><h4 class="panel-title">{{name}}</h4></div>
                            <div style="display: inline-block"><label>(<input type="checkbox" id="blockAddress" checked="checked" /> совпадает с {{community.shortRuName}}) </label></div>
                        </div>

                        <div id="collapse-profile-{{id}}" class="panel-collapse in">
                            <div class="panel-body" id="addressBlock" style="position: relative;">
                                {{#fields}}
                                    {{#showField}}
                                        <div class="row">
                                            <div class="col-xs-12">
                                                <div class="form-group{{#hideable}} hideable{{/hideable}}" data-required="{{required}}" data-has-points="{{hasPoints}}">
                                                <label>{{name}}</label>
                                                <div class="fieldContainer" id="fieldContainer_{{internalName}}">
                                                    {{#addressField}}
                                                        <input type="text" class="form-control" data-kladr-object-type='{{lowerType}}'
                                                        value='{{value}}'
                                                        name='f:{{id}}' data-field-name='{{internalName}}'
                                                        data-field-type='{{type}}' data-field-id='{{id}}'
                                                        data-field-value='{{value}}'
                                                        placeholder='{{example}}' />
                                                    {{/addressField}}

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
                                                        placeholder='{{example}}' />
                                                    {{/otherField}}

                                                    {{#attachedFile}}
                                                        <div class="fieldFileContainer" id="fieldFileContainer_{{internalName}}">
                                                            <a class="browseFieldFile"
                                                               has_rights_to_edit="true"
                                                               file_limit="-1"
                                                               title="Просмотреть прикреплённые файлы"
                                                               field_id="{{id}}"
                                                               field_files_url=""
                                                               field_files_save_url=""
                                                            ></a>
                                                        </div>
                                                    {{/attachedFile}}
                                                </div>
                                                <span class="help-block">{{comment}}</span>
                                                <span style="display : none;" class="help-block help-block-error"></span>
                                                </div>
                                            </div>
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
            {{/addressKuch.community.fieldGroups}}

            <%--<t:insertTemplate template="createKuchMeetingFieldsGroups.jsp" />--%>
        </div>

        <div class="form-group">
            <a href="javascript:void(0)" style="float: right;" class="btn btn-primary" id="saveMeeting">Сохранить</a>
            <div style="clear: both;"></div>
        </div>
    </div>
    <hr/>
</script>

<script id="requirementsTemplate" type="x-tmpl-mustache">
    <h2>Создание Кооперативного участка невозможно!</h2>
    <h3>Необходимо выполнить следующие требования:</h3>
    <ol>
        {{#requirementsForCreateMeeting}}
            <li>{{.}}</li>
        {{/requirementsForCreateMeeting}}
    </ol>
</script>