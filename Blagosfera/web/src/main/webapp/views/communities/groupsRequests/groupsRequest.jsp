<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
    .showDocument {
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>
<script>
    function loadDocumentPageData(documentHashCode, callback) {
        $.radomJsonPost("/document/service/document_data.json",
                {
                    document_hash_code : documentHashCode
                },
                callback
        );
    }

    function loadRequestDocuments(requestId, callBack) {
        $.radomJsonPost("/groups/documentrequests/request_grid_data.json", {
            id : requestId
        }, callBack);
    }

    // Подписать документ
    function signDocument(documentId, callback, errorCallback) {
        $.radomFingerJsonAjax({
            url : '/document/service/signDocument.json',
            type : "post",
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data : {
                document_id : documentId
            },
            successRequestMessage : "",
            errorMessage : "Ошибка подписи документа",
            closeModalOnSuccess: true,
            successCallback : callback,
            errorCallback : errorCallback,
            systemOption : 'document.sign.protected'
        });
    }

    // Отказаться от подписи документа
    function unSignDocument(documentId, callback, errorCallback) {
        $.radomFingerJsonAjax({
            url : '/document/service/unSignDocument.json',
            type : "post",
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data : {
                document_id : documentId
            },
            successRequestMessage : "",
            errorMessage : "Ошибка подписи документа",
            closeModalOnSuccess: true,
            successCallback : callback,
            errorCallback : errorCallback,
            systemOption : 'document.sign.protected'
        });
    }

    function initDocumentTemplate(documentHashCode, callBack) {
        loadDocumentPageData(documentHashCode, function(response){
            var model = response;

            $("#documentTextModal .modal-body").empty();

            var documentTemplate = $("#documentTemplate").html();
            Mustache.parse(documentTemplate);

            $("#documentTextModal .modal-body").append(
                    $(Mustache.render(documentTemplate, model))
            );
            callBack();
        });
    }

    function initDocumentsTable() {
        var documentsTemplate = $("#documentsTemplate").html();
        Mustache.parse(documentsTemplate);
        $("#groupsRequestBlock").empty();
        loadRequestDocuments(getLastPartOfUrl(), function(documents){
            $("#groupsRequestBlock").append(
                    $(Mustache.render(documentsTemplate, {
                        documents : documents
                    }))
            );
            $(".showDocument").radomTooltip({
                container : "body",
                delay : {
                    show: 200,
                    hide: 0
                }
            });
        });
    }

    $(document).ready(function(){
        initDocumentsTable();

        $("body").on('click', ".showDocument", function (event) {
            var documentId = $(this).attr("document_id");
            var documentHashCode = $(this).attr("document_hash");

            initDocumentTemplate(documentHashCode, function(){
                $("#linkToDocument").attr("href", "/document/service/documentPage?document_id=" + documentHashCode);
                $("#documentTextModal").modal("show");
            });
            return false;
        });

        // Кнопка обработки подписи документа
        $("body").on('click', ".signDocument", function (event) {
            var documentId = $(this).attr("document_id");
            signDocument(documentId, function () {
                initDocumentTemplate(documentId);
                initDocumentsTable();
            },function (error) {
                //initDocumentPage();
                //bootbox.alert(error);
            });
        });

        // Отказ подписывать документ
        $("body").on('click', ".unSignDocument", function (event) {
            var documentId = $(this).attr("document_id");
            unSignDocument(documentId, function () {
                initDocumentTemplate(documentId);
                initDocumentsTable();
            },function (error) {
                //initDocumentPage();
                //bootbox.alert(error);
            });
        });
    });
</script>
<script id="documentsTemplate" type="x-tmpl-mustache">
    <table class='table table-hover table-striped'>
        <thead>
            <tr>
                <th>#</th>
                <th>Наименование документа</th>
                <th></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            {{#documents}}
                <tr>
                    <td>{{id}}</td>
                    <td><a class='btn btn-primary btn-xs showDocument' document_id='{{id}}' document_hash='{{hashCode}}' href='#' title='Просмотреть документ {{name}}'>{{name}}</a></td>
                    <td>
                        {{^documentActive}}
                            <span style='font-weight: bold; color: red;'>Документ не действителен</span>
                        {{/documentActive}}
                        {{#documentActive}}
                            {{^signed}}
                                <button type="button" class="btn btn-primary btn-xs signDocument" document_id="{{id}}">Подписать документ</button>
                            {{/signed}}
                            {{#signed}}
                                <span style='font-weight: bold;'>Данный документ вами подписан</span>
                            {{/signed}}
                        {{/documentActive}}
                    </td>
                    <td>
                        {{^documentActive}}
                            <span style='font-weight: bold; color: red;'>Документ не действителен</span>
                        {{/documentActive}}
                        {{#documentActive}}
                            {{^signed}}
                                {{#canUnsignDocument}}
                                    <button type="button" class="btn btn-danger btn-xs unSignDocument" document_id="{{id}}">Отказаться от подписи</button>
                                {{/canUnsignDocument}}
                            {{/signed}}
                        {{/documentActive}}
                    </td>
                </tr>
            {{/documents}}
        </tbody>
    </table>
</script>

<script id="documentTemplate"  type="x-tmpl-mustache">
    <div id="documentTextModalContent" style="height: 45vh; overflow: auto;">
        <iframe style='border: 0px; width: 100%; height: 100%;' src='/document/service/documentPrintPage?document_id={{documentId}}&print=false'></iframe>
    </div>
    <hr/>
    <div id="documentButtons">
        {{^documentActive}}
            <span style='font-weight: bold; color: red;'>Документ не действителен</span>
        {{/documentActive}}
        {{#documentActive}}
            {{^signed}}
                <button type="button" class="btn btn-primary signDocument" document_id="{{documentId}}" style="margin: 5px; display: inline-block;">Подписать документ</button>
                {{#canUnsignDocument}}
                    <button type="button" class="btn btn-danger unSignDocument" document_id="{{documentId}}" style="margin: 5px; display: inline-block;">Отказаться от подписи</button>
                {{/canUnsignDocument}}
            {{/signed}}
            {{#signed}}
                <span style='font-weight: bold;'>Данный документ вами подписан</span>
            {{/signed}}
        {{/documentActive}}
    </div>
</script>

<div>
    <h1>Пакет документов для вступления в объединение </h1>
</div>
<hr/>
<div id="groupsRequestBlock"></div>
<hr/>

<div class="modal fade" role="dialog" id="documentTextModal" aria-labelledby="documentTextModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="documentTextModalLabel"></h4>
            </div>
            <div class="modal-body"></div>
            <div class="modal-footer">
                <a href="" id="linkToDocument" target="_blank" class="btn btn-primary">Перейти к документу</a>
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>