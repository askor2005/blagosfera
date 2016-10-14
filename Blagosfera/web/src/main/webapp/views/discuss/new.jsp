<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">
    label.error{
        color: #f05610;
    }
    
    input[type=checkbox]+input+span {
        margin-left: 1em;
    }
    
    
    .form-control-inline {
        min-width: 0;
        width: 30%;
        display: inline;
    }
    
    div > input + label {
        margin-left: 1em;
    }
</style>

<script>
    $(document).ready(function () {
    	
    	var participantsDiv = $("#participants-div"),
    	    communitiesDiv = $("#communities-div");
    	participantsDiv.hide();
    	communitiesDiv.hide();
    	
    	$("#createDiscussionForm").submit(function (event) {
            return $("#createDiscussionForm").valid();
        });
    	
        $("#createDiscussionForm").validate({
            messages: {
                content: {
                    required: 'Пожалуйста, опишите подробно суть темы, выносимой на обсуждение.'
                },
                title: {
                    required: 'Подберите, пожалуйста, заголовок. Он должен отражать основную суть обсуждения и быть кратким.'
                }
            }
        });
        
        $("#content").radomTinyMCE(); 
        
        $("#recommendations").radomTinyMCE();
        
        $("#participants").checkListBox();
        
        function cascadingSelectData(title, url, request, response) {
        	$.getJSON(url, request, function(data) {
                var selectOnlyOption = data.length <= 1;
                data.splice(0, 0, {title: title, id: "-1"});
                response($.map(data, function(item, index) {
                    return {
                        label: item.title,
                        value: item.id,
                        selected: selectOnlyOption
                    };
                }));
            });
        }
        
        $("#select-cascade").cascadingDropdown({
            selectBoxes: [
                  {
                      selector: '.area',
                      source:  function(request, response) {
                    	  cascadingSelectData('Сфера обсуждения...', '/discuss/topic/areas.json', request, response);
                      }
                  },
                  {
                      selector: '.field',
                      requires: ['.area'],
                      source: function(request, response) {
                    	  if(request.area > 0) cascadingSelectData('Область обсуждения...', '/discuss/topic/fields.json', request, response);
                              
                      }
                  },
                  {
                      selector: '.topic',
                      requires: ['.area', '.field'],
                      requireAll: true,
                      source: function(request, response) {
                    	  if(request.field > 0) cascadingSelectData('Тема обсуждения...', '/discuss/topic/topics.json', request, response);
                      },
                      onChange: function(event, value, requiredValues, requirementsMet) {
                          //
                      }
                  }
              ]
          });
        
          $("span[data-label=true]").click(function(e) {
        	  debugger;
        	  var checkbox = $("input[data-name=" + $(this).data("for") + "]:first"),
        	      checked = checkbox.attr("checked");
        	  checkbox.prop("checked", function() {return !checked;});
        	  console.log(checkbox, checkbox.attr("checked"));
          });
        
          $("input[data-toggles=true]").change(function() {
        	  var name = $(this).data("child"),
        	      checked = $(this).prop('checked');
        	  $("input[name="+name+"]").attr('disabled', !checked);
          });
          
          Ext.onReady(function() {
        	  console.log("11");
              Ext.create(
                  'Ext.form.field.Date',
                  {
                      renderTo : 'time-limit',
                      width : 170,
                      xtype : 'datefield',
                      name : 'timeLimit',
                      //value : '<fmt:formatDate pattern="dd.MM.yyyy" value="${startDate}" />',
                      format : 'd.m.Y',
                  //cls : 'form-control',
                  });
          });
          
          
          $("input[name=accessType]").change(function() {
        	 var value = $(this).val();
        	 participantsDiv.toggle(value === "SELECTED_USERS")
        	 communitiesDiv.toggle(value === "SELECTED_COMMUNITIES");
          });
          
          $("#okved-filter").okvedInput({
              title : "Выбор областей деятельности",
              editTitle: "Фильтр по ОКВЭД"
          }).change(function() {
        	  var widget = $(this).data('okvedInput');
        	  $.ajax({
        		  url: "/communities/filter.json",
        		  data: {okveds: widget.val()},
        		  dataType: "json",
        		  method: "GET"
        	  }).done(function(data) {
        		 var filter = $('#community-filter.check-list-box'),
        		     select = $('#communities');
        		 filter.empty();
        		 select.empty();
        		 filter.append(select);
        		 $.each(data, function(index, item) {
        			var opt = $('<option/>').attr('value', item.id).text(item.name);
        			select.append(opt);
        		 });
        		 
        		 
        		 $('#communities').checkListBox();
        	  });
          });
    });
</script>


<h1>Создание обсуждения</h1>

<f:form id="createDiscussionForm" role="form" method="post" modelAttribute="discussionForm" >
    <div class="panel panel-default ">
		<div class="panel-heading">Тема обсуждения</div>
		<div class="panel-body">
		    <div id="select-cascade" class="form-group">
                <f:select id="select-area" path="area" class="form-control form-control-inline area" data-width="30%" title="Сфера обсуждения..." data-live-search="true">
    	            <f:options items="${areaOptions}"/>
    	        </f:select>
    	        <f:select id="select-field" path="field" class="form-control form-control-inline field" data-width="30%" title="Область обсуждения..." data-live-search="true"/>
    	        <f:select id="select-topic" path="topic" class="form-control form-control-inline topic" data-width="30%" title="Тема обсуждения..." data-live-search="true"/> 
            </div>
		    
		    <button type="button" class="btn btn-sm btn-default" data-toggle="modal" data-target="#create-topic-modal">Предложить тему</button>
		</div>
	</div>
    
    <div class="form-group">
        <f:label path="title">Заголовок обсуждения</f:label>
        <f:input path="title" class="form-control" id="discussionTitle" placeholder="Заголовок нового обсуждения"
               data-rule-required="true" required="true"/>
    </div>


    <div class="form-group">
        <f:label path="content">Описание вопроса, выносимого на обсуждение</f:label>
        <f:textarea path="content" rows="20" class="form-control" id="content" placeholder="Введите подробное описание темы обсуждения"
                  required="true" style="width:100%;height:200px;"
                  data-rule-required="true"/>
        
    </div>

    <div class="form-group">
        <f:label path="description">Рекомендации для подготовки к обсуждению вопроса</f:label>
        <f:textarea path="description" rows="5" type="input" class="form-control" id="recommendations"
                  placeholder="Информация, которая поможет вникнуть в суть обсуждаемого вопроса"/>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">Регламентные ограничения</div>
        <div class="panel-body">
            <div class="form-group row">
               <div class="col-xs-8">
                   <f:checkbox path="timeLimited" data-toggles="true" data-child="timeLimit"/>
                   <span data-label="true" data-for="timeLimited" style="cursor: pointer;">Ограничение по времени</span>
               </div>
               <div class="col-xs-4">
                   <div id="time-limit" class="form-control"></div>
               </div>
            </div>
            
            <div class="form-group row">
               <div class="col-xs-8">
                   <f:checkbox path="commentsLimited" data-toggles="true" data-child="commentsLimit"/><span>Ограничение по кол-ву предложений</span>
               </div>
               <div class="col-xs-2">
                   <f:input path="commentsLimit" class="form-control" disabled="true"/>
               </div>
            </div>
            
            <div class="form-group row">
               <div class="col-xs-8">
                   <f:checkbox path="publicEvaluation" checked="true"/><span>Разрешить публичную оценку ответов</span>
               </div>  
            </div>
            
            <div class="form-group row">
               <div class="col-xs-8">
                   <f:checkbox path="mandatory" data-toggles="true" data-child="mandatoryPeriod"/><span>Все участники должны высказать мнение в течение</span>
                  
               </div> 
               <div class="col-xs-2">
                    <f:input path="mandatoryPeriod" class="form-control" disabled="true"/>
               </div>
               <div class="col-xs-2">дней</div>
                 
            </div>
        </div>
    </div>
    
    
    <div class="panel panel-default">
        <div class="panel-heading">Права доступа</div>
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-xs-8">
                    <f:radiobutton path="accessType" value="ALL" label="Доступно для всех пользователей"/>
                </div>
            </div>
            <c:choose>
                <c:when test="${forCommunity}">
                    <div class="form-group row">
                       <div class="col-xs-8">
                           <f:radiobutton path="accessType" value="SELECTED_USERS" label="Доступно для выбранных участников объединения" /> 
                           <div class="col-xs-offset-1" id="participants-div">
                               <f:select id="participants" path="participants" multiple="true" items="${members}"/>
                           </div>
                       </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="form-group row">
                        <div class="col-xs-8">
                            <f:radiobutton path="accessType" value="SELECTED_COMMUNITIES" label="Доступно для участников объединений"/> 
                            <div class="col-xs-offset-1" id="communities-div">
                                <input id="okved-filter" />
                                <div id="community-filter">
                                    <f:select id="communities" path="communities" multiple="multiple" style="display:none;"/>
                                </div>
                            </div>  
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
                
            
            
        </div>
    </div>
    
    <div class="form-group">
        <f:label path="description">Максимальное кол-во итоговых предложений</f:label>
        <input class="form-control" disabled/>
    </div>
    
    <f:button type="submit" class="btn btn-default">Создать</f:button>
    <p class="help-block"></p>
</f:form>

<div id="create-topic-modal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span></button>
        <h4 class="modal-title">Создать тему обсуждения</h4>
      </div>
      <div class="modal-body">
        <p>Функция в разработке</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
        <button type="button" class="btn btn-primary">Сохранить</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->        
