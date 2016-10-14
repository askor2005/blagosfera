<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<t:insertAttribute name="item" />

<script type="text/javascript">
	function initScrollListener(firstPage) {
		$("div#requests-list").empty();
		ScrollListener.init("/registrator/listRequest.json", "post", getParams, function() {
			$(".list-loader-animation").fadeIn();
		}, function(response) {
            response = JSON.parse(response);
			var $list = $("div#requests-list");
			$.each(response, function(index, request) {
				$list.append(getRequestMarkup(request));
				$list.append("<hr style='margin: 5px 0;' />");
			});
			if ($("div.row.request-item").length == 0) {
				$("div#requests-not-found").show();
			} else {
				$("div#requests-not-found").hide();
			}
			$(".list-loader-animation").fadeOut();
		}, null, firstPage);
	}

	function getParams() {
		var params = {};
		params.nameTemplate = $("#search-input").val();
        params.status = $("select#status-select").val();
        params.order_by = $("a.order-link.active").attr("data-order-by");
        params.asc = $("a.order-link.active").attr("data-asc");
        params.object_type = $("#selectObjectType").val();

        return params;
	}

    function updateCounts(all, notProcessed){
        $('#counts-label').text('Заявки на идентификацию, всего ' + all +
        ((notProcessed > 0) ? (' (не обработанные: ' +  notProcessed + ')') : ''));
    }

    function reloadCounts(){
        var objectType = $("#selectObjectType").val();
        $.radomJsonPost("/registrator/requestsCounts.json", {
            object_type : objectType
        }, function (data) {
            if(data){
                console.log(data);
                updateCounts(data.all, data.notProcessed);
            }
        });
    }
	
	$(document).ready(function() {
        reloadCounts();
		initSearchInput($("#search-input"), function() {
			initScrollListener();
		});
		var firstPage = null;
		initScrollListener(firstPage);
        $(radomEventsManager).bind("registrationRequest.updateList", function(){
            initScrollListener();
            reloadCounts();
        });
        $(radomEventsManager).bind("registrationRequest.updateRequest", function(event, data) {
            initScrollListener();
            reloadCounts();
        });
        $("a.order-link").click(function() {
            $("a.order-link").removeClass("active");
            $(this).addClass("active");
            initScrollListener();
            return false;
        });

        $("select#status-select").change(function(){
            initScrollListener();
        });

        $("a.do-clear-filter").click(function() {
            $("#search-input").val("");
            $("select#status-select").val("ALL");
            initScrollListener();
            return false;
        });

        $("a.order-link, a.do-clear-filter").radomTooltip({
            placement: "top",
            container: "body"
        });

        $(".searchStringFieldUp").tooltip('destroy');
        $(".searchStringFieldDown").tooltip('destroy');
        $(".createdFieldUp").tooltip('destroy');
        $(".createdFieldDown").tooltip('destroy');
        $(".searchStringFieldUp").tooltip({container : "body"});
        $(".searchStringFieldDown").tooltip({container : "body"});
        $(".createdFieldUp").tooltip({container : "body"});
        $(".createdFieldDown").tooltip({container : "body"});

        $("#selectObjectType").change(function() {

            var upTitle = "";
            var downTitle = "";
            if ($(this).val() == "SHARER") {
                $("#fieldSortName").text("ФИО");
                upTitle = "Сортировака по ФИО<br/>в алфавитном порядке";
                downTitle = "Сортировака по ФИО обратном алфавитном порядке";
            } else if ($(this).val() == "COMMUNITY") {
                $("#fieldSortName").text("Название организации");
                upTitle = "Сортировака по названию<br/>в алфавитном порядке";
                downTitle = "Сортировака по названию обратном алфавитном порядке";
            }

            $(".searchStringFieldUp").attr('title', upTitle)
                    .tooltip('fixTitle')
                    .data('bs.tooltip');
            $(".searchStringFieldDown").attr('title', downTitle)
                    .tooltip('fixTitle')
                    .data('bs.tooltip');

            initScrollListener();
            reloadCounts();
        });

        $("#search-input").val("");
        //$("select#status-select").val("NEW");
	});
</script>



<label id="counts-label"></label>
<div class="row">
    <div class="col-xs-12" style="margin-bottom: 5px;">
        <select class="form-control" id="selectObjectType">
            <option value="SHARER" <c:if test="${objectType == 'SHARER'}">selected="selected"</c:if>>Физ лицо</option>
            <option value="COMMUNITY" <c:if test="${objectType == 'COMMUNITY'}">selected="selected"</c:if>>Юр лицо</option>
        </select>
    </div>
    <div class="col-xs-7">
        <div class="form-group" id="search-input-block">
            <input type="text" class="form-control" id="search-input" placeholder="Начните вводить имя" />
        </div>
    </div>
    <div class="col-xs-4">
        <select class="form-control" id="status-select">
            <option value="ALL">Все</option>
            <option value="NEW" selected="selected">Не обработанные</option>
            <option value="DELETED">Удаленные заявителем</option>
            <option value="CANCELED">Отклоненные</option>
            <option value="PROCESSED">Обработанные</option>
        </select>
    </div>
    <div class="col-xs-1" style="padding: 0px;">
        <a href='#' data-title="Очистить форму поиска" style="text-decoration : none !important; font-size: 26px;" class="do-clear-filter"><i class="fa fa-remove"></i></a>
    </div>
</div>
<div class="row">
    <div class="col-xs-2">
        <label class="">Сортировка:</label>
    </div>
    <div class="col-xs-10 text-right">
        <div class="" style="margin-left : 20px; display : inline-block">
            <a href="#" class="order-link glyphicon glyphicon-chevron-up searchStringFieldUp" data-order-by="search_string" data-asc="true" data-placement="bottom" data-html="true" title="Сортировака по ФИО<br/>в алфавитном порядке"></a>
            <span id="fieldSortName">ФИО</span>
            <a href="#" class="order-link glyphicon glyphicon-chevron-down searchStringFieldDown" data-order-by="search_string" data-asc="false" data-placement="bottom" data-html="true" title="Сортировака по ФИО обратном алфавитном порядке"></a>
        </div>
        <div class="" style="margin-left : 20px; display : inline-block">
            <a href="#" class="order-link glyphicon glyphicon-chevron-up createdFieldUp" data-order-by="created" data-asc="true" data-placement="bottom" data-html="true" title="Сортировака по дате запроса<br/>от старых к новым"></a>
            <span>Дата запроса</span>
            <a href="#" class="order-link glyphicon glyphicon-chevron-down active createdFieldDown" data-order-by="created" data-asc="false" data-placement="bottom" data-html="true" title="Сортировака по дате запроса<br/>от новых к старым"></a>
        </div>
    </div>
</div>
<hr style="margin: 5px 0;" />
<div id="requests-list"></div>
<div class="row list-not-found" id="requests-not-found">
    <div class="panel panel-default">
        <div class="panel-body">Поиск не дал результатов</div>
    </div>
</div>
<div class="row list-loader-animation"></div>

<t:insertAttribute name="processDialog" />
<t:insertAttribute name="cancelDialog" />
<t:insertAttribute name="viewDialog" />