'use strict';

define([
    'app'
], function (app) {
    app.factory('userSettingsService', function (httpService, secureHttpService) {
        return {
            setAllowMultipleSessions: function (allow) {
                return httpService.post('/api/user/settings/set_allow_multiple_sessions.json', 'allow=' + allow, {headers: {'Content-Type': 'application/x-www-form-urlencoded'}});
            },
            closeOtherSessions: function () {
                return httpService.post('/api/user/settings/close_other_sessions.json', '', {headers: {'Content-Type': 'application/x-www-form-urlencoded'}});
            },
            initChangeEmail: function () {
                return httpService.post('/api/user/settings/init_change_email.json', '', {});
            },
            changeEmail: function (newEmail, code) {
                return httpService.post('/api/user/settings/complete_change_email.json', {
                    email: newEmail,
                    code: code
                }, {});
            },
            changePassword: function (oldPassword, newPassword) {
                return httpService.post('/api/user/settings/change_password.json', {
                    oldPassword: oldPassword,
                    newPassword: newPassword
                }, {});
            },
            changeShortLink: function (link) {
                return httpService.post('/api/user/settings/change_sharer_short_link_name.json', 'sharer_short_link_name=' + link, {headers: {'Content-Type': 'application/x-www-form-urlencoded'}});
            },
            setSetting: function (key, value) {
                return httpService.post('/api/user/settings/setting/set.json', {key: key, value: value}, {});
            },
            deleteProfile: function (key, value) {
                return httpService.post('/api/user/settings/delete_profile.json', '', {});
            },
            closeSession: function (sessionId) {
                return httpService.post('/api/user/settings/close_session.json', 'session_id=' + sessionId, {headers: {'Content-Type': 'application/x-www-form-urlencoded'}});
            },
            verifyPhone: function () {
                return httpService.get('/api/user/settings/phoneverify.json', {});
            },
            sendVerificationCode: function (phoneNumber) {
                return httpService.post('/api/user/settings/sendphoneverifycode.json', {phoneNumber : phoneNumber},{});
            },
            verifyCode: function (code) {
                return httpService.get('/api/user/settings/phoneverifycode.json', {params: {c: code}});
            },
            saveIdentificationMode: function (mode, completeCallback, errorCallback) {
                //return httpService.get('/api/user/settings/setidentificationmode.json', {params: {m: mode}});
                secureHttpService.get('/api/user/settings/setidentificationmode.json', {params: {m: mode}}, null, completeCallback, errorCallback);
            }
        }
    });

    return app;
});