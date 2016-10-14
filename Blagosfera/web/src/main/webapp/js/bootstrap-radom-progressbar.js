(function( $ ){
	$.fn.radomProgressbar = function(params) {
		
		var $progress = $(this);
		var timeout = params.timeout;
		
		$progress.empty();
		
		var $bar = $('<div class="progress-bar progress-bar-info" role="progressbar"></div>');
		$progress.append($bar)
	
		$bar.css("-webkit-transition", "width " + timeout + "s linear");
		$bar.css("-moz-transition", "width " + timeout + "s linear");
		$bar.css("-ms-transition", "width " + timeout + "s linear");
		$bar.css("-o-transition", "width " + timeout + "s linear");
		$bar.css("transition", "width " + timeout + "s linear");
		$bar.attr("aria-valuemax", timeout);
		$bar.attr("data-transitiongoal", timeout);

		$bar.progressbar(params);
		
	};
})( jQuery );