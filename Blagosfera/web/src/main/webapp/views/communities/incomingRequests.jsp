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
	
	function initScrollListener(isSharerMember) {
		$("div.members-list").empty();
		var url = "/group/" + communityId + "/search_request_members.json";
		if (!isSharerMember) {
			url = "/group/" + communityId + "/search_request_organization_members.json";
		}
		ScrollListener.init(url, "post", function() {
			var params = {};
			//var query = $("input#query").val();
			//if (query) {
			//	params.query = query;
			//}
			return params;
		}, function() {
			
		}, function(response) {
			$.each(response, function(index, member) {
				if (isSharerMember) {
					CommunityMembersListItem.append(member, $("div.members-list"));
				} else {
					OrganizationCommunityMembersListItem.append(member, $("div.members-list"));
				}
			});
		});	
	}

	$(document).ready(function() {
		loadMembersPageData(communityId, function (communityMembersPageData) {
			initCommunityHead(communityMembersPageData.community);
			initCommunityMenu(communityMembersPageData.community);
			initRequestsPage(communityMembersPageData);
		});
	});

	function initRequestsPage(communityMembersPageData) {

		CommunityMembersListItem.init(
				communityMembersPageData.community.id,
				communityMembersPageData.creator,
				communityMembersPageData.hasRightInvites,
				communityMembersPageData.hasRightRequests,
				communityMembersPageData.hasRightExclude
		);

		OrganizationCommunityMembersListItem.init(
				communityMembersPageData.community.id,
				communityMembersPageData.hasRightInvites,
				communityMembersPageData.hasRightRequests,
				communityMembersPageData.hasRightExclude
		);

		$("#memberType").css("visibility", "visible");
		$("#memberType").selectpicker("refresh");
		$("#memberType").change(function(){
			var memberType = $(this).val();
			if (memberType == "sharer_members") {
				$("#sharer_member_block").show();
				$("#community_member_block").hide();
				initScrollListener(true);
			} else if (memberType == "organization_members") {
				$("#sharer_member_block").hide();
				$("#community_member_block").show();
				initScrollListener(false);
			}
		});
		
		initScrollListener(true);

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.community.id == communityId) {
				switch(data.eventType) {
					case "accept_request":
					case "reject_request":
					case "cancel_request":
						CommunityMembersListItem.remove(data.member);
						break;
					case "request":
						CommunityMembersListItem.prepend(data.member);
						break;
				}
			}
		});
		
		/*$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});
		
		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("community-member.request", function(event, data) {
			if (data.community.id == communityId) {
				CommunityMembersListItem.prepend(data.member);
			}
		});*/

		//

		$(radomEventsManager).bind("organization-community-member.accept-request", function(event, data) {
			if (data.community.id == communityId) {
				OrganizationCommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("organization-community-member.reject-request", function(event, data) {
			if (data.community.id == communityId) {
				OrganizationCommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("organization-community-member.cancel-request", function(event, data) {
			if (data.community.id == communityId) {
				OrganizationCommunityMembersListItem.remove(data.member);
			}
		});

		$(radomEventsManager).bind("organization-community-member.request", function(event, data) {
			if (data.community.id == communityId) {
				OrganizationCommunityMembersListItem.prepend(data.member);
			}
		});


		
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
<t:insertAttribute name="organizationMembersListItem" />

<t:insertAttribute name="communityHeader" />
<h2>Запросы на вступление</h2>
<hr/>

<div class="form-group">
	<select id="memberType" class="selectpicker" data-hide-disabled="true" data-width="100%" style="visibility: hidden;">
		<option value="sharer_members" selected="selected">Участники - Физические лица</option>
		<option value="organization_members">Участники - Юридические лица</option>
	</select>
</div>
<div class="members-list"></div>