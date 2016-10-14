/**
 * Компонент для редактирования текста новости.
 * В качестве основы используется tinymce
 */

/**
 * Конструктор
 * @param selector селектор для textarea, на которую будет навешан tinymce
 * @param toolbar - строка настроек toolbar'a редактора.
 * @constructor
 */
function NewsTextEditor(selector, options) {

    //Ссылка на editor от tinymce
    var editor = null;
    var $overlayDiv = null;
    var self = this;

    if (!selector) {
        new Error('NewsTextEditor: selector is not specified.');
    }

    var toolbar = null;
    var approvedElements = null;

    if (options) {
        toolbar = options.toolbar || "";
        approvedElements = options.approvedElements || [];
    } else {
        toolbar = "";
        approvedElements = [];
    }

    var initOptions = {
        selector: selector, //Селектор textarea для обертки
        toolbar: toolbar,   //Элементы toolbar'a
        menubar: false,     //Отключаем menubar
        height: 150,        //Высота текстового поля
        setup: function(ed) {
            editor = ed;

            if (options && options.onChange) {
                editor.on('change', options.onChange);
                editor.on('init', function() {
                    editor.fire('paste');
                });
            }
        }
    };

    if (options && options.onPostPaste) {
        initOptions.plugins = "paste, autolink";
        initOptions.paste_postprocess = options.onPostPaste;
    }

    //Инициализация tinymce
    tinymce.init(initOptions);

    /**
     * Возвращает editor tinymce
     * @returns {*}
     */
    this.getEditor = function() {
        return editor;
    };

    /**
     * Возвращает текст новости
     * @returns (String) текст новости или null, если редактора не существует
     */
    this.getText = function() {
        if (editor) {
            return editor.getContent();
        }

        return null;
    };

    /**
     * Устанавливает текстовый контент в tinymce
     * @param text текс или html для вставки в редактор tinymce
     */
    this.setText = function (text) {

        if (!editor) {
            return;
        }

        if (text) {
            editor.setContent(text);
        }
    };

    /**
     * Запрещает/разрешает использование компонента
     * @param disabled
     */
    this.setDisabled = function(disabled) {

        if (!$overlayDiv) {
            //Лучше его здесь. При событии "setup" от tinymce DOM модель может быть еще не готова.
            createOverlayDiv();
        }

        if (disabled) {
            $overlayDiv.css('display', '');
        } else {
            $overlayDiv.css('display', 'none');
        }

    };

    //Функция уничтожения редактора
    this.destroy = function() {
        if (editor) {
            editor.destroy();
        }
    };

    function createOverlayDiv() {
        //Инициализация блока, симулирующего свойство input'ов "disable"
        $overlayDiv = $('<div></div>');
        $overlayDiv.css('width', '100%');
        $overlayDiv.css('height', '100%');
        $overlayDiv.css('position', 'absolute');
        $overlayDiv.css('top', '0');
        $overlayDiv.css('background-color', '#eee');
        $overlayDiv.css('opacity', '0.5');
        $overlayDiv.css('cursor', 'not-allowed');
        $overlayDiv.css('display', 'none');
        $(editor.getContainer()).append($overlayDiv);
    }


    /**
     * Фильтрует редактируемый html контент tinymce в соответствии с установленными требованиями
     * @param $elem
     */
    this.filter = function($elem) {

        if (!$elem) {
            $elem = $(editor.getBody());
        }

        if ($elem.prop('tagName').toLowerCase() == "a") {
            var adsghndkjasg = 1;
        }

        $.each($elem.children(), function() {

            var tagNameInLowerCase = $(this).prop('tagName').toLowerCase();

            var approvedElement = false;

            //Определяем, разрешен ли для использования текущий элемент
            for (var i = 0; i < approvedElements.length; ++i) {
                if (approvedElements[i].toLowerCase() == tagNameInLowerCase) {
                    approvedElement = true;
                    break;
                }
            }

            //Неразрешенные элементы без текста сразу же уничтожаются, а с текстом заменяются на текст
            if (!approvedElement) {

                if (!$(this).text()) {
                    $(this).remove();
                } else {
                    $(this).replaceWith($('<span>' + $(this).text() + '</span>'));
                }

                return;
            }

            //Перед проверкой разрешенного элемента, проверяются его дочерние элементы
            self.filter($(this));

            //Не трогаем <img> и <iframe>
            if (tagNameInLowerCase == 'img'.toLowerCase() || tagNameInLowerCase == 'iframe'.toLowerCase()) {
                return;
            }

            //Последний этап - проверка на то, заполнен ли элемент контентом (имеется текст или дети). Если нет, то он удаляется.
            if (!$(this).children().length && !$(this).text().trim().length) {
                $(this).remove();
                return;
            }

        });
    };

};





