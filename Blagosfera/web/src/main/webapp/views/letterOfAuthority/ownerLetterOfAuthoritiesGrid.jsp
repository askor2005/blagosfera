<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var gridSearchString = "";
            ownerLetterOfAuthoritiesStore = Ext.create('Ext.data.Store', {
                id		: 'ownerLetterOfAuthoritiesStore',
                autoLoad: {start: 0, limit: 15},
                fields  : ['id', {name: 'avatar', mapping: 'delegate.avatar'}, {name: 'delegateName', mapping: 'delegate.name'}, {name: 'roleName', mapping: 'authorityRole.name'}, 'documentLink', 'documentCode', 'documentName', 'expiredDate'],
                pageSize: 15,
                proxy: {
                    type: 'ajax',
                    url: '/letterofauthority/getOwnerLettersOfAuthority.json',
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
                        $("#ownerLetterOfAuthorityGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("ownerLetterOfAuthoritiesFilterName").getValue();
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#letterOfAuthorityRoleGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#ownerLetterOfAuthorityGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#ownerLetterOfAuthorityGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'ownerLetterOfAuthoritiesGrid',
                title: 'Доверенности выданные мной',
                store: ownerLetterOfAuthoritiesStore,

                columns: [ {
                    text     : '',
                    dataIndex: 'avatar',
                    width: 70,
                    autoSizeColumn: true,
                    renderer  : function(value, myDontKnow, record) {
                        var avatar = Images.getResizeUrl(record.data.avatar, "c48");
                        var link =
                                "<a href='/sharer/" + record.data.delegate.ikp + "'>" +
                                "<img data-src='holder.js/48x/48' alt='48x48' " +
                                "src='" + avatar + "' data-holder-rendered='true' "+
                                "class='media-object img-thumbnail tooltiped-avatar' "+
                                "data-sharer-ikp='" + record.data.delegate.ikp + "' data-placement='left'/>"+
                                "</a>";

                        return "<div style='display: inline-block;'>" + link + "</div>";
                    }
                }, {
                    text     : 'ФИО делегата',
                    dataIndex: 'delegateName',
                    flex: 1
                }, {
                    text     : 'Имя роли',
                    dataIndex: 'roleName',
                    flex: 1
                }, {
                    text     : 'Дата окончания доверенности',
                    dataIndex: 'expiredDate',
                    flex: 1
                }, {
                    text     : 'Документ',
                    dataIndex: 'documentName',
                    flex: 1,
                    renderer  : function(value, myDontKnow, record) {
                        var docName = "";
                        if (record.data.documentCode != null) {
                            docName = record.data.documentName + " " + record.data.documentCode;
                        } else {
                            docName = record.data.documentName;
                        }
                        return "<a href='" + record.data.documentLink + "' target='_blank'>" + docName + "</a>";
                    }
                }],
                tbar: [
                    {
                        id: 'ownerLetterOfAuthoritiesFilterName',
                        emptyText: 'Фильтр по ФИО делегата',
                        xtype: 'textfield',
                        labelWidth: '0',
                        width: '100%',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        ownerLetterOfAuthoritiesStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    ownerLetterOfAuthoritiesStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: ownerLetterOfAuthoritiesStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $("#ownerLetterOfAuthority-grid")), function(index, div){
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
                        // Открыть модальное окно для редактирования доверенности
                        openEditLetterOfAuthorityModal(record.data.id);
                    }
                },
                frame: true,
                renderTo: 'ownerLetterOfAuthority-grid'
            });
        });
    });
</script>