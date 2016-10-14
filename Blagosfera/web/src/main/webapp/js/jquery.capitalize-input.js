(function( $ ){
	$.fn.getCursorPosition = function() {
		var input = this.get(0);
		if (!input) return; // No (input) element found
		if ('selectionStart' in input) {
			// Standard-compliant browsers
			return input.selectionStart;
		} else if (document.selection) {
			// IE
			input.focus();
			var sel = document.selection.createRange();
			var selLen = document.selection.createRange().text.length;
			sel.moveStart('character', -input.value.length);
			return sel.text.length - selLen;
		}
	};
	$.fn.capitalizeInput = function(duration, minLength, callback) {
		this.keyup(function () {
			var $this = $(this);
			var newText = $this.val();
			if (newText.length > 0) {
				var oldText = $this.attr("data-old-text");
				if (oldText != newText) {			
					$this.attr("data-old-text", newText);
					newText = newText.substr(0, 1).toUpperCase() + newText.substr(1).toLowerCase();
					var caret = $this.getCursorPosition();
					$this.val(newText);
					$this.setCursorPosition(caret);
				}
			}
			
	    });
	};
})( jQuery );