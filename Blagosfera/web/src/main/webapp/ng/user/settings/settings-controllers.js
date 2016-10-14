'use strict';

define([
    'app'
], function (app) {
    app.controller('UserSettingsCtrl', ['settings', 'userSettingsService', 'broadcastService', '$scope', '$mdMedia', '$interval', function (settings, userSettingsService, broadcastService, $scope, $mdMedia, $interval) {
        var scope = this;
        scope.$mdMedia = $mdMedia;
        scope.settings = settings;
        scope.oldPhoneNumber = scope.settings.phoneVerify.phoneNumber;
        scope.timezones = [];

        for (var i = 0; i < settings.timezones.length; ++i) {
            scope.timezones.push({
                label: settings.timezones[i] + '(' + settings.timezoneOffsets[settings.timezones[i]] + ')',
                value: settings.timezones[i]
            });
        }

        scope.currentShowEmailMode = settings.currentShowEmailMode;
        scope.currentTimezone = settings.currentTimezone;
        scope.shortNameLink = window.location.origin + "/sharer/";
        scope.code = "";
        scope.newEmail = "";
        scope.newPassword = "";
        scope.oldPassword = "";
        scope.selectedTableItem = "";
        scope.newPasswordConfirm = "";
        scope.currentTimezone = scope.settings.currentTimezone;

        scope.setAllowMultipleSessions = function () {
            userSettingsService.setAllowMultipleSessions(scope.settings.allowMultipleSessions).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.closeOtherSessions = function () {
            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'need_confirm',
                question: 'are_you_sure',
                showYes: true,
                showNo: true,
                showCancel: false,
                onYes: function () {
                    userSettingsService.closeOtherSessions().then(function (response) {
                        scope.removeSession(scope.settings.currentSessionId, true);
                    });
                }
            });
        };

        scope.removeSession = function (sessionId, exclude) {
            var userSessions = [];

            for (var i in scope.settings.userSessions) {
                if (!exclude) {
                    if (scope.settings.userSessions[i].sessionId != sessionId) {
                        userSessions.push(scope.settings.userSessions[i]);
                    }
                } else {
                    if (scope.settings.userSessions[i].sessionId == sessionId) {
                        userSessions.push(scope.settings.userSessions[i]);
                    }
                }
            }

            scope.settings.userSessions = userSessions;
        };

        scope.initChangeEmail = function () {
            userSettingsService.initChangeEmail().then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'check_code_sent',
                });
            });
        };

        scope.changeEmail = function () {
            userSettingsService.changeEmail(scope.newEmail, scope.code).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.changePassword = function () {
            if (scope.newPassword != scope.newPasswordConfirm) {
                return;
            }

            userSettingsService.changePassword(scope.oldPassword, scope.newPassword).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'password_changed',
                });
            });
        };

        scope.changeShortLink = function () {
            userSettingsService.changeShortLink(scope.settings.sharerShortLink).then(function (response) {
                scope.settings.sharerShortLink = response.data;
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.changeTimezone = function () {
            userSettingsService.setSetting("profile.timezone", scope.currentTimezone).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.changeCurrentShowEmailMode = function () {
            userSettingsService.setSetting("profile.show-email.mode", scope.currentShowEmailMode).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.deleteProfile = function () {
            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'need_confirm',
                question: 'confirm_delete_profile',
                showYes: true,
                showNo: true,
                showCancel: false,
                onYes: function () {
                    userSettingsService.deleteProfile().then(function (response) {
                        broadcastService.send('logout', {}, {});
                    });
                }
            });
        };

        scope.setInterfaceTooltipEnable = function () {
            userSettingsService.setSetting("interface.tooltip.enable", scope.settings.interfaceTooltipEnable).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.setInterfaceTooltipDelayShow = function () {
            userSettingsService.setSetting("interface.tooltip.delay.show", scope.settings.interfaceTooltipDelayShow).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.setInterfaceTooltipDelayHide = function () {
            userSettingsService.setSetting("interface.tooltip.delay.hide", scope.settings.interfaceTooltipDelayHide).then(function (response) {
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
            });
        };

        scope.closeSession = function (session) {
            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'need_confirm',
                question: 'are_you_sure',
                showYes: true,
                showNo: true,
                showCancel: false,
                onYes: function () {
                    userSettingsService.closeSession(session.sessionId).then(function (response) {
                        scope.removeSession(session.sessionId, false);
                    });
                }
            });
        };

        scope.deviceIcons = {
            "Игровая консоль": "&#xE338",
            "Коммуникатор": "&#xE32C",
            "Персональный компьютер": "&#xE30A",
            "Телевизор": "&#xE333",
            "Смартфон": "&#xE32C",
            "Планшет": "&#xE32F",
            "Переносной компьютер": "&#xE31E"
        };

        scope.getDeviceIcon = function (device) {
            var icon = scope.deviceIcons[device];

            if (!icon) {
                return "&#xE337;";
            }

            return icon + ";";
        };

        scope.getOsDefinitely = function (osFamily) {
            osFamily = osFamily.toUpperCase();

            if (osFamily.indexOf('WIN') != -1) {
                return "Windows";
            } else if (osFamily.indexOf('LINUX') != -1) {
                return "Linux";
            } else if (osFamily.indexOf('OS X') != -1) {
                return "OS X";
            } else if (osFamily.indexOf('ANDROID') != -1) {
                return "Android";
            } else if (osFamily.indexOf('IOS') != -1) {
                return "iOS";
            } else {
                return "UNKNOWN";
            }
        };

        scope.getBrowserDefinitely = function (osFamily) {
            osFamily = osFamily.toUpperCase();

            if (osFamily.indexOf('CHROM') != -1) {
                return "Chrome";
            } else if (osFamily.indexOf('FIREFOX') != -1) {
                return "Firefox";
            } else if (osFamily.indexOf('SAFARI') != -1) {
                return "Safari";
            } else if (osFamily.indexOf('IE') != -1) {
                return "IE";
            } else {
                return "UNKNOWN";
            }
        };

        scope.osIcons = {
            "Windows": "win.svg",
            "OS X": "macintosh.svg",
            "Linux": "linux.svg",
            "Android": "android.svg",
            "iOS": "ios.svg",
            "UNKNOWN": "unknown.svg"
        };

        scope.browserIcons = {
            "Chrome": "chrome.svg",
            "Firefox": "firefox.svg",
            "Safari": "safari.svg",
            "IE": "ie.svg",
            "UNKNOWN": "unknown.svg"
        };

        scope.getOsIcon = function (family) {
            var result = scope.osIcons[scope.getOsDefinitely(family)];
            return result;
        };

        scope.getBrowserIcon = function (family) {
            var result = scope.browserIcons[scope.getBrowserDefinitely(family)];
            return result;
        };

        function onVerifyPhone(response) {
            var phoneVerify = response.data;
            scope.settings.phoneVerify = phoneVerify;
            scope.oldPhoneNumber = scope.settings.phoneVerify.phoneNumber;

            if (phoneVerify.verified) return;

            if (phoneVerify.secondsLeft) {
                scope.settings.phoneVerify.timer = $interval(function () {
                    scope.settings.phoneVerify.secondsLeft--;

                    if (scope.settings.phoneVerify.secondsLeft === 0) {
                        scope.verifyPhone();
                    }
                }, 1000, phoneVerify.secondsLeft);
            }
        }

        scope.verifyPhone = function () {
            $interval.cancel(scope.settings.phoneVerify.timer);
            userSettingsService.verifyPhone().then(onVerifyPhone);
        };

        scope.sendVerificationCode = function () {
            scope.settings.verificationCode = '';
            userSettingsService.sendVerificationCode(scope.settings.phoneVerify.phoneNumber).then(onVerifyPhone);
        };

        scope.verifyCode = function () {
            scope.settings.verificationInProgress = true;

            userSettingsService.verifyCode(scope.settings.verificationCode).then(function (response) {
                $interval.cancel(scope.settings.phoneVerify.timer);
                scope.settings.verificationInProgress = false;
                onVerifyPhone(response);
            });
        };

        scope.saveIdentificationMode = function () {
            /*userSettingsService.saveIdentificationMode(scope.settings.identificationMode).then(function (response) {
                scope.settings.identificationMode = response.data;
            });*/

            userSettingsService.saveIdentificationMode(scope.settings.identificationMode, function (response) {
                scope.settings.identificationMode = response.data;
            });
        };

        scope.verifyPhone();
    }]);

    return app;
});