<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
<script>
    var updateGeoPositionFunctions = {};
    var initedListItem = {};

    // Массив карт на странице
    var yandexMaps = {};

    // Типы помещений
    var RoomTypes = {
        OFFICE : "office",
        ROOM : "room"
    };

    function initCountryField(
            fieldGroupInternalName, fieldInternalName, zipCodeInternalName, onChange,
            countryListItem,
            roomType, roomInputInternalName,
            regionCodeInternalName,
            streetDescriptionShortInternalName,
            districtDescriptionShortInternalName,
            cityDescriptionShortInternalName,
            fieldValues,
            needClearInputs
    ) {
        var jqAddressBlock = $("[data-group-name=" + fieldGroupInternalName + "]");
        if (jqAddressBlock.length == 0) {
            return;
        }
        // инициализация компонента универсальных списков (страна)
        var selectedItems = [];
        var jqCountryValue = $("[data-field-internal-name='" + fieldInternalName + "']");
        var jqCountryName = $("[data-field-internal-name='" + fieldInternalName + "_NAME']");
        var roomInput = jqAddressBlock.find("input[data-field-name=" + roomInputInternalName + "]");
        var jqZipCode = $("[data-field-name=" + zipCodeInternalName + "]");
        var jqRegionCode = $("[data-field-name=" + regionCodeInternalName + "]");
        var jqStreetDescriptionShort = $("[data-field-name=" + streetDescriptionShortInternalName + "]");
        var jqDistrictDescriptionShort = $("[data-field-name=" + districtDescriptionShortInternalName + "]");
        var jqCityDescriptionShort = $("[data-field-name=" + cityDescriptionShortInternalName + "]");

        var jqBuildingDescription = jqAddressBlock.find("input[data-field-name=" +(jqAddressBlock.find("[data-field-type=BUILDING]").attr("data-field-name")) + "_DESCRIPTION]");
        var jqOfficeDescription = jqAddressBlock.find("input[data-field-name=" + roomInputInternalName + "_DESCRIPTION]");


        if (fieldValues != null) {
            if (fieldValues.countryId != null) {
                jqCountryValue.val(fieldValues.countryId);
            }
            if (fieldValues.postalCode != null) {
                jqZipCode.val(fieldValues.postalCode);
            }
            if (fieldValues.region != null) {
                jqAddressBlock.find("[data-field-type=REGION]").val(fieldValues.region);
            }
            if (fieldValues.district != null) {
                jqAddressBlock.find("[data-field-type=DISTRICT]").val(fieldValues.district);
            }
            if (fieldValues.city != null) {
                jqAddressBlock.find("[data-field-type=CITY]").val(fieldValues.city);
            }
            if (fieldValues.street != null) {
                jqAddressBlock.find("[data-field-type=STREET]").val(fieldValues.street);
            }
            if (fieldValues.building != null) {
                jqAddressBlock.find("[data-field-type=BUILDING]").val(fieldValues.building);
            }
            if (fieldValues.room != null) {
                roomInput.val(fieldValues.room);
            }
            if (fieldValues.geoLocation != null) {
                jqAddressBlock.find("[data-field-type=GEO_LOCATION]").val(fieldValues.geoLocation);
            }
            if (fieldValues.geoPosition != null) {
                jqAddressBlock.find("[data-field-type=GEO_POSITION]").val(fieldValues.geoPosition);
            }
            if (fieldValues.regionCode != null) {
                jqRegionCode.val(fieldValues.regionCode);
            }
            if (fieldValues.streetDescriptionShort != null) {
                jqStreetDescriptionShort.val(fieldValues.streetDescriptionShort);
            }
            if (fieldValues.districtDescriptionShort != null) {
                jqDistrictDescriptionShort.val(fieldValues.districtDescriptionShort);
            }
            if (fieldValues.cityDescriptionShort != null) {
                jqCityDescriptionShort.val(fieldValues.cityDescriptionShort);
            }

            if (fieldValues.buildingDescription != null) {
                jqBuildingDescription.val(fieldValues.buildingDescription);
            }
            if (fieldValues.officeDescription != null) {
                jqOfficeDescription.val(fieldValues.officeDescription);
            }
        }


        selectedItems.push(jqCountryValue.val());
        var jqCountryNode = $("#" + fieldInternalName);
        if (jqCountryNode.length > 0) {
            jqCountryNode.html("");
            RameraListEditorModule.init(
                jqCountryNode,
                {
                    labelClasses: ["checkbox-inline"],
                    labelStyle: "margin-left: 10px;",
                    selectedItems: selectedItems,
                    selectClasses: ["form-control"],
                },
                function (event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        jqCountryValue.val(data.value);
                        jqCountryName.val(data.text);

                        if (onChange != null) {
                            data.domNode = jqCountryNode;
                            onChange(data);
                        }

                        initedListItem[fieldGroupInternalName] = true;
                        var code = data.code.split("_")[0];
                        var addressSystem = "";
                        if (code == "ru") { // Для России кладр, для остального гугл
                            addressSystem = "kladr";
                        } else {
                            addressSystem = "google";
                        }
                        jqAddressBlock.attr("addressSystem", addressSystem);

                        // Если адресные поля уже были инциализированы, то после смены страны очищаем все поля
                        if (jQuery.data(jqAddressBlock.get(0), "inited") == null) {
                            jQuery.data(jqAddressBlock.get(0), "inited", true);
                        } else {
                            if (needClearInputs == null || needClearInputs == true) {
                                jqAddressBlock.find("input[type=text]").val("");
                            }
                            jqAddressBlock.find("input[type=text]").blur();
                            $.destroyRadomKladr(jqAddressBlock);
                            jqAddressBlock.destroyGoogleAddress();
                        }
                        roomInput = jqAddressBlock.find("input[data-field-name=" + roomInputInternalName + "]");
                        jqZipCode = $("[data-field-name=" + zipCodeInternalName + "]");
                        if (code == "ru"){ // Для России кладр, для остального гугл
                            $.radomKladr(
                                    jqAddressBlock, jqZipCode, roomType, roomInput,
                                    jqRegionCode, jqStreetDescriptionShort, jqDistrictDescriptionShort,
                                    jqCityDescriptionShort
                            );
                        } else {
                            jqAddressBlock.googleAddress(data, jqZipCode, roomInput, roomType);
                        }
                    }
                }
            );
        } else if (countryListItem != null) {
            roomInput = jqAddressBlock.find("input[data-field-name=" + roomInputInternalName + "]");
            jqZipCode = $("[data-field-name=" + zipCodeInternalName + "]");
            var code = countryListItem.code.split("_")[0];
            if (code == "ru"){ // Для России кладр, для остального гугл
                $.radomKladr(
                        jqAddressBlock, jqZipCode, roomType, roomInput,
                        jqRegionCode, jqStreetDescriptionShort, jqDistrictDescriptionShort,
                        jqCityDescriptionShort
                );
            } else {
                jqAddressBlock.googleAddress(countryListItem, jqZipCode, roomInput, roomType);
            }
        }
        return {
            getJqCountryIdInput : function() {return jqCountryValue},
            getJqCountryInput : function() {return jqCountryName},
            getJqPostalCodeInput : function() {return jqZipCode},
            getJqRegionInput : function() {return jqAddressBlock.find("[data-field-type=REGION]")},
            getJqDistrictInput : function() {return jqAddressBlock.find("[data-field-type=DISTRICT]")},
            getJqCityInput : function() {return jqAddressBlock.find("[data-field-type=CITY]")},
            getJqStreetInput : function() {return jqAddressBlock.find("[data-field-type=STREET]")},
            getJqBuildingInput : function() {return jqAddressBlock.find("[data-field-type=BUILDING]")},
            getJqOfficeInput : function() {return roomInput},
            getJqGeoLocation :  function() {return jqAddressBlock.find("[data-field-type=GEO_LOCATION]")},
            getJqGeoPosition :  function() {return jqAddressBlock.find("[data-field-type=GEO_POSITION]")},
            getJqRegionCodeInput : function() {return jqRegionCode},
            getJqStreetDescriptionShortInput : function() {return jqStreetDescriptionShort},
            getJqDistrictDescriptionShortInput : function() {return jqDistrictDescriptionShort},
            getJqCityDescriptionShortInput : function() {return jqCityDescriptionShort},

            getJqBuildingDescriptionInput : function() {return jqAddressBlock.find("input[data-field-name=" +(jqAddressBlock.find("[data-field-type=BUILDING]").attr("data-field-name")) + "_DESCRIPTION]")},
            getJqOfficeDescriptionInput : function() {return jqAddressBlock.find("input[data-field-name=" + roomInputInternalName + "_DESCRIPTION]")}
        };
    }

    function initCitizenshipCountryField(fieldGroupInternalName, fieldInternalName, callback) {
        // инициализация компонента универсальных списков (страна)
        var selectedItems = [];
        selectedItems.push($("[data-field-internal-name='" + fieldInternalName + "']").val());
        RameraListEditorModule.init(
                $("#" + fieldInternalName),
                {
                    labelClasses : ["checkbox-inline"],
                    labelStyle : "margin-left: 10px;",
                    selectedItems: selectedItems,
                    selectClasses: ["form-control"],
                    disableEmptyValue: true
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        $("[data-field-internal-name='" + fieldInternalName + "']").val(data.value);
                        $("[data-field-internal-name='" + fieldInternalName + "_NAME']").val(data.text);
                    }
                    if(callback) {
                        callback(event, data);
                    }
                }
        );
    }
</script>