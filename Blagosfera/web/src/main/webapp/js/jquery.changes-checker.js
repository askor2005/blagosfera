(function( $ ){
	$.fn.changesChecker = function(action) {

		var inputsSelector = "[name]:input:not([data-changes-checker-ignore=true]):not([name=country_id]):not([data-field-type=ADDRESS_FIELD_DESCRIPTION])";

		function init() {
			if ($(window).data("changesCheckerInitialized")) {
				return;
			}
			$(window).bind("beforeunload", function() {
				var $container = $("[changes-checker-container]");
				if ($container.length > 0 && $container.changesChecker("check")) {
					return("Имеются несохраненные изменения");
				}
			});
			$(window).data("changesCheckerInitialized", true);
		}

		function refresh($container) {
			$container.data("changesCheckerData", {
				serialized : serialize($container.find(inputsSelector)),
			});
			
			var $groups = $container.find(".form-group");
			$.each($groups, function(index, group) {
				var $group = $(group);
				$group.data("changesCheckerData", {
					serialized : serialize($group.find(inputsSelector)),
				});
				var $inputs = $group.find(inputsSelector);
				$inputs.off("change");
				$inputs.on("change", function() {
					var $input = $(this);
					var $parentGroup = $input.closest(".form-group");
					var savedParentGroupSerialized = $parentGroup.data("changesCheckerData").serialized;
					var currentParentGroupSerialized = serialize($parentGroup.find(inputsSelector));
					if (savedParentGroupSerialized != currentParentGroupSerialized) {
						$parentGroup.addClass("changed-form-group");
					} else {
						$parentGroup.removeClass("changed-form-group");
					}
				});
			});
			
			$("[changes-checker-container] .form-group").removeClass("changed-form-group");
			$("[changes-checker-container]").removeAttr("changes-checker-container");
			$container.attr("changes-checker-container", "");
		}

		function destroy($container) {
			$container.removeAttr("changes-checker-container");
			$container.find(".form-group").removeClass("changed-form-group");
		}
		
		function check($container) {
			var newValue = serialize($container.find(inputsSelector));
			var oldValue = $container.data("changesCheckerData").serialized;
			return (($container.is("[changes-checker-container]")) && (newValue != oldValue));
		}

		function serialize(jqNodes) {
			// Делаем сортировку имён у полей по возростанию и склеиваем имена со значением, чтоб при любом вызове
			// метода был всегда один и тот же результат
			var values = [];
			jqNodes.each(function(){
				// Делаем исключения для полей с телефоном, потому как плагин подгружает данные для форматирования телефона
				// асинхронно
				if ($(this).attr("data-field-type") != null &&
					($(this).attr("data-field-type") == "MOBILE_PHONE" || $(this).attr("data-field-type") == "LANDLINE_PHONE") ) {
					var value = $(this).val();
					if (value == null) {
						value = "";
					}
					value = value.replace(/[\+\s\-\)\(]*/g, "");

					values.push({name: $(this).attr("name"), value: value});
				} else if ($(this).val() != null && $(this).val() != "") {
					values.push({name: $(this).attr("name"), value: $(this).val()});
				}
			});
			values.sort(function(a, b){
				var result = -1;
				if (a.name > b.name) {
					result = 1;
				}
				return result;
			});
			var result = "";
			for (var index in values) {
				var obj = values[index];
				result += obj.name + "=" + obj.value + "&";
			}
			return result;
		}
		
		if (!action) {
			action = "init";
		}
		
		switch(action) {
		
		case "init":
			init();
			refresh($(this));
			break;
			
		case "refresh":
			refresh($(this));
			break;

		case "destroy":
			destroy($(this));
			break;
			
		case "check":
			return check($(this));
		
		}
		
	};
})( jQuery );