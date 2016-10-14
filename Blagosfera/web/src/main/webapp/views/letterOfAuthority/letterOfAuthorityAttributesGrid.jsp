<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    function showLetterOfAuthorityAttributesGrid(letterOfAuthorityId, jqGridNode, jqSearchResultNode) {
        Ext.onReady(function () {
            var gridSearchString = "";
            letterOfAuthorityAttributesStore = Ext.create('Ext.data.Store', {
                id		: 'letterOfAuthorityAttributesStore',
                autoLoad: {start: 0, limit: 15},
                fields  : ['id', 'name', 'value'],
                pageSize: 15,
                proxy: {
                    type: 'ajax',
                    url: '/letterofauthority/getLetterOfAuthorityAttributes.json',
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
                        jqSearchResultNode.hide();
                        store.proxy.extraParams.name = Ext.getCmp("letterOfAuthorityAttributesFilterName").getValue();

                        store.proxy.extraParams.letterOfAuthorityId = letterOfAuthorityId;
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            jqSearchResultNode.show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                jqSearchResultNode.text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                jqSearchResultNode.text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'letterOfAuthorityAttributesGrid',
                title: 'Атрибуты доверенности',
                store: letterOfAuthorityAttributesStore,

                columns: [ {
                    text     : 'Название',
                    dataIndex: 'name',
                    flex: 1
                }, {
                    text     : 'Значение',
                    dataIndex: 'value',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'letterOfAuthorityAttributesFilterName',
                        emptyText: 'Фильтр по названию',
                        xtype: 'textfield',
                        labelWidth: '0',
                        width: '100%',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        letterOfAuthorityAttributesStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    letterOfAuthorityAttributesStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: letterOfAuthorityAttributesStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", jqGridNode), function(index, div){
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
                        // Открыть модальное окно для редактирования атрибута
                        openEditLetterOfAuthorityAttributeModal(record.data.id, record.data.name, record.data.value);
                    }
                },
                frame: true,
                renderTo: jqGridNode.attr("id")
            });
        });
    }
</script>