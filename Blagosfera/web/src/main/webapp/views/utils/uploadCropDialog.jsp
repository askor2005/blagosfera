<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="upload-crop-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Выбор миниатюры</h4>
      		</div>
      		<div class="modal-body">
				<div class="form-group">
					<p>Используйте указатель для того чтобы выделить область.</p>
					<p>Для снятия выделения нажмите вне области.</p>
				</div>

				<div class="form-group">
					<div id="crop-img">
						<img id="crop-target"/>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Закрыть</button>
				<button type="button" class="btn btn-primary" id="save-button">Сохранить</button>
			</div>
		</div>
	</div>
</div>