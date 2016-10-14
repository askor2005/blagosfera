(function($) {
	$.fn.checkListBox = function(options) {

		var $select = this,
		    parent = $select.parent(),
		    $selectAll = $("<input type='checkbox' />"),
		    $selectAllLabel = $("<label class='check-list-box_select-all'>Выбрать всех</label>");
		$select.attr("multiple", true);
		$select.hide();
		
		createList(parent, $select);
		
		
		function createList(parent, select) {
			var div = $("<div class='check-list-box'></div>"),
			    ul = $("<ul></ul>");
			
			parent.append($selectAll);
			parent.append($selectAllLabel);
			$selectAllLabel.click(function(e) {
				var checked = $selectAll.prop("checked");
				$selectAll.prop("checked", !checked);
				$selectAll.trigger("change");
			});
			$selectAll.change(function(e) {
				var checked = $(this).prop("checked");
				div.find("input[type=checkbox]")
				    .prop("checked", checked)
				    .trigger("change");
			});
			
			$.each(select.children(), function(index, item) {
				var li = $("<li></li>"),
				    checkbox = $("<input type='checkbox' data-value='" + item.attributes.value.value + "'/>"),
				    label = $("<label data-value='" + item.attributes.value.value + "'>" + item.text + "</label>");
				
				checkbox.change(onChange);
				label.click(function(e) {
					var value = $(this).data("value"),
					    checkbox = $("input[data-value="+value+"]");
					checkbox.prop("checked", !checkbox.prop("checked"));
					checkbox.trigger("change");
				});
				li.append(checkbox);
				li.append(label);
				ul.append(li);
			});
			div.append(ul);
			parent.append(div);
		};
		
		function onChange(e) {
			var optValue = $(this).data("value"),
			    value = $(this).prop("checked"),
			    opt = $select.children("option[value="+optValue+"]");
			opt.prop("selected", value);
			
			var selectedCount = $select.find("option:selected").size(),
			    totalCount = $select.children().size();
			console.log("<>", selectedCount, totalCount);
			if(selectedCount === 0) {
				$selectAll.prop("indeterminate", false)
				    .prop("checked", false);
			} else if(selectedCount === totalCount) {
				$selectAll.prop("indeterminate", false)
			        .prop("checked", true);
			} else {
				$selectAll.prop("indeterminate", true);
			}
		};
	};
	
	
})(jQuery);