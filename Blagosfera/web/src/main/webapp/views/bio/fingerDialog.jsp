<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="finger-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Подтверждение действия<span class="fingerprint-mode"> отпечатком пальца</span></h4>
      		</div>
      		<div class="modal-body">
      			
      			<div class="alert alert-info" role="alert" style="position : relative;">
  		
				</div>

				<div class="form-group fingerprint-mode">
					<a href="/finger/instruction">Инструкция</a> по работе с сервером авторизации Благосферы.
				</div>
				
				<div id="remote-service-group" style="display : none;">
					<div class="form-group">
						<div class="input-group">
							<input class="form-control" id="remote-service-address" type="text" placeholder="Адрес сервера авторизации" />
							<span class="input-group-btn">
       							<button class="btn btn-primary" id="remote-service-button"><i class="fa fa-plug"></i> Подключиться</button>
							</span>
						</div>
					</div>
					<div class="form-group">
						<button class="btn btn-default btn-block" id="local-service-button"><i class="fa fa-plug"></i> Подключиться к локальному серверу</button>
					</div>			
				</div>

                <div class="form-group" style="display: none;" id="sms-verification-group">
                    <div class="input-group">
                        <span class="input-group-addon" style="min-width: 100px;">
                            <i class="fa fa-clock-o"></i>
                            <span id="sms-seconds-left"></span>
                        </span>

                        <input class="form-control" id="sms-code-input" type="number" maxlength="6" placeholder="смс-код"/>

                        <span class="input-group-btn">
                            <button class="btn btn-primary" id="sms-code-confirm"></i>OK</button>
                        </span>
                    </div>
                </div>
			</div>
			<div class="modal-footer" style="min-height: 105px;">
                <div class="progress">
                    <div class="progress-bar progress-bar-info" role="progressbar"></div>
                </div>

                <button type="button" class="btn btn-warning" id="stop-get-info-button" style="display: none;"><i class="glyphicon glyphicon-remove"></i>Отмена</button>
                <button type="button" class="btn btn-warning" id="stop-read-finger-button" style="display: none;"><i class="glyphicon glyphicon-remove"></i>Отмена</button>
                <button type="button" class="btn btn-warning" id="stop-sms-verification-button" style="display: none;"><i class="glyphicon glyphicon-remove"></i>Отмена</button>
				<button type="button" class="btn btn-info" id="retry-button" style="display: none;"><i class="glyphicon glyphicon-repeat"></i>Повторить</button>
				<button type="button" class="btn btn-warning" id="close-button" style="display: none;"><i class="glyphicon glyphicon-remove"></i>Закрыть</button>
			</div>
		</div>
	</div>
</div>

<script id="dialog-message-template" type="x-tmpl-mustache">
	<div class="row">
		<div class="col-xs-2">
			<img class="img-thumbnail" src="{{avatar}}"/>
		</div>
		<div class="col-xs-10">
			{{{text}}}
		</div>
	</div>
</script>