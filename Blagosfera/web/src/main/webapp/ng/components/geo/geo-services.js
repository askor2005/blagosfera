'use strict';

define([
    'app'
], function (app) {

    app.factory('addressfieldService', function (httpService, $q) {
        var addressfield = undefined;

        return {
            get: function () {
                var q = $q.defer();

                if (addressfield) {
                    q.resolve(addressfield);
                } else {
                    httpService.get('lib/bower_components/addressfield.json/src/addressfield.json', {headers: {'Cache-Control': 'no-cache'}}).then(
                        function (response) {
                            addressfield = response.data;
                            q.resolve(addressfield);
                        }, function (response) {
                            q.reject(response);
                        }
                    );
                }

                return q.promise;
            }
        }
    });

    app.factory('addressService', function (httpService, $q) {
        return {
            createEmptyAddress: function () {
                return {
                    countryName: undefined,
                    zipCode: undefined,

                    regionLabel: 'Область',
                    regionShortLabel: 'обл.',
                    regionName: undefined,

                    districtLabel: 'Район',
                    districtShortLabel: 'р-он.',
                    districtName: undefined,

                    cityLabel: 'Город',
                    cityShortLabel: 'г.',
                    cityName: undefined,

                    streetLabel: 'Улица',
                    streetShortLabel: 'ул.',
                    streetName: undefined,

                    buildingLabel: 'Дом/корпус',
                    buildingShortLabel: 'д.',
                    buildingName: undefined,

                    appartmentLabel: 'Квартира/офис',
                    appartmentShortLabel: 'кв./оф.',
                    appartmentName: undefined,

                    location: {
                        lat: undefined,
                        lng: undefined
                    }
                };
            },
            getGoogleAddressAutocomplete: function (address,language,components,responseFilter) {
                var data = {
                    address: address,
                    language: language,
                    components: components
                };
                var q = $q.defer();
                var responseFilter = responseFilter;
                httpService.getJson('https://maps.googleapis.com/maps/api/geocode/json',{},data).then(function (response) {
                    response = response.data;
                    var responseData = [];
                    var tempMap = {};
                    var responseFilterType = responseFilter.type;
                    var responseFilterExcludeContainsName = responseFilter.excludeContainsName;
                    for(var i in response.results) {
                        var result = response.results[i];
                        var coordinates = "";
                        var zipCode = "";
                        if (result.geometry != null && result.geometry.location != null) {
                            coordinates = result.geometry.location.lat;
                            coordinates += "," + result.geometry.location.lng;
                        }
                        var findAddressComponent = null;
                        for(var j in result.address_components) {
                            var addressComponent = result.address_components[j];
                            for (var index in addressComponent.types) {
                                var type = addressComponent.types[index];
                                var longName = addressComponent.long_name;
                                if (type.indexOf(responseFilterType) > -1 &&
                                    (responseFilterExcludeContainsName == null || longName.toLowerCase().indexOf(responseFilterExcludeContainsName) == -1)) {
                                    findAddressComponent = addressComponent;
                                }
                                if (type == "postal_code") {
                                    zipCode = longName;
                                }
                            }
                        }
                        if (findAddressComponent != null) {
                            findAddressComponent["placeData"] = coordinates + "||" + zipCode;
                            tempMap[findAddressComponent.long_name] = findAddressComponent;
                        }
                    }
                    for (var longName in tempMap) {
                        var item = tempMap[longName];
                        item.label = item.long_name;
                        responseData.push(item);
                    }
                    return q.resolve(responseData);
                },function(response) {
                    q.reject(response);
                });
                return q.promise;
            }
        }
    });

    return app;
});