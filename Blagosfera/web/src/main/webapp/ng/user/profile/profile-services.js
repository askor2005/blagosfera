'use strict';

define([
    'app'
], function (app) {

    app.factory('userProfileService', function (httpService) {
        var userProfileService = {};

        userProfileService.loadProfile = function (ikp) {
            return httpService.get('/api/user/profile.json', {params: {ikp: ikp}});
        };

        userProfileService.loadInvitation = function (userId) {
            return httpService.get('/api/user/invitation.json', {params: {user_id: userId}});
        };

        userProfileService.saveProfile = function (profile) {
            return httpService.post('/api/user/profile/save.json', profile,{});
        };
        userProfileService.saveAvatar = function (url,croppedUrl) {
            return httpService.post('/api/user/avatar/save.json', {url: url,croppedUrl:croppedUrl},{});
        };
        userProfileService.addContact = function (otherId,groupIds) {
            return httpService.post('/api/user/contacts/add.json', {otherId : otherId,groupIds : groupIds},{});
        };
        userProfileService.deleteContact = function (otherId) {
            return httpService.get('/api/user/contacts/delete.json?other_id='+otherId, {},{});
        };
        userProfileService.deleteRequest = function (id) {
            return httpService.get('/api/user/profile/deleteRequest.json?request_id='+id, {},{});
        };

        return userProfileService;
    });

    return app;
});