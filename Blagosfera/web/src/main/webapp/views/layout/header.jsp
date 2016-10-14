<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<c:if test="${not radom:isSharer()}">
	<t:insertAttribute name="anonymousHeader" />		
</c:if>

<c:if test="${radom:isSharer()}">
	<t:insertAttribute name="sharerHeader" />
</c:if>

<script id="rootSectionTemplate" type="x-tmpl-mustache">
	{{#rootSections}}
		{{#visible}}
			{{#published}}
				<li data-section-id="{{id}}" data-help-exists="{{helpExists}}" data-help-published="{{helpPublished}}" {{#active}}class="active"{{/active}} >
				    {{^openInNewLink}}
					<a data-help-section="{{helpLink}}" href="{{link}}" data-subportal="{{name}}" {{#hint}}data-title="{{hint}}"{{/hint}} >{{title}}</a>
					 {{/openInNewLink}}
					  {{#openInNewLink}}
					<a data-help-section="{{helpLink}}" href="#" onclick="window.open('{{link}}', '_blank');" data-subportal="{{name}}" {{#hint}}data-title="{{hint}}"{{/hint}} >{{title}}</a>
					 {{/openInNewLink}}
				</li>
			{{/published}}
		{{/visible}}
	{{/rootSections}}
</script>

<script>
	function loadRootSections(link, callBack) {
		$.radomJsonPost(
				"/sections/current_sections.json",
				{
					link : link
				},
				callBack
		);
	}

	$(document).ready(function(){
		var rootSectionTemplate = $("#rootSectionTemplate").html();
		Mustache.parse(rootSectionTemplate);
		var link = "${uri}";
		loadRootSections(link, function(rootSections){
			var children = null;
			for (var i in rootSections) {
				var rootSection = rootSections[i];
				if (rootSection.children != null) {
					children = rootSection.children;
					break;
				}
			}
			if (window.initLeftSections != null) {
				initLeftSections(rootSection);
			}

			var markup = Mustache.render(rootSectionTemplate, {
				rootSections : rootSections
			});
			$("#ra-menu").append(markup);
		});
	});
</script>