var ScrollListener = {

	firstPage: null,
		
	
	
    finished: false,
    page: 1,
    perPage: 20,
    url: "",
    getParams: null,
    beforeCallback: null,
    afterCallback: null,
    $loaderElement : null,
    method : null,
    $threshold: null,
    id : null,
    loading : false,
    active : false,
    
    off : function() {
    	ScrollListener.active = false;
		if (ScrollListener.id != null) {
			$("#" + ScrollListener.id).off("scroll");
		}
		else
			$(document).off("scroll");
    },
    
    init: function(url, method, getParams, beforeCallback, afterCallback, $threshold, firstPage, id,onlyFormData) {

    	ScrollListener.off();
    	
        ScrollListener.page = 1;
        ScrollListener.url = url;
        ScrollListener.method = method;
        ScrollListener.getParams = getParams;
        ScrollListener.beforeCallback = beforeCallback;
        ScrollListener.afterCallback = afterCallback;
        ScrollListener.finished = false;
        ScrollListener.$threshold = $threshold;
        ScrollListener.firstPage = firstPage;
        ScrollListener.id = id;
        ScrollListener.onlyFormData = onlyFormData;
        ScrollListener.load();
        
		if (ScrollListener.id == null) {
			$(document).on("scroll", function() {
				if (ScrollListener.active) {
					if (ScrollListener.$threshold) {
						if ((!ScrollListener.finished) && ($(window).height() + $(document).scrollTop() >= ScrollListener.$threshold.offset().top)) {
							//$threshold - одноразовый параметр и его следует переназначать во время обновления контента
							ScrollListener.$threshold = null;
							ScrollListener.load();
						}            		
					} else {
						if ((!ScrollListener.finished) && ($(window).height() + $(document).scrollTop() == $(document).height())) {
							ScrollListener.load();
						}            		
					}

				}
			});
		}
		else
		{
			var div = $("#" + id);
			div.on("scroll", function() {
				if (ScrollListener.active) {
					if (ScrollListener.$threshold) {
						if ((!ScrollListener.finished) && (div.height() + div.scrollTop() >= ScrollListener.$threshold.offset().top)) {
							//$threshold - одноразовый параметр и его следует переназначать во время обновления контента
							ScrollListener.$threshold = null;
							ScrollListener.load();
						}            		
					} else {
						if ((!ScrollListener.finished) && (div.height() + div.scrollTop() == div.get(0).scrollHeight)) {
							ScrollListener.load();
						}            		
					}

				}
			});

		}
        ScrollListener.active = true;
        
    },

    load: function() {
		
    	if (ScrollListener.loading) {
    		return;
    	}
    	
    	ScrollListener.loading = true;
    	
        var data = ScrollListener.getParams ? ScrollListener.getParams() : {};
        data.page = ScrollListener.page;
        ScrollListener.page += 1;
        if (!data.per_page) {
        	data.per_page = ScrollListener.perPage;
        }

        var queryString = "";
		var requestData = {};
		var requestUrl = ScrollListener.url;
        if (ScrollListener.onlyFormData) {
			for (field in data) {
				requestData[field] = data[field];
			}
		}
		else {
			for (field in data) {
				if (field != "query") {
					if (queryString != "") {
						queryString += "&";
					}
					queryString += field + "=" + data[field];
				}
			}

			requestUrl = ScrollListener.url + "?" + queryString;
			requestData.query = data.query;
		}
    	if (ScrollListener.beforeCallback) {
    		ScrollListener.beforeCallback();
    	}
    	
    	if (ScrollListener.firstPage) {
    		if (ScrollListener.afterCallback) {
        		ScrollListener.afterCallback(ScrollListener.firstPage, data.page);
        	}
    		ScrollListener.firstPage = null;
    		ScrollListener.loading = false;
    	} else  {
	        $.ajax({
	            type: ScrollListener.method,
	            dataType: "json",
	            data: requestData,
	            url: requestUrl,
	            success: function(response) {
	                if (($.isArray(response) && response.length == 0) || ($.isArray(response.list) && (response.list.length == 0))) {
	                    ScrollListener.finished = true;
	                }
	            	if (ScrollListener.afterCallback) {
	            		ScrollListener.afterCallback(response, data.page);
	            	}
	                ScrollListener.loading = false;
	            },
	            error: function() {
	            	ScrollListener.loading = false;
	                console.log("error");
	            }
	        });
    	}
	    
    }

}