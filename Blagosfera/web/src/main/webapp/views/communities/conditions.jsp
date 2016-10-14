<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<script type="text/javascript">

	var communityId = "${community.id}";
	var selfMemberId = "${selfMember.id}";
	var communityLink = "${community.link}";
	
	$(document).ready(function() {
		CommunityRequiredConditionsDialog.loadConditions({
			communityId : communityId,
			callback : function() {
				window.location = communityLink;
			},
			rejectCallback : function() {
				$.radomJsonPost("/communities/leave.json", {
					member_id : selfMemberId
				}, function() {
					window.location = "/groups"
				});
			},
			rejectButtonText : "Отказаться и выйти из объединений",
			dialogTitle : "Условия дальнейшего членства в объединении"
		});
	});

</script>