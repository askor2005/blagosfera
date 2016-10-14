package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import org.apache.commons.collections.map.HashedMap;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.mvc.dto.BatchVotingDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsByTemplateGridDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class BatchVotingsGridDto {

    private boolean success = true;

    private long total = 0;

    private List<BatchVotingItemDto> items = new ArrayList<>();

    public BatchVotingsGridDto(boolean success, long total, List<BatchVotingItemDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    private static Map<Long, User> convertUsers(List<User> users) {
        Map<Long, User> userMap = new HashMap<>();
        if (users != null && !users.isEmpty()) {
            for (User user : users) {
                userMap.put(user.getId(), user);
            }
        }
        return userMap;
    }

    public static BatchVotingsGridDto successDtoFromDomain(BatchVotingsPageResultDto batchVotingsPageResultDto, List<User> users) {
        Map<Long, User> userMap = convertUsers(users);
        List<BatchVotingItemDto> batchVotingDtos = new ArrayList<>();
        Map<Long, String> templateLinks = batchVotingsPageResultDto.getTemplateLinks();
        if (batchVotingsPageResultDto != null && batchVotingsPageResultDto.getBatchVotings() != null) {
            List<BatchVoting> batchVotings = batchVotingsPageResultDto.getBatchVotings();
            for (BatchVoting batchVoting : batchVotings) {
                String templateLink = null;
                if (templateLinks != null && templateLinks.containsKey(batchVoting.getId())) {
                    templateLink = templateLinks.get(batchVoting.getId());
                }
                batchVotingDtos.add(new BatchVotingItemDto(batchVoting, userMap.get(batchVoting.getOwnerId()), templateLink));
            }
        }
        return new BatchVotingsGridDto(true, batchVotingsPageResultDto.getCount(), batchVotingDtos);
    }

    public static BatchVotingsGridDto failDto() {
        return new BatchVotingsGridDto(false, 0, null);
    }


    /*private List<BatchVotingItemDto> batchVotings;

    private String baseLink;

    public BatchVotingsGridDto(
            List<BatchVoting> batchVotings,
            Map<Long, User> owners,
            String baseLink) {
        List<BatchVotingItemDto> batchVotingsDto;
        if (batchVotings != null) {
            batchVotingsDto = new ArrayList<>();
            for (BatchVoting batchVoting : batchVotings) {
                batchVotingsDto.add(new BatchVotingItemDto(batchVoting, owners.get(batchVoting.getId())));
            }
            setBatchVotings(batchVotingsDto);
        }
        setBaseLink(baseLink);
    }*/
}
