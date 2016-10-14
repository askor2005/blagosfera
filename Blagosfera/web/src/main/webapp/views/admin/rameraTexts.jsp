<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<h1>
    Управление текстами
</h1>

<button class="btn btn-primary create-button">
    Создать обычный текст
</button>

<button class="btn btn-primary create-button-html">
    Создать текст с форматированием (HTML)
</button>

<br/>
<br/>

<table class="table texts-table">

    <thead>
    <tr>
        <th>Код</th>
        <th>Описание</th>
        <th></th>
    </tr>
    </thead>

    <tbody>
    <c:forEach items="${rameraTexts}" var="t">
        <tr>
            <td>${t.code}</td>
            <td>${t.description}</td>
            <td>
                <a href="#" class="edit-link" data-ramera-text-id="${t.id}">
                    <i class="glyphicon glyphicon-pencil"></i>
                </a>
                <a href="#" class="delete-link" data-ramera-text-id="${t.id}">
                    <i class="glyphicon glyphicon-remove"></i>
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>

</table>

<div class="modal fade" id="ramera-text-edit-modal" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog" style="width: 1000px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title">Редактирование текста</h4>
            </div>

            <div class="modal-body">
                <input type="hidden" name="id"/>
                <input type="hidden" name="html" id="html"/>

                <div class="form-group">
                    <label>Код</label>
                    <input type="text" placeholder="Код" class="form-control" name="code"/>
                </div>

                <div class="form-group">
                    <label>Описание</label>
                    <input type="text" placeholder="Описание" class="form-control" name="description"/>
                </div>

                <div class="form-group">
                    <label>Текст</label>
                    <br>
                    <textarea id="text-content"></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                <button type="button" class="btn btn-primary apply-button">Сохранить</button>
            </div>
        </div>
    </div>
</div>

<script id="ramera-text-list-item-template" type="x-tmpl-mustache">
    <tr>
        <td>{{code}}</td>
        <td>{{description}}</td>
        <td>
            <a href="#" class="edit-link" data-ramera-text-id="{{id}}">
                <i class="glyphicon glyphicon-pencil"></i>
            </a>
            <a href="#" class="delete-link" data-ramera-text-id="{{id}}">
                <i class="glyphicon glyphicon-remove"></i>
            </a>
        </td>
    </tr>
</script>

<script type="text/javascript">
    $(document).ready(function () {
        $("table.texts-table").on("click", ".edit-link", function () {
            var $tr = $(this).closest("tr");
            var id = $(this).attr("data-ramera-text-id");
            $.radomJsonGet("/admin/rameraTexts/get.json", {
                ramera_text_id: id
            }, function (rameraText) {
                $("#ramera-text-edit-modal h4").html("Редактирование текста");
                $("#ramera-text-edit-modal input[name='id']").val(rameraText.id);
                $("#ramera-text-edit-modal input[name='code']").val(rameraText.code);
                $("#ramera-text-edit-modal input[name='description']").val(rameraText.description);

                $textarea = $('<textarea id="text-content" style="width: 100%; height: 400px;" name="text"></textarea>');
                $("#text-content").replaceWith($textarea);

                $("#ramera-text-edit-modal textarea[name='text']").val(rameraText.text);

                if (rameraText.isHtml) {
                    $('#html').val('true');

                    $("#text-content").radomTinyMCE({
                        useRadomParticipantFilter: false,
                        useRadomParticipantCustomFields: false,
                        useRadomParticipantCustomText: false,
                        useRadomSystemFields: false,
                        useRadomCopyPasteFields: false,
                        userRadomGroupFields: false,
                        useTempData : false
                    });
                } else {
                    $('#html').val('false');
                }

                $("#ramera-text-edit-modal").modal("show");
            });
            return false;
        });

        $("table.texts-table").on("click", ".delete-link", function () {
            var $tr = $(this).closest("tr");
            var id = $(this).attr("data-ramera-text-id");
            $.radomJsonPost("/admin/rameraTexts/delete.json", {
                ramera_text_id: id
            }, function () {
                $tr.fadeOut(function () {
                    $tr.remove();
                });
            });
            return false;
        });

        $(".create-button").click(function () {
            $('#html').val('false');
            $("#ramera-text-edit-modal h4").html("Создание текста");
            $("#ramera-text-edit-modal input[name='id']").val("");
            $("#ramera-text-edit-modal input[name='code']").val("");
            $("#ramera-text-edit-modal input[name='description']").val("");

            $textarea = $('<textarea id="text-content" style="width: 100%; height: 400px;" name="text"></textarea>');
            $("#text-content").replaceWith($textarea);

            $("#ramera-text-edit-modal textarea[name='text']").val("");
            $("#ramera-text-edit-modal").modal("show");
            return false;
        });

        $(".create-button-html").click(function () {
            $('#html').val('true');
            $("#ramera-text-edit-modal h4").html("Создание текста");
            $("#ramera-text-edit-modal input[name='id']").val("");
            $("#ramera-text-edit-modal input[name='code']").val("");
            $("#ramera-text-edit-modal input[name='description']").val("");

            $textarea = $('<textarea id="text-content" style="width: 100%; height: 400px;" name="text"></textarea>');
            $("#text-content").replaceWith($textarea);
            $("#text-content").radomTinyMCE({
                useRadomParticipantFilter: false,
                useRadomParticipantCustomFields: false,
                useRadomParticipantCustomText: false,
                useRadomSystemFields: false,
                useRadomCopyPasteFields: false,
                userRadomGroupFields: false
            });

            $("#ramera-text-edit-modal textarea[name='text']").val("");
            $("#ramera-text-edit-modal").modal("show");
            return false;
        });

        $("#ramera-text-edit-modal button.apply-button").click(function () {
            $.radomJsonPost("/admin/rameraTexts/save.json", $("#ramera-text-edit-modal :input").serialize(), function () {
                $.radomJsonGet("/admin/rameraTexts/list.json", {}, function (rameraTexts) {
                    $("table.texts-table tbody").empty();
                    $.each(rameraTexts, function (index, rameraText) {
                        var trTemplate = $('#ramera-text-list-item-template').html();
                        Mustache.parse(trTemplate);
                        $("table.texts-table tbody").append(Mustache.render(trTemplate, rameraText));
                    });
                });
                $("#ramera-text-edit-modal").modal("hide");
                bootbox.alert("Текст успешно сохранен");
            });
            return false;
        });
    });
</script>