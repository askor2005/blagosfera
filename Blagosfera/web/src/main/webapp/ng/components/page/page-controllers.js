'use strict';

define([
    'app'
], function (app) {

    app.controller('StaticPageCtrl', function ($routeParams, $sce, staticPageService) {
        var scope = this;
        scope.pageId = $routeParams.page;
        scope.pageContent = "Загрузка...";

        scope.loadPage = function (pageId) {
            staticPageService.loadPage(pageId).then(
                function (response) {
                    scope.pageContent = $sce.trustAsHtml(response.data);
                }
            );
        };

        scope.loadPage(scope.pageId);
    });

    return app;
});