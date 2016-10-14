<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        var gridSearchString = "";
        Ext.onReady(function () {
            storeDocumentTypes = Ext.create('Ext.data.TreeStore', {
                id		: 'documentTypesStore',
                fields  : ['id', 'parentId', 'parentName', 'name', 'key'],
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
                        if (Ext.getCmp("filterTypeName") != undefined) {
                            store.proxy.extraParams.name = Ext.getCmp("filterTypeName").getValue();
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

            Ext.create('Ext.tree.Panel', {
                id : 'documentTypesGrid',
                title: 'Укажите класс документов',
                useArrows: true,
                rootVisible: false,
                multiSelect: false,
                singleExpand: true,
                store: storeDocumentTypes,
                columns: [{
                    xtype: 'treecolumn',
                    text     : 'Наименование',
                    dataIndex: 'name',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterTypeName',
                        emptyText: 'Фильтр по наименованию',
                        xtype: 'textfield',
                        flex : 1,
                        labelWidth: '0',
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

                    }
                },
                frame: true,
                renderTo: 'documentTypes-grid'
            });
        });
    });
</script>