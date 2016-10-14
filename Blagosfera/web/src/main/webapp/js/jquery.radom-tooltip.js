(function($) {
	$.fn.radomTooltip = function(options) {
		this.each(function() {
			if (!options) {
				options = {};
			}
			if(!options.delay) {
				options.delay = {
					show: RadomTooltipSettings ? RadomTooltipSettings.showDelay : 2000,
					hide: RadomTooltipSettings ? RadomTooltipSettings.hideDelay : 0
				};
			}
			options.html = true;
			if (RadomTooltipSettings && RadomTooltipSettings.enable) {
				$(this).tooltip(options);
			}
			$(this).attr("data-radom-tooltiped", "true");
			$(this).data("radomTooltipOptions", options);
		});
	};
	
	$.fn.radomReTooltip = function() {
		this.each(function() {
			var options = $(this).data("radomTooltipOptions");
			if(!options.delay) {
				options.delay = {
					show: RadomTooltipSettings ? RadomTooltipSettings.showDelay : 2000,
					hide: RadomTooltipSettings ? RadomTooltipSettings.hideDelay : 0
				};
			}
			$(this).tooltip("destroy");
			if (RadomTooltipSettings && RadomTooltipSettings.enable) {
				$(this).tooltip(options);
			}
			$(this).data("radomTooltipOptions", options);
		});
	};	
	
})(jQuery);