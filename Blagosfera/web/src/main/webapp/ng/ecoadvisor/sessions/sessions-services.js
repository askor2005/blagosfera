'use strict';

define([
    'app'
], function (app) {

    app.factory('operatorSessionsService', function (httpService) {
        var sessionsService = {};

        sessionsService.getSessions = function (communityId, workplaceIds, page, size, sortDirection, sortColumn,
                                                operator, createdDateFrom, createdDateTo, active) {
            return httpService.get('/api/ecoadvisor/sessions.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    workplaceIds: workplaceIds,
                    page: page, size: size,
                    sortDirection: sortDirection, sortColumn: sortColumn,
                    operator: operator, createdDateFrom: createdDateFrom, createdDateTo: createdDateTo, active: active
                }
            })
        };

        sessionsService.getExchangeOperations = function (communityId, sessionId, page, size, sortDirection, sortColumn) {
            return httpService.get('/api/ecoadvisor/operations.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    sessionId: sessionId,
                    page: page, size: size,
                    sortDirection: sortDirection, sortColumn: sortColumn
                }
            })
        };

        sessionsService.getExchangeProducts = function (communityId, exchangeIds, page, size, sortDirection, sortColumn) {
            return httpService.get('/api/ecoadvisor/products.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    exchangeIds: exchangeIds,
                    page: page, size: size,
                    sortDirection: sortDirection, sortColumn: sortColumn
                }
            })
        };

        sessionsService.closeSession = function (communityId, sessionId) {
            return httpService.get('/api/ecoadvisor/closeSession.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    sessionId: sessionId
                }
            })
        };

        return sessionsService;
    });

    return app;
});