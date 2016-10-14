package ru.radom.kabinet.services.news.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.data.jpa.repositories.rameraListEditor.RameraListEditorItemRepository;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.radom.kabinet.enums.system.SystemSetting;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.NewsException;
import ru.radom.kabinet.services.news.NewsValidationService;
import ru.radom.kabinet.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service("newsValidationService")
public class NewsValidationServiceImpl implements NewsValidationService {

    private final List<String> approvedElements = Arrays.asList("p", "span", "strong", "em", "li", "ul", "ol", "i", "b");

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private RameraListEditorItemRepository rameraListEditorItemRepository;


    @Override
    public void validateNewsIsNotEmpty(NewsItem newsItem) {

        //Валидация заголовка
        String title = newsItem.getTitle();

        if (title == null || StringUtils.isEmpty(title.trim())) {
            throw new NewsException("Не задан заголовок новости!");
        }

        //Валидация категории
        if (newsItem.getCategory() == null || newsItem.getCategory().getId() == null) {
            throw new RuntimeException("Не задана категория новости!");
        }

        if (!rameraListEditorItemRepository.exists(newsItem.getCategory().getId())) {
            throw new RuntimeException("Задана несуществующая категория!");
        }

        //Валидация текста
        String text = newsItem.getText();
        if (text == null || StringUtils.isEmpty(text.trim()) || StringUtils.isEmpty(StringUtils.HtmlToPlain(text).trim())) {
            throw new RuntimeException("Не задан текст новости!");
        }
    }

    @Override
    public NewsItem filterNews(NewsItem newsItem) {
        String text = newsItem.getText();
        final Document document = Jsoup.parse(text);

        for (Element element : document.body().children()) {
            filterElement(element);
        }

        newsItem.setText(document.body().html());
        return newsItem;
    }

    @Override
    public void validateAttachmentsCount(NewsItem newsItem) {

        final int maxCountOfAttachments = Integer.valueOf(settingsManager.getSystemSetting("news.max-count-of-attachments", "10"));

        if (newsItem.getAttachments().size() > maxCountOfAttachments) {
            throw new RuntimeException("Одна новость не может содержать более " + maxCountOfAttachments  +  " картинок и видео.");
        }
    }

    @Override
    public void validateTagsCount(NewsItem newsItem) {
        final int maxCountOfTags = Integer.valueOf(settingsManager.getSystemSetting(SystemSetting.NEWS_TAGS_MAX_COUNT.getKey(), "10"));

        if (newsItem.getTags().size() > maxCountOfTags) {
            throw new RuntimeException("Одна новость не может содержать более " + maxCountOfTags  +  " тегов.");
        }
    }

    /**
     * Позволяет провести рекурсивую Фильтрацию html элемента в соответствии с требованиями системы.
     * @param element елемент для фильтрации
     */
    private void filterElement(Element element) {

        String tagNameInLowerCase = element.tagName().toLowerCase();

        //Определяем, разрешен ли в системе данный элемент
        boolean approvedElement = false;

        for (String tagName : approvedElements) {

            if (tagNameInLowerCase.equals(tagName.toLowerCase())) {
                approvedElement = true;
                break;
            }

        }

        //Неразрешенные элементы удаляем сразу.
        if (!approvedElement) {
            element.remove();
            return;
        }

        //Фильтрация детей данного элемента
        for (Element childElement : element.children()) {
            filterElement(childElement);
        }

        //Если html внутри элемента состоит только из пробельных символов - удаляем его.
        String html = element.html();
        html = html.replaceAll("&nbsp;", "");

        if (html.trim().isEmpty()) {
            element.remove();
            return;
        }

        //Последний этап - проверка на то, заполнен ли элемент контентом (имеется текст или дети). Если нет, то он удаляется.
        if (element.children().isEmpty() && element.html().trim().isEmpty()) {
            element.remove();
            return;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public void validateDataConsistency(NewsItem newsItem) {
        RameraListEditorItem rameraListEditorItem = rameraListEditorItemRepository.findOne(newsItem.getCategory().getId());

        if (!getRootParent(rameraListEditorItem).getListEditor().getName().equals("news_categories")) {
            throw new RuntimeException("Указанная категория не относится к новостям");
        }
    }

    /**
     * Позволяет получить корневого предка для указанного узла
     * @param child узел, чей корневой узел следует найти
     * @return один из корневых объектов RameraListEditorItem в своей иерархии
     */
    private RameraListEditorItem getRootParent(RameraListEditorItem child) {

        if (child.getParent() != null) {
            return getRootParent(child.getParent());
        } else {
            return child;
        }

    }

}
