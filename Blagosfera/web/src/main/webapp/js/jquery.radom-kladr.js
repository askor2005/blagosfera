(function ($) {
	
    $.extend({
		destroyRadomKladr : function(jqAddressBlock){
			jqAddressBlock.find("input[type=text]").removeAttr("data-kladr-type");
			jqAddressBlock.find("input[type=text]").removeAttr("data-kladr-id");
			jqAddressBlock.find("input[type=text]").removeAttr("autocomplete");
			jqAddressBlock.find("input[type=text]").each(function(){
				$(this).replaceWith($(this).clone(true));
			});
			var $region = $('input[data-field-type="REGION"]', jqAddressBlock);
			var $district = $('input[data-field-type="DISTRICT"]', jqAddressBlock);
			var $city = $('input[data-field-type="CITY"]', jqAddressBlock);
			var $street = $('input[data-field-type="STREET"]', jqAddressBlock);
			$district.closest(".row").show();

			$street.closest(".row").find("label").text("Улица");
			$city.closest(".row").find("label").text("Населенный пункт");
			$district.closest(".row").find("label").text("Район");
			$region.closest(".row").find("label").text("Регион");
			//yandexMaps = {};
		},

    	radomKladr : function(
			$container, $zipInput,
			roomType, $commonRoomInput,
			$regionCode, $streetDescriptionShort,
			$districtDescriptionShort,
			$cityDescriptionShort) {
			var $region = $('input[data-field-type="REGION"]', $container);
			var $regionDescription = $("#" + $region.attr("data-field-name") + "_DESCRIPTION");
			var $district = $('input[data-field-type="DISTRICT"]', $container);
			var $districtDescription = $("#" + $district.attr("data-field-name") + "_DESCRIPTION");
			var $city = $('input[data-field-type="CITY"]', $container);
			var $cityDescription = $("#" + $city.attr("data-field-name") + "_DESCRIPTION");
			var $street = $('input[data-field-type="STREET"]', $container);
			var $streetDescription = $("#" + $street.attr("data-field-name") + "_DESCRIPTION");
			var $building = $('input[data-field-type="BUILDING"]', $container);
			var $buildingDescription = $("#" + $building.attr("data-field-name") + "_DESCRIPTION");

			// Координаты
			var $geoPosition = $('input[data-field-type="GEO_POSITION"]', $container);
			// Полное название адреса
			var $geoLocation = $('input[data-field-type="GEO_LOCATION"]', $container);

			var $map = $container.find(".panel-map");

			// Ищем инпут с квартирой
			/*var roomInput = null;
			var $roomDescription = null;
			if($container.attr("data-group-name") === "PERSON_REGISTRATION_ADDRESS") {
				roomInput = $container.find("input[data-field-internal-name=ROOM]");
			} else if($container.attr("data-group-name") === "PERSON_ACTUAL_ADDRESS") {
				roomInput = $container.find("input[data-field-internal-name=FROOM]");
			}
			if (roomInput != null && roomInput.length > 0) {
				$roomDescription = $("#" + roomInput.attr("data-field-name") + "_DESCRIPTION");
			}

			// Ищем инпут с офисом
			var officeInput = null;
			var $officeDescription = null;
			if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS") {
				officeInput = $container.find("input[data-field-name=COMMUNITY_LEGAL_OFFICE]");
			} else if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS") {
				officeInput = $container.find("input[data-field-name=COMMUNITY_LEGAL_F_OFFICE]");
			} else if($container.attr("data-group-name") === "REGISTRATOR_OFFICE_ADDRESS") {
				officeInput = $container.find("input[data-field-name=REGISTRATOR_OFFICE_ROOM]");
			}
			if (officeInput != null && officeInput.length > 0) {
				$officeDescription = $("#" + officeInput.attr("data-field-name") + "_DESCRIPTION");
			}

			var $commonRoomInput = null;
			var $commonRoomDescription = null;
			if (roomInput != null && officeInput == null) {
				$commonRoomInput = roomInput;
				$commonRoomDescription = $roomDescription;
			} else if (officeInput != null && roomInput == null) {
				$commonRoomInput = officeInput;
				$commonRoomDescription = $officeDescription;
			}*/
			var $commonRoomDescription = null;
			var officeInput = null;
			var $officeDescription = null;
			var roomInput = null;
			var $roomDescription = null;
			if (roomType == RoomTypes.OFFICE) {
				officeInput = $commonRoomInput;
				$officeDescription = $("#" + officeInput.attr("data-field-name") + "_DESCRIPTION");
				$commonRoomDescription = $officeDescription;
			} else {
				roomInput = $commonRoomInput;
				$roomDescription = $("#" + roomInput.attr("data-field-name") + "_DESCRIPTION");
				$commonRoomDescription = $roomDescription;
			}

			if (roomInput != null && roomInput.length > 0) {
				roomInput.on('input',function(e){
					onUpdateRoomInput(roomInput, $roomDescription);
					updateGeoLocation();
				});
			}
			if (officeInput != null && officeInput.length > 0) {
				officeInput.on('input',function(e){
					onUpdateOfficeInput(officeInput, $officeDescription);
					updateGeoLocation();
				});
			}

			function onUpdateRoomInput(roomInput, roomInputDescription) {
				if (roomInput != null && roomInput != "") {
					if (roomInputDescription != null && roomInputDescription.length > 0) {
						roomInputDescription.val("квартира");
					}
				} else {
					if (roomInputDescription != null && roomInputDescription.length > 0) {
						roomInputDescription.val("");
					}
				}
			}
			function onUpdateOfficeInput(officeInput, officeInputDescription) {
				if (officeInput != null && officeInput != "") {
					if (officeInputDescription != null && officeInputDescription.length > 0) {
						officeInputDescription.val("офис");
					}
				} else {
					if (officeInputDescription != null && officeInputDescription.length > 0) {
						officeInputDescription.val("");
					}
				}
			}



    		function updateGeoPosition() {
    			var map = yandexMaps[$map.attr("id")];//$container.data("map");

    			var address = $.kladr.getAddress($container, function (objs) {console.log(objs);
    				var result = '';
    				var name = '';
    				var type = '';
    				var zoom = 4;
    				for (var i in objs) {
    					if (objs.hasOwnProperty(i)) {
    						if ($.type(objs[i]) === 'object') {
    							name = objs[i].name;
    							type = ' ' + objs[i].type;
    						}
    						else {
    							name = objs[i];
    							type = '';
    						}
    						if (result) result += ', ';
    						result += type + name;
    					}
    					
						if (objs.building) {
							zoom = 16;
						} else if (objs.street) {
							zoom = 13;
						} else if (objs.city) {
							zoom = 10;
						} else if (objs.district) {
							zoom = 7;
						} else {
							zoom = 4;
						}
    					
    				}
    				return {
    					value : result == "" ? "Россия" : result,
    					zoom : zoom
    				};
    			});
    			if (ymaps.geocode) {
    				var geocode = ymaps.geocode(address.value);
					geocode.then(function (res) {
						var position = res.geoObjects.get(0).geometry.getCoordinates();
						//console.log(position);
						$geoPosition.val(position);
						$geoPosition.blur();
						map.geoObjects.each(function (geoObject) {
							map.geoObjects.remove(geoObject);
						});
						var	placemark = new ymaps.Placemark(position, {}, {});
						map.geoObjects.add(placemark);
						map.setCenter(position, address.zoom);
					});
    			}
    		}
    		
    		function updateGeoLocation() {
				var countryName = $container.find("input.country-control").val();
				var subHouseName;
				subHouseName = $container.find("input[data-field-type=SUBHOUSE]").val();

				var roomName;
				if (roomInput != null && roomInput.length > 0) {
					roomName = roomInput.val();
				}

				/*if($container.attr("data-group-name") === "PERSON_REGISTRATION_ADDRESS") {
					roomInput = $container.find("input[data-field-internal-name=ROOM]");
					roomName = roomInput.val();
				} else if($container.attr("data-group-name") === "PERSON_ACTUAL_ADDRESS") {
					roomInput = $container.find("input[data-field-internal-name=FROOM]");
					roomName = roomInput.val();
				} else if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS") {
					// roomName = $container.find("input[data-field-type=BUILDING]").val();
				}else if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS") {
					// roomName = $container.find("input[data-field-type=BUILDING]").val();
				}else {
					//
				}*/

				var officeName;
				if (officeInput != null && officeInput.length > 0) {
					officeName = officeInput.val();
				}

				/*
				if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_ADDRESS") {
					officeInput = $container.find("input[data-field-name=COMMUNITY_LEGAL_OFFICE]");
					officeName = officeInput.val();
				} else if($container.attr("data-group-name") === "COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS") {
					officeInput = $container.find("input[data-field-name=COMMUNITY_LEGAL_F_OFFICE]");
					officeName = officeInput.val();
				} else {
					//
				}*/
    			if ($geoLocation && $geoLocation.length > 0) {
					var address = $.kladr.getAddress($container, null, $zipInput);
					if (countryName != null && countryName != "") {
						address = countryName + ", " + address;
					}
					if(subHouseName && subHouseName.trim()) {
						address = address + ", корпус " + subHouseName;
					}
					if(roomName && roomName.trim()) {
						address = address + ", квартира " + roomName;
					}
					if(officeName && officeName.trim()) {
						address = address + ", офис " + officeName;
					}
					$geoLocation.val(address);
					$geoLocation.blur();
					$geoLocation.attr("value",address);
					$geoLocation.attr("data-field-value",address);
    			}

				$.kladr.getAddress($container, function (objs) {
					var isRegionFound = false;
					var isDistrictFound = false;
					var isCityFound = false;
					var isStreetFound = false;
					var isBuildingFound = false;
					if (objs != null) {
						isRegionFound = objs["region"] != null;
						isDistrictFound = objs["district"] != null;
						isCityFound = objs["city"] != null;
						isStreetFound = objs["street"] != null;
						isBuildingFound = objs["building"] != null;
					}

					if (!isRegionFound) {
						if ($regionDescription != null && $regionDescription.length > 0) {
							$regionDescription.val("");
						}
						if ($regionCode != null && $regionCode.length > 0) {
							$regionCode.val("");
						}
					}
					if (!isDistrictFound) {
						if ($districtDescription != null && $districtDescription.length > 0) {
							$districtDescription.val("");
						}
						if ($districtDescriptionShort != null && $districtDescriptionShort.length > 0) {
							$districtDescriptionShort.val("");
						}
					}
					if (!isCityFound) {
						if ($cityDescription != null && $cityDescription.length > 0) {
							$cityDescription.val("");
						}
						if ($cityDescriptionShort != null && $cityDescriptionShort.length > 0) {
							$cityDescriptionShort.val("");
						}
					}
					if (!isStreetFound) {
						if ($streetDescription != null && $streetDescription.length > 0) {
							$streetDescription.val("");
						}
						if ($streetDescriptionShort != null && $streetDescriptionShort.length > 0) {
							$streetDescriptionShort.val("");
						}
					}
					if (!isBuildingFound && $buildingDescription != null && $buildingDescription.length > 0) {
						$buildingDescription.val("");
					}
					
					for (var i in objs) {
						var obj = objs[i];
						if ($.type(obj) === 'object') {
							var type = obj.type;
							switch (obj.contentType) {
								case "building":
									if ($buildingDescription != null && $buildingDescription.length > 0) {
										$buildingDescription.val(type);
									}
									break;
								case "street":
									if ($streetDescription != null && $streetDescription.length > 0) {
										$streetDescription.val(type);
									}
									if ($streetDescriptionShort != null && $streetDescriptionShort.length > 0) {
										$streetDescriptionShort.val(obj.typeShort);
									}
									break;
								case "city":
									if ($cityDescription != null && $cityDescription.length > 0) {
										$cityDescription.val(type);
									}
									if ($cityDescriptionShort != null && $cityDescriptionShort.length > 0) {
										$cityDescriptionShort.val(obj.typeShort);
									}
									break;
								case "district":
									if ($districtDescription != null && $districtDescription.length > 0) {
										$districtDescription.val(type);
									}
									if ($districtDescriptionShort != null && $districtDescriptionShort.length > 0) {
										$districtDescriptionShort.val(obj.typeShort);
									}
									break;
								case "region":
									if ($regionDescription != null && $regionDescription.length > 0) {
										$regionDescription.val(type);
									}
									if (obj.id && $regionCode != null && $regionCode.length > 0) {
										// Меняем нули в коде региона на пустую строку
										var regionCode = obj.id.replace(/[0]/g, "");
										$regionCode.val(regionCode);
									}
									break;
							}
						}
					}
					onUpdateRoomInput(roomInput, $roomDescription); // Если есть квартира
					onUpdateOfficeInput(officeInput, $officeDescription); // Если есть офис
				});
    		}

			// init - булевая - нужно ли инициализировать kladr сразу.
			// сделал это так для того, чтобы механика отключения kladr не для России
			// работала при редактировании профиля(sharer.jsp - теперь я там передаю дополнительный параметр)
			// и не поломала другие части программы где используется kladr.
			// Поля kladr в профиле теперь инифиализируются в как реакция на изменение значения.
			//if(typeof init === "undefined" || !init) {
				$region.kladr('type', $.kladr.type.region);
				$district.kladr('type', $.kladr.type.district);
				$city.kladr('type', $.kladr.type.city);
				$street.kladr('type', $.kladr.type.street);
				$building.kladr('type', $.kladr.type.building);
			//}
    		
    		$()
			.add($region)
			.add($district)
			.add($city)
			.add($street)
			.add($building)
			.kladr({
				token : '543f7e2a7c523985378b456e',
				parentInput: $container,
				verify: true,
				select: function (obj) {
					if (obj != null && obj.type != null) {
						switch (obj.contentType) {
							case "building":
								break;
							case "street":
								$street.closest(".row").find("label").text(obj.type);
								break;
							case "city":
								$city.closest(".row").find("label").text(obj.type);
								break;
							case "district":
								$district.closest(".row").find("label").text(obj.type);
								break;
							case "region":
								$region.closest(".row").find("label").text(obj.type);
								break;
						}
					}
					updateGeoLocation();
					updateGeoPosition();
				},
				check: function (obj) {
					updateGeoLocation();
					updateGeoPosition();
				}
			});

    		var values = {};
    		
    		values[$.kladr.type.region] = $region.val();
    		values[$.kladr.type.district] = $district.val();
    		values[$.kladr.type.city] = $city.val();
    		values[$.kladr.type.street] = $street.val();
    		values[$.kladr.type.building] = $building.val();
    		
    		$.kladr.setValues(values, $container);

			// Создаём слушателей событий
			$region.bind('kladr_change', function(event, data){
				if (data != null && data.type != null) {
					$region.closest(".row").find("label").text(data.type);
					if (data.type.toLowerCase() == "город") {
						$city.removeAttr("data-kladr-id");
						$city.closest(".row").hide();
					} else {
						$city.closest(".row").show();
					}
				}
			});
			$district.bind('kladr_change', function(event, data){
				if (data != null && data.type != null) {
					$district.closest(".row").find("label").text(data.type);
				}
			});
			$city.bind('kladr_change', function(event, data){
				if (data != null && data.name != null) {
					$city.closest(".row").find("label").text(data.type);
					if ($district.val() == null || $district.val() == "") {
						$district.closest(".row").hide();
					} else {
						$district.closest(".row").show();
					}
				} else {
					$district.closest(".row").show();
				}
			});
			$street.bind('kladr_change', function(event, data){
				if (data != null && data.name != null) {
					$street.closest(".row").find("label").text(data.type);
				}
			});

			$region.on('input', function(event){
				$city.closest(".row").show();
				$district.closest(".row").show();

				$district.val("");
				$district.blur();
				$city.val("");
				$city.blur();
				$street.val("");
				$street.blur();
				$building.val("");
				$building.blur();

				$districtDescription.val("");
				$cityDescription.val("");
				$streetDescription.val("");
				$buildingDescription.val("");

				$commonRoomInput.val("");
				$commonRoomInput.blur();
				$commonRoomDescription.val("");

				$zipInput.val("");
				$zipInput.blur();

				$street.closest(".row").find("label").text("Улица");
				$city.closest(".row").find("label").text("Населенный пункт");
				$district.closest(".row").find("label").text("Район");
				$region.closest(".row").find("label").text("Регион");
			});
			$district.on('input', function(event){
				$city.val("");
				$city.blur();
				$street.val("");
				$street.blur();
				$building.val("");
				$building.blur();

				$cityDescription.val("");
				$streetDescription.val("");
				$buildingDescription.val("");

				$commonRoomInput.val("");
				$commonRoomInput.blur();
				$commonRoomDescription.val("");

				$zipInput.val("");
				$zipInput.blur();

				$street.closest(".row").find("label").text("Улица");
				$city.closest(".row").find("label").text("Населенный пункт");
				$district.closest(".row").find("label").text("Район");
			});
			$city.on('input', function(event){
				$district.closest(".row").show();

				$street.val("");
				$street.blur();
				$building.val("");
				$building.blur();

				$streetDescription.val("");
				$buildingDescription.val("");

				$commonRoomInput.val("");
				$commonRoomInput.blur();
				$commonRoomDescription.val("");

				$zipInput.val("");
				$zipInput.blur();

				$street.closest(".row").find("label").text("Улица");
				$city.closest(".row").find("label").text("Населенный пункт");
			});
			$street.on('input', function(event){
				$building.val("");
				$building.blur();

				$buildingDescription.val("");

				$commonRoomInput.val("");
				$commonRoomInput.blur();
				$commonRoomDescription.val("");

				$zipInput.val("");
				$zipInput.blur();

				$street.closest(".row").find("label").text("Улица");
			});
			$building.on('input', function(event) {
				$commonRoomInput.val("");
				$commonRoomInput.blur();
				$commonRoomDescription.val("");

				$zipInput.val("");
				$zipInput.blur();
			});

    		
			//updateGeoLocation();
			//updateGeoPosition();
    		
    		if ($map && $map.length > 0) {
				var map = yandexMaps[$map.attr("id")];
				if (map != null && $map.children().length > 0) {
					updateGeoPosition();
				} else {
					ymaps.ready(function () {

						var position = [55.76, 37.64];
						var placemark = null;
						if ($geoPosition && $geoPosition.length > 0) {
							var geoPositionValue = $geoPosition.val();
							if (geoPositionValue) {
								position = geoPositionValue.split(",");
								placemark = new ymaps.Placemark(position, {}, {});
							}
						}
						var map = new ymaps.Map($map.attr("id"), {
							center: position,
							zoom: 16,
							controls: []
						});

						yandexMaps[$map.attr("id")] = map;
						//$container.data("map", map);

						map.controls.add('zoomControl', {
							position: {
								right: 10,
								top: 10
							}
						});

						if (placemark) {
							map.geoObjects.add(placemark);
						}

						//updateGeoLocation();
						updateGeoPosition();

					});
				}
    		} else {
    			//updateGeoLocation();
    			updateGeoPosition();
    		}
			var self = this;
			return function(){updateGeoLocation.call(self);updateGeoPosition.call(self);};
    	}
    });
})(jQuery);