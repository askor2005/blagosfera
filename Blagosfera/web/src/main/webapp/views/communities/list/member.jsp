<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<t:insertAttribute name="item" />
<script type="text/javascript" src="/js/rameraListEditor/rameraListEditor.js" ></script>
<script type="text/javascript">

	var sharerId = null;
	
	function initScrollListener(firstPage) {
		ScrollListener.off();
		$("div#communities-list").empty();
		ScrollListener.init("/communities/list.json", "post", function() {
			var params = {};
			params.query = $("input#query").val();
			params.sharer_id = sharerId;
			params.access_type = $("select#access-type").val();
            params.community_type = $("select#community-type").val();
			params.status = "MEMBER";
			params.activity_scope_id = selectedActivityScopeId == null ? "" : selectedActivityScopeId;
			if ($("input#creator[type=checkbox]:checked").length > 0) {
				params.creator = true;
			}
			
			var $activeOrderLink = $("a.order-link.active");
			params.order_by = $activeOrderLink.attr("data-order-by");
			params.asc = $activeOrderLink.attr("data-asc");
			params.deleted = false;
			return params;
		}, function() {
			$("div.list-loader-animation").show();
		}, function(response, page) {
			$("div.list-not-found").remove();
			if (response.count) {
				$("span#count-help-block").html("Найдено объединений: " + response.count).slideDown();
				$("hr#count-block-hr").slideDown();
			} else {
				$("span#count-help-block").html("").slideUp();
				$("hr#count-block-hr").slideUp();
			}
			
			$.each(response.list, function(index, item){
				CommunitiesListItem.append(item, $("div#communities-list"));
			});
			if (page == 1 && response.list.length == 0) {
				$("div#communities-list").append("<div style='display : block;' class='row list-not-found'><div class='panel panel-default'><div class='panel-body'>Список пуст</div></div></div>");
			}
			$("div.list-loader-animation").fadeOut();
		}, null, firstPage);		
	}

	var selectedActivityScopeId = null;
	$(document).ready(function() {
		$(eventManager).bind("inited", function(event, currentUser) {
			userId = currentUser.id;
			// Сфера деятельности
			RameraListEditorModule.init($("#activityScopes"), {
				selectEmptyValue: "Выберите сферу деятельности",
				selectId: "activity-scope",
				selectClasses: ["form-control"],
				forceViewType: RameraListEditorType.COMBOBOX
			}, function (eventType, data) {
				switch (eventType) {
					case RameraListEditorEvents.CREATED:
						readyInitPage();
						break;
					case RameraListEditorEvents.VALUE_CHANGED:
						selectedActivityScopeId = data.value;
						initScrollListener();
						break;
				}
			});
		});
	});

	function readyInitPage(){
		initScrollListener(null);
		$("input#query").callbackInput(500, 3, function() {
			initScrollListener(null);
		});
		$("select#access-type").change(function() {
			initScrollListener();
		});
        $("select#community-type").change(function() {
            initScrollListener();
        });
		$("input#query").radomTooltip();
		$("input#creator").change(function() {
			initScrollListener(null);
		});

		$(radomEventsManager).bind("community.deleted", function(event, data) {
			CommunitiesListItem.replace(data.community, data.member);
		});
		
		$(radomEventsManager).bind("community.restored", function(event, data) {
			CommunitiesListItem.replace(data.community, data.member);
		});

		$(radomEventsManager).bind("community-member.event", function(event, data) {
			if (data.member.user.id == userId) {
				switch(data.eventType) {
					case "join":
					case "accept_request":
						CommunitiesListItem.prepend(data.community, data.member);
						break;
					case "leave":
					case "exclude":
						CommunitiesListItem.remove(data.community, data.member);
						break;
				}
			}
		});

		/*$(radomEventsManager).bind("community-member.join", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.prepend(data.community, data.member);
			}
		});
		
		$(radomEventsManager).bind("community-member.leave", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.remove(data.community, data.member);
			}
		});
		
		$(radomEventsManager).bind("community-member.accept-request", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.prepend(data.community, data.member);
			}
		});

		$(radomEventsManager).bind("community-member.exclude", function(event, data) {
			if (data.member.user.id == userId) {
				CommunitiesListItem.remove(data.community, data.member);
			}
		});	*/
		
		$("a.order-link").click(function() {
			$("a.order-link").removeClass("active");
			$(this).addClass("active");
			initScrollListener();
			return false;
		});
		
		$("a.order-link").radomTooltip();
	};
</script>

<c:if test="${count > 0}">
	<h1>Я состою в ${count} ${countDeclension}</h1>
</c:if>

<c:if test="${count == 0}">
	<h1>Я не состою в объединениях</h1>
</c:if>

<hr/>

<form role="form">
	<div class="row">
		<div class="col-xs-8">
			<div class="form-group">
				<input type="text" class="form-control" id="query" placeholder="Начните ввод названия" data-toggle="tooltip" data-placement="top" title="Минимальная длина фильтра: 3 символа" />
			</div>
		</div>
		<div class="col-xs-4">
			<div class="checkbox" style="margin-top : 5px;">
				<label>
					<input id="creator" type="checkbox"> Только созданные мной
				</label>
			</div>		
		</div>
		<div class="col-xs-6">
			<div class="form-group">
				<select class="form-control" id="access-type">
					<option value="">Выберите тип доступа</option>
					<option value="OPEN">Открытый</option>
					<option value="CLOSE">Закрытый</option>
					<option value="RESTRICTED">Ограниченный</option>
				</select>
			</div>
		</div>
		<div class="col-xs-6">
			<div class="form-group" id="activityScopes" rameraListEditorName="activity_scope_id"></div>
		</div>		
	</div>
    <div class="form-group">
        <select class="form-control" id="community-type">
            <option value="">Выберите тип объединения</option>
            <option value="COMMUNITY_WITH_ORGANIZATION">Объединение в рамках юр. лица</option>
            <option value="COMMUNITY_WITHOUT_ORGANIZATION">Объединение вне рамок юр. лица</option>
        </select>
    </div>
</form>

<hr style="margin-top : 0;" />
<span class="help-block text-center" id="count-help-block" style="display : none;"></span>
<hr id="count-block-hr" style="display : none;" />

<div class="form-group">
	
	<div class="row">
		<div class="col-xs-2">
			<label class="">Сортировка:</label>
		</div>
		<div class="col-xs-10 text-right">	
			<div class="" style="margin-left : 20px; display : inline-block">
				<a href="#" class="order-link glyphicon glyphicon-chevron-up active" data-order-by="name" data-asc="true" data-placement="bottom" data-html="true" data-title="Сортировака по названию<br/>в алфавитном порядке"></a>
				<span>Название</span>
				<a href="#" class="order-link glyphicon glyphicon-chevron-down" data-order-by="name" data-asc="false" data-placement="bottom" data-html="true" data-title="Сортировака по названию<br/>в обратном алфавитном порядке"></a>
			</div>
			<div class="" style="margin-left : 20px; display : inline-block">
				<a href="#" class="order-link glyphicon glyphicon-chevron-up" data-order-by="createdAt" data-asc="true" data-placement="bottom" data-html="true" data-title="Сортировака по дате создания<br/>от старых к новым"></a>
				<span>Дата создания</span>
				<a href="#" class="order-link glyphicon glyphicon-chevron-down" data-order-by="createdAt" data-asc="false" data-placement="bottom" data-html="true" data-title="Сортировака по дате создания<br/>от новых к старым"></a>
			</div>
			<div class="" style="margin-left : 20px; display : inline-block">
				<a href="#" class="order-link glyphicon glyphicon-chevron-up" data-order-by="membersCount" data-asc="true" data-placement="bottom" data-html="true" data-title="Сортировака по количеству участников<br/>по возрастанию"></a>
				<span>Количество участников</span>
				<a href="#" class="order-link glyphicon glyphicon-chevron-down" data-order-by="membersCount" data-asc="false" data-placement="bottom" data-html="true" data-title="Сортировака по количеству участников<br/>по убыванию"></a>
			</div>
		</div>
	</div>

</div>

<hr style="margin-top : 0;" />

<div id="communities-list">

</div>

<div class="row list-loader-animation"></div>