/**
 * Created by vgusev on 05.06.2015.
 */
RameraListEditorAction = function() {
    this.rameraListEditorActionType = "";
    this.rameraListEditor = null;
    this.rameraListEditorItem = null;
    this.id = -1;
    this.listEditorName = "";
};

RameraListEditor = function () {
    this.name = "";
    this.formName = "";
    // Массив RameraListEditorItem
    this.items = [];
    // Поле типа RameraListEditorType
    this.listEditorType = null;
    //
    this.node = null;
};

RameraListEditorItem = function () {
    this.text = "";
    this.isActive = false;
    this.parent = null;
    this.children = [];
    // Ссылка на RameraListEditor
    //this.listEditor = null;
    // Поле типа RameraListEditorType
    this.listEditorItemType = null;
    //
    this.isSelectedItem = true;
    // Для сортировки - порядок элемента
    this.order = null;
    //
    this.mnemoCode = null;
};

RameraListEditorType = {
    COMBOBOX : "COMBOBOX",
    CHECKBOX : "CHECKBOX",
    RADIOBUTTON : "RADIOBUTTON"
};

RameraListEditorGlobalParameters = {
    isTreeComponentLoaded : false
}

RameraListEditorDataLoader = {

    url : "/ramera_list_editor/handle.json",

    init : function (url) {
        this.url = url;
    },

    ADD_RAMERA_LIST_EDITOR : "ADD_RAMERA_LIST_EDITOR",
    UPDATE_RAMERA_LIST_EDITOR : "UPDATE_RAMERA_LIST_EDITOR",
    DELETE_RAMERA_LIST_EDITOR : "DELETE_RAMERA_LIST_EDITOR",
    GET_RAMERA_LIST_EDITOR_BY_NAME : "GET_RAMERA_LIST_EDITOR_BY_NAME",
    GET_RAMERA_LIST_EDITOR_WITH_CHILDS_BY_NAME : "GET_RAMERA_LIST_EDITOR_WITH_CHILDS_BY_NAME",
    GET_ALL_RAMERA_LIST_EDITORS : "GET_ALL_RAMERA_LIST_EDITORS",

    ADD_RAMERA_LIST_EDITOR_ITEM : "ADD_RAMERA_LIST_EDITOR_ITEM",
    UPDATE_RAMERA_LIST_EDITOR_ITEM : "UPDATE_RAMERA_LIST_EDITOR_ITEM",
    DELETE_RAMERA_LIST_EDITOR_ITEM : "DELETE_RAMERA_LIST_EDITOR_ITEM",
    GET_RAMERA_LIST_EDITOR_ITEM_BY_NAME : "GET_RAMERA_LIST_EDITOR_ITEM_BY_NAME",

    // Права доступа пользователя
    sharerIsSuperAdmin : false,

    loadListEditorWithDataByName : function (name, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.GET_RAMERA_LIST_EDITOR_WITH_CHILDS_BY_NAME;
        rameraListEditorAction.listEditorName = name;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    loadListEditorByName : function (name, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.GET_RAMERA_LIST_EDITOR_BY_NAME;
        rameraListEditorAction.listEditorName = name;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    loadAllListEditors : function (onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.GET_ALL_RAMERA_LIST_EDITORS;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    addListEditor : function(rameraListEditor, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.ADD_RAMERA_LIST_EDITOR;
        rameraListEditorAction.rameraListEditor = rameraListEditor;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    /*fixCodeField : function(items) {
        if (items != null) {
            for (var index in items) {
                var item = items[index];
                item.mnemoCode = item.code;
                this.fixCodeField(item.child);
            }
        }
    },*/

    updateListEditor : function(rameraListEditor, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.UPDATE_RAMERA_LIST_EDITOR;
        rameraListEditorAction.rameraListEditor = rameraListEditor;
        //this.fixCodeField(rameraListEditorAction.rameraListEditor.items);
        //rameraListEditorAction.rameraListEditor.items[0].children[0] = {text : "asd"};
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    deleteListEditor : function (id, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.DELETE_RAMERA_LIST_EDITOR;
        rameraListEditorAction.id = id;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    deleteListEditorItem: function (id, onSuccessCallBack, onErrorCallBack) {
        var rameraListEditorAction = new RameraListEditorAction();
        rameraListEditorAction.rameraListEditorActionType = this.DELETE_RAMERA_LIST_EDITOR_ITEM;
        rameraListEditorAction.id = id;
        this.requestData(this.url, rameraListEditorAction, onSuccessCallBack, onErrorCallBack);
    },

    requestData : function(url, action, onSuccessCallBack, onErrorCallBack) {
        var self = this;
        $.radomJsonPost(
            url,
            JSON.stringify(action),
            function(param){
                self.sharerIsSuperAdmin = param.userSuperAdmin;
                var isError = false;
                try {
                    isError = param.result.stack != null;
                } catch (e) {
                    //
                }
                if (isError) {
                    if (!onErrorCallBack) {
                        alert("Произошла ошибка! Текст ошибки: " + param.result.message);
                    } else {
                        onErrorCallBack(param.result);
                    }
                } else {
                    onSuccessCallBack(param.result);
                }
            },
            function(param) {
                if (param.responseText != null && param.responseText != '') {
                    var exc = JSON.parse(param.responseText);
                    //exc.message,
                    //exc.stack
                    if (!onErrorCallBack) {
                        alert("Произошла ошибка! Текст ошибки: " + exc.message)
                    } else {
                        onSuccessCallBack(exc);
                    }
                }
            },
            {
                contentType: "application/json"
            }
        );
    }
};

RameraListEditorEvents = {
    VALUE_CHANGED : "value_changed",
    DATA_LOADED : "data_loaded",
    CREATED : "created"
};

RameraListEditorFactory = {
  createNewListEditorView : function(rameraListEditor, rameraListEditorItem, parentView) {
      var view = null;
      var listEditor = new RameraListEditor();
      listEditor.formName = rameraListEditor.formName;
      listEditor.listEditorType = rameraListEditorItem.listEditorItemType;
      listEditor.items = rameraListEditorItem.children;
      listEditor.node = rameraListEditor.node;
      var parameters = parentView.parameters == null ? {} : parentView.parameters;
      switch (rameraListEditorItem.listEditorItemType) {
          case RameraListEditorType.COMBOBOX:
              if (rameraListEditorItem.isSelectedItem) {
                  parameters.selectEmptyValue = "-- Выберите из списка --";
              } else {
                  parameters.selectEmptyValue = null;
              }
              view = new RameraListEditorComboView(listEditor, parentView.dataLoader, parameters, parentView.callBack);
              break;
          case RameraListEditorType.CHECKBOX:
              view = new RameraListEditorCheckBoxView(listEditor, parentView.dataLoader, parameters, parentView.callBack);
              break;
          case RameraListEditorType.RADIOBUTTON:
              view = new RameraListEditorRadioButtonView(listEditor, parentView.dataLoader, parameters, parentView.callBack);
              break;
      }
      return view;
  }
};

function onChangeListEditorItem(callBack, itemId, text, code) {
    code = code == "undefined" ? null : code;
    callBack(itemId, text, code);
    callBack(RameraListEditorEvents.VALUE_CHANGED, {value : itemId, text : text, code : code});
}

// Базовый класс представлений списков.
RameraListEditorBase = function (rameraListEditor, dataLoader, parameters, callBack) {
    this.rameraListEditor = rameraListEditor;
    this.dataLoader = dataLoader;
    this.parameters = parameters;
    this.callBack = callBack == null ? function(){} : callBack;
    // Основная нода для редактирования компонента
    var windowHeight = $(window).height();
    this.editNodeTemplate =
        "<div>" +
            "<div style='padding: 5px; margin: 5px; border: 1px solid #000; width: 45%; display: inline-block; vertical-align: top;'>" +
                "<div>" +
                    "<div><label>Поиск элементов:</label> <input type='text' id='searchElements' style='width: 200px;' /></div>"+
                    "<div>Элементы компонента:</div>"+
                    "<div class='listeditor_items' style='width: 100%; height: " + (windowHeight - 100) + "px; overflow: auto;'></div>"+
                "</div>" +
            "</div>" +

            "<div style='padding: 5px; margin: 5px; border: 1px solid #000; position: fixed; right: 0px; top: 0px; width: 45%;'>" +
                "<div>" +
                    "<div>Параметры компонента:</div>"+
                "</div>" +
                "<div>" +
                    "<input type='button' class='listeditor_save' value='Сохранить'/>"+
                    "<input type='button' class='listeditor_cancel' value='Отмена'/>"+
                    "<input type='button' class='add_listeditor_item' value='Добавить элемент'/>"+
                "</div>" +
                "<div style='position: relative; padding-top: 5px;' id='iframeNode'>" +
                    //"<form class='fileUploadForm' method='post' enctype='multipart/form-data'>" +
                        "<div style='display: inline-block; width: 250px;'>Загрузить элементы из CSV файла:</div>"+
                        "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'>" +
                            //"<input type='file' name='file' style='float: left;' />" +
                            "<input type='button' class='uploadFileButton' value='Загрузить файл'/>"+
                        "</div>"+
                    ///"</form>" +
                "</div>" +
                "<div style='position: relative; padding-top: 5px;'>" +
                    "<div style='display: inline-block; width: 250px;'>ИД списка:</div>"+
                    "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;' class='listeditor_id'></div>"+
                "</div>" +
                "<div style='position: relative; padding-top: 5px;'>" +
                    "<div style='display: inline-block; width: 250px;'>Имя списка:</div>"+
                    "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='text' style='width: 100%' class='listeditor_name'></div>"+
                "</div>" +
                "<div style='position: relative; padding-top: 5px;'>" +
                    "<div style='display: inline-block; width: 250px;'>Имя для формы:</div>"+
                    "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='text' style='width: 100%' class='listeditor_formname'></div>"+
                "</div>" +

                "<div style='position: relative; padding-top: 5px;'>" +
                    "<div style='display: inline-block; width: 250px;'>Тип списка:</div>"+
                    "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'>" +
                        "<select style='width: 100%' class='listeditor_type'>" +
                            "<option value='COMBOBOX'>Выпадающий список</option>" +
                            "<option value='CHECKBOX'>Флажки</option>" +
                            "<option value='RADIOBUTTON'>Переключатели</option>" +
                        "</select>" +
                    "</div>"+
                "</div>" +


            "</div>" +
            "<div style='margin: 5px; padding: 5px; border: 1px solid #000; position: fixed; right: 0px; top: 193px; width: 45%;' id='edit_selected_node'>" +
            "</div>" +
        "</div>";
    this.editNode = null;

    // Нода с содержимым элемента списка
    this.childNodeTemplate =
    //this.editSelectedNode =
        "<div class='listeditor_item' style=''>" +
            "<div>" +
                "<div>Параметры выбранного элемента:</div>"+
            "</div>" +
            "<div>" +
                "<input type='button' class='delete_listeditor_item' value='Удалить элемент'/>"+
            "</div>" +
            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>ИД:</div>" +
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;' class='listeditor_item_id'></div>" +
            "</div>" +
            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>Текст элемента:</div>"+
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='text' style='width: 100%' class='listeditor_item_text'></div>"+
            "</div>" +
            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>Мнемокод элемента:</div>"+
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='text' style='width: 100%' class='listeditor_item_mnemo_code'></div>"+
            "</div>" +
            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>Выбран по умолчанию:</div>"+
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='checkbox' class='listeditor_item_active'></div>"+
            "</div>" +

            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>Тип дочернего списка:</div>"+
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'>" +
                    "<select style='width: 100%' class='listeditor_item_type'>" +
                        "<option value='COMBOBOX'>Выпадающий список</option>" +
                        "<option value='CHECKBOX'>Флажки</option>" +
                        "<option value='RADIOBUTTON'>Переключатели</option>" +
                    "</select>" +
                "</div>"+
            "</div>" +

            "<div style='position: relative; padding-top: 5px;'>" +
                "<div style='display: inline-block; width: 250px;'>Выбираемое значение списка:</div>"+
                "<div style='display: inline-block; position: absolute; right: 0px; left: 250px;'><input type='checkbox' class='listeditor_item_selected' checked='checked'></div>"+
            "</div>" +

            "<div>" +
                "<input type='button' class='add_listeditor_item' value='Добавить дочерний элемент'/>"+
                "<input type='button' class='clone_listeditor_item' value='Скопировать элемент'/>"+
                "<input type='button' class='clone_listeditor_item_recursion' value='Скопировать элемент рекурсивно'/>"+
            "</div>" +
        "</div>";

    this.jqTree = null;

    // Сортировка элементов списка
    function _sort(items) {
        //items
        if (items != null) {
            items.sort(function(a, b){
                // Сортировка по спискам
                if (a.order != null && b.order != null) {
                    if (b.order > a.order) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    if (a.id > b.id) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            for (var i=0; i<items.length; i++) {
                var item = items[i];
                item.order = i;
                var children = item.children;
                _sort(children);
            }
        }
    };

    // Построить рекурсивно модель для дерева
    function _getTreeData(items, searchString) {
        var result = [];
        for (var index in items) {
            var item = items[index];
            var children = _getTreeData(item.children, searchString);
            var isFinded = true;
            if (searchString != null && searchString != "") {
                isFinded = false;
                var position = item.text.search(new RegExp( "(" + searchString + ")" , 'gi' ));
                if (position > -1) {
                    isFinded = true;
                }
            }
            if (children.length > 0 || isFinded) {
                result.push({
                    label: item.text,
                    id: item.id,
                    item : item,
                    children : children
                });
            }
        }
        return result;
    };

    // Синхронизация модели из дерева
    function _sycnModelFromTree(nodes) {
        var items = [];
        for (var i=0; i<nodes.length; i++) {
            var node = nodes[i];
            var item = node.item;
            item.children = [];
            if (node.children != null && node.children.length > 0) {
                item.children = _sycnModelFromTree(node.children);
            }
            item.order = i;
            items.push(item);
        }
        return items;
    };

    // Открыть форму редактирования
    this.drawEdit = function() {
        var self = this;
        this.editNode = $(this.editNodeTemplate);
        $("#editOverlayDiv").empty();
        var overlayDiv =
            $(
                "<div id='editOverlayDiv' style='position: fixed; top: 0px; left: 0px; right: 0px; bottom: 0px; background: #FFF; z-index: 1030; overflow: auto;'>" +
                "</div>"
            );
        overlayDiv.append(self.editNode);
        $("body").append(overlayDiv);

        // Сортирока элементов по ИД, потом по полю order
        _sort(self.rameraListEditor.items);

        // Создание дерева
        var treeJqueryData = _getTreeData(self.rameraListEditor.items);
        this.jqTree = $('.listeditor_items', self.editNode);
        this.jqTree.tree({
            data: treeJqueryData,
            autoOpen: true,
            dragAndDrop: true
        });

        // Событие выбора компонента
        self.jqTree.bind(
            'tree.click',
            function(event) {
                var node = event.node;
                if (node.item != null) {
                    //var item = node.item;
                    $("#edit_selected_node").children().remove();
                    _drawItemFromData(self, node, $("#edit_selected_node"), $(self.childNodeTemplate));
                }
            }
        );

        // Событие перетаскивания ноды
        self.jqTree.bind(
            'tree.move',
            function(event) {
                event.preventDefault();
                event.move_info.do_move();
                var rootNodes = self.jqTree.tree("getTree").getData();
                self.rameraListEditor.items = _sycnModelFromTree(rootNodes);
            }
        );

        // Добавление корневого элемента
        $(".add_listeditor_item", self.editNode).click(function() {
            var newItem = new RameraListEditorItem();
            newItem.listEditorItemType = self.rameraListEditor.listEditorType;
            newItem.text = 'new_node';
            var newNodeId = (Math.random() * 100000).toFixed(0);

            // Если есть выбранный элемент, то новый надо добавлять после него
            var selectedNode = self.jqTree.tree("getSelectedNode");
            var addNodeFunction = !selectedNode ? 'appendNode' : 'addNodeAfter';

            self.jqTree.tree(
                addNodeFunction,
                {
                    label: newItem.text,
                    id: newNodeId,
                    item : newItem
                },
                selectedNode
            );
            self.rameraListEditor.items.push(newItem);

            var newNode = self.jqTree.tree('getNodeById', newNodeId);
            if (newNode != null) {
                newNode.id = null;

                // Имитируем клик по новому элементу
                $(".jqtree-element", newNode.element).click();
            }
        });

        // Отрисовка данных
        _drawUiFromData(self);

        // Отмена редактирования
        $(".listeditor_cancel", self.editNode).click(function() {
            $("#editOverlayDiv").remove();
        });
        // Сохранение компонента
        $(".listeditor_save", self.editNode).click(function() {
            $("#editOverlayDiv").remove();
            if (self.rameraListEditor.id == null) {
                self.dataLoader.addListEditor(self.rameraListEditor, function(result){
                    alert("Компонент добавлен.");
                });
            } else {
                self.dataLoader.updateListEditor(self.rameraListEditor, function(result){
                    alert("Компонент обновлен.");
                });
            }
        });

        // Поиск по дереву
        $("#searchElements", self.editNode).on("input", function() {
            self.jqTree.tree('loadData', _getTreeData(self.rameraListEditor.items, $(this).val()));
            _searchStringInTree(self, $(this).val());
        });
        $("#searchElements", self.editNode).keydown(function( event ) {
            if (event.which == 27) {
                $(this).val("");
                self.jqTree.tree('loadData', _getTreeData(self.rameraListEditor.items));
            }
        });
    };

    function _searchStringInTree(self, searchString){
        $(".jqtree-title").each(function(){
            if (searchString != "") {
                $(this).html($(this).text().replace( new RegExp( "(" + searchString + ")" , 'gi' ), "<span class='foundTreeElement' style='background-color: red;'><i>$1</i></span>" ));
            } else {
                $(this).html($(this).text());
            }
        });
    }

    this.isActiveItem = function(item) {
        return item.isActive || this.isActiveFromParameters(item);
    };

    this.isActiveFromParameters = function(item) {
        var isActive = false;
        if (this.parameters != null && this.parameters.selectedItems != null) {
            for (var index in this.parameters.selectedItems) {
                var selectedId = this.parameters.selectedItems[index];
                if (item.id == selectedId) {
                    isActive = true;
                    break;
                }
            }
        }
        return isActive;
    };

    // Отрисока данных из объекта
    function _drawUiFromData(self) {
        $(".listeditor_id", self.editNode).text(self.rameraListEditor.id);
        //$(".fileUploadForm", self.editNode).attr("action", '/ramera_list_editor/uploadCsv?listId=' + self.rameraListEditor.id + '&location=' + document.location.href);
        // Кнопка загрузки файла csv
        $(".uploadFileButton", self.editNode).click(function(){
            $.radomUpload("file", "/ramera_list_editor/uploadCsv?listId=" + self.rameraListEditor.id, ["txt", "csv"], function(response) {
                response = JSON.parse(response);
                if (response.result == "true") {
                    bootbox.alert("Данные загружены.");
                    self.loadEditPage();
                } else {
                    bootbox.alert("Произошла ошибка при загрузке файла. Текст ошибки:<br/>" + response.error + "<br/>Стек ошибки:<br/>" + response.trace);
                }
            });
        });

        // Наивенование компонента
        $(".listeditor_name", self.editNode).val(self.rameraListEditor.name);
        $(".listeditor_name", self.editNode).on("input", function() {
            self.rameraListEditor.name = $(this).val();
        });

        // Наименование имени для html формы компонента
        $(".listeditor_formname", self.editNode).val(self.rameraListEditor.formName);
        $(".listeditor_formname", self.editNode).on("input", function() {
            self.rameraListEditor.formName = $(this).val();
        });

        // Тип компонента
        $(".listeditor_type", self.editNode).val(self.rameraListEditor.listEditorType);
        $(".listeditor_type", self.editNode).change(function(){
            self.rameraListEditor.listEditorType = $(this).val();
        });
    }

    // Отрисовать выбранный элемент
    function _drawItemFromData(self, node, parentNode, childNode) {
        var item = node.item;
        $(".listeditor_item_id", childNode).text(item.id==null?"":item.id);

        // Текст элемента
        $(".listeditor_item_text", childNode).val(item.text);
        $(".listeditor_item_text", childNode).on("input", function() {
            item.text = $(this).val();
            self.jqTree.tree('updateNode', node, item.text);
        });

        // Мнемокод элемента
        $(".listeditor_item_mnemo_code", childNode).val(item.mnemoCode);
        $(".listeditor_item_mnemo_code", childNode).on("input", function() {
            item.mnemoCode = $(this).val();
        });

        // Активность элемента по умолчанию
        $(".listeditor_item_active", childNode).prop("checked", item.isActive);
        $(".listeditor_item_active", childNode).click(function(){
            item.isActive = $(this).is(":checked");
        })

        // Вид отображения дочерних элементов
        $(".listeditor_item_type", childNode).val(item.listEditorItemType);
        $(".listeditor_item_type", childNode).change(function(){
            item.listEditorItemType = $(this).val();
        });

        // Значение элемента может быть выбранным
        $(".listeditor_item_selected", childNode).prop("checked", item.isSelectedItem);
        $(".listeditor_item_selected", childNode).click(function(){
            item.isSelectedItem = $(this).is(":checked")
        });

        parentNode.append(childNode);
        // Удаление элемента
        $(".delete_listeditor_item", childNode).click(function() {
            var itemNode = $(this).parent().parent();//.remove();
            var id = item.id;//$("> div > .listeditor_item_id", itemNode).text();
            if (id == null) {
                self.jqTree.tree('removeNode', node);
                itemNode.remove();
            } else {
                self.dataLoader.deleteListEditorItem(id, function() {
                    self.jqTree.tree('removeNode', node);
                    itemNode.remove();
                    alert("Элемент списка удален.");
                });
            }
        });
        // Добавление нового дочернего элемента
        $(".add_listeditor_item", childNode).click(function() {
            var selectedNode = self.jqTree.tree("getSelectedNode");
            var newItem = new RameraListEditorItem();
            newItem.listEditorItemType = selectedNode.item.listEditorItemType;
            newItem.text = 'new_node';
            var newNodeId = (Math.random() * 100000).toFixed(0);
            self.jqTree.tree(
                'appendNode',
                {
                    label: newItem.text,
                    id: newNodeId,
                    item : newItem
                },
                selectedNode
            );
            selectedNode.item.children.push(newItem);

            var newNode = self.jqTree.tree('getNodeById', newNodeId);
            if (newNode != null) {
                newNode.id = null;
                self.jqTree.tree('openNode', selectedNode);
                // Имитируем клик по новому элементу
                $(".jqtree-element", newNode.element).click();
                //self.jqTree.tree('selectNode', newNode);
            }
        });

        // Копирование элемента
        $(".clone_listeditor_item", childNode).click(function() {
            //var selectedNode = self.jqTree.tree("getSelectedNode");
            self.cloneJqTreeNode('addNodeAfter', node, node, false);
            // Синхронизируем элементы структуры
            var rootNodes = self.jqTree.tree("getTree").getData();
            self.rameraListEditor.items = _sycnModelFromTree(rootNodes);
        });

        // Копирование элемента рекурсивно
        $(".clone_listeditor_item_recursion", childNode).click(function() {
            var selectedNode = self.jqTree.tree("getSelectedNode");
            self.cloneJqTreeNode('addNodeAfter', node, node, true);
            // Синхронизируем элементы структуры
            var rootNodes = self.jqTree.tree("getTree").getData();
            self.rameraListEditor.items = _sycnModelFromTree(rootNodes);
        });
    }

    // Найти элемент по ИД и его дочерние элементы
    this.findItemsByParentItemId = function(id) {
        return _recursiveFindItemsByParentItemId(this, id, this.rameraListEditor.items);
    }

    function _recursiveFindItemsByParentItemId(self, id, items) {
        var result = null;
        for (var index in items) {
            var item = items[index];
            if (item.id == id) {
                result = {items: item.children, item: item};
                break;
            } else if (item.children != null && item.children.length > 0) {
                result = _recursiveFindItemsByParentItemId(self, id, item.children);
            }
        }
        return result;
    };

    // Получить ветку элементов по ИД конечного элемента
    this.getBranchForItem = function(id, items) {
        var result = [];
        if (items == null) {
            items = this.rameraListEditor.items;
        }
        for (var index in items) {
            var item = items[index];
            if (item.id == id) {
                result.push(id);
                break;
            } else {
                var child = item.children == null ? [] : item.children;
                result = this.getBranchForItem(id, child);
                if (result.length > 0) {
                    result.push(item.id);
                    break;
                }
            }
        }
        return result;
    };

    // Клонирование элемента
    this.cloneJqTreeNode = function(addNodeFunction, parentNode, nodeForClone, isRecursionClone) {
        var newItem = new RameraListEditorItem();
        newItem.text = nodeForClone.item.text;
        newItem.isActive = nodeForClone.item.isActive;
        newItem.isSelectedItem = nodeForClone.item.isSelectedItem;
        newItem.listEditorItemType = nodeForClone.item.listEditorItemType;
        newItem.order = nodeForClone.item.order;
        newItem.mnemoCode = nodeForClone.item.mnemoCode;

        var newNodeId = (Math.random() * 100000).toFixed(0);

        this.jqTree.tree(
            addNodeFunction,
            {
                label: newItem.text,
                id: newNodeId,
                item : newItem
            },
            parentNode
        );

        var newNode = this.jqTree.tree('getNodeById', newNodeId);

        if (nodeForClone.children != null && nodeForClone.children.length > 0 && isRecursionClone) {
            for (var i=0; i<nodeForClone.children.length; i++) {
                var cloneNode = nodeForClone.children[i];
                var clonedNode = this.cloneJqTreeNode('appendNode', newNode, cloneNode, isRecursionClone);
                newNode.item.children.push(clonedNode.item);
            }
        }
        return newNode;
        //jqTree
    }

    this.loadEditPage = function() {
        var self = this;
        self.dataLoader.loadListEditorWithDataByName(self.rameraListEditor.name, function(rameraListEditor){
            if (rameraListEditor.name == null || rameraListEditor.name == "") {
                rameraListEditor.name = self.rameraListEditor.name;
                rameraListEditor.formName = self.rameraListEditor.name;
                rameraListEditor.listEditorType = RameraListEditorType.COMBOBOX;
            }
            self.rameraListEditor = rameraListEditor;
            if (!RameraListEditorGlobalParameters.isTreeComponentLoaded) {
                $('head').append( $('<link rel="stylesheet" type="text/css" />').attr('href', '/css/jqtree.css') );
                $.getScript( "/js/tree.jquery.js", function( data, textStatus, jqxhr ) {
                    RameraListEditorGlobalParameters.isTreeComponentLoaded = true;
                    self.drawEdit();
                });
            } else {
                self.drawEdit();
            }
        });
    };

    this.draw = function(parentNode) {
        _sort(this.rameraListEditor.items);
        this.drawView(parentNode);
        var self = this;
        // TODO Проверка, что пользователь имеет права на редактирование
        if (this.dataLoader.sharerIsSuperAdmin) {
            parentNode = $(parentNode);
            var jqEditDiv = $("<div style='position: absolute; margin-top: -34px; right: -98px; z-index: 1300;'><input type='button' value='Редактировать' class='admin_edit_button'></div>");
            parentNode.bind("mouseover",function(){
                if ($(".admin_edit_button", parentNode).length == 0) {
                    $(".admin_edit_button", jqEditDiv).click(function() {
                        jqEditDiv.remove();
                        /*self.dataLoader.loadListEditorWithDataByName(self.rameraListEditor.name, function(rameraListEditor){
                            self.rameraListEditor = rameraListEditor;
                            if (!RameraListEditorGlobalParameters.isTreeComponentLoaded) {
                                $('head').append( $('<link rel="stylesheet" type="text/css" />').attr('href', '/css/jqtree.css') );
                                $.getScript( "/js/tree.jquery.js", function( data, textStatus, jqxhr ) {
                                    RameraListEditorGlobalParameters.isTreeComponentLoaded = true;
                                    self.drawEdit();
                                });
                            } else {
                                self.drawEdit();
                            }
                        });*/
                        self.loadEditPage();
                    });
                    parentNode.append(jqEditDiv);
                }
            }).bind("mouseout",function(){
                setTimeout(function(){
                    jqEditDiv.remove();
                }, 5000);
            });
        }
    };

    this.drawView = function(parentNode) {
        throw new Error("Call not implemented method!");
    };
}

// Представление комбобокса
RameraListEditorComboView = function (rameraListEditor, dataLoader, parameters, callBack) {
    RameraListEditorBase.apply(this, arguments);

    this.domNode = $(
        "<div>" +
            "<input type='hidden' class='listeditor_id' />" +
            "<div style='padding: 5px; background: gray; display: inline-block; margin: 5px; min-width: 150px;'>" +
                "<div class='combo_name'>New Combobox</div>" +
                //"<div class='listeditor_combo_node'>" +
                    //"<select class='combo_node' style='min-width: 100px;'></select>" +
                //"</div>" +
                "<input type='button' value='Редактировать' class='listeditor_combo_edit_button'>" +
                "<input type='button' value='Удалить' class='listeditor_combo_delete_button'>" +
            "</div>" +
        "</div>");

    this.comboNameNode = $(".combo_name", this.domNode);

    this.drawAdmin = function(parentNode) {
        var self = this;
        if (parentNode == null) {
            throw new Error("ParentNode is not defined!");
        }
        if (this.rameraListEditor == null) {
            throw new Error("RameraListEditor is not defined!");
        }
        $(parentNode).append(this.domNode);

        // Рисуем компонент
        self.drawListEditor();

        // Событие открытия окна с формой редактирования
        $(".listeditor_combo_edit_button", this.domNode).click(function(){
            self.drawEdit();
        });

        // Событие удаления комбобокса
        $(".listeditor_combo_delete_button", this.domNode).click(function(){
            if (confirm("Вы хотите удалить ComboBox?")) {
                dataLoader.deleteListEditor(self.rameraListEditor.id, function(result) {
                    $("input.listeditor_id", self.domNode).parent().remove();
                });
            }
        });
    }

    this.drawListEditor = function() {
        var self = this;
        $("input.listeditor_id", self.domNode).val(self.rameraListEditor.id);
        self.comboNameNode.text(self.rameraListEditor.name == "" ? self.comboNameNode.text() : self.rameraListEditor.name);
        /*if (self.rameraListEditor.items != null && self.rameraListEditor.items.length > 0) {
            var html = _recursionDraw(self, self.rameraListEditor.items);
            self.comboNode.append(html);
        } else {
            self.comboNode.append("<option disabled='disabled'>Список пуст</option>");
        }*/
    };

    /*function _recursionDraw(self, items) {
        var html = "";
        for (var index in items) {
            var item = items[index];
            var isActive = self.isActiveItem(item);
            if (item.children != null && item.children.length > 0) {
                html += "<optgroup label='" + item.text + "'>";
                html += _recursionDraw(self, item.children);
                html += "</optgroup>";
            } else {
                html += "<option value='" + item.id + "' " + (isActive?"selected='selected'" : "") + ">" + item.text + "</option>";
            }
        }
        return html;
    };*/

    function _drawOneLevel(self, items) {
        var html = "";
        var isWasActive = false;
        for (var index in items) {
            var item = items[index];
            var isActive = false;
            // Если передан массив с активными элементами, то делать активными только те, которые в массиве
            if (self.parameters.selectedItems != null) {
                isActive = self.isActiveFromParameters(item);
            } else {
                isActive = self.isActiveItem(item);
            }
            var tmpIsWasActive = isActive || isWasActive;
            if (isWasActive) {
                isActive = false;
            }
            isWasActive = tmpIsWasActive;

            html += "<option com_code='" + item.mnemoCode + "' value='" + item.id + "' " + (isActive?"selected='selected'" : "") + ">" + item.text + "</option>";
        }
        return html;
    }

    //----------------------------------------------------------------------------
    this.simpleTemplate = "<select data-live-search='true' ></select>";

    this.drawView = function(parentNode) {
        var self = this;
        var jqSelect = $(this.simpleTemplate);

        if (this.parameters != null && this.parameters.selectClasses != null) {
            jqSelect.attr("class", this.parameters.selectClasses.join(" "));
        }

        var parentId = $(parentNode).attr("parent_id");

        // Событие изменения значения списка
        jqSelect.change(function(event) {
            $("> .combobox_children", $(this).parent()).empty();
            $("> .combobox_children", $(this).parent()).remove();
            var selectedOption = $("option:selected", $(this));
            var value = selectedOption.val();
            var text = selectedOption.text();
            var code = selectedOption.attr("com_code");

            var selectedValue = value;
            //var parentId = $(this).attr("parent_id");
            // Если выбран элемент с пустым значением, то устанавливаем значение компонента от родителя
            if (parentId != null && parentId != "" && (value == null || value == "")) {
                selectedValue = parentId;
            }

            // Если нет input hidden элемента со значением выбранного списка, то создаем
            if ($(".select_value_" + self.rameraListEditor.formName, parentNode).length == 0) {
                $(parentNode).append("<input type='hidden' name='" + self.rameraListEditor.formName + "' class='select_value_" + self.rameraListEditor.formName + "' />");
            }

            // Устанавливаем значение input hidden значение
            $(".select_value_" + self.rameraListEditor.formName, parentNode).val(selectedValue);

            // Если выбран элемент со значением
            if (value != null) {
                // Ищем вложенные элементы
                var findObject = self.findItemsByParentItemId(value);
                if (findObject != null && findObject.items != null && findObject.items.length > 0) {
                    // Если элементы найдены, то удаляем input hidden поле со значенем, чтобы значения устанавливались у дочерних элементов
                    $(".select_value_" + self.rameraListEditor.formName, parentNode).remove();

                    // Создаем ноду для дочернего списка
                    if (self.parameters.childStyle){
                        $(parentNode).append("<div style='"+self.parameters.childStyle+"' class='combobox_children combobox_child_" + findObject.item.id + "'></div>");
                    }
                    else {
                        $(parentNode).append("<div class='combobox_children combobox_child_" + findObject.item.id + "'></div>");
                    }
                    var childItemsParentNode = $(".combobox_child_" + findObject.item.id, $(parentNode));

                    // Если текущий элемент является элементом с возможностью выбора значения при наличии дочерних элементов, то устанавливаем аттрибут
                    if (findObject.item.isSelectedItem) {
                        $(childItemsParentNode).attr("parent_id", findObject.item.id);
                    }

                    // Создаем дочерний список
                    var view = RameraListEditorFactory.createNewListEditorView(self.rameraListEditor, findObject.item, self);
                    view.drawView(childItemsParentNode);
                } else {
                    onChangeListEditorItem(self.callBack, selectedValue, text, code);
                }
            }
        });

        // --------------------
        // Отрисовка элементов списка
        // --------------------
        if (self.rameraListEditor.items != null && self.rameraListEditor.items.length > 0) {
            var html = "";
            if (this.parameters != null) {
                // Если есть пустое значение
                // Для элементов 2го и следующих уровней selectEmptyValue устанавливается на основе поля isSelectedItem
                if (this.parameters.selectEmptyValue != null){
                    if(this.parameters.disableEmptyValue != undefined && this.parameters.disableEmptyValue) {
                        html += "<option disabled='disabled' value=''>" + this.parameters.selectEmptyValue + "</option>";
                    } else {
                        html += "<option value=''>" + this.parameters.selectEmptyValue + "</option>";
                    }
                }
                if (this.parameters.selectId != null){
                    jqSelect.attr("id", this.parameters.selectId);
                }
            }
            html += _drawOneLevel(self, self.rameraListEditor.items);
            jqSelect.append(html);
        }
        // --------------------
        // Добавляем в родительскую ноду селект
        parentNode.append(jqSelect);
        jqSelect.selectpicker("refresh");
        //$("#" + control).selectpicker("val", null);

        var selectedOptionValue = $("option:selected", jqSelect).val();

        // имя формы поле formname, потому как необходимо перегружать компоненты
        var isValueSelected = false;
        if ($(".select_value_" + self.rameraListEditor.formName, parentNode).length == 0) {
            $(parentNode).append("<input type='hidden' name='" + self.rameraListEditor.formName + "' class='select_value_" + self.rameraListEditor.formName + "' value='" + selectedOptionValue + "' />");

            // Если передано значение выбранного элемента, то иищем все промежуточные элементы и раскрываем списки
            if (self.parameters != null && self.parameters.selectedItems != null) {
                var selectedItem = self.parameters.selectedItems[0];
                // Ищем ветку с выбранным элементом
                var brancheIds = self.getBranchForItem(selectedItem);
                if (brancheIds != null && brancheIds.length > 0) {
                    for (var i=brancheIds.length - 1; i>-1; i--) {
                        // Устанавливаем значение в список и вызываем событие изменения значения
                        var selectedId = brancheIds[i];
                        // Если наш селект есть в в ветке
                        if ($("option[value='" + selectedId + "']", jqSelect).length > 0) {
                            jqSelect.val(selectedId);
                            jqSelect.trigger("change");
                            //jqSelect = $("[parent_id='" + selectedId + "']");
                            isValueSelected = true;
                            break;
                        }
                    }
                }
            }
        }
        self.callBack(RameraListEditorEvents.CREATED, {domNode : self.domNode});

        // Если есть выбранный элемент с непустым значением и значение в селекте не выбрано из ветки
        if (selectedOptionValue != null && selectedOptionValue != "" && !isValueSelected) {
            jqSelect.trigger("change");
            jqSelect.selectpicker("val", selectedOptionValue);
        } else if (parentId != null && parentId != "" && !isValueSelected) { // Если есть родительский элемент, то устанавливаем значение от него
            $(".select_value_" + self.rameraListEditor.formName, parentNode).val(parentId);
            var optionNode = $("option[value=" + parentId + "]", $(self.rameraListEditor.node));
            onChangeListEditorItem(self.callBack, parentId, optionNode.text(), optionNode.attr("com_code"));
        }

    }
};

// Представление чекбокса
RameraListEditorCheckBoxView = function (rameraListEditor, dataLoader, parameters, callBack) {
    RameraListEditorBase.apply(this, arguments);

    this.domNode = $(
        "<div>" +
        "<input type='hidden' class='listeditor_id' />" +
        "<div style='padding: 5px; background: gray; display: inline-block; margin: 5px; min-width: 150px;'>" +
        "<div class='listeditor_name'>New CheckBox</div>" +
        "<input type='button' value='Редактировать' class='listeditor_edit_button'>" +
        "<input type='button' value='Удалить' class='listeditor_delete_button'>" +
        "</div>" +
        "</div>");

    this.nameNode = $(".listeditor_name", this.domNode);

    this.elementNode = null;

    //this.comboNode = $(".combo_node", this.domNode);
    //this.buttonEditNode = $("input", this.domNode);

    this.drawAdmin = function(parentNode) {
        var self = this;
        if (parentNode == null) {
            throw new Error("ParentNode is not defined!");
        }
        if (this.rameraListEditor == null) {
            throw new Error("RameraListEditor is not defined!");
        }
        $(parentNode).append(this.domNode);

        // Рисуем компонент
        self.drawListeditor();

        // Событие открытия окна с формой редактирования
        $(".listeditor_edit_button", this.domNode).click(function(){
            self.drawEdit();
        });

        // Событие удаления компонента
        $(".listeditor_delete_button", this.domNode).click(function(){
            if (confirm("Вы хотите удалить компонент?")) {
                dataLoader.deleteListEditor(self.rameraListEditor.id, function(result) {
                    $("input.listeditor_id", self.domNode).parent().remove();
                });
            }
        });
    }

    this.drawListeditor = function() {
        var self = this;
        $("input.listeditor_id", self.domNode).val(self.rameraListEditor.id);
        var name = self.rameraListEditor.name == "" ? "New listeditor" : self.rameraListEditor.name;
        self.nameNode.text(name);
        /*if (self.rameraListEditor.items != null && self.rameraListEditor.items.length > 0) {
            var html = _recursionDraw(self, self.rameraListEditor.items);
            self.comboNode.append(html);
        } else {
            self.comboNode.append("<option disabled='disabled'>Список пуст</option>");
        }*/
    };

    function _recursionDraw(self, items) {
        var html = "<div class='checkbox_group_container'>";
        for (var index in items) {
            var item = items[index];
            /*if (item.children != null && item.children.length > 0) {
                html += "<div><div>" + item.text + "</div>";
                html += _recursionDraw(self, item.children);
                html += "</div>";
            } else {*/
                var labelAttributes = "";
                var inputAttributes = "";
                if (self.parameters.labelClasses != null && self.parameters.labelClasses.length > 0) {
                    var classNames = self.parameters.labelClasses.join(" ");
                    labelAttributes = "class='" + classNames + "'";
                }
                if (self.parameters.labelStyle != null) {
                    labelAttributes += "style='" + self.parameters.labelStyle  + "'";
                }
                if (self.parameters.inputClasses != null && self.parameters.inputClasses.length > 0) {
                    var classNames = self.parameters.inputClasses.join(" ");
                    inputAttributes = "class='" + classNames + "'";
                }
                var isActive = false;
                // Если есть выбранные элементы в параметрах то активными будут только они
                if (self.parameters.selectedItems != null) {
                    isActive = self.isActiveFromParameters(item);
                } else {
                    isActive = self.isActiveItem(item);
                }

                // имя формы поле formname, потому как необходимо перегружать компоненты
                html += "<label " + labelAttributes + " for='" + self.rameraListEditor.name + "_" + item.id + "'>" +
                            "<input " + inputAttributes + " id='" + self.rameraListEditor.name + "_" + item.id + "' com_code='" + item.mnemoCode + "' comp_text='" + item.text + "' type='checkbox' name='" + self.rameraListEditor.formName + "' value='" + item.id + "' " + (isActive?"checked='checked'" : "") + "/>&nbsp;" +
                            item.text +
                        "</label>";
            //}
        }
        html += "</div>";
        return html;
    };

    //----------------------------------------------------------------------------

    // Обработка элементов для установки текущего выбранного значения формы
    function _handleFormValue(self, parentNode, parentId) {
        // Если не выбран ни один элемент, то для формы устанавливаем значение родительского элемента
        var jqSelectedInputs = $("input:checked", self.elementNode);
        if (jqSelectedInputs.length == 0 && parentId != null && parentId != "") {
            // Обнуляем имена для формы у input'ов
            $("input", self.elementNode).attr("name", "");
            parentNode.append("<input type='hidden' class='parent_" + parentId + "' name='" + self.rameraListEditor.formName + "' value='" + parentId + "'/>");
        } else { // Иначе удаляем hidden поле
            $("input", self.elementNode).attr("name", self.rameraListEditor.formName);
            $(".parent_" + parentId).remove();
        }
    };

    this.drawView = function(parentNode) {
        var self = this;
        //self.comboNameNode.text(self.rameraListEditor.name == "" ? self.comboNameNode.text() : self.rameraListEditor.name);
        if (self.rameraListEditor.items != null && self.rameraListEditor.items.length > 0) {
            self.elementNode = $(_recursionDraw(self, self.rameraListEditor.items));
            parentNode.append(self.elementNode);
        }

        // ИД родительского элемента
        var parentId = parentNode.attr("parent_id");

        $("input", self.elementNode).click(function(event) {
            /*$("> .checkbox_children", $(this).parent().parent().parent()).empty();
            $("> .checkbox_children", $(this).parent().parent().parent()).remove();*/

            var value = null;
            var text = null;
            var code = null;

            var isChecked = $(this).is(":checked");
            if (isChecked) {
                value = $(this).val();
                text = $(this).attr("comp_text");
                code = $(this).attr("comp_code");
            }

            // Устанавливаем значение для формы
            _handleFormValue(self, parentNode, parentId);

            /*var findObject = self.findItemsByParentItemId(value);
            if (findObject != null && findObject.items != null && findObject.items.length > 0) {
                // Создаем дочерний список
                var view = RameraListEditorFactory.createNewListEditorView(self.rameraListEditor, findObject.item, self);
                $(parentNode).append("<div class='checkbox_children checkbox_child_" + findObject.item.id + "'></div>");
                var childItemsParentNode = $(".checkbox_child_" + findObject.item.id, $(parentNode));
                view.drawView(childItemsParentNode);
            }*/

            onChangeListEditorItem(self.callBack, value, text, code);
        });
        self.callBack(RameraListEditorEvents.CREATED, {domNode : self.domNode});

        // Устанавливаем значение для формы
        _handleFormValue(self, parentNode, parentId);
    }
};

// Представление радиобаттона
RameraListEditorRadioButtonView = function (rameraListEditor, dataLoader, parameters, callBack) {
    RameraListEditorCheckBoxView.apply(this, arguments);

    // ИД элемента на основе случайного числа
    this.randomElementId = Math.random().toString().replace(".", "");

    this.elementNode = null;

    function _recursionDraw(self, items) {
        var html = "<div class='radio_group_container' id='" + self.randomElementId + "'>";
        var isWasActive = false;
        for (var index in items) {
            var item = items[index];
            /*if (item.children != null && item.children.length > 0) {
                html += "<div><div>" + item.text + "</div>";
                html += _recursionDraw(self, item.children);
                html += "</div>";
            } else {*/
                var labelAttributes = "";
                var inputAttributes = "";
                if (self.parameters.labelClasses != null && self.parameters.labelClasses.length > 0) {
                    var classNames = self.parameters.labelClasses.join(" ");
                    labelAttributes = "class='" + classNames + "'";
                }
                if (self.parameters.labelStyle != null) {
                    labelAttributes += "style='" + self.parameters.labelStyle  + "'";
                }
                if (self.parameters.inputClasses != null && self.parameters.inputClasses.length > 0) {
                    var classNames = self.parameters.inputClasses.join(" ");
                    inputAttributes = "class='" + classNames + "'";
                }
                var isActive = false;
                // Если есть выбранные элементы в параметрах то активными будут только они
                if (self.parameters.selectedItems != null) {
                    isActive = self.isActiveFromParameters(item);
                } else {
                    isActive = self.isActiveItem(item);
                }
                var tmpIsWasActive = isActive || isWasActive;
                if (isWasActive) {
                    isActive = false;
                }
                // Должен быть выбран только 1
                isWasActive = tmpIsWasActive;

                // имя формы поле formname, потому как необходимо перегружать компоненты
                html += "<label " + labelAttributes + " for='" + self.randomElementId + "_" + item.id + "'>" +
                            "<input " + inputAttributes + " id='" + self.randomElementId + "_" + item.id + "' com_code='" + item.mnemoCode + "' comp_text='" + item.text + "' type='radio' name='" + self.rameraListEditor.formName + "' value='" + item.id + "' " + (isActive?"checked='checked'" : "") + "/>&nbsp;" +
                            item.text +
                        "</label>";
            //}
        }
        html += "</div>";
        return html;
    };

    //----------------------------------------------------------------------------

    // Обработка элементов для установки текущего выбранного значения формы
    function _handleFormValue(self, parentNode, parentId) {
        // Если не выбран ни один элемент, то для формы устанавливаем значение родительского элемента
        var jqSelectedInputs = $("input:checked", self.elementNode);
        if (jqSelectedInputs.length == 0 && parentId != null && parentId != "") {
            // Обнуляем имена для формы у input'ов
            $("input", self.elementNode).attr("name", self.randomElementId);
            parentNode.append("<input type='hidden' class='parent_" + parentId + "' name='" + self.rameraListEditor.formName + "' value='" + parentId + "'/>");
        } else { // Иначе удаляем hidden поле
            $("input", self.elementNode).attr("name", self.rameraListEditor.formName);
            $(".parent_" + parentId).remove();
        }
    };

    this.drawView = function(parentNode) {
        var self = this;
        //self.comboNameNode.text(self.rameraListEditor.name == "" ? self.comboNameNode.text() : self.rameraListEditor.name);
        if (self.rameraListEditor.items != null && self.rameraListEditor.items.length > 0) {
            self.elementNode = $(_recursionDraw(self, self.rameraListEditor.items));
            parentNode.append(self.elementNode);
        }

        // ИД родительского элемента
        var parentId = parentNode.attr("parent_id");

        $("input", self.elementNode).click(function(event) {
            $("> .radio_children", $(this).parent().parent().parent()).empty();
            $("> .radio_children", $(this).parent().parent().parent()).remove();
            var value = null;
            var text = null;
            var code = null;

            var isChecked = $(this).is(":checked");
            if (isChecked) {
                value = $(this).val();
                text = $(this).attr("comp_text");
                code = $(this).attr("comp_code");
            }

            //
            _handleFormValue(self, parentNode, parentId);

            // Ищем дочерние элементы и подгружаем их на страницу
            if (value != null) {
                var findObject = self.findItemsByParentItemId(value);
                if (findObject != null && findObject.items != null && findObject.items.length > 0) {

                    // Меняем имена у радиобаттонов, чтобы дочерние элементы быили с этим именем
                    //$("input", self.elementNode).attr("name", self.randomElementId);
                    $("input", self.elementNode).attr("name", self.randomElementId);

                    // Создаем ноду для дочернего списка
                    $(parentNode).append("<div class='radio_children radio_child_" + findObject.item.id + "'></div>");
                    var childItemsParentNode = $(".radio_child_" + findObject.item.id, parentNode);

                    // Если текущий элемент является элементом с возможностью выбора значения при наличии дочерних элементов, то устанавливаем аттрибут
                    if (findObject.item.isSelectedItem) {
                        $(childItemsParentNode).attr("parent_id", findObject.item.id);
                    }

                    // Создаем дочерний список
                    var view = RameraListEditorFactory.createNewListEditorView(self.rameraListEditor, findObject.item, self);
                    view.drawView(childItemsParentNode);
                } else {
                    onChangeListEditorItem(self.callBack, value, text, code);
                }
                /* else {
                    // Обратно возвращаем имена у радиобаттонов
                    $("input", self.elementNode).attr("name", self.rameraListEditor.formName);
                }*/
            }

        });

        // Если передано значение выбранного элемента, то иищем все промежуточные элементы и раскрываем списки
        if (self.parameters != null && self.parameters.selectedItems != null) {
            var selectedItem = self.parameters.selectedItems[0];
            // ИД-ры ветки
            var brancheIds = self.getBranchForItem(selectedItem);
            if (brancheIds != null && brancheIds.length > 0) {
                for (var i=brancheIds.length - 1; i>-1; i--) {
                    // Устанавливаем значение в список и вызываем событие изменения значения
                    var selectedId = brancheIds[i];

                    // Ищем элемент по ИД
                    var selectedJqRadio =$("#" + self.randomElementId + "_" + selectedId);
                    if (selectedJqRadio.length > 0) {
                        selectedJqRadio.prop("checked", true);
                        break;
                    }
                }
            }
        }

        self.callBack(RameraListEditorEvents.CREATED, {domNode : self.domNode});

        // если есть выбранные элементы, то имитируем клик по ним
        var jqRadioButtonSelected = $("input:checked", self.elementNode);
        if (jqRadioButtonSelected.length > 0) {
            jqRadioButtonSelected.click();
        } else {
            _handleFormValue(self, parentNode, parentId);
        }
    };
}

RameraListEditorAdmin = {
    domNode : null,
    viewItems : [],

    url : "/ramera_list_editor/handle.json",

    template : $(
        "<div>" +
        "<div id='listeditor_container'></div>" +
        "<select class='listeditor_type'>" +
        "<option value='COMBOBOX'>ComboBox</option>" +
        "<option value='CHECKBOX'>CheckBox</option>" +
        "<option value='RADIOBUTTON'>RadioButton</option>" +
        "<input type='button' class='listeditor_add' value='Добавить'/>" +
        "</div>"),
    listeditorContainerNode : null,

    init : function(parentNode, url) {
        var self = this;
        if (url != null) {
            this.url = url;
        }
        if (parentNode == null) {
            throw new Error("ParentNode is not defined!");
        }
        this.listeditorContainerNode = $("#listeditor_container", this.template);
        RameraListEditorDataLoader.init(this.url);
        this.domNode = $(parentNode);
        this.domNode.append(this.template);

        // Добавление нового компонента
        $(".listeditor_add", this.domNode).click(function() {
            var rameraListEditorType = $(".listeditor_type", self.domNode).val();
            var view = null;
            switch(rameraListEditorType) {
                case RameraListEditorType.COMBOBOX:
                    var rameraListEditor = new RameraListEditor();
                    rameraListEditor.listEditorType = RameraListEditorType.COMBOBOX;
                    view = new RameraListEditorComboView(rameraListEditor, RameraListEditorDataLoader);
                    break;
                case RameraListEditorType.CHECKBOX:
                    var rameraListEditor = new RameraListEditor();
                    rameraListEditor.listEditorType = RameraListEditorType.CHECKBOX;
                    view = new RameraListEditorCheckBoxView(rameraListEditor, RameraListEditorDataLoader);
                    break;
                case RameraListEditorType.RADIOBUTTON:
                    var rameraListEditor = new RameraListEditor();
                    rameraListEditor.listEditorType = RameraListEditorType.RADIOBUTTON;
                    view = new RameraListEditorRadioButtonView(rameraListEditor, RameraListEditorDataLoader);
                    break;
            }
            view.drawAdmin(self.listeditorContainerNode);
            self.viewItems.push(view);
        });

        // Загружаем все компоненты из БД
        RameraListEditorDataLoader.loadAllListEditors(function(rameraListEditors) {
            if (rameraListEditors != null && rameraListEditors.length > 0) {
                for (var index in rameraListEditors) {
                    var rameraListEditor = rameraListEditors[index];
                    var view = null;
                    switch (rameraListEditor.listEditorType) {
                        case RameraListEditorType.COMBOBOX:
                            view = new RameraListEditorComboView(rameraListEditor, RameraListEditorDataLoader);
                            break;
                        case RameraListEditorType.CHECKBOX:
                            view = new RameraListEditorCheckBoxView(rameraListEditor, RameraListEditorDataLoader);
                            break;
                        case RameraListEditorType.RADIOBUTTON:
                            view = new RameraListEditorRadioButtonView(rameraListEditor, RameraListEditorDataLoader);
                            break;
                    }
                    if (view == null) {
                        throw new Error("Unknow listeditor type!");
                    }
                    view.drawAdmin(self.listeditorContainerNode);
                    self.viewItems.push(view);
                }
            }
        });
    }
};

RameraListEditorModule = {

    ATTRIBUTE_NAME : "rameraListEditorName",

    dataLoader : RameraListEditorDataLoader,

    listeditors : [],

    // Инициализация компонентов со списками на странице
    init : function(jqNodes, parameters, callBack) {
        var self = this;
        jqNodes.each(function(i){
            var parentNode = $(this);
            var listEditorName = $(this).attr(self.ATTRIBUTE_NAME);
            self.dataLoader.loadListEditorWithDataByName(listEditorName, function(rameraListEditor){
                if (rameraListEditor == null) {
                    return;
                }
                var view = null;
                var listEditorType = rameraListEditor.listEditorType == null || rameraListEditor.listEditorType == "" ? RameraListEditorType.COMBOBOX : rameraListEditor.listEditorType;
                rameraListEditor.name = listEditorName;
                rameraListEditor.node = parentNode;
                if (parameters == null) {
                    parameters = {};
                }
                if (parameters.forceViewType != null) {
                    listEditorType = parameters.forceViewType;
                }
                switch (listEditorType) {
                    case RameraListEditorType.COMBOBOX:
                        // Если значение для первого элемента не передано, то оно "Выберите из списка"
                        if (parameters.selectEmptyValue == null) {
                            parameters.selectEmptyValue = "-- Выберите из списка --";
                        }
                        view = new RameraListEditorComboView(rameraListEditor, self.dataLoader, parameters, callBack);
                        break;
                    case RameraListEditorType.CHECKBOX:
                        view = new RameraListEditorCheckBoxView(rameraListEditor, self.dataLoader, parameters, callBack);
                        break;
                    case RameraListEditorType.RADIOBUTTON:
                        view = new RameraListEditorRadioButtonView(rameraListEditor, self.dataLoader, parameters, callBack);
                        break;
                }
                if (view == null) {
                    throw new Error("Unknow listEditor type!");
                }
                view.draw(parentNode);
            }, function(error) { // Делаем обработку не найденных списков
                // Создание списка по обращению админа
                var rameraListEditor = new RameraListEditor();
                rameraListEditor.listEditorType = RameraListEditorType.COMBOBOX;
                rameraListEditor.name = listEditorName;
                rameraListEditor.formName = listEditorName;
                var view = new RameraListEditorComboView(rameraListEditor, RameraListEditorDataLoader, parameters, callBack);
                view.draw(parentNode);
            });
        });
    },

    // Функция для получения путей из элементов списка до элементов ИД-ры которых переданы в виде параметра
    getItemsPath : function() {

    }
}