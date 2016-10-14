'use strict';

define([
    'app'
], function (app) {

    app.controller('SessionsCtrl', function ($routeParams, broadcastService, operatorSessionsService, communitiesService, $mdDialog) {
        var scope = this;
        scope.community = {};
        scope.community.id = $routeParams.communityId ? parseInt($routeParams.communityId) : 0;

        scope.workplaces = {
            data: [],
            selected: [],
            promise: null
        };

        scope.sessions = {
            data: [],
            count: 0,
            selected: [],
            query: {
                sortColumn: null,
                sortDirection: null,
                pageSize: 10,
                page: 1,
                filters: {
                    operator: null,
                    createdDateFrom: null,
                    createdDateTo: null,
                    active: null
                }
            },
            promise: null
        };

        scope.operations = {
            data: [],
            count: 0,
            selected: [],
            query: {
                sortColumn: null,
                sortDirection: null,
                pageSize: 5,
                page: 1
            },
            promise: null
        };

        scope.products = {
            data: [],
            count: 0,
            selected: [],
            query: {
                sortColumn: null,
                sortDirection: null,
                pageSize: 10,
                page: 1
            },
            promise: null
        };

        scope.renderWorkplace = function (guid) {
            if (guid) {
                for (var i = 0; i < scope.workplaces.data.length; i++) {
                    if (scope.workplaces.data[i].guid === guid) return scope.workplaces.data[i].number;
                }

                return guid;
            } else return '';
        };

        var getWorkplaces = function () {
            scope.workplaces.promise = communitiesService.getWorkplaces(scope.community.id).then(
                function (response) {
                    scope.workplaces.data = response.data;
                    scope.workplaces.selected.length = 0;
                }
            );
        };

        var getSessions = function () {
            var sortColumn = scope.sessions.query.sortColumn;
            scope.sessions.query.sortDirection = sortColumn && sortColumn.startsWith('-') ? 'desc' : 'asc';
            scope.sessions.query.sortColumn = sortColumn && sortColumn.startsWith('-') ? sortColumn.substring(1) : sortColumn;

            var workplaceIds = [];

            for (var i = 0; i < scope.workplaces.selected.length; i++) {
                workplaceIds.push(scope.workplaces.selected[i].id);
            }

            scope.sessions.promise = operatorSessionsService.getSessions(scope.community.id, workplaceIds,
                scope.sessions.query.page - 1, scope.sessions.query.pageSize,
                scope.sessions.query.sortDirection, scope.sessions.query.sortColumn,
                scope.sessions.query.filters.operator, scope.sessions.query.filters.createdDateFrom,
                scope.sessions.query.filters.createdDateTo, scope.sessions.query.filters.active).then(
                function (response) {
                    scope.sessions.data = response.data.data;
                    scope.sessions.count = response.data.total;
                }
            );
        };

        var getExchangeOperations = function () {
            var sortColumn = scope.operations.query.sortColumn;
            scope.operations.query.sortDirection = sortColumn && sortColumn.startsWith('-') ? 'desc' : 'asc';
            scope.operations.query.sortColumn = sortColumn && sortColumn.startsWith('-') ? sortColumn.substring(1) : sortColumn;

            var sessionId = null;

            if (scope.sessions.selected.length > 0) {
                sessionId = scope.sessions.selected[0].id;
            }

            operatorSessionsService.getExchangeOperations(scope.community.id, sessionId,
                scope.operations.query.page - 1, scope.operations.query.pageSize,
                scope.operations.query.sortDirection, scope.operations.query.sortColumn).then(
                function (response) {
                    scope.operations.data = response.data.data;
                    scope.operations.count = response.data.total;
                }
            );
        };

        var getProducts = function () {
            var sortColumn = scope.products.query.sortColumn;
            scope.products.query.sortDirection = sortColumn && sortColumn.startsWith('-') ? 'desc' : 'asc';
            scope.products.query.sortColumn = sortColumn && sortColumn.startsWith('-') ? sortColumn.substring(1) : sortColumn;

            var exchangeIds = [];

            for (var i = 0; i < scope.operations.selected.length; i++) {
                exchangeIds.push(scope.operations.selected[i].id);
            }

            scope.products.promise = operatorSessionsService.getExchangeProducts(scope.community.id, exchangeIds,
                scope.products.query.page - 1, scope.products.query.pageSize,
                scope.products.query.sortDirection, scope.products.query.sortColumn).then(
                function (response) {
                    scope.products.data = response.data.data;
                    scope.products.count = response.data.total;
                }
            );
        };

        scope.onWorkplaceSelect = function () {
            getSessions();
        };

        scope.onSessionsPage = function (page, pageSize) {
            scope.sessions.query.page = page;
            scope.sessions.query.pageSize = pageSize;
            scope.sessions.selected.length = 0;
            getSessions();
        };

        scope.onSessionsReorder = function (sortColumn) {
            scope.sessions.query.sortColumn = sortColumn;
            getSessions();
        };

        scope.onSessionSelect = function (row) {
            scope.sessions.selected.length = 0;
            scope.sessions.selected.push(row);
            getExchangeOperations();
        };

        scope.onSessionDeselect = function (row) {
            //getExchangeOperations();
            scope.operations.data.length = 0;
            scope.operations.selected.length = 0;

            scope.products.data.length = 0;
            scope.products.selected.length = 0;
        };

        scope.onOperationsPage = function (page, pageSize) {
            scope.operations.query.page = page;
            scope.operations.query.pageSize = pageSize;
            scope.operations.selected.length = 0;
            getExchangeOperations();
        };

        scope.onOperationsReorder = function (sortColumn) {
            scope.operations.query.sortColumn = sortColumn;
            getExchangeOperations();
        };

        scope.onOperationSelect = function (row) {
            //scope.operations.selected.length = 0;
            //scope.operations.selected.push(row);
            getProducts();
        };

        scope.onOperationDeselect = function (row) {
            //scope.operations.selected.length = 0;
            //scope.operations.selected.push(row);

            if (scope.operations.selected.length) {
                getProducts();
            } else {
                scope.products.data.length = 0;
                scope.products.selected.length = 0;
            }
        };

        scope.onProductsPage = function (page, pageSize) {
            scope.products.query.page = page;
            scope.products.query.pageSize = pageSize;
            getProducts();
        };

        scope.onProductsReorder = function (sortColumn) {
            scope.products.query.sortColumn = sortColumn;
            getProducts();
        };

        scope.openSessionsFilterDialog = function ($event) {
            $event.stopPropagation();

            $mdDialog.show({
                controller: 'SessionsFilterDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: 'sessionsFilterDialogTemplate.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: false,
                filters: scope.sessions.query.filters
            }).then(
                function (filters) {
                    scope.sessions.query.filters.operator = filters.operator;
                    scope.sessions.query.filters.createdDateFrom = filters.createdDateFrom;
                    scope.sessions.query.filters.createdDateTo = filters.createdDateTo;
                    scope.sessions.query.filters.active = filters.active;
                    getSessions();
                }
            );
        };

        scope.closeSession = function ($event, session) {
            $event.stopPropagation();

            broadcastService.send('dialog', {
                type: 'yesno',
                title: 'Требуется подтверждение',
                question: 'Вы уверены что хотите закрыть сессию оператора-кассира?',
                showYes: true,
                showNo: true,
                showCancel: false,
                onYes: function () {
                    operatorSessionsService.closeSession(scope.community.id, session.id).then(
                        function (response) {
                            session.active = response.data.active;
                            session.endDate = response.data.endDate;
                        }
                    );
                }
            });
        };

        scope.openProductDetailsDialog = function ($event, product) {
            $event.stopPropagation();

            broadcastService.send('ecoAdvisorDialog', {
                type: 'productDetails',
                event: $event,
                product: product
            });
        };

        scope.openOperationDetailsDialog = function ($event, operation) {
            $event.stopPropagation();

            $mdDialog.show({
                controller: 'OperationDetailsDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: 'operationDetailsDialogTemplate.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: false,
                operation: operation
            });
        };

        scope.openDocumentsDialog = function ($event, operation) {
            $event.stopPropagation();

            $mdDialog.show({
                controller: 'DocumentsDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: 'documentsDialogTemplate.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: false,
                operation: operation
            });
        };

        communitiesService.getCommunity(scope.community.id).then(
            function (response) {
                scope.community.name = response.data.name;
                getWorkplaces();
            }
        );
    });

    app.controller('DocumentsDialogCtrl', function ($mdDialog, operation) {
        this.operation = operation;

        this.close = function () {
            $mdDialog.hide('close');
        };
    });

    app.controller('OperationDetailsDialogCtrl', function ($mdDialog, operation) {
        this.operation = operation;

        this.close = function () {
            $mdDialog.hide('close');
        };
    });

    app.controller('SessionsFilterDialogCtrl', function ($mdDialog, filters) {
        this.operator = filters.operator;
        this.createdDateFrom = filters.createdDateFrom;
        this.createdDateTo = filters.createdDateTo;
        this.active = filters.active;

        this.apply = function () {
            $mdDialog.hide({
                operator: this.operator,
                createdDateFrom: this.createdDateFrom,
                createdDateTo: this.createdDateTo,
                active: this.active
            });
        };

        this.cancel = function () {
            $mdDialog.cancel();
        };
    });

    return app;
});