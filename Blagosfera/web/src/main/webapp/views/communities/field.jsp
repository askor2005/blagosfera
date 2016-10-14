<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="form-group <c:if test='${f.hideable}'> hideable</c:if>" data-required="${f.required}" data-has-points="${f.points > 0}" data-field-type="${f.type}">
	<label>${f.name}</label>
    <div class="fieldContainer" id="fieldContainer_${f.internalName}">
        <c:choose>
            <c:when test="${f.type == 'SELECT'}">
                <c:choose>
                    <c:when test="${f.internalName == 'COMMUNITY_DIRECTOR_POSITION'}">
                        <input id="${f.fieldsGroup.internalName}_POSITION"
                               data-changes-checker-ignore="true" type="hidden" name="f:${f.id}"
                               value="${community.getFieldValue(f).stringValue}"
                               data-field-internal-name="${f.internalName}"
                               data-field-type="${f.type}" />
                        <div id="positions" rameraListEditorName="position_id"></div>
                    </c:when>
                    <c:otherwise>
                        <select class="form-control" name='f:${f.id}'>
                            <option></option>
                            <c:forEach items="${f.possibleValues}" var="p">
                                <option <c:if test="${p.stringValue.equals(community.getFieldValue(f).stringValue)}">selected='selected'</c:if>  value="${p.stringValue}">${p.stringValue}</option>
                            </c:forEach>
                        </select>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${f.type == 'REGION' or f.type == 'DISTRICT' or f.type == 'CITY' or f.type == 'STREET' or f.type == 'BUILDING'}">
                <input type="text" class="form-control" data-kladr-object-type='${fn:toLowerCase(f.type)}'
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
            </c:when>
            <c:when test="${f.type == 'SHARER'}">
                <input type="text" class="form-control"
                       id="${f.fieldsGroup.internalName}_SHARER"
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
            </c:when>
            <c:when test="${fn:substring(f.type, 0, fn:length('PARTICIPANTS_LIST')) == 'PARTICIPANTS_LIST'}">
                <input type="text" class="form-control"
                       id="${f.internalName}" value=''
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
                <br>
                <ul id="${f.internalName}_${f.type}">
                    <c:if test="${not empty participantsMap}">
                        <c:forEach items="${participantsMap}" var="map">
                            <c:if test="${map.key == f.internalName}">
                                <c:set var="participantsList" value="${map.value}" />
                                <c:forEach items="${participantsList}" var="participant">
                                    <li>${participant.getFullName()}
                                        <a data-${f.internalName}-id='${participant.getId()}' data-input-id="${f.internalName}" class='${f.internalName}-delete-link glyphicon glyphicon-remove' href='javascript:void(0)'></a>
                                    </li>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:if>
                </ul>
            </c:when>
            <c:when test="${f.internalName == 'COMMUNITY_INN'}">
                <input type="text" class="form-control" id="inn"
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
            </c:when>
            <c:when test="${f.internalName == 'COMMUNITY_BRIEF_DESCRIPTION'}">
                <textarea class="form-control" id="announcement" rows="4" maxlength="250"
                          name='f:${f.id}' data-field-name='${f.internalName}'
                          data-field-type='${f.type}' data-field-id='${f.id}'
                          data-field-value='${community.getFieldValue(f).stringValue}'
                          placeholder='${f.example}' >${community.getFieldValue(f).stringValue}</textarea>
            </c:when>
            <c:when test="${f.internalName == 'COMMUNITY_DESCRIPTION'}">
                <textarea class="form-control" id="description" rows="10"
                          name='f:${f.id}' data-field-name='${f.internalName}'
                          data-field-type='${f.type}' data-field-id='${f.id}'
                          data-field-value='${community.getFieldValue(f).stringValue}'
                          placeholder='${f.example}' >${community.getFieldValue(f).stringValue}</textarea>
            </c:when>
            <c:when test="${f.internalName == 'COMMUNITY_SHORT_LINK_NAME'}">
                <div class="input-group">
                    <div class="input-group-addon community-seo-link-addon">
                        <span class="community-seo-link-base"></span><c:if test="${not empty community.parent}">${community.parent.seoLink}/sg/</c:if>
                    </div>
                    <script type="text/javascript">
                        // Сразу, без document.ready, выставляем домен.
                        // Иначе поле появляется, но правильное здачение выставляется заметно позже.
                        $(".community-seo-link-base").html(window.location.origin+"/group/");
                    </script>
                    <input type="text" class="form-control"
                           value='${community.editableSeoLink}'
                           name='f:${f.id}' data-field-name='${f.internalName}'
                           data-field-type='${f.type}' data-field-id='${f.id}'
                           data-field-value='${community.editableSeoLink}'
                           placeholder='${f.example}' />
                </div>
            </c:when>
            <c:when test="${f.internalName == 'COMMUNITY_CHARTER_DESCRIPTION'}">
                <textarea class="form-control" id="charter_description" rows="10"
                          name='f:${f.id}' data-field-name='${f.internalName}'
                          data-field-type='${f.type}' data-field-id='${f.id}'
                          data-field-value='${community.getFieldValue(f).stringValue}'
                          placeholder='${f.example}' >${community.getFieldValue(f).stringValue}</textarea>
            </c:when>
            <c:when test="${f.type == 'COUNTRY'}">
                <input type="hidden" class="form-control"
                       data-field-type='${f.type}'
                       data-field-id='${f.id}'
                       name='f:${f.id}'
                       data-field-internal-name="${f.internalName}"
                       readonly="readonly"
                       value='${community.getFieldValue(f).stringValue}' />
                <input type="hidden" class="country-control" data-field-internal-name="${f.internalName}_NAME" />
                <div id="${f.internalName}" rameraListEditorName="country_id"></div>
            </c:when>
            <c:when test="${f.type == 'GEO_POSITION' or f.type == 'GEO_LOCATION'}">
                <input type="text" class="form-control" readonly="readonly"
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
            </c:when>
            <c:when test="${f.type == 'UNIVERSAL_LIST'}">
                <div class="universalList" rameraListEditorName="${f.internalName}"></div>
                <input type="hidden"
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                        />
            </c:when>

            <c:otherwise>
                <input type="text" class="form-control"
                       value='${community.getFieldValue(f).stringValue}'
                       name='f:${f.id}' data-field-name='${f.internalName}'
                       data-field-type='${f.type}' data-field-id='${f.id}'
                       data-field-value='${community.getFieldValue(f).stringValue}'
                       placeholder='${f.example}' />
            </c:otherwise>
        </c:choose>
        <c:if test="${f.attachedFile}" >
            <div class="fieldFileContainer" id="fieldFileContainer_${f.internalName}">
                <a class="browseFieldFile"
                    has_rights_to_edit="true"
                    file_limit="-1"
                    title="Просмотреть прикреплённые файлы"
                    field_id="${f.id}"
                    <c:if test="${not empty community}" >
                        field_files_url="/communities/${community.id}/${f.id}/fieldFiles.json"
                        field_files_save_url="/communities/${community.id}/${f.id}/saveFieldFiles.json"
                    </c:if>
                    <c:if test="${empty community}" >
                        field_files_url=""
                        field_files_save_url=""
                    </c:if>
                ></a>
            </div>
        </c:if>
    </div>
    <span class="help-block">${f.comment}</span>
    <span style="display : none;" class="help-block help-block-error"></span>
</div>