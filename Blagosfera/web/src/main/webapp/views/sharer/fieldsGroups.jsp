<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<c:set var="visibleGroupsCount" value="0"></c:set>

<c:forEach items="${fieldsGroups}" var="g">

  <c:set var="visibleFieldsCount" value="0"></c:set>
  <c:forEach items="${g.fields}" var="field">
    <c:if test="${fieldsStates[field].visible}">
      <c:set var="visibleFieldsCount" value="${visibleFieldsCount + 1}"></c:set>
    </c:if>
  </c:forEach>

  <c:if test="${visibleFieldsCount > 0}">
    <c:set var="visibleGroupsCount"	value="${visibleGroupsCount + 1}"></c:set>
    <div class="panel panel-default" id="fields-group-panel-${g.id}" data-group-name="${g.internalName}">
      <div class="panel-heading">
        <h4 class="panel-title">
            ${g.name}
          <a data-toggle="tooltip" data-placement="left"
             title='Скрыть блок' href="#"
             onclick="return slideBlock($(this));"
             class="glyphicon glyphicon-arrow-up hidden-group-eye"></a>
          <c:if test="${sharer.id == profile.id}">
            <a data-toggle="tooltip" data-placement="left"
               title='Скрыть все поля в этой группе' href="#"
               onclick="return changeGroupHidden($(this), ${g.id}, true);"
               class="glyphicon glyphicon-eye-close hidden-group-eye"></a>
            <a data-toggle="tooltip" data-placement="left"
               title='Показать все поля в этой группе' href="#"
               onclick="return changeGroupHidden($(this), ${g.id}, false);"
               class="glyphicon glyphicon-eye-open hidden-group-eye"></a>
          </c:if>

          <c:if test='${sharer.id == profile.id and "PERSON_REGISTRATION_ADDRESS" == g.internalName}'>
            <a class="pull-right collapse-group-control small" id="hideAll" href="#" onclick="return copyPersonAddressRegistrationFromActual();">Скопировать из адреса проживания</a>
          </c:if>
          <c:if test='${sharer.id == profile.id and "PERSON_ACTUAL_ADDRESS" == g.internalName}'>
            <a class="pull-right collapse-group-control small" id="hideAll" href="#" onclick="return copyPersonAddressActualFromRegistration();">Скопировать из адреса регистрации</a>
          </c:if>

        </h4>
      </div>
      <div id="collapse-profile-${g.id}" class="panel-collapse in">
        <div class="panel-body">
          <c:forEach items="${g.fields}" var="field">
            <c:set var="f" value="${field}" scope="request" />
            <c:choose>
              <c:when test="${field.type == 'GEO_POSITION' or field.type == 'GEO_LOCATION'}">
                <input data-changes-checker-ignore="true" type="hidden"
                       name="f:${field.id}"
                       value="${fieldValueMap.get(field).stringValue}"
                       data-field-internal-name="${field.internalName}"
                       data-field-type="${field.type}"  />
              </c:when>
              <c:when test="${f.type == 'ADDRESS_FIELD_DESCRIPTION' || f.type == 'HIDDEN_TEXT'}">
                <input type="hidden" class="form-control"
                       id="${f.internalName}"
                       value='${fieldValueMap.get(f).stringValue}'
                       name='f:${f.id}'
                       data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${fieldValueMap.get(f).stringValue}'/>
              </c:when>
              <c:otherwise>
                <c:if test="${fieldsStates[f].visible}">
                  <c:set var="visibleFieldsCount"	value="${visibleFieldsCount + 1}"></c:set>
                  <c:set var="fieldHidden" value="${fieldValueMap.get(f).hidden}" scope="request" />
                  <c:if test="${fieldValueMap.get(f) == null}" >
                    <c:set var="fieldHidden" value="${f.hiddenByDefault}" scope="request" />
                  </c:if>
                  <div class="row">
                    <c:choose>
                      <c:when test="${sharer.id == profile.id and f.hideable}">
                        <div class="col-xs-11">
                          <t:insertTemplate template="/views/sharer/field.jsp" />
                        </div>
                        <div class="col-xs-1">
                          <a href="#"
                             tabindex="-1"
                             data-toggle="tooltip"
                             data-placement="right"
                             title='<c:if test="${fieldHidden}">Сейчас это поле скрыто</c:if><c:if test="${!fieldHidden}">Сейчас это поле видно всем</c:if>'
                             onclick="return changeHidden($(this))"
                             class="glyphicon hidden-field-eye<c:if test="${not fieldHidden}"> glyphicon-eye-open</c:if><c:if test="${fieldHidden}"> glyphicon-eye-close</c:if>"
                             data-field-id="${f.id}"
                             data-hidden="${fieldHidden}"></a>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="col-xs-12">
                          <t:insertTemplate template="/views/sharer/field.jsp" />
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </c:if>
              </c:otherwise>
            </c:choose>
          </c:forEach>

          <c:if test="${g.internalName == 'PERSON_REGISTRATION_ADDRESS' or g.internalName == 'PERSON_ACTUAL_ADDRESS' or g.internalName == 'REGISTRATOR_OFFICE_ADDRESS'}">
            <div class="form-group" name="geo-block">
              <br/>
              <div id="${g.internalName}_MAP" class="panel-map" style="height : 300px;"></div>
              <hr/>
            </div>
          </c:if>
        </div>
      </div>
    </div>
  </c:if>
</c:forEach>

<c:if test="${visibleGroupsCount==0}">
    <h3 style="text-align: center;">Данные скрыты</h3>
</c:if>