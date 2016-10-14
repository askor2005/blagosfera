<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="community-members-list-item-template" type="x-tmpl-mustache">
<div class="row member-item" data-member-id="{{user.memberId}}" data-user-id="{{user.id}}">
	<div class="col-xs-3">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{user.link}}">
					<img style="display : block; width : 141px; height : 141px;" src="{{user.avatar}}" class="img-thumbnail">
					{{#user.online}}
						<img src="/i/icon-online.png" class="sharer-item-online-icon">
					{{/user.online}}
					{{^user.online}}
						<img src="/i/icon-offline.png" class="sharer-item-online-icon">
					{{/user.online}}
				</a>
				{{#user.online}}
					<span class="sharer-item-online-status">В сети</span>
				{{/user.online}}
				{{^user.online}}
					<span class="sharer-item-online-status text-muted">Не в сети</span>
				{{/user.online}}
			</div>
			<div class="col-xs-12 text-center">
				<a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{user.id}}');" class="btn btn-sm btn-link">Написать сообщение</a>
			</div>
		</div>
	</div>
	<div class="col-xs-9">
		<h3><a href="{{user.link}}">{{user.fullName}}</a></h3>
		<p class="text-muted">
			{{{currentStatus}}}
		</p>
		{{#invitedVerifiedCount}}
		Приглашено идентифицированных пользователей в систему данным пользователем: {{invitedVerifiedCount}}
		{{/invitedVerifiedCount}}
		<hr>
		{{#allowExclude}}
			<a class="btn btn-primary exclude-link" href="#">Исключить</a>
		{{/allowExclude}}
		{{#allowAccept}}
			<a class="btn btn-primary accept-link" href="#">Одобрить</a>
		{{/allowAccept}}
		{{#allowReject}}
			<a class="btn btn-primary reject-link" href="#">Отклонить</a>
		{{/allowReject}}
		{{#allowCancel}}
			<a class="btn btn-primary cancel-link" href="#">Отменить</a>
		{{/allowCancel}}
		{{#allowInvite}}
			<a class="btn btn-primary invite-link" href="#">Пригласить</a>
		{{/allowInvite}}
	</div>
</div>
<hr/>
</script>

<script type="text/javascript">
	var CommunityMembersListItem = {
		template : $('#community-members-list-item-template').html(),
		isCreator : false,
		hasInvitesPermission : false,
		hasRequestsPermission : false,
		hasExcludePermission : false,
		communityId : null,

		init : function(communityId, isCreator, hasInvitesPermission, hasRequestsPermission, hasExcludePermission) {
			this.communityId = parseInt(communityId);
			this.isCreator = isCreator;
			this.hasInvitesPermission = hasInvitesPermission;
			this.hasRequestsPermission = hasRequestsPermission;
			this.hasExcludePermission = hasExcludePermission;
		},
			
		getCurrentStatusText : function(user) {
			switch (user.memberStatus) {
				case "MEMBER":
					return "Состоит в объединении";
			   	case "REQUEST":
					return "Получен запрос <span class='request-distance' style='font-weight: bold; cursor: pointer;' data-title='" + user.requestDate + "'>" + (user.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(user.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
			   	case "INVITE":
					return "Отправлено приглашение <span class='request-distance' style='font-weight: bold; cursor: pointer;' data-title='" + user.requestDate + "'>" + (user.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(user.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
				case "REQUEST_TO_LEAVE":
					return "Выполнил запрос на выход из объединения";
				case "LEAVE_IN_PROCESS":
					return "Ожидает решения по выходу из объединения";
			   	default:
					return "Не состоит в объединении";
			}
		},
		
		templateParsed : false,
		
		getMarkup : function(user,invitedVerifiedCount) {
			if (!CommunityMembersListItem.templateParsed) {
				Mustache.parse(CommunityMembersListItem.template);
				CommunityMembersListItem.templateParsed = true;
			}
			user.avatar = Images.getResizeUrl(user.avatar, "c141");
			var model = {};
			model.user = user;
			if (user.requestDate != null) {
				model.user.requestDate = dateFormat(new Date(user.requestDate), "yyyy.mm.dd HH:MM:ss");
			}
			model.currentStatus = CommunityMembersListItem.getCurrentStatusText(user);
			model.allowExclude = (CommunityMembersListItem.hasExcludePermission && !user.creator && user.memberStatus == "MEMBER");
			model.allowAccept = (CommunityMembersListItem.hasRequestsPermission && user.memberStatus == "REQUEST");
			model.allowReject = (CommunityMembersListItem.hasRequestsPermission && user.memberStatus == "REQUEST");
			model.allowCancel = (CommunityMembersListItem.hasInvitesPermission && user.memberStatus == "INVITE");
			model.allowInvite = (CommunityMembersListItem.hasInvitesPermission && !user.memberStatus);
			model.invitedVerifiedCount = invitedVerifiedCount;
			
			var markup = Mustache.render(CommunityMembersListItem.template, model);
			var $markup = $(markup);
			
			$markup.find("a.exclude-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				
				$.radomJsonPost("/communities/exclude_member.json", {
					member_id : memberId
				}, function(response) {
                    $(radomEventsManager).trigger("community-member.event", response);
				});
				return false;
			});
			
			$markup.find("a.accept-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				$.radomJsonPost("/communities/accept_request.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.event", response);
				});
				return false;
			});
			
			$markup.find("a.reject-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				$.radomJsonPost("/communities/reject_request.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.event", response);
				});
				return false;
			});
			
			$markup.find("a.cancel-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				$.radomJsonPost("/communities/cancel_invite.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.event", response);
				});
				return false;
			});
			
			$markup.find("a.invite-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var userId = $item.attr("data-user-id");
				$.radomJsonPost("/communities/invite.json", {
					user_id : userId,
					community_id : communityId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.event", response);
				});
				return false;
			});
			return $markup;
		},
		
		append : function(user, $list,invitedVerifiedCount) {
			var $item = $("div.member-item[data-user-id=" + user.id + "]");
			if ($item.length == 0) {
				var $markup = $(CommunityMembersListItem.getMarkup(user,invitedVerifiedCount));
				$list.append($markup);
			}
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});
		},
		
		prepend : function(user, $list) {
			if ($list == null) {
				$list =  $($("div.members-list").get(0));
			}
			var $item = $("div.member-item[data-user-id=" + user.id + "]");
			if ($item.length == 0) {
				var $markup = CommunityMembersListItem.getMarkup(user);
				$list.prepend($markup);
			}
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});
		},
		
		replace : function(user) {
			var $item = $("div.member-item[data-user-id=" + user.id + "]");
			$item.next("hr").remove();
			var $markup = CommunityMembersListItem.getMarkup(user);
			$item.replaceWith($markup);
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});
		},
			
		remove : function(member) {
			var $item = $("div.member-item[data-user-id=" + member.user.id + "]");
			$item.fadeOut(function(){
				$item.next("hr").remove();
				$item.remove();
			});
		}
	};
</script>
