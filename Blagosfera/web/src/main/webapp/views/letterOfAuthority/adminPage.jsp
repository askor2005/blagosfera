<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@include file="letterOfAuthorityRolesGrid.jsp" %>
<style>
    #letterOfAuthorityRoleGridSearchResult {display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;}
</style>
<script type="text/javascript">
    //
    var rameraListEditorItemId = null;
    var scopeRoleTypes = [];
    <c:forEach var="scopeRoleType" items="${scopeRoleTypes}" varStatus="i">
        scopeRoleTypes.push("${scopeRoleType.key}");
    </c:forEach>

    // Создать роль
    function createLetterOfAuthorityRole(letterOfAuthorityRole, listEditorItemId, callBack) {
        $.radomJsonPostWithWaiter(
                "/admin/letterofauthority/createLetterOfAuthorityRole.json",
                JSON.stringify({
                    letterOfAuthorityRole : letterOfAuthorityRole,
                    listEditorItemId : listEditorItemId
                }),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        );
    }

    // Обновить роль
    function updateLetterOfAuthorityRole(letterOfAuthorityRole, callBack) {
        $.radomJsonPostWithWaiter(
                "/admin/letterofauthority/updateLetterOfAuthorityRole.json",
                JSON.stringify({
                    letterOfAuthorityRole : letterOfAuthorityRole,
                    listEditorItemId : null
                }),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        );
    }

    function initAssociationForm() {
        $("#associationForm").empty();
        RameraListEditorModule.init(
                $("#associationForm"),
                {
                    labelClasses: ["checkbox-inline"],
                    labelStyle: "margin-left: 10px;",
                    selectClasses: ["form-control"]
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        rameraListEditorItemId = data.value;
                    }
                }
        );
    }

    // Открыть модальное окно для редактирования роли
    function openEditRoleModal(roleId, key, name, script) {
        $("#edit_id").val(roleId);
        $("#edit_key").val(key);
        $("#edit_name").val(name);
        $("#edit_createDocumentScript").val(script);
        $("#editLetterOfAuthorityRoleWindow").modal("show");
    }

    $(document).ready(function () {
        // Кнопка открытия модального окна создания роли
        $("#createLetterOfAuthorityRoleModalButton").click(function(){
            $("#createLetterOfAuthorityRoleWindow").modal("show");
        });
        // Кнопка сохранения новой роли
        $("#createLetterOfAuthorityRoleButton").click(function(){
            if ($("#key").val() == null || $("#key").val() == '') {
                bootbox.alert("Не установлен код роли доверенности!");
                return false;
            }
            if ($("#name").val() == null || $("#name").val() == '') {
                bootbox.alert("Не установлено имя роли доверенности!");
                return false;
            }

            if ($("#scopeType").val() == null) {
                bootbox.alert("Не выбран тип объекта в рамках которого создаётся доверенность!");
                return false;
            }

            var scopeRoleType = null;
            var scopeRoleName = null;
            if ($("#scopeRoleTypeBlock").is(":visible")) {
                scopeRoleType = $("#scopeRoleType").val();
            }
            if ($("#scopeRoleNameBlock").is(":visible")) {
                for (var index in scopeRoleTypes) {
                    var scopeRoleType = scopeRoleTypes[index];
                    if ($("#scopeRoleName_" + scopeRoleType + "_block").is(":visible")) {
                        scopeRoleName = $("#scopeRoleName_" + scopeRoleType).val();
                        break;
                    }
                }
            }
            if (scopeRoleName == null) {
                scopeRoleType = null;
            }
            $("#scopeRoleNameTypeBlock").show();

            var letterOfAuthorityRoleForm = {
                key: $("#key").val(),
                name: $("#name").val(),
                createDocumentScript: $("#createDocumentScript").val(),
                scopeType: $("#scopeType").val(),
                scopeRoleType: scopeRoleType,
                scopeRoleName: scopeRoleName
            };
            if (!$("#associationFormBlock").is(":visible")) {
                rameraListEditorItemId = null;
            }
            createLetterOfAuthorityRole(letterOfAuthorityRoleForm, rameraListEditorItemId, function(){
                $("#createLetterOfAuthorityRoleWindow").modal("hide");
                initAssociationForm();
            });
        });
        $("#createLetterOfAuthorityRoleWindow").on("hidden.bs.modal", function () {
            letterOfAuthorityStore.load();
        });
        //
        $("#editLetterOfAuthorityRoleButton").click(function(){
            var letterOfAuthorityRoleForm = {
                id: $("#edit_id").val(),
                name: $("#edit_name").val(),
                createDocumentScript: $("#edit_createDocumentScript").val()
            };
            updateLetterOfAuthorityRole(letterOfAuthorityRoleForm);
        });
        $("#editLetterOfAuthorityRoleWindow").on("hidden.bs.modal", function () {
            letterOfAuthorityStore.load();
        });

        // Изменяется тип объекта
        $("#scopeType").on("change", function() {
            if ($(this).val() == "COMMUNITY") {
                $("#associationFormBlock").show();
                $("#scopeRoleTypeBlock").show();
                $("#scopeRoleNameBlock").show();
            } else {
                $("#associationFormBlock").hide();
                $("#scopeRoleTypeBlock").hide();
                $("#scopeRoleNameBlock").hide();
            }
        });
        // Изменяется тип роли объекта
        $("#scopeRoleType").on("change", function() {
            $(".scopeRoleName_block").hide();
            $("#scopeRoleName_" + $(this).val() + "_block").show();
            switch ($(this).val()) {
                case "COMMUNITY_POST": // Должность
                    break;
                case "COMMUNITY_PERMISSION": // Права в объединении
                case "COMMUNITY_FIELD_SHARER": // Поле с типом SHARER
                case "COMMUNITY_FIELD_SHARER_LIST": // Поле с типом SHARER_LIST
                    $("#scopeRoleName_" + $(this).val()).selectpicker("val", null);
                    break;
            }
        });

        $("#scopeType").selectpicker("val", null);
        $("#scopeRoleType").selectpicker("val", null);
        initAssociationForm();
    });
</script>
<h1>Список ролей доверенностей</h1>
<hr/>
<div id="letterOfAuthorityRole-grid"></div>
<div id="letterOfAuthorityRoleGridSearchResult"></div>
<hr/>
<button type="button" class="btn btn-primary" style="margin-top: 5px;" id="createLetterOfAuthorityRoleModalButton">Создать роль доверенности</button><br/>
<hr/>
<!-- Модальное окно для добавления роли доверенности-->
<div class="modal fade" role="dialog" id="createLetterOfAuthorityRoleWindow" aria-labelledby="createLetterOfAuthorityRoleTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="createLetterOfAuthorityRoleTextLabel">Добавить</h4>
            </div>
            <div class="modal-body">
                <div class="div-control">
                    <label>Код роли доверенности</label>
                    <input type="text" class="form-control" id="key"  />
                </div>
                <div class="div-control">
                    <label>Имя роли доверенности</label>
                    <input type="text" class="form-control" id="name" />
                </div>
                <div class="div-control">
                    <label>Скрипт создания документа доверенности</label>
                    <textarea class="form-control" id="createDocumentScript" style="height: 300px;"></textarea>
                </div>
                <div class="div-control">
                    <label>Тип объекта в рамках которого создаётся доверенность</label>
                    <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeType">
                        <c:forEach var="scopeType" items="${scopeTypes}" varStatus="i">
                            <option value="${scopeType.key}">${scopeType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="div-control" id="associationFormBlock" style="display: none;">
                    <label>Форма объединения</label>
                    <div id="associationForm" rameraListEditorName="community_association_forms_groups"></div>
                </div>
                <div class="div-control" id="scopeRoleTypeBlock" style="display: none;">
                    <label>Тип роли в объединении</label>
                    <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeRoleType">
                        <c:forEach var="scopeRoleType" items="${scopeRoleTypes}" varStatus="i">
                            <option value="${scopeRoleType.key}">${scopeRoleType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="div-control" id="scopeRoleNameBlock" style="display: none;">
                    <label>Имя роли в объединении</label>
                    <div class="scopeRoleName_block" id="scopeRoleName_COMMUNITY_POST_block" style="display: none;">
                        <input type="text" class="form-control" id="scopeRoleName_COMMUNITY_POST" />
                    </div>
                    <div class="scopeRoleName_block" id="scopeRoleName_COMMUNITY_PERMISSION_block" style="display: none;">
                        <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeRoleName_COMMUNITY_PERMISSION" >
                            <c:forEach var="communityPermission" items="${communityPermissions}" varStatus="i">
                                <option value="${communityPermission.name}">${communityPermission.title}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="scopeRoleName_block" id="scopeRoleName_COMMUNITY_FIELD_SHARER_block" style="display: none;">
                        <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeRoleName_COMMUNITY_FIELD_SHARER" >
                            <c:forEach var="sharerCommunityField" items="${sharerCommunityFields}" varStatus="i">
                                <option value="${sharerCommunityField.internalName}">${sharerCommunityField.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="scopeRoleName_block" id="scopeRoleName_COMMUNITY_FIELD_SHARER_LIST_block" style="display: none;">
                        <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeRoleName_COMMUNITY_FIELD_SHARER_LIST" >
                            <c:forEach var="sharerListCommunityField" items="${sharerListCommunityFields}" varStatus="i">
                                <option value="${sharerListCommunityField.internalName}">${sharerListCommunityField.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="createLetterOfAuthorityRoleButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- Модальное окно для редактирования роли доверенности-->
<div class="modal fade" role="dialog" id="editLetterOfAuthorityRoleWindow" aria-labelledby="editLetterOfAuthorityRoleTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editLetterOfAuthorityRoleTextLabel">Редактировать</h4>
            </div>
            <div class="modal-body">
                <input type="hidden" id="edit_id" />
                <div class="div-control">
                    <label>Код роли доверенности</label>
                    <input type="text" class="form-control" id="edit_key" disabled="disabled" />
                </div>
                <div class="div-control">
                    <label>Имя роли доверенности</label>
                    <input type="text" class="form-control" id="edit_name" />
                </div>
                <div class="div-control">
                    <label>Скрипт создания документа доверенности</label>
                    <textarea class="form-control" id="edit_createDocumentScript" style="height: 300px;"></textarea>
                </div>
                <!--div class="div-control">
                    <label>Тип объекта в рамках которого создаётся доверенность</label>
                    <input type="text" class="form-control" id="edit_scopeType" disabled="disabled" />
                </div>
                <div class="div-control" id="edit_associationFormBlock" style="display: none;">
                    <label>Форма объединения</label>
                    <input type="text" class="form-control" id="edit_associationForm" disabled="disabled"/>
                </div>
                <div class="div-control" id="edit_scopeRoleTypeBlock" style="display: none;">
                    <label>Тип роли в объединении</label>
                    <input type="text" class="form-control" id="edit_scopeRoleType" disabled="disabled"/>
                </div>
                <div class="div-control" id="edit_scopeRoleNameBlock" style="display: none;">
                    <label>Имя роли в объединении</label>
                    <input type="text" class="form-control scopeRoleName" id="edit_scopeRoleName" disabled="disabled" />
                </div-->
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="editLetterOfAuthorityRoleButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->