<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="upload-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title"></h4>
      		</div>
      		<div class="modal-body">

				<p id="description"></p>
				<p>Допустимые форматы файлов: <span id="extensions"></span></p>
				<!-- for crop image -->
                <!-- <div id="crop-img">
                    <img id='crop-target'/>
                    <button class="btn btn-default" id='release'>Убрать выделение</button>
                    <button class="btn btn-primary" id='crop'>Обрезать</button>
                </div> -->
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
				<button type="button" class="btn btn-primary" id="upload-button">Выбрать файл для загрузки</button>
			</div>
		</div>
	</div>
</div>