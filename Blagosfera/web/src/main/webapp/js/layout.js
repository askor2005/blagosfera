var Images = {getResizeUrl : function(url, resize) {
	if (url) {
		var dotIndex = url.lastIndexOf(".");
		return url.substr(0, dotIndex) + "_" + resize + url.substr(dotIndex);
	} else {
		return "";
	}
}};

var RadomUtils = {
		
	integerDivision : function(x, y) {
			return (x-x%y)/y
	},
		
	getDeclension : function(number, string1, string24, string50) {
		digit = (number < 0 ? (number - number * 2) : number) % 100;
		digit = digit > 20 ? digit % 10 : digit;
		return (digit == 1 ? string1 : (digit > 4 || digit < 1 ? string50 : string24));
	},
	
	getHumanReadableDatesDistance : function(hoursDistance, week1, week24, week50, day1, day24, day50, hour1, hour24, hour50, showHours) {
		var hours = hoursDistance % 24;
		var days = RadomUtils.integerDivision(hoursDistance, 24) % 7;
		var weeks = RadomUtils.integerDivision(hoursDistance, 24 * 7);
		return (weeks > 0 ? weeks + " " + RadomUtils.getDeclension(weeks, week1, week24, week50) + " " : "") + (days > 0 ? days + " " + RadomUtils.getDeclension(days, day1, day24, day50) + " " : "") + ((showHours && hours > 0) ? hours + " " + RadomUtils.getDeclension(hours, hour1, hour24, hour50) : "");
	},
	
	getHumanReadableDatesDistanceAccusative : function(hoursDistance, showHours) {
		return RadomUtils.getHumanReadableDatesDistance(hoursDistance, "неделю", "недели", "недель", "день", "дня", "дней", "час", "часа", "часов", showHours);
	},
	
	getSetting : function(key, deafultValue, callback) {
		$.radomJsonGet("/sharer/setting/get.json", {
			key : key,
			default_value : defaultValue 
		}, function(response) {
			if (callback) {
				callback(response.value);
			}
		});
	},
	
	setSetting : function(key, value, callback) {
		$.radomJsonPost("/sharer/setting/set.json", {
			key : key,
			value : value 
		}, function(response) {
			if (callback) {
				callback();
			}
		});		
	},
	
	 
	
	replaceLinks : function(text) {
		
		function splitText (text) {
		    var indexes = [];
		    var fromIndex = 0;
		    while (true) {
		        var index = text.indexOf("<a ", fromIndex);
		        if (index == -1) {
		             break;   
		        }
		        indexes.push(index);
		        fromIndex = index;
		        index = text.indexOf("/a>", fromIndex);
		        if (index == -1) {
		             break;   
		        }
		        index += 3;
		        indexes.push(index);
		        fromIndex = index;
		    }
		    
		    var parts = [];
		    for (var i = 0; i <= indexes.length; i++) {
		        var fromIndex = (i == 0 ? 0 : indexes[i - 1]);
		        var toIndex = i < indexes.length ? indexes[i] : undefined;
		        parts.push(text.substring(fromIndex, toIndex));
		    }
		    return parts;
		}
		
		var textParts = splitText(text);
		var result = "";
		
		for (var i = 0; i < textParts.length; i++) {
			if (i % 2 == 1) {
				result += textParts[i];
			} else {
				var words = textParts[i].split(new RegExp("[ \n\t;<>&]"));
				$.each(words, function(inedx, word){
					word = word.replace("\n", "");
					var matches = word.match(/^(https?:\/\/|ssh:\/\/|ftp:\/\/|file:\/|www\.|(?:mailto:)?[A-Z0-9._%+\-]+@)(.+)$/i);
					if (matches) {
							var part1 = matches[1];
							var part2 = matches[2];
							if (part1 == 'www.') {
								part1 = 'http://www.';
							} else if (/@$/.test(part1) && !/^mailto:/.test(part1)) {
								part1 = 'mailto:' + part1;
							}
							var href = part1 + part2;
							textParts[i] = textParts[i].replace(word,  "<a target='_blank' href='" + href + "'>" + matches[1] + matches[2] + "</a>");
					}
				});
				result += textParts[i];
			}
		}
		return result;
	},


	getYoutubeId: function(url){
		var regExp = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
		var match = url.match(regExp);
		return (match && match[1].length == 11) ? match[1] : null;
	},

    getYoutubePreview: function(url, type) {

		if (!type) {
			type = "hqdefault";
		} else if (type != "0" && type != "1" && type != "2" && type != "3"
			&& type != "default" && type != "hqdefault"
			&& type != "mqdefault" && type != "sddefault" && type != "maxresdefault") {
			throw new Error("RadomUtils.getYoutubePreview: Unknown type of youtube thumbnail.");
		}

        var regExp = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
        var match = url.match(regExp);
        return (match && match[1].length == 11) ? ("https://img.youtube.com/vi/" + match[1] + "/" + type +".jpg") : null;
    },

	getYoutubePreviewById: function(id, type) {

		if (!id) {
			throw new Error("RadomUtils.getYoutubePreview: Unknown id youtube video.");
		}

		if (!type) {
			type = "hqdefault";
		} else if (type != "0" && type != "1" && type != "2" && type != "3"
			&& type != "default" && type != "hqdefault"
			&& type != "mqdefault" && type != "sddefault" && type != "maxresdefault") {
			throw new Error("RadomUtils.getYoutubePreview: Unknown type of youtube thumbnail.");
		}

		return "https://img.youtube.com/vi/" + id + "/" + type +".jpg";
	}

};

function playDefaultSound() {
	ion.sound({
	    sounds: [
	        {
	            name: "button_tiny"
	        }
	    ],
	    volume: 1,
	    path: "/sounds/"
	    //preload: true
	});
	ion.sound.play("button_tiny");
}

$(document).ready(function(){
	bootbox.setDefaults({
		locale: "ru"
	});
	
	$(document).on('focusin', function(e) {
	    if ($(e.target).closest(".mce-window").length) {
	        e.stopImmediatePropagation();
	    }
	});
	

	$("div.modal-full-height .modal-body").css("height", Math.max(($(window).height() - 180), 200) + "px").css("overflow-y", "scroll");

	$('body').on('shown.bs.modal', function (e) {
		$(document).find("html head").append("<style id='mce-menu-position' type='text/css'> .mce-menu { position : fixed !important; max-height : 245px; } .mce-tooltip { position : fixed !important; } </style>");
	});

	$('body').on('hidden.bs.modal', function (e) {
		$(document).find("html head style#mce-menu-position").remove();
	});
	
});

var radomEventsManager = {};