'use strict';

define([
    'app'
], function (app) {

    app.factory('communitiesService', function (httpService) {
        var communitiesService = {};

        communitiesService.getCommunities = function () {
            return httpService.get('/api/ecoadvisor/communities.json', {headers: {'Cache-Control': 'no-cache'}})
        };

        communitiesService.getSubgroups = function (communityId) {
            return httpService.get('/api/ecoadvisor/subgroups.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            })
        };

        communitiesService.getCommunity = function (communityId) {
            return httpService.get('/api/ecoadvisor/community.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            })
        };

        communitiesService.getWorkplaces = function (communityId) {
            return httpService.get('/api/ecoadvisor/workplaces.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            })
        };

        communitiesService.getProductsFromStore = function (communityId, page, size, sortDirection, sortColumn, productGroup) {
            return httpService.get('/api/ecoadvisor/store.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    page: page, size: size,
                    sortDirection: sortDirection,
                    sortColumn: sortColumn,
                    productGroupId: productGroup ? productGroup.id : null
                }
            })
        };

        return communitiesService;
    });

    return app;
});