(function($, window){

    var addressFieldTypes = {
        country : "country",

        region : "region",
        district : "district",
        city : "city",
        street : "street",
        building : "building",

        geoPosition : "geoPosition",
        geoLocation : "geoLocation",

        zipCode : "zipCode",

        room : "room",
        roomType : "roomType"
    };

    function initFieldsValues($addressBlock) {
        var regionVal = jQuery.data($addressBlock.get(0), addressFieldTypes.region).val();

        // Если найден федеральный город, то лочим город
        if (regionVal.toLowerCase().indexOf("город") > -1) {
            jQuery.data($addressBlock.get(0), addressFieldTypes.city).closest(".row").hide();
        } else {
            jQuery.data($addressBlock.get(0), addressFieldTypes.city).closest(".row").show();
        }
    }

    function initRoomInput($addressBlock, jqRoom) {
        // При изменении квартиры делать изменение адреса
        if (jqRoom.attr("input_inited") != "true") {
            jqRoom.attr("input_inited", "true");console.log(jqRoom);
            jqRoom.on("input", function () {
                if ($addressBlock.attr("addressSystem") == "google") {
                    updateGeoPosition($addressBlock, null, null);
                }
            });
        }
    }

    function initAddressFieldTypehead($addressBlock, addressFieldType) {
        var jqAddressFieldNode = jQuery.data($addressBlock.get(0), addressFieldType);

        var clearFields = function() {
            switch (addressFieldType) {
                case addressFieldTypes.region:
                    jQuery.data($addressBlock.get(0), addressFieldTypes.city).closest(".row").show();
                    jQuery.data($addressBlock.get(0), addressFieldTypes.district).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.district + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.city).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.city + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.room).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode).val("");
                    break;
                case addressFieldTypes.district:
                    jQuery.data($addressBlock.get(0), addressFieldTypes.city).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.city + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.room).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode).val("");
                    break;
                case addressFieldTypes.city:
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.street + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.room).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode).val("");
                    break;
                case addressFieldTypes.street:
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.building + "_find_value", "");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.room).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode).val("");
                    break;
                case addressFieldTypes.building:
                    jQuery.data($addressBlock.get(0), addressFieldTypes.room).val("");
                    jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode).val("");
                    break;
            }
        };

        // Событие ввода текста в поле
        if (jqAddressFieldNode.attr("input_inited") != "true") {
            jqAddressFieldNode.attr("input_inited", "true");
            jqAddressFieldNode.on('input', function () {
                if ($addressBlock.attr("addressSystem") == "google") {
                    var val = jQuery.data($addressBlock.get(0), addressFieldType).val();
                    // Если поле полностью очистили
                    if (val == null || val == "") {
                        jQuery.data($addressBlock.get(0), addressFieldType + "_find_value", "");
                    }
                    clearFields();
                }
            });
        }
        if (jqAddressFieldNode.attr("blur_inited") != "true") {
            jqAddressFieldNode.attr("blur_inited", "true");
            jqAddressFieldNode.on("blur", function () {
                if ($addressBlock.attr("addressSystem") == "google" && $(this).val() != null && $(this).val() != "") {
                    jQuery.data($addressBlock.get(0), addressFieldType + "_find_value", $(this).val());
                    updateGeoPosition($addressBlock, null, null);
                }
            });
        }

        var getComponents = function($addressBlock, addressFieldType){
            var administrativeConditions = "";
            var localityConditions = "";
            var routeConditions = "";
            switch (addressFieldType) {
                case addressFieldTypes.region:
                    break;
                case addressFieldTypes.district:
                    var findRegionValue = jQuery.data($addressBlock.get(0), addressFieldTypes.region + "_find_value");
                    administrativeConditions = "|administrative_area:" + findRegionValue;
                    break;
                case addressFieldTypes.city:
                    var findRegionValue = jQuery.data($addressBlock.get(0), addressFieldTypes.region + "_find_value");
                    var findDistrictValue = jQuery.data($addressBlock.get(0), addressFieldTypes.district + "_find_value");
                    if (findDistrictValue != "") {
                        administrativeConditions = "|administrative_area:" + findRegionValue + " " + findDistrictValue;
                    } else {
                        administrativeConditions = "|administrative_area:" + findRegionValue;
                    }
                    break;
                case addressFieldTypes.street:
                    var findRegionValue = jQuery.data($addressBlock.get(0), addressFieldTypes.region + "_find_value");
                    var findDistrictValue = jQuery.data($addressBlock.get(0), addressFieldTypes.district + "_find_value");
                    var findCityValue = jQuery.data($addressBlock.get(0), addressFieldTypes.city + "_find_value");

                    // Если регион - федеральный город
                    if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                        administrativeConditions = "|administrative_area:" + findRegionValue;
                        if (findDistrictValue != "") {
                            localityConditions = "|locality:" + findDistrictValue;
                        }
                    } else {
                        if (findDistrictValue != "") {
                            administrativeConditions = "|administrative_area:" + findRegionValue + " " + findDistrictValue;
                        } else {
                            administrativeConditions = "|administrative_area:" + findRegionValue;
                        }
                        localityConditions = "|locality:" + findCityValue;
                    }
                    break;
                case addressFieldTypes.building:
                    var findRegionValue = jQuery.data($addressBlock.get(0), addressFieldTypes.region + "_find_value");
                    var findDistrictValue = jQuery.data($addressBlock.get(0), addressFieldTypes.district + "_find_value");
                    var findCityValue = jQuery.data($addressBlock.get(0), addressFieldTypes.city + "_find_value");
                    var findStreetValue = jQuery.data($addressBlock.get(0), addressFieldTypes.street + "_find_value");

                    if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                        administrativeConditions = "|administrative_area:" + findRegionValue;
                        if (findDistrictValue != "") {
                            localityConditions = "|locality:" + findDistrictValue;
                        }
                    } else {
                        if (findDistrictValue != "") {
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
        var getResponseFilter = function($addressBlock, addressFieldType){
            //var result = {type: "", excludeContainsName: ""};
            var type = "";
            var excludeContainsName = null;
            switch (addressFieldType) {
                case addressFieldTypes.region:
                    type = "administrative_area_level_1";
                    break;
                case addressFieldTypes.district:
                    // Если в названии региона есть слово город значит это город федеральный и у него есть
                    // районы с типом sublocality_level_1, sublocality_level_2 или sublocality
                    // иначе это простой район региона с типом administrative_area_level_2

                    var findRegionValue = jQuery.data($addressBlock.get(0), addressFieldTypes.region + "_find_value");
                    if (findRegionValue.toLowerCase().indexOf("город") > -1) {
                        type = "sublocality";
                    } else {
                        type = "administrative_area_level_2";
                        excludeContainsName = "город"; // Исключаем из районов города
                    }
                    break;
                case addressFieldTypes.city:
                    type = "locality";
                    break;
                case addressFieldTypes.street:
                    type = "route";
                    break;
                case addressFieldTypes.building:
                    type = "street_number";
                    break;
            }
            return {type: type, excludeContainsName: excludeContainsName};
        };

        jqAddressFieldNode.typeahead({
            displayField: 'long_name',
            valueField: 'placeData',
            triggerLength: 1,
            delay: 100,
            autoSelect: true,
            matcher: function () { return true; },
            highlight: false,
            updater: function(item) {
                // Выбран элемент из подсказки
                jqAddressFieldNode.val(item.placeData);
                jQuery.data($addressBlock.get(0), addressFieldType + "_find_value", item.long_name);
                // Очищаем поля
                clearFields();
                var placeDataArr = item.placeData.split("||");
                var coordinates = placeDataArr[0].split(",");
                var zipCode = placeDataArr[1];
                updateGeoPosition($addressBlock, coordinates, zipCode);
                initFieldsValues($addressBlock);
                return item;
                /*if (addressFieldType == addressFieldTypes.region) {
                    // Если найден федеральный город, то лочим город
                    if (item.text.toLowerCase().indexOf("город") > -1) {
                        jQuery.data($addressBlock.get(0), addressFieldTypes.city).closest(".row").hide();
                    } else {
                        jQuery.data($addressBlock.get(0), addressFieldTypes.city).closest(".row").show();
                    }
                }*/
            },
            source:  function (query, process) {
                // Локаль выбранной страны
                var locale = jQuery.data($addressBlock.get(0), addressFieldTypes.country).code;
                var data = {
                    address: query,
                    language: 'ru',
                    components: 'country:' + locale + getComponents($addressBlock, addressFieldType)
                };
                return $.ajax({
                    type: "get",
                    dataType: "json",
                    url: "https://maps.googleapis.com/maps/api/geocode/json",
                    data: data,
                    success: function (response) {
                        var responseData = [];
                        var tempMap = {};
                        var responseFilter = getResponseFilter($addressBlock, addressFieldType);
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
                            item.name = item.long_name;
                            responseData.push(item);
                        }
                        return process(responseData);
                    },
                    error: function () {
                        console.log("ajax error");
                        return process(false);
                    }
                });
            }
        });
    }

    function initMap($addressBlock) {
        var $map = $addressBlock.find(".panel-map");
        var map = yandexMaps[$map.attr("id")];
        if (map != null && $map.children().length > 0) {
            updateGeoPosition($addressBlock, null, null);
        } else {
            ymaps.ready(function () {
                var jqGeoPosition = jQuery.data($addressBlock.get(0), addressFieldTypes.geoPosition);
                var position = [55.76, 37.64];
                var	placemark = null;
                if (jqGeoPosition && jqGeoPosition.length > 0) {
                    var geoPositionValue = jqGeoPosition.val();
                    if (geoPositionValue) {
                        position = geoPositionValue.split(",");
                        placemark = new ymaps.Placemark(position, {}, {});
                    }
                }
                $map.empty();
                var map = new ymaps.Map($map.attr("id"), {
                    center: position,
                    zoom: 16,
                    controls: []
                });

                yandexMaps[$map.attr("id")] = map;

                map.controls.add('zoomControl', {
                    position: {
                        right: 10,
                        top: 10
                    }
                });

                if (placemark) {
                    map.geoObjects.add(placemark);
                }

                updateGeoPosition($addressBlock, null, null, true);
            });
        }
    }

    /**
     * Обновление данных на карте
     * @param $addressBlock
     * @param coordinates
     * @param zipCode
     * @param afterInit
     */
    function updateGeoPosition($addressBlock, coordinates, zipCode, afterInit) {
        var countryName = jQuery.data($addressBlock.get(0), addressFieldTypes.country).text;

        var regionVal = jQuery.data($addressBlock.get(0), addressFieldTypes.region).val();
        var districtVal = jQuery.data($addressBlock.get(0), addressFieldTypes.district).val();
        var cityVal = jQuery.data($addressBlock.get(0), addressFieldTypes.city).val();
        var streetVal = jQuery.data($addressBlock.get(0), addressFieldTypes.street).val();
        var buildingVal = jQuery.data($addressBlock.get(0), addressFieldTypes.building).val();
        var jqGeoPosition = jQuery.data($addressBlock.get(0), addressFieldTypes.geoPosition);
        var jqGeoLocation = jQuery.data($addressBlock.get(0), addressFieldTypes.geoLocation);

        var jqZipCode = jQuery.data($addressBlock.get(0), addressFieldTypes.zipCode);

        var roomInput = jQuery.data($addressBlock.get(0), addressFieldTypes.room);
        var roomType = jQuery.data($addressBlock.get(0), addressFieldTypes.roomType);

        if (afterInit != null && afterInit && jqGeoPosition.val() != null && jqGeoPosition.val() != "") {
            coordinates = jqGeoPosition.val().split(",");
        }

        var zoom = null;
        var addressString = "";
        if (buildingVal != null && buildingVal != "") {
            zoom = 16;
            addressString = "дом " + buildingVal;
        }
        if (streetVal != null && streetVal != "") {
            if (zoom == null) {
                zoom = 13;
            }
            addressString = streetVal + ", " + addressString;
        }
        if (cityVal != null && cityVal != "") {
            if (zoom == null) {
                zoom = 10;
            }
            addressString = cityVal + ", " + addressString;
        }
        if (districtVal != null && districtVal != "") {
            if (zoom == null) {
                zoom = 7;
            }
            addressString = districtVal + ", " + addressString;
        }
        if (regionVal != null && regionVal != "") {
            if (zoom == null) {
                zoom = 4;
            }
            addressString = regionVal + ", " + addressString;
        }

        if (zoom == null && afterInit && coordinates != null && coordinates != ""){
            zoom = 16;
        } else if (zoom == null){
            zoom = 4;
        }


        if (zipCode != null && zipCode != "") {
            jqZipCode.val(zipCode);
            jqZipCode.blur();
        }

        zipCode = jqZipCode.val();

        if (zipCode != null && zipCode != "") {
            countryName = countryName + ", " + zipCode;
        }
        if (addressString != "") {
            addressString = countryName + ", " + addressString;
        } else {
            addressString = countryName;
        }

        // Обновяем строку полного адреса
        if (afterInit == null || !afterInit) {
            var roomString = "";
            if (roomInput.val() != null && roomInput.val() != "") {
                switch (roomType) {
                    case RoomTypes.ROOM:
                        roomString = ", квартира " + roomInput.val();
                        break;
                    case RoomTypes.OFFICE:
                        roomString = ", офис " + roomInput.val();
                        break;
                }
            }
            addressString += roomString;
            jqGeoLocation.val(addressString);
            jqGeoLocation.blur();
        }

        var updateMap = function(position, zoom) {
            var $map = $addressBlock.find(".panel-map");
            var map = yandexMaps[$map.attr("id")];
            jqGeoPosition.val(position.join(","));
            jqGeoPosition.blur();
            map.geoObjects.each(function (geoObject) {
                map.geoObjects.remove(geoObject);
            });
            var	placemark = new ymaps.Placemark(position, {}, {});
            map.geoObjects.add(placemark);
            map.setCenter(position, zoom);
        }
        if (typeof ymaps!= "undefined") {
            if (ymaps.geocode) {
                if (coordinates == null) {
                    var geocode = ymaps.geocode(addressString);
                    geocode.then(function (res) {
                        var position = res.geoObjects.get(0).geometry.getCoordinates();
                        updateMap(position, zoom);
                    });
                } else {
                    updateMap(coordinates, zoom);
                }
            }
        }
    }

    $.fn.googleAddress = function(countryData, $zipCode, roomInput, roomType,disableMap){
        var jqRegion = $('input[data-field-type="REGION"]', this);
        var jqDistrict = $('input[data-field-type="DISTRICT"]', this);
        var jqCity = $('input[data-field-type="CITY"]', this);
        var jqStreet = $('input[data-field-type="STREET"]', this);
        var jqBuilding = $('input[data-field-type="BUILDING"]', this);

        // Координаты
        var jqGeoPosition = $('input[data-field-type="GEO_POSITION"]', this);
        // Полное название адреса
        var jqGeoLocation = $('input[data-field-type="GEO_LOCATION"]', this);

        jQuery.data(this.get(0), addressFieldTypes.country, countryData);
        jQuery.data(this.get(0), addressFieldTypes.region, jqRegion);
        jQuery.data(this.get(0), addressFieldTypes.district, jqDistrict);
        jQuery.data(this.get(0), addressFieldTypes.city, jqCity);
        jQuery.data(this.get(0), addressFieldTypes.street, jqStreet);
        jQuery.data(this.get(0), addressFieldTypes.building, jqBuilding);

        jQuery.data(this.get(0), addressFieldTypes.geoPosition, jqGeoPosition);
        jQuery.data(this.get(0), addressFieldTypes.geoLocation, jqGeoLocation);

        jQuery.data(this.get(0), addressFieldTypes.zipCode, $zipCode);

        jQuery.data(this.get(0), addressFieldTypes.room, roomInput);
        jQuery.data(this.get(0), addressFieldTypes.roomType, roomType);

        initAddressFieldTypehead(this, addressFieldTypes.region);
        initAddressFieldTypehead(this, addressFieldTypes.district);
        initAddressFieldTypehead(this, addressFieldTypes.city);
        initAddressFieldTypehead(this, addressFieldTypes.street);
        initAddressFieldTypehead(this, addressFieldTypes.building);
        initRoomInput(this, roomInput);

        $("#" + jqRegion.attr("data-field-name") + "_DESCRIPTION").val("");
        $("#" + jqDistrict.attr("data-field-name") + "_DESCRIPTION").val("");
        $("#" + jqCity.attr("data-field-name") + "_DESCRIPTION").val("");
        $("#" + jqStreet.attr("data-field-name") + "_DESCRIPTION").val("");
        $("#" + jqBuilding.attr("data-field-name") + "_DESCRIPTION").val("");


        jQuery.data(this.get(0), addressFieldTypes.region + "_find_value", jqRegion.val());
        jQuery.data(this.get(0), addressFieldTypes.district + "_find_value", jqDistrict.val());
        jQuery.data(this.get(0), addressFieldTypes.city + "_find_value", jqCity.val());
        jQuery.data(this.get(0), addressFieldTypes.street + "_find_value", jqStreet.val());
        jQuery.data(this.get(0), addressFieldTypes.building + "_find_value", jqBuilding.val());

        initFieldsValues(this);
        if (!disableMap) {
            initMap(this);
        }
        return this;
    };

    $.fn.destroyGoogleAddress = function (){
        var jqRegion = $('input[data-field-type="REGION"]', this);
        var jqDistrict = $('input[data-field-type="DISTRICT"]', this);
        var jqCity = $('input[data-field-type="CITY"]', this);
        var jqStreet = $('input[data-field-type="STREET"]', this);
        var jqBuilding = $('input[data-field-type="BUILDING"]', this);

        jqDistrict.closest(".row").show();
        jqStreet.closest(".row").find("label").text("Улица");
        jqCity.closest(".row").find("label").text("Населенный пункт");
        jqDistrict.closest(".row").find("label").text("Район");
        jqRegion.closest(".row").find("label").text("Регион");

        jqRegion.removeData('typeahead');
        jqDistrict.removeData('typeahead');
        jqCity.removeData('typeahead');
        jqStreet.removeData('typeahead');
        jqBuilding.removeData('typeahead');

        this.find("input[type=text]").each(function(){
            $(this).replaceWith($(this).clone(true));
        });
        //yandexMaps = {};
    }
})(jQuery, window, document);