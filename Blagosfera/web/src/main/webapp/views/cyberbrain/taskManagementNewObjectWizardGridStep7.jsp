<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            Ext.define('Ext.grid.CustomRowEditorButtons', {
                extend: 'Ext.grid.RowEditorButtons',
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
                            id: 'addItemStep7',
                            text: 'Добавить',
                            handler: function(editor, context, eOpts) {
                                var record = this.getEditor().getRecord().data;
                                var newRecord = {
                                    knowledge_rep_id : record.knowledge_rep_id,
                                    many_name : record.many_name,
                                    object_name : record.object_name,
                                    status_from : record.status_from,
                                    status_to : record.status_to,
                                    many_name_changeif : "",
                                    object_name_changeif : "",
                                    read_only : 0};

                                storeStep7.add(newRecord);
                            }
                        }, {
                            id: 'updateStep7',
                            text: 'Сохранить',
                            handler: function(editor, context, eOpts) {
                                var editor = this.getEditor();
                                var record = editor.getRecord().data;

                                if (record.read_only == 0) {
                                    var manyNameChangeif = Ext.getCmp("manyNameChangeif").getValue();
                                    var objectNameChangeif = Ext.getCmp("objectNameChangeif").getValue();

                                    record.many_name_changeif = manyNameChangeif;
                                    record.object_name_changeif = objectNameChangeif;

                                    storeStep7.update(record);
                                } else {
                                    bootbox.alert("Редактировать это влияние нельзя так как оно уже созданно!");
                                }

                                editor.cancelEdit();
                            }
                        }, {
                            id: 'cancelStep7',
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

            Ext.define('Ext.grid.CustomRowEditor', {
                extend: 'Ext.grid.RowEditor',
                requires: [
                    'Ext.grid.CustomRowEditorButtons'
                ],
                initComponent: function () {
                    this.callParent(arguments);
                },
                getFloatingButtons: function() {
                    var me = this,
                            btns = me.floatingButtons;

                    if (!btns) {
                        me.floatingButtons = btns = new Ext.grid.CustomRowEditorButtons({
                            rowEditor: me
                        });
                    }
                    return btns;
                }
            });

            Ext.define('Ext.grid.plugin.CustomRowEditing', {
                extend: 'Ext.grid.plugin.RowEditing',
                clicksToEdit: 1,
                requires: [
                    'Ext.grid.CustomRowEditor'
                ],
                initEditor: function() {
                    return new Ext.grid.CustomRowEditor(this.initEditorConfig());
                }
            });

            var editorStep7 = Ext.create('Ext.grid.plugin.CustomRowEditing', {});

            storeStep7 = Ext.create('Ext.data.Store', {
                id		: 'step7Store',
                fields  : [
                    'knowledge_rep_id',
                    'many_name',
                    'object_name',
                    'status_from',
                    'status_to',
                    'many_name_changeif',
                    'object_name_changeif',
                    'read_only']
            });

            Ext.create('Ext.grid.Panel', {
                id : 'step7Grid',
                title	: 'Переход из состояния в состояние обусловлен следующим',
                store: storeStep7,
                columns: [{
                    header: 'Множество',
                    dataIndex: 'many_name',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Объект',
                    dataIndex: 'object_name',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Состояние из',
                    dataIndex: 'status_from',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Состояние в',
                    dataIndex: 'status_to',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Множество условие',
                    dataIndex: 'many_name_changeif',
                    editor: {
                        id: 'manyNameChangeif',
                        xtype: 'textfield'
                    },
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    },
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Объект условие',
                    dataIndex: 'object_name_changeif',
                    editor: {
                        id: 'objectNameChangeif',
                        xtype: 'textfield'
                    },
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    },
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }],
                maxHeight: 300,
                height: 300,
                plugins: [editorStep7],
                renderTo: 'step7-grid'
            });
        });
    });
</script>