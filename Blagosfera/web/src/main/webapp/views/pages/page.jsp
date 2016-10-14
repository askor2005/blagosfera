<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<div id="target">
</div>
<script id="template" type="x-tmpl-mustache">
{{#hasEditPermission}}
	<hr/>
	<div class="row">
	<div class="col-xs-9">
	{{^hasEditions}}
		Данная страница не редактировалась никогда
    {{/hasEditions}}
	{{#hasEditions}}
		Данная страница редактировалась <a onclick="return false" tabindex="0" role="button" href="#" id="editions-count-link">{{page.editionsCount}} {{timesWord}} </a>
	{{/hasEditions}}
	</div>
	<div class="col-xs-3 text-right">
		{{#hasCurrentEditor}}
			<small class="text-muted">В данный момент страницу редактирует {{currentEditorName}}</small>
		{{/hasCurrentEditor}}
		{{^hasCurrentEditor}}
			<a href="{{pageEditLink}}">Редактировать</a>
		{{/hasCurrentEditor}}
	</div>
	</div>
	<hr/>
{{/hasEditPermission}}
	{{{page.content}}}
</script>
<script type="text/javascript">

	$(document).ready(function() {
	var pageId = ${pageId};
	$.radomJsonGet("/blagosfera/page/static/get/"+pageId, {}, function(response) {
					var page = response.page;
					for (var i = 0;i<response.editions.length;++i) {
						response.editions[i].date = dateFormat(response.editions[i].date,'dd-mm-yyyy HH:mm');
					}
					var hasEditions = response.editions.length > 0;
					var hasEditPermission = response.hasEditPermission;
					var hasCurrentEditor = (response.currentEditor != null);
					var currentEditorName = response.currentEditor;
					var pageEditLink = response.pageEditLink;
					var timesWord = response.timeWord;
					var template = $('#template').html();
					Mustache.parse(template);
					var rendered = Mustache.render(template, {hasEditions : hasEditions,page: page,hasEditPermission : hasEditPermission,
						hasCurrentEditor : hasCurrentEditor, currentEditorName:  currentEditorName, pageEditLink : pageEditLink,timesWord : timesWord});
					$('#target').html(rendered);
					var editionTemplate = "{{#editions}}{{date}} {{editorShortName}} <br/>{{/editions}}";
					Mustache.parse(editionTemplate);
					var editionsContent = Mustache.render(editionTemplate,{editions : response.editions});
					$('#editions-count-link').popover({
						animation : true,
						container : "body",
						title : "Изменения страницы",
						content : editionsContent,
						html : true,
						placement : "bottom",
						trigger : "focus"
					});

	})
	});
	</script>

