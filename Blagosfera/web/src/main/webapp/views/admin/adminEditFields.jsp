<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<style type="text/css">
    ul#possible-values-list {
        padding : 0;
    }

    li.possible-values-list-item {
        display : inline-block;
        border : 1px solid;
        padding : 3px;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;
        margin : 5px 5px 0 0;

        color: #31708f;
        background-color: #d9edf7;
        border-color: #bce8f1;
    }

    li.possible-values-list-item span {
        white-space : nowrap;
    }

    li.possible-values-list-item a {
        margin-left : 5px;
    }
</style>

<script id="possible-value-form-template" type="x-tmpl-mustache">
<div class="modal fade" role="dialog" id="edit-possible-value-window-{{index}}" aria-labelledby="edit-possible-value-window-label-{{index}}" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="edit-possible-value-window-label-{{index}}">Редактировать</h4>
			</div>
            <div class="modal-body">
                <input name="possibleValues[{{index}}].id" type="hidden" value="{{id}}">
                <input name="possibleValues[{{index}}].field" type="hidden" value="{{fieldId}}">
                <div class="form-group">
                    <label>Строковое значение</label>
                    <input id="possibleValuesStringValue-{{index}}" name="possibleValues[{{index}}].stringValue" class="form-control" type="text" value="{{stringValue}}">
                </div>
                   <div class="form-group">
                    <label>Позиция для сортировки</label>
                    <input  id="possibleValuesPosition-{{index}}" class="form-control" name="possibleValues[{{index}}].position" value="{{position}}" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="possible-value-button-close-{{index}}">Закрыть</button>
                <button type="button" class="btn btn-default" id="possible-value-button-cancel-{{index}}">Отмена</button>
            </div>
		</div>
	</div>
</div>
</script>

<script type="text/javascript">
    $(document).ready(function() {
        $("#participantType").val(null);
        $("#participantType").on("change", function() {
            var participantType = $(this).val();
            if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                $("#associationForm").empty();
                // Форма объединения
                RameraListEditorModule.init(
                        $("#associationForm"),
                        {
                            labelClasses: ["checkbox-inline"],
                            labelStyle: "margin-left: 10px;",
                            selectClasses: ["form-control"]
                        },
                        function(event, data) {
                            if (event == RameraListEditorEvents.VALUE_CHANGED) {
                                $("input#associationFormId").val(data.value);
                                updateFieldsList(participantType, data.value)
                            }
                        }
                );
                $("#association-form-group").css("display", "block");
                $("#fields-combobox").attr("title", "Укажите форму объединения");
                $("#fields-combobox").attr("disabled", "disabled");
                $("#fields-combobox").selectpicker("refresh");
                $("#fields-combobox").selectpicker("val", null);
            } else {
                $("input#associationFormId").val(-1);
                $("#association-form-group").css("display", "none");
                updateFieldsList(participantType, -1)
            }

            $("#editField").css("display", "none");
            $("#editFieldGroup").css("display", "none");
        });

        function updateFieldsList(participantType, associationFormId) {
            $.ajax({
                url: "/admin/flowOfDocuments/documentType/filters.json?participantType=" + participantType + "&associationForm=" + associationFormId,
                type: "post",
                data: "{}",
                success: function (response) {
                    if (response.result != "error") {
                        fillFiltersComboBox(response, associationFormId);
                    }
                }
            });
        }

        function fillFiltersComboBox(fields, associationFormId) {
            $("#fields-combobox").removeAttr("title");
            $("#fields-combobox").removeAttr("disabled");
            $("#fields-combobox").html("");
            associationFormId = parseInt(associationFormId);
            fields.forEach(function (entry) {
                var disabled = "";
                if (entry.associationForms.length > 0 && $.inArray(associationFormId, entry.associationForms) < 0) {
                    disabled = "disabled='disabled' ";
                }
                $("#fields-combobox").append("<optgroup " + disabled + "label='" + entry.group + "'></optgroup>");
                JSON.parse(entry.filters).forEach(function (field) {
                    // Если поле не ИД
                    if (field.internalName.substring(field.internalName.length - "_ID".length, field.internalName.length) != "_ID") {
                        $("#fields-combobox optgroup[label='" + entry.group + "']").append("<option data-object-id='" + field.id + "'>" + field.name + "</option>");
                    }
                });
            });
            $("#fields-combobox").selectpicker("refresh");
            $("#fields-combobox").selectpicker("val", null);
        }

        $("#fields-combobox").change(function() {
            getFieldById($("#fields-combobox option:selected").attr("data-object-id"));
        });

        function getFieldById(fieldId) {
            $.ajax({
                url: "/admin/adminEditFields/getField.json?fieldId=" + fieldId,
                type: "post",
                data: "{}",
                success: function (response) {
                    if (response.result != "error") {
                        fillFieldForm(response);
                        $("#editField").css("display", "block");
                        $("#editFieldGroup").css("display", "block");
                    } else {
                        $("#editField").css("display", "none");
                        $("#editFieldGroup").css("display", "none");
                        bootbox.alert(response.message);
                    }
                }
            });
        }

        function fillFieldForm(field) {
            $("#editFieldGroup").find("[name='id']").val(field.group.id);
            $("#editFieldGroup").find("[name='internalName']").val(field.group.internalName);
            $("#editFieldGroup").find("[name='name']").val(field.group.name);
            $("#editFieldGroup").find("[name='position']").val(field.group.position);
            $("#editFieldGroup").find("[name='objectType']").val(field.group.objectType);

            $("#editField").find("[name='id']").val(field.id);
            $("#editField").find("[name='fieldsGroup']").val(field.group.id);
            $("#editField").find("[name='internalName']").val(field.internalName);
            $("#editField").find("[name='type']").val(field.type);
            $("#editField").find("[name='name']").val(field.name);
            $("#editField").find("[name='example']").val(field.example);
            $("#editField").find("[name='comment']").val(field.comment);
            $("#editField").find("[name='position']").val(field.position);
            $("#editField").find("[name='points']").val(field.points);
            $("#editField").find("[name='hideable']").prop("checked", field.hideable);
            $("#editField").find("[name='hiddenByDefault']").prop("checked", field.hiddenByDefault);
            $("#editField").find("[name='unique']").prop("checked", field.unique);
            $("#editField").find("[name='useCase']").prop("checked", field.useCase);
            $("#editField").find("[name='required']").prop("checked", field.required);
            $("#editField").find("[name='verifiedEditable']").prop("checked", field.verifiedEditable);
            $("#editField").find("[name='attachedFile']").prop("checked", field.attachedFile);

            $("#possible-values-group").css("display", "none");
            clearPosibleValues();

            if (field.type == "SELECT") {
                $("#editField").find("#possible-values-group").css("display", "block");
                field.possibleValues.forEach(function (entry) {
                    addPossibleValue(entry, false);
                });
            } else {
                $("#editField").find("#possible-values-group").css("display", "none");
            }
        }

        function clearPosibleValues() {
            $("[id^='edit-possible-value-window']").each(function(index) {
                $(this).remove();
            });
            $("#possible-values-list").empty();
        }

        $("#editField").find("[name='type']").change(function() {
            var selectedValue = $("#editField").find("[name='type']").val();
            if (selectedValue == "SELECT") {
                $("#editField").find("#possible-values-group").css("display", "block");
            } else {
                $("#editField").find("#possible-values-group").css("display", "none");
            }
            clearPosibleValues();
        });

        function addPossibleValue(possibleValue, showModalWindow) {
            var template = $("#possible-value-form-template").html();
            Mustache.parse(template);
            var index = $("[id^='edit-possible-value-window-']").size();

            var rendered = Mustache.render(
                    template,
                    {
                        index: index,
                        id: possibleValue.id,
                        fieldId: possibleValue.field.id,
                        stringValue: possibleValue.stringValue,
                        position : possibleValue.position
                    }
            );
            $("#editField").append(rendered);

            $("ul#possible-values-list").append(
                    "<li class='possible-values-list-item'>" +
                    "<a data-object-index='" + index + "' class='possible-value-form-link' href='#'>" + possibleValue.stringValue + "</a> " +
                    "<a data-object-index='" + index + "' class='possible-value-delete-link glyphicon glyphicon-remove' href='#'></a>" +
                    "</li>");

            $("#possible-value-button-close-" + index).click(function () {
                var possibleValuesStringValue = $("#possibleValuesStringValue-" + index).val();
                if (possibleValuesStringValue == "") {
                    bootbox.alert("Укажите строковое значение!");
                    return false;
                }
                var possibleValuesPosition = $("#possibleValuesPosition-" + index).val();
                if (possibleValuesPosition == "") {
                    bootbox.alert("Укажите позицию для сортировки!");
                    return false;
                }
                $("ul#possible-values-list li").find("a.possible-value-form-link[data-object-index=" + index + "]").html(possibleValuesStringValue);
                $("#possible-value-button-cancel-" + index).css({"display":"none"});
                $("#edit-possible-value-window-" + index).modal("hide");
            });
            $("#possible-value-button-cancel-" + index).click(function () {
                $("#edit-possible-value-window-" + index).modal("hide");
                $("ul#possible-values-list li").find("a.possible-value-form-link[data-object-index=" + index + "]").parent().remove();
            });

            if (showModalWindow) {
                $("#edit-possible-value-window-" + index).modal({backdrop: false, keyboard: false});
            } else {
                $("#possible-value-button-cancel-" + index).css({"display":"none"});
            }
        }

        $("#editField").on("click", "a.possible-value-delete-link", function() {
            var index = parseInt($(this).attr("data-object-index"));
            $("#editField").find("a[data-object-index=" + index + "]").parent().remove();
            $("#edit-possible-value-window-" + index).remove();
            return false;
        });

        $("#editField").on("click", "a.possible-value-form-link", function() {
            var index = parseInt($(this).attr("data-object-index"));
            $("#edit-possible-value-window-" + index).modal({backdrop: false, keyboard: false});
            return false;
        });

        $("#add-possible-value-button").click(function () {
            var fieldId = $("#fields-combobox option:selected").attr("data-object-id");
            var possibleValue = {
                id: -1,
                field: {id: fieldId},
                stringValue: "",
                position: 0
            };
            addPossibleValue(possibleValue, true);
            return false;
        });

        $("#field-group-button-save").click(function () {
            $.ajax({
                url: "/admin/adminEditFields/saveFieldGroup",
                type: "post",
                data: $("#editFieldGroup").serialize(),
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("Данные успешно сохранены.");
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
            return false;
        });

        $("#field-button-save").click(function () {
            $.ajax({
                url: "/admin/adminEditFields/saveField",
                type: "post",
                data: $("#editField").serialize(),
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("Данные успешно сохранены.");
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
            return false;
        });
    });
</script>

<h1>Редактирование полей</h1>
<hr/>
<div class="form-group">
    <label for="participantType">Тип участника</label>
    <select class="form-control participantType" id="participantType" name="participantType">
        <option value="INDIVIDUAL">Физ. лицо</option>
        <option value="REGISTRATOR">Регистратор</option>
        <option value="COMMUNITY_WITH_ORGANIZATION">Объединение в рамках юр. лица</option>
        <option value="COMMUNITY_WITHOUT_ORGANIZATION">Объединение вне рамок юр. лица</option>
    </select>
</div>
<div class="form-group" id="association-form-group" style="display: none">
    <label>Форма объединения</label>
    <input type="hidden" class="form-control" value='-1' id="associationFormId" name="association_form_id"/>
    <div id="associationForm" rameraListEditorName="community_association_forms_groups"></div>
</div>
<div class="form-group">
    <label>Укажите поле для редактирования</label>
    <select id="fields-combobox" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%" disabled="disabled" title="Укажите тип участника"></select>
</div>
<hr/>
<f:form id="editFieldGroup" role="form" method="post" modelAttribute="fieldGroupForm" style="display: none">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">Редактировать группу поля</h4>
        </div>
        <div class="panel-collapse in">
            <div class="panel-body">
                <input name="id" type="hidden" value="-1">
                <div class="form-group">
                    <label>Внутреннее имя</label>
                    <input class="form-control" name="internalName" type="text" value="" readonly="true">
                </div>
                <div class="form-group">
                    <label>Наименование группы</label>
                    <input class="form-control" name="name" type="text" value="">
                </div>
                <div class="form-group">
                    <label>Позиция для сортировки</label>
                    <input class="form-control" name="position" value="0" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                </div>
                <div class="form-group">
                    <label>Тип объекта</label>
                    <select class="form-control" name="objectType">
                        <option value="SHARER">SHARER</option>
                        <option value="COMMUNITY">COMMUNITY</option>
                    </select>
                </div>
                <div class="form-group">
                    <button type="button" class="btn btn-primary" id="field-group-button-save">Сохранить</button>
                </div>
            </div>
        </div>
    </div>
</f:form>
<f:form id="editField" role="form" method="post" modelAttribute="fieldForm" style="display: none">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">Редактировать поле</h4>
        </div>
        <div class="panel-collapse in">
            <div class="panel-body">
                <input name="id" type="hidden" value="-1">
                <input name="fieldsGroup" type="hidden" value="-1">
                <div class="form-group">
                    <label>Внутреннее имя</label>
                    <input class="form-control" name="internalName" type="text" value="" readonly="true">
                </div>
                <div class="form-group">
                    <label>Тип поля</label>
                    <select class="form-control" name="type">
                        <option value="TEXT">Текст</option>
                        <option value="DATE">Дата</option>
                        <option value="SELECT">Список</option>
                        <option value="COUNTRY">Страна</option>
                        <option value="REGION">Регион</option>
                        <option value="DISTRICT">Раойн</option>
                        <option value="CITY">Город</option>
                        <option value="STREET">Улица</option>
                        <option value="BUILDING">Строение</option>
                        <option value="SUBHOUSE">Корпус</option>
                        <option value="MULTILINE_TEXT">Многострочный текст</option>
                        <option value="LANDLINE_PHONE">Мобильный телефон</option>
                        <option value="LINK">Ссылка</option>
                        <option value="SKYPE">Скайп</option>
                        <option value="MOBILE_PHONE">Мобильный телефон</option>
                        <option value="GEO_POSITION">Геопозиция</option>
                        <option value="GEO_LOCATION">Геолокация</option>
                        <option value="TIMETABLE">Временная шкала</option>
                        <option value="SHARER">Список пользователей системы</option>
                        <option value="SYSTEM">Системное</option>
                        <option value="PARTICIPANTS_LIST">Список участников</option>
                        <option value="PARTICIPANTS_LIST_COMMUNITY">Список участников объединения</option>
                    </select>
                </div>
                <div class="form-group" id="possible-values-group">
                    <label>Список возможных значений</label>
                    <ul id="possible-values-list"></ul>
                    <a href="#" id="add-possible-value-button">Добавить значение</a>
                </div>
                <div class="form-group">
                    <label>Наименование поля</label>
                    <input class="form-control" name="name" type="text" value="">
                </div>
                <div class="form-group">
                    <label>Подсказка при вводе (placeholder)</label>
                    <input class="form-control" name="example" type="text" value="">
                </div>
                <div class="form-group">
                    <label>Примечание</label>
                    <input class="form-control" name="comment" type="text" value="">
                </div>
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-6">
                            <label>Позиция для сортировки</label>
                            <input class="form-control" name="position" value="0" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                        </div>
                        <div class="col-xs-6">
                            <label>points</label>
                            <input class="form-control" name="points" value="1" type="number" min="0" onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="hideable"/> Скрываемое
                            </label>
                        </div>
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="hiddenByDefault"/> Скрыто по умолчанию
                            </label>
                        </div>
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="unique"/> Признак уникальности
                            </label>
                        </div>
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="useCase"/> Использовать падеж
                            </label>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="row">
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="required"/> Обязательное
                            </label>
                        </div>
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="verifiedEditable"/> verifiedEditable
                            </label>
                        </div>
                        <div class="col-xs-3">
                            <label>
                                <input type="checkbox" name="attachedFile"/> Возможность прикрепления файла
                            </label>
                        </div>
                        <div class="col-xs-3">

                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <button type="button" class="btn btn-primary" id="field-button-save">Сохранить</button>
                </div>
            </div>
        </div>
    </div>
</f:form>