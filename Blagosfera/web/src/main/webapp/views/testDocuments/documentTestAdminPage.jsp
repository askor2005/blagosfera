<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<style>
    #templatesRootNode {/*border: solid 1px #ddd; padding: 5px;*/}
    #templatesRootNode #templatesNode {display: inline-block; vertical-align: top; border: solid 1px #ddd; padding: 5px; width: 100%;}
    #templatesRootNode #templatesNode select {width: 100%;}
    #templatesRootNode #selectedTemplateNode {display: inline-block; vertical-align: top; border: solid 1px #ddd; border-left: none; padding: 5px; visibility: hidden; }
    #templatesRootNode #createDocumentButton {visibility: hidden; margin-top: 3px;}

    #templatesRootNode #selectedTemplateNode .participantsNode {padding-bottom: 3px; padding-top: 3px; border-bottom: solid 1px #ddd;}
    #templatesRootNode #selectedTemplateNode .typeParticipantName {display: inline-block; vertical-align: top; width: 287px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;}
    #templatesRootNode #selectedTemplateNode .possibleParticipants {display: inline-block; vertical-align: top;}
    #templatesRootNode #selectedTemplateNode .possibleParticipants select {width: 433px;}

    .templateSelect {width : 200px;}

    #templateUserFieldsNode {
        border-top: 1px solid #ccc;
        margin-top: 5px;
        padding-top: 5px;
    }
</style>
<script type="text/javascript">
    // Загрузить пользовательские поля по шаблону
    function loadUserFields(templateId, callBack) {
        $.radomJsonPost("/admin/docs/getUserFieldsByTemplate.json",
                {
                    template_id : templateId
                },
                callBack
        );
    }

    // Обёртка для отправки данных при сохранении документа.
    var DocumentParameters = function(){
        this.content = "";
        this.name = "";
        this.shortName = "";
        this.templateId = 0;
        this.participantParameters = [];
    };
    // Обёртка для отправки данных при работе с шаблонами.
    var TemplateParameters = function(){
        this.templateId = 0;
        this.participantParameters = [];
    };
    // Обёртка для отправки данных участников документа при операциях над шаблонами и документами.
    var ParticipantParameter = function(){
        this.participantTypeName = ""; // Тип участника документа в системе.
        this.participantTypeTemplateName = ""; // Наименование типа участниа шаблона документа.
        this.participantId = 0; // ИД участника документа в системе.
        this.participantIds = {}; // Список ИД участников (для участника документа с типом - список участников)
    };

    // Обёртка данных любой операции ajax
    OperationResult = {
        operationResult : false,
        operationMessage : "",
        data : {}
    };

    // Загрузчик данных
    DocumentDataLoader = {
        BASE_URL : "/admin/docs",
        requestData : function(url, data, onSuccessCallBack, onErrorCallBack) {
            var self = this;
            $.ajax({
                async: true,
                type: "POST",
                url: url,
                datatype: "json",
                //contentType: 'application/json; charset=utf-8',
                //data: {rameraListEditorData: JSON.stringify(action)},
                data : data,
                success: function (param) {
                    if (!param.operationResult) {
                        if (!onErrorCallBack) {
                            alert(param.operationMessage);
                        } else {
                            onErrorCallBack(param);
                        }
                    } else {
                        onSuccessCallBack(param.data, param.operationMessage);
                    }
                },
                error: function (param) {
                }
            });
        },
        // Загрузить все шаблоны
        loadTemplates : function(callBack) {
            var url = this.BASE_URL + "/process/getTemplates.json";
            var data = {};
            // Ответ от сервера:
            //id;
            //name;
            handleCallBack = function(data) {
                for (var index in data) {
                    var template = data[index];
                    if (template.name == null || template.name == "") {
                        template.name = "[Без имени]";
                    }
                }
                callBack(data);
            };
            this.requestData(url, data, handleCallBack);
        },
        // Загрузить шаблон по ИД
        getTemplateById : function(templateId, callBack) {
            var url = this.BASE_URL + "/process/getTemplate.json";
            var data = {template_id : templateId};

            // Оптимизация количества передаваемых данных с сервера
            var handleCallBack = function(template, message) {

                var possibleParticipantsByType = {};
                for (var index in template.flowOfDocumentTypeParticipants) {
                    var typeParticipant = template.flowOfDocumentTypeParticipants[index];
                    if (typeParticipant.documentParticipantEntities != null && typeParticipant.documentParticipantEntities.length > 0) {
                        possibleParticipantsByType[typeParticipant.type] = typeParticipant.documentParticipantEntities;
                    }
                }
                for (var index in template.flowOfDocumentTypeParticipants) {
                    var typeParticipant = template.flowOfDocumentTypeParticipants[index];
                    if (typeParticipant.documentParticipantEntities == null || typeParticipant.documentParticipantEntities.length == 0) {
                        typeParticipant.documentParticipantEntities = possibleParticipantsByType[typeParticipant.type];
                    }
                }

                callBack(template, message);
            };

            this.requestData(url, data, handleCallBack);
        },
        // Загрузить контент документа на основе выбранных параметров
        createDocumentContent : function(templateParameters, fieldsList, callBack) {
            //templateParameters - объект класса TemplateParameters
            var url = this.BASE_URL + "/process/createDocumentContent.json";
            var data = {parameters : JSON.stringify(templateParameters), fields_list : JSON.stringify(fieldsList)};
            this.requestData(url, data, callBack);
        },
        // Удалить документ
        deleteDocument : function(documentId, callBack){
            var url = this.BASE_URL + "/process/deleteDocument.json";
            var data = {document_id : documentId};
            this.requestData(url, data, callBack);
        },
        // Получить документ по ИД
        getDocument : function(documentId, callBack){
            var url = this.BASE_URL + "/process/getDocument.json";
            var data = {document_id : documentId};
            this.requestData(url, data, callBack);
        }
    };

    // События шаблонов.
    TemplateControllerEvents = {
        DOCUMENT_CREATED : "documentCreated",
        DOCUMENT_CREATING_IN_PROCESS : "documentCreatingInProcess",
        TEMPLATE_LOADING_IN_PROCESS : "templateLoadingInProcess",
        DOCUMENT_SAVED : "documentSaved"
    };

    TemplateController = {

        // Обработчик событий
        callBack : function(){},

        // Параметры текущего шаблона.
        currentTemplateParameters : {},

        // Основаная нода с шаблонами
        domNode : $(
                "<div id='templatesRootNode'>" +
                    "<h4 id='templatesHeader'>Шаблоны</h4>" +
                    "<div id='templatesNode'></div>" +
                    "<div id='templateUserFieldsNode'></div>" +
                    "<div id='selectedTemplateNode'></div>" +
                    "<a class='btn btn-primary' id='createDocumentButton'>Сформировать документ</a>" +
                "</div>"),

        // Нода для документа
        documentDomNode : $(
                "<div>"+
                    "<div id='documentStatus' style='display: none;'></div>"+
                    "<div id='documentContent' style='display: none;'>"+
                        "<div class='form-group'>"+
                            "<label for='documentShortName' id='field-name-lbl'>Сокращённое наименование документа</label>"+
                            "<input id='documentShortName' name='documentShortName' class='form-control' type='text' />"+
                        "</div>"+
                        "<div class='form-group'>"+
                            "<label for='documentName' id='field-name-lbl'>Полное наименование документа</label>"+
                            "<input id='documentName' name='documentName' class='form-control' type='text' />"+
                        "</div>"+
                        "<div class='form-group'>"+
                            "<label>Документ</label>"+
                            "<textarea id='parsedDocumentContent'></textarea>"+
                        "</div>"+
                    "</div>"+
                "</div>"),

        // Шаблон типа участника шаблона
        participantTypeTemplate :
                "<div class='participantsNode'>" +
                    "<span class='typeParticipantName'></span>" +
                    "<span class='possibleParticipants'></span>" +
                "</div>",

        // Отрисовка параметров выбранного шаблона.
        drawSelectedTemplate : function(template) {
            var self = this;
            var result = $("<div></div>");
            for (var index in template.flowOfDocumentTypeParticipants) {
                var typeParticipant = template.flowOfDocumentTypeParticipants[index];

                var typeParticipantNode = $(self.participantTypeTemplate);
                $(".typeParticipantName", typeParticipantNode).attr("title", typeParticipant.name);
                $(".typeParticipantName", typeParticipantNode).text(typeParticipant.name);

                var possibleParticipants = typeParticipant.documentParticipantEntities == null ? [] : typeParticipant.documentParticipantEntities;
                var jqSelect = $("<select index=" + index + " participant_type='" + typeParticipant.type +"' type_participant_select_id='" + typeParticipant.id + "' participant_type_name='" + typeParticipant.name + "'></select>");

                // Сортируем участников по имени
                possibleParticipants.sort(function(a,b){
                    return a.name > b.name ? 1 : -1;
                });

                for (var participantIndex in possibleParticipants) {
                    var participant = possibleParticipants[participantIndex];
                    var optionHtml = "<option value='" + participant.sourceParticipantId + "'>" + participant.name + "</option>";
                    jqSelect.append(optionHtml);
                }
                $(".possibleParticipants", typeParticipantNode).append(jqSelect);
                result.append(typeParticipantNode);

                // Устанавливаем тип участника и участника шаблона и документа в параметры
                var selectedOption = $("option:selected", jqSelect);
                var participantId = selectedOption.val();

                var participantParameter = new ParticipantParameter();
                participantParameter.participantTypeName = typeParticipant.type; // Тип участника документа в системе.
                participantParameter.participantTypeTemplateName = typeParticipant.name; // Наименование типа участниа шаблона документа.
                participantParameter.participantId = participantId;

                self.currentTemplateParameters.participantParameters.push(participantParameter);

                this.initChangeParticipant(jqSelect);

                if (typeParticipant.type == "INDIVIDUAL_LIST" || typeParticipant.type == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                    $(".possibleParticipants", typeParticipantNode).append("<button type='button' class='btn btn-primary addParticipantToList' style='margin-left: 5px;'>Добавить участника</button>");
                    // Добавление участников в список физ лиц
                    $(".addParticipantToList", typeParticipantNode).click(function(){
                        var newJqSelect = $($(this).prev().get(0).outerHTML);
                        newJqSelect.attr("index", newJqSelect.attr("index") + Math.random().toString().replace(".", ""));
                        $(this).parent().append(newJqSelect);
                        self.initChangeParticipant(newJqSelect);
                    });
                }
            }
            return result;
        },

        initChangeParticipant : function(jqSelect){
            var self = this;
            jqSelect.change(function(event) {
                var jqCurrentSelect = $(this);
                var selectedOption = $("option:selected", jqCurrentSelect);
                var participantTypeTemplateName = jqCurrentSelect.attr("participant_type_name");
                var participantType = jqCurrentSelect.attr("participant_type");
                var participantId = selectedOption.val();
                var index = jqCurrentSelect.attr("index");
                //var text = selectedOption.text();

                // Ищем параметр участника документа для обновления
                if (self.currentTemplateParameters.participantParameters != null && self.currentTemplateParameters.participantParameters.length > 0) {
                    for (var i in self.currentTemplateParameters.participantParameters) {
                        var participantParameter = self.currentTemplateParameters.participantParameters[i];
                        if (participantParameter.participantTypeTemplateName == participantTypeTemplateName) {
                            // Меняем выбранного ИД участника из системы
                            if (participantType == "INDIVIDUAL_LIST" || participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                                if (participantParameter.participantIds == null) {
                                    participantParameter.participantIds = {};
                                }
                                participantParameter.participantIds[index] = participantId;
                                //participantParameter.participantIds = participantId;
                            } else {
                                participantParameter.participantId = participantId;
                            }
                            break;
                        }
                    }
                }
            });
        },

        // Получить ноду с отрисованными шаблонами.
        drawTemplates : function(templates){
            var self = this;
            var jqSelectNode = $("<select class='templateSelect' size='10'></select>");
            for (var index in templates) {
                var template = templates[index];
                var optionHtml = "<option value='" + template.id + "' document_class_id='" + template.documentClassId + "'>" + template.name + "</option>";
                jqSelectNode.append(optionHtml);
            }
            jqSelectNode.change(function(event) {

                var selectedOption = $("option:selected", $(this));
                var templateId = selectedOption.val();
                var documentClassId = selectedOption.attr("document_class_id");

                //var text = selectedOption.text();
                $("#selectedTemplateNode", self.domNode).css("visibility", "visible");
                $("#createDocumentButton", self.domNode).css("visibility", "hidden");
                $("#selectedTemplateNode", self.domNode).text("Загрузка...");

                // Загрузка шаблона в процессе
                self.callBack(TemplateControllerEvents.TEMPLATE_LOADING_IN_PROCESS, {});
                $("#documentName", self.documentDomNode).val("");
                $("#documentShortName", self.documentDomNode).val("");
                $("#parsedDocumentContent", self.documentDomNode).val("");

                // Загружаем шаблон по ИД.
                self.loadTemplateById(templateId);
            });
            return jqSelectNode;
        },

        loadTemplateById : function(templateId) {
            var self = this;

            // Устанавиливаем параметры текущего шаблона
            self.currentTemplateParameters = {templateId : templateId, /*documentClassId : documentClassId, */ participantParameters : []};
            $("#templateUserFieldsNode").html("<p style='color: red;'>Загрузка пользовательских полей...</p>");
            // Загружаем шаблон по ИД.
            $("#createDocumentButton", self.domNode).hide();
            $("#selectedTemplateNode", self.domNode).html("<p style='color: red;'>Загрузка участников...</p>");
            DocumentDataLoader.getTemplateById(templateId, function(template) {
                $("#selectedTemplateNode", self.domNode).html("");
                $("#selectedTemplateNode", self.domNode).append(self.drawSelectedTemplate(template));
                $("#createDocumentButton", self.domNode).css("visibility", "visible");

                loadUserFields(templateId, function(userFields){
                    $("#createDocumentButton", self.domNode).show();
                    $("#templateUserFieldsNode").html("");
                    initUserFields(userFields, $("#templateUserFieldsNode"));
                });

                // Кнопка формирования документа.
                $("#createDocumentButton", self.domNode).unbind();
                $("#createDocumentButton", self.domNode).click(function() {

                    // Получаем пользовательские поля из формы
                    var userFields = getFieldsFromForm();

                    if (!$.isArray(userFields) && userFields == false) {
                        return;
                    }

                    // Создание документа в процессе
                    self.callBack(TemplateControllerEvents.DOCUMENT_CREATING_IN_PROCESS, {});
                    $("#documentName", self.documentDomNode).val("");
                    $("#documentShortName", self.documentDomNode).val("");
                    $("#parsedDocumentContent", self.documentDomNode).val("");
                    $("#documentStatus", self.documentDomNode).show();
                    $("#documentStatus", self.documentDomNode).text("Формирование документа...");

                    // Формирование документа
                    DocumentDataLoader.createDocumentContent(self.currentTemplateParameters, userFields, function(createDocumentContentResult){
                        self.callBack(TemplateControllerEvents.DOCUMENT_CREATED, {content : documentContent, name : template.name});
                        $("#documentStatus", self.documentDomNode).hide();
                        $("#documentContent", self.documentDomNode).show();

                        $("#documentName", self.documentDomNode).val(createDocumentContentResult.name);
                        $("#documentShortName", self.documentDomNode).val(createDocumentContentResult.shortName);
                        $("#parsedDocumentContent", self.documentDomNode).val(createDocumentContentResult.content);
                    });
                });
            });
        },

        initTemplates : function (jqParentNode, templateArray){
            var self = this;
            jqParentNode.append(self.domNode);
            jqParentNode.append(self.documentDomNode);
            $("#templatesNode", self.domNode).append(self.drawTemplates(templateArray));

            $("#parsedDocumentContent", self.documentDomNode).css("height", "500px").radomTinyMCE({});
        },

        // Инициализация компонента работы с шаблонами
        init : function(jqParentNode, callBack) {
            this.callBack = callBack;
            var self = this;
            if (window.needLoadTemplates == false) {
                self.initTemplates(jqParentNode, []);
            } else {
                DocumentDataLoader.loadTemplates(function (templateArray) {
                    self.initTemplates(jqParentNode, templateArray);
                });
            }
        }
    };

    $(document).ready(function () {
        // Грузим шаблоны
        TemplateController.init($("#templates"), function(event, data){
            switch (event) {
                // Загрузка шаблона выполняется...
                case TemplateControllerEvents.TEMPLATE_LOADING_IN_PROCESS:
                    //$("#documentName").val("");
                    //$("#parsedDocumentContent").val("");
                    break;
                // Загрузка документа в процессе
                case TemplateControllerEvents.DOCUMENT_CREATING_IN_PROCESS:
                    /*$("#documentName").val("");
                    $("#parsedDocumentContent").val("");
                    $("#documentStatus").show();
                    $("#documentStatus").text("Формирование документа...");*/
                    break;
                // Создан документ
                case TemplateControllerEvents.DOCUMENT_CREATED:
                    /*$("#documentStatus").hide();
                    $("#documentContent").show();
                    if (data != null && data != "") {
                        $("#documentName").val(data.name);
                        $("#parsedDocumentContent").val(data.content);
                    }*/
                    break;
                case TemplateControllerEvents.DOCUMENT_SAVED:
                    break;
            }
        });
    });
</script>
<div id="documentCreateHead">
    <h1>Создание документов</h1>
    <hr/>
</div>
<jsp:include page="../testDocuments/documentPageFieldList.jsp"/>
<div id="templates"></div>