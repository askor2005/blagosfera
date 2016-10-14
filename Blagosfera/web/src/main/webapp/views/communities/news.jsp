<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ include file="member/organizationMembersGrid.jsp"%>


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
	.tooltip-inner {
		max-width: 400px;
	}

	.dl-horizontal dt {
		width: 165px;
	}

	.tooltip-inner {
		max-width: 400px;
	}

	.dl-horizontal dt {
		width: 215px;
		margin-bottom : 10px;
	}

	.dl-horizontal dd {
		margin-left: 220px;
		margin-bottom : 10px;
	}

	.activity-scope {
		-webkit-border-radius: 4px;
		-moz-border-radius: 4px;
		border-radius: 4px;

		border : 1px solid #bce8f1;
		color: #31708f;
		background-color: #d9edf7;

		margin-right: 4px;
		padding: 2px 5px;
		white-space: nowrap;
		margin-bottom: 10px;
		margin-top: -3px;
		display: inline-block;

	}

	div#community-description {
		overflow : hidden;
	}

	pre {
		font-family: helvetica,arial,verdana,sans-serif !important;
		word-break: normal !important;
		white-space: pre-line !important;
	}

</style>

<script type="text/javascript">

	var userId = null;
	var communityId = ${communityId};
	var memberId = null;
	var isCreator = false;
	var maxFieldValueHeight = null;

	var lastLoadedNewsId = null;
	var newsEditor = null;

	var newsFirstPage = null;

	var newsPageTemplate = null;

	function checkDescriptionHeight() {
		var $link = $("a#comunity-description-show");
		var $linkParent = $link.parent();
		var $text = $("div#community-description");
		var $shadow = $("div#community-description-shadow");

		if ($text[0].offsetHeight < $text[0].scrollHeight) {
			$text.css("max-height", maxFieldValueHeight + "px");
			$linkParent.show();
			$shadow.show();
		} else {
			$linkParent.hide();
			$shadow.hide();
		}
	}

	function showDescription() {
		var $link = $("a#comunity-description-show");
		var $linkParent = $link.parent();
		var $text = $("div#community-description");
		var $shadow = $("div#community-description-shadow");

		$text.css("height", $text.height() + "px");
		$text.css("max-height", "none");
		$text.animateAuto("height", 300);
		$linkParent.remove();
		$shadow.remove();
		return false;
	}

	// TODO
	function getCurrentStatusLabel(community, member) {
		if (community.deleted) {
			return "Объединение удалено";
		} else {
			if (!member || !member.status) {
				return "Вы не состоите в " + (community.isRoot ? "объединении" : "группе");
			} else {
				switch (member.status) {
					case "MEMBER":
						if (self.creator) {
							return "Вы состоите в " + (community.isRoot ? "объединении" : "группе");
						} else {
							return "Вы состоите в " + (community.isRoot ? "объединении" : "группе");
						}
					case "REQUEST":
						return "Вы отправили запрос <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + member.requestDate + "'>" + (member.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(member.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
					case "INVITE":
						return "Вы получили приглашение <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + member.requestDate + "'>" + (member.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(member.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
					default:
						return "";
				}
			}
		}
	}

	function setVisible($element, visible) {
		if (visible) {
			$element.show();
		} else {
			$element.hide();
		}
	}

	function refreshButtons(data) {
		var community = data.community;
		var member = data.member;
		memberId = member.id;
		if (data.eventType == "reject_invite" || data.eventType == "cancel_invite" ||
			data.eventType == "exclude" || data.eventType == "leave" ||
			data.eventType == "reject_request" || data.eventType == "cancel_request") {
			member = null;
		}
		var currentStatusLabel = getCurrentStatusLabel(community, member);
        community.open = community.accessType == 'OPEN';
		var canJoin = !community.deleted && ((!member || !member.status) && community.open)/* && (memberDeleted)*/;
		var canRequest = !community.deleted &&  ((!member || !member.status) && !community.open);
		var canAcceptInvite = !community.deleted &&  (member && member.status == "INVITE");
		var canRejectInvite = !community.deleted &&  (member && member.status == "INVITE");
		var canCancelRequest =
				!community.deleted &&  (member &&
						(member.status == "REQUEST" ||
								member.status == "CONDITION_NOT_DONE_REQUEST" ||
								member.status == "CONDITION_DONE_REQUEST" ||
								member.status == "JOIN_IN_PROCESS"
						)
				);
		var canLeave = !community.deleted &&  (member && !member.creator && member.status == "MEMBER");

		var canCancelRequestToLeave = !community.deleted &&  (member && !member.creator &&
				(
						member.status == "REQUEST_TO_LEAVE" ||
						member.status == "LEAVE_IN_PROCESS"
				));

		var canDelete = community.canDelete;

		var $div = $("div#buttons-block");

		setVisible($div.find("a.join-link"), canJoin);
		setVisible($div.find("a.request-link"), canRequest);
		setVisible($div.find("a.accept-invite-link"), canAcceptInvite);
		setVisible($div.find("a.reject-invite-link"), canRejectInvite);
		setVisible($div.find("a.cancel-request-link"), canCancelRequest);
		setVisible($div.find("a.leave-link"), canLeave);
		setVisible($div.find("a.cancel-request-leave-link"), canCancelRequestToLeave);
		setVisible($div.find("a.delete-link"), canDelete);
		$div.find("span#self-status").html(currentStatusLabel);

		$("span.request-distance").radomTooltip({
			placement : "top",
			container : "body"
		});
	}

	function initNewsPage() {
		$("div#community-description").css("max-height", maxFieldValueHeight + "px");

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


		$("a#create-news-link").click(function(event) {
			//Инициализация компонента редактирования новости
			var $newsEditorContainer = $('<div style="display: none"><hr /></div>');
			$newsEditorContainer.insertBefore($('#content-delimiter'));
			if (newsEditor) {
				newsEditor.destroy();
			}
			newsEditor = new NewsEditor(
					$newsEditorContainer,
					null,
					{
						attachmentsEditor: {
							maxCountOfAttachments: maxCountOfAttachments
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
						submitCallback: function (editor, data) {
							data.scope = {
								id:communityId
							};

							$.radomJsonPost(
									//url
									"/news/create/community.json",
									//data
									JSON.stringify(data),
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
			$.scrollTo(newsEditor.$container, 300, {offset: -55});

			return false;
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
				{ communityId: communityId},
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

		reloadScrollListener();

		$(radomEventsManager).bind("news.create", function(event, news) {
			if (news.scopeType == "COMMUNITY" && news.scope.id == communityId) {
				showNewsListItemMarkup(news, $("div.news-list"), true);
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
			}
		});

		$("div#community-description").find("img").load(function() {
			checkDescriptionHeight();
		});

		checkDescriptionHeight();

		$("a#comunity-description-show").click(function() {
			showDescription();
			return false;
		});

		//------------------------------------------------------

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data);
			}
		});

		/*$(radomEventsManager).bind("community-member.join", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.leave", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		// Отмена выхода из объединения
		$(radomEventsManager).bind("community-member.cancel-request-leave", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		// Запрос на выход из объединения
		$(radomEventsManager).bind("community-member.request-to-leave", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			if (data.member.user.id == userId) {
                refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.exclude", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.request", function(event, data) {
			if (data.member.user.id == userId) {
            	refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.accept-invite", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.reject-invite", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member,true);
			}
		});

		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.invite", function(event, data) {
			if (data.member.user.id == userId) {
				refreshButtons(data.community, data.member);
			}
		});*/

		var $div = $("div#buttons-block");

		$div.find("a.join-link").click(function() {
			CommunityFunctions.joinToOpenCommunity(communityId);
			return false;
		});

		$div.find("a.request-link").click(function() {
			CommunityFunctions.createRequestToJoinInCommunity(communityId);
			return false;
		});

		$div.find("a.accept-invite-link").click(function() {
			CommunityFunctions.acceptToJoinToCommunity(memberId);
			return false;
		});

		$div.find("a.reject-invite-link").click(function() {
			CommunityFunctions.rejectInvite(memberId);
			return false;
		});

		$div.find("a.cancel-request-link").click(function() {
			CommunityFunctions.cancelRequest(memberId);
			return false;
		});

		$div.find("a.leave-link").click(function() {
			CommunityFunctions.leaveFromCommunity(memberId);
			return false;
		});

		$div.find("a.cancel-request-leave-link").click(function() {
			CommunityFunctions.cancelRequestToLeaveFromCommunity(memberId);
			return false;
		});

		$div.find("a.delete-link").click(function() {
			function doDelete(comment) {

				$.radomFingerJsonAjax({
					url : "/communities/delete.json",
					data : {
						community_id : communityId,
						comment : comment
					},
					type : "post",
					successMessage : "Удаление завершено",
					errorMessage : "Ошибка удаления",
					successCallback : function(response) {
						window.location = "/groups/all";
					}
				});

			}

			if (isCreator) {
				bootbox.confirm("Подтвердите удаление", function(result) {
					if (result) {
						doDelete();
					}
				});
			} else {
				bootbox.prompt("Укажите причину удаления", function(result) {
					if (result) {
						doDelete(result)
					}
				});
			}

			return false;

		});

		$("span.request-distance").radomTooltip({
			placement : "top",
			container : "body"
		});

		$("#showModalChooseMyCommunities").click(function(){
			$("#modalChooseMyCommunities").modal("show");
		});
		$("#modalChooseMyCommunities").on("shown.bs.modal", function () {
			initOrganizationMembersGrid();
		});
		// Вступить в объединение
		$("body").on("click", ".requestToJoinButton", function(){
			var candidateCommunityId = $(this).attr("candidate_id");
			CommunityFunctions.requestToJoinCommunity(candidateCommunityId, function(){
				organizationMembersStore.load();
			});
		});
		// Выйти из объединения
		$("body").on("click", ".requestToExcludeButton", function(){
			var memberId = $(this).attr("member_id");
			CommunityFunctions.requestFromOrganizationToExcludeCommunity(memberId, function(){
				organizationMembersStore.load();
			});
		});
		// Отменить запрос на вступление в объединение
		$("body").on("click", ".cancelRequestButton", function(){
			var memberId = $(this).attr("member_id");
			CommunityFunctions.cancelRequestCommunity(memberId, function(){
				organizationMembersStore.load();
			});
		});
		// Отменить запрос на выход из объединения
		$("body").on("click", ".cancelRequestToLeaveButton", function(){
			var memberId = $(this).attr("member_id");
			CommunityFunctions.cancelRequestToLeaveCommunity(memberId, function(){
				organizationMembersStore.load();
			});
		});


		function reloadScrollListener() {
			$('.news-list').html('');
			lastLoadedNewsId = null;
			newsFirstPage = null;
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

			}, function(response) {
				$.each(response, function(index, news) {
					if (news.id < lastLoadedNewsId || lastLoadedNewsId == null) {
						lastLoadedNewsId = news.id;
					}
					showNewsListItemMarkup(news, $("div.news-list"));
				});
				if ($("div.news-item").length == 0) {
					$("div.list-not-found").show();
				} else {
					$("div.list-not-found").remove();

					var newsCount = $('.news-item').length;
					if (newsCount >= countOfNotScrolledNewsBeforeDownloading && !ScrollListener.$threshold) {
						ScrollListener.$threshold = $($('.news-item')[newsCount - countOfNotScrolledNewsBeforeDownloading]);
					} else {
						ScrollListener.$threshold = $($('.news-item')[0]);
					}
				}
			}, null, newsFirstPage);
		};

		function renderNewsFilter(filterData) {
			//Создаем новостной фильтр в начале правого меню
			var $newsFilterParentContainer = $('<div></div>');
			$newsFilterParentContainer.insertAfter($('#wall-block'));

			newsFilter = new NewsFilter($newsFilterParentContainer, {
				subscribesList: {
					url: "/newsSubscribe/communityMembers",
					communityId: communityId
				},
				dateWidth : 305,
				authorsSelectWidth : 587,
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
					data.communityId = communityId;
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

			$newsFilterParentContainer.append('</hr>');
		};
	}

	function loadNewsPageData(communityId, callBack) {
		$.radomJsonPost(
				"/communities/news_page_data.json",
				{
					community_id : communityId
				},
				callBack
		);
	}

	$(document).ready(function() {
		$(eventManager).bind("inited", function(event, currentUser) {
			userId = currentUser.id;
			newsPageTemplate = $("#newsPageTemplate").html();
			Mustache.parse(newsPageTemplate);

			loadNewsPageData(communityId, function(newsPageData){
				initCommunityHead(newsPageData.community);
				initCommunityMenu(newsPageData.community);
				var model = initNewsPageModel(newsPageData);
				var markup = Mustache.render(newsPageTemplate, model);
				memberId = newsPageData.community.selfMember != null ? newsPageData.community.selfMember.id : null;
				maxFieldValueHeight = newsPageData.maxFieldValueHeight;
				$("#newsPageDataBlock").append(markup);
				initNewsPage();
			});
		});
	});

	function initNewsPageModel(newsPageData) {
		var model = newsPageData;
		var associationFormInfo = "";
		var accessTypeName = "";
		var isWithOrganization = false;
		switch (newsPageData.community.type) {
			case "COMMUNITY_WITH_ORGANIZATION":
				isWithOrganization = true;
				break;
			case "COMMUNITY_WITHOUT_ORGANIZATION":
				isWithOrganization = false;
				break;
		}

		if (newsPageData.community.associationForm != null) {
			associationFormInfo = newsPageData.community.associationForm.text;
		}

		var isRoot = newsPageData.community.root;
		switch (newsPageData.community.accessType) {
			case 'OPEN':
				accessTypeName = isRoot ? "открытое" : "открытая";
				break;
			case 'CLOSE':
				accessTypeName = isRoot ? "закрытое" : "закрытая";
				break;
			case 'RESTRICTED':
				accessTypeName = isRoot ? "ограниченное" : "ограниченная";
				break;
		}

		model.community.mainOkvedExists = newsPageData.community.mainOkved != null;
		model.community.additionalOkvedsExists =
				newsPageData.community.additionalOkveds != null &&
				newsPageData.community.additionalOkveds.length > 0;

		model.community.associationFormExists = associationFormInfo != null;

		model.community.activityScopesExists =
				newsPageData.community.activityScopes != null && newsPageData.community.activityScopes.length > 0;


		model.community.isWithOrganization = isWithOrganization;
		model.community.associationFormInfo = associationFormInfo;
		model.community.accessTypeName = accessTypeName;
		model.isSelfMember = newsPageData.community.selfMember != null;
		if (model.isSelfMember) {
			model.community.selfMember.statusMember = model.community.selfMember.status == "MEMBER";
			model.community.selfMember.statusRequest = model.community.selfMember.status == "REQUEST";
			model.community.selfMember.statusInvite = model.community.selfMember.status == "INVITE";
			model.community.selfMember.requestDateFormat = dateFormat(new Date(model.community.selfMember.requestDate), "dd.mm.yyyy HH:MM:ss");
		}

		return model;
	}
</script>

<script id="newsPageTemplate" type="x-tmpl-mustache">
	<div id="wall-block">
		<dl class="dl-horizontal" style="margin-bottom : 10px;">
			<dt>Тип объединения</dt>
			<dd>
				{{#community.isWithOrganization}}
					Объединение в рамках юридического лица
				{{/community.isWithOrganization}}
				{{^community.isWithOrganization}}
					Объединение вне рамок юридического лица
				{{/community.isWithOrganization}}
			</dd>

			<dt>Организатор</dt>
			<dd>
				<a href="{{community.creatorLink}}">{{community.creatorFullName}}</a>
			</dd>
			{{#activityScopesExists}}
				<dt>Сфера деятельности</dt>
				<dd>
					{{#community.activityScopes}}
						<a class="activity-scope" style="white-space: normal;" href="/groups/all?activity_scope_id={{id}}">{{text}}</a>
					{{/community.activityScopes}}
				</dd>
			{{/activityScopesExists}}
			{{#community.mainOkvedExists}}
				<dt title="Основоной вид деятельности">Основоной вид деятельности</dt>
				<dd>{{community.mainOkved.code}} {{community.mainOkved.longName}}</dd>
			{{/community.mainOkvedExists}}

			{{#community.additionalOkvedsExists}}
				<dt title="Дополнительные виды деятельности">Дополнительные виды деятельности</dt>
				<dd>
					{{#community.additionalOkveds}}
						{{code}} {{longName}}<br/>
					{{/community.additionalOkveds}}
				</dd>
			{{/community.additionalOkvedsExists}}

			{{#associationFormExists}}
				<dt>Форма объединения</dt>
				<dd>
					{{community.associationFormInfo}}
				</dd>
			{{/associationFormExists}}

			<dt>Доступ к {{#community.root}}объединению{{/community.root}}{{^community.root}}группе{{/community.root}}</dt>
			<dd>
				{{community.accessTypeName}}
			</dd>
			<dt>Информация об Объединении</dt>
			<dd>
				{{#community.isWithOrganization}}
					<a href="{{community.link}}/info" >Смотреть информацию о юридическом лице</a>
				{{/community.isWithOrganization}}
				{{^community.isWithOrganization}}
					<a href="{{community.link}}/info" >Смотреть информацию об Объединении вне рамок юридического лица</a>
				{{/community.isWithOrganization}}
			</dd>
			{{#community.isWithOrganization}}
				<dt>Выписка из ЕГРЮЛ</dt>
				<dd>
					<a href="{{community.link}}/ulinfo">Смотреть выписку из ЕГРЮЛ</a>
				</dd>
			{{/community.isWithOrganization}}
		</dl>
		<div class="row">
			<div class="col-xs-6">
				<dl class="dl-horizontal" style="margin-bottom : 0;">
					<dt style="margin-bottom : 0;">Количество участников</dt>
					<dd style="margin-bottom : 0;">{{community.membersCount}}</dd>
				</dl>
			</div>
			<div class="col-xs-6">
				<dl class="dl-horizontal" style="margin-bottom : 0;">
					<dt style="margin-bottom : 0;">Количество подгрупп</dt>
					<dd style="margin-bottom : 0;">{{community.subgroupsCount}}</dd>
				</dl>
			</div>
		</div>

		<hr/>

		{{#community.isWithOrganization}}
			<div id="buttons-block">
				<div class="form-group">
					<span class="text-muted">Денежные операции</span>
				</div>
				<div class="form-group">
					<a href="#" onclick="SharerToCommunityAccountsMoveDialog.show(); return false;" class="btn btn-success nat-org-accounts-move-link" >Перевод этому объединению</a>
					{{#canTransferMoney}}
						<a href="#" onclick="CommunityToSharerAccountsMoveDialog.show(); return false;" class="btn btn-success org-nat-accounts-move-link" >Перевод участнику</a>
					{{/canTransferMoney}}
				</div>

				{{#canTransferMoney}}
					<div class="form-group">
						<a href="#" onclick="CommunityToCommunityAccountsMoveDialog.show(); return false;" class="btn btn-success org-nat-accounts-move-link" >Перевод другому объединению</a>
						{{#consumerSociety}}
							{{#isSelfMember}}
								<a href="#" onclick="CommunityToBookAccountsMoveDialog.show(); return false;" class="btn btn-success nat-org-accounts-move-link" >Перевод пайщику</a>
							{{/isSelfMember}}
						{{/consumerSociety}}
					</div>
				{{/canTransferMoney}}
				{{#consumerSociety}}{{#isSelfMember}}
					<hr/>
					<div class="form-group">
						<a href="#" onclick="SharerToBookAccountsMoveDialog.show(); return false;" class="btn btn-success nat-org-accounts-move-link" >Положить на личный счёт в ПО</a>
						<a href="#" onclick="BookToSharerAccountsMoveDialog.show(); return false;" class="btn btn-success nat-org-accounts-move-link" >Снять с личного счёта в ПО</a>
					</div>
				{{/isSelfMember}}{{/consumerSociety}}
			</div>
			<hr/>
		{{/community.isWithOrganization}}

		<div id="buttons-block">
			<div class="form-group">
				<span class="text-muted" id="self-status">
					{{^isSelfMember}}
						Вы не состоите в {{#community.root}}объединении{{/community.root}}{{^community.root}}подгруппе{{/community.root}}
					{{/isSelfMember}}
					{{#isSelfMember}}
						{{#community.selfMember.statusRequest}}
							Вы отправили запрос
							<span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='{{community.selfMember.requestDateFormat}}'	>
								{{community.selfMember.sendRequestHumanString}} назад
							</span>
						{{/community.selfMember.statusRequest}}
						{{#community.selfMember.statusInvite}}
							Вы получили приглашение
							<span
								class='request-distance'
								style='font-weight : bold; cursor : pointer;'
								data-title='{{community.selfMember.requestDateFormat}}'	>
								{{community.selfMember.sendRequestHumanString}} назад
								назад
							</span>
						{{/community.selfMember.statusInvite}}
						{{#community.selfMember.statusMember}}
							Вы состоите в {{#community.root}}объединении{{/community.root}}{{^community.root}}подгруппе{{/community.root}}
						{{/community.selfMember.statusMember}}
					{{/isSelfMember}}
				</span>
			</div>
			{{#canJoinInCommunityAsOrganization}}
				<hr/>
				<div class="form-group">
					<button class="btn btn-success join-link" id="showModalChooseMyCommunities">Участники юр лица в объединении</button>
				</div>
			{{/canJoinInCommunityAsOrganization}}

			<div class="form-group">
				<a href="#" class="btn btn-success join-link" {{^canJoin}}style="display : none;"{{/canJoin}} >Вступить</a>
				<a href="#" class="btn btn-primary request-link" {{^canRequest}}style="display : none;"{{/canRequest}} >Подать заявку</a>
				<a href="#" class="btn btn-warning cancel-request-link" {{^canCancelRequest}}style="display : none;"{{/canCancelRequest}} >Отменить заявку</a>
				<a href="#" class="btn btn-success accept-invite-link" {{^canAcceptInvite}}style="display : none;"{{/canAcceptInvite}} >Принять приглашение</a>
				<a href="#" class="btn btn-warning reject-invite-link" {{^canRejectInvite}}style="display : none;"{{/canRejectInvite}} >Отклонить приглашение</a>
				<a href="#" class="btn btn-warning leave-link" {{^canLeave}}style="display : none;"{{/canLeave}} >Выйти</a>
				<a href="#" class="btn btn-warning cancel-request-leave-link" {{^canCancelRequestToLeave}}style="display : none;"{{/canCancelRequestToLeave}} >Отменить запрос на выход из объединения</a>
				<a href="#" class="btn btn-danger delete-link" {{^canDelete}}style="display : none;"{{/canDelete}} >Удалить</a>
			</div>

		</div>

		<hr/>

		<div class="community-description">
			<h2>Описание {{#community.root}}объединения{{/community.root}}{{^community.root}}группы{{/community.root}}</h2>
			<div id="community-description">
				<pre>{{{community.menuData.fields.COMMUNITY_DESCRIPTION}}}</pre>
			</div>
			<div class="form-group shadow" id="community-description-shadow"></div>
			<div class="form-group text-right">
				<a href="#" id="comunity-description-show" class="btn btn-xs btn-default">Развернуть</a>
			</div>
			<hr/>
		</div>
	</div>

	<div id="news" style="padding-top: 40px; margin-top: -40px;"></div>
	<h2>Новости {{#community.root}}объединения{{/community.root}}{{^community.root}}группы{{/community.root}}
		{{#permissions.NEWS_CREATE}}
			<a href="#" class="btn btn-primary pull-right" id="create-news-link">Создать новость</a>
		{{/permissions.NEWS_CREATE}}
	</h2>
</script>

<t:insertAttribute name="newsDialog" />
<t:insertAttribute name="newsListItem" />

<t:insertAttribute name="communityHeader" />
<hr/>
<t:insertAttribute name="menu" />
<t:insertAttribute name="schema" />

<div id="newsPageDataBlock"></div>

<hr id="content-delimiter" />

<div class="news-list">

	<div class="row list-not-found" style="display : none;">
		<div class="panel panel-default">
			<div class="panel-body">Еще не добавлено ни одной новости</div>
		</div>
	</div>

</div>
<!-- Модальное окно с выбором объединения-->
<div class="modal fade" role="dialog" id="modalChooseMyCommunities" aria-labelledby="modalChooseMyCommunitiesTextLabel"
	 aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="modalChooseMyCommunitiesTextLabel">Организации для вступления в объединение</h4>
			</div>
			<div class="modal-body" id="possibleOrganizationMembers-grid">

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div><!-- /.modal -->