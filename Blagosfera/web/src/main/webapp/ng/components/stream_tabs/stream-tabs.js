'use strict';

define([
    'app'
], function (app) {

    app.directive('streamtabs', function() {
            return {
                restrict: 'E',
                transclude: true,
                scope: {
                    cancelAction : "=cancel",
                    showCancel : "=showCancel"
                },
                controller: [ "$scope", function($scope) {
                    var panes = $scope.panes = [];
                    var self = this;
                    this.select = function(pane) {
                        var foundCurrentPage = false;
                        angular.forEach(panes, function(loopPane) {
                            loopPane.active = false;
                            loopPane.notActiveTab = false;
                            loopPane.doneTab = false;
                            if (loopPane == pane) {
                                foundCurrentPage = true;
                            } else {
                                if (foundCurrentPage) {
                                    loopPane.notActiveTab = true;
                                    loopPane.doneTab = false;
                                } else {
                                    loopPane.notActiveTab = false;
                                    loopPane.doneTab = true;
                                }
                            }
                        });
                        this.initFocusElement(pane);
                        if (pane) {
                            pane.active = true;
                        }
                    };

                    this.nextPane = function (){
                        var foundActive = false;
                        var clicked = false;
                        var foundPane = null;
                        angular.forEach(panes, function(loopPane) {
                            if (foundActive && !clicked) {
                                foundPane = loopPane;
                                clicked = true;
                            }
                            if (loopPane.active) {
                                foundActive = true;
                            }
                        });
                        self.select(foundPane);
                        return foundPane;
                    };

                    this.addPane = function(pane) {
                        pane.active = false;
                        pane.notActiveTab = true;
                        pane.doneTab = false;
                        if (pane.activeTab) {
                            pane.notActiveTab = false;
                            self.select(pane);
                        }
                        panes.push(pane);
                    };

                    this.initFocusElement = function(pane) {
                        if (pane != null) {
                            var autoFocusElements = $(pane.element).find("[autofocus]");
                            if (autoFocusElements.length > 0) {
                                setTimeout(function () {
                                    autoFocusElements.get(0).focus();
                                }, 200);
                            }
                        }
                    };

                }],
                template:
                '<div class="tabbable">' +
                    '<div class="tabbable_header" layout="row" layout-wrap>' +
                        '<div ng-repeat="pane in panes" ng-class="{activeTab:pane.active, notActiveTab:pane.notActiveTab, doneTab:pane.doneTab}" class="tabhead" flex="{{100/panes.length}}" >' +
                            '<div class="circleNumber">{{$index + 1}}</div>' +
                            '<div class="leftLine" ng-show="!$first"></div>' +
                            '<div class="rightLine" ng-show="!$last"></div>' +
                            '<div class="paneTitle">{{pane.title}}</div>' +
                        '</div>' +
                    '</div>' +
                    '<div class="tab-content" ng-transclude></div>' +
                    '<div style="width: 100%; text-align: center" ng-if="showCancel == null || showCancel"><md-button class="md-primary cancelAction" ng-click="cancelAction()">Отменить</md-button></div>'+
                '</div>',
                replace: true
            };
        }).
        directive('pane', function() {

            function nextTabHandler(scope, tabsCtrl) {
                scope.loading = true;
                scope.$apply("nextTabAction()").then(
                    function () {
                        scope.loading = false;
                        var nextPane = tabsCtrl.nextPane();
                        tabsCtrl.initFocusElement(nextPane);
                    }, function () {
                        scope.loading = false;
                    }
                );
            }

            return {
                require: '^streamtabs',
                restrict: 'E',
                transclude: true,
                scope: {
                    title: '@',
                    buttonTitle: "@",
                    nextTabAction: "=nexttab",
                    nextTabOnEnter: "=nextOnEnter",
                    activeTab: "@"
                },
                link: function(scope, element, attrs, tabsCtrl) {
                    scope.activeTab = scope.activeTab == "true";
                    scope.element = element;
                    tabsCtrl.addPane(scope);
                    $(element).on('click', '.nextPaneBtn', function (e) {
                        nextTabHandler(scope, tabsCtrl);
                    });
                    if (scope.nextTabOnEnter==true) {
                        $(element).on('keydown keypress', 'input', function (event) {
                            if(event.which === 13) {
                                nextTabHandler(scope, tabsCtrl);
                                event.preventDefault();
                            }
                        });
                    }
                },
                template:
                '<div>' +
                    '<div class="tab-pane" ng-show="active" ng-transclude>' +
                    '</div>' +
                    '<div style="width: 100%; text-align: center;"><md-button class="md-raised nextPaneBtn" ng-show="active" ng-disabled="loading">{{buttonTitle}}</md-button></div>'+
                '</div>',
                replace: true
            };
        });

    return app;
});


