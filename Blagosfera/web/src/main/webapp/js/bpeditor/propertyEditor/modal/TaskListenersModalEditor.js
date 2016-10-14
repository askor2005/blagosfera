/**
 * Created by aotts on 27.10.2015.
 * Редактор слушателей жизненного цикла задания
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "utils/utils",
    "backbone/components/ListView",
    "./MulticomplexValueModalEditor",
    "backbone",
    "jquery",
    "text!./template/TaskListenersModal.html"
], function (CollectionBinder, TableRowView, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate) {
    var eventRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    '<strong name="id"></strong>',
                    TableRowView.templates.select("event", [
                        {value: "create", text: "Создание"},
                        {value: "assignment", text: "Вызов"},
                        {value: "complete", text: "Завершение"},
                        {value: "delete", text: "Удаление"}
                    ]),
                    TableRowView.templates.input("value"),
                    TableRowView.templates.select("type", [
                        {value: "className", text: "Класс"},
                        {value: "expression", text: "Выражение"},
                        {value: "delegateExpression", text: "Делегатное выражение"}
                    ]),
                    TableRowView.templates.minusBtn()
                ]
            });
        });
    };

    var attributeRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                rowConfig: [
                    TableRowView.templates.input("name"),
                    function (data) {
                        if(data.type === "string") {
                            return TableRowView.templates.textarea("value");
                        }
                        return TableRowView.templates.input("value");
                    },
                    TableRowView.templates.select("type", [
                        {value: "stringValue", text: "Строка"},
                        {value: "expression", text: "Выражение"},
                        {value: "string", text: "Текст"}
                    ]),
                    TableRowView.templates.minusBtn()
                ]
            });
        });
    };

    var toFieldValue = function (value) {
        var res = {
            field: value.name || "",
            stringValue: "",
            expression: "",
            string: ""
        };
        res[value.type] = value.value;
        return res;
    };

    var fromFieldValue = function (value) {
        var type;
        var val;
        if(val = value.stringValue) {
            type = "stringValue"
        } else {
            val = value.expression;
            type = "expression"
        }
        return {
            name: value.field || "",
            type: type,
            value: val || ""
        };
    };

    var toModelValue = function (model) {
        var fields = _.map(model.get("attributes"), toFieldValue);
        var res = {
            event: model.get("event") || "assignment",
            className: "",
            expression: "",
            delegateExpression: "",
            fields: fields.length === 0 ? null : fields
        };
        res[model.get("type")] = model.get("value") || "";
        return res;
    };


    var fromModelValue = function (value, i) {
        var type;
        var val;
        if(val = value.delegateExpression) {
            type = "delegateExpression"
        } else if(val = value.expression) {
            type = "expression"
        } else {
            val = value.className;
            type = "className"
        }
        return {
            id: i + 1,
            event: value.event || "assignment",
            type: type,
            value: val || "",
            attributes: _.map(value.fields || [], fromFieldValue)
        };
    };

    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        eventsList: null,
        attributesList: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue"),
            "click table[name=events] .addItem": utils.clickEventWrapper("addEvent"),
            "click table[name=attributes] .addItem": utils.clickEventWrapper("addAttr"),
            "click table[name=events] tr": "selectItemByEvent"
        },

        initialize: function () {
            this.eventsList = new ListView({
                childFactory: eventRowFactory()
            });
            this.attributesList = new ListView({
                childFactory: attributeRowFactory(),
                collectionAttr: "attributes"
            });
            this.bindEventTableRows();
            this.bindAttributesTableRows();
            this.applyModelValue();
            this.bindListCollection();
        },

        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            var $eventsTableBody = this.$("table[name=events] tbody");
            this.eventsList.setElement($eventsTableBody).render();
            var $attributesTableBody = this.$("table[name=attributes] tbody");
            this.attributesList.setElement($attributesTableBody).render();
            return this;
        },

        remove: function () {
            this.eventsList.remove();
            this.attributesList.remove();
            Backbone.View.prototype.remove.call(this);
        },

        bindListCollection: function () {
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
        },

        applyModelValue: function () {
            var index = this.eventsList.$("tr.info").index();
            if(index === -1) {
                index = 0;
            }
            var value = this.model.get("value");
            value = value && value.taskListeners || [];
            if(typeof value === "string") {
                try {
                    value = JSON.parse(value);
                } catch(e) {
                    value = [];
                }
            }
            var data = _.map(value, fromModelValue, this);
            if(data.length <= index) {
                index = data.length - 1;
            }
            this.eventsList.getCollection().set(data);
            if(index >= 0) {
                var model = this.eventsList.getCollection().at(index);
                this.selectItemByModel(model);
            } else {
                this._selectItemByManager(null);
            }
        },

        saveValue: function () {
            var value = this.eventsList.getCollection().map(toModelValue);
            if(value.length === 0) {
                value = null;
            }
            this.model.set("value", !value ? null : {
                taskListeners: value
            });
        },

        addEvent: function () {
            var collection = this.eventsList.getCollection();
            collection.add({id: collection.length + 1, event: "assignment", value: "", type: "className", attributes: []});
        },

        addAttr: function () {
            var collection = this.attributesList.getCollection();
            collection.add({name: "", value: "", type: "stringValue"})
        },

        bindEventTableRows: function () {
            var table = this.eventsList;
            this.listenTo(table.collectionBinder, "elCreated", function (model, el) {
                $(el).on("click", "a", _.bind(function (e) {
                    if(this.attributesList.model == model) {
                        this.selectItemByEl(el.prev()[0] || el.next()[0]);
                    }
                    table.getCollection().remove(model);
                    if(e.which === 1 || e.which === 2) {
                        e.preventDefault();
                    }
                }, this));
                this.selectItemByModel(model);
            });
        },

        bindAttributesTableRows: function () {
            var table = this.attributesList;
            this.listenTo(table.collectionBinder, "elCreated", function (model, view) {
                view.$el.on("click", "a", function () {
                    table.getCollection().remove(model);
                });
            });
        },
        
        selectItemByEvent: function (e) {
            this.selectItemByEl(e.target);
        },

        selectItemByEl: function (el) {
            var manager = this.eventsList.collectionBinder.getManagerForEl(el);
            this._selectItemByManager(manager);
        },
        
        selectItemByModel: function (model) {
            var manager = this.eventsList.collectionBinder.getManagerForModel(model);
            this._selectItemByManager(manager);
        },

        _selectItemByManager: function (manager) {
            this.eventsList.$("tr").removeClass("info");
            if(manager) {
                $(manager.getEl()).addClass("info");
                this.attributesList.setModel(manager.getModel());
                this.$("table[name=attributes] .addItem").show();
            } else {
                this.$("table[name=attributes] .addItem").hide();
                this.attributesList.setModel(null);
            }
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});