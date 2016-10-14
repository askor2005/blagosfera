community.shape.DirectorUnit = community.shape.AbstractUnit.extend({
    NAME: "community.shape.DirectorUnit",
    TYPE: "DIRECTOR",

    init : function()
    {
        this._super(this.TYPE);

        this.setCssClass("director_unit");
        this.setBackgroundColor("#f3f3f3");

        this.setUnitName("Генеральный директор");

        // cannot be removed
        this.nameLabel.installEditor(new community.shape.LabelInPlaceTextAreaEditor());
        //this.nameLabel.off("contextmenu");
    }

});

community.shape.DepartmentUnit = community.shape.AbstractUnit.extend({
    NAME: "community.shape.DepartmentUnit",
    TYPE: "DEPARTMENT",

    init : function()
    {
        this._super(this.TYPE);

        this.setCssClass("department_unit");
        this.setBackgroundColor("#f3f3a3");
    }

});
