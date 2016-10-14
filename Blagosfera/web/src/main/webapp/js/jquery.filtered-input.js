(function( $ ){
	$.fn.filteredInput = function(disallowed) {
		
		this.keypress(function (e) {
			var $this = $(this);
	        var character = String.fromCharCode(e.keyCode);
	        console.log(character);
	        if (disallowed.indexOf(character) != -1) {
	        	e.preventDefault();
	        	$this.tooltip('destroy');
	        	$this.tooltip({
	        		title : "Недопустимый символ " + character,
	        		placement : "top"
	        	});
	        	$this.tooltip('show');
	        	setTimeout(function() {
	        		$this.tooltip('destroy');
	        	}, 3000);
	        }
	    });
	};
})( jQuery );