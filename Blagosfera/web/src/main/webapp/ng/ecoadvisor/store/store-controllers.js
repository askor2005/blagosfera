'use strict';

define([
    'app'
], function (app) {

    app.controller('StoreCtrl', function ($routeParams, communitiesService, broadcastService, $mdDialog, ecoAdvisorSettingsService, storeService, $q) {
        var scope = this;
        scope.community = {};
        scope.community.id = $routeParams.communityId ? parseInt($routeParams.communityId) : 0;
        scope.isUserSuperAdmin = false;
        scope.currentUser = null;

        scope.products = {
            data: [],
            count: 0,
            query: {
                sortColumn: 'id',
                sortDirection: 'asc',
                pageSize: 5,
                page: 1
            },
            selected: []
        };

        scope.onSelect = function (row) {
            // if single selection required
            //scope.products.selected.length = 0;
            //scope.products.selected.push(row);
        };

        var getProducts = function () {
            var sortColumn = scope.products.query.sortColumn;
            scope.products.query.sortDirection = sortColumn && sortColumn.startsWith('-') ? 'desc' : 'asc';
            scope.products.query.sortColumn = sortColumn && sortColumn.startsWith('-') ? sortColumn.substring(1) : sortColumn;

            scope.promise = communitiesService.getProductsFromStore(scope.community.id,
                scope.products.query.page - 1, scope.products.query.pageSize,
                scope.products.query.sortDirection, scope.products.query.sortColumn).then(
                function (response) {
                    scope.products.data = response.data.data;
                    scope.products.count = response.data.total;
                }
            );
        };

        scope.onPaginate = function (page, pageSize) {
            scope.products.selected.length = 0;
            scope.products.query.page = page;
            scope.products.query.pageSize = pageSize;
            getProducts();
        };

        scope.onReorder = function (sortColumn) {
            scope.products.query.sortColumn = sortColumn;
            getProducts();
        };

        scope.openProductDetailsDialog = function ($event, product) {
            $event.stopPropagation();

            broadcastService.send('ecoAdvisorDialog', {
                type: 'productDetails',
                event: $event,
                product: product
            });
        };

        scope.productGroups = {
            data: [],
            selected: null
        };

        scope.openProductGroupsManageDialog = function ($event) {
            $mdDialog.show({
                controller: 'ProductGroupsManageCtrl',
                controllerAs: 'productGroups',
                templateUrl: 'productGroupsManageDialogTemplate.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: true,
                fullscreen: false,
                productGroups: scope.productGroups,
                communityId: scope.community.id
            }).then(function () {
                scope.productsGroupsSearchText = '';
                getProducts();
            }, function () {
                scope.productsGroupsSearchText = '';
                getProducts();
            });
        };

        var getProductGroups = function () {
            ecoAdvisorSettingsService.getProductGroups(scope.community.id).then(
                function (response) {
                    scope.productGroups.data = response.data;
                }
            );
        };

        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);

            return function filterFn(group) {
                return (group.name.indexOf(lowercaseQuery) === 0);
            };
        }

        scope.productsGroupsSearchText = '';

        scope.productsGroupsSearch = function (query) {
            return query ? scope.productGroups.data.filter(createFilterFor(query)) : scope.productGroups.data;
        };

        scope.setProductsGroup = function () {
            var productIds = [];

            for (var i = 0; i < scope.products.selected.length; i++) {
                productIds.push(scope.products.selected[i].id);
            }

            ecoAdvisorSettingsService.setProductsGroup(scope.community.id, scope.productGroups.selected.id, productIds).then(function (response) {
                getProducts();
            });
        };

        scope.resetProductsGroup = function () {
            var productIds = [];

            for (var i = 0; i < scope.products.selected.length; i++) {
                productIds.push(scope.products.selected[i].id);
            }

            ecoAdvisorSettingsService.resetProductsGroup(scope.community.id, productIds).then(function (response) {
                getProducts();
            });
        };

        communitiesService.getCommunity(scope.community.id).then(
            function (response) {
                scope.community.name = response.data.name;
                getProducts();
                getProductGroups();
            }
        );

        scope.openEmuKassaDialog = function ($event) {
            $mdDialog.show({
                controller: 'EmuKassaCtrl',
                controllerAs: 'emuKassa',
                templateUrl: 'emuKassaDialogTemplate.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose: false,
                fullscreen: true,
                communityId: scope.community.id,
                storeService: storeService,
                broadcastService : broadcastService,
                storeCtrl: scope,
                currentUser: scope.currentUser
            }).then(function () {
                //scope.productsGroupsSearchText = '';
                //getProducts();
            }, function () {
                //scope.productsGroupsSearchText = '';
                //getProducts();
            });
        };

        scope.userIsSuperAdmin = function() {
            var defer = $q.defer();
            broadcastService.send("isSuperAdmin", defer);
            return defer.promise;
        };

        scope.getCurrentUser = function() {
            var defer = $q.defer();
            broadcastService.send("getUserDetails", defer);
            return defer.promise;
        };

        scope.userIsSuperAdmin().then(function(isSuperAdmin){
            scope.isUserSuperAdmin = isSuperAdmin;
        });

        scope.getCurrentUser().then(function(user){
            scope.currentUser = user;
        });

    });

    app.controller('ProductGroupsManageCtrl', function ($mdDialog, productGroups, communityId, ecoAdvisorSettingsService) {
        var scope = this;

        scope.productGroups = productGroups;

        scope.selectedGroup = null;
        scope.editId = null;
        scope.editName = null;

        scope.editProductGroup = function ($event, group) {
            scope.selectedGroup = group;
            scope.editId = group ? group.id : null;
            scope.editName = group ? group.name : null;
            scope.editMode = true;
        };

        scope.cancelEdit = function ($event) {
            scope.selectedGroup = null;
            scope.editMode = false;
        };

        scope.removeProductGroup = function ($event, $index) {
            ecoAdvisorSettingsService.deleteProductGroup(communityId, scope.productGroups.data[$index].id).then(
                function (response) {
                    scope.productGroups.data.splice($index, 1);
                }
            );
        };

        scope.saveProductGroup = function ($event) {
            ecoAdvisorSettingsService.saveProductGroup(communityId, {id: scope.editId, name: scope.editName}).then(
                function (response) {
                    if (!scope.selectedGroup) {
                        scope.selectedGroup = {id: response.data.id, name: response.data.name};
                        scope.productGroups.data.push(scope.selectedGroup);
                    } else {
                        scope.selectedGroup.name = response.data.name;
                    }

                    scope.selectedGroup = null;
                    scope.editMode = false;
                }
            );
        };

        scope.close = function () {
            $mdDialog.hide();
        };
    });

    app.controller('EmuKassaCtrl', function ($mdDialog, communityId, storeService, broadcastService, storeCtrl, currentUser) {
        var scope = this;

        scope.communityId = communityId;
        scope.sessionActive = false;
        scope.sessionIsMy = false;
        scope.inited = false;
        scope.tempProducts = [];
        scope.workplaces = [];
        scope.selectedWorkplace = null;

        scope.productForAdd = {
            code : "", // Код
            name : "", // Наименование
            count : 0, // Количество
            unitOfMeasure : "", // Единица измерения
            wholesalePrice : {
                value : "",
                currency : "RUR",
                withVat : false
            }, // Оптовая цена
            finalPrice : {
                value : "",
                currency : "RUR",
                withVat : false
            }, // Розничная цена
            vat : 0 // НДС
        };
        scope.enableAddProduct = false;
        //console.log(currentUser);
        //var workplaceId = "38676a24-c1de-4eee-b020-2f926abc4651";
        var operatorIkp = currentUser.ikp;//"21275149946074214018";
        //console.log(operatorIkp);

        scope.operatorStart = function () {

            storeService.operatorStart(scope.selectedWorkplace, operatorIkp).then(function (response) {

                if (response.data.status.code == 'ACCEPTED') {
                    scope.sessionActive = true;
                    scope.sessionIsMy = true;
                } else {
                    showError(response.data.status.message);
                }
            });
        };

        scope.operatorStop = function () {
            storeService.operatorStop(scope.selectedWorkplace, operatorIkp).then(function (response) {
                if (response.data.status.code == 'ACCEPTED') {
                    scope.sessionActive = false;
                    scope.sessionIsMy = false;
                } else {
                    showError(response.data.status.message);
                }
            });
        };

        scope.showAddProductDialog = function(){
            scope.enableAddProduct = true;
        };

        scope.addProduct = function(){
            scope.enableAddProduct = false;
            scope.tempProducts.push(scope.productForAdd);
            scope.productForAdd = {
                code : "", // Код
                name : "", // Наименование
                count : 0, // Количество
                unitOfMeasure : "", // Единица измерения
                wholesalePrice : {
                    value : "",
                    currency : "RUR",
                    withVat : false
                }, // Оптовая цена
                finalPrice : {
                    value : "",
                    currency : "RUR",
                    withVat : false
                }, // Розничная цена
                vat : 0 // НДС
            };
        };

        scope.importProducts = function(){
            var shop = scope.communityId;

            for (var index in scope.tempProducts) {
                var tempProduct = scope.tempProducts[index];
                tempProduct.wholesalePrice.value = parseInt(tempProduct.wholesalePrice.value) * 100;
                tempProduct.finalPrice.value = parseInt(tempProduct.finalPrice.value) * 100;
            }

            var products = {
                product : scope.tempProducts
            };
            storeService.importProducts(shop, products).then(function (response) {
                scope.tempProducts = [];
            });
        };
        scope.closeDialog = function () {
            $mdDialog.hide();
            storeCtrl.getProducts();
        };

        scope.changeWorkplace = function(){
            storeService.sessionCheck(scope.selectedWorkplace).then(function (response) {
                if (response.data.status.code == 'ACCEPTED' && response.data.operatorIkp == operatorIkp) {
                    scope.sessionActive = true;
                    scope.sessionIsMy = true;
                } else if (response.data.status.code == 'ACCEPTED') {
                    scope.sessionActive = true;
                    scope.sessionIsMy = false;
                } else if (response.data.status.code == 'REJECTED') {
                    scope.sessionActive = false;
                    scope.sessionIsMy = false;
                }
            });
        };

        storeService.getWorkplaces(scope.communityId).then(function (response) {
            scope.workplaces = response.data;
            scope.inited = true;
        });

        var showError = function (message) {
            broadcastService.send('dialog', {
                type: 'info',
                title: 'Ошибка выполнения операции.',
                message: message
            });
        };
    });

    return app;
});