define(["oryx"], function (oryx) {
    return oryx.Core.Command.extend({
        construct: function (option, currentParent, canAttach, position, facade) {
            this.option = option;
            this.currentParent = currentParent;
            this.canAttach = canAttach;
            this.position = position;
            this.facade = facade;
            this.selection = this.facade.getSelection();
        },
        execute: function () {
            if (!this.shape) {
                this.shape = this.facade.createShape(this.option);
                this.parent = this.shape.parent;
            } else {
                this.parent.add(this.shape);
            }

            if (this.canAttach && this.currentParent instanceof oryx.Core.Node && this.shape.dockers.length > 0) {

                var docker = this.shape.dockers[0];

                if (this.currentParent.parent instanceof oryx.Core.Node) {
                    this.currentParent.parent.add(docker.parent);
                }

                docker.bounds.centerMoveTo(this.position);
                docker.setDockedShape(this.currentParent);
                //docker.update();
            }

            this.facade.setSelection([this.shape]);
            this.facade.getCanvas().update();
            this.facade.updateSelection();

        },
        rollback: function () {
            this.facade.deleteShape(this.shape);

            //this.currentParent.update();

            this.facade.setSelection(this.selection.without(this.shape));
            this.facade.getCanvas().update();
            this.facade.updateSelection();

        }
    });
});
