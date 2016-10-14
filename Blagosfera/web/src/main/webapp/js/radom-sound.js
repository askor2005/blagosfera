RadomSound = {
	audio : null,
	defaultId : 'button_tiny',
	playDefault : function() {
		RadomSound.play(RadomSound.defaultId);
	},
	play : function(id) {
		var src = '/sounds/' + id + '.mp3';
		RadomSound.audio.load(src);
		RadomSound.audio.play();
	},
};

audiojs.events.ready(function() {
	$("body").append("<style> .audiojs { display : block; position : absolute; top : 0; left : 0;	width : 1px; height : 1px; } </style>").append("<audio id='radom-audio-tag' preload='auto'></audio>");
	RadomSound.audio = audiojs.create(document.getElementById('radom-audio-tag'));
});