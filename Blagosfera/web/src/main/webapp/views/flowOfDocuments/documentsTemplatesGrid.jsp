<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<style>
    .upDocTemplate, .downDocTemplate {
        cursor: pointer;
    }
</style>
<script type="text/javascript" language="javascript">

    function updatePositions(positionsData, callBack) {
        $.radomJsonPost(
                '/admin/flowOfDocuments/updatePositionsDocTemplates.json',
                JSON.stringify(positionsData),
                callBack,
                null,
                {
                    contentType : 'application/json'
                }
        )
    }
    function moveSelectedRow(grid, store, rowIndex, direction) {
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
        for (var i in store.data.items) {
            var record = store.data.items[i];
            dataForUpdate[record.data.id] = grid.getStore().indexOf(record);
        }
        updatePositions(dataForUpdate);
    }


    var selectedClassId = null;
    $(document).ready(function() {
        $("body").on("click", ".upDocTemplate", function(){
            var jqRow = $(this).closest("table.x-grid-item");
            var rowIndex = jqRow.attr("data-recordindex");
            moveSelectedRow(documentTemplatesGrid, storeDocumentTemplates, rowIndex, -1);
        });
        $("body").on("click", ".downDocTemplate", function(){
            var jqRow = $(this).closest("table.x-grid-item");
            var rowIndex = jqRow.attr("data-recordindex");
            moveSelectedRow(documentTemplatesGrid, storeDocumentTemplates, rowIndex, 1);
        });



        var gridTypesSearchString = "";
        var isSearchStringChanged = false;
        Ext.onReady(function () {
            storeDocumentTypesForTemplates = Ext.create('Ext.data.TreeStore', {
                id		: 'documentTemplatesStore',
                fields  : ['id', 'parentId', 'parentName', 'name', 'key'],
                proxy: {
                    type: 'ajax',
                    url: '/admin/flowOfDocuments/documentTypesForTemplates.json',
                    actionMethods: {
                        read: 'POST'
                    }
                },
                folderSort: true,
                listeners: {
                    beforeload: function(store, options) {
                        $("#documentTypesForTemplatesGridSearchResult").hide();
                        if (Ext.getCmp("filterTemplateName") != undefined) {
                            store.proxy.extraParams.name = Ext.getCmp("filterTemplateName").getValue();
                        }
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0 && isSearchStringChanged) {
                            $("#documentTypesForTemplatesGridSearchResult").show();
                            // Ничего не найдено
                            if (gridTypesSearchString != "") {
                                $("#documentTypesForTemplatesGridSearchResult").text("По вашему запросу \"" + gridTypesSearchString + "\" ничего не найдено.");
                            } else {
                                $("#documentTypesForTemplatesGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            Ext.create('Ext.tree.Panel', {
                id : 'documentTypesForTemplatesGrid',
                title: 'Классы документов',
                useArrows: true,
                rootVisible: false,
                multiSelect: false,
                singleExpand: false,
                store: storeDocumentTypesForTemplates,
                columns: [{
                    xtype: 'treecolumn',
                    text     : 'Наименование',
                    dataIndex: 'name',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterTemplateName',
                        emptyText: 'Фильтр по наименованию',
                        xtype: 'textfield',
                        flex : 1,
                        labelWidth: '0',
                        listeners: {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridTypesSearchString = searchString;
                                        isSearchStringChanged = true;
                                        storeDocumentTypesForTemplates.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridTypesSearchString = "";
                                    storeDocumentTypesForTemplates.load();
                                }
                            }
                        }
                    }
                ],
                listeners: {
                    itemclick: function(dataview, record, item, index, e) {
                        selectedClassId = record.data.id;
                        //var documentClassName = record.data.name;
                        //loadDocumentsGrid(documentClassId, documentClassName);
                        storeDocumentTemplates.load();
                    },
                    itemdblclick: function(dataview, record, item, index, e) {
                    },
                    afteritemexpand: function(c) {
                        if (gridTypesSearchString != null && gridTypesSearchString.length > 2 && isSearchStringChanged) {
                            var regex = new RegExp( '(' + gridTypesSearchString + ')', 'gi' );
                            $.each($("span.x-tree-node-text"), function(index, span){
                                var $span = $(span);
                                var html = $span.html();
                                if (html.indexOf("<i ") == -1) {
                                    html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );
                                    $span.html(html);
                                }
                            });
                            isSearchStringChanged = false;
                        }
                    }
                },
                frame: true,
                renderTo: 'documentTypesForTemplates-grid'
            });
        });
    });


    $(document).ready(function() {
        Ext.onReady(function () {
            var gridSearchString = "";
            storeDocumentTemplates = Ext.create('Ext.data.Store', {
                id		: 'documentTemplatesStore',
                autoLoad: {start: 0, limit: 25},
                fields  : ['id', 'className', 'name'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/admin/flowOfDocuments/documentTemplates.json',
                    actionMethods: {
                        read: 'POST'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'items',
                        totalProperty: 'total'
                    }
                },
                listeners: {
                    beforeload: function(store, options) {
                        $("#documentTemplatesGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                        store.proxy.extraParams.classId = selectedClassId;
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#documentTemplatesGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#documentTemplatesGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#documentTemplatesGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            documentTemplatesGrid = Ext.create('Ext.grid.Panel', {
                id : 'documentTemplatesGrid',
                title: 'Шаблоны документов',
                store: storeDocumentTemplates,
                columns: [{
                    text     : 'Класс документов',
                    dataIndex: 'className',
                    width: "45%"
                }, {
                    text     : 'Имя шаблона',
                    dataIndex: 'name',
                    width: "43%"
                }, {
                    text     : 'Порядок',
                    dataIndex: 'id',
                    width: "10%",
                    renderer  : function(value, myDontKnow, record) {
                        var result =
                                "<div class='glyphicon glyphicon-chevron-up upDocTemplate'></div>" +
                                "<div class='glyphicon glyphicon-chevron-down downDocTemplate'></div>";
                        return result;
                    }
                }],
                tbar: [
                    {
                        id: 'filterName',
                        emptyText:'Фильтр по имени шаблона',
                        xtype: 'textfield',
                        labelWidth: '0',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        storeDocumentTemplates.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    storeDocumentTemplates.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeDocumentTemplates,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $('#documentTemplates-grid')), function(index, div){
                                    var $div = $(div);
                                    var html = $div.html();
                                    if (html.indexOf("<i ") == -1) {
                                        html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );
                                        $div.html(html);
                                    }
                                });
                            }
                        }
                    }
                },
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        var url = "/admin/flowOfDocuments/documentTemplate/edit?documentTemplateId=" + record.data.id;
                        $(location).attr("href", url);
                    }
                },
                frame: true,
                renderTo: 'documentTemplates-grid'
            });
        });
    });
</script>