'use strict';

define([
    'app'
], function (app) {

    app.directive('appVersion', ['version', function (version) {
        return function (scope, elm, attrs) {
            elm.text(version);
        };
    }]);

    return app;
})();