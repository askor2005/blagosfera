<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            Ext.define('Ext.ux.grid.HeaderToolTip', {
                alias: 'plugin.headertooltip',
                init : function(grid) {
                    var headerCt = grid.headerCt;
                    grid.headerCt.on("afterrender", function(g) {
                        grid.tip = Ext.create('Ext.tip.ToolTip', {
                            target: headerCt.el,
                            delegate: ".x-column-header",
                            trackMouse: true,
                            renderTo: Ext.getBody(),
                            listeners: {
                                beforeshow: function(tip) {
                                    var c = headerCt.down('gridcolumn[id=' + tip.triggerElement.id  +']');
                                    if (c && c.tooltip) {
                                        tip.update(c.tooltip);
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        });
                    });
                }
            });
        });
    });
</script>