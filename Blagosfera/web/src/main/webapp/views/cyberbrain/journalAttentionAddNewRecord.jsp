<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="cyberbrainCommunitySelector.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        var data = {
            performerId: "",
            fixTimeKvant: "",
            textKvant: "",
            tagKvant: "",
            attentionKvant: "",
            community_id: -1
        };

        $("#communitySelectorModalWindow").find("#community-btn-add").click(function() {
            var communityId = $("#communitySelectorModalWindow").find("#combobox-communities option:selected").attr("data-community-id");

            if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                var jsonData = '{' +
                        'performer_id: \"' + data.performerId + '\",' +
                        'fix_time_kvant: \"' + data.fixTimeKvant + '\",' +
                        'text_kvant: \"' + data.textKvant + '\",' +
                        'tag_kvant: \"' + data.tagKvant + '\",' +
                        'attention_kvant: \"' + data.attentionKvant + "\"," +
                        'community_id: \"' + communityId + "\"" +
                        '}';

                $.ajax({
                    type: "post",
                    dataType: "json",
                    data: jsonData,
                    url: "/cyberbrain/journalAttention/addAttention",
                    success: function (response) {
                        if (response.result == "error") {
                            bootbox.alert(response.message);
                        } else {
                            document.getElementById("text-kvant").value = "";
                            document.getElementById("tag-kvant").value = "";
                            document.getElementById("attention-kvant").value = "";

                            storeJournalAttention.load();

                            // обновим счетчики
                            getCountsRecordsAndScore();

                            $('#communitySelectorModalWindow').modal('hide');
                            bootbox.alert("Запись успешно добавленна.");
                        }
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            } else {
                bootbox.alert("Для добавления новой записи нужно выбрать для какого объединения будет добавленна данная запись.");
            }

            return false;
        });

        $("a#add-data-button").click(function() {
            data.textKvant = document.getElementById("text-kvant").value;
            data.tagKvant = document.getElementById("tag-kvant").value;
            data.attentionKvant = document.getElementById("attention-kvant").value;

            if (data.textKvant == null || data.textKvant == "") {
                bootbox.alert("Необходимо заполнить поле \"Краткое описание задачи\"!");
                return false;
            }

            if (data.tagKvant == null || data.tagKvant == "") {
                bootbox.alert("Необходимо заполнить поле \"Теговое описание задачи\"!");
                return false;
            }

            if (data.attentionKvant == null || data.attentionKvant == "") {
                bootbox.alert("Необходимо заполнить поле \"Квант внимания\"!");
                return false;
            }

            $('#communitySelectorModalWindow').modal({backdrop:false, keyboard:false});

            return false;
        });

        function prepareTagKvantControl() {
            var $input = $('form').find('input#tag-kvant');
            $input.typeahead({
                onSelect: function(item) {
                    $("input#tag-kvant").val(item.text);
                },
                ajax: {
                    url: "/cyberbrain/journalAttention/search_by_tag_kvant.json",
                    timeout: 500,
                    displayField: "tagKvant",
                    triggerLength: 1,
                    method: "post",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        return {
                            query : query
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

        function prepareTextKvantControl() {
            var $input = $('form').find('input#text-kvant');
            $input.typeahead({
                onSelect: function(item) {
                    $("input#text-kvant").val(item.text);
                },
                ajax: {
                    url: "/cyberbrain/journalAttention/search_by_text_kvant.json",
                    timeout: 500,
                    displayField: "textKvant",
                    triggerLength: 1,
                    method: "post",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        return {
                            query : query
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

        prepareTagKvantControl();
        prepareTextKvantControl();
    });
</script>

<h3 align="center">Добавить новое событие</h3>

<div class="form-group">
    <div class="row">
        <div class="col-xs-4">
            <label>Краткое описание задачи</label>
            <input id="text-kvant" type="text" autocomplete="off" class="form-control" />
        </div>

        <div class="col-xs-4">
            <label>Теговое описание задачи</label>
            <input id="tag-kvant" type="text" autocomplete="off" class="form-control" />
        </div>

        <div class="col-xs-2">
            <label>Квант внимания (мин.)</label>
            <input id="attention-kvant" type="number" onkeypress='return event.charCode >= 48 && event.charCode <= 57' autocomplete="off" class="form-control" />
        </div>

        <div class="col-xs-2">
            <label>&nbsp;</label>
            <a href="#" class="btn btn-default btn-block" id="add-data-button">Добавить</a>
        </div>
    </div>
</div>