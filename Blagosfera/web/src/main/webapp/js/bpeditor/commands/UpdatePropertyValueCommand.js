define(["oryx"], function (oryx) {
    return oryx.Core.Command.extend({
        construct: function (property, shape, newValue, facade) {
            this.key = property.prefix() + "-" + property.id();
            this.oldValue = shape.properties[this.key];
            this.newValue = newValue;
            this.shape = shape;
            this.facade = facade;
        },
        execute: function () {
            this.shape.setProperty(this.key, this.newValue);
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        },
        rollback: function () {
            this.shape.setProperty(this.key, this.oldValue);
            this.facade.getCanvas().update();
            this.facade.updateSelection();
        }
    });
});
