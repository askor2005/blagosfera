'use strict';

define([
    'app'
], function (app) {
    app.controller('SupportRequestsCtrl', ['supportRequestService','info',function (supportRequestService,info) {
        var scope = this;
        scope.transactions = [];
        scope.requests = [];
        scope.query = {
            limit: 20,
            page: 1
        };
        scope.total = info.totalRequestsCount;
        scope.refreshRequests = function () {
            scope.loadRequests(scope.query.page, scope.query.limit);
        };
        scope.loadRequests = function (page, perPage) {
            supportRequestService.loadNotResolvedRequests(page, perPage).then(
                function (requests) {
                    scope.requests = requests;
                }
            );
        };
        scope.resolveRequest = function(requestId) {
          supportRequestService.resolveRequest(requestId).then(function(response) {
              scope.refreshRequests();
          });
        }
        scope.limitOptions = [];
        scope.refreshRequests();

    }]);
    return app;
});