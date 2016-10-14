'use strict';

define([
    'app'
], function (app) {

    app.factory('storeService', function (httpService) {
        var storeService = {};

        storeService.operatorStart = function (workplaceId, operatorIkp) {
            return httpService.post(
                '/cashboxemu/operatorstart',
                {
                    workplaceId: workplaceId,
                    operatorIkp: operatorIkp
                },
                {
                    headers: {'Cache-Control': 'no-cache'}
                }
            );
        };

        storeService.sessionCheck = function (workplaceId) {
            return httpService.post(
                '/cashboxemu/sessioncheck',
                {
                    workplaceId: workplaceId
                },
                {
                    headers: {'Cache-Control': 'no-cache'}
                }
            );
        };

        storeService.operatorStop = function (workplaceId, operatorIkp) {
            return httpService.post(
                '/cashboxemu/operatorstop',
                {
                    workplaceId: workplaceId,
                    operatorIkp: operatorIkp
                },
                {
                    headers: {'Cache-Control': 'no-cache'}
                }
            );
        };

        storeService.importProducts = function (shop, products) {
            return httpService.post(
                '/cashboxemu/importProducts',
                {
                    shop: shop,
                    products: products
                },
                {
                    headers: {'Cache-Control': 'no-cache'}
                }
            );
        };

        storeService.getWorkplaces = function (communityId) {
            return httpService.get('/api/ecoadvisor/workplaces.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            })
        };

        return storeService;
    });

    return app;
});