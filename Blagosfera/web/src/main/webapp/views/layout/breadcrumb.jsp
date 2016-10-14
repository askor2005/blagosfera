<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<div id="breadcrumbsDataBlock"></div>
<script id="breadcrumbsTemplate" type="x-tmpl-mustache">
	<div class="bs-example">
	    <ul class="breadcrumb">
	    	{{#breadcrumb}}
				{{#withLink}}
					<li><a href="{{link}}">{{title}}</a></li>
				{{/withLink}}
				{{^withLink}}
					<li><span>{{title}}</span></li>
				{{/withLink}}
			{{/breadcrumb}}
	    </ul>
	</div>
</script>
<script>
	function loadBreadcrumbData(link, callBack) {
		$.radomJsonPost(
				"/breadcrumb.json",
				{
					link : link
				},
				callBack
		);
	}
	function prepareBreadcrumbs(breadcrumbs) {
		if (breadcrumbs != null) {
			for (var i in breadcrumbs) {
				var breadcrumb = breadcrumbs[i];
				breadcrumb.withLink = true;
				if (breadcrumb.link == "#") {
					breadcrumb.withLink = false;
				}
			}
		}
		return breadcrumbs;
	}
	$(document).ready(function() {
		var breadcrumbsTemplate = $("#breadcrumbsTemplate").html()
		Mustache.parse(breadcrumbsTemplate);
		var link = "${uri}";

		<c:if test="${radom:isSharer()}">

		loadBreadcrumbData(link, function(breadcrumbData){
			var breadCrumbs = prepareBreadcrumbs(breadcrumbData);
			var model = {
				breadcrumb : breadCrumbs
			};
			var markup = Mustache.render(breadcrumbsTemplate, model);
			$("#breadcrumbsDataBlock").append(markup);
		});

		</c:if>
	});
</script>
