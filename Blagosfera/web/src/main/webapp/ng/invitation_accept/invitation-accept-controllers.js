'use strict';

define([
    'app'
], function (app) {

    app.controller('InvitationAcceptCtrl', function (
        $scope,
        $routeParams, invitationAcceptService, $mdDialog, $q, $translatePartialLoader, localStorageService,
        broadcastService
    ) {
        //$translatePartialLoader.addPart('invitations');
        var scope = this;
        // Приняте оферты

        scope.authUser = true;

        scope.actionInProcess = false;
        scope.actionResult = null;

        scope.pageDataLoaded = false;
        scope.hash = $routeParams.hash;
        //scope.accept = null;//$routeParams.action == "accept";
        //scope.acceptOffer = false;
        scope.acceptOfferError = false;

        //scope.password = "";
        //scope.checkPassword = "";

        //scope.sourcePhotoUrl = "";
        //scope.resultPhotoUrl = "";

        scope.requestData = {
            accept : null,
            acceptOffer : false,
            password : "",
            checkPassword : "",
            needSendPassword : false,
            sourcePhotoUrl : "",
            resultPhotoUrl : "",
            activeFirstTab : true,
            activeSecondTab : false,
            activeThirdTab : false
        };

        scope.pageData = {
            inviteId : "",
            status : "",
            offerText : "",
            isExpired : false
        };

        scope.passwordForm = null;

        scope.isLoadFromDataStorage = false;

        /*$scope.$watch('invitationAccept.requestData', function(newValue, oldValue) {
            //scope.create.firstName =  handleName(newValue);
            console.log(scope.requestData);
            scope.saveDataToStorage(scope.requestData);
        });*/

        for(var key in scope.requestData) {
            if(scope.requestData.hasOwnProperty(key)) {
                $scope.$watch("invitationAccept.requestData['" + key + "']", function(val, oldVal) {
                    if (scope.isLoadFromDataStorage) {
                        //console.log("saveDataToStorage" + val);
                        scope.saveDataToStorage(scope.requestData);
                    }
                });
            }
        }

        scope.loadDataFromStorage = function() {
            console.log("loadDataFromStorage");
            var defer = $q.defer();
            var result = localStorageService.get("acceptInvitation-" + scope.hash);
            scope.isLoadFromDataStorage = true;
            if (result != null) {
                defer.resolve(result);
            } else {
                defer.reject();
            }
            return defer.promise;
        };

        scope.saveDataToStorage = function(requestData) {
            var defer = $q.defer();
            localStorageService.set("acceptInvitation-" + scope.hash, requestData);
            defer.resolve(true);
            //defer.reject(false);
            return defer.promise;
        };

        scope.clearDataInStore = function() {
            localStorageService.remove("acceptInvitation-" + scope.hash);
        };

        scope.startAcceptInvitation = function() {
            scope.requestData.accept = true;
        };

        scope.showOffer = function() {
            $mdDialog.show({
                templateUrl: '/ng/invitation_accept/views/showOffer.html',
                clickOutsideToClose:true,
                controller: ['scope', function($scope) {
                    $scope.offerText = scope.pageData.offerText;

                    $scope.closeOffer = function() {
                        $mdDialog.hide();
                    };
                }]
            });
        };

        scope.loadAcceptPageData = function () {
            var nowTimeStamp = new Date().getTime();
            invitationAcceptService.loadAcceptPageData(scope.hash).then(
                function(response) {
                    scope.pageData = response.data;
                    scope.pageData.offerText = response.data.offer;
                    scope.pageData.isExpired = nowTimeStamp > response.data.expireDate;
                    scope.authUser = response.data.authUser;
                    scope.loadDataFromStorage().then(function(loadedRequestData){
                        scope.requestData = loadedRequestData;
                        scope.pageDataLoaded = true;
                    },function(){
                        scope.pageDataLoaded = true;
                    });
                }, function() {

                }
            );
        };

        scope.successInvitation = function() {
            scope.clearDataInStore();
            broadcastService.send("setLocation", "/");
            $mdDialog.show({
                templateUrl: '/ng/invitation_accept/views/acceptSuccess.html',
                clickOutsideToClose:true,
                controller: ['scope', function($scope) {
                    $scope.closeSuccessDialog = function() {
                        $mdDialog.hide();
                    };
                    $scope.showLoginDialog = function(){
                        broadcastService.send("loginDialog");
                        $mdDialog.hide();
                    };
                }]
            });
        };

        scope.acceptInvitation = function () {
            scope.actionInProcess = true;
            scope.actionResult = null;
            invitationAcceptService.acceptInvitation(
                scope.hash,
                scope.requestData.password,
                scope.requestData.needSendPassword,
                scope.requestData.sourcePhotoUrl,
                scope.requestData.resultPhotoUrl).then(
                function(response) {
                    scope.actionInProcess = false;
                    scope.actionResult = response.data;
                    if (scope.actionResult = 'ACCEPT_SUCCESS') {
                        scope.successInvitation();
                    }
                }, function(response) {
                    scope.actionInProcess = false;
                    scope.actionResult = response.data;
                }
            );
        };

        scope.rejectInvitation = function () {
            scope.requestData.accept = false;
            scope.actionInProcess = true;
            scope.actionResult = null;
            invitationAcceptService.rejectInvitation(scope.hash).then(
                function(response) {
                    scope.actionInProcess = false;
                    scope.actionResult = response.data;
                }, function(response) {
                    scope.actionInProcess = false;
                    scope.actionResult = response.data;
                }
            );
        };

        scope.firstTabDone = function() {
            var defer = $q.defer();
            if (scope.requestData.acceptOffer) {
                scope.requestData.activeFirstTab = false;
                scope.requestData.activeSecondTab = true;
                scope.requestData.activeThirdTab = false;
                scope.saveDataToStorage();
                defer.resolve(true);
            } else {
                scope.acceptOfferError = true;
                defer.reject(false);
            }
            return defer.promise;
        };

        scope.secondTabDone = function() {
            scope.passwordForm.$setSubmitted();
            var defer = $q.defer();
            if (Object.keys(scope.passwordForm.$error).length > 0) {
                defer.reject(false);
            } else {
                scope.requestData.activeFirstTab = false;
                scope.requestData.activeSecondTab = false;
                scope.requestData.activeThirdTab = true;
                scope.saveDataToStorage();
                defer.resolve(true);
            }
            return defer.promise;
        };

        scope.thirdTabDone = function() {
            var defer = $q.defer();
            if (
                scope.requestData.sourcePhotoUrl != null && scope.requestData.sourcePhotoUrl != "" &&
                scope.requestData.resultPhotoUrl != null && scope.requestData.resultPhotoUrl != ""
            ) {
                scope.acceptInvitation();
                defer.resolve(true);
            } else {
                defer.reject(false);
            }
            return defer.promise;
        };

        scope.loadAcceptPageData();
    });

    return app;
});
