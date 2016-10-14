<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="view-request-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Детальная информация по заявке на идентификацию</h4>
      		</div>
      		<div class="modal-body">
                <div id="view-request-details"></div>
			</div>
			<div class="modal-footer">
                <div class="form-inline">
                    <button type="button" class="btn btn-default pull-right" data-dismiss="modal">Закрыть</button>
                </div>
			</div>
		</div>
	</div>
</div>

<script id="view-request-details-template" type="x-tmpl-mustache">
    <div class="registration-dialog-level">Дата подачи заявки: {{request.createdDate}} в {{request.createdTime}}</div>
    <div class="tooltiped-avatar" data-sharer-ikp="{{object.ikp}}" data-data="{{object.fullName}}" style="cursor:pointer;">
        <img src="{{avatar}}" class="img-thumbnail"/>
        <a href="{{object.link}}" style="padding-left: 10px;" class="registration-dialog-registrator-name">{{object.fullName}}</a>
    </div>
    <div style="padding-top: 15px;" class="registration-dialog-level">{{statusName}}{{^isNew}}: {{request.updatedDate}} в {{request.updatedTime}}{{/isNew}}</div>
    {{#isCanceled}}
    <div class="registration-dialog-level">Причина:</div>
    <div class="alert alert-success">{{request.comment}}</div>
    {{/isCanceled}}
</script>

<script type="text/javascript">

    var ViewCertificationRequestDialog = {
        show: function (request) {
            var model = {};
            model.avatar = Images.getResizeUrl(request.object.avatar, "c50"),
            model.object = request.object;
            model.request = request;
            model.isNew = (request.status == "NEW");
            model.isCanceled = (request.status == "CANCELED");
            model.statusName = getRequestStatusName(request.status);
            $('#view-request-details').html(Mustache.to_html($('#view-request-details-template').html(), model));
            $("div#view-request-modal").modal("show");
        },
        init: function(){
        }
    };

    ViewCertificationRequestDialog.init();

    function getRequestStatusName(status){
        switch (status){
            case 'NEW': return 'Не обработанная';
            case 'DELETED': return 'Удалена заявителем';
            case 'CANCELED': return 'Отклонена регистратором';
            case 'PROCESSED': return 'Обработана';
        }
        return '';
    }
	
</script>