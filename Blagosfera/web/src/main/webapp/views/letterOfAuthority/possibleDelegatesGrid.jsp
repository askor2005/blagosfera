<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var gridSearchString = "";
            possibleDelegatesStore = Ext.create('Ext.data.Store', {
                id		: 'possibleDelegatesStore',
                autoLoad: {start: 0, limit: 15},
                fields  : ['id', 'avatar', 'fullName'],
                pageSize: 15,
                proxy: {
                    type: 'ajax',
                    url: '/letterofauthority/getPossibleDelegates.json',
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
                        $("#delegatesGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();

                        store.proxy.extraParams.role_key = currentRoleKey;
                        store.proxy.extraParams.radom_account_id = currentScopeObjectId;
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#delegatesGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#delegatesGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#delegatesGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            possibleDelegatesGrid = Ext.create('Ext.grid.Panel', {
                id : 'possibleDelegatesGrid',
                title: 'Делегаты доверенности',
                store: possibleDelegatesStore,
                columns: [{
                    text     : 'Аватар',
                    dataIndex: 'avatar',
                    flex: 1,
                    renderer  : function(value, myDontKnow, record) {
                        var avatar = Images.getResizeUrl(record.data.avatar, "c48");
                        var link =
                                "<a href='/sharer/" + record.data.ikp + "'>" +
                                "<img data-src='holder.js/48x/48' alt='48x48' " +
                                "src='" + avatar + "' data-holder-rendered='true' "+
                                "class='media-object img-thumbnail tooltiped-avatar' "+
                                "data-sharer-ikp='" + record.data.ikp + "' data-placement='left'/>"+
                                "</a>";

                        return "<div style='display: inline-block;'>" + link + "</div>";
                    }
                }, {
                    text     : 'ФИО делегата',
                    dataIndex: 'fullName',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterName',
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
                                        possibleDelegatesStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    possibleDelegatesStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: possibleDelegatesStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $("#delegates-grid")), function(index, div){
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
                    itemclick: function(dataview, record, item, index, e) {
                        selectPossibleDelegate(record.data.id);
                    }
                },
                frame: true,
                renderTo: 'delegates-grid'
            });
        });
    });
</script>