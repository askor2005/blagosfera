define([], function () {

    var currentNodeParameters = null;
    var utils = null;

    var sharers = [];
    var selectedTemplate = null;
    var selectedSharer = null;
    var sharersTableTemplate = null;
    var currentSendJobId = null;

    var handleResponse = function(response) {
        bootbox.alert("Запрос на отправку писем отправлен");
        // TODO Доработать статус отправки писем
        currentSendJobId = response.sendJobId;
    };

    var handleErrorResponse = function(response) {
        bootbox.alert(response.message);
    };


    var sendEmailsToActiveSharers = function(){
        $.radomJsonPost(
            "/admin/emailsender/sendEmailsToActiveSharers.json",
            {
                template_code: getTemplateCode(),
                mail_subject: getMailSubject(),
                mail_from: getMailFrom()
            },
            handleResponse,
            handleErrorResponse
        );
    };

    var sendEmailsToSharers = function(){
        $.radomJsonPost(
            "/admin/emailsender/sendEmailsToSharers.json",
            {
                template_code: getTemplateCode(),
                mail_subject: getMailSubject(),
                mail_from: getMailFrom(),
                sharer_ids: getSharerIds()
            },
            handleResponse,
            handleErrorResponse
        );
    };

    var sendEmailsToMan = function(){
        $.radomJsonPost(
            "/admin/emailsender/sendEmailsToMan.json",
            {
                template_code: getTemplateCode(),
                mail_subject: getMailSubject(),
                mail_from: getMailFrom()
            },
            handleResponse,
            handleErrorResponse
        );
    };

    var sendEmailsToWomen = function(){
        $.radomJsonPost(
            "/admin/emailsender/sendEmailsToWomen.json",
            {
                template_code: getTemplateCode(),
                mail_subject: getMailSubject(),
                mail_from: getMailFrom()
            },
            handleResponse,
            handleErrorResponse
        );
    };

    // Инициализация поиска пользователей
    var initSearchSharers = function(jqInput, onSelect){
        jqInput.typeahead({
            triggerLength: 1,
            delay: 500,
            autoSelect: true,
            updater: function(item) {
                onSelect({
                    id: item.id,
                    fullName: item.fullName
                });
                return item;
            },
            source:  function (query, process) {
                var ids = [];
                for (var index in sharers) {
                    var sharer = sharers[index];
                    ids.push(sharer.id);
                }
                var data = {
                    query: query,
                    "id[]" : ids
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/sharer/searchActive.json",
                    data: data,
                    success: function (data) {
                        for (var index in data) {
                            var item = data[index];
                            item.name = item.fullName;
                        }
                        return process(data);
                    },
                    error: function () {
                        console.log("ajax error");
                        return process(false);
                    }
                });
            }
        });
    };

    // Инициализация поиска шаблона
    var initSearchTemplates = function(jqInput, onSelect){
        jqInput.typeahead({
            triggerLength: 1,
            delay: 500,
            autoSelect: true,
            updater: function(item) {
                onSelect({
                    code: item.code,
                    name: item.name
                });
                return item;
            },
            source:  function (query, process) {
                var data = {
                    template_name: query
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/admin/emailsender/findDocumentTemplates.json",
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
    };

    var initSharersTable = function(nodeParameters){
        var jqSharersTemplate = nodeParameters.jqSharersTemplate;
        var jqSharersTable = nodeParameters.jqSharersTable;
        var deleteSharerClass = nodeParameters.deleteSharerClass;
        sharersTableTemplate = jqSharersTemplate.html();
        Mustache.parse(sharersTableTemplate);

        var jqSharerNameNode = nodeParameters.jqSharerNameNode;
        var jqAddSharer = nodeParameters.jqAddSharer;
        initSearchSharers(jqSharerNameNode, function(sharer){
            selectedSharer = sharer;
            jqAddSharer.prop("disabled", false);
        });
        jqAddSharer.click(function(){
            sharers.push(selectedSharer);
            jqSharerNameNode.val("");
            selectedSharer = null;
            jqAddSharer.prop("disabled", true);
            drawSharersTable(sharers, sharersTableTemplate, jqSharersTable)
        });
        jqSharersTable.on("click", "." + deleteSharerClass, function(){
            var sharerId = $(this).attr("sharer_id");
            utils.removeItemFromArray(sharers, "id", sharerId);
            drawSharersTable(sharers, sharersTableTemplate, jqSharersTable)
        });

        nodeParameters.jqSharersProviderAll.click(function(){
            nodeParameters.jqSharersProviderSelectedBlock.hide();
        });
        nodeParameters.jqSharersProviderMan.click(function(){
            nodeParameters.jqSharersProviderSelectedBlock.hide();
        });
        nodeParameters.jqSharersProviderWoman.click(function(){
            nodeParameters.jqSharersProviderSelectedBlock.hide();
        });
        nodeParameters.jqSharersProviderSelected.click(function(){
            nodeParameters.jqSharersProviderSelectedBlock.show();
        });
    };

    var initTemplate = function(nodeParameters) {
        var jqTemplateNameNode = nodeParameters.jqTemplateNameNode;
        initSearchTemplates(jqTemplateNameNode, function(template){
            selectedTemplate = template;
        });
    };

    var drawSharersTable = function(sharers, tableTemplate, jqSharersTable){
        for (var index in sharers) {
            sharers[index].index = parseInt(index) + 1;
            sharers[index].canDelete = true;
        }

        var jqTableNode = $(Mustache.render(tableTemplate,
            {
                sharers: sharers
            }
        ));
        jqSharersTable.empty();
        jqSharersTable.append(jqTableNode);
    };

    var initSendToEmailButton = function(nodeParameters){
        var jqSendToEmailButton = nodeParameters.jqSendToEmailButton;
        jqSendToEmailButton.click(function(){
            if (nodeParameters.jqSharersProviderAll.prop("checked")) {
                sendEmailsToActiveSharers();
            } else if (nodeParameters.jqSharersProviderMan.prop("checked")) {
                sendEmailsToMan();
            } else if (nodeParameters.jqSharersProviderWoman.prop("checked")) {
                sendEmailsToWomen();
            } else { //nodeParameters.jqSharersProviderSelected
                sendEmailsToSharers();
            }
        });
    };

    var getMailSubject = function(){
        return currentNodeParameters.jqMailSubject.val();
    };

    var getMailFrom = function(){
        return currentNodeParameters.jqMailFrom.val();
    };

    var getTemplateCode = function(){
        return selectedTemplate.code;
    };

    var getSharerIds = function(){
        var ids = [];
        for (var index in sharers) {
            var sharer = sharers[index];
            ids.push(sharer.id);
        }
        return ids;
    };

    var initShowSendEmailsData = function(nodeParameters) {
        // SendSharerMailDto
        radomStompClient.subscribeToUserQueue("send_sharer_mail", function(payload){
            var resultNode = nodeParameters.jqSendMailResults.find("[request_time=" + payload.requestTime + "]");
            var countSharers = parseInt(payload.countSharers);
            var sharerIndex = parseInt(payload.sharerIndex);
            if (resultNode == null || resultNode.length == 0) {
                var formattedDate = new Date(payload.requestTime).format("HH:MM:ss");
                resultNode = $("<div request_time='" + payload.requestTime + "'><label style='font-weight: bold; font-size: 18px;'>Дата запуска: " + formattedDate + "</label><div class='send_progress'></div></div>");
                nodeParameters.jqSendMailResults.append(resultNode);
            }
            if (payload.result) {
                resultNode.append("<div>Письмо для " + payload.fio + " отправлено успешно</div>");
            } else {
                resultNode.append("<div>При отправке писма для  " + payload.fio + " произошла ошибка: " + payload.error + "</div>");
            }
            $(".send_progress", resultNode).html("Обработано " + (sharerIndex + 1) + " из " + countSharers + " (" + Math.floor(((sharerIndex + 1) / countSharers) * 100) + "%)");
        });
    };


    var emailSenderModule = {

    };

    // Инициализация шаблона
    emailSenderModule.init = function(jqTemplateNode, nodeParameters){
        require(
            [
                "utils/utils"
            ],
            function (utilsLocal) {
                currentNodeParameters = nodeParameters;
                utils = utilsLocal;
                initSharersTable(nodeParameters);
                initTemplate(nodeParameters);
                initSendToEmailButton(nodeParameters);
                initShowSendEmailsData(nodeParameters);
            },
            function (error) {
                console.log(error);
            }
        );
    };

    return emailSenderModule;
});