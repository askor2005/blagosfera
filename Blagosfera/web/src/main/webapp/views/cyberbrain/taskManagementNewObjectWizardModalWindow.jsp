<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="taskManagementNewObjectWizardGridStep1.jsp" %>
<%@include file="taskManagementNewObjectWizardGridStep5.jsp" %>
<%@include file="taskManagementNewObjectWizardGridStep6.jsp" %>
<%@include file="taskManagementNewObjectWizardGridStep7.jsp" %>

<script type="text/javascript">
    var previousTabIndex = 0;
    var tagId = -1;

    function generateGUID() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    }

    $(document).ready(function() {
        Ext.onReady(function () {
            newObjectTimeReady = Ext.create('Ext.form.field.Date', {
                renderTo: 'new-object-time-ready',
                xtype: 'datefield',
                name: 'new-object-time-ready',
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

        $("#new-object-wizard").bootstrapWizard({"tabClass": "nav nav-pills", onTabChange: function(tab, navigation, index) {

        }, onTabClick: function(tab, navigation, index) {
            return false;
        }, onPrevious: function(tab, navigation, index) {
            var $current = index + 1;
            previousTabIndex = $current + 1;

            if ($current == 2) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId > 0) {
                    $("#new-object-wizard").bootstrapWizard("enable", 1);
                }
            }
            if ($current == 4) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId < 0) {
                    $("#new-object-wizard").bootstrapWizard("enable", 3);
                }
            }
        }, onNext: function(tab, navigation, index) {
            var $current = index + 1;
            previousTabIndex = $current - 1;
            var selectedManyId = $("#new-object-new-many").attr("data-object-id");

            if (previousTabIndex == 1) {
                if ($("#new-object-new-many").val() == "") {
                    bootbox.alert("Введите наименование нового множества или выберите существующее!");
                    return false;
                }
            } else if (previousTabIndex == 2) {
                if ($("#new-object-new-many-properties").val() == "") {
                    bootbox.alert("Введите свойства множества!");
                    return false;
                }
            } else if (previousTabIndex == 4) {
                if (selectedManyId > 0 && $("#new-object-is-create-copy").prop("checked")) {
                    if ($("#new-object-source-object").attr("data-object-id") < 0 || $("#new-object-source-object").attr("data-object-id") == "") {
                        bootbox.alert("Объект оригинал не указан!");
                        return false;
                    }
                    if (!(newObjectTimeReady.validate() && newObjectTimeReady.getValue() != null)) {
                        bootbox.alert("Дата готовности не указана!");
                        return false;
                    }
                    if ($("#new-object-responsible").attr("data-object-id") < 0 || $("#new-object-responsible").attr("data-object-id") == "") {
                        bootbox.alert("Ответственный не указан!");
                        return false;
                    }
                }
            }

            if ($current == 2) {
                if (selectedManyId > 0) {
                    $("#new-object-wizard").bootstrapWizard("enable", 1);
                }
            } else if ($current == 3) {
                $("#new-object-new-object-name").attr($("#new-object-new-many").attr("data-object-id"));

                if (selectedManyId < 0 || selectedManyId == "") {
                    $("#new-object-new-object-name").val($("#new-object-new-many").val() + " 1");
                } else {
                    $("#new-object-new-object-name").val($("#new-object-new-many").val() + " " + $("#new-object-new-many").attr("data-is-numbered-value"));
                }
            } else if ($current == 4) {
                if (selectedManyId < 0) {
                    $("#new-object-wizard").bootstrapWizard("enable", 3);
                }
            }
        }, onTabShow: function(tab, navigation, index) {
            var $total = navigation.find("li").length;
            var $current = index + 1 ;

            if ($current == 1 || $current > 4) {
                $("#new-object-wizard").find(".pager .previous").hide();
                if ($current > 4) {
                    $("#new-object-combobox-communities").attr("disabled", "disabled");
                    $("#new-object-combobox-communities").selectpicker("refresh");
                }
            } else {
                $("#new-object-wizard").find(".pager .previous").show();
            }

            if ($current >= $total) {
                $("#new-object-wizard").find(".pager .next").hide();
                $("#new-object-btn-save").css({display: "block"});
            } else {
                $("#new-object-wizard").find(".pager .next").show();
                $("#new-object-btn-save").css({display: "none"});
            }

            if (previousTabIndex == 1 && $current == 2) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId > 0) {
                    $("#new-object-wizard").bootstrapWizard("next");
                    $("#new-object-wizard").bootstrapWizard("disable", 1);
                }
            } else if (previousTabIndex == 3 && $current == 2) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId > 0) {
                    $("#new-object-wizard").bootstrapWizard("previous");
                    $("#new-object-wizard").bootstrapWizard("disable", 1);
                }
            }

            if (previousTabIndex == 3 && $current == 4) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId < 0) {
                    $("#new-object-wizard").bootstrapWizard("next");
                    $("#new-object-wizard").bootstrapWizard("disable", 3);
                }
            } else if (previousTabIndex == 5 && $current == 4) {
                var selectedManyId = $("#new-object-new-many").attr("data-object-id");
                if (selectedManyId < 0) {
                    $("#new-object-wizard").bootstrapWizard("previous");
                    $("#new-object-wizard").bootstrapWizard("disable", 3);
                }
            }

            if ($current == 5) {
                createObject($current);
            } else if ($current == 7) {
                fillStoreStep7();
            }

            refreshGrids($current);
        }});

        function createObject(step) {
            var communityId = $("#new-object-combobox-communities option:selected").attr("data-community-id");
            if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                var data = {};
                data.step = step;
                data.community_id = communityId;
                data.new_many_id = $("#new-object-new-many").attr("data-object-id");
                data.new_many_name = $("#new-object-new-many").val();
                data.new_many_is_numbered = $("#new-object-new-many-is-numbered").prop("checked");
                data.new_many_properties = $("#new-object-new-many-properties").val();
                data.new_object_name = $("#new-object-new-object-name").val();
                data.copy_is_create_copy = $("#new-object-is-create-copy").prop("checked");
                data.copy_object_source_id = $("#new-object-source-object").attr("data-object-id");
                data.copy_object_source_name = $("#new-object-source-object").val();

                var step7Data = storeStep7.data;
                var newArray = [];
                step7Data.each(function(record) {
                    newArray.push(record.data);
                });
                data.affects = newArray;

                if (newObjectTimeReady.validate() && newObjectTimeReady.getValue() != null) {
                    data.copy_object_time_ready = newObjectTimeReady.value.getFullYear() + '/' + ('0' + (newObjectTimeReady.value.getMonth() + 1)).slice(-2) + '/' + ('0' + newObjectTimeReady.value.getDate()).slice(-2) + ' 00:00:00';
                } else {
                    data.copy_object_time_ready = "";
                }

                data.copy_object_responsible_id = $("#new-object-responsible").attr("data-object-id");
                data.copy_object_responsible_name = $("#new-object-responsible").val();

                $.ajax({
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(data),
                    url: "/cyberbrain/taskManagement/newObjectWizardForm",
                    success: function (response) {
                        if (step == 5) {
                            tagId = response.tagOwnerId;
                            storeStep5.load();
                        } else if (step == 8) {
                            $("#newObjectWizardModalWindow").modal("hide");
                            bootbox.alert("Сведения об объекте успешно сохранены.");

                            // обновим счетчики
                            getCountsRecordsAndScore();
                        }

                        refreshGrids(step);
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            }
        }

        function fillStoreStep7() {
            var data = {tagOwnerId : tagId};
            $.ajax({
                type: "post",
                dataType: "json",
                data: JSON.stringify(data),
                url: "/cyberbrain/taskManagement/new_object_wizard_form_get_tracks_for_object.json",
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#problem-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                            var manyName = $("#new-object-new-many").val();
                            var record = {
                                knowledge_rep_id : entry.knowledge_rep_id,
                                many_name : manyName,
                                object_name : entry.thesaurus_tag_new_object,
                                status_from : entry.thesaurus_tag_from_name,
                                status_to : entry.thesaurus_tag_to_name,
                                many_name_changeif : entry.thesaurus_tag_many,
                                object_name_changeif : entry.thesaurus_tag_owner,
                                read_only : entry.read_only};

                            storeStep7.add(record);
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        getCommunities();
        prepareObjectControl("newObjectWizardModalWindow", "new-object-source-object", "new-object");
        prepareSharerControl($("#newObjectWizardModalWindow"), "new-object-responsible");

        function getCommunities() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: "/cyberbrain/sections/get_user_communities.json",
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#new-object-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        $("#new-object-combobox-communities").on("change", function() {
            refreshGrids(1)
            clearFieldsStep1();
            clearFieldsStep2();
            clearFieldsStep4();
            $("#new-object-wizard").bootstrapWizard("first");
        });

        function refreshGrids(step) {
            if (step == 1) {
                if (Ext.getCmp("step1Grid") != undefined) {
                    Ext.getCmp("step1Grid").update();
                    storeStep1.load();
                }
            } else if (step == 5) {
                if (Ext.getCmp("step5Grid") != undefined) {
                    Ext.getCmp("step5Grid").update();
                    storeStep5.load();
                }
            } else if (step == 6) {
                if (Ext.getCmp("step6Grid") != undefined) {
                    Ext.getCmp("step6Grid").update();
                    storeStep6.load();
                }
            } else if (step == 7) {
                if (Ext.getCmp("step7Grid") != undefined) {
                    Ext.getCmp("step7Grid").update();
                    storeStep7.load();
                }
            }
        }

        $("#newObjectWizardModalWindow").on("shown.bs.modal", function() {
            refreshGrids(1);

            clearFieldsStep1();
            clearFieldsStep2();
            clearFieldsStep4();
        });

        $("#newObjectWizardModalWindow").on("hidden.bs.modal", function () {
            $("#new-object-wizard").bootstrapWizard("first");
            $("#new-object-btn-save").css({display: "none"});

            $("#new-object-combobox-communities").removeAttr("disabled");
            $("#new-object-combobox-communities").selectpicker("refresh");
        });

        $("#new-object-btn-save").click(function() {
            createObject(8);
        });

        //////////// Step 1
        function clearFieldsStep1() {
            $("#new-object-new-many-lbl").html($("#new-object-new-many-lbl").attr("data-caption-new"));
            $("#new-object-new-many").removeAttr("disabled");
            $("#new-object-new-many").attr("data-object-id", -1);
            $("#new-object-new-many").val("");
            $("#new-object-wizard").bootstrapWizard("enable", 1);
            $("#new-object-wizard").bootstrapWizard("disable", 3);

            $("#new-object-new-many-is-numbered").prop("checked", false);
            $("#new-object-new-many-is-numbered").removeAttr("disabled");

            $("#new-object-is-create-copy").prop("checked", false);

            storeStep7.clearData();
        }

        $("#new-object-new-many-clear").click(function() {
            clearFieldsStep1();
            clearFieldsStep2();
            clearFieldsStep4();
        });

        //////////// Step 2
        function clearFieldsStep2() {
            $("#new-object-new-many-properties").val("");
            $("#new-object-new-many-properties").attr("data-skip", false);
        }

        //////////// Step 4
        function clearFieldsStep4() {
            $("#new-object-source-object").val("");
            newObjectTimeReady.setValue(null);
            clearFieldAttributes("new-object-responsible", true);
        }
    });
</script>

<!-- Modal -->
<div class="modal fade" id="newObjectWizardModalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 80%" id="newObjectWizardModalDialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Новый объект</h4>
            </div>
            <div class="modal-body">
                <label>Объединение в рамках которого создается объект</label>
                <div class="form-group">
                    <select id="new-object-combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
                </div>

                <hr/>

                <div id="new-object-wizard">
                    <h4 style="float: left">Шаги обучения:&nbsp;&nbsp;</h4>
                    <ul style="alignment: center">
                        <li><a href="#tab1" data-toggle="tab">Шаг 1</a></li>
                        <li><a href="#tab2" data-toggle="tab">Шаг 2</a></li>
                        <li><a href="#tab3" data-toggle="tab">Шаг 3</a></li>
                        <li><a href="#tab4" data-toggle="tab">Шаг 4</a></li>
                        <li><a href="#tab5" data-toggle="tab">Шаг 5</a></li>
                        <li><a href="#tab6" data-toggle="tab">Шаг 6</a></li>
                        <li><a href="#tab7" data-toggle="tab">Шаг 7</a></li>
                    </ul>
                    <div class="tab-content">
                        <hr/>

                        <div class="tab-pane" id="tab1">
                            <div class="form-group">
                                <div class="form-group">
                                    <label>Вы хотите обучить меня знанию о новом объекте?</label>
                                    <div id="step1-grid"></div>
                                </div>

                                <div class="input-group">
                                    <span class="input-group-addon" id="new-object-new-many-lbl" data-caption-new="Ввод нового множества" data-caption-selected="Выбрано уже созданное множество"></span>
                                    <input type="text" class="form-control" id="new-object-new-many" placeholder="Введите здесь название нового множества">
                                    <span class="input-group-btn">
                                        <button class="btn btn-default" type="button" id="new-object-new-many-clear">Очистить</button>
                                    </span>
                                </div>

                                <div class="input-group">
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" id="new-object-new-many-is-numbered"> Признак номерного множества
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="tab-pane" id="tab2">
                            <div class="form-group">
                                <label>Такого множества я не знаю, укажите его свойства</label>
                                <input type="text" id="new-object-new-many-properties" data-skip="false" class="form-control" placeholder="Введите здесь свойства множества"/>
                            </div>
                        </div>

                        <div class="tab-pane" id="tab3">
                            <div class="form-group">
                                <label>Имя нового объекта</label>
                                <input type="text" id="new-object-new-object-name" class="form-control" value=""/>
                            </div>
                        </div>

                        <div class="tab-pane" id="tab4">
                            <div class="form-group">
                                <label>Укажите как будем создавать объект если множество было известно:</label>
                                <label>С чистого листа, на основе конкретного объекта того же множества, на основе усредненного объекта множества, на основе лучшего по NPV объекта множетсва</label>
                            </div>

                            <div class="form-group">
                                <label>
                                    <input type="checkbox" id="new-object-is-create-copy"> Создать копию на основании существующего объекта?
                                </label>
                            </div>

                            <div class="form-group">
                                <label id="new-object-source-object-lbl">Объект оригинал</label>
                                <input id="new-object-source-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                                       data-toggle="popover" data-trigger="" data-placement="left"
                                       data-content="Требуется указать объединение!" />
                            </div>

                            <div class="form-group">
                                <label>Дата готовности</label>
                                <div class="form-control" id="new-object-time-ready"></div>
                            </div>

                            <div class="form-group">
                                <label id="new-object-responsible-lbl" data-caption="Ответственный:" data-caption-value="---"></label>
                                <input id="new-object-responsible" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя исполнителя" />
                            </div>
                        </div>

                        <div class="tab-pane" id="tab5">
                            <div class="form-group">
                                <div id="step5-grid"></div>
                            </div>
                        </div>

                        <div class="tab-pane" id="tab6">
                            <div class="form-group">
                                <div id="step6-grid"></div>
                            </div>
                        </div>

                        <div class="tab-pane" id="tab7">
                            <div class="form-group">
                                <div id="step7-grid"></div>
                            </div>
                        </div>

                        <ul class="pager wizard">
                            <li class="previous"><a href="#"><span class="glyphicon glyphicon-arrow-left"></span> Назад</a></li>
                            <li class="next"><a href="#">Вперед <span class="glyphicon glyphicon-arrow-right"></span></a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="new-object-btn-save" class="btn btn-primary" style="display: none;float: left">Сохранить все сведения об объекте</button>
                <button type="button" id="new-object-btn-close" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>