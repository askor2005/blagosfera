<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var myPageSize = 25;

            storeRatingSystem = Ext.create('Ext.data.Store', {
                id		: 'ratingSystemStore',
                autoLoad: {start: 0, limit: myPageSize},
                remoteSort: true,
                fields  : ['user', 'score_sharer'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'ratingSystem/get_rating_system.json',
                    actionMethods: {
                        read: 'POST'
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'items',
                        totalProperty: 'total'
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                store: storeRatingSystem,
                columns: [{
                    header: 'Пользователь',
                    dataIndex: 'user',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                },{
                    header: 'Баллы',
                    dataIndex: 'score_sharer',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }],
                bbar: Ext.create('Ext.PagingToolbar', {
                    store : storeRatingSystem,
                    displayInfo : true,
                    displayMsg: '{0} - {1} из {2}'
                }),
                renderTo: 'ratingSystem-grid'
            });
        });
    });
</script>

<div id="ratingSystem-grid"></div>