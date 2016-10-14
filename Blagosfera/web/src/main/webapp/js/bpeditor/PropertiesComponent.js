/**
 * Created by aotts on 21.10.2015.
 * Компонент, который отвечает за отображение и изменение параметров компонентов
 */
define([
    "./commands/UpdatePropertyValueCommand",
    "backbone",
    "oryx",
    "text!./template/PropertiesComponent.html",
    "require"
], function (UpdatePropertyValueCommand, Backbone, oryx, template, require) {
    return Backbone.View.extend({
        tagName: "div",

        template: _.template(template, {variable: 'title'}),

        /**
         * Экземпляр ORYX.Editor за которым будем следить
         */
        editor: null,

        /**
         * Компонент, который обрабатывался в последний раз
         */
        lastShownShape: null,

        /**
         * Модели, которые представление слушает в данный момент
         */
        listeningModels: null,

        /**
         * Экземпляры редакторов
         */
        editors: null,

        propertiesMapping: {
            "string": "./propertyEditor/ValidateTextEditor",
            "boolean": "./propertyEditor/BooleanEditor",
            "integer": "./propertyEditor/NumberEditor",
            "float": "./propertyEditor/NumberEditor",
            "color": "",
            "date": "./propertyEditor/DateEditor",
            "choice": "./propertyEditor/ChoiceEditor",
            "url": "",
            "text": "./propertyEditor/modal/TemplateModalTextEditor",
            "oryx-sequencefloworder-complex": "./propertyEditor/SequenceFlowOrderEditor",
            "kisbpm-multiinstance": "./propertyEditor/MultiInstanceEditor",
            "oryx-formproperties-complex": "./propertyEditor/modal/FormPropertiesModalEditor",
            "oryx-executionlisteners-multiplecomplex": "./propertyEditor/modal/ExecutionListenersModalEditor",
            "oryx-tasklisteners-multiplecomplex": "./propertyEditor/modal/TaskListenersModalEditor",
            "oryx-eventlisteners-multiplecomplex": "./propertyEditor/modal/EventListenersModalEditor",
            "oryx-usertaskassignment-complex": "./propertyEditor/modal/UserAssignmentsModalEditor",
            "oryx-servicetaskfields-complex": "./propertyEditor/modal/ClassFieldsModalEditor",
            "oryx-callactivityinparameters-complex": "./propertyEditor/modal/InParametersModalEditor",
            "oryx-callactivityoutparameters-complex": "./propertyEditor/modal/OutParametersModalEditor",
            "oryx-conditionsequenceflow-complex": "./propertyEditor/FlowConditionEditor",
            "oryx-signaldefinitions-multiplecomplex": "./propertyEditor/modal/SignalDefinitionsModalEditor",
            "oryx-signalref-string": "./propertyEditor/SignalRefEditor",
            "oryx-messagedefinitions-multiplecomplex": "./propertyEditor/modal/MessageDefinitionsModalEditor",
            "oryx-messageref-string": "./propertyEditor/MessageRefEditor",
            "scriptcomplex": "./propertyEditor/modal/ScriptModalEditor",
            "document_template_complex": "./propertyEditor/modal/DocumentTemplateModalEditor",
            "key_value_complex": "./propertyEditor/modal/KeyValueModalEditor",
            "multiplecomplex": "./propertyEditor/modal/ListModalEditor",
            "votings_template_complex": "./propertyEditor/modal/VotingsTemplateModalEditor"/*,
            "texteditor": "./propertyEditor/modal/TemplateModalTextEditor"*/
        },

        initialize: function (options) {
            if(options.propertiesMapping) {
                this.propertiesMapping = options.propertiesMapping;
            }
            this.editor = options.editor;
            this.editor.registerOnEvent(
                oryx.CONFIG.EVENT_SELECTION_CHANGED,
                _.bind(this.render, this)
            );
        },

        remove: function () {
            this.cleanUpResources();
            Backbone.View.prototype.remove.call(this);
        },

        cleanUpResources: function () {
            //если внутри какого либо редактора есть елемент с фокусом
            //сбрасываем его чтобы не потерять значение
            this.$(".children-container :focus").blur();
            if(this.listeningModels) {
                _.each(this.listeningModels, this.stopListening, this);
                this.listeningModels = null;
            }
            if(this.editors) {
                _.each(this.editors, function (editor) {
                    editor.remove();
                });
                this.editors = null;
            }
        },

        render: function () {
            var shapes = this.editor.getSelection();
            if (shapes && shapes.length == 0) {
                shapes = [this.editor.getCanvas()];
            }
            if (shapes.length == 1) {
                var shape = shapes.first();
                if(this.lastShownShape == shape) {
                    this.refreshingEditorsFromShape = true;
                    _.each(this.editors, function (editor) {
                        var shape = editor.model.get("shape");
                        var property = editor.model.get("property");
                        editor.model.set({
                            "value": this.parseValue(shape.properties[editor.model.get("key")], property),
                            "refresh": Number(!editor.model.get("refresh"))
                        });
                    }, this);
                    this.refreshingEditorsFromShape = false;
                    return this;
                }
                this.lastShownShape = shape;
                this.cleanUpResources();

                var stencil = shape.getStencil();
                if (stencil) {
                    this.listeningModels = [];

                    var title = stencil.title();
                    var props = this.extractProperties(shape, stencil);

                    var classesToLoad = _(props).map(function (prop) {
                        if(prop.useTitleAsName) {
                            title = prop.model.get("value") || title;
                            this.listeningModels.push(prop.model);
                            this.listenTo(prop.model, "change:value", function (model, value) {
                                this.$(".component-title").text(value || stencil.title());
                            });
                        }
                        return prop.editorClass;
                    }, this).uniq();

                    this.$el.html(this.template(title));
                    var transaction = this._transaction = _.uniqueId();

                    require(classesToLoad, _.bind(function () {
                        if(transaction !== this._transaction) {
                            return;
                        }
                        this.editors = [];
                        var fragment = document.createDocumentFragment();
                        for (var i = 0; i < props.length; i++) {
                            var prop = props[i];
                            var editor = this.createEditor(require(prop.editorClass), prop.model);
                            this.editors.push(editor);
                            fragment.appendChild(editor.render().el);
                        }
                        this.$(".children-container").append(fragment);
                    }, this));

                    return this;
                }
            }
            this.cleanUpResources();
            this.$el.html(this.template("Для изменения параметров выберите компонент"));
            return this;
        },

        createEditor: function (editorClass, model) {
            return new editorClass({model: model});
        },

        extractProperties: function (shape, stencil) {
            if(!stencil) {
                stencil = shape.getStencil();
            }
            var properties = stencil.properties();
            var result = [];
            for (var i = 0; i < properties.length; i++) {
                var property = properties[i];
                if (!property.popular() || property.isHidden()) {
                    continue;
                }
                var editorClass = this.extractEditorClass(property);
                if(!editorClass) {
                    continue;
                }
                var propertyId = property.id();

                result.push({
                    editorClass: editorClass,
                    useTitleAsName: propertyId === "name",
                    model: this.createModel(property, shape)
                });
            }
            return result;
        },

        extractEditorClass: function (property) {
            var type = property.type().toLowerCase();
            //TODO тут можно сделать поддержку кастомных редакторов
            return this.propertiesMapping[property.prefix() + "-" + property.id() + "-" + type] ||
                this.propertiesMapping[property.prefix() + "-" + type] ||
                this.propertiesMapping[type];

        },

        createModel: function (property, shape) {
            var key = property.prefix() + "-" + property.id();
            var value = shape.properties[key];
            var model = new Backbone.Model({
                id: property.id(),
                key: key,
                title: property.title(),
                description: property.description(),
                required: !property.optional(),
                value: this.parseValue(value, property),
                property: property,
                shape: shape,
                refresh: 0//переменная, которая изменяется при каждом обновлении модели из компонента
            });
            this.addModelEvents(model, property, shape);
            return model;
        },

        parseValue: function (value, property) {
            var type = property.type().toLowerCase();
            if (typeof value === "string" && (type.indexOf('complex') !== -1) && value.length > 0) {
                try {
                    value = JSON.parse(value);
                    if(typeof value === "string") {
                        value = JSON.parse(value);
                    }
                } catch(e) {
                }
            }
            return value;
        },

        addModelEvents: function (model, property, shape) {
            this.listeningModels.push(model);
            this.listenTo(model, "change:value", function (model, value) {
                if(!this.refreshingEditorsFromShape) {
                    this.editor.executeCommands([
                        new UpdatePropertyValueCommand(property, shape, value, this.editor)
                    ]);
                }
            });
        }

    });
});