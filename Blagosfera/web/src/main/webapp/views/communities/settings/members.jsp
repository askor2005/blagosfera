<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@include file="membersPostsGrid.jsp" %>
<style type="text/css">
	#membersPosts-grid {

		width: 100%;
	}
	#membersPostsGridSearchResult{
		display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;
	}
</style>

<script id="membersPostsRequestTemplate" type="x-tmpl-mustache">
	<div><span>Объединение</span></div>
	<select id="communitySelect" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
		{{#isHasChild}}
			<option value="{{community.id}}">{{community.name}}</option>
			<optgroup label="Подгруппы {{community.name}}">
				{{#children}}
					<option value="{{id}}">{{name}}</option>
				{{/children}}
			</optgroup>
		{{/isHasChild}}
		{{^isHasChild}}
			<option value="{{community.id}}">{{community.name}}</option>
		{{/isHasChild}}
	</select>
	<div><span>Кандидат</span></div>
	<select id="candidateSelect" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
		{{#candidates}}
			<option value="{{id}}">{{name}}</option>
		{{/candidates}}
	</select>
	<div><span>Должность</span></div>
	<select id="postSelect" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
	<button type="button" class="btn btn-primary" id="createRequestAppoint" style="margin-top: 5px;">Предложить должность</button>
	<hr/>
</script>

<script type="text/javascript">

	var communityId = "${communityId}";

	var loadMembersPostsLink = "/group/" + communityId + "/load_members_posts.json";
	var fireMemberFromPostLink = "/group/" + communityId + "/fire_member_from_post.json";
	var requestAppointMemberPostLink = "/group/" + communityId + "/request_appoint_member_post.json";
	var loadPostsByCommunityLink = "/group/" + communityId + "/load_posts_by_community.json";
	var postRequestPageDataLink = "/group/" + communityId + "/post_request_page_data.json";

	// Уволить участника с должности
	function fireMemberFromPost(sharerId, postId) {
		$.radomJsonPostWithWaiter(fireMemberFromPostLink, {
			user_id : sharerId,
			post_id : postId
		}, function(){
			// Перезагрузить таблицу с должностями
			storeMembersPosts.reload();
		});
	}

	// Предложить участнику должность
	function requestAppointMemberPost(communityId, sharerId, postId) {
		$.radomJsonPostWithWaiter(requestAppointMemberPostLink, {
			community_id : communityId,
			user_id : sharerId,
			post_id : postId
		});
	}

	// Загрузить должности по объединению
	function loadPostsByCommunity(communityId, callBack) {
		$.radomJsonPostWithWaiter(loadPostsByCommunityLink, {communityId : communityId}, callBack);
	}

	function loadPostRequestPageData(callBack) {
		$.radomJsonPost(
				postRequestPageDataLink,
				{},
				callBack
		);
	}

	$(document).ready(function() {
		loadPostRequestPageData(function (communityPostRequestPageData) {
			initCommunityHead(communityPostRequestPageData.community);
			initCommunityMenu(communityPostRequestPageData.community);
			initPostRequestPage(communityPostRequestPageData);
		});
	});

	function initPostRequestPage (communityPostRequestPageData) {
		var membersPostsRequestTemplate = $("#membersPostsRequestTemplate").html();
		Mustache.parse(membersPostsRequestTemplate);
		var isHasChild = communityPostRequestPageData.children != null && communityPostRequestPageData.children.length > 0;
		var markup = Mustache.render(membersPostsRequestTemplate, {
			community : communityPostRequestPageData.community,
			children : communityPostRequestPageData.children,
			isHasChild : isHasChild,
			candidates : communityPostRequestPageData.candidates
		});
		$("#postRequestBlock").append(markup);


		$("#communitySelect").selectpicker("refresh");
		$("#communitySelect").selectpicker("val", null);

		$("#candidateSelect").selectpicker("refresh");
		$("#candidateSelect").selectpicker("val", null);

		$("#communitySelect").change(function(){
			var communityId = $(this).val();
			$("#postSelect").empty();
			loadPostsByCommunity(communityId, function(posts){
				for (var index in posts) {
					var post = posts[index];
					var htmlOption = "<option value='" + post.id + "'>" + post.name + "</option>";
					$("#postSelect").append(htmlOption);
				}
				$("#postSelect").selectpicker("refresh");
				$("#postSelect").selectpicker("val", null);
			});
		});

		$("#createRequestAppoint").click(function(){
			var communityId = $("#communitySelect").val();
			var sharerId = $("#candidateSelect").val();
			var postId = $("#postSelect").val();
			if (communityId == null || communityId == "") {
				bootbox.alert("Необходимо выбрать объединение!");
				return;
			}
			if (sharerId == null || sharerId == "") {
				bootbox.alert("Необходимо выбрать участника!");
				return;
			}
			if (postId == null || postId == "") {
				bootbox.alert("Необходимо выбрать должность!");
				return;
			}
			requestAppointMemberPost(communityId, sharerId, postId);
		});
	}
</script>

<t:insertAttribute name="communityHeader" />
<h2>Назначение должностей</h2>
<hr/>
<div id="postRequestBlock"></div>
<div id="membersPosts-grid"></div>
<div id="membersPostsGridSearchResult"></div>
<hr/>

<div class="modal fade" id="appointPost" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog modal-xl">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Предложить должность</h4>
      		</div>
      		<div class="modal-body">

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="apply-button">Предложить должность</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
			</div>
		</div>
	</div>
</div>