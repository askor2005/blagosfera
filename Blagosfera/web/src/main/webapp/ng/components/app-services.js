'use strict';

define([
    'app',
    'petrovich'
], function (app, petrovich) {

    app.factory('broadcastService', function ($rootScope) {
        return {
            send: function (topic, data) {
                $rootScope.$broadcast(topic, data);
            },
            sendWithTitle: function (topic, title, data) {
                this.send(topic, {"title": title, "data": data});
            }
        }
    });

    app.factory('translateService', function ($translatePartialLoader, $translate, $filter) {
        return {
            translate: function (key) {
                return $filter('translate')(key);
            },
            translateAsync: function (key) {
                return $translate(key);
            },
            loadTranslations: function (parts) {
                return function () {
                    for (var i = 0; i < parts.length; i++) {
                        $translatePartialLoader.addPart(parts[i]);
                    }

                    return $translate.refresh();
                }
            }
        }
    });

    app.factory('httpService', function ($http, $q, broadcastService) {
        var showError = function (message) {
            broadcastService.send('dialog', {
                type: 'info',
                title: 'Ошибка обработки запроса.',
                message: message
            });
        };

        var showResult = function (message) {
            broadcastService.send('dialog', {
                type: 'info',
                title: 'Запрос выполнен успешно.',
                message: message
            });
        };

        var debug = function (message, data) {
            broadcastService.sendWithTitle('debug', message, data);
        };

        var httpService = {};
        httpService.getJson = function (url, data,params) {
            var defer = $q.defer();
            $http({
                method: "GET",
                url: url,
                params : params,
                data : data
            }).then(
                function (response) {
                    if (response.data.result && response.data.result === 'error') {
                        showError(response.data.message);
                        defer.reject(response);
                    } else {
                        defer.resolve(response);
                    }
                }, function (response) {
                    if ((response.status === 401) || (response.status === 403)) {
                        broadcastService.send('accessDenied', response);
                    } else {
                        if (response.status && response.statusText)
                            showError('Ошибка: ' + response.status + ' ' + response.statusText);
                        else
                            showError('Ошибка: неверный ответ от сервера');
                    }

                    debug('httpService.get failed', {
                        url: url,
                        params: params,
                        response: response
                    });

                    defer.reject(response);
                }
            );

            return defer.promise;
        };

        httpService.get = function (url, params) {
            var defer = $q.defer();

            $http.get(url, params).then(
                function (response) {
                    if (response.data.result && response.data.result === 'error') {
                        showError(response.data.message);
                        defer.reject(response);
                    } else {
                        defer.resolve(response);
                    }
                }, function (response) {
                    if ((response.status === 401) || (response.status === 403)) {
                        broadcastService.send('accessDenied', response);
                    } else {
                        if (response.status && response.statusText)
                            showError('Ошибка: ' + response.status + ' ' + response.statusText);
                        else
                            showError('Ошибка: неверный ответ от сервера');
                    }

                    debug('httpService.get failed', {
                        url: url,
                        params: params,
                        response: response
                    });

                    defer.reject(response);
                }
            );

            return defer.promise;
        };

        httpService.post = function (url, data, params) {
            var defer = $q.defer();
            $http.post(url, data, params).then(
                function (response) {
                    if (response.data.result && response.data.result === 'error') {
                        if (params.showError == null || params.showError) {
                            showError(response.data.message);
                        }
                        defer.reject(response);
                    } else {
                        if (params.showResult != null && params.showResult) {
                            showResult(params.resultMessage);
                        }
                        defer.resolve(response);
                    }
                }, function (response) {
                    if (response.status && response.statusText)
                        showError('Ошибка: ' + response.status + ' ' + response.statusText);
                    else
                        showError('Ошибка: неверный ответ от сервера');

                    debug('httpService.post failed', {
                        url: url,
                        data: data,
                        params: params,
                        response: response
                    });
                    defer.reject(response);
                }
            );

            return defer.promise;
        };

        return httpService;
    });

    app.factory('secureHttpService', function (httpService, userService, broadcastService, RASService, $mdDialog) {
        var secureHttpService = {};
        secureHttpService.identificationEnv = 'ras';

        if (typeof RegistratorTablet === 'undefined') {
        } else {
            secureHttpService.identificationEnv = 'tablet';
        }

        function doRequest(mode, url, params, data, ikp, token, completeCallback, errorCallback) {
            if (!params.headers) params.headers = {};

            params.headers.IKP = ikp;
            params.headers.FINGER_TOKEN = token;
            
            if ('get' === mode) {
                httpService.get(url, params).then(function (response) {
                    if (completeCallback) completeCallback(response);
                }, function (response) {
                    if (errorCallback) errorCallback(response);
                });
            } else {
                httpService.post(url, data, params).then(function (response) {
                    if (completeCallback) completeCallback(response);
                }, function (response) {
                    if (errorCallback) errorCallback(response);
                });
            }
        }

        function startRequest(mode, url, params, data, ikp, completeCallback, errorCallback) {
            userService.identify(ikp).then(function (identifyResponse) {
                if (!identifyResponse.data.ikp) return;

                ikp = identifyResponse.data.ikp;

                RASService.initTokenByIkp(ikp).then(function (tokenResponse) {
                    if (!tokenResponse.data.requestId) return;

                    if ('fingerprint' === identifyResponse.data.identificationMode) {
                        if ('tablet' === secureHttpService.identificationEnv) {
                            window.tabletScanFingerCallback = function (result) {
                                if (result === 0) {
                                    RegistratorTablet.getToken(ikp, tokenResponse.data.requestId, 'tabletGetTokenCallback');
                                } else {
                                    broadcastService.send('dialog', {
                                        type: 'info',
                                        title: 'Ошибка сканирования',
                                        message: 'Ошибка сканирования'
                                    });
                                }
                            };

                            window.tabletGetTokenCallback = function(token) {
                                doRequest(mode, url, params, data, ikp, token, completeCallback, errorCallback);
                            };

                            RegistratorTablet.scanFinger(tokenResponse.data.finger, 30, 'tabletScanFingerCallback');
                        } else {
                            $mdDialog.show({
                                controller: 'FingerprintConfirmDialogCtrl',
                                controllerAs: 'dialog',
                                templateUrl: '/ng/components/dialogs/fingerprintConfirm.html',
                                parent: angular.element(document.body),
                                targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                                userInfo: identifyResponse.data,
                                requestInfo: tokenResponse.data
                            }).then(function (answer) {
                                if (answer && answer.token) {
                                    doRequest(mode, url, params, data, ikp, answer.token, completeCallback, errorCallback);
                                } else {
                                    broadcastService.send('dialog', {
                                        type: 'info',
                                        title: 'Ошибка обработки запроса.',
                                        message: 'Сервис проверки отпечатков недоступен, либо адрес Сервера Авторизации БЛАГОСФЕРА указан неверно. Проверьте правильность адреса Сервера Авторизации БЛАГОСФЕРА и повторите попытку.'
                                    });
                                }
                            });
                        }
                    } else if ('sms' === identifyResponse.data.identificationMode) {
                        $mdDialog.show({
                            controller: 'SmsConfirmDialogCtrl',
                            controllerAs: 'dialog',
                            templateUrl: '/ng/components/dialogs/smsConfirm.html',
                            parent: angular.element(document.body),
                            targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                            userInfo: identifyResponse.data,
                            secondsLeft: tokenResponse.data.secondsLeft,
                            requestId: tokenResponse.data.requestId
                        }).then(function (answer) {
                            doRequest(mode, url, params, data, ikp, answer.token, completeCallback, errorCallback);
                        });
                    }
                }, function (response) {
                    broadcastService.send('dialog', {
                        type: 'info',
                        title: 'Ошибка инициализации токена',
                        message: 'Ошибка инициализации токена'
                    });
                });
            }, function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    title: 'Ошибка обработки запроса',
                    message: 'Ошибка обработки запроса'
                });
            });
        }

        secureHttpService.get = function (url, params, ikp, completeCallback, errorCallback) {
            startRequest('get', url, params, null, ikp, completeCallback, errorCallback);
        };

        secureHttpService.post = function (url, data, params, ikp) {
            startRequest('post', url, params, data, ikp, completeCallback, errorCallback);
        };

        return secureHttpService;
    });

    app.factory('appService', function ($q, $rootScope, httpService, broadcastService) {
        var appService = {};

        appService.loadDeps = function (deps) {
            return function () {
                var q = $q.defer();

                if (deps.length == 0) {
                    q.resolve();
                } else {
                    require(deps,
                        function (response) {
                            $rootScope.$apply(function () {
                                q.resolve();
                            });
                        }, function (error) {
                            $rootScope.$apply(function () {
                                q.reject(error);
                            });
                        });
                }

                return q.promise;
            }
        };

        appService.loadCtrlParam = function (url) {
            return function () {
                var q = $q.defer();

                httpService.get(url, {headers: {'Cache-Control': 'no-cache'}}).then(
                    function (response) {
                        q.resolve(response.data)
                    }, function (response) {
                        broadcastService.send('routeChangeCancel');
                        broadcastService.send('back');
                        q.reject(response);
                    }
                );

                return q.promise;
            }
        };

        return appService;
    });

    app.factory('supportRequestService', function (httpService, $q) {
        var supportRequestService = {};

        supportRequestService.saveRequest = function (email, theme, supportRequestTypeId, description, captcha) {
            return httpService.post('/api/support/requests/save.json', {
                email: email, theme: theme,
                supportRequestTypeId: supportRequestTypeId, description: description, captcha: captcha
            }, {headers: {'Cache-Control': 'no-cache'}});
        };

        supportRequestService.getInitFormInfo = function () {
            return httpService.get('/api/support/requests/info.json', {}, {headers: {'Cache-Control': 'no-cache'}});
        };

        supportRequestService.loadNotResolvedRequests = function (page, perPage) {
            var params = {};
            params.page = page;
            params.perPage = perPage;
            var q = $q.defer();
            httpService.get('/api/support/requests/admin/search.json?page=' + page + "&perPage=" + perPage, {}).then(
                function (response) {
                    q.resolve(response.data)
                }, function (response) {
                    q.reject(response);
                });
            return q.promise;
        };

        supportRequestService.resolveRequest = function (requestId) {
            var q = $q.defer();
            httpService.get('/api/support/requests/admin/resolve.json?id=' + requestId, {}).then(
                function (response) {
                    q.resolve(response.data)
                }, function (response) {
                    q.reject(response);
                });
            return q.promise;
        };

        return supportRequestService;
    });

    app.factory('userService', function (httpService) {
        var userService = {};

        userService.identify = function (ikp) {
            return httpService.get('/api/user/identify.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {ikp: ikp}
            })
        };

        userService.login = function (username, password, rememberMe, captchaResponse) {
            return httpService.get('/api/user/login.json', {
                params: {
                    u: username,
                    p: password,
                    r: rememberMe,
                    c: captchaResponse
                }
            });
        };

        userService.loginWithFingerprint = function (username, token, rememberMe, ikp) {
            return httpService.post('/api/user/loginWithFingerprint.json', {}, {
                params: {u: username, r: rememberMe},
                headers: {'FINGER_TOKEN': token, 'IKP': ikp}
            })
        };

        userService.logout = function () {
            return httpService.get('/api/user/logout.json', {})
        };

        userService.restorePassword = function (username, captchaResponse) {
            return httpService.post('/api/user/restorePassword.json', {}, {
                params: {u: username, c: captchaResponse}
            })
        };

        userService.loginWithFingerprintStart = function (username) {
            return httpService.post('/api/user/loginWithFingerprintStart.json', {}, {
                params: {u: username}
            })
        };

        return userService;
    });

    app.factory('RASService', function (httpService) {
        var RASService = {};

        RASService.info = function (RASUrl) {
            return httpService.get(RASUrl + '/info.sma', {
                params: {jsonMode: "yes"}
            });
        };

        RASService.minVersion = function () {
            return httpService.get('/ras/version/min', {});
        };

        RASService.initTokenByIkp = function (ikp) {
            return httpService.get('/finger/inittoken.json', {params: {ikp: ikp}});
        };

        RASService.initTokenByEmail = function (username) {
            return httpService.get('/finger/initTokenByEmail.json', {params: {u: username}});
        };

        RASService.scan = function (RASUrl) {
            return httpService.get(RASUrl + '/scan.sma', {
                params: {
                    combined: "no",
                    jsonMode: "yes",
                    timeout: 15
                }
            });
        };

        RASService.sendencrypted = function (RASUrl, devhost, ikp, requestId) {
            /*return httpService.post(RASUrl + '/sendencrypted.sma', {}, {
             params: {
             devhost : devhost,
             ikp : ikp,
             request_id : requestId,
             jsonMode : "yes",
             type : "finger"
             }});*/

            return httpService.post(RASUrl + '/sendencrypted.sma', 'devhost=' + devhost + '&ikp=' + ikp + '&request_id=' + requestId + '&jsonMode=yes&type=finger',
                {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
        };

        return RASService;
    });

    app.factory('petrovichService', function () {
        var petrovichService = {};

        petrovichService.convertName = function (gender, first, middle, last, targetCase) {
            var person = {
                gender: gender,
                first: first,
                middle: middle,
                last: last
            };

            return petrovich(person, targetCase);
        };

        petrovichService.convertNameAndJoin = function (gender, first, middle, last, targetCase) {
            var person = petrovichService.convertName(gender, first, middle, last, targetCase);
            return person.last + ' ' + person.first + ' ' + person.middle;
        };

        return petrovichService;
    });

    app.factory('useragentService', function ($window, $q, cookieService) {
        var useragentService = {};

        useragentService.getBrowser = function () {
            var browsers = {chrome: /chrome/i, safari: /safari/i, firefox: /firefox/i, ie: /internet explorer/i};
            var useragent = $window.navigator.userAgent;

            for (var key in  browsers) {
                if (browsers[key].test(useragent)) {
                    return key;
                }
            }
        };

        useragentService.isBrowserCompatible = function () {
            var supportedBrowsers = ['chrome'];
            return (supportedBrowsers.indexOf(this.getBrowser()) != -1);
        };

        useragentService.checkBrowserCompability = function () {
            var deferred = $q.defer();

            setTimeout(function () {
                if (!useragentService.isBrowserCompatible()) {
                    if (!cookieService.exists("wrong_browser_alerted")) {
                        deferred.reject();
                        return;
                    }
                }

                deferred.resolve();
            }, 0);

            return deferred.promise;
        };

        useragentService.setNotified = function () {
            var q = $q.defer();

            setTimeout(function () {
                var exp = new Date();
                exp.setDate(exp.getDate() + 30);//новое напоминание о необходимости переключиться на chrome через 30 дней
                cookieService.set("wrong_browser_alerted", "wrong_browser_alerted", exp);
                q.resolve();
            }, 0);

            return q.promise;
        };

        return useragentService;
    });

    app.factory('cookieService', function ($cookies) {
        var cookieService = {};

        cookieService.get = function (key) {
            return $cookies.get(key);
        };

        cookieService.set = function (key, value, exp) {
            if (exp) {
                $cookies.put(key, value, {'expires': exp});
            } else {
                $cookies.put(key, value);
            }
        };

        cookieService.exists = function (key) {
            var result = $cookies.get(key);
            return !!result;
        };
        cookieService.delete = function(key) {
          $cookies.remove(key, { path: '/' });
        };

        return cookieService;
    });
    app.factory('jivositeService', function (httpService,cookieService,$q) {
        var jivositeService = {};

        jivositeService.getContactInfo = function () {
            var q = $q.defer();
            httpService.get('/api/user/jivosite/info.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {}
            }).then(function (response) {
                q.resolve(response.data);
            }, function (response) {
                q.reject(response);
            });
            return q.promise;
        };
        jivositeService.setContactData = function (name,email,phone,description) {
            jivo_api.setContactInfo(
                {
                    name: name,
                    email: email,
                    phone: phone,
                    description : description

                }
            );
        };
        jivositeService.clearContactData = function (jivositeKey) {
            cookieService.delete('jv_client_name_'+jivositeKey);
            cookieService.delete('jv_email_'+jivositeKey);
            cookieService.delete('jv_phone_'+jivositeKey);
        };
        return jivositeService;
    });

    return app;
});