<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="taskManagementTaskModalWindow.jsp" %>
<%@include file="taskManagementProblemModalWindow.jsp" %>
<%@include file="taskManagementCopyModalWindow.jsp" %>
<%@include file="taskManagementNewObjectWizardModalWindow.jsp" %>

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
            onSelect: function (item) {
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

    $(document).ready(function() {
        $("#button-add-task").click(function() {
            showTaskModalWindow(null);
        });

        $("#button-add-problem").click(function() {
            showProblemModalWindow(null);
        });

        $("#button-add-copy").click(function() {
            $("#copyModalWindow").modal({backdrop:false, keyboard:false});
        });

        $("#button-new-object").click(function() {
            $("#newObjectWizardModalWindow").modal({backdrop:false, keyboard:false});
        });
    });

    function clearFieldAttributes(control, useLabel) {
        setFieldAttributes(control, "", "", useLabel);
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
</script>

<%@include file="cyberbrainSections.jsp" %>

<hr/>

<h1>Цели и дела</h1>

<hr/>

<button type="submit" class="btn btn-primary" id="button-add-task">
	<span class="glyphicon glyphicon-plus"></span> Новое задание
</button>

<button type="submit" class="btn btn-primary" id="button-add-problem">
    <span class="glyphicon glyphicon-plus"></span> Проблема / Научить
</button>

<button type="submit" class="btn btn-primary" id="button-add-copy">
    <span class="glyphicon glyphicon-plus"></span> Копия
</button>

<button type="submit" class="btn btn-primary" id="button-new-object">
    <span class="glyphicon glyphicon-plus"></span> Новый объект
</button>

<hr />

<form role="form" method="post" enctype="multipart/form-data">
	<div class="row">
		<div class="col-xs-6">
			<%@include file="taskManagementGridSubcontractors.jsp" %>
		</div>

		<div class="col-xs-6">
			<%@include file="taskManagementGridCustomers.jsp" %>
		</div>
	</div>

	<hr/>

    <%@include file="taskManagementGridGoals.jsp" %>

    <hr/>
</form>