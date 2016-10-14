<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<jsp:include page="../../fields/addressFields.jsp" />
<script type="text/javascript">
    function clickBlockAddressBlock(jqCheckBox, jqAddressOverlayDiv) {
        if (jqCheckBox.prop("checked")) {
            jqAddressOverlayDiv.remove();
            jqAddressOverlayDiv.css("opacity", "0.1");
            $("#addressBlock").append(jqAddressOverlayDiv);
            initDefaultAddressBlock();
        } else {
            jqAddressOverlayDiv.remove();
        }
    }

    function onChangeCountryField(listEditorItemData) {
    }

    function initDefaultAddressBlock() {
        $("input[data-field-name]").each(function(){
            var internalName = $(this).attr("data-field-name");
            $(this).val(defaultInputValues[internalName]);
        });
        $("#COMMUNITY_LEGAL_F_COUNTRY").empty();
        $("[data-field-internal-name='COMMUNITY_LEGAL_F_COUNTRY']").val(defaultInputValues['COMMUNITY_LEGAL_F_COUNTRY']);
        // инициализация компонентов универсальных списков (страна)
        initCountryField("COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS", "COMMUNITY_LEGAL_F_COUNTRY", "COMMUNITY_LEGAL_F_POST_CODE", onChangeCountryField, null, RoomTypes.OFFICE, "COMMUNITY_LEGAL_F_OFFICE");
    }

    var defaultInputValues = {};

    var jqAddressOverlayDiv = $("<div style='position: absolute; top: 0px; right: 0px; bottom: 0px; left: 0px; background-color: #000000;'></div>");
    $(document).ready(function () {
        // Инициализация аддресного блока данных
        //initCountryField("COMMUNITY_WITH_ORGANIZATION_LEGAL_F_ADDRESS", "COMMUNITY_LEGAL_F_COUNTRY", "COMMUNITY_LEGAL_F_POST_CODE", onChangeCountryField, null, RoomTypes.OFFICE, "COMMUNITY_LEGAL_F_OFFICE");

        $("input[data-field-name]").each(function(){
            var internalName = $(this).attr("data-field-name");
            var value = $(this).val();
            defaultInputValues[internalName] = value;
        });
        defaultInputValues['COMMUNITY_LEGAL_F_COUNTRY'] = $("[data-field-internal-name='COMMUNITY_LEGAL_F_COUNTRY']").val();

        // Лочим блок с адресом
        $("#blockAddress").click(function(){
            clickBlockAddressBlock($(this), jqAddressOverlayDiv);
        });
        clickBlockAddressBlock($("#blockAddress"), jqAddressOverlayDiv);
    })
</script>


<c:forEach items="${fieldsGroups}" var="g">
    <div class="panel panel-default" id="fields-group-panel" data-group-name="${g.internalName}">

        <div class="panel-heading">
            <div style="display: inline-block"><h4 class="panel-title">${g.name}</h4></div>
            <div style="display: inline-block"><label>(<input type="checkbox" id="blockAddress" checked="checked" /> совпадает с ${community.rusShortName}) </label></div>
        </div>
        <div id="collapse-profile-${g.id}">
            <div class="panel-body" style="position: relative;" id="addressBlock">
                <c:forEach items="${g.fields}" var="field">
                    <c:set var="f" value="${field}" scope="request"/>
                    <c:if test="${
                        f.internalName == 'COMMUNITY_LEGAL_F_COUNTRY' or
                        f.internalName == 'COMMUNITY_LEGAL_F_POST_CODE' or
                        f.internalName == 'COMMUNITY_LEGAL_F_REGION' or
                        f.internalName == 'COMMUNITY_LEGAL_F_AREA' or
                        f.internalName == 'COMMUNITY_LEGAL_F_LOCALITY' or
                        f.internalName == 'COMMUNITY_LEGAL_F_STREET' or
                        f.internalName == 'COMMUNITY_LEGAL_F_HOUSE' or
                        f.internalName == 'COMMUNITY_LEGAL_F_OFFICE'
                    }">
                        <%
                            // Если это блок с полным адресом или с геоданными, то скрываем их
                        %>
                        <div class="row" style="
                        <c:if test="${field.internalName == 'COMMUNITY_LEGAL_F_GEO_LOCATION' || field.internalName == 'COMMUNITY_LEGAL_F_GEO_POSITION'}">
                                display: none;
                                </c:if>"
                                >
                            <div class="col-xs-12">
                                <t:insertTemplate template="/views/communities/field.jsp"/>
                            </div>
                        </div>

                    </c:if>
                </c:forEach>
                <div class="form-group" name="geo-block">
                    <br/>
                    <div id="${g.internalName}_MAP" class="panel-map" style="height : 300px;"></div>
                    <hr/>
                </div>
            </div>
        </div>
    </div>
</c:forEach>