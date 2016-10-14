(function ($) {
    $.extend({
        radomSharerCertification: function (params) {
            var defaultParams = {
                stages: ["agreement-upload", "fingers", "card", "agreement-text", "finish"],
                address: "localhost",
                ajax: {}
            };

            var identificationDocTypes = [];

            params.address = $.cookie("DEFAULT_SERVICE");
            params = $.extend({}, defaultParams, params);

            function isLocal(address) {
                return address == "127.0.0.1" || address == "localhost";
            }

            function abortAjax(ajax) {
                if (ajax) {
                    ajax.abort();
                }
            }

            function abortAllAjaxes() {
                $.each(params.ajax, function (index, ajax) {
                    abortAjax(ajax);
                });
            }

            function getFullAddress(address) {
                return "https://" + address + ":36123";
            }

            function getDialog() {
                return $("div#sharer-certification-dialog");
            }

            function getSpinnerMarkup() {
                return "<i class='fa fa-spinner faa-spin animated' style='float : right; font-size : 22px; position : absolute; top : 15px; right : 15px;'></i>";
            }

            function collapsePanel($panel, skipAnimation) {
                var $body = $panel.find(".panel-body");
                var $footer = $panel.find(".panel-footer");
                if (skipAnimation) {
                    $body.hide();
                    $footer.hide();
                } else {
                    $body.slideUp();
                    $footer.slideUp();
                }
                return $panel;
            }

            function showPanel($panel, skipAnimation) {
                $panel.show();
                var $body = $panel.find(".panel-body");
                var $footer = $panel.find(".panel-footer");
                if (skipAnimation) {
                    $body.show();
                    $footer.show();
                } else {
                    $body.slideDown();
                    $footer.slideDown();
                }
                return $panel;
            }

            function hidePanel($panel) {
                $panel.hide();
                return $panel;
            }

            function decoratePanel($panel, cls, icon) {
                $panel.removeClass("panel-success");
                $panel.removeClass("panel-info");
                $panel.removeClass("panel-warning");
                $panel.removeClass("panel-danger");
                $panel.removeClass("panel-primary");
                $panel.addClass("panel-" + cls);

                var $header = $panel.find(".panel-heading");
                var $icon = $header.find("i");
                if ($icon.length == 0) {
                    $icon = $("<i></i>");
                    $header.append($icon);
                }
                $icon.removeAttr("class").addClass("pull-right").addClass(icon);

                return $panel;
            }

            function getAgreementTextPanel() {
                return getDialog().find("div#agreement-text-panel");
            }

            function getAgreementUploadPanel() {
                return getDialog().find("div#agreement-upload-panel");
            }

            function getFingersPanel() {
                return getDialog().find("div#fingers-panel");
            }

            function getCardPanel() {
                return getDialog().find("div#card-panel");
            }

            function getFinishPanel() {
                return getDialog().find("div#finish-panel");
            }

            function getStagePanel(stage) {
                switch (stage) {
                    case "agreement-text" :
                        return getAgreementTextPanel();
                        break;
                    case "agreement-upload" :
                        return getAgreementUploadPanel();
                        break;
                    case "fingers" :
                        return getFingersPanel();
                        break;
                    case "card" :
                        return getCardPanel();
                        break;
                    case "finish" :
                        return getFinishPanel();
                        break;
                    default :
                        break;
                }
            }

            function startStage(stage) {
                var $panel = getStagePanel(stage);
                showPanel($panel);
                decoratePanel($panel, "info", "glyphicon glyphicon-screenshot");
                switch (stage) {
                    case "agreement-text" :
                        startAgreementTextStage();
                        break;
                    case "agreement-upload" :
                        startAgreementUploadStage();
                        break;
                    case "fingers" :
                        startFingersStage();
                        break;
                    case "card" :
                        startCardStage();
                        break;
                    case "finish" :
                        startFinishStage();
                        break;
                    default :
                        break;
                }
            }

            function finishStage(stage) {
                var $panel = getStagePanel(stage);
                collapsePanel($panel);
                decoratePanel($panel, "success", "glyphicon glyphicon-ok-sign");
                var nextIndex = $.inArray(stage, params.stages) + 1;
                var nextStage = params.stages[nextIndex];
                if (nextStage) {
                    startStage(nextStage);
                    /*if(nextStage=="fingers") {
                     startStage("finish");
                     }*/
                } else {
                    getDialog().find("button#close-button").fadeOut();
                    getDialog().find("button#finish-button").fadeIn();
                }
            }

            function initialCheck() {
                $.radomFingerJsonAjax({
                    url: "/sharer/init_certification.json",
                    type: "get",
                    data: {
                        sharer_id: params.sharerId
                    },
                    successMessage: "Проверка данных профиля завершена",
                    errorMessage: "Ошибка проверки данных профиля",

                    getScanDescription: function (server, fingerTitle) {
                        return "Регистратор, просканируйте <b style='text-decoration : underline;'>" + fingerTitle + "</b> на сканере подключенном к " + ((server == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + server)) + ", для подтверждения при помощи отпечатка пальца соответствия данных профиля участника его документам";
                    },

                    successCallback: function (response) {
                        var timer = $('span#sessionTimer');
                        timer.timer('remove');
                        timer.timer({
                            duration: response.sessionTimer,
                            countdown: true,
                            callback: function () {
                                timer.timer('pause');

                                bootbox.alert("Время, отведенное на процедуру идентификации, истекло. Необходимо повторить процедуру идентификации заново.", function () {
                                    timer.timer('remove');
                                    abortAllAjaxes();
                                    getDialog().modal("hide");
                                });

                            },
                            format: '%H:%M:%S'
                        });

                        params.registrator = response.registrator;
                        params.sharer = response.sharer;
                        params.sharer_short_name_padeg_2 = response.sharer_short_name_padeg_2;
                        params.sessionId = response.sessionId;

                        identificationDocTypes = response.docTypes;

                        if ($.inArray("card", params.stages) > -1 && !response.isAllowWriteCard) {
                            bootbox.alert("Вы не можете записать карту данному участнику");
                        } else if ($.inArray("fingers", params.stages) > -1 && !response.isAllowSaveFinger) {
                            bootbox.alert("Вы не можете сохранить образцы отпечатков данного участника");
                        } else if ($.inArray("finish", params.stages) > -1 && !response.isAllowSetVerified) {
                            bootbox.alert("Вы не можете идентифицировать данного участника");
                        } else if ($.inArray("finish", params.stages) > -1 && response.sharerProfileFilling.percent < response.sharerProfileFilling.treshold) {
                            bootbox.alert("Профиль участника заполнен менее чем на " + response.sharerProfileFilling.treshold + "%, идентификация невозможна");
                        } else {
                            show();
                        }
                    },
                    errorCallback: function (response) {
                        bootbox.alert(response.message);
                    },
                    closeOnComplete: true
                });
            }

            function show() {
                getDialog().find("button#close-button").off().on("click", function () {
                    bootbox.confirm("Процедура не завершена. В случае закрытия диалога, все несохраненные изменения будут утеряны. Подтвердите закрытие диалога, или нажмите кнопку Отмена чтобы продолжить процедуру.", function (result) {
                        if (result) {
                            abortAllAjaxes();
                            getDialog().modal("hide");
                        }
                    });
                }).show();

                getDialog().find("button#finish-button").hide();

                var first = true;

                $.each(defaultParams.stages, function (index, stage) {
                    var $panel = getStagePanel(stage);

                    if ($.inArray(stage, params.stages) > -1) {
                        showPanel($panel, true);

                        if (first) {
                            first = false;
                            decoratePanel($panel, "info", "glyphicon glyphicon-screenshot");
                        } else {
                            collapsePanel($panel, true);
                            decoratePanel($panel, "default", "glyphicon glyphicon-minus-sign");
                        }
                    } else {
                        hidePanel($panel);
                    }
                });

                startStage(params.stages[0]);

                $("div#sharer-certification-dialog").modal("show");
            }

            function startAgreementTextStage() {
                function loadAgreement() {
                    $.radomJsonGet("/certification-agreement-text", {}, function(response) {
                        if (response.message) {
                            bootbox.alert(response.message);
                        } else {
                            var $agreementTextPanel = getAgreementTextPanel();
                            //$agreementTextPanel.addClass('height-100');
                            //$agreementTextPanel.find("div.panel-body div#text-container").html(response.text);
                            $('div#agreement-text').html(response.text);

                            $("#show-agreement-link").click(function(){
                                $("#agreement-modal").modal("show");
                            });

                            $agreementTextPanel.find("#agree-with-agreement").prop('checked', false);
                            $agreementTextPanel.find("#go-to-agreement-upload").attr('disabled', true);

                            $agreementTextPanel.find("#agree-with-agreement").click(function () {
                                $agreementTextPanel.find("#go-to-agreement-upload").attr('disabled', !$(this).is(':checked'));
                            });

                            $agreementTextPanel.find("#go-to-agreement-upload").off("click").on("click", function () {
                                $.radomFingerJsonAjax({
                                    avatar: Images.getResizeUrl(params.sharer.avatar, "c119"),
                                    url: "/sharer/set_agreed.json",
                                    type: "post",
                                    data: {
                                        user_id: params.sharerId,
                                        session_id: params.sessionId
                                    },
                                    ikp: params.sharer.ikp,
                                    successMessage: "Идентификация профиля успешно подтверждена",
                                    errorMessage: "Ошибка подтверждения идентификации профиля",
                                    closeModalOnSuccess: true,
                                    getScanDescription: function (server, fingerTitle) {
                                        return "Участник [<b>" + params.sharer.shortName + "</b>], просканируйте <b style='text-decoration : underline;'>" + fingerTitle + "</b> на сканере подключенном к " + ((server == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + server)) + ", для подтверждения Вашего согласия с Пользовательским Соглашением"
                                    },
                                    successCallback: function (response) {
                                        finishStage("agreement-text");
                                        //$agreementTextPanel.removeClass('height-100');
                                        params.avatar = undefined;
                                    },
                                    errorCallback: function (response) {
                                        params.avatar = undefined;
                                    }
                                });

                                return false;
                            });
                        }
                    });
                }

                loadAgreement();
            }

            function startAgreementUploadStage() {
                function allUploaded() {
                    for (var i = 0; i < identificationDocTypes.length; i++) {
                        if (!identificationDocTypes[i].uploaded) return false;
                    }

                    return true;
                }

                for (var i = 0; i < identificationDocTypes.length; i++) {
                    identificationDocTypes[i].uploaded = false;
                }

                function doAgreementUpload() {
                    for (var i = 0; i < identificationDocTypes.length; i++) {
                        var docType = identificationDocTypes[i];
                        var dropzone = Dropzone.forElement("#agreement-dropzone-" + docType.name);

                        if (dropzone.files.length < docType.minFiles) {
                            var mes;

                            if (docType.minFiles == 1) mes = '1 файл';
                            else if (docType.minFiles < 5) mes = docType.minFiles + ' файла';
                            else mes = docType.minFiles + ' файлов';

                            bootbox.alert('Выберите для загрузки минимум ' + mes + ' категории "' + docType.title + '"');
                            return;
                        }
                    }

                    identificationDocTypes.forEach(function (item) {
                        dropzoneUpload(item);
                    });
                }

                var $agreementUploadPanel = getAgreementUploadPanel();
                $agreementUploadPanel.find('.panel-body form').remove();
                $agreementUploadPanel.find('.panel-body span').remove();

                var $panelBody = $agreementUploadPanel.find('.panel-body')

                identificationDocTypes.forEach(function (item) {
                    initDropzone(item);
                });

                function initDropzone(docType) {
                    /*var previewTemplate = '<div class="dz-preview dz-file-preview">' +
                        '<div class="dz-image"><img data-dz-thumbnail /></div>' +
                        '<div class="dz-details">' +
                          '<div class="dz-size"><span data-dz-size></span></div>' +
                          '<div class="dz-filename"><span data-dz-name></span></div>' +
                        '</div>' +
                        '<div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>' +
                        '<div class="dz-error-message"><span data-dz-errormessage></span></div>' +
                        '<div class="dz-success-mark">' +
                          '<svg width="54px" height="54px" viewBox="0 0 54 54" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns">' +
                            '<title>Check</title>' +
                            '<defs></defs>' +
                            '<g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" sketch:type="MSPage">' +
                              '<path d="M23.5,31.8431458 L17.5852419,25.9283877 C16.0248253,24.3679711 13.4910294,24.366835 11.9289322,25.9289322 C10.3700136,27.4878508 10.3665912,30.0234455 11.9283877,31.5852419 L20.4147581,40.0716123 C20.5133999,40.1702541 20.6159315,40.2626649 20.7218615,40.3488435 C22.2835669,41.8725651 24.794234,41.8626202 26.3461564,40.3106978 L43.3106978,23.3461564 C44.8771021,21.7797521 44.8758057,19.2483887 43.3137085,17.6862915 C41.7547899,16.1273729 39.2176035,16.1255422 37.6538436,17.6893022 L23.5,31.8431458 Z M27,53 C41.3594035,53 53,41.3594035 53,27 C53,12.6405965 41.3594035,1 27,1 C12.6405965,1 1,12.6405965 1,27 C1,41.3594035 12.6405965,53 27,53 Z" id="Oval-2" stroke-opacity="0.198794158" stroke="#747474" fill-opacity="0.816519475" fill="#FFFFFF" sketch:type="MSShapeGroup"></path>' +
                            '</g>' +
                          '</svg>' +
                        '</div>' +
                        '<div class="dz-error-mark">' +
                          '<svg width="54px" height="54px" viewBox="0 0 54 54" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns">' +
                            '<title>Error</title>' +
                            '<defs></defs>' +
                            '<g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" sketch:type="MSPage">' +
                              '<g id="Check-+-Oval-2" sketch:type="MSLayerGroup" stroke="#747474" stroke-opacity="0.198794158" fill="#FFFFFF" fill-opacity="0.816519475">' +
                                '<path d="M32.6568542,29 L38.3106978,23.3461564 C39.8771021,21.7797521 39.8758057,19.2483887 38.3137085,17.6862915 C36.7547899,16.1273729 34.2176035,16.1255422 32.6538436,17.6893022 L27,23.3431458 L21.3461564,17.6893022 C19.7823965,16.1255422 17.2452101,16.1273729 15.6862915,17.6862915 C14.1241943,19.2483887 14.1228979,21.7797521 15.6893022,23.3461564 L21.3431458,29 L15.6893022,34.6538436 C14.1228979,36.2202479 14.1241943,38.7516113 15.6862915,40.3137085 C17.2452101,41.8726271 19.7823965,41.8744578 21.3461564,40.3106978 L27,34.6568542 L32.6538436,40.3106978 C34.2176035,41.8744578 36.7547899,41.8726271 38.3137085,40.3137085 C39.8758057,38.7516113 39.8771021,36.2202479 38.3106978,34.6538436 L32.6568542,29 Z M27,53 C41.3594035,53 53,41.3594035 53,27 C53,12.6405965 41.3594035,1 27,1 C12.6405965,1 1,12.6405965 1,27 C1,41.3594035 12.6405965,53 27,53 Z" id="Oval-2" sketch:type="MSShapeGroup"></path>' +
                              '</g>' +
                            '</g>' +
                          '</svg>' +
                        '</div>' +
                      '</div>';*/
                    var previewTemplate = '<div class="dz-preview dz-file-preview">' +
                        '<div class="dz-image"><img data-dz-thumbnail /></div>' +
                        '<div class="dz-details">' +
                          '<div><span data-dz-size></span></div>' +
                        '</div>' +
                        '<div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>' +
                        '<div class="dz-error-message"><span data-dz-errormessage></span></div>' +
                        '<div class="dz-success-mark">' +
                          '<svg width="54px" height="54px" viewBox="0 0 54 54" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns">' +
                            '<title>Check</title>' +
                            '<defs></defs>' +
                            '<g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" sketch:type="MSPage">' +
                              '<path d="M23.5,31.8431458 L17.5852419,25.9283877 C16.0248253,24.3679711 13.4910294,24.366835 11.9289322,25.9289322 C10.3700136,27.4878508 10.3665912,30.0234455 11.9283877,31.5852419 L20.4147581,40.0716123 C20.5133999,40.1702541 20.6159315,40.2626649 20.7218615,40.3488435 C22.2835669,41.8725651 24.794234,41.8626202 26.3461564,40.3106978 L43.3106978,23.3461564 C44.8771021,21.7797521 44.8758057,19.2483887 43.3137085,17.6862915 C41.7547899,16.1273729 39.2176035,16.1255422 37.6538436,17.6893022 L23.5,31.8431458 Z M27,53 C41.3594035,53 53,41.3594035 53,27 C53,12.6405965 41.3594035,1 27,1 C12.6405965,1 1,12.6405965 1,27 C1,41.3594035 12.6405965,53 27,53 Z" id="Oval-2" stroke-opacity="0.198794158" stroke="#747474" fill-opacity="0.816519475" fill="#FFFFFF" sketch:type="MSShapeGroup"></path>' +
                            '</g>' +
                          '</svg>' +
                        '</div>' +
                        '<div class="dz-error-mark">' +
                          '<svg width="54px" height="54px" viewBox="0 0 54 54" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:sketch="http://www.bohemiancoding.com/sketch/ns">' +
                            '<title>Error</title>' +
                            '<defs></defs>' +
                            '<g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd" sketch:type="MSPage">' +
                              '<g id="Check-+-Oval-2" sketch:type="MSLayerGroup" stroke="#747474" stroke-opacity="0.198794158" fill="#FFFFFF" fill-opacity="0.816519475">' +
                                '<path d="M32.6568542,29 L38.3106978,23.3461564 C39.8771021,21.7797521 39.8758057,19.2483887 38.3137085,17.6862915 C36.7547899,16.1273729 34.2176035,16.1255422 32.6538436,17.6893022 L27,23.3431458 L21.3461564,17.6893022 C19.7823965,16.1255422 17.2452101,16.1273729 15.6862915,17.6862915 C14.1241943,19.2483887 14.1228979,21.7797521 15.6893022,23.3461564 L21.3431458,29 L15.6893022,34.6538436 C14.1228979,36.2202479 14.1241943,38.7516113 15.6862915,40.3137085 C17.2452101,41.8726271 19.7823965,41.8744578 21.3461564,40.3106978 L27,34.6568542 L32.6538436,40.3106978 C34.2176035,41.8744578 36.7547899,41.8726271 38.3137085,40.3137085 C39.8758057,38.7516113 39.8771021,36.2202479 38.3106978,34.6538436 L32.6568542,29 Z M27,53 C41.3594035,53 53,41.3594035 53,27 C53,12.6405965 41.3594035,1 27,1 C12.6405965,1 1,12.6405965 1,27 C1,41.3594035 12.6405965,53 27,53 Z" id="Oval-2" sketch:type="MSShapeGroup"></path>' +
                              '</g>' +
                            '</g>' +
                          '</svg>' +
                        '</div>' +
                        '<div class="dz-remove"><span data-dz-name></span></div>' +
                      '</div>';

                    $('<span>' + docType.title + '</span>').appendTo($panelBody);

                    var dropzoneForm = $('<form id="agreement-dropzone-' + docType.name + '"></form>');
                    dropzoneForm.appendTo($panelBody);

                    if (dropzoneForm.get(0).dropzone) {
                    } else {
                        dropzoneForm.dropzone({
                            headers : {
                                FINGER_TOKEN: 'FINGER_TOKEN'
                            },
                            params: {
                                "sharer_id": params.sharerId,
                                "session_id": params.sessionId
                            },
                            url: '/sharer/upload_registration_agreement.json',
                            uploadMultiple: true,
                            parallelUploads: 10,
                            maxFiles: 10,
                            addRemoveLinks: true,
                            acceptedFiles: 'image/*,application/pdf',
                            autoProcessQueue: false,
                            clickable: ['#agreement-dropzone-' + docType.name],
                            dictCancelUpload: 'отменить',
                            dictCancelUploadConfirmation: 'отменить загрузку?',
                            dictDefaultMessage: 'переместите сюда файлы для загрузки',
                            dictInvalidFileType: 'недопустимый формат файла',
                            dictMaxFilesExceeded: 'вы не можете загрузить больше файлов',
                            dictRemoveFile: 'удалить',
                            dictResponseError: 'код ответа сервера: {{statusCode}}',
                            renameFilename: function(filename) {
                                return docType.name + '.' + /(?:\.([^.]+))?$/.exec(filename)[1];
                            },
                            previewTemplate: previewTemplate
                        });

                        dropzoneForm.addClass('dropzone');
                    }
                }

                function dropzoneUpload(docType) {
                    var dropzone = Dropzone.forElement("#agreement-dropzone-" + docType.name);

                    dropzone.on('success', function (file, response, event) {
                        response = JSON.parse(response);

                        if (response.result === 'error') {
                            resetFiles();
                        } else {
                            docType.uploaded = true;

                            if (allUploaded()) finishStage("agreement-upload");
                        }
                    });

                    dropzone.on('error', function (file, response) {
                        resetFiles();
                    });

                    function resetFiles() {
                        dropzone.files.forEach(function (file) {
                            file.status = Dropzone.QUEUED;
                        });
                    }

                    dropzone.processQueue();
                }

                $agreementUploadPanel.find("a#do-agreement-upload-link").off("click").on("click", function () {
                    doAgreementUpload();
                    return false;
                });
            }

            function startFingersStage(skipAnimation) {

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

                function getAlert() {
                    return getFingersPanel().find(".alert#message-alert");
                }

                function showAlert(html, cls, skipAnimation) {
                    var $alert = getAlert();
                    $alert.html(html).removeClass("alert-success").removeClass("alert-info").removeClass("alert-warning").removeClass("alert-danger").addClass("alert-" + cls);
                    if (skipAnimation) {
                        $alert.show();
                    } else {
                        $alert.slideDown();
                    }
                }

                function hideAlert(skipAnimation) {
                    if (skipAnimation) {
                        getAlert().hide();
                    } else {
                        getAlert().slideUp();
                    }
                }

                function getFingerSelectBlock() {
                    return getFingersPanel().find("#finger-select-block");
                }

                function getServerSelectBlock() {
                    return getFingersPanel().find("#server-select-block");
                }

                function getScanBlock() {
                    return getFingersPanel().find("#scan-block");
                }

                function getFooter() {
                    return getFingersPanel().find(".panel-footer");
                }

                function getFinishLink() {
                    return getFooter().find("a#finish-fingers-link");
                }

                function getCancelLink() {
                    return getFooter().find("a#cancel-fingers-link");
                }

                function getRetryLink() {
                    return getFooter().find("a#retry-fingers-link");
                }

                function getContinueLink() {
                    return getFooter().find("a#continue-fingers-link");
                }

                function selectFinger(finger) {
                    getFingerSelectBlock().slideUp();
                    getCancelLink().show();
                    getFinishLink().hide();
                    params.selectedFingerNumber = parseInt(finger);

                    /*window.RegistratorTablet = {
                        saveFinger: function (finger, ikp, registrator_ikp, session_id, timeout, callbackName){
                            console.log(finger, ikp, registrator_ikp, session_id, timeout, callbackName);
                            eval(callbackName)(1);
                        }
                    };*/

                    if (typeof RegistratorTablet === 'undefined') {
                        getInfo();
                    } else {
                        RegistratorTablet.saveFinger(params.selectedFingerNumber, params.sharer.ikp,
                            params.registrator.ikp, params.sessionId, SystemSettings.fingerScanTimeout,
                            'finishFingerScan');
                    }
                }

                function showServerSelectBlock() {
                    var $serverSelectBlock = getServerSelectBlock();

                    $serverSelectBlock.find("a#connect-to-remote-server-link").off("click").on("click", function () {
                        var address = $serverSelectBlock.find("input#remote-server-address-input").val();
                        if (!address) {
                            bootbox.alert("Необходимо ввести адрес");
                        } else {
                            $.cookie("DEFAULT_SERVICE", address, {path: "/", expires: 7});
                            params.address = address;
                            $serverSelectBlock.slideUp();
                            getInfo();
                        }
                        return false;
                    });

                    $serverSelectBlock.find("a#connect-to-local-server-link").off("click").on("click", function () {
                        $.removeCookie("DEFAULT_SERVICE");
                        params.address = "localhost";
                        $serverSelectBlock.slideUp();
                        getInfo();
                        return false;
                    });

                    $serverSelectBlock.slideDown();
                }

                function getInfo() {
                    var defaultService = $.cookie("DEFAULT_SERVICE");
                    if (defaultService) {
                        params.address = defaultService;
                    }

                    showAlert("Подключение к " + (isLocal(params.address) ? "локальному серверу авторизации" : "серверу авторизации " + params.address) + getSpinnerMarkup(), "info", false);

                    function errorHandler(message) {
                        showAlert(message ? message : "Ошибка подключения к серверу авторизации. Сервер недоступен или версия устарела.", "danger", false);
                        $.removeCookie("DEFAULT_SERVICE");
                        showServerSelectBlock();
                    }

                    params.ajax.info = $.ajax({
                        crossDomain: true,
                        type: "get",
                        dataType: "json",
                        url: getFullAddress(params.address) + "/info.sma",
                        data: {
                            jsonMode: "yes"
                        },
                        success: function (response) {
                            if (response.version) {
                                checkVersion(response.version);
                            } else {
                                errorHandler();
                            }
                        },
                        error: function () {
                            errorHandler();
                        }
                    });

                    function checkVersion(version) {
                        $.ajax({
                            crossDomain : false,
                            url :  "/ras/version/min",
                            type : "get",
                            dataType : "text",
                            data : {},
                            success : function(response) {
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
                                    showScanBlock();
                                } else  {
                                    errorHandler('Вы используете Сервер авторизации "БЛАГОСФЕРА" версии ' + version + '. Минимальная поддерживаемая версия ' + response + '. Пожалуйста скачайте новую версию по <a target="_blank" href="/ras/download">данной ссылке</a>');
                                }
                            },
                            error: function() {
                                errorHandler();
                            }
                        });
                    }
                }

                function showScanBlock() {
                    var $block = getScanBlock();
                    var $totalProgress = $block.find("#total-progress.progress");
                    $totalProgress.empty().show().append("<div class='progress-bar' role='progressbar'></div>");
                    $block.slideDown(function () {
                        readFinger();

                        $totalProgress.find(".progress-bar").attr('data-transitiongoal', 0).progressbar({
                            display_text: 'center',
                            percent_format: function (p) {
                                return "Общий прогресс " + p + "%";
                            },
                            done: function () {
                                if (params.onTotalChange) {
                                    params.onTotalChange();
                                }
                            }
                        });
                    });
                }

                function readFinger() {
                    var devhost = window.location.protocol + "//" + window.location.host;
                    //devhost = "http://10.0.2.2:8080";

                    var timeout = SystemSettings.fingerScanTimeout;
                    var $block = getScanBlock();
                    var $scanTimeoutProgress = $block.find("#scan-timeout.progress");

                    function startReadFingerStep() {
                        showAlert(Mustache.render($("script#certification-scan-description-template").html(), {
                            avatar: Images.getResizeUrl(params.sharer.avatar, "c119"),
                            text: "Участник [<b>" + params.sharer.shortName + "</b>], просканируйте <b style='text-decoration : underline;'>" + getFingerTitle(params.selectedFingerNumber, true) + "</b> на сканере подключенном к " + ((params.address == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + params.address)) + ", для формирования образца отпечатка"
                        }), "info");

                        var $block = getScanBlock();
                        var $scanTimeoutProgress = $block.find("#scan-timeout.progress");
                        $scanTimeoutProgress.slideDown(function () {
                            $scanTimeoutProgress.radomProgressbar({
                                timeout: timeout,
                                display_text: 'center',
                                use_percentage: false,
                                amount_format: function (p, max, min) {
                                    var secondsLeft = max - p;
                                    return 'Осталось ' + secondsLeft + ' ' + RadomUtils.getDeclension(secondsLeft, 'секунда', 'секунды', 'секунд');
                                },
                                done: function () {
                                    abortAjax(params.ajax.scanCombinedStatus);
                                }
                            });
                        });
                    }

                    function errorHandler(response) {
                        $block.find("#total-progress.progress").slideUp().empty();
                        $scanTimeoutProgress.slideUp();
                        showAlert("Ошибка сканирования" + (response && response.message ? (", " + response.message) : ""), "danger");

                        abortAjax(params.ajax.scanCombinedStatus);
                        abortAjax(params.ajax.scanCombined);

                        getRetryLink().show().off("click").on("click", function () {
                            showScanBlock();
                            getRetryLink().hide();
                            return false;
                        });
                    }

                    var showDanger = function (message) {
                        var progressDiv = $("div#progressDiv");
                        progressDiv.addClass("alert-danger");
                        progressDiv.removeClass("alert-info");
                        progressDiv.html(message);
                    };

                    var showInfo = function (message) {
                        var progressDiv = $("div#progressDiv");
                        progressDiv.removeClass("alert-danger");
                        progressDiv.addClass("alert-info");
                        progressDiv.html(message);
                    };

                    var showNormal = function (message) {
                        var progressDiv = $("div#progressDiv");
                        progressDiv.removeClass("alert-danger");
                        progressDiv.removeClass("alert-info");
                        progressDiv.html(message);
                    };

                    var scanFinger = function () {
                        startReadFingerStep();
                        
                        params.scanAjax = $.ajax({
                            crossDomain : true,
                            url : getFullAddress(params.address) + "/scan.sma",
                            type : "get",
                            dataType : "json",
                            data : {
                                combined : "no",
                                jsonMode : "yes",
                                timeout : timeout
                            },
                            success : function(response) {
                                if (response.result == "success") {
                                    sendFinger();
                                } else {
                                    showDanger("Ошибка сканирования", "danger");
                                    setTimeout(function () {
                                        scanFinger()
                                    }, 0);
                                }
                            },
                            error : function() {
                                showDanger("Ошибка сканирования", "danger");
                                $("div#scan-timeout").hide();
                            }
                        });
                    };

                    function sendFinger() {
                        showInfo("Обработка отпечатка пальца", "info");

                        params.ajax.scanCombined = $.ajax({
                            crossDomain: true,
                            url: getFullAddress(params.address) + '/sendencrypted.sma',
                            type: 'post',
                            dataType: 'json',
                            data: {
                                devhost: devhost,
                                type: 'create',
                                combined: 'no',
                                jsonMode: 'yes',
                                timeout: timeout,
                                finger: params.selectedFingerNumber,
                                ikp: params.sharer.ikp,
                                registrator_ikp: params.registrator.ikp,
                                session_id: params.sessionId
                            },
                            success: function (response) {
                                if (response.result === 'more') {
                                    if (response.count === 'SUCCESS') {
                                        showInfo("Готово!");
                                        getCancelLink().click();
                                    } else {
                                        showInfo("Осталось проходов: " + response.count);
                                        setTimeout(function () {
                                            scanFinger()
                                        }, 0);
                                    }
                                } else if (response.result === 'error') {
                                    showDanger(response.message);
                                    setTimeout(function () {
                                        scanFinger()
                                    }, 0);
                                } else {
                                    showDanger("Ошибка обращения к серверу проверки отпечатков");
                                    $("div#scan-timeout").hide();
                                }
                            },
                            error: function () {
                                errorHandler();
                            },
                            complete: function () {
                                $scanTimeoutProgress.empty();
                            }
                        });
                    }

                    showNormal("");
                    $("div#scan-timeout").show();

                    showInfo("Ожидание сканирования");
                    scanFinger();
                }

                function showFingerSelectBlock(skipAnimation) {
                    function getFingerMarkup(number, exists) {
                        var $markup = $(Mustache.render($("#certification-scan-finger-template").html(), {
                            number: number,
                            title: getFingerTitle(number),
                            exists: exists
                        }));

                        if (!exists) {
                            $markup.radomTooltip({
                                title: "Отпечаток не загружен в базу данных. Нажмите для загрузки",
                                placement: "top",
                                container: "body"
                            });
                            $markup.click(function () {
                                var number = $(this).attr("data-finger-number");
                                selectFinger(number);
                            });
                        } else {
                            $markup.radomTooltip({
                                title: "Отпечаток загружен в базу данных. Нажмите для удаления",
                                placement: "top",
                                container: "body"
                            });
                            $markup.click(function () {
                                var number = $(this).attr("data-finger-number");
                                $.radomFingerJsonAjax({
                                    url: "/finger/deletefinger.json",
                                    type: "post",
                                    data: {
                                        ikp: params.sharer.ikp,
                                        registrator_ikp: params.registrator.ikp,
                                        finger: number
                                    },
                                    successMessage: "Отпечаток успешно удален",
                                    errorMessage: "Ошибка удаления отпечатка",
                                    closeModalOnSuccess: true,
                                    getScanDescription: function (server, fingerTitle) {
                                        return "Регистратор [<b>" + params.registrator.shortName + "</b>], просканируйте <b style='text-decoration : underline;'>" + fingerTitle + "</b> на сканере подключенном к " + ((server == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + server)) + ", для подтверждения при помощи отпечатка пальца удаления отпечатка участника [" + params.sharer_short_name_padeg_2 + "]"
                                    },
                                    successCallback: function (response) {
                                        startFingersStage();
                                    },
                                    errorCallback: function (response) {
                                    }
                                });
                                return false;
                            });
                        }

                        return $markup;
                    }

                    function errorHandler() {
                        showAlert("Ошибка при формировании списка отпечатков", "danger");
                        getRetryLink().show().off("click").on("click", function () {
                            showFingerSelectBlock(false);
                            getRetryLink().hide();
                        });
                    }

                    getCancelLink().hide();

                    var $block = getFingerSelectBlock();
                    var $hands = $block.find("#hands");

                    params.ajax.fingersList = $.ajax({
                        type: "get",
                        dataType: "json",
                        url: "/finger/fingers.json",
                        data: {
                            ikp: params.sharer.ikp
                        },
                        success: function (response) {
                            if (response.fingers) {
                                var fingers = response.fingers;
                                $hands.empty();
                                for (var number = 1; number <= 10; number++) {
                                    $hands.append(getFingerMarkup(number, $.inArray(number, fingers) > -1));
                                }
                                if (fingers.length > 0) {
                                    getFinishLink().show();
                                } else {
                                    getFinishLink().hide();
                                }
                                if (skipAnimation) {
                                    $block.show();
                                } else {
                                    $block.slideDown();
                                }
                            } else {
                                errorHandler();
                            }
                        },
                        error: function () {
                            errorHandler();
                        }
                    });
                }

                getCancelLink().hide().off("click").on("click", function () {
                    abortAjax(params.ajax.info);
                    abortAjax(params.ajax.scanCombinedStatus);
                    abortAjax(params.ajax.scanCombined);
                    abortAjax(params.ajax.scanVerifyTemplate);
                    abortAjax(params.ajax.sendEncrypted);
                    getAlert().hide();
                    getServerSelectBlock().hide();
                    startFingersStage();
                    return false;
                });

                window.finishFingerScan = function (result) {
                    if (result === 0)
                        getCancelLink().click();
                    else {
                        getCancelLink().click();
                        bootbox.alert("Ошибка сканирования");
                    }
                };

                getRetryLink().hide();
                getContinueLink().hide();

                getFinishLink().off("click").on("click", function () {
                    finishStage("fingers");
                    return false;
                });

                hideAlert(skipAnimation);

                if (skipAnimation) {
                    getServerSelectBlock().hide();
                    getScanBlock().hide();
                } else {
                    getServerSelectBlock().slideUp();
                    getScanBlock().slideUp();
                }

                showFingerSelectBlock(skipAnimation);
            }

            function startCardStage() {
                function getAlert() {
                    return getCardPanel().find("div.alert");
                }

                function hideAlert(skipAnimation, callback) {
                    if (skipAnimation) {
                        getAlert().hide();
                        if (callback) {
                            callback();
                        }
                    } else {
                        getAlert().slideUp(function () {
                            if (callback) {
                                callback();
                            }
                        });
                    }
                }

                function showAlert(html, cls, skipAnimation, callback) {
                    var $alert = getAlert();
                    $alert.html(html).removeClass("alert-success").removeClass("alert-info").removeClass("alert-warning").removeClass("alert-danger").addClass("alert-" + cls);
                    if (skipAnimation) {
                        $alert.show();
                        if (callback) {
                            callback();
                        }
                    } else {
                        $alert.slideDown(function () {
                            if (callback) {
                                callback();
                            }
                        });
                    }
                }

                function getFooter() {
                    return getCardPanel().find("div.panel-footer");
                }

                function getCancelLink() {
                    return getFooter().find("a#cancel-card-link");
                }

                function getRetryLink() {
                    return getFooter().find("a#retry-card-link");
                }

                function getFinishLink() {
                    return getFooter().find("a#finish-card-link");
                }

                function getStartLink() {
                    return getFooter().find("a#start-card-link");
                }

                function getSkipLink() {
                    return getFooter().find("a#skip-card-link");
                }

                function errorHandler(response, defaultMessage) {
                    if (response && response.message) {
                        showAlert("Ошибка: " + response.message, "danger");
                    } else {
                        showAlert(defaultMessage, "danger");
                    }
                    getCancelLink().hide();
                    getRetryLink().show();
                }

                function checkCardStatus() {
                    showAlert("Получение статуса карты" + getSpinnerMarkup(), "info", true);
                    getCancelLink().show();
                    params.ajax.card = $.ajax({
                        crossDomain: true,
                        url: getFullAddress(params.address) + "/cardstatus.sma",
                        type: "get",
                        dataType: "json",
                        data: {
                            jsonMode: "yes"
                        },
                        success: function (response) {
                            if (response.result == "card on") {
                                //hideAlert();
                                writeCard();
                            } else {
                                if (response.result == "card off") {
                                    showAlert("Положите карту, предназначенную для записи ЛИК участника <b>[" + params.sharer_short_name_padeg_2 + "]</b>, на ридер" + getSpinnerMarkup(), "info", false);
                                    setTimeout(function () {
                                        checkCardStatus();
                                    }, 1000);
                                } else if (response.result = "no terminal") {
                                    errorHandler(response, "Не подключен ридер, подключите его и повторите попытку");
                                } else if (response.result == "error") {
                                    errorHandler(response, "Ошибка получения статуса карты");
                                }
                            }
                        },
                        error: function () {
                            errorHandler(null, "Ошибка получения статуса карты");
                        }
                    });
                }

                function writeCard() {
                    showAlert("Производится запись карты для участника <b>[" + params.sharer_short_name_padeg_2 + "]</b>, не снимайте карту с ридера до завершения процесса" + getSpinnerMarkup(), "info", false, function () {
                        params.ajax.card = $.ajax({
                            crossDomain: true,
                            url: getFullAddress(params.address) + "/writecard.sma",
                            type: "get",
                            dataType: "json",
                            data: {
                                jsonMode: "yes",
                                ikp: params.sharer.ikp
                            },
                            success: function (response) {
                                if (response.result == "success") {
                                    //hideAlert();
                                    setCardNumber(response.serial);
                                } else if (response.result == "error") {
                                    errorHandler(response, "Ошибка записи карты");
                                }
                            },
                            error: function () {
                                errorHandler(null, "Ошибка записи карты");
                            }
                        });
                    });
                }

                function setCardNumber(cardNumber) {
                    showAlert("Производится сохранение номера карты участника <b>[" + params.sharer_short_name_padeg_2 + "]</b> в базу данных, не снимайте карту с ридера до завершения процесса" + getSpinnerMarkup(), "info", false, function () {
                        params.ajax.card = $.ajax({
                            type: "post",
                            dataType: "json",
                            data: {
                                ikp: params.sharer.ikp,
                                card_number: cardNumber,
                                session_id: params.sessionId
                            },
                            url: "/sharer/set_card_number.json",
                            success: function (response) {
                                if (response.result == "success") {
                                    //hideAlert();
                                    checkCardAfterWrite(cardNumber);
                                } else {
                                    errorHandler(response, "Ошибка сохранения номера карты");
                                }
                            },
                            error: function () {
                                errorHandler(null, "Ошибка сохранения номера карты");
                            }
                        });
                    });
                }

                function checkCardAfterWrite(cardNumber) {
                    showAlert("Производится проверка карты участника <b>[" + params.sharer_short_name_padeg_2 + "]</b>, не снимайте карту с ридера до завершения процесса" + getSpinnerMarkup(), "info", false, function () {
                        params.ajax.card = $.ajax({
                            crossDomain: true,
                            url: getFullAddress(params.address) + "/userinfo.sma",
                            type: "get",
                            dataType: "json",
                            data: {
                                jsonMode: "yes"
                            },
                            success: function (response) {
                                if (response.result == "success" && response.ikp == params.sharer.ikp && response.serial == cardNumber) {
                                    //hideAlert();
                                    waitForCardOff();
                                } else {
                                    errorHandler(response, "Ошибка проверки карты");
                                }
                            },
                            error: function () {
                                errorHandler(null, "Ошибка проверки карты");
                            }
                        });
                    });
                }

                function waitForCardOff() {
                    showAlert("Снимите записанную карту участника <b>[" + params.sharer_short_name_padeg_2 + "]</b> с ридера" + getSpinnerMarkup(), "info", false, function () {
                        params.ajax.card = $.ajax({
                            crossDomain: true,
                            url: getFullAddress(params.address) + "/cardstatus.sma",
                            type: "get",
                            dataType: "json",
                            data: {
                                jsonMode: "yes",
                                wait_for_off: "yes"
                            },
                            success: function (response) {
                                if (response.result == "card on") {
                                    setTimeout(function () {
                                        waitForCardOff();
                                    }, 1000);
                                } else {
                                    showAlert("Карта записана, необходимые проверки проведены, нажмите кнопку Завершить", "info");
                                    getFinishLink().show();
                                    getCancelLink().hide();
                                }
                            },
                            error: function () {
                                showAlert("Карта записана, необходимые проверки проведены, нажмите кнопку Завершить", "info");
                                getFinishLink().show();
                                getCancelLink().hide();
                            }
                        });
                    });
                }

                getRetryLink().hide().off("click").on("click", function () {
                    checkCardStatus();
                    getRetryLink().hide();
                    return false;
                });

                getCancelLink().off("click").hide().on("click", function () {
                    abortAjax(params.ajax.card);
                    return false;
                });

                getFinishLink().hide().off("click").on("click", function () {
                    finishStage("card");
                    return false;
                });

                getSkipLink().off("click").on("click", function () {
                    finishStage("card");
                    return false;
                });

                getStartLink().show().off("click").on("click", function () {
                    checkCardStatus();
                    getStartLink().hide();
                    return false;
                });

                showAlert("На данном этапе будет производиться запись карты для участника <b>[" + params.sharer_short_name_padeg_2 + "]</b>. Положите карту, предназначенную для этого на ридер и нажмите кнопку Начать запись.", "info", true);
            }

            function startFinishStage() {
                function generateEcp() {
                    setTimeout(function () {
                        var $finishPanel = getFinishPanel();
                        $finishPanel.find("#ecp-alert").removeClass("alert-info").addClass("alert-success").find("i").removeClass("fa").removeClass("fa-spinner").removeClass("faa-spin").removeClass("animated").addClass("glyphicon").addClass("glyphicon-ok");
                        $finishPanel.find("#certificate-alert").removeClass("alert-warning").addClass("alert-info").find("i").removeClass("fa-clock-o").addClass("fa-spinner").addClass("faa-spin").addClass("animated");
                        generateCertificate();
                    }, 3000);
                }

                function generateCertificate() {
                    setTimeout(function () {
                        var $finishPanel = getFinishPanel();
                        $finishPanel.find("#certificate-alert").removeClass("alert-info").addClass("alert-success").find("i").removeClass("fa").removeClass("fa-spinner").removeClass("faa-spin").removeClass("animated").addClass("glyphicon").addClass("glyphicon-ok");
                        $finishPanel.find("#protect-alert").removeClass("alert-warning").addClass("alert-info").find("i").removeClass("fa-clock-o").addClass("fa-spinner").addClass("faa-spin").addClass("animated");
                        protectProfile();
                    }, 3000);
                }

                function protectProfile() {
                    setTimeout(function () {
                        var $finishPanel = getFinishPanel();
                        $finishPanel.find("#protect-alert").removeClass("alert-info").addClass("alert-success").find("i").removeClass("fa").removeClass("fa-spinner").removeClass("faa-spin").removeClass("animated").addClass("glyphicon").addClass("glyphicon-ok");
                        $finishPanel.find("#fixation-alert").removeClass("alert-warning").addClass("alert-info").find("i").removeClass("fa-clock-o").addClass("fa-spinner").addClass("faa-spin").addClass("animated");
                        setVerified();
                    }, 3000);
                }

                function setVerified() {
                    $.radomFingerJsonAjax({
                        url: "/sharer/set_verified.json",
                        type: "post",
                        data: {
                            sharer_id: params.sharerId
                        },
                        successMessage: "Идентификация профиля успешно подтверждена",
                        errorMessage: "Ошибка подтверждения идентификации профиля",
                        closeModalOnSuccess: true,
                        getScanDescription: function (server, fingerTitle) {
                            return "Регистратор [<b>" + params.registrator.shortName + "</b>], просканируйте <b style='text-decoration : underline;'>" + fingerTitle + "</b> на сканере подключенном к " + ((server == "localhost") ? ("локальному серверу авторизации Благосфера") : ("серверу авторизации Благосфера на " + server)) + ", для подтверждения при помощи отпечатка пальца завершения идентификации участника [" + params.sharer_short_name_padeg_2 + "]"
                        },
                        successCallback: function (response) {
                            getFinishPanel().find("#fixation-alert").removeClass("alert-info").addClass("alert-success").find("i").removeClass("fa").removeClass("fa-spinner").removeClass("faa-spin").removeClass("animated").addClass("glyphicon").addClass("glyphicon-ok");
                            if (params.successCallback) {
                                params.successCallback(response);
                            }
                            finishStage("finish");
                        },
                        errorCallback: function (response) {
                        }
                    });
                }

                generateEcp();
            }

            initialCheck();
        }
    });
})(jQuery);