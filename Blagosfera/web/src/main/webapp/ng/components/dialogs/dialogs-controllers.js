'use strict';

define([
    'app'
], function (app) {

    app.controller('DialogsCtrl', function ($scope, $log, $mdDialog,broadcastService) {
        var scope = this;

        scope.openYesNoDialog = function (data) {
            $mdDialog.show({
                controller: 'YesNoDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/yesNo.html',
                parent: angular.element(document.body),
                targetEvent: data.event,
                clickOutsideToClose: true,
                fullscreen: true,
                disableParentScroll: false,
                data: data
            }).then(function (answer) {
                if (answer === 'yes' && data.onYes) data.onYes();
                if (answer === 'no' && data.onNo) data.onNo();
            }, function () {
                if (data.onCancel) data.onCancel();
            });
        };

        scope.openInfoDialog = function (data) {
            $mdDialog.show({
                controller: 'InfoDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/information.html',
                parent: angular.element(document.body),
                targetEvent: data.event,
                clickOutsideToClose: true,
                fullscreen: true,
                disableParentScroll: false,
                data: data
            });
        };

        $scope.$on('dialog', function (event, data) {
            if (data.type === 'yesno') {
                scope.openYesNoDialog(data);
            } else if (data.type === 'info') {
                scope.openInfoDialog(data);
            }
        });
    });

    app.controller('YesNoDialogCtrl', function (data, $mdDialog) {
        this.data = data;

        this.yes = function () {
            $mdDialog.hide('yes');
        };

        this.no = function () {
            $mdDialog.hide('no');
        };

        this.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('InfoDialogCtrl', function ($mdDialog, data) {
        this.data = data;

        this.close = function () {
            $mdDialog.hide('close');
        };
    });

    app.controller('SupportRequestDialogCtrl', function ($mdDialog,$mdToast,recaptchaWidgets,supportRequestService,supportRequestTypes,broadcastService) {
        var scope = this;
        scope.theme = "";
        scope.description="";
        scope.email = "";
        scope.supportRequestTypeId = supportRequestTypes[0].id;
        scope.supportRequestTypes = supportRequestTypes;

        scope.saveRequest = function() {
            var captchaResponse = grecaptcha.getResponse(recaptchaWidgets.supportRequest);

            supportRequestService.saveRequest(scope.email,scope.theme,scope.supportRequestTypeId,scope.description,captchaResponse).then(
                function(response) {
                    if (response.data.status === 'ok') {
                        $mdDialog.hide();
                        broadcastService.send('dialog', {
                            type: 'info',
                            message: 'SUPPORT_REQUEST_SENT'
                        });
                    } else {
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent(response.data.error)
                                .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                                .parent(angular.element('md-dialog#support-request-dialog'))
                        );

                        grecaptcha.reset(recaptchaWidgets.supportRequest);
                    }
                }
            );
        };

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });
    app.controller('uploadAvatarDialogController', function ($mdDialog,userProfileService,$scope,$mdToast, broadcastService) {
        var scope = this;
        scope.requestData = {
            sourcePhotoUrl : "",
            resultPhotoUrl : "",
        };
        scope.oldRequestData = {
            sourcePhotoUrl : "",
            resultPhotoUrl : "",
        };
        $scope.$watch('dialog.requestData', function(newValue, oldValue) {
            if  ((newValue.resultPhotoUrl === scope.oldRequestData.resultPhotoUrl)){
                return;
            }
            if ((!scope.requestData.sourcePhotoUrl) || (!scope.requestData.resultPhotoUrl)){
                return;
            }
            userProfileService.saveAvatar(scope.requestData.sourcePhotoUrl,scope.requestData.resultPhotoUrl).then(function(response){
                if (response.data.status === 'ok') {
                    broadcastService.send('avatarChanged',{avatar : response.data.croppedUrl,croppedAvatar : response.data.url});
                } else {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent(response.data.error)
                            .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                            .parent(angular.element('md-dialog#support-request-dialog'))
                    );
                }
            });
        },true);
        scope.saveAvatar = function() {
            $mdDialog.hide();
        };
        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('LoginDialogCtrl', function (localStorageService, userService, $mdDialog, $mdToast, recaptchaWidgets, cancelDisabled) {
        var scope = this;
        scope.username = undefined;
        scope.password = undefined;
        scope.readOnly = false;
        scope.authSettings = localStorageService.get('authSettings');
        scope.cancelDisabled = cancelDisabled;

        if (!scope.authSettings) {
            scope.authSettings = {
                rememberMe: false,
                RASIsLocal: true,
                RASUrl: ''
            };
        }

        scope.login = function () {
            scope.readOnly = true;
            scope.username = $('form[name=loginForm] input[name=username]').val();
            scope.password = $('form[name=loginForm] input[name=password]').val();

            var captchaResponse = grecaptcha.getResponse(recaptchaWidgets.login);
            localStorageService.set('authSettings', scope.authSettings);

            userService.login(scope.username, scope.password, scope.authSettings.rememberMe, captchaResponse).then(function (response) {
                if (response.data === 'OK') {
                    $mdDialog.hide({code: 'identify'});
                } else {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Ошибка входа в систему. Проверьте введенные данные и попробуйте снова.')
                            .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                            .parent(angular.element('md-dialog#login-dialog'))
                    );

                    grecaptcha.reset(recaptchaWidgets.login);
                    scope.readOnly = false;
                }
            });
        };

        scope.loginWithFingerprint = function () {
            $mdDialog.hide({
                code: 'loginWithFingerprint',
                username: scope.username,
                rememberMe: scope.authSettings.rememberMe
            });
        };

        scope.restorePassword = function () {
            $mdDialog.hide({code: 'restorePassword'});
        };

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('LoginWithFingerprintDialogCtrl', function (localStorageService, userService, $mdDialog, $mdToast,
                                                               username, rememberMe, RASService, $interval) {
        var scope = this;
        scope.username = username;
        scope.rememberMe = rememberMe;
        scope.readOnly = false;
        scope.canLogin = false;
        scope.RASLocalUrl = 'https://localhost:36123';
        scope.RASVersion = undefined;
        scope.RASMinVersion = undefined;
        scope.RASVersionCorrect = undefined;
        scope.token = undefined;
        scope.scanInProgress = false;
        scope.scanProgress = 0;
        scope.scanTimer = undefined;

        scope.authSettings = localStorageService.get('authSettings');

        if (!scope.authSettings) {
            scope.authSettings = {
                rememberMe: false,
                RASIsLocal: true,
                RASUrl: ''
            };
        }

        var versionSupported = function (min, current) {
            var versionSupported = false;
            var minVersion = min.split('.');
            var usedVersion = current.split('.');

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

            return versionSupported;
        };

        var finger2string = function (finger) {
            if (typeof finger == "string") finger = parseInt(finger);

            switch (finger) {
                case 1 :
                    return "мизинец  левой руки";
                case 2 :
                    return "безымянный палец левой руки";
                case 3 :
                    return "средний палец левой руки";
                case 4 :
                    return "указательный палец левой руки";
                case 5 :
                    return "большой палец левой руки";
                case 6 :
                    return "большой палец правой руки";
                case 7 :
                    return "указательный палец правой руки";
                case 8 :
                    return "средний палец правой руки";
                case 9 :
                    return "безымянный палец правой руки";
                case 10 :
                    return "мизинец правой руки";
                default :
                    return "";
            }
        };

        scope.connectToRAS = function () {
            scope.readOnly = true;
            var RASUrl = scope.authSettings.RASIsLocal ? scope.RASLocalUrl : scope.authSettings.RASUrl;

            RASService.info(RASUrl).then(function (response) {
                scope.RASVersion = response.data.version;

                RASService.minVersion().then(function (response) {
                    scope.RASMinVersion = response.data;
                    scope.RASVersionCorrect = versionSupported(scope.RASMinVersion, scope.RASVersion);
                    scope.readOnly = false;

                    if (!scope.RASVersionCorrect) {
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Вы используете Сервер авторизации "БЛАГОСФЕРА" версии ' + scope.RASVersion + '. Минимальная поддерживаемая версия ' + scope.RASMinVersion + '. Пожалуйста скачайте новую версию.')
                                .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                                .parent(angular.element('md-dialog#login-with-fingerprint-dialog'))
                        );
                    }
                });
            });
        };

        scope.scanFinger = function () {
            scope.readOnly = true;
            scope.scanProgress = 0;
            scope.scanInProgress = true;
            scope.canLogin = false;

            var RASUrl = scope.authSettings.RASIsLocal ? scope.RASLocalUrl : scope.authSettings.RASUrl;

            RASService.initTokenByEmail(scope.username).then(function (response) {
                scope.token = response.data;
                scope.token.fingerString = finger2string(scope.token.finger);

                scope.scanTimer = $interval(function () {
                    scope.scanProgress += 100 / 15;
                }, 1000, 15, true);

                scope.scanTimer.then(function (count) {
                    //alert('finish');
                });

                RASService.scan(RASUrl).then(function (response) {
                    $interval.cancel(scope.scanTimer);
                    scope.scanInProgress = false;
                    scope.readOnly = false;

                    if (response.data.result === 'failed') {
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Ошибка сканирования.')
                                .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                                .parent(angular.element('md-dialog#login-with-fingerprint-dialog'))
                        );
                    } else {
                        var devhost = window.location.protocol + "//" + window.location.host;
                        //devhost = "http://10.0.2.2:8080";

                        RASService.sendencrypted(RASUrl, devhost, scope.token.ikp, scope.token.requestId).then(function (response) {
                            if (response.data.token) {
                                scope.token.token = response.data.token;
                                scope.canLogin = true;
                                scope.scanProgress = 100;
                            } else {
                                $mdToast.show(
                                    $mdToast.simple()
                                        .textContent('Ошибка сканирования. Попробуйте снова.')
                                        .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                                        .parent(angular.element('md-dialog#login-with-fingerprint-dialog'))
                                );
                            }
                        });
                    }
                });
            });
        };

        scope.login = function () {
            userService.loginWithFingerprint(scope.username, scope.token.token, scope.rememberMe, scope.token.ikp).then(function (response) {
                if (response.data === 'OK') {
                    localStorageService.set('authSettings', scope.authSettings);
                    $mdDialog.hide();
                } else {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Ошибка входа в систему. Попробуйте снова.')
                            .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                            .parent(angular.element('md-dialog#login-with-fingerprint-dialog'))
                    );
                    scope.readOnly = false;
                }
            });
        };

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('RestorePasswordDialogCtrl', function (userService, $mdDialog, $mdToast, recaptchaWidgets) {
        var scope = this;
        scope.username = undefined;
        scope.readOnly = false;

        scope.restorePassword = function () {
            var captchaResponse = grecaptcha.getResponse(recaptchaWidgets.restorePassword);

            userService.restorePassword(scope.username, captchaResponse).then(function (response) {
                if (response.data === 'OK') {
                    $mdDialog.hide();

                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('На Ваш e-mail высланы инструкции по восстановлению доступа.')
                            .position('top right').hideDelay(5000).action('OK').highlightAction(false).theme('warn-toast')
                            .parent(angular.element('div.layout_container'))
                    );
                } else {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Участник с таким e-mail не зарегистрирован или капча введена неверно.')
                            .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                            .parent(angular.element('md-dialog#password-restore-dialog'))
                        //.capsule(true)
                    );

                    grecaptcha.reset(recaptchaWidgets.restorePassword);
                }
            });
        };

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('AccessDeniedDialogCtrl', function ($mdDialog) {
        var scope = this;

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    return app;
});