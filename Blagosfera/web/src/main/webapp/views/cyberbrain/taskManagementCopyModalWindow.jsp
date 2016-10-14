<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript">
    var copyModalWindow;

    $(document).ready(function() {
        var copyData;

        Ext.onReady(function () {
            timeReady = Ext.create('Ext.form.field.Date', {
                renderTo: 'copy-time-ready',
                xtype: 'datefield',
                name: 'copy-time-ready',
                format: 'Y-m-d',
                width: '100%',
                listeners: {
                    change: function (t, n, o) {
                    },
                    select: function (t, n, o) {
                        copyData.time_ready = n.getFullYear() + '/' + ('0' + (n.getMonth() + 1)).slice(-2) + '/' + ('0' + n.getDate()).slice(-2) + ' 00:00:00';
                    }
                }
            });
        });

        copyModalWindow = $("#copyModalWindow");
        copyModalWindow.on("shown.bs.modal", function() {
            copyData = {
                source_object: "",
                target_object: "",
                time_ready: "",
                responsible_id: "",
                community_id: -1
            };

            $("#copy-source-object").val("");
            $("#copy-target-object").val("");
            timeReady.setValue(null);
            $("#copy-responsible-lbl").html("Ответственный: ---");
            $("#copy-responsible").val("");

            if ($("#copy-combobox-communities").html() == "") {
                $("#copy-source-object").popover("enable");
                $("#copy-target-object").popover("enable");
            }
        });

        getCommunities();
        prepareObjectControl("copyModalWindow", "copy-source-object", "copy");
        prepareObjectControl("copyModalWindow", "copy-target-object", "copy");
        prepareSharerControl(copyModalWindow, "copy-responsible");

        function getCommunities() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: "/cyberbrain/sections/get_user_communities.json",
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#copy-combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        $("#copy-btn-save").click(function() {
            var communityId = $("#copy-combobox-communities option:selected").attr("data-community-id");
            copyData.community_id = communityId;

            copyData.source_object = $("#copy-source-object").val();
            copyData.target_object = $("#copy-target-object").val();
            copyData.responsible_id = $("#copy-responsible").attr("data-object-id");

            if (copyData.community_id == null || copyData.community_id == "" || copyData.community_id < 0 || copyData.community_id == "undefined") {
                bootbox.alert("Объединение не указано!");
                return false;
            }

            if (copyData.source_object == null || copyData.source_object == "") {
                bootbox.alert("Объект оригинал не указан!");
                return false;
            }

            if (copyData.target_object == null || copyData.target_object == "") {
                bootbox.alert("Объект копия не указан!");
                return false;
            }

            if (copyData.time_ready == null || copyData.time_ready == ""  || timeReady.value == null || timeReady.validate() == false) {
                bootbox.alert("Дата готовности не указана!");
                return false;
            }

            if (copyData.responsible_id == null || copyData.responsible_id == "" || copyData.responsible_id < 0) {
                bootbox.alert("Ответственный не указан!");
                return false;
            }

            $.ajax({
                type : "post",
                dataType : "json",
                data : JSON.stringify(copyData),
                url : "/cyberbrain/taskManagement/copyObject",
                success : function(response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                    } else {
                        copyModalWindow.modal("hide");

                        storeSubcontractors.load();
                        storeCustomers.load();

                        // обновим счетчики
                        getCountsRecordsAndScore();

                        bootbox.alert("Задание на копирование объекта созданно!");
                    }
                },
                error : function() {
                    console.log("ajax error");
                }
            });

            return false;
        });
    });
</script>

<!-- Modal -->
<div class="modal fade" id="copyModalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Создать копию объекта</h4>
            </div>
            <div class="modal-body">
                <label>Объединение в рамках которого создается копия</label>
                <div class="form-group">
                    <select id="copy-combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
                </div>

                <hr/>

                <div class="form-group">
                    <label id="copy-source-object-lbl">Объект оригинал</label>
                    <input id="copy-source-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                           data-toggle="popover" data-trigger="" data-placement="left"
                           data-content="Требуется указать объединение!" />
                </div>

                <div class="form-group">
                    <label id="copy-target-object-lbl">Объект копия</label>
                    <input id="copy-target-object" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя объекта"
                           data-toggle="popover" data-trigger="" data-placement="left"
                           data-content="Требуется указать объединение!" />
                </div>

                <div class="form-group">
                    <label>Дата готовности</label>
                    <div class="form-control" id="copy-time-ready"></div>
                </div>

                <div class="form-group">
                    <label id="copy-responsible-lbl" data-caption="Ответственный:" data-caption-value="---"></label>
                    <input id="copy-responsible" data-object-id="" data-object-name="" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя исполнителя" />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="copy-btn-save" class="btn btn-primary">Создать</button>
                <button type="button" id="copy-btn-close" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>