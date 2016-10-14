<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            function rowEditorClearFields() {
                Ext.getCmp('lifecycleStatus').setRawValue('');
                Ext.getCmp('timeReady').setValue('');
                Ext.getCmp('customStatus').setValue('');
            }

            function rowEditorCheckFields() {
                var customStatus = Ext.getCmp('customStatus').getValue();
                var timeReady = Ext.getCmp('timeReady').getValue();
                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getValue();

                if (lifecycleStatus == null || lifecycleStatus == "" ) {
                    bootbox.alert("Нужно указать состояние ЖЦ объекта!");
                    return false;
                }

                if (timeReady == null || timeReady == "" ) {
                    bootbox.alert("Нужно указать дату готовности объекта!");
                    return false;
                }

                if (customStatus == null || customStatus == "" ) {
                    bootbox.alert("Нужно указать состояние объекта!");
                    return false;
                }

                return true;
            }

            function refreshGrid() {
                storeQuestionsTracks.load();
                storeQuestions.load();

                // обновим счетчики
                getCountsRecordsAndScore();
            }

            Ext.define('Ext.grid.CustomRowEditorButtonsTracks', {
                extend: 'Ext.grid.RowEditorButtons',
                alias : 'widget.customroweditorbuttonstracks',
                constructor: function (config) {
                    var me = this,
                            rowEditor = config.rowEditor,
                            cssPrefix = Ext.baseCSSPrefix,
                            plugin = rowEditor.editingPlugin;

                    config = Ext.apply({
                        baseCls: cssPrefix + 'grid-row-editor-buttons',
                        defaults: {
                            xtype: 'button',
                            ui: rowEditor.buttonUI,
                            scope: plugin,
                            flex: 1,
                            minWidth: Ext.panel.Panel.prototype.minButtonWidth
                        },
                        items: [{
                            id: 'addTrackTracks',
                            text: 'Добавить трек',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (!rowEditorCheckFields()) {
                                    return false;
                                }

                                var customStatus = Ext.getCmp('customStatus').getValue();
                                var timeReady = Ext.getCmp('timeReady').getValue();
                                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getRawValue();

                                record.custom_status = customStatus;

                                var r =  lifecycleStatusStore.find('name', lifecycleStatus);
                                record.lifecycle_status = lifecycleStatusStore.getAt(r).data.id;

                                record.time_ready = timeReady;

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/addNewTrack',
                                    jsonData : record,
                                    success  : function(response){
                                        rowEditorClearFields();
                                        editor.cancelEdit();
                                        refreshGrid();
                                    }
                                });
                            }
                        }, {
                            id: 'updateTracks',
                            text: 'Сохранить',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (!rowEditorCheckFields()) {
                                    return false;
                                }

                                var customStatus = Ext.getCmp('customStatus').getValue();
                                var timeReady = Ext.getCmp('timeReady').getValue();
                                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getRawValue();

                                record.custom_status = customStatus;

                                var r =  lifecycleStatusStore.find('name', lifecycleStatus);
                                record.lifecycle_status = lifecycleStatusStore.getAt(r).data.id;

                                record.time_ready = timeReady;

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/saveTrack',
                                    jsonData : record,
                                    success  : function(response){
                                        rowEditorClearFields();
                                        editor.cancelEdit();
                                        refreshGrid();
                                    }
                                });
                            }
                        }, {
                            id: 'finishTrackTracks',
                            text: 'Завершить',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (record.is_track == "0") {
                                    bootbox.alert("Для данного объекта нет треков!");
                                    return false;
                                }

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/editTracksFinish',
                                    jsonData : record,
                                    success  : function(response){
                                        rowEditorClearFields();
                                        editor.cancelEdit();
                                        refreshGrid();
                                    }
                                });
                            }
                        }, {
                            id: 'cancelTracks',
                            text: 'Отмена',
                            handler: function(editor, context, eOpts) {
                                this.getEditor().cancelEdit();
                            }
                        }]
                    }, config);

                    me.callParent([config]);
                    me.addClsWithUI(me.position);
                }
            });

            Ext.define('Ext.grid.CustomRowEditorTracks', {
                extend: 'Ext.grid.RowEditor',
                alias : 'widget.customroweditortracks',
                requires: [
                    'Ext.grid.CustomRowEditorButtonsTracks'
                ],
                initComponent: function () {
                    this.callParent(arguments);
                },
                getFloatingButtons: function() {
                    var me = this,
                            btns = me.floatingButtons;

                    if (!btns) {
                        me.floatingButtons = btns = new Ext.grid.CustomRowEditorButtonsTracks({
                            rowEditor: me
                        });
                    }
                    return btns;
                }
            });

            Ext.define('Ext.grid.plugin.CustomRowEditingTracks', {
                extend: 'Ext.grid.plugin.RowEditing',
                alias : 'widget.customroweditingtracks',
                clicksToEdit: 1,
                requires: [
                    'Ext.grid.CustomRowEditorTracks'
                ],
                initEditor: function() {
                    return new Ext.grid.CustomRowEditorTracks(this.initEditorConfig());
                }
            });

            var editorTracks = Ext.create('Ext.grid.plugin.CustomRowEditingTracks', {});

            lifecycleStatusStore = Ext.create('Ext.data.Store', {
                id		: 'questionsTracksStore',
                fields  : ['id', 'name'],
                data : [
                    {id: '1', name: 'Идея'},
                    {id: '2', name: 'Декомпозиция'},
                    {id: '3', name: 'Контрактовано'},
                    {id: '4', name: 'Исполняется'},
                    {id: '5', name: 'Готово'},
                    {id: '6', name: 'Изношено'},
                    {id: '7', name: 'Убирается'},
                    {id: '8', name: 'Память'}
                ]
            });

            storeQuestionsTracks = Ext.create('Ext.data.Store', {
                id		: 'questionsTracksStore',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : [
                    'knowledge_rep_id',
                    'thesaurus_tag_many_id',
                    'thesaurus_tag_many',
                    'thesaurus_tag_owner_id',
                    'thesaurus_tag_owner',
                    'custom_status',
                    {name: 'time_ready', type: 'date'},
                    'lifecycle_status',
                    'is_track'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'knowledgeRepository/get_questions_tracks.json',
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
                        store.proxy.extraParams.tracksMany = document.getElementById("questions-tracks-many-filter").value;
                        store.proxy.extraParams.tracksTag = document.getElementById("questions-tracks-tag-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title	: 'Вопросы о треке объекта',
                store: storeQuestionsTracks,
                columns: [{
                    header: 'Множество',
                    dataIndex: 'thesaurus_tag_many',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Tег владелец',
                    dataIndex: 'thesaurus_tag_owner',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Состояние ЖЦ',
                    dataIndex: 'lifecycle_status',
                    width: 150,
                    sortable: false,
                    menuDisabled: true,
                    editor: {
                        id: 'lifecycleStatus',
                        xtype: 'combobox',
                        store: lifecycleStatusStore,
                        displayField: 'name',
                        valueField: 'id',
                        editable: false
                    },
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    }
                }, {
                    header: 'Дата готовности',
                    dataIndex: 'time_ready',
                    xtype: 'datecolumn',
                    format: 'Y-m-d',
                    width: 150,
                    sortable: false,
                    menuDisabled: true,
                    editor: {
                        id: 'timeReady',
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        editable: false
                    },
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return Ext.util.Format.date(val, 'Y-m-d');
                    }
                }, {
                    header: 'Состояние',
                    dataIndex: 'custom_status',
                    width: 200,
                    sortable: false,
                    menuDisabled: true,
                    editor: {
                        id: 'customStatus',
                        xtype: 'textfield'
                    },
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    }
                }],
                maxHeight : 400,
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeQuestionsTracks,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                plugins: [editorTracks],
                renderTo: 'questionsTracks-grid'
            });
        });
    });
</script>

<div id="questionsTracks-grid"></div>