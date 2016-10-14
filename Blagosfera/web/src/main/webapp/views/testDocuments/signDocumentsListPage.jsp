<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    #documentsDiv table {
        width: 100%;
        border: 1px solid #000;
        border-collapse: collapse;
    }

    #documentsDiv th {
        text-align: left;
        background: #ccc;
        padding: 5px;
        border: 1px solid black;
    }

    #documentsDiv td {
        padding: 5px;
        border: 1px solid black;
    }
</style>
<script type="text/javascript">

    var documentSignPageTemplate = null;

    // Подписать список документов
    function signDocuments(documentIds, callback) {
        $.radomFingerJsonAjax({
            url : '/document/service/signDocuments.json',
            type : "post",
            contentType: 'application/json',
            data : JSON.stringify(documentIds),
            successRequestMessage : "",
            errorMessage : "Ошибка подписи документа",
            closeModalOnSuccess: true,
            successCallback : callback,
            systemOption : 'document.sign.protected'
        });
    }

    function loadDocumentSignPageData(callBack) {
        $.radomJsonPost("/document/service/document_sign_page_data.json",
                {},
                callBack
        );
    };

    $(document).ready(function () {
        documentSignPageTemplate = $("#documentSignPageTemplate").html();
        Mustache.parse(documentSignPageTemplate);
        initSignDocumentsPage();
    });

    function initSignDocumentsPage() {
        loadDocumentSignPageData(function (response) {
            var model = {documents : response};
            model.hasDocuments = model.documents != null && model.documents.length > 0;

            var markup = Mustache.render(documentSignPageTemplate, model);
            $("#documentSignPageBlock").html("");
            $("#documentSignPageBlock").append(markup);
            initHandlers();
        });
    }

    function initHandlers() {

        $(".signCheckboxCommon").click(function () {
            var checked = $(this).prop("checked");
            $(".signCheckbox").prop("checked", checked);
        });
        $(".signCheckbox").click(function () {
            var allChecked = true;
            $(".signCheckbox").each(function () {
                if (!$(this).prop("checked")) {
                    allChecked = false;
                }
            });
            $(".signCheckboxCommon").prop("checked", allChecked);
        });
        $("#signDocuments").click(function () {
            var checkedDocuments = [];
            $(".signCheckbox").each(function () {
                if ($(this).prop("checked")) {
                    var documentId = $(this).attr("id");
                    checkedDocuments.push(documentId);
                }
            });
            if (checkedDocuments.length > 0) {
                signDocuments(checkedDocuments, function () {
                    initSignDocumentsPage();
                });
            } else {
                bootbox.alert("Выберите документы на подписание!");
            }
        });
    }
</script>
<script id="documentSignPageTemplate" type="x-tmpl-mustache">
    <div>
        {{#hasDocuments}}
            <h4>Для Вас есть документы на подпись</h4>
            <hr/>
            <div>Отметьте галочками документы, которые Вы хотите подписать и нажмите кнопку "Подписать" и следуйте
                инструкциям.
            </div>
            <div id="documentsDiv" style="margin-top: 3px;">
                <table>
                    <tr>
                        <th>№</th>
                        <th>Документ</th>
                        <th>Дата формирования</th>
                        <th>
                            <label>
                                <input class="signCheckboxCommon" type="checkbox"/>
                                Подписать
                            </label>
                        </th>
                    </tr>
                    {{#documents}}
                        <tr>
                            <td>{{id}}</td>
                            <td><a href="/document/service/documentPage?document_id={{id}}">{{name}}</a></td>
                            <td>{{createDate}}</td>
                            <td>
                                <label>
                                    <input class="signCheckbox" type="checkbox" id="{{id}}"/>
                                    Да
                                </label>
                            </td>
                        </tr>
                    {{/documents}}
                </table>
                <a href="javascript:void(0)" class="btn btn-primary" id="signDocuments" style="float: right; margin-top: 3px;">Подписать</a>
            </div>
        {{/hasDocuments}}
        {{^hasDocuments}}
            <h4>Для Вас нет документов на подпись</h4>
        {{/hasDocuments}}
    </div>
</script>
<div id="documentSignPageBlock"></div>


