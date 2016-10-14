<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="bp-editor-modal" class="modal" tabindex="-1" role="dialog" aria-labelledby="bp-editor-modal-label" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h3 id="bp-editor-modal-label">Создание элемента</h3>
            </div>
            <div class="modal-body">
                <form class="form-vertical" id="bp-editor-modal-form">
                    <div class="form-group">
                        <label for="bp-editor-modal-name">Название:</label>
                        <input type="text" class="form-control" placeholder="Название" name="itemName" id="bp-editor-modal-name">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" aria-hidden="true">Отмена</button>
                <button class="btn btn-primary" id="bp-editor-modal-create">Создать</button>
            </div>
        </div>
    </div>
</div>
