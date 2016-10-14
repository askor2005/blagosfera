/**
 *
 */
define([
    "utils/utils",
    "./MulticomplexValueModalEditor",
    "text!./template/TemplateModalTextEditor.html"
], function (utils, MulticomplexValueModalEditor, modalTemplate) {
    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        contentTextEditor: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue")
            /*"click .accept": utils.clickEventWrapper("saveValue"),
            "click table[name=events] .addItem": utils.clickEventWrapper("addEvent"),
            "click table[name=attributes] .addItem": utils.clickEventWrapper("addAttr"),
            "click table[name=events] tr": "selectItemByEvent"*/
        },

        initialize: function () {
            /*this.eventsList = new ListView({
                childFactory: eventRowFactory()
            });
            this.attributesList = new ListView({
                childFactory: attributeRowFactory(),
                collectionAttr: "attributes"
            });
            this.bindEventTableRows();
            this.bindAttributesTableRows();
            this.applyModelValue();
            this.bindListCollection();*/
        },

        render: function () {
            var self = this;
            /*this.$el.html(this.template(this.model.omit("property", "shape")));
            var $eventsTableBody = this.$("table[name=events] tbody");
            this.eventsList.setElement($eventsTableBody).render();
            var $attributesTableBody = this.$("table[name=attributes] tbody");
            this.attributesList.setElement($attributesTableBody).render();*/
            this.$el.html(modalTemplate);

            this.$el.on("shown.bs.modal", function() {
                try {
                    self.$("#contentTemplate").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#contentTemplate").radomTinyMCE({
                    useTempData : false,
                    onCreate: function (editor) {
                        self.contentTextEditor = editor;
                        /*setTimeout(function(){
                            self.contentTextEditor.setContent(self.model.get("value").data);
                        }, 500);*/
                        editor.on('init', function(args) {
                            console.debug(args.target.id);
                            self.contentTextEditor.setContent(self.model.get("value"));
                        });
                        //self.contentTextEditor.setContent(self.model.get("value").data);
                    }
                });
            });


            return this;
        },

        remove: function () {
            /*this.eventsList.remove();
            this.attributesList.remove();*/
            Backbone.View.prototype.remove.call(this);
        },

        saveValue: function () {
            this.model.set("value", this.contentTextEditor.getContent());
        },

        applyModelValue: function () {
            /*var index = this.eventsList.$("tr.info").index();
            if(index === -1) {
                index = 0;
            }
            var value = this.model.get("value");
            value = value && value.executionListeners || [];
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
            }*/
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});