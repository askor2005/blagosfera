<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<div id="helpPageTarget">
</div>
<script id="helpPageTemplate" type="x-tmpl-mustache">
<h1>
	{{#currentHelpSectionExists}}Список подразделов раздела {{currentHelpSection.name}}{{/currentHelpSectionExists}}
	{{^currentHelpSectionExists}}Список корневых разделов{{/currentHelpSectionExists}}
</h1>
<hr/>
	<a href="#" id="create-section-link" class="btn btn-primary">
		{{#currentHelpSectionExists}}Создать подраздел{{/currentHelpSectionExists}}
		{{^currentHelpSectionExists}}Создать корневой раздел{{/currentHelpSectionExists}}
	</a>
<hr/>

<table class="table">

	<thead>
		<tr>
			<th>Имя</th>
			<th class="text-right">Действия</th>
		</tr>
	</thead>

	<tbody>
		{{#children}}
			<tr>
				<td><a href="/admin/help?parent_id={{id}}">{{name}}</a></td>
				<td class="text-right">
					<a href="/help/{{name}}" target="_blank" class="btn btn-xs btn-default">Открыть</a>
					<a href="/admin/help/edit/{{id}}" class="btn btn-xs btn-primary">Редактировать</a>
					<a href="#" class="publish-section-link btn btn-xs btn-info" data-section-id="{{id}}" {{#published}}style="display : none;"{{/published}}>Опубликовать</a>
					<a href="#" class="unpublish-section-link btn btn-xs btn-warning" data-section-id="{{id}}" {{^published}}style="display : none;"{{/published}} >Скрыть</a>
					<a href="#" class="delete-section-link btn btn-xs btn-danger" data-section-id="{{id}}">Удалить</a>
				</td>
			</tr>
		{{/children}}
	</tbody>

</table>
</script>

<script type="text/javascript">
	$(document).ready(function() {
		var initPage = function() {
			$("table.table").fixMe();

			$("a#create-section-link").click(function() {
				bootbox.prompt("Введите имя раздела", function(result) {
					$.radomJsonPost("/admin/help/create.json", {
						parent_id : currentHelpSectionId,
						name : result
					}, function(response) {
						window.location.reload();
					});
				});
				return false;
			});

			$.each($("a.publish-section-link"), function(index, link) {
				var $link = $(link);
				var sectionId = $link.attr("data-section-id");
				$link.click(function() {
					$.radomJsonPost("/admin/help/publish.json", {
						id : sectionId
					}, function() {
						$link.hide();
						$("a.unpublish-section-link[data-section-id=" + sectionId + "]").show();
					});
					return false;
				});
			});

			$.each($("a.unpublish-section-link"), function(index, link) {
				var $link = $(link);
				var sectionId = $link.attr("data-section-id");
				$link.click(function() {
					$.radomJsonPost("/admin/help/unpublish.json", {
						id : sectionId
					}, function() {
						$link.hide();
						$("a.publish-section-link[data-section-id=" + sectionId + "]").show();
					});
					return false;
				});
			});

			$.each($("a.delete-section-link"), function(index, link) {
				var $link = $(link);
				var sectionId = $link.attr("data-section-id");
				$link.click(function() {
					bootbox.confirm("Подтвердите удаление раздела вместе со всеми его подразделами", function(result) {
						if (result) {
							$.radomJsonPost("/admin/help/delete.json", {
								id : sectionId
							}, function() {
								$link.parents("tr").remove();
							});
						}
					});
					return false;
				});
			});
		}
		var currentHelpSectionId = "${currentHelpSectionId}";
		if (currentHelpSectionId != "") {
			$.radomJsonGet(encodeURI("/admin/help/get/" + currentHelpSectionId+"?children=true"), {}, function (response) {
				var currentHelpSection = response.currentHelpSection;
				var children = response.children;
				var template = $('#helpPageTemplate').html();
				Mustache.parse(template);
				var rendered = Mustache.render(template, {
					currentHelpSectionExists: true,
					children: children,
					currentHelpSection: currentHelpSection
				});
				$('#helpPageTarget').html(rendered);
				initPage();

			});
		}
		else {
			$.radomJsonGet(encodeURI("/admin/help/roots"), {}, function (response) {
				var children = response;
				var template = $('#helpPageTemplate').html();
				Mustache.parse(template);
				var rendered = Mustache.render(template, {
					currentHelpSectionExists: false,
					children: children,
				});
				$('#helpPageTarget').html(rendered);
				initPage();

			});

		}


	});

</script>