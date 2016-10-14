'use strict';

define([
    'app',
    'petrovich'
], function (app, petrovich) {

    app.filter('htmlToPlaintext', function () {
        /*return function(text) {
         return  text ? String(text).replace(/<[^>]+>/gm, '') : '';
         };*/

        return function (text) {
            return angular.element(text).text();
        }
    });

    app.filter('petrovichLastName', function () {
        return function (lastName, padeg, sex) {
            if (lastName == null) {
                return "";
            }
            var gender = "male";
            if (sex == null) {
                petrovich.detect_gender(lastName);
            } else {
                gender = sex ? "male" : "female"
            }
            var person = {
                gender : gender,
                //first: 'Петр',
                //middle: 'Ильич'
                last: lastName
            };
            var result = petrovich(person, padeg);
            return result.last;
        }
    });

    app.filter('petrovichFull', function () {
        return function (last, first, middle, targetCase, sex) {
            var gender = "male";
            if (sex == null) {
                petrovich.detect_gender(last);
            } else {
                gender = sex ? "male" : "female"
            }
            var person = {
                gender: gender,
                first: first,
                middle: middle,
                last: last
            };
            var result = petrovich(person, targetCase);

            return result.last + ' ' + result.first + ' ' + result.middle;
        }
    });

    app.filter('resizeImage', function () {
        return function (url, resize) {
            if (url) {
                var dotIndex = url.lastIndexOf(".");
                return url.substr(0, dotIndex) + "_c" + resize + url.substr(dotIndex);
            } else {
                return "";
            }
        }
    });

    return app;
});