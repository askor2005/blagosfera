'use strict';

define([
    'app'
], function (app) {

    app.directive('checkStrength', function () {

        return {
            replace: false,
            restrict: 'EACM',
            link: function (scope, iElement, iAttrs) {

                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (pass) {
                        var score = 0;
                        if (!pass)
                            return score;

                        // award every unique letter until 5 repetitions
                        var letters = new Object();
                        for (var i=0; i<pass.length; i++) {
                            letters[pass[i]] = (letters[pass[i]] || 0) + 1;
                            score += 5.0 / letters[pass[i]];
                        }

                        // bonus points for mixing it up
                        var variations = {
                            digits: /\d/.test(pass),
                            lower: /[a-z]/.test(pass),
                            upper: /[A-Z]/.test(pass),
                            nonWords: /\W/.test(pass),
                        };

                        var variationCount = 0;
                        for (var check in variations) {
                            variationCount += (variations[check] == true) ? 1 : 0;
                        }
                        score += (variationCount - 1) * 10;

                        return parseInt(score);

                    },
                    getColor: function (score) {
                        var idx = 0;
                        if (score > 80) {
                            idx = 4;
                        } else if (score > 60) {
                            idx = 3;
                        } else if (score >= 30) {
                            idx = 2;
                        } else if (score > 0) {
                            idx = 1;
                        }
                        return { idx: idx + 1, col: this.colors[idx] };

                    }
                };

                scope.$watch(iAttrs.checkStrength, function () {
                    var password = scope.$eval(iAttrs.checkStrength);
                    if (scope.pw === '') {
                        iElement.hide();
                    } else {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.show();
                        iElement.find('li')
                            .css({ "background": "#DDD" })
                            .slice(0, c.idx)
                            .css({ "background": c.col });
                        iElement.find('.text-muted').hide();
                        var textId = c.idx - 1;
                        var strengthTitleNode = $(iElement.find('.text-muted').get(textId));
                        strengthTitleNode.show();
                    }
                });

            },
            template:

                '<div layout="row" layout-align="start start">'+
                    '<ul class="strength">' +
                        '<li class="point"></li>' +
                        '<li class="point"></li>' +
                        '<li class="point"></li>' +
                        '<li class="point"></li>' +
                        '<li class="point"></li>' +
                    '</ul>'+
                    '<div class="strength-title">'+
                        '<span class="text-muted"></span>' +
                        '<span class="text-muted">Плохой пароль!</span>' +
                        '<span class="text-muted">Хороший пароль!</span>' +
                        '<span class="text-muted">Отличный пароль!</span>' +
                        '<span class="text-muted">Надёжный пароль!</span>' +
                    '<div>'+
                '<div>'
        };

    });

    return app;
});