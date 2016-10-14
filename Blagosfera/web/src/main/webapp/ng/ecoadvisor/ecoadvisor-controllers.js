'use strict';

define([
    'app'
], function (app) {

    app.controller('EcoAdvisorDialogsCtrl', function ($scope, $log, $mdDialog) {
        var scope = this;

        scope.openProductDetails = function (data) {
            $mdDialog.show({
                controller: 'ProductDetailsDialogCtrl',
                controllerAs: 'dialog',
                templateUrl: '/ng/ecoadvisor/views/productDetails.html',
                parent: angular.element(document.body),
                targetEvent: data.event,
                clickOutsideToClose: true,
                fullscreen: false,
                disableParentScroll: false,
                product: data.product
            });
        };

        $scope.$on('ecoAdvisorDialog', function (event, data) {
            if (data.type === 'productDetails') {
                scope.openProductDetails(data);
            }
        });
    });

    app.controller('ProductDetailsDialogCtrl', function ($mdDialog, product) {
        this.product = product;

        this.close = function () {
            $mdDialog.hide();
        };
    });

    return app;
});