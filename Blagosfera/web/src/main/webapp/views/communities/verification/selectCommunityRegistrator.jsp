<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<c:choose>
    <c:when test="${not empty errorMessage}">
        <h1 style="font-size: 30px;">${community.name}</h1>
        <h2><small>${errorMessage}</small></h2>
        <hr/>
    </c:when>
    <c:otherwise>
        <style type="text/css">
            .registrators-map-organization {
                height: 300px;
                width: 100%;
                margin-bottom: 20px;
            }
        </style>

        <script id="request-item-template" type="x-tmpl-mustache">
        <div class="request-item">
            <a href='javascript:void(0);' class="btn btn-primary do-cancel-request" style="margin-right: 10px;">Отменить заявку</a>
            <span class="my-request-item-date">{{request.createdDate}}</span> в
            <span class="my-request-item-date">{{request.createdTime}} </span>
                Вы подали заявку на сертификацию Регистратору
                <a href="{{request.registrator.link}}">
                    {{request.registrator.fullNameDat}}
                    <img class="img-thumbnail" src="{{resizedAvatar}}" />
                </a>
            </span>
        </div>
        </script>
        <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
        <script type="text/javascript">
            var requestItemTemplate = $('#request-item-template').html();
            Mustache.parse(requestItemTemplate);


            function initScrollListener(firstPage) {
                $("div#registrators-list").empty();
                ScrollListener.init("/communities/registratorsList.json", "post", getParams, function() {
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
                }, null, firstPage, "registrators-list-panel");
            }

            function getParams() {
                var params = {};
                params.nameTemplate = $("#search-input").val();
                params.level = $("select#level-select").val();
                params.latitude = window.position[0];
                params.longitude = window.position[1];

                return params;
            }

            function loadRequest() {
                $.radomJsonPost("/communities/getVerificationRequest.json", {
                    community_id: "${community.id}"
                }, function(data){
                    if(data && data.result == null){
                        updateRequestItem(data);
                    }
                });
            }

            $(document).ready(function() {
                if(!radomLocalStorage.getItem("INFO.REGISTRATORS.FOR_ORGANIZATION")){
                    $("#info-registrators").show();
                }

                var organizationAddress = ${organizationAddress};
                window.registrators = {};
                window.position = [!(organizationAddress.latitude) ? '55.45' : organizationAddress.latitude, !(organizationAddress.longitude) ? '37.36' : organizationAddress.longitude];

                loadRequest();
                $(radomEventsManager).bind("registrationRequest.createRequest", function(event) {
                    loadRequest();
                });

                initSearchInput($("#search-input"), function() {
                    initScrollListener();
                });
                initScrollListener();
                $("select#level-select").change(function(){
                    initScrollListener();
                });

                $("a.do-clear-filter").click(function() {
                    $("#search-input").val("");
                    $("select#level-select").val("ALL");
                    initScrollListener();
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

                    window.organizationOnMap = new ymaps.Placemark(position,
                            {
                                balloonContent: organizationAddress.full,
                                iconContent: ''
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

                    map.geoObjects.add(window.organizationOnMap);
                    searchAddressByName();
                    loadRegistratorsForMap();
                    $(window).resize(function(){
                        map.container.fitToViewport();
                    })
                });


                if(organizationAddress && organizationAddress.full){
                    $("#address-input").val(organizationAddress.full);
                }

            });
            function loadRegistratorsForMap(){
                $.radomJsonPost("/communities/allRegistrators.json", {
                }, function(response){
                    window.map.geoObjects.removeAll();
                    window.geoObjects = {};
                    // Создаем коллекцию геообъектов.
                    if(window.organizationOnMap) window.map.geoObjects.add(window.organizationOnMap);
                    var collection = new ymaps.GeoObjectCollection();
                    $.each(response, function(index, registrator) {
                        var sharer = registrator.user;
                        var address = registrator.user.userData.officeAddress;
                        var marker = getRegistratorIcon(registrator.level);
                        if(address.latitude && address.longitude) {
                            window.geoObjects[sharer.id] =
                                    new ymaps.Placemark(
                                            [address.latitude, address.longitude],
                                            {
                                                id: sharer.id,
                                                balloonContent: getRegistratorMapMarkup(registrator).html(),
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

            function getRegistratorIcon(level){
                var result = {
                    icon: 'twirl#greyIcon',
                    result: '#3caa3c',
                    text: '?'
                };

                if(level) {
                    switch(level.mnemo) {
                        case 'registrator.level0':
                            result.color = '#00ffff';
                            result.text = 'В';
                            break;
                        case 'registrator.level1':
                            result.color = '#0000ff';
                            result.text = '1';
                            break;
                        case 'registrator.level2':
                            result.color = '#3caa3c';
                            result.text = '2';
                            break;
                        case 'registrator.level3':
                            result.color = '#ffff33';
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
                                window.organizationOnMap.geometry.setCoordinates(position);
                                window.organizationOnMap.properties.set({balloonContent: query});
                                window.map.setCenter(position);
                                window.map.setZoom(11);
                                window.position = position.slice();
                                initScrollListener();
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
                bootbox.confirm("Вы действительно хотите отменить заявку на сертификацию",
                        function(result){
                            if(result){
                                $.radomJsonPost("/registrator/deleteRequest", {
                                    requestId: requestId
                                }, function(data){
                                    if(data.result == 'success'){
                                        $("div#current-request").empty();
                                        bootbox.alert("Заявка на сертификацию успешно отменена");
                                        //$(radomEventsManager).trigger("registrationRequest.deleteRequest");
                                    }
                                });
                            }
                        }
                );
            }

        </script>
        <h1 style="font-size: 30px;">${community.name}</h1>
        <h2><small>Выбор регистратора для сертификации организации</small></h2>
        <hr/>
        <div id="current-request"></div>
        <div id="info-registrators" style="display: none;">
            <div class="alert alert-info alert-dismissible" role="alert">
                <button onclick="radomLocalStorage.setItem('INFO.REGISTRATORS.FOR_ORGANIZATION', 'true'); return false;" type="button" class="close do-hide-info" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                Данная страница позволяет подать заявку на сертификацию, для этого нужно выбрать регистратора на карте или в списке.
                На карте регистратор отображается ввиде кружка с номером его ранга (высший ранг отображается буквой 'В'), для отправки заявки на сертификацию необходимо нажать кнопку
                <i class="fa fa-chevron-down"></i>.    <strong>СОЗДАТЬ МОЖНО ТОЛЬКО ОДНУ ЗАЯВКУ</strong>.
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="row">
                    <div class="col-xs-11">
                        <div class="input-group">
                            <input type="text" class="form-control" id="address-input" placeholder="Введите адрес">
                            <div class="input-group-btn">
                                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Выберите адрес<span class="caret"></span></button>
                                <ul class="dropdown-menu dropdown-menu-right" role="menu">
                                    <c:if test="${community.actualAddress.fullAddress.length() > 0}">
                                        <li><a href="#" class="do-select-address">${community.actualAddress.fullAddress}</a></li>
                                    </c:if>
                                    <c:if test="${community.registrationAddress.fullAddress.length() > 0}">
                                        <li><a href="#" class="do-select-address">${community.registrationAddress.fullAddress}</a></li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>

                    </div>
                    <div class="col-xs-1">
                        <a href='#' data-title="Показать на карте" style="text-decoration : none !important; font-size: 26px;" class="do-goto-map"><i class="fa fa-map-marker"></i></a>
                    </div>
                </div>
                <div id="registrators-map" class="registrators-map-organization"></div>
            </div>
        </div>

        <div class="row">
            <div class="col-xs-7">
                <div class="form-group" id="search-input-block"  style="margin-bottom: 5px;">
                    <input type="text" class="form-control" id="search-input" placeholder="Начните вводить имя" />
                </div>
                <div style="font-size: 12px;">всего ${registratorsCount}</div>
            </div>
            <div class="col-xs-4">
                <select class="form-control" id="level-select">
                    <option value="ALL">Все</option>
                    <option value="LEVEL_0">Высшего ранга</option>
                    <option value="LEVEL_1">1-го ранга</option>
                    <option value="LEVEL_2">2-го ранга</option>
                </select>
            </div>
            <div class="col-xs-1" style="padding: 0px;">
                <a href='#' data-title="Очистить форму поиска" style="text-decoration : none !important; font-size: 26px;" class="do-clear-filter"><i class="fa fa-remove"></i></a>
            </div>
        </div>

        <div class="registrators-list-panel" id="registrators-list-panel">
            <hr style="margin: 4px 0;" />
            <div id="registrators-list"></div>

            <div class="row list-not-found" id="registrators-not-found">
                <div class="panel panel-default">
                    <div class="panel-body">Поиск не дал результатов</div>
                </div>
            </div>
            <div class="row list-loader-animation"></div>
        </div>


        <!-- Блок работы с регистратором -->
        <script id="registrators-list-item-template" type="x-tmpl-mustache">
        <div class="row registrator-item" data-sharer-id="{{sharer.id}}">
            <div class="col-xs-2">
                <div class="row">
                    <div class="col-xs-12">
                        <a class="sharer-item-avatar-link" href="{{sharer.link}}">
                            <img class="img-thumbnail" src="{{resizedAvatar}}" />
                        </a>
                    </div>
                </div>
            </div>
            <div class="col-xs-10">
                <div class="row">
                    <div class="col-xs-9">
                        <div><a href="{{sharer.link}}" class="registrator-item-link-text">{{sharer.fullName}}</a></div>
                        <div class="registrator-item-level">{{level.name}}</div>
                    </div>
                    <div class="col-xs-3">
                        <div style="margin: 0 5px 0 0; font-size: 18px;">
                            <a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');" class="fa fa-comments-o pull-right go-to-chat" data-title="Написать сообщение" style="text-decoration : none !important;"></a>
                            <a href="#" class="fa fa-map-marker pull-right do-map-marker" data-title="Показать на карте" style="text-decoration : none !important;"></a>
                            <a href="#" class="fa fa-chevron-down pull-right do-send-request" data-title="Подать заявку на сертификацию" style="text-decoration : none !important;"></a>
                        </div>
                        <div style="text-align: right; font-size: 12px;  margin-top: 30px !important;">{{distance}}</div>
                    </div>
                    <!--<a href="#" class="fa fa-money pull-right do-accounts-move" data-title="Перевести средства" style="text-decoration : none !important;"></a>-->
                </div>
            </div>
        </div>
        </script>

        <script id="registrators-map-item-template" type="x-tmpl-mustache">
            <div>
                <div class="registrator-map-address">
                    {{#sharer.userData.officeAddress.fullAddress}}<div><i class="fa fa-building"></i> {{sharer.userData.officeAddress.fullAddress}}</a></div>{{/sharer.userData.officeAddress.fullAddress}}
                </div>
                <table class="fixed-thead">
                    <tr>
                        <td style="vertical-align: top;">
                            <a class="map-avatar-wrapper" href='{{sharer.link}}'>
                                <img class="avatar img-thumbnail" src='{{resizedAvatar}}' />
                            </a>
                        </td>
                        <td style="vertical-align: top;">
                            <div class="registrator-map-level"><i class="fa fa-user-plus"></i> {{level.name}}</div>
                            <div><a href='{{sharer.link}}' class="name">{{sharer.fullName}}</a></div>
                            <div><a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');"><i class="glyphicon glyphicon-envelope"></i> Написать сообщение</a></div>
                            <div><a href='#' onclick="showRequestDialog({{sharer.id}}); return false"><i class="fa fa-chevron-down"></i> Подать заявку на сертификацию</a></div>
                            {{#registrator.registratorMobilePhone}}<div><a href="callto:{{registrator.registratorMobilePhone}}?call"><i class="fa fa-mobile"></i> {{registrator.registratorMobilePhone}}</a></div>{{/registrator.registratorMobilePhone}}
                            {{#registrator.registratorOfficePhone}}<div><a href="callto:{{registrator.registratorOfficePhone}}?call"><i class="fa fa-phone"></i>{{registrator.registratorOfficePhone}}</a></div>{{/registrator.registratorOfficePhone}}
                            {{#registrator.skype}}<div><a href="skype:{{registrator.skype}}?call"><i class="fa fa-skype"></i> {{registrator.skype}}</a></div>{{/registrator.skype}}

                            {{#timetable}}
                                Время работы:
                                {{#timetable.combinedDays}}
                                    <br/>
                                    {{title}} : {{text}}
                                {{/timetable.combinedDays}}
                                <br/>
                                Сейчас : {{#timetable.now}}работает{{/timetable.now}}{{^timetable.now}}не работает{{/timetable.now}}
                            {{/timetable}}
                        </td>
                    </tr>
                </table>
            </div>
        </script>

        <script type="text/javascript">
            $(document).ready(function() {
                $("table.fixed-thead").fixMe();
            });

            var $searchInput = null;

            function initSearchInput($input, callback) {
                $searchInput = $input;
                $input.keyup(function () {

                    var timeout = $input.data("timeout");
                    if (timeout) {
                        clearTimeout(timeout);
                    }
                    timeout = setTimeout(function () {
                        var newValue = $input.val();
                        var oldValue = $input.data("old-value");
                        if (newValue != oldValue) {
                            if ((newValue.length >= 4) || (newValue.length == 0)) {
                                $input.data("old-value", newValue);
                                callback(newValue);
                            }
                        }
                    }, 300);
                    $input.data("timeout", timeout);
                });
            }


            function selectRegistratorOnMap(registrator){
                var sharer = registrator.user;
                var address = sharer.userData.officeAddress;
                if(address && address.latitude && address.longitude){
                    var position = [address.latitude, address.longitude];
                    var map = window.map;

                    // Создаем коллекцию геообъектов.
                    var myCollection = new ymaps.GeoObjectCollection();

                    // Добавляем метки в коллекцию.
                    var geoRegistrator = window.geoObjects[sharer.id];
                    myCollection.add(geoRegistrator);
                    myCollection.add(window.organizationOnMap);
                    map.geoObjects.add(myCollection);
        //        ymaps.modules.require(['geoObject.Arrow'], function (Arrow) {
        //            var arrow = new Arrow([[57.733835, 38.788227], [55.833835, 35.688227]], null, {
        //                geodesic: true,
        //                strokeWidth: 5,
        //                opacity: 0.5,
        //                strokeStyle: 'shortdash'
        //            });
        //            myMap.geoObjects.add(arrow);
        //        });

                    // Устанавливаем карте центр и масштаб так, чтобы охватить коллекцию целиком.
                    map.setBounds(myCollection.getBounds());
                    if(map.getZoom() > 1) map.setZoom(map.getZoom() - 1);

                    if(geoRegistrator) geoRegistrator.balloon.open();
                    delete myCollection;
                }
            }

            function showRequestDialog(id) {
                CreateCertificationRequestDialog.show(window.registrators[id]);
            }

            var registratorsListItemTemplate = $('#registrators-list-item-template').html();
            Mustache.parse(registratorsListItemTemplate);

            var registratorsMapItemTemplate = $('#registrators-map-item-template').html();
            Mustache.parse(registratorsMapItemTemplate);


            var groups = null;


            function getRegistratorMarkup(registrator) {

                var sharer = registrator.user;
                window.registrators[sharer.id] = registrator;

                var model = {};
                model.sharer = sharer;
                model.level=registrator.level
                if(registrator.distance) {
                    model.distance = registrator.distance.toFixed(2) + " км";
                } else {
                    model.distance = "Ошибка";
                }
                model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

                var rendered = Mustache.render(registratorsListItemTemplate, model);

                var $row = $(rendered);

                $row.data("sharer", sharer);

                $row.find("span.request-distance").radomTooltip({
                    placement : "top",
                    container : "body"
                });

                $row.find("a.go-to-chat, a.do-map-marker, a.do-send-request").radomTooltip({
                    placement : "top",
                    container : "body"
                });

                $row.find("a.do-map-marker").click(function() {
                    selectRegistratorOnMap(registrator);
                    return false;
                });
                $row.find("a.do-send-request").click(function(event) {
                    event.stopPropagation();
                    CreateCertificationRequestDialog.show(registrator);
                    return false;
                });

                $row.click(function(){
                    $(".registrator-item").removeClass("selected-registrator");
                    var sharerId = $(this).data('sharer-id');
                    selectRegistratorOnMap(window.registrators[sharerId]);
                    $(this).addClass("selected-registrator");
                });

                return $row;
            }

            function getRegistratorMapMarkup(registrator) {

                var sharer = registrator.user;

                var model = {};
                model.registrator = registrator;
                model.sharer = sharer;
                model.level= registrator.level;
                model.timetable = registrator.timetable;
                model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

                var rendered = Mustache.render(registratorsMapItemTemplate, model);

                var $row = $(rendered);

                $row.data("sharer", sharer);

                $row.find("a.go-to-chat, a.do-send-request").radomTooltip({
                    placement : "top",
                    container : "body"
                });

                return $row;
            }

        </script>


        <!-- Блок создания заявки -->
        <div class="modal fade" id="create-registration-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            <span aria-hidden="true">&times;</span>
                            <span class="sr-only">Закрыть</span></button>
                        <h4 class="modal-title">Подать заявку на сертификацию</h4>
                    </div>
                    <div class="modal-body">
                        <div id="registrator-details"></div>
                        <div class="alert alert-info" role="alert" style="margin-top: 10px;">
                            Перед подачей заявки на сертификацию свяжитесь с выбранным регистратором.
                            Вы можете подать только одну заявку, если Вы хотите подать заявку другому регистратору,
                            то необходимо отменить уже существующую заявку, это можно сделать на странице
                            <a href="${community.link}/registrator/select" class="alert-link">выбора регистратора</a>.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div class="form-inline">
                            <button id="create-request-button" type="button" class="btn btn-default pull-left">Подать заявку</button>
                            <button type="button" class="btn btn-default pull-right" data-dismiss="modal">Закрыть</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script id="registrator-details-template" type="x-tmpl-mustache">
            <div class="registration-dialog-level">Регистратор {{level.name}}</div>
            <div class="tooltiped-avatar" data-sharer-ikp="{{user.ikp}}" data-data="{{user.fullName}}" style="cursor:pointer;">
                <img src="{{avatar}}" class="img-thumbnail" style="padding-right: 10px;"/>
                <a class="registration-dialog-registrator-name">{{user.fullName}}</a>
            </div>
        </script>

        <script type="text/javascript">
            var CreateCertificationRequestDialog = {
                init: false,
                registrator: null,
                show: function (registrator) {
                    var model = {};
                    model.avatar = Images.getResizeUrl(registrator.user.avatar, "c50");
                    model.user = registrator.user;
                    model.level = registrator.level;

                    $('#registrator-details').html(Mustache.to_html($('#registrator-details-template').html(), model));
                    this.registrator = registrator.user;
                    $("div#create-registration-request-modal").modal("show");
                },
                init: function(){
                    $('#create-request-button').click($.proxy(function () {
                        $.radomJsonPostWithWaiter("/communities/createVerificationRequest.json", {
                            registrator_id: this.registrator.id,
                            community_id: "${community.id}"
                        }, $.proxy(function (data) {
                            if(data.result = "success"){
                                $(radomEventsManager).trigger("registrationRequest.createRequest");
                                $("div#create-registration-request-modal").modal("hide");
                                console.log(this);

                                // Дательный падеж
                                var rn = new RussianName(this.fullName);
                                var fullNameDat = rn.fullName(rn.gcaseDat);

                                bootbox.dialog({
                                    closeButton: false,
                                    message: "Заявка на сертификацию успешно передана Регистратору " + fullNameDat + ". " +
                                    "Если Вы подали заявку по ошибке или же передумали ее подавать, " +
                                    "Вы можете удалить её на странице <a href='${community.link}/registrator/select' class='alert-link'>выбора регистратора</a>.",
                                    buttons: {
                                        success:{
                                            label: "ОК",
                                            callback: function(){

                                            }
                                        }
                                    }
                                });
                            }
                        }, this.registrator));
                    }, this));
                }

            };

            CreateCertificationRequestDialog.init();


        </script>
    </c:otherwise>
</c:choose>