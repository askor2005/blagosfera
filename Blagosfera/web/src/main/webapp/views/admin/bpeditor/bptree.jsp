<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<div class="row">
    <div class="col-md-12">
        <h3>
            Дерево бизнес процессов
        </h3>
    </div>
</div>
<div class="row">
    <div class="col-md-6">
        <div class="btn-group">
            <button type="button" class="btn btn-primary" id="bp-editor-add-root-folder">
                <i class="glyphicon glyphicon-plus"></i>
                Корневая папка
            </button>
            <button type="button" class="btn btn-success" id="bp-editor-add-root-model">
                <i class="glyphicon glyphicon-plus"></i>
                Корневая модель
            </button>
        </div>
    </div>
    <div class="col-md-6">
        <%--<div class="form-inline">
            <div class="form-group">
                <label for="bp-search">Поиск</label>
                <input type="text" class="form-control" id="bp-search" placeholder="Поиск">
            </div>
        </div>--%>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        <div id="bpeditor-tree"></div>
    </div>
</div>
<script>
    require(["backbone/components/ModalLoadingScreen", "underscore", "jquery", "jstree"], function (ModalLoadingScreen, _, $) {
        var baseUrl = "/admin/bpeditor/tree/items";
        var idPrefix = _.uniqueId("jstree-node-") + "-";
        var fixUpData = function (data) {
            var fixed = _.omit(data, "id");
            fixed.id = idPrefix + data.id;
            return fixed;
        };
        var removeIdPrefix = function (id) {
            return id && id.replace(idPrefix, "");
        };
        
        var defaultSuccessOnCreate = function (options) {
            return function (data) {
                var ref = $('#bpeditor-tree').jstree(true);
                var parentNode = ref.get_node(options.parentId);
                if(!parentNode.state || parentNode.state.loaded) {
                    ref.create_node(
                            options.parentId || null,
                            fixUpData(data),
                            options.position || "first"
                    );

                }
                if(parentNode.state && !parentNode.state.opened) {
                    ref.open_node(parentNode)
                }
            }
        };

        var appendModelWithHref = function (node) {
            _.defer(function () {
                var ref = $('#bpeditor-tree').jstree(true);
                node = ref.get_node(node);
                if(node.type === "model") {
                    var $li = $("#" + node.id);
                    if($li.has("div[type=editModel]").length === 0) {
                        var elem = '<div class="pull-right" style="margin: 4px" type="editModel">' +
                                '<a href="/admin/bpeditor/' + node.original.modelId + '">' +
                                '<i class="glyphicon glyphicon-pencil"></i>' +
                                '</a>' +
                                '</div>'
                        $li.append(elem);
                    }
                } else if(node.children.length > 0) {
                    _.each(node.children, appendModelWithHref);
                }
            });
        };
        
        var create = function (options) {
            options = _.defaults(options, {
                parentId: null,
                name: "",
                type: "folder",
                position: "first",
                error: null,
                context: this
            });
            if(!options.success) {
                options.success = defaultSuccessOnCreate(options);
            }
            var screen = new ModalLoadingScreen().render().show();
            $("body").append(screen);
            var resp = $.ajax({
                url: baseUrl,
                type: "POST",
                contentType: "application/json; charset=UTF-8",
                dataType: "json",
                data: JSON.stringify({
                    parentId: removeIdPrefix(options.parentId),
                    name: options.name,
                    type: options.type,
                    position: options.position,
                }),
                success: options.success,
                error: options.error,
                context: options.context || this
            });
            resp.always(function () {
                screen.remove();
            });
            return resp;
        };

        var showCreateModal = function (options) {
            var $modal = $("#bp-editor-modal");
            var $form = $("#bp-editor-modal-form");
            var fun = function () {
                $modal.modal("hide");
                var opts = _.clone(options);
                opts.name = $("#bp-editor-modal-name").val();
                create(opts);
                return false;
            };
            var hideFun = function () {
                $modal.off("hide.bs.modal", hideFun);
                $form.off("submit", fun);
            };
            $form.on("submit", fun);
            $modal.on("hide.bs.modal", hideFun);
            $modal.modal("show");
        };

        var itemActionCreate = function(type) {
            return function (data) {
                var inst = $.jstree.reference(data.reference);
                var node = inst.get_node(data.reference);
                showCreateModal({
                    type: type,
                    parentId: node.id,
                    position: "first"
                });
            }
        };

        var itemActionCreateFolder = itemActionCreate("folder");
        var itemActionCreateModel = itemActionCreate("model");

        var itemActionRemove = function(type) {
            return function (data) {
                var inst = $.jstree.reference(data.reference);
                var node = inst.get_node(data.reference);
                var screen = new ModalLoadingScreen().render().show();
                var resp = $.ajax({
                    url: baseUrl,
                    type: "DELETE",
                    contentType: "application/json; charset=UTF-8",
                    data: JSON.stringify({
                        id: removeIdPrefix(node.id),
                        type: type
                    }),
                    success: function () {
                        this.delete_node(node);
                    },
                    error: function () {
                        alert("error");
                    },
                    context: inst
                });
                resp.always(function () {
                    screen.remove();
                });
                return resp;
            }
        };

        var itemActionRemoveFolder = itemActionRemove("folder");
        var itemActionRemoveModel = itemActionRemove("model");

        $(function () {
            $("bp-editor-modal").on("show.bs.modal", function () {
                $("#bp-editor-modal-name").val("");
            });
            $("#bp-editor-add-root-folder").on("click", _.partial(showCreateModal, {type: "folder", parentId: null, position: "first"}));
            $("#bp-editor-add-root-model").on("click", _.partial(showCreateModal, {type: "model", parentId: null, position: "first"}));
            var $form = $("#bp-editor-modal-form");
            $form.validate({
                rules: {
                    itemName: "required"
                },
                messages: {
                    itemName: "Название не может быть пустым"
                }
            });
            $("#bp-editor-modal-create").on("click", function () {
                if($form.valid()) {
                    $form.submit();
                }
            });

            var $bpeditor = $("#bpeditor-tree");
            $bpeditor.jstree({
                "core": {
                    "check_callback": true,
                    'force_text': true,
                    "themes": {"stripes": true},
                    'data': function (o, cb) {
                        var url = baseUrl + "?preventCache=" + new Date().getTime();
                        return $.ajax({
                            url: url,
                            type: "GET",
                            dataType: "json",
                            data: o.id === "#" ? null : {id: removeIdPrefix(o.id)},
                            context: this,
                            success: function (data) {
                                cb.call(this, _.map(data, fixUpData));
                            },
                            error: function (f) {
                                callback.call(this, false);
                                this._data.core.last_error = { 'error' : 'ajax', 'plugin' : 'core', 'id' : 'core_04', 'reason' : 'Could not load node', 'data' : JSON.stringify({ 'id' : obj.id, 'xhr' : f }) };
                                this.settings.core.error.call(this, this._data.core.last_error);
                            }
                        });
                    }
                },
                contextmenu: {
                    items: function (node) {
                        if(node.type === "model") {
                            return {
                                /*"rename": {
                                    label: "Переименовать",
                                    action: function (data) {
                                        console.log("rename", data);
                                    }
                                },*/
                                "remove": {
                                    label: "Удалить",
                                    action: itemActionRemoveModel
                                }
                            }
                        } else {
                            return {
                                "createFolder": {
                                    label: "Создать вложенную папку",
                                    action: itemActionCreateFolder
                                },
                                "createModel": {
                                    label: "Создать вложенную модель",
                                    action: itemActionCreateModel
                                },
                                "remove": {
                                    label: "Удалить",
                                    action: itemActionRemoveFolder
                                }
                            }
                        }
                    }
                },
                "types": {
                    "#": {
                        "valid_children": ["folder", "model"]
                    },
                    "folder": {
                        "icon": "glyphicon glyphicon-folder-open",
                        "valid_children": ["folder", "model"]
                    },
                    "model": {
                        icon: "glyphicon glyphicon-file",
                        "valid_children": []
                    }
                },
                "plugins": [
                    "contextmenu", /*"dnd",*/ /*"search",*/
                    "state", "types", "wholerow"
                ]
            });
            $bpeditor.on("redraw.jstree", function (e, obj) {
                _.each(obj.nodes, appendModelWithHref);
            });
            $bpeditor.on("load_node.jstree open_node.jstree create_node.jstree show_node.jstree", function (e, obj) {
                appendModelWithHref(obj.node);
            });
        });
    });
</script>
<link rel="stylesheet" type="text/css" href="/css/jstree/style.css?v=${buildNumber}"/>
<jsp:include page="bptreeAddItemModal.jsp"/>