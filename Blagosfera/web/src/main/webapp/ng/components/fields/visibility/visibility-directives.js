'use strict';

define([
    'app',
], function (app) {
    app.directive('askorFieldVisibility', function () {

        function link(scope, element, attrs, controller) {
        }

        function controller($scope,visibilityService) {
            var scope = this;
            scope.hidden = $scope.fieldHidden;
            scope.name = $scope.name;
            scope.fieldType = $scope.fieldType;
            scope.toggleVisibility = function() {
                if (scope.fieldType === "basic_information") {
                    var oldVal = scope.hidden;
                    visibilityService.changeBasicInformationFieldVisibility(scope.name,scope.hidden).then(
                        function(response){
                            scope.hidden = response.data;
                        },function(response){

                    });
                }
                else if (scope.fieldType === "registrator_data") {
                    var oldVal = scope.hidden;
                    visibilityService.changeRegistratorDataFieldVisibility(scope.name,scope.hidden).then(
                        function(response){
                            scope.hidden = response.data;
                        },function(response){

                        });
                }
                else if (scope.fieldType === "registrator_office_address") {
                    var oldVal = scope.hidden;
                    visibilityService.changeRegistratorOfficeAddressFieldVisibility(scope.name,scope.hidden).then(
                        function(response){
                            scope.hidden = response.data;
                        },function(response){

                        });
                }
                else if (scope.fieldType === "fact_address") {
                    var oldVal = scope.hidden;
                    visibilityService.changeFactAddressFieldVisibility(scope.name,scope.hidden).then(
                        function(response){
                            scope.hidden = response.data;
                        },function(response){
                        });
                }
                else if (scope.fieldType === "registration_address") {
                    var oldVal = scope.hidden;
                    visibilityService.changeRegistrationAddressFieldVisibility(scope.name,scope.hidden).then(
                        function(response){
                            scope.hidden = response.data;
                        },function(response){
                        });
                }
            }

        }

        return {
            restrict: 'E',
            transclude: false,
            scope: {
                fieldHidden: "=",
                name: "=",
                fieldType: "="
            },
            controller: controller,
            controllerAs: 'askorFieldVisibility',
            templateUrl: 'components/fields/visibility/fieldvisibility.html',
            link: link
        }
    });

    return app;
});