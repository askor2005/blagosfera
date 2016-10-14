package ru.radom.kabinet.web.news;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.common.Tag;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.news.NewsItem;
import ru.askor.blagosfera.domain.news.filter.NewsFilterData;
import ru.askor.blagosfera.domain.news.filter.NewsFilterDataBuilder;
import ru.askor.blagosfera.domain.systemaccount.SystemAccount;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.news.NewsDao;
import ru.radom.kabinet.dao.rating.RatingDao;
import ru.radom.kabinet.dto.SuccessResponseDto;
import ru.radom.kabinet.dto.news.NewsDto;
import ru.radom.kabinet.dto.news.NewsFilterDataDto;
import ru.radom.kabinet.dto.news.NewsListItemCategoryDto;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.discussion.Discussion;
import ru.radom.kabinet.model.news.News;
import ru.radom.kabinet.model.notifications.SystemAccountEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.NewsException;
import ru.radom.kabinet.services.common.TagService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.discuss.DiscussionService;
import ru.radom.kabinet.services.news.NewsService;
import ru.radom.kabinet.services.rating.RatingService;
import ru.radom.kabinet.services.systemAccount.SystemAccountService;
import ru.radom.kabinet.utils.DiscussionUtils;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller()
public class NewsController {
    private static final Logger logger = LoggerFactory.createLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private TagService tagService;

    @Autowired
    private SystemAccountService systemAccountService;

    @Autowired
    private CommunityDataService communityDataService;

    @RequestMapping(value = "/news/create/sharer.json", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public NewsDto createBySharer(@RequestBody NewsDto newsDto) {
        NewsItem news = newsDto.toDomain(sharerDao.getById(SecurityUtils.getUser().getId()), sharerDao.getById(SecurityUtils.getUser().getId()));
        news = newsService.createNews(news);
        return new NewsDto(news);
    }

    @RequestMapping(value = "/news/create/community.json", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public NewsDto createBySharerAndCommunity(@RequestBody NewsDto newsDto) {
        NewsItem news = newsDto.toDomain(sharerDao.getById(SecurityUtils.getUser().getId()), newsDto.scope);
        news = newsService.createNews(news);
        return new NewsDto(news);
    }

    @RequestMapping(value = "/news/delete/{id}.json", method = RequestMethod.POST)
    @ResponseBody
    public String delete(@PathVariable("id") Long id) {
        try {
            News news = newsDao.getById(id);
            news = newsService.deleteNews(news, sharerDao.getById(SecurityUtils.getUser().getId()));
            return serializationManager.serialize(news).toString();
        } catch (NewsException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }


    @RequestMapping(value = "/news/edit.json", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public NewsDto edit(@RequestBody NewsDto newsDto) {
        NewsItem news = newsService.editNews(newsDto.toDomain(), sharerDao.getById(SecurityUtils.getUser().getId()));
        return new NewsDto(news);
    }

    @RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        News news = newsDao.getById(id);

        if (news != null) {

            final Discussion discussion = news.getDiscussion();
            if (discussion == null) {
                news = newsService.attachDiscussionAndSave(news); // у старых
                // новостей
                // нет
                // обсуждений
            }
            DiscussionUtils.attachDiscussion(model, news.getDiscussion(), discussionService, sharerDao);

            model.addAttribute("news", news);

            //Заполняем список категорий (жаль, что приходится делать это здесь...нужно слишном много рефакторить)
            List<NewsListItemCategoryDto> categories = new ArrayList<>();

            NewsDto newsDto = new NewsDto(news.toDomain());

            NewsListItemCategoryDto newsListItemCategoryDto = newsDto.category;

            while (newsListItemCategoryDto != null) {
                categories.add(0, newsListItemCategoryDto);
                newsListItemCategoryDto = newsListItemCategoryDto.parent;
            }

            model.addAttribute("categories", categories);

            ratingService.appendToModel(model, news);

            if (news.getScope() instanceof CommunityEntity) {
                CommunityEntity community = (CommunityEntity) news.getScope();
                model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Новости", "/feed").add("Новости объединения " + community.getName(), community.getLink()).add(news.getTitle(), news.getLink()));
            } else if (news.getScope() instanceof UserEntity) {
                model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("Новости", "/feed").add(news.getTitle(), news.getLink()));
            }

            return "news";
        } else {
            try {
                response.sendError(404);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @RequestMapping(value = "/news/list.json", method = RequestMethod.GET)
    @ResponseBody
    public List<NewsDto> list(@RequestParam(value = "sharer_id") UserEntity userEntity, @RequestParam(value = "last_loaded_id", defaultValue = "-1") News lastLoaded, @RequestParam(value = "per_page", defaultValue = "10") Integer perPage) {
        List<News> list = newsDao.getByScope(userEntity, lastLoaded, perPage);

        List<NewsDto> result = new ArrayList<>();

        for (News news : list) {
            NewsItem newsItem = news.toDomain();
            newsItem.setDiscussion(discussionService.fillLastCommentInfo(news.getDiscussion()));
            newsItem.setRatingSum(ratingDao.sumWeights(news));
            newsItem.setRatingWeight(ratingDao.getWeights(SecurityUtils.getUser().getId(),
                    Collections.singletonList(news.getId()), News.class).get(news.getId()));
            result.add(new NewsDto(newsItem));
        }

        return result;
    }

    @RequestMapping(value = "/news/community.json", method = RequestMethod.GET)
    @ResponseBody
    public List<NewsDto> list(@RequestParam(value = "community_id") Long communityId, @RequestParam(value = "last_loaded_id", defaultValue = "-1") News lastLoaded, @RequestParam(value = "per_page", defaultValue = "10") Integer perPage, @RequestParam(value = "exclude_moderated", defaultValue = "false") boolean excludeModerated) {
        Community community = communityDataService.getByIdFullData(communityId);

        NewsItem lastLoadedDomain = null;

        if (lastLoaded != null) {
            lastLoadedDomain = lastLoaded.toDomain();
        }

        List<NewsItem> newsItems = newsService.getByScope(communityId, lastLoadedDomain, perPage, excludeModerated);

        if ((community.getAssociationForm() != null) &&
                Community.BLAGOSFERA_EDITORS_ASSOCIATION_FORM_CODE.equals(community.getAssociationForm().getCode())) {
            SystemAccount account = systemAccountService.getById(SystemAccountEntity.BLAGOSFERA_ID);

            for (NewsItem item : newsItems) {
                item.setAuthor(account);
            }
        }

        return newsItems.stream().map(NewsDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/news/feed.json", method = RequestMethod.GET)
    @ResponseBody
    public List<NewsDto> feed(@RequestParam(value = "last_loaded_id", defaultValue = "-1") News lastLoaded,
                              @RequestParam(value = "per_page", defaultValue = "10") Integer perPage,
                              UserEntity userEntity) {

        NewsItem lastLoadedDomain = null;

        if (lastLoaded != null) {
            lastLoadedDomain = lastLoaded.toDomain();
        }

        List<NewsItem> domains = newsService.getBySharer(userEntity, lastLoadedDomain, perPage);

        return domains.stream().map(NewsDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/news/set_moderated.json", method = RequestMethod.POST)
    @ResponseBody
    public String setModerated(@RequestParam(value = "news_id", defaultValue = "-1") News news) {
        try {
            news = newsService.setModerated(news, sharerDao.getById(SecurityUtils.getUser().getId()));
            return serializationManager.serialize(news).toString();
        } catch (NewsException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/news/saveFilter.json", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SuccessResponseDto saveFilter(@RequestBody NewsFilterDataDto newsFilterDataDto) {
            NewsFilterDataBuilder builder = new NewsFilterDataBuilder(newsFilterDataDto);
            newsService.saveFilter(builder.createNewsFilterData());
            return SuccessResponseDto.get();

    }

    @RequestMapping(value = "/news/filterData.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public NewsFilterDataDto getFilterData(@RequestParam(value = "communityId", required = false) Long communityId) {
        NewsFilterData newsFilterData = newsService.getNewsFilterData(communityId);
        return newsFilterData.toDto();
    }


    @RequestMapping(value = "/news/tags.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> tagsAutoComplete(@RequestParam(value = "term", required = true) String term) throws UnsupportedEncodingException {
        //Получаем список domain классов
        List<Tag> domains = tagService.getTagsForAutocompleteByTerm(term);

        //И преобразовываем в список строк
        return domains.stream()
                .map(Tag::getText)
                .collect(Collectors.toList());
    }

}
