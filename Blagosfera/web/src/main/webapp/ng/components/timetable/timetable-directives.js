'use strict';

define([
    'app',
], function (app) {
    app.directive('askorTimetable', function () {

        function link(scope, element, attrs, controller) {
        }

        function controller($scope, translateService) {
            var scope = this;
            initData();
            scope.toggleSelected = function(day,week) {
                if ($scope.readonly) {
                    return;
                }
                scope.timeTableArray[day][week] = !scope.timeTableArray[day][week];
                if ($scope.timeTableChange) {
                    $scope.timeTableChange(scope.timeTableArray);
                }
            };
            scope.hourFull = function (hour) {
                if ($scope.readonly) {
                    return false;
                }
              for (var i in scope.timeTableArray) {
                  if (!scope.timeTableArray[i][hour]) {
                      return false;
                  }
              }
              return true;
            };
            scope.dayFull = function (day) {
                if ($scope.readonly) {
                    return false;
                }
                for (var i in scope.timeTableArray[day]) {
                    if (!scope.timeTableArray[day][i]) {
                        return false;
                    }
                }
                return true;
            };
            scope.toggleDay = function(day) {
                if ($scope.readonly) {
                    return;
                }
                var valueToSet = !scope.dayFull(day);
                for (var i in scope.timeTableArray[day]) {
                    scope.timeTableArray[day][i] = valueToSet;
                }
                if ($scope.timeTableChange) {
                    $scope.timeTableChange(scope.timeTableArray);
                }
            };
            scope.toggleHour = function(hour) {
                if ($scope.readonly) {
                    return;
                }
                var valueToSet = !scope.hourFull(hour);
                for (var i in scope.timeTableArray) {
                    scope.timeTableArray[i][hour] = valueToSet;

                }
                if ($scope.timeTableChange) {
                    $scope.timeTableChange(scope.timeTableArray);
                }
            };
            function initData() {
                scope.days = ['Пн.','Вт.','Ср.','Чт.','Пт.','Сб.',
                'Вс.'];
                scope.hours = ['00','01','02','03','04','05','06','07','08','09','10',
                    '11','12','13','14','15','16','17','18','19','20','21','22','23'];
                scope.timeTableArray = $scope.timeTableArray;
                scope.readonly = $scope.readonly;
            }


        }


        return {
            restrict: 'E',
            transclude: false,
            scope: {
                timeTableArray: '=',
                timeTableChange: "=",
                readonly: "="
            },
            controller: controller,
            controllerAs: 'askorTimetable',
            templateUrl: 'components/timetable/timetable.html',
            link: link
        }
    });

    return app;
});