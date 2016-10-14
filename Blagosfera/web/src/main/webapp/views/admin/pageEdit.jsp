<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<div id="target">
</div>
<script id="template" type="x-tmpl-mustache">
	{{^hasCurrentEditor}}
		<style type="text/css">

		.bootstrap-tagsinput {
		width : 100%;
		}

		.bootstrap-tagsinput .label {
		font-size : 100%;
		font-weight : normal;
		}

		</style>

		<h1>
		Редактирование страницы
		</h1>
		<hr />


		<form role="form" id="form">
		<div class="form-group">
		<label for="title">Заголовок страницы</label>
		<input name="title" type="text" class="form-control" id="title" placeholder="Метатэг title" value="{{page.title}}" />
		</div>
		<div class="form-group">
		<label for="content">Содержимое страницы</label>
		<textarea name="content" id="content" rows="30" style="font-size : 11px; font-family: monospace;">{{page.content}}</textarea>
		</div>
		<div class="form-group">
		<label for="description">Описание страницы</label>
		<textarea name="description" class="form-control" id="description" rows="5">{{page.description}}</textarea>
		</div>
		<div class="form-group">
		<label for="keywords">Ключевые слова</label>
		<input type="text" name="keywords" class="form-control" id="keywords" data-role="tagsinput" value="{{page.keywords}}"/>
		</div>
		<div class="form-group">
		<label for="accessType">Доступ</label>
		<select id="accessType" name="accessType" class="selectpicker" data-width="100%">
		<option value="ALL" {{#allAccessType}}selected="selected"{{/allAccessType}} >Все пользователи</option>
		<option value="REGISTERED" {{#registeredAccessType}}selected="selected"{{/registeredAccessType}} >Зарегистрированные пользователи</option>
		<option value="VERIFIED" {{#verifiedAccessType}}selected="selected"{{/verifiedAccessType}} >Идентифицированные пользователи</option>
		</select>
		</div>
		<hr/>
		<a href="#" id="edit" class="btn btn-primary btn-sm">Сохранить изменения</a>
		<a href="#" id="publish" class="btn btn-success btn-sm" {{#published}}style="display : none;"{{/published}} >Сделать видимой</a>
		<a href="#" id="unpublish" class="btn btn-danger btn-sm" {{^published}}style="display : none;"{{/published}}>Сделать невидимой</a>
		<a href="{{sectionLink}}" target="_blank" class="btn btn-default btn-sm">Открыть в новом окне</a>
		</form>

		<hr/>
	{{/hasCurrentEditor}}
   {{#hasCurrentEditor}}
		<h4>В данный момент страница редактируется участником {{currentEditorWithPadeg}}</h4>
	{{/hasCurrentEditor}}
</script>
<script type="text/javascript">

	var PageEditor = {

		pageId : "${pageId}",

		editFields : function() {
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/fields.json", $("form#fields-form").serialize(), function() {
				bootbox.alert("Параметры страницы успешно сохранены");
			});
		},

		editContent : function() {
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/content.json", $("form#content-form").serialize(), function() {
				bootbox.alert("Контент страницы успешно сохранены");
			});
		},

		edit : function() {
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit.json", $("form#form").serialize(), function() {
				bootbox.alert("Изменения успешно сохранены");
			});
		},

		publish : function() {
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/publish.json", {}, function() {
				$("a#publish").hide();
				$("a#unpublish").show();
				bootbox.alert("Видимость страницы изменена");
			});
		},

		unpublish : function() {
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/unpublish.json", {}, function() {
				$("a#publish").show();
				$("a#unpublish").hide();
				bootbox.alert("Видимость страницы изменена");
			});
		},

		setCurrentEditor: function() { // Продлить время редактирования
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/setCurrentEditor.json", {}, null, function(error) {
				console.log(error);
			});
		},

		releasePage: function() { // Освободить страницу
			$.radomJsonPost("/admin/page/" + PageEditor.pageId + "/edit/releasePage.json", {}, null, function(error) {
				console.log(error);
			});
		},

		init : function() {
			var self = this;
			setInterval(function(){
				self.setCurrentEditor();
			}, 60000); // 1 раз в минуту

			$(window).bind("beforeunload", function() {
				self.releasePage();
			});

			$("a#edit-fields").click(function() {
				PageEditor.editFields();
				return false;
			});
			$("a#edit-content").click(function() {
				PageEditor.editContent();
				return false;
			});
			$("a#edit").click(function() {
				PageEditor.edit();
				return false;
			});
			$("a#publish").click(function() {
				PageEditor.publish();
				return false;
			});
			$("a#unpublish").click(function() {
				PageEditor.unpublish();
				return false;
			});
			$("textarea#content").radomTinyMCE();
		}

	}
	$(document).ready(function() {
		var pageId = ${pageId};
		$.radomJsonGet("/admin/page/"+pageId+"/get.json?lock=true", {}, function(response) {
			var page = response.page;
			var hasCurrentEditor = (response.currentEditor != null);
			var currentEditorWithPadeg = response.currentEditor;
			var published = response.published;
			var allAccessType = ((response.accessType == null) || (response.accessType == 'ALL'));
			var verifiedAccessType = ((response.accessType != null) && (response.accessType == 'VERIFIED'));;
			var registeredAccessType = ((response.accessType != null) && (response.accessType == 'REGISTERED'));
			var sectionLink = response.sectionLink;
			var template = $('#template').html();
			Mustache.parse(template);
			var rendered = Mustache.render(template, {page: page, hasCurrentEditor : hasCurrentEditor,currentEditorWithPadeg : currentEditorWithPadeg,
				published : published,allAccessType : allAccessType,verifiedAccessType : verifiedAccessType,registeredAccessType : registeredAccessType, sectionLink : sectionLink});
			$('#target').html(rendered);
			$("input[data-role=tagsinput], select[multiple][data-role=tagsinput]").tagsinput();
			$('.selectpicker').selectpicker();
			PageEditor.init();
		})
	})

</script>


