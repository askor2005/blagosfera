<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="organization-community-members-list-item-template" type="x-tmpl-mustache">
<div class="row member-item" data-member-id="{{organization.memberId}}" data-organization-id="{{organization.id}}">
	<div class="col-xs-3">
		<div class="row">
			<div class="col-xs-12">
				<a class="organization-item-avatar-link" href="{{organization.link}}">
					<img style="display : block; width : 141px; height : 141px;" src="{{organization.avatar}}" class="img-thumbnail">
				</a>
			</div>
		</div>
	</div>
	<div class="col-xs-9">
		<h3><a href="{{organization.link}}">{{organization.name}}</a></h3>
		<p class="text-muted">
			{{{currentStatus}}}
		</p>
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

	var OrganizationCommunityMembersListItem = {

		template : $('#organization-community-members-list-item-template').html(),
		
		hasInvitesPermission : false,
		hasRequestsPermission : false,
		hasExcludePermission : false,
		
		communityId : null,

		init : function(communityId, hasInvitesPermission, hasRequestsPermission, hasExcludePermission) {
			this.communityId = parseInt(communityId);
			this.hasInvitesPermission = hasInvitesPermission;
			this.hasRequestsPermission = hasRequestsPermission;
			this.hasExcludePermission = hasExcludePermission;
		},
			
		getCurrentStatusText : function(member) {
			var result = "";
			switch (member.status) {
				case "MEMBER":
					result = "Состоит в объединении";
					break;
				case "REQUEST":
					//result = "Получен запрос <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + member.requestDate + "'>" + (member.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(member.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
					result = "Получен запрос";
					break;
				case "INVITE":
					//result = "Отправлено приглашение <span class='request-distance' style='font-weight : bold; cursor : pointer;' data-title='" + member.requestDate + "'>" + (member.requestHoursDistance > 1 ? RadomUtils.getHumanReadableDatesDistanceAccusative(member.requestHoursDistance - 1, true) : "менее 1 часа") + " назад</span>";
					result = "Отправлено приглашение";
					break;
				default:
					result = "Не состоит в объединении";
					break;
			}
			return result;
		},
		
		templateParsed : false,
		
		getMarkup : function(organization) {
			if (!this.templateParsed) {
				Mustache.parse(this.template);
				this.templateParsed = true;
			}
			organization.avatar = Images.getResizeUrl(organization.avatar, "c141");
			var model = {};
			model.organization = organization;
			model.currentStatus = this.getCurrentStatusText(organization);
			model.allowExclude = (this.hasExcludePermission && organization.status == "MEMBER");
			model.allowAccept = (this.hasRequestsPermission && organization.status == "REQUEST");
			model.allowReject = (this.hasRequestsPermission && organization.status == "REQUEST");
			model.allowCancel = (this.hasInvitesPermission && organization.status == "INVITE");
			model.allowInvite = (this.hasInvitesPermission && !organization.status);
			
			var markup = Mustache.render(this.template, model);
			var $markup = $(markup);

			$markup.find("a.exclude-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				CommunityFunctions.requestFromCommunityToExcludeCommunity(memberId, function(response){
					$(radomEventsManager).trigger("organization-community-member.exclude", response);
				});
				return false;
			});

			$markup.find("a.accept-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				CommunityFunctions.acceptOrganizationRequest(memberId, function(response){
					$(radomEventsManager).trigger("organization-community-member.accept-request", response);
				});
				return false;
			});

			$markup.find("a.reject-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				CommunityFunctions.rejectRequestToJoinCommunity(memberId, function(response){
					$(radomEventsManager).trigger("organization-community-member.reject-request", response);
				});
				return false;
			});
			/*
			$markup.find("a.cancel-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				$.radomJsonPost("/communities/cancel_invite.json", {
					member_id : memberId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.cancel-invite", response);
				});
				return false;
			});

			$markup.find("a.invite-link").click(function(){
				var $this = $(this);
				var $item = $this.parents(".member-item");
				var memberId = $item.attr("data-member-id");
				var sharerId = $item.attr("data-sharer-id");
				
				$.radomJsonPost("/communities/invite.json", {
					sharer_id : sharerId,
					community_id : communityId
				}, function(response) {
					$(radomEventsManager).trigger("community-member.invite", response);
				});
				return false;
			});*/
			return $markup;
		},
		
		append : function(organization, $list) {
			var $item = $("div.member-item[data-organization-id=" + organization.id + "]");
			if ($item.length == 0) {
				$list.append(this.getMarkup(organization));
			}
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});
		},
		
		prepend : function(organization, $list) {
			var $item = $("div.member-item[data-organization-id=" + organization.id + "]");
			if ($item.length == 0) {
				$list.prepend(this.getMarkup(organization));
			}
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});
		},
		
		replace : function(organization) {
			var $item = $("div.member-item[data-organization-id=" + organization.id + "]");
			$item.next("hr").remove();
			$item.replaceWith(this.getMarkup(organization));
			$("span.request-distance").radomTooltip({
				placement : "top",
				container : "body"
			});;
		},
			
		remove : function(organization) {
			var $item = $("div.member-item[data-organization-id=" + organization.id + "]");
			$item.fadeOut(function(){
				$item.next("hr").remove();
				$item.remove();
			});
		}
		
	};
	
</script>
