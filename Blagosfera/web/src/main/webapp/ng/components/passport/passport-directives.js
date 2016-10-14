'use strict';

define([
    'app',
], function (app) {
    app.directive('askorPassportForm', function () {

        function link(scope, element, attrs, controller) {
        }

        function controller($scope, translateService,  addressfieldService,$filter,passportService) {
            var scope = this;
            scope.data = $scope.passportData;
            scope.citizenshipSettings = $scope.citizenshipSettings;
            scope.currentCitizenshipSettings = {};
            scope.countrySearchString = "";
            scope.countryLabel = "Country";
            scope.addressfield = [];
            scope.countries = [];
            scope.readonly = $scope.readonly;
            $scope.maxUploadSize = "8MB";
            $scope.minPhotoWidth = 100;
            $scope.minPhotoHeight = 30;
            $scope.onChooseFile = function(){
                if ($scope.file != null) {
                    var fileReader = new FileReader();
                    fileReader.readAsDataURL($scope.file);
                    fileReader.onload = function (e) {
                        $scope.imageIsLoading = false;
                        $scope.fileUrl = e.target.result;
                        passportService.saveSignature($scope.fileUrl).then(function(response){
                            scope.data.signature = response.data;
                        });
                    };
                }
            };
            scope.setDataToSend = function(field,value,translateCountry) {
                if (!value) {
                    value = "";
                }
                if ($scope.passportFieldChange) {
                    $scope.passportFieldChange(field,value,translateCountry);
                }
            };
            scope.setDataToSendDate = function(field,value){
                if ($scope.passportFieldChange) {
                    if (!value) {
                        $scope.passportFieldChange(field, "");
                        return;
                    }
                    $scope.passportFieldChange(field, moment(new Date(value)).format('DD.MM.YYYY'));
                }
            };
            scope.matchedCountries = function (searchString) {
                if (!searchString) return scope.countries;

                var result = [];

                scope.countries.forEach(function (country) {
                    if (country.label.toLowerCase().startsWith(searchString.toLowerCase())) {
                        result.push(country);
                    }
                });

                return result;
            };
            scope.setCurrentCitizenshipSettings = function(iso){
                for (var i in scope.citizenshipSettings) {
                  if (scope.citizenshipSettings[i].countryComCode === iso) {
                      scope.currentCitizenshipSettings  = scope.citizenshipSettings[i];
                      return;
                  }
                }
                scope.setCurrentCitizenshipSettings("ru");
            };
            scope.onCountryChange = function () {
                if (scope.data.citizenship) {
                    scope.setDataToSend('citizenship',scope.data.citizenship.label,true);
                    scope.setCurrentCitizenshipSettings(scope.data.citizenship.iso.toLowerCase());
                }
                else {
                    scope.setDataToSend('citizenship','');
                }
            };
            function initCountries() {
                scope.addressfield.options.forEach(function (country) {
                    var label = translateService.translate(country.label);

                    if (label === country.label) {
                    } else {
                        country.label = label;
                        if (scope.data.citizenship === label) {
                            scope.data.citizenship = country;
                        }
                        scope.countries.push(country);
                    }
                });
                if ($scope.readonly) {
                    scope.onCountryChange();
                }
            }
            addressfieldService.get().then(function (addressfield) {
                scope.addressfield = addressfield;
                scope.countryLabel = scope.addressfield.label;
                initCountries();
            });
        }

        return {
            restrict: 'E',
            transclude: false,
            scope: {
                passportFieldChange: "=",
                passportData: "=",
                readonly: "=",
                citizenshipSettings: "="
            },
            controller: controller,
            controllerAs: 'askorPassportForm',
            templateUrl: 'components/passport/passport.html',
            link: link
        }
    });

    return app;
});