<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<style>
    #documentCreateHead {display: none !important;}
    #templatesNode {display: none !important;}
    #templatesHeader {display: none !important;}
    #selectedTemplateNode {visibility: visible !important;}
</style>

<%@include file="documentsParentsTypesGrid.jsp" %>
<%@include file="userFieldsEdit.jsp" %>
<script id="document-template-filter-template" type="x-tmpl-mustache">
<div style="margin-bottom: 5px" name="form-group-document-template-filter">
    <input name="filters[{{index}}].id" type="text" value="-1" style="display:none">
    <input name="filters[{{index}}].participant.id" type="text" value="{{filterParticipantId}}" style="display:none">
    <input name="filters[{{index}}].filterField.id" type="text" value="{{filterFieldId}}" style="display:none">
    <div class="input-group input-group-sm">
        <span class="input-group-addon">{{filterParticipantName}}</span>
        <span class="input-group-addon">{{filterFieldName}}</span>
        <input type="text" class="form-control " name="filters[{{index}}].value" value="{{filterValue}}">
        <span class="input-group-btn">
            <a href="#" name="delete-document-template-filter" class="btn btn-danger btn-sm"><i class="glyphicon glyphicon-remove"></i></a>
        </span>
    </div>
</div>
</script>

<script type="text/javascript">
    var templateId = getParameterByName("documentTemplateId");
    var documentClassId = null;
    var countOpenedModals = 0;
    var initedModals = {};
    var needLoadTemplates = false; // Не нужно загружать список шаблонов в форму с предпросмотром документов

    var TEMPLATE_PARTICIPANTS = {};
    <%--<c:forEach var="templateParticipant" items="${documentTemplateForm.templateParticipants}" varStatus="i">
        TEMPLATE_PARTICIPANTS["${templateParticipant.parentParticipantName}${templateParticipant.participantName}"] = "${templateParticipant.participantName}";
    </c:forEach>--%>
    
    var countFilters = 0;
    var templateEditor = null;
    var documentNameEditor = null;
    var documentShortNameEditor = null;
    var selectedCurrencyTypeId = null; // Выбранная валюта у поля с типом "Денежный"

    var documentTemplatePageTemplate = null;
    var participantContainerTemplate = null;

    var FILTERS_INDIVIDUAL = "";
    var FILTERS_INDIVIDUAL_LIST = "";
    var FILTERS_REGISTRATOR = "";
    var FILTERS_COMMUNITY_WITH_ORGANIZATION = "";
    var FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST = "";
    var FILTERS_COMMUNITY_WITHOUT_ORGANIZATION = "";
    var FILTERS_COMMUNITY_IP = "";

    // Массив полей, которые необходимо загружать. Созданы на основе фильтров полей в классе документов
    var PAPRTICIPANTS_FILTERED_FIELDS = {};
    var SYSTEM_FIELDS = {};
    var CLASS_DOCUMENT_PARTICIPANTS = [];

    var windowLoaded = false;

    function showModal(jqModalNode) {
        jqModalNode.modal("show");
        if (!initedModals[jqModalNode.attr("id")]) {
            jqModalNode.on("hidden.bs.modal", function () {
                countOpenedModals--;
                if (countOpenedModals > 0 && !$("body").hasClass("modal-open")) {
                    $("body").addClass("modal-open");
                }
            });
        }
        initedModals[jqModalNode.attr("id")] = true;
        countOpenedModals++;
    }

    function hideModal(jqModalNode) {
        jqModalNode.modal("hide");
    }

    function hashCode(str) {
        var hash = 0, i, chr, len;
        if (str.length == 0) return hash;
        for (i = 0, len = str.length; i < len; i++) {
            chr   = str.charCodeAt(i);
            hash  = ((hash << 5) - hash) + chr;
            hash |= 0; // Convert to 32bit integer
        }
        return hash;
    };

    function loadTemplatePageData(templateId, callBack, errorCallBack) {
        errorCallBack = errorCallBack != null ? errorCallBack : function() {};
        $.radomJsonPost(
            "/admin/flowOfDocuments/documentTemplate/pageData.json",
            {templateId : templateId},
            callBack,
            errorCallBack
        );
    };

    function initPageVars(documentTemplatePageData, callBack) {
        participantContainerTemplate = $("#participantContainerTemplate").html();
        Mustache.parse(participantContainerTemplate);

        for (var index in documentTemplatePageData.templateParticipants) {
            var templateParticipant = documentTemplatePageData.templateParticipants[index];
            TEMPLATE_PARTICIPANTS[templateParticipant.parentParticipantName + templateParticipant.participantName] = templateParticipant.participantName;
        }
        documentClassId = documentTemplatePageData.documentClassId;

        //получить список всех возможных полей фильтров
        getFiltersFields("INDIVIDUAL");
        getFiltersFields("INDIVIDUAL_LIST");
        getFiltersFields("REGISTRATOR");
        getFiltersFields("COMMUNITY_WITH_ORGANIZATION");
        getFiltersFields("COMMUNITY_WITH_ORGANIZATION_LIST");
        getFiltersFields("COMMUNITY_WITHOUT_ORGANIZATION");
        getFiltersFields("COMMUNITY_IP");

        // Массив полей, которые необходимо загружать. Созданы на основе фильтров полей в классе документов
        getParticipantsFilteredFields();
        getSystemFields();
        getParticipants(callBack);
    }

    function initDocumentTemplatePage(documentTemplatePageData) {
        var model = documentTemplatePageData;
        documentTemplatePageTemplate = $("#documentTemplatePageTemplate").html();
        Mustache.parse(documentTemplatePageTemplate);

        var documentTemplateMarkup = $(Mustache.render(
                documentTemplatePageTemplate,
                {
                    documentTemplate : model,
                    hasTemplateId : documentTemplatePageData.id != null,
                    hasHelpLink : documentTemplatePageData.helpLink != null && documentTemplatePageData.helpLink != ""
                }
        ));

        $("#documentTemplateBlock").html(documentTemplateMarkup);
    }

    $(document).ready(function () {
        loadTemplatePageData(templateId, function(documentTemplatePageData){
            initDocumentTemplatePage(documentTemplatePageData);
            initPageVars(documentTemplatePageData, function(){
                initTemplatePage();
            });
        });
    });

    $(window).load(function() {
        windowLoaded = true;
    });


    function initTemplatePage() {
        var height = $(window).height() - 490;
        if (height < 200) {
            height = 200;
        }

        $("#field-documentShortName").css("height", "100px").radomTinyMCE({
            useRadomParticipantFilter : true,
            useRadomParticipantCustomFields : true,
            useRadomParticipantCustomText : true,
            useRadomSystemFields : true,
            useRadomCopyPasteFields : true,
            userRadomGroupFields : true
        });

        $("#field-documentName").css("height", "100px").radomTinyMCE({
            useRadomParticipantFilter : true,
            useRadomParticipantCustomFields : true,
            useRadomParticipantCustomText : true,
            useRadomSystemFields : true,
            useRadomCopyPasteFields : true,
            userRadomGroupFields : true
        });

        $("#field-content").css("height", height + "px").radomTinyMCE({
            useRadomParticipantFilter : true,
            useRadomParticipantCustomFields : true,
            useRadomParticipantCustomText : true,
            useRadomSystemFields : true,
            useRadomCopyPasteFields : true,
            userRadomGroupFields : true
        });

        $("#field-name").focus();
        countFilters = $("[name='form-group-document-template-filter']").size();

        $("#save-documentTemplate-button").click(function () {
            if ($("#field-name").val() == "") {
                bootbox.alert("Введите наименование шаблона!");
                return false;
            }
            if ($("#field-content").val() == "") {
                bootbox.alert("Введите содержимое шаблона!");
                return false;
            }

            //var participantIndex = 0;
            var documentParticipants = [];
            $(".participantContainer").each(function() {
                var jqInputParentName = $(".parentTemplateParticipants", $(this));
                var jqInputName = $(".templateParticipants", $(this));
                var checked = jqInputName.prop("checked");
                jqInputParentName.prop("checked", checked);
                if (checked) {
                    documentParticipants.push({
                        parentParticipantName : jqInputParentName.val(),
                        participantName : jqInputName.val()
                    });
                    /*jqInputName.attr("name", "templateParticipants[" + participantIndex + "].participantName");
                    jqInputParentName.attr("name", "templateParticipants[" + participantIndex + "].parentParticipantName");*/
                    //participantIndex++;
                }
            });

            var documentTemplate = {
                id: $("#field-documentTemplateId").val(),
                documentClassId: $("#field-documentClassId").val(),
                name: $("#field-name").val(),
                documentShortName: $("#field-documentShortName").val(),
                documentName: $("#field-documentName").val(),
                content: $("#field-content").val(),
                code: $("#field-code").val(),
                templateParticipants: documentParticipants,
                helpLink: $("#field-helpLink").val(),
                pdfExportArguments : $("#pdfExportArguments").val()
            };

            $.radomJsonPostWithWaiter(
                    "/admin/flowOfDocuments/documentTemplate/save.json",
                    JSON.stringify(documentTemplate),
                    null,
                    null,
                    {
                        contentType: "application/json"
                    }
            );

            return false;
        });
        $("#delete-documentTemplate-button").click(function () {
            bootbox.confirm("Вы действительно хотите удалить шаблон?", function(result){
               if (result) {
                   var id = $("#field-documentTemplateId").val();
                   $.ajax({
                       url: "/admin/flowOfDocuments/documentTemplate/delete?id=" + id,
                       type: "post",
                       data: "{}",
                       datatype: "json",
                       success: function (response) {
                           if (response.result != "error") {
                               window.location.replace("/admin/flowOfDocuments/documentTemplates");
                           } else {
                               bootbox.alert(response.message);
                           }
                       }
                   });
               }
            });
            return false;
        });

        var selectedParticipantId = -1;
        $("#participant-combobox").on("change", function() {
            selectedParticipantId = $("#participant-combobox").find("option:selected").val();
            var participantType = $("#participant-combobox").find("option:selected").attr("data-participant-type");
            var associationForm = parseInt($("#participant-combobox").find("option:selected").attr("data-association-form"));

            $("#community-with-organization-form-groups").css({"display": "none"});
            //$("#custom-attribute-filter-group").css({"display": "none"});
            $("#community-with-organization-form-select").val(null);
            $("#custom-attribute-filter-cases").val("CASE_I");
            $("#custom-attribute-chars-type").val("NORMAL");

            if (participantType == "INDIVIDUAL") {
                fillFiltersComboBox("filters-combobox", FILTERS_INDIVIDUAL, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            } else if (participantType == "INDIVIDUAL_LIST") {
                fillFiltersComboBox("filters-combobox", FILTERS_INDIVIDUAL_LIST, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            } else if (participantType == "REGISTRATOR") {
                fillFiltersComboBox("filters-combobox", FILTERS_REGISTRATOR, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            } else if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                $("#community-with-organization-form-groups").css({"display": "block"});
                fillFiltersComboBox("filters-combobox", FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
                // Загружаем наименования групп полей пользователей организации
                fillCommunityGroups("community-with-organization-form-select", FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm);
            } else if (participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                fillFiltersComboBox("filters-combobox", FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            } else if (participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
                fillFiltersComboBox("filters-combobox", FILTERS_COMMUNITY_WITHOUT_ORGANIZATION, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            } else if (participantType == "COMMUNITY_IP") {
                fillFiltersComboBox("filters-combobox", FILTERS_COMMUNITY_IP, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            }

            $("#filters-combobox").removeAttr("disabled");
            $("#filters-combobox").removeAttr("title");
            $("#filters-combobox").selectpicker("refresh");

            if ($("#radom-participant-filter-is-edit").val() > 0) {
                var dataObjectId = parseInt($("#radom-participant-filter-is-edit").attr("data-field-id"));
                var dataObjectIsMetaField = $("#radom-participant-filter-is-edit").attr("data-is-meta-field");
                var dataCaseId = $("#radom-participant-filter-is-edit").attr("data-case-id");
                var charsType = $("#radom-participant-filter-is-edit").attr("data-chars-type");

                var selectedFieldOption = $("#filters-combobox").find("optgroup option[data-object-id=" + dataObjectId + "][data-object-is-meta-field='" + dataObjectIsMetaField + "']");
                selectedFieldOption.attr("selected", "selected");
                $("#filters-combobox").trigger("change");
                $("#custom-attribute-filter-cases").val(dataCaseId);

                if (charsType != "UPPERCASE" && charsType != "NORMAL" && charsType != "LOWERCASE") {
                    charsType = "NORMAL";
                }

                $("#custom-attribute-chars-type").val(charsType);

                var type = selectedFieldOption.attr("data-object-type");
                switch(type) {
                    case "DATE":
                        $("#field-date-attributes").show();
                        var dateIsWord = $("#radom-participant-filter-is-edit").attr("data-object-date-is-word");
                        dateIsWord = (dateIsWord == null || dateIsWord == "") ? false : (dateIsWord == true || "true" == dateIsWord);
                        $("#data-object-date-is-word").prop("checked", dateIsWord);
                        break;
                    case "NUMBER":
                        $("#field-number-attributes").show();
                        var numberIsWord = $("#radom-participant-filter-is-edit").attr("data-object-number-is-word");
                        numberIsWord = (numberIsWord == null || numberIsWord == "") ? false : (numberIsWord == true || "true" == numberIsWord);
                        $("#data-object-number-is-word").prop("checked", numberIsWord);
                        break;
                    case "CURRENCY":
                        $("#field-currency-attributes").show();
                        var currencyIsWord = $("#radom-participant-filter-is-edit").attr("data-object-currency-is-word");
                        currencyIsWord = (currencyIsWord == null || currencyIsWord == "") ? false : (currencyIsWord == true || "true" == currencyIsWord);
                        $("#data-object-currency-is-word").prop("checked", currencyIsWord);
                        var selectedCurrencyTypeId = $("#radom-participant-filter-is-edit").attr("data-object-currency-type");
                        // Инициализация валюты
                        $("#currencyTypeControl").empty();
                        RameraListEditorModule.init($("#currencyTypeControl"),
                            {
                                labelClasses: ["checkbox-inline"],
                                labelStyle: "margin-left: 10px;",
                                selectClasses: ["form-control"],
                                selectedItems : [selectedCurrencyTypeId]
                            },
                            function (event, data) {
                                if (event == RameraListEditorEvents.VALUE_CHANGED) {
                                    selectedCurrencyTypeId = data.value;
                                }
                            }
                        );
                        break;
                    case "SYSTEM_IMAGE": // Картинка из custom источника
                        $("#field-system-image-attributes").show();
                        var imageSize = $("#radom-participant-filter-is-edit").attr("data-system-image-size");
                        var imageFloat = $("#radom-participant-filter-is-edit").attr("data-system-image-float");
                        $("#system-image-size").val(imageSize);
                        $("#system-image-float").val(imageFloat);
                        break;
                    default:
                        $("#field-date-attributes").hide();
                        $("#field-number-attributes").hide();
                        $("#field-currency-attributes").hide();
                        $("#field-system-image-attributes").hide();
                        break;
                }
            } else {
                $("#filters-combobox").selectpicker("val", null);
            }
            $("#filters-combobox").selectpicker("refresh");
        });

        $("#community-with-organization-form-select").on("change", function() {
            var selectedValue = $("#community-with-organization-form-select").val();

            // Выбран участник объединения
            if (selectedValue != null && selectedValue != "") {
                fillFiltersComboBox("filters-combobox", FILTERS_INDIVIDUAL, -1);
            } else { // Выбраны поля самой организации
                var associationForm = parseInt($("#participant-combobox").find("option:selected").attr("data-association-form"));
                fillFiltersComboBox("filters-combobox", FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm, PAPRTICIPANTS_FILTERED_FIELDS[selectedParticipantId]);
            }
            $("#filters-combobox").removeAttr("disabled");
            $("#filters-combobox").removeAttr("title");
            $("#filters-combobox").selectpicker("refresh");
            $("#filters-combobox").selectpicker("val", null);
            //$("#custom-attribute-filter-group").css({"display": "none"});
            $("#custom-attribute-filter-cases").val("CASE_I");
            $("#custom-attribute-chars-type").val("NORMAL");
        });
        $("#filters-combobox").on("change", function() {
            var useCase = $("#filters-combobox").find("option:selected").attr("data-object-use-case");
            /*$("#custom-attribute-filter-group").css({"display": "block"});
            $("#custom-attribute-filter-cases-lbl").css({"display": "none"});
            $("#custom-attribute-filter-cases").css({"display": "none"});
            // Если есть выбор падежа у участника, то отобразить комбобокс падежей.
            if (useCase == "true") {
                $("#custom-attribute-filter-cases-lbl").css({"display": "block"});
                $("#custom-attribute-filter-cases").css({"display": "block"});
            }*/
            $("#custom-attribute-filter-cases").val("CASE_I");
            $("#custom-attribute-chars-type").val("NORMAL");
            var type = $("#filters-combobox").find("option:selected").attr("data-object-type");
            switch(type) {
                case "DATE":
                    $("#field-date-attributes").show();
                    break;
                case "NUMBER":
                    $("#field-number-attributes").show();
                    break;
                case "CURRENCY":
                    $("#field-currency-attributes").show();
                    break;
                case "SYSTEM_IMAGE":
                    $("#field-system-image-attributes").show();
                    break;
                default:
                    $("#field-date-attributes").hide();
                    $("#field-number-attributes").hide();
                    $("#field-currency-attributes").hide();
                    $("#field-system-image-attributes").hide();
                    break;
            }
        });
        $("#radomParticipantFilter-button").click(function () {
            var participantId = $("#participant-combobox").find("option:selected").attr("data-object-id");
            var participantName = $("#participant-combobox").find("option:selected").text();
            var fieldId = $("#filters-combobox").find("option:selected").attr("data-object-id");
            var fieldName = $("#filters-combobox").find("option:selected").val();
            var groupId = $("#community-with-organization-form-select").val();
            var groupName = $("#community-with-organization-form-select option:selected").text();
            var caseId = $("#custom-attribute-filter-cases").val();
            var charsType = $("#custom-attribute-chars-type").val();
            var caseName = $("#custom-attribute-filter-cases option:selected").attr("data-text");
            var isMetaField = $("#filters-combobox").find("option:selected").attr("data-object-is-meta-field");
            var internalName = $("#filters-combobox").find("option:selected").attr("data-object-internal-name");

            var type = $("#filters-combobox").find("option:selected").attr("data-object-type");
            var additionalParameters = {};
            switch(type) {
                case "DATE":
                    additionalParameters["data-object-date-is-word"] = $("#data-object-date-is-word").prop("checked");
                    break;
                case "NUMBER":
                    additionalParameters["data-object-number-is-word"] = $("#data-object-number-is-word").prop("checked");
                    break;
                case "CURRENCY":
                    additionalParameters["data-object-currency-is-word"] = $("#data-object-currency-is-word").prop("checked");
                    additionalParameters["data-object-currency-type"] = selectedCurrencyTypeId;
                    break;
                case "SYSTEM_IMAGE":
                    additionalParameters["data-system-image-size"] = $("#system-image-size").val();
                    additionalParameters["data-system-image-float"] = $("#system-image-float").val();
                default:
                    break;
            }

            if (participantId == undefined) {
                bootbox.alert("Укажите источник данных!");
                return false;
            }
            if ($("#community-with-organization-form-select").visible()) {
                if (groupId == null) {
                    bootbox.alert("Укажите группу!");
                    return false;
                }
            }
            if (fieldId == undefined) {
                bootbox.alert("Укажите поле!");
                return false;
            }
            if ($("#custom-attribute-filter-cases").visible()) {
                if (caseId == null) {
                    bootbox.alert("Укажите падеж!");
                    return false;
                }
            }

            if (groupId != null && groupId != "") {
                var signParticipantId = hashCode(groupName);
            } else {
                var signParticipantId = hashCode(participantName);
            }
            $("#" + signParticipantId).prop("checked", true);

            var additionalParametersString = "";
            for (var paramName in additionalParameters) {
                var paramValue = additionalParameters[paramName];
                additionalParametersString += paramName + "='" + paramValue + "'";
            }

            var insertedCaption = "[" + participantName + ((groupId != null && groupId != "") ? ":" + groupName : "") + ":" + fieldName + (caseId != null ? ":" + caseName : "") + "]";
            var insertedContent = "<span data-placeholder class='mceNonEditable' data-mce-contenteditable='false' data-span-type='radom-participant-filter' " +
                    "data-span-id='" + Math.round(new Date().getTime() + (Math.random() * 100))  + "' " +
                    "data-is-meta-field='" + isMetaField + "' data-participant-id='" + participantId + "' " +
                    (groupId != null ? "data-group-internal-name='" + groupId + "' " : "") +
                    "data-field-id='" + fieldId + "' data-internal-name='" + internalName + "' " +
                    (caseId != null ? "data-case-id='" + caseId + "' " : "") + " data-chars-type='" + charsType + "' " +
                    additionalParametersString +" >" + insertedCaption + "</span>&nbsp;";

            if ($("#radom-participant-filter-is-edit").val() == 0) {
                tinymce.activeEditor.selection.setContent(insertedContent);
            } else {
                var spanId = $("#radom-participant-filter-is-edit").attr("data-span-id");
                var ed = tinymce.activeEditor;
                var element = ed.dom.getParent(ed.selection.getNode(), "span[data-span-id='" + spanId + "']");
                element.setAttribute("data-is-meta-field", isMetaField);
                element.setAttribute("data-participant-id", participantId);
                if (groupId != null) {
                    element.setAttribute("data-group-internal-name", groupId);
                } else {
                    element.removeAttribute("data-group-internal-name");
                }
                element.setAttribute("data-field-id", fieldId);
                if (caseId != null) {
                    element.setAttribute("data-case-id", caseId);
                } else {
                    element.removeAttribute("data-case-id");
                }
                if (charsType != null) {
                    element.removeAttribute("data-chars-type");
                    element.setAttribute("data-chars-type", charsType);
                }
                element.setAttribute("data-internal-name", internalName);
                for (var paramName in additionalParameters) {
                    var paramValue = additionalParameters[paramName];
                    element.setAttribute(paramName, paramValue);
                }
                element.textContent = insertedCaption;
            }

            //$("#radomParticipantFilterWindow").modal("hide");
            hideModal($("#radomParticipantFilterWindow"));
            tinymce.activeEditor.focus();

            //парсить вставленные теги плагином "Добавить [Участник:Поле]" можно так
            /*
            var html = jQuery($("#field-content").val())
            $.each(html.find("span[data-placeholder]"), function(index, span) {
                var $span = $(span);
                var label = $span.html();
                var placeholder = $(span).attr("data-participant-id");
                $span.replaceWith(label);
                console.log(label);
            });
            */
            return false;
        });
        $("#radomParticipantFilterWindow").on("show.bs.modal", function () {
            fillParticipantsComboBox($("#participant-combobox"));

            if ($("#radom-participant-filter-is-edit").val() == 0) {
                $("#radomParticipantFilter-button").text("Добавить");
                $("#radomParticipantFilterLabel").text("Добавить [Источник данных:Поле]");
                $("#filters-combobox").attr("disabled", "disabled");
                $("#filters-combobox").attr("title", "Укажите источник данных");
                $("#filters-combobox").selectpicker("refresh");
                $("#filters-combobox").selectpicker("val", null);

                $("#community-with-organization-form-groups").css({"display": "none"});
                //$("#custom-attribute-filter-group").css({"display": "none"});
                $("#community-with-organization-form-select").val(null);
                $("#custom-attribute-filter-cases").val("CASE_I");
                $("#custom-attribute-chars-type").val("NORMAL");
                $("#field-date-attributes").hide();
                $("#field-number-attributes").hide();
                $("#field-currency-attributes").hide();
                $("#field-system-image-attributes").hide();
            } else {
                $("#radomParticipantFilter-button").text("Сохранить");
                $("#radomParticipantFilterLabel").text("Редактировать [Источник данных:Поле]");
                $("#filters-combobox").removeAttr("disabled");
            }
        });
        $("#radomParticipantFilterWindow").on("shown.bs.modal", function () {
            if ($("#radom-participant-filter-is-edit").val() > 0) {
                $("#participant-combobox").selectpicker("refresh");
                $("#participant-combobox").selectpicker("val", parseInt($("#radom-participant-filter-is-edit").val()));
                $("#participant-combobox").trigger("change");
            } else {
                // Инициализация валюты
                $("#currencyTypeControl").empty();
                RameraListEditorModule.init($("#currencyTypeControl"),
                        {
                            labelClasses: ["checkbox-inline"],
                            labelStyle: "margin-left: 10px;",
                            selectClasses: ["form-control"]
                        },
                        function (event, data) {
                            if (event == RameraListEditorEvents.VALUE_CHANGED) {
                                selectedCurrencyTypeId = data.value;
                            }
                        }
                );
            }
        });
        $("#radomParticipantFilterWindow").on("hide.bs.modal", function () {
            $("#radom-participant-filter-is-edit").val(0);
        });

        $("#edit-filters-documentTemplate-button").click(function () {
            showModal($("#editFiltersWindow"));
            //$("#editFiltersWindow").modal({backdrop: false, keyboard: false});
            return false;
        });
        $("#editFiltersWindow").on("show.bs.modal", function () {
            fillParticipantsComboBox($("#document-template-participants-combobox"));
            $("#document-template-filters-combobox").attr("disabled", "disabled");
            $("#document-template-filters-combobox").attr("title", "Укажите источник данных");
            $("#document-template-filters-combobox").selectpicker("refresh");
            $("#document-template-filters-combobox").selectpicker("val", null);
        });
        $("#document-template-filters-combobox").on("change", function() {
            var fieldId = $(this).find("option:selected").attr("data-object-id");
            var fieldName = $(this).find("option:selected").val();
            var participantId = $("#document-template-participants-combobox").find("option:selected").attr("data-object-id");
            var participantName = $("#document-template-participants-combobox").find("option:selected").text();

            var template = $("#document-template-filter-template").html();
            Mustache.parse(template);
            countFilters = countFilters + 1;
            var rendered = Mustache.render(template, {index: countFilters, filterFieldId: fieldId, filterFieldName: fieldName, filterParticipantId: participantId, filterParticipantName: participantName, filterValue: ""});
            $("#form-group-filters").append(rendered);
            updateEvents();

            $(this).val(null);
        });
        $("#document-template-participants-combobox").on("change", function() {
            var participantType = $("#document-template-participants-combobox").find("option:selected").attr("data-participant-type");
            var associationForm = parseInt($("#document-template-participants-combobox").find("option:selected").attr("data-association-form"));
            fillComboBoxByParticipantType(participantType, associationForm, "document-template-filters-combobox");
        });

        updateEvents();
        function updateEvents() {
            $("a[name='delete-document-template-filter']").on("click", function (e) {
                $(this).parent().parent().parent().remove();
                return false;
            });
        }

        $("#radomParticipantCustomText-button").click(function () {
            var maleCustomText = $("#male-custom-text").val();
            var femaleCustomText = $("#female-custom-text").val();
            
            if (maleCustomText == null || maleCustomText == "") {
                bootbox.alert("Укажите текст мужского варианта!");
                return false;
            }
            if (femaleCustomText == null || femaleCustomText == "") {
                bootbox.alert("Укажите текст женского варианта!");
                return false;
            }

            var jqParticipantSelect = $("#participant-custom-text-combobox");
            var participantId = $("option:selected", jqParticipantSelect).attr("data-object-id");
            var participantName = $("option:selected", jqParticipantSelect).text();

            var signParticipantId = hashCode(participantName);
            $("#" + signParticipantId).prop("checked", true);

            var insertedCaption = "[" + participantName + ":" + maleCustomText + "]";
            var insertedContent = "<span data-placeholder class='mceNonEditable' data-mce-contenteditable='false' data-span-type='participant-custom-text' " +
                    "data-span-id='" + Math.round(new Date().getTime() + (Math.random() * 100))  + "' " +
                    "data-participant-id='" + participantId  + "' " +
                    "data-participant-name='" + participantName + "' " +
                    "data-custom-text-male='" + maleCustomText + "' " +
                    "data-custom-text-female='" + femaleCustomText + "' " +
                    " >" + insertedCaption + "</span>&nbsp;";

            if ($("#participant-custom-text-is-edit").val() == 0) {
                tinymce.activeEditor.selection.setContent(insertedContent);
            } else {
                var spanId = $("#participant-custom-text-is-edit").attr("data-span-id");
                var ed = tinymce.activeEditor;
                var element = ed.dom.getParent(ed.selection.getNode(), "span[data-span-id='" + spanId + "']");
                element.setAttribute("data-participant-id", participantId);
                element.setAttribute("data-participant-name", participantName);
                element.setAttribute("data-custom-text-male", maleCustomText);
                element.setAttribute("data-custom-text-female", femaleCustomText);
                element.textContent = insertedCaption;
            }

            //$("#radomParticipantCustomTextWindow").modal("hide");
            hideModal($("#radomParticipantCustomTextWindow"));
            tinymce.activeEditor.focus();
            return false;
        });
        $("#radomParticipantCustomTextWindow").on("show.bs.modal", function () {
            if ($("#participant-custom-text-is-edit").val() == 0) {
                $("#radomParticipantCustomText-button").text("Добавить");
                $("#radomParticipantCustomTextLabel").text("Добавить текст с указанием пола");
                var isModalVisible = $(this).is(":visible");
                if (!isModalVisible) {
                    fillParticipantsComboBox($("#participant-custom-text-combobox"));
                    $("#male-custom-text").val("Доступно только для физ. лиц.");
                    $("#male-custom-text").attr("disabled", "disabled");
                    $("#female-custom-text").val("Доступно только для физ. лиц.");
                    $("#female-custom-text").attr("disabled", "disabled");
                    $("#male-custom-text-button").attr("disabled", "disabled");
                    $("#female-custom-text-button").attr("disabled", "disabled");
                    $("#radomParticipantCustomText-button").attr("disabled", "disabled");
                }
            } else {
                $("#radomParticipantCustomText-button").text("Сохранить");
                $("#radomParticipantCustomTextLabel").text("Редактировать текст с указанием пола");
            }
        });
        $("#radomParticipantCustomTextWindow").on("shown.bs.modal", function () {
            if ($("#participant-custom-text-is-edit").val() > 0) {
                $("#participant-custom-text-combobox").selectpicker("refresh");
                $("#participant-custom-text-combobox").selectpicker("val", parseInt($("#participant-custom-text-is-edit").val()));
            }
        });
        $("#radomParticipantCustomTextWindow").on("hide.bs.modal", function () {
            $("#participant-custom-text-is-edit").val(0);
        });
        $("#participant-custom-text-combobox").on("change", function() {
            var participantType = $("#participant-custom-text-combobox").find("option:selected").attr("data-participant-type");
            if (participantType == "INDIVIDUAL") {
                $("#male-custom-text").val("");
                $("#male-custom-text").removeAttr("disabled");
                $("#female-custom-text").val("");
                $("#female-custom-text").removeAttr("disabled");
                $("#male-custom-text-button").removeAttr("disabled");
                $("#female-custom-text-button").removeAttr("disabled");
                $("#radomParticipantCustomText-button").removeAttr("disabled");
            } else {
                $("#male-custom-text").val("Доступно только для физ. лиц.");
                $("#male-custom-text").attr("disabled", "disabled");
                $("#female-custom-text").val("Доступно только для физ. лиц.");
                $("#female-custom-text").attr("disabled", "disabled");
                $("#male-custom-text-button").attr("disabled", "disabled");
                $("#female-custom-text-button").attr("disabled", "disabled");
                $("#radomParticipantCustomText-button").attr("disabled", "disabled");
            }
        });
        $("#female-custom-text-button").click(function () {
            $("#female-custom-text").val($("#male-custom-text").val());
        });
        $("#male-custom-text-button").click(function () {
            $("#male-custom-text").val($("#female-custom-text").val());
        });

        // Обработка диалогового окна с системныни полями
        $("#radomSystemFieldsWindow").on("show.bs.modal", function () {
            // Комбобокс с системыми полями шаблона документа
            fillSystemFieldsComboBox("radomSystemFieldsСombobox", SYSTEM_FIELDS);
            var jqSystemFieldsCombo = $("#radomSystemFieldsСombobox");

            if ($("#radom-system-fields-is-edit").val() == 0) {
                $("#radomSystemFieldsWindowButton").text("Добавить");
                $("#radomSystemFieldsTextLabel").text("Добавить системное поле");
                jqSystemFieldsCombo.attr("title", "Укажите системное поле");
                jqSystemFieldsCombo.selectpicker("refresh");
                jqSystemFieldsCombo.selectpicker("val", "");
            } else {
                $("#radomSystemFieldsWindowButton").text("Сохранить");
                $("#radomSystemFieldsTextLabel").text("Редактировать системное поле");
            }
        });
        $("#radomSystemFieldsWindow").on("shown.bs.modal", function () {
            if ($("#radom-system-fields-is-edit").val() > 0) {
                var dataObjectId = parseInt($("#radom-system-fields-is-edit").attr("data-field-id"));
                var dataObjectInternalName = $("#radom-system-fields-is-edit").attr("data-system-field-internal-name");
                var jqOption = $("#radomSystemFieldsСombobox").find("optgroup option[data-object-id=" + dataObjectId + "][data-object-internal-name='" + dataObjectInternalName + "']");
                jqOption.attr("selected", "selected");
                $("#radomSystemFieldsСombobox").selectpicker("refresh");

                switch(dataObjectInternalName) {
                    case "DATE_CREATE_DOCUMENT": // Дата создания документа
                    case "DATE_LAST_SIGN_DOCUMENT": // Дата подписания документа
                        $("#system-field-date-attributes").show();
                        var dateIsWord = $("#radom-system-fields-is-edit").attr("data-object-system-date-is-word");
                        dateIsWord = (dateIsWord == null || dateIsWord == "") ? false : (dateIsWord == true || "true" == dateIsWord);
                        $("#data-object-system-date-is-word").prop("checked", dateIsWord);
                        break;
                    default:
                        $("#system-field-date-attributes").hide();
                        break;
                }
            }
        });
        $("#radomSystemFieldsWindow").on("hide.bs.modal", function () {
            $("#radom-system-fields-is-edit").val(0);
        });
        $("#groupFieldsWindow").on("shown.bs.modal", function () {
            if ($("#radom-group-fields-is-edit").val() > 0) {
                var joinString = $("#radom-group-fields-is-edit").attr("data-join-string");
                $("#groupFieldsJoinString").val(joinString);
            }
        });
        $("#groupFieldsWindow").on("hide.bs.modal", function () {
            $("#radom-group-fields-is-edit").val(0);
        });



        $("#radomSystemFieldsСombobox").on("change", function(){
            var jqOption = $("#radomSystemFieldsСombobox").find("option:selected");
            var dataObjectInternalName = jqOption.attr("data-object-internal-name");

            switch(dataObjectInternalName) {
                case "DATE_CREATE_DOCUMENT": // Дата создания документа
                case "DATE_LAST_SIGN_DOCUMENT": // Дата подписания документа
                    $("#system-field-date-attributes").show();
                    break;
                default:
                    $("#system-field-date-attributes").hide();
                    break;
            }
        });

        // Обработчик добавления системного поля
        $("#radomSystemFieldsWindowButton").click(function(){
            var systemFieldId = $("option:selected", "#radomSystemFieldsСombobox").attr("data-object-id");
            var systemFieldInternalName = $("option:selected", "#radomSystemFieldsСombobox").attr("data-object-internal-name");
            var systemFieldName = $("option:selected", "#radomSystemFieldsСombobox").text();
            if (systemFieldId == null || systemFieldId == "") {
                bootbox.alert("Укажите поле!");
                return false;
            }

            var additionalParameters = {};
            switch(systemFieldInternalName) {
                case "DATE_CREATE_DOCUMENT": // Дата создания документа
                case "DATE_LAST_SIGN_DOCUMENT": // Дата подписания документа
                    additionalParameters["data-object-system-date-is-word"] = $("#data-object-system-date-is-word").prop("checked");
                    break;
                default:
                    break;
            }

            var additionalParametersString = "";
            for (var paramName in additionalParameters) {
                var paramValue = additionalParameters[paramName];
                additionalParametersString += paramName + "='" + paramValue + "' ";
            }

            var insertedCaption = "[документ:" + systemFieldName + "]";
            var insertedContent = "<span data-placeholder class='mceNonEditable' data-mce-contenteditable='false' data-span-type='radom-system-fields' " +
                    "data-span-id='" + Math.round(new Date().getTime() + (Math.random() * 100))  + "' " +
                    "data-field-id='" + systemFieldId + "' " +
                    "data-system-field-internal-name='" + systemFieldInternalName + "' " +
                    additionalParametersString +
                    " >" + insertedCaption + "</span>&nbsp;";

            if ($("#radom-system-fields-is-edit").val() == 0) {
                tinymce.activeEditor.selection.setContent(insertedContent);
            } else {
                var spanId = $("#radom-system-fields-is-edit").attr("data-span-id");
                var ed = tinymce.activeEditor;
                var element = ed.dom.getParent(ed.selection.getNode(), "span[data-span-id='" + spanId + "']");
                element.setAttribute("data-field-id", systemFieldId);
                element.setAttribute("data-system-field-internal-name", systemFieldInternalName);
                for (var paramName in additionalParameters) {
                    var paramValue = additionalParameters[paramName];
                    element.setAttribute(paramName, paramValue);
                }
                element.textContent = insertedCaption;
            }

            //$("#radomSystemFieldsWindow").modal("hide");
            hideModal($("#radomSystemFieldsWindow"));
            tinymce.activeEditor.focus();
            return false;
        });

        // Обработчик добавления начала группы полей
        $("#groupFieldsWindowButton").click(function () {
            var groupFieldsJoinString = $("#groupFieldsJoinString").val();

            var insertedContent = "<span data-placeholder class='mceNonEditable groupFieldStart' data-mce-contenteditable='false' data-join-string='" + groupFieldsJoinString + "' >[[</span>&nbsp;&nbsp;";

            if ($("#radom-group-fields-is-edit").val() == 0) {
                tinymce.activeEditor.selection.setContent(insertedContent);
            } else {
                var element = tinymce.activeEditor.selection.getNode();
                element.setAttribute("data-join-string", groupFieldsJoinString);
            }

            hideModal($("#groupFieldsWindow"));
            tinymce.activeEditor.focus();
            return false;
        });

        function initTemplateEditor(editor) {
            editor.on("dblclick", function(e) {
                if (e.target.nodeName.toLowerCase() == "span") {
                    var dataSpanId = e.target.getAttribute("data-span-id");
                    var dataSpanType = e.target.getAttribute("data-span-type");
                    var isGroupFieldStart = e.target.getAttribute("class").indexOf("groupFieldStart") > -1;

                    if (dataSpanType == "radom-participant-filter") {
                        $("#radom-participant-filter-is-edit").val(e.target.getAttribute("data-participant-id"));
                        $("#radom-participant-filter-is-edit").attr("data-span-id", dataSpanId);
                        $("#radom-participant-filter-is-edit").attr("data-field-id", e.target.getAttribute("data-field-id"));
                        $("#radom-participant-filter-is-edit").attr("data-is-meta-field", e.target.getAttribute("data-is-meta-field"));
                        $("#radom-participant-filter-is-edit").attr("data-case-id", e.target.getAttribute("data-case-id"));
                        $("#radom-participant-filter-is-edit").attr("data-chars-type", e.target.getAttribute("data-chars-type"));
                        $("#radom-participant-filter-is-edit").attr("data-object-date-is-word", e.target.getAttribute("data-object-date-is-word")); // Поле с типом дата - прописью
                        $("#radom-participant-filter-is-edit").attr("data-object-number-is-word", e.target.getAttribute("data-object-number-is-word")); // Поле с типом число - прописью
                        $("#radom-participant-filter-is-edit").attr("data-object-currency-is-word", e.target.getAttribute("data-object-currency-is-word")); // Поле с денежным типом - прописью
                        $("#radom-participant-filter-is-edit").attr("data-object-currency-type", e.target.getAttribute("data-object-currency-type")); // Поле с типом валюты
                        $("#radom-participant-filter-is-edit").attr("data-system-image-size", e.target.getAttribute("data-system-image-size")); // Поле с размером картинки
                        $("#radom-participant-filter-is-edit").attr("data-system-image-float", e.target.getAttribute("data-system-image-float")); // Поле с типом отображения картинки
                        //$("#radomParticipantFilterWindow").modal({backdrop: false, keyboard: false});
                        showModal($("#radomParticipantFilterWindow"));
                    } else if (dataSpanType == "participant-custom-text") {
                        fillParticipantsComboBox($("#participant-custom-text-combobox"));

                        $("#participant-custom-text-is-edit").val(e.target.getAttribute("data-participant-id"));
                        $("#participant-custom-text-is-edit").attr("data-span-id", dataSpanId);

                        $("#male-custom-text").removeAttr("disabled");
                        $("#male-custom-text").val(e.target.getAttribute("data-custom-text-male"));
                        $("#female-custom-text").removeAttr("disabled");
                        $("#female-custom-text").val(e.target.getAttribute("data-custom-text-female"));
                        $("#male-custom-text-button").removeAttr("disabled");
                        $("#female-custom-text-button").removeAttr("disabled");
                        //$("#radomParticipantCustomTextWindow").modal({backdrop: false, keyboard: false});
                        showModal($("#radomParticipantCustomTextWindow"));
                    } else if (dataSpanType == "radom-participant-custom-fields") {
                        openDialogEditCustomField(dataSpanId, e);
                    } else if (dataSpanType == "radom-system-fields") {
                        $("#radom-system-fields-is-edit").val(1);
                        $("#radom-system-fields-is-edit").attr("data-span-id", dataSpanId);
                        $("#radom-system-fields-is-edit").attr("data-field-id", e.target.getAttribute("data-field-id"));
                        $("#radom-system-fields-is-edit").attr("data-system-field-internal-name", e.target.getAttribute("data-system-field-internal-name"));
                        $("#radom-system-fields-is-edit").attr("data-object-system-date-is-word", e.target.getAttribute("data-object-system-date-is-word"));
                        //$("#radomSystemFieldsWindow").modal({backdrop: false, keyboard: false});
                        showModal($("#radomSystemFieldsWindow"));
                    } else if (isGroupFieldStart) {
                        var joinString = e.target.getAttribute("data-join-string");
                        $("#radom-group-fields-is-edit").val(1);
                        $("#radom-group-fields-is-edit").attr("data-join-string", joinString);
                        showModal($("#groupFieldsWindow"));
                    }
                }
            });
        }

        var initEditorsInterval = setInterval(function() {
            if (windowLoaded) {
                templateEditor = tinymce.EditorManager.get("field-content");
                initTemplateEditor(templateEditor);
                documentNameEditor = tinymce.EditorManager.get("field-documentName");
                initTemplateEditor(documentNameEditor);
                documentShortNameEditor = tinymce.EditorManager.get("field-documentShortName");
                initTemplateEditor(documentShortNameEditor);

                // Устанавливаем подписантов документа
                var participantsForInput = {};
                var parentParticipantsForInput = {};
                for (var index in CLASS_DOCUMENT_PARTICIPANTS) {
                    var participant = CLASS_DOCUMENT_PARTICIPANTS[index];
                    var associationFormId = participant.associationForm != null ? participant.associationForm.id : -1;
                    switch (participant.participantType) {
                        case "INDIVIDUAL":
                        case "INDIVIDUAL_LIST":
                        case "REGISTRATOR":
                            participantsForInput[participant.participantName + index] = {
                                participantName: participant.participantName,
                                participantVisibleName: participant.participantName
                            };
                            break;
                        case "COMMUNITY_WITH_ORGANIZATION":
                        case "COMMUNITY_WITH_ORGANIZATION_LIST":
                            var childrenParticipants = getParticipantsOfCommunity(FILTERS_COMMUNITY_WITH_ORGANIZATION, associationFormId);
                            for (var childInternalName in childrenParticipants) {
                                var childName = childrenParticipants[childInternalName];
                                participantsForInput[childName + index] = {
                                    participantVisibleName: participant.participantName + ": " + childName,
                                    participantName: childName
                                };
                                parentParticipantsForInput[childName + index] = {participantName: participant.participantName};
                            }
                            break;
                        case "COMMUNITY_WITHOUT_ORGANIZATION":
                            participantsForInput[participant.participantName + index] = {participantName: participant.participantName};
                            break;
                        case "COMMUNITY_IP":
                            break;
                    }
                }
                var temlateContent = tinymce.activeEditor.getContent();
                var participantIndex = 0;
                var participantCount = 0;
                for (var index in TEMPLATE_PARTICIPANTS) {
                    participantCount++;
                }
                for (var participantKey in participantsForInput) {
                    var participantObj = participantsForInput[participantKey];
                    var participantName = participantObj.participantName;
                    var participantVisibleName = participantObj.participantVisibleName;
                    var parentParticipantName = parentParticipantsForInput[participantKey] == null ? "" : parentParticipantsForInput[participantKey].participantName;

                    var checked = false;
                    if (participantCount > 0) {
                        if (TEMPLATE_PARTICIPANTS[parentParticipantName + participantName] != null) {
                            checked = true;
                        }
                    } else { // Если нет сохранённых участников, то анализируем шаблон на наличие полей участников
                        var position = temlateContent.search(new RegExp("(" + participantName + ":)", 'gi'));
                        if (position > -1) {
                            checked = true;
                        }
                    }
                    var signParticipantId = hashCode(parentParticipantName + participantName);

                    var participantMarkup = $(Mustache.render(
                            participantContainerTemplate,
                            {
                                parentParticipantName: parentParticipantName,
                                signParticipantId: signParticipantId,
                                participantName: participantName,
                                checked: checked,
                                participantVisibleName: participantVisibleName
                            }
                    ));
                    $("#signDocumentParticipants").append(participantMarkup);
                    //$("#" + signParticipantId).prop("checked", checked);
                    participantIndex++;
                }
                clearInterval(initEditorsInterval);
            }
        }, 100);

        // Выбор класса документов
        $("#chooseClassDocument").click(function(){
            viewParentClassTreePanel($("#field-documentClassId").val());
            return false;
        });
        $("#setClassDocument").click(function(){
            //$("#editDocumentParentTypeWindow").modal("hide");
            hideModal($("#editDocumentParentTypeWindow"));
        });

        // Вызов модального окна для проверки шаблона
        // TODO
        /*$("#testTemplate").click(function(){
            $("#documentContent").hide();
            TemplateController.loadTemplateById(${documentTemplateForm.id});
            $("#testTemplateWindow").modal("show");
        });*/
        initPreviewTemplate(templateId);
    };

    // Заполнить комбобокс полями участника шаблона документа
    function fillComboBoxByParticipantType(participantType, associationForm, comboBoxId) {
        if (participantType == "INDIVIDUAL") {
            fillFiltersComboBox(comboBoxId, FILTERS_INDIVIDUAL, associationForm);
        } else if (participantType == "INDIVIDUAL_LIST") {
            fillFiltersComboBox(comboBoxId, FILTERS_INDIVIDUAL_LIST, associationForm);
        } else if (participantType == "REGISTRATOR") {
            fillFiltersComboBox(comboBoxId, FILTERS_REGISTRATOR, associationForm);
        } else if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
            fillFiltersComboBox(comboBoxId, FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm);
        } else if (participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
            fillFiltersComboBox(comboBoxId, FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST, associationForm);
        } else if (participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
            fillFiltersComboBox(comboBoxId, FILTERS_COMMUNITY_WITHOUT_ORGANIZATION, associationForm);
        } else if (participantType == "COMMUNITY_IP") {
            fillFiltersComboBox(comboBoxId, FILTERS_COMMUNITY_IP, associationForm);
        }
        var jqSelect = $("#" + comboBoxId);
        jqSelect.removeAttr("disabled");
        jqSelect.attr("title", "Нажмите здесь и выберите нужный фильтр чтобы добавить его");
        jqSelect.selectpicker("refresh");
        jqSelect.selectpicker("val", null);
    }

    // Установить значения выбранного родительского класса документов
    function setParentClassDocument(id, name, pathName){
        $("#field-documentClassId").val(id);
        $("#documentClassName").val(name);
        $("#documentClassName").attr("title", pathName);
    }

    function isNumber(value) {
        var nmb = Number(value);
        return nmb % 1 === 0 || nmb % 1 > 0;
    }

    function isInt(value) {
        var nmb = Number(value);
        return nmb % 1 === 0;
    }

    function getFiltersFields(participantType) {
        $.ajax({
            url: "/admin/flowOfDocuments/documentType/filters.json?participantType=" + participantType,
            type: "post",
            data: "{}",
            datatype: "json",
            success: function (response) {
                if (response.result != "error") {
                    if (participantType == "INDIVIDUAL") {
                        FILTERS_INDIVIDUAL = response;
                    } else if (participantType == "INDIVIDUAL_LIST") {
                        FILTERS_INDIVIDUAL_LIST = response;
                    } else if (participantType == "REGISTRATOR") {
                        FILTERS_REGISTRATOR = response;
                    } else if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                        FILTERS_COMMUNITY_WITH_ORGANIZATION = response;
                    } else if (participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                        FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST = response;
                    } else if (participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
                        FILTERS_COMMUNITY_WITHOUT_ORGANIZATION = response;
                    } else if (participantType == "COMMUNITY_IP") {
                        FILTERS_COMMUNITY_IP = response;
                    }
                }
            }
        });
    }

    function getParticipantsFilteredFields() {
        $.ajax({
            url: "/admin/flowOfDocuments/participantsFilteredFields.json",
            type: "post",
            data: {templateId : templateId},
            datatype: "json",
            success: function (response) {
                if (response.errorMessage != null) {
                    bootbox.alert("При загрузке полей источников данных класса документа произошла ошибка! " + response.errorMessage);
                } else {
                    PAPRTICIPANTS_FILTERED_FIELDS = response;
                }
            }
        });
    };

    function getSystemFields() {
        $.ajax({
            url: "/admin/flowOfDocuments/documentType/systemFields.json",
            type: "post",
            data: {},
            datatype: "json",
            success: function (response) {
                if (response.errorMessage != null) {
                    bootbox.alert("При загрузке системных полей произошла ошибка! " + response.errorMessage);
                } else {
                    SYSTEM_FIELDS = response;
                }
            }
        });
    }

    // Загружаем участников шаблона документа

    function getParticipants(callBack) {
        $.ajax({
            url: "/admin/flowOfDocuments/documentType/participants.json?id=" + documentClassId,
            type: "post",
            data: "{}",
            datatype: "json",
            success: function (response) {
                if (response.message != null) {
                    bootbox.alert("При загрузке источников данных шаблона документа произошла ошибка! " + response.message);
                } else {
                    CLASS_DOCUMENT_PARTICIPANTS = response;
                    callBack();
                }
            }
        });
    }

    //заполнить ComboBox списком с участниками
    function fillParticipantsComboBox(jqControl) {
        var participants = CLASS_DOCUMENT_PARTICIPANTS;

        jqControl.html("");
        jqControl.append("<optgroup label='Физ. лица'></optgroup>");
        jqControl.append("<optgroup label='Список физ. лиц'></optgroup>");
        jqControl.append("<optgroup label='Регистраторы'></optgroup>");
        jqControl.append("<optgroup label='Объединения в рамках юр. лица'></optgroup>");
        jqControl.append("<optgroup label='Список юр. лиц'></optgroup>");
        jqControl.append("<optgroup label='Объединения вне рамок юр. лица'></optgroup>");
        jqControl.append("<optgroup label='Объединения ИП'></optgroup>");
        participants.forEach(function (entry) {
            var groupName = "";
            if (entry.participantType == "INDIVIDUAL") {
                groupName = "Физ. лица";
            } else if (entry.participantType == "INDIVIDUAL_LIST") {
                groupName = "Список физ. лиц";
            } else if (entry.participantType == "REGISTRATOR") {
                groupName = "Регистраторы";
            } else if (entry.participantType == "COMMUNITY_WITH_ORGANIZATION") {
                groupName = "Объединения в рамках юр. лица";
            } else if (entry.participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                groupName = "Список юр. лиц";
            } else if (entry.participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
                groupName = "Объединения вне рамок юр. лица";
            } else if (entry.participantType == "COMMUNITY_IP") {
                groupName = "Объединения ИП";
            }
            $("optgroup[label='" + groupName + "']", jqControl).append("<option value='" + entry.id + "' data-object-id='" + entry.id + "' data-participant-type='" + entry.participantType + "' data-association-form='" + entry.associationForm.id + "'>" + entry.participantName + "</option>");
        });
        jqControl.selectpicker("refresh");
        jqControl.selectpicker("val", null);
    }

    //заполнить ComboBox списком с фильтрами
    function fillFiltersComboBox(control, filters, associationForm, participantFilteredFields) {
        $("#" + control).html("");
        filters.forEach(function (entry) {
            var disabled = "";
            if (entry.associationForms.length > 0 && $.inArray(associationForm, entry.associationForms) < 0) {
                disabled = "disabled='disabled' ";
            }
            /*if (entry.rameraListEditorItem != associationForm && entry.rameraListEditorItem != -1) {
                disabled = "disabled='disabled' ";
            }*/
            $("#" + control).append("<optgroup " + disabled + "label='" + entry.group + "' data-association-form='" + associationForm + "'></optgroup>");
            entry.filters.forEach(function (filter) {
                // Фильтруем поля, которые нужно отобразить
                var needShowField = true;
                if (participantFilteredFields != null && participantFilteredFields.length > 0) {
                    needShowField = false;
                    for (var index in participantFilteredFields) {
                        var participantFilteredField = participantFilteredFields[index];
                        if (filter.internalName == participantFilteredField.internalName) {
                            needShowField = true;
                            break;
                        }
                    }
                }

                // Если поле не ИД
                if (filter.internalName.substring(filter.internalName.length - "_ID".length, filter.internalName.length) != "_ID" && needShowField) {
                    $("#" + control + " optgroup[label='" + entry.group + "']").append("<option data-object-id='" + filter.id + "' data-object-internal-name='" + filter.internalName + "' data-object-use-case='" + filter.useCase + "' data-object-is-meta-field='" + filter.isMetaField + "' data-object-type='" + filter.type + "'>" + filter.name + "</option>");
                }
            });
        });
    }

    // Заполнитб комбобокс системынми полями
    function fillSystemFieldsComboBox(control, fieldsGroupsParam) {
        $("#" + control).html("");
        var fieldsGroups = [];
        if ($.isArray(fieldsGroupsParam)) {
            fieldsGroups = fieldsGroupsParam;
        } else {
            fieldsGroups = JSON.parse(fieldsGroupsParam);
        }
        fieldsGroups.forEach(function (fieldsGroup) {
            $("#" + control).append("<optgroup label='" + fieldsGroup.name + "'></optgroup>");
            fieldsGroup.fields.forEach(function (field) {
                $("#" + control + " optgroup[label='" + fieldsGroup.name + "']").append("<option data-object-id='" + field.id + "' data-object-internal-name='" + field.internalName + "'>" + field.name + "</option>");
            });
        });
    }

    // Получить список участников объединения
    function getParticipantsOfCommunity(filters, associationForm) {
        var result = {};
        for (var index in filters) {
            var entry = filters[index];
            var fields = entry.filters;
            for (var fieldIndex in fields) {
                var field = fields[fieldIndex];
                if (field != null && field.type == 'SHARER' || field.type == 'PARTICIPANTS_LIST') {
                    //if (entry.rameraListEditorItem != associationForm && entry.rameraListEditorItem != -1) {
                    if (entry.associationForms.length > 0 && $.inArray(associationForm, entry.associationForms) < 0) {
                        continue;
                    }
                    result[field.internalName] = field.name;
                }
            }
        }
        return result;
    }

    // Загрузить в селект данные по полям типа SHARER
    function fillCommunityGroups(selectId, filters, associationForm){
        $("#" + selectId).empty();
        $("#" + selectId).append("<option value=''>Поля объединения</option>");
        $("#" + selectId).append("<option value='MEMBERS'>Все участники объединения</option>");
        $("#" + selectId).append("<option value='CREATOR'>Создатель объединения</option>");
        var participants = getParticipantsOfCommunity(filters, associationForm);
        for (var internalName in participants) {
            var name = participants[internalName];
            $("#" + selectId).append("<option value='" + internalName + "'>" + name + "</option>");
        }
    }
</script>
<div>
    <h2 style="display: inline-block; vertical-align: top;">Создание шаблона документа</h2>
    <button type="button" class="btn btn-primary" id="previewTemplate" style="display: inline-block; margin-top: 22px;">Предпросмотр документа</button>
</div>
<hr/>
<div id="documentTemplateBlock"></div>

<!-- Modal окно для плагина TinyMCE "Добавить [Участник:Поле]" -->
<div class="modal fade" role="dialog" id="radomParticipantFilterWindow" aria-labelledby="radomParticipantFilterLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="radomParticipantFilterLabel">Добавить</h4>
            </div>
            <div class="modal-body">
                <input id="radom-participant-filter-is-edit" type="text" value="0" style="display:none">
                <div class="form-group">
                    <label>Источник данных</label>
                    <select id="participant-combobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                </div>
                <div class="form-group" id="community-with-organization-form-groups" style="display: none">
                    <select class="form-control" id="community-with-organization-form-select"></select>
                </div>
                <div class="form-group">
                    <label>Поле</label>
                    <select id="filters-combobox" class="selectpicker" data-live-search="true" data-width="100%" data-hide-disabled="true"></select>
                </div>
                <div class="form-group" id="custom-attribute-filter-group">
                    <label for="custom-attribute-filter-cases" id="custom-attribute-filter-cases-lbl">Падеж</label>
                    <select class="form-control" id="custom-attribute-filter-cases">
                        <option value="CASE_I" data-text="Именительный">Именительный (Кто? Что? - Семен Семенович)</option>
                        <option value="CASE_R" data-text="Родительный">Родительный (Кого? Чего? - Семена Семеновича)</option>
                        <option value="CASE_D" data-text="Дательный">Дательный (Кому? Чему? - Семену Семеновичу)</option>
                        <option value="CASE_V" data-text="Винительный">Винительный (Кого? Что? - Семена Семеновича)</option>
                        <option value="CASE_T" data-text="Творительный">Творительный (Кем? Чем? - Семеным Семеновичом)</option>
                        <option value="CASE_P" data-text="Предложный">Предложный (О ком? О чём? В ком? В чём? - Семене Семеновиче)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="custom-attribute-chars-type">Регистр</label>
                    <select class="form-control" id="custom-attribute-chars-type">
                        <option value="NORMAL" data-text="Именительный">Без изменения регистра</option>
                        <option value="UPPERCASE" data-text="Родительный">Верхний регистр</option>
                        <option value="LOWERCASE" data-text="Дательный">Нижний регистр</option>
                    </select>
                </div>
                <div class="form-group" id="field-date-attributes" style="display: none">
                    <label>
                        Дата прописью
                        <input type="checkbox" id="data-object-date-is-word" />
                    </label>
                </div>
                <div class="form-group" id="field-number-attributes" style="display: none">
                    <label>
                        Число прописью
                        <input type="checkbox" id="data-object-number-is-word" />
                    </label>
                </div>
                <div class="form-group" id="field-currency-attributes" style="display: none">
                    <label>
                        Сумма прописью
                        <input type="checkbox" id="data-object-currency-is-word" />
                    </label>
                    <div id='currencyTypeControl' rameraListEditorName='currency_types'></div>
                </div>
                <div id="field-system-image-attributes" style="display: none">
                    <div class="form-group">
                        <label>Размер картинки</label>
                        <input type="text" class="form-control" id="system-image-size" />
                    </div>
                    <div class="form-group">
                        <label>Расположение картинки относительно текста</label>
                        <select class="form-control" id="system-image-float" >
                            <option value="">Как есть</option>
                            <option value="left">Слева</option>
                            <option value="right">Справа</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="radomParticipantFilter-button" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Модальное окно для добавления текста с указанием пола-->
<div class="modal fade" role="dialog" id="radomParticipantCustomTextWindow" aria-labelledby="radomParticipantCustomTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="radomParticipantCustomTextLabel">Добавить</h4>
            </div>
            <div class="modal-body">
                <input id="participant-custom-text-is-edit" type="text" value="0" style="display:none">
                <div class="form-group">
                    <label>Источник данных</label>
                    <select id="participant-custom-text-combobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                </div>
                <div class="form-group">
                    <label>Мужской вариант</label>
                    <div class="input-group">
                        <input id="male-custom-text" type="text" autocomplete="off" class="form-control"/>
                        <span class="input-group-btn">
                             <button id="male-custom-text-button" class="btn btn-default" type="button">Скопировать из Ж.</button>
                        </span>
                    </div>
                </div>
                <div class="form-group">
                    <label>Женский вариант</label>
                    <div class="input-group">
                        <input id="female-custom-text" type="text" autocomplete="off" class="form-control"/>
                        <span class="input-group-btn">
                             <button id="female-custom-text-button" class="btn btn-default" type="button">Скопировать из М.</button>
                        </span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="radomParticipantCustomText-button" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Модальное окно для добавления системного поля-->
<div class="modal fade" role="dialog" id="radomSystemFieldsWindow" aria-labelledby="radomSystemFieldsTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="radomSystemFieldsTextLabel">Добавить</h4>
            </div>
            <div class="modal-body">
                <input id="radom-system-fields-is-edit" type="text" value="0" style="display:none">
                <div class="form-group">
                    <label>Поле</label>
                    <select id="radomSystemFieldsСombobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                </div>
                <div class="form-group" id="system-field-date-attributes" style="display: none">
                    <label>
                        Дата прописью
                        <input type="checkbox" id="data-object-system-date-is-word" />
                    </label>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="radomSystemFieldsWindowButton" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Модальное окно для проверки шаблона документа-->
<%@include file="templatePreview.jsp" %>

<!-- Модальное окно для добавления начала группы полей-->
<div class="modal fade" role="dialog" id="groupFieldsWindow" aria-labelledby="groupFieldsTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="groupFieldsTextLabel">Добавить</h4>
            </div>
            <div class="modal-body">
                <input id="radom-group-fields-is-edit" type="text" value="0" style="display:none">
                <div class="form-group">
                    <label>Строка между группами полей</label>
                    <input type="text" class="form-control" id="groupFieldsJoinString" value=", " />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="groupFieldsWindowButton" style="float: left;">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script id="documentTemplatePageTemplate" type="x-tmpl-mustache">
    <input id="field-documentTemplateId" type="hidden" value="{{documentTemplate.id}}" />
    <input id="field-documentClassId" type="hidden" value="{{documentTemplate.documentClassId}}"/>

    <div class="form-group" style="position: relative; height: 55px;">
        <label>Класс документов</label>
        <div style="position: absolute; left: 0px; right: 110px; top: 20px;">
            <input class="form-control" id="documentClassName" title="{{documentTemplate.classDocumentPath}}" value="{{documentTemplate.documentClassName}}" disabled="disabled"/>
        </div>
        <div style="position: absolute; right: 0px; width: 100px; top: 20px; padding-top: 6px;">
            <a href="#" id="chooseClassDocument">Выбрать класс</a>
        </div>
    </div>
    <div class="form-group">
        <label id="field-code-lbl">Ссылка на страницу со справкой по данному шаблону</label>
        <input type="text" class="form-control" id="field-helpLink" value="{{documentTemplate.helpLink}}"/>
        {{#hasHelpLink}}
            <div><a href="{{documentTemplate.helpLink}}">Перейти на страницу справки</a></div>
        {{/hasHelpLink}}
        </div>
        <div class="form-group">
            <label id="field-code-lbl">Код шаблона</label>
            <input type="text" class="form-control" id="field-code" value="{{documentTemplate.code}}"/>
        </div>
        <div class="form-group">
            <label type="text" id="field-name-lbl">Наименование шаблона</label>
            <input type="text" class="form-control" id="field-name" value="{{documentTemplate.name}}"/>
        </div>
        <div class="form-group">
            <label>Сокращённое название итогового документа</label>
            <textarea id="field-documentShortName">{{documentTemplate.documentShortName}}</textarea>
        </div>
        <div class="form-group">
            <label>Полное название итогового документа</label>
            <textarea id="field-documentName">{{documentTemplate.documentName}}</textarea>
        </div>
        <div class="form-group">
            <label>Шаблон документа</label>
            <textarea id="field-content">{{documentTemplate.content}}</textarea>
        </div>
        <div class="form-group">
            <label>Документ подписывают:</label>
            <div id="signDocumentParticipants"></div>
        </div>
        <div class="form-group">
            <label>Параметры выгрузки в ПДФ файл</label>
            <input type="text" class="form-control" id="pdfExportArguments" value="{{documentTemplate.pdfExportArguments}}"/>
        </div>
        <button class="btn btn-primary" id="save-documentTemplate-button" style="float: left;">Сохранить</button>
        {{#hasTemplateId}}
            <button class="btn btn-danger" id="delete-documentTemplate-button" style="margin-left:10px;float: left;">Удалить</button>
        {{/hasTemplateId}}
        <a href="/admin/flowOfDocuments/documentTemplates" class="btn btn-default" style="margin-left:10px;float: right;">Перейти к шаблонам документа</a>
        <!--a href="#" class="btn btn-primary" id="edit-filters-documentTemplate-button" style="float: right;">Редактировать фильтры</a-->
        <div class="form-group">
            <br><br><h6>Создатель шаблона документа: {{documentTemplate.creatorFullName}}</h6>
        </div>
    </div>
    <!-- Modal -->
    <div class="modal fade" role="dialog" id="editFiltersWindow" aria-labelledby="filtersLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="filtersLabel">Редактировать фильтры</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label>Источник данных</label>
                        <select id="document-template-participants-combobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                    </div>
                    <div class="form-group">
                        <label>Добавить новый фильтр</label>
                        <select id="document-template-filters-combobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                    </div>
                    <%--<div class="form-group" id="form-group-filters">
                        {{#documentTemplate.filters}}
                            <div style="margin-bottom: 5px" name="form-group-document-template-filter">
                                <input name="filters[{{@index}}].id" type="text" value="{{id}}" style="display:none" />
                                <input name="filters[{{@index}}].participant.id" type="text" value="{{participant.id}}" type="hidden" />
                                <input name="filters[{{@index}}].filterField.id" type="text" value="{{filterField.id}}" type="hidden" />
                                <div class="input-group input-group-sm">
                                    <span class="input-group-addon">{{participant.participantName}}</span>
                                    <span class="input-group-addon">{{filterField.name}}</span>
                                    <input type="text" class="form-control" name="filters[{{@index}}].value" value="{{value}}">
                                    <span class="input-group-btn">
                                    <a href="#" name="delete-document-template-filter" class="btn btn-danger btn-sm"><i class="glyphicon glyphicon-remove"></i></a>
                                    </span>
                                </div>
                            </div>
                        {{#documentTemplate.filters}}
                    </div>--%>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
</script>

<script id="participantContainerTemplate" type="x-tmpl-mustache">
    <div class='participantContainer'>
        <input type='checkbox' style='display: none' name='templateParticipants' class='parentTemplateParticipants' value='{{parentParticipantName}}' />
        <label>
            <input id='{{signParticipantId}}' class='templateParticipants' type='checkbox' name='templateParticipants' value='{{participantName}}' {{#checked}}checked="checked"{{/checked}} />
            {{participantVisibleName}}
        </label>
    </div>
</script>