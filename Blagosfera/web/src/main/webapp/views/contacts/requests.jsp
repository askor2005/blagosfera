<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<t:insertAttribute name="item" />

<script type="text/javascript">
	function initScrollListener() {
		var $list = $("div#contacts-list").empty();
		ScrollListener.init("/contacts/list.json", "post", getParams, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
			var $list = $("div#contacts-list");
			$.each(response, function(index, sharer) {
				$list.append(getContactMarkup(sharer));
				$list.append("<hr style='margin-top : 5px;' />");
			});
			if ($("div.row.sharer-item").length == 0) {
				
				if (($("#search-input").val() == "") && ($("select#filter-select").val() == 0) && ($("select#group-select").val() == -1)) {
					$("div#contacts-empty").show();
					$("div#contacts-not-found").hide();
				} else {
					$("div#contacts-not-found").show();
					$("div#contacts-empty").hide();
				}
			} else {
				$("div#contacts-not-found").hide();
				$("div#contacts-empty").hide();
			}
			$(".list-loader-animation").fadeOut();
		}, null, null);
	}

	function getParams() {
		var params = {};
		params.query = $("#search-input").val();
		params.group_id = $("select#group-select").val();
		
		params.sharer_status = "NEW";
		params.other_status = "ACCEPTED";
		
		return params;
	}
	
	$(document).ready(function() {
		initSearchInput($("#search-input"), initScrollListener);
		initScrollListener();
		$("select#group-select").change(function(){
			initScrollListener();
		});
		$("select#filter-select").change(function(){
			initScrollListener();
		});		
		
		$(radomEventsManager).bind("contact.add", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		$(radomEventsManager).bind("contact.delete", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		$("a.help-block.edit-contacts-groups-link").click(function() {
	    	$.cookie("EDIT_CONTACTS_LISTS_REFERER", window.location.pathname, {path : "/"});
	    	return true;
		});
		
	});
</script>


<h1>Заявки на рассмотрении</h1>


<div class="row">

	<div class="col-xs-7">
		<label>Фильтр по имени или названию</label>
		<div class="form-group" id="search-input-block">
			<input type="text" class="form-control" id="search-input" placeholder="Начните вводить имя или название организации" />
		</div>
	</div>

	<div class="col-xs-5">
			<label>Фильтр по списку</label>
			<select class="form-control" id="group-select">
				<option value="-1">Все списки</option>
				<option value="0">Список по умолчанию (${defaultGroupCount} ${radom:getDeclension(defaultGroupCount, "контакт", "контакта", "контактов")})</option>
				<c:forEach items="${groups}" var="g">
					<option <c:if test="${g.id == group.id}">selected="selected"</c:if> value="${g.id}">${g.name} (${g.contactsCount} ${radom:getDeclension(g.contactsCount, "контакт", "контакта", "контактов")})</option>
				</c:forEach>
			</select>	
			<a class="help-block text-right edit-contacts-groups-link" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
	</div>
</div>

<hr/>

<div id="contacts-list"></div>

<div class="row list-not-found" id="contacts-not-found">
	<div class="panel panel-default">
		<div class="panel-body">Поиск не дал результатов</div>
	</div>
</div>

<div class="row list-not-found" id="contacts-empty">
	<div class="panel panel-default">
		<div class="panel-body">Cписок пуст, воспользуйтесь разделом <a href="/contacts/search">поиска контактов</a></div>
	</div>
</div>

<div class="row list-loader-animation"></div>