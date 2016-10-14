<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<t:insertAttribute name="item" />

<style type="text/css">

	div.communities-tree ul {
		position : relative;
		list-style : none;
		padding-left : 0;
	}

	div.communities-tree ul:before {
		content: "";
		display: block;
		width: 0;
		position: absolute;
		top: 0;
		bottom: 0;
		left: 25px;
		border-left: 1px solid #ddd;
		
	}

	div.communities-tree ul[data-level='1'] {
		
	}
	
	div.communities-tree ul[data-level='1']:before {
		border-left : none;
	}
	
	div.communities-tree ul li {
		position : relative;
		padding-left : 50px;
		
	}
	
	div.communities-tree ul li div.communities-tree-item {
		
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper {
		padding-top : 20px;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper {
		/*height : 80px;*/
		border : 1px solid #ddd;
		padding: 5px;
		border-radius : 4px;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper img.photo {
		width : 68px;
		height : 68px;
		float : left;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.text {
		margin-left : 80px;
		margin-right : 10px;
		border-bottom : 1px solid #ddd;
		/*height : 40px;*/
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.text div.left-text-block{
		font-size : 16px;	
		padding : 0;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.text div.left-text-block div.status {
		font-size : 12px;
	}
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.text div.right-text-block{
		font-size : 12px;
		padding : 0;
		line-height : 20px;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.controls {
		margin: 5px 10px 5px 80px;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.controls div.icons {
		padding : 0;
	}
	
	div.communities-tree ul li div.communities-tree-item div.outer-wrapper div.inner-wrapper div.controls div.buttons {
		padding : 0;
	}
	
	div.communities-tree ul li:before {
		content: "";
		display: block;
		width: 25px;
		height: 0;
		border-top: 1px solid #ddd;
		margin-top: -1px;
		position: absolute;
		top: 50px;
		left: 25px;
	}

	div.communities-tree ul[data-level='1'] > li {
		padding-left : 0;
	}
	
	div.communities-tree ul[data-level='1'] > li > span {
		
	}
	
	div.communities-tree ul[data-level='1'] > li:before {
		border : none;
	}
	
	div.communities-tree ul li:last-child:before {
		background: #fff;
		height: auto;
		top: 50px;
		bottom: 0;
		z-index : 2;
		left : 25px;
		width : 25px;
	}
	
	div.communities-tree ul[data-level='1'] > li:last-child:before {
		background : transparent;
		width : 0px;
		height : 0px;
	}
	
</style>

<script type="text/javascript">
	
	var userId = null;
	var communityId = ${communityId};

	function loadAnyPageData(communityId, callBack) {
		$.radomJsonPost(
				"/communities/any_page_data.json",
				{
					community_id : communityId
				},
				callBack
		);
	}
	
	function initScrollListener() {
		ScrollListener.off();
		$("div#communities-list").empty();
		ScrollListener.init("/communities/get_children_hierarchy_list.json", "post", function() {
			var params = {};
			params.community_id = communityId;
			return params;
		}, function() {
			$("div.list-loader-animation").show();
		}, function(response, page) {
			$("div.list-not-found").remove();
			$.each(response.list, function(index, item){
				CommunitiesListItem.appendTree(item, $("#communities-list"));
			});
			if (page == 1 && response.list.length == 0) {
				$("#communities-list").append("<div style='display : block;' class='row list-not-found'><div class='panel panel-default'><div class='panel-body'>Список пуст</div></div></div>");
			}
			$("div.list-loader-animation").fadeOut();
		});
	}
	
	$(document).ready(function() {
		$(eventManager).bind("inited", function (event, currentUser) {
			userId = currentUser.id;
			loadAnyPageData(communityId, function(communityAnyPageData) {
				initCommunityHead(communityAnyPageData.community);
				initCommunityMenu(communityAnyPageData.community);
			});
			initSubgroupsPage();
		});
	});

	function initSubgroupsPage() {

		initScrollListener();

		$(radomEventsManager).bind("community.deleted", function(event, data) {
			CommunitiesListItem.replaceTree(data.community);
		});
		
		$(radomEventsManager).bind("community.restored", function(event, data) {
			CommunitiesListItem.replaceTree(data.community);
		});

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		/*$(radomEventsManager).bind("community-member.join", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.leave", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});

		$(radomEventsManager).bind("community-member.reject-request", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.exclude", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});	
		
		$(radomEventsManager).bind("community-member.request", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});

		$(radomEventsManager).bind("community-member.cancel-request", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.accept-invite", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});

		$(radomEventsManager).bind("community-member.reject-invite", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.cancel-invite", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});
		
		$(radomEventsManager).bind("community-member.invite", function(event, data) {
			if (data.member.sharer.id == userId) {
				CommunitiesListItem.replaceTree(data.community);
			}
		});*/
		
	}
</script>

<t:insertAttribute name="communityHeader" />
<h2>Подгруппы</h2>
<hr/>

<div class="communities-tree">
	<ul id="communities-list" data-level="1"></ul>
</div>
<div class="row list-loader-animation"></div>
<hr/>