<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="taskManagementProblemModalWindow.jsp" %>

<script type="text/javascript">
	var myPageSize = 25;

    function prepareSharerControl(window, control) {
        var $input = window.find("input#" + control);

        $input.typeahead({
            onSelect: function(item) {
                setFieldAttributes(control, item.value, item.text, true);
            },
            ajax: {
                url: "/contacts/search.json",
                timeout: 500,
                displayField: "fullName",
                triggerLength: 1,
                method: "post",
                loadingClass: "loading-circle",
                preDispatch: function (query) {
                    return {
                        query : query,
                        include_context_sharer : true
                    }
                },
                preProcess: function (response) {
                    if (response.result == "error") {
                        console.log("ajax error");
                        return false;
                    }
                    return response;
                }
            }
        });
    }

    function prepareObjectControl(wnd, control, prefix) {
        var window = $("#" + wnd);
        var $input = window.find("input#" + control);

        $input.typeahead({
            onSelect: function(item) {
                if (wnd === "taskModalWindow") {
                    setFieldAttributes(control, item.value, item.text, true);
                } else {
                    setFieldAttributes(control, item.value, item.text, false);
                }
            },
            ajax: {
                url: "/cyberbrain/thesaurus/search.json",
                timeout: 500,
                displayField: "essence",
                triggerLength: 1,
                method: "post",
                loadingClass: "loading-circle",
                preDispatch: function (query) {
                    var communityId = window.find("#" + prefix + "-combobox-communities option:selected").attr("data-community-id");
                    if (communityId == "undefined") {
                        communityId = -1;
                    }

                    return {query: query, communityId: communityId}
                },
                preProcess: function (response) {
                    if (response.result == "error") {
                        console.log("ajax error");
                        return false;
                    }
                    return response;
                }
            }
        });
    }

    function setFieldAttributes(control, id, value, useLabel) {
        if (useLabel) {
            if (value === "") {
                $("#" + control + "-lbl").attr("data-caption-value", "---");
            } else {
                $("#" + control + "-lbl").attr("data-caption-value", value);
            }
        }

        $("#" + control).attr("data-object-id", id);
        $("#" + control).attr("data-object-name", value);

        if (useLabel) {
            var caption = $("#" + control + "-lbl").attr("data-caption");
            var caption_value = $("#" + control + "-lbl").attr("data-caption-value");
            $("#" + control + "-lbl").html(caption + " " + caption_value);
        }

        $("#" + control).val(value);
    }

	$(document).ready(function() {
        getCommunities();
        function getCommunities() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: "/cyberbrain/sections/get_user_communities.json",
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#questions-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        $("#questions-combobox-communities").on("change", function() {
            storeQuestions.load();
            storeQuestionsMany.load();
            storeQuestionsProperties.load();
            storeQuestionsTracks.load();
            storeUserProblems.load();
        });

		$("a#refresh-questions").click(function() {
			storeQuestions.load();
			return false;
		});

		$("a#refresh-questions-many").click(function() {
			storeQuestionsMany.load();
			return false;
		});

		$("a#refresh-questions-properties").click(function() {
			storeQuestionsProperties.load();
			return false;
		});

        $("a#refresh-questions-tracks").click(function() {
            storeQuestionsTracks.load();
            return false;
        });
	});
</script>

<%@include file="cyberbrainSections.jsp" %>

<h1>Вопросы</h1>

<hr/>

<form role="form" method="post" enctype="multipart/form-data">
    <label>Отобразить вопросы для объединения</label>
    <div class="form-group">
        <select id="questions-combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
    </div>
    <hr/>

	<div class="row">
		<div class="col-xs-6">
			<div class="row">
				<div class="col-xs-9">
					<div class="form-group">
						<label>Фильтр по тегу</label>
						<input id="questions-tag-filter" type="text" autocomplete="off" class="form-control" />
					</div>
				</div>

				<div class="col-xs-3">
					<div class="form-group">
						<label>&nbsp;</label>
						<a href="#" class="btn btn-default btn-block" id="refresh-questions">Обновить</a>
					</div>
				</div>
			</div>

			<%@include file="knowledgeRepositoryGridQuestions.jsp" %>
		</div>

		<div class="col-xs-6">
			<div class="row">
				<div class="col-xs-9">
					<div class="form-group">
						<label>Фильтр по множеству</label>
						<input id="questions-many-filter" type="text" autocomplete="off" class="form-control" />
					</div>
				</div>

				<div class="col-xs-3">
					<div class="form-group">
						<label>&nbsp;</label>
						<a href="#" class="btn btn-default btn-block" id="refresh-questions-many">Обновить</a>
					</div>
				</div>
			</div>

			<%@include file="knowledgeRepositoryGridQuestionsMany.jsp" %>
		</div>
	</div>

	<hr/>

	<div class="row">
		<div class="col-xs-3">
			<div class="form-group">
				<label>Фильтр по множеству</label>
				<input id="questions-properties-many-filter" type="text" autocomplete="off" class="form-control" />
			</div>
		</div>

		<div class="col-xs-3">
			<div class="form-group">
				<label>Фильтр по тегу</label>
				<input id="questions-properties-tag-filter" type="text" autocomplete="off" class="form-control" />
			</div>
		</div>

		<div class="col-xs-3">
			<div class="form-group">
				<label>Фильтр по свойству</label>
				<input id="questions-properties-property-filter" type="text" autocomplete="off" class="form-control" />
			</div>
		</div>

		<div class="col-xs-3">
			<div class="form-group">
				<label>&nbsp;</label>
				<a href="#" class="btn btn-default btn-block" id="refresh-questions-properties">Обновить</a>
			</div>
		</div>
	</div>

	<%@include file="knowledgeRepositoryGridQuestionsProperties.jsp" %>

	<hr/>

    <div class="row">
        <div class="col-xs-5">
            <div class="form-group">
                <label>Фильтр по множеству</label>
                <input id="questions-tracks-many-filter" type="text" autocomplete="off" class="form-control" />
            </div>
        </div>

        <div class="col-xs-5">
            <div class="form-group">
                <label>Фильтр по тегу</label>
                <input id="questions-tracks-tag-filter" type="text" autocomplete="off" class="form-control" />
            </div>
        </div>

        <div class="col-xs-2">
            <div class="form-group">
                <label>&nbsp;</label>
                <a href="#" class="btn btn-default btn-block" id="refresh-questions-tracks">Обновить</a>
            </div>
        </div>
    </div>

    <%@include file="knowledgeRepositoryGridQuestionsTracks.jsp" %>

    <hr/>

    <%@include file="taskManagementGridUserProblems.jsp" %>

    <hr/>
</form>