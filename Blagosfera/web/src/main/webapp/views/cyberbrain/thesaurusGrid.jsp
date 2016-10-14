<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="cyberbrainCommunitySelector.jsp" %>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var myPageSize = 25;

            storeThesaurus = Ext.create('Ext.data.Store', {
                id		: 'thesaurusStore',
                autoLoad: {start: 0, limit: myPageSize},
                remoteSort: true,
                fields  : [
                    'id',
                    'essence',
                    'frequency_essence',
                    'attentionFrequency',
                    'sinonim',
                    {name: 'essence_owner', mapping: 'essence_owner.name'}],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'thesaurus/get_thesaurus.json',
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
                        store.proxy.extraParams.filterField = 'essence';
                        store.proxy.extraParams.filterText = document.getElementById("tag-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                store: storeThesaurus,
                columns: [{
                    header: 'Tег',
                    dataIndex: 'essence',
                    flex: 1
                },{
                    header: 'Синоним',
                    dataIndex: 'sinonim',
                    editor: 'textfield',
                    flex: 1
                },{
                    header: 'Частота',
                    dataIndex: 'frequency_essence',
                    width: 100
                },{
                    header: 'Объем внимания',
                    dataIndex: 'attention_frequency',
                    width: 150
                },{
                    header: 'Ответственный',
                    dataIndex: 'essence_owner',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'newEssenceName',
                        emptyText:'Введите наименование нового тега',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Добавить',
                        handler: function(){
                            var window = $("#communitySelectorModalWindow");
                            window.find("#community-btn-add").off("click").click(function() {
                                var newEssenceName = Ext.getCmp("newEssenceName").getValue();
                                var communityId = window.find("#combobox-communities option:selected").attr("data-community-id");

                                if (communityId != -1 && communityId != "undefined" && newEssenceName != "") {
                                    var data = '{"newEssenceName":"' + newEssenceName + '", "newEssenceThisValue":"", "community_id":"' + communityId + '"}';

                                    Ext.Ajax.request({
                                        url: "thesaurus/addNewTag",
                                        jsonData: data,
                                        success: function (response) {
                                            storeThesaurus.load();
                                            Ext.getCmp("newEssenceName").setValue("");

                                            // обновим счетчики
                                            getCountsRecordsAndScore();

                                            window.modal("hide");
                                            bootbox.alert("Запись успешно добавленна.");
                                        }
                                    });
                                }
                            });

                            window.modal({backdrop:false, keyboard:false});
                        }
                    }
                ],
                bbar: Ext.create('Ext.PagingToolbar', {
                    store : storeThesaurus,
                    displayInfo : true,
                    displayMsg: '{0} - {1} из {2}'
                }),
                plugins: [
                    Ext.create('Ext.grid.plugin.RowEditing', {
                        clicksToEdit: 1,
                        saveBtnText: 'Сохранить',
                        cancelBtnText : "Отмена",
                        errorsText : "Ошибка",
                        listeners: {
                            edit: function (editor, context, eOpts) {
                                var record = context.record.data;
                                var store = context.store;

                                Ext.Ajax.request({
                                    url: 'thesaurus/updateThesaurus',
                                    jsonData : record,
                                    success  : function(response){
                                        if (response.responseText !== "") {
                                            bootbox.alert(response.responseText);
                                        }

                                        store.load();

                                        // обновим счетчики
                                        getCountsRecordsAndScore();
                                    }
                                });
                            }
                        }
                    })
                ],
                renderTo: 'thesaurus-grid'
            });
        });
    });
</script>

<div id="thesaurus-grid"></div>