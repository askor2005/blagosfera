<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@include file="systemSettingsGrid.jsp" %>
<%@include file="smtpServersGrid.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        $("#editSystemSettingForm").submit(function (event) {
            event.preventDefault();
            return false;
        });
        $("#editSmtpServersForm").submit(function (event) {
            event.preventDefault();
            return false;
        });

        $("#save-systemSetting-button").click(function() {
            if ($("#field-key").val() == "") {
                bootbox.alert("Укажите наименование параметра!");
                return false;
            }
            if ($("#field-value").val() == "") {
                bootbox.alert("Укажите значение параметра!");
                return false;
            }
            if ($("#field-description").val() == "") {
                bootbox.alert("Укажите описание параметра!");
                return false;
            }
            $.ajax({
                url: "/admin/systemSettings/saveSystemSetting",
                type: "post",
                data: $("#editSystemSettingForm").serialize(),
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("Параметр успешно сохранён.");
                        $("#editSystemSettingWindow").modal("hide");
                        storeSystemSettings.load();
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
            return false;
        });

        $("#save-smtpServers-button").click(function() {
            if ($("#field-host").val() == "") {
                bootbox.alert("Укажите наименование Хоста!");
                return false;
            }
            if ($("#field-port").val() == "") {
                bootbox.alert("Укажите Порт!");
                return false;
            }
            if ($("#field-username").val() == "") {
                bootbox.alert("Укажите Имя пользователя!");
                return false;
            }
            if ($("#field-password").val() == "") {
                bootbox.alert("Укажите Пароль!");
                return false;
            }
            if ($("#field-protocol").val() == "") {
                bootbox.alert("Укажите Протокол!");
                return false;
            }
            $.ajax({
                url: "/admin/systemSettings/saveSmtpServer",
                type: "post",
                data: $("#editSmtpServersForm").serialize(),
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("SMTP сервер успешно сохранён.");
                        $("#editSmtpServersWindow").modal("hide");
                        storeSmtpServers.load();
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
            return false;
        });

        $("#add-systemSetting-button").click(function() {
            $("#field-SystemSettingId").val(-1);
            $("#editSystemSettingWindow").modal({backdrop:false, keyboard:false});
            $("#systemSettingLabel").html("Добавить новый параметр");
            return false;
        });

        $("#add-systemSetting-button2").click(function() {
            $("#field-SystemSettingId").val(-1);
            $("#editSystemSettingWindow").modal({backdrop:false, keyboard:false});
            $("#systemSettingLabel").html("Добавить новый параметр");
            return false;
        });

        $("#add-smtpServer-button").click(function() {
            $("#field-id").val(-1);
            $("#editSmtpServersWindow").modal({backdrop:false, keyboard:false});
            $("#smtpServersLabel").html("Добавить новый SMTP сервер");
            return false;
        });

        $("#delete-systemSetting-button").click(function() {
            var id = $("#field-SystemSettingId").val();
            $.ajax({
                url: "/admin/systemSettings/deleteSystemSetting?id=" + id,
                type: "post",
                data: "{}",
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("Параметр успешно удален.");
                        $("#editSystemSettingWindow").modal("hide");
                        storeSystemSettings.load();
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
        });
        $("#delete-smtpServers-button").click(function() {
            var id = $("#field-id").val();
            $.ajax({
                url: "/admin/systemSettings/deleteSmtpServer?id=" + id,
                type: "post",
                data: "{}",
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("SMTP сервер успешно удален.");
                        $("#editSmtpServersWindow").modal("hide");
                        storeSmtpServers.load();
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
        });

        $("#editSystemSettingWindow").on("hidden.bs.modal", function() {
            $("#editSystemSettingForm").trigger("reset");
            $("#field-key").removeAttr("readonly");
            $("#field-SystemSettingId").val(-1);
            $("#delete-systemSetting-button").css({"display": "none"});
        });
        $("#editSmtpServersWindow").on("hidden.bs.modal", function() {
            $("#editSmtpServersForm").trigger("reset");
            $("#field-id").val(-1);
            $("#delete-smtpServers-button").css({"display": "none"});
        });
    });
</script>

<h1>Настройки системы</h1>
<hr/>
<a href="#" class="btn btn-primary" id="add-systemSetting-button">Добавить новый параметр в системную таблицу</a>
<hr/>
<div id="systemSettings-grid"></div>
<hr/>
<a href="#" class="btn btn-primary" id="add-systemSetting-button2">Добавить новый параметр в системную таблицу</a>
<hr/>
<a href="#" class="btn btn-primary" id="add-smtpServer-button">Добавить новый SMTP сервер</a>
<hr/>
<div id="smtpServers-grid"></div>
<hr/>

<sec:authorize access="hasRole('ECO_ADVISOR_ADMIN')">
    <div>
        <a target="_blank" href="/ng/index.html#/ecoadvisor/admin">Параметры ЭКС</a>
    </div>

    <hr/>
</sec:authorize>

<!-- Modal -->
<div class="modal fade" role="dialog" id="editSystemSettingWindow" aria-labelledby="systemSettingLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 100%;">
        <div class="modal-content" style=";">
            <div class="modal-header">
                <h4 class="modal-title" id="systemSettingLabel">Редактировать / Создать параметр</h4>
            </div>
            <f:form action="/admin/systemSettings/saveSystemSetting" id="editSystemSettingForm" role="form" method="post" modelAttribute="systemSettingForm" >
                <div class="modal-body">
                    <input id="field-SystemSettingId" style="display: none"/>
                    <div class="form-group">
                        <f:label path="key">Введите наименование параметра</f:label>
                        <f:input path="key" class="form-control" id="field-key"/>
                    </div>
                    <div class="form-group">
                        <f:label path="value">Введите значение параметра</f:label>
                        <f:textarea path="value" class="form-control" id="field-value" style="height: 300px;"/>
                    </div>
                    <div class="form-group">
                        <f:label path="description">Введите описание параметра</f:label>
                        <f:textarea path="description" class="form-control" id="field-description" style="height: 150px;"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                    <f:button class="btn btn-primary" id="save-systemSetting-button" style="float: left;">Сохранить</f:button>
                    <f:button class="btn btn-danger" id="delete-systemSetting-button" style="display: none; float: left;">Удалить</f:button>
                </div>
            </f:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Modal -->
<div class="modal fade" role="dialog" id="editSmtpServersWindow" aria-labelledby="smtpServersLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="smtpServersLabel">Редактировать / Создать</h4>
            </div>
            <f:form action="/admin/systemSettings/saveSmtpServer" id="editSmtpServersForm" role="form" method="post" modelAttribute="smtpServerForm" >
                <div class="modal-body">
                    <div class="form-group" style="display: none">
                        <f:input path="id" class="form-control" id="field-id"/>
                    </div>
                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-9">
                                <f:label path="host">Хост</f:label>
                                <f:input path="host" class="form-control" id="field-host"/>
                            </div>
                            <div class="col-xs-3">
                                <f:label path="port">Порт</f:label>
                                <f:input path="port" class="form-control" id="field-port" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57"/>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="row">
                            <div class="col-xs-6">
                                <f:label path="username">Имя пользователя</f:label>
                                <f:input path="username" class="form-control" id="field-username"/>
                            </div>
                            <div class="col-xs-6">
                                <f:label path="password">Пароль</f:label>
                                <f:input path="password" class="form-control" id="field-password"/>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <f:label path="protocol">Протокол</f:label>
                        <f:input path="protocol" class="form-control" id="field-protocol"/>
                    </div>
                    <div class="form-group">
                        <f:checkbox path="debug" value="true" data-toggles="true" id="field-debug"/>
                        <f:label path="debug" for="field-debug">Отладка</f:label>
                    </div>
                    <div class="form-group">
                        <f:checkbox path="using" value="true" data-toggles="true" id="field-using"/>
                        <f:label path="using" for="field-using">Использовать этот сервер для отправки писем</f:label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                    <f:button class="btn btn-primary" id="save-smtpServers-button" style="float: left;">Сохранить</f:button>
                    <f:button class="btn btn-danger" id="delete-smtpServers-button" style="display: none; float: left;">Удалить</f:button>
                </div>
            </f:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->