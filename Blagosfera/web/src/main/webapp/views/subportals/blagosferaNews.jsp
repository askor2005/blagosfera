<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<spring:url value="/css/jquery/jquery.mCustomScrollbar.css" var="customScrollbarCss" />
<link rel="stylesheet" type="text/css" href="${customScrollbarCss}"/>
<spring:url value="/css/jquery/jquery.tag-editor.css" var="tagEditorCss" />
<link rel="stylesheet" type="text/css" href="${tagEditorCss}"/>
<spring:url value="/css/news/attachments-editor.css" var="attachmentsEditorCss" />
<link rel="stylesheet" type="text/css" href="${attachmentsEditorCss}"/>


<spring:url value="/js/jquery/jquery.tag-editor.min.js" var="tagEditorJS" />
<script type="text/javascript" src="${tagEditorJS}"></script>
<spring:url value="/js/rameraListEditor/rameraListEditor.js" var="rameraListEditorJs" />
<script type="text/javascript" src="${rameraListEditorJs}"></script>
<spring:url value="/js/jquery/jquery.mCustomScrollbar.concat.min.js" var="customScrollbarJs" />
<script type="text/javascript" src="${customScrollbarJs}"></script>
<spring:url value="/js/news/components/attachments-editor.js" var="attachmentsEditorJs" />
<script type="text/javascript" src="${attachmentsEditorJs}"></script>
<spring:url value="/js/news/components/category-editor.js" var="categoryEditorJs" />
<script type="text/javascript" src="${categoryEditorJs}"></script>
<spring:url value="/js/news/components/text-editor.js" var="newsTextEditorJs" />
<script type="text/javascript" src="${newsTextEditorJs}"></script>
<spring:url value="/js/news/components/tags-editor.js" var="radomTagEditorJS" />
<script type="text/javascript" src="${radomTagEditorJS}"></script>
<spring:url value="/js/news/components/news-editor.js" var="newsEditorJs" />
<script type="text/javascript" src="${newsEditorJs}"></script>
<spring:url value="/js/news/components/filter/subscribes-list.js" var="subscribesListJs" />
<script type="text/javascript" src="${subscribesListJs}"></script>
<spring:url value="/js/news/components/filter/category-filter.js" var="categoryFilterJs" />
<script type="text/javascript" src="${categoryFilterJs}"></script>
<spring:url value="/js/news/components/filter/date-filter.js" var="dateFilterJs" />
<script type="text/javascript" src="${dateFilterJs}"></script>
<spring:url value="/js/news/components/filter/news-filter.js" var="newsFilterJs" />
<script type="text/javascript" src="${newsFilterJs}"></script>

<script type="text/javascript">
	var newsEditor = null;
	var allowEdit = false;
	<security:authorize access="hasRole('ROLE_BLAGOSFERA_NEWS_WRITER')">
	allowEdit = true;
	</security:authorize>
	var communityId = "${radom:getEditorsCommunity().id}";
	var lastLoadedNewsId = null;
	var newsFirstPage = null;
    var defaultCategoryId="${defaultCategoryId}";
	$(document).ready(function() {


		//Число новостей, подгружающихся при загрузке страницы
		var defaultNewsCount = parseInt('${radom:systemParameter("news.items.default-count", "10")}');
		//Число новостей для динамической подгрузки во время прокрутки страницы.
		var countOfLazyDownloadableNews = parseInt('${radom:systemParameter("news.items.count-of-lazy-downloadable", "5")}');
		//Число непромотанных новостей, при котором происходит загрузка новой порции котента
		var countOfNotScrolledNewsBeforeDownloading = parseInt('${radom:systemParameter("news.items.count-of-not-scrolled-items-before-downloading", "3")}');

		$("a#create-news-link").click(function() {
			showNewsDialog(null, function(data) {
				/*data.community_id = communityId;
				$.radomJsonPost("/news/create/community.json", data, function(news) {
					showNewsListItemMarkup(news, $("div.news-list"), true);
					$("div.list-not-found").hide();
					hideNewsDialog();
				});*/
				data.scope = {
					id:communityId,
				};
				data.category = {
					id: defaultCategoryId
				};
				$.radomJsonPost(
						//url
						"/news/create/community.json",
						//data
						JSON.stringify(data),
						//callback
						function (news) {

							showNewsListItemMarkup(news, $("div.news-list"), true,false,allowEdit);
							$("div.list-not-found").hide();
							hideNewsDialog();
						},
						//errorCallback
						function (response) {
							bootbox.alert(response.message);
						}, {
							contentType: "application/json"
						}
				);
			});
			return false;
		});
		
		ScrollListener.init("/news/community.json", "get", function() {
			var params = {};
			params.community_id = communityId;

			if (lastLoadedNewsId) {
				params.last_loaded_id = lastLoadedNewsId
				//Новости загружались (используем параметр для динамической подгрузки)
				params.per_page = countOfLazyDownloadableNews;
			} else {
				//Новости еще не загружались (используем параметр по умолчанию)
				params.per_page = defaultNewsCount;
			}

			return params;
		}, function() {
			$("div.list-loader-animation").show();
		}, function(response) {
			$.each(response, function(index, news) {
				if (news.id < lastLoadedNewsId || lastLoadedNewsId == null) {
					lastLoadedNewsId = news.id;
				}
				showNewsListItemMarkup(news, $("div.news-list"),false,false,allowEdit);
			});
			if ($("div.news-item").length == 0) {
				$("div.list-not-found").show();
			} else {
				$("div.list-not-found").hide();

				var newsCount = $('.news-item').length;
				if (newsCount >= countOfNotScrolledNewsBeforeDownloading && !ScrollListener.$threshold) {
					ScrollListener.$threshold = $($('.news-item')[newsCount - countOfNotScrolledNewsBeforeDownloading]);
				} else {
					ScrollListener.$threshold = $($('.news-item')[0]);
				}
			}
			$("div.list-loader-animation").fadeOut();
		}, null, newsFirstPage);
		
		$(radomEventsManager).bind("news.create", function(event, news) {
			if (news.scopeType == "COMMUNITY" && news.scope.id == communityId) {
				showNewsListItemMarkup(news, $("div.news-list"), true,false,allowEdit);
				$("div.list-not-found").hide();
			}
		});

		$(radomEventsManager).bind("news.edit", function(event, news) {
			if (news.scopeType == "COMMUNITY" && news.scope.id == communityId) {
				replaceNewsListItemMarkup(news);
			}
		});
		
		$(radomEventsManager).bind("news.delete", function(event, news) {
			if (news.scopeType == "COMMUNITY" && news.scope.id == communityId) {
				deleteNewsListItemMarkup(news);
				if ($("div.news-item:not([id=news-item-" + news.id + "])").length == 0) {
					$("div.list-not-found").show();
				}
			}
		});
		
	});

</script>

<t:insertAttribute name="newsDialog" />
<t:insertAttribute name="newsListItem" />

<h1>
	Новости системы БЛАГОСФЕРА
	<security:authorize access="hasRole('ROLE_BLAGOSFERA_NEWS_WRITER')">
	<div class="btn-group">
		<a href="#" class="btn btn-primary pull-right" id="create-news-link">Создать новость</a>
	</div>
	</security:authorize>
</h1>
<hr/>
<div class="news-list">
	<div class="row list-not-found" style="display : none;">
		<div class="panel panel-default">
			<div class="panel-body">Еще не добавлено ни одной новости</div>
		</div>
	</div>
</div>
<div class="row list-loader-animation" style="display: block;"></div>