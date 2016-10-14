/**
 * Created by aotts on 26.10.2015.
 * Редактор входных сообщений процесса
 */
define([
    "utils/utils",
    "backbone/components/ListView",
    "./MulticomplexValueModalEditor",
    "backbone",
    "jquery",
    "text!./template/MessageDefinitionsModal.html"
], function (utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate) {
    var childTemplate = _.template('' +
        '<tr id="<%-data.itemId%>">' +
        '   <td><input class="form-control" type="text" name="id"/></td>' +
        '   <td><input class="form-control" type="text" name="name"/></td>' +
        '   <td>' +
        '       <a href="#" class="btn btn-danger btn-sm">' +
        '           <span class="glyphicon glyphicon-minus"></span>' +
        '       </a>' +
        '   </td>' +
        '</tr>',
        {variable: "data"}
    );

    var toDefinition = function (model) {
        return {
            id: model.get("id") || "",
            name: model.get("name") || ""
        }
    };

    var MessageCollection = Backbone.Collection.extend({
        modelId: function (attrs) {
            return attrs["itemId"];
        }
    });

    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        eventsList: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue"),
            "click .addItem": utils.clickEventWrapper("addNew")
        },

        initialize: function () {
            this.eventsList = new ListView({
                childTemplate: childTemplate,
                childBindings: "name",
                collectionClass: MessageCollection
            });
            this.bindTableRows();
            this.applyModelValue();
            this.bindListCollection()
        },

        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            var $tableBody = this.$("tbody");
            this.eventsList.setElement($tableBody).render();
            return this;
        },

        remove: function () {
            this.eventsList.remove();
            Backbone.View.prototype.remove.call(this);
        },

        bindListCollection: function () {
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
        },

        applyModelValue: function () {
            var data = _.map(this.model.get("value") || [], function (item, i) {
                return _.extend({itemId: this.cid + "-" + i}, item);
            }, this);
            this.eventsList.getCollection().set(data);
        },

        saveValue: function () {
            var value = this.eventsList.getCollection().map(toDefinition);
            if(value.length === 0) {
                value = null;
            }
            this.model.set("value", value);
        },

        addNew: function () {
            var collection = this.eventsList.getCollection();
            collection.add({id: "", name: "", scope: "global", itemId: this.cid + "-" + collection.length})
        },

        bindTableRows: function () {
            this.listenTo(this.eventsList.collectionBinder, "elCreated", function (model, el) {
                var self = this;
                $(el).on("click", "a", function () {
                    self.eventsList.getCollection().remove(model);
                });
            });
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});