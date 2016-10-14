'use strict';

define([
    'app'
], function (app) {
    app.factory('passportService', function (httpService) {
        var userProfileService = {};
        userProfileService.saveSignature = function (signature) {
            return httpService.post('/api/user/profile/signature/save.json', {signature: signature},{});
        };
        return userProfileService;
    });
    return app;
});