(function( $ ){
	$.fn.moneyInput = function() {
		
		this.keydown(function (e) {
			var $this = $(this);
			var dotIndex = $this.val().indexOf(".");
			var key = e.charCode || e.keyCode || 0;
			console.log(key);
            return (
                key == 8 || 
                key == 9 ||
                key == 13 ||
                key == 46 ||
                key == 110 ||
                (key == 190 && dotIndex == -1) ||
                (key >= 35 && key <= 40) ||
                (key >= 48 && key <= 57) ||
                (key >= 112 && key <= 123) ||
                (key >= 96 && key <= 105));
	    });
		
		this.blur(function (e) {
			var $this = $(this);
			var value = parseFloat($this.val());
			$this.val(value.toFixed(2));
		});
	};
})( jQuery );