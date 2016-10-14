/**
 * Created by aotts on 21.10.2015.
 * Редактор порядка вызова процессов для "ворот"
 */
define([
    "jquery",
    "./AbstractPropertyEditor",
    "text!./template/SequenceFlowOrderEditor.html",
    "text!./template/SequenceFlowOrderEditorItem.html"
], function ($, AbstractPropertyEditor, template, itemTemplate) {
    var SequenceFlowStencilId = "SequenceFlow";

    return AbstractPropertyEditor.extend({
        template: _.template(template, {variable: "data"}),
        itemTemplate: _.template(itemTemplate, {variable: "data"}),
        events: {
            "click .move-up": "moveUp",
            "click .move-down": "moveDown"
        },

        /**
         * Пометка о том, что в данный момент обновляется значение модели
         */
        updatingModel: false,

        initialize: function () {
            AbstractPropertyEditor.prototype.initialize.call(this);
            this.listenTo(this.model, "change:value change:refresh", this.updateFlow);
        },

        applyTemplate: function () {
            AbstractPropertyEditor.prototype.applyTemplate.call(this);
            this.$('[flowcontainer]').sortable({
                update: _.bind(function () {
                    _.each(this.$(".order-item"), function (elem) {
                        this.updateButtonsState($(elem));
                    }, this);
                    this.updateModel();
                }, this)
            });
            this.updateFlow();
        },

        updateFlow: function () {
            if(this.updatingModel) {
                return;
            }
            var value = this.model.get("value");
            var sequenceFlowOrder = value && value.sequenceFlowOrder || [];

            var outgoingSequenceFlow = {};
            var outgoingSequenceFlowIds = [];
            var shape = this.model.get("shape");
            var outgoingNodes = shape.getOutgoingShapes();
            for (var i=0; i<outgoingNodes.length; i++) {
                var outgoingNode = outgoingNodes[i];
                if (outgoingNode.getTarget() && outgoingNode.getStencil().idWithoutNs() === SequenceFlowStencilId) {
                    outgoingSequenceFlowIds.push(outgoingNode.resourceId);
                    outgoingSequenceFlow[outgoingNode.resourceId] = outgoingNode;
                }
            }

            var elems = _.chain(sequenceFlowOrder)
                .intersection(outgoingSequenceFlowIds)
                .union(outgoingSequenceFlowIds)
                .map(function (resourceId) {
                    var outgoingNode = outgoingSequenceFlow[resourceId];
                    var target = outgoingNode.getTarget();
                    var stencil = target.getStencil();
                    return $(this.itemTemplate({
                        cid: this.cid,
                        id: resourceId,
                        title: target.properties["oryx-name"] || stencil.title(),
                        icon: "/img/bpeditor/" + stencil.icon()
                    }));
                }, this).value();

            var $last;
            var flowcontainer = this.$('[flowcontainer]');
            flowcontainer.empty();
            for (var i = 0; i < elems.length; i++) {
                var $elem = elems[i];
                if(i === 0) {
                    flowcontainer.prepend($elem);
                    $(".move-up", $elem).hide();
                } else {
                    $last.after($elem);
                    $(".move-up", $elem).show();
                    $(".move-down", $last).show();
                }
                $last = $elem;
            }
            if($last) {
                $(".move-down", $last).hide();
            }
        },

        updateModel: function () {
            this.updatingModel = true;

            var sequenceFlowOrder = _.map(this.$(".order-item"), function (item) {
                return $(item).attr("ref");
            });
            this.model.set("value", {sequenceFlowOrder: sequenceFlowOrder});

            this.updatingModel = false;
        },

        moveUp: function (event) {
            if(event.which === 1) {
                var $elem = $(event.target).parents(".order-item");
                var $prev = $elem.prev();
                if($prev.length) {
                    $elem.insertBefore($prev);
                    this.updateButtonsState($elem);
                    this.updateButtonsState($prev);
                    this.updateModel();
                }
            }
            if(event.which === 1 || event.which === 2) {
                event.preventDefault();
            }
        },

        moveDown: function (event) {
            if(event.which === 1) {
                var $elem = $(event.target).parents(".order-item");
                var $next = $elem.next();
                if($next.length) {
                    $elem.insertAfter($next);
                    this.updateButtonsState($elem);
                    this.updateButtonsState($next);
                    this.updateModel();
                }
            }
            if(event.which === 1 || event.which === 2) {
                event.preventDefault();
            }
        },

        updateButtonsState: function ($elem) {
            if(!$elem.next().length) {
                $(".move-down", $elem).hide();
            } else {
                $(".move-down", $elem).show();
            }
            if(!$elem.prev().length) {
                $(".move-up", $elem).hide();
            } else {
                $(".move-up", $elem).show();
            }
        }

    });
});