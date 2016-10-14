<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript">
    // Глобальные переменные
    var storeUserProblems;
    var tableProblemsRows = 1;
    var dataUserProblemId;
    var dataUserProblemPerformers;

    var problemModalWindow;

    function showProblemModalWindow(data) {
        tableProblemsRows = 0;
        dataUserProblemPerformers = [];

        problemModalWindow.on("click", "a.problem-delete-link", function() {
            var $problemId = parseInt($(this).attr("data-problem-id"));

            dataUserProblemPerformers.forEach(function(entry) {
                if (entry[0] == $problemId) {
                    var index = dataUserProblemPerformers.indexOf(entry);

                    if (index != -1) {
                        dataUserProblemPerformers.splice(index, 1);
                    }
                }
            });

            $("#tableSectionB tr").each(function (i, row) {
                var $row = $(row);
                var $rowId = parseInt($row.find("a.problem-delete-link").attr("data-problem-id"));

                if ($problemId == $rowId) {
                    $row.remove();
                }
            });

            return false;
        });

        // Чистим таблицу
        $("#tableSectionB tr").each(function (i, row) {
            var $row = $(row);

            if (i != 0) {
                $row.remove();
            }
        });

        prepareObjectControl("problemModalWindow", "problem-object", "problem");
        prepareObjectControl("problemModalWindow", "problem-many", "problem");
        prepareObjectControl("problemModalWindow", "problem-depends-object", "problem");
        prepareObjectControl("problemModalWindow", "problem-depends-many", "problem");
        prepareSharerControl(problemModalWindow, "problem-depends-performer");

        $('.nav-tabs a[href="#sectionA"]').tab("show");

        $("#problem-depends-object").val("");
        $("#problem-depends-many").val("");
        $("#problem-depends-performer-lbl").html("По моему мнению это должен был сделать: ---");
        $("#problem-depends-performer").val("");
        $("#problem-depends-performer").attr("data-object-id", "");
        $("#problem-depends-performer").attr("data-object-name", "");

        if (data == null) {
            dataUserProblemId = -1;
            problemModalWindow.find(".modal-title").text("Обозначить проблему");

            $("#data-combobox-communities").removeAttr("disabled");
            $("#data-combobox-communities").selectpicker("refresh");

            $("#problem-description").val("");
            $("#problem-object").val("");
            $("#problem-many").val("");

            if ($("#problem-combobox-communities").html() == "") {
                $("#problem-object").popover("enable");
                $("#problem-many").popover("enable");
                $("#problem-depends-object").popover("enable");
                $("#problem-depends-many").popover("enable");
            }
        } else {
            dataUserProblemId = data.id;
            problemModalWindow.find(".modal-title").text("Проблема № " + dataUserProblemId);

            $("#problem-combobox-communities").val(data.community_name);
            $("#problem-combobox-communities").attr("disabled", "disabled");
            $("#problem-combobox-communities").selectpicker("refresh");

            $("#problem-description").val(data.description);
            $("#problem-object").val(data.tag_object_name);
            $("#problem-many").val(data.tag_many_name);

            getProblems(data, dataUserProblemPerformers);

            $("#problem-object").popover("disable");
            $("#problem-many").popover("disable");
            $("#problem-depends-object").popover("disable");
            $("#problem-depends-many").popover("disable");
        }

        problemModalWindow.modal({backdrop:false, keyboard:false});
    }

    function getProblems(data, array) {
        $.ajax({
            type: "post",
            dataType: "json",
            data: data,
            url: "/cyberbrain/taskManagement/get_problems_by_user_problem_id.json",
            success: function (response) {
                if (response.result == "error") {
                    bootbox.alert(response.message);
                } else {
                    response.forEach(function (entry) {
                        var problemDependsObject = entry.tag_object.name;
                        var problemDependsMany = entry.tag_many.name;
                        var problemDependsPerformerId = entry.performer.id;

                        var arrayData = {
                            tag_object: problemDependsObject,
                            tag_many: problemDependsMany,
                            performer_id: problemDependsPerformerId
                        };

                        array.push([tableProblemsRows, problemDependsObject, problemDependsMany, problemDependsPerformerId, JSON.stringify(arrayData)]);

                        $('#tableSectionB tr').last().after(
                                "<tr><td style='display:none;'>" + tableProblemsRows + "</td>" +
                                "<td>" + problemDependsObject + "</td>" +
                                "<td>" + problemDependsMany + "</td>" +
                                "<td>" + entry.performer.name + "</td>" +
                                "<td><a data-problem-id='" + tableProblemsRows + "' class='problem-delete-link glyphicon glyphicon-remove' href='#'></a></td></tr>"
                        );

                        tableProblemsRows++;
                    });
                }
            },
            error: function () {
                console.log("ajax error");
            }
        });
    }

    $(document).ready(function() {
        $("table#tableSectionB").fixMe();

        problemModalWindow = $("#problemModalWindow");
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
                            $("#problem-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        $("#problem-btn-save").click(function() {
            var problemDescription = $("#problem-description").val();
            var problemObject = $("#problem-object").val();
            var problemMany = $("#problem-many").val();
            var communityId = $("#problem-combobox-communities option:selected").attr("data-community-id");

            if (communityId == null || communityId == "" || communityId < 0 || communityId == "undefined") {
                bootbox.alert("Объединение не указано!");
                return false;
            }

            if (problemDescription == null || problemDescription === "") {
                bootbox.alert("Поле 'Я не могу сделать' не заполнено!");
                return false;
            }

            if (problemObject == null || problemObject === "") {
                bootbox.alert("Поле 'Над объектом' не заполнено!");
                return false;
            }

            if (dataUserProblemPerformers.length == 0) {
                bootbox.alert("Нужно добавить хотя бы одно условие!");
                return false;
            }

            var url = "/cyberbrain/taskManagement/addUserProblem";

            if (dataUserProblemId >= 0) {
                url = "/cyberbrain/taskManagement/updateUserProblem";
            }

            var newArray = [];
            dataUserProblemPerformers.forEach(function (entry) {
                newArray.push(entry[4]);
            });

            var dataUserProblem = {
                id: dataUserProblemId,
                description: problemDescription,
                tag_object: problemObject,
                tag_many: problemMany,
                problems: newArray,
                community_id: communityId
            };

            $.ajax({
                type : "post",
                dataType : "json",
                data : JSON.stringify(dataUserProblem),
                url : url,
                success : function(response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                    } else {
                        problemModalWindow.modal("hide");

                        if (typeof storeUserProblems !== "undefined") {
                            storeUserProblems.load();
                        }

                        // обновим счетчики
                        getCountsRecordsAndScore();
                    }
                },
                error : function() {
                    console.log("ajax error");
                }
            });

            return false;
        });

        $("#problem-depends-btn-add").click(function() {
            var problemDependsObject = $("#problem-depends-object").val();
            var problemDependsMany = $("#problem-depends-many").val();
            var problemDependsPerformerId = $("#problem-depends-performer").attr("data-object-id");

            if (problemDependsObject == null || problemDependsObject === "") {
                bootbox.alert("Поле 'У меня нет объекта' не заполнено!");
                return false;
            }

            if (problemDependsPerformerId == null || problemDependsPerformerId === "") {
                bootbox.alert("Поле 'По моему мнению это должен был сделать' не заполнено!");
                return false;
            }

            var alreadyAdded = false;
            dataUserProblemPerformers.forEach(function (entry) {
                if (entry[1] === problemDependsObject && entry[2] === problemDependsMany && entry[3] === problemDependsPerformerId) {
                    bootbox.alert("Данное условие уже добавленно!");
                    alreadyAdded = true;
                }
            });
            if (alreadyAdded) return false;

            $("#tableSectionB tr").last().after(
                    "<tr><td style='display:none;'>" + tableProblemsRows + "</td>" +
                    "<td>" + $("#problem-depends-object").val() + "</td>" +
                    "<td>" + $("#problem-depends-many").val() + "</td>" +
                    "<td>" + $("#problem-depends-performer").attr("data-object-name") + "</td>" +
                    "<td><a data-problem-id='" + tableProblemsRows + "' class='problem-delete-link glyphicon glyphicon-remove' href='#'></a></td></tr>"
            );

            var arrayData = {
                tag_object: problemDependsObject,
                tag_many: problemDependsMany,
                performer_id: problemDependsPerformerId
            };

            dataUserProblemPerformers.push([tableProblemsRows, problemDependsObject, problemDependsMany, problemDependsPerformerId, JSON.stringify(arrayData)]);
            tableProblemsRows++;
        });

        problemModalWindow.on('hidden.bs.modal', function () {

        });
    });
</script>

<!-- Modal -->
<div class="modal fade" id="problemModalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Обозначить проблему</h4>
            </div>
            <div class="modal-body">
                <label id="problem-community-lbl">Объединение в рамках которого будет обозначена проблема</label>
                <div class="form-group">
                    <select id="problem-combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
                </div>

                <ul class="nav nav-tabs" role="tablist">
                    <li class="active"><a href="#sectionA" role="tab" data-toggle="tab">Задача</a></li>
                    <li><a href="#sectionB" role="tab" data-toggle="tab">Необходимые условия</a></li>
                </ul>

                <br/>

                <div class="tab-content">
                    <div class="tab-pane active" id="sectionA">
                        <div class="form-group">
                            <label id="problem-description-lbl" >Я не могу сделать</label>
                            <input id="problem-description" type="text" autocomplete="off" class="form-control" placeholder="Введите описание работы" />
                        </div>
                        <div class="form-group">
                            <label id="problem-object-lbl" >Над объектом</label>
                            <input id="problem-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                                   data-toggle="popover" data-trigger="" data-placement="left"
                                   data-content="Требуется указать объединение!" />
                        </div>
                        <div class="form-group">
                            <label id="problem-many-lbl" >Который относится к множеству</label>
                            <input id="problem-many" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя множества"
                                   data-toggle="popover" data-trigger="" data-placement="left"
                                   data-content="Требуется указать объединение!" />
                        </div>
                    </div>

                    <div class="tab-pane" id="sectionB">
                        <div class="form-group">
                            <label id="problem-depends-object-lbl" >У меня нет объекта</label>
                            <input id="problem-depends-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                                   data-toggle="popover" data-trigger="" data-placement="left"
                                   data-content="Требуется указать объединение!" />
                        </div>
                        <div class="form-group">
                            <label id="problem-depends-many-lbl" >Который относится к множеству</label>
                            <input id="problem-depends-many" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя множества"
                                   data-toggle="popover" data-trigger="" data-placement="left"
                                   data-content="Требуется указать объединение!" />
                        </div>
                        <div class="form-group">
                            <label id="problem-depends-performer-lbl" data-caption="По моему мнению это должен был сделать:" data-caption-value="---"></label>
                            <input id="problem-depends-performer" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя исполнителя" />
                        </div>
                        <div class="form-group">
                            <a id="problem-depends-btn-add" class="btn btn-primary">Добавить</a>
                        </div>

                        <table class="table" id="tableSectionB">
                            <tr>
                                <th style='display:none;'>#</th>
                                <th>У меня нет объекта</th>
                                <th>Который относится к множеству</th>
                                <th>По моему мнению это должен был сделать</th>
                                <th></th>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" id="problem-btn-save" class="btn btn-primary">Сохранить</button>
                <button type="button" id="problem-btn-close" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>