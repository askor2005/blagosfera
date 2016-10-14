<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {

            var gridSearchString = "";
            myLetterOfAuthoritiesStore = Ext.create('Ext.data.Store', {
                id		: 'myLetterOfAuthoritiesStore',
                autoLoad: {start: 0, limit: 15},
                fields  : ['id', {name: 'avatar', mapping: 'owner.avatar'}, {name: 'ownerName', mapping: 'owner.name'}, {name: 'roleName', mapping: 'authorityRole.name'}, 'documentLink', 'documentCode', 'documentName', 'expiredDate'],
                pageSize: 15,
                proxy: {
                    type: 'ajax',
                    url: '/letterofauthority/getMyLettersOfAuthority.json',
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
                        $("#letterOfAuthorityMyGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("myLetterOfAuthoritiesFilterName").getValue();
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#letterOfAuthorityRoleGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#letterOfAuthorityMyGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#letterOfAuthorityMyGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            myLetterOfAuthoritiesGrid = Ext.create('Ext.grid.Panel', {
                id : 'myLetterOfAuthoritiesGrid',
                title: 'Доверенности выданные мне',
                store: myLetterOfAuthoritiesStore,

                columns: [ {
                    text     : ' ',
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
                    text     : 'Автор доверенности',
                    dataIndex: 'ownerName',
                    flex: 1
                }, {
                    text     : 'Имя роли',
                    dataIndex: 'roleName',
                    flex: 1
                }, {
                    text     : 'Дата окончания доверенности',
                    dataIndex: 'expiredDate',
                    autoSizeColumn: true,
                    flex: 1
                }, {
                    text     : 'Документ',
                    dataIndex: 'documentName',
                    flex: 1,
                    renderer  : function(value, myDontKnow, record) {
                        return "<a href='" + record.data.documentLink + "' target='_blank'>" + record.data.documentName + " " + record.data.documentCode + "</a>";
                    }
                }],
                tbar: [
                    {
                        id: 'myLetterOfAuthoritiesFilterName',
                        emptyText: 'Фильтр по ФИО автора доверенности',
                        xtype: 'textfield',
                        labelWidth: '0',
                        width: '100%',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        myLetterOfAuthoritiesStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    myLetterOfAuthoritiesStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: myLetterOfAuthoritiesStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $("#letterOfAuthorityMy-grid")), function(index, div){
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
                        // TODO
                        // Открыть модальное окно для редактирования доверенности
                        //openEditRoleModal(record.data.id, record.data.key, record.data.name, record.data.createDocumentScript)
                    }
                },
                frame: true,
                renderTo: 'letterOfAuthorityMy-grid'
            });
        });
    });
</script>