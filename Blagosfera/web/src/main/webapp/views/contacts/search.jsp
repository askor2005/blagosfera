<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<t:insertAttribute name="item" />

<script type="text/javascript">
    function initScrollListener(query, firstPage) {
        var $list = $("div#contacts-list").empty();
		ScrollListener.init("/contacts/search.json", "post", function() {
			return {
				query : $("#search-input").val(),
                certification_requested: $("#search-certification-requests").is(':checked'),
				order_by : $("a.order-link.active").attr("data-order-by"),
				asc : $("a.order-link.active").attr("data-asc"),
				requestedForRegistrationsOnlyToMe: $("#includeRequestedForRegistrationMode").val() === "true" ? true : false,
				country : $("#countrySearch").val() ? $("#countrySearch").val() : null,
				city : $("#filterCity").val() ? $("#filterCity").val() : null,
				ageFrom :  $("#ageFrom").val() ? $("#ageFrom").val() : null,
				ageTo : $("#ageTo").val() ? $("#ageTo").val() : null,
				sex: $("#sexFilter").val() ? $("#sexFilter").val() : null
			};
		}, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
			var $list = $("div#contacts-list");

            if (typeof response === 'string') {
                response = JSON.parse(response);
            }

            response.forEach(function(sharer) {
				$list.append(getSharerMarkup(sharer));
				$list.append("<hr style='margin-top : 5px;' />");
			});

            if ($("div.row.sharer-item").length == 0) {
				$("div#contacts-not-found").show();
			} else {
				$("div#contacts-not-found").hide();
			}

            $(".list-loader-animation").fadeOut();
		}, null, firstPage,null,true);
	}

	$(document).ready(function() {
        if (isRegistrator) {
            $('#search-certification-requests-block').show();
        }

		initSearchInput($("#search-input"), initScrollListener);
		var firstPage = ${firstPage};
		initScrollListener("", firstPage);
		
		$(radomEventsManager).bind("contact.add", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		$(radomEventsManager).bind("contact.delete", function(event, data) {
			var $sharerItem = $("div.sharer-item[data-sharer-id=" + data.id + "]");
			$sharerItem.replaceWith(getSharerMarkup(data));
		});
		
		$("a.order-link").click(function() {
			$("a.order-link").removeClass("active");
			$(this).addClass("active");
			initScrollListener();
			return false;
		});
		
		$("a.order-link").radomTooltip();

        $("#search-certification-requests").change(function() {
			if ($("#search-certification-requests").prop('checked') === true) {
				$("#includeRequestedForRegistrationModeBlock").show();
			}
			else {
				$("#includeRequestedForRegistrationModeBlock").hide();
			}
            initScrollListener($("#search-input").val());
        });
		$("#includeRequestedForRegistrationMode").change(function() {
			initScrollListener($("#search-input").val());
		});
	});
</script>

<h1>Поиск людей</h1>

<div class="form-group" id="search-input-block">
	<input type="text" class="form-control" id="search-input" placeholder="Начните вводить имя или адрес"/>
    <span>Используйте символ "," для поиска нескольких значений. Например "Иван Иванов, Пётр Петров"</span>
</div>

<div class="form-group" id="search-certification-requests-block" style="display: none;">
	<input type="checkbox" id="search-certification-requests"/>
    <span>Искать только пользователей подавших заявку на идентификацию</span>
	<div style="margin-top: 6px; display: none;"  id="includeRequestedForRegistrationModeBlock">
			<select class="form-control" id="includeRequestedForRegistrationMode">
				<option selected value="false">Все</option>
				<option value="true">Только мои</option>
			</select>

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

<div class="row list-loader-animation"></div>
