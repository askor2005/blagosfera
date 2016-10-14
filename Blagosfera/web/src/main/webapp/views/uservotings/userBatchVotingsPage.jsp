<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ include file="batchVotingsGrid.jsp" %>
<%@ include file="batchVotingVotersGrid.jsp" %>
<script>
    var currentBatchVotingId = null;
    var filterParameters = {
        startDateStart : null,
        startDateEnd : null,
        endDateStart : null,
        endDateEnd : null,
        ownerId : null,
        ownerName : "",
        subject : null,
        state : null
    };

    function initControlsView() {
        $.mask.definitions['0']='[0]';
        $.mask.definitions['1']='[0-1]';
        $.mask.definitions['2']='[0-2]';
        $.mask.definitions['3']='[0-3]';
        $("[data-type=DATE]").mask("39.19.2999", {placeholder: "_"}).attr("placeholder", "__.__.____");
        $("[data-type=DATE]").radomDateInput({
            startView : 2
        });
        $("#batchVotingState").selectpicker("refresh");
        $("#batchVotingState").selectpicker("val", null);
    }

    function filterBatchVotings() {
        $("#ownerName").val(filterParameters.ownerName);
        $("#subjectFilter").val(filterParameters.subject);
        $("#batchVotingState").selectpicker("refresh");
        $("#batchVotingState").selectpicker("val", filterParameters.state);

        $("#startDateStart").datepicker('update', filterParameters.startDateStart);
        $("#startDateEnd").datepicker('update', filterParameters.startDateEnd);
        $("#endDateStart").datepicker('update', filterParameters.endDateStart);
        $("#endDateEnd").datepicker('update', filterParameters.endDateEnd);

        batchVotingsStore.reload();
    }

    function initChangeDate($selector, dateIndex, onChangeFunc) {
        $selector.bind("changeDate", function(){
            var dateStr = $(this).val();
            if (dateStr.indexOf("_") > -1) {
                // do nothing
            } else {
                filterParameters[dateIndex] = $(this).val();
                onChangeFunc();
            }
        });
    }

    function initControls() {
        $("#toggleFilterBlock").click(function(){
            $("#filterBlock").slideToggle("fast", function() {
                // Animation complete.
            });
            return false;
        });

        initChangeDate($("#startDateStart"), "startDateStart", filterBatchVotings);
        initChangeDate($("#startDateEnd"), "startDateEnd", filterBatchVotings);
        initChangeDate($("#endDateStart"), "endDateStart", filterBatchVotings);
        initChangeDate($("#endDateEnd"), "endDateEnd", filterBatchVotings);

        $("#batchVotingState").change(function(){
            filterParameters.state = $(this).val();
            filterBatchVotings();
        });

        setInterval(function(){
            var newSubjectValue = $("#subjectFilter").val();
            newSubjectValue = newSubjectValue == "" ? null : newSubjectValue;
            if (filterParameters.subject != newSubjectValue) {
                filterParameters.subject = newSubjectValue;
                if (filterParameters.subject == null || filterParameters.subject.length > 2) {
                    filterBatchVotings();
                }
            }
        }, 500);

        $("body").on('click', '.batchVotingOwner', function(){
            filterParameters.ownerId = $(this).attr("owner_id");
            filterParameters.ownerName = $(this).attr("owner_name");
            filterBatchVotings();
            return false;
        });

        $("#clearOwner").click(function(){
            filterParameters.ownerId = null;
            filterParameters.ownerName = "";
            filterBatchVotings();
            return false;
        });
        $("#clearSubject").click(function(){
            filterParameters.subject = null;
            filterBatchVotings();
            return false;
        });
        $("#clearState").click(function(){
            filterParameters.state = null;
            filterBatchVotings();
            return false;
        });

        $("#clearStartDateStart").click(function(){
            filterParameters.startDateStart = null;
            filterBatchVotings();
            return false;
        });
        $("#clearStartDateEnd").click(function(){
            filterParameters.startDateEnd = null;
            filterBatchVotings();
            return false;
        });
        $("#clearEndDateStart").click(function(){
            filterParameters.endDateStart = null;
            filterBatchVotings();
            return false;
        });
        $("#clearEndDateEnd").click(function(){
            filterParameters.endDateEnd = null;
            filterBatchVotings();
            return false;
        });


        $("body").on('click', '.batchVotingState', function(){
            filterParameters.state = $(this).attr("state");

            $("#batchVotingState").selectpicker("val", filterParameters.state);
            $("#batchVotingState").selectpicker("refresh");
            filterBatchVotings();
            return false;
        });


        $("body").on('click', '.batchVotingStartDate', function(){
            var startDate = $(this).attr("start_date");
            $("#startDateStart").datepicker('update', startDate);
            $("#startDateEnd").datepicker('update', startDate);
            filterParameters.startDateStart = $("#startDateStart").val();
            filterParameters.startDateEnd = $("#startDateEnd").val();
            filterBatchVotings();
            return false;
        });
        $("body").on('click', '.batchVotingEndDate', function(){
            var startDate = $(this).attr("end_date");
            $("#endDateStart").datepicker('update', startDate);
            $("#endDateEnd").datepicker('update', startDate);
            filterParameters.endDateStart = $("#endDateStart").val();
            filterParameters.endDateEnd = $("#endDateEnd").val();
            filterBatchVotings();
            return false;
        });

        $("body").on('click', '.batchVotingVoters', function(){
            currentBatchVotingId = $(this).attr("batch_voting_id");
            $("#batchVotingVoters").modal("show");
            return false;
        });

        $("#batchVotingVoters").on("shown.bs.modal", function () {
            initBatchVotingVotersGrid(currentBatchVotingId);
        });

        $("#batchVotingVoters").on("hidden.bs.modal", function () {
            clearBatchVotingVotersGrid();
        });
    }

    $(document).ready(function() {
        initBatchVotingsGrid(filterParameters);
        initControlsView();
        initControls();
    });
</script>

<h2>Все собрания с моим участием</h2>
<hr/>
<div>
    <h4><a href="#" id="toggleFilterBlock">Фильтрация собраний</a></h4>
    <div id="filterBlock" style="display: none;">
        <div>
            <div style="display: inline-block; width: 50%; vertical-align: top;">
                <div class="form-group" style="margin-right: 5px;">
                    <label>Дата начала собрания от</label>
                    <div style="height: 34px; position: relative;">
                        <div style="position: absolute; left: 0px; right: 50px;">
                            <input type="text" class="form-control" id="startDateStart" data-type="DATE"/>
                        </div>
                        <div style="position: absolute; right: 0px;">
                            <button class="btn btn-warning" style="height: 34px;" id="clearStartDateStart" title="Очистить">
                                <i class='fa fa-fw fa-times'></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div><div style="display: inline-block; width: 50%; vertical-align: top;">
                <div class="form-group" style="margin-left: 5px;">
                    <label>Дата начала собрания до</label>
                    <div style="height: 34px; position: relative;">
                        <div style="position: absolute; left: 0px; right: 50px;">
                            <input type="text" class="form-control" id="startDateEnd" data-type="DATE"/>
                        </div>
                        <div style="position: absolute; right: 0px;">
                            <button class="btn btn-warning" style="height: 34px;" id="clearStartDateEnd" title="Очистить">
                                <i class='fa fa-fw fa-times'></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div style="display: inline-block; width: 50%;">
                <div class="form-group" style="margin-right: 5px;">
                    <label>Дата окончания собрания от</label>
                    <div style="height: 34px; position: relative;">
                        <div style="position: absolute; left: 0px; right: 50px;">
                            <input type="text" class="form-control" id="endDateStart" data-type="DATE"/>
                        </div>
                        <div style="position: absolute; right: 0px;">
                            <button class="btn btn-warning" style="height: 34px;" id="clearEndDateStart" title="Очистить">
                                <i class='fa fa-fw fa-times'></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div><div style="display: inline-block; width: 50%;">
                <div class="form-group" style="margin-left: 5px;">
                    <label>Дата окончания собрания до</label>
                    <div style="height: 34px; position: relative;">
                        <div style="position: absolute; left: 0px; right: 50px;">
                            <input type="text" class="form-control" id="endDateEnd" data-type="DATE"/>
                        </div>
                        <div style="position: absolute; right: 0px;">
                            <button class="btn btn-warning" style="height: 34px;" id="clearEndDateEnd" title="Очистить">
                                <i class='fa fa-fw fa-times'></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div class="form-group">
                <label>Статус собрания</label>
                <div style="height: 34px; position: relative;">
                    <div style="position: absolute; left: 0px; right: 50px;">
                        <select id="batchVotingState" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            <option value="VOTING">Идёт голосование</option>
                            <option value="VOTERS_REGISTRATION">Регистрация в собрании</option>
                            <option value="FINISHED">Собрание завершено</option>
                        </select>
                    </div>
                    <div style="position: absolute; right: 0px;">
                        <button class="btn btn-warning" style="height: 34px;" id="clearState" title="Очистить">
                            <i class='fa fa-fw fa-times'></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div class="form-group">
                <label>Название собрания или названия голосования</label>
                <div style="height: 34px; position: relative;">
                    <div style="position: absolute; left: 0px; right: 50px;">
                        <input type="text" class="form-control" id="subjectFilter"/>
                    </div>
                    <div style="position: absolute; right: 0px;">
                        <button class="btn btn-warning" style="height: 34px;" id="clearSubject" title="Очистить">
                            <i class='fa fa-fw fa-times'></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label>Автор собрания</label>
            <div style="height: 34px; position: relative;">
                <div style="position: absolute; left: 0px; right: 50px;">
                    <input type="text" class="form-control" id="ownerName" disabled="disabled"/>
                </div>
                <div style="position: absolute; right: 0px;">
                    <button class="btn btn-warning" style="height: 34px;" id="clearOwner" title="Очистить">
                        <i class='fa fa-fw fa-times'></i>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<div>
    <div id="batchVotings-grid"></div>
    <div id="batchVotingsGridSearchResult"></div>
</div>

<div class="modal fade" id="batchVotingVoters" tabindex="-1" role="dialog" aria-hidden="true" data-keyboard="false">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title">Участники собрания</h4>
            </div>
            <div class="modal-body">
                <div id="batchVotingVoters-grid"></div>
                <div id="batchVotingVotersGridSearchResult"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>