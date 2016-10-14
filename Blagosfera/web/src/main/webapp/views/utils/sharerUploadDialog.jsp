<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="sharer-upload-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog" style="width: 300px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Загрузка фотографии</h4>
			</div>
			<div class="modal-body">
				<input type="hidden" id="profile-id" value="${profile.id}" />

				<div class="form-group">
					<button id="show-upload-from-computer-dialog-button" type="button" class="btn btn-primary" style="display: block; width: 100%; white-space: normal;">Выбрать файл</button>
				</div>
				<div class="form-group">
					<button id="show-upload-from-url-dialog-button" type="button" class="btn btn-primary" style="display: block; width: 100%;">Указать URL</button>
				</div>
				<div class="form-group">
					<button id="show-upload-from-webcam-dialog-button" type="button" class="btn btn-primary" style="display: block; width: 100%;">Сделать снимок</button>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default center-block" id="cancel-button">Закрыть</button>
			</div>
		</div>
	</div>
</div>