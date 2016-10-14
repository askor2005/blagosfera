<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<%@include file="documentTypesGrid.jsp" %>
<%@include file="documentsParentsTypesGrid.jsp" %>
<style>
    #parentName{
        width: 466px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: inline-block;
        vertical-align: top;
    }
    #chooseParentClass {
        vertical-align: top;
    }
</style>

<script id="participant-form-template" type="x-tmpl-mustache">
<div class="modal fade participantDialog" role="dialog" id="edit-participant-window-{{index}}" aria-labelledby="participant-window-label-{{index}}" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="participant-window-label-{{index}}">Редактировать</h4>
			</div>
            <div class="modal-body">
                <input id="id-{{index}}" name="participants[{{index}}].id" type="text" value="{{id}}" style="display:none">
                <div class="form-group">
                    <label for="participants[{{index}}].participantType">Тип источника данных</label>
                    <select class="form-control participantType" id="participantType-{{index}}" name="participants[{{index}}].participantType">
                        <option value="INDIVIDUAL">Физ. лицо</option>
                        <option value="INDIVIDUAL_LIST">Список физ. лиц</option>
                        <option value="REGISTRATOR">Регистратор</option>
                        <option value="COMMUNITY_WITH_ORGANIZATION">Объединение в рамках юр. лица</option>
                        <option value="COMMUNITY_WITH_ORGANIZATION_LIST">Список юр. лиц</option>
                        <option value="COMMUNITY_WITHOUT_ORGANIZATION">Объединение вне рамок юр. лица</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="participants[{{index}}].participantName">Наименование источника данных</label>
                    <input id="participantName-{{index}}" name="participants[{{index}}].participantName" class="form-control" type="text" value="{{name}}">
                </div>
                <div class="form-group associationFormContainer" id="associationFormContainer-{{index}}"  style="display: none;">
                    <label>Форма объединения</label>
                    <div>
                        <label style="font-weight: normal;">
                            <input type="hidden" class="associationFormSearchType" name="participants[{{index}}].associationFormSearchType" value="{{associationFormSearchType}}" />
                            <input type="checkbox" class="associationFormSearchTypeCheckBox" {{#associationFormSearchTypeChecked}}checked="checked"{{/associationFormSearchTypeChecked}} />
                            Строгое соответствие
                        </label>
                    </div>
                    <input type="hidden" class="form-control" value='{{associationFormId}}' id="participants[{{index}}].associationForm" name="participants[{{index}}].associationForm"/>
                    <div id="associationForm-{{index}}" rameraListEditorName="community_association_forms_groups"></div>
                </div>
                <label>Список Фильтров</label>
                <div class="form-group">
                    <select id="filters-combobox-{{index}}" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%"></select>
                </div>
                <div class="form-group">
                    <ul id="filters-list-{{index}}"></ul>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="participant-window-button-close-{{index}}">Принять</button>
                <button type="button" class="btn btn-default" id="participant-window-button-cancel-{{index}}">Отмена</button>
            </div>
		</div>
	</div>
</div>
</script>

<script type="text/javascript">
    var selectedGridItem = null;
	$(document).ready(function () {
		$("#add-document-type-button").click(function () {
            if (selectedGridItem != null) {
                $("#field-parent").val(selectedGridItem.id);
                $("#parentName").text(selectedGridItem.name);
                $("#parentName").attr("title", selectedGridItem.pathName);
                $("#chooseParentClass").attr("parent_id", selectedGridItem.id);
            }
            $("#field-documentTypeId").val(-1);
			$("#documentTypeLabel").html("Добавить новый класс документов");
			$("#editDocumentTypeWindow").modal({backdrop: false, keyboard: false});
			return false;
		});

		$("#editDocumentTypeWindow").on("hidden.bs.modal", function () {
			$("#editDocumentTypeForm").trigger("reset");
			$("#form-group-key").css({"display": "block"});
			$("#form-group-participants-list").css({"display": "block"});
			$("#delete-documentType-button").css({"display": "none"});
            $("ul#participants-list").empty();

            var count = $("input[name^='participants']").size();
            for (var i = 0; i <= count; i++) {
                $("#edit-participant-window-" + i).remove();
            }
		});

		$("#save-documentType-button").click(function () {
            if (fieldsCheck()) {
                saveClassOfDocuments();
            }
            return false;
		});
		$("#delete-documentType-button").click(function () {
            var className = $("#field-name").val();
            bootbox.confirm("Ваше действие приведет к УДАЛЕНИЮ всех шаблонов которые использут класс документов \"" + className + "\".<br>Вы уверены, что хотите УДАЛИТЬ ВСЕ ШАБЛОНЫ И КЛАСС?", function(result) {
                if (result) {
                    deleteClassOfDocuments();
                }
            });
			return false;
		});

		function fieldsCheck() {
			if ($("#field-name").val() == "") {
                bootbox.alert("Укажите наименование!");
				return false;
			}
			if ($("#form-group-key").is(":visible")) {
				if ($("#field-key").val() == "") {
					bootbox.alert("Укажите ключ!");
					return false;
                }
            }
			return true;
		}

        function saveClassOfDocuments() {
			$.ajax({
				url: "/admin/flowOfDocuments/saveDocumentType",
				type: "post",
				data: $("#editDocumentTypeForm").serialize(),
				success: function (response) {
                    if (typeof(response) == 'string') {
                        response = JSON.parse(response);
                    }
					if (response.result != "error") {
						bootbox.alert("Данные успешно сохранены.");
						$("#editDocumentTypeWindow").modal("hide");
						storeDocumentTypes.load();
                        //storeComboDocumentTypes.load();
					} else {
						bootbox.alert(response.message);
					}
				}
			});
        }

        function deleteClassOfDocuments() {
            var id = $("#field-documentTypeId").val();
            $.ajax({
                url: "/admin/flowOfDocuments/deleteDocumentType?id=" + id,
                type: "post",
                data: "{}",
                success: function (response) {
                    if (response.result != "error") {
                        bootbox.alert("Запись успешно удалена.");
                        $("#editDocumentTypeWindow").modal("hide");
                        storeDocumentTypes.load();
                        storeComboDocumentTypes.load();
                    } else {
                        bootbox.alert(response.message);
                    }
                }
            });
        }

        $("#editDocumentTypeWindow").on("click", "a.participant-delete-link", function() {
            var id = parseInt($(this).attr("data-object-index"));
            $("#editDocumentTypeWindow").find("a[data-object-index=" + id + "]").parent().remove();
            $("#edit-participant-window-" + id).remove();
            return false;
        });

        $("#editDocumentTypeWindow").on("click", "a.participant-form-link", function() {
            var id = parseInt($(this).attr("data-object-index"));
            $("#edit-participant-window-" + id).modal({backdrop: false, keyboard: false});
            return false;
        });

        $("#add-participant-button").click(function () {
            addParticipant(-1, "", "", -1, "SEARCH_SUB_STRUCTURES", null, [], true);
        });

        $("#chooseParentClass").click(function(){
            viewParentClassTreePanel($(this).attr("parent_id"));
        });

        $('body').on('click', '.associationFormSearchTypeCheckBox', function () {
            var searchType = $(this).prop("checked") ? 'SEARCH_EQUALS' : 'SEARCH_SUB_STRUCTURES';
            $(".associationFormSearchType", $(this).parent()).val(searchType);
        });
	});

    //получить список всех возможных полей фильтров
    var FILTERS_INDIVIDUAL = "";
    var FILTERS_INDIVIDUAL_LIST = "";
    var FILTERS_REGISTRATOR = "";
    var FILTERS_COMMUNITY_WITH_ORGANIZATION = "";
    var FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST = "";
    var FILTERS_COMMUNITY_WITHOUT_ORGANIZATION = "";
    var FILTERS_COMMUNITY_IP = "";
    getFiltersFields("INDIVIDUAL");
    getFiltersFields("INDIVIDUAL_LIST");
    getFiltersFields("REGISTRATOR");
    getFiltersFields("COMMUNITY_WITH_ORGANIZATION");
    getFiltersFields("COMMUNITY_WITH_ORGANIZATION_LIST");
    getFiltersFields("COMMUNITY_WITHOUT_ORGANIZATION");
    getFiltersFields("COMMUNITY_IP");
    function getFiltersFields(participantType) {
        $.ajax({
            url: "/admin/flowOfDocuments/documentType/filters.json?participantType=" + participantType,
            type: "post",
            data: "{}",
            success: function (response) {
                if (response.result != "error") {
                    if (participantType == "INDIVIDUAL") {
                        FILTERS_INDIVIDUAL = response;
                    } else if (participantType == "INDIVIDUAL_LIST") {
                        FILTERS_INDIVIDUAL_LIST = response;
                    } else if (participantType == "REGISTRATOR") {
                        FILTERS_REGISTRATOR = response;
                    } else if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                        FILTERS_COMMUNITY_WITH_ORGANIZATION = response;
                    } else if (participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                        FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST = response;
                    } else if (participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
                        FILTERS_COMMUNITY_WITHOUT_ORGANIZATION = response;
                    } else if (participantType == "COMMUNITY_IP") {
                        FILTERS_COMMUNITY_IP = response;
                    }
                }
            }
        });
    }

    //заполнить ComboBox списком с фильтрами
    function fillFiltersComboBox(id, filters, associationForm) {
        $("#filters-combobox-" + id).html("");
        filters.forEach(function (entry) {
            var disabled = "";
            //if (entry.rameraListEditorItem != associationForm && entry.rameraListEditorItem != -1) {
            if (entry.associationForms.length > 0 && $.inArray(associationForm, entry.associationForms) < 0) {
                disabled = "disabled='disabled' ";
            }
            var associationFormsClasses = [];
            for (var index in entry.associationForms) {
                associationFormsClasses.push("associationForm-" + entry.associationForms[index]);
            }
            var associationFormsClassesStr = "";
            if (associationFormsClasses.length > 0) {
                associationFormsClassesStr = associationFormsClasses.join(" ") + " associationFormGroup";
            }
            $("#filters-combobox-" + id).append("<optgroup " + disabled + "label='" + entry.group + "' class='" + associationFormsClassesStr + "'></optgroup>");
            entry.filters.forEach(function (filter) {
                // Если поле не ИД
                if (filter.internalName.substring(filter.internalName.length - "_ID".length, filter.internalName.length) != "_ID") {
                    $("#filters-combobox-" + id + " optgroup[label='" + entry.group + "']").append("<option data-object-id='" + filter.id + "'>" + filter.name + "</option>");
                }
            });
        });
    }

    //добавить участника
    function addParticipant(id, type, name, associationForm, associationFormSearchType, filters, children, showModalWindow) {
        var template = $("#participant-form-template").html();
        Mustache.parse(template);
        var count = $("[id^='edit-participant-window-']").size();
        var associationFormSearchTypeChecked = associationFormSearchType == 'SEARCH_EQUALS';
        var rendered = Mustache.render(template, {
            index: count,
            id: id,
            type: type,
            name: name,
            associationFormId: associationForm,
            associationFormSearchTypeChecked: associationFormSearchTypeChecked,
            associationFormSearchType: associationFormSearchType
        });

        $("#editDocumentTypeForm").append(rendered);

        $("#participantType-" + count).val(type);
        if (type != "" && type != null) {
            if (type == "INDIVIDUAL") {
                fillFiltersComboBox(count, FILTERS_INDIVIDUAL, associationForm);
            } else if (type == "INDIVIDUAL_LIST") {
                fillFiltersComboBox(count, FILTERS_INDIVIDUAL_LIST, associationForm);
            } else if (type == "REGISTRATOR") {
                fillFiltersComboBox(count, FILTERS_REGISTRATOR, associationForm);
            } else if (type == "COMMUNITY_WITH_ORGANIZATION") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm);
            } else if (type == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST, associationForm);
            } else if (type == "COMMUNITY_WITHOUT_ORGANIZATION") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITHOUT_ORGANIZATION, associationForm);
            } else if (type == "COMMUNITY_IP") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_IP, associationForm);
            }
        }
        $("#participantType-" + count + "[value='" + type + "']").attr("selected", "selected");

        $("ul#participants-list").append(
                "<li>" +
                "<a data-object-index='" + count + "' class='participant-form-link' href='#'>" + name + "</a> " +
                "<a data-object-index='" + count + "' class='participant-delete-link glyphicon glyphicon-remove' href='#'></a>" +
                "</li>");

        if (filters != null) {
            filters.forEach(function (entry) {
                addFilterField($("#edit-participant-window-" + count), count, entry.id, entry.name);
            });
        }
        $("#participantType-" + count).on("change", function() {
            // Скрыть блок с дочерними участниками
            $("#associationFormContainer-" + count).hide();

            var participantType = $("#participantType-" + count).val();
            if (participantType == "INDIVIDUAL") {
                fillFiltersComboBox(count, FILTERS_INDIVIDUAL, associationForm);
            } else if (participantType == "INDIVIDUAL_LIST") {
                fillFiltersComboBox(count, FILTERS_INDIVIDUAL_LIST, associationForm);
            } else if (participantType == "REGISTRATOR") {
                fillFiltersComboBox(count, FILTERS_REGISTRATOR, associationForm);
            } else if (participantType == "COMMUNITY_WITH_ORGANIZATION") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITH_ORGANIZATION, associationForm);
                $("#associationFormContainer-" + count).show();
            } else if (participantType == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITH_ORGANIZATION_LIST, associationForm);
                $("#associationFormContainer-" + count).show();
            } else if (participantType == "COMMUNITY_WITHOUT_ORGANIZATION") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_WITHOUT_ORGANIZATION, associationForm);
            } else if (participantType == "COMMUNITY_IP") {
                fillFiltersComboBox(count, FILTERS_COMMUNITY_IP, associationForm);
            }
            $("#filters-combobox-" + count).removeAttr("disabled");
            $("#filters-combobox-" + count).attr("title", "Нажмите здесь и выберите нужный фильтр чтобы добавить его");
            $("#filters-combobox-" + count).selectpicker("refresh");
            $("#filters-combobox-" + count).selectpicker("val", null);
        });
        $("#edit-participant-window-" + count).on("show.bs.modal", function () {
            if ($("#participantType-" + count).val() == null) {
                $("#filters-combobox-" + count).attr("disabled", "disabled");
                $("#filters-combobox-" + count).attr("title", "Укажите источник данных");
            } else {
                // Если юр лицо, то показать блок с дочерними участниками
                if ($("#participantType-" + count).val() == "COMMUNITY_WITH_ORGANIZATION" ||
                    $("#participantType-" + count).val() == "COMMUNITY_WITH_ORGANIZATION_LIST") {
                    $("#associationFormContainer-" + count).show();
                }
                $("#filters-combobox-" + count).removeAttr("disabled");
                $("#filters-combobox-" + count).attr("title", "Нажмите здесь и выберите нужный фильтр чтобы добавить его");
            }
            $("#filters-combobox-" + count).selectpicker("refresh");
            $("#filters-combobox-" + count).selectpicker("val", null);
        });
        $("#participant-window-button-close-" + count).click(function () {
            var participantType = $("#participantType-" + count).val();
            if (participantType == null) {
                bootbox.alert("Укажите тип источника данных!");
                return false;
            }
            var participantName = $("#participantName-" + count).val();
            if (participantName == "") {
                bootbox.alert("Введите наименование источника данных!");
                return false;
            }
            $("ul#participants-list li").find("a.participant-form-link[data-object-index=" + count + "]").html(participantName);
            $("#participant-window-button-cancel-" + count).css({"display":"none"});
            $("#edit-participant-window-" + count).modal("hide");
        });
        $("#participant-window-button-cancel-" + count).click(function () {
            $("#edit-participant-window-" + count).modal("hide");
            $("ul#participants-list li").find("a.participant-form-link[data-object-index=" + count + "]").parent().remove();
        });
        $("#edit-participant-window-" + count).on("click", "a.filter-delete-link", function() {
            var id = parseInt($(this).attr("data-object-id"));
            var index = parseInt($(this).attr("data-object-index"));
            $("#edit-participant-window-" + index).find("a[data-object-id=" + id + "]").parent().remove();
            return false;
        });
        $("#filters-combobox-" + count).on("change", function() {
            var id = $(this).find("option:selected").attr("data-object-id");
            var name = $(this).find("option:selected").val();
            addFilterField($("#edit-participant-window-" + count), count, id, name);
            $(this).val(null);
        });
        if (showModalWindow) {
            $("#edit-participant-window-" + count).modal({backdrop: false, keyboard: false});
        } else {
            $("#participant-window-button-cancel-" + count).css({"display":"none"});
        }

        // инициализация компонента - формы объединения
        var associationFormItems = [];
        if (associationForm > 0) {
            associationFormItems.push(associationForm);
        }
        RameraListEditorModule.init(
                $("#associationForm-" + count),
                {
                    labelClasses: ["checkbox-inline"],
                    labelStyle: "margin-left: 10px;",
                    selectedItems: associationFormItems,
                    selectClasses: ["form-control"]
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        var rameraListEditorItemId = data.value;
                        $("[name='participants[" + count + "].associationForm']").val(rameraListEditorItemId);
                        //$("#filters-combobox-" + count + " optgroup[data-association-form!=-1]").each(function(index) {
                        $("#filters-combobox-" + count + " optgroup.associationFormGroup").each(function(index) {
                            $(this).attr("disabled", "disabled");
                        });
                        //$("#filters-combobox-" + count + " optgroup[data-association-form=" + rameraListEditorItemId + "]").removeAttr("disabled");
                        $("#filters-combobox-" + count + " optgroup.associationForm-" + rameraListEditorItemId).removeAttr("disabled");
                        $("#filters-combobox-" + count).selectpicker("refresh");
                    }
                }
        );
    }

    //получить список участников
    function getParticipants(id) {
        $.ajax({
            type: "post",
            dataType: "json",
            url: "/admin/flowOfDocuments/documentType/participants.json?id=" + id,
            success: function (response) {
                if (response.result == "error") {
                    bootbox.alert(response.message);
                } else {
                    response.forEach(function (entry) {
                        addParticipant(
                                entry.id,
                                entry.participantType,
                                entry.participantName,
                                entry.associationForm.id,
                                entry.associationFormSearchType,
                                entry.filters,
                                entry.children,
                                false
                        );
                    });
                }
            },
            error: function () {
                console.log("ajax error");
            }
        });
    }

    //добавить поле фильтр
    function addFilterField(window, index, id, name) {
        var obj = window.find("a[data-object-id=" + id + "]");
        if (obj.size() == 0) {
            $("ul#filters-list-" + index).append(
                    "<li>" + name +
                    " <a data-object-id='" + id + "' data-object-index='" + index + "' class='filter-delete-link glyphicon glyphicon-remove' href='#'></a>" +
                    "<input name='participants[" + index + "].fieldsFilters' data-toggles='true' type='checkbox' value='" + id + "' checked='true' style='display: none'>" +
                    "<input type='hidden' name='_participants[" + index + "].fieldsFilters' value='on'>" +
                    "</li>");
        }
    }

    // Установить значения выбранного родительского класса документов
    function setParentClassDocument(id, name, pathName){
        $("#field-parent").val(id);
        $("#parentName").text(name);
        $("#parentName").attr("title", pathName);
        $("#chooseParentClass").attr("parent_id", id);
    }
    // Установить выбранный элемент таблицы
    function setSelectedGridItem(record) {
        selectedGridItem = record;
    }
</script>

<h1>Классы документов</h1>
<hr/>
<a href="#" class="btn btn-primary" id="add-document-type-button">Добавить новый класс документов</a>
<hr/>
<div id="documentTypes-grid"></div>
<div id="documentTypesGridSearchResult" style="display: none; padding: 7px 10px 7px 10px; color: #666; font: 300 13px/15px helvetica,arial,verdana,sans-serif;"></div>
<hr/>

<f:form id="editDocumentTypeForm" role="form" method="post" modelAttribute="documentTypeForm">
    <!-- Modal -->
    <div class="modal fade" role="dialog" id="editDocumentTypeWindow" aria-labelledby="documentTypeLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="documentTypeLabel">Редактировать / Создать</h4>
                </div>
                <div class="modal-body">
                    <f:input path="id" class="form-control" id="field-documentTypeId" style="display: none"/>
                    <div class="form-group">
                        <label>Поместить в класс документов</label>
                        <div>
                            <div id="parentName"></div>
                            <a id="chooseParentClass" parent_id="" href="javascript:void(0)">Выбрать класс</a>
                        </div>
                        <!--div id="documentTypes-treeCombo"></div-->
                        <f:input path="parent" class="form-control" id="field-parent" style="display: none"/>
                    </div>
                    <div class="form-group">
                        <f:label path="name" id="field-name-lbl">Введите наименование</f:label>
                        <f:input path="name" class="form-control" id="field-name"/>
                    </div>
                    <div class="form-group" id="form-group-key">
                        <f:label path="key">Введите наименование ключа</f:label>
                        <f:input path="key" class="form-control" id="field-key"/>
                    </div>
                    <div class="form-group" id="form-group-participants-list">
                        <label>Список источников данных</label>
                        <ul id="participants-list"></ul>
                        <a href="#" id="add-participant-button">Добавить источник данных</a>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                    <f:button class="btn btn-primary" id="save-documentType-button" style="float: left;">Сохранить</f:button>
                    <f:button class="btn btn-danger" id="delete-documentType-button" style="display: none; float: left;">Удалить</f:button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
</f:form>