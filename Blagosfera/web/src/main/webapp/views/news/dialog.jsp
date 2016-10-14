<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="create-news-modal" tabindex="-1" role="dialog" aria-labelledby="create-news-label" aria-hidden="true" data-keyboard="false"  data-backdrop="static">
	<div class="modal-dialog" style="width : 940px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title" id="create-news-label"></h4>
      		</div>
      		<div class="modal-body">
      			<div class="form-group">
      				<label>Заголовок новости</label>
      				<input type="text" placeholder="Заголовок новости" class="form-control" id="news-title" name="news-title" />
      				<span class="help-block">Поле не обязательно для заполнения</span>
      			</div>
   				<textarea class="form-control" id="news-text" name="news-text"></textarea>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
				<button type="button" class="btn btn-primary" id="apply-button"></button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function(){
		var height = $(window).height() - 450;
		if (height < 200) {
			height = 200;
		}

		//Максимальное число картинок и видео в новости
		var maxCountOfAttachments = parseInt('${radom:systemParameter("news.max-count-of-attachments", "10")}');
		//Число картинок в одной строчке коллажа новости
		var imagesPerRowInCollage = parseInt('${radom:systemParameter("news.collage.images-per-row", "3")}');

		$("#create-news-modal textarea").css("height", height + "px").radomTinyMCE({
			collage: true,
			maxCountOfAttachments: maxCountOfAttachments,
			imagesPerRowInCollage: imagesPerRowInCollage
		});

	});
	
	function showNewsDialog(news, callback) {
		
		var $modal = $("#create-news-modal");
		
		$modal.changesChecker();
		
		var $header = $modal.find("h4");
		var $applyButton = $modal.find("#apply-button");
		var $title = $modal.find("#news-title");
		var $text = $modal.find("#news-text");
		if (news) {
			$header.html("Редактирование новости");
			$applyButton.html("Сохранить");
			$title.val(news.title);
			$text.val(news.text);
		} else {
			$header.html("Создание новости");
			$applyButton.html("Создать");
			$title.val("");
			$text.val("");			
		}
		
		$applyButton.off("click");
		
		$applyButton.on("click", function() {
			var data = {};
			data.title = $("#create-news-modal input#news-title").val();
			data.text = $("#create-news-modal textarea#news-text").val();
			
			data.text = RadomUtils.replaceLinks(data.text);
			
			if (news) {
				data.id = news.id;
			}
			if (callback) {
				callback(data);
			}
			return false;
		});
		
		$modal.off("hide.bs.modal");
		$modal.on("hide.bs.modal", function(event) {
			if ($modal.changesChecker("check")) {
				event.preventDefault();
				bootbox.confirm("Новость не сохранена. Подтвердите закрытие диалога", function(result) {
					if (result) {
						hideNewsDialog();
					}
				});
			} else {
				$modal.changesChecker("destroy");
			} 
				
		});

		$modal.off("shown.bs.modal");
		$modal.on("shown.bs.modal", function() {
			tinyMCE.activeEditor.fire('change');
		});

		$modal.modal("show");
		//tinyMCE.activeEditor.fire('change');
	}
	
	function hideNewsDialog() {
		var $modal = $("#create-news-modal");
		$modal.changesChecker("destroy");
		$modal.modal("hide");	
	}
</script>