<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>
<%@ taglib prefix="security"
		   uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<t:insertAttribute name="head" />

<body>
<t:insertAttribute name="header" />
<div class="container main">
	<div class="row">
		<div class="col-xs-2 left-menu">
			<!--<div id="hide-left-menu" class="hide-menu-btn"></div>-->
			<div id="left-menu-wrapper" style="width:165px;">
				<div id="left-menu-height-wrapper">
					<t:insertAttribute name="leftSidebar" />
					<div style="height: 96px;"></div>
				</div>
			</div>
		</div>
		<div class="col-xs-7 main-block">
			<t:insertAttribute name="breadcrumb" />
			<t:insertAttribute name="content" />
		</div>
		<div class="col-xs-3 right-menu">
			<!--<div id="hide-right-menu" class="hide-menu-btn"></div>-->
			<div id="right-menu-wrapper" style="width:277px;">
				<div id="right-menu-height-wrapper">
					<t:insertAttribute name="rightSidebar" />
					<div style="height: 96px;"></div>
				</div>
			</div>
		</div>
	</div>
	<p id="back-to-top"><a href="#top"><span class="glyphicon glyphicon-chevron-up"></span>Наверх</a></p>
</div>
<t:insertAttribute name="footer" />

<script type="text/javascript">
	$(document).ready(function(){
		$(window).on('beforeunload ',function() {
			clearJivositeInfo();
			return undefined;
		});
		var $leftMenuHeightWrapper = $("#left-menu-height-wrapper");
		var $leftMenuWrapper = $("#left-menu-wrapper");
		var $leftMenu = $(".left-menu");

		var $mainBlock = $('.container.main .row .main-block');

		var $rightMenuHeightWrapper = $("#right-menu-height-wrapper");
		var $rightMenuWrapper = $("#right-menu-wrapper");
		var $rightMenu = $(".right-menu");

		$('#left-sidebar-accordion').on('hidden.bs.collapse', onLeftCollapse);
		$('#left-sidebar-accordion').on('shown.bs.collapse', onLeftCollapse);
		function onLeftCollapse() {
			var menuHeightFix = $leftMenuHeightWrapper.offset().top + $leftMenuHeightWrapper.height() - $("body").height();
			if(menuHeightFix>0) {
				var newScrollPosition = $leftMenuWrapper.scrollTop() + menuHeightFix;
				$leftMenuWrapper.scrollTo(newScrollPosition)
			}
			$leftMenu.height($leftMenuHeightWrapper.height());
		}

		$('#community-sidebar-accordion').on('hidden.bs.collapse', onRightCollapse);
		$('#community-sidebar-accordion').on('shown.bs.collapse', onRightCollapse);
		function onRightCollapse() {
			$rightMenu.height($rightMenuHeightWrapper.height());
			var menuHeightFix = $rightMenuHeightWrapper.offset().top + $rightMenuHeightWrapper.height() - $("body").height();
			if(menuHeightFix>0) {
				var newScrollPosition = $rightMenuWrapper.scrollTop() + menuHeightFix;
				$rightMenuWrapper.scrollTo(newScrollPosition)
			}
		}

		$(window).resize(onResize);

		onResize();

		var prevHeight = $mainBlock.height();
		setInterval(function(){
			if ($mainBlock.height() != prevHeight) {
				$mainBlock.trigger('heightChange'); //<====
			}
		}, 1000);

		$mainBlock.bind('heightChange', function(){
			onResize();
		});

		//setTimeout(onResize, 2000); // для некоторых страниц, где высота контента зависит от загружаемых аяксом данных
		function onResize() {
			if($leftMenuHeightWrapper.height() > $mainBlock.height() ) {
				$leftMenuWrapper.css("position", "static");
			} else {
				$leftMenuWrapper.height($(window).height());
				$leftMenuWrapper.css("position", "fixed");
				$leftMenuWrapper.css("overflow-y", "hidden");
			}

			if($rightMenuWrapper.height() == 0 || $rightMenuWrapper.height() > $mainBlock.height() ) {
				$rightMenuWrapper.css("position", "static");
			} else {
				$rightMenuWrapper.height($(window).height());
				$rightMenuWrapper.css("position", "fixed");
				$rightMenuWrapper.css("overflow-y", "hidden");
			}
		}

		var $scrollable = $(jQuery.browser.webkit ? "body": "html"); // фикс чтобы работало во всех браузерах
		var lastScrollPosition = $scrollable.scrollTop(); // когда слишком много ивентов(например если скролить мышкой щёлкая по полосе прокрутки), то он не правильно высчитывается
		var newLeftScrollPosition = $leftMenuWrapper.scrollTop();
		var newRightScrollPosition = $rightMenuWrapper.scrollTop();
		$(window).scroll(function() {
			var currentScrollPosition = $scrollable.scrollTop();
			var scrollOffset = currentScrollPosition - lastScrollPosition;
			onScrollOffset(scrollOffset);
			lastScrollPosition = currentScrollPosition;
		});

		onScrollOffset(lastScrollPosition);
		function onScrollOffset(scrollOffset) {
			newLeftScrollPosition += scrollOffset;
			if(newLeftScrollPosition < 0) newLeftScrollPosition = 0;
			var leftMaxScroll = $leftMenuHeightWrapper.height() - $leftMenuWrapper.height();
			if(newLeftScrollPosition > leftMaxScroll) newLeftScrollPosition = leftMaxScroll;
			$leftMenuWrapper.scrollTo(newLeftScrollPosition);

			newRightScrollPosition += scrollOffset;
			var rightMaxScroll = $rightMenuHeightWrapper.height()- $rightMenuWrapper.height();
			if(newRightScrollPosition < 0) newRightScrollPosition = 0;
			if(newRightScrollPosition > rightMaxScroll) newRightScrollPosition = rightMaxScroll;
			$rightMenuWrapper.scrollTo(newRightScrollPosition);
		}

		var timeoutOpenMenu,
			timeoutHideMenu,
			hideMenuLeft = false,
			hideMenuRight = false,
			standartMinWidth = $('.right-menu').css('min-width');

		var leftMenuDisplay = radomLocalStorage.getItem('left-menu-display');
		if(leftMenuDisplay) {
			$leftMenuWrapper.css("display",leftMenuDisplay);
		}

		var rightMenuDisplay = radomLocalStorage.getItem('right-menu-display');
		if(leftMenuDisplay) {
			$rightMenuWrapper.css("display",rightMenuDisplay);
		}

		$('.right-menu').attr('standart-min-width', standartMinWidth);
		$('.left-menu').css('margin-left', radomLocalStorage.getItem('left-menu-margin') + 'px');
		$('.right-menu').css({'width': radomLocalStorage.getItem('right-menu-width'), 'min-width': parseInt(radomLocalStorage.getItem('right-menu-width'), 10)});

		/*$('#hide-left-menu').bind("mousedown", function (event) {
			clearTimeout(timeoutOpenMenu);
			var menu = $(this).parent('.left-menu'),
				offsetLeft = menu.offset().left,
				marginLeft = parseInt(menu.css("margin-left"), 10),
				mainWidth = $('.main-block').width(),
				newMarginLeft;
			$('body').bind("mousemove", function (event) {
				event.preventDefault();

				var w = event.pageX - offsetLeft + marginLeft - 30,
					left = w - menu.width();
				if(w + 30 > parseInt(menu.css("max-width"), 10) || w + 30 < parseInt(menu.css("min-width"), 10)) {
					if (-left > (menu.width() + 30) / 2) {
						menu.css('margin-left', -menu.width() - 15);
						newMarginLeft = parseInt(menu.css('margin-left'), 10);
						$('.main-block').css('width', mainWidth - newMarginLeft + marginLeft + 30);
						radomLocalStorage.setItem('left-menu-margin', parseInt($('.left-menu').css('margin-left'), 10));
						radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
						$(this).unbind("mousemove mouseup");
					}
					return;
				}
				menu.css("margin-left", left);
				$('.main-block').css("width", mainWidth - left + marginLeft + 29);
			});
			$('body').bind("mouseup", function (event) {
				radomLocalStorage.setItem('left-menu-margin', parseInt($('.left-menu').css('margin-left'), 10));
				radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
				$(this).unbind("mousemove mouseup");
			});
			hideMenuLeft = false;
		});*/

		/*$('#hide-left-menu').click(function() {
			clearTimeout(timeoutOpenMenu);
			var marginLeft = parseInt($('.left-menu').css("margin-left"), 10),
				mainWidth = $('.main-block').width(),
				menuWidth = $('.left-menu').width(),
				newMarginLeft;
			if(-marginLeft < (menuWidth + 30) / 2) {
				$('.left-menu').css('margin-left', -menuWidth - 15);
				newMarginLeft = parseInt($('.left-menu').css('margin-left'), 10);
				$('.main-block').css('width', mainWidth - newMarginLeft + marginLeft + 30);
			} else {
				$('.left-menu').css('margin-left', 0);
				$('.main-block').css('width', mainWidth + marginLeft + 30);
			}
			hideMenuLeft = false;
			radomLocalStorage.setItem('left-menu-margin', newMarginLeft);
			radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
			$(window).resize();
			$(document).trigger('sideMenuResizeEvent');
		});*/
		/*$('#hide-left-menu').click(function() {
			if($leftMenuWrapper.css("display") == "none") {
				$leftMenuWrapper.css("display","inline");
				radomLocalStorage.setItem('left-menu-display', "inline");
			} else {
				$leftMenuWrapper.css("display","none");
				radomLocalStorage.setItem('left-menu-display', "none");
			}
		});*/

		$('.left-menu').hover(function(){
			var marginLeft = parseInt($('.left-menu').css("margin-left"), 10),
				menuWidth = $('.left-menu').width();
			clearTimeout(timeoutHideMenu);
			if(-marginLeft > (menuWidth + 30) / 2) {
				timeoutOpenMenu = setTimeout(function () {
					hideMenuLeft = true;
					openLeftMenu()
				}, 1500);
			}
		},function(){
			clearTimeout(timeoutOpenMenu);
			if(hideMenuLeft == true) {
				clearTimeout(timeoutHideMenu);
				timeoutHideMenu = setTimeout(function () {
					hideMenuLeft = false;
					hideLeftMenu()
				}, 500);
			}
		});

		/*$('#hide-right-menu').bind("mousedown", function (event) {
			clearTimeout(timeoutOpenMenu);
			var menu = $(this).parent('.right-menu'),
				offsetLeft = menu.offset().left,
				marginLeft = parseInt(menu.css("left"), 10),
				mainWidth = $('.main-block').width(),
				menuWidth = $('.right-menu').width(),
				standartMenuWidth = parseInt($('.right-menu').attr('standart-width'), 10);
			$('body').bind("mousemove", function (event) {
				event.preventDefault();
				var w = event.pageX - offsetLeft + marginLeft,
					rw = menuWidth - w + marginLeft + 15,
					dynamicMenuWidth = $('.right-menu').width();
				if(rw > parseInt(menu.css("max-width"), 10) || rw < parseInt(menu.attr("standart-min-width"), 10)) {
					if (rw < (standartMenuWidth + 15) / 2) {
						$('.right-menu').css({'width': 5, 'min-width': 5});
						$('.main-block').css('width', $('.main-block').width() + dynamicMenuWidth + 30);
						radomLocalStorage.setItem('right-menu-width', $('.right-menu').width() + 15);
						radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
						$(this).unbind("mousemove mouseup");
					}
					return;
				}
				menu.css("width", rw);
				$('.main-block').css("width", mainWidth + w + marginLeft + 30);
			});
			$('body').bind("mouseup", function (event) {
				$(this).unbind("mousemove mouseup");
				radomLocalStorage.setItem('right-menu-width', $('.right-menu').width() + 15);
				radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
			});
			hideMenuRight = false;
		});*/

	/*$('#hide-right-menu').click(function() {
			clearTimeout(timeoutOpenMenu);
			var mainWidth = $('.main-block').width(),
				menuWidth = $('.right-menu').width(),
				standartMenuWidth = parseInt($('.right-menu').attr('standart-width'), 10);
			if(menuWidth > (standartMenuWidth + 15) / 2) {
				$('.right-menu').css({'width': 5, 'min-width': 5});
				$('.main-block').css('width', mainWidth + menuWidth + 30);
			} else {
				$('.right-menu').css({'min-width': $('.right-menu').attr('standart-min-width'),'width': standartMenuWidth + 15});
				$('.main-block').css('width', mainWidth - standartMenuWidth + menuWidth + 30);
			}
			hideMenuRight = false;
			radomLocalStorage.setItem('right-menu-width', $('.right-menu').width() + 15);
			radomLocalStorage.setItem('main-block-width', $('.main-block').width() + 30);
			$(window).resize();
			$(document).trigger('sideMenuResizeEvent');
		});*/

		/*$('#hide-right-menu').click(function() {
			if($rightMenuWrapper.css("display") == "none") {
				$rightMenuWrapper.css("display","inline");
				radomLocalStorage.setItem('right-menu-display', "inline");
			} else {
				$rightMenuWrapper.css("display","none");
				radomLocalStorage.setItem('right-menu-display', "none");
			}
		});*/

		function openLeftMenu() {
			var marginLeft = parseInt($('.left-menu').css("margin-left"), 10),
				mainWidth = $('.main-block').width();
			hideMenuLeft = true;
			$('.left-menu').attr('prev-margin-left', parseInt($('.left-menu').css('margin-left'), 10) + 15);
			$('.left-menu').css('margin-left', 0);
			$('.main-block').css('width', mainWidth + marginLeft + 30);
		}

		function hideLeftMenu() {
			var marginLeft = parseInt($('.left-menu').css("margin-left"), 10),
				mainWidth = $('.main-block').width(),
				prevMarginLeft = parseInt($('.left-menu').attr('prev-margin-left'), 10),
				newMarginLeft;
			$('.left-menu').css('margin-left', prevMarginLeft - 15);
			newMarginLeft = parseInt($('.left-menu').css('margin-left'), 10);
			$('.main-block').css('width', mainWidth - newMarginLeft + marginLeft + 30);
		}

		function openRightMenu() {
			var mainWidth = $('.main-block').width(),
				menuWidth = $('.right-menu').width(),
				standartMenuWidth = parseInt($('.right-menu').attr('standart-width'), 10);
			$('.right-menu').css('width', standartMenuWidth + 15);
			$('.main-block').css('width', mainWidth - standartMenuWidth + 30);
		}

		function hideRightMenu() {
			var mainWidth = $('.main-block').width(),
				menuWidth = $('.right-menu').width(),
				standartMenuWidth = parseInt($('.right-menu').attr('standart-width'), 10);
			$('.right-menu').css('width', 5);
			$('.main-block').css('width', mainWidth + standartMenuWidth + 30);
		}
	});
</script>
<!-- Yandex.Metrika counter -->
<script type="text/javascript">
	(function (d, w, c) {
		if (location.host.startsWith('localhost')) return;

		(w[c] = w[c] || []).push(function () {
			try {
				w.yaCounter37200660 = new Ya.Metrika({
					id: 37200660,
					clickmap: true,
					trackLinks: true,
					accurateTrackBounce: true,
					webvisor: true,
					trackHash: true
				});
			} catch (e) {
			}
		});

		var n = d.getElementsByTagName("script")[0];
		var s = d.createElement("script");
		var f = function () {
			n.parentNode.insertBefore(s, n);
		};

		s.type = "text/javascript";
		s.async = true;
		s.src = "https://mc.yandex.ru/metrika/watch.js";

		if (w.opera == "[object Opera]") {
			d.addEventListener("DOMContentLoaded", f, false);
		} else {
			f();
		}

	})(document, window, "yandex_metrika_callbacks");
</script>
<noscript>
	<div><img src="https://mc.yandex.ru/watch/37200660" style="position: absolute; left: -9999px;" alt=""/></div>
</noscript>
<!-- BEGIN JIVOSITE CODE {literal} -->
<script type='text/javascript'>
	var jivositeKey = "${jivositeKey}";
	function jivo_onLoadCallback() {
		$.radomJsonGet(
				"/jivosite/info.json",
				null,
				function (response) {
					clearJivositeInfo();
					if (response.userInfo) {
						var date = new Date(0);
						jivo_api.setContactInfo(
								{
									name: response.userInfo.name,
									email: response.userInfo.email,
									phone: response.userInfo.phone,
									description : ""

								}
						);
						//jivo_api.setUserToken(response.userToken);
					}
				});
	}
	function clearJivositeInfo() {
		delete_cookie('jv_client_name_'+jivositeKey);
		delete_cookie('jv_email_'+jivositeKey);
		delete_cookie('jv_phone_'+jivositeKey);
	}
	function delete_cookie ( cookie_name )

	{
		var cookie_date = new Date ( );
		cookie_date.setTime ( 0 );
		document.cookie = cookie_name += "=; path=/ ; expires=" +
				cookie_date.toUTCString();

	}

	(function(){ var widget_id = jivositeKey;var d=document;var w=window;function l(){
		var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);}if(d.readyState=='complete'){l();}else{if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})();
</script>
<!-- {/literal} END JIVOSITE CODE -->
</body>
</html>
