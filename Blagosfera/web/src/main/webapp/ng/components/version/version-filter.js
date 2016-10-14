'use strict';

define([
    'app'
], function (app) {

    app.filter('appVersion', ['version', function (version) {
        return function (text) {
            return String(text).replace(/%VERSION%/mg, version);
        };
    }]);

    return app;
})();