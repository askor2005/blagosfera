<%--
  Created by IntelliJ IDEA.
  User: aotts
  Date: 04.11.2015
  Time: 15:02
  Представление в котором происходит добавление, редактирование и удаление компонентов редактора
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="col-sm-offset-1 col-md-offset-1 col-sm-4 col-md-3 fullscreen-child" id="bp-editor-components-holder">
    <div class="panel-group" role="tablist" id="bp-editor-components"></div>
</div>
<div class="col-sm-7 col-md-8 fullscreen-child component-form" style="overflow: auto">
    <div class="row">
        <div class="col-md-12">
            <h3>
                Редактор компонентов
                <a href="#" id="bp-editor-components-add"><i class="glyphicon glyphicon-plus"></i></a>
            </h3>
        </div>
    </div>
    <div class="row">
        <div class="col-md-11 col-sm-12">
            <form class="form-horizontal" id="bp-editor-components-form">
                <div class="form-group">
                    <label for="bp-component-editor-name" class="col-sm-2 control-label">Название</label>
                    <div class="col-sm-10">
                        <input type="text" name="name" class="form-control" id="bp-component-editor-name" placeholder="Название" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-answer-type" class="col-sm-2 control-label">Очередь</label>
                    <div class="col-sm-10">
                        <select name="answerType" id="bp-component-editor-answer-type" required>
                            <option value="wam">Ждать ответного сообщения</option>
                            <option value="faf">Не ожидать ответа</option>
                            <option value="iw">Ждать ответа сразу (блокирующий вызов)</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-queue" class="col-sm-2 control-label">Очередь</label>
                    <div class="col-sm-10">
                        <input type="text" name="queueToSend" class="form-control" id="bp-component-editor-queue" placeholder="Очередь" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-description" class="col-sm-2 control-label">Описание</label>
                    <div class="col-sm-10">
                        <textarea class="form-control" name="description" id="bp-component-editor-description" placeholder="Описание"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-icon" class="col-sm-2 control-label">Иконка</label>
                    <div class="col-sm-10">
                        <input type="text" name="icon" class="form-control" id="bp-component-editor-icon" placeholder="Иконка" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-svg" class="col-sm-2 control-label">SVG</label>
                    <div class="col-sm-10">
                        <textarea name="view" class="form-control" id="bp-component-editor-svg" placeholder="SVG" required></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-groups" class="col-sm-2 control-label">Группы</label>
                    <div class="col-sm-10">
                        <input type="text" name="groups" class="form-control" id="bp-component-editor-groups" placeholder="Группы">
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-roles" class="col-sm-2 control-label">Роли</label>
                    <div class="col-sm-10">
                        <input type="text" name="roles" class="form-control" id="bp-component-editor-roles" placeholder="Роли">
                    </div>
                </div>
                <div class="form-group">
                    <label for="bp-component-editor-properties" class="col-sm-2 control-label">Свойства</label>
                    <div class="col-sm-10">
                        <textarea class="form-control" name="properties" id="bp-component-editor-properties" placeholder="Свойства"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default" id="bp-component-editor-create">Создать</button>
                        <button type="submit" class="btn btn-default" style="display: none;" id="bp-component-editor-update">Обновить</button>
                        <button type="submit" class="btn btn-danger" style="display: none;" id="bp-component-editor-delete">Удалить</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    var stencils = ${stencils} ||[];
    require([
        "bpeditor/ComponentsMenu",
        "jquery",
        "underscore",
        "codemirror/codemirror",
        "codemirror/show-hint",
        "codemirror/javascript",
        "codemirror/javascript-hint",
        "codemirror/xml",
        "css!/css/codemirror.css"
    ], function (ComponentsMenu, $, _, CodeMirror) {
        var url = "/admin/bpeditor/stencil";
        var propsCodeMirror;
        var viewCodeMirror;

        var collectFormData = function() {
            var $form = $("#bp-editor-components-form");
            return {
                title: $form.find("[name=name]").val(),
                description: $form.find("[name=description]").val(),
                icon: $form.find("[name=icon]").val(),
                view: viewCodeMirror.getValue(),
                groups: $form.find("[name=groups]").val(),
                roles: $form.find("[name=roles]").val(),
                properties: propsCodeMirror.getValue(),
                queueToSend: $form.find("[name=queueToSend]").val(),
                answerType: $form.find("[name=answerType]").val()
            }
        };

        function fillFormByStencil(stencil) {
            var $form = $("#bp-editor-components-form");
            $form.find("[name=name]").val(stencil.title || "");
            $form.find("[name=description]").val(stencil.description || "");
            $form.find("[name=icon]").val(stencil.icon || "");
            $form.find("[name=groups]").val(stencil.groups || "");
            $form.find("[name=roles]").val(stencil.roles || "");
            $form.find("[name=queueToSend]").val(stencil.queueToSend || "");
            $form.find("[name=answerType]").val(stencil.answerType || "wam");
            propsCodeMirror.setValue(stencil.properties || "");
            viewCodeMirror.setValue(stencil.view || "");
        }

        var setMode = function(mode) {
            return function () {
                $("#bp-component-editor-create")[mode === 0 ? "show" : "hide"]();
                $("#bp-component-editor-update")[mode === 1 ? "show" : "hide"]();
                $("#bp-component-editor-delete")[mode === 1 ? "show" : "hide"]();
            }
        };

        var clearForm = function () {
            fillFormByStencil({
                answerType: "wam",
                roles: "Activity, sequence_start, sequence_end, ActivitiesMorph, all"
            });
        };

        var setCreateMode = setMode(0);
        var setEditMode = setMode(1);

        $(function () {
            var selectedStencil;
            var menu = new ComponentsMenu({el: $("#bp-editor-components")}).setStencils(stencils).render();
            menu.on("stencil.clicked", function (stencil) {
                selectedStencil = stencil;
                fillFormByStencil(stencil);
                setEditMode();
            });
            var $form = $("#bp-editor-components-form");
            propsCodeMirror = CodeMirror.fromTextArea($form.find("[name=properties]")[0], {
                lineNumbers: true,
                mode: "application/json"
            });
            viewCodeMirror = CodeMirror.fromTextArea($form.find("[name=view]")[0], {
                lineNumbers: true,
                mode: "application/xml"
            });

            $form.validate({
                errorPlacement: function (error, element) {},
                highlight: function (element) {
                    $(element).parents(".form-group").addClass("has-error");
                },
                unhighlight: function (element) {
                    $(element).parents(".form-group").removeClass("has-error");
                }
            });

            $("#bp-component-editor-create").on("click", function (e) {
                e.preventDefault();
                if($form.valid()) {
                    var data = collectFormData();
                    $.ajax({
                        url: url,
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=UTF-8",
                        data: JSON.stringify(data),
                        context: this,
                        success: function (data) {
                            var stencils = _.clone(menu.stencils);
                            stencils.push(data);
                            menu.setStencils(stencils).render();
                            fillFormByStencil(data);
                            setEditMode();
                        },
                        error: function (f) {
                            alert("error");
                        }
                    });
                }
            });
            $("#bp-component-editor-update").on("click", function (e) {
                e.preventDefault();
                if($form.valid()) {
                    var data = collectFormData();
                    data.id = selectedStencil.id;
                    $.ajax({
                        url: url,
                        type: "PUT",
                        dataType: "json",
                        contentType: "application/json; charset=UTF-8",
                        data: JSON.stringify(data),
                        context: this,
                        success: function (stencilData) {
                            var stencil = _.find(menu.stencils, function (stencil) {
                                return stencil.id === stencilData.id;
                            });
                            if(stencil) {
                                _.extend(stencil, stencilData);
                            }
                            menu.setStencils(menu.stencils).render();
                            fillFormByStencil(data);
                            setEditMode();
                        },
                        error: function (f) {
                            alert("error");
                        }
                    });
                }
            });
            $("#bp-component-editor-delete").on("click", function (e) {
                e.preventDefault();
                var id = selectedStencil.id;
                $.ajax({
                    url: url + "/delete",
                    type: "POST",
                    contentType: "application/json; charset=UTF-8",
                    data: JSON.stringify({id: id}),
                    context: this,
                    success: function () {
                        var stencils = _.filter(menu.stencils, function (stencil) {
                            return stencil.id !== id;
                        });
                        menu.setStencils(stencils).render();
                        clearForm();
                        setCreateMode();
                        selectedStencil = null;
                    },
                    error: function (f) {
                        alert("error");
                    }
                });
            });

            $("#bp-editor-components-add").on("click", function (e) {
                e.preventDefault();
                clearForm();
                setCreateMode();
            });

            clearForm();

        });
    });
</script>
<link rel="stylesheet" type="text/css" href="/css/bpeditor/componetns-editod.css?v=${buildNumber}"/>
