<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<div id="helpTarget">
</div>
<script id="helpTemplate" type="x-tmpl-mustache">
{{#admin}}
	<hr/>
		<div class="row">
			<div class="col-xs-9">
				{{^hasEditions}}
					Данная страница не редактировалась никогда
				{{/hasEditions}}
				{{#hasEditions}}
					Данная страница редактировалась <a onclick="return false" tabindex="0" role="button" href="#" id="editions-count-link">{{editionsCount}} {{timesWord}} </a>
				{{/hasEditions}}
			</div>
			<div class="col-xs-3 text-right">
				<a href="/admin/help/edit/{{currentHelpSection.id}}">Редактировать</a>
			</div>
		</div>
	<hr/>

 {{/admin}}


	{{#adminOrPublished}}
		{{{page.content}}}
	{{/adminOrPublished}}
	{{^adminOrPublished}}
		Раздел справки не опубликован.
	{{/adminOrPublished}}
</script>

<script type="text/javascript">
	var initChildrenHelpSections = function(children) {
		var template = $('#helpChildrenTemplate').html();//все это см в файле sharerRightSlidebar.jsp
		Mustache.parse(template);
		var rendered = Mustache.render(template, {children : children});
		$('#helpChildren').html(rendered);
	}
	$(document).ready(function() {
		var helpName = "${helpName}";
		$.radomJsonGet(encodeURI("/help/"+helpName+"/get.json"), {}, function(response) {
			var admin = response.admin;
			var timesWord = response.timesWord;
			var page = response.page;
			var currentHelpSection = response.currentHelpSection;
			var template = $('#helpTemplate').html();
			var children = response.children;
			var adminOrPublished = currentHelpSection.published || admin;
			var editionsCount = response.editionsCount;
			var hasEditions = editionsCount > 0;
			Mustache.parse(template);
			var rendered = Mustache.render(template, {editionsCount : editionsCount,hasEditions : hasEditions,page: page,timesWord : timesWord, admin : admin,currentHelpSection : currentHelpSection,adminOrPublished : adminOrPublished});
			$('#helpTarget').html(rendered);
			var editions = response.editions;
			if ((editions != null) && (admin)) {
				for (var i = 0;i<editions.length;++i) {
					editions[i].date = dateFormat(editions[i].date,'dd-mm-yyyy HH:mm');
				}
				var editionTemplate = "{{#editions}}{{date}} {{editorShortName}} <br/>{{/editions}}";
				Mustache.parse(editionTemplate);
				var editionsContent = Mustache.render(editionTemplate, {editions: editions});
				$('#editions-count-link').popover({
					animation: true,
					container: "body",
					title: "Изменения страницы",
					content: editionsContent,
					html: true,
					placement: "bottom",
					trigger: "focus"
				});
			}
			if (children.length > 0) {
				initChildrenHelpSections(children);
			}
		});
	});

</script>