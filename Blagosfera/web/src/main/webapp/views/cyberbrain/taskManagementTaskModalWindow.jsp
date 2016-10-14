<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript">
    // Глобальные переменные для задач
    var dataUserTask;
    var dataUserSubtasks;
    var taskDateExecution;

    // Глобальные переменные для хранилищ данных
    var storeSubcontractors;
    var storeCustomers;
    var storeGoals;

    var taskModalWindow;

    function showTaskModalWindow(data) {
        dataUserTask = data;
        dataUserSubtasks = [];

        taskModalWindow.on("click", "a.subtask-delete-link", function() {
            var id = parseInt($(this).attr("data-subtask-id"));

            if (dataUserSubtasks.indexOf(id) != -1) {
                dataUserSubtasks.splice(dataUserSubtasks.indexOf(id), 1);
            }

            taskModalWindow.find("a[data-subtask-id=" + id + "]").parent().remove();
            return false;
        });

        if (dataUserTask == null) {
            dataUserTask = {
                id: -1,
                performer_id: -1,
                performer_name: "",
                date_execution: "",
                description: "",
                customer_id: -1,
                customer_name: "",
                object_id: -1,
                object_name: "",
                community_id: -1,
                community_name: "",
                from_status_id: -1,
                from_status_name: "",
                to_status_id: -1,
                to_status_name: "",
                lifecycle_id: -1
            };

            if ($("#task-combobox-communities").html() == "") {
                $("#task-subtask").popover("enable");
                $("#task-object").popover("enable");
            }
        } else {
            taskModalWindow.find(".modal-title").text("Задача № " + dataUserTask.id);
            $("#task-combobox-communities").val(dataUserTask.community_name);
            $("#task-combobox-communities").attr("disabled", "disabled");
            $("#task-combobox-communities").selectpicker("refresh");

            taskDateExecution.setValue(new Date(dataUserTask.date_execution));

            $("#task-subtask").popover("disable");
            $("#task-object").popover("disable");

            if (dataUserTask.is_customer == 0 || dataUserTask.is_customer == 2) {
                $("#task-btn-save").text("Решить");
            } else {
                if (dataUserTask.lifecycle_id == 1) {
                    $("#task-btn-reject").css({"display": "inline"});
                    $("#task-btn-confirm").css({"display": "inline"});
                }
            }

            getTrackObjectStatusFromToInfo(dataUserTask.object_id, dataUserTask.object_name);
            getSubtasks(dataUserTask, dataUserSubtasks)
        }

        $("#task-description").val(dataUserTask.description);
        setFieldAttributes("task-customer", dataUserTask.customer_id, dataUserTask.customer_name, true);
        setFieldAttributes("task-performer", dataUserTask.performer_id, dataUserTask.performer_name, true);
        setFieldAttributes("task-object", dataUserTask.object_id, dataUserTask.object_name, true);
        setFieldAttributes("task-from-object-status", dataUserTask.from_status_id, dataUserTask.from_status_name, false);
        setFieldAttributes("task-to-object-status", dataUserTask.to_status_id, dataUserTask.to_status_name, false);
        $("#task-subtask").val("");

        $("#taskModalWindow").modal({backdrop:false, keyboard:false});
    }

    function getCommunities() {
        $.ajax({
            type: "post",
            dataType: "json",
            data: "{}",
            url: "/cyberbrain/sections/get_user_communities.json",
            success: function (response) {
                if (response.success == true) {
                    response.items.forEach(function (entry) {
                        $("#task-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                    });
                }
            },
            error: function () {
                console.log("ajax error");
            }
        });
    }

    function getTrackObjectStatusFromToInfo(knowledgeId, object) {
        if (knowledgeId != null && knowledgeId !== 'undefined' && knowledgeId !== "") {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{'knowledgeId':" + knowledgeId + "}",
                url: "/cyberbrain/taskManagement/get_track_object_status_from_to_info.json",
                success: function (response) {
                    setFieldAttributes("task-from-object-status", response.status_from_id, response.status_from_name, false);
                    setFieldAttributes("task-to-object-status", response.status_to_id, response.status_to_name, false);

                    $("#track-info-properties-list").empty();
                    if (response.tag_many_id != "") {
                        $("#track-info-panel").css({"display": "block"});
                        $("#track-info-panel-title").html(object + " это " + response.tag_many_name);

                        response.tag_many_properties.forEach(function (entry) {
                            var jsonObject = JSON.parse(entry);
                            $("ul#track-info-properties-list").append("<li>" + jsonObject.property + ": " + jsonObject.property_value + "</li>");
                        });
                    } else if (response.tag_many_name != "") {
                        $("#track-info-panel").css({"display": "block"});
                        $("#track-info-panel-title").html("Нет данных по множеству!");
                        $("ul#track-info-properties-list").append("<li>Нет данных по свойствам!</li>");
                    } else {
                        $("#track-info-panel").css({"display": "none"});
                    }

                    $("#conditions-panel").css({"display": "block"});
                    $("#conditions-panel").css({"top": $("#left-content").position().top + $("#left-content").height()});
                    $("#conditions-panel").css({"width": $("#left-content").width()});

                    $("#conditions-list").empty();
                    if (response.conditions != "") {
                        response.conditions.forEach(function (entry) {
                            var style = "margin-top:5px;padding-left:5px;-moz-border-radius:5px;border-radius:5px;";
                            if (entry.is_topical == "null") {
                                style += "background-color:#FFCCCC;";
                            } else {
                                style += "background-color:#99FF99;";
                            }

                            $("ol#conditions-list").append("<li style='" + style + "'>Множество:" + entry.tag_many_name + " Объект:" + entry.tag_object_name + " Состояние:" + entry.custom_status_name + "</li>");
                        });
                    } else {
                        $("ol#conditions-list").append("<li>Для данното объекта нет условий!</li>");
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }
    }

    function getSubtasks(data, array) {
        $.ajax({
            type: "post",
            dataType: "json",
            data: data,
            url: "/cyberbrain/taskManagement/get_subtasks_by_user_task_id.json",
            success: function (response) {
                if (response.result == "error") {
                    bootbox.alert(response.message);
                } else {
                    response.forEach(function (entry) {
                        array.push(entry.id);
                        $("ul#task-subtasks-list").append("<li>" + entry.description + " <a data-subtask-id='" + entry.id + "' class='subtask-delete-link glyphicon glyphicon-remove' href='#'></a></li>");
                    });
                }
            },
            error: function () {
                console.log("ajax error");
            }
        });
    }

    $(document).ready(function() {
        Ext.onReady(function () {
            taskDateExecution = Ext.create('Ext.form.field.Date', {
                renderTo: 'task-date-execution',
                xtype: 'datefield',
                name: 'task-date-execution',
                format: 'Y-m-d',
                width: '100%',
                listeners: {
                    change: function (t, n, o) {
                    },
                    select: function (t, n, o) {
                    }
                }
            });
        });

        taskModalWindow = $("#taskModalWindow");
        prepareSharerControl(taskModalWindow, "task-performer");
        prepareSharerControl(taskModalWindow, "task-customer");
        getCommunities();

        prepareSubtaskControl(taskModalWindow);
        function prepareSubtaskControl(window) {
            var $input = window.find("input#task-subtask");

            $input.typeahead({
                onSelect: function (item) {
                    if ($.inArray(parseInt(item.value), dataUserSubtasks)) {
                        dataUserSubtasks.push(parseInt(item.value));
                        $("ul#task-subtasks-list").append("<li>" + item.text + " <a data-subtask-id='" + item.value + "' class='subtask-delete-link glyphicon glyphicon-remove' href='#'></a></li>");
                    } else {
                        bootbox.alert("Задача \"" + item.text + "\" уже добавленна!");
                    }
                },
                ajax: {
                    url: "/cyberbrain/taskManagement/search.json",
                    timeout: 500,
                    displayField: "description",
                    triggerLength: 1,
                    method: "post",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        var communityId = window.find("#task-combobox-communities option:selected").attr("data-community-id");
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

        prepareTrackObjectControl("taskModalWindow", "task-object");
        function prepareTrackObjectControl(wnd, control) {
            var window = $("#" + wnd);
            var $input = window.find("input#" + control);

            $input.typeahead({
                onSelect: function (item) {
                    setFieldAttributes(control, item.value, item.text, true);
                    getTrackObjectStatusFromToInfo(item.value, item.text)
                },
                ajax: {
                    url: "/cyberbrain/taskManagement/get_track_object.json",
                    timeout: 500,
                    displayField: "tag_owner_name",
                    triggerLength: 1,
                    method: "post",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        var communityId = window.find("#task-combobox-communities option:selected").attr("data-community-id");
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

        taskModalWindow.on("hidden.bs.modal", function () {
            taskModalWindow.find(".modal-title").text("Создать новое задание");
            $("#task-combobox-communities").removeAttr("disabled");
            $("#task-combobox-communities").selectpicker("refresh");

            clearFieldAttributes("task-customer", true);
            clearFieldAttributes("task-performer", true);
            clearFieldAttributes("task-object", true);
            clearFieldAttributes("task-from-object-status", false);
            clearFieldAttributes("task-to-object-status", false);

            $("#task-btn-reject").css({"display": "none"});
            $("#task-btn-confirm").css({"display": "none"});
            $("#task-btn-save").text("Сохранить");

            $("#track-info-properties-list").empty();
            $("#conditions-list").empty();
            $("#task-subtasks-list").empty();
            $("#task-description").val("");
            $("#task-subtask").val("");
            taskDateExecution.setValue(null);
            $("#track-info-panel").css({"display":"none"});
            $("#conditions-panel").css({"display": "none"});
        });

        $("#task-btn-reject").click(function() {
            saveUserTask(1);
            return false;
        });

        $("#task-btn-confirm").click(function() {
            saveUserTask(2);
            return false;
        });

        $("#task-btn-save").click(function() {
            if (dataUserTask.is_customer == 0 || dataUserTask.is_customer == 2) {
                if (dataUserTask.lifecycle_id == 2) {
                    saveUserTask(-1);
                } else {
                    saveUserTask(1);
                }
            } else {
                if (dataUserTask.lifecycle_id != -1) {
                    saveUserTask(0);
                } else {
                    saveUserTask(1);
                }
            }

            return false;
        });

        function saveUserTask(lifecycleInc) {
            var communityId = $("#task-combobox-communities option:selected").attr("data-community-id");
            dataUserTask.community_id = communityId;

            dataUserTask.subtask_ids = dataUserSubtasks;

            if (taskDateExecution.validate() && taskDateExecution.getValue() != null) {
                dataUserTask.date_execution = taskDateExecution.value.getFullYear() + '/' + ('0' + (taskDateExecution.value.getMonth() + 1)).slice(-2) + '/' + ('0' + taskDateExecution.value.getDate()).slice(-2) + ' 00:00:00';
            } else {
                dataUserTask.date_execution = null;
            }

            dataUserTask.description = $("#task-description").val();
            dataUserTask.performer_id = $("#task-performer").attr("data-object-id");
            dataUserTask.performer_name = $("#task-performer").attr("data-object-name");
            dataUserTask.customer_id = $("#task-customer").attr("data-object-id");
            dataUserTask.customer_name = $("#task-customer").attr("data-object-name");

            dataUserTask.object_id = $("#task-object").attr("data-object-id");
            dataUserTask.object_name = $("#task-object").attr("data-object-name");
            dataUserTask.from_status_id = $("#task-from-object-status").attr("data-object-id");
            dataUserTask.from_status_name = $("#task-from-object-status").attr("data-object-name");
            dataUserTask.to_status_id = $("#task-to-object-status").attr("data-object-id");
            dataUserTask.to_status_name = $("#task-to-object-status").attr("data-object-name");

            if (dataUserTask.community_id == null || dataUserTask.community_id == "" || dataUserTask.community_id < 0 || dataUserTask.community_id == "undefined") {
                bootbox.alert("Объединение не указано!");
                return false;
            }

            if (dataUserTask.description == null || dataUserTask.description == "") {
                bootbox.alert("Текст задания не указан!");
                return false;
            }

            if (dataUserTask.customer_id == null || dataUserTask.customer_id == "" || dataUserTask.customer_id < 0) {
                bootbox.alert("Заказчик не указан!");
                return false;
            }

            if (dataUserTask.performer_id == null || dataUserTask.performer_id == "" || dataUserTask.performer_id < 0) {
                bootbox.alert("Исполнитель не указан!");
                return false;
            }

            if (dataUserTask.date_execution == null || dataUserTask.date_execution == "") {
                bootbox.alert("Дата исполнения не указана!");
                return false;
            }

            if (dataUserTask.object_id == null || dataUserTask.object_id == "" || dataUserTask.object_id < 0) {
                bootbox.alert("Объект тег не указан!");
                return false;
            }

            if (dataUserTask.from_status_id == null || dataUserTask.from_status_id == "" || dataUserTask.from_status_id < 0) {
                bootbox.alert("Поле 'Из какого состояния' не заполнено!");
                return false;
            }

            if (dataUserTask.to_status_id == null || dataUserTask.to_status_id == "" || dataUserTask.to_status_id < 0) {
                bootbox.alert("Поле 'В какое состояние' не заполнено!");
                return false;
            }

            // задаем следующий ЖЦ для задачи
            dataUserTask.lifecycle_id += lifecycleInc;

            var url = "/cyberbrain/taskManagement/addUserTask";

            if (dataUserTask.id >= 0) {
                url = "/cyberbrain/taskManagement/updateUserTask";
            }

            $.ajax({
                type : "post",
                dataType : "json",
                data : JSON.stringify(dataUserTask),
                url : url,
                success : function(response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                    } else {
                        taskModalWindow.modal("hide");

                        storeSubcontractors.load();
                        storeCustomers.load();
                        storeGoals.load();

                        // обновим счетчики
                        getCountsRecordsAndScore();
                    }
                },
                error : function() {
                    console.log("ajax error");
                }
            });
        }
    });
</script>

<!-- Modal -->
<div class="modal fade" id="taskModalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 80%;">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Создать новое задание</h4>
            </div>
            <div class="modal-body" style="overflow:auto;">
                <label id="task-community-lbl" >Объединение в рамках которого создается задача</label>
                <div class="form-group">
                    <select id="task-combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
                </div>

                <hr/>

                <div class="row">
                    <div class="col-xs-4" id="left-content">
                        <div class="form-group">
                            <label>Условия</label>
                            <input id="task-subtask" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя задачи"
                                   data-toggle="popover" data-trigger="" data-placement="bottom"
                                   data-content="Требуется указать объединение!"/>
                        </div>

                        <div class="form-group">
                            <ul id="task-subtasks-list"></ul>
                        </div>

                        <div class="panel panel-info" id="conditions-panel" style="display: none;position:absolute;">
                            <div class="panel-heading" id="conditions-panel-title">Условия</div>
                            <div class="panel-body" id="conditions-panel-content">
                                <ol id="conditions-list" style="margin: 0;padding-left: 20px"></ol>
                            </div>
                        </div>
                    </div>

                    <div class="col-xs-4">
                        <div class="form-group">
                            <label>Задание</label>
                            <input type="text" id="task-description" class="form-control" value="" />
                        </div>

                        <div class="form-group">
                            <label id="task-customer-lbl" data-caption="Заказчик:" data-caption-value="---"></label>
                            <input id="task-customer" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя заказчика" />
                        </div>

                        <div class="form-group">
                            <label id="task-performer-lbl" data-caption="Исполнитель:" data-caption-value="---"></label>
                            <input id="task-performer" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя исполнителя" />
                        </div>

                        <div class="form-group">
                            <label>Дата исполнения</label>
                            <div class="form-control" id="task-date-execution"></div>
                        </div>
                    </div>

                    <div class="col-xs-4">
                        <div class="form-group">
                            <label id="task-object-lbl" data-caption="Объект:" data-caption-value="---"></label>
                            <input id="task-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                                   data-toggle="popover" data-trigger="" data-placement="bottom"
                                   data-content="Требуется указать объединение!" />
                        </div>

                        <div class="form-group">
                            <label id="task-from-object-status-lbl">Из какого состояния</label>
                            <input id="task-from-object-status" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" disabled="disabled"/>
                        </div>

                        <div class="form-group">
                            <label id="task-to-object-status-lbl">В какое состояние</label>
                            <input id="task-to-object-status" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" disabled="disabled"/>
                        </div>

                        <div class="panel panel-info" id="track-info-panel" style="display: none;">
                            <div class="panel-heading" id="track-info-panel-title"></div>
                            <div class="panel-body" id="track-info-panel-content">
                                <ul id="track-info-properties-list" style="list-style-type: none;padding: 0;margin: 0;"></ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="task-btn-reject" class="btn btn-primary" style="display: none;float: left;">Отклонить</button>
                <button type="button" id="task-btn-confirm" class="btn btn-primary" style="display: none;">Подтвердить</button>
                <button type="button" id="task-btn-save" class="btn btn-primary">Сохранить</button>
                <button type="button" id="task-btn-close" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>