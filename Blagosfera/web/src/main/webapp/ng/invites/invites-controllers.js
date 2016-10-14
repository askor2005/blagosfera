'use strict';
var qwe;
define([
    'app',
    'petrovich'
], function (app, petrovich) {

    app.controller('InvitesPageCtrl', function (
        $scope, $filter, $routeParams, invitesPageService, $mdDialog, $q, broadcastService, localStorageService) {
        
        var scope = this;

        scope.relations = [];

        scope.showFilter = false;
        scope.showSearch = false;

        //scope.qwe = "Строк на странице:";

        scope.create = {
            //email : "dreanuwegwe@bk.ru",
            email : "",
            emailError : "",
            //firstName : "Пупа",
            firstName : "",
            firstNameError : true,
            //middleName : "Олегович",
            middleName : "",
            middleNotExists : false,
            //lastName : "Васькин",
            lastName : "",
            lastNameError : true,
            middleNameError : true,
            //gender : "male",
            gender : "",
            genderError : true,
            fioRodPadeg : "",
            fioTvorPadeg : "",
            isGuarantee : false,
            isKnowPersonally : false,
            howLongFamiliar : 0,
            fillFormError : "",
            secondTabButtonClicked : false
        };

        function handleName(value) {
            var result = "";
            if (value != null) {
                value = value.replace(/[\s]*/g, "");
                value = value.toLowerCase();
                if (value.length > 0) {
                    result = value.substr(0,1).toUpperCase() + value.substr(1,value.length - 1);
                }
            }
            return result;
        }

        $scope.$watch('invites.create.firstName', function(newValue, oldValue) {
            scope.create.firstName =  handleName(newValue);
        });
        $scope.$watch('invites.create.middleName', function(newValue, oldValue) {
            scope.create.middleName =  handleName(newValue);
        });
        $scope.$watch('invites.create.lastName', function(newValue, oldValue) {
            scope.create.lastName =  handleName(newValue);
        });

        function clearCreateData() {
            //scope.create.email = "dreanuwegwe@bk.ru";
            scope.create.email = "";
            scope.create.emailError = "";
            //scope.create.firstName = "Пупа";
            scope.create.firstName = "";
            //scope.create.middleName = "Олегович";
            scope.create.middleName = "";
            scope.create.middleNotExists = false;
            //scope.create.lastName = "Васькин";
            scope.create.lastName = "";
            //scope.create.gender = "male";
            scope.create.gender = "";
            scope.create.firstNameError = true;
            scope.create.lastNameError = true;
            scope.create.middleNameError = true;
            scope.create.genderError = true;
            scope.create.fioRodPadeg = "";
            scope.create.fioTvorPadeg = "";
            scope.create.isGuarantee = false;
            scope.create.isKnowPersonally = false;
            scope.create.howLongFamiliar = 0;
            scope.create.fillFormError = "";
            scope.create.secondTabButtonClicked = false;
        }

        scope.pageData = {
            inviteCount : {
                countRegisterd : "???",
                countVerified : "???",
                countRegistrators : "???"
            }
        };

        scope.selectedInvite = null;

        var limit = localStorageService.get("invites.query.limit") != null ? localStorageService.get("invites.query.limit") : 10;

        scope.count = 0;
        scope.query = {
            order: '0',
            limit: limit,
            page: 1
        };

        $scope.$watch('invites.query.limit', function(newValue, oldValue) {
            //scope.create.firstName =  handleName(newValue);
            localStorageService.set("invites.query.limit", newValue);
        });

        scope.data = [];

        scope.filter = {
            emailFilter : "",
            status : null,
            inviteDateStart : null,
            inviteDateEnd : null,
            registrationDateStart : null,
            registrationDateEnd : null,
            verified : null,
            verifiedDateStart : null,
            verifiedDateEnd : null,
            countUsersStart : null,
            countUsersEnd : null,
            countCommunitiesStart : null,
            countCommunitiesEnd : null,
            verifierName : null,
            guarantee : null,
            gender : null,
            countInvitesStart : null,
            countInvitesEnd : null,
            howLongFamiliarStart : null,
            howLongFamiliarEnd : null,
            registratorLevel : null
        };

        /*$scope.$watch('invites.filter.emailFilter', function(newValue, oldValue) {
            if ((newValue != null && newValue.length > 2) || (newValue == null || newValue.length == 0)) {
                scope.getInvites();
            }
        });*/

        scope.getInvites = function() {
            // TODO Почему то параметры страницы устанавливаеются позже вызова этой функкции
            if (
                ((scope.filter.emailFilter != null && scope.filter.emailFilter.length > 2) ||
                (scope.filter.emailFilter == null || scope.filter.emailFilter.length == 0)) &&
                ((scope.filter.verifierName != null && scope.filter.verifierName.length > 2) ||
                (scope.filter.verifierName == null || scope.filter.verifierName.length == 0))
            ) {
                setTimeout(function () {
                    invitesPageService.loadInvites(scope.query, scope.filter).then(
                        function (response) {
                            scope.data = response.data.invites;
                            scope.count = response.data.count;
                        }
                    );
                }, 100);
            }
        };

        scope.loadPageData = function() {
            invitesPageService.getPageData().then(
                function (response) {
                    scope.pageData.inviteCount = response.data.inviteCount;
                    //scope.count = scope.pageData.inviteCount.commonCount;
                    scope.relations = response.data.inviteRelationShipTypes;
                }
            );
        };
        //$scope.qweqwe = function(){};
        scope.checkEmail = function() {
            invitesPageService.validateEmail(scope.create.email).then(
                function () {
                }, function (response) {
                    scope.create.emailError = response.data.message;
                }
            );
        };
        qwe = scope;
        scope.loadPageData();
        scope.getInvites();
        scope.showTabDialog = function(isUserVerified) {
            if (isUserVerified) {
                clearCreateData();
                $mdDialog.show({
                    //controller: scope,
                    templateUrl: '/ng/invites/views/invites-create.html',
                    clickOutsideToClose: true,
                    controller: [ "$scope", function($scope) {
                        $scope.invites = scope;
                    }]
                }).then(function(answer) {
                    //$scope.status = 'You said the information was "' + answer + '".';
                }, function() {
                    //$scope.status = 'You cancelled the dialog.';
                });
            } else {
                broadcastService.send('dialog', {
                    type: 'info',
                    title: '',
                    message: $filter('translate')('invite.not.verified.user.create.error')
                });
            }
        };

        scope.firstTabDone = function() {
            var defer = $q.defer();
            if (scope.create.emailError == '' && scope.create.email != null && scope.create.email != '') {
                invitesPageService.validateEmail(scope.create.email).then(
                    function () {
                        defer.resolve(true);
                    }, function (response) {
                        scope.create.emailError = response.data.message;
                        defer.reject(false);
                    }
                );
            } else {
                defer.reject(false);
            }
            return defer.promise;
        };

        scope.secondTabDone = function() {
            scope.create.secondTabButtonClicked = true;
            var sex = scope.create.gender == "male";
            var defer = $q.defer();
            if (scope.create.firstNameError || scope.create.lastNameError || scope.create.middleNameError == true || scope.create.genderError) {
                defer.reject(false);
            } else {
                scope.create.fioRodPadeg = getFioRodPadeg(sex, scope.create.firstName, scope.create.middleName, scope.create.lastName);
                scope.create.fioTvorPadeg = getFioTvorPadeg(sex, scope.create.firstName, scope.create.middleName, scope.create.lastName);
                defer.resolve(true);
            }
            return defer.promise;
        };

        scope.thirdTabDone = function() {
            var defer = $q.defer();
            var relationships = [];
            for (var index in scope.relations) {
                var relation = scope.relations[index];
                if (relation.active) {
                    relationships.push(relation.id);
                }
            }

            var createInviteWrapper = {
                email : scope.create.email,
                invitedLastName : scope.create.lastName,
                invitedFirstName : scope.create.firstName,
                invitedFatherName : scope.create.middleName,
                invitedGender : scope.create.gender == "male" ? "М" : "Ж",
                guarantee : scope.create.isGuarantee,
                howLongFamiliar : scope.create.howLongFamiliar,
                relationships : relationships
            };

            invitesPageService.createInvite(createInviteWrapper).then(
                function (response) {
                    scope.loadPageData();
                    scope.getInvites();
                    $mdDialog.hide();
                    scope.doneCreate();
                    defer.resolve(true);
                }, function (response){
                    scope.create.fillFormError = response.data.message;
                    defer.reject(false);
                }
            );

            return defer.promise;
        };

        scope.doneCreate = function() {
            $mdDialog.show({
                templateUrl: '/ng/invites/views/invites-create-done.html',
                clickOutsideToClose:true,
                controller: [ "$scope", function($scope) {
                    $scope.invites = scope;
                }]
            }).then(function(answer) {

            }, function() {

            });
        };
        //scope.doneCreate();
        scope.closeDoneDialog = function() {
            $mdDialog.hide();
        };

        scope.cancelCreate = function (){
            $mdDialog.hide();
        };

        scope.selectRow = function (invite, $app){
            $mdDialog.show({
                templateUrl: '/ng/invites/views/invites-details.html',
                clickOutsideToClose:true,
                controller: ['$scope', function($scope) {
                    $scope.invite = invite;
                    $scope.app = $app;

                    $scope.setLocation = function(url) {
                        $mdDialog.hide();
                        broadcastService.send("setLocation", url);
                    }
                }]
            }).then(function(answer) {

            }, function() {

            });

            //console.log(invite);
        };

        scope.sendToEmail = function(id) {
            invitesPageService.sendToEmail(id).then(
                function() {
                    scope.getInvites();
                }, function() {

                }
            );
        };


    });

    function getFioRodPadeg(sex, firstName, middleName, lastName) {
        var lastNamePadeg = getLastNameRodPadeg(sex, lastName);
        var result = lastNamePadeg + " " + firstName.substr(0, 1) + ".";
        if (middleName != null && middleName != "") {
            result = result + " " + middleName.substr(0,1) + "."
        }
        return result;
    }

    function getFioTvorPadeg(sex, firstName, middleName, lastName) {
        var lastNamePadeg = getLastNameTvorPadeg(sex, lastName);
        var result = lastNamePadeg + " " + firstName.substr(0, 1) + ".";
        if (middleName != null && middleName != "") {
            result = result + " " + middleName.substr(0,1) + "."
        }
        return result;
    }

    function getLastNameRodPadeg(sex, lastName) {
        var result = "";
        if (sex) {
            result = petrovich.male.last.genitive(lastName);
        } else {
            result = petrovich.female.last.genitive(lastName);
        }
        return result;
    }

    function getLastNameTvorPadeg(sex, lastName) {
        var result = "";
        if (sex) {
            result =  petrovich.male.last.instrumental(lastName);
        } else {
            result =  petrovich.female.last.instrumental(lastName);
        }
        return result;
    }

    return app;
});
