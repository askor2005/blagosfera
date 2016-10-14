<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<style>
    .documentFileName {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        width: 930px;
    }
</style>
<script type="application/javascript">
    $(document).ready(function(){
        // Регистрируем загрузчик данных документов для прикрепления к полям
        var callBack = registerFilesSource("Выбрать документ объединения", function(){
            $("#selectedDocumentFile").hide();
            $("#documentFileSourceModal").modal('show');
        });
        var selectedFile = {};
        $("#documentName").typeahead({
            updater: function(item) {
                selectedFile = item;
                $("#selectedDocumentFile").show();
                $("#selectedDocument").text(item.documentName);
                $("#selectedDocument").radomTooltip({
                    position : "top",
                    container : "body",
                    title : item.documentName
                });
                return item;
            },
            source:  function (query, process) {
                var data = {
                    search_string : query
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/communities/${communityId}/documents.json",
                    data: data,
                    success: function (data) {
                        var requestData = [];
                        for (var index in data) {
                            data[index].name = data[index].documentName;
                        }
                        return process(data);
                    },
                    error: function () {
                        console.log("ajax error");
                        return process(false);
                    }
                });
            }
        });
        $("#addFile").click(function(){
            $("#documentFileSourceModal").modal('hide');
            callBack(selectedFile.documentName, selectedFile.fileLink);
        });
    });
</script>
<div class="modal fade" id="documentFileSourceModal" tabindex="11" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 970px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title" >Прикрепить документ объединения к полю</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>Наименование документа</label>
                    <input type="text" class="form-control" id="documentName" placeholder="Наименование документа" />
                </div>
                <div class="form-group" id="selectedDocumentFile" style="display: none;">
                    <label>Выбранный документ</label>
                    <div class="documentFileName" id="selectedDocument"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="addFile">Добавить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>