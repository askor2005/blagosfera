<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<style type="text/css">

	.bootstrap-tagsinput {
		width : 100%;
	}

	.bootstrap-tagsinput .label {
		font-size : 100%;
		font-weight : normal;
	}

</style>
<div id="target">
</div>
<script id="template" type="x-tmpl-mustache">

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
	<a href="#" id="edit" class="btn btn-primary btn-sm">Сохранить изменения</a>
</form>

<hr/>
</script>

<script type="text/javascript">

	var PageEditor = {
			
		pageId : "${pageId}",
			
		edit : function() {
			$.radomJsonPost("/blagosfera/editor/page/" + PageEditor.pageId + "/edit.json", $("form#form").serialize(), function() {
				bootbox.alert("Изменения успешно сохранены");
				$("form#form").changesChecker("refresh");
			});
		},
		
		init : function() {
			$("a#edit").click(function() {
				PageEditor.edit();
				return false;
			});			
			$("textarea#content").radomTinyMCE();
			$("form#form").changesChecker();
		}
		
	};

	$(document).ready(function() {
		var pageId = ${pageId};
		$.radomJsonGet("/blagosfera/editor/page/"+pageId+"/get.json", {}, function(response) {
			var page = response.page;
			var template = $('#template').html();
			Mustache.parse(template);
			var rendered = Mustache.render(template, {page: page});
			$('#target').html(rendered);
			$("input[data-role=tagsinput], select[multiple][data-role=tagsinput]").tagsinput();
			$('.selectpicker').selectpicker();
			PageEditor.init();
		})
	})
	
</script>