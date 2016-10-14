'use strict';

define([
    'app'
], function (app) {

    app.controller('CommunitiesCtrl', function (broadcastService, communitiesService) {
        var scope = this;
        scope.communities = [];
        scope.selectedId = undefined;

        scope.openSettings = function (communityId) {
            broadcastService.send('setLocation', '/ecoadvisor/settings/' + communityId);
        };

        scope.openSessions = function (communityId) {
            broadcastService.send('setLocation', '/ecoadvisor/sessions/' + communityId);
        };

        scope.openStore = function (communityId) {
            broadcastService.send('setLocation', '/ecoadvisor/store/' + communityId);
        };

        scope.selectCommunity = function (communityId) {
            scope.selectedId = communityId;
        };

        scope.communitiesTreeOptions = {};

        scope.getSubgroups = function (community) {
            community.loadingIndicator = true;
            community.communities.length = 0;

            communitiesService.getSubgroups(community.id).then(
                function (response) {
                    community.loadingIndicator = false;
                    community.communities = response.data;
                }
            );
        };

        scope.loadingIndicator = true;

        communitiesService.getCommunities().then(
            function (response) {
                scope.loadingIndicator = false;
                scope.communities = response.data;
            }
        );
    });

    return app;
});