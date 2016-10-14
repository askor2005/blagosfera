<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@include file="ownerLetterOfAuthoritiesGrid.jsp" %>
<%@include file="possibleDelegatesGrid.jsp" %>
<%@include file="letterOfAuthorityAttributesGrid.jsp" %>
<style>
    #ownerLetterOfAuthorityGridSearchResult,
    #delegatesGridSearchResult,
    #letterOfAuthorityAttributesGridEditSearchResult {
        display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;
    }

    #letterOfAuthorityRoleBlock,
    #scopeObjectBlock,
    #delegatesBlock,
    #expiredDateBlock,
    #createLetterOfAuthorityButton,
    #prolongLetterOfAuthorityDateBlock,
    #letterOfAuthorityAttributesBlock {
        display: none;
    }
</style>
<script type="text/javascript">
    var currentRoleKey = null; // Выбранная роль
    var currentScopeObjectId = null; // Выбранный объект
    var currentDelegateId = null; // Выбранный делегат

    var currentId = null;
    var currentLetterOfAuthority = null;

    // Создать атрибут к существующей доверенности
    function saveLetterOfAuthorityAttribute(letterOfAuthorityId, name, value, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/saveLetterOfAuthorityAttribute.json",
                {
                    letterOfAuthorityId: letterOfAuthorityId,
                    name: name,
                    value: value
                },
                callBack
        );
    };

    // Обновить атрибут
    function updateLetterOfAuthorityAttribute(id, name, value, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/updateLetterOfAuthorityAttribute.json",
                {
                    id: id,
                    name: name,
                    value: value
                },
                callBack
        );
    };

    // Удалить атрибут
    function deleteLetterOfAuthorityAttribute(id, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/deleteLetterOfAuthorityAttribute.json",
                {
                    id: id
                },
                callBack
        );
    };

    // Создать доверенность
    function createLetterOfAuthority(roleKey, expiredDate, scopeObjectId, delegateId, attributes, callBack) {
        var createLetterRequest = {
            roleKey : roleKey,
            expiredDate : expiredDate,
            radomAccountId : scopeObjectId,
            delegateId : delegateId,
            attributes : attributes
        };

        $.radomJsonPostWithWaiter(
                "/letterofauthority/createLetterOfAuthority.json",
                JSON.stringify(createLetterRequest),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        );
    }

    // Загрузить роли доверенностей
    function getLetterOfAuthorityRoles(scopeType, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/getLetterOfAuthorityRoles.json",
                {
                    scope_type : scopeType
                },
                callBack
        );
    }

    // Загрузить возможные объекты в рамках которых создаётся доверенность
    function getPossibleScopes(roleKey, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/getPossibleScopes.json",
                {
                    role_key : roleKey
                },
                callBack
        );
    }

    // Загрузить возможные объекты в рамках которых создаётся доверенность
    function getLetterOfAuthority(id, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/getLetterOfAuthority.json",
                {
                    id : id
                },
                callBack
        );
    }

    // Загрузить возможные объекты в рамках которых создаётся доверенность
    function deleteLetterOfAuthority(id, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/deleteLetterOfAuthority.json",
                {
                    id : id
                },
                callBack
        );
    }

    // Обновить доверенность
    function updateLetterOfAuthority(letterOfAuthority, callBack) {
        $.radomJsonPostWithWaiter(
                "/letterofauthority/updateLetterOfAuthority.json",
                JSON.stringify(letterOfAuthority),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        );
    }

    // Выбрали делегата
    function selectPossibleDelegate(delegateId) {
        $("#expiredDateBlock").show();
        $("#letterOfAuthorityAttributesBlock").show();
        $("#createLetterOfAuthorityButton").show();
        currentDelegateId = delegateId;
    }

    // Открыть модальное окно редактирования доверенности
    function openEditLetterOfAuthorityModal(id) {
        getLetterOfAuthority(id, function(letterOfAuthority){
            $("#edit_roleName").val(letterOfAuthority.authorityRole.name);
            $("#edit_delegateName").val(letterOfAuthority.delegate.name);
            $("#old_expiredDate").val(letterOfAuthority.expiredDate);
            $("#edit_active").prop("checked", letterOfAuthority.active);
            currentId = letterOfAuthority.id;
            currentLetterOfAuthority = letterOfAuthority;
            $("#prolongLetterOfAuthorityDateBlock").hide();
            $("#prolongLetterOfAuthority").prop("checked", false);
            $("#editLetterOfAuthorityWindow").modal("show");

            // Загружаем таблицу с атрибутами
            showLetterOfAuthorityAttributesGrid(letterOfAuthority.id, $("#letterOfAuthorityAttributesEdit-grid"), $("#letterOfAuthorityAttributesGridEditSearchResult"))
        });
    }

    // Открыть модальное окно редактирования атрибута
    function openEditLetterOfAuthorityAttributeModal(attrId, attrName, attrValue) {
        $(".attr_name", $("#editLetterOfAuthorityAttrWindow")).val(attrName)
        $(".attr_value", $("#editLetterOfAuthorityAttrWindow")).val(attrValue);
        $("#editLetterOfAuthorityAttrWindow").attr("attr_id", attrId);
        $("#editLetterOfAuthorityAttrWindow").modal("show");
    }

    $(document).ready(function () {
        var attributeTemplate = $("#attributeTemplate").html();
        Mustache.parse(attributeTemplate);


        var currentDateStr = new Date().format("dd.mm.yyyy");
        $("#expiredDate").val(currentDateStr);
        $("#expiredDate").radomDateInput({
            startView : 2,
            startDate: currentDateStr
        });
        $("#edit_expiredDate").val(currentDateStr);
        $("#edit_expiredDate").radomDateInput({
            startView : 2,
            startDate: currentDateStr
        });

        $("#createLetterOfAuthorityModalButton").click(function(){
            $("#createLetterOfAuthorityWindow").modal("show");
        });

        $("#createLetterOfAuthorityButton").click(function() {
            var attributes = {};

            // Сибираем атрибуты
            var errorText = "";
            $(".attrValuesContainer").each(function(){
                var attrName = $(".attr_name", $(this)).val();
                var attrValue = $(".attr_value", $(this)).val();
                if (attrName == null || attrName == "") {
                    errorText = "У атрибута не указано наименование"
                    return;
                }
                if (attrValue == null || attrValue == "") {
                    errorText = "У атрибута не указано значение";
                    return;
                }
                attributes[attrName] = attrValue;
            });
            if (errorText != "") {
                bootbox.alert(errorText);
                return false;
            }

            createLetterOfAuthority(currentRoleKey, $("#expiredDate").val(), currentScopeObjectId, currentDelegateId, attributes, function(){
                bootbox.alert("Доверенность успешно создана", function(){
                    $("#createLetterOfAuthorityWindow").modal("hide");
                    $("#letterOfAuthorityRoleBlock").hide();
                    $("#scopeObjectBlock").hide();
                    $("#delegatesBlock").hide();
                    $("#expiredDateBlock").hide();
                    $("#letterOfAuthorityAttributesBlock").hide();
                    $("#createLetterOfAuthorityButton").hide();
                    $("#letterOfAuthorityAttributesContainer").empty();

                    ownerLetterOfAuthoritiesStore.load();
                });
            });
        });

        $("#scopeType").selectpicker('val', null);

        //------------------
        // Область доверенности
        //------------------
        var letterOfAuthorityRolesTemplate = $("#letterOfAuthorityRolesTemplate").html();
        Mustache.parse(letterOfAuthorityRolesTemplate);
        $("#scopeType").on("change", function(){
            $("#scopeObject").empty();
            $("#scopeObject").selectpicker('refresh');
            $("#scopeObject").selectpicker('val', null);

            $("#letterOfAuthorityRoleBlock").hide();
            $("#scopeObjectBlock").hide();
            $("#delegatesBlock").hide();
            $("#expiredDateBlock").hide();
            $("#letterOfAuthorityAttributesBlock").hide();
            $("#createLetterOfAuthorityButton").hide();

            $("#letterOfAuthorityRole").empty();
            $("body").addClass("modal-open");
            getLetterOfAuthorityRoles($(this).val(), function(roles){
                $("#letterOfAuthorityRoleBlock").show();
                $("#letterOfAuthorityRole").empty();
                $("#letterOfAuthorityRole").append($(Mustache.render(letterOfAuthorityRolesTemplate, {roles : roles})));
                $("#letterOfAuthorityRole").selectpicker('refresh');
                $("#letterOfAuthorityRole").selectpicker('val', null);
            });
        });
        //------------------

        //------------------
        // Роль доверенности
        //------------------
        var scopeObjectTemplate = $("#scopeObjectTemplate").html();
        Mustache.parse(scopeObjectTemplate);
        $("#letterOfAuthorityRole").on("change", function(){
            currentRoleKey = $(this).val();

            $("#scopeObjectBlock").hide();
            $("#delegatesBlock").hide();
            $("#expiredDateBlock").hide();
            $("#letterOfAuthorityAttributesBlock").hide();
            $("#createLetterOfAuthorityButton").hide();

            $("body").addClass("modal-open");
            getPossibleScopes(currentRoleKey, function(scopeObject){
                $("#scopeObjectBlock").show();
                $("#scopeObject").empty();
                $("#scopeObject").append($(Mustache.render(scopeObjectTemplate, {scopeObject : scopeObject})));
                $("#scopeObject").selectpicker('refresh');
                $("#scopeObject").selectpicker('val', null);
            });
        });
        //------------------

        //------------------
        // Выбор объекта - области доверенности
        //------------------
        $("#scopeObject").on("change", function(){
            currentScopeObjectId = $(this).val();

            $("#expiredDateBlock").hide();
            $("#letterOfAuthorityAttributesBlock").hide();
            $("#createLetterOfAuthorityButton").hide();

            $("#delegatesBlock").show();

            $("body").addClass("modal-open");
            // Загрузить таблицу с делегатами
            possibleDelegatesStore.load();
        });

        // Продлить доверенность
        $("#prolongLetterOfAuthority").click(function(){
            if ($(this).prop("checked")) {
                $("#prolongLetterOfAuthorityDateBlock").show();
            } else {
                $("#prolongLetterOfAuthorityDateBlock").hide();
            }
        });

        // Удалить доверенность
        $("#deleteLetterOfAuthorityButton").click(function(){
            bootbox.confirm("Вы действительно хотите удалить доверенность?", function(result) {
                if (result) {
                    deleteLetterOfAuthority(currentId, function(){
                        $("#editLetterOfAuthorityWindow").modal("hide");
                        ownerLetterOfAuthoritiesStore.load();
                    });
                }
            });
        });
        // Обновить доверенность
        $("#updateLetterOfAuthorityButton").click(function(){
            currentLetterOfAuthority.active = $("#edit_active").prop("checked");
            if ($("#prolongLetterOfAuthority").prop("checked")) {
                currentLetterOfAuthority.expiredDate = $("#edit_expiredDate").val();
            } else {
                currentLetterOfAuthority.expiredDate = null;
            }
            updateLetterOfAuthority(currentLetterOfAuthority, function(){
                currentLetterOfAuthority = null;
                $("#editLetterOfAuthorityWindow").modal("hide");
                ownerLetterOfAuthoritiesStore.load();
            });
        });

        // Добавить атрибут
        $("#addLetterOfAuthorityAttrButton").click(function(){
            var newAttrNode = $(Mustache.render(attributeTemplate, {}));
            $("#letterOfAuthorityAttributesContainer").append(newAttrNode);
        });
        // Удалить атрибут
        $("body").on('click', '.deleteAttrButton', function (e) {
            $(this).parent().remove();
        });

        // Добавить атриубут у существующей доверенности
        $("#appendLetterOfAuthorityAttrButton").click(function(){
            //
            var jqNameInput = $(".attr_name", $("#letterOfAuthorityAttributyAddContainer"));
            var jqValueInput = $(".attr_value", $("#letterOfAuthorityAttributyAddContainer"));

            var errorText = "";
            var attrName = jqNameInput.val();
            var attrValue = jqValueInput.val();
            if (attrName == null || attrName == "") {
                errorText = "У атрибута не указано наименование"
            }
            if ((attrValue == null || attrValue == "") && errorText == "") {
                errorText = "У атрибута не указано значение";
            }
            if (errorText != "") {
                bootbox.alert(errorText);
                return false;
            }
            saveLetterOfAuthorityAttribute(currentId, attrName, attrValue, function(){
                jqNameInput.val("");
                jqValueInput.val("");
                // Перезагрузить таблицу с атрибутами
                letterOfAuthorityAttributesStore.load();
            });
        });

        // Удалить атрибут
        $("#deleteLetterOfAuthorityAttrButton").click(function(){
            var attrId = $("#editLetterOfAuthorityAttrWindow").attr("attr_id");
            deleteLetterOfAuthorityAttribute(attrId, function(){
                $("#editLetterOfAuthorityAttrWindow").modal("hide");
                // Перезагрузить таблицу с атрибутами
                letterOfAuthorityAttributesStore.load();
            });
        });
        // Обновить атрибут
        $("#updateLetterOfAuthorityAttrButton").click(function(){
            var attrId = $("#editLetterOfAuthorityAttrWindow").attr("attr_id");
            var attrName = $(".attr_name", $("#editLetterOfAuthorityAttrWindow")).val();
            var attrValue = $(".attr_value", $("#editLetterOfAuthorityAttrWindow")).val();
            updateLetterOfAuthorityAttribute(attrId, attrName, attrValue, function(){
                $("#editLetterOfAuthorityAttrWindow").modal("hide");
                // Перезагрузить таблицу с атрибутами
                letterOfAuthorityAttributesStore.load();
            });
        });
    });
</script>
<h2>${currentPageTitle}</h2>
<hr/>
<div id="ownerLetterOfAuthority-grid"></div>
<div id="ownerLetterOfAuthorityGridSearchResult"></div>
<hr/>
<button type="button" class="btn btn-primary" style="margin-top: 5px;" id="createLetterOfAuthorityModalButton">Создать доверенность</button><br/>
<hr/>
<div class="modal fade" role="dialog" id="createLetterOfAuthorityWindow" aria-labelledby="createLetterOfAuthorityTextLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="createLetterOfAuthorityTextLabel">Добавить доверенность</h4>
            </div>
            <div class="modal-body">
                <div class="div-control">
                    <label>Тип объекта в рамках которого создаётся доверенность</label>
                    <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeType">
                        <c:forEach var="scopeType" items="${scopeTypes}" varStatus="i">
                            <option value="${scopeType.key}">${scopeType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="div-control" id="letterOfAuthorityRoleBlock">
                    <label>Роль доверенности</label>
                    <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="letterOfAuthorityRole"></select>
                </div>
                <div class="div-control" id="scopeObjectBlock">
                    <label>Объект в рамках которого создаётся доверенность</label>
                    <select class="form-control selectpicker" field-list-index="0" data-live-search="true" data-width="100%" id="scopeObject"></select>
                </div>
                <div class="div-control" id="delegatesBlock">
                    <!--label>Делегат, которому выдаётся доверенность</label-->
                    <div id="delegates-grid"></div>
                    <div id="delegatesGridSearchResult"></div>
                </div>
                <div class="div-control" id="expiredDateBlock">
                    <label>Дата окончания действия доверенности</label>
                    <input type="text" class="form-control" id="expiredDate" />
                </div>
                <div class="div-control" id="letterOfAuthorityAttributesBlock">
                    <div><label>Атрибуты доверенности</label></div>
                    <button type="button" class="btn btn-primary" id="addLetterOfAuthorityAttrButton">Добавить атрибут</button>
                    <div id="letterOfAuthorityAttributesContainer"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="createLetterOfAuthorityButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- Модальное окно для редактирования доверенности-->
<div class="modal fade" role="dialog" id="editLetterOfAuthorityWindow" aria-labelledby="editLetterOfAuthorityTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editLetterOfAuthorityTextLabel">Редактировать доверенность</h4>
            </div>
            <div class="modal-body">
                <div class="div-control">
                    <label>Наименование роли доверенности</label>
                    <input type="text" class="form-control" id="edit_roleName" disabled="disabled" />
                </div>
                <div class="div-control">
                    <label>ФИО делегата</label>
                    <input type="text" class="form-control" id="edit_delegateName" disabled="disabled" />
                </div>
                <div class="div-control">
                    <label>Дата окончания действия доверенности</label>
                    <input type="text" class="form-control" id="old_expiredDate" disabled="disabled" />
                </div>
                <div class="div-control">
                    <label>
                        Продлить доверенность
                        <input type="checkbox" id="prolongLetterOfAuthority" />
                    </label>
                </div>
                <div class="div-control" id="prolongLetterOfAuthorityDateBlock">
                    <label>Новая дата окончания действия доверенности</label>
                    <input type="text" class="form-control" id="edit_expiredDate" />
                </div>
                <div class="div-control">
                    <label>
                        Активность доверенности
                        <input type="checkbox" id="edit_active" />
                    </label>
                </div>
                <div class="div-control" id="letterOfAuthorityAttributesEditBlock">
                    <div id="letterOfAuthorityAttributesEdit-grid"></div>
                    <div id="letterOfAuthorityAttributesGridEditSearchResult"></div>
                    <div id="letterOfAuthorityAttributyAddContainer">
                        <div style="display: inline-block; vertical-align: bottom;">
                            <span>Наименование: </span>
                            <input type="text" class="form-control attr_name" style="width: 200px;" />
                        </div>
                        <div style="display: inline-block; margin-left: 10px; vertical-align: bottom;">
                            <span>Значение: </span>
                            <input type="text" class="form-control attr_value" style="width: 200px;" />
                        </div>
                        <button type="button" class="btn btn-primary" id="appendLetterOfAuthorityAttrButton" style="margin-left: 10px;">Добавить атрибут</button>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" id="deleteLetterOfAuthorityButton">Удалить</button>
                <button type="button" class="btn btn-primary" id="updateLetterOfAuthorityButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<!-- Модальное окно для редактирования атрибута-->
<div class="modal fade" role="dialog" id="editLetterOfAuthorityAttrWindow" aria-labelledby="editLetterOfAuthorityAttrTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editLetterOfAuthorityAttrTextLabel">Редактировать атрибут</h4>
            </div>
            <div class="modal-body">
                <div class="div-control">
                    <div style="display: inline-block; vertical-align: bottom;">
                        <span>Наименование: </span>
                        <input type="text" class="form-control attr_name" style="width: 200px;" />
                    </div>
                    <div style="display: inline-block; margin-left: 10px; vertical-align: bottom;">
                        <span>Значение: </span>
                        <input type="text" class="form-control attr_value" style="width: 200px;" />
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" id="deleteLetterOfAuthorityAttrButton">Удалить</button>
                <button type="button" class="btn btn-primary" id="updateLetterOfAuthorityAttrButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script id="letterOfAuthorityRolesTemplate" type="x-tmpl-mustache">
    {{#roles}}
        <option value="{{key}}">{{name}}</option>
    {{/roles}}
</script>
<script id="scopeObjectTemplate" type="x-tmpl-mustache">
    {{#scopeObject}}
        <option value="{{id}}">{{name}}</option>
    {{/scopeObject}}
</script>
<script id="attributeTemplate" type="x-tmpl-mustache">
    <div class="attrValuesContainer">
        <div style="display: inline-block; vertical-align: bottom;">
            <span>Наименование: </span>
            <input type="text" class="form-control attr_name" style="width: 200px;" />
        </div>
        <div style="display: inline-block; margin-left: 10px; vertical-align: bottom;">
            <span>Значение: </span>
            <input type="text" class="form-control attr_value" style="width: 200px;" />
        </div>
        <button type="button" class="btn btn-danger deleteAttrButton" style="vertical-align: bottom;">Удалить</button>
    <div>
</script>