// x1, y1, x2, y2 - Координаты для обрезки изображения
// crop - Папка для обрезанных изображений
var x1, y1, x2, y2, crop = 'crop/';
var jcrop_api;
var onChangeCallbackJcrop=null;

jQuery(function($){
	// Подключение плагина к изображению

	// такой вид инициализации не работает в IE
	//$('#crop-target').Jcrop({
	//	onChange:   showCoords,
	//	onSelect:   showCoords,
	//	boxWidth: 500,
	//	boxHeight: 500,
	//	keySupport: false
	//},function(){
	//	jcrop_api = this;
	//});

	var minSize = 10;
	jcrop_api = $.Jcrop($('#crop-target'), {
		onChange:   showCoords,
		onSelect:   showCoords,
		boxWidth: 500,
		boxHeight: 500,
		minSize: [ minSize, minSize ],
		aspectRatio: 1,
		keySupport: false,
		touchSupport: true
	});

	//Центрирование загруженного изображения
	$(".jcrop-holder img").load(function() {
		$(".jcrop-holder").animate({
			marginLeft: -($(this).width()/2)
		}, 1000, function() {
			// Animation complete.
			setDefaultSelection();
		});
	});
	// Снять выделение	
	$('#release').click(function(e) {
		release();
	});
	// Соблюдать пропорции
	$('#ar_lock').change(function(e) {
		jcrop_api.setOptions(this.checked?
		{ aspectRatio: 4/3 }: { aspectRatio: 0 });
		jcrop_api.focus();
	});
	// Установка минимальной/максимальной ширины и высоты
	$('#size_lock').change(function(e) {
		jcrop_api.setOptions(this.checked? {
			minSize: [ 80, 80 ],
			maxSize: [ 350, 350 ]
		}: {
			minSize: [ 0, 0 ],
			maxSize: [ 0, 0 ]
		});
		jcrop_api.focus();
	});
	// Изменение координат
	function showCoords(c){
		if(onChangeCallbackJcrop) {
			onChangeCallbackJcrop(c);
		}

		x1 = c.x; $('#x1').val(c.x);
		y1 = c.y; $('#y1').val(c.y);
		x2 = c.x2; $('#x2').val(c.x2);
		y2 = c.y2; $('#y2').val(c.y2);

		$('#w').val(c.w);
		$('#h').val(c.h);

		if(c.w > 0 && c.h > 0){
			$('#crop').show();
		}else{
			$('#crop').hide();
		}
	}

	function setDefaultSelection() {
		//Установка первоначального выделения
		var bounds = jcrop_api.getBounds();
		var widthBound = bounds[0];
		var heightBound = bounds[1];

		//Сторона результирующего квадрата (выбирается минимальная сторона прямоугольника)
		var sideWidth = Math.min(widthBound, heightBound);

		//Выделяем область на фото таким образом, чтобы оно было максимально и по центру
		var xOffset, yOffset;

		xOffset = (widthBound - sideWidth) / 2;
		yOffset = (heightBound - sideWidth) / 2;

		jcrop_api.setSelect([xOffset, yOffset, widthBound - xOffset, heightBound - yOffset]);
	}

});

function release(){
	jcrop_api.release();
	$('#crop').hide();
}
// Обрезка изображение и вывод результата
jQuery(function($){
	$('#crop').click(function(e) {
		var img = $('#crop-target').attr('src');
		console.log(x1);
		console.log(x2);
		console.log(y1);
		console.log(y2);
		console.log(crop);
		/*$.post('action.php', {'x1': x1, 'x2': x2, 'y1': y1, 'y2': y2, 'img': img, 'crop': crop}, function(file) {
		 $('#cropresult').append('<img src="'+crop+file+'" class="mini">');
		 release();
		 });*/

	});
});