(function( $ ){
	$.fn.radomLightbox = function(minWidth) {
		
		if (!minWidth) {
			minWidth = 650;
		}
		
		var $img = $(this);
		
		var preloader = new Image();
		preloader.onload = function() {
			
			var width = preloader.width;
			var height = preloader.height;
			
			var padLeft = 10;
			var padRight = 10;
			var padTop = 10;
			var padBottom = 10;
			
			if (width >= minWidth) {
				$img.css("width", minWidth + "px");
				$img.css("height", (height * minWidth / width) + "px");
				$img.css("cursor", "pointer");
				$img.click(function() {
					var $target = $("div#image-lightbox");
					var src = $(this).attr("src");
					
					var windowHeight = $(window).height();
					var windowWidth  = $(window).width();
					
					if ((preloader.width + padLeft + padRight) >= windowWidth) {
						originalWidth = preloader.width;
						originalHeight = preloader.height;
						preloader.width = windowWidth - padLeft - padRight;
						preloader.height = originalHeight / originalWidth * preloader.width;
					}

					if ((preloader.height + padTop + padBottom) >= windowHeight) {
						originalWidth = preloader.width;
						originalHeight = preloader.height;
						preloader.height = windowHeight - padTop - padBottom;
						preloader.width = originalWidth / originalHeight * preloader.height;
					}
					
					$target.find("img").attr("src", src);
					
					$target.find(".modal-dialog").css({
						'position': 'fixed',
						'width': preloader.width + padLeft + padRight,
						'height': preloader.height + padTop + padBottom,
						'top' : (windowHeight / 2) - ( (preloader.height + padTop + padBottom) / 2),
						'left' : '50%',
						'margin-left' : -1 * (preloader.width + padLeft + padRight) / 2,
						'margin-top' : 0,
						'margin-bottom' : 0,
						'padding-left' : padLeft, 
						'padding-right' : padRight,
						'padding-top' : padTop,
						'padding-bottom' : padBottom				
					});
					
					$target.find("img").css({
						'width': preloader.width,
						'height': preloader.height
					});

					$target.modal("show");
				});
			}				
		}
		
		preloader.src = $img.attr("src");
		
	};
})( jQuery );