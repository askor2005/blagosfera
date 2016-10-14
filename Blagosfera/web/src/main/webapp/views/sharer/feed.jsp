<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
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



<style type="text/css">

div.tooltip {
	display: block;
}

</style>

<script type="text/javascript">

	var lastLoadedNewsId = null;
	var newsEditor = null;
	var newsFilter = null;
	
	$(document).ready(function() {

		//Максимальное число картинок и видео в новости
		var maxCountOfAttachments = parseInt('${radom:systemParameter("news.max-count-of-attachments", "10")}');
		//Число новостей, подгружающихся при загрузке страницы
		var defaultNewsCount = parseInt('${radom:systemParameter("news.items.default-count", "10")}');
		//Число новостей для динамической подгрузки во время прокрутки страницы.
		var countOfLazyDownloadableNews = parseInt('${radom:systemParameter("news.items.count-of-lazy-downloadable", "5")}');
		//Число непромотанных новостей, при котором происходит загрузка новой порции котента
		var countOfNotScrolledNewsBeforeDownloading = parseInt('${radom:systemParameter("news.items.count-of-not-scrolled-items-before-downloading", "3")}');
		//Максимальное число тегов в новости
		var tagsMaxCount = parseInt('${radom:systemParameter("news.tags.max-count", "10")}');

		var feedFirstPage = null;

		reloadScrollListener();


		$("a#create-news-link").click(function(event) {
			//Инициализация компонента редактирования текста
			var $newsEditorContainer = $('<div style="display: none; width: 100%;"><hr /></div>');
			$newsEditorContainer.insertBefore($('#content-delimiter'));
			if (newsEditor) {
				newsEditor.destroy();
			}
			newsEditor = new NewsEditor(
					$newsEditorContainer,
					null,
					{
						attachmentsEditor: {
							maxCountOfAttachments: maxCountOfAttachments,
						},
						categoryEditor: true,
						textEditor: {
							options: {
								approvedElements: ['p', 'span', 'strong', 'em', 'li', 'ul', 'ol', 'i', 'b', 'a'],
								toolbar: 'fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist'
							}
						},
						tagEditor: {
							options: {
								url: '/news/tags.json',
								maxTags: tagsMaxCount
							}
						},
						submitCallback: function (editor, news) {
							$.radomJsonPost(
									//url
									"/news/create/sharer.json",
									//data
									JSON.stringify(news),
									//callback
									function (response) {

										if (response.result && response.result == "error") {

											if (response.message) {
												bootbox.alert(response.message);
											} else {
												bootbox.alert("Произошла ошибка!");
											}

											editor.setDisabled(false);
											return;
										}

										showNewsListItemMarkup(response, $("div.news-list"), true);
										editor.destroy();
									},
									//errorCallback
									function (response) {
										if (response.message) {
											bootbox.alert(response.message);
										} else {
											bootbox.alert("Произошла ошибка!");
										}
										editor.setDisabled(false);
									}, {
										contentType: "application/json"
									}
							);
						},
						cancelCallback: function () {
							$(event.target).css('display', '');
						}
					}
			);
			$(event.target).css('display', 'none');
			newsEditor.$container.css('display', '');

			return false;
		});
		
		$(radomEventsManager).bind("news.create", function(event, news) {
			showNewsListItemMarkup(news, $("div.news-list"), true, (news.scopeType == "COMMUNITY"));
		});

		$(radomEventsManager).bind("news.edit", function(event, news) {
			replaceNewsListItemMarkup(news);
		});
		
		$(radomEventsManager).bind("news.delete", function(event, news) {
			deleteNewsListItemMarkup(news);
		});


		var defaultFilterData = {
				authorId: null,
				categoryId: null,
				dateFrom: null,
				dateTo: null
				};

		//Пытаемся загрузить информацию по фильтру пользователя
		$.radomJsonGet(
				//url
				"/news/filterData.json",
				//data
				null,
				//callback
				function(response) {

					//Обрабатываем ошибки
					if (response.result && response.result == "error") {
						if (response.message) {
							console.log(response.message);
						} else {
							console.log("ajaxError");
						}

						renderNewsFilter(defaultFilterData);
						return;
					}

					//Рендерим загруженный фильтр
					renderNewsFilter(response);

				},
				//errorCallback
				function(response) {
					if (response.message) {
						console.log(response.message);
					} else {
						console.log("ajaxError");
					}

					renderNewsFilter(defaultFilterData);
				}
		);

		function reloadScrollListener() {
			lastLoadedNewsId = null;
			feedFirstPage = null;
			$('.news-list').html('');
			ScrollListener.init("/news/feed.json", "get", function() {
				var params = {};
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
					showNewsListItemMarkup(news, $("div.news-list"), false, (news.scopeType == "COMMUNITY"));
				});

				var newsCount = $('.news-item').length;

				if (newsCount > 0) {
					if (newsCount >= countOfNotScrolledNewsBeforeDownloading && !ScrollListener.$threshold) {
						ScrollListener.$threshold = $($('.news-item')[newsCount - countOfNotScrolledNewsBeforeDownloading]);
					} else {
						ScrollListener.$threshold = $($('.news-item')[0]);
					}
				}

				$("div.list-loader-animation").fadeOut();
			}, null, feedFirstPage);
		}

		function renderNewsFilter(filterData) {
			//Создаем новостной фильтр в начале правого меню
			var $rightMenu = $('#right-menu-height-wrapper');
			var $newsFilterParentContainer = $('<div></div>');
			$rightMenu.prepend($newsFilterParentContainer);

			newsFilter = new NewsFilter($newsFilterParentContainer, {
				subscribesList: {
					url: "/newsSubscribe/sharers",
					communityId: null
				},
				dateWidth : 115,
				categoryList: true,
				dateFilter: true,
				tagFilter: {
					options: {
						url: '/news/tags.json',
						maxTags: tagsMaxCount
					}
				},
				data: filterData,
				acceptCallback: function(data) {
					data.communityId = null;
					$.radomJsonPost(
							//url
							"/news/saveFilter.json",
							//data
							JSON.stringify(data),
							function(response) {
								if (response.result == "success") {
									reloadScrollListener();
									return;
								}

								if (response.result == "error") {
									if (response.message) {
										bootbox.alert(response.message);
									} else {
										bootbox.alert("Произошла ошибка!")
									}
								}
							},
							//errorCallback
							function (response) {
								if (response.message) {
									bootbox.alert(response.message);
								} else {
									bootbox.alert("Произошла ошибка!");
								}
							}, {
								contentType: "application/json"
							}
					);
				}
			});
		};

	});
</script>

<t:insertAttribute name="newsListItem" />
<t:insertAttribute name="newsDialog" />

<h2>
	Лента новостей
	<a class="btn btn-primary pull-right" id="create-news-link">Добавить новость</a>
</h2>

<hr id="content-delimiter" />
<div class="news-list"></div>
<div class="row list-loader-animation" style="display: block;"></div>