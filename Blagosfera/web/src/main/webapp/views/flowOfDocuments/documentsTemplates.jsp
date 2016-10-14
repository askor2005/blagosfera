<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@include file="documentsTemplatesGrid.jsp" %>
<%@include file="documentsTemplatesTypesGrid.jsp" %>

<style type="text/css">
    .modal .modal-body {
        max-height: 500px;
        overflow-y: auto;
    }
</style>

<script type="text/javascript">
    $(document).ready(function () {
        $("#add-document-template-button").click(function () {
            $("#editDocumentTemplateWindow").modal({backdrop: false, keyboard: false});
            return false;
        });

        $("#editDocumentTemplateWindow").on("shown.bs.modal", function () {
            Ext.getCmp("documentTypesGrid").update();
            storeDocumentTypes.load();
        });

        $("#goto-create-document-template").click(function () {
            var grid = Ext.getCmp("documentTypesGrid");

            if (grid.selection == null || grid.selection == undefined || grid.selection == "") {
                bootbox.alert("Укажите класс документов!");
                return false;
            }

            //$("#field-documentType").val(grid.selection.data.id);

            var documentTemplate = {
                id: -1,
                documentClassId: grid.selection.data.id
            };

            $.radomJsonPostWithWaiter(
                    "/admin/flowOfDocuments/documentTemplate/save.json",
                    JSON.stringify(documentTemplate),
                    function(response){
                        var url = "/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=" + response.id;
                        window.location.href = url;
                    },
                    null,
                    {
                        contentType: "application/json"
                    }
            );
        });
    });
</script>

<h1>Шаблоны документов</h1>
<hr/>
<a href="#" class="btn btn-primary" id="add-document-template-button">Добавить новый шаблон документа</a>
<hr/>
<div id="documentTypesForTemplates-grid"></div>
<div id="documentTypesForTemplatesGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;"></div>
<hr/>
<div id="documentTemplates-grid"></div>
<div id="documentTemplatesGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;"></div>

<hr/>

<!-- Modal -->
<div class="modal fade" role="dialog" id="editDocumentTemplateWindow" aria-labelledby="documentTemplateLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="documentTemplateLabel">Создание шаблона документа</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <div id="documentTypes-grid"></div>
                    <div id="documentTypesGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;"></div>
                </div>
            </div>
            <div class="modal-footer">
                <a href="#" id="goto-create-document-template" class="btn btn-primary" style="float: left;">Создать шаблон документа</a>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<f:form id="editDocumentTemplateForm" role="form" method="post" modelAttribute="documentTemplateForm">
    <f:input path="id" style="display: none" value="-1"/>
    <f:input path="documentType" id="field-documentType" style="display: none" value="-1"/>
</f:form>