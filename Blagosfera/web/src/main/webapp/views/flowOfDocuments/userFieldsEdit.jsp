<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    #fieldsGroupsList, #userFieldsList { list-style-type: none; margin: 0; padding: 0; }
    #fieldsGroupsList li, #userFieldsList li { margin: 0 3px 3px 3px; padding: 0.4em; padding-left: 1.5em; background-color: #ccc; border: 1px solid #adadad;}
</style>
<script type="text/javascript">
    var selectedParticipantId = null;
    var selectedParticipantName = null;
    var fieldsGroupsTemplateEditor = null;
    var activeEditors = [];
    var activeEditorIndex = 0;

    // Получить пользовательские поля шаблона документа
    function getParticipantsWithFieldsFromTemplate() {
        // Получаем все пользовательские поля документа
        var jqUserFields = $(getCurrentEditor().dom.doc).find("span[data-span-type=radom-participant-custom-fields]");
        var participantsByName = {};
        var fieldsByName = {};
        var participants = [];
        jqUserFields.each(function(){
            var fieldNode = $(this);
            var participantName = fieldNode.attr("data-participant-name");
            var fieldName = fieldNode.attr("data-custom-field-name");
            var type = fieldNode.attr("data-custom-field-type-name");
            switch (fieldType) {
                case "string":
                    fieldType = "Строка";
                    break;
                case "date":
                    fieldType = "Дата";
                    break;
                case "fieldsGroups":
                    fieldType = "Группа полей";
                    break;
                case "number":
                    fieldType = "Число";
                    break;
                case "currency":
                    fieldType = "Деньги";
                    break;
                case "participant":
                    fieldType = "Источник данных";
                    break;
                case "document":
                    fieldType = "Документ";
                    break;
            }

            var fieldPosition = fieldNode.attr("data-position");
            var participant = null;
            if (participantsByName[participantName] == null) {
                participantsByName[participantName] = participants.length;
                participant = {participantName : participantName, fields : []};
                participants.push(participant);
            } else {
                participant = participants[participantsByName[participantName]];
            }
            if (fieldsByName[participantName + "_" + fieldName] == null) {
                fieldsByName[participantName + "_" + fieldName] = participant["fields"].length;
                var field = {fieldName : fieldName, fieldNodes : [fieldNode], fieldPosition : fieldPosition, fieldType : fieldType};
                participant["fields"].push(field);
            } else {
                participant["fields"][fieldsByName[participantName + "_" + fieldName]]["fieldNodes"].push(fieldNode);
            }

        });
        return participants;
    }

    $(document).ready(function () {

        // Дублируем модальное окно
        var cloneUserFieldsModal = $($("#radomParticipantCustomFieldsWindow").get(0).outerHTML);
        cloneUserFieldsModal.attr("id", "userFieldsGroupsModal");
        $("body").append(cloneUserFieldsModal);
        // Удаляем тип поля группы полей у дублированного модального окна
        $("select.customFieldsTypeCombobox option[value=fieldsGroups]", "#userFieldsGroupsModal").remove();
        $(".fieldsGroupsParameters", "#userFieldsGroupsModal").remove();
        // Поля в группе могут быть только того участника, который создал групповое поле
        $(".participantCustomFieldsComboboxContainer", "#userFieldsGroupsModal").append("<input type='text' class='participantCustomFieldsText form-control' disabled='disabled' />")
        $(".customFieldsParticipantFields", "#userFieldsGroupsModal").attr("id", "customFieldsGroupsParticipantFields");

        // Событие изменения значений типа пользовательского поля
        $(".customFieldsTypeCombobox").change(function(){
            var jqModal = $(this).closest(".modal");
            var customFieldsType = $(this).val();
            $(".customFieldParameters", jqModal).hide();
            $("." + customFieldsType + "Parameters", jqModal).show();
        });

        // Событие изменения типа пользовательского поля - список
        $(".customFieldIsList").click(function(){
            var jqModal = $(this).closest(".modal");
            var isChecked = $(this).is(":checked");
            if (isChecked) {
                $(".customFieldListParameters", jqModal).show();
            } else {
                $(".customFieldListParameters", jqModal).hide();
            }
        });

        // Событие отрытия модального окна со списком пользовательских полей
        $("#userFieldsListWindow").on("shown.bs.modal", function () {
            var jqModal = $(this);
            var participants = getParticipantsWithFieldsFromTemplate();

            var fieldsForDraw = {fields : []};
            for (var index in participants) {
                var participant = participants[index];
                for (var fieldIndex in participant.fields) {
                    var field = participant.fields[fieldIndex];
                    fieldsForDraw.fields.push({
                        fieldName : field.fieldName,
                        participantName : participant.participantName,
                        fieldPosition : field.fieldPosition,
                        fieldType : field.fieldType
                    });
                }
            }

            fieldsForDraw.fields.sort(function(a, b){
                return a.fieldPosition > b.fieldPosition ? 1 : -1;
            })

            var userFieldsListTemplate = $("#userFieldsListTemplate").html();
            Mustache.parse(userFieldsListTemplate);

            $(".modal-body", jqModal).empty();
            $(".modal-body", jqModal).append($(Mustache.render(userFieldsListTemplate, fieldsForDraw)));

            $("#userFieldsList", jqModal).sortable({
                update : function () {
                }
            });
        });

        // Сохранить порядок полей после сортировки
        $("#saveUserFieldsPositionsButton").click(function(){
            var participants = getParticipantsWithFieldsFromTemplate();
            var jqModal = $(this).closest(".modal");
            var positionIndex = 0;
            $("#userFieldsList li", jqModal).each(function(){
                var jqListItem = $(this);
                var participantName = jqListItem.attr("participant_name");
                var fieldName = jqListItem.attr("field_name");

                for (var index in participants) {
                    var participant = participants[index];
                    if (participant.participantName == participantName) {
                        for (var fieldIndex in participant.fields) {
                            var field = participant.fields[fieldIndex];
                            if (field.fieldName == fieldName) {
                                for (var nodeIndex in field.fieldNodes) {
                                    var jqNode = field.fieldNodes[nodeIndex];
                                    jqNode.attr("data-position", positionIndex);
                                }
                                positionIndex++;
                            }
                        }
                    }
                }
            });
            hideModal(jqModal);
        });

        // При открытии формы с пользовательскими полями подгружаем участников в комбобокс
        $(".radomParticipantCustomFieldsWindow").on("shown.bs.modal", function () {
            activeEditors.push(tinymce.activeEditor)
            activeEditorIndex = activeEditors.length - 1;
            var jqModal = $(this);

            var jqParticipantCombo = $("select.participantCustomFieldsCombobox", jqModal);
            fillParticipantsComboBox(jqParticipantCombo);
            // Если есть текстовое поле с наименованием участника
            if ($(".participantCustomFieldsText", jqModal).length > 0) {
                $(".participantCustomFieldsCombobox", jqModal).hide();
                $(".participantCustomFieldsText", jqModal).attr("data-object-id", selectedParticipantId);
                $(".participantCustomFieldsText", jqModal).val(selectedParticipantName);
            }

            jqParticipantCombo.attr("title", "Укажите источник данных");
            jqParticipantCombo.selectpicker("refresh");
            jqParticipantCombo.selectpicker("val", null);

            var jqCustomFieldsTypeCombo = $(".customFieldsTypeCombobox", jqModal);
            jqCustomFieldsTypeCombo.attr("title", "Укажите тип поля");
            jqCustomFieldsTypeCombo.selectpicker("refresh");
            jqCustomFieldsTypeCombo.selectpicker("val", null);

            // Очищаем поля ввода
            $(".customFieldParameters", jqModal).hide();
            $(".customFieldsParticipantTemplate", jqModal).val("");
            $(".nameCustomField", jqModal).val("");
            $(".descriptionCustomField", jqModal).val("");
            $(".stringMask", jqModal).val("");
            $(".stringCase", jqModal).val("CASE_I");
            $(".numberStartValue", jqModal).val("");
            $(".numberEndValue", jqModal).val("");
            $(".numberPrecisionForm", jqModal).hide();
            $(".numberIsDouble", jqModal).prop("checked", false);
            $(".numberPrecision", jqModal).val("2");
            $(".dateStartValue", jqModal).val("");
            $(".dateEndValue", jqModal).val("");

            $(".customFieldsParticipantType", jqModal).val(null);

            $(".customFieldIsList", jqModal).prop("checked", false);
            $(".customFieldListParameters", jqModal).hide();

            // Инициализация датапикеров
            initDateInputs($(".dateInput", jqModal));

            drawFieldsGroupsList(fieldsGroups);

            // Иниациализация клика по чекбоксу дробных значений
            $(".numberIsDouble", jqModal).unbind();
            $(".numberIsDouble", jqModal).click(function () {
                var numberIsDoubleCheckBox = $(this);
                if (numberIsDoubleCheckBox.is(":checked")) {
                    $(".numberPrecisionForm", jqModal).show();
                } else {
                    $(".numberPrecisionForm", jqModal).hide();
                }
            });

            if ($(".radom-participant-custom-fields-is-edit", jqModal).val() == 0) {
                $(".radomParticipantCustomFieldsButton", jqModal).text("Добавить");
                $(".radomParticipantCustomFieldsLabel", jqModal).text("Добавить пользовательское поле");
            } else {
                $(".radomParticipantCustomFieldsButton", jqModal).text("Сохранить");
                $(".radomParticipantCustomFieldsLabel", jqModal).text("Редактировать пользовательское поле");
            }
        });
        $(".radomParticipantCustomFieldsWindow").on("shown.bs.modal", function () {
            var jqModal = $(this);
            if ($(".radom-participant-custom-fields-is-edit", jqModal).val() > 0) {
                var dataListSize = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-list-size");
                var dataListViewType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-list-view-type");
                var dataParticipantId = parseInt($(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-id"));
                var dataParticipantType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-type");
                var dataCustomFieldTypeName = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-type-name");
                var dataCustomFieldName = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-name");
                var dataCustomFieldDescription = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-description");
                var dataParticipantFieldTemplate = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-field-template");

                var dataStringMask = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-string-mask");
                var dataStringCase = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-string-case");
                var dataNumberStart = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-start");
                var dataNumberEnd = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-end");
                var dataNumberIsPrecision = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-is-precision");
                var dataNumberCountDigitals = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-count-digitals");
                var dataDateStart = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-start");
                var dataDateEnd = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-end");

                var fieldsGroupsStr = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-fields-groups");

                var documentViewType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-document-view-type")

                var dateIsWordsType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-is-words-type") == "true" ? true : false;
                var numberIsWordsType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-is-words-type") == "true" ? true : false;
                var currencyIsWordsType = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-currency-is-words-type") == "true" ? true : false;

                var fieldPosition = $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-position");

                $(".participantCustomFieldsCombobox", jqModal).selectpicker("refresh");
                $(".participantCustomFieldsCombobox", jqModal).selectpicker("val", dataParticipantId);
                $(".participantCustomFieldsCombobox", jqModal).trigger("change");

                $(".nameCustomField", jqModal).val(dataCustomFieldName);
                $(".descriptionCustomField", jqModal).val(dataCustomFieldDescription);

                var jqCustomFieldsTypeCombo = $(".customFieldsTypeCombobox", jqModal);
                jqCustomFieldsTypeCombo.selectpicker("refresh");
                jqCustomFieldsTypeCombo.selectpicker("val", null);

                $(".customFieldsTypeCombobox option[value='" + dataCustomFieldTypeName + "']", jqModal).attr("selected", "selected");
                $(".customFieldsTypeCombobox", jqModal).selectpicker("refresh");
                $(".customFieldsTypeCombobox", jqModal).trigger("change");

                $(".customFieldsParticipantType", jqModal).val(dataParticipantType);
                $(".customFieldsParticipantType", jqModal).trigger("change");

                $(".customFieldsParticipantTemplate", jqModal).val(dataParticipantFieldTemplate);

                $(".stringMask", jqModal).val(dataStringMask);
                if (dataStringCase != null) {
                    $(".stringCase", jqModal).val(dataStringCase);
                } else {
                    $(".stringCase", jqModal).val("CASE_I");
                }
                $(".numberStartValue", jqModal).val(dataNumberStart);
                $(".numberEndValue", jqModal).val(dataNumberEnd);

                if (dataNumberIsPrecision == "true") {
                    $(".numberIsDouble", jqModal).trigger("click");
                    $(".numberPrecision", jqModal).val(dataNumberCountDigitals);
                }

                $(".dateStartValue", jqModal).val(dataDateStart);
                $(".dateEndValue", jqModal).val(dataDateEnd);

                if (fieldsGroupsStr != null && fieldsGroupsStr != "") {
                    fieldsGroups = JSON.parse(decodeSpecialCharacters(fieldsGroupsStr));
                    drawFieldsGroupsList(fieldsGroups);
                }

                if (documentViewType == null || documentViewType == "fullName") {
                    $("#documentFullName", jqModal).prop("checked", true);
                } else {
                    $("#documentShortName", jqModal).prop("checked", true);
                }

                $(".dateWordsValueType", jqModal).prop("checked", dateIsWordsType);
                $(".numberWordsValueType", jqModal).prop("checked", numberIsWordsType);
                $(".currencyWordsValueType", jqModal).prop("checked", currencyIsWordsType);

                if (dataListSize != undefined) {
                    $(".customFieldIsList", jqModal).trigger("click");
                    $(".customFieldListSize", jqModal).val(dataListSize);
                    $(".customFieldListViewType option[value='" + dataListViewType + "']", jqModal).attr("selected", "selected");
                }

                $(".fieldPosition", jqModal).val(fieldPosition);
            }
        });
        $("#radomParticipantCustomFieldsWindow").on("hide.bs.modal", function () {
            var jqModal = $(this);
            $(".radom-participant-custom-fields-is-edit", jqModal).val(0);
            fieldsGroups = [];
        });
        $(".radomParticipantCustomFieldsWindow").on("hide.bs.modal", function () {
            activeEditorIndex--;
        });

        // Изменение селекта с полями участника в пользовательском поле
        $(".customFieldsParticipantType").change(function(){
            var jqModal = $(this).closest(".modal");
            var participantType = $(this).val();
            var associationForm = -1;
            if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                associationForm = parseInt($("select.participantCustomFieldsCombobox", jqModal).find("option:selected").attr("data-association-form"));
            }
            fillComboBoxByParticipantType(participantType, associationForm, $("select.customFieldsParticipantFields", jqModal).attr("id"));
        });

        // Клик по кнопке добавления поля в шаблон участника в пользовательском поле
        $(".customFieldsParticipantButton").click(function(){
            var jqModal = $(this).closest(".modal");
            var selectedFieldInternalName = $(".customFieldsParticipantFields", jqModal).find("option:selected").attr("data-object-internal-name");
            if (selectedFieldInternalName == null || selectedFieldInternalName == "") {
                bootbox.alert("Необходимо выбрать поле перед его добавлением!");
                return false;
            }
            var templateValue = $(".customFieldsParticipantTemplate", jqModal).val();
            $(".customFieldsParticipantTemplate", jqModal).val(templateValue + "{" + selectedFieldInternalName + "}")
        });

        // Обработчик кнопки сохранения пользовательского поля
        $(".radomParticipantCustomFieldsButton").click(function () {
            var jqModal = $(this).closest(".modal");
            var jqParticipantSelect = $("select.participantCustomFieldsCombobox", jqModal);
            var jqParticipantText = $(".participantCustomFieldsText", jqModal);

            var participantId = null;
            var participantName = null;

            if (jqParticipantText.length == 0) {
                participantId = $("option:selected", jqParticipantSelect).attr("data-object-id");
                participantName = $("option:selected", jqParticipantSelect).text();
            } else {
                participantId = $(jqParticipantText).attr("data-object-id");
                participantName = $(jqParticipantText).val();
            }
            var fieldName = $(".nameCustomField", jqModal).val();
            var fieldDescription = $(".descriptionCustomField", jqModal).val();
            var fieldTypeName = $("option:selected", $(".customFieldsTypeCombobox", jqModal)).val();

            var fieldPosition = $(".fieldPosition", jqModal).val();
            if (fieldPosition == null || fieldPosition == "") {
                fieldPosition = -1;
            }

            if (participantId == null || participantId == "") {
                bootbox.alert("Укажите источник данных!");
                return false;
            }

            if (fieldName == null || fieldName == "") {
                bootbox.alert("Укажите наименование поля!");
                return false;
            }
            if (fieldDescription == null || fieldDescription == "") {
                bootbox.alert("Укажите описание поля!");
                return false;
            }
            if (fieldTypeName == null || fieldTypeName == "") {
                bootbox.alert("Укажите тип поля!");
                return false;
            }

            var additionalFieldParametersAttributesHtml = "";
            switch (fieldTypeName) {
                case "participant":
                    var customFieldsParticipantType = $(".customFieldsParticipantType", jqModal).val();
                    if (customFieldsParticipantType == null || customFieldsParticipantType == "") {
                        bootbox.alert("Укажите тип источника данных!");
                        return false;
                    }
                    var participantFieldTemplate = $(".customFieldsParticipantTemplate", jqModal).val();
                    if (participantFieldTemplate == null || participantFieldTemplate == "") {
                        bootbox.alert("Укажите шаблон поля источника данных!");
                        return false;
                    }
                    additionalFieldParametersAttributesHtml += " data-participant-type='" + customFieldsParticipantType + "'";
                    additionalFieldParametersAttributesHtml += " data-participant-field-template='" + participantFieldTemplate + "'";
                    break;
                case "string":
                    var stringMask = $(".stringMask", jqModal).val();
                    var stringCase = $(".stringCase", jqModal).val();
                    additionalFieldParametersAttributesHtml += " data-string-mask='" + stringMask + "'";
                    additionalFieldParametersAttributesHtml += " data-string-case='" + stringCase + "'";
                    break;
                case "currency":
                    var isWordsValueType = $(".currencyWordsValueType", jqModal).is(":checked");
                    additionalFieldParametersAttributesHtml += " data-currency-is-words-type='" + isWordsValueType + "'";
                    break;
                case "number":
                    var startValue = $(".numberStartValue", jqModal).val();
                    var endValue = $(".numberEndValue", jqModal).val();
                    var isWordsValueType = $(".numberWordsValueType", jqModal).is(":checked");
                    var isPrecision = $(".numberIsDouble", jqModal).is(":checked");
                    if (startValue != null && startValue != "" && !isNumber(startValue)) {
                        bootbox.alert("Поле 'начальное значение числа' не является числом!");
                        return false;
                    }
                    if (startValue != null && startValue != "" && startValue < 0) {
                        bootbox.alert("Поле 'начальное значение числа' не может быть отрицательным!");
                        return false;
                    }
                    if (endValue != null && endValue != "" && !isNumber(endValue)) {
                        bootbox.alert("Поле 'конечное значение числа' не является числом!");
                        return false;
                    }
                    if (endValue != null && endValue != "" && endValue < 0) {
                        bootbox.alert("Поле 'конечное значение числа' не может быть отрицательным!");
                        return false;
                    }
                    if (startValue != null && startValue != "" && endValue != null && endValue != "" && startValue > endValue) {
                        bootbox.alert("Конечное значение числа должно быть больше или равно начальному числу!");
                        return false;
                    }

                    additionalFieldParametersAttributesHtml +=
                            " data-number-start='" + startValue + "'" +
                            " data-number-end='" + endValue + "'" +
                            " data-number-is-precision='" + isPrecision + "'" +
                            " data-number-is-words-type='" + isWordsValueType + "'";
                    if (isPrecision) {
                        var countDigitals = $(".numberPrecision", jqModal).val();
                        if (countDigitals == null || countDigitals == "") {
                            bootbox.alert("Укажите количество знаков после запятой!");
                            return false;
                        }
                        if (!isInt(countDigitals)) {
                            bootbox.alert("Значение поля 'количество знаков после запятой' не является целым числом!");
                            return false;
                        }
                        additionalFieldParametersAttributesHtml += " data-number-count-digitals='" + countDigitals + "'";
                    }
                    break;
                case "date":
                    var dateStartValue = $(".dateStartValue", jqModal).val();
                    var dateEndValue = $(".dateEndValue", jqModal).val();
                    var dateStart = $(".dateStartValue", jqModal).datepicker('getDate');
                    var dateEnd = $(".dateEndValue", jqModal).datepicker('getDate');
                    var isWordsValueType = $(".dateWordsValueType", jqModal).is(":checked");
                    if (dateStartValue != null && dateStartValue != "" && dateEndValue != null && dateEndValue != "" && dateStart.getTime() > dateEnd.getTime()) {
                        bootbox.alert("Конечное значение даты должно быть больше начального!");
                        return false;
                    }
                    additionalFieldParametersAttributesHtml += " data-date-start='" + dateStartValue + "' data-date-end='" + dateEndValue + "' data-date-is-words-type='" + isWordsValueType + "'";
                    break;
                case "fieldsGroups":
                    var jsonStr = stripSpecialCharacters(JSON.stringify(fieldsGroups));
                    additionalFieldParametersAttributesHtml += " data-fields-groups='" + jsonStr + "'";
                    break;
                case "document":
                    var documentViewType = "";
                    //Полное наименование документа
                    documentViewType = $("#documentFullName").prop("checked") ? "fullName" : null;
                    //Сокращённое наименование документа
                    documentViewType = $("#documentShortName").prop("checked") ? "shortName" : null;
                    documentViewType = documentViewType == null ? "fullName" : documentViewType;
                    additionalFieldParametersAttributesHtml += " data-document-view-type='" + documentViewType + "'";
                    break;
            }

            // Если выбран тип поля - список
            if ($(".customFieldListParameters", jqModal).is(":visible")) {
                var customFieldListSize = $(".customFieldListSize", jqModal).val();
                var customFieldListViewType = $("option:selected", $(".customFieldListViewType", jqModal)).val();

                if (customFieldListSize == null || customFieldListSize == "") {
                    bootbox.alert("Укажите количество элементов списка!");
                    return false;
                }
                if (!isInt(customFieldListSize)) {
                    bootbox.alert("Количество элементов списка не является целым числом!");
                    return false;
                }
                if (customFieldListSize < 0) {
                    bootbox.alert("Количество элементов списка не может быть отрицательным числом!");
                    return false;
                }
                if (customFieldListViewType == null || customFieldListViewType == "") {
                    bootbox.alert("Необходимо выбрать вид отображения списка полей!");
                    return false;
                }
                additionalFieldParametersAttributesHtml += " data-list-size='" + customFieldListSize + "' data-list-view-type='" + customFieldListViewType + "'";
            }

            var signParticipantId = hashCode(participantName);
            $("#" + signParticipantId).prop("checked", true);
            
            var insertedCaption = "[" + participantName + ":" + fieldName + ":" + fieldDescription + "]";
            var insertedContent = "<span data-placeholder class='mceNonEditable' data-mce-contenteditable='false' data-span-type='radom-participant-custom-fields' " +
                    "data-span-id='" + Math.round(new Date().getTime() + (Math.random() * 100))  + "' " +
                    "data-participant-id='" + participantId  + "' " +
                    "data-participant-name='" + participantName + "' " +
                    "data-custom-field-type-name='" + fieldTypeName + "' " +
                    "data-custom-field-name='" + fieldName + "' " +
                    "data-custom-field-description='" + fieldDescription + "' " +
                    "data-position='" + fieldPosition + "' " +
                    additionalFieldParametersAttributesHtml +
                    " >" + insertedCaption + "</span>";

            if ($(".radom-participant-custom-fields-is-edit", jqModal).val() == 0) {
                insertedContent = insertedContent + "&nbsp;";
            }
            getCurrentEditor().selection.setContent(insertedContent);

            //jqModal.modal("hide");
            hideModal(jqModal);
            getCurrentEditor().focus();
            return false;
        });

        // Добавить группу полей в список групп полей
        $("#addFieldsGroup").click(function(){
            var jqModal = $(this).closest(".modal");

            var jqParticipantSelect = $("select.participantCustomFieldsCombobox", jqModal);

            selectedParticipantId = $("option:selected", jqParticipantSelect).attr("data-object-id");
            selectedParticipantName = $("option:selected", jqParticipantSelect).text();

            if (selectedParticipantId == null || selectedParticipantId == "") {
                bootbox.alert("Перед добавлением группы полей необходимо выбрать источник данных!");
                return false;
            }

            $("#fieldsGroupAddCustomFieldsWindow").removeAttr("index");
            //$("#fieldsGroupAddCustomFieldsWindow").modal("show");
            showModal($("#fieldsGroupAddCustomFieldsWindow"));
        });

        $("#fieldsGroupContent").radomTinyMCE({
            useRadomParticipantCustomFields : true,
            useRadomCopyPasteFields : true
        });

        // Событие показа модального окна редактирования группы полей
        $("#fieldsGroupAddCustomFieldsWindow").on("shown.bs.modal", function () {
            var fieldsGroup = null;
            if ($(this).attr("index") != null && $(this).attr("index") != '') {
                fieldsGroup = fieldsGroups[parseInt($(this).attr("index"))];
            }

            if (fieldsGroup != null) {
                $("#fieldsGroupAddCustomFieldsWindowLabel").text("Редактировать группу полей");
                $("#fieldsGroupAddButton").text("Сохранить");
                $("#fieldsGroupName").val(fieldsGroup.name);
                fieldsGroupsTemplateEditor.setContent(fieldsGroup.content);
            } else {
                $("#fieldsGroupAddCustomFieldsWindowLabel").text("Добавить группу полей");
                $("#fieldsGroupAddButton").text("Добавить");
                $("#fieldsGroupName").val("");
                fieldsGroupsTemplateEditor.setContent("");
            }
        });

        var fieldsGroups = [];
        // Добавление группы полей в поле
        $("#fieldsGroupAddButton").click(function(){
            var jqModal = $("#fieldsGroupAddCustomFieldsWindow");
            var name = $("#fieldsGroupName").val();
            var content = $("#fieldsGroupContent").val();
            var fieldGroupsIndex = -1;
            if (jqModal.attr("index") != null && jqModal.attr("index") != '') {
                fieldGroupsIndex = parseInt(jqModal.attr("index"));
            }

            if (name == null || name == "") {
                bootbox.alert("Необходимо указать имя группы полей");
                return false;
            }

            if (fieldGroupsIndex == -1) {
                for (var index in fieldsGroups) {
                    var fieldsGroup = fieldsGroups[index];
                    if (fieldsGroup.name == name) {
                        bootbox.alert("Группа с таким именем уже существует!");
                        return false;
                    }
                }
                fieldsGroups.push({
                    name : name,
                    content : content
                });
            } else {
                for (var index in fieldsGroups) {
                    var fieldsGroup = fieldsGroups[index];
                    if (fieldsGroup.name == name && fieldGroupsIndex != index) {
                        bootbox.alert("Группа с таким именем уже существует!");
                        return false;
                    }
                }
                var fieldsGroup = fieldsGroups[fieldGroupsIndex];
                fieldsGroup["name"] = name;
                fieldsGroup["content"] = content;
            }

            drawFieldsGroupsList(fieldsGroups);
            //$("#fieldsGroupAddCustomFieldsWindow").modal("hide");
            hideModal($("#fieldsGroupAddCustomFieldsWindow"));
        });

        function drawFieldsGroupsList(fieldsGroupArray) {

            $("#fieldsGroupsList").empty();
            for (var index in fieldsGroupArray) {
                var fieldsGroup = fieldsGroupArray[index];

                $("#fieldsGroupsList").append(
                        "<li index='" + index +"' fields_group_name='" + fieldsGroup.name + "'>" +
                            "<a class='fieldsGroupFormLink' href='javascript:void(0)'>" + fieldsGroup.name + "</a>" +
                            "<a class='fieldsGroupDelete glyphicon glyphicon-remove' href='javascript:void(0)'></a>" +
                        "</li>"
                );
            }
            $("#fieldsGroupsList").sortable({
                update : function () {
                    //console.log("asd");
                    var sortedFieldsGroups = [];
                    //fieldsGroups
                    $("#fieldsGroupsList li").each(function(){
                        var index = parseInt($(this).attr("index"));
                        sortedFieldsGroups.push(fieldsGroups[index]);
                    });
                    fieldsGroups = sortedFieldsGroups;
                    drawFieldsGroupsList(fieldsGroups);
                }
            });

            $(".fieldsGroupFormLink").click(function(){
                var jqModal = $(this).closest(".modal");
                var jqParticipantSelect = $("select.participantCustomFieldsCombobox", jqModal);

                selectedParticipantId = $("option:selected", jqParticipantSelect).attr("data-object-id");
                selectedParticipantName = $("option:selected", jqParticipantSelect).text();

                $("#fieldsGroupAddCustomFieldsWindow").attr("index", $(this).parent().attr("index"));
                //$("#fieldsGroupAddCustomFieldsWindow").modal("show");
                showModal($("#fieldsGroupAddCustomFieldsWindow"));
            });
            $(".fieldsGroupDelete").click(function(){
                $(this).parent().remove();
                fieldsGroupArray.splice(parseInt($(this).parent().attr("index")), 1);
                drawFieldsGroupsList(fieldsGroupArray);
            });
        }
    });

    // Открыть диалог для редактирования пользовательских полей
    function openDialogEditCustomField(dataSpanId, e) {
        //var jqModal = $("#radomParticipantCustomFieldsWindow");
        // Нужно определить модальное окно
        var jqModal = $("#radomParticipantCustomFieldsWindow");
        if ($("#radomParticipantCustomFieldsWindow").is(":visible")) {
            jqModal = $("#userFieldsGroupsModal");
        }

        $(".radom-participant-custom-fields-is-edit", jqModal).val(1);
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-span-id", dataSpanId);
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-list-size", e.target.getAttribute("data-list-size"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-list-view-type", e.target.getAttribute("data-list-view-type"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-id", e.target.getAttribute("data-participant-id"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-name", e.target.getAttribute("data-participant-name"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-type", e.target.getAttribute("data-participant-type"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-type-name", e.target.getAttribute("data-custom-field-type-name"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-name", e.target.getAttribute("data-custom-field-name"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-custom-field-description", e.target.getAttribute("data-custom-field-description"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-participant-field-template", e.target.getAttribute("data-participant-field-template"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-string-mask", e.target.getAttribute("data-string-mask"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-string-case", e.target.getAttribute("data-string-case"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-start", e.target.getAttribute("data-number-start"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-end", e.target.getAttribute("data-number-end"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-is-precision", e.target.getAttribute("data-number-is-precision"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-count-digitals", e.target.getAttribute("data-number-count-digitals"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-start", e.target.getAttribute("data-date-start"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-end", e.target.getAttribute("data-date-end"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-fields-groups", e.target.getAttribute("data-fields-groups"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-document-view-type", e.target.getAttribute("data-document-view-type"));

        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-date-is-words-type", e.target.getAttribute("data-date-is-words-type"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-number-is-words-type", e.target.getAttribute("data-number-is-words-type"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-currency-is-words-type", e.target.getAttribute("data-currency-is-words-type"));
        $(".radom-participant-custom-fields-is-edit", jqModal).attr("data-position", e.target.getAttribute("data-position"));

        showModal(jqModal);
        //jqModal.modal({backdrop: false, keyboard: false});
    }

    function initDateInputs(jqDateSelector) {
        $.each(jqDateSelector, function(index, input){
            var $input = $(input);
            $input.radomDateInput({
                startView : 2
            });
        });
    }

    function stripSpecialCharacters(htmlContent){
        return encodeURIComponent(htmlContent).replace(/'/g, "sqquot;");
    }

    function decodeSpecialCharacters(encodedStr) {
        return decodeURIComponent(encodedStr.replace(/sqquot;/g, "'"));
    }

    $(window).load(function() {
        fieldsGroupsTemplateEditor = tinymce.EditorManager.get("fieldsGroupContent");
        tinymce.EditorManager.get("fieldsGroupContent").on("dblclick", function (e) {
            if (e.target.nodeName.toLowerCase() == "span") {
                var dataSpanId = e.target.getAttribute("data-span-id");
                openDialogEditCustomField(dataSpanId, e);
            }
        });
    });

    function getCurrentEditor() {
        return $("#fieldsGroupAddCustomFieldsWindow").is(":visible") ? fieldsGroupsTemplateEditor : (activeEditors[activeEditorIndex] != null ? activeEditors[activeEditorIndex] : tinymce.activeEditor);
    }
</script>


<!-- Модальное окно для добавления пользовательских полей-->
<div class="modal fade radomParticipantCustomFieldsWindow" role="dialog" id="radomParticipantCustomFieldsWindow" aria-labelledby="radomParticipantCustomFieldsLabel"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Добавить пользовательское поле</h4>
            </div>
            <div class="modal-body">
                <input class="radom-participant-custom-fields-is-edit" type="text" value="0" style="display:none">
                <div class="form-group participantCustomFieldsComboboxContainer">
                    <label>Источник данных</label>
                    <select class="participantCustomFieldsCombobox selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                </div>
                <div class="form-group">
                    <label>Наименование поля</label>
                    <input type="text" class="nameCustomField form-control" />
                </div>
                <div class="form-group">
                    <label>Описание поля</label>
                    <input type="text" class="descriptionCustomField form-control" />
                </div>
                <div class="form-group">
                    <label>Порядковый номер поля при заполнении</label>
                    <input type="text" class="fieldPosition form-control" />
                </div>
                <div class="form-group">
                    <label>Тип поля</label>
                    <select class="customFieldsTypeCombobox selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                        <option value="participant">Источник данных</option>
                        <option value="string">Строка</option>
                        <option value="number">Число</option>
                        <option value="currency">Денежный</option>
                        <option value="date">Дата</option>
                        <option value="fieldsGroups">Группы полей</option>
                        <option value="document">Документ</option>
                    </select>
                </div>
                <div class="participantParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Тип источника данных</label>
                        <select class="customFieldsParticipantType form-control">
                            <option value="INDIVIDUAL">Физ. лицо</option>
                            <option value="REGISTRATOR">Регистратор</option>
                            <option value="COMMUNITY_WITH_ORGANIZATION">Объединение в рамках юр. лица</option>
                            <!--option value="COMMUNITY_WITHOUT_ORGANIZATION">Объединение вне рамок юр. лица</option-->
                            <!--option value="COMMUNITY_IP" disabled="disabled">Объединение ИП</option-->
                        </select>
                    </div>
                    <div class="participantTemplateBlock form-group">
                        <label>Шаблон поля источника данных</label>
                        <div style="margin-bottom: 5px;">
                            <div style="display: inline-block; vertical-align: top; width: 441px;">
                                <select id="customFieldsParticipantFields" class="customFieldsParticipantFields selectpicker" data-hide-disabled="true" data-width="100%"></select>
                            </div>
                            <div style="display: inline-block; vertical-align: top;">
                                <button type="button" class="customFieldsParticipantButton btn btn-primary" style="float: left;">Добавить поле</button>
                            </div>
                        </div>
                        <input type="text" class="customFieldsParticipantTemplate form-control" />
                    </div>
                </div>
                <div class="stringParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Маска ввода</label>
                        <input type="text" class="stringMask form-control" />
                    </div>
                    <div class="form-group">
                        <label>Значения для маски:</label>
                        <div>\d - любое число</div>
                        <div>* - любой символ русского, английского алфавита и любая цифра</div>
                        <div>\r - любой символ русского алфавита</div>
                        <div>\e - любой символ английского алфавита</div>
                        <div>\s - любой символ русского, английского алфавита</div>
                        <div>пустое поле - произвольная строка</div>
                    </div>
                    <div class="form-group">
                        <label>Падеж</label>
                        <select class="form-control stringCase">
                            <option value="CASE_I" data-text="Именительный">Именительный (Кто? Что? - Семен Семенович)</option>
                            <option value="CASE_R" data-text="Родительный">Родительный (Кого? Чего? - Семена Семеновича)</option>
                            <option value="CASE_D" data-text="Дательный">Дательный (Кому? Чему? - Семену Семеновичу)</option>
                            <option value="CASE_V" data-text="Винительный">Винительный (Кого? Что? - Семена Семеновича)</option>
                            <option value="CASE_T" data-text="Творительный">Творительный (Кем? Чем? - Семеным Семеновичом)</option>
                            <option value="CASE_P" data-text="Предложный">Предложный (О ком? О чём? В ком? В чём? - Семене Семеновиче)</option>
                        </select>
                    </div>
                </div>
                <div class="numberParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Начальное значение</label>
                        <input type="text" class="numberStartValue form-control" />
                    </div>
                    <div class="form-group">
                        <label>Конечное значение</label>
                        <input type="text" class="numberEndValue form-control" />
                    </div>
                    <div class="form-group">
                        <label>Дробное значение</label>
                        <input type="checkbox" class="numberIsDouble"/>
                    </div>
                    <div class="numberPrecisionForm form-group" style="display: none;">
                        <label>Количество знаков после запятой</label>
                        <input type="text" value="2" class="numberPrecision form-control"/>
                    </div>
                    <label>
                        <input type="checkbox" class="numberWordsValueType" />
                        Число прописью
                    </label>
                </div>
                <div class="currencyParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>
                            <input type="checkbox" class="currencyWordsValueType" />
                            Сумма прописью
                        </label>
                    </div>
                </div>
                <div class="dateParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Начальное значение</label>
                        <input type="text" class="dateStartValue form-control dateInput"/>
                    </div>
                    <div class="form-group">
                        <label>Конечное значение</label>
                        <input type="text" class="dateEndValue form-control dateInput"/>
                    </div>
                    <label>
                        <input type="checkbox" class="dateWordsValueType" />
                        Дата прописью
                    </label>
                </div>
                <div class="fieldsGroupsParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Список групп полей</label>
                        <ol id="fieldsGroupsList"></ol>
                        <a href="javascript:void(0)" id="addFieldsGroup" class="btn btn-default">Добавить группу полей</a>
                    </div>
                </div>
                <div class="documentParameters customFieldParameters" style="display: none;">
                    <div class="form-group">
                        <label>Вид отображения документа</label>
                        <div>
                            <label>
                                Сокращённое наименование документа
                                <input type="radio" id="documentShortName" name="documentParameterName" />
                            </label><br/>
                            <label>
                                Полное наименование документа
                                <input type="radio" id="documentFullName" name="documentParameterName" />
                            </label>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label>
                        <input type="checkbox" class="customFieldIsList" />
                        Поле является списком
                    </label>
                </div>
                <div class="customFieldListParameters" style="display: none;">
                    <div class="form-group">
                        <label title="если значение 0 - то количество не ограничено">Количество элементов списка</label>
                        <input title="если значение 0 - то количество не ограничено" type="text" class="customFieldListSize form-control" />
                    </div>
                    <div class="form-group" >
                        <label>Вид отображения списка полей</label>
                        <select class="customFieldListViewType form-control" data-width="100%">
                            <option value="table">Таблица</option>
                            <option value="byComma">Через запятую</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="radomParticipantCustomFieldsButton btn btn-primary" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Модальное окно для добавления пользовательских полей-->
<div class="modal fade" role="dialog" id="fieldsGroupAddCustomFieldsWindow" aria-labelledby="fieldsGroupAddCustomFieldsWindowLabel"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="fieldsGroupAddCustomFieldsWindowLabel">Добавить группу полей</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Наименование группы</label>
                    <input type="text" id="fieldsGroupName" class="form-control" />
                </div>
                <div class="form-group">
                    <label>Контент группы полей</label>
                    <textarea id="fieldsGroupContent"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="fieldsGroupAddButton" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Модальное окно со списком пользовательских полей-->
<div class="modal fade" role="dialog" id="userFieldsListWindow" aria-labelledby="userFieldsListWindowLabel"  aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="userFieldsListWindowLabel">Список пользовательских полей</h4>
            </div>
            <div class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="saveUserFieldsPositionsButton" style="float: left;">Сохранить порядок</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script id="userFieldsListTemplate" type="x-tmpl-mustache">
    <div class="form-group">
        <label>Список полей</label>
        <ol id="userFieldsList">
            {{#fields}}
                <li participant_name="{{participantName}}" field_name="{{fieldName}}">{{participantName}}:{{fieldName}} ({{type}})</li>
            {{/fields}}
        </ol>
    </div>
</script>

