/**
 * Компонент для редактирования вложений новости.
 * Под вложениями подразумеваются:
 *  1. Изображения;
 *  2. Видео.
 */

/**
 * Конструктор
 * @param maxCountOfAttachments - максимальное число вложений
 * @constructor
 */
function AttachmentsEditor($parent, maxCountOfAttachments, options) {

    var self = this;
    var attachments = {};
    var enabled = true;

    //Контейнер для виджета
    var $container = $('<div class="radom_attachments_container"></div>');

    //Блок добавления вложений
    var $attachmentsAddingBlock = $('<div class="dropdown radom_attachment_add_select"></div>');

    var $attachmentsAddingButton = $('<button class="dropdown-toggle radom_attachments_add radom_attachments_add_hoverable" data-toggle="dropdown"></button>');
    $attachmentsAddingButton.css('text-align', 'left');
    $attachmentsAddingButton.append($('<label style="cursor: inherit;">Фото/видео</label>'));
    $attachmentsAddingBlock.append($attachmentsAddingButton);

    var $attachmentsDropDownMenu = $('<ul class="dropdown-menu" role="menu"></ul>');

    var $imageAttachmentLi = $('<li class="dropdown-submenu"></li>');
    $imageAttachmentLi.append('<a tabindex="-1" href="#" class="dropdown-toggle" aria-hidden="true" ' +
        'data-toggle="dropdown"><span class="glyphicon glyphicon-picture" style="padding-right: 5px;"></span>Изображение</a>');
    var $imageAttachmentMenu = $('<ul class="dropdown-menu"></ul>');
    $imageAttachmentLi.append($imageAttachmentMenu);
    $attachmentsDropDownMenu.append($imageAttachmentLi);

    var $imageFileAttachmentLi = $('<li></li>');
    var $imageFileAttachmentA = $('<a>Выбрать файл</a>');

    $imageFileAttachmentA.on("click", function () {

        if (self.attachmentsCount() >= maxCountOfAttachments) {
            bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments  +  ' изображений и видео.');
            return;
        }

        $.radomUpload("image", "/files/upload.json", ["jpg", "jpeg", "png", "bmp", "gif"], function (response) {
            self.putAttachment(response.url, "IMAGE");
            $("body").find("form#radom-upload-form").remove();
        });
    });

    $imageFileAttachmentLi.append($imageFileAttachmentA);
    $imageAttachmentMenu.append($imageFileAttachmentLi);

    var $imageSrcAttachmentLi = $('<li></li>');
    var $imageSrcAttachmentA = $('<a>Ввести ссылку</a>');

    $imageSrcAttachmentA.on("click", function () {
        if (self.attachmentsCount() >= maxCountOfAttachments) {
            bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments  +  ' изображений и видео.');
            return;
        }

        bootbox.prompt({
            title: 'Введите ссылку на изображение',
            placeholder: 'Ссылка на изображение',
            callback: function (src) {

                if (src == null) {
                    return;
                }

                if (!src) {
                    bootbox.alert("Ссылка не была введена!");
                    return false;
                }

                //Введена ссылка на изображение
                var img = $('<img>', {
                        src: src,
                        error: function () {
                            bootbox.alert("Указана ссылка на несуществующее изображение!");
                        },
                        load: function () {
                            //Сохраняем на наш сервер
                            $.radomJsonPost("/images/PHOTO/upload_url.json", {
                                url: src
                            }, function (r) {
                                self.putAttachment(r.image, "IMAGE");
                            }, function (r) {
                                if (r && r.message) {
                                    bootbox.alert(r.message);
                                } else {
                                    bootbox.alert("Ошибка загрузки");
                                }
                            });
                        }
                    }
                );

            }
        });

    });

    $imageSrcAttachmentLi.append($imageSrcAttachmentA);
    $imageAttachmentMenu.append($imageSrcAttachmentLi);

    var $videoSrcAttachmentLi = $('<li></li>');
    var $videoSrcAttachmentA = $('<a aria-hidden="true">' +
        '<span class="glyphicon glyphicon-facetime-video" style="padding-right: 5px;"></span>' +
        'Видеозапись</a>');

    //Прикручиваем обработчики
    $videoSrcAttachmentA.on("click", function () {

        if (self.attachmentsCount() >= maxCountOfAttachments) {
            bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments  +  ' изображений и видео.');
            return;
        }

        bootbox.prompt({
            title: 'Введите ссылку на видеозапись с Youtube',
            placeholder: 'Ссылка на видеозапись с Youtube',
            callback: function (src) {

                if (src == null) {
                    return;
                }

                if (!src) {
                    bootbox.alert("Ссылка не была введена!");
                    return false;
                }

                if (parseYoutubeUrl(src)) {
                    //Введена ссылка на видео с youtube
                    self.putAttachment(src, "VIDEO");
                } else {
                    bootbox.alert("Указана неправильная ссылка на видеозапись с Youtube!");
                    return false;
                }

            }
        });
    });

    $videoSrcAttachmentLi.append($videoSrcAttachmentA);
    $attachmentsDropDownMenu.append($videoSrcAttachmentLi);

    $attachmentsAddingBlock.append($attachmentsDropDownMenu);

    if (options && options.attachmentsAddingBlockCallback) {
        options.attachmentsAddingBlockCallback($attachmentsAddingBlock);
    } else {
        $container.append($attachmentsAddingBlock);
    }

    //Создание области хранения вложений
    var $imgHolder = $('<div class="radom_attachments_img_holder"></div>');
    //По умолчанию эта область скрыта. И отображается только в случае, если непуста.
    $imgHolder.css('display', 'none');
    $container.append($imgHolder);

    /**
     * Позволяет заполнить редактор вложениями из переданного объекта
     * @param attachments
     */
    this.setAttachments = function(attachments) {
      for (var i = 0; i < attachments.length; ++i) {
          self.putAttachment(attachments[i]['src'], attachments[i]['type'], {ignoreAttachmentsLimit : true});
      }
    };


    /**
     * Позволяет провести валидацию медиа контента
     * @returns {boolean}
     */
    this.validate = function() {

        //Проверка на то, что число вложений не превышает допустимых пределов
        if (self.attachmentsCount() > maxCountOfAttachments) {
            bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments  +  ' изображений и видео.');
            return false;
        }

        return true;
    };

    /**
     * Получить базовый контейнер виджета
     * @returns {Mixed|jQuery|HTMLElement}
     */
    this.getContainer = function() {
        return $container;
    };

    /**
     * Запрещает взаимодействие с редактором кроме просмотра изображений и видео в lightbox'е
     * @param disabled
     */
    this.setDisabled = function(disabled) {
        $attachmentsAddingButton.attr('disabled', disabled);
        var $deleteAnchors = $container.find('.radom_attachments_delete');

        if (disabled) {
            $attachmentsAddingButton.css('cursor', 'not-allowed');
            $attachmentsAddingButton.removeClass('radom_attachments_add_hoverable');
            $deleteAnchors.css('cursor', 'not-allowed');
            $deleteAnchors.removeClass('radom_attachments_delete_hoverable');
        } else {
            $attachmentsAddingButton.css('cursor', 'pointer');
            $attachmentsAddingButton.addClass('radom_attachments_add_hoverable');
            $deleteAnchors.css('cursor', 'pointer');
            $deleteAnchors.addClass('radom_attachments_delete_hoverable');
        }

        enabled = !disabled;
    };

    this.destroy = function() {
        $imgHolder.mCustomScrollbar("destroy");
        $container.remove();
    };

    //Обновление документа
    $parent.append($container);
    $imgHolder.mCustomScrollbar({
        axis: "x",
        theme:"dark"
    });

    /**
     * Добавляет в виджет фото/[видео с youtube] по указанному параметру src
     * @param src
     * @param type
     * @param youtubeId
     * @param options
     */
    this.putAttachment = function(src, type, options) {

        if (self.attachmentsCount() >= maxCountOfAttachments && options && !options.ignoreAttachmentsLimit) {

            if (options.doNotShowAlertOnAttachmentsOverflow) {
                return;
            }

            bootbox.alert('Одна новость не может содержать более ' + maxCountOfAttachments  +  ' изображений и видео.');
            return;
        }

        if (type != "VIDEO" && type != "IMAGE") {
            throw new Error("AttachmentsEditor.putAttachment: unknown type of attachment '" + type + "'");
        }

        //Все вложения уникальны, поэтому если вложение с указанным src уже существует - выходим из функции.
        if (attachments[src]) {
            return;
        }

        //Обертка
        var $imgWrapper = $('<div class="radom_attachments_img_wrapper radom_attachments_scrollable_area_item"></div>');

        //Изображение
        var $img = $('<img/>');

        if (type == "VIDEO") {
            var youtubeId = parseYoutubeUrl(src);
            $img.attr('src', RadomUtils.getYoutubePreviewById(youtubeId, "hqdefault"));
            $img.css('cursor', 'pointer');

            var $wrapperA = $('<a style="width: inherit; height: inherit;"></a>')
                .attr('data-toggle', 'lightbox')
                .attr("data-gallery", 'news-creating')
                .attr('href', 'https://www.youtube.com/watch?v=' + youtubeId);
            $wrapperA.append($img);
            $imgWrapper.append($wrapperA);
            var $thumbPlay = $('<div class="news_thumb_play"></div>');
            $thumbPlay.bind('click', function() {
               $img.click();
            });
            $imgWrapper.append($thumbPlay);

        } else if (type == "IMAGE") {
            $img.attr('src', src);
            var $lightBoxAnchor = $('<a href="' + src + '" class="radom_attachments_lightbox"></a>')
                .attr('data-toggle', 'lightbox')
                .attr("data-gallery", 'news-creating');
            $lightBoxAnchor.append($img);
            $imgWrapper.append($lightBoxAnchor);
        }

        //Кнопка удаления вложения
        var $removeButton = $('<a class="radom_attachments_delete radom_attachments_delete_hoverable"></a>');

        $removeButton.on('click', function(event) {

            if (!enabled) {
                return false;
            }

            delete attachments[src];
            var newScrollerLeftOffset = $imgHolder.find('.mCSB_container')[0].scrollWidth + "px";
            $imgWrapper.remove();
            $imgHolder.mCustomScrollbar("destroy");
            $imgHolder.mCustomScrollbar({
                axis: "x",
                theme:"dark",
                setLeft: newScrollerLeftOffset
            });
            $imgHolder.mCustomScrollbar("scrollTo","right");

            //Если вложений не осталось, скрываем область их хранения
            if (self.attachmentsCount() == 0) {
                $imgHolder.css('display', 'none');
            }
        });

        $imgWrapper.append($removeButton);

        //Финал.
        $imgHolder.find('.mCSB_container').append($imgWrapper);
        attachments[src] = type;

        $imgHolder.mCustomScrollbar("update");
        $imgHolder.mCustomScrollbar("scrollTo","right");

        $imgHolder.css('display', '');
    };

    /**
     * Вычисляет число вложений в виджете
     */
    this.attachmentsCount = function() {
        var count = 0;

        for (var key in attachments) {
            ++count;
        }

        return count;
    };

    /**
     * Возвращает массив с информацией о вложениях для передачи серверу
     */
    this.getAttachmentsTransferObject = function() {

        var result = [];

        for (var key in attachments) {
            var item = {};

            item.src = key;
            item.type = attachments[key];

            //Запоминаем размеры изображения или превью видео
            var img;

            if (item.type == "IMAGE") {
                img = $container.find('img[src="' + key + '"]')[0];
            } else if (item.type == "VIDEO") {
                var previewUrl = RadomUtils.getYoutubePreviewById(parseYoutubeUrl(key), "hqdefault");
                img = $container.find('img[src="' + previewUrl + '"]')[0];
            }

            item.width = img.naturalWidth;
            item.height = img.naturalHeight;

            result.push(item);
        }

        return result;
    };

    function parseYoutubeUrl(url) {
        var youTubeId = false;

        var regExp = /(youtu(?:\.be|be\.com)\/(?:.*v(?:\/|=)|(?:.*\/)?)([\w'-]+))/i;
        var match = url.match(regExp);

        if (match && match[2].length == 11) {
            youTubeId = match[2];
        }

        return youTubeId;
    };

};