<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<style>
    .document_class_data_source_block {
        border: 1px solid #ccc;
        padding: 5px;
        margin-bottom: 5px;
    }
</style>
<script type="text/javascript">
    var previewTemplateId = null;
    var previewDocumentTemplate = null;

    // Загрузка возможных участников документа и их пользовательские поля
    function loadSourceParticipantsData(templateId, callBack) {
        $.radomJsonPost(
                "/admin/docs/process/getPossibleParticipants.json",
                {
                    template_id : templateId
                },
                callBack,
                null
        );

        callBack();
    }

    function createDocumentPreview(createDocumentParameters, callBack) {
        $.radomJsonPost(
                "/admin/docs/process/createDocumentContent.json",
                JSON.stringify(createDocumentParameters),
                callBack,
                null,
                {
                    contentType: "application/json"
                }
        );
    }

    function getCreateDocumentParameters() {
        var createDocumentParameters = [];

        $(".document_class_data_source_block").each(function(i){
            var $selectParticipants = $(this).find(".sourceParticipantList");
            var dataSourceName =  $(this).attr("data_source_name");
            var dataSourceType = $(this).attr("data_source_type");
            var $userFields = $(this).find(".user_field_input");
            var participantIds = [];
            if ($selectParticipants != null && $selectParticipants.length > 0) {
                $selectParticipants.each(function(){
                    participantIds.push($(this).val());
                });
            }

            var userFieldValueList = [];
            if ($userFields != null) {
                $userFields.each(function() {
                    var $userFieldInput = $(this);
                    var fieldName = $userFieldInput.attr("user_field_name");
                    var type = $userFieldInput.attr("user_field_type");
                    var value = $userFieldInput.val();
                    var values = [value];
                    var userFieldValue = {
                        fieldName : fieldName,
                        type : type,
                        values : values
                    };
                    userFieldValueList.push(userFieldValue);
                })
            }


            var participantParameter = {
                type : dataSourceType,
                ids : participantIds,
                name : dataSourceName
            };

            createDocumentParameters.push({
                participantParameter: participantParameter,
                userFieldValueList : userFieldValueList
            });
        });
        return {
            templateId : previewTemplateId,
            createDocumentParameters : createDocumentParameters
        }
    }

   function initPreviewTemplate(templateId) {
       $("#parsedDocumentContent").css("height", "500px").radomTinyMCE({});
        previewTemplateId = templateId;
        previewDocumentTemplate = $("#previewDocumentTemplate").html();
        Mustache.parse(previewDocumentTemplate);

        $("body").on("click", ".addSourceParticipant", function (e) {
            var $self = $(this);
            var documentClassDataSourceId = $self.attr("document_class_data_source_id");
            var sourceParticipantList = $($("[document_class_data_source_block=" + documentClassDataSourceId + "]").find(".selectParticipant").get(0));
            sourceParticipantList.parent().append(sourceParticipantList.clone());
        });

       $("#previewTemplate").click(function(){
           $("#previewTemplateWindow").modal("show");
       });

       $("#previewTemplateWindow").on("shown.bs.modal", function () {
           $("#documentContent").hide();
           $("#documentStatus").show();
           $("#documentStatus").text("Загрузка...");
           $("#document_class_data_sources_list").html("");
           loadSourceParticipantsData(previewTemplateId, function(response){
               $("#documentStatus").hide();
               var rendered = Mustache.render(previewDocumentTemplate,
                       {
                           possibleSourceParticipantsList : response
                       }
               );
               /*{
                   documentClassDataSources : [
                       {
                           id : 1,
                           name : "Получатель",
                           type : "INDIVIDUAL_LIST",
                           sourceParticipants : [
                               {
                                   id : 485,
                                   name : "Гусев Владимир"
                               },
                               {
                                   id : 3,
                                   name : "Дьяков Костя"
                               }
                           ],
                           listParticipant : true,
                           userFields : [
                               {
                                   name : "qweqwe"
                               }
                           ]
                       }
                   ]
               });*/
               $("#document_class_data_sources_list").append(rendered);
           })
       });

       $("#createDocumentButton").click(function(){
           $("#documentContent").hide();
           createDocumentPreview(getCreateDocumentParameters(), function(response){
               $("#documentContent").show();
               $("#parsedDocumentShortName").val(response.shortName);
               $("#parsedDocumentName").val(response.name);
               $("#parsedDocumentContent").val(response.content);
           })
       });
    }
</script>
<script id="previewDocumentTemplate" type="x-tmpl-mustache">
    {{#possibleSourceParticipantsList}}
        <div class="document_class_data_source_block" data_source_id="{{documentClassDataSourceId}}" data_source_type="{{documentClassDataSourceType}}" data_source_name="{{documentTypeDataSourceName}}">
            <label>{{documentTypeDataSourceName}}</label>
            <div document_class_data_source_block="{{documentClassDataSourceId}}">
                <div class="selectParticipant" style="padding: 10px 0px 10px 0px;">
                    <select class="sourceParticipantList" document_class_data_source_id="{{documentClassDataSourceId}}" style="width: 100%;">
                        {{#possibleSourceParticipants}}
                            <option value="{{id}}">{{name}}</option>
                        {{/possibleSourceParticipants}}
                    </select>
                </div>
            </div>
            {{#listParticipant}}
                <button type="button" class="btn btn-primary addSourceParticipant" document_class_data_source_id="{{documentClassDataSourceId}}">Добавить участника</button>
            {{/listParticipant}}
            {{#userFields}}
                <div field_name="{{name}}">
                    <label>{{name}}</label>
                    <div>
                        <input type="test" class="form-control user_field_input" user_field_name="{{name}}" user_field_type="{{type}}" />
                    </div>
                </div>
            {{/userFields}}
        </div>
    {{/possibleSourceParticipantsList}}
</script>


<!-- Модальное окно для проверки шаблона документа-->
<div class="modal fade" role="dialog" id="previewTemplateWindow" aria-labelledby="previewTemplateTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="previewTemplateTextLabel">Предпросмотр документа</h4>
            </div>
            <div class="modal-body">
                <div id="document_class_data_sources_list"></div>
                <a class="btn btn-primary" id="createDocumentButton">Сформировать документ</a>
                <div id="documentStatus" style="display: none;"></div>
                <div id="documentContent" style="display: none;">
                    <div class="form-group">
                        <label for="parsedDocumentShortName">Сокращённое наименование документа</label>
                        <input id="parsedDocumentShortName" name="parsedDocumentShortName" class="form-control" type="text" />
                        </div>
                    <div class="form-group">
                        <label for="parsedDocumentName">Полное наименование документа</label>
                        <input id="parsedDocumentName" name="parsedDocumentName" class="form-control" type="text" />
                        </div>
                    <div class="form-group">
                        <label>Документ</label>
                        <textarea id="parsedDocumentContent"></textarea>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->