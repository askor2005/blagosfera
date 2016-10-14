'use strict';

define([
    'app'
], function (app) {

    app.factory('invitationAcceptService', function (httpService) {
        var invitationAcceptService = {};

        invitationAcceptService.loadAcceptPageData = function (hash) {
            return httpService.post('/invitationaccept/accept_page_data.json', {'hash' : hash}, {headers: {'Cache-Control': 'no-cache'}});
        };

        invitationAcceptService.acceptInvitation = function(hash, password, needSendPassword, base64AvatarSrc, base64Avatar) {
            return httpService.post('/invitationaccept/accept.json', {
                'hash' : hash,
                'password' : password,
                'needSendPassword' : needSendPassword,
                'base64AvatarSrc' : base64AvatarSrc,
                'base64Avatar' : base64Avatar
            }, {headers: {'Cache-Control': 'no-cache'}});
        };

        invitationAcceptService.rejectInvitation = function(hash) {
            return httpService.post('/invitationaccept/reject.json', {
                'hash' : hash
            }, {headers: {'Cache-Control': 'no-cache'}});
        };

        return invitationAcceptService;
    });

    return app;
});