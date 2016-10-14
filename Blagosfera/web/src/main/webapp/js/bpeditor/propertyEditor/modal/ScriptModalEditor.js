/**
 * Created by aotts on 27.10.2015.
 * Редактор пользователей, которые могут выполнять UserTask
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "utils/utils",
    "backbone/components/ListView",
    "./MulticomplexValueModalEditor",
    "backbone",
    "jquery",
    "text!./template/ScriptModal.html",
    "codemirror/codemirror",
    "codemirror/show-hint",
    "codemirror/javascript",
    "codemirror/javascript-hint",
    "css!/css/codemirror.css"
], function (CollectionBinder, TableRowView, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate, CodeMirror) {
    var contextRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    TableRowView.templates.minusBtn(),
                    TableRowView.templates.input("key"),
                    TableRowView.templates.input("value")
                ]
            });
        });
    };

    var outputRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    TableRowView.templates.input("key"),
                    TableRowView.templates.input("value"),
                    TableRowView.templates.minusBtn()
                ]
            });
        });
    };

    var notEmpty = function (value) {
        return value && value.key && value.key.length > 0 && value.value && value.value.length > 0;
    };

    var convert = function (model) {
        return model.toJSON();
    };

    var toObj = function (val, key) {
        return {key: key, value: val};
    };

    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        contextList: null,
        outputList: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue"),
            "click table[name=context] .addItem": utils.clickEventWrapper("addContextRow"),
            "click table[name=output] .addItem": utils.clickEventWrapper("addOutputRow")
        },

        initialize: function () {
            this.contextList= new ListView({
                childFactory: contextRowFactory()
            });
            this.outputList = new ListView({
                childFactory: outputRowFactory()
            });
            this.bindTableRows(this.contextList);
            this.bindTableRows(this.outputList);
            this.bindCurrentState();
        },

        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            this.applyModelValue();
            var $contextTableBody = this.$("table[name=context] tbody");
            this.contextList.setElement($contextTableBody).render();
            var $outputTableBody = this.$("table[name=output] tbody");
            this.outputList.setElement($outputTableBody).render();
            this.editor = CodeMirror.fromTextArea(this.$("textarea")[0], {
                lineNumbers: true,
                extraKeys: {"Ctrl-Space": "autocomplete"},
                mode: "javascript"
            });
            return this;
        },

        remove: function () {
            this.contextList.remove();
            this.outputList.remove();
            this.editor.toTextArea();
            Backbone.View.prototype.remove.call(this);
        },

        bindCurrentState: function () {
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
        },

        applyModelValue: function () {
            var value = this.model.get("value");
            var context = _.map(value && value.context || [], toObj);
            var output = _.map(value && value.outputMapping || [], toObj);
            this.contextList.getCollection().set(context);
            this.outputList.getCollection().set(output);
            if(this.editor) {
                this.editor.setValue(value && value.script || "");
                _.defer(_.bind(this.editor.refresh, this.editor));
            }
        },

        saveValue: function () {
            var script = this.editor.getValue() || "";
            var context = {};
            this.contextList.getCollection().chain().map(convert).filter(notEmpty).uniq().each(function (obj) {
                context[obj.key] = obj.value;
            });
            var output = {};
            this.outputList.getCollection().chain().map(convert).filter(notEmpty).uniq().each(function (obj) {
                output[obj.key] = obj.value;
            });
            var res = null;
            if(script && script.length || _.size(context) || _.size(output)) {
                res = {
                    script: script,
                    context: context,
                    outputMapping: output
                };
            }
            this.model.set("value", res);
        },

        addContextRow: function () {
            var collection = this.contextList.getCollection();
            collection.add({key: "", value: ""});
        },

        addOutputRow: function () {
            var collection = this.outputList.getCollection();
            collection.add({key: "", value: ""})
        },

        bindTableRows: function (table) {
            this.listenTo(table.collectionBinder, "elCreated", function (model, view) {
                view.$el.on("click", "a", function () {
                    table.getCollection().remove(model);
                });
            });
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});