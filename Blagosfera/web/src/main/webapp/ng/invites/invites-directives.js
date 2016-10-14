'use strict';

define([
    'app'
], function (app) {

    app.directive('emailValidate', function(invitesPageService) {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {

                $(elm).on("input", function() {
                    invitesPageService.validateEmail($(this).val()).then(
                        function () {
                            ctrl.$setValidity('servererror', true);
                            //return viewValue;
                        }, function (response) {
                            ctrl.$setValidity('servererror', false);
                            scope.emailError = response.data.message;
                            //response.data.message
                            //return undefined;
                        }
                    );
                });

                /*ctrl.$parsers.unshift(function(viewValue) {

                    invitesPageService.validateEmail(viewValue).then(
                        function () {
                            ctrl.$setValidity('servererror', true);
                            //return viewValue;
                        }, function (response) {
                            ctrl.$setValidity('servererror', response.data.message);
                            //return undefined;
                        }
                    );
                    return viewValue;

                });*/
            }
        };
    });

    return app;
});