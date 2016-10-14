<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<c:choose>
    <c:when test="${userHasDocumentRight == true}">
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

            var documentId = ${document.id};

            // Сохранить контент документа
            function saveContent(fieldsList, callBack) {
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
                            callBack(param.data);
                        }
                    },
                    error: function (param) {
                    }
                });
            };

            // Подписать документ
            function signDocument(callBack) {
                $.radomJsonPostWithWaiter("/document/service/signDocument.json",
                        {
                            document_id : documentId
                        }, callBack
                );
            };

            // Отказаться от подписи документа
            function unSignDocument(callBack) {
                $.radomJsonPostWithWaiter("/document/service/unSignDocument.json",
                        {
                            document_id : documentId
                        }, callBack
                );
            };

            // Проперить подпись
            function checkSignature(participantId, callBack){
                $.radomJsonPostWithWaiter("/document/service/checkSignature.json",
                        {
                            participantId : participantId
                        }, callBack
                );
            };

            // Загрузить пользовательские поля
            function getUserFields(callBack){
                $.radomJsonPost("/document/service/getUserFields.json",
                        {
                            document_id : documentId
                        },
                        callBack
                );
            };

            $(document).ready(function() {
                // Кнопка обработки подписи документа
                $("#signDocument").click(function () {
                    signDocument(function () {
                        bootbox.alert("Документ успешно подписан!", function () {
                            document.location.reload();
                        });
                    });
                });

                // Отказ подписывать документ
                $("#unSignDocument").click(function () {
                    unSignDocument(function () {
                        bootbox.alert("Данный документ теперь не действителен!", function () {
                            document.location.reload();
                        });
                    });
                });


                <c:if test="${userFields != null && fn:length(userFields) > 0}" >
                    getUserFields(function(userFields){
                        initUserFields(userFields);
                    });
                </c:if>

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
            });
        </script>
        <div>
            <hr/>
            <div style="float: right;">
                <a href="javascript:void(0);" id="printDocumentLink">Печать</a>
                <a href="/document/service/exportDocumentToPdf?document_id=${document.id}" target="_blank">PDF</a>
            </div>
            <div style="clear: both;"></div>
            <h4><b>Документ:</b> ${document.name}</h4>
            <hr/>
            <c:if test="${not document.active}">
                <h4 style="color: red;">Документ не действителен</h4>
                <hr/>
            </c:if>
            <div>

                <c:choose>
                    <c:when test="${userFields != null && fn:length(userFields) > 0}" >
                    </c:when>
                    <c:when test="${signParticipants != null && fn:length(signParticipants) == 0}" >
                    </c:when>
                    <c:when test="${signParticipants != null && isSigned == false && document.active}" >
                        <div>${sharer.officialAppeal} ${sharer.officialName}, просим Вас прочитать и подписать документ в качестве следующих участников документа:</div>
                        <div style="margin-top: 5px;">
                            <c:forEach var="participant" items="${signParticipants}" varStatus="i">
                                <c:if test="${participant.isNeedSignDocument}">
                                    <c:choose>
                                        <c:when test="${participant.parent != null}" >
                                            <c:choose>
                                                <c:when test="${participant.participantTypeName == 'INDIVIDUAL_LIST'}" >
                                                    ${i.count}. Как участник ${radom:getPadeg(participant.participantTemplateTypeName, 2)} - ${radom:getPadeg(participant.parent.name, 2)}<br/>
                                                </c:when>
                                                <c:otherwise>
                                                    ${i.count}. Как ${participant.participantTemplateTypeName} - ${radom:getPadeg(participant.parent.name, 2)}<br/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${participant.participantTypeName == 'INDIVIDUAL_LIST'}" >
                                                    ${i.count}. Как участник ${radom:getPadeg(participant.participantTemplateTypeName, 2)}<br/>
                                                </c:when>
                                                <c:otherwise>
                                                    ${i.count}. Как ${participant.participantTemplateTypeName}<br/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:forEach>
                        </div>
                        <button type="button" class="btn btn-primary" id="signDocument" style="margin: 5px; display: inline-block;">Подписать документ</button>
                        <button type="button" class="btn btn-danger" id="unSignDocument" style="margin: 5px; display: inline-block;">Отказаться от подписи</button>
                        <hr/>
                    </c:when>
                    <c:when test="${isSigned != null and isSigned == true}" >
                        <h4>Данный документ вами подписан</h4>
                    </c:when>
                </c:choose>


                <c:if test="${creator != null}" >
                    <p>Создатель документа: ${creator.name}</p>
                    <c:choose>
                        <c:when test="${creator.ikp != null}" >
                            <a href="/sharer/${creator.ikp}" style="display: inline-block;">
                                <img data-src="holder.js/84x/84" alt="84x84"
                                     src="${radom:resizeImage(creator.avatar, "c84")}" data-holder-rendered="true"
                                     class="media-object img-thumbnail tooltiped-avatar"
                                     data-sharer-ikp="${creator.ikp}" data-placement="left" />
                            </a>
                        </c:when>
                        <c:otherwise>
                            <img data-src="holder.js/84x/84" alt="84x84"
                                 src="${radom:resizeImage(creator.avatar, "c84")}" data-holder-rendered="true"
                                 class="media-object img-thumbnail tooltiped-avatar"
                                 data-placement="left" />
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <p>Дата формирования документа: <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${document.createDate}" /></p>
                <p>Код документа: ${document.code}</p>
                <div>Текст документа:</div>
                <div style="width: 100%; height: 400px; overflow: auto;">
                    <iframe
                        id="printDocument"
                        name="printDocument"
                        style="width: 100%; height: 100%; border: 1px solid #ccc;"
                        src="/document/service/documentPrintPage?document_id=${document.id}&print=false"></iframe>
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
                        <c:forEach var="participant" items="${document.participants}" varStatus="i">
                            <c:choose>
                                <c:when test="${fn:length(participant.children) > 0}">
                                    <c:forEach var="childParticipant" items="${participant.children}" varStatus="i">
                                        <c:if test="${childParticipant.isNeedSignDocument}" >
                                            <tr>
                                                <td>${participant.participantTemplateTypeName}</td>
                                                <td>${participant.name}</td>
                                                <td>${childParticipant.participantTemplateTypeName} ${childParticipant.name}</td>
                                                <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${childParticipant.signDate}" /></td>
                                                <td>
                                                    <button class="btn btn-primary checkSignature" participant_id="${childParticipant.id}" >Проверить подпись</button>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${participant.isNeedSignDocument}" >
                                        <tr>
                                            <td>${participant.participantTemplateTypeName}</td>
                                            <td>${participant.name}</td>
                                            <td>${participant.name}</td>
                                            <td><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${participant.signDate}" /></td>
                                            <td>
                                                <button class="btn btn-primary checkSignature" participant_id="${participant.id}" >Проверить подпись</button>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
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
                        <c:forEach var="participant" items="${document.participants}" varStatus="i">
                            <c:choose>
                                <c:when test="${fn:length(participant.children) > 0}">
                                    <c:forEach var="childParticipant" items="${participant.children}" varStatus="i">
                                        <c:if test="${!childParticipant.isNeedSignDocument}" >
                                            <tr>
                                                <td>${participant.participantTemplateTypeName}</td>
                                                <td>${participant.name}</td>
                                                <td>${childParticipant.participantTemplateTypeName} ${childParticipant.name}</td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${!participant.isNeedSignDocument}" >
                                        <tr>
                                            <td>${participant.participantTemplateTypeName}</td>
                                            <td>${participant.name}</td>
                                            <td>${participant.name}</td>
                                        </tr>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </table>
                </div>

            </div>
            <c:if test="${userFields != null && fn:length(userFields) > 0}" >
                <h4>Заполнить пользовательские поля документа:</h4>
                <div style="margin-top: 5px;">
                    <jsp:include page="documentPageFieldList.jsp"/>
                </div>
                <button type="button" class="btn btn-primary" id="saveUserFields" style="margin-top: 5px;">Сохранить значения полей в документ</button>
                <hr/>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <h4>У Вас нет прав доступа к документу.</h4>
    </c:otherwise>
</c:choose>
