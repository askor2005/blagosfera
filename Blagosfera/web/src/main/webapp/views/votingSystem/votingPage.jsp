<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="votingProtocolGrid.jsp" %>
<%@include file="votingTemplates.jsp" %>

<script type="text/javascript">
    var currentVoting;
    var nextVotingLink;
    var abstainExistsGlobal = false;
    var allVotings = [];
    var allowVote = true;
    var votingId = '${votingId}';
    var batchVotingId = '${batchVotingId}';
    var isClosedVoting = ${isClosedVoting};
    var currentBatchVoting = null;
    var userId = ${sharer.id};
    var baseLink = '${baseLink}';
    var batchVotingEndDate = createIsoDate('${batchVotingEndDate}');
    var nowDate = createDate();
    var startDate = createIsoDate('${votingStartDate}');
    var votingEndDate = createIsoDate('${votingEndDate}');
    var voteEndedDialogHeaderTemplate = $("#voteEndedDialogHeaderTemplate").html();
    var voteEndedDialogContentTemplate = $("#voteEndedDialogContentTemplate").html();
    var isCurrentSharerCanRestartVoting = ${isCurrentSharerCanRestartVoting}; // У текущего пользователя есть парава на рестарт собрания
    var protocolIsInited = false;
    var useBiometricIdentification = ${useBiometricIdentification};
    var skipResults = "${skipResults}" === "true";
    var PRO_CONTRA = "${votingTypes.PRO_CONTRA}";
    var CANDIDATE = "${votingTypes.CANDIDATE}";
    var INTERVIEW = "${votingTypes.INTERVIEW}";
    var SINGLE_SELECTION = "${votingTypes.SINGLE_SELECTION}";
    var MULTIPLE_SELECTION = "${votingTypes.MULTIPLE_SELECTION}";

    var candidates = [];
    <c:forEach var="candidate" items="${candidates}" varStatus="i">
    candidates.push({
        id: ${candidate.id},
        ikp: "${candidate.ikp}",
        name: "${candidate.name}",
        avatarSrc: Images.getResizeUrl("${candidate.avatarSrc}", "c48"),
        avatarMediumSrc: Images.getResizeUrl("${candidate.avatarSrc}", "c100"),
        avatarBigSrc: Images.getResizeUrl("${candidate.avatarSrc}", "c140")
    });
    </c:forEach>

    var voters = [];
    <c:forEach var="voter" items="${voters}" varStatus="i">
    voters.push({
        id: ${voter.id},
        ikp: "${voter.ikp}",
        name: "${voter.name}",
        avatarSrc: Images.getResizeUrl("${voter.avatarSrc}", "c48"),
        avatarMediumSrc: Images.getResizeUrl("${voter.avatarSrc}", "c93")
    });
    </c:forEach>
    // ИД участников
    var votersWhoVotes = {};
    <c:forEach var="voterId" items="${votersWhoVotes}" varStatus="i">
    votersWhoVotes[${voterId}] = ${voterId};
    </c:forEach>


    // Полчить пакет голосований
    function getBatchVoting(callBack, errorCallBack) {
        $.ajax({
            type: "get",
            dataType: "json",
            url: "/voting/getBatchVoting?batchVotingId=" + batchVotingId,
            data: {},
            success: function (response) {
                callBack(response);
            },
            error: function (error) {
                errorCallBack(error);
            }
        });
    }

    // Получить данные голосования
    function getVoting(callBack, errorCallBack) {
        $.ajax({
            type: "get",
            dataType: "json",
            url: "/voting/getVoting?votingId=" + votingId,
            data: {},
            success: function (response) {
                callBack(response);
            },
            error: function (error) {
                errorCallBack(error);
            }
        });
    }
    ;

    // Получить протокол голосования
    function getVotingProtocol(callBack, errorCallBack) {
        $.ajax({
            type: "get",
            dataType: "json",
            url: "/voting/getVotingFull?votingId=" + votingId,
            data: {},
            success: function (response) {
                callBack(response);
            },
            error: function (error) {
                errorCallBack(error);
            }
        });
    }

    function addVotes(votes, callback, errorCallback) {
        if (useBiometricIdentification) {
            $.radomFingerJsonAjax({
                url: "/voting/addVotes",
                contentType: "application/json",
                type: "post",
                data: JSON.stringify({votes: votes}),
                successRequestMessage: "Голос учтен",
                errorMessage: "Ошибка выполнения запроса. Попробуйте еще раз.",
                successCallback: callback,
                errorCallback: errorCallback
            });
        } else {
            $.radomJsonPostWithWaiter(
                    "/voting/addVotes",
                    JSON.stringify({votes: votes}),
                    callback,
                    errorCallback,
                    {
                        contentType: "application/json"
                    }
            );
        }
    }

    // Отменить результаты голосования
    function deleteVote(voteId, callBack, errorCallBack) {
        $.ajax({
            type: "get",
            contentType: "application/json",
            datatype: "json",
            url: "/voting/deleteVote?voteId=" + voteId,
            data: {},
            success: function (response) {
                callBack(response);
            },
            error: function (error) {
                errorCallBack(error);
            }
        });
    }
    ;
    function deleteVotingItem(votingItemId,votingId,callback,errorCallback) {
        if (useBiometricIdentification) {
            $.radomFingerJsonAjax({
                url: "/voting/deleteVotingItemF",
                contentType: "application/json",
                type: "post",
                data: JSON.stringify({
                    votingId: votingId,
                    votingItemId: votingItemId
                }),
                successRequestMessage: "Ответ учтен",
                errorMessage: "Ошибка выполнения запроса. Попробуйте еще раз.",
                successCallback: callback,
                errorCallback: errorCallback
            });
        } else {
            $.ajax({
                type: "post",
                contentType: "application/json",
                datatype: "json",
                url: "/voting/deleteVotingItem",
                data: JSON.stringify({
                    votingId: votingId,
                    votingItemId: votingItemId
                }),
                success: callback,
                error: errorCallback
            });
        }
    }
    // Добавить вариант для голосования
    function addVotingItem(voteItemText, callback, errorCallback) {
        if (useBiometricIdentification) {
            $.radomFingerJsonAjax({
                url: "/voting/addVotingItem",
                contentType: "application/json",
                type: "post",
                data: JSON.stringify({
                    id: null,
                    votingId: votingId,
                    value: voteItemText
                }),
                successRequestMessage: "Ответ учтен",
                errorMessage: "Ошибка выполнения запроса. Попробуйте еще раз.",
                successCallback: callback,
                errorCallback: errorCallback
            });
        } else {
            $.ajax({
                type: "post",
                contentType: "application/json",
                datatype: "json",
                url: "/voting/addVotingItem",
                data: JSON.stringify({
                    id: null,
                    votingId: votingId,
                    value: voteItemText
                }),
                success: callback,
                error: errorCallback
            });
        }
    }

    // Получить массив проголосовавших
    function getVotersWhoVotes(callBack, errorCallBack) {
        $.ajax({
            type: "post",
            //contentType: "application/json",
            datatype: "json",
            url: "/votingsystem/getVotersWhoVotes.json",
            data: {
                votingId: votingId
            },
            success: function (response) {
                if ($.isArray(response)) {
                    callBack(response);
                } else if (response.result == "error") {
                    errorCallBack(response.message);
                }
            },
            error: function (error) {
                errorCallBack(error);
            }
        });
    }

    // Перезапустить голосование
    function restartVoting(callBack) {
        $.radomJsonPostWithWaiter("/votingsystem/restartVoting.json",
                {
                    votingId: votingId
                },
                callBack
        );
    }

    // Завершить голосование
    function finishVoting(callBack) {
        $.radomJsonPostWithWaiter("/votingsystem/finishVoting.json",
                {
                    votingId: votingId
                },
                callBack
        );
    }

    // Оповестить участника о голосовании
    function hurryInVoting(voterId, callBack) {
        $.radomJsonPostWithWaiter("/votingsystem/hurryVoterInVoting.json",
                {
                    votingId: votingId,
                    voterId: voterId
                },
                callBack
        );
    }

    // Выбранные значения голосования
    var selectedVoteIds = [];

    $(document).ready(function () {
        init();

        radomStompClient.subscribeToUserQueue("new_notification", function (notification) {
            console.log(batchVotingId, notification);
            if (notification.type === 'VOTING') {
                if (batchVotingId === notification.data.batchVotingId) {
                    $.radomJsonPost("/notifications/markasread.json", {
                        notification_id: notification.id
                    }, function () {
                        console.log('notification prevented');
                        setTimeout(function () {
                            showHeaderUnreadNotifications();
                        }, 100);
                    });
                }
            }
        });
    });

    // Скрыть\раскрыть протокол голосования
    function toggleProtocol(votingItemId) {
        $("#votingProtocol" + votingItemId + "-grid").toggle();
        var grid = Ext.getCmp("votingProtocolPanel" + votingItemId);
        if (grid != null) { // Есть таблица с результатами
            grid.getView().refresh();
        }
    }

    // Создать плашку с тем, что за вариант никто не проголосовал
    function createEmptyProtocol(jqSelector) {
        jqSelector.append("<div class='emptyProtocol'><span>Голосов не зафиксировано</span></div>");
    }

    // Создать плашку с тем, что за вариант никто не проголосовал
    function createClosedVotingProtocol(jqSelector) {
        jqSelector.append("<div class='closedVotingProtocol'><span>Голосование тайное</span></div>");
    }

    var isInitedNowDateChange = false;
    function initNowDateChange() {
        if (isInitedNowDateChange) {
            return;
        }
        isInitedNowDateChange = true;
        setInterval(function () {
            nowDate.setTime(nowDate.getTime() + 1000);
        }, 1000);
    }

    // Инициализация отображения оставшегося времени
    function initLeftTime(endDate, jqNode) {
        initNowDateChange();
        var timeFunction = function () {
            var millisecondsLeft = endDate.getTime() - nowDate.getTime();
            if (millisecondsLeft < 0) {
                jqNode.text("время вышло");
            } else {
                var seccondsLeft = parseInt(millisecondsLeft / 1000) % 60;
                var minutesLeft = parseInt((millisecondsLeft / 1000) / 60) % 60;
                var hoursLeft = parseInt(((millisecondsLeft / 1000) / 60) / 60);
                if (seccondsLeft < 10) {
                    seccondsLeft = "0" + seccondsLeft;
                }
                if (minutesLeft < 10) {
                    minutesLeft = "0" + minutesLeft;
                }
                jqNode.text(hoursLeft + ":" + minutesLeft + ":" + seccondsLeft);
            }
        };
        setInterval(function () {
            timeFunction();
        }, 1000);
        timeFunction();
    }

    // Инициализация вариантов голосований
    function initVotingItems(votingItems) {
        var isAbstainExists = false;
        for (var index in votingItems) {
            var votingItem = votingItems[index];
            votingItem.sourceValue = votingItem.value;
            if (votingItem.value == "ABSTAIN") {
                votingItem.isAbstain = true;
                votingItem.value = "Воздержаться";
                isAbstainExists = true;
            } else {
                votingItem.isAbstain = false;
            }
        }

        // Если среди вариантов есть "ABSTAIN" то сортируем
        if (isAbstainExists) {
            votingItems.sort(function (a, b) {
                var result = -1;
                //console.log(a.value < b.value);
                if (a.sourceValue > b.sourceValue) {
                    result = 1;
                }
                return result;
            });
        }
        abstainExistsGlobal = isAbstainExists;
    }

    // Иницаализируем голосование из данных
    function initVotingFromData(voting, templates, needSort) {
        //PRO_CONTRA - за/против/воздержался,
        //CANDIDATE - выбор кандидата,
        //INTERVIEW - вариант ответа задает сам участник,
        //SINGLE_SELECTION - выбор одного из вариантов,
        //MULTIPLE_SELECTION - выбор нескольких вариантов
        var votingType = voting.response.parameters.votingType;
        var votingTypeTemplate = null;
        switch (votingType) {
            case PRO_CONTRA:
                voting.response.maxSelectionCountStringForm = stringForms(voting.response.parameters.maxSelectionCount, "вариант", "варианта", "вариантов");
                voting.response.parameters.isProContraAbstain = true;
                var proVotingItem = null;
                var contraVotingItem = null;
                var abstainVotingItem = null;
                var values = ["За", "Против", "Воздержаться"];

                // Меняем наименования вариантов ответа на русские
                for (var index in voting.response.votingItems) {
                    var votingItem = voting.response.votingItems[index];
                    votingItem.sourceValue = votingItem.value;
                    switch (votingItem.value) {
                        case "PRO":
                            votingItem.isAbstain = false;
                            votingItem.isProItem = true;
                            votingItem.value = values[0];
                            proVotingItem = votingItem;
                            break;
                        case "CONTRA":
                            votingItem.isAbstain = false;
                            votingItem.isProItem = false;
                            votingItem.value = values[1];
                            contraVotingItem = votingItem;
                            break;
                        case "ABSTAIN":
                            votingItem.isAbstain = true;
                            votingItem.isProItem = false;
                            votingItem.value = values[2];
                            abstainVotingItem = votingItem;
                            break;
                    }
                }

                voting.response.votingItems = [];
                if (proVotingItem != null) {
                    voting.response.votingItems.push(proVotingItem);
                }
                if (contraVotingItem != null) {
                    voting.response.votingItems.push(contraVotingItem);
                }
                if (abstainVotingItem != null) {
                    voting.response.votingItems.push(abstainVotingItem);
                }
                votingTypeTemplate = templates["votingProContraAbstainSelectionTemplate"];
                break;
            case CANDIDATE:
                voting.response.maxSelectionCountStringForm = stringForms(voting.response.parameters.maxSelectionCount, "кандидата", "кандидатов", "кандидатов");
                voting.response.parameters.isCandidate = true;
                initVotingItems(voting.response.votingItems);
                // Добавляем параметр - имя кандитата в модель
                for (var index in voting.response.votingItems) {
                    var votingItem = voting.response.votingItems[index];
                    if (votingItem.value != "ABSTAIN") {
                        for (var candidateIndex in candidates) {
                            var candidate = candidates[candidateIndex];
                            if (parseInt(votingItem.value) == candidate.id) {
                                votingItem.candidate = candidate;
                            }
                        }
                    }
                }
                votingTypeTemplate = templates["votingCandidateSelectionTemplate"];
                break;
            case INTERVIEW:
                voting.response.maxSelectionCountStringForm = stringForms(voting.response.parameters.maxSelectionCount, "вариант", "варианта", "вариантов");
                voting.response.parameters.isInterview = true;
                voting.response.parameters.isAllowedVote = false;//если это интервью
                allowVote = voting.response.parameters.isAllowedVote;
                initVotingItems(voting.response.votingItems);
                votingTypeTemplate = templates["votingInterviewTemplate"];
                break;
            case SINGLE_SELECTION:
                voting.response.maxSelectionCountStringForm = stringForms(voting.response.parameters.maxSelectionCount, "вариант", "варианта", "вариантов");
                voting.response.parameters.isSignleSelection = true;
                initVotingItems(voting.response.votingItems);
                votingTypeTemplate = templates["votingSingleSelectionTemplate"];
                break;
            case MULTIPLE_SELECTION:
                if (voting.response.parameters.minSelectionCount === voting.response.parameters.maxSelectionCount) {
                    voting.response.selectionHint = "Вам необходимо выбрать и проголосовать за";
                    voting.response.maxSelectionCountStringForm = voting.response.parameters.maxSelectionCount +
                            ' ' +
                            stringForms(voting.response.parameters.maxSelectionCount, "вариант", "варианта", "вариантов");
                } else {
                    voting.response.selectionHint = "Вы должны выбрать"
                    voting.response.maxSelectionCountStringForm = "от " + voting.response.parameters.minSelectionCount + " до " + voting.response.parameters.maxSelectionCount + " вариантов";
                }

                voting.response.parameters.isMultipleSelection = true;
                initVotingItems(voting.response.votingItems);
                votingTypeTemplate = templates["votingMultipleSelectionTemplate"];
                break;
        }
        if (voting.response.result != null) {
            if (voting.response.result.resultType != "VALID") {
                voting.response.valid = false;
                voting.response.errorMessage = "";
                switch (voting.response.result.resultType) {
                    case "INVALID_NO_VOTES": // нет голосов
                        voting.response.errorMessage = "нет голосов";
                        break;
                    case "INVALID_WRONG_RESULT":
                        voting.response.errorMessage = "все воздержались от голосования";
                        break;
                    case "INVALID_NO_QUORUM": // недостаточное число голосов
                        voting.response.errorMessage = "недостаточное число голосов";
                        break;
                    case "INVALID_DEAD_HEAT": // ничья
                        voting.response.errorMessage = "ничья";
                        break;
                    case "INVALID_OUT_OF_DATE_RANGE": // завершено до даты начала или до даты окончания если запрещено досрочное завершение
                        voting.response.errorMessage = "завершено до даты начала или до даты окончания если запрещено досрочное завершение";
                        break;
                }
            }
            else {
                voting.response.valid = true;
            }
        }

        voting.response.votingTypeTemplate = votingTypeTemplate;
        // Форматим дату
        voting.response.dateCreatedFormatted = new Date(voting.response.created).format("dd.mm.yyyy HH:MM");

        var votersWhoVotesCount = 0;
        for (var voterId in votersWhoVotes) {
            votersWhoVotesCount++;
        }

        // Участники голосования
        for (var voterIndex in voters) {
            var voter = voters[voterIndex];
            voter.isVote = false;
            for (var voterId in votersWhoVotes) {
                if (voter.id == voterId) {
                    voter.isVote = true;
                }
            }
        }
        // Колиество проголосовавших и всего голосующих
        voting.response.votersCount = voters.length;
        voting.response.votersWhoVotesCount = votersWhoVotesCount;
        voting.response.voters = voters;

        // Сортируем варианты по ИД
        if (needSort) {
            voting.response.votingItems.sort(function (a, b) {
                if (a.id > b.id) {
                    return 1;
                } else {
                    return -1;
                }
            });
        }
    }

    function init() {
        // Инициализация голосования
        initBatchVoting(initVoting);
        // Подписываемся на события изменения текущего голосования
        radomStompClient.subscribeToUserQueue("change_votes_" + votingId, function () {
            console.log("change_votes_");
            updateVotingData(true);
        });
        // Голосование перезапущено
        radomStompClient.subscribeToUserQueue("restart_voting_" + votingId, function () {
            console.log("restart_voting_");
            updateVotingData(true);
        });
        // Состояние было завершено
        radomStompClient.subscribeToUserQueue("batch_voting_finished_" + batchVotingId, function () {
            console.log("batch_voting_finished_");
            if ($("#restartBatchVotingBlock").length > 0) {
                $("#restartBatchVotingBlock").remove();
            }
        });
        // Событие добавления варианта голосования в интервью
        /*radomStompClient.subscribeToUserQueue("change_voting_item_" + votingId, function(){
         initVoting();
         });*/
    }

    // Инициализация пакетного голосования
    function initBatchVoting(callBack) {
        // Если передан ИД пакетного голосования
        if (batchVotingId != '') {
            getBatchVoting(function (votingBatch) {
                if (votingBatch.errorCode != 0) {
                    bootbox.alert(votingBatch.message);
                    return false;
                }
                currentBatchVoting = votingBatch;
                var votingBatchTemplate = $("#votingBatchTemplate").html();
                Mustache.parse(votingBatchTemplate);
                allVotings = votingBatch.response.votings;
                for (var index in votingBatch.response.votings) {
                    var voting = votingBatch.response.votings[index];
                    
                    if (voting.id == votingId) {
                        voting.isActive = true;
                    } else {
                        voting.isActive = false;
                    }

                    if (voting.state == "FINISHED") {
                        voting.isFinished = true;
                    } else {
                        voting.isFinished = false;
                    }
                    voting.votingLink = baseLink + voting.id;
                }

                // Пакетное голосование завершено
                if (votingBatch.response.state == "FINISHED") {
                    votingBatch.response.finished = true;
                    //$("#votingBatch").append("Голосование завершено!");
                    //$("#errorMessage").remove();
                }
                if (votingBatch.response.votings != null) {
                    votingBatch.response.votings.sort(function (a, b) {
                        if (parseInt(a.id) > parseInt(b.id)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    });
                }

                var jqVotingBatchContent = $(Mustache.render(votingBatchTemplate, votingBatch));
                $("#votingBatch").empty();
                $("#votingBatch").append(jqVotingBatchContent);

                // Осталось времени до окончания собрания
                if (votingBatch.response.state == "FINISHED") {
                    $("#leftTimeEndOfBatchVoting").hide();
                } else {
                    $("#leftTimeEndOfBatchVoting").show();
                    initLeftTime(batchVotingEndDate, $("#leftTimeEndOfBatchVotingSpan"));
                }

                $("#batchVotingDialog").click(function () {
                    var dialogId = $(this).attr("dialog_id");
                    var dialogName = $(this).attr("dialog_name");
                    ChatView.showPopupChatDialog(dialogId, dialogName);
                });
                callBack(votingBatch);
            }, function (errorMessage) {
                bootbox.alert(errorMessage);
            })
        } else {
            callBack();
        }
    }

    // Инициализация событий обработки кликов по нопкам при неудачном голосовании
    function initVotingButtonsEvents() {
        $("#restartVoting").click(function () {
            restartVoting(function () {
                $("#restartVoting").hide();
                $("#finishVoting").hide();
                //window.location.reload();
            })
        });

        $("#restartVoting").radomTooltip({
            position: "top",
            container: "body",
            title: "Нажмите эту кнопку чтобы перезапустить текущее голосование заново."
        });

        $("#finishVoting").click(function () {
            finishVoting(function () {
                $("#restartVoting").hide();
                $("#finishVoting").hide();
                //window.location.reload();
            })
        });

        $("#finishVoting").radomTooltip({
            position: "top",
            container: "body",
            title: "Нажмите эту кнопку, если Вы действительно хотите <span style='color: #FF0000; font-weight: bold;'>ПРЕРВАТЬ СОБРАНИЕ</span>! ВСЕ РЕЗУЛЬТАТЫ СОБРАНИЯ БУДУТ <span style='color: #FF0000; font-weight: bold;'>АННУЛИРОВАНЫ</span> СИСТЕМОЙ! Собрание придется созывать ЗАНОВО!"
        });

        initShowHideDescription();
    }

    function initShowHideDescription() {
        if ($("#descriptionBlock").height() < 201) {
            $("#descriptionShow").parent().hide();
            $("#descriptionShadow").hide();
        } else {
            $("#descriptionBlock").css("max-height", "200px");
            $("#descriptionShow").click(function () {
                if ($("#descriptionShadow").is(":visible")) {
                    $("#descriptionBlock").css("max-height", "initial");
                    $("#descriptionShadow").hide();
                    $(this).text("Свернуть");
                } else {
                    $("#descriptionBlock").css("max-height", "200px");
                    $("#descriptionShadow").show();
                    $(this).text("Развернуть");
                }
                return false;
            });
        }
    }

    // Инициализация страницы протокола голосования
    function initVotingProtocol(showNextVoteModal) {
        protocolIsInited = true;
        initBatchVoting(function (batchVoting) {
            // Шаблон для головсования За\Против
            var votingProtocolProContraAbstainTemplate = $("#votingProtocolProContraAbstainTemplate").html();
            // Шаблон выбора из списка кандидатов
            var votingProtocolCandidateTemplate = $("#votingProtocolCandidateTemplate").html();
            // Шаблон для интервью
            var votingProtocolInterviewTemplate = $("#votingProtocolInterviewTemplate").html();
            // Шаблон для голосования с одним вариантом
            var votingProtocolSingleSelectionTemplate = $("#votingProtocolSingleSelectionTemplate").html();
            // Шаблон для голосования с множеством вариантов
            var votingProtocolMultipleSelectionTemplate = $("#votingProtocolMultipleSelectionTemplate").html();

            var votingProtocolTemplate = $("#votingProtocolTemplate").html();
            Mustache.parse(votingProtocolTemplate);

            if ($("#voting").length > 0) {
                $("#voting").empty();
                getVotingProtocol(function (votingProtocol) {
                    if (votingProtocol.errorCode != 0) {
                        bootbox.alert(votingProtocol.message);
                        return false;
                    }
                    var proContraTemplate = votingProtocolProContraAbstainTemplate;

                    var votingType = votingProtocol.response.parameters.votingType;

                    // Текст при победе варианта голосования
                    var votingWinnerText = votingProtocol.response.additionalData.votingWinnerText;
                    if (votingWinnerText == null || votingWinnerText == "") {
                        switch (votingType) {
                            case PRO_CONTRA:
                                votingWinnerText = "Победил вариант \"ЗА\"";
                                break;
                            case CANDIDATE:
                                votingWinnerText = "&nbsp;победитель на выборах";
                                break;
                            case INTERVIEW:
                                votingWinnerText = "";
                                break;
                            case SINGLE_SELECTION:
                                votingWinnerText = "Вариант голосования, который победил";
                                break;
                            case MULTIPLE_SELECTION:
                                votingWinnerText = "Вариант голосования, который победил";
                                break;
                        }
                    }
                    votingProtocol.response.votingWinnerText = votingWinnerText;

                    // Инициализация голосования
                    var templates = {
                        votingProContraAbstainSelectionTemplate: proContraTemplate,
                        votingCandidateSelectionTemplate: votingProtocolCandidateTemplate,
                        votingInterviewTemplate: votingProtocolInterviewTemplate,
                        votingSingleSelectionTemplate: votingProtocolSingleSelectionTemplate,
                        votingMultipleSelectionTemplate: votingProtocolMultipleSelectionTemplate
                    };
                    initVotingFromData(votingProtocol, templates, true);
                    var batchVotingFinished = false;
                    // Если голосование завершилось ничьей и текущий пользователь имеет права останавливать и перезапускать собрания
                    if (votingProtocol.response.state == "PAUSED") {
                        var percentForWin = 51;
                        if (votingProtocol.response.additionalData.percentForWin != null) {
                            percentForWin = parseInt(votingProtocol.response.additionalData.percentForWin);
                        }
                        var notScoredPercent = true;
                        var mostVotersAbstain = false;
                        for (var j in votingProtocol.response.votingItems) {
                            var votingItem = votingProtocol.response.votingItems[j];
                            if (votingItem.votesPercent > 50 && votingItem.sourceValue == "ABSTAIN") {
                                mostVotersAbstain = true;
                            }
                            if (votingType == PRO_CONTRA) {
                                if (votingItem.votesPercent >= percentForWin && votingItem.sourceValue == "PRO") {
                                    notScoredPercent = false;
                                }
                            } else if (votingItem.votesPercent >= percentForWin && votingItem.sourceValue != "ABSTAIN") {
                                notScoredPercent = false;
                            }

                        }


                        votingProtocol.response.stateVotingPaused = true;

                        var reasonText = "";
                        var reasonHtml = "";
                        var votingTypeName = "";
                        switch (votingType) {
                            case PRO_CONTRA:
                                votingTypeName = "голосование";
                                if (mostVotersAbstain) {
                                    reasonText = "Большинство воздержалось. ";
                                } else if (notScoredPercent) {
                                    reasonText = "Не набран необходимый процент за вариант \"За\". ";
                                }
                                reasonHtml = "<span style='text-decoration: underline;'>Голосование завершилось</span>. ";
                                break;
                            case CANDIDATE:
                                votingTypeName = "выборы";
                                if (mostVotersAbstain) {
                                    reasonText = "Большинство воздержалось. ";
                                } else if (notScoredPercent) {
                                    reasonText = "Никто из кандидатов не набрал перевеса в голосах. ";
                                }
                                reasonHtml = "<span style='text-decoration: underline;'>Выборы завершились</span>. ";
                                break;
                            case INTERVIEW:
                                votingTypeName = "интервью";
                                reasonText = "Никто не предложил свой вариант. ";
                                reasonHtml = "<span style='text-decoration: underline;'>Интерьвю завершилось</span>.";
                                break;
                            case SINGLE_SELECTION:
                                votingTypeName = "голосование";
                                if (mostVotersAbstain) {
                                    reasonText = "Большинство воздержалось. ";
                                } else if (notScoredPercent) {
                                    reasonText = "Ни один из вариантов не набрал необходимый процент. ";
                                }
                                reasonHtml = "<span style='text-decoration: underline;'>Голосование завершилось</span>. ";
                                break;
                            case MULTIPLE_SELECTION:
                                votingTypeName = "голосование";
                                if (mostVotersAbstain) {
                                    reasonText = "Большинство воздержалось. ";
                                } else if (notScoredPercent) {
                                    reasonText = "Ни один из вариантов не набрал необходимый процент. ";
                                }
                                reasonHtml = "<span style='text-decoration: underline;'>Голосование завершилось</span>. ";
                                break;
                        }

                        if (isCurrentSharerCanRestartVoting) {
                            votingProtocol.response.isCanRestartVoting = true;
                            votingProtocol.response.stateVotingText = reasonHtml + "<span style='color: #FF0000; font-weight: bold;'>" + reasonText + "</span>" +
                                    " Чтобы пройти процедуру голосования по этому вопросу снова, нажмите кнопку <span style='font-weight: bold;'>Переголосовать</span>. " +
                                    "Чтобы завершить собрание досрочно без каких-либо результатов, нажмите <span style='font-weight: bold;'>Прервать собрание</span>.";
                        } else {
                            votingProtocol.response.isCanRestartVoting = false;
                            votingProtocol.response.stateVotingText = reasonHtml + "<span style='color: #FF0000; font-weight: bold;'>" + reasonText + "</span>" +
                                    " Если создатель собрания перезапустит " + votingTypeName + " Вам придёт уведомление об этом.";
                        }
                    } else {
                        votingProtocol.response.stateVotingPaused = false;
                        batchVotingFinished = batchVoting.response.state == "FINISHED";
                    }
                    votingProtocol.batchVotingFinished = batchVotingFinished;
                    votingProtocol.batchVotingId = batchVoting.response.id;
                    // Сортируем варианты по результатам голосований
                    votingProtocol.response.votingItems.sort(function (a, b) {
                        if (parseFloat(a.votesPercent) < parseFloat(b.votesPercent)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    });
                    if (votingProtocol.response.result != null && votingProtocol.response.result.resultType == "VALID" &&
                            votingProtocol.response.state == "FINISHED") {

                        if (votingProtocol.response.parameters.votingType === 'INTERVIEW') {
                            for (var i = 0; i < votingProtocol.response.votingItems.length; i++) {
                                votingProtocol.response.votingItems[i].isWinner = false;
                            }
                        } else {
                            // Если установлен флаг - множество победителей,
                            // то считываем минимальный процент для победы и устанавливаем победителей
                            if (votingProtocol.response.parameters.multipleWinners) {
                                var percentForWin = votingProtocol.response.additionalData.percentForWin;
                                if (percentForWin == null || percentForWin == "" || percentForWin == "0") {
                                    percentForWin = 51;
                                } else {
                                    percentForWin = parseInt(percentForWin);
                                }

                                /*if (votingProtocol.response.parameters.votingType === 'CANDIDATE') {
                                    percentForWin = 1;
                                }*/

                                for (var i = 0; i < votingProtocol.response.votingItems.length; i++) {
                                    var votingItem = votingProtocol.response.votingItems[i];
                                    if (votingItem.votesPercent >= percentForWin) {
                                        votingItem.isWinner = true;
                                    } else {
                                        votingItem.isWinner = false;
                                    }
                                }
                            } else { // Иначе победитель - первый вариант голосования
                                for (var i = 0; i < votingProtocol.response.votingItems.length; i++) {
                                    var votingItem = votingProtocol.response.votingItems[i];
                                    if (i == 0) {
                                        votingItem.isWinner = true;
                                    } else {
                                        votingItem.isWinner = false;
                                    }
                                }
                            }
                        }
                    }

                    var votingTypeTemplate = votingProtocol.response.votingTypeTemplate;

                    var countVoters = voters.length;

                    // Ищем участников кто за что проголосовал
                    for (var itemIndex in votingProtocol.response.votingItems) {
                        var item = votingProtocol.response.votingItems[itemIndex];
                        item.countVotes = item.votes.length;
                        item.countVotesStringForm = stringForms(item.countVotes, "человек", "человека", "человек");

                        var votesPercent = isNaN(parseFloat(item.votesPercent)) ? 0 : parseFloat(item.votesPercent);
                        item.votesPercentFormatted = votesPercent.toFixed(2) + "%";
                        for (var voteIndex in item.votes) {
                            var vote = item.votes[voteIndex];
                            for (var voterIndex in voters) {
                                var voter = voters[voterIndex];
                                if (parseInt(voter.id) == parseInt(vote.ownerId)) {
                                    vote.owner = voter;
                                }
                            }
                        }
                    }
                    for (var itemIndex in votingProtocol.response.votingItems) {
                        var item = votingProtocol.response.votingItems[itemIndex];
                        item.countVoters = countVoters;
                    }

                    // Ищем следующее голосование в собрании
                    if (currentBatchVoting != null) {
                        var nextVotingId = -2;
                        for (var votingIndex in currentBatchVoting.response.votings) {
                            var votingFromBatch = currentBatchVoting.response.votings[votingIndex];
                            if (nextVotingId == -1) {
                                nextVotingId = votingFromBatch.id;
                                break;
                            }
                            if (votingFromBatch.id == votingProtocol.response.id) {
                                nextVotingId = -1;
                            }
                        }
                        // Если это последнее голосование в собрании
                        if (nextVotingId == -1) {
                            //window.location.reload();
                        } else if (nextVotingId != -2 && currentBatchVoting.response.state != "FINISHED") { // Есть ещё голосования
                            votingProtocol.response.nextVotingId = nextVotingId;
                            votingProtocol.response.nextVotingLink = baseLink + nextVotingId;
                            if (showNextVoteModal) {
                                $("#voteEndedModalLabel").html(Mustache.render(voteEndedDialogHeaderTemplate, {votingName: currentVoting.subject}));
                                $("#voteEndedModalContent").html(Mustache.render(voteEndedDialogContentTemplate, {nextVotingLink: votingProtocol.response.nextVotingLink}));

                                $("#voteEndedModal").modal("show");
                                RadomSound.playDefault();
                            }
                            //window.location.href = "/votingsystem/votingPage.html?votingId=" + nextVotingId + "&batchVotingId=" + batchVotingId;
                        }
                    }

                    votingProtocol.isClosedVoting = isClosedVoting;
                    $("#voting").append($(Mustache.render(votingProtocolTemplate, votingProtocol)));

                    if (currentVoting.parameters.votingType === 'INTERVIEW') $('#voting-caption').html('Результаты:');

                    var jqVotingTypeContent = $(Mustache.render(votingTypeTemplate, votingProtocol));
                    $("#votingProtocolContent").append(jqVotingTypeContent);

                    // Инициализация кликов по нопкам рестарта голосования
                    initVotingButtonsEvents();

                    // Инициализируем таблицы с проголосовавшими
                    for (var itemIndex in votingProtocol.response.votingItems) {
                        var item = votingProtocol.response.votingItems[itemIndex];

                        // Голосование закрытое
                        if (isClosedVoting) {
                            // Показываем плашку с информацией что голосование закрытое
                            createClosedVotingProtocol($("#votingProtocol" + item.id + "-grid"));
                        } else if (item.votes != null && item.votes.length > 0) {
                            // Загружаем таблицу с результатами
                            createVotingProtocolGrid("votingProtocol" + item.id + "-grid", item.id, votingId);
                        } else {
                            // Показываем плашку с пустым результатом
                            createEmptyProtocol($("#votingProtocol" + item.id + "-grid"));
                        }
                    }

                }, function (errorMessage) {
                    bootbox.alert(errorMessage);
                })
            }
        });
    }

    function moveVotersAtTheEnd(voterIds) {
        var votersContainer = $('div#voters_container');

        voterIds.forEach(function (value) {
            var div = $("#voter_div_" + value);
            div.detach();
            votersContainer.append(div);
        });
    }

    // Обновление данных страницы голосования
    function updateVotingData(showNextVoteLink) {
        getVotersWhoVotes(function (voterIds) {
            if ($("#votersListLabel").length > 0) {
                // Если проголосовали ещё не все
                if (voterIds.length < voters.length) {
                    $("#votersListLabel").text("Список участников голосования. Проголосовали " + voterIds.length + " из " + voters.length + ".");

                    for (var voterIndex in voters) {
                        var voterId = voters[voterIndex].id;
                        var found = false;

                        for (var index in voterIds) {
                            var voterIdWhoVote = voterIds[index];

                            if (voterId == voterIdWhoVote) {
                                found = true;
                                break;
                            }
                        }

                        var div = $("#voter_div_" + voterId);

                        div.removeClass("voterBlockIsVote");
                        div.removeClass("voterBlockIsNotVote");

                        if (found) {
                            div.addClass("voterBlockIsVote");
                            $(".hurryVoterBlock[voter_id=" + voterId + "]").hide();
                        } else {
                            div.addClass("voterBlockIsNotVote");

                            if (voterId != userId) {
                                $(".hurryVoterBlock[voter_id=" + voterId + "]").show();
                            }
                        }

                        moveVotersAtTheEnd(voterIds);
                    }
                } else {
                    if ((skipResults) && (nextVotingLink)) {
                        window.location.href = nextVotingLink;
                        return;
                    }
                    if (!protocolIsInited) { // Если все проголосовали
                        initVotingProtocol(showNextVoteLink == true);
                    }
                }
            } else if (protocolIsInited && voterIds.length < voters.length) {
                protocolIsInited = false;
                initVoting();
            }
        }, function (errorMessage) {
            bootbox.alert(errorMessage);
        });
    }

    // Инициализация страницы голосования
    function initVoting(showNextVotePopup) {
        var votingTemplate = $("#votingTemplate").html();

        // Шаблон для головсования За\Против\Воздержался
        var votingProContraAbstainSelectionTemplate = $("#votingProContraAbstainSelectionTemplate").html();
        Mustache.parse(voteEndedDialogContentTemplate);
        Mustache.parse(voteEndedDialogHeaderTemplate);
        // Шаблон выбора из списка кандидатов
        var votingCandidateSelectionTemplate = $("#votingCandidateSelectionTemplate").html();
        // Шаблон для интервью
        var votingInterviewTemplate = $("#votingInterviewTemplate").html();
        // Шаблон для голосования с одним вариантом
        var votingSingleSelectionTemplate = $("#votingSingleSelectionTemplate").html();
        // Шаблон для голосования с множеством вариантов
        var votingMultipleSelectionTemplate = $("#votingMultipleSelectionTemplate").html();

        Mustache.parse(votingTemplate);
        if ($("#voting").length > 0) {
            $("#voting").empty();

            getVoting(function (voting) {
                currentVoting = voting.response;

                if (voting.errorCode != 0) {
                    bootbox.alert(voting.message);
                    return false;
                }

                var tempNextVoting;

                for (var i in currentBatchVoting.response.votings) {
                    if ((!tempNextVoting || (currentBatchVoting.response.votings[i].index < tempNextVoting.index))
                            && (currentBatchVoting.response.votings[i].index > voting.response.index)) {

                        tempNextVoting = currentBatchVoting.response.votings[i];
                    }
                }

                if (tempNextVoting) {
                    nextVotingLink = baseLink + tempNextVoting.id;
                }

                //var startDate = createTimestampDate(votingStartDate);
                // - тайм зона пользователя + таймзона сервера
                //startDate.setTime(startDate.getTime() - startDate.getTimezoneOffset() * 60 * 1000 - timeZoneOffset);

                // Если проголосовали и голосование завершилось
                if (voting.response.state == "FINISHED" || voting.response.state == "PAUSED") {
                    if (skipResults && (nextVotingLink)) {
                        window.location.href = nextVotingLink;
                        return;
                    }

                    initVotingProtocol(showNextVotePopup == true);

                    if (currentVoting.parameters.votingType === 'INTERVIEW') $('#voting-caption').html('Результаты:');

                    return false;
                } else if (voting.response.state != "ACTIVE") {
                    $("#voting").append("<h4>Голосование ещё не началось.</h4>");
                    return false;
                }

                if (startDate.getTime() > nowDate.getTime()) {
                    $("#voting").append("<h4>Голосование ещё не началось. Дата начала голосования: " + startDate.format("dd.mm.yyyy HH:MM") + "</h4>");
                    return false;
                }

                // Инициализация голосования
                var templates = {
                    votingProContraAbstainSelectionTemplate: votingProContraAbstainSelectionTemplate,
                    votingCandidateSelectionTemplate: votingCandidateSelectionTemplate,
                    votingInterviewTemplate: votingInterviewTemplate,
                    votingSingleSelectionTemplate: votingSingleSelectionTemplate,
                    votingMultipleSelectionTemplate: votingMultipleSelectionTemplate
                };
                initVotingFromData(voting, templates, false);
                var votingTypeTemplate = voting.response.votingTypeTemplate;

                // Ищем выбранные элементы
                selectedVoteIds = [];
                if (voting.response.votingItems != null) {
                    for (var votingItemIndex in voting.response.votingItems) {
                        var votingItem = voting.response.votingItems[votingItemIndex];
                        if (votingItem != null && votingItem.votes != null) {
                            for (var voteIndex in votingItem.votes) {
                                var vote = votingItem.votes[voteIndex];
                                if (vote.ownerId == userId) {
                                    voting.response.isAllreadyVoting = true;
                                    votingItem.selectedVotingId = true;
                                    votingItem.comment = vote.comment;
                                    selectedVoteIds.push(vote.id);
                                }
                            }
                        }
                    }
                }

                // отрисовка кнопок голосования
                var buttonsContentJson = voting.response.additionalData.votingButtonsWithModalContents;
                var buttonsContentModel = null;
                if (buttonsContentJson != null) {
                    try {
                        buttonsContentModel = JSON.parse(buttonsContentJson);
                    } catch (e) {
                        console.log(e);
                    }
                }
                voting.response.buttonsContent = buttonsContentModel;
                voting.response.interview = (voting.response.parameters['votingType'] === "INTERVIEW");
                var jqVotingNode = $(Mustache.render(votingTemplate, voting));
                $("#voting").append(jqVotingNode);

                $(".hurryInVoting").radomTooltip({
                    position: "top",
                    container: "body",
                    title: "Оповестить участника о голосовании"
                });
                // скрываем кнопку "поторопить" у себя
                $(".hurryVoterBlock[voter_id=" + userId + "]").hide();

                var jqVotingTypeContent = $(Mustache.render(votingTypeTemplate, voting));
                $("#votingTypeContent").append(jqVotingTypeContent);

                // Инициализация окончания времени голосования
                initLeftTime(votingEndDate, $("#leftTimeEndOfVotingSpan"));
                $("#leftTimeEndOfVotingSpan").radomTooltip({
                    position: "top",
                    container: "body",
                    title: "Осталось времени до окончания голосования"
                });

                // Сохранить результаты голосования
                $("#saveVoting").click(function () {
                    var jqSelectedVotingItemsRows = $(".votingTable tr[selected_item=true]");
                    if ((jqSelectedVotingItemsRows.length == 0) && (!allowVote)){
                        jqSelectedVotingItemsRows = $(".votingTable tr[voting_item][is_abstain=false]").slice(0, 1);
                    }
                    if (jqSelectedVotingItemsRows.length == 0) {
                        bootbox.alert("Вы не выбрали вариант для голосования!");
                        return false;
                    }
                    var selectedItems = [];
                    jqSelectedVotingItemsRows.each(function () {
                        selectedItems.push($(this).attr("voting_item"));
                    });

                    var votes = [];
                    for (var votingItemIndex in selectedItems) {
                        var votingItemId = selectedItems[votingItemIndex];
                        var comment = null;
                        if ($(".votingItemComment[name=" + votingItemId + "_comment]").length > 0) {
                            comment = $(".votingItemComment[name=" + votingItemId + "_comment]").val();
                        }

                        votes.push({
                            votingItemId: parseInt(votingItemId),
                            ownerId: userId,
                            comment: comment,
                            parameters: {}
                        });
                    }
                    addVotes(votes, function (response) {
                        if (response.errorCode != 0) {
                            bootbox.alert(response.message);
                        } else {
                            votersWhoVotes[userId] = userId;
                            initVoting(true);
                        }
                    }, function (errorMessage) {
                        bootbox.alert(errorMessage);
                    });
                });
                // Выполнить отмену выбранных значений голосования
                $("#reVoting").click(function () {
                    var unvotedCount = 0;
                    for (var index in selectedVoteIds) {
                        var voteId = selectedVoteIds[index];
                        deleteVote(voteId, function (response) {
                            if (response.errorCode != 0) {
                                bootbox.alert(response.message);
                            } else {
                                delete votersWhoVotes[userId];
                                unvotedCount++;
                                if (unvotedCount == selectedVoteIds.length) {
                                    $(".votingItem").prop("disabled", false);
                                    $(".votingItem").prop("checked", false);
                                    $(".votingItemComment").prop("disabled", false);
                                    $(".addVoteItemContainer").show();
                                    $(".allReadyVoting").hide();
                                    $("#reVoting").hide();
                                    $("#saveVoting").show();
                                    initVoting();
                                }
                            }
                        }, function (errorMessage) {
                            bootbox.alert(errorMessage);
                        });
                    }
                });

                $("#addVoteItem").click(function () {
                    var voteItemText = $("#voteItemText").val();
                    if (voteItemText == null || voteItemText == "") {
                        bootbox.alert("Необходимо добавить описание варианта для голосования!");
                        return false;
                    }
                    addVotingItem(voteItemText, function (response) {
                        if (response.errorCode != 0) {
                            bootbox.alert(response.message);
                        } else {
                            initVoting();
                        }
                    }, function (errorMessage) {
                        bootbox.alert(errorMessage);
                    })
                });

                // Оповестить участника о голосовании
                $(".hurryInVoting").click(function () {
                    var voterId = $(this).parent().attr("voter_id");
                    hurryInVoting(voterId, function () {
                        bootbox.alert("Участник оповещён!");
                    });
                });

                // Текстовый блок с количеством выбранных вариантов
                var jqVotingForNonAbstainItemsText = $("#votingForNonAbstainItemsText");
                // Текстовый блок - воздержаться
                var jqVotingForAbstainItemsText = $("#votingForAbstainItemsText");

                // Обработчик кнопки - добавить голос
                $(".buttonAddVote").click(function () {
                    var jqRow = $(this).closest("tr");
                    var jqTable = $(this).closest("table");

                    // Если выбран варинат - воздержался, то лочим все остальные варианты
                    if (jqRow.attr("is_abstain") == "true") {
                        jqTable.find("tr").attr("selected_item", "false");
                        $(".buttonAddVote", jqTable).prop("disabled", true);
                        $(".buttonAddVote", jqTable).parent().show();
                        $(".buttonDeleteVote", jqTable).parent().hide();
                        $(".notAcceptVotingValue", jqTable).hide();
                        jqVotingForNonAbstainItemsText.hide();
                        jqVotingForAbstainItemsText.show();
                    } else {
                        jqVotingForNonAbstainItemsText.show();
                        jqVotingForAbstainItemsText.hide();
                    }

                    jqRow.attr("selected_item", "true");

                    var minSelectionCount = parseInt(jqTable.attr("min_selection_count"));
                    var maxSelectionCount = parseInt(jqTable.attr("max_selection_count"));
                    var countSelectedItems = jqTable.find("tr[selected_item=true][is_abstain=false]").length;

                    if (maxSelectionCount == countSelectedItems) {
                        $("tr[is_abstain=false] .buttonAddVote", jqTable).prop("disabled", true);
                    }

                    var isMultipleSelection = jqTable.attr("is-multiple-selection");

                    if (isMultipleSelection === "true") {
                        if ((countSelectedItems >= minSelectionCount) && ((maxSelectionCount > 0) && (countSelectedItems <= maxSelectionCount))) {
                            $("#saveVoting").removeAttr("disabled");
                        } else {
                            $("#saveVoting").attr("disabled", true);
                        }
                    }

                    $(".buttonDeleteVote", jqRow).parent().show();
                    $(".notAcceptVotingValue", jqRow).show();
                    $(this).parent().hide();
                    $("#selectedCountVotingItems").text(countSelectedItems);
                });

                // Обработчик кнопки - удалить голос
                $(".buttonDeleteVote").click(function () {
                    jqVotingForNonAbstainItemsText.show();
                    jqVotingForAbstainItemsText.hide();

                    var jqRow = $(this).closest("tr");
                    var jqTable = $(this).closest("table");
                    jqRow.attr("selected_item", "false");

                    $(".notAcceptVotingValue", jqRow).hide();
                    $(".buttonAddVote", jqTable).prop("disabled", false);
                    $(".buttonAddVote", jqRow).parent().show();
                    $(this).parent().hide();

                    var minSelectionCount = parseInt(jqTable.attr("min_selection_count"));
                    var maxSelectionCount = parseInt(jqTable.attr("max_selection_count"));
                    var countSelectedItems = jqTable.find("tr[selected_item=true][is_abstain=false]").length;
                    $("#selectedCountVotingItems").text(countSelectedItems);

                    var isMultipleSelection = jqTable.attr("is-multiple-selection");

                    if (isMultipleSelection === "true") {
                        if ((countSelectedItems >= minSelectionCount) && ((maxSelectionCount > 0) && (countSelectedItems <= maxSelectionCount))) {
                            $("#saveVoting").removeAttr("disabled");
                        } else {
                            $("#saveVoting").attr("disabled", true);
                        }
                    }
                });
                $(".buttonDeleteVariant").click(function () {
                 var votingItemId = $(this).attr("data-voting-item-id");
                 deleteVotingItem(votingItemId,votingId,function (response) {
                     if (response.errorCode != 0) {
                         bootbox.alert(response.message);
                     } else {
                        // votersWhoVotes[userId] = userId;
                         initVoting(true);
                     }
                 }, function (errorMessage) {
                     bootbox.alert(errorMessage);
                 });
                });

                initShowHideDescription();

                var jqTable = $("table.votingTable");
                var minSelectionCount = parseInt(jqTable.attr("min_selection_count"));
                var maxSelectionCount = parseInt(jqTable.attr("max_selection_count"));
                var countSelectedItems = jqTable.find("tr[selected_item=true][is_abstain=false]").length;
                var isMultipleSelection = jqTable.attr("is-multiple-selection");
                console.log(jqTable);

                if (isMultipleSelection === "true") {
                    if ((countSelectedItems >= minSelectionCount) && ((maxSelectionCount > 0) && (countSelectedItems <= maxSelectionCount))) {
                        $("#saveVoting").removeAttr("disabled");
                    } else {
                        $("#saveVoting").attr("disabled", true);
                    }
                } else $("#saveVoting").removeAttr("disabled");

                // Обработчик клика по кнопке с доп инфой
                $(".buttonText").click(function () {
                    var buttonText = $(this).text();
                    var foundButtonContent = null;
                    for (var index in voting.response.buttonsContent) {
                        var buttonContent = voting.response.buttonsContent[index];
                        if (buttonContent.buttonText == buttonText) {
                            foundButtonContent = buttonContent;
                            break;
                        }
                    }
                    if (foundButtonContent != null) {
                        $("#buttonTextModal").find("#buttonTextModalLabel").html(foundButtonContent.buttonText);
                        $("#buttonTextModal").find("#buttonTextModalContent").html(foundButtonContent.content);
                        $("#buttonTextModal").modal("show");
                    }
                });

                updateVotingData();

            }, function (message) {
                bootbox.alert(message);
            });
        }
    }
</script>
<div id="votingBatch"></div>
<div id="voting"></div>
<!-- Модальное окно для отображения побробностей собрания-->
<div class="modal fade" role="dialog" id="meetingTargets" aria-labelledby="meetingTargetsLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="meetingTargetsLabel">${batchVotingDescription}</h4>
            </div>
            <div class="modal-body">
                ${meetingTargets}
                <hr/>
                <p>${additionalMeetingTargets}</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<!-- Модальное окно для отображения доп. информации голосования-->
<div class="modal fade" role="dialog" id="buttonTextModal" aria-labelledby="buttonTextModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="buttonTextModalLabel"></h4>
            </div>
            <div class="modal-body" id="buttonTextModalContent" style="height: 45vh; overflow: auto;">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<div class="modal fade" role="dialog" id="voteEndedModal" aria-labelledby="voteEndedModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="voteEndedModalLabel"></h4>
            </div>
            <div class="modal-body" id="voteEndedModalContent">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>