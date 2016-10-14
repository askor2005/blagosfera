<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">

    // Описание полей.
    var userFields = [];

    // Инициализация контролов датапикеров
    function initDateInputs(jqDateControls) {
        $.each(jqDateControls, function(index, input){
            var $input = $(input);
            var dateFieldStartStr = $input.attr("dateFieldStart");
            var dateFieldEndStr = $input.attr("dateFieldEnd");

            if (dateFieldStartStr != "" && dateFieldEndStr != "") {
                $input.radomDateInput({
                    startView : 2,
                    startDate: dateFieldStartStr,
                    endDate: dateFieldEndStr
                });
            } else if (dateFieldStartStr != "") {
                $input.radomDateInput({
                    startView : 2,
                    startDate: dateFieldStartStr
                });
            } else if (dateFieldEndStr != "") {
                $input.radomDateInput({
                    startView : 2,
                    endDate: dateFieldEndStr
                });
            } else {
                $input.radomDateInput({
                    startView : 2
                });
            }
        });
    };

    // Загрузка списка участников по типу
    var participantsCache = {};
    function loadParticipants(participantType, callBack) {
        if (participantsCache[participantType] != null) {
            callBack(participantsCache[participantType]);
        } else {
            $.ajax({
                async: true,
                type: "POST",
                url: "/document/service/getParticipants.json",
                datatype: "json",
                data: {participant_type : participantType},
                success: function (param) {
                    if (!param.operationResult) {
                        bootbox.alert(param.operationMessage);
                    } else {
                        participantsCache[participantType] = param.data;
                        callBack(param.data);
                    }
                },
                error: function (param) {
                }
            });
        }
    };

    // Загрузка полей участника
    /*var participantsFieldsCache = {};
    function loadParticipantFields(participantType, participantId, callBack) {
        if (participantsFieldsCache[participantType + "_" + participantId] != null) {
            callBack(participantsFieldsCache[participantType + "_" + participantId]);
        } else {
            $.ajax({
                async: true,
                type: "POST",
                url: "/document/service/getParticipantFields.json",
                datatype: "json",
                data: {participant_type : participantType, participant_id : participantId},
                success: function (param) {
                    if (!param.operationResult) {
                        bootbox.alert(param.operationMessage);
                    } else {
                        participantsFieldsCache[participantType + "_" + participantId] = param.data;
                        callBack(param.data);
                    }
                },
                error: function (param) {
                }
            });
        }
    };*/

    function fillParticipantsInControl(participants, jqControl) {
        var html = "";
        for(var i=0; i<participants.length; i++) {
            var participant = participants[i];
            html += "<option value='" + participant.sourceParticipantId + "'>" + participant.name + "</option>";
        }
        jqControl.append(html);
    };

    // Загрузка данных участников
    function initParticipantsCombo(jqSelect) {
        jqSelect.each(function(){
            var jqParticipantControl = $(this);
            if (jqParticipantControl.next().hasClass("bootstrap-select")) {
                jqParticipantControl.next().remove();
            }
            var participantType = jqParticipantControl.attr("participantType");
            jqParticipantControl.prop("disabled", true);
            jqParticipantControl.attr("title", "Загрузка данных...");
            jqParticipantControl.selectpicker("refresh");
            jqParticipantControl.selectpicker("val", null);
            loadParticipants(participantType, function(data){
                fillParticipantsInControl(data, jqParticipantControl);
                jqParticipantControl.prop("disabled", false);
                jqParticipantControl.attr("title", "Укажите участника");
                jqParticipantControl.selectpicker("refresh");
                jqParticipantControl.selectpicker("val", null);
            });
        });
    }

    // Обработка выбора участника
    function initParticipantChangeEvent(jqSelect) {
        jqSelect.change(function(){
            var jqParticipantControl = $(this);
            var jqDivWithValue = $(".form-control-value", jqParticipantControl.parent());
            //var fieldIndex = jqParticipantControl.attr("field-index");
            var participantId = $("option:selected", jqParticipantControl).val();
            var participantType = jqParticipantControl.attr("participantType");
            var participantFieldTemplate = jqParticipantControl.attr("participantFieldTemplate");
            jqDivWithValue.text(participantId);
        });
    }

    // инициализация строковых контролов
    function initStringControls(jqStringControl) {
        jqStringControl.each(function(){
            var jqStringControl = $(this);
            var stringMask = jqStringControl.attr("stringMask");
            if (stringMask != "") {
                stringMask = stringMask.replace(new RegExp("\\\\d", 'g'), "\d");
                stringMask = stringMask.replace(new RegExp("\\\\s", 'g'), "\s");
                stringMask = stringMask.replace(new RegExp("\\\\r", 'g'), "\r");
                stringMask = stringMask.replace(new RegExp("\\\\e", 'g'), "\e");

                var placeholder = "";
                for (var i = 0; i < stringMask.length; i++) {
                    placeholder += "_";
                }

                jqStringControl.mask(stringMask, {placeholder: "_"}).attr("placeholder", placeholder);
            }
        });
    }

    // Инициализация числовых контролов
    function initNumberControls(jqNumberControls) {
        jqNumberControls.on("input", function(e) {
            var jqNumberControl = $(this);
            var currentValue = parseFloat(jqNumberControl.val());
            if (isNaN(currentValue)) {
                jqNumberControl.val("");
                return;
            } else {
                var strValue = jqNumberControl.val();
                if (strValue[strValue.length - 1] == ".") {
                    jqNumberControl.val(currentValue + ".");
                } else {
                    jqNumberControl.val(currentValue);
                }
            }

            var numberFieldStart = -1;
            var numberFieldEnd = -1;

            if (jqNumberControl.attr("numberFieldStart") != "") {
                numberFieldStart = parseInt(jqNumberControl.attr("numberFieldStart"));
            }
            if (jqNumberControl.attr("numberFieldEnd") != "") {
                numberFieldEnd = parseInt(jqNumberControl.attr("numberFieldEnd"));
            }
            var numberIsPrecision = jqNumberControl.attr("numberIsPrecision") == "true" ? true : false;
            var numberCountDigitals =  parseInt(jqNumberControl.attr("numberCountDigitals"));

            if (numberFieldStart != -1 && numberFieldStart > currentValue) {
                jqNumberControl.val(numberFieldStart);
            } else if (numberFieldEnd != -1 && currentValue > numberFieldEnd) {
                jqNumberControl.val(numberFieldEnd);
            } else if (numberIsPrecision) {
                var strValue = new String(currentValue);
                var digitsArr = strValue.split(".");
                if (digitsArr.length > 1) {
                    var precisionDigitsStr = digitsArr[1];
                    if (precisionDigitsStr.length > numberCountDigitals) {
                        jqNumberControl.val(digitsArr[0] + "." + precisionDigitsStr.substr(0, numberCountDigitals));
                    }
                }
            }
        });
    }

    // Инициализация денежных контролов
    function initCurrencyControls(jqCurrencyControls){
        jqCurrencyControls.each(function(){
            $(this).attr("numberIsPrecision", "true");
            $(this).attr("numberCountDigitals", "2");

            var jqListEditorNode = $(".currencyTypeControl", $(this).parent().parent());
            jqListEditorNode.html("");

            if (jqListEditorNode.length > 0) {
                // Инициализация валюты
                RameraListEditorModule.init(jqListEditorNode,
                        {
                            labelClasses: ["checkbox-inline"],
                            labelStyle: "margin-left: 10px;",
                            selectClasses: ["form-control"]
                        },
                        function (event, data) {
                            if (event == RameraListEditorEvents.VALUE_CHANGED) {
                                jqListEditorNode.attr("currency_type", data.code);
                            }
                        }
                );
            }
        });
        initNumberControls(jqCurrencyControls);
    }

    // Инициализация документов - контролов
    function initDocumentControls(jqDocumentControls){
        jqDocumentControls.each(function(){

        });
    }

    function addUserFieldValue(name, value) {
        var foundField = null;
        for (var index in userFields) {
            var field = userFields[index];
            if (field.name == name) {
                foundField = field;
            }
        }
        foundField.documentFieldValues.push(value);
    }

    function addUserFieldParameter(name, parameterName, parameterValue) {
        var foundField = null;
        for (var index in userFields) {
            var field = userFields[index];
            if (field.name == name) {
                foundField = field;
            }
        }
        foundField.parameters[parameterName] = parameterValue;
    }

    // Шаблон контейнера поля участника
    function getParticipantFieldDivTemplate(newListIndex, participantFieldTemplate, participantType, fieldName) {
        return "<div class='div-control-participant' field-list-index='" + newListIndex + "' field-name='" + fieldName + "'>" +
                "<select class='form-control form-control-participant selectpicker' field-list-index='" + newListIndex + "' participantFieldTemplate='" + participantFieldTemplate + "' participantType='" + participantType + "' data-live-search='true' data-width='100%'></select>" +
                "<div class='form-control-value'></div>" +
                    //"<hr/>" +
                "</div>";
    }

    // Шаблон контейнера поля строки
    function getStringFieldDivTemplate(newListIndex, stringMask, fieldName) {
        return "<div class='div-control-string' field-list-index='" + newListIndex + "' field-name='" + fieldName + "'>" +
                "<input type='text' class='form-control form-control-string' stringMask='" + stringMask + "'/>" +
                    //"<hr/>" +
                "</div>";
    }

    // Шаблон контейнера поля числа
    function getNumberFieldDivTemplate(newListIndex, numberFieldStart, numberFieldEnd, numberIsPrecision, numberCountDigitals, fieldName) {
        return "<div class='div-control-number' field-list-index='" + newListIndex + "' field-name='" + fieldName + "'>" +
                "<input type='text' class='form-control form-control-number' numberFieldStart='" + numberFieldStart + "' numberFieldEnd='" + numberFieldEnd + "' numberIsPrecision='" + numberIsPrecision + "' numberCountDigitals='" + numberCountDigitals + "' />"+
                    //"<hr/>" +
                "</div>";
    }

    // Шаблон контейнера поля числа
    function getCurrencyFieldDivTemplate(newListIndex, fieldName) {
        return "<div class='div-control div-control-currency' field-list-index='" + newListIndex + "' field-name='" + fieldName + "'>" +
                "<div style='position: relative; height: 34px;'>" +
                "<div class='currencyControl' style='right: 0px;'>" +
                "<input type='text' class='form-control form-control-currency'  />"+
                "</div>" +
                    //"<div class='currencyTypeControl' rameraListEditorName='currency_types'></div>" +
                "</div>" +
                    //"<hr/>" +
                "</div>";
    }

    // Шаблон контейнера поля даты
    function getDateFieldDivTemplate(newListIndex, dateFieldStart, dateFieldEnd, fieldName) {
        return "<div class='div-control-date' field-list-index='" + newListIndex + "' field-name='" + fieldName + "'>" +
                "<input type='text' class='form-control form-control-date' dateFieldStart='" + dateFieldStart + "' dateFieldEnd='" + dateFieldEnd + "'/>" +
                    //"<hr/>" +
                "</div>";
    }

    function getDocumentFieldDivTemplate(newListIndex, fieldName) {
        return "<div class='div-control-document' field-name='" + fieldName + "'>" +
                "<input type='text' class='form-control form-control-document'/>" +
                "</div>";
    }

    function prepareUserFields(userFields) {
        for (var index in userFields) {
            var userField = userFields[index];
            userField['type' + userField.type] = true;
            if (userField.parameters != null) {
                switch (userField.type) {
                    case 'date':
                        userField.parameters.existsDateFieldStart = userField.parameters.dateFieldStart != null && userField.parameters.dateFieldStart != '';
                        userField.parameters.existsDateFieldEnd = userField.parameters.dateFieldEnd != null && userField.parameters.dateFieldEnd != '';
                        userField.parameters.existsDateFieldEndOrFieldStart = userField.parameters.existsDateFieldStart || userField.parameters.existsDateFieldEnd;
                        break;
                    case 'number':
                        userField.parameters.existsNumberFieldStart = userField.parameters.numberFieldStart != null && userField.parameters.numberFieldStart != '';
                        userField.parameters.existsNumberFieldEnd = userField.parameters.numberFieldEnd != null && userField.parameters.numberFieldEnd != '';
                        userField.parameters.existsNumberFieldEndOrFieldStart = userField.parameters.existsNumberFieldStart || userField.parameters.existsNumberFieldEnd || userField.parameters.numberIsPrecision;
                        break;
                    case 'fieldsGroups':
                        if (userField.parameters.fieldsGroupsList != null) {
                            for (var index in userField.parameters.fieldsGroupsList) {
                                var fieldsGroup = userField.parameters.fieldsGroupsList[index];
                                if (fieldsGroup.userFields != null && fieldsGroup.userFields.length > 0) {
                                    prepareUserFields(fieldsGroup.userFields);
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    // Инициализация пользовательских полей
    function initUserFields(uFields, jqParentNode){
        userFields = uFields;
        // Инициализация шаблона
        //var userFields = [];

        prepareUserFields(userFields);

        var userFieldsTemplate = $("#userFieldsTemplate").html();
        jqParentNode.append($(Mustache.render(userFieldsTemplate, {userFields : userFields})));

        // Анализ группы полей
        for (var index in userFields) {
            var userField = userFields[index];
            if (userField.type == 'fieldsGroups') {
                if (userField.parameters.fieldsGroupsList != null) {
                    for (var i in userField.parameters.fieldsGroupsList) {
                        var fieldsGroup = userField.parameters.fieldsGroupsList[i];
                        var fieldsGroupNode = $("[fields-group-name=" + fieldsGroup.name + "]");
                        if (fieldsGroupNode.length > 0) {
                            fieldsGroupNode.append($(Mustache.render(userFieldsTemplate, {userFields : fieldsGroup.userFields})));
                        }
                    }
                }
            }
        }

        // Инициализация датапикеров
        initDateInputs($(".form-control-date"));

        // Создание шаблонов маски ввода строки
        $.mask.definitions['~']='[+-]';
        $.mask.definitions['*']='[a-zA-Z0-9а-яА-Я]';
        $.mask.definitions['\d']='[0-9]';
        $.mask.definitions['\r']='[а-яА-Я]';
        $.mask.definitions['\e']='[a-zA-Z]';
        $.mask.definitions['\s']='[a-zA-Zа-яА-Я]';

        // Инициализация маски ввода строки
        initStringControls($(".form-control-string"));

        // Загрузка участников
        initParticipantsCombo($(".form-control-participant"));
        // Обработка выбора участника
        initParticipantChangeEvent($(".form-control-participant"));

        // Обработка ввода числа
        initNumberControls($(".form-control-number"));

        // Обработка ввода денежного типа
        initCurrencyControls($(".form-control-currency"));

        // Обработка документов
        initDocumentControls($(".form-control-document"));

        // Обработка клика на кнопку добавления значения поля в список.
        $(".addFieldValueInList").click(function(){
            var jqButton = $(this);
            var jqFieldDiv = jqButton.prev();

            var fieldName = jqFieldDiv.attr("field-name");

            var newListIndex = parseInt(jqFieldDiv.attr("field-list-index")) + 1;

            // Если это контейнер с участником
            if (jqFieldDiv.hasClass("div-control-participant")) {
                var participantFieldTemplate = $("select", jqFieldDiv).attr("participantFieldTemplate");
                var participantType = $("select", jqFieldDiv).attr("participantType");

                var newJqFieldDiv = $(getParticipantFieldDivTemplate(newListIndex, participantFieldTemplate, participantType, fieldName));

                jqFieldDiv.after(newJqFieldDiv);

                var jqSelect = $("select", newJqFieldDiv);

                // Загрузка участников
                initParticipantsCombo(jqSelect);
                // Обработка выбора участника
                initParticipantChangeEvent(jqSelect);
            } else if (jqFieldDiv.hasClass("div-control-string")) {
                var stringMask = $("input", jqFieldDiv).attr("stringMask");
                var newJqFieldDiv = $(getStringFieldDivTemplate(newListIndex, stringMask, fieldName));
                jqFieldDiv.after(newJqFieldDiv);

                // Инициализация маски ввода строки
                initStringControls($("input", newJqFieldDiv));
            } else if (jqFieldDiv.hasClass("div-control-number")) {

                var numberFieldStart = $("input", jqFieldDiv).attr("numberFieldStart");
                var numberFieldEnd = $("input", jqFieldDiv).attr("numberFieldEnd");
                var numberIsPrecision = $("input", jqFieldDiv).attr("numberIsPrecision");
                var numberCountDigitals = $("input", jqFieldDiv).attr("numberCountDigitals");

                var newJqFieldDiv = $(getNumberFieldDivTemplate(newListIndex, numberFieldStart, numberFieldEnd, numberIsPrecision, numberCountDigitals, fieldName));
                jqFieldDiv.after(newJqFieldDiv);

                // Инициализация обработки числа
                initNumberControls($("input", newJqFieldDiv));
            } else if (jqFieldDiv.hasClass("div-control-currency")) {
                var newJqFieldDiv = $(getCurrencyFieldDivTemplate(newListIndex, fieldName));
                jqFieldDiv.after(newJqFieldDiv);

                // Инициализация обработки денежного типа
                initCurrencyControls($("input", newJqFieldDiv));
            } else if (jqFieldDiv.hasClass("div-control-date")) {
                var dateFieldStart = $("input", jqFieldDiv).attr("dateFieldStart");
                var dateFieldEnd = $("input", jqFieldDiv).attr("dateFieldEnd");

                var newJqFieldDiv = $(getDateFieldDivTemplate(newListIndex, dateFieldStart, dateFieldEnd, fieldName));
                jqFieldDiv.after(newJqFieldDiv);

                // Инициализация обработки даты
                initDateInputs($("input", newJqFieldDiv));
            } else if (jqFieldDiv.hasClass("div-control-fieldsGroups")) {
                newListIndex = parseInt($(".fieldsGroups", jqFieldDiv).length);
                var newJqNode = $($($(".fieldsGroups", jqFieldDiv).get(0)).get(0).outerHTML);
                $($(".fieldsGroups", jqFieldDiv).get(newListIndex - 1)).after(newJqNode);

                // Инициализация маски ввода строки
                initStringControls($(".form-control-string", newJqNode));
                // Загрузка участников
                initParticipantsCombo($("select.form-control-participant", newJqNode));
                // Обработка выбора участника
                initParticipantChangeEvent($(".form-control-participant", newJqNode));
                // Обработка ввода числа
                initNumberControls($(".form-control-number", newJqNode));
                // Обработка ввода денежного типа
                initCurrencyControls($(".form-control-currency", newJqNode));
                // Документы
                initDocumentControls($(".form-control-document", newJqNode));
            } else if (jqFieldDiv.hasClass("div-control-document")) {
                var newJqFieldDiv = $(getDocumentFieldDivTemplate(newListIndex, fieldName));
                jqFieldDiv.after(newJqFieldDiv);

                // Инициализация обработки документов
                initDocumentControls($("input", newJqFieldDiv));
            }

            // Див со значениями списка
            var jqSizeDiv = $(".list-size", jqButton.parent());
            if (jqSizeDiv.length > 0) {
                var maxSize = parseInt(jqSizeDiv.attr("max-size"));
                if (maxSize > newListIndex) {
                    jqSizeDiv.text("(" + (newListIndex + 1) + "/" + maxSize + ")")
                }
                if (maxSize == newListIndex + 1) {
                    jqButton.hide();
                }
            }
        });

        // Сохранения значений полей в документ
        $("#saveUserFields").click(function(){
            var userFields = getFieldsFromForm();
            if (userFields != false) {
                saveContent(userFields, function(){
                    bootbox.alert("Поля документа успешно сохранены!", function(){
                        document.location.reload();
                    });
                });
            }
            return false;
        });
    }

    function getFieldsFromForm() {
        var result = [];
        for (var index in userFields) {
            var field = userFields[index];
            field.documentFieldValues = [];
        }

        $(".div-control").removeAttr("readed");
        var fieldsValues = getFieldsValues();
        if (fieldsValues != false) {
            for (var fieldName in fieldsValues) {
                var fieldForm = fieldsValues[fieldName];

                var foundField = null;
                for (var index in userFields) {
                    var field = userFields[index];
                    if (field.name == fieldName) {
                        foundField = field;
                    }
                }
                if (foundField != null) {
                    foundField.documentFieldValues = fieldForm.documentFieldValues;
                    foundField.parameters = fieldForm.parameters;
                }
            }
            result = userFields;
        } else {
            result = false;
        }
        return result;
    }

    function getFieldsValues(jqParentNode) {
        var result = {};
        // Перебираем поля и смотрим значения
        var alertString = "";
        $(".div-control-fieldsGroups", jqParentNode).each(function(){
            var fieldsGroupsResult = [];

            $(".fieldsGroups", $(this)).each(function(){
                var fieldsGroupResult = [];
                $(".fieldsGroup", $(this)).each(function(){
                    var fieldsGroupName = $(this).attr("fields-group-name");
                    var fieldsGroupValues = getFieldsValues($(this));

                    // Делаем из мапы массив, чтобы на сервере резолвился в объект
                    var userFields = [];
                    for (var fieldName in fieldsGroupValues) {
                        var field = fieldsGroupValues[fieldName];
                        userFields.push(field);
                    }

                    fieldsGroupResult.push({
                        name : fieldsGroupName,
                        userFields : userFields
                    })
                });
                fieldsGroupsResult.push(fieldsGroupResult);
            });

            var jqFieldDiv = $(this);
            var fieldName = jqFieldDiv.attr("field-name");
            if (result[fieldName] == null){
                result[fieldName] = {name : fieldName, documentFieldValues : [], parameters : {}, type : "fieldsGroups"};
            }
            result[fieldName].parameters["fieldsGroups"] = fieldsGroupsResult;
        });
        $(".div-control-participant", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueDiv = $(".form-control-value", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueDiv.text() == null || jqValueDiv.text() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null) {
                    result[fieldName] = {name : fieldName, documentFieldValues: [], parameters: {}, type : "participant"};
                }
                result[fieldName].documentFieldValues.push(jqValueDiv.text());
            }
        });
        $(".div-control-string", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueInput = $("input", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueInput.val() == null || jqValueInput.val() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null) {
                    result[fieldName] = {name : fieldName, documentFieldValues: [], parameters: {}, type : "string"};
                }
                result[fieldName].documentFieldValues.push(jqValueInput.val());
            }
        });
        $(".div-control-number", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueInput = $("input", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueInput.val() == null || jqValueInput.val() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null) {
                    result[fieldName] = {name : fieldName, documentFieldValues: [], parameters: {}, type : "number"};
                }
                result[fieldName].documentFieldValues.push(jqValueInput.val());
            }
        });
        $(".div-control-currency", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueInput = $("input", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueInput.val() == null || jqValueInput.val() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null) {
                    result[fieldName] = {name : fieldName, documentFieldValues: [], parameters: {}, type : "currency"};
                }
                result[fieldName].documentFieldValues.push(jqValueInput.val());
                // Добавить параметр - наименование типа валюты
                var currencyType = $(".currencyTypeControl", jqValueInput.parent().parent()).attr("currency_type");
                if (currencyType != null) {
                    //addUserFieldParameter(fieldName, "currency_type", currencyType);
                    result[fieldName].parameters["currency_type"] = currencyType;
                }
            }
        });
        $(".div-control-date", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueInput = $("input", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueInput.val() == null || jqValueInput.val() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null){
                    result[fieldName] = {name : fieldName, documentFieldValues : [], parameters : {}, type : "date"};
                }
                result[fieldName].documentFieldValues.push(jqValueInput.val());
            }
        });
        $(".div-control-document", jqParentNode).each(function(){
            var jqFieldDiv = $(this);
            if (jqFieldDiv.attr("readed") != "true") {
                jqFieldDiv.attr("readed", "true");
                var jqValueInput = $("input", jqFieldDiv);
                var fieldName = jqFieldDiv.attr("field-name");
                if (jqValueInput.val() == null || jqValueInput.val() == '') {
                    alertString = "Необходимо заполнить поле '" + fieldName + "'";
                }
                if (result[fieldName] == null){
                    result[fieldName] = {name : fieldName, documentFieldValues : [], parameters : {}, type : "document"};
                }
                result[fieldName].documentFieldValues.push(jqValueInput.val());
            }
        });

        if (alertString != "") {
            bootbox.alert(alertString);
            return false;
        }

        return result;
    }
</script>
<script id="userFieldsTemplate" type="x-tmpl-mustache">
    {{#userFields}}
        <div class="form-group" field-name="{{name}}" style="border: 1px solid #ccc; border-radius: 4px; padding: 5px;">
        <label>{{name}} : {{description}}
        {{#isList}} (поле со списком значений){{/isList}}
        {{#typedate}}
            {{#parameters.existsDateFieldEndOrFieldStart}}
                (ограничения:
                {{#parameters.existsDateFieldStart}}
                    Начальное значение от {{parameters.dateFieldStart}}.
                {{/parameters.existsDateFieldStart}}
                {{#parameters.existsDateFieldEnd}}
                    Конечное значение до {{parameters.dateFieldEnd}}.
                {{/parameters.existsDateFieldEnd}}
                )
            {{/parameters.existsDateFieldEndOrFieldStart}}
        {{/typedate}}
        {{#typenumber}}
            {{#parameters.existsNumberFieldEndOrFieldStart}}
                (ограничения:
                {{#parameters.existsNumberFieldStart}}
                    Начальное значение от {{parameters.numberFieldStart}}.
                {{/parameters.existsNumberFieldStart}}
                {{#parameters.existsNumberFieldEnd}}
                    Конечное значение до {{parameters.numberFieldEnd}}.
                {{/parameters.existsNumberFieldEnd}}
                {{#parameters.numberIsPrecision}}
                    Дробное, знаков после запятой: {{parameters.numberCountDigitals}}
                {{/parameters.numberIsPrecision}}
                )
            {{/parameters.existsNumberFieldEndOrFieldStart}}
        {{/typenumber}}
        </label>
        {{#isList}}
            <div class="list-size" max-size="{{listSize}}" style="float: right">(1/{{listSize}})</div>
        {{/isList}}
        <div class="div-control div-control-{{type}}" field-list-index="0" field-name="{{name}}">
            {{#typeparticipant}}
                <select class="form-control form-control-participant selectpicker" field-list-index="0" participantFieldTemplate="{{parameters.participantFieldTemplate}}" participantType="{{parameters.participantType}}" data-live-search="true" data-width="100%"></select>
                <div class="form-control-value" ></div>
            {{/typeparticipant}}
            {{#typestring}}
                <input type="text" class="form-control form-control-string" stringMask="{{parameters.stringMask}}" />
            {{/typestring}}
            {{#typenumber}}
                <input type="text" class="form-control form-control-number" numberFieldStart="{{parameters.numberFieldStart}}" numberFieldEnd="{{parameters.numberFieldEnd}}" numberIsPrecision="{{parameters.numberIsPrecision}}" numberCountDigitals="{{parameters.numberCountDigitals}}" />
            {{/typenumber}}
            {{#typecurrency}}
                <div style="position: relative; height: 34px;">
                    <div class="currencyControl">
                        <input type="text" class="form-control form-control-currency" />
                    </div>
                    <div class="currencyTypeControl" rameraListEditorName="currency_types"></div>
                </div>
            {{/typecurrency}}
            {{#typedate}}
                <input type="text" class="form-control form-control-date" dateFieldStart="{{parameters.dateFieldStart}}" dateFieldEnd="{{parameters.dateFieldEnd}}"/>
            {{/typedate}}
            {{#typefieldsGroups}}
                <div class="fieldsGroups">
                    {{#parameters.fieldsGroupsList}}
                        <div style="border: 1px solid #ccc; border-radius: 4px; margin: 3px; padding: 3px;">
                            <div>Группа полей {{name}}</div>
                            <div fields-group-name="{{name}}" class="fieldsGroup"></div>
                        </div>
                    {{/parameters.fieldsGroupsList}}
                    <hr/>
                </div>
            {{/typefieldsGroups}}
            {{#typedocument}}
                <input type="text" class="form-control form-control-document" />
            {{/typedocument}}
        </div>
        {{#isList}}
            <button type="button" class="btn btn-primary addFieldValueInList" style="margin-top: 5px;" control_id="{{name}}_{{type}}">Добавить значение в список</button>
        {{/isList}}
        </div>
    {{/userFields}}
</script>