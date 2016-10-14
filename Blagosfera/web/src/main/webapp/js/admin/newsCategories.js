
//Загружаем дерево категорий новостей
$(document).ready(function() {

    var treeView;
    var treeModel;
    var selectedCategoryGridItem = null;
    var $editModalHolder;


    $.radomJsonGet(
        //url
        "/admin/news/categories/tree.json",
        //data
        null,
        //callback
        function (response) {

            if (response.result && response.result == "error") {
                bootbox.alert(response.message);
                return;
            }

            var tree = response;

            //Дерево пусто, выводим сообщение
            if (!tree.children.length) {
                $('#alert-area').append('<div class="alert alert-warning">  ' +
                    '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>' +
                    '<strong>Категории новостей отсутствуют</strong></div>');
            }

            //инициализируем  дерево категорий (ответ от сервера и есть дерево)
            initTree(response);
            initEditModal();
        });


    function initEditModal() {
        $editModalHolder = $('#editNewsCategoryModal');

        $('#add-news-category-button').click(function () {
            var parentId = null;

            if (selectedCategoryGridItem && selectedCategoryGridItem.data && selectedCategoryGridItem.data.id) {
                parentId = selectedCategoryGridItem.data.id;
            }

            $("#field-parent-id").val(parentId);
            $('#save-news-category-button').text("Добавить");
            $editModalHolder.modal('show');
        });

        $editModalHolder.on("hidden.bs.modal", function () {
            $editModalHolder.find('input, textarea').val('');
            $editModalHolder.find('#delete-news-category-button').css('display', 'none');
        });

        //Обработчик кнопки "сохранить" модального окна
        $('#save-news-category-button').click(function() {
            var data = $('#editNewsCategoryForm').serialize();
            setControlElementsDisabled(true);

            trimFormData();

            if (!validateEditNewsCategoryForm()) {
                setControlElementsDisabled(false);
                return false;
            }

            if ($('#field-id').val()) {
                //Существующие узлы редактируются
                sendUpdateRequest(data);
            } else {
                //Новые создаются
                sendCreateRequest(data);
            }
        });

        //Обработчик кнопки "удалить" модального окна
        $('#delete-news-category-button').click(function() {
            setControlElementsDisabled(true);

            var id = selectedCategoryGridItem.data.id;
            if (selectedCategoryGridItem.data.children.length) {
                bootbox.confirm(
                    //message
                    "Удаление данной категории приведет к потере всех ее дочерних категорий. " +
                    "Вы действительно хотите продолжить?",
                    //callback
                    function (result) {
                        if (result) {
                            sendDeleteRequest(id);
                        }
                        setControlElementsDisabled(false);
                    }
                );
            } else {
                sendDeleteRequest(id);
            }
        });

    };


    function sendDeleteRequest(id) {
        $.radomJsonPost(
            //url
            "/admin/news/categories/delete.json",
            //data
            {id : id},
            //callback
            function(response) {
                if (response.result) {

                    if (response.result == "error") {
                        if (response.message) {
                            bootbox.alert(response.message);

                        } else {
                            console.log("ajax response error");
                        }

                        setControlElementsDisabled(false);
                        return;
                    } else if (response.result = "success") {
                        deleteSelectedGridItem();
                        setControlElementsDisabled(false);
                        $editModalHolder.modal("hide");
                    } else {
                        bootbox.alert("Произошла ошибка!");
                        setControlElementsDisabled(false);
                    }
                } else {
                    bootbox.alert("Произошла ошибка!");
                    setControlElementsDisabled(false);
                }
            },
            //errorCallback
            function(response) {
                if (response && response.message) {
                    bootbox.alert(response.message);
                } else {
                    console.log("ajax response error");
                }
                setControlElementsDisabled(false);
            }
        );

    };


    function sendCreateRequest(data) {
        $.radomJsonPost(
            //url
            "/admin/news/categories/create.json",
            //data
            data,
            //callback
            function(response) {
                if (response.result && response.result == "error") {

                    if (response.message) {
                        bootbox.alert(response.message);

                    } else {
                        console.log("ajax response error");
                    }

                    setControlElementsDisabled(false);
                    return;
                }

                appendNewCategory(response);
                setControlElementsDisabled(false);
                $editModalHolder.modal("hide");
            },
            //errorCallback
            function(response) {
                if (response && response.message) {
                    bootbox.alert(response.message);
                } else {
                    console.log("ajax response error");
                }
                setControlElementsDisabled(false);
            }
        );
    };

    function sendUpdateRequest(data) {
        $.radomJsonPost(
            //url
            "/admin/news/categories/updateData.json",
            //data
            data,
            //callback
            function(response) {
                if (response.result && response.result == "error") {

                    if (response.message) {
                        bootbox.alert(response.message);

                    } else {
                        console.log("ajax response error");
                    }

                    setControlElementsDisabled(false);
                    return;
                }

                updateSelectedGridItem(response);
                setControlElementsDisabled(false);
                $editModalHolder.modal("hide");
            },
            //errorCallback
            function(response) {
                if (response && response.message) {
                    bootbox.alert(response.message);
                } else {
                    console.log("ajax response error");
                }
                setControlElementsDisabled(false);
            }
        );
    };

    function updateSelectedGridItem(response) {
        selectedCategoryGridItem.data.text = response.text;
        selectedCategoryGridItem.data.description = response.description;
        selectedCategoryGridItem.data.key = response.key;
        treeView.refresh();
    };

    function deleteSelectedGridItem() {
        selectedCategoryGridItem.remove();
        treeView.refresh();
    };



    function validateEditNewsCategoryForm() {
        var $title = $('#field-title');
        var $description = $('#field-description');
        var $key = $('#field-key');

        //Валидация не пустые значения
        if (!validateEmpty($title) || !validateEmpty($key)) {
            return false;
        }

        //Валидация на превышение длины
        if (!validateFieldsLength($title, 200) || !validateFieldsLength($description, 1000) || !validateFieldsLength($key, 200)) {
            return false;
        }

        return true;
    };


    function validateFieldsLength($field, length) {
        if ($field.val().length > length) {
            bootbox.alert($field.attr('placeholder') + ' не может превышать длину в ' + length + ' символов');
            return false;
        }

        return true;
    };

    function validateEmpty($field) {
        if ($field.val().trim().length == 0) {
            bootbox.alert('Введите ' + $field.attr('placeholder'));
            return false;
        }

        return true;
    };

    function setControlElementsDisabled(disabled) {

        //Выкл/вкл элементы ввода и управления внутри modal'а
        $('#editNewsCategoryForm').find('input, textarea, button').attr('disabled', disabled);

        //Запретить/разрешить скрывать modal
        if (disabled) {
            $editModalHolder.on('hide.bs.modal', function (e) {
                e.preventDefault();
            });
        } else {
            $editModalHolder.off('hide.bs.modal');
        }

        //Выкл/вкл кнопку создания категории
        $('#add-news-category-button').attr('disabled', disabled);

        //Выкл/вкл дерево
        if (disabled) {
            treeView.disable();
        } else {
            treeView.enable();
        }
    };


    /**
     * Функция инициализации дерева
     * @param tree
     */
    function initTree(tree) {

        //Ссылка на старого родителя перемещаемого узла
        var beforeDropParentNode = null;
        //Ссылка на старого следующего соседа перемещаемого узла
        var beforeDropNextSiblingNode = null;

        Ext.onReady(function() {

            treeModel = Ext.create('Ext.tree.Panel', {
                id : 'newsCategoriesGrid',
                title : 'Категории новостей',
                useArrows : true,
                rootVisible : false,
                multiSelect : false,
                singleExpand : false,
                root: tree,
                fields: ['id', 'title', 'description', 'key', 'position'],
                renderTo: 'news-categories-grid',
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop'
                    },
                    listeners: {
                        beforedrop: function(node, data, overModel, dropPosition, dropHandlers) {
                            //Приостанавливаем обработку события drop
                            dropHandlers.wait = true;
                            //Запоминаем исходное положение узла
                            beforeDropParentNode = data.records[0].parentNode;
                            beforeDropNextSiblingNode = data.records[0].nextSibling;
                            //Обрабатываем событие drop
                            dropHandlers.processDrop();
                        },
                        drop: function (node, data, overModel, dropPosition, eOpts) {
                            setControlElementsDisabled(true);

                            //Получаем идентификаторы перемещаемого узла, нового родителя и нового следующего соседа
                            var id = data.records[0].data.id;

                            var parentId = null;

                            if ( data.records[0].parentNode != treeModel.getRootNode()) {
                                parentId = data.records[0].parentNode.data.id;
                            }

                            var nextSiblingId = null;

                            if (data.records[0].nextSibling) {
                                nextSiblingId = data.records[0].nextSibling.data.id;
                            }

                            //Отправляем запрос на обработку
                            changeHierarchyRequest(id, parentId, nextSiblingId);
                        }
                    }
                },
                listeners: {
                    viewready: function (tree) {
                        treeView = tree.getView();
                    },
                    select: function (dataview, record, index, eOpts) {
                        setSelectedCategoryGridItem(record);
                        console.log(record.data.text);
                    },
                    itemdblclick: function(dataview, record) {
                        treeModel.getSelectionModel().select(record);
                        $('#field-id').val(record.data.id);
                        $('#field-title').val(record.data.text);
                        $('#field-description').val(record.data.description);
                        $('#field-key').val(record.data.key);
                        $editModalHolder.find('#delete-news-category-button').css('display', '');
                        $('#save-news-category-button').text("Сохранить");
                        $editModalHolder.modal('show');
                    },
                    itemexpand: function(node, eOpts ) {
                        treeModel.getSelectionModel().select(node);
                    },
                    itemcollapse: function(node, eOpts ) {
                        treeModel.getSelectionModel().select(node);
                    }
                }
            });
        });

        function changeHierarchyRequest(id, parentId, nextSiblingId) {
            $.radomJsonPost(
                //url
                "/admin/news/categories/changeHierarchy.json",
                //data
                {
                    id : id,
                    parent_id : parentId,
                    next_sibling_id : nextSiblingId
                },
                //callback
                function(response) {
                    if (response.result) {

                        if (response.result == "error") {
                            if (response.message) {
                                bootbox.alert(response.message);
                            } else {
                                console.log("ajax response error");
                            }

                            cancelDrop();
                            setControlElementsDisabled(false);
                            return;
                        } else if (response.result = "success") {
                            beforeDropParentNode = null;
                            beforeDropNextSiblingNode = null;
                            setControlElementsDisabled(false);
                        } else {
                            bootbox.alert("Произошла ошибка!");
                            cancelDrop();
                            setControlElementsDisabled(false);
                        }
                    } else {
                        bootbox.alert("Произошла ошибка!");
                        cancelDrop();
                        setControlElementsDisabled(false);
                    }
                },
                //errorCallback
                function(response) {
                    if (response && response.message) {
                        bootbox.alert(response.message);
                    } else {
                        bootbox.alert("Произошла ошибка!");
                    }
                    cancelDrop();
                    setControlElementsDisabled(false);
                }
            );
        };


        /**
         * Позволяет отменить перемещение элемента. Используется при возникновении ошибок
         */
        function cancelDrop() {
            if (!beforeDropNextSiblingNode) {
                beforeDropParentNode.appendChild(selectedCategoryGridItem);
            } else {
                beforeDropParentNode.insertBefore(selectedCategoryGridItem, beforeDropNextSiblingNode);
            }

            treeView.refresh();
        }

    };

    // Установить идентификатор узла, выбранный в таблице
    function setSelectedCategoryGridItem(record) {
        selectedCategoryGridItem = record;
    };

    function appendNewCategory(node) {
        if (selectedCategoryGridItem == null) {
            selectedCategoryGridItem = treeModel.getRootNode()
        }

        selectedCategoryGridItem.appendChild(node);
        selectedCategoryGridItem.expand();
        treeView.refresh();
    }


    function trimFormData() {
        $('#field-title').val($('#field-title').val().trim());
        $('#field-description').val($('#field-description').val().trim());
        $('#field-key').val($('#field-key').val().trim());
    };

    $('#field-title, #field-description, #field-key').on("click, blur", function() {
        trimFormData();
    });

    //Костыль для отмены выделения узла дерева (нужно для перехода в корневой элемент)
    $(document).on('click', function(event){
        var $target = $(event.target);
        if ($target.closest('.x-tree-view, #editNewsCategoryForm, #add-news-category-button').length == 0) {
            treeModel.getSelectionModel().deselectAll();
            selectedCategoryGridItem = null;
        }
    });
});

