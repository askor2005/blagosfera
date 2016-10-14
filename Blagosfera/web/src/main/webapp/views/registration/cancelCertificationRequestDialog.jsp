<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="cancel-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Отклонить заявку на идентификацию</h4>
      		</div>
      		<div class="modal-body">
                <div id="cancel-request-details"></div>
                <div style="margin-top: 10px;">
                    <div class="form-group" data-required="true" data-message="Для отмены заявки данное поле обязательно к заполнению.">
                        <textarea class="form-control" rows="5" id="cancel-comment" name="cancel-comment" placeholder="Введите причину"></textarea>
                    </div>
                </div>
			</div>
			<div class="modal-footer">
                <div class="form-inline">
                    <button id="cancel-request-button" type="button" class="btn btn-default pull-left">Отклонить заявку</button>
                    <button type="button" class="btn btn-default pull-right" data-dismiss="modal">Закрыть</button>
                </div>
			</div>
		</div>
	</div>
</div>

<script id="cancel-request-details-template" type="x-tmpl-mustache">
    <div class="registration-dialog-level">Дата подачи заявки: {{request.createdDate}} в {{request.createdTime}}</div>
    <div class="tooltiped-avatar" data-sharer-ikp="{{object.ikp}}" data-data="{{object.fullName}}" style="cursor:pointer;">
        <img src="{{avatar}}" class="img-thumbnail"/>
        <a href="{{object.link}}" style="padding-left: 10px;" class="registration-dialog-registrator-name">{{object.fullName}}</a>
    </div>
    <div class="alert alert-info" role="alert" style="margin-top: 10px;">
        Для отклонения заявки, необходимо указать причину и нажать кнопку <b>Отклонить заявку</b>, пользователь получит уведомление об отклонении заявки.
    </div>
</script>

<script type="text/javascript">
    function checkFormGroupCommon($div) {
        $div.removeClass("has-error").removeClass("has-warning").removeClass("has-feedback");
        var $input = $div.find("input, select, textarea");

        if ($div.attr("data-required") == "true") {
            if (!$input.val()) {
                //$div.append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
                $div.addClass("has-error").addClass("has-feedback");
                $div.radomTooltip({
                    title : $div.attr("data-message") | "Поле обязательно для заполнения.",
                    placement : "top",
                    container : "body"
                });
            }
        }
    }

    var CancelCertificationRequestDialog = {
        requestId: null,
        show: function (request) {
            var model = {};
            model.avatar = Images.getResizeUrl(request.object.avatar, "c50"),
            model.object = request.object;
            model.request = request;
            this.requestId = request.id;
            $('#cancel-comment').val("");
            $('#cancel-request-details').html(Mustache.to_html($('#cancel-request-details-template').html(), model));
            $.each($("div.form-group"), function(index, div) {
                var $div = $(div);
                checkFormGroupCommon($div);
            });
            $("div#cancel-request-modal").modal("show");
        },
        init: function(){
            $('#cancel-request-button').click($.proxy(function(){
                if($("#cancel-comment").is(":blank")){
                    $('#cancel-comment').show();
                    $('#cancel-comment').focus();
                    return;
                }
                $.radomJsonPost("/registrator/cancelRequest", {
                    requestId: this.requestId,
                    comment: $('#cancel-comment').val()
                }, function(data){
                    if(data.result = "success") {
                        $("div#cancel-request-modal").modal("hide");
                        bootbox.alert("Заявка на идентификацию успешно отклонена");
                        $(radomEventsManager).trigger("registrationRequest.updateList");
                    }

                });
            }, this));
            $.each($("div.form-group"), function(index, div) {
                var $div = $(div);
                checkFormGroupCommon($div);
                var $input = $div.find("input, select, textarea");
                $input.keyup(function() {
                    checkFormGroupCommon($div);
                });
                $input.click(function() {
                    checkFormGroupCommon($div);
                });
                $input.change(function() {
                    checkFormGroupCommon($div);
                });
                $input.blur(function() {
                    checkFormGroupCommon($div);
                });
            })
        }
    };

    CancelCertificationRequestDialog.init();
	
</script>