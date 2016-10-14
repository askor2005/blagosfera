(function ($) {
    var waiterModalTemplate =
        '<div class="modal fade" role="dialog" id="radomAjaxWaiterWindow" aria-labelledby="radomAjaxWaiterTextLabel" aria-hidden="true">' +
        '<div class="modal-dialog modal-lg" style="width: 500px; height: 200px; position: absolute; top: 50%; left: 50%; margin-top: -100px; margin-left: -250px;">' +
        '<div class="modal-content">' +
        '<div class="modal-header">' +
        '<button type="button" class="close" id="closeAjaxButton" data-dismiss="modal" style="display: none;">&times;</button>' +
        '<h4 class="modal-title" id="radomAjaxWaiterTextLabel">Запрос выполняется</h4>' +
        '</div>' +
        '<div class="modal-body">' +
        '<div style="text-align: center;" id="ajaxWaiterImgBlock">' +
        '<img src="/i/search-ajax-loader.gif" />' +
        '</div>' +
        '<div id="ajaxResultBlock" style="display: none;">' +
        '</div>' +
        '<div id="ajaxWaiterMessageBlock">' +
        '</div>' +
        '</div>' +
        '<div class="modal-footer">' +
        '<button type="button" class="btn btn-primary" data-dismiss="modal" id="ajaxWaiterCloseButton" style="display: none;">Ок</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';

    $.extend({
        radomJsonPost: function (url, data, callback, errorCallBack, params) {
            var defaultParameters = {
                type: "post",
                dataType: "json",
                url: url,
                data: data,
                success: function (response) {
                    if (response.result == "error") {
                        if (errorCallBack != null) {
                            errorCallBack(response);
                        } else {
                            if (response.message) {
                                bootbox.alert(response.message);
                            } else {
                                console.log("ajax response error");
                            }
                        }
                    } else {
                        // Если есть редакторы на странице, то при успешном сохранении данных, сбрасываем временные данные
                        if (typeof(tinymce) != "undefined" && tinymce != null && tinymce.clearTempData != null) {
                            tinymce.clearTempData();
                        }
                        if (callback) {
                            callback(response);
                        }

                        var event = "";

                        switch (url.split("?")[0]) {
                            case "/contacts/add.json":
                                event = "contact.add";
                                break;
                            case "/contacts/delete.json":
                                event = "contact.delete";
                                break;
                            default:
                                break;
                        }

                        if (event) {
                            $(radomEventsManager).trigger(event, response);
                        }

                    }
                },
                error: function () {
                    if (errorCallBack != null) {
                        errorCallBack("ajax error");
                    }
                    console.log("ajax error");
                }
            };
            var resultParams = $.extend({}, defaultParameters, params);
            $.ajax(resultParams);
        },
        radomJsonWithWaiter: function (url, data, callback, errorCallBack, params, method) {

            $("#radomAjaxWaiterWindow").remove();
            var jqWaiterModalNode = $(waiterModalTemplate);

            var handleCallBack = function (response) {
                // Ишем нужные параметры с сервера для построения дополнительного контента в инф. модальном окне

                var neededParameters = null;
                var content = null;
                if (params != null && params.responseParameters != null && params.responseParameters[response.responseType] != null) {
                    neededParameters = params.responseParameters[response.responseType].neededParameters;
                    content = params.responseParameters[response.responseType].content;
                } else if (params != null) {
                    neededParameters = params.neededParameters;
                    content = params.content;
                }

                var foundAllParametersInResponse = false;
                if (params != null && neededParameters != null) {
                    foundAllParametersInResponse = true;
                    for (var index in neededParameters) {
                        var neededParameterName = neededParameters[index];
                        if (response[neededParameterName] == null) {
                            foundAllParametersInResponse = false;
                            break;
                        }
                    }
                }

                // Если есть параметры запроса
                if (params != null && content != null && foundAllParametersInResponse) {
                    for (var paramName in response) {
                        var paramValue = response[paramName];
                        var parts = content.split("{" + paramName + "}");
                        content = parts.join(paramValue);
                        //content = content.replace("{" + paramName + "}", paramValue);
                    }
                    $("#ajaxResultBlock").html(content);
                    $("#ajaxWaiterCloseButton").hide();
                    $("#ajaxWaiterImgBlock").hide();
                    $("#radomAjaxWaiterTextLabel").text("Запрос выполнен успешно!");
                    $("#ajaxResultBlock").show();
                    $("#closeAjaxButton").show();
                }

                if (response.redirectUrl != null) { // Если в ответе от севрера есть урл редиректа, то переходим по этому урлу
                    window.location.href = response.redirectUrl;
                } else if (callback != null) {
                    if (params == null || !foundAllParametersInResponse) {
                        jqWaiterModalNode.modal("hide");
                    }
                    callback(response);
                } else {
                    if (params == null || !foundAllParametersInResponse) {
                        $("#ajaxWaiterCloseButton").show();
                    }
                    $("#ajaxWaiterImgBlock").hide();
                    $("#radomAjaxWaiterTextLabel").text("Запрос выполнен успешно!");
                    $("#ajaxResultBlock").show();
                    $("#closeAjaxButton").show();
                }
            };
            var handleErrorCallBack = function (response) {
                if (errorCallBack != null) {
                    jqWaiterModalNode.modal("hide");
                    errorCallBack(response);
                } else {
                    var message = "";
                    if (response != null) {
                        message = response.result == "error" && response.message != null ? response.message : response;
                    }

                    $("#ajaxWaiterCloseButton").show();
                    $("#ajaxWaiterImgBlock").hide();
                    $("#radomAjaxWaiterTextLabel").text("Во время выполнения запроса произошла ошибка");
                    $("#ajaxWaiterMessageBlock").text(message);
                    $("#ajaxWaiterMessageBlock").show();
                }
            };
            jqWaiterModalNode.modal({backdrop: 'static', keyboard: false});
            $("#ajaxWaiterCloseButton").hide();
            $("#ajaxWaiterImgBlock").show();
            $("#radomAjaxWaiterTextLabel").text("Запрос выполняется");
            $("#ajaxWaiterMessageBlock").hide();

            // Дожидаемся открытия окна с вейтером
            jqWaiterModalNode.on('shown.bs.modal', function () {
                method(url, data, handleCallBack, handleErrorCallBack, params);
            });
        },
        radomJsonPostWithWaiter: function (url, data, callback, errorCallBack, params) {
            this.radomJsonWithWaiter(url, data, callback, errorCallBack, params, this.radomJsonPost);
        },

        radomJsonGet: function (url, data, callback, errorCallback) {
            $.ajax({
                type: "get",
                dataType: "json",
                url: url,
                data: data,
                success: function (response) {
                    if (typeof response === 'string') response = JSON.parse(response);

                    if (response.result == "error") {
                        if (errorCallback != undefined)
                            errorCallback(response);
                        else if (response.message) {
                            bootbox.alert(response.message);
                        } else {
                            console.log("ajax response error");
                        }
                    } else {
                        if (callback) {
                            callback(response);
                        }
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });

        },
        radomJsonGetWithWaiter: function (url, data, callback, errorCallBack, params) {
            this.radomJsonWithWaiter(url, data, callback, errorCallBack, params, this.radomJsonGet);
        }
    });
})(jQuery);

(function ($) {
    $.extend({
        radomUpload: function (inputName, url, extensions, callback, data) {
            var $form = $("body").find("form#radom-upload-form");
            var $input = null;
            if ($form.length == 0) {
                $form = $("<form id='radom-upload-form' enctype='multipart/form-data'></form>");
                $("body").append($form);
                $form.hide();
                $input = $("<input type='file' />");
                $form.html($input);
                $input.change(function () {
                    $form.submit();
                });

                $form.submit(function (e) {

                    e.preventDefault();

                    var $input = $form.find("input[type=file]");
                    var path = $input.val();
                    if (path == "") {
                        return;
                    }

                    var extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
                    var allowedExtensions = $form.data("extensions");
                    var url = $form.data("url");

                    if (allowedExtensions && allowedExtensions.length > 0 && $.inArray(extension, allowedExtensions) < 0) {
                        bootbox.alert("Разрешена загрузка файлов с расширениями " + allowedExtensions.join(", "));
                        $input.val("");
                        return;
                    }

                    var formData = new FormData($(this)[0]);

                    if (data) {
                        $.each(data, function (key, value) {
                            formData.append(key, value);
                        });
                    }

                    $.ajax({
                        url: url,
                        type: 'POST',
                        data: formData,
                        async: false,
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (response) {
                            if (response.result == "error") {
                                if (response.message) {
                                    bootbox.alert(response.message);
                                } else {
                                    bootbox.alert("Ошибка загрузки");
                                }
                            } else {
                                if (callback) {
                                    callback(response);
                                }
                            }
                            $input.val("");
                        },
                        error: function () {
                            bootbox.alert("Ошибка загрузки");
                            $input.val("");
                        }
                    });

                });
            }
            $input = $form.find("input[type=file]");
            $form.data("extensions", extensions);
            $form.data("url", url);
            $input.attr("name", inputName);
            $input.click();

        }
    });
})(jQuery);


(function ($) {
    $.extend({
        radomUploadDialog: function (params) {
            var $dialog = $("div#upload-dialog");
            $dialog.find("h4").html(params.title);
            $dialog.find("p#description").html(params.description);
            if (params.extensions) {
                $dialog.find("span#extensions").html(params.extensions.join(", ")).parent().show();
            } else {
                $dialog.find("span#extensions").html("").parent().hide();
            }
            var uploadParams = {};
            $dialog.find("button#upload-button").off("click").on("click", function () {
                $.radomUpload(params.inputName, params.url, params.extensions, function (response) {
                    if (params.callback) {
                        params.callback(response);
                    }
                    if (params.hideOnSuccess) {
                        $dialog.modal("hide");
                    }
                }, params.data);
            });
            $dialog.modal("show");
        }
    });
})(jQuery);

/*RegistratorTablet = {
    scanFinger: function(callbackName) {
        console.log('scanFinger');
        eval(callbackName)(0);
    },
    getToken: function(ikp, requestId, callbackName) {
        console.log('getToken', ikp, requestId);
        eval(callbackName)('toooookeeeeeeen');
    }
};*/

(function ($) {
    $.extend({
        radomFingerJsonAjax: function (params) {
            var async = !params.isSync;

            if (typeof RegistratorTablet === 'undefined') {
                params.identificationMode = "ras";
            } else {
                params.identificationMode = "tablet";
            }

            params.finished = false;
            params.address = $.cookie("DEFAULT_SERVICE");

            if (!params.address) {
                params.address = "localhost";
            }

            var progressStartTimeout;
            var progressStopTimeout;

            getModal().find("button#retry-button").off("click").on("click", function () {
                hideButtons();
                hideRemoteServiceGroup();
                checkVerified();
            });

            getModal().find("button#remote-service-button").off("click").on("click", function () {
                var remoteServiceAddress = $("input#remote-service-address").val();
                if (!remoteServiceAddress) {
                    bootbox.alert("Необходимо ввести адрес сервера авторизации");
                } else {
                    $.cookie("DEFAULT_SERVICE", remoteServiceAddress, {path: "/", expires: 7});
                    params.address = remoteServiceAddress;
                    hideButtons();
                    hideRemoteServiceGroup();
                    getInfo();
                }
            });

            getModal().find("button#local-service-button").off("click").on("click", function () {
                //$.removeCookie("DEFAULT_SERVICE");
                $.cookie("DEFAULT_SERVICE", 'localhost', {path: "/", expires: 7});
                params.address = "localhost";
                getInfo();
            });

            getModal().find("button#stop-get-info-button").off("click").on("click", function () {
                stopGetInfo();
            });

            getModal().find("button#stop-read-finger-button").off("click").on("click", function () {
                stopReadFinger();
            });

            getModal().find("button#close-button").off("click").on("click", function () {
                if (!params.finished && params.abortCallback) {
                    params.abortCallback();
                }

                stopProgress();
                hideModal();
            });

            getModal().find("button#stop-sms-verification-button").off("click").on("click", function () {
                stopSmsVerification();
            });

            function stopSmsVerification() {
                if (!params.finished && params.abortCallback) {
                    params.abortCallback();
                }

                stopProgress();
                hideModal();
            }

            var verifying = false;

            getModal().find("button#sms-code-confirm").off("click").on("click", function () {
                if (verifying) return false;
                var code = $('#sms-code-input').val();
                if (!code) return false;

                verifying = true;
                stopProgress();

                $('#sms-code-input').attr('disabled', true);
                $('button#sms-code-confirm').attr('disabled', true);

                $.ajax({
                    url: "/finger/verifytoken.json",
                    async: async,
                    type: "post",
                    dataType: "json",
                    data: {
                        i: params.ikp,
                        r: params.requestId,
                        c: code
                    },
                    success: function (response) {
                        verifying = false;
                        if (response.token) {
                            doAction(response.token);
                        } else {
                            if (response.message) {
                                setModalAlert(response.message, "danger");
                            } else {
                                setModalAlert("Вы ввели неверный код подтверждения.", "danger");
                            }
                        }
                    },
                    error: function () {
                        verifying = false;
                        setModalAlert("Сервис проверки отпечатков недоступен. Повторите попытку позже или обратитесь к администратору.", "danger");
                    }
                });
            });

            function isLocal(address) {
                return address == "127.0.0.1" || address == "localhost";
            }

            function getFullAddress(address) {
                return "https://" + address + ":36123";
            }

            function getModal() {
                return $("div#finger-dialog");
            }

            function setModalAlert(text, cls) {
                getModal().find("div.alert").removeClass("alert-success").removeClass("alert-info").removeClass("alert-warning").removeClass("alert-danger").addClass("alert-" + cls).html(text);
            }

            function showModal() {
                getModal().modal("show");
            }

            function hideModal() {
                getModal().modal("hide");
            }

            function showButtons(hideRetry) {
                getModal().find("button#close-button").removeAttr("disabled");
                getModal().find("button#retry-button").removeAttr("disabled");

                getModal().find("button#close-button").show();
                getModal().find("button#retry-button").show();

                if (hideRetry) {
                    getModal().find("button#retry-button").hide();
                } else {
                    getModal().find("button#retry-button").show();
                }

                getModal().find("button#stop-sms-verification-button").hide();
            }

            function hideButtons() {
                getModal().find("button#close-button").attr("disabled", "disabled");
                getModal().find("button#retry-button").attr("disabled", "disabled");

                getModal().find("button#close-button").hide();
                getModal().find("button#retry-button").hide();
            }

            function showRemoteServiceGroup() {
                getModal().find("input#remote-service-address").val($.cookie("DEFAULT_SERVICE"));
                getModal().find("div#remote-service-group").slideDown();
            }

            function hideRemoteServiceGroup() {
                getModal().find("div#remote-service-group").slideUp();
            }

            function showStopGetInfoGroup() {
                getModal().find("#stop-get-info-button").show();
            }

            function hideStopGetInfoGroup() {
                getModal().find("#stop-get-info-button").hide();
            }

            function showStopReadFingerGroup() {
                getModal().find("#stop-read-finger-button").show();
            }

            function showSmsVerificationUI() {
                getModal().find("#stop-sms-verification-button").show();
                getModal().find("#sms-verification-group").show();
                getModal().find(".progress").hide();
            }

            function hideSmsVerificationUI() {
                getModal().find("#stop-sms-verification-button").hide();
                getModal().find("#sms-verification-group").hide();
                getModal().find(".progress").show();
            }

            function hideStopReadFingerGroup() {
                getModal().find("#stop-read-finger-button").hide();
            }

            function checkVerified() {
                $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "/sharer/me.json",
                    async: async,
                    data: {
                        system_option: params.systemOption
                    },
                    success: function (response) {
                        if (response.verified) {
                            params.user = response;

                            if (response.identificationMode === 'sms') {
                                params.identificationMode = "sms";
                            }

                            if (params.identificationMode === 'sms') {
                                $('.fingerprint-mode').hide();
                            } else {
                                $('.fingerprint-mode').show();
                            }

                            showModal();

                            if (response.identificationRequired) {
                                if (params.identificationMode === 'ras') {
                                    getInfo();
                                } else if ((params.identificationMode === 'tablet') || (params.identificationMode === 'sms')) {
                                    initToken();
                                }
                            } else {
                                doAction('SISKI!!!');
                            }
                        } else {
                            bootbox.alert("Требуется подтверждение действия при помощи отпечатка пальца. Данный механизм доступен только идентифицированным пользователям.");
                        }
                    },
                    error: function () {
                        bootbox.alert("Ошибка связи с сервером, повторите попытку позже");
                    }
                });
            }

            function getInfo() {
                hideButtons();
                hideRemoteServiceGroup();
                showStopGetInfoGroup();

                var infoMessage = isLocal(params.address)
                    ? "Подключение к локальному серверу авторизации Благосфера"
                    : "Подключение к серверу авторизации Благосфера " + params.address;

                infoMessage += "<i class='fa fa-spinner faa-spin animated' style='float : right; font-size : 22px; position : absolute; top : 15px; right : 15px;'></i>"

                setModalAlert(infoMessage, "info");

                var errorHandler = function (shouldShowRemoteServiceGroup, message) {
                    var errorMessage = message ? message : isLocal(params.address)
                        ? "Локальный сервер авторизации Благосфера не найден. Запустите его если он имеется на Вашем компьютере, или загрузите, следуя <a class='alert-link' href='/finger/instruction'>инструкции</a>. Также Вы можете указать адрес сервера авторизации в Вашей локальной сети используя поле ниже."
                        : "Сервер авторизации Благосфера по указанному адресу не найден.";

                    setModalAlert(errorMessage, "danger");
                    showButtons(true);
                    if (shouldShowRemoteServiceGroup) showRemoteServiceGroup();
                    $.removeCookie("DEFAULT_SERVICE");
                };

                params.getInfoAjax = $.ajax({
                    crossDomain: true,
                    url: getFullAddress(params.address) + "/info.sma",
                    async: async,
                    type: "get",
                    dataType: "json",
                    data: {
                        jsonMode: "yes"
                    },
                    success: function (response) {
                        if (response.version) {
                            checkVersion(response.version);
                        } else {
                            errorHandler(true);
                        }
                    },
                    error: function () {
                        errorHandler(true);
                    },
                    complete: function () {
                        hideStopGetInfoGroup();
                    }
                });

                function checkVersion(version) {
                    $.ajax({
                        crossDomain: false,
                        url: "/ras/version/min",
                        async: async,
                        type: "get",
                        dataType: "text",
                        data: {},
                        success: function (response) {
                            var versionSupported = false;
                            var minVersion = response.split('.');
                            var usedVersion = version.split('.');

                            if ((minVersion.length == usedVersion.length) && (minVersion.length == 3)) {
                                if (parseInt(minVersion[0]) > parseInt(usedVersion[0])) {
                                    // wrong major version
                                } else if (parseInt(minVersion[0]) < parseInt(usedVersion[0])) {
                                    versionSupported = true;
                                } else if (parseInt(minVersion[1]) > parseInt(usedVersion[1])) {
                                    // wrong middle version
                                } else if (parseInt(minVersion[1]) < parseInt(usedVersion[1])) {
                                    versionSupported = true;
                                } else if (parseInt(minVersion[2]) > parseInt(usedVersion[2])) {
                                    // wrong minor version
                                } else if (parseInt(minVersion[2]) <= parseInt(usedVersion[2])) {
                                    versionSupported = true;
                                }
                            }

                            if (versionSupported) {
                                initToken();
                            } else {
                                errorHandler(false, 'Вы используете Сервер авторизации "БЛАГОСФЕРА" версии ' + version + '. Минимальная поддерживаемая версия ' + response + '. Пожалуйста скачайте новую версию по <a target="_blank" href="/ras/download">данной ссылке</a>');
                            }
                        },
                        error: function () {
                            errorHandler(true);
                        },
                        complete: function () {
                            hideStopGetInfoGroup();
                        }
                    });
                }
            }

            function stopGetInfo() {
                stopProgress();

                if (params.getInfoAjax) {
                    params.getInfoAjax.abort();
                }

                setModalAlert("Укажите адрес сервера авторизации Благосфера для подключения или подключитесь к локальному серверу авторизации Благосфера", "info");

                showButtons(true);
                showRemoteServiceGroup();
            }

            function readFinger(ikp, requestId, finger) {
                function getFingerTitle(number, withHand) {
                    if (typeof number == "string") {
                        number = parseInt(number);
                    }

                    switch (number) {
                        case 1 :
                            return "Мизинец" + (withHand ? " левой руки" : "");
                        case 2 :
                            return "Безымянный" + (withHand ? " палец левой руки" : "");
                        case 3 :
                            return "Средний" + (withHand ? " палец левой руки" : "");
                        case 4 :
                            return "Указательный" + (withHand ? " палец левой руки" : "");
                        case 5 :
                            return "Большой" + (withHand ? " палец левой руки" : "");
                        case 6 :
                            return "Большой" + (withHand ? " палец правой руки" : "");
                        case 7 :
                            return "Указательный" + (withHand ? " палец правой руки" : "");
                        case 8 :
                            return "Средний" + (withHand ? " палец правой руки" : "");
                        case 9 :
                            return "Безымянный" + (withHand ? " палец правой руки" : "");
                        case 10 :
                            return "Мизинец" + (withHand ? " правой руки" : "");
                        default :
                            return "";
                    }
                }

                var infoMessage = params.getScanDescription
                    ? (params.getScanDescription(params.address, getFingerTitle(finger, true)))
                    : (isLocal(params.address)
                    ? "<b>" + params.user.shortName + "</b>, просканируйте <b style='text-decoration : underline;'>" + getFingerTitle(finger, true) + "</b> на сканере подключенном к локальному серверу авторизации Благосфера"
                    : "<b>" + params.user.shortName + "</b>, просканируйте <b style='text-decoration : underline;'>" + getFingerTitle(finger, true) + "</b> на сканере подключенном к серверу авторизации Благосфера " + params.address);

                setModalAlert(Mustache.render($("script#dialog-message-template").html(), {
                    avatar: params.avatar ? params.avatar : Images.getResizeUrl(params.user.avatar, "c119"),
                    text: infoMessage
                }), "info");

                var timeout = SystemSettings.fingerScanTimeout;

                startProgress(timeout, function (p, max, min) {
                    var secondsLeft = max - p;
                    return 'Осталось ' + secondsLeft + ' ' + RadomUtils.getDeclension(secondsLeft, 'секунда', 'секунды', 'секунд');
                });

                showStopReadFingerGroup();

                params.scanAjax = $.ajax({
                    crossDomain: true,
                    url: getFullAddress(params.address) + "/scan.sma",
                    async: async,
                    type: "get",
                    dataType: "json",
                    data: {
                        combined: "no",
                        jsonMode: "yes",
                        timeout: timeout
                    },
                    success: function (response) {
                        stopProgress();

                        if (response.result === "success") {
                            getToken(ikp, requestId);
                        } else {
                            if (response.error_code === 1001) {
                                setModalAlert("Прежде чем выполнять сканирование отпечатка пальца, Вы должны подключить биометрический сенсор.", "danger");
                            } else {
                                setModalAlert("Ошибка сканирования", "danger");
                            }

                            showRemoteServiceGroup();
                            showButtons();
                        }
                    },
                    error: function () {
                        stopProgress();

                        setModalAlert("Ошибка сканирования", "danger");
                        showButtons();
                    },
                    complete: function () {
                        stopProgress();
                        hideStopReadFingerGroup();
                    }
                });
            }

            function enterSmsCode() {
                $('#sms-code-input').attr('disabled', false);
                $('button#sms-code-confirm').attr('disabled', false);
                $('#sms-code-input').val('');

                var infoMessage = "<b>" + params.user.shortName + "</b>, введите код полученный в СМС и нажмите кнопку ОК";

                setModalAlert(Mustache.render($("script#dialog-message-template").html(), {
                    avatar: Images.getResizeUrl(params.user.avatar, "c119"),
                    text: infoMessage
                }), "info");

                var timeout = params.secondsLeft;
                $('#sms-seconds-left').html(timeout);

                clearInterval(progressStartTimeout);
                progressStartTimeout = setInterval(function(){
                    timeout--;
                    $('#sms-seconds-left').html(timeout);

                    if (timeout === 0){
                        clearInterval(progressStartTimeout);
                        stopSmsVerification();
                    }
                }, 1000);

                showSmsVerificationUI();
            }

            function startProgress(timeout, formatter) {
                clearTimeout(progressStopTimeout);

                progressStartTimeout = setTimeout(function() {
                    var $bar = $(".progress-bar");

                    $bar.attr("aria-valuemax", timeout);
                    $bar.attr("data-transitiongoal", timeout);
                    $bar.attr("aria-valuenow", 0);
                    $bar.css("width", "0%");

                    $bar.css("-webkit-transition", "width " + timeout + "s linear");
                    $bar.css("-moz-transition", "width " + timeout + "s linear");
                    $bar.css("-ms-transition", "width " + timeout + "s linear");
                    $bar.css("-o-transition", "width " + timeout + "s linear");
                    $bar.css("transition", "width " + timeout + "s linear");

                    $bar.progressbar({
                        display_text: 'center',
                        use_percentage: false,
                        amount_format: formatter
                    });
                }, 100);
            }

            function stopProgress() {
                clearTimeout(progressStartTimeout);

                $('#sms-seconds-left').html('');
                var $bar = $(".progress-bar");

                $bar.css("-webkit-transition", "none");
                $bar.css("-moz-transition", "none");
                $bar.css("-ms-transition", "none");
                $bar.css("-o-transition", "none");
                $bar.css("transition", "none");

                progressStopTimeout = setTimeout(function() {
                    $bar.css("width", "0%").attr("aria-valuenow", 0);
                }, 100);
            }

            function stopReadFinger() {
                stopProgress();

                if (params.scanAjax) {
                    params.scanAjax.abort();
                }

                setModalAlert("Укажите адрес сервера авторизации Благосфера для подключения или подключитесь к локальному серверу авторизации Благосфера", "info");

                showButtons(true);
                showRemoteServiceGroup();
            }

            function initToken() {
                $.ajax({
                    url: "/finger/inittoken.json",
                    async: async,
                    type: "get",
                    dataType: "json",
                    data: {
                        ikp: params.ikp
                    },
                    success: function (response) {
                        params.requestId = response.requestId;
                        params.ikp = response.ikp;
                        params.finger = response.finger;
                        params.secondsLeft = response.secondsLeft;

                        hideSmsVerificationUI();

                        if (params.identificationMode === 'ras') {
                            readFinger(params.ikp, params.requestId, params.finger);
                        } else if (params.identificationMode === 'tablet') {
                            setModalAlert("Сканирование отпечатка пальца", "info");
                            showModal();
                            RegistratorTablet.scanFinger(params.finger, SystemSettings.fingerScanTimeout, 'tabletScanFingerCallback');
                        } else if (params.identificationMode === 'sms') {
                            enterSmsCode();
                        }
                    },
                    error: function () {
                        setModalAlert("Ошибка инициализации токена", "danger");
                        showButtons();
                    }
                });
            }

            window.tabletScanFingerCallback = function(result) {
                if (result === 0) {
                    setModalAlert("Получение токена <i class='fa fa-spinner faa-spin animated' style='float : right; font-size : 22px; position : absolute; top : 15px; right : 15px;'></i>", "info");
                    RegistratorTablet.getToken(params.ikp, params.requestId, 'tabletGetTokenCallback');
                } else {
                    setModalAlert("Ошибка сканирования", "danger");
                    showButtons(true);
                }
            };

            window.tabletGetTokenCallback = function(token) {
                doAction(token);
            };

            function getToken(ikp, requestId) {
                setModalAlert("Получение токена <i class='fa fa-spinner faa-spin animated' style='float : right; font-size : 22px; position : absolute; top : 15px; right : 15px;'></i>", "info");

                var timeout = SystemSettings.fingerScanTimeout;
                var devhost = window.location.protocol + "//" + window.location.host;
                //devhost = "http://10.0.2.2:8080";

                $.ajax({
                    crossDomain: true,
                    url: getFullAddress(params.address) + "/sendencrypted.sma",
                    async: async,
                    type: "post",
                    dataType: "json",
                    timeout: timeout * 1000,
                    data: {
                        devhost: devhost,
                        ikp: ikp,
                        request_id: requestId,
                        jsonMode: "yes",
                        type: "finger"
                    },
                    success: function (response) {
                        if (response.token) {
                            var token = response.token;
                            doAction(token);
                        } else {
                            if (response.message) {
                                setModalAlert(response.message, "danger");
                            } else {
                                setModalAlert("Сервис проверки отпечатков недоступен. Повторите попытку позже или обратитесь к администратору.", "danger");
                            }
                            showButtons();
                        }
                    },
                    error: function () {
                        setModalAlert("Сервис проверки отпечатков недоступен. Повторите попытку позже или обратитесь к администратору.", "danger");
                        showButtons();
                    }
                });
            }

            function doAction(token) {
                setModalAlert("Выполнение действия <i class='fa fa-spinner faa-spin animated' style='float: right; font-size: 22px; position: absolute; top: 15px; right: 15px;'></i>", "info");
                if (!params.contentType) params.contentType = 'application/x-www-form-urlencoded';

                if (typeof params.url === 'string') {
                    var ajaxParams = {
                        url: params.url,
                        async: async,
                        type: params.type,
                        contentType: params.contentType,
                        dataType: "json",
                        data: params.data,
                        headers: {
                            FINGER_TOKEN: token,
                            IKP: params.ikp
                        },
                        success: function (response) {
                            if (response.result == "error") {
                                if (response.message) {
                                    setModalAlert(response.message, "danger");
                                } else {
                                    setModalAlert(params.errorMessage, "danger");
                                }

                                if (params.errorCallback) params.errorCallback(response);
                                showButtons();
                            } else {
                                setModalAlert(params.successRequestMessage, "success");
                                if (params.successCallback) params.successCallback(response);

                                if (params.closeModalOnSuccess) {
                                    hideModal();
                                } else {
                                    showButtons(true);
                                }
                            }
                        },
                        error: function () {
                            setModalAlert(params.errorMessage, "danger");
                            showButtons();
                            if (params.errorCallback) params.errorCallback();
                        },
                        complete: function () {
                            params.finished = true;
                            if (params.closeOnComplete) getModal().modal("hide");
                        }
                    };

                    if (params.preventProcessData) ajaxParams.processData = false;
                    if (params.preventContentType) ajaxParams.contentType = false;

                    $.ajax(ajaxParams);
                } else if (typeof params.url === 'function') {
                    params.url(token, function (response) {
                        if (response.result == "error") {
                            if (response.message) {
                                setModalAlert(response.message, "danger");
                            } else {
                                setModalAlert(params.errorMessage, "danger");
                            }

                            if (params.errorCallback) params.errorCallback(response);
                            showButtons();
                        } else {
                            setModalAlert(params.successRequestMessage, "success");
                            if (params.successCallback) params.successCallback(response);

                            if (params.closeModalOnSuccess) {
                                hideModal();
                            } else {
                                showButtons(true);
                            }
                        }
                    }, function () {
                        setModalAlert(params.errorMessage, "danger");
                        showButtons();
                        if (params.errorCallback) params.errorCallback();
                    }, function () {
                        params.finished = true;
                        if (params.closeOnComplete) getModal().modal("hide");
                    });
                }
            }

            hideButtons();
            hideStopGetInfoGroup();
            hideStopReadFingerGroup();
            hideRemoteServiceGroup();
            checkVerified();
        }
    });
})(jQuery);