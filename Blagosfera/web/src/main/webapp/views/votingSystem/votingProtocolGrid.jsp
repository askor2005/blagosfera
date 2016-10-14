<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" language="javascript">
    // Создать таблицу с результатами голосования
    function createVotingProtocolGrid(parentNodeId, votingItemId, votingId) {
        var storeVotingProtocol = null;
        Ext.onReady(function () {
            storeVotingProtocol = Ext.create('Ext.data.Store', {
                id: 'storeVotingProtocol' + votingItemId,
                autoLoad: {start: 0, limit: 10},
                fields: ['id', 'voterId', 'voterName', 'voteDateTime'],
                pageSize: 10,
                proxy: {
                    type: 'ajax',
                    url: '/votingsystem/getVotingProtocol.json',
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
                        store.proxy.extraParams.votingItemId = votingItemId;
                        store.proxy.extraParams.votingId = votingId;
                    },
                    load: function (component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            // TODO Ничего не найдено
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id: 'votingProtocolPanel' + votingItemId,
                title: '',
                store: storeVotingProtocol,
                columns: [{
                    text: 'Избиратель',
                    dataIndex: 'voterName',
                    width: '50%',
                    renderer  : function(value, myDontKnow, record) {
                        var voter = null;
                        for (var index in voters) {
                            var findVoter = voters[index];
                            if (findVoter.id == record.data.voterId) {
                                voter = findVoter;
                                break;
                            }
                        }

                        var link =
                        "<a href='/sharer/" + voter.ikp + "'>" +
                            "<img data-src='holder.js/140x/140' alt='140x140' " +
                                "src='" + voter.avatarSrc + "' data-holder-rendered='true' "+
                                "class='media-object img-thumbnail tooltiped-avatar' "+
                                "data-sharer-ikp='" + voter.ikp + "' data-placement='left'/>"+
                        "</a>";

                        return "<div style='display: inline-block; vertical-align: top;'>" + link + "</div><div style='display: inline-block; padding: 21px; vertical-align: top;'>" + record.data.voterName + "</div>";
                    }
                }, {
                    text: 'Дата и время голосования',
                    dataIndex: 'voteDateTime',
                    flex: 1,
                    renderer  : function(value, myDontKnow, record) {
                        // Получаем дату для пользователя в его таймзоне
                        var date = createIsoDate(record.data.voteDateTime);
                        var dateFormatted = date.format("dd.mm.yyyy HH:MM:ss");
                        dateFormatted = dateFormatted.replace(" ", "&nbsp;&nbsp;&nbsp;&nbsp;");
                        return "<div style='display: inline-block; padding: 21px 0px 21px 0px; vertical-align: top;'>" + dateFormatted + "</div>";
                    }
                }, {
                    text: 'Проверка электронной подписи',
                    dataIndex: 'signCheck',
                    flex: 1
                }],
                tbar: [

                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeVotingProtocol,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function (gridview) {

                        }
                    }
                },
                listeners: {
                    itemdblclick: function (dataview, record, item, index, e) {

                    }
                },
                frame: true,
                renderTo: parentNodeId
            });
        });
    };
</script>