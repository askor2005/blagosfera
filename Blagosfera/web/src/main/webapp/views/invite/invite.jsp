<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript">
    var previousTabIndex = 0;

    $(document).ready(function() {
        $.radomJsonGet("/invite.json", {}, function(response) {
            var admin = response.admin;
            var relations = response.inviteRelationShipTypes;
            var verified = response.verified;
            var template = $('#invite-template').html();
            Mustache.parse(template);
            var rendered = Mustache.render(template, {admin : admin,relations : relations,verified : verified});
            $('#invite-target').html(rendered);
            $("#invite-wizard-form").bootstrapWizard({
                "tabClass": "nav nav-pills", onTabChange: function (tab, navigation, index) {

                }, onTabClick: function (tab, navigation, index) {
                    return false;
                }, onPrevious: function (tab, navigation, index) {
                    var $current = index + 1;
                    previousTabIndex = $current + 1;

                }, onNext: function (tab, navigation, index) {
                    var $current = index + 1;
                    previousTabIndex = $current - 1;

                    if (previousTabIndex == 1) {
                        if ($("#invited-email").attr("data-is-valid") == 0) {
                            bootbox.alert($("#invited-email").attr("data-is-valid-message"));
                            return false;
                        }
                    } else if (previousTabIndex == 2) {
                        if ($("#invited-last-name").val() == "") {
                            bootbox.alert("Введите Фамилию нового участника!");
                            return false;
                        }
                        if ($("#invited-first-name").val() == "") {
                            bootbox.alert("Введите Имя нового участника!");
                            return false;
                        }
                        if ($("input[name=invitedGender]:checked").val() == undefined) {
                            bootbox.alert("Укажите пол нового участника!");
                            return false;
                        }

                        updateCaptions();
                    }
                }, onTabShow: function (tab, navigation, index) {
                    var $total = navigation.find("li").length;
                    var $current = index + 1;

                    if ($current == 1) {
                        $("#invite-wizard-form").find(".pager .previous").hide();
                    } else {
                        $("#invite-wizard-form").find(".pager .previous").show();
                    }

                    if ($current >= $total) {
                        $("#invite-wizard-form").find(".pager .next").hide();
                        $("#invite-user-button").css({display: "block"});
                    } else {
                        $("#invite-wizard-form").find(".pager .next").show();
                        $("#invite-user-button").css({display: "none"});
                    }
                }
            });

            function updateCaptions() {
                var newUser = $("#invited-last-name").val() + " " + $("#invited-first-name").val() + " " + $("#invited-father-name").val();
                var rn = new RussianName(newUser);

                $("#guarantee-lbl").html($("#guarantee-lbl").attr("data-caption") + rn.fullName(rn.gcaseVin));
                $("#know-personally-lbl").html($("#know-personally-lbl").attr("data-caption") + rn.fullName(rn.gcaseTvor));
                $("#how-long-familiar-lbl").html($("#how-long-familiar-lbl").attr("data-caption") + rn.fullName(rn.gcaseTvor));
                $("#relationship-lbl").html($("#relationship-lbl").attr("data-caption") + "(" +  rn.fullName(rn.gcaseTvor) + ")");
            }

            $("#createInviteForm").submit(function (event) {
                event.preventDefault();
                $('#invite-user-button').attr('disabled', true);

                if ($("#invited-email").attr("data-is-valid") == 0) {
                    bootbox.alert($("#invited-email").attr("data-is-valid-message"),
                            function() {
                                $('#invite-user-button').attr('disabled', false);
                            });
                    return false;
                }
                if ($("#invited-last-name").val() == "") {
                    bootbox.alert("Введите Фамилию нового участника!",
                            function() {
                                $('#invite-user-button').attr('disabled', false);
                            });
                    return false;
                }
                if ($("#invited-first-name").val() == "") {
                    bootbox.alert("Введите Имя нового участника!",
                            function() {
                                $('#invite-user-button').attr('disabled', false);
                            });
                    return false;
                }
                if ($("input[name=invitedGender]:checked").val() == undefined) {
                    bootbox.alert("Укажите пол нового участника!",
                            function() {
                                $('#invite-user-button').attr('disabled', false);
                            });
                    return false;
                }

                var relationIsChecked = false;
                $("input[name=relationships]").each(function () {
                    if (this.checked) {
                        relationIsChecked = true;
                    }
                });




                var newUser = $("#invited-last-name").val() + " " + $("#invited-first-name").val() + " " + $("#invited-father-name").val();
                var rn = new RussianName(newUser);


                if (!relationIsChecked) {
                    bootbox.alert("Укажите ваше отношение с " + rn.fullName(rn.gcaseTvor) + "!",
                            function() {
                                $('#invite-user-button').attr('disabled', false);
                            });
                    return false;
                }
                if ($("#know-personally").prop("checked")) {
                    if ($("#how-long-familiar").val() == "") {
                        bootbox.alert("Укажите сколько лет вы знакомы с " + rn.fullName(rn.gcaseTvor) + "!",
                                function() {
                                    $('#invite-user-button').attr('disabled', false);
                                });
                        return false;
                    }
                }

                var valid = $("#createInviteForm").valid();
                if (valid) {

                    $.radomJsonPost(
                            //url
                            "/invite/create.json",
                            //data
                            $(this).serialize(),
                            //callback
                            function (data) {

                                if (data.result == "error") {
                                    var message = data.message ? data.message : 'Произошла ошибка!';
                                    bootbox.alert(message, function() {
                                        $('#invite-user-button').attr('disabled', false);
                                    });

                                    return;
                                }

                                bootbox.alert("Приглашение для " + rn.fullName(rn.gcaseRod) + " успешно отправлено.",
                                        function() {
                                            window.location.href = "/invites"
                                        }
                                );
                            },
                            //errorCallback
                            function(data) {
                                var message = data.message ? data.message : 'Произошла ошибка!';
                                bootbox.alert(message, function() {
                                    $('#invite-user-button').attr('disabled', false);
                                });

                            }
                    );

                } else {
                    $('#invite-user-button').attr('disabled', false);
                }

                return false;
            });

            $("#know-personally").change(function () {
                if ($(this).prop("checked")) {
                    $("#how-long-familiar-group").css({display: "block"});
                } else {
                    $("#how-long-familiar-group").css({display: "none"});
                }
                $("#how-long-familiar").val("");
            });

            function checkEmail(jqSelectorInput) {
                var value = jqSelectorInput.val();
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/invite/validateEmail.json?email=" + value,
                    success: function (response) {
                        if (response.result == "error") {
                            jqSelectorInput.attr("data-is-valid", 0);
                            jqSelectorInput.attr("data-is-valid-message", response.message);
                        } else {
                            jqSelectorInput.attr("data-is-valid", 1);
                            jqSelectorInput.attr("data-is-valid-message", "");
                        }
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            }

            $("#invited-email").on("input", function () {
                checkEmail($(this));
            });
            // Проверка при вставке мыла
            $("#invited-email").bind('paste', function(e) {
                var jqSelf = $(this);
                setTimeout(function(){
                    jqSelf.val(jqSelf.val().split(" ").join("").toLowerCase());
                    checkEmail(jqSelf);
                }, 100);
            });

            $("#edit-relationships").click(function() {
                $("#editRelationshipsWindow").modal({backdrop:false, keyboard:false});
                return false;
            });

            $("#add-relationship-type").click(function() {
                $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/invite/addRelationshipType.json",
                    success: function (response) {
                        if (response.result != "error") {
                            var id = response;
                            $("#relationship-list").append(
                                    "<li id='listItem-" + id + "' data-object-id='" + id + "'>" +
                                    "<div class='row' id='relationship-type-row-" + id + "' style='margin: 5px'>" +
                                    "<div class='input-group'>" +
                                    "<span class='input-group-addon handle' style='cursor:pointer;'><i class='fa fa-bars'></i></span>" +
                                    "<input id='edit-relationship-type-id" + id + "' data-object-id='" + id + "' name='input-relationship-type' type='text' class='form-control input-sm'/>" +
                                    "<span class='input-group-btn'>" +
                                    "<a href='#' name='delete-relationship-type' data-object-id='" + id + "' class='btn btn-danger btn-sm'>&nbsp;<i class='glyphicon glyphicon-remove'></i></a>" +
                                    "</span></div></div></li>"
                            );
                            $("#form-group-relationship").append(
                                    "<p id='p-relationship-type-" + id + "'>" +
                                    "<input id='relationship-type-id" + id + "' name='relationships' data-toggles='true' type='checkbox' value='" + id + "'>" +
                                    "<input type='hidden' name='_relationships' value='on'>" +
                                    "&nbsp;<label id='relationship-type-id" + id + "-lbl' for='relationship-type-id" + id + "'></label>" +
                                    "</p>"
                            );
                            updateEditEvents();
                            updateRelationshipTypeIndexes();
                        }
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
                return false;
            });

            updateEditEvents();
            function updateEditEvents() {
                $("a[name=delete-relationship-type]").off("click").click(function() {
                    var id = $(this).attr("data-object-id");
                    $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "/invite/deleteRelationshipType.json?id=" + id,
                        success: function (response) {
                            if (response.result != "error") {
                                $("#relationship-type-row-" + id).remove();
                                $("#p-relationship-type-" + id).remove();
                            }
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                    return false;
                });

                $("input[name=input-relationship-type]").off("input").on("input", function () {
                    var id = $(this).attr("data-object-id");
                    var name = $(this).val();
                    $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "/invite/updateRelationshipType.json?id=" + id + "&name=" + name,
                        success: function (response) {
                            if (response.result != "error") {
                                $("#relationship-type-id" + id + "-lbl").html(name);
                            }
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                });
            }

            $("#relationship-list").sortable({
                handle : ".handle",
                update : function () {
                    updateRelationshipTypeIndexes();
                }
            });

            function updateRelationshipTypeIndexes() {
                var arr = $("#relationship-list").sortable("toArray", {attribute: "data-object-id"});
                $.ajax({
                    type: "post",
                    contentType: 'application/json',
                    processData: false,
                    dataType: "json",
                    data: JSON.stringify(arr),
                    url: "/invite/updateRelationshipTypeIndexes.json",
                    success: function (response) {
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            }

            //первая буква всегда заглавная
            $("input[name=invitedLastName]").capitalizeInput();
            $("input[name=invitedFirstName]").capitalizeInput();
            $("input[name=invitedFatherName]").capitalizeInput();
        });
    });
</script>
<div id="invite-target">
</div>
<script id="invite-template" type="x-tmpl-mustache">
<h1>Приглашение в систему</h1>

<hr/>

   {{#verified}}
        <form action="/invite/create.json" id="createInviteForm" role="form" method="post" modelAttribute="inviteForm" >
            <div id="invite-wizard-form">
                <ul style="alignment: center">
                    <li><a href="#tab1" data-toggle="tab">Ввод E-Mail</a></li>
                    <li><a href="#tab2" data-toggle="tab">Ввод ФИО</a></li>
                    <li><a href="#tab3" data-toggle="tab">Ввод поручения</a></li>
                </ul>
                <div class="tab-content">
                    <hr/>

                    <div class="tab-pane" id="tab1">
                        <div class="form-group">
                            <label >Введите E-Mail нового участника</label>
                            <input name="email" class="form-control" id="invited-email" data-is-valid="0" data-is-valid-message="E-mail не введен!" placeholder="Введите здесь E-Mail" onkeypress="return RegExp('[a-zA-Z0-9_.+-@\.]').test(String.fromCharCode(event.charCode))"/>
                        </div>
                    </div>

                    <div class="tab-pane" id="tab2">
                        <div class="form-group">
                            <label ">Введите Фамилию нового участника</label>
                            <input name="invitedLastName" class="form-control" id="invited-last-name" onkeypress="return event.charCode != 32"/>
                        </div>
                        <div class="form-group">
                            <label>Введите Имя нового участника</label>
                            <input name="invitedFirstName" class="form-control" id="invited-first-name" onkeypress="return event.charCode != 32"/>
                        </div>
                        <div class="form-group">
                            <label >Введите Отчество нового участника</label>
                            <input name="invitedFatherName" class="form-control" id="invited-father-name" onkeypress="return event.charCode != 32"/>
                        </div>
                        <div class="form-group">
                            <label>Пол:</label>
                            <input type="radio" name="invitedGender"  value="М" style="margin: 5px;">Мужской</input>
                            <input type="radio" name="invitedGender"  value="Ж" style="margin: 5px;"/>Женский</input>
                        </div>
                    </div>

                    <div class="tab-pane" id="tab3">
                        <div class="form-group">
                            <input type="checkbox" name="guarantee" id="guarantee"  data-toggles="true"/>
                            <label id="guarantee-lbl" name="guarantee" data-caption="Я ручаюсь за "></label>
                        </div>

                        <div class="form-group">
                            <input type="checkbox" id="know-personally">
                            <label id="know-personally-lbl" for="know-personally" data-caption="Я знаком лично с "></label>
                        </div>

                        <div class="form-group" id="how-long-familiar-group" style="display: none">
                            <label id="how-long-familiar-lbl" name="howLongFamiliar" data-caption="Сколько лет вы знакомы с ">Сколько лет знаком с новым участником</label>
                            <input name="howLongFamiliar" id="how-long-familiar" class="form-control" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57" style="width: 100px"/>
                        </div>

                        <div class="form-group" id="form-group-relationship">
                            <label id="relationship-lbl" data-caption="В каких отношениях вы с "></label>
                            {{#relations}}
                                <p id="p-relationship-type-{{id}}">
                                    <input type="checkbox" name="relationships" id="relationship-type-id{{id}}" value="{{id}}" data-toggles="true"/>
                                    <label id="relationship-type-id{{id}}-lbl" for="relationship-type-id{{id}}">{{name}}</label>
                                </p>
                            {{/relations}}
                        </div>
                        <div class="form-group">
                                {{#admin}}
                                    <a href="#" id="edit-relationships"><i class="glyphicon glyphicon-pencil"></i> Редактировать список отношений</a>
                                {{/admin}}
                        </div>
                    </div>

                    <ul class="pager wizard">
                        <li class="previous"><a href="#"><span class="glyphicon glyphicon-arrow-left"></span> Назад</a></li>
                        <li class="next"><a href="#">Вперед <span class="glyphicon glyphicon-arrow-right"></span></a></li>
                        <li><button type="submit" class="btn btn-default" id="invite-user-button" style="display: none;float: right;">Пригласить пользователя</button></li>
                    </ul>
                </div>
            </div>
        </form>
        <hr/>
    {{/verified}}
    {{^verified}}
        <div class="alert alert-info" role="alert" style="text-align: center">
            Данная функция доступна только для идентифицированных пользователей
        </div>
        <hr/>
    {{/verified}}


<a href="/invites" class="btn btn-default btn-block" id="goto-invites-button">Перейти к списку приглашенных</a>

{{#admin}}
        <!-- Modal -->
        <div class="modal fade" role="dialog" id="editRelationshipsWindow" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="myModalLabel">Редактировать список отношений</h4>
                    </div>
                    <div class="modal-body" style="max-height:400px;overflow:auto;">
                        <a href="#" id="add-relationship-type" class="btn btn-primary">Добавить</a>
                        <hr/>
                        <ul id="relationship-list" style="list-style:none;margin: 0px;padding: 0px">
                            {{#relations}}
                                <li id="listItem-{{id}}" data-object-id="{{id}}">
                                    <div class="row" id="relationship-type-row-{{id}}" style="margin: 5px">
                                        <div class="input-group">
                                            <span class="input-group-addon handle" style="cursor:pointer;"><i class="fa fa-bars"></i></span>
                                            <input id="edit-relationship-type-id{{id}}" data-object-id="{{id}}" name="input-relationship-type" type="text" class="form-control input-sm" value="{{name}}"/>
                                            <span class="input-group-btn">
                                                <a href="#" name="delete-relationship-type" data-object-id="{{id}}" class="btn btn-danger btn-sm">&nbsp;<i class="glyphicon glyphicon-remove"></i></a>
                                            </span>
                                        </div>
                                    </div>
                                </li>
                            {{/relations}}
                        </ul>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    {{/admin}}
</script>