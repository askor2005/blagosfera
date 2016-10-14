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
    "text!./template/UserAssignmentsModal.html"
], function (CollectionBinder, TableRowView, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate) {
    var rowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    TableRowView.templates.input("value"),
                    TableRowView.templates.minusBtn()
                ]
            });
        });
    };

    var fromModel = function (value) {
        return {
            value: value
        }
    };

    var notEmpty = function (value) {
        return value && value.length > 0;
    };

    var toModel = function (model) {
        return model.get("value");
    };

    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        usersList: null,
        groupsList: null,

        events: {
            "click .accept": utils.clickEventWrapper("saveValue"),
            "click table[name=users] .addItem": utils.clickEventWrapper("addUser"),
            "click table[name=groups] .addItem": utils.clickEventWrapper("addGroup")
        },

        initialize: function () {
            this.usersList= new ListView({
                childFactory: rowFactory()
            });
            this.groupsList = new ListView({
                childFactory: rowFactory()
            });
            this.bindTableRows(this.usersList);
            this.bindTableRows(this.groupsList);
            this.bindCurrentState();
        },

        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            this.applyModelValue();
            var $usersTableBody = this.$("table[name=users] tbody");
            this.usersList.setElement($usersTableBody).render();
            var $groupsTableBody = this.$("table[name=groups] tbody");
            this.groupsList.setElement($groupsTableBody).render();
            return this;
        },

        remove: function () {
            this.usersList.remove();
            this.groupsList.remove();
            Backbone.View.prototype.remove.call(this);
        },

        bindCurrentState: function () {
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.$el.on("show.bs.modal", _.bind(this.applyModelValue, this));
        },

        applyModelValue: function () {
            var value = this.model.get("value");
            value = value && value.assignment || {};
            var assignee = value.assignee || "";
            var candidateUsers = _.map(value.candidateUsers || [], fromModel);
            var candidateGroups = _.map(value.candidateGroups || [], fromModel);

            this.$("[name=assignee]").val(assignee);
            this.usersList.getCollection().set(candidateUsers);
            this.groupsList.getCollection().set(candidateGroups);
        },

        saveValue: function () {
            var assignee = this.$("[name=assignee]").val() || "";
            var candidateUsers = this.usersList.getCollection().chain().map(toModel).filter(notEmpty).uniq().value();
            var candidateGroups = this.groupsList.getCollection().chain().map(toModel).filter(notEmpty).uniq().value();
            this.model.set("value", {
                assignment: {
                    assignee: assignee,
                    candidateUsers: candidateUsers,
                    candidateGroups: candidateGroups
                }
            });
        },

        addUser: function () {
            var collection = this.usersList.getCollection();
            collection.add({id: collection.length + 1, event: "assignment", value: "", type: "className", attributes: []});
        },

        addGroup: function () {
            var collection = this.groupsList.getCollection();
            collection.add({name: "", value: "", type: "stringValue"})
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