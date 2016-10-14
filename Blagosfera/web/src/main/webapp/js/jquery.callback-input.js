(function( $ ){
	$.fn.callbackInput = function(duration, minLength, callback) {
		this.keyup(function () {
			var $this = $(this);
	        var timeout = $this.data("timeout");
	        if (timeout) {
	            clearTimeout(timeout);
	        }
	        timeout = setTimeout(function () {

	            var newValue = $this.val();
	            var oldValue = $this.data("old-value");

	            if (newValue != oldValue) {
	                if ((newValue.length >= minLength) || (newValue.length == 0)) {
	                	$this.data("old-value", newValue);
	                    callback(newValue);
	                }
	            }

	        }, duration);
	        $this.data("timeout", timeout);
	    });
	};
})( jQuery );