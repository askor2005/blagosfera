<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<jsp:include page="documentPageFieldList.jsp"/>
<style>
    #participantsDiv, #otherParticipantsDiv {margin: 5px 0px 5px 0px;}

    .form-control-value {
        display: none;
    }

    .currencyControl {
        position: absolute; left: 0px; right: 200px; top: 0px;
    }
    .currencyTypeControl {
        position: absolute; width: 200px; top: 0px; right: 0px;
    }

    .div-control {
        position: relative;
        /*height: 34px;*/
    }

    /* Скрываем незаполненные системные поля */
    span[data-span-type="radom-system-fields"] {
        display: none;
    }

    pre {
        font-family: helvetica,arial,verdana,sans-serif !important;
        word-break: normal !important;
        white-space: pre-line !important;
    }
</style>
<link rel="stylesheet" type="text/css" href="/document/service/fieldsStyles">
<script type="text/javascript">

    var documentId = null;
    var documentHashCode = "${documentHashCode}";
    var userModel = null;
    var documentPageTemplate = null;

        // Сохранить контент документа
    function saveContent(fieldsList, callback) {
        $.ajax({
            async: true,
            type: "POST",
            url: "/document/service/saveUserFieldsInDocument.json",
            datatype: "json",
            data: {document_id : documentId, fields_list : JSON.stringify(fieldsList)},
            success: function (param) {
                if (!param.operationResult) {
                    bootbox.alert(param.operationMessage);
                } else {
                    callback(param.data);
                }
            },
            error: function (param) {
            }
        });
    }

    // Подписать документ
    function signDocument(callback, errorCallback) {
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
    function unSignDocument(callback, errorCallback) {
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

    // Проперить подпись
    function checkSignature(participantId, callback){
        $.radomJsonPostWithWaiter("/document/service/checkSignature.json", {
            participantId : participantId
        }, callback);
    }

    // Загрузить пользовательские поля
    /*function getUserFields(callback){
        $.radomJsonPost("/document/service/getUserFields.json",
                {
                    document_id : documentId
                },
                callback
        );
    }*/

    function loadDocumentPageData(documentHashCode, callback) {
        $.radomJsonPost("/document/service/document_data.json",
                {
                    document_hash_code : documentHashCode
                },
                callback
        );
    }

    $(document).ready(function() {
        documentPageTemplate = $("#documentPageTemplate").html();
        Mustache.parse(documentPageTemplate);

        userModel = null;
        $(eventManager).bind("inited", function(event, currentUser) {
            var officialAppeal = currentUser.sex ? "Уважаемый" : "Уважаемая";
            userModel = currentUser;
            userModel.officialAppeal = officialAppeal;
            initDocumentPage();
        });
    });

    function initDocumentPage() {
        loadDocumentPageData(documentHashCode, function(response){
            documentId = response.documentId;
            var model = response;
            model.user = userModel;

            model.hasUserFields = response.allUserFields != null && response.allUserFields.length > 0;
            model.hasSignParticipants = response.signParticipants != null && response.signParticipants.length > 0;

            model.hasCreator = response.creator != null;
            if (model.hasCreator) {
                model.creator.avatar_c84 = Images.getResizeUrl(response.creator.avatar, "c84");
            }
            if (response.documentCreateDate != null) {
                model.documentCreateDate = dateFormat(new Date(response.documentCreateDate), "dd.mm.yyyy");
            } else {
                model.documentCreateDate = "";
            }

            if (model.signParticipants != null) {
                var index = 1;
                for (var i in model.signParticipants) {
                    var participant = model.signParticipants[i];
                    participant.participantList = participant.type == 'INDIVIDUAL_LIST';
                    participant.index = index++;
                }
            }


            var markup = Mustache.render(documentPageTemplate, model);
            $("#documentPageBlock").html("");
            $("#documentPageBlock").append(markup);
            initHandlers();
            if (model.hasUserFields) {
                initUserFields(response.allUserFields, $("#userFieldsContainer"));
            }
        });
    }

    function initHandlers() {
        // Кнопка обработки подписи документа
        $("#signDocument").click(function () {
            signDocument(function () {
                initDocumentPage();
                bootbox.alert("Документ успешно подписан!");
            },function (error) {
                initDocumentPage();
                bootbox.alert(error);
            });
        });

        // Отказ подписывать документ
        $("#unSignDocument").click(function () {
            unSignDocument(function () {
                initDocumentPage();
                bootbox.alert("Данный документ теперь не действителен!");
            },function (error) {
                initDocumentPage();
                bootbox.alert(error);
            });
        });


        <%--<c:if test="${userFields != null && fn:length(userFields) > 0}" >
        getUserFields(function(userFields){
            initUserFields(userFields);
        });
        </c:if>--%>

        // Проверка подписи участника
        $(".checkSignature").click(function(){
            var participantId = $(this).attr("participant_id");
            checkSignature(participantId,
                    function(){
                        bootbox.alert("Проверка ЭЦП выполнена успешно");
                    }
            )
        });

        $("#printDocumentLink").click(function(){
            window.frames["printDocument"].focus();
            window.frames["printDocument"].print();
        });
    }
</script>
<script id="documentPageTemplate" type="x-tmpl-mustache">
    {{#userHasDocumentRight}}
        <div>
            <hr/>
            <div style="float: right;">
                <a href="javascript:void(0);" style="margin-right: 10px;" id="printDocumentLink"><i class="fa fa-print"></i> Печать</a>
                <a href="/document/service/exportDocumentToPdf?document_id={{documentId}}" target="_blank"><i class="fa fa-file-pdf-o"></i> PDF</a>
            </div>
            <div style="clear: both;"></div>
            <h4><b>Документ:</b> {{documentName}}</h4>
            <hr/>
                {{^documentActive}}
                    <h4 style="color: red;">Документ не действителен</h4>
                    <hr/>
                {{/documentActive}}
            <div>
            {{#hasUserFields}}

            {{/hasUserFields}}
            {{^hasUserFields}}
                {{#documentActive}}
                    {{^signed}}

                        {{#hasSignParticipants}}
                            <div>{{user.officialAppeal}} {{user.officialName}}, просим Вас прочитать и подписать документ в качестве следующих участников документа:</div>
                            <div style="margin-top: 5px;">
                                {{#signParticipants}}
                                    {{#parentName}}
                                        {{#participantList}}
                                            {{index}}. Как участник группы «{{name}} - {{parentSourceName}}»<br/>
                                        {{/participantList}}
                                        {{^participantList}}
                                            {{index}}. Как {{name}} - {{parentSourceName}}<br/>
                                        {{/participantList}}
                                    {{/parentName}}
                                    {{^parentName}}
                                        {{#participantList}}
                                            {{index}}. Как участник группы «{{name}}»<br/>
                                        {{/participantList}}
                                        {{^participantList}}
                                            {{index}}. Как {{name}}<br/>
                                        {{/participantList}}
                                    {{/parentName}}
                                {{/signParticipants}}
                            </div>
                            <button type="button" class="btn btn-primary" id="signDocument" style="margin: 5px; display: inline-block;">Подписать документ</button>
                            {{#canUnsignDocument}}
                                <button type="button" class="btn btn-danger" id="unSignDocument" style="margin: 5px; display: inline-block;">Отказаться от подписи</button>
                            {{/canUnsignDocument}}
                            <hr/>
                        {{/hasSignParticipants}}

                    {{/signed}}
                    {{#signed}}
                        <h4>Данный документ вами подписан</h4>
                    {{/signed}}
                {{/documentActive}}
            {{/hasUserFields}}

            {{#hasCreator}}
                <p>Создатель документа: {{creator.name}}</p>
                {{#creator.user}}
                    <a href="/sharer/{{creator.ikp}}" style="display: inline-block;">
                        <img data-src="holder.js/84x/84" alt="84x84"
                        src="{{creator.avatar_c84}}" data-holder-rendered="true"
                        class="media-object img-thumbnail tooltiped-avatar"
                        data-sharer-ikp="{{creator.ikp}}" data-placement="left" />
                        </a>
                {{/creator.user}}
                {{^creator.user}}
                    <img data-src="holder.js/84x/84" alt="84x84"
                        src="{{creator.avatar_c84}}" data-holder-rendered="true"
                        class="media-object img-thumbnail tooltiped-avatar"
                        data-placement="left" />
                {{/creator.user}}
            {{/hasCreator}}

            <p>Дата формирования документа: {{documentCreateDate}}</p>
            <p>Код документа: {{documentCode}}</p>
            <div>Текст документа:</div>
            <div style="width: 100%; height: 400px; overflow: auto;">
                <iframe
                    id="printDocument"
                    name="printDocument"
                    style="width: 100%; height: 100%; border: 1px solid #ccc;"
                    src="/document/service/documentPrintPage?document_id={{documentId}}&print=false"></iframe>
            </div>
            <hr/>

            <a href="javascript:void(0);" onclick="$('#participantsDiv').toggle();">Подписанты документа</a>
            <div id="participantsDiv" style="display: none;">
                <table class="standardTable">
                    <tr>
                        <th>Код источника данных</th>
                        <th>Имя</th>
                        <th>Подписан</th>
                        <th>Подпись</th>
                        <th>Проверка</th>
                    </tr>
                    {{#participants}}
                        {{#needSignDocument}}
                            {{#parentName}}
                                <tr>
                                    <td>{{parentName}}</td>
                                    <td>{{parentSourceName}}</td>
                                    <td>{{name}} {{sourceName}}</td>
                                    <td>{{signDate}}</td>
                                    <td>
                                        <button class="btn btn-primary checkSignature" participant_id="{{id}}" >Проверить подпись</button>
                                    </td>
                                </tr>
                            {{/parentName}}

                            {{^parentName}}
                                <tr>
                                    <td>{{name}}</td>
                                    <td>{{sourceName}}</td>
                                    <td>{{sourceName}}</td>
                                    <td>{{signDate}}</td>
                                    <td>
                                        <button class="btn btn-primary checkSignature" participant_id="{{id}}" >Проверить подпись</button>
                                    </td>
                                </tr>
                            {{/parentName}}

                        {{/needSignDocument}}
                    {{/participants}}
                </table>
            </div>
            <hr/>

            <a href="javascript:void(0);" onclick="$('#otherParticipantsDiv').toggle();">Остальные участники документа</a>
            <div id="otherParticipantsDiv" style="display: none;">
                <table class="standardTable">
                    <tr>
                        <th>Код источника данных</th>
                        <th>Имя</th>
                        <th>Участник документа</th>
                    </tr>
                    {{#participants}}
                        {{^needSignDocument}}
                            {{#parentName}}
                                <tr>
                                    <td>{{parentName}}</td>
                                    <td>{{parentSourceName}}</td>
                                    <td>{{name}} {{sourceName}}</td>
                                </tr>
                            {{/parentName}}
                            {{^parentName}}
                                <tr>
                                    <td>{{name}}</td>
                                    <td>{{sourceName}}</td>
                                    <td>{{sourceName}}</td>
                                </tr>
                            {{/parentName}}
                        {{/needSignDocument}}
                    {{/participants}}
                </table>
            </div>
        </div>

        {{#hasUserFields}}
            <h4>Заполнить пользовательские поля документа:</h4>
            <div style="margin-top: 5px;">
                <div id="userFieldsContainer"></div>
            </div>
            <button type="button" class="btn btn-primary" id="saveUserFields" style="margin-top: 5px;">Сохранить значения полей в документ</button>
            <hr/>
        {{/hasUserFields}}
    {{/userHasDocumentRight}}
    {{^userHasDocumentRight}}
        <h4>У Вас нет прав доступа к документу.</h4>
    {{/userHasDocumentRight}}
</script>
<div id="documentPageBlock"></div>