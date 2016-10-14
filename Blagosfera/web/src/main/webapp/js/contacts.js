var $searchInput = null;

function initSearchInput($input, callback) {
    $searchInput = $input;
    $input.keyup(function () {
        var timeout = $input.data("timeout");
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(function () {
            var newValue = $input.val();
            var oldValue = $input.data("old-value");
            if (newValue != oldValue) {
                if ((newValue.length >= 4) || (newValue.length == 0)) {
                    $input.data("old-value", newValue);
                    callback(newValue);
                }
            }
        }, 1000);
        $input.data("timeout", timeout);
    });
}