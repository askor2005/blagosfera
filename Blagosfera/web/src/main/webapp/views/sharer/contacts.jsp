<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>


<t:insertAttribute name="item" />

<script type="text/javascript">
	function initScrollListener(firstPage) {
		var $list = $("div#contacts-list").empty();
		ScrollListener.init("/contacts/list.json", "post", getParams, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
			var $list = $("div#contacts-list");
			$.each(response, function(index, sharer) {
				$list.append(getSharerMarkup(sharer));
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
		}, null, firstPage);		
	}

	function getParams() {
		var params = {};
		params.query = $("#search-input").val();
		params.sharer_status = "ACCEPTED";
		params.other_status = "ACCEPTED";
		params.sharer_id = "${profile.id}";
		params.order_by = $("a.order-link.active").attr("data-order-by");
		params.asc = $("a.order-link.active").attr("data-asc");
		return params;
	}
	
	$(document).ready(function() {
		
		$(radomEventsManager).bind("contact.add", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		$(radomEventsManager).bind("contact.delete", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		initSearchInput($("#search-input"), function() {
			initScrollListener();
		});
		var firstPage = ${firstPage};
		initScrollListener(firstPage);
		
		$("a.order-link").click(function() {
			$("a.order-link").removeClass("active");
			$(this).addClass("active");
			initScrollListener();
			return false;
		});
		
		$("a.order-link").radomTooltip();
		
	});
</script>


<h1>
	${profile.shortName}
	<small>контакты, всего $ {contactDao.getContactsCount(profile)}</small>
</h1>

<div class="row">

	<div class="col-xs-12">
		<label>Фильтр по фамилии, имени</label>
		<div class="form-group" id="search-input-block">
			<input type="text" class="form-control" id="search-input" placeholder="Начните вводить фамилию или имя" />
		</div>
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
		<div class="panel-body">Cписок пуст</a></div>
	</div>
</div>

<div class="row list-loader-animation"></div>