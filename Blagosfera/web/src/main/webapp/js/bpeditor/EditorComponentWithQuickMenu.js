define([
    "./MorphDialog",
    "./commands/CreateAndLinkCommand",
    "./EditorComponentWithDnD",
    "oryx",
    "mousetrap",
    "text!./template/EditorComponentQuickMenuItem.html"
], function (MorphDialog, CreateAndLinkCommand, EditorComponentWithDnD, oryx, Mousetrap, quickMenuItemTemplate) {
    var imgBase = "/img/bpeditor/";
    var stencilQuickItemSelector = ".stencil-quick-item";

    var deleteBtnDescription = "Удалить";
    var morphBtnDescription = "Изменить тип";

    return EditorComponentWithDnD.extend({

        quickItemSize: 24,

        quickItemClass: "stencil-quick-item",

        events: {
            "click .delete-button":         "deleteItem",
            "click .morph-button":          "morphItem",
            "click .stencil-quick-item":    "stencilCreate"
        },

        dndState: _.extend({
            quickMenu: undefined
        }, EditorComponentWithDnD.prototype.dndState),

        quickMenuItemTemplate: _.template(quickMenuItemTemplate),

        quickMenuItems: [
            'CallActivity',
            'EndNoneEvent',
            'ExclusiveGateway',
            'CatchTimerEvent',
            'ThrowNoneEvent',
            'TextAnnotation',
            'SequenceFlow',
            'Association'
        ],

        morphDialog: new MorphDialog({}),

        initialize: function (options) {
            EditorComponentWithDnD.prototype.initialize.call(this, options);
            if (options.quickMenuItems) {
                this.quickMenuItems = options.quickMenuItems;
            }
            this.dndState = _.clone(this.dndState);
        },

        render: function () {
            var wasInitialized = !!this.editor;
            EditorComponentWithDnD.prototype.render.call(this);
            if (!wasInitialized) {
                var btns = "";
                btns += this.quickMenuItemTemplate({
                    id: this.cid + "-delete-button",
                    additionalClass: "delete-button",
                    description: deleteBtnDescription,
                    icon: imgBase + "delete.png",
                    stencilId: "nope"
                });
                btns += this.quickMenuItemTemplate({
                    id: this.cid + "-morph-button",
                    additionalClass: "morph-button",
                    description: morphBtnDescription,
                    icon: imgBase + "wrench.png",
                    stencilId: "nope"
                });
                _.each(this.quickMenuItems, function (item) {
                    item = this.resolveStencilById(item);
                    if (item) {
                        item = item.stencil;
                        btns += this.quickMenuItemTemplate({
                            id: _.uniqueId(this.cid + "-stencil-"),
                            additionalClass: this.quickItemClass,
                            description: item.title(),
                            icon: imgBase + item.icon(),
                            stencilId: item.idWithoutNs()
                        });
                    }
                }, this);
                this.$el.append(btns);
                this.$(stencilQuickItemSelector).draggable({
                    helper: "clone",
                    opacity: 0.5,
                    appendTo: this.$el,
                    drag: _.bind(this.quickMenuDrag, this),
                    stop: _.bind(this.quickMenuDragStop, this),
                    start: _.bind(this.quickMenuDragStart, this)
                });
                this.$el.on("scroll", _.bind(this.layoutQuickMenuItems, this));
            }
            return this;
        },

        afterInit: function () {
            EditorComponentWithDnD.prototype.afterInit.call(this);
            this.editor.registerOnEvent(
                oryx.CONFIG.EVENT_SELECTION_CHANGED,
                _.bind(this.layoutQuickMenuItems, this)
            );
        },

        bindKeyboard: function () {
            EditorComponentWithDnD.prototype.bindKeyboard.call(this);
            var self = this;
            Mousetrap.bind("mod+q a", function (e) {
                self.quickAddItem("UserTask");
                return false;
            });
            Mousetrap.bind("mod+q s", function (e) {
                self.quickAddItem("ServiceTask");
                return false;
            });
        },

        checkAccept: function ($elem) {
            return $elem.is(".stencil-quick-item") || EditorComponentWithDnD.prototype.checkAccept.call(this, $elem);
        },

        quickMenuDragStop: function (event, ui) {
            this.dndState.quickMenu = false;
        },

        quickMenuDragStart: function (event, ui) {
            this.dndState.quickMenu = true;
        },

        quickMenuDrag: function (event, ui) {
            if (this.dndState.quickMenu) {
                var coord = this.editor.eventCoordinatesXY(event.pageX, event.pageY);

                var additionalIEZoom = 1;
                if (!isNaN(screen.logicalXDPI) && !isNaN(screen.systemXDPI)) {
                    var ua = navigator.userAgent;
                    if (ua.indexOf('MSIE') >= 0) {
                        //IE 10 and below
                        var zoom = Math.round((screen.deviceXDPI / screen.logicalXDPI) * 100);
                        if (zoom !== 100) {
                            additionalIEZoom = zoom / 100
                        }
                    }
                }

                if (additionalIEZoom !== 1) {
                    coord.x = coord.x / additionalIEZoom;
                    coord.y = coord.y / additionalIEZoom;
                }

                var aShapes = this.editor.getCanvas().getAbstractShapesAtPosition(coord);

                if (aShapes.length <= 0) {
                    if (event.helper) {
                        this.dndState.dragCanContain = false;
                        return;
                    }
                }

                if (aShapes[0] instanceof ORYX.Core.Canvas) {
                    this.editor.getCanvas().setHightlightStateBasedOnX(coord.x);
                }

                var stencilObject = this.resolveStencil(event, ui);
                var stencil = stencilObject.stencil;

                var candidate = aShapes.last();

                var isValid = false;
                if (stencil.type() === "node")
                {
                    //check containment rules
                    var canContain = this.editor.getRules().canContain({containingShape:candidate, containedStencil:stencil});

                    var parentCandidate = aShapes.reverse().find(function (candidate) {
                        return (candidate instanceof ORYX.Core.Canvas
                        || candidate instanceof ORYX.Core.Node
                        || candidate instanceof ORYX.Core.Edge);
                    });

                    if (!parentCandidate) {
                        this.dndState.dragCanContain = false;
                        return;
                    }

                    this.dndState.dragCurrentParent = parentCandidate;
                    this.dndState.dragCurrentParentId = parentCandidate.id;
                    this.dndState.dragCurrentParentStencil = parentCandidate.getStencil().id();
                    this.dndState.dragCanContain = canContain;
                    this.dndState.dropTargetElement = parentCandidate;
                    isValid = canContain;

                } else { //Edge

                    var shapes = this.editor.getSelection();
                    if (shapes && shapes.length == 1)
                    {
                        var currentSelectedShape = shapes.first();
                        var curCan = candidate;
                        var canConnect = false;

                        var targetStencil = curCan.getStencil();
                        if (targetStencil)
                        {
                            var associationConnect = false;
                            var targetStencilId = targetStencil.idWithoutNs();
                            if (stencilObject.id === 'Association' && (targetStencilId === 'TextAnnotation' || targetStencilId === 'BoundaryCompensationEvent'))
                            {
                                associationConnect = true;
                            }
                            else if (stencilObject.id === 'DataAssociation' && targetStencilId === 'DataStore')
                            {
                                associationConnect = true;
                            }

                            if (associationConnect || targetStencil.roles().indexOf(targetStencil.namespace() + "sequence_end") !== -1)
                            {
                                while (!canConnect && curCan && !(curCan instanceof ORYX.Core.Canvas))
                                {
                                    candidate = curCan;
                                    //check connection rules
                                    canConnect = this.editor.getRules().canConnect({
                                        sourceShape: currentSelectedShape,
                                        edgeStencil: stencil,
                                        targetShape: curCan
                                    });
                                    curCan = curCan.parent;
                                }
                            }
                        }
                        var parentCandidate = this.editor.getCanvas();

                        isValid = canConnect;
                        this.dndState.dragCurrentParent = parentCandidate;
                        this.dndState.dragCurrentParentId = parentCandidate.id;
                        this.dndState.dragCurrentParentStencil = parentCandidate.getStencil().id();
                        this.dndState.dragCanContain = canConnect;
                        this.dndState.dropTargetElement = candidate;
                    }

                }

               this.editor.handleEvents({
                    type:		ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW,
                    highlightId:'shapeMenu',
                    elements:	[candidate],
                    color:		isValid ? ORYX.CONFIG.SELECTION_VALID_COLOR : ORYX.CONFIG.SELECTION_INVALID_COLOR
                });
            }
        },

        dropCallback: function (event, ui) {
            EditorComponentWithDnD.prototype.dropCallback.call(this, event, ui);
            this.dndState.quickMenu = undefined;
        },

        processDrop: function (event, ui, pos) {
            if(this.dndState.quickMenu) {
                var shapes = this.editor.getSelection();
                if (shapes && shapes.length == 1) {
                    var stencilObject = this.resolveStencil(event, ui);
                    if (!stencilObject) {
                        return;
                    }

                    var currentSelectedShape = shapes.first();

                    var option = {
                        type: stencilObject.stencilType,
                        namespace: stencilObject.stencil.namespace(),
                        connectedShape: currentSelectedShape,
                        parent: this.dndState.dragCurrentParent,
                        containedStencil: stencilObject.stencil
                    };

                    if (stencilObject.id !== 'SequenceFlow' && stencilObject.id !== 'Association' &&
                        stencilObject.id !== 'MessageFlow' && stencilObject.id !== 'DataAssociation') {
                        var args = {sourceShape: currentSelectedShape, targetStencil: stencilObject.stencil};
                        var targetStencil = this.editor.getRules().connectMorph(args);
                        if (!targetStencil) {
                            return;
                        }// Check if there can be a target shape
                        option.connectingType = targetStencil.id();
                    }


                    // If the ctrl key is not pressed,
                    // snapp the new shape to the center
                    // if it is near to the center of the other shape
                    if (!event.ctrlKey) {
                        // Get the center of the shape
                        var cShape = currentSelectedShape.bounds.center();
                        // Snapp +-20 Pixel horizontal to the center
                        if (20 > Math.abs(cShape.x - pos.x)) {
                            pos.x = cShape.x;
                        }
                        // Snapp +-20 Pixel vertical to the center
                        if (20 > Math.abs(cShape.y - pos.y)) {
                            pos.y = cShape.y;
                        }
                    }

                    option.position = pos;

                    this.editor.executeCommands([
                        new CreateAndLinkCommand(option, this.dndState.dropTargetElement, pos, this.editor)
                    ]);
                }
            } else {
                EditorComponentWithDnD.prototype.processDrop.call(this, event, ui, pos);
            }
        },

        morphItem: function () {
            var shapes = this.editor.getSelection();
            if (shapes && shapes.length == 1) {
                var currentSelectedShape = shapes.first();
                this.morphDialog.show(currentSelectedShape, this.editor);
            }
        },

        stencilCreate: function (event) {
            this.quickAddItem(jQuery(event.currentTarget).attr("stencil-id"));
        },

        quickAddItem: function (stencilId) {
            var shapes = this.editor.getSelection();
            if (shapes && shapes.length == 1) {
                var currentSelectedShape = shapes.first();

                var containedStencilObject = this.resolveStencilById(stencilId);
                if (!containedStencilObject) {
                    return;
                }

                var targetStencil = this.editor.getRules().connectMorph({
                    sourceShape: currentSelectedShape,
                    targetStencil: containedStencilObject.stencil
                });

                if (!targetStencil) {
                    return;
                }

                var option = {
                    connectedShape: currentSelectedShape,
                    parent: currentSelectedShape.parent,
                    containedStencil: containedStencilObject.stencil,
                    type: containedStencilObject.stencilType,
                    namespace: containedStencilObject.stencil.namespace(),
                    connectingType: targetStencil.id()
                };

                this.editor.executeCommands([
                    new CreateAndLinkCommand(option, undefined, undefined, this.editor)
                ]);
            }
        },

        /**
         * Выстраиваем кнопки быстрого меню вокруг выделенного элемента
         */
        layoutQuickMenuItems: function () {
            this.hideQuickMenuItems();
            var shapes = this.editor.getSelection();
            if (shapes && shapes.length == 1) {
                var currentSelectedShape = shapes.first();
                var stencil = currentSelectedShape.getStencil();
                if(!stencil) {
                    return;
                }

                var bounds = this.calculateShapeBounds(currentSelectedShape);

                var stencilSet = this.editor.getStencilSets()[stencil.namespace()];
                var rules = stencilSet.jsonRules().morphingRules;
                var roles = _.map(stencil.roles(), function (role) {
                    return role.replace(stencil.namespace(), "");
                });
                var hasMorphs = false;
                for (var i = 0; i < rules.length; i++) {
                    var rule = rules[i];
                    if(roles.indexOf(rule.role) !== -1) {
                        hasMorphs = true;
                        break;
                    }
                }

                var upperLeft = bounds.upperLeft();
                var lowerRight = bounds.lowerRight();
                var x = upperLeft.x;
                var y = lowerRight.y + 2;

                var quickItemSize = this.quickItemSize;
                if (bounds.width() < quickItemSize * 2) {
                    x -= quickItemSize;
                }
                if(hasMorphs) {
                    this.$(".morph-button").css({top: y + "px", left: x + "px" }).show();
                }
                this.$(".delete-button").css({top: y + "px", left: (x + quickItemSize) + "px"  }).show();

                var stencilId = stencil.idWithoutNs();
                var canConnectAssociations = stencilId === 'TextAnnotation' || stencilId === 'BoundaryCompensationEvent'
                var canConnect = roles.indexOf("sequence_start") !== -1;

                if(canConnect || canConnectAssociations) {
                    x = lowerRight.x + 5;
                    y = upperLeft.y;
                    var stepPerColumn = Math.max(3, (bounds.height() / quickItemSize) | 0);
                    var counter = 1;
                    this.$(stencilQuickItemSelector).each(function (i, obj) {
                        if (counter > stepPerColumn) {
                            y = upperLeft.y;
                            x += quickItemSize;
                            counter = 1;
                        }
                        jQuery(obj).css({top: y + "px", left: x + "px"}).show();
                        y += quickItemSize;
                        counter++;
                    });
                }
            }
        },

        /**
         * Скрыть кнопки быстрого меню
         */
        hideQuickMenuItems: function () {
            jQuery(".quick-menu-item").hide();
        },

        calculateShapeBounds: function (shape) {
            var a = this.editor.getCanvas().node.getScreenCTM();

            var absoluteXY = shape.absoluteXY();

            absoluteXY.x *= a.a;
            absoluteXY.y *= a.d;

            var additionalIEZoom = 1;
            if (!isNaN(screen.logicalXDPI) && !isNaN(screen.systemXDPI)) {
                var ua = navigator.userAgent;
                if (ua.indexOf('MSIE') >= 0) {
                    //IE 10 and below
                    var zoom = Math.round((screen.deviceXDPI / screen.logicalXDPI) * 100);
                    if (zoom !== 100) {
                        additionalIEZoom = zoom / 100
                    }
                }
            }

            if (additionalIEZoom === 1) {
                absoluteXY.y = absoluteXY.y - this.$el.offset().top + 5;
                absoluteXY.x = absoluteXY.x - this.$el.offset().left;

            } else {
                var canvasOffsetLeft = this.$el.offset().left;
                var canvasScrollLeft = this.$el.scrollLeft();
                var canvasScrollTop = this.$el.scrollTop();

                var offset = a.e - (canvasOffsetLeft * additionalIEZoom);
                var additionaloffset = 0;
                if (offset > 10) {
                    additionaloffset = (offset / additionalIEZoom) - offset;
                }
                absoluteXY.y = absoluteXY.y - (this.$el.offset().top * additionalIEZoom) + 5 + ((canvasScrollTop * additionalIEZoom) - canvasScrollTop);
                absoluteXY.x = absoluteXY.x - (canvasOffsetLeft * additionalIEZoom) + additionaloffset + ((canvasScrollLeft * additionalIEZoom) - canvasScrollLeft);
            }

            return new ORYX.Core.Bounds(
                a.e + absoluteXY.x,
                a.f + absoluteXY.y,
                a.e + absoluteXY.x + a.a*shape.bounds.width(),
                a.f + absoluteXY.y + a.d*shape.bounds.height()
            );
        }
    });
});
