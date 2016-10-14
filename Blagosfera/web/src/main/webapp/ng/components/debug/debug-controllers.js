'use strict';

define([
    'app'
], function (app) {

    app.controller('DebugCtrl', function ($scope, $log, debugEnable) {
        if (debugEnable) {
            $scope.$on('debug', function (event, data) {
                $log.info(data);
            });
        }
    });

    return app;
});