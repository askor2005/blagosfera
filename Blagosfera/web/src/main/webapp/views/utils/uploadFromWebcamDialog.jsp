<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="upload-from-webcam-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog" style="width: 700px; transform:none !important;">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Снимок</h4>
      		</div>
      		<div class="modal-body">
				<div class="form-group" id="camera-status-error" style="display: none;">
					<p class="text-center">Камера не обнаружена</p>
				</div>

				<div id="camera-main">
					<div class="form-group">
						<div class="row">

							<div class="col-xs-6" >
								<%--<video id="webcam_stream" autoplay width="320" height="240" style="width: 320px; height: 240px; outline: 1px solid black;outline-offset: 1px;"></video>--%>
								<div id="webcam_stream" style="width: 320px; height: 240px; outline: 1px solid black;outline-offset: 1px;"></div>
							</div>

							<div class="col-xs-6" style="width: 320px; height: 240px; outline: 1px solid black; outline-offset: 1px; padding: 0px; margin-left: 15px;">
								<img id="canvas_snapshot" style="width: 320px; height: 240px;"></img>
							</div>

						</div>
					</div>

					<div class="form-group">
						<button id="take-snapshot-button" class="btn btn-primary center-block">Сделать снимок</button>
					</div>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="cancel-button">Закрыть</button>
				<button type="button" class="btn btn-primary" id="next-button">Далее</button>
			</div>
		</div>
	</div>
</div>