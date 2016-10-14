<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:choose>
   <c:when test="${userHasRightsToPage == true}">
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

         // Подписать список документов
         function signDocuments(documentIds, callBack) {
            $.ajax({
               async: true,
               type: "POST",
               url: "/document/service/signDocuments.json",
               datatype: "json",
               data: {documentIds : JSON.stringify(documentIds)},
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

         $(document).ready(function(){
            $(".signCheckboxCommon").click(function(){
               var checked = $(this).prop("checked");
               $(".signCheckbox").prop("checked", checked);
            });
            $(".signCheckbox").click(function(){
               var allChecked = true;
               $(".signCheckbox").each(function(){
                  if (!$(this).prop("checked")) {
                     allChecked = false;
                  }
               });
               $(".signCheckboxCommon").prop("checked", allChecked);
            });
            $("#signDocuments").click(function(){
               var checkedDocuments = [];
               $(".signCheckbox").each(function(){
                  if ($(this).prop("checked")) {
                     var documentId = $(this).attr("id");
                     checkedDocuments.push(documentId);
                  }
               });
               if (checkedDocuments.length > 0) {
                  signDocuments(checkedDocuments, function() {
                     document.location.reload();
                  });
               } else {
                  bootbox.alert("Выберите документы на подписание!");
               }
            });
         });

      </script>
      <div>
         <c:choose>
            <c:when test="${fn:length(documents) > 0}" >
               <h4>Для Вас есть документы на подпись</h4>
               <hr/>
               <div>Отметьте галочками документы, которые Вы хотите подписать и нажмите кнопку "Подписать" и следуйте инструкциям.</div>
               <div id="documentsDiv" style="margin-top: 3px;">
                  <table>
                     <tr>
                        <th>№</th>
                        <th>Документ</th>
                        <th>Дата формирования</th>
                        <th>
                           <label>
                              <input class="signCheckboxCommon" type="checkbox" />
                              Подписать
                           </label>
                        </th>
                     </tr>
                     <c:forEach var="document" items="${documents}" varStatus="i">
                        <tr>
                           <td>${document.id}</td>
                           <td><a href="/document/service/documentPage?document_id=${document.id}">${document.name}</a></td>
                           <td><fmt:formatDate pattern="dd.MM.yyyy" value="${document.createDate}" /></td>
                           <td>
                              <label>
                                 <input class="signCheckbox" type="checkbox" id="${document.id}" />
                                 Да
                              </label>
                           </td>
                        </tr>
                     </c:forEach>
                  </table>
                  <a href="javascript:void(0)" class="btn btn-primary" id="signDocuments" style="float: right; margin-top: 3px;">Подписать</a>
               </div>
            </c:when>
            <c:otherwise>
               <h4>Для Вас нет документов на подпись</h4>
            </c:otherwise>
         </c:choose>
      </div>
   </c:when>
   <c:otherwise>
      <h4>У Вас нет прав подписи документов.</h4>
   </c:otherwise>
</c:choose>

