package ru.radom.kabinet.web;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.rating.Rating;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.rating.RatingService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    @Autowired
    RatingService ratingService;

    @Autowired
    protected SerializationManager serializationManager;

    @RequestMapping(value = "/create.json", method = RequestMethod.POST)
    public @ResponseBody RatingResponseDto create(final @RequestParam(value = "contentId") Long contentId,
                                       final @RequestParam(value = "contentType") String type,
                                       final @RequestParam(value = "direction") String direction) {
            //final Map<String, Object> result = new StringObjectHashMap();
            final Rating rating = ratingService.saveRating(contentId, type, SecurityUtils.getUser().getId(), direction);
            final Number count = ratingService.sumWeights(contentId, type);
            // update ratings via stomp (really need???)
            ratingService.updateRating(rating, count);
            //result.put(RatingService.COUNT_KEY, count.intValue());
            //result.put(RatingService.RATING_KEY, rating);
            RatingResponseDto ratingResponseDto = new RatingResponseDto();
            ratingResponseDto.setCount(count.intValue());
            ratingResponseDto.setRating(rating.toDomain());
            return ratingResponseDto;
    }

    @RequestMapping(value = "/list.json", method = RequestMethod.GET)
    public @ResponseBody String list(final @RequestParam(value = "contentIds") List<Long> contentIds,
                                     final @RequestParam(value = "contentType") String type) {
        try {
            return new JSONObject(ratingService.countMultiple(contentIds, type)).toString();
        } catch (Exception e) {
            log.error("load list rating error: ", e.getMessage());
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/page.json", method = RequestMethod.GET)
    public @ResponseBody
    RatingPageResponseDto page(final @RequestParam(value = "contentId") Long contentId,
                               final @RequestParam(value = "contentType") String contentType,
                               final @RequestParam(value = "offset") Integer offset,
                               final @RequestParam(value = "limit") Integer limit,
                               final @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                               final @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate,
                               final @RequestParam(value = "userNamePattern") String userNamePattern,
                               final @RequestParam(value = "weight") Double weight,
                               final @RequestParam(value = "onlyActive") Boolean onlyActive) throws UnsupportedEncodingException {
          //  final JSONObject result = new JSONObject();
            final List<Rating> page = ratingService.page(offset, limit, contentId, contentType, (userNamePattern == null) ? null : URLDecoder.decode(userNamePattern, "UTF-8"),
                    DateUtils.getDayBegin(fromDate), DateUtils.getDayEnd(toDate), weight, onlyActive);
            final Integer count = ratingService.count(contentId, contentType, (userNamePattern == null) ? null : URLDecoder.decode(userNamePattern, "UTF-8"),
                    DateUtils.getDayBegin(fromDate), DateUtils.getDayEnd(toDate), weight, onlyActive);
            RatingPageResponseDto ratingResponseDto = new RatingPageResponseDto();
            ratingResponseDto.setTotal(count);
            ratingResponseDto.setRows(page.stream().map(rating -> rating.toDomain()).collect(Collectors.toList()));
            //result.put("total", count);
            //result.put("rows", serializationManager.serializeCollection(page));
            return ratingResponseDto;

    }

    @RequestMapping(value = "/countUsers.json", method = RequestMethod.POST)
    public @ResponseBody
    Map countUsers(final @RequestParam(value = "contentId") Long contentId,
                   final @RequestParam(value = "contentType") String type) {
            RatingPageResponseDto ratingPageResponseDto = new RatingPageResponseDto();
            return ratingService.countUsers(contentId, type);

    }

}
