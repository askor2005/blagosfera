<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeSmtpServers = Ext.create('Ext.data.Store', {
                id		: 'smtpServersStore',
                autoLoad: {start: 0, limit: 25},
                fields  : ['id', 'host', 'port', 'username', 'password', 'protocol', 'using', 'debug'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/admin/systemSettings/SmtpServers.json',
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
                        store.proxy.extraParams.host = Ext.getCmp("filterHost").getValue();
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'smtpServersGrid',
                title: 'Список SMTP серверов',
                store: storeSmtpServers,
                columns: [{
                    text     : 'Хост',
                    dataIndex: 'host',
                    flex: 1
                }, {
                    text     : 'Порт',
                    dataIndex: 'port',
                    flex: 1
                }, {
                    text     : 'Пользователь',
                    dataIndex: 'username',
                    flex: 1
                }, {
                    text     : 'Пароль',
                    dataIndex: 'password',
                    flex: 1
                }, {
                    text     : 'Протокол',
                    dataIndex: 'protocol',
                    flex: 1
                }, {
                    text     : 'Используется сейчас',
                    dataIndex: 'using',
                    flex: 1
                }, {
                    text     : 'Отладка',
                    dataIndex: 'debug',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterHost',
                        emptyText:'Фильтр по хосту',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Найти',
                        handler: function(){
                            storeSmtpServers.load()
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeSmtpServers,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        $("#field-id").val(record.data.id);
                        $("#field-host").val(record.data.host);
                        $("#field-port").val(record.data.port);
                        $("#field-username").val(record.data.username);
                        $("#field-password").val(record.data.password);
                        $("#field-protocol").val(record.data.protocol);

                        //$("#field-using").val(record.data.using);
                        $("#field-using").prop('checked',record.data.using);

                        //$("#field-debug").val(record.data.debug);
                        $("#field-debug").prop('checked',record.data.debug);

                        $("#delete-smtpServers-button").css({"display": "inline"});

                        $("#smtpServersLabel").html("Редактировать");
                        $("#editSmtpServersWindow").modal({backdrop:false, keyboard:false});
                    }
                },
                frame: true,
                renderTo: 'smtpServers-grid'
            });
        });
    });
</script>