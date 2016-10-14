<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<link rel="stylesheet" type="text/css" href="/css/bpeditor/layout.css?v=${buildNumber}"/>
<div class="row">
    <div class="subheader editor-toolbar" id="editor-header">
        <div class="btn-group">
            <div class="btn-toolbar pull-left">
                <button id="bpeditor-save" title="Save the model" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-save" data-toggle="tooltip" title="Save the model"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>
                <button id="bpeditor-cut" title="Cut (select one or more elements in your business process)" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-cut" data-toggle="tooltip" title="Cut (select one or more elements in your business process)"></i>
                </button>
                <button id="bpeditor-copy" title="Copy (select one or more elements in your business process)" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-copy" data-toggle="tooltip" title="Copy (select one or more elements in your business process)"></i>
                </button>
                <button id="bpeditor-paste" title="Paste" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-paste" data-toggle="tooltip" title="Paste"></i>
                </button>
                <button id="bpeditor-delete" title="Delete the selected element" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-delete" data-toggle="tooltip" title="Delete the selected element"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>
                <button id="bpeditor-redo" title="Redo" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-redo" data-toggle="tooltip" title="Redo"></i>
                </button>
                <button id="bpeditor-undo" title="Undo" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-undo" data-toggle="tooltip" title="Undo"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>
                <%--<button id="bpeditor-align-vertical" title="Align model vertical" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-align-vertical" data-toggle="tooltip" title="Align model vertical"></i>
                </button>
                <button id="bpeditor-align-horizontal" title="Align model horizontal" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-align-horizontal" data-toggle="tooltip" title="Align model horizontal"></i>
                </button>
                <button id="bpeditor-same-size" title="Same size" class="btn btn-inverse" disabled="disabled">
                    <i class="toolbar-button editor-icon editor-icon-same-size" data-toggle="tooltip" title="Same size"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>--%>
                <button id="bpeditor-zoom-in" title="Zoom in" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-zoom-in" data-toggle="tooltip" title="Zoom in"></i>
                </button>
                <button id="bpeditor-zoom-out" title="Zoom out" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-zoom-out" data-toggle="tooltip" title="Zoom out"></i>
                </button>
                <button id="bpeditor-zoom-actual" title="Zoom to actual size" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-zoom-actual" data-toggle="tooltip" title="Zoom to actual size"></i>
                </button>
                <button id="bpeditor-zoom-fit" title="Zoom to fit" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-zoom-fit" data-toggle="tooltip" title="Zoom to fit"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>
                <button id="bpeditor-add-bendpoint" title="Add bend-point to the selected sequence flow" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-bendpoint-add" data-toggle="tooltip" title="Add bend-point to the selected sequence flow"></i>
                </button>
                <button id="bpeditor-remove-bendpoint" title="Remove bend-point from the selected sequence flow" class="btn btn-inverse">
                    <i class="toolbar-button editor-icon editor-icon-bendpoint-remove" data-toggle="tooltip" title="Remove bend-point from the selected sequence flow"></i>
                </button>
                <button class="btn btn-inverse separator" disabled="disabled">
                    <div class="toolbar-separator"></div>
                </button>
                <button title="Развернуть модель" class="btn btn-inverse" id="bpeditor-deploy">
                    <i class="toolbar-button editor-icon editor-icon-deploy" data-toggle="tooltip" title="Развернуть модель"></i>
                </button>
            </div>
        </div>
    </div>
</div>
<div class="row fullscreen-child editor-content">
    <div class="col-sm-3 col-md-2 fullscreen-child" id="bp-editor-components-holder">
        <div class="panel-group" role="tablist" id="bp-editor-components"></div>
    </div>
    <div class="col-sm-6 col-md-8 fullscreen-child" id="bp-editor-oryx">
        <div id="canvasSection"></div>
    </div>
    <div class="col-sm-3 col-md-2 fullscreen-child" id="bp-editor-properties">
    </div>
</div>
<script>
    var fixUpModel = function (json, isOut) {
        if(!json || !json.properties) {
            return;
        }
        var id = json.properties["__stencil__entity__id__"];
        if(id) {
            json.stencil.id = isOut ? "rabbitTaskStencil" : id;
        }
        if(json.childShapes && json.childShapes.length > 0) {
            for (var i = 0; i < json.childShapes.length; i++) {
                var childJson = json.childShapes[i];
                fixUpModel(childJson, isOut);
            }
        }
    };
    var model = ${model};
    fixUpModel(model);
    require([
        "bpeditor/PropertiesComponent",
        "bpeditor/ComponentsMenu",
        "bpeditor/EditorComponentWithQuickMenu",
        "jquery",
        "utils/utils"
    ], function (PropertiesComponent, ComponentsMenu, EditorComponent, $, utils) {
        var menu = new ComponentsMenu({el: $("#bp-editor-components")}).render();
        var editorComponent = new EditorComponent({jsonModel: model}).render();
        var propertiesComponent = new PropertiesComponent({
            el: $("#bp-editor-properties"),
            editor: editorComponent.editor
        }).render();



        $("#bpeditor-undo").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.undo();
        }));

        $("#bpeditor-redo").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.redo();
        }));

        $("#bpeditor-cut").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.cut();
        }));

        $("#bpeditor-copy").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.copy();
        }));

        $("#bpeditor-paste").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.paste();
        }));

        $("#bpeditor-delete").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.deleteItem();
        }));

        $("#bpeditor-zoom-in").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.zoomIn();
        }));

        $("#bpeditor-zoom-out").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.zoomOut();
        }));

        $("#bpeditor-zoom-actual").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.zoomActual();
        }));

        $("#bpeditor-zoom-fit").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.zoomFit();
        }));

        $("#bpeditor-align-vertical").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.alignVertical();
        }));

        $("#bpeditor-align-horizontal").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.alignHorizontal();
        }));

        $("#bpeditor-same-size").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.sameSize();
        }));

        $("#bpeditor-add-bendpoint").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.addBendPoint();
        }));

        $("#bpeditor-remove-bendpoint").on("click", utils.clickEventWrapper(function (e) {
            editorComponent.removeBendPoint();
        }));

        editorComponent.on(EditorComponent.StatusChangedEvent.paste, function (enabled) {
            $("#bpeditor-paste").attr("disabled", !enabled);
        });

        editorComponent.on(EditorComponent.StatusChangedEvent.undo, function (enabled) {
            $("#bpeditor-undo").attr("disabled", !enabled);
        });

        editorComponent.on(EditorComponent.StatusChangedEvent.redo, function (enabled) {
            $("#bpeditor-redo").attr("disabled", !enabled);
        });

        $("#bpeditor-deploy").on("click", utils.clickEventWrapper(function (e) {
            $.ajax({
                url: "/admin/bpeditor/model/" + model.modelId + "/deploy",
                method: 'POST',
                success: function () {
                    alert("successfully")
                },
                error: function () {
                    alert("error");
                }
            });
        }));

        $("#bpeditor-save").on("click", utils.clickEventWrapper(function (e) {

            var editor = editorComponent.editor;
            var json = editor.getJSON();
            json.modelId = model.modelId;
            fixUpModel(json, true);
            json = JSON.stringify(json);

            var selection = editor.getSelection();
            editor.setSelection([]);

            // Get the serialized svg image source
            var svgClone = editor.getCanvas().getSVGRepresentation(true);
            editor.setSelection(selection);
            if (editor.getCanvas().properties["oryx-showstripableelements"] === false) {
                var stripOutArray = jQuery(svgClone).find(".stripable-element");
                for (var i = stripOutArray.length - 1; i >= 0; i--) {
                    stripOutArray[i].remove();
                }
            }

            // Parse dom to string
            var svgDOM = $(svgClone).clone().wrap('<div/>').parent().html();

            var params = {
                model: json,
                svg: ""//svgDOM
            };

            // Update
            $.ajax({
                url: "/admin/bpeditor/model/" + model.modelId + "/save",
                method: 'PUT',
                data: JSON.stringify(params),
                contentType: "application/json; charset=UTF-8",
                success: function () {
                    alert("successfully")
                },
                error: function () {
                    alert("error");
                }
            });
        }));
    });
</script>