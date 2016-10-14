(function($) {
    $.fn.fixMe = function() {
        return this.each(function() {
            var $this = $(this),
                $t_fixed;

            function init() {
                if($this.closest('table.fixed').length > 0) {
                    $t_fixed = $this.closest('table.fixed');
                } else {
                    $t_fixed = $this.clone();
                    $t_fixed.find("tbody").remove().end().removeAttr("id").addClass("fixed").css("z-index","1001").insertBefore($this);
                }
                resizeFixed();
                scrollFixed();
            }

            function resizeFixed() {
                $t_fixed.find("th").each(function(index) {
                    $(this).css("text-align","center").css("width", $this.find("th").eq(index).outerWidth() + "px");
                });
            }

            function scrollFixed() {
                var offset = $(this).scrollTop(),
                    tableOffsetTop = $this.offset().top - $('.navbar-fixed-top').height(),
                    tableOffsetBottom = tableOffsetTop + $this.height() - $this.find("thead").height();
                if($(window).width() < 1170) {
                    tableOffsetTop = $this.offset().top;
                }
                if (offset < tableOffsetTop || offset > tableOffsetBottom)
                    $t_fixed.hide();
                else if (offset >= tableOffsetTop && offset <= tableOffsetBottom && $t_fixed.is(":hidden"))
                    $t_fixed.show();
                    if($(window).width() >= 1170)
                        $t_fixed.css('top', $('.navbar-fixed-top').height());
                    else
                        $t_fixed.css('top', 0);
            }
            $(window).resize(resizeFixed);
            $(window).scroll(scrollFixed);
            init();
        });
    };
})(jQuery);