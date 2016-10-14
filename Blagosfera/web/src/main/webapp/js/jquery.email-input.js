(function( $ ){
	$.fn.emailInput = function() {
		
		var $this = $(this);
		
		if ($this.parent().find("span.form-control-feedback").length == 0) {
			$this.after("<span class='glyphicon form-control-feedback'></span>");
		}
		
		$this.keyup(function () {
			var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		    if (!re.test($this.val())) {
		    	$this.parent().removeClass("has-success").addClass("has-error").addClass("has-feedback");
		    	$this.parent().find(".form-control-feedback").removeClass("glyphicon-ok").addClass("glyphicon-remove");
		    } else {
		    	$this.parent().removeClass("has-error").addClass("has-success").addClass("has-feedback");
		    	$this.parent().find(".form-control-feedback").addClass("glyphicon-ok").removeClass("glyphicon-remove");
		    }
			
			
	    });
	};
})( jQuery );