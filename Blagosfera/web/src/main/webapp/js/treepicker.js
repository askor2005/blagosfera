/**
 * @class Ext.ux.TreePicker
 * @extends Ext.form.field.Picker
 *
 * A Picker field that contains a tree panel on its popup, enabling selection of tree nodes.
 * 下拉树，摘自Extjs4.1.1
 *
 * @last modify 2012-08-02 17:28:05 by mashanling
 */
Ext.define('Ext.ux.TreePicker', {
    extend: 'Ext.form.field.Picker',
    xtype: 'treepicker',

    triggerCls: Ext.baseCSSPrefix + 'form-arrow-trigger',

    config: {
        /**
         * @cfg {Ext.data.TreeStore} store
         * A tree store that the tree picker will be bound to
         */
        store: null,

        /**
         * @cfg {String} displayField
         * The field inside the model that will be used as the node's text.
         * Defaults to the default value of {@link Ext.tree.Panel}'s `displayField` configuration.
         */
        displayField: null,

        valueField: null,//选中值对应字段名称 by mashanling on 2012-08-02 17:49:31

        /**
         * @cfg {Array} columns
         * An optional array of columns for multi-column trees
         */
        columns: null,

        /**
         * @cfg {Boolean} selectOnTab
         * Whether the Tab key should select the currently highlighted item. Defaults to `true`.
         */
        selectOnTab: true,

        /**
         * @cfg {Number} maxPickerHeight
         * The maximum height of the tree dropdown. Defaults to 300.
         */
        maxPickerHeight: 300,

        /**
         * @cfg {Number} minPickerHeight
         * The minimum height of the tree dropdown. Defaults to 100.
         */
        minPickerHeight: 100,

        panelId: Ext.id()
    },
    editable: false,
    initComponent: function() {
        var me = this;
        me.callParent(arguments);

        //this.addEvents(
        //    /**
        //     * @event select
        //     * Fires when a tree node is selected
        //     * @param {Ext.ux.TreePicker} picker        This tree picker
        //     * @param {Ext.data.Model} record           The selected record
        //     */
        //    'select'
        //);

        me.store.on('load', me.onLoad, me);
    },

    /**
     * Creates and returns the tree panel to be used as this field's picker.
     * @private
     */
    createPicker: function() {
        var me = this,
            picker = Ext.create('Ext.tree.Panel', {
                id: me.panelId,
                useArrows: true,
                rootVisible: true,
                multiSelect: false,
                store: me.store,
                floating: true,
                hidden: true,
                displayField: me.displayField,
                columns: me.columns,
                maxHeight: me.maxTreeHeight,
                shadow: true,
                frame: true,
                manageHeight: false,
                listeners: {
                    itemclick: Ext.bind(me.onItemClick, me)
                },
                viewConfig: {
                    listeners: {
                        render: function(view) {
                            view.getEl().on('keypress', me.onPickerKeypress, me);
                        }
                    }
                }
            }),
            view = picker.getView();

        view.on('render', me.setPickerViewStyles, me);

        if (Ext.isIE9 && Ext.isStrict) {
            // In IE9 strict mode, the tree view grows by the height of the horizontal scroll bar when the items are highlighted or unhighlighted.
            // Also when items are collapsed or expanded the height of the view is off. Forcing a repaint fixes the problem.
            view.on('highlightitem', me.repaintPickerView, me);
            view.on('unhighlightitem', me.repaintPickerView, me);
            view.on('afteritemexpand', me.repaintPickerView, me);
            view.on('afteritemcollapse', me.repaintPickerView, me);
        }
        return picker;
    },

    /**
     * Sets min/max height styles on the tree picker's view element after it is rendered.
     * @param {Ext.tree.View} view
     * @private
     */
    setPickerViewStyles: function(view) {
        view.getEl().setStyle({
            'min-height': this.minPickerHeight + 'px',
            'max-height': this.maxPickerHeight + 'px'
        });
    },

    /**
     * repaints the tree view
     */
    repaintPickerView: function() {
        var style = this.picker.getView().getEl().dom.style;

        // can't use Element.repaint because it contains a setTimeout, which results in a flicker effect
        style.display = style.display;
    },

    /**
     * Aligns the picker to the input element
     * @private
     */
    alignPicker: function() {
        var me = this,
            picker;

        if (me.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                // Auto the height (it will be constrained by max height)
                picker.setWidth(me.bodyEl.getWidth());
            }
            if (picker.isFloating()) {
                me.doAlign();
            }
        }
    },

    /**
     * Handles a click even on a tree node
     * @private
     * @param {Ext.tree.View} view
     * @param {Ext.data.Model} record
     * @param {HTMLElement} node
     * @param {Number} rowIndex
     * @param {Ext.EventObject} e
     */
    onItemClick: function(view, record, node, rowIndex, e) {
        this.selectItem(record);
    },

    /**
     * Handles a keypress event on the picker element
     * @private
     * @param {Ext.EventObject} e
     * @param {HTMLElement} el
     */
    onPickerKeypress: function(e, el) {
        var key = e.getKey();
        if(key === e.ENTER || (key === e.TAB && this.selectOnTab)) {
            this.selectItem(this.picker.getSelectionModel().getSelection()[0]);
        }
    },

    /**
     * Changes the selection to a given record and closes the picker
     * @private
     * @param {Ext.data.Model} record
     */
    selectItem: function(record) {
        var me = this;
        me.setValue(record.get(me.valueField || record.idProperty || 'id'));
        me.picker.hide();
        me.inputEl.focus();
        me.fireEvent('select', me, record)
    },

    /**
     * Runs when the picker is expanded.  Selects the appropriate tree node based on the value of the input element,
     * and focuses the picker so that keyboard navigation will work.
     * @private
     */
    onExpand: function() {
        var me = this,
            picker = me.picker,
            store = picker.store,
            value = me.value;

        if(value) {
            picker.selectPath(store.getNodeById(value).getPath());
        } else {
            picker.getSelectionModel().select(store.getRootNode());
        }

        Ext.defer(function() {
            picker.getView().focus();
        }, 1);
    },

    /**
     * Sets the specified value into the field
     * @param {Mixed} value
     * @return {Ext.ux.TreePicker} this
     */
    setValue: function(value) {
        var me = this,
            record;

        me.value = value;

        if (me.store.loading) {
            // Called while the Store is loading. Ensure it is processed by the onLoad method.
            return me;
        }

        // try to find a record in the store that matches the value
        record = value ? me.store.getNodeById(value) : me.store.getRootNode();

        // set the raw value to the record's display field if a record was found
        me.setRawValue(record ? record.get(this.displayField) : '');

        return me;
    },


    /**
     * Returns the current data value of the field (the idProperty of the record)
     * @return {Number}
     */
    getValue: function() {
        return this.value;
    },

    /**
     * Handles the store's load event.
     * @private
     */
    onLoad: function() {
        var value = this.value;

        if (value) {
            this.setValue(value);
        }
    }
});