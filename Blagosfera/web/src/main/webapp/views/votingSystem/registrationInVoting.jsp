<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<c:choose>
    <c:when test="${errorMessage != null}" >
        <h2>${errorMessage}</h2>
    </c:when>
    <c:otherwise>
        <style>
            .userBlock, .registeredUsers, .notRegisteredUsers {
                padding: 10px;
            }

            .currentUserBlock {
                display: inline-block;
                vertical-align: top;
            }

            .toogleButtons {
                margin-top: 10px;
                text-align: center;
            }

            .sharerLinkListItem {
                width: 48px; height: 48px;
            }

            .notRegisteredSharer, .registeredSharer {
                margin-top: 3px;
            }
        </style>
        <script type="text/javascript">
            var batchVotingId = '${param['batchVotingId']}';
            var voterId = '${sharer.id}';
            var registeredCount = ${countRegisteredVoters};
            var notRegisteredCount = ${countNotRegisteredVoters};
            var currentBatchVoting = null;
            var nowDate = createDate();
            var votersRegistrationEndDate = createIsoDate("${votersRegistrationEndDate}");
            // Время регистрации истекло
            var isRegistrationTimeIsElapsed = false;
            var isRegisteredGlobal = false;
            var visibleParticipantsCountDefault = 3;
            var useBiometricIdentificationInRegistration = ${useBiometricIdentificationInRegistration};

            // Зарегистрироваться
            function registrationInVoting(callback, errorCallback) {
                if (useBiometricIdentificationInRegistration) {
                    $.radomFingerJsonAjax({
                        url: "/voting/registerVoter?batchVotingId=" + batchVotingId + "&voterId=" + voterId,
                        //contentType: "application/json",
                        type: "get",
                        data: {},
                        successRequestMessage: "Регистрация успешна",
                        errorMessage: "Ошибка выполнения запроса. Попробуйте еще раз.",
                        successCallback: callback,
                        errorCallback: errorCallback
                    });
                } else {
                    var url = "/voting/registerVoter?batchVotingId=" + batchVotingId + "&voterId=" + voterId;
                    $.radomJsonGetWithWaiter(url, {}, callback, errorCallback);
                }
            }

            // Получить пакет голосований
            function getBatchVoting(callBack, errorCallBack) {
                $.ajax({
                    type : "get",
                    dataType : "json",
                    url : "/voting/getBatchVoting?batchVotingId=" + batchVotingId,
                    data : {},
                    success : function(response) {
                        callBack(response);
                    },
                    error : function(error) {
                        if (errorCallBack) errorCallBack(error);
                    }
                });
            }

            // Количество участников
            function loadCountVoters(callBack, withWaiter) {
                if (withWaiter) {
                    $.radomJsonPostWithWaiter("/votingsystem/getCountVoters.json", {batchVotingId : batchVotingId}, callBack);
                } else {
                    $.radomJsonPost("/votingsystem/getCountVoters.json", {batchVotingId : batchVotingId}, callBack);
                }
            }

            // Загрузить участников, которые зарегистрировались
            function loadRegisteredVoters(batchVotingId, firstIndex, count, callBack, errorCallBack) {
                $.ajax({
                    type : "post",
                    dataType : "json",
                    url : "/votingsystem/getRegisteredVoters.json",
                    data : {
                        batchVotingId : batchVotingId,
                        firstIndex : firstIndex,
                        count : count
                    },
                    success : function(response) {
                        if ($.isArray(response)) {
                            callBack(response);
                        } else if (response.result == "error") {
                            errorCallBack(response.message);
                        }
                    },
                    error : function(error) {
                        errorCallBack(error);
                    }
                });
            }

            // Загрузка первых 3х участников
            function loadFirstRegisteredVoters(callBack) {
                // Загружаем первые 3 элемента
                loadRegisteredVoters(batchVotingId, 0, visibleParticipantsCountDefault, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Загрузка остальных участников
            function loadLastRegisteredVoters(callBack) {
                loadRegisteredVoters(batchVotingId, visibleParticipantsCountDefault, null, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Загрузка всех участников
            function loadAllRegisteredVoters(callBack) {
                loadRegisteredVoters(batchVotingId, 0, null, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Загрузить участников, которые зарегистрировались
            function loadNotRegisteredVoters(batchVotingId, firstIndex, count, callBack, errorCallBack) {
                $.ajax({
                    type : "post",
                    dataType : "json",
                    url : "/votingsystem/getNotRegisteredVoters.json",
                    data : {
                        batchVotingId : batchVotingId,
                        firstIndex : firstIndex,
                        count : count
                    },
                    success : function(response) {
                        if ($.isArray(response)) {
                            callBack(response);
                        } else if (response.result == "error") {
                            errorCallBack(response.message);
                        }
                    },
                    error : function(error) {
                        errorCallBack(error);
                    }
                });
            };

            // Загрузка первых 3х участников
            function loadFirstNotRegisteredVoters(callBack) {
                // Загружаем первые 3 элемента
                loadNotRegisteredVoters(batchVotingId, 0, visibleParticipantsCountDefault, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Загрузка остальных участников
            function loadLastNotRegisteredVoters(callBack) {
                loadNotRegisteredVoters(batchVotingId, visibleParticipantsCountDefault, null, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Загрузка всех участников
            function loadAllNotRegisteredVoters(callBack) {
                loadNotRegisteredVoters(batchVotingId, 0, null, function(result){
                    callBack(result);
                }, function(errorMessage){
                    bootbox.alert(errorMessage);
                });
            };

            // Поторопить участника
            function hurryVoter(voterId, callBack){
                $.radomJsonPostWithWaiter(
                    "/votingsystem/hurryVoter.json",
                    {
                        batchVotingId : batchVotingId,
                        voterId : voterId
                    },
                    callBack
                );
            }

            // Инициализация оставшегося времени
            function initLeftTime(endDate, jqLeftTimeNode) {
                var timeFunction = function(){
                    var millisecondsLeft = endDate.getTime() - nowDate.getTime();
                    if (millisecondsLeft < 0) {
                        isRegistrationTimeIsElapsed = true;
                        jqLeftTimeNode.text("время вышло.");
                        // Скрыть кнопки "Поторопить"
                        $(".chatBlock").hide();
                    } else {
                        var seccondsLeft = parseInt(millisecondsLeft / 1000) % 60;
                        var minutesLeft = parseInt((millisecondsLeft / 1000) / 60) % 60;
                        var hoursLeft = parseInt(((millisecondsLeft / 1000) / 60) / 60) % 24;
                        var daysLeft = parseInt((((millisecondsLeft / 1000) / 60) / 60) / 24);
                        if (seccondsLeft < 10) {
                            seccondsLeft = "0" + seccondsLeft;
                        }
                        if (minutesLeft < 10) {
                            minutesLeft = "0" + minutesLeft;
                        }
                        var result = "";
                        var daysLeftInt = parseInt(daysLeft);
                        var hoursLeftInt = parseInt(hoursLeft);
                        var minutesLeftInt = parseInt(minutesLeft);
                        var seccondsLeftInt = parseInt(seccondsLeft);
                        if (daysLeftInt > 0) {
                            result = result + daysLeft + " " + stringForms(daysLeftInt, "день", "дня", "дней");
                        }
                        if (daysLeftInt > 0 || hoursLeftInt > 0) {
                            result = result + " " + hoursLeft + " час.";
                        }
                        if (daysLeftInt > 0 || hoursLeftInt > 0 || minutesLeftInt > 0) {
                            result = result + " " + minutesLeft + " мин.";
                        }
                        if (daysLeftInt > 0 || hoursLeftInt > 0 || minutesLeftInt > 0 || seccondsLeftInt > 0) {
                            result = result + " " + seccondsLeft + " сек.";
                        }
                        jqLeftTimeNode.text(result);
                    }
                    nowDate.setTime(nowDate.getTime() + 1000);
                };
                setInterval(function(){
                    timeFunction();
                }, 1000);
                timeFunction();
            }

            // Обновление данных на странице
            function updateDataInPage(isRegistered) {
                if (isRegistered == null) {
                    isRegistered = isRegisteredGlobal;
                }


                // Время регистрации закончилось
                if (isRegistrationTimeIsElapsed) {
                    // Скрыть кнопку "Регистрация"
                    $("#registrationStatusEndDate").show();
                    $("#registrationInVoting").hide();
                } else if (isRegistered) {
                    $("#registrationStatusInProccess").hide();
                    $("#registrationStatusSuccess").show();
                    $("#registrationInVoting").hide();
                } else {
                    $("#registrationStatusInProccess").show();
                    $("#registrationInVoting").show();
                }

                // Редиректим по окончанию регистрации всех участников
                if (notRegisteredCount == 0 && currentBatchVoting != null) {
                    window.location.href = "/votingsystem/votingPage.html?votingId=" + currentBatchVoting.response.votings[0].id;
                    return false;
                }

                if (registeredCount < (visibleParticipantsCountDefault + 1) && ($("#registeredSharersShow").is(":visible") || $("#registeredSharersHide").is(":visible"))) {
                    $("#registeredSharersShow").hide();
                    $("#registeredSharersHide").hide();
                }
                if (notRegisteredCount < (visibleParticipantsCountDefault + 1) && ($("#notRegisteredSharersHide").is(":visible") || $("#notRegisteredSharersShow").is(":visible"))) {
                    $("#notRegisteredSharersHide").hide();
                    $("#notRegisteredSharersShow").hide();
                }

                // Загружаем участников
                if (!registeredSharersFirstLoaded) {
                    loadFirstRegisteredVoters(function(voters){
                        drawVoters(registeredVoterTemplate, voters, $("#registeredUsers"), false);
                    });
                    registeredSharersFirstLoaded = true;
                    if (registeredCount > visibleParticipantsCountDefault) {
                        $("#registeredSharersShow").show();
                    }
                    // Если кнопка Скрыть видна, то обновляем все
                } else if ($("#registeredSharersHide").is(":visible")) {
                    // Загружаем всех участников
                    loadAllRegisteredVoters(function(voters){
                        drawVoters(registeredVoterTemplate, voters, $("#registeredUsers"), true);
                    });
                } else if ($("#registeredSharersShow").is(":visible")) {
                    loadFirstRegisteredVoters(function(voters){
                        drawVoters(registeredVoterTemplate, voters, $("#registeredUsers"), true);
                    });
                } else if (registeredCount > visibleParticipantsCountDefault) {
                    $("#registeredSharersShow").show();
                    loadFirstRegisteredVoters(function(voters){
                        drawVoters(registeredVoterTemplate, voters, $("#registeredUsers"), true);
                    });
                } else {
                    loadFirstRegisteredVoters(function(voters){
                        drawVoters(registeredVoterTemplate, voters, $("#registeredUsers"), true);
                    });
                }

                if (!notRegisteredSharersFirstLoaded) {
                    loadFirstNotRegisteredVoters(function(voters){
                        drawVoters(notRegisteredVoterTemplate, voters, $("#notRegisteredUsers"), false);
                    });
                    notRegisteredSharersFirstLoaded = true;
                    if (notRegisteredCount > visibleParticipantsCountDefault) {
                        $("#notRegisteredSharersShow").show();
                    }
                    // Если кнопка Скрыть видна, то обновляем все
                } else if ($("#notRegisteredSharersHide").is(":visible")) {
                    // Загружаем всех участников
                    loadAllNotRegisteredVoters(function(voters){
                        drawVoters(notRegisteredVoterTemplate, voters, $("#notRegisteredUsers"), true);
                    });
                } else if ($("#notRegisteredSharersShow").is(":visible")) {
                    loadFirstNotRegisteredVoters(function(voters){
                        drawVoters(notRegisteredVoterTemplate, voters, $("#notRegisteredUsers"), true);
                    });
                } else if (notRegisteredCount > visibleParticipantsCountDefault) {
                    $("#notRegisteredSharersShow").show();
                    loadFirstNotRegisteredVoters(function(voters){
                        drawVoters(notRegisteredVoterTemplate, voters, $("#notRegisteredUsers"), true);
                    });
                } else {
                    loadFirstNotRegisteredVoters(function(voters){
                        drawVoters(notRegisteredVoterTemplate, voters, $("#notRegisteredUsers"), true);
                    });
                }
            }

            // Инициализация получения количества зарегистрировавшихся и нет уастников
            function initVotersCountInfo() {
                radomStompClient.subscribeToUserQueue("voter_registered_" + batchVotingId, function(){
                    loadCountVoters(function(countVoters){
                        registeredCount = countVoters["countRegisteredVoters"];
                        notRegisteredCount = countVoters["countNotRegisteredVoters"];
                        updateDataInPage();
                    });
                });
                updateDataInPage();
            }

            // Инициализация кнопки "Поторопить"
            function initHurryVotersLinks(jqParentNode) {
                $(".hurryVoter", jqParentNode).click(function(){
                    var voterId = $(this).attr("voterId");
                    hurryVoter(voterId, function(response){
                        bootbox.alert("Участник оповещён!");
                    });
                });
            }

            function drawVoters(template, voters, jqParentNode, clear) {
                if (clear) {
                    jqParentNode.empty();
                }
                var selfExists = false;
                for (var voterIndex in voters) {
                    var voter = voters[voterIndex];
                    voter.avatar = Images.getResizeUrl(voter.avatar, "c48");
                    //voter.chatHref = "/chat/" + voter.ikp;
                    var jqVoter = $(Mustache.render(template, voter));
                    jqParentNode.append(jqVoter);
                    // Удаляем "Поторопить" самому себе
                    if ($(".chatBlock", jqVoter).length > 0 && voter.id == voterId) {
                        $(".chatBlock", jqVoter).remove();
                    }
                    selfExists = selfExists || voter.id == voterId;
                }
                initHurryVotersLinks(jqParentNode);


                // Выставляем количества зарегистрированных и не зарегистрированных участников
                if (jqParentNode.attr("id") == "registeredUsers") {
                    if (registeredCount > 0) {
                        if (selfExists) {
                            if (registeredCount > 1) {
                                var registeredCountForm = stringForms((registeredCount - 1), "человек", "человека", "человек");
                                var registeredForm = stringForms((registeredCount - 1), "зарегистрировался", "зарегистрировались", "зарегистрировались");
                                $("#registeredSharers").text("Уже " + registeredForm + " Вы и " + (registeredCount - 1) + " " + registeredCountForm);
                            } else {
                                $("#registeredSharers").text("Пока зарегистрировались только Вы");
                            }
                        } else {
                            var registeredCountForm = stringForms(registeredCount, "человек", "человека", "человек");
                            var registeredForm = stringForms(registeredCount, "зарегистрировался", "зарегистрировались", "зарегистрировались");
                            $("#registeredSharers").text("Уже " + registeredForm + " " + registeredCount + " " + registeredCountForm);
                        }
                    } else {
                        $("#registeredSharers").text("Пока никто не зарегистрировался, будьте первым");
                    }
                }

                if (jqParentNode.attr("id") == "notRegisteredUsers") {
                    if (notRegisteredCount > 0) {
                        if (selfExists && notRegisteredCount == 1) {
                            $("#notRegisteredSharers").text("Все зарегистрировались кроме Вас");
                        } else {
                            var notRegisteredCountForm = stringForms(notRegisteredCount, "человек", "человека", "человек");
                            var notRegisteredForm = stringForms(notRegisteredCount, "Ожидается", "Ожидаются", "Ожидаются");
                            $("#notRegisteredSharers").text(notRegisteredForm + " к регистрации " + notRegisteredCount + " " + notRegisteredCountForm);
                        }
                    } else {
                        $("#notRegisteredSharers").text("Все зарегистрировались");
                    }
                }

            }

            var registeredSharersLoaded = false;
            var notRegisteredSharersLoaded = false;
            var registeredVoterTemplate = null;
            var notRegisteredVoterTemplate = null;

            var registeredSharersFirstLoaded = false;
            var notRegisteredSharersFirstLoaded = false;
            $(document).ready(function() {
                radomStompClient.subscribeToUserQueue("new_notification", function(notification) {
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

                registeredVoterTemplate = $("#registeredVoterTemplate").html();
                Mustache.parse(registeredVoterTemplate);

                notRegisteredVoterTemplate = $("#notRegisteredVoterTemplate").html();
                Mustache.parse(notRegisteredVoterTemplate);

                getBatchVoting(function(batchVoting){
                    if (batchVoting.errorCode != 0) {
                        bootbox.alert(batchVoting.message);
                        return false;
                    }
                    currentBatchVoting = batchVoting;
                    var isRegistered = false;
                    for (var index in batchVoting.response.votings) {
                        var voting = batchVoting.response.votings[index];
                        isRegistered = $.inArray(parseInt(voterId), voting.parameters.votersAllowed) != -1;
                        if (!isRegistered) {
                            break;
                        }
                    }
                    isRegisteredGlobal = isRegistered;
                    if (isRegistered) {
                        $("#registrationStatusInProccess").hide();
                        $("#registrationStatusSuccess").show();
                    } else {
                        //$("#themeVoting").text("Тема собрания: " + batchVoting.response.subject);
                        $("#registrationInVoting").show();
                    }

                    // Оставшееся время до окончания регистрации
                    initLeftTime(votersRegistrationEndDate, $("#leftTimeToEndOfRegistrationSpan"));
                    // Инициализация количества участников
                    initVotersCountInfo();
                });

                $("#registrationInVoting").click(function(response){
                    registrationInVoting(function(response) {
                        if (response.errorCode != 0) {
                            bootbox.alert(response.message);
                            return false;
                        }
                        getBatchVoting(function(batchVoting) {
                            if (batchVoting.errorCode != 0) {
                                bootbox.alert(batchVoting.message);
                                return false;
                            }
                            var isRegistered = false;
                            for (var index in batchVoting.response.votings) {
                                var voting = batchVoting.response.votings[index];
                                isRegistered = $.inArray(parseInt(voterId), voting.parameters.votersAllowed) != -1;
                                if (!isRegistered) {
                                    break;
                                }
                            }
                            isRegisteredGlobal = isRegistered;

                            // Обновляем инфу по тем кто зарегался
                            loadCountVoters(function(countVoters){
                                registeredCount = countVoters["countRegisteredVoters"];
                                notRegisteredCount = countVoters["countNotRegisteredVoters"];
                                updateDataInPage(isRegisteredGlobal);
                            }, true);
                        });
                    }, function(errorMessage){
                        bootbox.alert(errorMessage);
                    });
                });

                // Инициализация кнопок Показать\Скрыть
                $("#registeredSharersShow").click(function(){
                    $(this).hide();
                    /*if (registeredSharersLoaded) {
                        $(".registeredSharer:hidden").show();
                        $("#registeredSharersHide").show();
                    } else {*/
                        // Отображение загрузки
                        $("#registeredVotersAjaxLoaderImage").show();
                        loadLastRegisteredVoters(function(response){
                            $("#registeredVotersAjaxLoaderImage").hide();
                            drawVoters(registeredVoterTemplate, response, $("#registeredUsers"), false);
                            $("#registeredSharersHide").show();
                            registeredSharersLoaded = true;
                        });
                    //}
                });
                $("#registeredSharersHide").click(function() {
                    $(this).hide();
                    var index = 0;
                    $(".registeredSharer:visible").each(function(){
                        if (index > (visibleParticipantsCountDefault - 1)) {
                            $(this).hide();
                        }
                        index++;
                    });
                    $("#registeredSharersShow").show();
                });


                $("#notRegisteredSharersShow").click(function(){
                    $(this).hide();
                    /*if (notRegisteredSharersLoaded) {
                        $(".notRegisteredSharer:hidden").show();
                        $("#notRegisteredSharersHide").show();
                    } else {*/
                        // Отображение загрузки
                        $("#notRegisteredVotersAjaxLoaderImage").show();
                        loadLastNotRegisteredVoters(function(response){
                            $("#notRegisteredVotersAjaxLoaderImage").hide();
                            drawVoters(notRegisteredVoterTemplate, response, $("#notRegisteredUsers"), false);
                            $("#notRegisteredSharersHide").show();
                            notRegisteredSharersLoaded = true;
                        }, function(errorMessage){
                            bootbox.alert(errorMessage);
                        });
                    //}
                });
                $("#notRegisteredSharersHide").click(function() {
                    $(this).hide();
                    var index = 0;
                    $(".notRegisteredSharer:visible").each(function(){
                        if (index > (visibleParticipantsCountDefault - 1)) {
                            $(this).hide();
                        }
                        index++;
                    });
                    $("#notRegisteredSharersShow").show();
                });

                $("#batchVotingDialog").click(function(){
                    var dialogId = $(this).attr("dialog_id");
                    var dialogName = $(this).attr("dialog_name");
                    ChatView.showPopupChatDialog(dialogId, dialogName);
                });
            });
        </script>
        <h1><b>Тема:</b> ${subject}</h1>
        <hr/>
        <p>
            ${description}
        </p>
        <button type="button" class="btn btn-primary" onclick="$('#meetingTargets').modal('show');" >${batchVotingDescription}</button>
        <c:if test="${dialog != null}" >
            <button type="button" class="btn btn-primary" id="batchVotingDialog" dialog_id="${dialog.id}" dialog_name="${dialog.name}">Чат собрания</button>
        </c:if>
        <h3>Регистрация участников собрания</h3>
        <p>${registrationCommonDescription}</p>
        <h3 id="leftTimeToEndOfRegistration">До завершения регистрации в собрании осталось: <span id="leftTimeToEndOfRegistrationSpan"></span></h3>
        <div class="panel panel-default userBlock">
            <div class="currentUserBlock">
                <a href="/sharer/${currentSharer.ikp}">
                    <img data-src="holder.js/90x/90" alt="90x90"
                         src="${radom:resizeImage(currentSharer.avatar, "c140")}" data-holder-rendered="true"
                         style="width: 140px; height : 140px;" class="media-object img-thumbnail tooltiped-avatar"
                         data-sharer-ikp="${currentSharer.ikp}" data-placement="left">
                </a>
            </div>
            <div class="currentUserBlock" style="width: 470px;">
                <h4 style="margin-top: 0px;">${currentSharer.name}</h4>
                <p id="registrationStatusInProccess" style="display: none;">${registrationSharerText}</p>
                <p id="registrationStatusSuccess" style="display: none;">
                    Вы уже зарегистрировались в собрании.<br/>
                    Вы получите уведомление о начале собрания.
                </p>
                <p id="registrationStatusEndDate" style="display: none;">
                    Время регистрации вышло.
                </p>
            </div>
            <div class="currentUserBlock" style="width: 300px; text-align: center;">
                <button type="button" class="btn btn-primary" id="registrationInVoting" style="display: none; margin-top: 52px;" >Зарегистрироваться</button>
            </div>
        </div>
        <hr/>
        <h3 id="registeredSharers"></h3>
        <div class="panel panel-default registeredUsers">
            <div id="registeredUsers"></div>
            <div class="toogleButtons">
                <div>
                    <img src="/i/search-ajax-loader.gif" id="registeredVotersAjaxLoaderImage" style="display: none;" />
                </div>
                <button type="button" class="btn btn-primary" id="registeredSharersShow" style="display: none;" >Развернуть</button>
                <button type="button" class="btn btn-primary" id="registeredSharersHide" style="display: none;" >Свернуть</button>
            </div>
        </div>
        <hr/>
        <h3 id="notRegisteredSharers"></h3>
        <div class="panel panel-default notRegisteredUsers">
            <div id="notRegisteredUsers"></div>
            <div class="toogleButtons">
                <div>
                    <img src="/i/search-ajax-loader.gif" id="notRegisteredVotersAjaxLoaderImage" style="display: none;" />
                </div>
                <button type="button" class="btn btn-primary" id="notRegisteredSharersShow" style="display: none;" >Развернуть</button>
                <button type="button" class="btn btn-primary" id="notRegisteredSharersHide" style="display: none;" >Свернуть</button>
            </div>
        </div>

        <script id="registeredVoterTemplate" type="x-tmpl-mustache">
            <div class="registeredSharer" id="registered{{ikp}}">
                <div class="currentUserBlock">
                    <a href="/sharer/{{ikp}}">
                        <img data-src="holder.js/90x/90" alt="90x90"
                             src="{{avatar}}" data-holder-rendered="true"
                             class="sharerLinkListItem media-object img-thumbnail tooltiped-avatar"
                             data-sharer-ikp="{{ikp}}" data-placement="left">
                    </a>
                </div>
                <div class="currentUserBlock">
                    <a href="/sharer/{{ikp}}">{{fullName}}</a>
                </div>
            </div>
        </script>
        <script id="notRegisteredVoterTemplate" type="x-tmpl-mustache">
            <div class="notRegisteredSharer" id="notRegistered{{ikp}}">
                <div class="currentUserBlock">
                    <a href="/sharer/{{ikp}}">
                        <img data-src="holder.js/90x/90" alt="90x90"
                             src="{{avatar}}" data-holder-rendered="true"
                             class="sharerLinkListItem media-object img-thumbnail tooltiped-avatar"
                             data-sharer-ikp="{{ikp}}" data-placement="left">
                    </a>
                </div>
                <div class="currentUserBlock">
                    <a href="/sharer/{{ikp}}">{{fullName}}</a>
                </div>
                <div class="currentUserBlock chatBlock">
                    <a class="btn btn-default hurryVoter" voterId="{{id}}" href="javascript:void(0)" >Поторопить</a>
                </div>
            </div>
        </script>
        <!-- Модальное окно для отображения целей и задач КУч-->
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
        </div><!-- /.modal -->

    </c:otherwise>
</c:choose>