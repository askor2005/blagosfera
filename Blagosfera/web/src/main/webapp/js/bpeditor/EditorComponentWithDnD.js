define([
    "./commands/SimpleCreateCommand",
    "./EditorComponent",
    "oryx"
], function (SimpleCreateCommand, EditorComponent, oryx) {
    return EditorComponent.extend({
        stencilSelector: ".stencil-item",

        dndState: {
            stencilDragOver: false,
            dragCurrentParent: undefined,
            dragCurrentParentId: undefined,
            dragCurrentParentStencil: undefined,
            dragCanContain: undefined,
            dropTargetElement: undefined
        },

        initialize: function (options) {
            EditorComponent.prototype.initialize.call(this, options);
            if (options.stencilSelector) {
                this.stencilSelector = options.stencilSelector;
            }
            this.dndState = _.clone(this.dndState);
        },

        prepare: function () {
            EditorComponent.prototype.prepare.call(this);
            this.$el.droppable({
                accept: _.bind(this.checkAccept, this),
                drop: _.bind(this.dropCallback, this),
                over: _.bind(this.overCallback, this),
                out: _.bind(this.outCallback, this)
            });
            jQuery("body").on("drag", this.stencilSelector, _.bind(this.dragElemOver, this));
            jQuery("body").on("dragstop", this.stencilSelector, _.bind(this.dragElemStop, this));
        },

        checkAccept: function ($elem) {
            return $elem.is(this.stencilSelector);
        },

        /**
         * Получить id конпонента, который участвует в DnD событии
         */
        resolveStencilId: function (event, ui) {
            return (ui && (ui.draggable || ui.helper) || jQuery(event.target)).attr("stencil-id");
        },

        /**
         * Пытаемся определить какой компонент задействован в событии DnD
         * @returns Object{
         *
         *     id: String,
         *     stencil: Stencil,
         *     stencilSet: StencilSet,
         *     stencilType: String
         * }
         */
        resolveStencil: function (event, ui) {
            var stencilId = this.resolveStencilId(event, ui);
            return this.resolveStencilById(stencilId);
        },

        /**
         * Ищем компонент по id
         * @returns Object{
         *
         *     id: String,
         *     stencil: Stencil,
         *     stencilSet: StencilSet,
         *     stencilType: String
         * }
         */
        resolveStencilById: function (stencilId) {
            var stencilSets = this.editor.getStencilSets().values();
            var item;
            var stencilSet;
            var stencilType;
            for (var i = 0; i < stencilSets.length; i++) {
                stencilSet = stencilSets[i];
                stencilType = stencilSet.namespace() + stencilId;
                item = stencilSet.stencil(stencilType);
                if (item) {
                    return {
                        id: stencilId,
                        stencil: item,
                        stencilSet: stencilSet,
                        stencilType: stencilType
                    }
                }
            }
            return null;
        },


        /**
         * Обработчик события drop
         */
        dropCallback: function (event, ui) {

            this.editor.handleEvents({
                type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                highlightId: "shapeRepo.attached"
            });
            this.editor.handleEvents({
                type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                highlightId: "shapeRepo.added"
            });

            this.editor.handleEvents({
                type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                highlightId: "shapeMenu"
            });


            if (this.dndState.dragCanContain) {
                var pos = this.calculateEventPosition(event, ui);
                this.processDrop(event, ui, pos);
            }

            this.dndState.dragCurrentParent = undefined;
            this.dndState.dragCurrentParentId = undefined;
            this.dndState.dragCurrentParentStencil = undefined;
            this.dndState.dragCanContain = undefined;
            this.dndState.dropTargetElement = undefined;
            this.dndState.stencilDragOver = false;
        },

        /**
         * Вычисленение позиции куда был брошен элемент
         * @returns {{x: Number, y: Number}}
         */
        calculateEventPosition: function (event, ui) {
            var pos = {
                x: event.pageX != null ? event.pageX : ui.position.left,
                y: event.pageY != null ? event.pageY : ui.position.top
            };
            if (ui.helper) {
                pos.x += ui.helper.width() / 2;
                pos.y += ui.helper.height() / 2;
            }

            var additionalIEZoom = 1;
            if (!isNaN(screen.logicalXDPI) && !isNaN(screen.systemXDPI)) {
                var ua = navigator.userAgent;
                if (ua.indexOf('MSIE') >= 0) {
                    //IE 10 and below
                    var zoom = Math.round((screen.deviceXDPI / screen.logicalXDPI) * 100);
                    if (zoom !== 100) {
                        additionalIEZoom = zoom / 100;
                    }
                }
            }

            var screenCTM = this.editor.getCanvas().node.getScreenCTM();

            // Correcting the UpperLeft-Offset
            pos.x -= (screenCTM.e / additionalIEZoom);
            pos.y -= (screenCTM.f / additionalIEZoom);
            // Correcting the Zoom-Factor
            pos.x /= screenCTM.a;
            pos.y /= screenCTM.d;

            // Correcting the ScrollOffset
            pos.x -= document.documentElement.scrollLeft;
            pos.y -= document.documentElement.scrollTop;

            var parentAbs = this.dndState.dragCurrentParent.absoluteXY();
            pos.x -= parentAbs.x;
            pos.y -= parentAbs.y;
            return pos;
        },

        /**
         * Непосредственная обработка события drop
         */
        processDrop: function (event, ui, pos) {
            var stencilObject = this.resolveStencil(event, ui);
            var option = {
                type: stencilObject.stencilType,
                namespace: stencilSet.namespace(),
                position: pos,
                parent: this.dndState.dragCurrentParent
            };


            // Update canvas
            this.editor.executeCommands([
                new SimpleCreateCommand(option, this.dndState.dragCurrentParent, false, pos, this.editor)
            ]);
        },

        /**
         * Обработчик события over
         */
        overCallback: function () {
            this.dndState.stencilDragOver = true;
        },

        /**
         * Обработчик события out
         */
        outCallback: function () {
            this.dndState.stencilDragOver = false;
        },

        /**
         * Обработчик события dragstop для элеменентов с селектором {@link #stencilSelector}
         */
        dragElemStop: function () {
            this.dndState.stencilDragOver = false;
        },

        /**
         * Обработчик события drag для элеменентов с селектором {@link #stencilSelector}
         */
        dragElemOver: function (event, ui) {
            if (!this.dndState.stencilDragOver) {
                return;
            }

            var parentCandidate;
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

            if (aShapes[0] instanceof oryx.Core.Canvas) {
                this.editor.getCanvas().setHightlightStateBasedOnX(coord.x);
            }

            if (aShapes.length == 1 && aShapes[0] instanceof oryx.Core.Canvas) {
                parentCandidate = aShapes[0];

                this.dndState.dragCanContain = true;
                this.dndState.dragCurrentParent = parentCandidate;
                this.dndState.dragCurrentParentId = parentCandidate.id;

                this.editor.handleEvents({
                    type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                    highlightId: "shapeRepo.attached"
                });
                this.editor.handleEvents({
                    type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                    highlightId: "shapeRepo.added"
                });
                return;
            } else {
                var stencilObject = this.resolveStencil(event, ui);
                var item = stencilObject.stencil;

                parentCandidate = aShapes.reverse().find(function (candidate) {
                    return (candidate instanceof oryx.Core.Canvas
                    || candidate instanceof oryx.Core.Node
                    || candidate instanceof oryx.Core.Edge);
                });

                if (!parentCandidate) {
                    this.dndState.dragCanContain = false;
                    return;
                }

                if (item.type() === "node") {

                    // check if the draggable is a boundary event and the parent an Activity
                    var _canContain = false;
                    var parentStencilId = parentCandidate.getStencil().id();

                    if (this.dndState.dragCurrentParentId && this.dndState.dragCurrentParentId === parentCandidate.id) {
                        return;
                    }

                    var parentItem = parentCandidate.getStencil();
                    if (parentItem.roles().indexOf(parentItem.namespace() + "Activity") > -1) {
                        if (item.roles().indexOf(parentItem.namespace() + "IntermediateEventOnActivityBoundary") > -1) {
                            _canContain = true;
                        }
                    } else if (parentItem.idWithoutNs() === 'Pool') {
                        if (item.idWithoutNs() === 'Lane') {
                            _canContain = true;
                        }
                    }

                    if (_canContain) {
                        this.editor.handleEvents({
                            type: oryx.CONFIG.EVENT_HIGHLIGHT_SHOW,
                            highlightId: "shapeRepo.attached",
                            elements: [parentCandidate],
                            style: oryx.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE,
                            color: oryx.CONFIG.SELECTION_VALID_COLOR
                        });

                        this.editor.handleEvents({
                            type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                            highlightId: "shapeRepo.added"
                        });
                    } else {
                        var parentId = parentItem.idWithoutNs();
                        var containmentRules = stencilObject.stencilSet.jsonRules().containmentRules;
                        var roles = _.map(item.roles(), function (role) {
                            return role.replace(item.namespace(), "");
                        });
                        for (var i = 0; i < containmentRules.length; i++) {
                            var rule = containmentRules[i];
                            if (rule.role === parentId) {
                                for (var j = 0; j < rule.contains.length; j++) {
                                    if (roles.indexOf(rule.contains[j]) > -1) {
                                        _canContain = true;
                                        break;
                                    }
                                }

                                if (_canContain) {
                                    break;
                                }
                            }
                        }

                        // Show Highlight
                        this.editor.handleEvents({
                            type: oryx.CONFIG.EVENT_HIGHLIGHT_SHOW,
                            highlightId: 'shapeRepo.added',
                            elements: [parentCandidate],
                            color: _canContain ? oryx.CONFIG.SELECTION_VALID_COLOR : oryx.CONFIG.SELECTION_INVALID_COLOR
                        });

                        this.editor.handleEvents({
                            type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                            highlightId: "shapeRepo.attached"
                        });
                    }

                    this.dndState.dragCurrentParent = parentCandidate;
                    this.dndState.dragCurrentParentId = parentCandidate.id;
                    this.dndState.dragCurrentParentStencil = parentStencilId;
                    this.dndState.dragCanContain = _canContain;

                } else {
                    var canvasCandidate = this.editor.getCanvas();
                    var canConnect = false;

                    var targetStencil = parentCandidate.getStencil();
                    if (targetStencil) {
                        var associationConnect = false;
                        var idWithoutNs = item.idWithoutNs();
                        if (idWithoutNs === 'Association') {
                            associationConnect = true;
                        } else if (idWithoutNs === 'DataAssociation') {
                            associationConnect = true;
                        }

                        if (associationConnect || targetStencil.roles().indexOf(targetStencil.namespace() + "sequence_end") !== -1) {
                            canConnect = true;
                        }
                    }

                    //Edge
                    this.dndState.dragCurrentParent = canvasCandidate;
                    this.dndState.dragCurrentParentId = canvasCandidate.id;
                    this.dndState.dragCurrentParentStencil = canvasCandidate.getStencil().id();
                    this.dndState.dragCanContain = canConnect;

                    // Show Highlight
                    this.editor.handleEvents({
                        type: oryx.CONFIG.EVENT_HIGHLIGHT_SHOW,
                        highlightId: 'shapeRepo.added',
                        elements: [canvasCandidate],
                        color: oryx.CONFIG.SELECTION_VALID_COLOR
                    });

                    this.editor.handleEvents({
                        type: oryx.CONFIG.EVENT_HIGHLIGHT_HIDE,
                        highlightId: "shapeRepo.attached"
                    });
                }
            }
        }
    });
});