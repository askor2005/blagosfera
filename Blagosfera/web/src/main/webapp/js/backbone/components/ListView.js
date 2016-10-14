/**
 * Created by aotts on 23.10.2015.
 * Списковое отображение
 */
define([
    "./ViewWithModelBinder",
    "backbone",
    "Backbone.CollectionBinder"
], function (ViewWithModelBinder, Backbone, CollectionBinder) {
    return ViewWithModelBinder.extend({
        tagName: "ul",
        className: "list-group",
        template: null,
        model: null,
        /**
         * Если не предоставлена фабрика {@link #childFactory}, то этот шаблон использует для создания фабрики
         */
        childTemplate: _.template('<li class="list-group-item"></li>'),
        /**
         * Если не предоставлена фабрика {@link #childFactory}, то эти биндинги использует для создания фабрики
         */
        childBindings: {
            "value": ''
        },
        /**
         * Фабрика элементов коллекции
         */
        childFactory: null,
        /**
         * Атрибут который должен быть взять у модели, чтобы использовать его в качестве коллекции
         */
        collectionAttr: null,

        /**
         * Коллекция для отображения
         */
        collection: null,

        collectionClass: Backbone.Collection,

        collectionBinder: null,

        _internalCollectionChanging: false,

        initialize: function (options) {
            ViewWithModelBinder.prototype.initialize.apply(this, arguments);
            _.extend(this, _.pick(options, "rowClass", "collectionAttr", "childTemplate", "childBindings", "childFactory", "collectionClass"));
            this.collectionBinder = new CollectionBinder(this.getChildFactory());
        },
        
        remove: function () {
            this.collectionBinder.unbind();
            ViewWithModelBinder.prototype.remove.call(this);
        },

        render: function () {
            if(this.template) {
                this.$el.html(this.getTemplateParams());
            }
            if(this.model instanceof Backbone.Model) {
                this.applyBindings();
            }
            this.collectionBinder.bind(this.getCollection(), this.getChildContainerEl());
            return this;
        },

        getTemplateParams: function () {
            return this.model.toJSON();
        },
        
        setModel: function (model) {
            if(this.model != model) {
                if(this.collection) {
                    this.stopListening(this.collection);
                    this.collection = null;
                }
                if(this.model) {
                    this.stopListening(this.model);
                }
                this.model = model;
                if(this.model instanceof Backbone.Model) {
                    this.applyBindings();
                }
                this.collectionBinder.bind(this.getCollection(), this.getChildContainerEl());
            }
        },

        getCollection: function () {
            return this.collection || (this.collection = this.extractCollection());
        },

        extractCollection: function () {
            var collectionAttr = this.collectionAttr;
            var model = this.model;
            if(collectionAttr && model) {
                var modelCollection = model.get(collectionAttr);
                var collection;
                if(!modelCollection || _.isArray(modelCollection)) {
                    collection = new this.collectionClass(modelCollection || [], this.collectionClass);
                    this.listenTo(collection, "change update", function () {
                        var arr = collection.toJSON();
                        this.changeCollectionInternal(model.set, model, collectionAttr, arr);
                    });
                    this.listenTo(model, "change:"+collectionAttr, function (model, value) {
                        if(!this._internalCollectionChanging) {
                            value = value || [];
                            if(!_.isEqual(value, collection.toJSON())) {
                                var changed = false;
                                var fn = function () {
                                    changed = true;
                                };
                                this.listenToOnce(collection, "change update", fn);
                                collection.set(value);
                                if (!changed) {
                                    this.collectionBinder._onCollectionReset();
                                    this.trigger("force-collection-reset");
                                    this.stopListening(collection, "change update", fn);
                                }
                            }
                        }
                    });
                } else {
                    collection = modelCollection;
                }
                return collection;
            } else {
                return new this.collectionClass([]);
            }
        },

        changeCollectionInternal: function (fun, context) {
            this._internalCollectionChanging = true;
            fun.apply(context || this, _.tail(arguments, 2));
            this._internalCollectionChanging = false;
        },

        getChildFactory: function () {
            return this.childFactory ||
                (this.childFactory = new CollectionBinder.ElManagerFactory(this.childTemplate, this.childBindings));
        },

        getChildContainerEl: function () {
            return this.el;
        }
    });
});