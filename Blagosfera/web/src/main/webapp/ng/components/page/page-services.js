'use strict';

define([
    'app'
], function (app) {

    app.factory('staticPageService', function (httpService) {
        var staticPageService = {};

        staticPageService.loadPage = function (pageId) {
            return httpService.get('/api/p/' + pageId, {headers: {'Cache-Control': 'no-cache'}})
        };

        return staticPageService;
    });

    return app;
});