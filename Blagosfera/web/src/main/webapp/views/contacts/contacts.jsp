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
			$.each(response, function(index, contact) {
				$list.append(getContactMarkup(contact));
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
		}, null);
	}

	function getParams() {
		var params = {};
		params.query = $("#search-input").val();
		params.group_id = $("select#group-select").val();

		params.order_by = $("a.order-link.active").attr("data-order-by");
		params.asc = $("a.order-link.active").attr("data-asc");

		return params;
	}

	$(document).ready(function() {
		$.radomJsonGet("/contacts/init/info.json", {}, function(response) {
			var groups = response.groups;
			var defaultGroupCountWord = response.defaultGroupCountWord;
			var contactsCount = response.contactsCount;
			var defaultGroupCount = response.defaultGroupCount;
			var template = $('#contacts-template').html();
			var groupId = parseInt("${groupId}");
			for (var i = 0;i < groups.length;++i){
				if (groupId == groups[i].id){
					groups[i].active = true;
				}
				else {
					groups[i].active = false;
				}
			}
			Mustache.parse(template);
			var rendered = Mustache.render(template, {groups: groups, defaultGroupCountWord :  defaultGroupCountWord,contactsCount : contactsCount,defaultGroupCount : defaultGroupCount});
			$('#contacts-target').html(rendered);
			initSearchInput($("#search-input"), function() {
				initScrollListener();
			});
			initScrollListener();
			$("select#group-select").change(function(){
				initScrollListener();
			});
			$("select#filter-select").change(function(){
				initScrollListener();
			});

			$(radomEventsManager).bind("contact.add", function(event, data) {
				var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
				$sharerItem.replaceWith(getContactMarkup(data));
			});

			$(radomEventsManager).bind("contact.delete", function(event, data) {
				var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
				$sharerItem.replaceWith(getContactMarkup(data));
			});

			$("a.help-block.edit-contacts-groups-link").click(function() {
				$.cookie("EDIT_CONTACTS_LISTS_REFERER", window.location.pathname, {path : "/"});
				return true;
			});

			$("a.order-link").click(function() {
				$("a.order-link").removeClass("active");
				$(this).addClass("active");
				initScrollListener();
				return false;
			});
			$("a.order-link").radomTooltip();
		});
	});
</script>
<div id="contacts-target">
</div>
<script id="contacts-template" type="x-tmpl-mustache">

<h1>Мои контакты, всего {{contactsCount}}</h1>

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
				<option value="0">Список по умолчанию ({{defaultGroupCount}} {{defaultGroupCountWord}})</option>
				{{#groups}}
					<option {{#active}}selected="selected"{{/active}} value="{{id}}">{{name}} ({{contactsCount}} {{contactsCountWord}})</option>
				{{/groups}}
			</select>
			<a class="help-block text-right edit-contacts-groups-link" href="/contacts/lists"> <span class="glyphicon glyphicon-pencil"></span> Управление списками</a>
	</div>
</div>

<hr style="margin-top : 0;" />

<div class="form-group">

	<div class="row">
		<div class="col-xs-2">
			<label class="">Сортировка:</label>
		</div>
		<div class="col-xs-10 text-right">
			<div class="" style="margin-left : 20px; display : inline-block">
				<a href="#" class="order-link glyphicon glyphicon-chevron-up active" data-order-by="searchString" data-asc="true" data-placement="bottom" data-html="true" data-title="Сортировака по ФИО<br/>в алфавитном порядке"></a>
				<span>ФИО</span>
				<a href="#" class="order-link glyphicon glyphicon-chevron-down" data-order-by="searchString" data-asc="false" data-placement="bottom" data-html="true" data-title="Сортировака по ФИО обратном алфавитном порядке"></a>
			</div>
			<div class="" style="margin-left : 20px; display : inline-block">
				<a href="#" class="order-link glyphicon glyphicon-chevron-up" data-order-by="registeredAt" data-asc="true" data-placement="bottom" data-html="true" data-title="Сортировака по дате регистрации<br/>от старых к новым"></a>
				<span>Дата регистрации</span>
				<a href="#" class="order-link glyphicon glyphicon-chevron-down" data-order-by="registeredAt" data-asc="false" data-placement="bottom" data-html="true" data-title="Сортировака по дате регистрации<br/>от новых к старым"></a>
			</div>
		</div>
	</div>

</div>

<hr style="margin-top : 0;" />

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
</script>