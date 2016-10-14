'use strict';

define([
    'angular',
    'angular-chart',
    'chart',
    'moment'
], function (angular, AngularChart, Chart, moment) {

    /* mdPickers won't work if moment is not global */
    window.moment = moment;

    Chart.noConflict();

    var app = angular.module('blagosfera', [
        'ngRoute',
        'ngCookies',
        'ngAnimate',
        'ngSanitize',
        'ngMessages',
        'ngAria',
        'angular-spinkit',
        'ngMdIcons',
        'chart.js',
        'pascalprecht.translate',
        'ngMaterial',
        'ngFileSaver',
        'md.data.table',
        'ui.tree',
        'angular-notification-icons',
        'LocalStorageModule',
        'ngFileUpload',
        'ngImgCrop',
        'webcam',
        'nemLogging',
        'ui-leaflet',
        'mdPickers',
        'ui.mask'
    ]);

    app.config(function ($routeProvider, $translateProvider, $translatePartialLoaderProvider,
                         $mdThemingProvider, $locationProvider, localStorageServiceProvider,
                         $controllerProvider, $provide, $compileProvider, $filterProvider) {

        // lazy-loading of angular components

        app._controller = app.controller;
        app._service = app.service;
        app._factory = app.factory;
        app._value = app.value;
        app._directive = app.directive;
        app._filter = app.filter;

        app.controller = function (name, constructor) {
            $controllerProvider.register(name, constructor);
            return (this);
        };

        app.service = function (name, constructor) {
            $provide.service(name, constructor);
            return (this);
        };

        app.factory = function (name, factory) {
            $provide.factory(name, factory);
            return (this);
        };

        app.value = function (name, value) {
            $provide.value(name, value);
            return (this);
        };

        app.directive = function (name, factory) {
            $compileProvider.directive(name, factory);
            return (this);
        };

        app.filter = function (name, constructor) {
            $filterProvider.register(name, constructor);
            return (this);
        };

        app.when = function (path, route) {
            $routeProvider.when(path, route);
        };

        app.otherwise = function (route) {
            $routeProvider.otherwise(route);
        };

        // configuration

        $locationProvider.html5Mode(false);

        /* localization */

        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: '/ng/components/i18n/{part}/{lang}.json'
        });

        $translateProvider.use('ru_RU');
        $translateProvider.preferredLanguage('ru_RU');
        $translateProvider.fallbackLanguage('ru_RU');
        // TODO если раскоментить, то не работает html из i18n даже с ng-hmtl-bind
        //$translateProvider.useSanitizeValueStrategy('escape');

        /* themes */

        //Available palettes:
        // red,    pink,  purple,      deep-purple,
        // indigo, blue,  light-blue,  cyan,
        // teal,   green, light-green, lime,
        // yellow, amber, orange,      deep-orange,
        // brown,  grey,  blue-grey

        $mdThemingProvider.theme('default')
            .primaryPalette('blue-grey')
            .accentPalette('amber')
            .warnPalette('red')
        //.backgroundPalette('white')
        //.dark()
        ;

        /* fake themes for $mdToast customizations */
        $mdThemingProvider.theme('warn-toast');
        $mdThemingProvider.theme('error-toast');

        /* storage */

        localStorageServiceProvider.setPrefix('blagosfera');
    });

    /*if (navigator.userAgent.indexOf("Firefox") >= 0)
     app.constant('WHEEL_SPEED', 20);
     else
     app.constant('WHEEL_SPEED', 1);

     app.run(function ($rootScope, WHEEL_SPEED) {
     $rootScope.WHEEL_SPEED = WHEEL_SPEED;
     });*/

    app.run(function ($rootScope, $translate) {
        /*$rootScope.$on('$translatePartialLoaderStructureChanged', function () {
            $translate.refresh();
        });*/
    });

    return app;
});