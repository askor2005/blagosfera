'use strict';

define([
    'app'
], function (app) {

    app.directive('routeLoadingIndicator', function ($rootScope) {
        return {
            restrict: 'E',
            template: '<div ng-show="isRouteLoading" class="loading-indicator">' +
            '<div class="loading-indicator-body">' +
            '<h3 class="loading-title"><ng-transclude></ng-transclude></h3>' +
            '<div class="spinner"><chasing-dots-spinner></chasing-dots-spinner></div>' +
            '</div></div>',
            replace: true,
            transclude: true,
            link: function (scope, elem, attrs) {
                scope.isRouteLoading = false;

                $rootScope.$on('$routeChangeStart', function (event, next, current) {
                    if (current) scope.isRouteLoading = true;
                });

                $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
                    scope.isRouteLoading = false;
                });

                /*$rootScope.$on('$locationChangeStart', function () {
                 //scope.isRouteLoading = true;
                 });

                 $rootScope.$on('$locationChangeSuccess', function () {
                 //scope.isRouteLoading = false;
                 });*/

                $rootScope.$on('routeChangeCancel', function (event, next, current) {
                    scope.isRouteLoading = false;
                });
            }
        };
    });

    app.directive('loadingIndicator', function ($rootScope) {
        return {
            restrict: 'E',
            template: '<div class="loading-indicator">' +
            '<div class="loading-indicator-body">' +
            '<h3 class="loading-title"><ng-transclude></ng-transclude></h3>' +
            '<div class="spinner"><chasing-dots-spinner></chasing-dots-spinner></div>' +
            '</div></div>',
            replace: true,
            transclude: true,
            link: function (scope, elem, attrs) {

            }
        };
    });
    app.directive('match', function($parse){
        return {
            require: '?ngModel',
            restrict: 'A',
            link: function(scope, elem, attrs, ctrl) {
                if(!ctrl) {
                    return;
                }

                var matchGetter = $parse(attrs.match);
                var caselessGetter = $parse(attrs.matchCaseless);
                var noMatchGetter = $parse(attrs.notMatch);
                var matchIgnoreEmptyGetter = $parse(attrs.matchIgnoreEmpty);

                scope.$watch(getMatchValue, function(){
                    ctrl.$$parseAndValidate();
                });

                ctrl.$validators.match = function(){
                    var match = getMatchValue();
                    var notMatch = noMatchGetter(scope);
                    var value;

                    if (matchIgnoreEmptyGetter(scope) && !ctrl.$viewValue) {
                        return true;
                    }

                    if(caselessGetter(scope)){
                        value = angular.lowercase(ctrl.$viewValue) === angular.lowercase(match);
                    }else{
                        value = ctrl.$viewValue === match;
                    }
                    /*jslint bitwise: true */
                    value ^= notMatch;
                    /*jslint bitwise: false */
                    return !!value;
                };

                function getMatchValue(){
                    var match = matchGetter(scope);
                    if(angular.isObject(match) && match.hasOwnProperty('$viewValue')){
                        match = match.$viewValue;
                    }
                    return match;
                }
            }
        };
    });

    app.directive('pwCheck', [function () {
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                var firstPassword = '#' + attrs.pwCheck;
                elem.add(firstPassword).on('keyup', function () {
                    scope.$apply(function () {
                        // console.info(elem.val() === $(firstPassword).val());
                        ctrl.$setValidity('pwmatch', elem.val() === $(firstPassword).val());
                    });
                });
            }
        }
    }]);

    app.directive('ngEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function(event) {
                if(event.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.ngEnter, {'event': event});
                    });

                    event.preventDefault();
                }
            });
        };
    });

    app.directive('focusOn', function() {
        return function(scope, elem, attr) {
            scope.$on(attr.focusOn, function(e) {
                elem[0].focus();
            });
        };
    });

    return app;
});