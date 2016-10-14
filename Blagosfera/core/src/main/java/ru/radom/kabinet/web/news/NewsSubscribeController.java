package ru.radom.kabinet.web.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dto.news.subscribe.AuthorDto;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.news.NewsSubscribeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер, обрабатывающий запросы, связанные с подписками на новости
 */
@Controller
@RequestMapping(value = "/newsSubscribe/")
public class NewsSubscribeController {

    @Autowired
    private NewsSubscribeService newsSubscribeService;

    @Autowired
    private CommunityRepository communityRepository;

    @RequestMapping(value = "/sharers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AuthorDto> authorsList() {
        List<AuthorDto> result = new ArrayList<>();
        List<User> authors = newsSubscribeService.getAuthorsBySharer(SecurityUtils.getUser().getId());

        for (User user : authors) {
            AuthorDto authorDto = new AuthorDto();

            authorDto.setId(user.getId());
            authorDto.setShortName(user.getShortName());
            authorDto.setAvatarSrc(user.getAvatarSrc());

            result.add(authorDto);
        }

        return result;
    }

    @RequestMapping(value = "/communityMembers", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuthorDto> communityAuthorsList(@RequestParam(value = "communityId", required = true) Long communityId) {

        List<AuthorDto> result = new ArrayList<>();

        CommunityEntity community = communityRepository.findOne(communityId);

        for (CommunityMemberEntity communityMember : community.getMembers()) {

            UserEntity userEntity = communityMember.getUser();

            AuthorDto authorDto = new AuthorDto();

            authorDto.setId(userEntity.getId());
            authorDto.setShortName(userEntity.getShortName());
            authorDto.setAvatarSrc(userEntity.getAvatarSrc());

            result.add(authorDto);
        }

        return result;
    }
}
