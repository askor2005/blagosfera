/**
 * Created by aotts on 26.10.2015.
 * Редактор входных параметров посылаемых в вызов процесса
 */
define([
    "Backbone.CollectionBinder",
    "utils/utils",
    "backbone/components/ListView",
    "./MulticomplexValueModalEditor",
    "backbone",
    "jquery",
    "text!./template/SingleTableClassFactory.html"
], function (CollectionBinder, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate) {

    var getDottedValue = function (obj, attrs) {
        return _.reduce(attrs, function (obj, attr) {
            return obj && obj[attr];
        }, obj);
    };

    var setDottedValue =  function(obj, attrs, value) {
        var size = attrs.length - 1;
        var objValue = obj;
        for (var i = 0; i < size; i++) {
            var attr = attrs[i];
            objValue = objValue[attr] || (objValue[attr] = {});
        }
        objValue[attrs[size]] = value;
        return obj;
    };

    var defaultGetter = function (attr) {
        if(attr) {
            var attrs = attr.split(".");
            return function (model) {
                var value = model.get("value");
                value = getDottedValue(value, attrs);
                if(typeof value === "string") {
                    try {
                        value = JSON.parse(value);
                    } catch(e) {
                        value = [];
                    }
                }
                return value;
            }
        }
        return function (model) {
            return model.get("value")
        };
    };
    
    var defaultSetter = function (attr) {
        if(attr) {
            var attrs = attr.split(".");
            return function (model, value) {
                if(value.length === 0) {
                    value = null;
                } else {
                    value = setDottedValue({}, attrs, value);
                }
                model.set("value", value);
            };
        }
        return function (model, value) {
            if(value.length === 0) {
                value = null;
            }
            model.set("value", value);
        };
    };


    var noop = function (a) {
        return a;
    };

    var produceEmpty = function(i) {
        return {id: i};
    };


    return function (options) {
        var rowFactoryProvider = options.rowFactoryProvider;
        var attributeName = options.attributeName || null;
        var title = options.title || "";
        var getter = options.getter || defaultGetter(attributeName);
        var setter = options.setter || defaultSetter(attributeName);
        var fromModelValueConverter = options.fromModelValueConverter || noop;
        var toModelValueConverter = options.toModelValueConverter || noop;
        var header = options.header || [];
        var rowObjectFactory = options.rowObjectFactory || produceEmpty;
        var dialogWidth = options.dialogWidth;

        var ModalEditor = Backbone.View.extend({
            tagName: "div",
            className: "modal",
            template: _.template(modalTemplate, {variable: "data"}),
            model: null,
            tableList: null,
            title: title,
            header: header,
            dialogWidth: dialogWidth,

            events: {
                "click .accept": utils.clickEventWrapper("saveValue"),
                "click table .addItem": utils.clickEventWrapper("addRow")
            },

            initialize: function () {
                this.tableList = new ListView({
                    childFactory: rowFactoryProvider(),
                    collectionAttr: "attributes"
                });
                this.bindTableRows();
                this.applyModelValue();
                this.bindListCollection();
            },

            render: function () {
                this.$el.html(this.template({
                    title: _.result(this, "title"),
                    header: _.result(this, "header"),
                    dialogWidth: _.result(this, "dialogWidth")
                }));
                var $attributesTableBody = this.$("tbody");
                this.tableList.setElement($attributesTableBody).render();
                return this;
            },

            remove: function () {
                this.tableList.remove();
                Backbone.View.prototype.remove.call(this);
            },

            bindListCollection: function () {
                this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
                this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
            },

            applyModelValue: function () {
                var value = getter.call(this, this.model) || [];
                var data = _.map(value, fromModelValueConverter, this);
                this.tableList.getCollection().set(data);
            },

            saveValue: function () {
                var value = this.tableList.getCollection().map(toModelValueConverter, this);
                setter.call(this, this.model, value);
            },

            addRow: function () {
                var collection = this.tableList.getCollection();
                collection.add(rowObjectFactory.call(this, collection.length));
            },

            bindTableRows: function () {
                this.listenTo(this.tableList.collectionBinder, "elCreated", this.onRowCreated);
            },

            onRowCreated: function (model, view) {
                view.$el.on("click", "a.minusBtn", utils.clickEventWrapper(function () {
                    this.tableList.getCollection().remove(model);
                }, this));
            }
        });

        return MulticomplexValueModalEditor.extend({
            modalClass: ModalEditor
        });

    };


});