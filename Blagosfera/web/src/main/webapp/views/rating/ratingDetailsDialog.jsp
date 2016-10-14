<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="modal fade" id="rating-details-modal" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">&times;</span>
				<span class="sr-only">Закрыть</span></button>
        		<h4 class="modal-title">Детальная информация по рейтингу</h4>
                <span id="rating-details-title"></span>
      		</div>
      		<div class="modal-body">
                <div id="custom-toolbar">
                    <div class="form-inline">
                        <div class="form-group">
                            <div class="input-group">
                                <input class="form-control" type="text" placeholder="Введите ФИО" id="userNamePattern" style="width: 200px;">
                            </div>
                        </div>
                        <div class="form-group">
                            <label>с</label>
                            <input class="form-control" type="text" placeholder="Дата" id="fromDate" style="width: 100px;">
                            <label>по</label>
                            <input class="form-control" type="text" placeholder="Дата" id="toDate" style="width: 100px;">
                            <input type="hidden" id="rating-filter-weight" value="0" />
                        </div>
                        <div class="form-group" style="padding-left: 10px;">
                            <a id="rating-filter-weight-up" class="rating-filter-icon">
                                <i class="fa fa-thumbs-o-up"></i>
                            </a>
                            <a id="rating-filter-weight-down" class="rating-filter-icon">
                                <i class="fa fa-thumbs-o-down"></i>
                            </a>
                            <a id="rating-filter-weight-all" style="color: blue;" class="rating-filter-icon">
                                <i class="fa fa-globe"></i>
                            </a>
                            <a id="rating-clear-filter" style="color: black;" class="rating-filter-icon">
                                <i class="fa fa-refresh"></i>
                            </a>
                        </div>
                    </div>
                    <div class="form-inline" style="padding-top: 5px;">
                        <span class="checkbox" style="float: right;">
                            <input type="checkbox" id="allRatingRecords" style="padding-right: 5px;">
                            <label>все детали</label>
                        </span>
                    </div>
                </div>
                <div id="table-rating-details"></div>
			</div>
			<div class="modal-footer">
                <div class="form-inline">
                <div style="float: left; padding-top: 5px;">
                    Всего голосовавших: <span id="rating-user-count" style="font-weight: bold; font-size: 16px;">0</span>
                    <span id="rating-my-details"></span>
                </div>
                <div style="float: right;">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                </div>
                    </div>
			</div>
		</div>
	</div>
</div>

<script id="rating-user-cell-template" type="x-tmpl-mustache">
    <div class="rating_userName tooltiped-avatar" data-sharer-ikp="{{user.ikp}}" data-data="{{user.fullName}}" style="cursor:pointer;">
        <img src="{{avatar}}" style="padding-right: 10px;"/>
        <a>{{user.fullName}}</a>
    </div>
</script>

<script type="text/javascript">
    $(document).ready(function () {
        RatingDetailsDialog.init();
    });

    var RatingDetailsDialog = {
        itemsPerPage: 10,
        init: function () {
            var that = this;
            that.weightInput = $("#rating-filter-weight");
            that.weightUp = $('#rating-filter-weight-up');
            that.weightDown = $('#rating-filter-weight-down');
            that.weightAll = $('#rating-filter-weight-all');

            $('#fromDate').radomDateInput();
            $('#toDate').radomDateInput();

            $("#fromDate").on("change", function(){$(radomEventsManager).trigger("rating.updateDetails");});
            $("#toDate").on("change", function(){$(radomEventsManager).trigger("rating.updateDetails");});
            $("#fromDate").on('changeDate', function (selected) {
                $("#toDate").datepicker('setStartDate',selected.date);
            });
            $("#toDate").on('changeDate', function (selected) {
                $("#fromDate").datepicker('setEndDate',selected.date);
            });
            $("#userNamePattern").on("change paste keyup", function() {
                if(($(this).val().length >= 3) || ($(this).val().length == 0) ) $(radomEventsManager).trigger("rating.updateDetails");
            });
            $('#allRatingRecords').click(function() {
                $(radomEventsManager).trigger("rating.updateDetails")
            });
            $('#rating-clear-filter').click($.proxy(function() {
                this.clearForm();
                $(radomEventsManager).trigger("rating.updateDetails")
            }, that));

            that.weightUp.radomTooltip({
                placement : "right",
                container: "body",
                title: 'Показать голоса ЗА'
            });

            that.weightDown.radomTooltip({
                placement : "right",
                container: "body",
                title: 'Показать голоса ПРОТИВ'
            });

            that.weightAll.radomTooltip({
                placement : "right",
                container: "body",
                title: 'Показать все голоса'
            });

            $('#rating-clear-filter').radomTooltip({
                placement : "right",
                container: "body",
                title: 'Очистить фильтр'
            });

            that.weightUp.click($.proxy(function() {
                that.filterByWeightUp();
            }, that));

            that.weightDown.click($.proxy(function() {
                that.filterByWeightDown()
            }, that));

            that.weightAll.click($.proxy(function() {
                var that = this;
                if(that.weightInput.val() == 0) return;
                that.weightInput.val(0);
                $(radomEventsManager).trigger("rating.updateDetails")
                that.weightUp.css("color", "grey");
                that.weightDown.css("color", "grey");
                that.weightAll.css("color", "blue");
            }, that));

            $('#table-rating-details').bootstrapTable({
                method: 'get',
                dataType: 'json',
                url: '/rating/page.json',
                sidePagination: 'server',
                queryParams: $.proxy(that.queryParams, that),
                cache: false,
                striped: true,
                pagination: true,
                pageSize: that.itemsPerPage,
                showHeader: false,
                showColumns: false,
                formatLoadingMessage: function() {
                    return 'Загрузка данных, пожалуйста подождите...'
                },
                formatShowingRows: function(pageFrom, pageTo, totalRows) {
                    return 'Показаны с ' + pageFrom + ' по ' + pageTo + ' из ' + totalRows + ' записей'
                },
                formatRecordsPerPage: function (pageNumber) {
                    return "";//pageNumber + ' записей на страницу';
                },
                formatNoMatches: function () {
                    return 'Ничего не найдено';
                },
                columns: [
                    {
                        field: 'created',
                        title: 'Дата',
                        align: 'center',
                        valign: 'middle',
                        width: 150,
                        sortable: false,
                        formatter: function(data){
                            return '<span class="rating_created" data-data="' + data + '"><a style="cursor:pointer;">' + new Date(data).format("dd.mm.yyyy HH:mm") + '</a></span>';
                        }
                    },
                    {
                        field: 'user',
                        title: 'Пользователь',
                        align: 'left',
                        valign: 'middle',
                        width: 350,
                        sortable: false,
                        formatter: function (user, data) {
                            var model = {
                                avatar: Images.getResizeUrl(user.avatar, "c40"),
                                user: user
                            };
                            return Mustache.to_html($('#rating-user-cell-template').html(), model);
                        }
                    },
                    {
                        field: 'weight',
                        title: 'За/Против',
                        align: 'center',
                        valign: 'middle',
                        width: 66,
                        sortable: false,
                        formatter: function (data){
                            var cssClass = (data >= 0) ? 'fa fa-thumbs-o-up' : 'fa fa-thumbs-o-down';
                            var color = (data >= 0) ? "green" : "red";
                            var cell = '<a class="rating_weight" data-data="'+ data + '"style="cursor: pointer; font-size: 18px; color: '
                                    + color + '"><i class="' + cssClass + '"></i></a>';
                            return cell;
                        }
                    }
                ]
            });

            $(radomEventsManager).bind("rating.updateDetails", $.proxy(that.reloadData, that));
            $("#table-rating-details").on("click", ".rating_created", function(){
                var data = $(this).data("data");
                if(data) {
                    $("#fromDate").datepicker("setDate", data.substring(0, 10));
                    $("#toDate").datepicker("setDate", data.substring(0, 10));
                    $(radomEventsManager).trigger("rating.updateDetails");
                }

            });
            $("#table-rating-details").on("click", ".rating_userName", function() {
                var data = $(this).data("data");
                if (data) {
                    $("#userNamePattern").val(data);
                    $(radomEventsManager).trigger("rating.updateDetails");
                }
            });
            $("#table-rating-details").on("click", ".rating_weight", $.proxy(function (e) {
                var data = $(e.target).parent().data("data");
                var that = this;
                if (data) {
                    if(data > 0){
                        if(that.weightInput.val() > 0) return;
                        that.filterByWeightUp()
                    } else {
                        if(that.weightInput.val() < 0) return;
                        that.filterByWeightDown()
                    }
                }
            }, that));

        },
        queryParams: function(params){
            var that = this;
            if(that.contentId && that.contentType){
                return {
                    offset: params.offset | 0,
                    limit: params.limit,
                    contentId: that.contentId,
                    contentType: that.contentType,
                    userNamePattern: encodeURI($("#userNamePattern").val()),
                    fromDate: $("#fromDate").val(),
                    toDate: $("#toDate").val(),
                    weight: that.weightInput.val() | 0,
                    onlyActive: !($("#allRatingRecords").is(":checked"))
                }
            }
            return false;
        },

        reloadData: function(){
            $('#table-rating-details').bootstrapTable("refresh");
        },
        updateUsersCount: function (data) {
            $('#rating-user-count').text(data.count);
            if(data.me){
                $('#rating-my-details').html(', Вы голосовали' +
                ((data.me.weight > 0) ?
                        '<i class="fa fa-thumbs-o-up" style="font-size: 18px; color: green; padding: 0 8px;";></i>' :
                        '<i class="fa fa-thumbs-o-down" style="font-size: 18px; color: red; padding: 0 8px;"></i>')
                + new Date(data.me.created).format("dd.mm.yyyy HH:mm"));
            } else {
                $('#rating-my-details').text('');
            }
        },
        filterByWeightUp: function(){
            var that = this;
            if(that.weightInput.val() > 0) return;
            that.weightInput.val(1);
            $(radomEventsManager).trigger("rating.updateDetails")
            that.weightUp.css("color", "green");
            that.weightDown.css("color", "grey");
            that.weightAll.css("color", "grey");
        },
        filterByWeightDown: function(){
            var that = this;
            if(that.weightInput.val() < 0) return;
            that.weightInput.val(-1);
            $(radomEventsManager).trigger("rating.updateDetails")
            that.weightUp.css("color", "grey");
            that.weightDown.css("color", "red");
            that.weightAll.css("color", "grey");
        },
        clearForm: function (){
            var that = this;
            $('#fromDate').val(null);
            $('#toDate').val(null);
            $('#userNamePattern').val(null);
            $('#allRatingRecords').prop("checked", false);
            that.weightInput.val(0);
            that.weightUp.css("color", "grey");
            that.weightDown.css("color", "grey");
            that.weightAll.css("color", "blue");
        },
        show: function (contentId, contentType, contentTitle) {
            var that = this;
            that.contentId = contentId;
            that.contentType = contentType;
            that.clearForm();
            $('#rating-details-title').html(contentTitle);
            $.radomJsonPost("/rating/countUsers.json", {
                contentId: that.contentId,
                contentType: that.contentType
            }, $.proxy(that.updateUsersCount, that));

            $("div#rating-details-modal").modal("show");
            that.reloadData();
        }

    };
	
</script>