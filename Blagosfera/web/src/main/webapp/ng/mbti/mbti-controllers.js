'use strict';

(function () {

    var app = angular.module('blagosfera');

    app.controller('MbtiCtrl', function ($routeParams, broadcastService, $scope) {
        var scope = this;

        scope.chartLabels = [
            'E - широта интересов',
            'T - логика и анализ',
            'S - опора на факты',
            'J - организованность',
            'I - глубина концентрации',
            'F - тепло и симпатия',
            'P - приспособляемость',
            'N - понимание возможностей'];

        scope.chartData = [
            [7.65, 74.28, 17.93, 27.27, 92.35, 25.72, 72.73, 82.07],
            [50, 50, 50, 50, 50, 50, 50, 50]];

        scope.chartSeries = ['Никитин М.С.', 'Иванов И.И.'];

        scope.chartOptions = {
            legendTemplate: '<ul class="<%=name.toLowerCase()%>-legend"><% for (var i = 0; i < datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><% if (datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>'
        };

        scope.refreshChart = function () {
            var e = scope.sliders.ei.val;
            var i = 100 - e;
            scope.chartData[1][0] = e;
            scope.chartData[1][4] = i;

            var s = scope.sliders.sn.val;
            var n = 100 - s;
            scope.chartData[1][2] = s;
            scope.chartData[1][7] = n;

            var t = scope.sliders.tf.val;
            var f = 100 - t;
            scope.chartData[1][1] = t;
            scope.chartData[1][5] = f;

            var j = scope.sliders.jp.val;
            var p = 100 - j;
            scope.chartData[1][3] = j;
            scope.chartData[1][6] = p;

            $scope.$apply();
        };

        scope.sliders = {
            ei: {
                val: 50,
                options: {
                    start: function (event, ui) {
                    },
                    stop: function (event, ui) {
                        scope.refreshChart()
                    }
                }
            },
            sn: {
                val: 50,
                options: {
                    start: function (event, ui) {
                    },
                    stop: function (event, ui) {
                        scope.refreshChart()
                    }
                }
            },
            tf: {
                val: 50,
                options: {
                    start: function (event, ui) {
                    },
                    stop: function (event, ui) {
                        scope.refreshChart()
                    }
                }
            },
            jp: {
                val: 50,
                options: {
                    start: function (event, ui) {
                    },
                    stop: function (event, ui) {
                        scope.refreshChart()
                    }
                }
            }
        };

        scope.saveImage = function () {
            var canvas = document.getElementById('mbti-image');
            var button = document.getElementById('mbti-save-button');
            button.href = canvas.toDataURL();
        };
    });

})();