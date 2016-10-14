define(["oryx"], function (oryx) {
    return oryx.Core.Command.extend({
        construct: function(shape, stencil, facade){
            this.shape = shape;
            this.stencil = stencil;
            this.facade = facade;
        },
        execute: function(){

            var shape = this.shape;
            var stencil = this.stencil;
            var resourceId = shape.resourceId;

            // Serialize all attributes
            var serialized = shape.serialize();
            stencil.properties().each((function(prop) {
                if(prop.readonly()) {
                    serialized = serialized.reject(function(serProp) {
                        return serProp.name==prop.id();
                    });
                }
            }).bind(this));

            var newShape;
            // Get shape if already created, otherwise create a new shape
            if (this.newShape){
                newShape = this.newShape;
                this.facade.getCanvas().add(newShape);
            } else {
                newShape = this.facade.createShape({
                    type: stencil.id(),
                    namespace: stencil.namespace(),
                    resourceId: resourceId
                });
            }

            // calculate new bounds using old shape's upperLeft and new shape's width/height
            var boundsObj = serialized.find(function(serProp){
                return (serProp.prefix === "oryx" && serProp.name === "bounds");
            });

            var changedBounds = null;

            if (!this.facade.getRules().preserveBounds(shape.getStencil())) {

                var bounds = boundsObj.value.split(",");
                if (parseInt(bounds[0], 10) > parseInt(bounds[2], 10)) { // if lowerRight comes first, swap array items
                    var tmp = bounds[0];
                    bounds[0] = bounds[2];
                    bounds[2] = tmp;
                    tmp = bounds[1];
                    bounds[1] = bounds[3];
                    bounds[3] = tmp;
                }
                bounds[2] = parseInt(bounds[0], 10) + newShape.bounds.width();
                bounds[3] = parseInt(bounds[1], 10) + newShape.bounds.height();
                boundsObj.value = bounds.join(",");

            }  else {

                var height = shape.bounds.height();
                var width  = shape.bounds.width();

                // consider the minimum and maximum size of
                // the new shape

                if (newShape.minimumSize) {
                    if (shape.bounds.height() < newShape.minimumSize.height) {
                        height = newShape.minimumSize.height;
                    }


                    if (shape.bounds.width() < newShape.minimumSize.width) {
                        width = newShape.minimumSize.width;
                    }
                }

                if(newShape.maximumSize) {
                    if(shape.bounds.height() > newShape.maximumSize.height) {
                        height = newShape.maximumSize.height;
                    }

                    if(shape.bounds.width() > newShape.maximumSize.width) {
                        width = newShape.maximumSize.width;
                    }
                }

                changedBounds = {
                    a : {
                        x: shape.bounds.a.x,
                        y: shape.bounds.a.y
                    },
                    b : {
                        x: shape.bounds.a.x + width,
                        y: shape.bounds.a.y + height
                    }
                };

            }

            var oPos = shape.bounds.center();
            if(changedBounds !== null) {
                newShape.bounds.set(changedBounds);
            }

            // Set all related dockers
            this.setRelatedDockers(shape, newShape);

            // store DOM position of old shape
            var parentNode = shape.node.parentNode;
            var nextSibling = shape.node.nextSibling;

            // Delete the old shape
            this.facade.deleteShape(shape);

            // Deserialize the new shape - Set all attributes
            newShape.deserialize(serialized);
            /*
             * Change color to default if unchanged
             * 23.04.2010
             */
            if(shape.getStencil().property("oryx-bgcolor")
                && shape.properties["oryx-bgcolor"]
                && shape.getStencil().property("oryx-bgcolor").value().toUpperCase()== shape.properties["oryx-bgcolor"].toUpperCase()){
                if(newShape.getStencil().property("oryx-bgcolor")){
                    newShape.setProperty("oryx-bgcolor", newShape.getStencil().property("oryx-bgcolor").value());
                }
            }
            if(changedBounds !== null) {
                newShape.bounds.set(changedBounds);
            }

            if(newShape.getStencil().type()==="edge" || (newShape.dockers.length==0 || !newShape.dockers[0].getDockedShape())) {
                newShape.bounds.centerMoveTo(oPos);
            }

            if(newShape.getStencil().type()==="node" && (newShape.dockers.length==0 || !newShape.dockers[0].getDockedShape())) {
                this.setRelatedDockers(newShape, newShape);

            }

            // place at the DOM position of the old shape
            if(nextSibling) parentNode.insertBefore(newShape.node, nextSibling);
            else parentNode.appendChild(newShape.node);

            // Set selection
            this.facade.setSelection([newShape]);
            this.facade.getCanvas().update();
            this.facade.updateSelection();
            this.newShape = newShape;

        },
        rollback: function(){

            if (!this.shape || !this.newShape || !this.newShape.parent) {return;}

            // Append shape to the parent
            this.newShape.parent.add(this.shape);
            // Set dockers
            this.setRelatedDockers(this.newShape, this.shape);
            // Delete new shape
            this.facade.deleteShape(this.newShape);
            // Set selection
            this.facade.setSelection([this.shape]);
            // Update
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        },

        /**
         * Set all incoming and outgoing edges from the shape to the new shape
         * @param {Shape} shape
         * @param {Shape} newShape
         */
        setRelatedDockers: function(shape, newShape){

            if(shape.getStencil().type()==="node") {

                (shape.incoming||[]).concat(shape.outgoing||[])
                    .each(function(i) {
                        i.dockers.each(function(docker) {
                            if (docker.getDockedShape() == shape) {
                                var rPoint = Object.clone(docker.referencePoint);
                                // Move reference point per percent

                                var rPointNew = {
                                    x: rPoint.x*newShape.bounds.width()/shape.bounds.width(),
                                    y: rPoint.y*newShape.bounds.height()/shape.bounds.height()
                                };

                                docker.setDockedShape(newShape);
                                // Set reference point and center to new position
                                docker.setReferencePoint(rPointNew);
                                if(i instanceof oryx.Core.Edge) {
                                    docker.bounds.centerMoveTo(rPointNew);
                                } else {
                                    var absXY = shape.absoluteXY();
                                    docker.bounds.centerMoveTo({x:rPointNew.x+absXY.x, y:rPointNew.y+absXY.y});
                                    //docker.bounds.moveBy({x:rPointNew.x-rPoint.x, y:rPointNew.y-rPoint.y});
                                }
                            }
                        });
                    });

                // for attached events
                if(shape.dockers.length>0&&shape.dockers.first().getDockedShape()) {
                    newShape.dockers.first().setDockedShape(shape.dockers.first().getDockedShape());
                    newShape.dockers.first().setReferencePoint(Object.clone(shape.dockers.first().referencePoint));
                }

            } else { // is edge
                newShape.dockers.first().setDockedShape(shape.dockers.first().getDockedShape());
                newShape.dockers.first().setReferencePoint(shape.dockers.first().referencePoint);
                newShape.dockers.last().setDockedShape(shape.dockers.last().getDockedShape());
                newShape.dockers.last().setReferencePoint(shape.dockers.last().referencePoint);
            }
        }
    });
});