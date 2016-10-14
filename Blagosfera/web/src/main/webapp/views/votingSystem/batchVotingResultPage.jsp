<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="votingsResultGrid.jsp" %>
<%@ include file="votingProtocolGrid.jsp" %>
<%@ include file="../uservotings/batchVotingVotersGrid.jsp" %>
<script type="text/javascript">
    var batchVotingId = '${param['batchVotingId']}';
    var documentChangeSubscribed = false;

    var statusMap = {};
    statusMap["REGISTERED"] = {
        "male" : "Участвовал в собрании",
        "female" : "Участвовала в собрании",
        "color" : "green"
    };
    statusMap["UNKNOWN"] = {
        "male" : "Не зарегистрировался в собрании",
        "female" : "Не зарегистрировалась в собрании",
        "color" : "blue"
    };

    // Полчить протокол пакета голосований
    function getProtocolBatchVoting(callBack, errorCallBack, withWaiter) {
        var ajaxFuncName = "radomJsonGet";
        if (withWaiter) {
            ajaxFuncName = "radomJsonGetWithWaiter";
        }

        $[ajaxFuncName](
                "/voting/getBatchVotingFull?batchVotingId=" + batchVotingId,
                {
                },
                callBack,
                errorCallBack
        );
    }

    function getDocumentData(documentId, callBack, errorCallBack) {
        $.radomJsonPost(
                "/document/service/getDocumentData.json",
                {
                    document_id : documentId
                },
                callBack,
                errorCallBack
        );
    }

    function getDialogData(dialog, callBack, errorCallBack) {
        $.radomJsonGet(
                "/chat/dialog.json",
                {
                    dialog_id : dialog
                },
                callBack,
                errorCallBack
        );
    }

    function initControls() {
        if ($("#protocolLink").length > 0) {
            $("#protocolLink").click(function () {
                $("#protocolBlock").slideToggle("fast", function () {
                    // Animation complete.
                });
                $(".fa-chevron-up", $("#protocolLink")).toggle();
                $(".fa-chevron-down", $("#protocolLink")).toggle();
                return false;
            });
        }
        $("[name=votersFilter]").click(function(){
            var registeredStatus = $(this).attr("id");
            registeredStatus = registeredStatus == "allVoters" ? null : registeredStatus;
            initBatchVotingVotersGrid(batchVotingId, statusMap, registeredStatus);
        });

        $("body").on("click", ".votingProtocol", function(){
            $("#votingResult").modal("show");
            $("#votingProtocolList").empty();

            var votingId = $(this).attr("voting_id");
        });

        $("#batchVotingDialog").click(function(){
            var dialogId = $(this).attr("dialog_id");
            var dialogName = $(this).attr("dialog_name");
            ChatView.showPopupChatDialog(dialogId, dialogName);
        });
        initResultPageButtons();
    }

    function initDocumentData(batchVoting, document) {
        var initDocument = function(batchVoting, document) {
            var result = "";
            if (document.isSigned) {
                result = "<span style='color: green;'>Протокол подписан</span>";
            } else {
                result = "<span style='color: red;'>Протокол не подписан</span>";
            }
            initDialogData(batchVoting, !document.isSigned);
            $("#documentSignedInfo").html(result);

            if (!documentChangeSubscribed) {
                documentChangeSubscribed = true;
                radomStompClient.subscribeToTopic("document_signed_" + document.id, function (messageBody) {
                    var iframeHtml =
                    "<iframe " +
                    "id='printDocument' " +
                    "name='printDocument' " +
                    "style='width: 100%; height: 100%; border: 1px solid #ccc;' " +
                    "src='/document/service/documentPrintPage?document_id=" + document.id + "&print=false'></iframe> "
                    $("#documentBlock").html(iframeHtml);
                    initDocumentData(batchVoting, messageBody);
                });
            }
        }

        if (document == null) {
            getDocumentData(batchVoting.response.additionalData.batchVotingProtocolId, function(response){
                initDocument(batchVoting, response);
            }, function(error){
                $("#documentSignedInfo").html(error.message);
            });
        } else {
            initDocument(batchVoting, document);
        }
    }

    function initDialogData(batchVoting, show) {
        if (batchVoting.response.additionalData.batchVotingDialogId != null) {
            if ($("#batchVotingDialog").attr("dialog_id") == null) {
                getDialogData(batchVoting.response.additionalData.batchVotingDialogId, function(response){
                    $("#batchVotingDialog").attr("dialog_id", batchVoting.response.additionalData.batchVotingDialogId);
                    $("#batchVotingDialog").attr("dialog_name", response.name);
                });
                if (show) {
                    $("#batchVotingDialog").show();
                } else {
                    $("#batchVotingDialog").hide();
                }
            } else {
                if (show) {
                    $("#batchVotingDialog").show();
                } else {
                    $("#batchVotingDialog").hide();
                }
            }
        }
    }

    function initResultPageButtons() {
        $('body').on('click', '.resultPageButton', function (e) {
            var buttonText = $(this).text();
            getProtocolBatchVoting(function(batchVoting){
                var resultPageButtons = [];
                if (batchVoting.response.additionalData.resultPageButtons != null) {
                    resultPageButtons = JSON.parse(batchVoting.response.additionalData.resultPageButtons);
                }
                var foundResultPageButton = null;
                if (resultPageButtons != null && resultPageButtons.length > 0) {
                    for (var index in resultPageButtons) {
                        var resultPageButton = resultPageButtons[index];
                        if (resultPageButton.buttonText == buttonText) {
                            foundResultPageButton = resultPageButton;
                            break;
                        }
                    }
                }
                if (foundResultPageButton != null) {
                    if (foundResultPageButton.buttonType == "alert") {
                        bootbox.alert(foundResultPageButton.text);
                    } else if (foundResultPageButton.buttonType == "link") {
                        window.location = foundResultPageButton.text;
                    }
                }
            }, null, true);
        });
    }

    $(document).ready(function () {
        getProtocolBatchVoting(function(batchVoting){
            var batchVotingProtocolResultTemplate = $("#batchVotingProtocolResultTemplate").html();
            Mustache.parse(batchVotingProtocolResultTemplate);
            var model = batchVoting;
            if (batchVoting.errorCode == 0) {
                if (batchVoting.response.additionalData.batchVotingProtocolId != null) {
                    batchVoting.response.additionalData.hasBatchVotingProtocolId = true;
                } else {
                    batchVoting.response.additionalData.hasBatchVotingProtocolId = false;
                }


                var startDate = null;
                var endDate = null;
                if (batchVoting.response.startedAt != null) {
                    startDate = createIsoDate(batchVoting.response.startedAt);
                } else {
                    startDate = createIsoDate(batchVoting.response.parameters.startDate);
                }

                if (batchVoting.response.finishedAt != null) {
                    endDate = createIsoDate(batchVoting.response.finishedAt);
                } else {
                    endDate = createIsoDate(batchVoting.response.parameters.endDate);
                }

                var startDateFormatted = startDate.format("dd.mm.yyyy HH:MM");
                model.startDateFormatted = startDateFormatted;


                var datePeriod = endDate.getTime() - startDate.getTime();
                datePeriod = datePeriod / 1000;
                var countDays = parseInt(datePeriod / (24 * 60 * 60));
                var countSeconds = datePeriod - (countDays * 24 * 60 * 60);
                var countHours = parseInt(countSeconds / (60 * 60));
                countSeconds = datePeriod - (countDays * 24 * 60 * 60) - (countHours * 60 * 60);
                var countMinutes = parseInt(countSeconds / (60));
                countSeconds = datePeriod - (countDays * 24 * 60 * 60) - (countHours * 60 * 60) - (countMinutes * 60);

                //3 дня 2 часа 34 минуты
                var datePeriodFormatted = "";
                if (countDays == 0 && countHours == 0 && countMinutes == 0) {
                    datePeriodFormatted = "меньше минуты";
                } else {
                    var daysForm = stringForms(countDays, "день", "дня", "дней");
                    var hoursForm = stringForms(countHours, "час", "часа", "часов");
                    var minutesForm = stringForms(countMinutes, "минуту", "минуты", "минут");

                    if (countDays > 0) {
                        datePeriodFormatted = countDays + " " + daysForm;
                    }
                    if (countDays == 0 && countHours == 0) {
                        // do nothing
                    } else {
                        datePeriodFormatted = datePeriodFormatted + " " + countHours + " " + hoursForm;
                    }
                    datePeriodFormatted = datePeriodFormatted + " " + countMinutes + " " + minutesForm;
                }
                model.datePeriodFormatted = datePeriodFormatted;
                model.batchVotingDescription =
                        (batchVoting.response.additionalData.batchVotingDescription == null ||
                        batchVoting.response.additionalData.batchVotingDescription == "") ?
                                "Подробности собрания" : batchVoting.reponse.additionalData.batchVotingDescription;

                var resultPageButtons = [];
                if (batchVoting.response.additionalData.resultPageButtons != null) {
                    resultPageButtons = JSON.parse(batchVoting.response.additionalData.resultPageButtons);
                }
                model.resultPageButtons = resultPageButtons;
                model.hasResultPageButtons = resultPageButtons != null && resultPageButtons.length > 0;
            }

            var $markup = Mustache.render(batchVotingProtocolResultTemplate, model);
            $("#batchVotingFull").append($markup);

            if (batchVoting.errorCode == 0) {
                initDocumentData(batchVoting);
                initVotingsResultGrid(batchVoting);
                initBatchVotingVotersGrid(batchVotingId, statusMap);
                initControls();
            }
        })
    });
</script>
<div id="batchVotingFull"></div>

<script id="batchVotingProtocolResultTemplate" type="x-tmpl-mustache">
    {{#errorCode}}
        <h4>{{message}}</h4>
    {{/errorCode}}
    {{^errorCode}}
        <div class="form-group">
            <h2>Итоги проведения собрания по теме {{response.subject}}</h2>
        </div>
        <button type="button" class="btn btn-primary" onclick="$('#meetingTargets').modal('show');" >{{batchVotingDescription}}</button>
        <button type="button" class="btn btn-primary" id="batchVotingDialog" style="display: none;">Чат собрания</button>
        <div class="form-group" style="margin: 10px 0px; {{^hasResultPageButtons}}display: none;{{/hasResultPageButtons}}">
            {{#resultPageButtons}}
                <button type="button" class="btn btn-primary resultPageButton" style="margin-right: 5px" >{{buttonText}}</button>
            {{/resultPageButtons}}
        </div>
        {{#response.additionalData.hasBatchVotingProtocolId}}
            <div class="form-group">
                <a href="#" id="protocolLink"><h4 style="display: inline-block;"><i class="fa fa-chevron-up"></i><i class="fa fa-chevron-down" style="display: none;"></i> Протокол собрания</h4></a>
                <div id="documentSignedInfo" style="float: right; font-size: 18px; font-weight: bold;"></div>
                <div style="clear: both;"></div>
                <div id="protocolBlock">
                    <div style="float: left;">
                        <a href="/document/service/documentPage?document_id={{response.additionalData.batchVotingProtocolId}}" target="_blank" style="font-size: 18px; font-weight: bold;"><i class="fa fa-file-text-o"></i> Перейти на страницу документа</a>
                    </div>
                    <div style="float: right;">
                        <a style="margin-right: 10px;" href="/document/service/documentPrintPage?document_id={{response.additionalData.batchVotingProtocolId}}&print=true" target="_blank"><i class="fa fa-print"></i> Печать</a>
                        <a href="/document/service/exportDocumentToPdf?document_id={{response.additionalData.batchVotingProtocolId}}" target="_blank"><i class="fa fa-file-pdf-o"></i> PDF</a>
                    </div>
                    <div style="clear: both;"></div>
                    <div style="width: 100%; height: 400px; overflow: auto;" id="documentBlock">
                        <iframe
                            id="printDocument"
                            name="printDocument"
                            style="width: 100%; height: 100%; border: 1px solid #ccc;"
                            src="/document/service/documentPrintPage?document_id={{response.additionalData.batchVotingProtocolId}}&print=false"></iframe>
                    </div>
                </div>
            </div>
        {{/response.additionalData.hasBatchVotingProtocolId}}
        <div class="form-group">
            <div>
                <label>Дата начала собрания: {{startDateFormatted}}.</label>
            </div>
            <div>
                <label>Собрание длилось {{datePeriodFormatted}}.</label>
            </div>
        </div>
        <div class="form-group">
            <div id="votingsResult-grid"></div>
        </div>
        <hr/>
        <div class="form-group">
            <label><input type="radio" name="votersFilter" id="allVoters" checked="checked"/> Все участники собрания</label>
            <label><input type="radio" name="votersFilter" id="REGISTERED" /> Участники, которые зарегистрировались</label>
            <label><input type="radio" name="votersFilter" id="UNKNOWN" /> Участники, которые не зарегистрировались</label>
            <div id="batchVotingVoters-grid"></div>
        </div>

        <!-- Модальное окно для отображения побробностей собрания-->
        <div class="modal fade" role="dialog" id="meetingTargets" aria-labelledby="meetingTargetsLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="meetingTargetsLabel">{{batchVotingDescription}}</h4>
                    </div>
                    <div class="modal-body">
                        {{{response.additionalData.meetingTargets}}}
                        <hr/>
                        <p>{{{response.additionalData.additionalMeetingTargets}}}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    {{/errorCode}}
</script>

