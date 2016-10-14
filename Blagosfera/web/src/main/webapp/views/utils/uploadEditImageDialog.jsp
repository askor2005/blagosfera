<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style>
	.icon-flipped {
		transform: scaleX(-1);
		-moz-transform: scaleX(-1);
		-webkit-transform: scaleX(-1);
		-ms-transform: scaleX(-1);
	}
	.thumbnail {
		display:table;
		border-spacing: 2px;
		border-collapse: separate;
		border-radius:10px;
	}
	.thumbnail_wrapper {
		display:table-cell;
		vertical-align:middle;
	}
	.thumbnail_wrapper > img {
		max-width:100%;
	}
</style>

<div class="modal fade" id="upload-edit-image-dialog" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
        		<h4 class="modal-title">Редактирование изображения</h4>
      		</div>
      		<div class="modal-body">

				<div id="image-wrapper">
					<div>
						<div class="thumbnail center-block" style="width: 568px; height: 568px">
							<div class="thumbnail_wrapper text-center">
								<img id="editable-image" style="max-width: 548px; max-height: 548px;"/>
							</div>
						</div>
					</div>

					<div class="text-center">
						<div class="btn-group">
							<a id="image-rotate-left" href="#" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-repeat icon-flipped"></span></a>
							<a id="image-rotate-right" href="#" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-repeat"></span></a>
						</div>
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