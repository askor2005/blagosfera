/**
 * Компонент для редактирования тегов
 */

;
function RadomTagEditor($parent, options) {

    var self = this;

    //Контейнер для виджета
    var $container = $('<div class="radom_tags_container"></div>');
    $container.css('display', 'none');

    $container.append($('<label>Теги</label>'));

    var $textarea = $('<textarea></textarea>');
    $container.append($textarea);

    $parent.append($container);


    $textarea.tagEditor({
        autocomplete: {
            'source': options.url,
            minLength: 2
        },
        initialTags: (options.initialTags ? options.initialTags : []),
        forceLowercase: true,
        delimiter: ', ',
        maxLength: 32,
        maxTags: options.maxTags
    });

    $container.css('display', '');


    this.getTags = function() {
        return $textarea.tagEditor('getTags')[0].tags;
    };

    this.addTag = function(text) {
        $textarea.tagEditor('addTag', text);
        $textarea.blur();
    };

    this.clear = function () {
        var tags = $textarea.tagEditor('getTags')[0].tags;
        for (var i = 0; i < tags.length; i++) {
            $textarea.tagEditor('removeTag', tags[i]);
        }
    }

};
