'use strict';

define([
    'app'
], function (app) {

    app.factory('ecoAdvisorSettingsService', function (httpService) {
        var ecoAdvisorSettingsService = {};

        ecoAdvisorSettingsService.getBonusAllocations = function (communityId) {
            return httpService.get('/api/ecoadvisor/bonusAllocations.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            });
        };

        ecoAdvisorSettingsService.saveBonusAllocations = function (communityId, items) {
            var data = [];

            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                data.push({
                    receiverType: item.receiverType.value, allocationPercent: item.allocationPercent, id: item.id
                });
            }

            return httpService.post('/api/ecoadvisor/bonusAllocations.json', data, {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            })
        };

        ecoAdvisorSettingsService.getAdvisorSettings = function (communityId, productGroup) {
            return httpService.get('/api/ecoadvisor/advisorSettings.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    productGroupId: productGroup ? productGroup.id : null
                }
            });
        };

        ecoAdvisorSettingsService.saveAdvisorSettings = function (communityId, advisorSettings, productGroup) {
            return httpService.post('/api/ecoadvisor/advisorSettings.json', advisorSettings, {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    productGroupId: productGroup ? productGroup.id : null
                }
            })
        };

        ecoAdvisorSettingsService.exportAdvisorReport2Excel = function (ecoAdvisor) {
            return httpService.post('/api/ecoadvisor/export2excel.xlsx', ecoAdvisor, {
                responseType: 'arraybuffer',
                headers: {
                    'Cache-Control': 'no-cache',
                    'Content-type': 'application/json',
                    'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                }
            })
        };

        ecoAdvisorSettingsService.getProductGroups = function (communityId) {
            return httpService.get('/api/ecoadvisor/productGroups.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            });
        };

        ecoAdvisorSettingsService.deleteProductGroup = function (communityId, groupId) {
            return httpService.get('/api/ecoadvisor/deleteProductGroup.json', {
                headers: {'Cache-Control': 'no-cache'},
                params: {
                    communityId: communityId,
                    groupId: groupId
                }
            });
        };

        ecoAdvisorSettingsService.saveProductGroup = function (communityId, group) {
            return httpService.post('/api/ecoadvisor/saveProductGroup.json', group, {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            });
        };

        ecoAdvisorSettingsService.setProductsGroup = function (communityId, groupId, productIds) {
            return httpService.post('/api/ecoadvisor/setProductsGroup.json', productIds, {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId, groupId: groupId}
            });
        };

        ecoAdvisorSettingsService.resetProductsGroup = function (communityId, productIds) {
            return httpService.post('/api/ecoadvisor/resetProductsGroup.json', productIds, {
                headers: {'Cache-Control': 'no-cache'},
                params: {communityId: communityId}
            });
        };

        return ecoAdvisorSettingsService;
    });

    return app;
});