<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="process-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Обработать заявку на идентификацию</h4>
      		</div>
      		<div class="modal-body">
                <div id="request-details"></div>
                <div style="margin-top: 10px;">
                    <div class="form-group" data-required="true" data-message="Для отмены заявки данное поле обязательно к заполнению.">
                        <textarea class="form-control" rows="5" id="comment" name="comment" placeholder="Введите комментарий"></textarea>
                    </div>
                </div>
			</div>
			<div class="modal-footer">
                <div class="form-inline">
                    <button id="process-request-button" type="button" class="btn btn-default pull-left" style="display: none;">Перейти к идентификации</button>
                    <button type="button" class="btn btn-default pull-right" data-dismiss="modal">Закрыть</button>
                </div>
			</div>
		</div>
	</div>
</div>

<script id="request-user-details-template" type="x-tmpl-mustache">
    <div class="registration-dialog-level">Дата подачи заявки: {{request.createdDate}} в {{request.createdTime}}</div>
    <div class="tooltiped-avatar" data-sharer-ikp="{{object.ikp}}" data-data="{{object.fullName}}" style="cursor:pointer;">
        <img src="{{avatar}}" class="img-thumbnail"/>
        <a href="{{object.link}}" style="padding-left: 10px;" class="registration-dialog-registrator-name">{{object.fullName}}</a>
    </div>
    <div class="alert alert-info" role="alert" style="margin-top: 10px;">
        Чтобы перейти к идентификации пользователя <a class="alert-link" href="{{object.link}}" target="_blank">{{object.fullNameRod}}</a>
        необходимо <a class="alert-link" href="{{object.link}}" target="_blank">Проверить данные {{object.fullNameRod}}</a>.<br/>
    </div>
    <div class="alert alert-danger" role="alert" style="margin-top: 10px; font-weight: bold;">
        Проверить соответствие фотографии пользователя в ЛИК<br/>
        Проверить паспортные данные и адрес регистрации в ЛИК<br/>
        Проверить ИНН (Если имеется)<br/>
        Сделать фото пользователя с раскрытым паспортом перед собой<br>
        Сделать фото или скан паспорта (лист с персональными данными и адресом регистрации)<br>
    </div>
    <div>
        <a class="btn btn-primary" href="{{object.link}}" target="_blank">Проверить данные</a>
        нажмите сюда чтобы перейти к проверке профиля
    </div>
    <div class="checkbox">
        <label><input type="checkbox" value="" id="object-data-checked">Подтверждаю что данные, введенные в профиль {{object.shortNameRod}}, соответствуют предоставленным документам.</label>
    </div>
    <div class="alert alert-danger" role="alert" style="margin-top: 10px; display: none;" id="registrator-responsibility-danger">
        <b>ВНИМАНИЕ!</b> Вы несете ответственность за то что данные введенные в профиле {{object.shortNameRod}} совпадают с данными указанными в предоставленных документах.
    </div>

</script>

<div class="modal fade" id="process-community-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title">Обработать заявку на сертификацию</h4>
            </div>
            <div class="modal-body">
                <div id="community-request-details"></div>
            </div>
            <div class="modal-footer">
                <div class="form-inline">
                    <button id="process-community-request-button" type="button" class="btn btn-primary pull-left">Выполнить сертификацию</button>
                    <button type="button" class="btn btn-default pull-right" data-dismiss="modal">Закрыть</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script id="request-community-details-template" type="x-tmpl-mustache">
    <div class="registration-dialog-level">Дата подачи заявки: {{request.createdDate}} в {{request.createdTime}}</div>
    <div class="tooltiped-avatar" data-community-ikp="{{object.ikp}}" data-data="{{object.name}}" style="cursor:pointer;">
        <img src="{{avatar}}" class="img-thumbnail"/>
        <a href="{{object.link}}" style="padding-left: 10px;" class="registration-dialog-registrator-name">{{object.name}}</a>
    </div>
    <div class="alert alert-info" role="alert" style="margin-top: 10px;">
        Чтобы перейти к сертификации организации <a class="alert-link" href="{{object.link}}" target="_blank">{{object.name}}</a>
        необходимо <a class="alert-link" href="{{object.link}}/edit" target="_blank">проверить её данные</a>.<br/>
    </div>
    <div class="alert alert-danger" role="alert" style="margin-top: 10px; font-weight: bold;">
        Проверить общие данные организации<br/>
    </div>
    <div>
        <a class="btn btn-primary" href="{{object.link}}/edit" target="_blank">Проверить данные</a>
        нажмите сюда чтобы перейти к проверке объединения
    </div>
</script>

<script type="text/javascript">

    var ProcessCertificationRequestDialog = {
        requestId: null,
        objectId: null,
        show: function (request) {
            var model = {};

            model.avatar = Images.getResizeUrl(request.object.avatar, "c50"),
            model.object = request.object;
            model.request = request;
            this.requestId = request.id;
            this.objectType = request.objectType;
            this.objectId = request.object.id;
            $('#comment').val("");
            $('#comment').hide();

            if (this.objectType == "SHARER") { // Сертификация участника

                // Родительный падеж
                var rn = new RussianName(request.object.fullName);
                var fullNameRod = rn.fullName(rn.gcaseRod);
                var shortNameRod = rn.lastName(rn.gcaseRod);
                var firstName = rn.firstName(rn.gcaseRod);
                var middleName = rn.middleName(rn.gcaseRod);
                if ((firstName) && (firstName.length > 0)){
                    shortNameRod+=(" "+firstName.charAt(0)+".");
                }
                if ((middleName) && (middleName.length > 0)){
                    shortNameRod+=(" "+middleName.charAt(0)+".");
                }
                model.object.fullNameRod = fullNameRod;
                model.object.shortNameRod = shortNameRod;

                $('#request-details').html(Mustache.to_html($('#request-user-details-template').html(), model));
                $('#object-data-checked').click(function () {
                    if ($('#object-data-checked').is(':checked')) {
                        $("#process-request-button").show();
                        $("#registrator-responsibility-danger").show();
                    } else {
                        $("#process-request-button").hide();
                        $("#registrator-responsibility-danger").hide();
                    }
                });
                $("div#process-request-modal").modal("show");
            } else if (this.objectType == "COMMUNITY") { // Сертификация организации
                $('#community-request-details').html(Mustache.to_html($('#request-community-details-template').html(), model));
                $("div#process-community-request-modal").modal("show");
            }
        },
        init: function(){
            $("#process-request-button").click($.proxy(function(){ // Сертификация участника
                $("div#process-request-modal").modal("hide");
                $.radomSharerCertification({
                    sharerId: this.objectId,
                    successCallback: function (response) {
                        $(radomEventsManager).trigger("registrationRequest.updateList");
                    }
                });
            }, this));
            $("#process-community-request-button").click($.proxy(function(){ // Сертификация организации
                $("div#process-community-request-modal").modal("hide");
                $.radomJsonPostWithWaiter("/communities/verified.json",
                        {
                            community_id: this.objectId
                        }, function(){
                            $(radomEventsManager).trigger("registrationRequest.updateList");
                        }
                );
            }, this));
        }
    };

    ProcessCertificationRequestDialog.init();
	
</script>