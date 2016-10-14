<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="documentListPageGrid.jsp" %>
<%@include file="documentListPageDocumentsGrid.jsp" %>
<c:choose>
   <c:when test="${userHasDocumentsRight == true}">
      <style>
         #documentsDiv {padding-top: 5px;}
      </style>
      <script type="text/javascript">
         var communityId = null;
         var documentLink = "/document/service/documentPage?document_id=";
         <c:if test="${communityId != null}" >
         communityId = ${communityId};
         documentLink = "/group/" + communityId + "/document/";
         </c:if>

         // Фильтрация документов.
         function filterDocuments(documentClassId, dateStart, dateEnd, name, participantType, participantId, content, callBack) {
            $.ajax({
               async: true,
               type: "POST",
               url: "/document/service/filterDocuments.json",
               datatype: "json",
               data: {documentClassId : documentClassId, dateStart : dateStart, dateEnd : dateEnd, name : name, participantType : participantType, participantId : participantId, content : content, communityId : communityId},
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

         // Получить участников документов по классу документов
         function getParticipantsOfDocuments(documentClassId, callBack) {
            $.ajax({
               async: true,
               type: "POST",
               url: "/document/service/getParticipantsOfDocuments.json",
               datatype: "json",
               data: {documentClassId : documentClassId, communityId : communityId},
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

         function printDocument(documentId) {
            var newWindowForPrint = window.open();
            newWindowForPrint.location.href = "/document/service/documentPrintPage?document_id=" + documentId;
         };

         $(document).ready(function(){

         });

      </script>
      <div>
         <h4>Список документов</h4>
         <div id="classDocumentsDiv" ></div>
         <div id="classDocumentsGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;"></div>
         <div id="documentsDiv"></div>
         <div id="documentsGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;">По вашему запросу ничего не найдено.</div>
      </div>
   </c:when>
   <c:otherwise>
      <h4>У Вас нет прав доступа к списку документов.</h4>
   </c:otherwise>
</c:choose>