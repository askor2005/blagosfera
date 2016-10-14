<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="agreement-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
    <div class="modal-dialog" style="overflow-y: initial !important;">
		<div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				    <span class="sr-only">Закрыть</span>
                </button>
                <h3 class="panel-title">
                    Пользовательское соглашение
                </h3>
            </div>
            <div class="modal-body">
                <div class="alert alert-info">
                    Для идентификации профиля Вам необходимо принять приведенное ниже соглашение.
                </div>
                <div id="text-container" style="max-height: 400px; overflow-y: auto;">
                    <div class="checkbox" style="margin: 0 !important;">
                        <label>
                            <input type="checkbox" id="agree-with-agreement">Принять <a href="#" id="agreementLink"> условия соглашения</a>
                        </label>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6 text-right">
                       <!-- <a href="/certification-agreement-print" class="btn btn-info btn-xs"
                           id="agreement-print-link" target="_blank"><i class="glyphicon glyphicon-print"></i>На
                            печать</a>-->

                        <button class="btn btn-success btn-xs" id="agree-button" disabled="true"><i
                                class="glyphicon glyphicon-ok"></i>Далее
                        </button>
                        <button type="button" class="btn btn-default btn-xs" data-dismiss="modal">Закрыть</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="create-registration-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Подать заявку на идентификацию</h4>
      		</div>
      		<div class="modal-body">
                <div id="registrator-details"></div>
                <div class="alert alert-info" role="alert" style="margin-top: 10px;">
                    Перед подачей заявки на идентификацию свяжитесь с выбранным регистратором.
                    Вы можете подать только одну заявку, если Вы хотите подать заявку другому регистратору,
                    то необходимо отменить уже существующую заявку, это можно сделать либо на странице <a href="/sharer" class="alert-link">профиля</a>,
                    либо на странице <a href="/registrator/select" class="alert-link">выбора регистратора</a>.
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
            $.radomJsonGet("/certification-agreement-text", {}, function(response) {
                var $agreementTextPanel = $('#agreement-modal');
                $("#modalAgreementText").html(response.text);
                $("#acceptAgreementButton").click(function () {
                    $agreementTextPanel.find("#agree-with-agreement").prop('checked', true);
                    $agreementTextPanel.find("#agree-button").attr('disabled', false);
                    $("#licenseAgreementModal").modal("hide");
                });
                $("#agreementLink").click(function(){
                    $("#licenseAgreementModal").modal("show");
                });
                //$agreementTextPanel.find("#text-container").html(response.text);

                $agreementTextPanel.find("#agree-with-agreement").prop('checked', false);
                $agreementTextPanel.find("#agree-button").attr('disabled', true);

                $agreementTextPanel.find("#agree-with-agreement").click(function () {
                    $agreementTextPanel.find("#agree-button").attr('disabled', !$(this).is(':checked'));
                });

                $agreementTextPanel.find("#agree-button").off("click").on("click", function () {
                    $agreementTextPanel.modal('hide');

                    var model = {};
                    model.avatar = Images.getResizeUrl(registrator.user.avatar, "c50"),
                    model.user = registrator.user;
                    model.level = registrator.level;
                    $('#registrator-details').html(Mustache.to_html($('#registrator-details-template').html(), model));

                    this.registrator = registrator.user;
                    $('#create-request-button').click($.proxy(function () {
                        $.radomJsonPost("/registrator/createRequest", {
                            registratorId: this.registrator.id
                        }, $.proxy(function (data) {

                            if(data.result = "success"){
                                $(radomEventsManager).trigger("registrationRequest.createRequest");
                                $("div#create-registration-request-modal").modal("hide");

                                // Дательный падеж
                                var rn = new RussianName(this.fullName);
                                var fullNameDat = rn.fullName(rn.gcaseDat);

                                bootbox.dialog({
                                    closeButton: false,
                                    message: "Заявка на идентификацию успешно передана Регистратору " + fullNameDat + ". " +
                                    "Если Вы подали заявку по ошибке или же передумали ее подавать, " +
                                    "Вы можете удалить её на странице Вашего профиля (кнопка расположена под Вашей фотографией).",
                                    buttons: {
                                        success:{
                                            label: "ОК",
                                            callback: function(){
                                                window.location.href = '${sharer.link}';
                                            }
                                        }
                                    }
                                });
                            }
                        }, this.registrator));
                    }, this));
                    $("div#create-registration-request-modal").modal("show");
                });

                $agreementTextPanel.modal('show');
            });
        },
        init: function(){

        }
    };

    CreateCertificationRequestDialog.init();
</script>
<div id="licenseAgreementModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Соглашение по идентификации физического лица</h4>
            </div>
            <div class="modal-body" id="modalAgreementText">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-dismiss" data-dismiss="modal">Закрыть</button>
                <button type="button" id="acceptAgreementButton" class="btn btn-success">Принять</button>
            </div>
        </div>

    </div>
</div>