<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="upload-from-computer-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Выбор изображения</h4>
      		</div>
      		<div class="modal-body">

				<div class="form-group">
					<form id="file-to-upload-form" enctype="multipart/form-data">
						<p>Минимальный размер фотографии для загрузки: <span id="min-photo-size"></span> пикселей</p>
						<p>Максимальный размер фотографии для загрузки: <span id="max-photo-size"></span> пикселей</p>
						<p>Максимальный объем файла: <span id="max-file-size"></span></p>
						<p>Допустимые форматы файлов: <span id="extensions"></span></p>
						<input id="file-to-upload" type="file" name="fileToUpload" accept="image/*">
						<div id="upload-progress">
							<br>
							<span>Загрузка...</span>
						</div>
					</form>
				</div>

				<div class="form-group">
					<img id="slide-image" style="max-width: 100%; max-height: 254px;">
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Закрыть</button>
				<button type="button" class="btn btn-primary" id="next-button">Далее</button>
			</div>
		</div>
	</div>
</div>