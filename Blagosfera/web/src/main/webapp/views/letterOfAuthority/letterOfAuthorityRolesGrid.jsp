<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var gridSearchString = "";
            letterOfAuthorityStore = Ext.create('Ext.data.Store', {
                id		: 'letterOfAuthorityStore',
                autoLoad: {start: 0, limit: 25},
                fields  : ['id', 'key', 'name', 'createDocumentScript'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/admin/letterofauthority/getLetterOfAuthorityRolesByFilter.json',
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
                        $("#letterOfAuthorityRoleGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#letterOfAuthorityRoleGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#letterOfAuthorityRoleGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#letterOfAuthorityRoleGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'letterOfAuthorityGrid',
                title: 'Роли доверенностей',
                store: letterOfAuthorityStore,
                columns: [{
                    text     : 'Код роли',
                    dataIndex: 'key',
                    flex: 1
                }, {
                    text     : 'Имя роли',
                    dataIndex: 'name',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterName',
                        emptyText:'Фильтр по имени роли доверенности',
                        xtype: 'textfield',
                        labelWidth: '0',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        letterOfAuthorityStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    letterOfAuthorityStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: letterOfAuthorityStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner"), function(index, div){
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
                        // Открыть модальное окно для редактирования
                        openEditRoleModal(record.data.id, record.data.key, record.data.name, record.data.createDocumentScript)
                    }
                },
                frame: true,
                renderTo: 'letterOfAuthorityRole-grid'
            });
        });
    });
</script>