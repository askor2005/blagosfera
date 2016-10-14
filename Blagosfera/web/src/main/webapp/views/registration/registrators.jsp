<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<t:insertAttribute name="item" />
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
<script id="request-item-template" type="x-tmpl-mustache">
<div class="request-item" style="white-space: nowrap;">
    <a href='#' class="btn btn-primary do-cancel-request" style="margin-right: 10px;">Отменить заявку</a>
    <span class="my-request-item-date">{{request.createdDate}}</span> в
    <span class="my-request-item-date">{{request.createdTime}} </span>
        Вы подали заявку на идентификацию Регистратору
        <a href="{{request.registrator.link}}">
            {{request.registrator.fullNameDat}}
            <img class="img-thumbnail" src="{{resizedAvatar}}" style="height:40px;width:40px;" />
        </a>
    </span>
</div>
</script>

<script type="text/javascript">
    var requestItemTemplate = $('#request-item-template').html();
    Mustache.parse(requestItemTemplate);
    var registratorsGrid;
    var registratorsStore;

    function initScrollListener() {
		//var $list = $("div#registrators-list").empty();
		/*ScrollListener.init("/registrator/list.json", "post", getParams, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
			var $list = $("div#registrators-list");
			$.each(response, function(index, registrator) {
				$list.append(getRegistratorMarkup(registrator));
				$list.append("<hr style='margin: 4px 0;' />");
			});
			if ($("div.row.registrator-item").length == 0) {
				$("div#registrators-not-found").show();
			} else {
				$("div#registrators-not-found").hide();
			}
			$(".list-loader-animation").fadeOut();
		}, null, null, "registrators-list-panel");*/


	}

	function getParams() {
		var params = {};
		params.nameTemplate = $("#search-input").val();
        params.level = $("select#level-select").val();
        params.latitude = window.position ? window.position[0] : 0;
        params.longitude = window.position ? window.position[1] : 0;
        params.includeRequestedForRegistration = false;
		return params;
	}
	
	$(document).ready(function() {
        $.radomJsonGet("/registrator/select/init/info.json", {}, function(response){
            var profileFillingPercent = parseInt(response.profileFillingPercent);
            var registratorsCount = response.registratorsCount;
            var actualAddress = response.actualAddress;

            var registrationAddress = response.registrationAddress;
            var template = $('#registrators-template').html();
            Mustache.parse(template);
            var rendered = Mustache.render(template, {registratorsCount : registratorsCount, actualAddress :  actualAddress,
                registrationAddress : registrationAddress});
            $("#registrators-target").html(rendered);
            if(!$.cookie("INFO.REGISTRATORS")){
                $("#info-registrators").show();
            }

            var sharerAddress = response.sharerAddress;
            window.registrators = {};
            window.position = [!(sharerAddress.latitude) ? '55.45' : sharerAddress.latitude, !(sharerAddress.longitude) ? '37.36' : sharerAddress.longitude];

            updateRequestItem(response.currentRequest);

            $(radomEventsManager).bind("registrationRequest.createRequest", function(event) {
                $.radomJsonPost("/registrator/myRequest.json", {
                }, function(data){
                    if(data){
                        updateRequestItem(data);
                    }
                });
            });
            initScrollListener();

            initSearchInput($("#search-input"), function() {
                //initScrollListener();
                registratorsStore.reload({params : getParams()});
            });
            //initScrollListener();
            $("select#level-select").change(function(){
                registratorsStore.reload({params : getParams()});
                //initScrollListener();
            });

            $("a.do-clear-filter").click(function() {
                $("#search-input").val("");
                $("select#level-select").val("ALL");
                registratorsStore.reload({params : getParams()});
                //initScrollListener();
                return false;
            });
            $("a.do-select-address").click(function() {
                $("#address-input").val($(this).text());
                searchAddressByName();
                return true;
            });

            $("a.do-goto-map").click(function() {
                searchAddressByName();
                return false;
            });
            $('#address-input').keypress(function (e) {
                if (e.which == 13) {
                    searchAddressByName();
                }
            });

            $("a.order-link, a.do-clear-filter, a.do-goto-map").radomTooltip({
                placement: "top",
                container: "body"
            });

            ymaps.ready(function () {

                window.meOnMap = new ymaps.Placemark(position,
                        {
                            balloonContent: sharerAddress.fullAddress,
                            iconContent: 'Я'
                        },
                        {
                            balloonMaxWidth: 300,
                            preset: "twirl#blueStretchyIcon",
                            iconColor: '#ff0000'
                        }
                );

                var map = new ymaps.Map("registrators-map", {
                    center: window.position,
                    zoom: 11,
                    controls: []
                });

                window.map = map;
                map.controls.add('zoomControl', {
                    position: {
                        right: 10,
                        top: 10
                    }
                });

                map.geoObjects.add(window.meOnMap);
                searchAddressByName();
                loadRegistratorsForMap(profileFillingPercent);

            });
            if(sharerAddress && sharerAddress.fullAddress){
                $("#address-input").val(sharerAddress.fullAddress);
            }
            $("#includeRequestedForRegistration").change(function(){
                loadRegistratorsForMap(profileFillingPercent);
                if ($("#includeRequestedForRegistration").prop('checked') === true) {
                    $("#includeRequestedForRegistrationModeBlock").show();
                }
                else {
                    $("#includeRequestedForRegistrationModeBlock").hide();
                }
            });
            $("#includeRequestedForRegistrationMode").change(function() {
                loadRegistratorsForMap(profileFillingPercent);
            });
            if (!response.registrator) {
                $('#includeRequestedForRegistrationBlock').hide();
            }
            Ext.onReady(function () {
                registratorsStore = Ext.create('Ext.data.Store', {
                    fields: ['user','level','distance','timetable','registratorOfficePhone','skype','registratorMobilePhone','requested','requestedToMe',{name: 'searchString', mapping: 'user.searchString'}],
                    id: 'registratorsStore',
                    autoLoad: {start: 0, limit: 20},
                    remoteSort: true,
                    pageSize: 20,
                    proxy: {
                        type: 'ajax',
                        url: "/registrator/list.json",
                        actionMethods: {
                            read: 'POST'
                        },
                        reader: {
                            type: 'json',
                        },
                        extraParams: getParams(),
                    },
                    listeners: {
                        load: function (component, dataList) {
                            $("a.go-to-chat, a.do-map-marker, a.do-send-request").radomTooltip({
                                placement : "top",
                                container : "body"
                            });
                            if (dataList == null || dataList.length == 0) {
                                $("div#registrators-not-found").show();
                            }
                            else {
                                $("div#registrators-not-found").hide();
                            }
                        }
                    }
                });
                registratorsStore.sort("searchString", "ASC"),
                        registratorsGrid = Ext.create('Ext.grid.Panel', {
                            id: 'registratorsGrid',
                            title: '',
                            store: registratorsStore,
                            columns: [{
                                text: '',
                                sortable: false,
                                dataIndex: '',
                                width: "16%",
                                renderer: function (value, myDontKnow, record) {
                                    return getRegistratorAvatarMarkup(record.data);
                                }
                            },{
                                text: 'ФИО',
                                dataIndex: 'searchString',
                                width: "55%",
                                sortable: true,
                                renderer: function (value, myDontKnow, record) {
                                    return getRegistratorInfoMarkup(record.data);
                                }
                            },{
                                sortable: false,
                                text: 'Расстояние',
                                dataIndex: 'distance',
                                width: "28%",
                                renderer: function (value, myDontKnow, record) {
                                    return getRegistratorControlsMarkup(record.data);
                                }
                            }],
                            dockedItems: [],

                            viewConfig: {},
                            listeners: {
                                cellclick: function(view, td, cellIndex, record, tr, rowIndex, event, eOpts) {
                                    if ($(event.target).is("a.do-map-marker") ) {
                                        selectRegistratorOnMap(record.data);
                                    }
                                    else if ($(event.target).is("a.do-send-request") ) {
                                        if (profileFillingPercent == 100) {
                                            CreateCertificationRequestDialog.show(record.data);
                                        }
                                        else {
                                            bootbox.alert("Ваш профиль не заполнен на 100%! Вы не можете подать заявку регистратору пока не заполните профиль.");
                                        }
                                    }
                                    else if ($(event.target).is("a.go-to-chat") ) {
                                        ChatView.showDialogWithSharer(record.data.user.id);
                                    }
                                    else {
                                        $("tr").removeClass("selected-registrator");
                                        selectRegistratorOnMap(record.data);
                                        $(tr).addClass("selected-registrator");
                                    }
                                },
                                expand: function() {
                                    console.log('saved-search/expand');
                                }
                            },
                            frame: true,
                            renderTo: 'registrators-list-panel'
                        });
            });
        });

	});
    function loadRegistratorsForMap(profileFillingPercent){
        $.radomJsonPost("/registrator/all.json", {
            includeRequestedForRegistration : $("#includeRequestedForRegistration").prop('checked'),
            requestedForRegistrationsOnlyToMe: $("#includeRequestedForRegistrationMode").val() === "true" ? true : false
        }, function(response){
            window.map.geoObjects.removeAll();
            window.geoObjects = {};
            // Создаем коллекцию геообъектов.
            if(window.meOnMap) window.map.geoObjects.add(window.meOnMap);
            var collection = new ymaps.GeoObjectCollection();
            $.each(response, function(index, registrator) {
                var sharer = registrator.user;
                var address = registrator.user.officeAddress;
                var marker = getRegistratorIcon(registrator.level,registrator.requestedToMe);
                if(address.latitude && address.longitude) {
                    window.geoObjects[sharer.id] =
                            new ymaps.Placemark(
                                    [address.latitude, address.longitude],
                                    {
                                        id: sharer.id,
                                        balloonContent: getRegistratorMapMarkup(registrator,profileFillingPercent).html(),
                                        iconContent: marker.text
                                    },
                                    {
                                        balloonMaxWidth: 300,
                                        preset: marker.icon,
                                        iconColor: marker.color
                                    }
                            );
                    collection.add(window.geoObjects[sharer.id]);
                }
            });
            window.map.geoObjects.add(collection);
        });
    }

    function getRegistratorIcon(level,requestedToMe){
        var result = {
            icon: 'twirl#greyIcon',
            result: '#3caa3c',
            text: '?'
        };
        if (requestedToMe == true) {
            result.color = 'green';
            result.text = 'И';
        }
        else  if(level) {
            switch(level.mnemo) {
                case 'registrator.level0':
                    result.color = '#0000ff';
                    result.text = 'В';
                    break;
                case 'registrator.level1':
                    result.color = '#0000ff';
                    result.text = '1';
                    break;
                case 'registrator.level2':
                    result.color = '#0000ff';
                    result.text = '2';
                    break;
                case 'registrator.level3':
                    result.color = '#0000ff';
                    result.text = '3';
                    break;
            }
        }
        return result;
    }

    function searchAddressByName(){
        var query = $("#address-input").val();
        if(!query) return;
        var myGeocoder = ymaps.geocode(query, {results: 1});
        myGeocoder.then(
                function (res) {
                    if(res.geoObjects.getLength() > 0){
                        var position = res.geoObjects.get(0).geometry.getCoordinates();
                        console.log(position);
                        window.meOnMap.geometry.setCoordinates(position);
                        window.meOnMap.properties.set({balloonContent: query});
                        window.map.setCenter(position);
                        window.map.setZoom(11);
                        window.position = position.slice();
                        registratorsStore.reload({params : getParams()});
                      //  initScrollListener();
                    } else {
                        bootbox.alert('Поиск не дал результатов, уточните адрес поиска');
                    }
                },
                function (err) {
                    bootbox.alert('Произошла ошибка поиска объекта на карте');
                }
        );

    }

    function getRequestMarkup(request){
        var model = {};

        // Дательный падеж
        var rn = new RussianName(request.registrator.fullName);
        var fullNameDat = rn.fullName(rn.gcaseDat);

        request.registrator.fullNameDat = fullNameDat;
        model.request = request;
        model.resizedAvatar = Images.getResizeUrl(request.registrator.avatar, "c40");

        var rendered = Mustache.render(requestItemTemplate, model);

        var $row = $(rendered);


        $row.find("a.do-cancel-request").click(function() {
            cancelRequest(request.id);
            return false;
        });

        return $row;
    }

    function updateRequestItem(request){
        if(!request) return;
        var $requestDiv = $("div#current-request");
        $requestDiv.empty();
        $requestDiv.append(getRequestMarkup(request));
    }

    function cancelRequest(requestId){
        bootbox.confirm("Вы действительно хотите отменить заявку на идентификацию",
                function(result){
                    if(result){
                        $.radomJsonPost("/registrator/deleteRequest", {
                            requestId: requestId
                        }, function(data){
                            if(data.result == 'success'){
                                $("div#current-request").empty();
                                bootbox.alert("Заявка на идентификацию успешно отменена");
                                location.reload();
                            }
                        });
                    }
                }
        );
    }

</script>
<div id="registrators-target">
</div>
<script id="registrators-template" type="x-tmpl-mustache">
<div id="current-request"></div>
<div id="info-registrators" style="display: none;">
    <div class="alert alert-info alert-dismissible" role="alert">
        <button onclick="$.cookie('INFO.REGISTRATORS', true); return false;" type="button" class="close do-hide-info" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
        Данная страница позволяет подать заявку на идентификацию, для этого нужно выбрать регистратора на карте или в списке.
        На карте регистратор отображается ввиде кружка с номером его ранга (высший ранг отображается буквой 'В'), для отправки заявки на идентификацию необходимо нажать кнопку
        <i class="fa fa-chevron-down"></i>.    <strong>СОЗДАТЬ МОЖНО ТОЛЬКО ОДНУ ЗАЯВКУ</strong>.
    </div>
</div>
 <div id="includeRequestedForRegistrationBlock" style="padding-top: 0px;
    padding-right: 10px;
    padding-bottom: 15px;
    ">
    <div class="row" style="padding-left: 10px;">
     <div class="col-xs-9" class="checkbox">
     <span>
     <input id="includeRequestedForRegistration"  type="checkbox"></input>
     </span>
     <span style="padding: 10px; margin-left: 2px; ">
     <span>Показывать заявки на идентификацию</span>
     </span>
     </div>
     </div>
     <div class="row" style="padding-top: 15px;">
      <div class="col-xs-5">
      <div class="row" id="includeRequestedForRegistrationModeBlock" style="display: none;">
      <div class="col-xs-11">
       <select class="form-control" id="includeRequestedForRegistrationMode">
                    <option selected value="false">Все</option>
                    <option value="true">Только мои</option>
        </select>
        </div>
        </div>
        </div>
     </div>
     </div>
<div class="row">
    <div class="col-xs-5">
        <div class="row">
            <div class="col-xs-7">
                <div class="form-group" id="search-input-block"  style="margin-bottom: 5px;">
                    <input type="text" class="form-control" id="search-input" placeholder="Начните вводить имя" />
                </div>
                <div style="font-size: 12px;">всего {{registratorsCount}}</div>
            </div>
            <div class="col-xs-4">
                <select class="form-control" id="level-select">
                    <option value="ALL">Все</option>
                    <option value="LEVEL_0">Высшего ранга</option>
                    <option value="LEVEL_1">1-го ранга</option>
                    <option value="LEVEL_2">2-го ранга</option>
                    <option value="LEVEL_3">3-го ранга</option>
                </select>
            </div>
            <div class="col-xs-1" style="padding: 0px;">
                <a href='#' data-title="Очистить форму поиска" style="text-decoration : none !important; font-size: 26px;" class="do-clear-filter"><i class="fa fa-remove"></i></a>
            </div>
        </div>

        <div class="registrators-list-panel" id="registrators-list-panel" >
            <hr style="margin: 4px 0;" />
            <div id="registrators-list"></div>

            <div class="row list-not-found" id="registrators-not-found">
                <div class="panel panel-default">
                    <div class="panel-body">Поиск не дал результатов</div>
                </div>
            </div>
            <div class="row list-loader-animation"></div>
        </div>

    </div>

    <div class="col-xs-7">
        <div class="row">
            <div class="col-xs-11">
                <div class="input-group">
                    <input type="text" class="form-control" id="address-input" placeholder="Введите адрес">
                    <div class="input-group-btn">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Выберите адрес<span class="caret"></span></button>
                        <ul class="dropdown-menu dropdown-menu-right" role="menu">
                            {{#actualAddress.fullAddress}}
                                <li><a href="#" class="do-select-address">{{actualAddress.fullAddress}}</a></li>
                            {{/actualAddress.fullAddress}}
                            {{#registrationAddress.fullAddress}}
                                <li><a href="#" class="do-select-address">{{registrationAddress.fullAddress}}</a></li>
                            {{/registrationAddress.fullAddress}}
                        </ul>
                    </div>
                </div>

            </div>
            <div class="col-xs-1">
                <a href='#' data-title="Показать на карте" style="text-decoration : none !important; font-size: 26px;" class="do-goto-map"><i class="fa fa-map-marker"></i></a>
            </div>
        </div>
        <div id="registrators-map" class="registrators-map"></div>
    </div>
</div>
</script>
<t:insertAttribute name="requestDialog" />