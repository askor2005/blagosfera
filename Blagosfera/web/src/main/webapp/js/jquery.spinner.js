(function ($) {

    //var isPressCtrl = false;

    var spinners = [];

    function filterDigitsOnly($node) {
        $node.keypress(function (e) {
            if ((e.which == 8) || (e.which == 0)) {
                return true;
            }
            var txt = String.fromCharCode(e.which);
            if (!txt.match(/[0-9]/)) {
                return false;
            }
        });
    }
    function filterDiapason($node, min, max) {
        $node.on('input', function (e) {
            var digit = parseInt($node.val());
            if (!isNaN(digit)) {
                if (digit < min) {
                    $node.val(min);
                } else if (max != null && digit > max) {
                    $node.val(max);
                }
            }
        });
    }

    $(document).keydown(function(e) {
        if(e.ctrlKey) {
            for (var index in spinners) {
                if (spinners[index].attr("change_step") != null && spinners[index].attr("change_step") != "") {
                    spinners[index].spinner("option", "step", spinners[index].attr("change_step"));
                }
            }
        }
    });

    $(document).keyup(function(e) {
        if(!e.ctrlKey) {
            for (var index in spinners) {
                spinners[index].spinner( "option", "step", 1);
            }
        }
    });

    $.extend({
        radomSpinner: function($input, min, max, onChange){
            spinners.push($input);
            if (onChange == null) {
                onChange = function(){};
            }
            $input.spinner({
                min: min,
                max: max,
                numberFormat: "n",
                mouseWheel: true,
                spin: onChange
            });

            if ($input.attr("change_step") != null && $input.attr("change_step") != "") {
                $input.attr("title", "При зажатом Ctrl шаг меняется на " + $input.attr("change_step"));
                $input.radomTooltip({
                    placement: "top",
                    container: "body",
                    delay: {
                        show: 100,
                        hide: 100
                    }
                });
            }


            $input.on('input', onChange);

            //$input.after("<span class='text-muted'>При зажатом Ctrl шаг меняется на 1000</span>");

            filterDigitsOnly($input);
            filterDiapason($input, min, max);
        }
    });

})(jQuery);