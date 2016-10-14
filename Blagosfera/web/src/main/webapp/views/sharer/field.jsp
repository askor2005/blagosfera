<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div class="form-group<c:if test='${f.hideable}'> hideable</c:if>" data-required="${f.required}" data-has-points="${f.points > 0}" data-field-type="${f.type}">
	<label>${f.name}</label>
    <c:choose>
        <c:when test="${not fieldsStates[f].valueChangeAllowed}">
            <c:choose>
                <c:when test="${f.type == 'IMAGE'}">
                    <div class="form-control" style="width: auto !important; padding: 1px 12px 1px 12px;">
                        <c:set var="fieldImageUrl" value="${radom:getFirstFieldFileUrl(fieldValueMap.get(f))}"></c:set>
                        <c:if test="${fieldImageUrl == null || fieldImageUrl == ''}" >
                            <c:set var="fieldImageUrl" value="${f.example}"></c:set>
                        </c:if>
                        <img class="fieldImage" src="${fieldImageUrl}" style="height: 30px;"/>
                    </div>
                </c:when>
                <c:when test="${f.internalName == 'WWW' and not empty fieldValueMap.get(f).stringValue}">
                    <div class="form-control">
                        <a data-field-value="${fieldValueMap.get(f).stringValue}" data-field-internal-name="${f.internalName}" class="form-control-input-link" href="${radom:checkWebsitePrefix(fieldValueMap.get(f).stringValue)}"><i class="fa fa-cloud"></i> ${fieldValueMap.get(f).stringValue}</a>
                    </div>
                </c:when>
                <c:when test="${f.internalName == 'SKYPE' and not empty fieldValueMap.get(f).stringValue}">
                    <div class="form-control">
                        <a data-field-value="${fieldValueMap.get(f).stringValue}" data-field-internal-name="${f.internalName}" class="form-control-input-link" href="skype:${fieldValueMap.get(f).stringValue}?call"><i class="fa fa-skype"></i> ${fieldValueMap.get(f).stringValue}</a>
                    </div>
                </c:when>
                <c:when test="${f.internalName == 'HOME_TEL' and not empty fieldValueMap.get(f).stringValue}">
                    <div class="form-control">
                        <a data-field-value="${fieldValueMap.get(f).stringValue}" data-field-internal-name="${f.internalName}" class="form-control-input-link" href="callto:${fieldValueMap.get(f).stringValue}"><i class="fa fa-phone"></i> ${fieldValueMap.get(f).stringValue}</a>
                    </div>
                </c:when>
                <c:when test="${f.internalName == 'MOB_TEL' and not empty fieldValueMap.get(f).stringValue}">
                    <div class="form-control">
                        <a data-field-value="${fieldValueMap.get(f).stringValue}" data-field-internal-name="${f.internalName}" class="form-control-input-link" href="callto:${fieldValueMap.get(f).stringValue}"><i class="fa fa-mobile"></i> ${fieldValueMap.get(f).stringValue}</a>
                    </div>
                </c:when>
                <c:when test="${(f.type == 'TIMETABLE')}">
                    <dl class="dl-horizontal">
                        <dt>&nbsp;</dt>
                        <dd>&nbsp;</dd>
                        <c:forEach items="${timetables[f].combinedDays}" var="d">
                            <dt>${d.title}</dt>
                            <dd>${d.text}</dd>
                        </c:forEach>
                        <dt>&nbsp;</dt>
                        <dd>&nbsp;</dd>

                        <dt>Сейчас</dt>
                        <dd>${timetables[f].now ? "Работает" : "Не работает"}</dd>
                    </dl>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${f.type == 'COUNTRY'}">
                            <input type="text" class="form-control"
                                   data-field-type='${f.type}'
                                   data-field-id='${f.id}'
                                   data-field-internal-name="${f.internalName}"
                                   readonly="readonly"
                                   com_code='${radom:getRameraListEditorItemMnemoCodeById(fieldValueMap.get(f).stringValue)}'
                                   value='${radom:getRameraListEditorItemTextById(fieldValueMap.get(f).stringValue)}' />
                        </c:when>
                        <c:otherwise>
                            <input type="text" class="form-control"
                                   data-field-type='${f.type}'
                                   data-field-id='${f.id}'
                                   data-field-internal-name="${f.internalName}"
                                   readonly="readonly"
                                   value='${fieldValueMap.get(f).stringValue}' />
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <div class="fieldContainer" id="fieldContainer_${f.internalName}">
                <c:choose>
                    <c:when test="${f.type == 'IMAGE'}">
                        <div class="form-control" style="width: auto !important; padding: 1px 12px 1px 12px;">
                            <c:set var="fieldImageUrl" value="${radom:getFirstFieldFileUrl(fieldValueMap.get(f))}"></c:set>
                            <c:if test="${fieldImageUrl == null || fieldImageUrl == ''}" >
                                <c:set var="fieldImageUrl" value="${f.example}"></c:set>
                            </c:if>
                            <img class="fieldImage" src="${fieldImageUrl}" style="height: 30px;"/>
                        </div>
                    </c:when>
                    <c:when test="${(f.type == 'COUNTRY') and ((not profile.verified) or radom:hasRole('ROLE_SUPERADMIN') == true)}">
                        <input type="hidden" class="form-control"
                               data-field-type='${f.type}'
                               data-field-id='${f.id}'
                               name='f:${f.id}'
                               data-field-internal-name="${f.internalName}"
                               data-field-value='${fieldValueMap.get(f).stringValue}'
                               value='${fieldValueMap.get(f).stringValue}' />
                        <input type="hidden" class="country-control" data-field-internal-name="${f.internalName}_NAME" />
                        <div id="${f.internalName}" rameraListEditorName="country_id"></div>
                    </c:when>
                    <c:when test="${(f.type == 'SELECT') and ((not profile.verified) or radom:hasRole('ROLE_SUPERADMIN') == true)}">
                        <!--<select class="form-control" name='f:${f.id}'
                                data-field-value="${fieldValueMap.get(f).stringValue}"
                                <c:if test="${sharer.id != profile.id}">
                                    <c:if test="${showRegisteredAt == false || profile.verified == true}">
                                        <c:if test="${radom:hasRole('ROLE_SUPERADMIN') == false}">
                                            disabled='disabled'
                                        </c:if>
                                    </c:if>
                                </c:if>>
                            <option></option>
                            <c:forEach items="${f.possibleValues}" var="p">
                                <option <c:if test="${p.stringValue.equals(fieldValueMap.get(f).stringValue)}">selected='selected'</c:if>  value="${p.stringValue}">${p.stringValue}</option>
                            </c:forEach>
                        </select>-->
                        <input type="text" class="form-control" name='f:${f.id}'
                               value="${fieldValueMap.get(f).stringValue}"
                               data-field-value="${fieldValueMap.get(f).stringValue}"
                                <c:if test="${sharer.id != profile.id}">
                                    <c:if test="${showRegisteredAt == false || profile.verified == true}">
                                        <c:if test="${radom:hasRole('ROLE_SUPERADMIN') == false}">
                                            disabled="disabled"
                                        </c:if>
                                    </c:if>
                                </c:if>>
                        <script type="application/javascript">
                            (function() {
                                var $input = $("input[name='f:${f.id}']");
                                var options = [];

                                <c:forEach items="${f.possibleValues}" var="p">
                                    options.push("${p.stringValue}");
                                </c:forEach>

                                $input.typeahead({
                                    source: options,
                                    showHintOnFocus: true,
                                    minLength: 0,
                                    items: 10
                                });

                                $input.blur(function() {
                                    var value = $(this).val();
                                    var clear = true;

                                    for (var i = 0; i < options.length; i++) {
                                        if (value === options[i]) {
                                            clear = false;
                                            break;
                                        }
                                    }

                                    if (clear) $(this).val('');
                                });
                            }());
                        </script>
                    </c:when>
                    <c:when test="${(f.type == 'TIMETABLE')}">
                        <table class="table table-bordered table-timetable" data-field-id="${f.id}" data-editable="true">
                            <c:forEach items="${timetables[f].days}" var="d" varStatus="ds">
                                <tr>
                                    <td class="day-timetable text-right">
                                            ${d.title}
                                        <input type="checkbox" data-day="${ds.index}" <c:if test="${d.full}">checked="checked"</c:if> />
                                    </td>
                                    <c:forEach items="${d.hours}" var="h" varStatus="hs">
                                        <td class="cell-timetable" data-day="${ds.index}" data-hour="${hs.index}" data-selected="${h}"></td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td></td>
                                <c:forEach items="${timetables[f].hours}" var="h" varStatus="hs">
                                    <td class="hour-timetable">
                                        <div>${h.title}</div>
                                        <input type="checkbox" class="changeHour" data-hour="${hs.index}" <c:if test="${h.full}">checked="checked"</c:if> />
                                    </td>
                                </c:forEach>
                            </tr>
                        </table>
                        <input name='f:${f.id}' data-field-type='${f.type}' data-field-id='${f.id}' data-field-internal-name="${f.internalName}" type="hidden" value='${fieldValueMap.get(f).stringValue}' />
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${f.type == 'COUNTRY'}">
                                <input type="hidden"
                                       class="form-control"
                                       value='${fieldValueMap.get(f).stringValue}'
                                       name='f:${f.id}' data-field-name='${f.internalName}' data-field-internal-name='${f.internalName}'
                                       data-field-type='${f.type}' data-field-id='${f.id}'
                                       data-field-value='${fieldValueMap.get(f).stringValue}'
                                       placeholder='${f.example}'
                                        <c:if test="${sharer.id != profile.id}">
                                            <c:if test="${showRegisteredAt == false || profile.verified == true}">
                                                <c:if test="${radom:hasRole('ROLE_SUPERADMIN') == false}">
                                                    disabled='disabled'
                                                </c:if>
                                            </c:if>
                                        </c:if>
                                        />
                                <input type="hidden" class="country-control" data-field-internal-name="${f.internalName}_NAME" />
                                <div id="${f.internalName}" rameraListEditorName="country_id"></div>
                            </c:when>
                            <c:otherwise>
                                <input type="text"
                                       class="form-control"
                                       value='${fieldValueMap.get(f).stringValue}'
                                       name='f:${f.id}' data-field-name='${f.internalName}' data-field-internal-name='${f.internalName}'
                                       data-field-type='${f.type}' data-field-id='${f.id}'
                                       data-field-value='${fieldValueMap.get(f).stringValue}'
                                       placeholder='${f.example}'
                                        <c:if test="${sharer.id != profile.id}">
                                            <c:if test="${showRegisteredAt == false || profile.verified == true}">
                                                <c:if test="${radom:hasRole('ROLE_SUPERADMIN') == false}">
                                                    disabled='disabled'
                                                </c:if>
                                            </c:if>
                                        </c:if>
                                />
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>

                <c:if test="${f.attachedFile}" >
                    <div class="fieldFileContainer" id="fieldFileContainer_${f.internalName}">
                        <a class="browseFieldFile"
                           has_rights_to_edit="true"
                           title="Просмотреть прикреплённые файлы"
                           field_id="${f.id}"
                           field_files_url="/sharer/${profile.id}/${f.id}/fieldFiles.json"
                           field_files_save_url="/sharer/${profile.id}/${f.id}/saveFieldFiles.json"
                            <c:choose>
                                <c:when test="${f.type == 'IMAGE'}">
                                    file_limit="1"
                                </c:when>
                                <c:otherwise>
                                    file_limit="-1"
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${f.internalName == 'PERSON_SYSTEM_SIGNATURE'}">
                                    field_types="png" min_width="100" min_height="30" max_width="100" max_height="30"
                                </c:when>
                                <c:otherwise>
                                </c:otherwise>
                            </c:choose>
                        ></a>
                    </div>
                </c:if>
            </div>

        </c:otherwise>
    </c:choose>

	<c:if test="${sharer.id == profile.id}">
		<span class="help-block">${f.comment}</span>
	</c:if>
</div>