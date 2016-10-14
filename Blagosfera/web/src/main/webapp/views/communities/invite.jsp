<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
.tooltip-inner {
	max-width: 400px;
}

.dl-horizontal dt {
	width: 165px;
}

</style>

<script type="text/javascript">

	var communityId = ${communityId};

	function loadMembersPageData(communityId, callBack) {
		$.radomJsonPost(
				"/group/" + communityId + "/members_page_data.json",
				{},
				callBack
		);
	}
	
	function initScrollListener() {
		$("div.members-list").empty();
		ScrollListener.init("/group/" + communityId + "/possible_members_with_verified_count.json", "post", function() {
			var params = {};
			var query = $("input#possible-members-query").val();
			if (query) {
				params.query = query;
			}
			return params;
		}, function() {
			
		}, function(response) {
			$.each(response, function(index, member) {
				CommunityMembersListItem.append(member.communityMember, $("div.members-list"),member.countVerified);
			});
		});	
	}

	$(document).ready(function() {
		loadMembersPageData(communityId, function (communityMembersPageData) {
			initCommunityHead(communityMembersPageData.community);
			initCommunityMenu(communityMembersPageData.community);
			initInvitePage(communityMembersPageData);
		});
	});

	function initInvitePage(communityMembersPageData) {
		CommunityMembersListItem.init(
				communityMembersPageData.community.id,
				communityMembersPageData.creator,
				communityMembersPageData.hasRightInvites,
				communityMembersPageData.hasRightRequests,
				communityMembersPageData.hasRightExclude
		);
		initScrollListener();
		
		/*$(radomEventsManager).bind("community-member.exclude", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});

		$(radomEventsManager).bind("community-member.accept-invite", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$(radomEventsManager).bind("community-member.invite", function(event, data) {
            data.member.user.member = data.member;
            data.member.user.memberId = data.member.id;
            data.member.user.memberStatus = data.member.status;
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$(radomEventsManager).bind("community-member.leave", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});

		$(radomEventsManager).bind("community-member.request", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
			CommunityMembersListItem.replace(data.member.user);
		});*/

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			data.member.user.member = data.member;
			data.member.user.memberId = data.member.id;
			if (data.eventType == "reject_invite" || data.eventType == "cancel_invite") {
				data.member.user.memberStatus = null;
			} else {
				data.member.user.memberStatus = data.member.status;
			}
			CommunityMembersListItem.replace(data.member.user);
		});
		
		$("input#possible-members-query").callbackInput(500, 3, function() {
			initScrollListener();
		});
		
		$("input#query").radomTooltip({
			title : "Фильтр активируется после ввода минимум трех символов",
			placement : "top"
		});
		
	}

</script>

<t:insertAttribute name="membersListItem" />

<t:insertAttribute name="communityHeader" />
<h2>Пригласить участников</h2>
<hr/>

<div class="form-group">
	<input class="form-control" type="text" id="possible-members-query" placeholder="Начните вводить фамилию или имя" />
</div>
<hr/>
<div class="members-list"></div>