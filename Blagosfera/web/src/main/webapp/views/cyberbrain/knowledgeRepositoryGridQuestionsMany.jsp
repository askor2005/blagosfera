<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeQuestionsMany = Ext.create('Ext.data.Store', {
                id		: 'questionsManyStore',
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
                    url: 'knowledgeRepository/get_questions_many.json',
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
                        store.proxy.extraParams.manyFilter = document.getElementById("questions-many-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title	: 'Вопросы по множествам',
                store: storeQuestionsMany,
                columns: [{
                    header: 'Множество',
                    dataIndex: 'thesaurus_tag_owner',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Свойства множества',
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
                height: 400,
                tbar: [
                    {
                        id: 'newManyName',
                        emptyText:'Множество',
                        xtype: 'textfield',
                        flex  :1
                    }, '-',
                    {
                        id: 'newManyValue',
                        emptyText:'Свойства',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Добавить',
                        handler: function(){
                            var newManyName = Ext.getCmp("newManyName").getValue();
                            var newManyValue = Ext.getCmp("newManyValue").getValue();

                            if (newManyName == "" || newManyName == "null") {
                                bootbox.alert("Требуется задать наименование множества!");
                                return false;
                            }

                            var communityId = $("#questions-combobox-communities option:selected").attr("data-community-id");
                            if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                                if (newManyName != "") {
                                    var data = '{"newManyName":"' + newManyName + '","newManyValue":"' + newManyValue + '", "community_id":"' + communityId + '"}';

                                    Ext.Ajax.request({
                                        url: "knowledgeRepository/addNewMany",
                                        jsonData: data,
                                        success: function (response) {
                                            storeQuestions.load();
                                            storeQuestionsMany.load();
                                            storeQuestionsProperties.load();

                                            Ext.getCmp("newManyName").setValue("");
                                            Ext.getCmp("newManyValue").setValue("");

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
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeQuestionsMany,
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
                                    url: 'knowledgeRepository/answerTheQuestionMany',
                                    jsonData : record,
                                    success  : function(response){
                                        store.load();
                                        storeQuestions.load();
                                        storeQuestionsProperties.load();

                                        // обновим счетчики
                                        getCountsRecordsAndScore();
                                    }
                                });
                            }
                        }
                    })
                ],
                renderTo: 'questionsMany-grid'
            });
        });
    });
</script>

<div id="questionsMany-grid"></div>