'use strict';

define([
    'app'
], function (app) {

    app.factory('ecoAdvisorAdminSettingsService', function (httpService) {
        var ecoAdvisorSettingsService = {};

        ecoAdvisorSettingsService.getAdvisorSettings = function () {
            return httpService.get('/api/ecoadvisor/admin/advisorSettings.json', {
                headers: {'Cache-Control': 'no-cache'}
            });
        };

        ecoAdvisorSettingsService.saveAdvisorSettings = function (advisorSettings) {
            return httpService.post('/api/ecoadvisor/admin/advisorSettings.json', advisorSettings, {
                headers: {'Cache-Control': 'no-cache'}
            })
        };

        return ecoAdvisorSettingsService;
    });

    return app;
});