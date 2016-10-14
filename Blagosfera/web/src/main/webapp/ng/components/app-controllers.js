'use strict';

define([
    'app'
], function (app) {
    app.controller('AppCtrl', function ($scope, $location, $mdDialog, $route,
                                        appService, userService,
                                        $mdToast, broadcastService, $q, $mdSidenav,
                                        navigationService, $interval, stompService,
                                        $translatePartialLoader, $translate, translateService, $rootScope, useragentService, cookieService, supportRequestService,jivositeService) {
        $translatePartialLoader.addPart('app');

        setTimeout(function () {
            $translate.refresh();
        }, 0);

        var scope = this;
        scope.appLoaded = false;
        scope.history = [];
        scope.userDetails = {
            authorised: undefined,
            availableAreas: ['public'],
            username: '',
            ikp: '',
            avatar: '',
            shortName: '',
            verified: false
        };

        scope.recaptchaWidgets = {
            login: undefined,
            restorePassword: undefined,
            supportRequest: undefined
        };

        scope.notificationsCount = 55;

        /*$interval(function () {
         scope.notificationsCount += 1;
         }, 1000, 15, true);*/

        scope.navigationTreeOptions = {};

        scope.openNavigationPane = function () {
            if (scope.userDetails.authorised) {
                return $mdSidenav('left-sidenav').toggle();
            } else {
                var defer = $q.defer();
                defer.reject();
                return defer.promise;
            }
        };

        scope.closeNavigationPane = function () {
            return $mdSidenav('left-sidenav').close();
        };

        scope.setLocation = function (path) {
            if (path) {
                scope.closeNavigationPane();
                $location.path(path);
            }
        };

        $scope.$on('setLocation', function (event, path) {
            scope.setLocation(path);
        });

        $scope.$on('getUserDetails', function (event, defer) {
            defer.resolve(scope.userDetails);
        });

        $scope.$on('isSuperAdmin', function (event, defer) {
            var result = false;
            if (scope.userDetails.roles != null) {
                for (var index in scope.userDetails.roles) {
                    var role = scope.userDetails.roles[index];
                    if (role.name == "SUPERADMIN") {
                        result = true;
                        break;
                    }
                }
            }
            defer.resolve(result);
        });

        $scope.$on('accessDenied', function (event, response) {
            if (scope.userDetails.authorised === true) {
                scope.accessDeniedDialog();
            } else {
                scope.loginDialog(true);
            }
        });

        $scope.$on('$routeChangeStart', function (event, next, current) {
            //console.log(event, next, current);
            //console.log(scope.userDetails);
            //if (next.$$route && (next.$$route.controllerAs === 'communities')) {
            //    event.preventDefault();
            //    broadcastService.send('routeChangeCancel', event, next, current);
            //    scope.setLocation('/p/76');
            //}
        });

        $scope.$on('$routeChangeSuccess', function (event, current, previous) {
            scope.history.push($location.$$path);
            scope.appLoaded = true;
            //console.log(event, current, previous);
            //console.log(current.area);
        });

        scope.back = function () {
            var prevUrl = scope.history.length > 1 ? scope.history.splice(-2)[0] : "/";
            scope.setLocation(prevUrl);
        };

        $scope.$on('back', function () {
            scope.back();
        });

        scope.navigationContext = navigationService.navigationMenu;
        scope.userMenu = navigationService.userMenu;

        scope.navigationMenuItemClick = function ($event, treeNode, item) {
            if (item.path) {
                scope.setLocation(item.path);
            } else if (item.expandable) {
                $event.stopPropagation();

                if (treeNode.collapsed) {
                    treeNode.expand();
                    item.collapsed = false;

                    if (item.lazyLoad) {
                        navigationService.loadLazyMenu(item.id);
                    }
                } else {
                    treeNode.collapse();
                    item.collapsed = true;
                }
            } else if (item.switchMenu) {
                scope.navigateForward(item);
            }
        };

        scope.navigateForward = function (item) {
            scope.navigationContext = item;

            if (item.lazyLoad) {
                navigationService.loadLazyMenu(item.id);
            }
        };

        scope.navigateBack = function (item) {
            if (item.parentId) {
                var parentItem = navigationService.findMenuItem(item.parentId);
                scope.navigateForward(parentItem);

                /*if (parentItem.switchMenu) {
                 scope.navigateForward(parentItem);
                 } else {
                 scope.navigationContext = navigationService.navigationMenu;
                 }*/
            } else {
                scope.navigationContext = navigationService.navigationMenu;
            }
        };

        scope.identify = function () {
            var defer = $q.defer();

            userService.identify().then(function (response) {
                scope.userDetails = response.data;

                if (scope.userDetails.authorised) {
                    navigationService.loadMenu();
                    navigationService.loadUserMenu();
                    stompService.connect();
                } else {
                    navigationService.clearMenu();
                    stompService.disconnect();
                }

                defer.resolve(response);
            }, function (response) {
                defer.reject(response);
            });

            return defer.promise;
        };
        scope.setJivositeId = function (jivositeId) {
            scope.jivositeId = jivositeId;
        };
        scope.setJivositeData = function (jivositeId) {
            scope.jivositeId = jivositeId;
            jivositeService.clearContactData(scope.jivositeId);
            jivositeService.getContactInfo().then(function(response){
                if (response.authorized) {
                    jivositeService.setContactData(response.userInfo.name,response.userInfo.email,response.userInfo.phone,"");
                }
            });
        };

        $scope.$on('loginDialog', function () {
            scope.loginDialog();
        });
        scope.loginDialog = function (cancelDisabled) {
            $mdDialog.show({
                controller: 'LoginDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/login.html',
                parent: angular.element(document.body),
                targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                recaptchaWidgets: scope.recaptchaWidgets,
                cancelDisabled: cancelDisabled,
                onComplete: function () {
                    scope.recaptchaWidgets.login = grecaptcha.render('g-recaptcha-login', {
                        'sitekey': '6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc'
                    });
                }
            }).then(function (result) {
                if (result.code === 'identify') {
                    scope.identify().then(function () {
                        $route.reload();
                        if (scope.jivositeId) {
                            scope.setJivositeData(scope.jivositeId);
                        }
                    });
                } else if (result.code === 'restorePassword') {
                    scope.restorePasswordDialog(cancelDisabled);
                } else if (result.code === 'loginWithFingerprint') {
                    scope.loginWithFingerprintDialog(result.username, result.rememberMe, cancelDisabled);
                }
            });
        };

        scope.supportRequestDialog = function () {
            supportRequestService.getInitFormInfo().then(function (response) {
                $mdDialog.show({
                    controller: 'SupportRequestDialogCtrl',
                    controllerAs: 'dialog',
                    templateUrl: '/ng/components/dialogs/supportRequest.html',
                    parent: angular.element(document.body),
                    recaptchaWidgets: scope.recaptchaWidgets,
                    supportRequestTypes: response.data.supportRequestTypes,
                    targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                    onComplete: function () {
                        scope.recaptchaWidgets.supportRequest = grecaptcha.render('g-recaptcha-support-request', {
                            'sitekey': '6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc'
                        });
                    }
                }).then(function (result) {

                });
            });
        };

        scope.loginWithFingerprintDialog = function (username, rememberMe, cancelDisabled) {
            $mdDialog.show({
                controller: 'LoginWithFingerprintDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/loginWithFingerprint.html',
                parent: angular.element(document.body),
                targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                username: username,
                rememberMe: rememberMe,
                cancelDisabled: cancelDisabled
            }).then(function (result) {
                scope.identify().then(function () {
                    $route.reload();
                });
            }, function (result) {
                scope.loginDialog(cancelDisabled);
            });
        };

        scope.restorePasswordDialog = function (cancelDisabled) {
            $mdDialog.show({
                controller: 'RestorePasswordDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/restorePassword.html',
                parent: angular.element(document.body),
                targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                recaptchaWidgets: scope.recaptchaWidgets,
                onComplete: function () {
                    scope.recaptchaWidgets.restorePassword = grecaptcha.render('g-recaptcha-restore', {
                        'sitekey': '6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc'
                    });
                }
            }).then(function (result) {
                scope.loginDialog(cancelDisabled);
            }, function (result) {
                scope.loginDialog(cancelDisabled);
            });
        };

        scope.accessDeniedDialog = function () {
            $mdDialog.show({
                controller: 'AccessDeniedDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/accessDenied.html',
                parent: angular.element(document.body),
                targetEvent: false, clickOutsideToClose: true, fullscreen: true, disableParentScroll: false
            });
        };

        scope.logout = function () {
            userService.logout().then(function () {
                scope.identify().then(function () {
                    scope.setLocation("/");
                    if (scope.jivositeId) {
                        scope.setJivositeData(scope.jivositeId);
                    }
                });
            });
        };

        scope.openProfile = function () {
            if (scope.userDetails.authorised) scope.setLocation('/user/profile/' + scope.userDetails.ikp);
        };

        scope.openSettings = function () {
            if (scope.userDetails.authorised) scope.setLocation('/user/settings');
        };

        scope.openInvitations = function () {
            if (scope.userDetails.authorised) scope.setLocation('/invites');
        };

        scope.openAccountBalance = function () {
            if (scope.userDetails.authorised) scope.setLocation('/account');
        };

        scope.identify();
        $scope.$on('avatarChanged', function (avatarCropped,data) {
            scope.userDetails.avatar = data.croppedAvatar;
        });
        useragentService.checkBrowserCompability().then(function () {
        }, function () {
            useragentService.setNotified();

            broadcastService.send('dialog', {
                type: 'info',
                message: 'BROWSER_ALERT',
                linkMessage: 'BY_LINK',
                link: "https://www.google.ru/chrome/browser"
            });
        });
    });

    app.controller('SmsConfirmDialogCtrl', function ($mdDialog, $interval, $mdToast, httpService, RASService,
                                                     userInfo, secondsLeft, requestId) {
        var scope = this;
        scope.userInfo = userInfo;
        scope.secondsLeft = secondsLeft;
        scope.requestId = requestId;
        scope.verificationCode = undefined;

        var timer;

        function startTimer(seconds) {
            scope.secondsLeft = seconds;

            if (timer) $interval.cancel(timer);

            if (scope.secondsLeft) {
                timer = $interval(function () {
                    scope.secondsLeft--;
                }, 1000, scope.secondsLeft);
            }
        }

        startTimer(secondsLeft);

        scope.newCode = function () {
            RASService.initTokenByIkp(scope.userInfo.ikp).then(function (tokenResponse) {
                scope.requestId = tokenResponse.data.requestId;
                startTimer(tokenResponse.data.secondsLeft);
            });
        };

        scope.confirm = function () {
            httpService.post('/finger/verifytoken.json', {}, {
                params: {
                    i: scope.userInfo.ikp,
                    r: scope.requestId,
                    c: scope.verificationCode
                }
            }).then(function (response) {
                $mdDialog.hide({token: response.data.token});
            });
        };

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    app.controller('FingerprintConfirmDialogCtrl', function (localStorageService, userService, $mdDialog, $mdToast,
                                                             RASService, $interval, userInfo, requestInfo) {
        var scope = this;
        scope.readOnly = false;

        scope.RASLocalUrl = 'https://localhost:36123';
        scope.RASVersion = undefined;
        scope.RASMinVersion = undefined;
        scope.RASVersionCorrect = undefined;

        scope.scanInProgress = false;
        scope.scanProgress = 0;
        scope.scanTimer = undefined;

        scope.userInfo = userInfo;
        scope.requestInfo = requestInfo;
        scope.fingerString = finger2string(scope.requestInfo.finger);

        scope.authSettings = localStorageService.get('authSettings');

        if (!scope.authSettings) {
            scope.authSettings = {
                rememberMe: false,
                RASIsLocal: true,
                RASUrl: ''
            };
        }

        function versionSupported(min, current) {
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
        }

        function finger2string(finger) {
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
        }

        function showError(message) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent(message)
                    .position('bottom left').hideDelay(5000).action('OK').highlightAction(false).theme('error-toast')
                    .parent(angular.element('md-dialog#fingerprint-dialog'))
            );
        }

        scope.confirm = function() {
            scope.readOnly = true;
            var RASUrl = scope.authSettings.RASIsLocal ? scope.RASLocalUrl : scope.authSettings.RASUrl;

            localStorageService.set('authSettings', scope.authSettings);

            RASService.info(RASUrl).then(function (response) {
                scope.RASVersion = response.data.version;

                RASService.minVersion().then(function (response) {
                    scope.RASMinVersion = response.data;
                    scope.RASVersionCorrect = versionSupported(scope.RASMinVersion, scope.RASVersion);
                    scope.readOnly = false;

                    if (scope.RASVersionCorrect) {
                        scanFinger();
                    } else {
                        showError('Вы используете Сервер авторизации "БЛАГОСФЕРА" версии ' + scope.RASVersion + '. Минимальная поддерживаемая версия ' + scope.RASMinVersion + '. Пожалуйста скачайте новую версию.');
                    }
                });
            });
        };

        function scanFinger() {
            scope.readOnly = true;
            scope.scanProgress = 0;
            scope.scanInProgress = true;

            var RASUrl = scope.authSettings.RASIsLocal ? scope.RASLocalUrl : scope.authSettings.RASUrl;

            scope.scanTimer = $interval(function () {
                scope.scanProgress += 100 / 15;
            }, 1000, 15, true);

            RASService.scan(RASUrl).then(function (response) {
                $interval.cancel(scope.scanTimer);
                scope.scanInProgress = false;
                scope.scanProgress = 0;

                if (response.data.result === 'failed') {
                    showError('Ошибка сканирования.');
                    scope.readOnly = false;
                } else {
                    var devhost = window.location.protocol + "//" + window.location.host;
                    //devhost = "http://10.0.2.2:8080";

                    RASService.sendencrypted(RASUrl, devhost, scope.userInfo.ikp, scope.requestInfo.requestId).then(function (response) {
                        if (typeof response.data == 'string') {
                            //showError('Сервис проверки отпечатков недоступен. Повторите попытку позже.');
                            $mdDialog.hide({token: null});
                        } else {
                            $mdDialog.hide({token: response.data.token});
                        }
                    });
                }
            });
        }

        scope.cancel = function () {
            $mdDialog.cancel();
        };
    });

    return app;
});