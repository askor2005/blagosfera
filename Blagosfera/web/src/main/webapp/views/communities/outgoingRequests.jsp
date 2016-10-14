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
		ScrollListener.init("/group/" + communityId + "/search_invite_members.json", "post", function() {
			var params = {};

			//var query = $("input#query").val();
			//if (query) {
			//	params.query = query;
			//}
			return params;
		}, function() {
			
		}, function(response) {
			$.each(response, function(index, member) {
				CommunityMembersListItem.append(member, $("div.members-list"));
			});
		});	
	}
	
	$(document).ready(function() {
		loadMembersPageData(communityId, function (communityMembersPageData) {
			initCommunityHead(communityMembersPageData.community);
			initCommunityMenu(communityMembersPageData.community);
			initInvitesPage(communityMembersPageData);
		});
	});

	function initInvitesPage(communityMembersPageData) {

		CommunityMembersListItem.init(
				communityMembersPageData.community.id,
				communityMembersPageData.creator,
				communityMembersPageData.hasRightInvites,
				communityMembersPageData.hasRightRequests,
				communityMembersPageData.hasRightExclude
		);

		initScrollListener();

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.community.id == communityId) {
				switch(data.eventType) {
					case "accept_invite":
					case "reject_invite":
					case "cancel_invite":
						CommunityMembersListItem.remove(data.member);
						break;
					case "invite":
						CommunityMembersListItem.prepend(data.member);
						break;
				}
			}
		});
		
		/*$(radomEventsManager).bind("community-member.accept-invite", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("community-member.reject-invite", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});
		
		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("community-member.invite", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.prepend(data.member);
			}
		});*/
		
		//$("input#query").callbackInput(500, 3, function() {
		//	initScrollListener();
		//});
		
		//$("input#query").tooltip({
		//	title : "Фильтр активируется после ввода минимум трех символов",
		//	placement : "top"
		//});
		
	}

</script>

<t:insertAttribute name="membersListItem" />

<t:insertAttribute name="communityHeader" />
<h2>Отправленные приглашения</h2>
<hr/>

<div class="members-list"></div>