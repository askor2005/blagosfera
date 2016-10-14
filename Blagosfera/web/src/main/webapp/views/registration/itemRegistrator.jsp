<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<script id="registrator-avatar-template" type="x-tmpl-mustache">
<div class="registrator-item" data-sharer-id="{{sharer.id}}">
    <a class="sharer-item-avatar-link" href="{{sharer.link}}">
        <img class="img-thumbnail" src="{{resizedAvatar}}" style="height:50px;width:50px;" />
    </a>
</div>

</script>
<script id="registrator-info-template" type="x-tmpl-mustache">
<div class="row registrator-item" style="{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}" data-sharer-id="{{sharer.id}}">
    <div class="registrator-item" data-sharer-id="{{sharer.id}}">
        <div><a href="javascript:void(0);" class="registrator-item-link-text" style="{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}">{{sharer.fullName}}</a></div>
        <div class="registrator-item-level">{{level.name}}</div>
    </div>
</div>
</script>

<script id="registrator-controls-template" type="x-tmpl-mustache">
<div class="row registrator-item" style="{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}" data-sharer-id="{{sharer.id}}">
    <div style="margin: 0 0px 0 0; font-size: 18px;">
        <a href="javascript:void(0);" class="fa fa-comments-o pull-right go-to-chat"
           title="Написать сообщение" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>
        <a href="#" class="fa fa-map-marker pull-right do-map-marker"
           data-title="Показать на карте" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>

        {{^hasRegistrationRequests}}
        <a href="#" class="fa fa-chevron-down pull-right do-send-request"
           data-title="Подать заявку на идентификацию" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>
        {{/hasRegistrationRequests}}

        {{#hasRegistrationRequests}}
        {{#requested}}
        <a href="#" class="fa fa-check pull-right" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"
           title="Вы уже подали заявку этому регистратору"></a>
        {{/requested}}
        {{^requested}}
        {{/requested}}
        {{/hasRegistrationRequests}}
    </div>
    <div style="text-align: right; font-size: 12px;  margin-top: 30px !important;">{{distance}}</div>
</div>
</script>

<script id="registrators-list-item-template" type="x-tmpl-mustache">
<div class="row registrator-item" style="{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}" data-sharer-id="{{sharer.id}}">
	<div class="col-xs-2">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{sharer.link}}">
					<img class="img-thumbnail" src="{{resizedAvatar}}" style="height:50px;width:50px;" />
				</a>
			</div>
		</div>
	</div>
	<div class="col-xs-10">
		<div class="row">
		    <div class="col-xs-8">
			    <div><a href="javascript:void(0);" class="registrator-item-link-text" style="{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}">{{sharer.fullName}}</a></div>
			    <div class="registrator-item-level">{{level.name}}</div>
			</div>
			<div class="col-xs-4">
			    <div style="margin: 0 10px 0 0; font-size: 18px;">
			        <a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');" class="fa fa-comments-o pull-right go-to-chat" data-title="Написать сообщение" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>
			        <a href="#" class="fa fa-map-marker pull-right do-map-marker"  data-title="Показать на карте" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>

                    {{^hasRegistrationRequests}}
			        <a href="#"  class="fa fa-chevron-down pull-right do-send-request" data-title="Подать заявку на идентификацию" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}"></a>
			        {{/hasRegistrationRequests}}

			        {{#hasRegistrationRequests}}
			        {{#requested}}
			        <a href="#" class="fa fa-check pull-right" style="text-decoration : none !important;{{#requestedToMe}}color:#E94E44;{{/requestedToMe}}" title="Вы уже подали заявку этому регистратору"></a>
			        {{/requested}}
			        {{^requested}}
			        {{/requested}}
			        {{/hasRegistrationRequests}}
                </div>
                <div style="text-align: right; font-size: 12px;  margin-top: 30px !important; margin-right: 10px;">{{distance}}</div>
			</div>
			<!--<a href="#" class="fa fa-money pull-right do-accounts-move" data-title="Перевести средства" style="text-decoration : none !important;"></a>-->
		</div>
	</div>
</div>
</script>

<script id="registrators-map-item-template" type="x-tmpl-mustache">
	<div>
	    <div class="registrator-map-address"  style="{{#requestedToMe}}{{/requestedToMe}}">
    	    {{#sharer.officeAddress.fullAddress}}<div><i class="fa fa-building"></i> {{sharer.officeAddress.fullAddress}}</a></div>{{/sharer.officeAddress.fullAddress}}
	    </div>
	    <table class="fixed-thead">
	        <tr>
	            <td style="vertical-align: top;">
	                <a class="map-avatar-wrapper" href='{{sharer.link}}'>
                        <img class="avatar img-thumbnail" src='{{resizedAvatar}}' />
                    </a>
	            </td>
	            <td style="vertical-align: top;">
	                {{^requestedToMe}}<div class="registrator-map-level"><i class="fa fa-user-plus"></i> {{level.name}}</div>{{/requestedToMe}}
                    <div><a style="{{#requestedToMe}}{{/requestedToMe}}" href='{{sharer.link}}' class="name">{{sharer.fullName}}</a></div>
                    <div><a style="{{#requestedToMe}}{{/requestedToMe}}" href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');"><i class="glyphicon glyphicon-envelope"></i> Написать сообщение</a></div>
                    {{^hasRegistrationRequests}}{{^requestedToMe}}<div><a href='#' {{#allProfileFilled}}onclick="showRequestDialog({{sharer.id}}); return false"{{/allProfileFilled}}{{^allProfileFilled}}onclick="bootbox.alert('Ваш профиль не заполнен на 100%! Вы не можете подать заявку регистратору пока не заполните профиль.');return false"{{/allProfileFilled}}><i class="fa fa-chevron-down"></i> Подать заявку на идентификацию</a></div>{{/requestedToMe}} {{/hasRegistrationRequests}}
                    {{#registrator.registratorMobilePhone}}<div><a style="{{#requestedToMe}}{{/requestedToMe}}" href="callto:{{registrator.registratorMobilePhone}}?call"><i class="fa fa-mobile"></i> {{registrator.registratorMobilePhone}}</a></div>{{/registrator.registratorMobilePhone}}
                    {{#registrator.registratorOfficePhone}}<div><a style="{{#requestedToMe}}{{/requestedToMe}}" href="callto:{{registrator.registratorOfficePhone}}?call"><i class="fa fa-phone"></i>{{registrator.registratorOfficePhone}}</a></div>{{/registrator.registratorOfficePhone}}
                    {{#registrator.skype}}<div><a  style="{{#requestedToMe}}{{/requestedToMe}}" href="skype:{{registrator.skype}}?call"><i class="fa fa-skype"></i> {{registrator.skype}}</a></div>{{/registrator.skype}}

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
    $(document).ready(function () {
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

    function selectRegistratorOnMap(registrator) {
        var sharer = registrator.user;
        var address = sharer.officeAddress;
        if (address && address.latitude && address.longitude) {
            var position = [address.latitude, address.longitude];
            var map = window.map;

            // Создаем коллекцию геообъектов.
            var myCollection = new ymaps.GeoObjectCollection();

            // Добавляем метки в коллекцию.
            var geoRegistrator = window.geoObjects[sharer.id];
            myCollection.add(geoRegistrator);
            myCollection.add(window.meOnMap);
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
            if (map.getZoom() > 1) map.setZoom(map.getZoom() - 1);

            if (geoRegistrator) geoRegistrator.balloon.open();
            delete myCollection;
        }
    }

    function showRequestDialog(id) {
        CreateCertificationRequestDialog.show(window.registrators[id]);
    }

    var registratorsListItemTemplate = $('#registrators-list-item-template').html();
    Mustache.parse(registratorsListItemTemplate);

    var registratorAvatarTemplate = $('#registrator-avatar-template').html();
    Mustache.parse(registratorAvatarTemplate);

    var registratorInfoTemplate = $('#registrator-info-template').html();
    Mustache.parse(registratorInfoTemplate);

    var registratorControlsTemplate = $('#registrator-controls-template').html();
    Mustache.parse(registratorControlsTemplate);

    var registratorsMapItemTemplate = $('#registrators-map-item-template').html();
    Mustache.parse(registratorsMapItemTemplate);

    var groups = null;

    function getRegistratorAvatarMarkup(registrator) {
        var sharer = registrator.user;
        window.registrators[sharer.id] = registrator;
        var model = {};
        model.requestedToMe = registrator.requestedToMe;
        model.sharer = sharer;
        model.hasRegistrationRequests = "${hasRegistrationRequests}" === "true";
        model.requested = registrator.requested;
        model.level = registrator.level
        if (registrator.distance) {
            model.distance = registrator.distance.toFixed(2) + " км";
        } else {
            model.distance = "Ошибка";
        }
        model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

        var rendered = Mustache.render(registratorAvatarTemplate, model);

        var $row = $(rendered);
        return $row.html();
    }

    function getRegistratorInfoMarkup(registrator) {
        var sharer = registrator.user;
        window.registrators[sharer.id] = registrator;
        var model = {};
        model.requestedToMe = registrator.requestedToMe;
        model.sharer = sharer;
        model.hasRegistrationRequests = "${hasRegistrationRequests}" === "true";
        model.requested = registrator.requested;
        model.level = registrator.level
        if (registrator.distance) {
            model.distance = registrator.distance.toFixed(2) + " км";
        } else {
            model.distance = "Ошибка";
        }
        model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

        var rendered = Mustache.render(registratorInfoTemplate, model);

        var $row = $(rendered);
        return $row.html();
    }

    function getRegistratorControlsMarkup(registrator) {
        var sharer = registrator.user;
        window.registrators[sharer.id] = registrator;
        var model = {};
        model.requestedToMe = registrator.requestedToMe;
        model.sharer = sharer;
        model.hasRegistrationRequests = "${hasRegistrationRequests}" === "true";
        model.requested = registrator.requested;
        model.level = registrator.level
        if (registrator.distance) {
            model.distance = registrator.distance.toFixed(2) + " км";
        } else {
            model.distance = "Ошибка";
        }
        model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

        var rendered = Mustache.render(registratorControlsTemplate, model);

        var $row = $(rendered);
        return $row.html();
    }

    function getRegistratorMarkup(registrator) {
        var sharer = registrator.user;
        window.registrators[sharer.id] = registrator;
        var model = {};
        model.requestedToMe = registrator.requestedToMe;
        model.sharer = sharer;
        model.hasRegistrationRequests = "${hasRegistrationRequests}" === "true";
        model.requested = registrator.requested;
        model.level = registrator.level
        if (registrator.distance) {
            model.distance = registrator.distance.toFixed(2) + " км";
        } else {
            model.distance = "Ошибка";
        }
        model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

        var rendered = Mustache.render(registratorsListItemTemplate, model);

        var $row = $(rendered);

        $row.data("sharer", sharer);

        $row.find("span.request-distance").radomTooltip({
            placement: "top",
            container: "body"
        });

        $row.find("a.go-to-chat, a.do-map-marker, a.do-send-request").radomTooltip({
            placement: "top",
            container: "body"
        });

        $row.find("a.do-map-marker").click(function () {
            selectRegistratorOnMap(registrator);
            return false;
        });
        $row.find("a.do-send-request").click(function (event) {
            event.stopPropagation();
            CreateCertificationRequestDialog.show(registrator);
            return false;
        });

        $row.click(function () {
            $(".registrator-item").removeClass("selected-registrator");
            var sharerId = $(this).data('sharer-id');
            selectRegistratorOnMap(window.registrators[sharerId]);
            $(this).addClass("selected-registrator");
        });

        return $row;
    }

    function getRegistratorMapMarkup(registrator, profileFillingPercent) {
        var sharer = registrator.user;

        var model = {};
        if (profileFillingPercent) {
            if (profileFillingPercent == 100) {
                model.allProfileFilled = true;
            }
            else {
                model.allProfileFilled = false;
            }
        }
        else {
            model.allProfileFilled = true;
        }
        model.hasRegistrationRequests = "${hasRegistrationRequests}" === "true";
        model.requestedToMe = registrator.requestedToMe;
        model.registrator = registrator;
        model.sharer = sharer;
        model.level = registrator.level;
        model.timetable = registrator.timetable;
        model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c50");

        var rendered = Mustache.render(registratorsMapItemTemplate, model);

        var $row = $(rendered);

        $row.data("sharer", sharer);

        $row.find("a.go-to-chat, a.do-send-request").radomTooltip({
            placement: "top",
            container: "body"
        });

        return $row;
    }
</script>