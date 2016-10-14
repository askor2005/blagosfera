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
	
	function initScrollListener(queryValue, isSharerMember) {
		$("div.members-list").empty();
		var url = "/group/" + communityId + "/search_members.json";
		if (!isSharerMember) {
			url = "/group/" + communityId + "/search_organization_members.json";
		}
		ScrollListener.init(url, "post", function() {
			var params = {
				include_context_user : true
			};
			var query = queryValue;
			if (query) {
				params.query = query;
			}
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

	function loadMembersPageData(communityId, callBack) {
		$.radomJsonPost(
				"/group/" + communityId + "/members_page_data.json",
				{},
				callBack
		);
	}

	$(document).ready(function () {
		loadMembersPageData(communityId, function(communityMembersPageData) {
			initMembersPage(communityMembersPageData);
			initCommunityHead(communityMembersPageData.community);
			initCommunityMenu(communityMembersPageData.community);
		});
	});
	
	function initMembersPage(communityMembersPageData) {
		$("#countMembers").html(communityMembersPageData.communityMembersCount);
		$("#countOrganizationMembers").html(communityMembersPageData.organizationMembersCount); // TODO
		$("#memberType").css("visibility", "visible");
		$("#memberType").selectpicker("refresh");
		$("#memberType").change(function(){
			var memberType = $(this).val();
			if (memberType == "sharer_members") {
				$("#sharer_member_block").show();
				$("#community_member_block").hide();

				$("#communityMembersCount").show();
				$("#communityOrganizationMembersCount").hide();

				initScrollListener($("input#sharerQuery").val(), true);
			} else if (memberType == "organization_members") {
				$("#sharer_member_block").hide();
				$("#community_member_block").show();

				$("#communityMembersCount").hide();
				$("#communityOrganizationMembersCount").show();

				initScrollListener($("input#communityQuery").val(), false);
			}
		});

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

		initScrollListener($("input#sharerQuery").val(), true);

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			switch(data.eventType) {
				case "exclude":
				case "leave":
					CommunityMembersListItem.remove(data.member);
					break;
				case "accept_request":
				case "join":
					CommunityMembersListItem.prepend(data.member, $("div.members-list"));
					break;
			}
		});
		
		/*$(radomEventsManager).bind("community-member.exclude", function(event, data) {
			CommunityMembersListItem.remove(data.member);
		});
		
		$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			CommunityMembersListItem.prepend(data.member, $("div.members-list"));
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			
		});
		
		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
			
		});

		$(radomEventsManager).bind("community-member.invite", function(event, data) {
			
		});
		
		$(radomEventsManager).bind("community-member.leave", function(event, data) {
			CommunityMembersListItem.remove(data.member);
		});
		
		$(radomEventsManager).bind("community-member.join", function(event, data) {
			CommunityMembersListItem.prepend(data.member, $("div.members-list"));
		});*/

		//

		$(radomEventsManager).bind("organization-community-member.exclude", function(event, data) {
			OrganizationCommunityMembersListItem.remove(data.member);
		});

		$(radomEventsManager).bind("organization-community-member.accept-request", function(event, data) {
			OrganizationCommunityMembersListItem.prepend(data.member, $("div.members-list"));
		});

		$(radomEventsManager).bind("organization-community-member.reject-request", function(event, data) {

		});

		$(radomEventsManager).bind("organization-community-member.cancel-invite", function(event, data) {

		});

		$(radomEventsManager).bind("organization-community-member.invite", function(event, data) {

		});

		$(radomEventsManager).bind("organization-community-member.leave", function(event, data) {
			OrganizationCommunityMembersListItem.remove(data.member);
		});

		$(radomEventsManager).bind("organization-community-member.join", function(event, data) {
			OrganizationCommunityMembersListItem.prepend(data.member, $("div.members-list"));
		});


		
		$("input#sharerQuery").callbackInput(500, 3, function() {
			initScrollListener($("input#sharerQuery").val(), true);
		});
		$("input#communityQuery").callbackInput(500, 3, function() {
			initScrollListener($("input#communityQuery").val(), false);
		});
		
		$("input#sharerQuery").radomTooltip({
			title : "Фильтр активируется после ввода минимум трех символов",
			placement : "top"
		});
		$("input#communityQuery").radomTooltip({
			title : "Фильтр активируется после ввода минимум трех символов",
			placement : "top"
		});

	}

</script>

<t:insertAttribute name="membersListItem" />
<t:insertAttribute name="organizationMembersListItem" />

<t:insertAttribute name="communityHeader" />
<h2>Участники объединения</h2>
<hr/>
<t:insertAttribute name="menu" />
<div class="form-group">
	<select id="memberType" class="selectpicker" data-hide-disabled="true" data-width="100%" style="visibility: hidden;">
		<option value="sharer_members" selected="selected">Участники - Физические лица</option>
		<option value="organization_members">Участники - Юридические лица</option>
	</select>
</div>
<div class="form-group" id="sharer_member_block">
	<input class="form-control query" type="text" id="sharerQuery" placeholder="Начните вводить фамилию или имя" />
</div>
<div class="form-group" id="community_member_block" style="display: none;">
	<input class="form-control query" type="text" id="communityQuery" placeholder="Начните вводить наименование объединения" />
</div>
<div id="communityMembersCount">
	<label>Общее количество участников: <span id="countMembers"></span></label>
</div>
<div id="communityOrganizationMembersCount" style="display: none;">
	<label>Общее количество участников юридических лиц: <span id="countOrganizationMembers"></span></label>
</div>
<hr/>
<div class="members-list"></div>