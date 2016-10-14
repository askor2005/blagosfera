<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<script type="text/javascript">
    // Загрузить все роли объединения
    function loadPermissions(callBack, errorCallBack) {
        $.radomJsonPost(
            "/admin/community_permissions/getPermissions.json",
            {},
            callBack,
            errorCallBack
        )
    }

    // Загрузкить возможные объединения
    function loadPossibleCommunities(callBack, errorCallBack) {
        $.radomJsonPost(
            "/admin/community_permissions/getPossibleCommunities.json",
            {},
            callBack,
            errorCallBack
        )
    }

    // Сохранить роль
    function savePermission(id, title, description, associationForms, communities, callBack) {
        $.radomJsonPostWithWaiter(
            "/admin/community_permissions/savePermission.json",
            JSON.stringify({
                id: id,
                title: title,
                description: description,
                associationForms: associationForms,
                communities: communities
            }),
            callBack,
            null,
            {
                contentType : 'application/json'
            }
        )
    }

    var permissions = {};
    var permissionsTemplate = null;
    var permissionEditTemplate = null;
    var associationFormTemplate = null;
    var communitiesTemplate = null;

    // Установить в модель роли выбранные объединения
    function initPermissionCommunities(permission, communities) {
        var permissionCommunities = [];
        if (permission.communities != null) {
            for (var index in permission.communities) {
                var communityId = permission.communities[index];
                for (var j in communities) {
                    var possibleCommunity = communities[j];
                    if (communityId == possibleCommunity.id){
                        possibleCommunity.disabled = true;
                        permissionCommunities.push(possibleCommunity);
                    } else {
                        possibleCommunity.disabled = false;
                    }
                }
            }
        }

        var jqCommunitiesDataNode = $(Mustache.render(communitiesTemplate, {
            permissionCommunities : permissionCommunities
        }));
        $("#communitiesBlock", $("#permissionEditModal")).empty();
        $("#communitiesBlock", $("#permissionEditModal")).append(jqCommunitiesDataNode);

        $("#community_select").selectpicker("refresh");
        $("#community_select").selectpicker("val", null);

        $("table", $("#permissionEditModal")).fixMe();

        // Удалить объединение из списка
        $(".deleteCommunity", $("table", $("#permissionEditModal"))).click(function(){
            var communityId = $(this).attr("community_id");
            $("option[value=" + communityId + "]", $("#community_select")).prop("disabled", false);
            for (var index in permission.communities) {
                var commId = permission.communities[index];
                if (commId == communityId) {
                    delete permission.communities[index];
                    break;
                }
            }
            initPermissionCommunities(permission, communities);
        });
    }

    // Инициализация конторола с формами объединений
    function initAssociationFormControl(associationFormId, jqParentNode) {
        var jqAssociationFormNode = $(Mustache.render(associationFormTemplate,{}));
        jqParentNode.append(jqAssociationFormNode);
        // Инициализация сфер деятельности
        RameraListEditorModule.init($(".associationForm", jqAssociationFormNode),
                {
                    labelClasses : ["checkbox-inline"],
                    labelStyle : "margin-left: 10px;",
                    selectedItems : [associationFormId],
                    selectClasses: ["form-control"]
                },
                function (event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        $(".association_form_id", jqParentNode).val(data.value);
                    }
                }
        );
        // Удалить форму объединения у роли
        $(".deleteAssociationForm", jqParentNode).click(function(){
            $(this).closest(".associationFormBlock").remove();
        });
    }

    // Отобразить данные на странице
    function drawPermissionsTable() {
        $(".table-responsive").empty();
        $("#tableDataLoaderAnimation").show();
        loadPermissions(function(response){
            $("#tableDataLoaderAnimation").hide();

            for (var index in response) {
                var permission = response[index];
                permissions[permission.id] = permission;
            }

            var findPermissions = response.length > 0;
            var jqTableNode = $(Mustache.render(permissionsTemplate,
                    {
                        permissions : response,
                        findPermissions : findPermissions,
                        hasError : false,
                        errorMessage : ""
                    }
            ));
            $(".table-responsive").append(jqTableNode);

            jqTableNode.fixMe();

            $(".editPermission").radomTooltip({
                container: "body",
                delay : { "show": 100, "hide": 100 }
            });

            $(".editPermission").click(function(){
                var permissionId = $(this).attr("permission_id");
                var permission = permissions[permissionId];
                $("#modalContent", $("#permissionEditModal")).empty();
                $("#modalDataLoaderAnimation").show();

                if (permission.securityRole) {
                    loadPossibleCommunities(function (communities) {
                        $("#modalDataLoaderAnimation").hide();
                        var permission = permissions[permissionId];

                        var jqModalDataNode = $(Mustache.render(permissionEditTemplate, {
                            permission : permission,
                            possibleCommunities : communities
                        }));
                        $("#modalContent", $("#permissionEditModal")).append(jqModalDataNode);

                        // Устанавливаем объединения, которые выбраны
                        initPermissionCommunities(permission, communities);

                        // Добавить объединение в роль
                        $("#addCommunity", jqModalDataNode).click(function(){
                            var jqOption = $("#community_select").find("option:selected");
                            var communityId = jqOption.val();
                            if (communityId == null) {
                                bootbox.alert("Необходимо выбрать объединение");
                                return false;
                            }
                            jqOption.prop("disabled", true);
                            permission.communities.push(communityId);
                            initPermissionCommunities(permission, communities);
                        });

                    }, function (errorPagesResponse) {
                        $("#modalDataLoaderAnimation").hide();
                        $("#modalContent", $("#permissionEditModal")).text(errorPagesResponse.message);
                    });
                } else {
                    $("#modalDataLoaderAnimation").hide();
                    var jqModalDataNode = $(Mustache.render(permissionEditTemplate, {
                        permission : permission
                    }));
                    $("#modalContent", $("#permissionEditModal")).append(jqModalDataNode);
                    for (var index in permission.associationForms) {
                        var associationFormId = permission.associationForms[index];
                        initAssociationFormControl(associationFormId, $("#associationFormsBlock"));
                    }
                    // Добавить новую форму объединения к роли
                    $("#addAssociationForm").click(function(){
                        initAssociationFormControl(null, $("#associationFormsBlock"));
                    });
                }

                $("#permissionEditModal").modal("show");
            });
        }, function(response){
            $("#tableDataLoaderAnimation").hide();

            var jqTableNode = $(Mustache.render(permissionsTemplate,
                {
                    hasError : true,
                    errorMessage : response.message
                }
            ));
            $(".table-responsive").append(jqTableNode);

            jqTableNode.fixMe();
        });
    }

    $(document).ready(function() {
        permissionsTemplate = $("#permissionsTemplate").html();
        permissionEditTemplate = $("#permissionEditTemplate").html();
        associationFormTemplate = $("#associationFormTemplate").html();
        communitiesTemplate = $("#communitiesTemplate").html();

        Mustache.parse(permissionsTemplate);
        Mustache.parse(permissionEditTemplate);
        Mustache.parse(associationFormTemplate);
        Mustache.parse(communitiesTemplate);

        drawPermissionsTable();

        $("#permissionEditModalSaveButton").click(function(){
            var id = $("#permission_id").val() == null ? null : $("#permission_id").val();
            var title = $("#permission_title").val() == null ? null : $("#permission_title").val();
            var description = $("#permission_description").val() == null ? null : $("#permission_description").val();

            var associationForms = null;
            var communities = null;

            // Если это системная роль - то можно добавлять объединения
            if (permissions[id].securityRole) {
                communities = permissions[id].communities;
            } else { // Для простой роли можно добавлять формы объединения
                associationForms = [];
                $(".association_form_id").each(function(){
                    var formId = $(this).val();
                    if (formId != null && formId != "") {
                        associationForms.push(formId);
                    }
                });
            }

            // Сохранить изменения
            savePermission(id, title, description, associationForms, communities,
                function() {
                    $("#permissionEditModal").modal("hide");
                    drawPermissionsTable();
                }
            );
        });
    });
</script>
<div class="page-header">
    <h1>${currentPageTitle}</h1>
</div>

<script id="permissionsTemplate" type="x-tmpl-mustache">
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th>Название</th>
            <th width="100"></th>
        </tr>
        </thead>
        <tbody>
            {{#hasError}}
                <tr>
                    <td colspan="2">{{errorMessage}}</td>
                </tr>
            {{/hasError}}
            {{^hasError}}
                {{#findPermissions}}
                    {{#permissions}}
                        <tr id="{{id}}">
                            <td
                                {{#securityRole}}
                                    style="color: red;" title="Системная роль"
                                {{/securityRole}}
                                {{^securityRole}}
                                    title="Обычная роль"
                                {{/securityRole}}
                                >
                                {{title}}
                            </td>
                            <td class="text-right">
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-default editPermission" permission_id="{{id}}" title="Редатировать роль"><i class="fa fa-fw fa-pencil"></i></button>
                                </div>
                            </td>
                        </tr>
                    {{/permissions}}
                {{/findPermissions}}
                {{^findPermissions}}
                    <tr>
                        <td colspan="2">Ролей нет</td>
                    </tr>
                {{/findPermissions}}
            {{/hasError}}
        </tbody>
    </table>
</script>

<script id="permissionEditTemplate" type="x-tmpl-mustache">
    <input type="hidden" value="{{permission.id}}" id="permission_id" />
    {{#permission.securityRole}}
        <div class="form-group">
            <label>Системная роль (роль, которая даёт права в разделах системы)</label>
        </div>
    {{/permission.securityRole}}
    {{^permission.securityRole}}
        <div class="form-group">
            <label>Обычная роль (роль, которая даёт права только в рамках объединения)</label>
        </div>
    {{/permission.securityRole}}
    <div class="form-group">
        <label>Название роли</label>
        <input type="text" class="form-control" placeholder="Название роли" value="{{permission.title}}" id="permission_title" />
    </div>
    <div class="form-group">
        <label>Код роли</label>
        <input type="text" class="form-control" value="{{permission.name}}" readonly="true" />
    </div>
    <div class="form-group">
        <label>Описание роли</label>
        <input type="text" class="form-control" placeholder="Описание роли" value="{{permission.description}}" id="permission_description" />
    </div>
    {{#permission.securityRole}}
        <div class="form-group" style="position: relative;">
            <label>Объединения роли</label>
            <div style="position: absolute; right: 50px;left: 0px;">
                <select id="community_select" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                    {{#possibleCommunities}}
                        <option value="{{id}}" {{#disabled}}disabled="disabled"{{/disabled}}>{{name}}</option>
                    {{/possibleCommunities}}
                </select>
            </div>
            <div style="position: absolute; right: 0px;">
                <button class="btn btn-success" id="addCommunity" title="Добавить объединение"><i class="fa fa-fw fa-plus"></i></button>
            </div>

            <div id="communitiesBlock"></div>
        </div>
    {{/permission.securityRole}}
    {{^permission.securityRole}}
        <div class="form-group">
            <label>Формы объединения роли</label>
            <button class="btn btn-success" id="addAssociationForm" title="Добавить форму объединения"><i class="fa fa-fw fa-plus"></i></button>
            <div id="associationFormsBlock"></div>
        </div>
    {{/permission.securityRole}}
</script>

<script id="associationFormTemplate" type="x-tmpl-mustache">
    <div style="border: 1px #ccc solid; margin-top: 5px; padding: 5px;" class="associationFormBlock">
        <div style="height: 30px;">
            <button style="float: right;" class="btn btn-danger deleteAssociationForm" title="Удалить форму объединения"><i class="fa fa-fw fa-remove"></i></button>
        </div>
        <div class="associationForm" rameraListEditorName='community_association_forms_groups'></div>
        <input type="hidden" class="association_form_id" />
    </div>
</script>

<script id="communitiesTemplate" type="x-tmpl-mustache">
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th>Название</th>
            <th width="100"></th>
        </tr>
        </thead>
        <tbody>
            {{#permissionCommunities}}
                <tr id="{{id}}">
                    <td>{{name}}</td>
                    <td class="text-right">
                        <input type="hidden" value="{{id}}" />
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-danger deleteCommunity" community_id="{{id}}" title="Удалить объединение"><i class="fa fa-fw fa-remove"></i></button>
                        </div>
                    </td>
                </tr>
            {{/permissionCommunities}}
        </tbody>
    </table>
</script>

<div class="table-responsive"></div>
<div class="row list-loader-animation" id="tableDataLoaderAnimation" style="display: block;"></div>

<!-- Модальное окно для редактирования роли-->
<div class="modal fade" role="dialog" id="permissionEditModal" aria-labelledby="permissionEditModalTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="permissionEditModalTextLabel">Редактировать роль</h4>
            </div>
            <div class="modal-body">
                <div id="modalContent"></div>
                <div class="row list-loader-animation" id="modalDataLoaderAnimation" style="display: block;"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="permissionEditModalSaveButton" style="float: left;">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->