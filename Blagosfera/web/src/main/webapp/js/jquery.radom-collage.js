(function( $ ) {

    function collageRow(collage ,items, minHeight) {
        var $collage = $(collage);

        //Оперируем массивами чисел, чтобы лишний раз не нагружать DOM
        var widths = [];
        var heights = [];

        //Масштабируем размеры картинок таким образом, чтобы высота каждой оказалась равной minHeight
        //Также, в зависимости от позиции картинки в строке, выставляем отступы

        var margin = 5;

        for (var i = 0; i < items.length; ++i) {


            $(items[i]).css("margin-bottom", margin + "px");

            if (i == items.length - 1) {
                $(items[i]).css("margin-right", "0px");
            } else {
                $(items[i]).css("margin-right", margin + "px");
            }

            var width;
            var height;


            if ( $(items[i]).attr("orig-width") &&  $(items[i]).attr("orig-height")) {
                width =  $(items[i]).attr('orig-width');
                height = $(items[i]).attr('orig-height');
            } else {
                width =  $(items[i]).width();
                height = $(items[i]).height();
            }

            var scaleCoef = minHeight / height;

            if (scaleCoef != 1) {
                width = width * scaleCoef;
                height = height * scaleCoef;
            }

            widths.push(width);
            heights.push(height);
        }

        /* Возможны 2 случая:
         *   1 - длина строки меньше длины контейнера;
         *   2 - длина строки больше длины контейнера.
         */

        var rowWidth = 0;
        for (var i = 0; i < widths.length; ++i) {
            rowWidth += parseInt(widths[i]);
        }

        var scaleCoef = 1;

        //Ширину контейнера-коллажа вычисляем за вычетом отступов между картинками
        var collageWidth = $collage.width() - (items.length - 1) * margin;

        if (rowWidth < collageWidth) {
            scaleCoef = 1 + (collageWidth - rowWidth) / rowWidth;
        } else {
            scaleCoef = collageWidth / rowWidth;
        }

        for (var i = 0; i < items.length; ++i) {

            if (i == items.length - 1) {
                $(items[i]).css("width", widths[i] * scaleCoef - 2);
            } else {
                $(items[i]).css("width", widths[i] * scaleCoef);
            }
            $(items[i]).css("height", heights[i] * scaleCoef);

            $.each($(items[i]).find('img'), function() {
                $(this).css("width", $(items[i]).width());
                $(this).css("height", $(items[i]).height());
            })
        }

    };


    $.fn.radomCollage = function (imagesPerRow) {
        var $collage = $(this);
        var $collageItems = $collage.children();

        for (var i = 0; i < $collageItems.length; ++i) {

            var $item = $($collageItems[i]);

            if (!($item.attr("orig-width") > 0 && $item.attr("orig-height") > 0)) {
                if ($item.prop('tagName') == 'img' && $item[0].naturalWidth > 0 && $item[0].naturalHeight > 0) {
                    $item.attr("orig-width", $item[0].naturalWidth);
                    $item.attr("orig-height", $item[0].naturalHeight);
                } else
            if ($item.attr('width')  && $item.attr('height')) {
                    $item.attr("orig-width", $item.attr('width'));
                    $item.attr("orig-height", $item.attr('height'));
                } else {
                    $item.attr("orig-width", $item.width());
                    $item.attr("orig-height", $item.height());
                }
            }
        }

        for (var i = 0; i < $collageItems.length;) {

            //Число картинок для занесения в очередную строку
            var countOfImagesForNewRow;

            /*
             * В случае, когда число картинок не кратно imagesPerRow, первая строка
             * будет содержать число картинок, равное остатку от деления их общего числа на imagesPerRow,
             * а их размер получится больше.
             */
            if (i == 0 && $collageItems.length % imagesPerRow != 0) {
                countOfImagesForNewRow = $collageItems.length % imagesPerRow;
            } else {
                countOfImagesForNewRow = imagesPerRow;
            }

            //Получаем минимальную высоту из картинок строки
            var heightArray = [];
            var rowImages = [];

            for (var j = i; j < i + countOfImagesForNewRow && j < $collageItems.length; ++j) {

                if ($($collageItems[j]).attr("orig-height")) {
                    heightArray.push($($collageItems[j]).attr('orig-height'));
                } else {
                    heightArray.push($($collageItems[j]).height());
                }

                rowImages.push($collageItems[j]);
                $($collageItems[j]).css("display", "inline-block");
                $($collageItems[j]).css("border-radius", "6px");
                $($collageItems[j]).css("border", "6px solid #ddd");
            }

            var minHeight = Math.min.apply(Math, heightArray);

            collageRow($collage, rowImages, minHeight);
            i += countOfImagesForNewRow;
        }
    }
})( jQuery );