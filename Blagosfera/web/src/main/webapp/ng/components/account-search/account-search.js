'use strict';

define([
    'app'
], function (app) {

    app.component('accountSearch', {
        template: '<h1>Hello {{ $ctrl.getFullName() }}</h1>',
        bindings: {
            firstName: '<',
            lastName: '<'
        },
        controller: function () {
            this.getFullName = function () {
                return this.firstName + ' ' + this.lastName;
            };
        }
    });

    return app;
})();