'use strict';

define([
    'app',
    'leaflet'
], function (app, L) {
    app.directive('askorAddressForm', function () {

        function link(scope, element, attrs, controller) {
            controller.setContainer(element);

            element.on('$destroy', function () {
                //console.log('askorAddressForm $destroy');
            });
        }

        function controller($scope, translateService, leafletData, addressfieldService, $timeout,addressService) {
            var scope = this;

            scope.disabled = $scope.disabled !== undefined ? $scope.disabled : false;
            scope.addressType = $scope.addressType;
            scope.visibilityMap = $scope.visibilityMap;

            scope.addressfield = undefined;
            scope.countries = [];

            var addressBind = $scope.address;

            function initCountries() {
                scope.addressfield.options.forEach(function (country) {
                    var label = translateService.translate(country.label);

                    if (label === country.label) {
                    } else {
                        country.label = label;

                        if (addressBind.countryName && (addressBind.countryName === label)) {
                            scope.address.selectedCountry = country;
                        }

                        scope.countries.push(country);
                    }
                });
            };
            function initRegion() {
                if (addressBind.regionName)
                 scope.address.selectedRegion = {label : addressBind.regionName};
            };
            function initCity() {
                if (addressBind.cityName)
                 scope.address.selectedCity = {label : addressBind.cityName};
            };
            function initDistrict() {
                if (addressBind.districtName)
                 scope.address.selectedDistrict = {label : addressBind.districtName};
            };
            function initStreet() {
                if (addressBind.streetName)
                 scope.address.selectedStreet = {label : addressBind.streetName};
            };
            function initBuilding() {
                if (addressBind.buildingName)
                 scope.address.selectedBuilding = {label : addressBind.buildingName};
            };
            scope.setContainer = function (element) {
                scope.address.kladr.dom.$container = element;
            };
            scope.address = {
                optimalZoom: 4,

                countrySearchString: undefined,
                selectedCountry: undefined,
                countryLabel: 'Country',

                zipLabel: 'Почтовый индекс',
                zipCode: addressBind.zipCode,
                zipHint: undefined,
                zipFormat: undefined,

                regionLabel: addressBind.regionLabel,
                regionShortLabel: addressBind.regionShortLabel,
                regionSearchString: undefined,
                citySearchString: undefined,
                districtSearchString: undefined,
                streetSearchString: undefined,
                buildingSearchString: undefined,
                regions: [],
                selectedRegion: undefined,
                selectedCity: undefined,
                selectedDistrict: undefined,
                selectedStreet: undefined,
                selectedBuilding: undefined,
                regionName: addressBind.regionName,

                districtLabel: addressBind.districtLabel,
                districtShortLabel: addressBind.districtShortLabel,
                districtName: addressBind.districtName,

                cityLabel: addressBind.cityLabel,
                cityShortLabel: addressBind.cityShortLabel,
                cityName: addressBind.cityName,

                streetLabel: addressBind.streetLabel,
                streetShortLabel: addressBind.streetShortLabel,
                streetName: addressBind.streetName,

                buildingLabel: addressBind.buildingLabel,
                buildingShortLabel: addressBind.buildingShortLabel,
                buildingName: addressBind.buildingName,

                appartmentLabel: addressBind.appartmentLabel,
                appartmentShortLabel: addressBind.appartmentShortLabel,
                appartmentName: addressBind.appartmentName,

                /*thoroughfare: undefined,
                 thoroughfareLabel: undefined,

                 premise: undefined,
                 premiseLabel: undefined,*/

                checkFailed: undefined,
                google: {
                    dom: {

                    }
                },
                kladr: {
                    enabled: false,
                    token: $scope.kladrToken,
                    ready: false,
                    dom: {
                        $container: undefined,
                        $zip: undefined,
                        $region: undefined,
                        $district: undefined,
                        $city: undefined,
                        $street: undefined,
                        $building: undefined
                    },
                    toAddressString: function () {
                        var address = [];

                        if (scope.address.selectedCountry) {
                            address.push(scope.address.selectedCountry.label);
                            address.push($.kladr.getAddress(scope.address.kladr.dom.$container));

                            if (scope.address.appartmentShortLabel && scope.address.appartmentName) {
                                address.push(scope.address.appartmentShortLabel + ' ' + scope.address.appartmentName);
                            }
                        }

                        return address.join(', ');
                    }
                },

                toAddressString: function () {
                    var address = [];

                    if (this.selectedCountry) {
                        address.push(this.selectedCountry.label);

                        if (this.zipCode) {
                            address.push(this.zipCode);
                        }

                        if (this.regionName) {
                            address.push(this.regionName + (this.regionShortLabel ? ' ' + this.regionShortLabel: ''));
                            //address.push(this.regionName);
                        }

                        if (this.cityName) {
                            address.push((this.cityShortLabel ? this.cityShortLabel + ' ' : '') + this.cityName);
                            //address.push(this.cityName);
                        }

                        if (this.streetName) {
                            address.push((this.streetShortLabel ? this.streetShortLabel + ' ' : '') + this.streetName);
                            //address.push(this.streetName);
                        }

                        if (this.buildingName) {
                            address.push((this.buildingShortLabel ? this.buildingShortLabel + ' ' : '') + this.buildingName);
                            //address.push(this.buildingName);
                        }

                        if (scope.address.appartmentShortLabel && scope.address.appartmentName) {
                            address.push(scope.address.appartmentShortLabel + ' ' + scope.address.appartmentName);
                            //address.push(scope.address.appartmentName);
                        }

                        /*if (this.thoroughfare) {
                         var streetAddress = this.thoroughfare;

                         if (this.premise) streetAddress += ' ' + this.premise;

                         address.push(streetAddress);
                         }*/
                    }

                    return address.join(', ');
                },
                emptyZipRegion: function () {
                    this.zipCode = '';
                    this.zipHint = undefined;
                    this.zipFormat = undefined;

                    this.regionSearchString = undefined;
                    this.regions.length = 0;
                    this.selectedRegion = undefined;
                    this.regionName = undefined;
                }
            };

            scope.onCountryChange = function () {
                scope.address.kladr.enabled = false;
                if (scope.address.selectedCountry) {
                    if ($scope.addressFieldChange){
                        $scope.addressFieldChange('country',scope.address.selectedCountry.label,true);
                    }
                    if (scope.address.selectedCountry.iso === 'RU') {
                        scope.address.kladr.enabled = true;
                        initKladr();
                    }
                    else {
                       // initGoogleAddress(scope.address.selectedCountry.iso.toLowerCase());
                    }

                    for (var i = 0; i < scope.address.selectedCountry.fields.length; i++) {
                        var field = scope.address.selectedCountry.fields[i];

                        for (var key in field) {
                            /*if (key === 'thoroughfare') {
                             scope.address.thoroughfareLabel = field[key].label;
                             } else if (key === 'premise') {
                             scope.address.premiseLabel = field[key].label;
                             } else */
                            if (key === 'locality') {
                                for (var j = 0; j < field[key].length; j++) {
                                    var localityField = field[key][j];

                                    for (var localityKey in localityField) {
                                        if (localityKey === 'postalcode') {
                                            scope.address.zipLabel = localityField[localityKey].label;
                                            scope.address.zipHint = localityField[localityKey].eg;
                                            if (localityField[localityKey].format) scope.address.zipFormat = localityField[localityKey].format;
                                        } else if (localityKey === 'localityname') {
                                            scope.address.cityLabel = localityField[localityKey].label;
                                        } else if (localityKey === 'administrativearea') {
                                            scope.address.regionLabel = localityField[localityKey].label;
                                            if (localityField[localityKey].options && !scope.address.kladr.enabled) {

                                                localityField[localityKey].options.forEach(function (region) {
                                                    addRegion(region);
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if ($scope.addressFieldChange){
                        $scope.addressFieldChange('country','');
                    }
                    scope.address.emptyZipRegion();
                }

                sortRegions();

                addressBind.countryName = scope.address.selectedCountry ? scope.address.selectedCountry.label : undefined;

                scope.onZipCodeChange();
            };

            scope.onZipCodeChange = function () {
                addressBind.zipCode = scope.address.zipCode;
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('zipCode',scope.address.zipCode);
                }
                scope.onRegionChange();
            };

            scope.onRegionChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('region',scope.address.regionName);
                }
                if (!scope.address.regionName) {
                    scope.address.districtName = undefined;
                    scope.address.cityName = undefined;

                    if (scope.address.kladr.ready) {
                        scope.address.kladr.dom.$district.kladr('controller').clear();
                        scope.address.kladr.dom.$city.kladr('controller').clear();
                    }
                }

                addressBind.regionLabel = scope.address.regionLabel;
                addressBind.regionShortLabel = scope.address.regionShortLabel;
                addressBind.regionName = scope.address.regionName;

                scope.onDistrictChange();
            };

            scope.onDistrictChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('district',scope.address.districtName);
                }
                addressBind.districtLabel = scope.address.districtLabel;
                addressBind.districtShortLabel = scope.address.districtShortLabel;
                addressBind.districtName = scope.address.districtName;

                scope.onCityChange();
            };

            scope.onCityChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('city',scope.address.cityName);
                }
                if (!scope.address.cityName) {
                    scope.address.streetName = undefined;
                    if (scope.address.kladr.ready) scope.address.kladr.dom.$street.kladr('controller').clear();
                }

                addressBind.cityLabel = scope.address.cityLabel;
                addressBind.cityShortLabel = scope.address.cityShortLabel;
                addressBind.cityName = scope.address.cityName;

                scope.onStreetChange();
            };

            scope.onStreetChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('street',scope.address.streetName);
                }
                if (!scope.address.streetName) {
                    scope.address.buildingName = undefined;
                    if (scope.address.kladr.ready) scope.address.kladr.dom.$building.kladr('controller').clear();
                }

                addressBind.streetLabel = scope.address.streetLabel;
                addressBind.streetShortLabel = scope.address.streetShortLabel;
                addressBind.streetName = scope.address.streetName;

                scope.onBuildingChange();
            };

            scope.onBuildingChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('building',scope.address.buildingName);
                }
                if (!scope.address.buildingName) {
                    scope.address.appartmentName = undefined;
                }

                addressBind.buildingLabel = scope.address.buildingLabel;
                addressBind.buildingShortLabel = scope.address.buildingShortLabel;
                addressBind.buildingName = scope.address.buildingName;

                scope.onAppartmentChange();
            };

            scope.onAppartmentChange = function () {
                if ($scope.addressFieldChange){
                    $scope.addressFieldChange('room',scope.address.appartmentName);
                    $scope.addressFieldChange('roomLabel',scope.address.appartmentLabel);
                }
                addressBind.appartmentLabel = scope.address.appartmentLabel;
                addressBind.appartmentShortLabel = scope.address.appartmentShortLabel;
                addressBind.appartmentName = scope.address.appartmentName;
                scope.centerMap();
            };

            scope.autoZoom = function () {
                scope.address.optimalZoom = 4;
                if (scope.address.regionName) scope.address.optimalZoom = 7;
                if (scope.address.districtName) scope.address.optimalZoom = 7;
                if (scope.address.cityName) scope.address.optimalZoom = 10;
                if (scope.address.streetName) scope.address.optimalZoom = 13;
                if (scope.address.buildingName) scope.address.optimalZoom = 16;
            };

            function addRegion(region) {
                for (var code in region) {
                    var add = true;

                    for (var i = 0; i < scope.address.regions.length; i++) {
                        if (scope.address.regions[i].value === code) {
                            add = false;
                            break;
                        }
                    }

                    if (add && code) scope.address.regions.push({
                        value: code,
                        label: translateService.translate(region[code])
                    });
                }
            }

            function sortRegions() {
                scope.address.regions.sort(function (a, b) {
                    var keyA = a.label;
                    var keyB = b.label;

                    if (keyA < keyB) return -1;
                    if (keyA > keyB) return 1;
                    return 0;
                });
            }

            /*scope.matchedRegions = function (searchString) {
                if (!searchString) return scope.address.regions;

                var result = [];

                scope.address.regions.forEach(function (region) {
                    if (region.label.toLowerCase().startsWith(searchString.toLowerCase())) {
                        result.push(region);
                    }
                });

                return result;
            };*/
            var getResponseFilter = function(addressFieldType){
                var type = "";
                var excludeContainsName = null;
                switch (addressFieldType) {
                    case 'region':
                        type = "administrative_area_level_1";
                        break;
                    case 'district':
                        // Если в названии региона есть слово город значит это город федеральный и у него есть
                        // районы с типом sublocality_level_1, sublocality_level_2 или sublocality
                        // иначе это простой район региона с типом administrative_area_level_2

                        var findRegionValue = scope.address.regionName;
                        if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                            type = "sublocality";
                        } else {
                            type = "administrative_area_level_2";
                            excludeContainsName = "город"; // Исключаем из районов города
                        }
                        break;
                    case 'city':
                        type = "locality";
                        break;
                    case 'street':
                        type = "route";
                        break;
                    case 'building':
                        type = "street_number";
                        break;
                }
                return {type: type, excludeContainsName: excludeContainsName};
            };
            scope.matchedRegions = function(searchString) {
                if (!searchString) {
                    return [];
                }
                var locale = scope.address.selectedCountry.iso.toLowerCase();
                return addressService.getGoogleAddressAutocomplete(searchString,"ru",'country:' + locale + getComponents('region'),getResponseFilter('region'));
            };
            scope.matchedCities = function(searchString) {
                if (!searchString) {
                    return [];
                }
                var locale = scope.address.selectedCountry.iso.toLowerCase();
                return addressService.getGoogleAddressAutocomplete(searchString,"ru",'country:' + locale + getComponents('city'),getResponseFilter('city'));
            };
            scope.matchedDistricts = function(searchString) {
                if (!searchString) {
                    return [];
                }
                var locale = scope.address.selectedCountry.iso.toLowerCase();
                return addressService.getGoogleAddressAutocomplete(searchString,"ru",'country:' + locale + getComponents('district'),getResponseFilter('district'));
            };
            scope.matchedStreets = function(searchString) {
                if (!searchString) {
                    return [];
                }
                var locale = scope.address.selectedCountry.iso.toLowerCase();
                return addressService.getGoogleAddressAutocomplete(searchString,"ru",'country:' + locale + getComponents('street'),getResponseFilter('street'));
            };
            scope.matchedBuildings = function(searchString) {
                if (!searchString) {
                    return [];
                }
                var locale = scope.address.selectedCountry.iso.toLowerCase();
                return addressService.getGoogleAddressAutocomplete(searchString,"ru",'country:' + locale + getComponents('building'),getResponseFilter('building'));
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

            scope.onRegionSelect = function () {
                if (scope.address.selectedRegion) {
                    scope.address.regionName = scope.address.selectedRegion.label;
                } else {
                    scope.address.regionName = undefined;
                }

                scope.onRegionChange();
            };
            scope.onCitySelect = function () {
                if (scope.address.selectedCity) {
                    scope.address.cityName = scope.address.selectedCity.label;
                } else {
                    scope.address.cityName = undefined;
                }

                scope.onCityChange();
            };
            scope.onDistrictSelect = function () {
                if (scope.address.selectedDistrict) {
                    scope.address.districtName = scope.address.selectedDistrict.label;
                } else {
                    scope.address.districtName = undefined;
                }

                scope.onDistrictChange();
            };
            scope.onStreetSelect = function () {
                if (scope.address.selectedStreet) {
                    scope.address.streetName = scope.address.selectedStreet.label;
                } else {
                    scope.address.streetName = undefined;
                }

                scope.onStreetChange();
            };
            scope.onBuildingSelect = function () {
                if (scope.address.selectedBuilding) {
                    scope.address.buildingName = scope.address.selectedBuilding.label;
                } else {
                    scope.address.buildingLabel = undefined;
                }
                scope.onBuildingChange();
            };

            /*var mapVar;

            leafletData.getMap($scope.name + '-map').then(function (map) {
                mapVar = map;
            });*/

            var geocoder = new google.maps.Geocoder();

            scope.centerMap = function () {
                scope.autoZoom();

                if (scope.address.kladr.enabled) {
                    var address = scope.address.kladr.toAddressString();

                    ymaps.geocode(address).then(
                        function (res) {
                            if (res.geoObjects.get(0).geometry.getCoordinates()) {
                                if ($scope.addressFieldChange){
                                    $scope.addressFieldChange('geoPosition',res.geoObjects.get(0).geometry.getCoordinates()[0]+","+res.geoObjects.get(0).geometry.getCoordinates()[1]);
                                }
                                scope.mapCenter.lat = res.geoObjects.get(0).geometry.getCoordinates()[0];
                                scope.mapCenter.lng = res.geoObjects.get(0).geometry.getCoordinates()[1];

                                scope.markers.m1.lat = res.geoObjects.get(0).geometry.getCoordinates()[0];
                                scope.markers.m1.lng = res.geoObjects.get(0).geometry.getCoordinates()[1];

                                scope.mapCenter.zoom = scope.address.optimalZoom;
                            }
                        }, function (err) {
                            console.log('Yandex maps ошибка', err);
                        }
                    );
                } else {
                    var address = scope.address.toAddressString();

                    geocoder.geocode({'address': address}, function (results, status) {
                        if (status === google.maps.GeocoderStatus.OK) {
                            if (results.length) {
                                if ($scope.addressFieldChange){
                                    $scope.addressFieldChange('geoPosition',results[0].geometry.location.lat()+","+results[0].geometry.location.lng());
                                }
                                scope.mapCenter.lat = results[0].geometry.location.lat();
                                scope.mapCenter.lng = results[0].geometry.location.lng();
                                scope.mapCenter.zoom = scope.address.optimalZoom;

                                scope.markers.m1.lat = results[0].geometry.location.lat();
                                scope.markers.m1.lng = results[0].geometry.location.lng();
                            }

                            scope.mapCenter.zoom = scope.address.optimalZoom;
                        } else {
                            console.log('Geocode was not successful for the following reason: ' + status);
                        }
                    });
                }
            };

            scope.mapCenter = {
                lat: 61.52401,
                lng: 105.31875600000001,
                zoom: 4
            };

            scope.markers = {
                m1: {
                    lat: 61.52401,
                    lng: 105.31875600000001
                }
            };

            scope.layers = {
                baselayers: {
                    yandex: {
                        name: 'Yandex',
                        type: 'yandex',
                        layerOptions: {
                            layerType: 'map'
                        }
                    },
                    googleTerrain: {
                        name: 'Google Terrain',
                        layerType: 'TERRAIN',
                        type: 'google'
                    },
                    googleHybrid: {
                        name: 'Google Hybrid',
                        layerType: 'HYBRID',
                        type: 'google'
                    },
                    googleRoadmap: {
                        name: 'Google Streets',
                        layerType: 'ROADMAP',
                        type: 'google'
                    },
                    googleSatellite: {
                        name: 'Google Satellite',
                        layerType: 'SATELLITE',
                        type: 'google'
                    },
                    osm: {
                        name: 'OpenStreetMap',
                        url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                        type: 'xyz'
                    }
                }
            };
            function initGoogleAddress(countryData) {
                scope.address.google.dom.$room = scope.address.kladr.dom.$container.find('[name="roomGoogle"]');
                scope.address.google.dom.$zip = scope.address.kladr.dom.$container.find('[name="zipGoogle"]');
                scope.address.google.dom.$addressBlock = scope.address.kladr.dom.$container.find('[name="otherCountrySection"]');
                scope.address.google.dom.$addressBlock.googleAddress(countryData, scope.address.google.dom.$zip, scope.address.google.dom.$room, "room");
            }
            var getComponents = function(addressFieldType){
                var administrativeConditions = "";
                var localityConditions = "";
                var routeConditions = "";
                switch (addressFieldType) {
                    case 'region':
                        break;
                    case 'district':
                        var findRegionValue = scope.address.regionName;
                        if (findRegionValue) {
                            administrativeConditions = "|administrative_area:" + findRegionValue;
                        }
                        break;
                    case 'city':
                        var findRegionValue = scope.address.regionName;
                        var findDistrictValue = scope.address.districtName;
                        if (findDistrictValue) {
                            administrativeConditions = "|administrative_area:" + findRegionValue + " " + findDistrictValue;
                        } else {
                            if (findRegionValue) {
                                administrativeConditions = "|administrative_area:" + findRegionValue;
                            }
                        }
                        break;
                    case 'street':
                        var findRegionValue = scope.address.regionName;
                        var findDistrictValue = scope.address.districtName;
                        var findCityValue = scope.address.cityName;

                        // Если регион - федеральный город
                        if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                            administrativeConditions = "|administrative_area:" + findRegionValue;
                            if (findDistrictValue) {
                                localityConditions = "|locality:" + findDistrictValue;
                            }
                        } else {
                            if (findDistrictValue) {
                                administrativeConditions = "|administrative_area:" + findRegionValue + " " + findDistrictValue;
                            } else {
                                administrativeConditions = "|administrative_area:" + findRegionValue;
                            }
                            localityConditions = "|locality:" + findCityValue;
                        }
                        break;
                    case 'building':
                        var findRegionValue = scope.address.regionName;
                        var findDistrictValue = scope.address.districtName;
                        var findCityValue = scope.address.cityName;
                        var findStreetValue = scope.address.streetName;

                        if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                            administrativeConditions = "|administrative_area:" + findRegionValue;
                            if (findDistrictValue) {
                                localityConditions = "|locality:" + findDistrictValue;
                            }
                        } else {
                            if (findDistrictValue) {
                                administrativeConditions = "|administrative_area:" + findRegionValue + " " + findDistrictValue;
                            } else {
                                administrativeConditions = "|administrative_area:" + findRegionValue;
                            }
                            localityConditions = "|locality:" + findCityValue;
                        }
                        routeConditions = "|route:" + findStreetValue;
                        break;
                }
                return administrativeConditions + localityConditions + routeConditions;
            };

            function initKladr() {
                if (scope.address.kladr.ready) return;

                scope.address.kladr.dom.$zip = scope.address.kladr.dom.$container.find('[name="zipKladr"]');
                scope.address.kladr.dom.$region = scope.address.kladr.dom.$container.find('[name="regionKladr"]');
                scope.address.kladr.dom.$district = scope.address.kladr.dom.$container.find('[name="districtKladr"]');
                scope.address.kladr.dom.$city = scope.address.kladr.dom.$container.find('[name="cityKladr"]');
                scope.address.kladr.dom.$street = scope.address.kladr.dom.$container.find('[name="streetKladr"]');
                scope.address.kladr.dom.$building = scope.address.kladr.dom.$container.find('[name="buildingKladr"]');

                $.kladr.setDefault({
                    parentInput: scope.address.kladr.dom.$container,
                    verify: true,
                    check: function (obj) {
                        if (obj) {
                            scope.address.checkFailed = false;
                        } else {
                            scope.address.checkFailed = true;
                        }

                        $scope.$apply();
                    }
                });

                scope.address.kladr.dom.$region.kladr({
                    type: $.kladr.type.region,
                    token: scope.address.kladr.token,
                    verify : true,
                    change: function (obj) {
                        if (obj) {
                            scope.address.regionName = obj.name;
                            scope.address.regionLabel = obj.type;
                            if ($scope.addressFieldChange) {
                                $scope.addressFieldChange('regionLabel', scope.address.regionLabel);
                            }
                            scope.address.regionShortLabel = obj.typeShort;
                            scope.onRegionChange();
                        }
                    }
                });

                scope.address.kladr.dom.$district.kladr({
                    type: $.kladr.type.district,
                    token: scope.address.kladr.token,
                    change: function (obj) {
                        if (obj) {
                            scope.address.districtName = obj.name;
                            scope.address.districtLabel = obj.type;
                            scope.address.districtShortLabel = obj.typeShort;
                            if ($scope.addressFieldChange) {
                                $scope.addressFieldChange('districtLabel', scope.address.districtLabel);
                                $scope.addressFieldChange('districtShortLabel', scope.address.districtShortLabel);
                            }
                            scope.onDistrictChange();
                        }
                    }
                });

                scope.address.kladr.dom.$city.kladr({
                    type: $.kladr.type.city,
                    token: scope.address.kladr.token,
                    change: function (obj) {
                        if (obj) {
                            scope.address.cityName = obj.name;
                            scope.address.cityLabel = obj.type;
                            scope.address.cityShortLabel = obj.typeShort;
                            if ($scope.addressFieldChange) {
                                $scope.addressFieldChange('cityLabel', scope.address.cityLabel);
                                $scope.addressFieldChange('cityLabelShort', scope.address.cityShortLabel);
                            }
                            scope.onCityChange();
                        }
                    }
                });
                scope.address.kladr.dom.$street.kladr({
                    type: $.kladr.type.street,
                    token: scope.address.kladr.token,
                    change: function (obj) {
                        if (obj) {
                            scope.address.streetName = obj.name;
                            scope.address.streetLabel = obj.type;
                            scope.address.streetShortLabel = obj.typeShort;
                            if ($scope.addressFieldChange) {
                                $scope.addressFieldChange('streetLabel',  scope.address.streetLabel);
                                $scope.addressFieldChange('streetLabelShort', scope.address.streetShortLabel);
                            }
                            scope.onStreetChange();
                        }
                    }
                });

                scope.address.kladr.dom.$building.kladr({
                    type: $.kladr.type.building,
                    token: scope.address.kladr.token,
                    verify: true,
                    change: function (obj) {
                        if (obj) {
                            scope.address.buildingName = obj.name;
                            scope.address.buildingLabel = obj.type;
                            scope.address.buildingShortLabel = obj.typeShort;
                            if ($scope.addressFieldChange) {
                                $scope.addressFieldChange('houseLabel',scope.address.buildingLabel);
                            }
                            scope.onBuildingChange();
                        }
                    }
                });

                scope.address.kladr.dom.$zip.kladrZip(scope.address.kladr.dom.$container);

                scope.address.kladr.ready = true;
            }

            addressfieldService.get().then(function (addressfield) {
                scope.addressfield = addressfield;
                scope.address.countryLabel = scope.addressfield.label;
                initCountries();
            });
            initRegion();
            initCity();
            initDistrict();
            initStreet();
            initBuilding();
        }

        return {
            restrict: 'E',
            transclude: false,
            scope: {
                name: '=',
                address: '=',
                kladrToken: '=',
                disabled: '=',
                addressFieldChange: "=",
                addressType: "=",
                visibilityMap: "="
            },
            controller: controller,
            controllerAs: 'addressForm',
            templateUrl: 'components/geo/address.html',
            link: link
        }
    });

    return app;
});