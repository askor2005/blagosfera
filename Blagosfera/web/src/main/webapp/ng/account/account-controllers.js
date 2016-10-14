'use strict';

define([
    'app'
], function (app) {

    app.controller('AccountCtrl', function (broadcastService, $scope, accService, acc) {
        var scope = this;

        scope.transactions = [];
        scope.accounts = acc.accounts;

        scope.totalBalance = acc.total;

        scope.total = acc.totalTransactions;

        scope.from = new Date();
        scope.from.setHours(0, 0, 0);
        scope.from.setDate(scope.from.getDate() - 30);

        scope.to = new Date();
        scope.to.setHours(0, 0, 0);
        scope.to.setDate(scope.to.getDate() + 1);

        scope.accountTypeId = "";

        scope.count = 100;

        scope.query = {
            limit: 10,
            page: 1
        };

        scope.transactionState = '';
        scope.transactionDirection = '';

        scope.refreshTransactions = function () {
            scope.loadTransactions(scope.query.page, scope.query.limit);
        };

        scope.loadTransactions = function (page, perPage) {
            accService.loadTransactions(scope.accountTypeId, scope.from, scope.to, page, perPage, scope.transactionDirection, scope.transactionState).then(
                function (response) {
                    scope.transactions = response.transactions;
                    scope.total = response.totalElements;
                    scope.query.page = response.number + 1;
                }
            );
        };

        scope.limitOptions = [];
        scope.refreshTransactions();
    });

    return app;
});