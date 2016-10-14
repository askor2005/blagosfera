(function( $ ){
	$.fn.radomDateInput = function(params) {
		
		var defaultParams = {
		    format: "dd.mm.yyyy",
		    weekStart: 1,
		    todayBtn: "linked",
		    language: "ru",
		    todayHighlight: true,
		    autoclose: true
		};
		
		var $this = $(this);
		$this.datepicker($.extend({}, defaultParams, params)).on('changeDate', function(e){
			$this.trigger("click");
		});
		
	};
	$.fn.radomDateRangeInput = function(params, onChangeCallBack) {
		var defaultParams = {
			"autoApply" : true,
			"timePicker": true,
			"timePicker24Hour": true,
			"timePickerIncrement": 1,
			//"startDate": new Date().format("dd.mm.yyyy"),
			//"endDate": new Date().format("dd.mm.yyyy"),
			"minDate": new Date().format("dd.mm.yyyy"),
			"opens": "left",
			"drops": "down",
			"buttonClasses": "btn btn-sm",
			"applyClass": "btn-success",
			"cancelClass": "btn-default",

			"locale": {
				"format": "DD.MM.YYYY HH:mm",
				"separator": " - ",
				"applyLabel": "Применить",
				"cancelLabel": "Отмена",
				"fromLabel": "От",
				"toLabel": "До",
				"customRangeLabel": "Custom",
				"daysOfWeek": [
					"Вс",
					"Пн",
					"Вт",
					"Ср",
					"Чт",
					"Пт",
					"Сб"
				],
				"monthNames": [
					"Январь",
					"Февраль",
					"Март",
					"Апрель",
					"Май",
					"Июнь",
					"Июль",
					"Август",
					"Сентябрь",
					"Октябрь",
					"Ноябрь",
					"Декабрь"
				],
				"firstDay": 1
			}
		};

		var $this = $(this);
		$this.daterangepicker($.extend({}, defaultParams, params), function(start, end, label) {
			var dateStart = start.toDate();
			var dateEnd = end.toDate();
			if (onChangeCallBack != null) {
				onChangeCallBack(dateStart, dateEnd);
			}
		});

	};

	$.fn.radomDateTimeInput = function(params, onChangeCallBack) {
		var $this = $(this);
		var defaultParams = {
			defaultDate: new Date(),
			viewMode: 'years',
			//dormat: 'DD.MM.YYYY',
			locale: 'ru'
		};

		$this.datetimepicker($.extend({}, defaultParams, params)).on("dp.change", function(event){
			if (onChangeCallBack != null) {
				onChangeCallBack(event.date.toDate());
			}
		});
	};

})( jQuery );