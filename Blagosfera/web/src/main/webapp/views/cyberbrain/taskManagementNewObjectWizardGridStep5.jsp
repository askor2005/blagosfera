<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            function rowEditorClearFields() {
                Ext.getCmp('lifecycleStatus').setRawValue('');
                Ext.getCmp('timeReadyStep5').setValue('');
                Ext.getCmp('customStatus').setValue('');
            }

            function rowEditorCheckFields() {
                var customStatus = Ext.getCmp('customStatus').getValue();
                var timeReadyStep5 = Ext.getCmp('timeReadyStep5').getValue();
                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getValue();

                if (lifecycleStatus == null || lifecycleStatus == "" ) {
                    bootbox.alert("Нужно указать состояние ЖЦ объекта!");
                    return false;
                }

                if (timeReadyStep5 == null || timeReadyStep5 == "" ) {
                    bootbox.alert("Нужно указать дату готовности объекта!");
                    return false;
                }

                if (customStatus == null || customStatus == "" ) {
                    bootbox.alert("Нужно указать состояние объекта!");
                    return false;
                }

                return true;
            }

            Ext.define('Ext.grid.CustomRowEditorButtonsStep5', {
                extend: 'Ext.grid.RowEditorButtons',
                alias : 'widget.customroweditorbuttonsstep5',
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
                            id: 'addTrackStep5',
                            text: 'Добавить трек',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (!rowEditorCheckFields()) {
                                    return false;
                                }

                                var customStatus = Ext.getCmp('customStatus').getValue();
                                var timeReadyStep5 = Ext.getCmp('timeReadyStep5').getValue();
                                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getRawValue();

                                record.custom_status = customStatus;

                                var r =  lifecycleStatusStore.find('name', lifecycleStatus);
                                record.lifecycle_status = lifecycleStatusStore.getAt(r).data.id;

                                record.time_ready = timeReadyStep5;

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/addNewTrack',
                                    jsonData : record,
                                    success  : function(response){
                                        rowEditorClearFields();
                                        editor.cancelEdit();
                                        storeStep5.load();
                                    }
                                });
                            }
                        }, {
                            id: 'updateStep5',
                            text: 'Сохранить',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (!rowEditorCheckFields()) {
                                    return false;
                                }

                                var customStatus = Ext.getCmp('customStatus').getValue();
                                var timeReadyStep5 = Ext.getCmp('timeReadyStep5').getValue();
                                var lifecycleStatus = Ext.getCmp('lifecycleStatus').getRawValue();

                                record.custom_status = customStatus;

                                var r =  lifecycleStatusStore.find('name', lifecycleStatus);
                                record.lifecycle_status = lifecycleStatusStore.getAt(r).data.id;

                                record.time_ready = timeReadyStep5;

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/saveTrack',
                                    jsonData : record,
                                    success  : function(response){
                                        rowEditorClearFields();
                                        editor.cancelEdit();
                                        storeStep5.load();
                                    }
                                });
                            }
                        }, {
                            id: 'finishTrackStep5',
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
                                        storeStep5.load();
                                    }
                                });
                            }
                        }, {
                            id: 'cancelStep5',
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

            Ext.define('Ext.grid.CustomRowEditorStep5', {
                extend: 'Ext.grid.RowEditor',
                alias : 'widget.customroweditorstep5',
                requires: [
                    'Ext.grid.CustomRowEditorButtonsStep5'
                ],
                initComponent: function () {
                    this.callParent(arguments);
                },
                getFloatingButtons: function() {
                    var me = this,
                            btns = me.floatingButtons;

                    if (!btns) {
                        me.floatingButtons = btns = new Ext.grid.CustomRowEditorButtonsStep5({
                            rowEditor: me
                        });
                    }
                    return btns;
                }
            });

            Ext.define('Ext.grid.plugin.CustomRowEditingStep5', {
                extend: 'Ext.grid.plugin.RowEditing',
                alias : 'widget.customroweditingstep5',
                clicksToEdit: 1,
                requires: [
                    'Ext.grid.CustomRowEditorStep5'
                ],
                initEditor: function() {
                    return new Ext.grid.CustomRowEditorStep5(this.initEditorConfig());
                }
            });

            var editorStep5 = Ext.create('Ext.grid.plugin.CustomRowEditingStep5', {});

            lifecycleStatusStore = Ext.create('Ext.data.Store', {
                id		: 'step5Store',
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

            storeStep5 = Ext.create('Ext.data.Store', {
                id		: 'step5Store',
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
                    url: 'taskManagement/new_object_wizard_form_questions_tracks.json',
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
                        if (tagId > 0) {
                            store.proxy.extraParams.tagOwnerId = tagId;
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'step5Grid',
                title	: 'Введите трек для объектов которые нужно создавать',
                store: storeStep5,
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
                        id: 'timeReadyStep5',
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
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeStep5,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                plugins: [editorStep5],
                height: 300,
                maxHeight: 300,
                frame: true,
                renderTo: 'step5-grid'
            });
        });
    });
</script>