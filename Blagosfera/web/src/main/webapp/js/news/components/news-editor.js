/**
 * Компонент для редактирования новости
 */

/**
 * Конструктор компонента
 * @param container контейнер для размещения компонента
 * @param options опции для дополнительных модулей редактирования callback'ов и стартовых данных
 *
 * Заметка: submitCallback оформляется в виде post запроса и должен принимать в качестве параметров
 * ссылку на объект NewsEditor и на отправляемые данные.
 * Url запроса и обработчики успеха/ошибки задается извне при создании компонента.
 *
 * @constructor
 */
function NewsEditor($container, news, options) {

    if (options.cancelCallback && typeof(options.cancelCallback) != "function" ) {
        throw new Error("options.cancelCallback is not a function!");
    }
    if (options.submitCallback && typeof(options.submitCallback) != "function" ) {
        throw new Error("options.submitCallback is not a function!");
    }

    var self = this;
    var newsId = null;
    var $titleInput = null;
    this.$container = $container;
    this.attachmentsEditor = null;
    this.categoryEditor = null;
    this.textEditor = null;
    this.tagEditor = null;


    //Поле для ввода заголовка
    var $titleLabel = $('<label>Заголовок новости</label>');
    $container.append($titleLabel);

    $titleInput = $('<input type="text" placeholder="Заголовок новости" class="form-control">');
    var $titleFormGroup = $('<div class="input-group"></div>');

    $titleFormGroup.append($titleInput);
    $container.append($titleFormGroup);


    //Редактор вложений
    if (options && options.attachmentsEditor) {
        var maxCountOfAttachments = options.attachmentsEditor.maxCountOfAttachments;

        var attachmentsEditorOptions = {};

        attachmentsEditorOptions.attachmentsAddingBlockCallback = function($attachmentsAddingBlock) {
            $titleFormGroup.append($attachmentsAddingBlock);
            $attachmentsAddingBlock.addClass("input-group-addon");
        };

        if (!maxCountOfAttachments) {
            throw new Error("options.attachmentsEditor.maxCountOfAttachments is not specified!");
        } else {
            this.attachmentsEditor = new AttachmentsEditor($container, maxCountOfAttachments, attachmentsEditorOptions);
        }
    }

    if (options && options.categoryEditor) {
        var categoryId = null;

        if (news && news.category && news.category.id) {
            categoryId = news.category.id;
        }

        this.categoryEditor = new CategoryEditor($container, categoryId);
    }

    //Текстовый редактор
    if (options && options.textEditor) {

        //Создание текстового редактора
        try {

            //Если в виджете используется AttachmentsEditor - вешаем на TextEditor событие onChange,
            // перемещающее все изображения в AttachmentsEditor.
            if (options.attachmentsEditor) {

                var onPostPasteTextEditor = function() {

                    var $body = $(self.textEditor.getEditor().getBody());

                    //Исходные адреса изображений
                    var imgSrcs = [];

                    $.each($body.find('img'), function () {

                        if ($(this).attr('src')) {
                            imgSrcs.push($(this).attr('src'));
                        }

                        $(this).remove();
                    });

                    //Фильтруем остальной контент (имитируя отдельный поток, чтобы фильр не упал в случае ошибки, связанной с медиа контентом)
                    setTimeout(function() {
                        self.textEditor.filter();
                    }, 0);

                    var remainedPlacesForAttachments = maxCountOfAttachments - self.attachmentsEditor.attachmentsCount();

                    if (remainedPlacesForAttachments < 1) {
                        return;
                    }

                    for (var i = 0; i < imgSrcs.length; ++i) {
                        (function() {
                            var index = i;
                            var img = $('<img>', {
                                src: imgSrcs[index],
                                error: function () {
                                },
                                load: function () {
                                    if (remainedPlacesForAttachments < 1) {
                                        return;
                                    }
                                    //Сохраняем на наш сервер
                                    $.radomJsonPost("/images/PHOTO/upload_url.json", {
                                        url: imgSrcs[index]
                                    }, function (r) {
                                        if (remainedPlacesForAttachments < 1) {
                                            return;
                                        }
                                        --remainedPlacesForAttachments;
                                        self.attachmentsEditor.putAttachment(r.image, "IMAGE",
                                            {doNotShowAlertOnAttachmentsOverflow: true});
                                    }, function (r) {
                                        if (r && r.message) {
                                            console.error(r.message);
                                        } else {
                                            console.error("Upload error");
                                        }
                                    });
                                }
                            });
                        })()
                    }
                };

                //Здесь будем хранить обработанные ссылки из текстового редактора
                var processedLinks = {};

                var onChange = function() {
                    //Читаем все ссылки в текстовом редакторе, определяем среди них ссылки на изображения и видео,
                    // после чего добавляем в редактор вложений, если не превышен лимит медиа контента
                    var $body = $(self.textEditor.getEditor().getBody());

                    var remainedPlacesForAttachments = maxCountOfAttachments - self.attachmentsEditor.attachmentsCount();

                    $.each($body.find('a'), function() {
                        var $this = $(this);

                        //Обрабатываем только ссылки c указанным ресурсом
                        if (!$this.attr('href')) {
                            return;
                        }

                        var href = $this.attr('href');

                        if (processedLinks[href]) {
                            //Ссылка обрабтывалась ранее
                            return;
                        }

                        processedLinks[href] = true;

                        if (remainedPlacesForAttachments < 1) {
                            return;
                        }

                        //Если ссылка на картинку или видео с youtube - обрабатываем
                        if (validateImageUrl(href)) {
                            //Формат ссылки подходит под изображение.
                            // Проверяем существование ресурса и при успехе пытаемся положить в редактор вложений.
                            var img = $('<img>', {
                                    src : href,
                                    error: function() {
                                        return;
                                    },
                                    load: function() {

                                        if (remainedPlacesForAttachments < 1) {
                                            return;
                                        }

                                        //Сохраняем на наш сервер
                                        $.radomJsonPost("/images/PHOTO/upload_url.json", {
                                            url : href
                                        }, function (r) {

                                            if (remainedPlacesForAttachments < 1) {
                                                return;
                                            }

                                            --remainedPlacesForAttachments;
                                            self.attachmentsEditor.putAttachment(r.image, "IMAGE",
                                                {doNotShowAlertOnAttachmentsOverflow: true});
                                        },function (r) {
                                            if (r && r.message) {
                                                console.error(r.message);
                                            } else {
                                                console.error("Ошибка загрузки");
                                            }
                                        });
                                    }
                                }
                            );
                        } else if (parseYoutubeUrl(href)) {
                            //Формат ссылки подходит под видео с youtube
                            --remainedPlacesForAttachments;
                            self.attachmentsEditor.putAttachment(href, "VIDEO",
                                {doNotShowAlertOnAttachmentsOverflow: true});
                        } else {
                            return;
                        }

                    });
                };

                if (options.textEditor.options) {
                    options.textEditor.options.onPostPaste = onPostPasteTextEditor;
                    options.textEditor.options.onChange = onChange;
                } else {
                    options.textEditor.options = {
                        onPostPaste : onPostPasteTextEditor,
                        onChange : onChange
                    }
                }
            };

            var textEditorAreaId = 'news-text-area';
            $container.append('<textarea id="' + textEditorAreaId + '" style="width: 100%;"></textarea>');
            this.textEditor = new NewsTextEditor('#' + textEditorAreaId, options.textEditor.options);

        } catch(e) {
            console.error(e.message);
        }
    };


    //Редактор тегов
    if (options && options.tagEditor) {
        var $tagEditorContainer = $('<div style="margin: 10px 0 15px 0;"></div>');

        if (news) {
            options.tagEditor.options.initialTags = news.tags;
        }

        self.tagEditor = new RadomTagEditor($tagEditorContainer, options.tagEditor.options);

        $container.append($tagEditorContainer);

        this.getTagEditor = function() {
            return self.tagEditor;
        }
    }



    //Костыль, без которого выпадающие списки категорий могут не закрываться при наличии текстового редактора
    if (self.categoryEditor && self.textEditor) {
        $(self.textEditor.getEditor().getDoc()).bind('click', function (e) {
            self.categoryEditor.close();
        });
    }


    //Создание управляющих кнопок
    var $cancelButton = $('<button class="btn btn-danger" style="margin-right: 4px;">Отменить</button>');
    var $saveButton = $('<button class="btn btn-primary">Добавить</button>');


    $(window).on('beforeunload' ,function() {
        if (!self.isEmpty()) {
            return 'Если Вы нажмете покините страницу, то все введенные Вами данные будут безвозвратно уничтожены. ' +
            'Вы уверены что хотите это сделать?';
        }
    });

    //При клике по кнопке отмены просто уничтожаем виджет
    $cancelButton.click(function () {

        if (!self.isEmpty()) {

            bootbox.dialog({
                message: 'Если Вы нажмете "ДА", то все введенные Вами данные будут безвозвратно уничтожены. ' +
                'Вы уверены что хотите это сделать?',
                buttons: {
                    danger: {
                        label: "Нет",
                        className: "btn-danger",
                        callback: function() {
                            return;
                        }
                    },
                    main: {
                        label: "Да",
                        className: "btn-primary",
                        callback: function() {
                            self.destroy();
                        }
                    }
                }
            });

            bootbox.confirm('',
                function (result) {
                    console.log(result);
                });
        } else {
            self.destroy();
        }
    });

    //При клике по кнопке отправки формы вызываем submitCallback
    $saveButton.click(function() {
        self.setDisabled(true);

        if (!validate()) {
            self.setDisabled(false);
            return;
        }

        options.submitCallback(self ,getNewsData());
    });

    //Добавление панели с кнопками
    var $buttonPanel = $('<div style="margin-top: 5px;"></div>');
    $buttonPanel.append($cancelButton);
    $buttonPanel.append($saveButton);
    $container.append($buttonPanel);


    /**
     * Устанавливает заголовок новости
     * @param title текст заголовка
     */
    this.setTitle = function(title) {
        $titleInput.val(title);
    };

    /**
     * Устанавливает текстовый контент в текстовый редактор
     * @param text текс или html для вставки в текстовый редактор
     */
    this.setText = function(text) {

        if (!self.textEditor) {
            return;
        }
        if (text) {
            self.textEditor.setText(text);
        }
    };

    this.setAttachments = function(attachments) {
        if (self.attachmentsEditor && attachments) {
            self.attachmentsEditor.setAttachments(attachments);
        }
    };

    if (news) {
        setNews(news);
    }

    /**
     * Запрещает/разрешает использование компонента
     * @param disabled
     */
    this.setDisabled = function(disabled) {

        if (self.attachmentsEditor) {
            //Блокируем/включаем редактор вложений
            self.attachmentsEditor.setDisabled(disabled);
        }

        if (self.textEditor) {
            //Блокируем/включаем текстовый редактор
            self.textEditor.setDisabled(disabled);
        }

        //Блокируем все кнопки и input'ы компонента
        var $controls = self.$container.find('input, button');
        $controls.attr('disabled', disabled);
    };


    /**
     * Позволяет определить, пуст ли новостной контент
     * @returns {boolean}
     */
    this.isEmpty = function() {
        if ($titleInput.val().trim()
            || (self.attachmentsEditor && self.attachmentsEditor.attachmentsCount())
            || (self.categoryEditor && self.categoryEditor.getCategoryId())
            || (self.textEditor && self.textEditor.getText().trim())
            || (self.tagEditor && self.tagEditor.getTags().length > 0)) {
            return false;
        }

        return true;
    };

    //Функция уничтожения редактора
    this.destroy = function() {

        $(window).off('beforeunload');

        if (self.attachmentsEditor) {
            self.attachmentsEditor.destroy();
        }

        if (self.categoryEditor) {
            self.categoryEditor.destroy();
        }

        if (self.textEditor) {
            self.textEditor.destroy();
        }

        $container.remove();

        if (options.cancelCallback) {
            options.cancelCallback();
        }
    };


    /**
     * Устанавливает контент новости
     * @param news объект с данными о новости
     */
    function setNews(news) {
        newsId = news.id;
        self.setTitle(news.title);
        self.setAttachments(news.attachments);
        self.setText(news.text);

        if (newsId) {
            $saveButton.text("Сохранить");
        }
    };

    /**
     * Возвращает объект с данными о новости
     * @returns {{}}
     */
    function getNewsData() {
        var result = {};

        if (newsId) {
            result.id = newsId;
        }

        result.title = $titleInput.val();

        if (self.attachmentsEditor) {
            var attachmentsObject = self.attachmentsEditor.getAttachmentsTransferObject();

            result.attachments = attachmentsObject;
        }

        if (self.categoryEditor) {
            result['category'] = {
                id: self.categoryEditor.getCategoryId()
            };
        }

        if (self.textEditor) {
            result.text = self.textEditor.getText();
        }

        if (self.tagEditor) {
            result.tags = self.tagEditor.getTags();
        }

        return result;
    };


    function validate() {
        $titleInput.val($titleInput.val().trim());

        if (!$titleInput.val().trim().length) {
            bootbox.alert("Не задан заголовок новости!");
            return false;
        }

        if (self.attachmentsEditor) {
            if (!self.attachmentsEditor.validate()) {
                return false;
            }
        }

        if (self.categoryEditor) {
            if (!self.categoryEditor.validate()) {
                return false;
            }
        }

        if (self.textEditor) {
            self.textEditor.filter();

            if (!self.textEditor.getText().length) {
                bootbox.alert("Не задан текст новости!");
                return false;
            }
        }

        return true;
    }

    function validateImageUrl(url) {
        return /^https?:\/\/.+\.(gif|png|jpg|jpeg)$/i.test(url);
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