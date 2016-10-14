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
    "text!./template/DocumentTemplateModal.html",
    "noty",
    "typeahead"
], function (CollectionBinder, TableRowView, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate, noty) {
    var participantsRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    function (data) {
                        return data.name;
                    },
                    TableRowView.templates.input("value")
                ]
            });
        });
    };

    var fieldsRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    function (data) {
                        return data.name;
                    },
                    TableRowView.templates.input("value")
                ]
            });
        });
    };

    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        participantsList: null,
        fieldsList: null,
        templateSelect: null,
        classSelect: null,
        documentOwner: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue")
        },

        initialize: function () {
            this.participantsList= new ListView({
                childFactory: participantsRowFactory()
            });
            this.fieldsList = new ListView({
                childFactory: fieldsRowFactory()
            });
            this.bindCurrentState();
        },

        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            var $participantsTableBody = this.$("div[name=participants] tbody");
            this.participantsList.setElement($participantsTableBody).render();
            var $fieldsTableBody = this.$("div[name=fields] tbody");
            this.fieldsList.setElement($fieldsTableBody).render();
            this.documentOwner = this.$("div[name=owner] input");
            this.initializeTemplateSelect();
            this.initializeClassSelect();
            this.applyModelValue();
            return this;
        },

        remove: function () {
            this.participantsList.remove();
            this.fieldsList.remove();
            this.templateSelect.remove();
            this.classSelect.remove();
            Backbone.View.prototype.remove.call(this);
        },

        bindCurrentState: function () {
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
        },

        applyModelValue: function () {
            var value = this.model.get("value");
            this.documentOwner.val(value && value.owner || "");
            this._template = value && value.template || null;
            this._templateClass = value && value.templateClass || null;
            if(this.templateSelect) {
                this.templateSelect.val(this._template && this._template.name || "");
                this.loadFields();
                this.loadParticipants();
            }
            if(this.classSelect) {
                this.classSelect.val(this._templateClass && this._templateClass.path || "");
            }
            this.updateState();
        },

        applyModelFields: function () {
            var value = this.model.get("value") || {};
            var fields = value.fields || {};
            this.fieldsList.getCollection().each(function (model) {
                var id = model.get("name");
                var field = fields[id];
                model.set("value", field && field.value || "");
            });
        },

        applyModelParticipants: function () {
            var value = this.model.get("value") || {};
            var participants = value.participants || {};
            this.participantsList.getCollection().each(function (model) {
                var id = model.get("id");
                var participant = participants[id];
                model.set("value", participant && participant.value || "");
            });
        },

        saveValue: function () {
            var res = null;
            var owner = this.documentOwner.val();
            if(owner) {
                res = {
                    owner: owner
                };
            }
            if(this._templateClass) {
                res = res || {};
                res.templateClass = this._templateClass;
                if(this._template) {
                    res.template = this._template;
                    var fields = {};
                    this.fieldsList.getCollection().each(function (model) {
                        if(model.get("value")) {
                            fields[model.get("name")] = model.toJSON();
                        }
                    });
                    var participants = {};
                    this.participantsList.getCollection().each(function (model) {
                        if(model.get("value")) {
                            participants[model.get("id")] = model.toJSON();
                        }
                    });
                    res.fields = fields;
                    res.participants = participants;
                }
            }
            this.model.set("value", res);
        },

        templateSelected: function (template) {
            this._template = {
                id: template.id,
                name: template.name
            };
            this.updateState();
            this.loadFields();
            this.loadParticipants();
        },

        templateClassSelected: function (obj) {
            this._templateClass = {
                id: obj.id,
                path: obj.pathName
            };
            this._template = null;
            this.templateSelect.val("");
            this.updateState();
        },

        updateState: function () {
            var classPresented = !!this._templateClass;
            var templatePresented = classPresented && this._template;
            this.$("div[name=template]")[classPresented ? "show" : "hide"]();
            this.$("div[name=fields]")[templatePresented ? "show" : "hide"]();
            this.$("div[name=participants]")[templatePresented ? "show" : "hide"]();
        },

        loadFields: function () {
            this.fieldsList.getCollection().reset([]);
            if(this._template) {
                this.$("div[name=fields] img").fadeIn("fast");
                var temp = this._template;
                $.ajax({
                    async: true,
                    type: "GET",
                    url: "/admin/docs/getTemplateUserFields",
                    datatype: "json",
                    data: {template_id : temp.id},
                    context: this,
                    success: function (data) {
                        if(_.isEqual(this._template, temp)) {//иначе уже просим другой шаблон и результаты не актуальны
                            this.fieldsList.getCollection().reset(data);
                            this.applyModelFields();
                        }
                    }
                }).always(_.bind(function () {
                    if(_.isEqual(this._template, temp)) {
                        this.$("div[name=fields] img").fadeOut("fast");
                    }
                }, this));
            } else {
                this.$("div[name=fields] img").fadeOut("fast");
            }
        },

        loadParticipants: function () {
            this.fieldsList.getCollection().reset([]);
            if(this._template) {
                var temp = this._template;
                this.$("div[name=participants] img").fadeIn("fast");
                $.ajax({
                    async: true,
                    type: "GET",
                    url: "/admin/docs/getTemplateParticipants",
                    datatype: "json",
                    data: {templateId : temp.id},
                    context: this,
                    success: function (param) {
                        if(_.isEqual(this._template, temp)) {//иначе уже просим другой шаблон и результаты не актуальны
                            /*if (!param.operationResult) {
                                noty({
                                    text: param.operationMessage,
                                    type: "alert",
                                    layout: "bottom",
                                    timeout: 3000
                                });
                            } else {*/

                            this.participantsList.getCollection().reset(_.map(param, function (item) {
                                return _.omit(item, "flowOfDocumentParticipants");
                            }));
                            this.applyModelParticipants();
                            /*}*/
                        }
                    }
                }).always(_.bind(function () {
                    if(_.isEqual(this._template, temp)) {
                        this.$("div[name=participants] img").fadeOut("fast");
                    }
                }, this));
            } else {
                this.$("div[name=participants] img").fadeOut("fast");
            }
        },

        initializeTemplateSelect: function () {
            var self = this;
            this.templateSelect = this.$("div[name=template] input").typeahead({
                delay: 1000,
                autoSelect: false,
                updater: function (item){
                    self.templateSelected(item);
                    return item;
                },
                matcher: function () {
                    return true;//считаем что с сервера пришли правильные данные
                },
                highlighter: function (item) {
                    if (!this.query) {
                        return item;
                    }
                    var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#]/g, '\\$&');
                    var parts = _.uniq(query.toLowerCase().split(/\s+/ig)).join("|");
                    return item.replace(new RegExp('(' + parts + ')', 'ig'), function ($1, match) {
                        return '<strong>' + match + '</strong>';
                    });
                },
                source: function (query, process) {
                    self._template = null;
                    self.updateState();
                    return $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "/admin/docs/process/getTemplates",
                        data: {
                            query: query,
                            templateClass: self._templateClass.id,
                            page: 1,
                            per_page: 8
                        },
                        success: function (data) {
                            return process(data);
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                }

                /*ajax: {
                    url: "/admin/docs/process/getTemplates",
                    timeout: 100,
                    displayField: "name",
                    triggerLength: 0,
                    method: "get",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        self._template = null;
                        self.updateState();
                        return {
                            query: query,
                            templateClass: self._templateClass.id,
                            page: 1,
                            per_page: 8
                        }
                    },
                    preProcess: function (response) {
                        if (!response.operationResult) {
                            noty({
                                text: response.operationMessage,
                                type: "alert",
                                layout: "bottom",
                                timeout: 3000
                            });
                            return false;
                        }
                        _.each(response.data, function (item) {
                            if (!item.name) {
                                item.name = "Без имени id[" + item.id + "]";
                            }
                        });
                        return response.data;
                    }
                }*/
            });
        },

        initializeClassSelect: function () {
            var self = this;

            this.classSelect = this.$("div[name=templateClass] input").typeahead({
                updater: function(item){
                    self.templateClassSelected(item);
                    return item;
                },
                matcher: function () {
                    return true;//считаем что с сервера пришли правильные данные
                },
                highlighter: function(item) {
                    if(!this.query) {
                        return item.replace(new RegExp(" / ", "g"), " /<br/> ");
                    }
                    var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#]/g, '\\$&');
                    var parts = _.chain(query.toLowerCase().split(new RegExp("\\s*!/\\s*|\\s+"))).uniq().filter(function (item) {
                        return !!item;
                    }).value().join("|");
                    if(!parts) {
                        return item.replace(new RegExp(" / ", "g"), " /<br/> ");
                    }
                    return item.replace(new RegExp(" / ", "g"), " /<br/> ").replace(new RegExp('(' + parts + ')', 'ig'), function($1, match) {
                        return '<strong>' + match + '</strong>';
                    });
                },
                source: function (query, process) {
                    self._templateClass = null;
                    self._template = null;
                    self.updateState();
                    return $.ajax({
                        type: "post",
                        dataType: "json",
                        url: "/admin/flowOfDocuments/getDocumentTypes",
                        data: {
                            query: query,
                            page: 1,
                            per_page: 8
                        },
                        success: function (data) {
                            return process(data);
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                }
                /*ajax: {
                    url: "/admin/flowOfDocuments/getDocumentTypes",
                    timeout: 1000,
                    displayField: "pathName",
                    triggerLength: 0,
                    method: "get",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        self._templateClass = null;
                        self._template = null;
                        self.updateState();
                        return {
                            query: query,
                            page: 1,
                            per_page: 8
                        }
                    },
                    preProcess: function (data) {
                        showLoadingMask(false);
                        if (data.success === false) {
                            // Hide the list, there was some error
                            return false;
                        }
                        // We good!
                        return data.mylist;
                    }
                }*/
            });


            /*this.classSelect = this.$("div[name=templateClass] input").typeahead({
                delay: 1000,
                autoSelect: false,
                onSelect: _.bind(this.templateClassSelected, this),
                matcher: function () {
                    return true;//считаем что с сервера пришли правильные данные
                },
                highlighter: function(item) {
                    if(!this.query) {
                        return item.replace(new RegExp(" / ", "g"), " /<br/> ");
                    }
                    var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#]/g, '\\$&');
                    var parts = _.chain(query.toLowerCase().split(new RegExp("\\s*!/\\s*|\\s+"))).uniq().filter(function (item) {
                        return !!item;
                    }).value().join("|");
                    if(!parts) {
                        return item.replace(new RegExp(" / ", "g"), " /<br/> ");
                    }
                    return item.replace(new RegExp(" / ", "g"), " /<br/> ").replace(new RegExp('(' + parts + ')', 'ig'), function($1, match) {
                        return '<strong>' + match + '</strong>';
                    });
                },
                ajax: {
                    url: "/admin/flowOfDocuments/getDocumentTypes",
                    timeout: 100,
                    displayField: "pathName",
                    triggerLength: 0,
                    method: "get",
                    loadingClass: "loading-circle",
                    preDispatch: function (query) {
                        self._templateClass = null;
                        self._template = null;
                        self.updateState();
                        return {
                            query: query,
                            page: 1,
                            per_page: 8
                        }
                    }
                }
            });*/
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});