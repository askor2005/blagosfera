<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<h1>
    Управление шаблонами нотификаций
</h1>

<button class="btn btn-primary create-button">
    Создать
</button>

<br/>
<br/>

<table class="table templates-table">

    <thead>
        <tr>
            <th>Мнемо-код</th>
            <th>Тема</th>
            <th></th>
        </tr>
    </thead>

    <tbody>
        <c:forEach items="${templates}" var="t">
            <tr>
                <td>${t.mnemo}</td>
                <td>${t.subject}</td>
                <td>
                    <a href="#" class="edit-link" data-template-id="${t.id}">
                        <i class="glyphicon glyphicon-pencil"></i>
                    </a>
                    <a href="#" class="delete-link" data-template-id="${t.id}">
                        <i class="glyphicon glyphicon-remove"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </tbody>

</table>

<div class="modal fade" id="notification-template-edit-modal" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <input type="hidden" name="id" />
                <div class="form-group">
                    <label>Мнемо-код</label>
                    <input type="text" placeholder="Мнемо-код" class="form-control" name="mnemo"/>
                </div>
                <div class="form-group">
                    <label>Тема</label>
                    <input type="text" placeholder="Тема" class="form-control" name="subject"/>
                </div>
                <div class="form-group">
                    <label>Тема</label>
                    <textarea class="form-control" name="shortText"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                <button type="button" class="btn btn-primary apply-button">Сохранить</button>
            </div>
        </div>
    </div>
</div>

<script id="notification-templates-list-item-template" type="x-tmpl-mustache">
    <tr>
        <td>{{mnemo}}</td>
        <td>{{subject}}</td>
        <td>{{shortText}}</td>
        <td>
            <a href="#" class="edit-link" data-template-id="{{id}}">
                <i class="glyphicon glyphicon-pencil"></i>
            </a>
            <a href="#" class="delete-link" data-template-id="{{id}}">
                <i class="glyphicon glyphicon-remove"></i>
            </a>
        </td>
    </tr>
</script>

<script type="text/javascript">

    $(document).ready(function () {

        $("table.templates-table").on("click", ".edit-link", function () {
            var $tr = $(this).closest("tr");
            var id = $(this).attr("data-template-id");
            $.radomJsonGet("/admin/notifications/get.json", {
                template_id: id
            }, function (template) {
                $("#notification-template-edit-modal h4").html("Редактирование шаблона уведомления");
                $("#notification-template-edit-modal input[name='id']").val(template.id);
                $("#notification-template-edit-modal input[name='mnemo']").val(template.mnemo);
                $("#notification-template-edit-modal input[name='subject']").val(template.subject);
                $("#notification-template-edit-modal textarea[name='shortText']").val(template.shortText);
                $("#notification-template-edit-modal").modal("show");
            });
            return false;
        });

        $("table.templates-table").on("click", ".delete-link", function () {

            var $tr = $(this).closest("tr");
            var id = $(this).attr("data-template-id");
            $.radomJsonPost("/admin/notifications/delete.json", {
                template_id: id
            }, function () {
                $tr.fadeOut(function () {
                    $tr.remove();
                });
            });
            return false;
        });

        $(".create-button").click(function () {
            $("#notification-template-edit-modal h4").html("Создание шаблона уведомления");
            $("#notification-template-edit-modal input[name='id']").val("");
            $("#notification-template-edit-modal input[name='mnemo']").val("");
            $("#notification-template-edit-modal input[name='subject']").val("");
            $("#notification-template-edit-modal textarea[name='shortText']").val("");
            $("#notification-template-edit-modal").modal("show");
            return false;
        });

        $("#notification-template-edit-modal button.apply-button").click(function () {
            $.radomJsonPost("/admin/notifications/save.json", $("#notification-template-edit-modal :input").serialize(), function () {
                $.radomJsonGet("/admin/notifications/list.json", {}, function (templates) {
                    $("table.templates-table tbody").empty();
                    $.each(templates, function (index, template) {
                        var trTemplate = $('#notification-templates-list-item-template').html();
                        Mustache.parse(trTemplate);
                        $("table.templates-table tbody").append(Mustache.render(trTemplate, template));
                    });
                });
                $("#notification-template-edit-modal").modal("hide");
            })
            return false;
        });

    });

</script>