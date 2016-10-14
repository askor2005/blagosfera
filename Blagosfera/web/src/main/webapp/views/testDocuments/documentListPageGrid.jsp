<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" language="javascript">

    $(document).ready(function(){
        var searchStringName = "";

        Ext.onReady(function () {
            var dataStore = Ext.create('Ext.data.TreeStore', {
                id: 'classDocumentsStore',
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    contentType : 'application/json',
                    url: '/document/service/documentTree.json',
                    actionMethods: {
                        read: 'POST'
                    }
                },
                folderSort: true,
                listeners: {
                    beforeload: function (store, options) {
                        $("#classDocumentsGridSearchResult").hide();
                        if (Ext.getCmp("filterName") != undefined) {
                            store.proxy.extraParams.name = searchStringName;
                        }
                        if (window.communityId != null) {
                            store.proxy.extraParams.communityId = communityId;
                        }
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#classDocumentsGridSearchResult").show();
                            // Ничего не найдено
                            $("#classDocumentsGridSearchResult").text("По вашему запросу \"" + searchStringName + "\" ничего не найдено.");
                        }
                    }
                }
            });

            var treePanel = Ext.create('Ext.tree.Panel', {
                id : 'classDocumentsGrid',
                title: 'Классификатор документов',
                useArrows: true,
                rootVisible: false,
                multiSelect: false,
                singleExpand: false,
                store: dataStore,
                columns: [{
                    xtype: 'treecolumn',
                    text     : 'Наименование',
                    dataIndex: 'name',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterName',
                        emptyText: 'Наименование',
                        xtype: 'textfield',
                        labelWidth: 0,
                        width: '100%',
                        listeners: {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        searchStringName = searchString;
                                        dataStore.load();
                                    } else if (searchString.length == 0) {
                                        searchStringName = searchString;
                                        dataStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    searchStringName = "";
                                    component.setValue("");
                                    dataStore.load();
                                }
                            }
                        }
                    }
                ],
                listeners: {
                    itemclick: function(dataview, record, item, index, e) {
                        var documentClassId = record.data.id;
                        var documentClassName = record.data.name;
                        loadDocumentsGrid(documentClassId, documentClassName);
                    },
                    viewready: function (tree) {
                    },
                    afteritemexpand: function(c) {
                        if (searchStringName != null && searchStringName.length > 2) {
                            var regex = new RegExp( '(' + searchStringName + ')', 'gi' );
                            $.each($("span.x-tree-node-text"), function(index, span){
                                var $span = $(span);
                                var html = $span.html();
                                if (html.indexOf("<i ") == -1) {
                                    html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );
                                    $span.html(html);
                                }
                            });
                        }
                    }
                },
                frame: true,
                renderTo: 'classDocumentsDiv'
            });
        });
    });
</script>