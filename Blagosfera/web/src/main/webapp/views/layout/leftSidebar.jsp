<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<c:if test="${radom:isSharer()}">

<script type="text/javascript">

	var LeftSidebar = {

		sharerId : "${sharer.id}",

		refreshNewContactRequestsCount : function() {
			$.ajax({
				type: "get",
				dataType : "json",
				url : "/contacts/new_requests_count.json",
				success : function(response) {
                    response = JSON.parse(response);

					if (response.result == "error") {

					} else {
						console.log("contacts requests count : " + response.count);
						var $li = $("li[data-section-name=contactsNewRequests]");
						var $parentAdditionalIcon = $li.parents(".panel").find("#section-additional-icon");
						if (response.count == 0) {
							$li.slideUp();
							$parentAdditionalIcon.removeAttr("class");
						} else {
							$li.find("a").html("На рассмотрении (" + response.count + ")");
							$li.slideDown();
							$parentAdditionalIcon.attr("class", "fa fa-warning faa-flash animated");
						}


					}
				},
				error : function() {
					console.log("ajax error");
				}
			});
		},

		changeRequestsCount : function(value) {
			var $li = $("li[data-section-name=communitiesRequests]");
			var $a = $li.find("a");

            var requestsCount = parseInt($("li[data-section-name=communitiesRequests]").attr("data-requests-count"));
            if (!requestsCount) requestsCount = 0;

            var invitesCount = parseInt($("li[data-section-name=communitiesInvites]").attr("data-invites-count"));
			if (!invitesCount) invitesCount = 0;

            requestsCount = requestsCount + value;
			var totalCount = requestsCount + invitesCount;

			$a.html("Кандидатуры (" + requestsCount + ")")
			$li.attr("data-requests-count", requestsCount);

            if (requestsCount <= 0) {
				$li.slideUp();
			} else {
				$li.slideDown();
			}

            var $parentAdditionalIcon = $li.parents(".panel").find("#section-additional-icon");

            if (totalCount <= 0) {
				$parentAdditionalIcon.removeAttr("class");
			} else {
				$parentAdditionalIcon.attr("class", "fa fa-warning faa-flash animated");
			}
		},

		changeInvitesCount : function(value) {
			var $li = $("li[data-section-name=communitiesInvites]");
			var $a = $li.find("a");
			var requestsCount = parseInt($("li[data-section-name=communitiesRequests]").attr("data-requests-count"));
			var invitesCount = parseInt($("li[data-section-name=communitiesInvites]").attr("data-invites-count"));
			invitesCount = invitesCount + value;
			var totalCount = requestsCount + invitesCount;
			$a.html("Приглашения (" + invitesCount + ")")
			$li.attr("data-invites-count", invitesCount);
			if (invitesCount <= 0) {
				$li.slideUp();
			} else {
				$li.slideDown();
			}
			var $parentAdditionalIcon = $li.parents(".panel").find("#section-additional-icon");
			if (totalCount <= 0) {
				$parentAdditionalIcon.removeAttr("class");
			} else {
				$parentAdditionalIcon.attr("class", "fa fa-warning faa-flash animated");
			}
		},

		changeMyRequestsCount : function(value) {
			var $li = $("li[data-section-name=communitiesMyRequests]");
			var $a = $li.find("a");
			var myRequestsCount = parseInt($("li[data-section-name=communitiesMyRequests]").attr("data-my-requests-count"));

            if (!myRequestsCount) myRequestsCount = 0;
			myRequestsCount = myRequestsCount + value;

            $a.html("Запросы (" + myRequestsCount + ")")
			$li.attr("data-my-requests-count", myRequestsCount);

            if (myRequestsCount <= 0) {
				$li.slideUp();
			} else {
				$li.slideDown();
			}
		},

		showSection : function(sectionId) {
			var $group = $("div#left-sidebar-accordion div.panel[data-section-id=" + sectionId + "]");
			var $item = $("div#left-sidebar-accordion div.panel ul.nav li[data-section-id=" + sectionId + "]");
			if ($group.length > 0) {
				$group.slideDown();
			} else if ($item.length > 0) {
				$item.slideDown();
				$item.parents("div.panel[data-section-id]").slideDown();
			}
		},

		hideSection : function(sectionId) {
			var $group = $("div#left-sidebar-accordion div.panel[data-section-id=" + sectionId + "]");
			var $item = $("div#left-sidebar-accordion div.panel ul.nav li[data-section-id=" + sectionId + "]");
			if ($group.length > 0) {
				$group.slideUp();
			} else if ($item.length > 0) {
				$item.slideUp();
				$group = $item.parents("div.panel[data-section-id]");

				var $items = $group.find("li[data-section-id]");
				var count  = 0;
				$.each($items, function(index, item) {
					if ($(item).css("display") == "block") {
						count++;
					}
				});
				if (count < 2) {
					$group.slideUp();
				}
			}
		}

	};

	$(document).ready(function(){


		$('.left-menu').css('margin-left', getCookie('left-menu-margin') + 'px');
		function getCookie(name) {
			var matches = document.cookie.match(new RegExp(
					"(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
			));
			return matches ? decodeURIComponent(matches[1]) : undefined;
		}

		radomStompClient.subscribeToUserQueue("new_notification", function(){
			//refreshWaitingVotingsCount();
		});

		$(radomEventsManager).bind("contact.add", function(event, data) {
			LeftSidebar.refreshNewContactRequestsCount();
		});

		$(radomEventsManager).bind("contact.delete", function(event, data) {
			LeftSidebar.refreshNewContactRequestsCount();
		});

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			switch(data.eventType) {
				case "accept_request":
				case "reject_request":
				case "cancel_request":
					if (data.member.user.id == LeftSidebar.sharerId) {
						LeftSidebar.changeMyRequestsCount(-1);
					} else if (data.community.creator.id == LeftSidebar.sharerId) {
						LeftSidebar.changeRequestsCount(-1);
					}
					break;
				case "request":
					if (data.member != null && data.member.user != null &&
							data.member.user.id == LeftSidebar.sharerId && data.member.status == "REQUEST") {
						LeftSidebar.changeMyRequestsCount(1);
					} else if (data.community != null && data.community.creatorId == LeftSidebar.sharerId) {
						LeftSidebar.changeRequestsCount(1);
					}
					break;
				case "accept_invite":
				case "reject_invite":
				case "cancel_invite":
					if (data.member.user.id == LeftSidebar.sharerId) {
						LeftSidebar.changeInvitesCount(-1);
					}
					break;
				case "invite":
					if (data.member.user.id == LeftSidebar.sharerId) {
						LeftSidebar.changeInvitesCount(1);
					}
					break;
			}
		});

		/*$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
            console.log("community-member.accept-request", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeMyRequestsCount(-1);
			} else if (data.community.creator.id == LeftSidebar.sharerId) {
				LeftSidebar.changeRequestsCount(-1);
			}
		});+

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
            console.log("community-member.reject-request", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeMyRequestsCount(-1);
			} else if (data.community.creator.id == LeftSidebar.sharerId) {
				LeftSidebar.changeRequestsCount(-1);
			}
		});+

		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
            console.log("community-member.cancel-request", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeMyRequestsCount(-1);
			} else if (data.community.creator.id == LeftSidebar.sharerId) {
				LeftSidebar.changeRequestsCount(-1);
			}
		});+

		$(radomEventsManager).bind("community-member.request", function(event, data) {
            console.log("community-member.request",
                    data,
                    data.member.user.id,
                    LeftSidebar.sharerId,
                    data.member.status,
                    data.member.status == "REQUEST");

            if (data.member != null && data.member.user != null &&
					data.member.user.id == LeftSidebar.sharerId && data.member.status == "REQUEST") {
				LeftSidebar.changeMyRequestsCount(1);
			} else if (data.community != null && data.community.creatorId == LeftSidebar.sharerId) {
				LeftSidebar.changeRequestsCount(1);
			}
		});

		$(radomEventsManager).bind("community-member.accept-invite", function(event, data) {
            console.log("community-member.accept-invite", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeInvitesCount(-1);
			}
		});

		$(radomEventsManager).bind("community-member.reject-invite", function(event, data) {
            console.log("community-member.reject-invite", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeInvitesCount(-1);
			}
		});

		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
            console.log("community-member.cancel-invite", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeInvitesCount(-1);
			}
		});

		$(radomEventsManager).bind("community-member.invite", function(event, data) {
            console.log("community-member.invite", data);
			if (data.member.user.id == LeftSidebar.sharerId) {
				LeftSidebar.changeInvitesCount(1);
			}
		});*/

		$(radomEventsManager).bind("contact.accepted", function(event, data) {
			LeftSidebar.refreshNewContactRequestsCount();
		});

		$(radomEventsManager).bind("contact.delete", function(event, data) {
			LeftSidebar.refreshNewContactRequestsCount();
		});

		$("div#left-sidebar-accordion a[href=#]").click(function() {
			bootbox.alert("Раздел в разработке");
			return false;
		});

		$.each($("div#left-sidebar-accordion a[data-title]"), function(index, link) {
			var $link = $(link);
			$link.radomTooltip({
				placement : "right"
			});
		});

		$("#left-sidebar-community-create").radomTooltip({
			placement : "right",
			title : "Переход к созданию нового объединения"
		});

		$(radomEventsManager).bind("section.show", function(event, data) {
			LeftSidebar.showSection(data.id);
		});

		$(radomEventsManager).bind("section.hide", function(event, data) {
			LeftSidebar.hideSection(data.id);
		});

	});

</script>

</c:if>

<script>
	var isMenuEditor = false;
	var isRegistrator = false;
	<security:authorize access="hasRole('ROLE_ADMIN')">
	isMenuEditor = true;
	</security:authorize>
	<security:authorize access="hasAnyRole('ROLE_REGISTRATOR_REGISTRATORS_EDITOR', 'ROLE_REGISTRATOR_COMMUNITIES_EDITOR', 'ROLE_REGISTRATOR_SHARERS_EDITOR')">
	isRegistrator = true;
	</security:authorize>

	function initLeftSections(rootSection) {
		var sections = null;
		if (rootSection == null || rootSection.children == null) {
			sections = [];
		} else {
			sections = rootSection.children;
		}
		var sectionsTemplate = $("#leftSectionsTemplate").html();
		Mustache.parse(sectionsTemplate);
		prepareLeftSections(sections);
		var markup = Mustache.render(sectionsTemplate, {
			sections : sections,
			rootSection : rootSection,
			isMenuEditor : isMenuEditor,
			isRegistrator : isRegistrator
		});
		$("#left-sidebar-accordion").append(markup);
	}

	function prepareLeftSections(sections) {
		for (var i in sections) {
			var section = sections[i];
			section.hasLink = section.link != null;
			if (section.children != null) {
				var childVisible = false;
				for (var j in section.children) {
					var child = section.children[j];
					child.hasLink = child.link != null;
					if (child.visible) {
						childVisible = true;
					}
					child.visible = child.details.visible == null ? child.visible : child.details.visible;
				}
				section.hasChildren = childVisible;
			} else {
				section.hasChildren = false;
			}
			section.iconIsUrl = false;
			section.iconIsClass = false;
			if (section.icon != null && section.icon.indexOf("http") > -1) {
				section.iconIsUrl = true;
			} else if (section.icon != null) {
				section.iconIsClass = true;
			}
		}
	}
</script>

<script id="leftSectionsTemplate" type="x-tmpl-mustache">
	{{#sections}}
		{{#visible}}
			<div class="panel panel-default" data-section-id="{{id}}" data-help-exists="{{helpExists}}" data-help-published="{{helpPublished}}">
				<div class="panel-heading" data-help-section="{{helpLink}}">
					<h4 class="panel-title">
						<a {{#disabled}}style="pointer-events: none;cursor: default;color: #999;"{{/disabled}}
							{{#hasChildren}}{{^hasLink}}
								data-toggle="collapse" data-parent="#left-sidebar-accordion" href="#collapse-{{id}}"
							{{/hasLink}}{{/hasChildren}}
							{{^hasChildren}}{{#hasLink}}{{^openInNewLink}}href='{{link}}'{{/openInNewLink}}{{#openInNewLink}}href='#' onclick="window.open('{{link}}', '_blank');" {{/openInNewLink}}{{/hasLink}}{{/hasChildren}}
							{{^hasChildren}}{{^hasLink}}
								href="#" class="in-work"
							{{/hasLink}}{{/hasChildren}}
							{{#hasChildren}}{{#hasLink}}{{^openInNewLink}}href='{{link}}'{{/openInNewLink}}{{#openInNewLink}}href='#' onclick="window.open('{{link}}', '_blank');" {{/openInNewLink}}{{/hasLink}}{{/hasChildren}}
						>
							<span id="section-icon">
								{{#iconIsUrl}}
									<img id="image-icon" style="width: 19px; height: 19px;" src="{{icon}}"/>
								{{/iconIsUrl}}
								{{#iconIsClass}}
									<i class="{{icon}}"></i>
								{{/iconIsClass}}
								{{^icon}}
									<i class="fa fa-question-circle"></i>
								{{/icon}}
							</span>
							{{title}}
							<i id="section-additional-icon" style="float : right; margin-top : 1px;"
								class='{{details.additionalIcon}}'
							></i>
						</a>
						<a>
						</a>
					</h4>
					{{^disabled}}
					{{#hasChildren}}{{#hasLink}}
					    <div style="margin-top: 4px;">
						<a data-toggle="collapse" data-parent="#left-sidebar-accordion" href="#collapse-{{id}}">
							Показать подразделы
						</a>
						</div>
					{{/hasLink}}{{/hasChildren}}
					{{/disabled}}
				</div>
				{{#hasChildren}}
					<div id="collapse-{{id}}" class="panel-collapse collapse{{#active}} in{{/active}}">
						<div class="panel-body">
							<ul class="nav list-unstyled">
								{{#children}}
									<li
										{{#active}}class="active"{{/active}}
										{{^visible}}style="display : none;"{{/visible}}
										data-section-name = "{{name}}"
										data-section-id = "{{id}}"
										data-help-exists="{{helpExists}}"
										data-help-published="{{helpPublished}}"

										{{details.liAttrs}}

										data-help-section="{{helpLink}}"
									>
										<a  {{#disabled}}style="pointer-events: none;cursor: default;color: #999;"{{/disabled}}
											{{#hasLink}}href="{{link}}"{{/hasLink}}
											{{^hasLink}}href="#" class="in-work"{{/hasLink}}
											{{#hint}}data-title="{{hint}}"{{/hint}}
											>
											{{title}} {{details.titleSuffix}}
										</a>
									</li>
								{{/children}}
							</ul>
						</div>
					</div>
				{{/hasChildren}}
			</div>
		{{/visible}}
	{{/sections}}
	<div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a target="_blank" href="/ng/index.html#/ecoadvisor/communities"><i class="fa fa-question-circle"></i>&nbsp;Экономический советник</a>
            </h4>
        </div>
    </div>
    {{#isMenuEditor}}
    	<a href="/menu_edit/{{rootSection.name}}"><i class="glyphicon glyphicon-pencil"></i> Редактировать меню</a>
    {{/isMenuEditor}}
    {{#isRegistrator}}
    	<div style="margin-top: 10px;"><a href="/registrator/requests"><i class="fa fa-user-plus"></i> Обработка заявок</a></div>
    {{/isRegistrator}}
</script>

<div class="panel-group" id="left-sidebar-accordion"></div>

<%--

<c:if test="${sectionsHierarchy[0].name == 'blagosfera'}">
	<security:authorize access="hasAnyRole('ROLE_ADMIN', 'ROLE_BLAGOSFERA_MENU_EDITOR')">
		<a href="/Благосфера/редактор/меню"><i class="glyphicon glyphicon-pencil"></i> Редактировать меню</a>
	</security:authorize>
</c:if>

<c:if test="${sectionsHierarchy[0].name != 'blagosfera'}">
	<security:authorize access="hasRole('ROLE_ADMIN')">
		<a href="/menu_edit/${sectionsHierarchy[0].name}"><i class="glyphicon glyphicon-pencil"></i> Редактировать меню</a>
	</security:authorize>
</c:if>
<security:authorize access="hasAnyRole('ROLE_REGISTRATOR_REGISTRATORS_EDITOR', 'ROLE_REGISTRATOR_COMMUNITIES_EDITOR', 'ROLE_REGISTRATOR_SHARERS_EDITOR')">
    <div style="margin-top: 10px;"><a href="/registrator/requests"><i class="fa fa-user-plus"></i> Обработка заявок</a></div>
</security:authorize>--%>
