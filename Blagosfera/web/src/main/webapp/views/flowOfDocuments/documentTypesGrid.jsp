<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<style>
    .categoryIconCls {
        content: "\f115";
    }
    .documentTypeIconCls {
        content: "\f016";
    }
    .upDocType, .downDocType {
        cursor: pointer;
    }
</style>

<script type="text/javascript" language="javascript">
    function updatePositions(positionsData, callBack) {
        $.radomJsonPost(
                '/admin/flowOfDocuments/updatePositionsDocTypes.json',
                JSON.stringify(positionsData),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        )
    }

    var treePanel = null;
    function moveSelectedRow(grid, rowIndex, direction) {
        var record = grid.getView().getRecord(parseInt(rowIndex));
        if (!record) {
            return;
        }
        var index = grid.getStore().indexOf(record);
        if (direction < 0) {
            index--;
            if (index < 0) {
                return;
            }
        } else {
            index++;
            if (index >= grid.getStore().getCount()) {
                return;
            }
        }
        grid.getStore().remove(record);
        grid.getStore().insert(index, record);

        var dataForUpdate = {};
        if (record.parentNode.childNodes != null) {
            for (var childRecordIndex in record.parentNode.childNodes) {
                var childRecord = record.parentNode.childNodes[childRecordIndex];
                dataForUpdate[childRecord.data.id] = grid.getStore().indexOf(childRecord);
            }
        }
        updatePositions(dataForUpdate);
    }

    $(document).ready(function() {
        $("body").on("click", ".upDocType", function(){
            var jqRow = $(this).closest("table.x-grid-item");
            var rowIndex = jqRow.attr("data-recordindex");
            moveSelectedRow(treePanel, rowIndex, -1);
        });
        $("body").on("click", ".downDocType", function(){
            var jqRow = $(this).closest("table.x-grid-item");
            var rowIndex = jqRow.attr("data-recordindex");
            moveSelectedRow(treePanel, rowIndex, 1);
        });

        var gridSearchString = "";
        Ext.onReady(function () {
            storeDocumentTypes = Ext.create('Ext.data.TreeStore', {
                id		: 'documentTypesStore',
                fields  : ['id', 'parentId', 'parentName', 'pathName', 'name', 'key', 'position'],
                proxy: {
                    type: 'ajax',
                    url: '/admin/flowOfDocuments/documentTypes.json',
                    actionMethods: {
                        read: 'POST'
                    }
                },
                folderSort: true,
                listeners: {
                    beforeload: function(store, options) {
                        $("#documentTypesGridSearchResult").hide();
                        if (Ext.getCmp("filterName") != undefined) {
                            store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                        }
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#documentTypesGridSearchResult").show();
                            // Ничего не найдено
                            $("#documentTypesGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                        }
                    }
                }
            });

            treePanel = Ext.create('Ext.tree.Panel', {
                id : 'documentTypesGrid',
                title: 'Классы документов',
                useArrows: true,
                rootVisible: false,
                multiSelect: false,
                singleExpand: false,
                store: storeDocumentTypes,
                viewConfig: {
                    plugins: {
                        ptype: 'treeviewdragdrop'
                    },
                    listeners: {
                        drop: function (node, data, overModel, dropPosition) {
                            var source = data.view.getSelection();
                            var sourceId = source[0].getData().id;
                            var targetId = overModel.getId();

                            $.ajax({
                                type: "post",
                                dataType: "json",
                                url: "/admin/flowOfDocuments/documentType/changeParent?id=" + sourceId + "&parentId=" + targetId,
                                success: function (response) {
                                    if (response.result == "error") {
                                        bootbox.alert(response.message);
                                        storeComboDocumentTypes.load();
                                    }
                                },
                                error: function () {
                                    console.log("ajax error");
                                }
                            });
                        }
                    }
                },
                columns: [{
                    xtype: 'treecolumn',
                    text     : 'Наименование',
                    dataIndex: 'name',
                    width: "87%"
                }, {
                    text     : 'Порядок',
                    dataIndex: 'id',
                    width: "10%",
                    renderer  : function(value, myDontKnow, record) {
                        var result =
                                "<div class='glyphicon glyphicon-chevron-up upDocType'></div>" +
                                "<div class='glyphicon glyphicon-chevron-down downDocType'></div>";
                        return result;
                    }
                }],
                tbar: [
                    {
                        id: 'filterName',
                        emptyText: 'Фильтр по наименованию',
                        xtype: 'textfield',
                        labelWidth: 0,
                        flex : 1,
                        listeners: {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        storeDocumentTypes.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    storeDocumentTypes.load();
                                }
                            }
                        }
                    }
                ],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        getParticipants(record.data.id);

                        $("#field-name").val(record.data.name);
                        $("#field-key").val(record.data.key);
                        $("#field-parent").val(record.data.parentId);
                        $("#field-position").val(record.data.position);
                        $("#parentName").text(record.data.parentName);
                        $("#parentName").attr("title", record.data.pathName);
                        $("#chooseParentClass").attr("parent_id", record.data.parentId);

                        $("#delete-documentType-button").css({"display": "block"});
                        $("#field-documentTypeId").val(record.data.id);
                        $("#documentTypeLabel").html("Редактировать");
                        $("#editDocumentTypeWindow").modal({backdrop: false, keyboard: false});
                    },
                    itemclick: function(dataview, record, item, index, e){
                        setSelectedGridItem(record.data);
                    },
                    viewready: function (tree) {
                        var view = tree.getView();
                        var dd = view.findPlugin('treeviewdragdrop');

                        dd.dragZone.onDragOver = function(e, id) {
                            var target = this.cachedTarget || Ext.dd.DragDropManager.getDDById(id);
                            if (this.beforeDragOver(target, e, id) !== false) {
                                if(target.isNotifyTarget){
                                    var status = target.notifyOver(this, e, this.dragData);

                                    var v = target.view;
                                    var cell = e.getTarget(v.cellSelector);
                                    var row = v.findItemByChild(cell);
                                    var record = v.getRecord(row);

                                    status = "x-dd-drop-nodrop";

                                    this.proxy.setStatus(status);
                                }
                                if (this.afterDragOver) {
                                    /**
                                     * An empty function by default, but provided so that you can perform a custom action
                                     * while the dragged item is over the drop target by providing an implementation.
                                     * @param {Ext.dd.DragDrop} target The drop target
                                     * @param {Event} e The event object
                                     * @param {String} id The id of the dragged element
                                     * @method afterDragOver
                                     */
                                    this.afterDragOver(target, e, id);
                                }
                            }
                        };
                    },
                    afteritemexpand: function(c) {
                        if (gridSearchString != null && gridSearchString.length > 2) {
                            var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                            $.each($("span.x-tree-node-text"), function(index, span){
                                var $span = $(span);
                                var html = $span.html();
                                if (html.indexOf("<i ") == -1) {
                                    //var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                    html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );

                                    //html = html.replace(gridSearchString, "<i style='background-color:#dddddd;'>" + gridSearchString + "</i>");
                                    $span.html(html);
                                }
                            });
                        }
                    }
                },
                frame: true,
                renderTo: 'documentTypes-grid'
            });
            treePanel.getEl().addKeyMap({
                eventName: "keyup",
                binding: [{
                    key: "esc",
                    fn:  function(){ console.log("Space key pressed"); }
                }]
            });
        });
    });
</script>