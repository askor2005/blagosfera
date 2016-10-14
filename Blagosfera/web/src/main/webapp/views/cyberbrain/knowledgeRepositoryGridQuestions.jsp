<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeQuestions = Ext.create('Ext.data.Store', {
                id		: 'questionsStore',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : [
                    'knowledge_rep_id',
                    'thesaurus_tag_id',
                    'thesaurus_tag',
                    'thesaurus_tag_owner_id',
                    'thesaurus_tag_owner',
                    'thesaurus_tag_attribute_id',
                    'thesaurus_tag_attribute'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'knowledgeRepository/get_questions.json',
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
                        var communityId = $("#questions-combobox-communities option:selected").attr("data-community-id");
                        if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                            store.proxy.extraParams.communityId = communityId;
                        } else {
                            store.proxy.extraParams.communityId = -1;
                        }
                        store.proxy.extraParams.tagFilter = document.getElementById("questions-tag-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title: 'Вопросы "что это"',
                store: storeQuestions,
                columns: [{
                    header: 'Tег',
                    dataIndex: 'thesaurus_tag_owner',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Это',
                    dataIndex: 'thesaurus_tag_attribute',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: '?',
                    dataIndex: 'thesaurus_tag',
                    editor: 'textfield',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true,
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    }
                }],
                tbar: [
                    {
                        id: 'newEssenceName',
                        emptyText:'Тег',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'newEssenceThisValue',
                        emptyText:'Это',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Добавить',
                        handler: function() {
                            var communityId = $("#questions-combobox-communities option:selected").attr("data-community-id");
                            if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                                var newEssenceName = Ext.getCmp("newEssenceName").getValue();
                                var newEssenceThisValue = Ext.getCmp("newEssenceThisValue").getValue();

                                if (newEssenceName != "") {
                                    var data = '{"newEssenceName":"' + newEssenceName + '", "newEssenceThisValue": "' + newEssenceThisValue + '", "community_id":"' + communityId + '"}';

                                    Ext.Ajax.request({
                                        url: "thesaurus/addNewTag",
                                        jsonData: data,
                                        success: function (response) {
                                            storeQuestions.load();
                                            storeQuestionsMany.load();
                                            storeQuestionsProperties.load();

                                            Ext.getCmp("newEssenceName").setValue("");
                                            Ext.getCmp("newEssenceThisValue").setValue("");

                                            // обновим счетчики
                                            getCountsRecordsAndScore();

                                            bootbox.alert("Запись успешно добавленна.");
                                        }
                                    });
                                }
                            } else {
                                bootbox.alert("Перед тем как добавить новую запись нужно выбрать объединение в рамках которого будет добавленна запись.");
                            }
                        }
                    }
                ],
                height: 400,
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeQuestions,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
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
                                    url: 'knowledgeRepository/answerTheQuestion',
                                    jsonData : record,
                                    success  : function(response){
                                        store.load();
                                        storeQuestionsMany.load();
                                        storeQuestionsProperties.load();

                                        // обновим счетчики
                                        getCountsRecordsAndScore();
                                    }
                                });
                            }
                        }
                    })
                ],
                renderTo: 'questions-grid'
            });
        });
    });
</script>

<div id="questions-grid"></div>