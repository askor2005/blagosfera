(function(){

    var $editTemplateModal = null;

    var TABLE_TEMPLATE =
    "<table class='table table-hover table-striped'>" +
        "<thead>" +
            "<tr>" +
                "<th>Наименование шаблона</th>" +
                "<th>Редактировать</th>" +
                "<th>Удалить</th>" +
            "</tr>" +
        "</thead>" +
        "<tbody>" +
            "{{#templates}}" +
                "<tr index='{{index}}' template_id='{{templateId}}' setting_id='{{id}}'>" +
                    "<td>{{name}}</td>" +
                    "<td><button type='button' class='btn btn-primary editTemplateSetting' index='{{index}}'>Редактировать</button></td>" +
                    "<td><button type='button' class='btn btn-warning deleteTemplateSetting' index='{{index}}'>Удалить</button></td>" +
                "</tr>" +
            "{{/templates}}" +
        "</tbody>" +
    "</table>";

    var PAGE_TEMPLATE =
        "<div>" +
            "<div id='documentTemplateSettingsTable'></div>" +
            "<div>" +
                "<button type='button' class='btn btn-primary' id='addDocumentTemplate' style='margin-right: 10px;'>Добавить шаблон</button>" +
                "<button type='button' class='btn btn-primary' id='saveDocumentTemplates'>Сохранить</button>" +
            "</div>" +
        "</div>";

    var EDIT_TEMPLATE =
        "<div class='modal fade' role='dialog' id='editDocumentTemplateSettingsModal' aria-labelledby='editDocumentTemplateSettingsModalLabel' aria-hidden='true'>" +
            "<div class='modal-dialog modal-lg'>" +
                "<div class='modal-content'>" +
                    "<div class='modal-header'>" +
                        "<h4 class='modal-title' id='editTemplateHeader'></h4>" +
                    "</div>" +
                    "<div class='modal-body'>" +
                        "<div class='form-group'>" +
                            "<label>Название шаблона</label>" +
                            "<input type='text' class='form-control' id='documentTemplateName' template_id='{{templateId}}' setting_id='{{id}}' value='{{template.name}}' />" +
                        "</div>" +
                        "<div id='templateParticipants'></div>" +
                    "</div>" +
                    "<div class='modal-footer'>" +
                        "<button type='button' class='btn btn-primary' id='saveDocumentTemplateSetting'>Сохранить</button>" +
                        "<button type='button' class='btn btn-default' data-dismiss='modal'>Закрыть</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>";

    var TEMPLATE_PARTICIPANTS =
        "<div class='form-group'>" +
            "<label>Участники шаблона</label>" +
            "<table class='table table-hover table-striped'>" +
                "<thead>" +
                    "<tr>" +
                        "<th>Наименование участника</th>" +
                        "<th></th>" +
                        "<th></th>" +
                    "</tr>" +
                "</thead>" +
                "<tbody>" +
                    "{{#participants}}" +
                        "<tr participant_setting_id='{{id}}' participant_id='{{participantId}}'>" +
                            "<td>{{name}}</td>" +
                            "<td>" +
                                "<div><label><input {{#defaultType}}checked='checked'{{/defaultType}} participant_id='{{participantId}}' type='radio' name='sourceParticipant_{{participantId}}' class='selectParticipant' source_type='searchParticipant' /> Ручная установка участника</label></div>" +
                                "<div><label><input {{#customType}}checked='checked'{{/customType}} participant_id='{{participantId}}' type='radio' name='sourceParticipant_{{participantId}}' class='selectParticipant' source_type='insertParticipant' /> Автоматическая установка участника</label></div>" +
                            "</td>" +
                            "<td>" +
                                "<div class='searchParticipantBlock' participant_id='{{participantId}}' {{^defaultType}}style='display: none;'{{/defaultType}}>" +
                                    "<input type='text' class='form-control searchParticipant' source_id='{{sourceId}}' participant_id='{{participantId}}' value='{{participantSourceName}}' />" +
                                "</div>" +
                                "<div class='insertParticipantBlock' participant_id='{{participantId}}' {{^customType}}style='display: none;'{{/customType}}>" +
                                    "<select class='insertParticipant selectpicker' participant_id='{{participantId}}' data-live-search='true' data-hide-disabled='true' data-width='100%'>" +
                                        "<option value=''>-- Ничего не выбрано --</option>" +
                                        "{{#sourceNames}}" + //sourceName
                                            "<option {{#selected}}selected='selected'{{/selected}} value='{{sourceCode}}'>{{name}}</option>" +
                                        "{{/sourceNames}}" +
                                    "</select>" +
                                "</div>" +
                            "</td>" +
                        "</tr>" +
                    "{{/participants}}" +
                "</tbody>" +
            "</table>" +
        "</div>";

    var pageTemplate = null;
    var tableTemplate = null;
    var editTemplate = null;
    var templateParticipantsTemplate = null;

    var selectedTemplate = null;
    
    DocumentTemplateSettings = function(){

        function _drawDocumentTemplates(self, $drawNode, htmlTemplate, documentTemplates) {
            var markup = $(Mustache.render(htmlTemplate, {templates : documentTemplates}));
            $drawNode.empty();
            $drawNode.append(markup);
            _initEvents(self, $drawNode);
        }

        function _initSearchParticipants($input, participantType, searchFunc, onSelect) {
            $input.typeahead({
                triggerLength: 1,
                delay: 500,
                autoSelect: true,
                updater: function(item) {
                    onSelect(item);
                    return item;
                },
                source: function (query, process) {
                    searchFunc(query, participantType, function(response){
                        return process(response);
                    });
                }
            });
        }

        function _initSearchTemplate($input, onSelect) {
            $input.typeahead({
                triggerLength: 1,
                delay: 500,
                autoSelect: true,
                updater: function(item) {
                    onSelect(item);
                    return item;
                },
                source: function (query, process) {
                    var data = {
                        query : query
                    };
                    return $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "/flowOfDocuments/filterTemplates.json",
                        data: data,
                        success: function (data) {
                            return process(data);
                        },
                        error: function () {
                            console.log("ajax error");
                            return process(false);
                        }
                    });
                }
            });
        }

        function _initParticipant(self, participant) {
            var $input = $(".searchParticipant[participant_id=" + participant.participantId + "]");
            _initSearchParticipants($input, participant.type, self.findParticipantFunc, function(selectedParticipant){
                $input.attr("source_id", selectedParticipant.id);
            });
            $(".insertParticipant[participant_id=" + participant.participantId + "]").selectpicker();
        }

        function _initTemplate(self, $documentTemplateName, documentTemplate) {
            $documentTemplateName.val(documentTemplate.name);
            $documentTemplateName.attr("template_id", documentTemplate.templateId);
            $documentTemplateName.attr("setting_id", documentTemplate.id);

            if (documentTemplate.participants != null) {
                for (var index in documentTemplate.participants) {
                    var participant = documentTemplate.participants[index];
                    participant.sourceNames = self.getSourceNames(participant.type);
                    if (participant.sourceNames != null) {
                        participant.sourceNames = participant.sourceNames.slice(0);
                        for (var index in participant.sourceNames) {
                            participant.sourceNames[index] = jQuery.extend(true, {}, participant.sourceNames[index]);
                        }
                    }
                    console.log(participant.sourceNames);
                    participant.defaultType = participant.sourceType == "DEFAULT";
                    participant.customType = participant.sourceType == "CUSTOM";
                    if (participant.sourceNames != null) {
                        for (var index in participant.sourceNames) {
                            var sourceSystemParticipant = participant.sourceNames[index];
                            sourceSystemParticipant.selected =
                                (sourceSystemParticipant.sourceCode == participant.sourceName
                                && sourceSystemParticipant.sourceCode != null &&
                                participant.sourceName != null);
                        }
                    }
                }
            }

            $("#templateParticipants").empty();
            $("#templateParticipants").append($(Mustache.render(templateParticipantsTemplate, documentTemplate)));

            $(".selectParticipant", $editTemplateModal).off();
            $(".selectParticipant", $editTemplateModal).click(function(){
                var sourceType = $(this).attr("source_type");
                var participantId = $(this).attr("participant_id");
                if (sourceType == "searchParticipant") {
                    $(".searchParticipantBlock[participant_id=" + participantId + "]").show();
                    $(".insertParticipantBlock[participant_id=" + participantId + "]").hide();
                } else if (sourceType == "insertParticipant") {
                    $(".searchParticipantBlock[participant_id=" + participantId + "]").hide();
                    $(".insertParticipantBlock[participant_id=" + participantId + "]").show();
                }
            });

            if (documentTemplate.participants != null) {
                for (var index in documentTemplate.participants) {
                    var participant = documentTemplate.participants[index];
                    _initParticipant(self, participant);
                }
            }
        }

        function _addOrEditTemplateHandler(self, $this) {
            if ($this.attr("index") != null && $this.attr("index") != "") {
                var index = parseInt($this.attr("index"));
                selectedTemplate = self.documentTemplates[index];
            } else {
                selectedTemplate = {};
            }
            var $documentTemplateName = $("#documentTemplateName");

            _initTemplate(
                self,
                $documentTemplateName,
                selectedTemplate
            );

            _initSearchTemplate($documentTemplateName, function(template){
                var documentTemplate = {
                    id : null,
                    templateId : template.id,
                    name : template.name,
                    participants : null,
                    index : selectedTemplate.index
                };

                if (template.documentType != null && template.documentType.documentClassDataSources != null) {
                    var participants = [];
                    for (var index in template.documentType.documentClassDataSources) {
                        var documentClassDataSource = template.documentType.documentClassDataSources[index];
                        participants.push({
                            id : null,
                            participantId : documentClassDataSource.id,
                            name : documentClassDataSource.participantName,
                            type : documentClassDataSource.participantType,
                            sourceType : "DEFAULT",
                            sourceName : null,
                            sourceId : null,
                            participantSourceName : null,
                            sourceNames : self.getSourceNames(documentClassDataSource.participantType)
                        });
                    }
                    documentTemplate.participants = participants;
                }

                selectedTemplate = documentTemplate;
                _initTemplate(
                    self,
                    $documentTemplateName,
                    documentTemplate
                );
            });

            $editTemplateModal.modal("show");
        }

        function _initEvents(self, $drawNode) {
            $("#addDocumentTemplate").off();
            $("#addDocumentTemplate").click(function(){
                _addOrEditTemplateHandler(self, $(this));
            });

            $("#saveDocumentTemplates").off();
            $("#saveDocumentTemplates").click(function(){
                self.saveTemplatesFunc(self.documentTemplates);
            });

            $(".editTemplateSetting", $drawNode).off();
            $(".editTemplateSetting", $drawNode).click(function(){
                _addOrEditTemplateHandler(self, $(this));
            });

            $(".deleteTemplateSetting", $drawNode).off();
            $(".deleteTemplateSetting", $drawNode).click(function(){
                var index = parseInt($(this).attr("index"));
                removeItemFromArray(self.documentTemplates, "index", index);
                self.reInit();
            });

            $("#saveDocumentTemplateSetting").click(function(){

                var usedSourceNames = {};
                for (var participantType in self.sourceNames) {
                    var sourceNameSettings = self.sourceNames[participantType];
                    for (var index in sourceNameSettings) {
                        var sourceNameSetting = sourceNameSettings[index];
                        usedSourceNames[sourceNameSetting.sourceCode] = {name : sourceNameSetting.name, used : false};
                    }
                }

                if (selectedTemplate.participants != null) {
                    for (var index in selectedTemplate.participants) {
                        var participant = selectedTemplate.participants[index];

                        var sourceName = null;
                        var sourceId = null;
                        var participantSourceName = null;
                        var sourceType = $(".selectParticipant[participant_id=" + participant.participantId + "]").filter(":checked").attr("source_type");
                        switch (sourceType) {
                            case "searchParticipant":
                                $searchInput = $(".searchParticipant[participant_id=" + participant.participantId + "]");
                                sourceType = "DEFAULT";
                                sourceName = null;
                                sourceId = $searchInput.attr("source_id");
                                participantSourceName = $searchInput.val();
                                break;
                            case "insertParticipant":
                                sourceType = "CUSTOM";
                                sourceName = $(".insertParticipant[participant_id=" + participant.participantId + "]").val();
                                sourceId = null;
                                if (sourceName != null && sourceName != "") {
                                    usedSourceNames[sourceName].used = true;
                                }
                                break;
                        }

                        if (
                            (sourceName == null || sourceName == "") &&
                            (sourceId == null || sourceId == "")
                        ) {
                            bootbox.alert("Участник шаблона документа \"" + participant.name + "\" не установлен");
                            return false;
                        }

                        participant.sourceType = sourceType;
                        participant.sourceName = sourceName;
                        participant.sourceId = sourceId;
                        participant.participantSourceName = participantSourceName;
                    }
                }

                for (var sourceCode in usedSourceNames) {
                    var usedSourceName = usedSourceNames[sourceCode];
                    if (!usedSourceName.used) {
                        bootbox.alert("Автоматическая установка участника \"" + usedSourceName.name + "\" не задействован в шаблоне");
                        return false;
                    }
                }

                if (selectedTemplate.index != null) {
                    var index = parseInt(selectedTemplate.index)
                    self.documentTemplates[index] = selectedTemplate;
                } else {
                    self.documentTemplates.push(selectedTemplate);
                }
                selectedTemplate = null;
                $editTemplateModal.modal("hide");
                self.reInit();
            });
        }

        this.sourceNames = {};

        this.documentTemplates = [];

        this.findParticipantFunc = function(){};

        this.saveTemplatesFunc = function(){};

        this.$drawNode = null;

        function _initIndex(documentTemplates) {
            for (var index in documentTemplates) {
                documentTemplates[index]["index"] = index;
            }
        }

        this.getSourceNames = function(participantType){
            var result = null;
            if (this.sourceNames != null) {
                result = this.sourceNames[participantType];
            }
            return result;
        };
        
        this.init = function($drawNode, documentTemplates, findParticipantFunc, saveTemplatesFunc, sourceNames) {
            _initIndex(documentTemplates);
            this.$drawNode = $drawNode;
            this.documentTemplates = documentTemplates;
            this.findParticipantFunc = findParticipantFunc;
            this.saveTemplatesFunc = saveTemplatesFunc;
            this.sourceNames = sourceNames;

            if ($editTemplateModal == null) {
                $editTemplateModal = $(editTemplate);
                $("body").append($editTemplateModal);
            }

            var pageMarkup = $(pageTemplate);

            $drawNode.empty();
            $drawNode.append(pageMarkup);

            _drawDocumentTemplates(this, $("#documentTemplateSettingsTable", pageMarkup), tableTemplate, this.documentTemplates);
        };

        this.reInit = function() {
            this.init(this.$drawNode, this.documentTemplates, this.findParticipantFunc, this.saveTemplatesFunc, this.sourceNames);
        };
    };

    var pluginInited = false;

    initDocumentTemplateSettings = function($drawNode, documentTemplates, findParticipantFunc, saveTemplatesFunc, sourceNames){
        if (!pluginInited) {
            pageTemplate = PAGE_TEMPLATE;
            Mustache.parse(pageTemplate);
            tableTemplate = TABLE_TEMPLATE;
            Mustache.parse(tableTemplate);
            editTemplate = EDIT_TEMPLATE;
            Mustache.parse(editTemplate);
            templateParticipantsTemplate = TEMPLATE_PARTICIPANTS;
            Mustache.parse(templateParticipantsTemplate);
        }
        pluginInited = true;

        var documentTemplateSettings = new DocumentTemplateSettings();

        documentTemplateSettings.init($drawNode, documentTemplates, findParticipantFunc, saveTemplatesFunc, sourceNames);
    };
})();