package ru.radom.kabinet.web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.BatchVotingState;
import ru.askor.voting.domain.RegisteredVoterStatus;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingVotersPageResultDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;
import ru.radom.kabinet.services.sharer.UserBatchVotingService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.web.communities.dto.BatchVotingVotersGridDto;
import ru.radom.kabinet.web.communities.dto.BatchVotingsGridDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Controller
@RequestMapping("/uservotings")
public class UserVotingController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserBatchVotingService userBatchVotingService;

    @RequestMapping(value = "/batchvotings", method = RequestMethod.GET)
    public String getUserBatchVotingsPage(Model model) {
        return "userbatchvotings";
    }

    @RequestMapping(value = "/testtest", method = RequestMethod.GET)
    @ResponseBody
    public String getBatchVotingsPageData() {
        return "qweqweqwe";
    }


    @RequestMapping(value = "/batch_votings_page_grid_data.json", method = {RequestMethod.POST, RequestMethod.GET}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingsGridDto getBatchVotingsPageData(
            @RequestParam(value = "startDateStart", required = false) String startDateStartStr,
            @RequestParam(value = "startDateEnd", required = false) String startDateEndStr,
            @RequestParam(value = "endDateStart", required = false) String endDateStartStr,
            @RequestParam(value = "endDateEnd", required = false) String endDateEndStr,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "state", required = false) BatchVotingState state,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        BatchVotingsGridDto result;
        try {
            Date startDateStart = DateUtils.parseDate(startDateStartStr, null);
            Date startDateEnd = DateUtils.parseDate(startDateEndStr, null);
            Date endDateStart = DateUtils.parseDate(endDateStartStr, null);
            Date endDateEnd = DateUtils.parseDate(endDateEndStr, null);

            page = page - 1;
            Long voterId = SecurityUtils.getUser().getId();

            BatchVotingsPageResultDto batchVotingsPageResultDto = userBatchVotingService.filterBatchVotings(
                    ownerId,
                    voterId,
                    startDateStart, startDateEnd, endDateStart, endDateEnd, null, state, subject, page,
                    SecurityUtils.getUser().getId()
            );
            List<Long> userIds = new ArrayList<>();
            if (batchVotingsPageResultDto.getBatchVotings() != null) {
                userIds.addAll(batchVotingsPageResultDto.getBatchVotings().stream().map(BatchVoting::getOwnerId).collect(Collectors.toList()));
            }
            List<User> users = null;
            if (!userIds.isEmpty()) {
                users = userDataService.getByIds(userIds);
            }
            result = BatchVotingsGridDto.successDtoFromDomain(batchVotingsPageResultDto, users);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingsGridDto.failDto();
        }
        return result;
    }

    @RequestMapping(value = "/batch_voting_voters_grid_data.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public BatchVotingVotersGridDto getBatchVotingsPageData(
            @RequestParam(value = "batchVotingId", required = true) Long batchVotingId,
            @RequestParam(value = "votersStatus", required = false) RegisteredVoterStatus registeredVoterStatus,
            @RequestParam(value = "name", required = false) String voterName,
            @RequestParam(value = "page", defaultValue = "1")int page) {
        BatchVotingVotersGridDto result;
        try {
            page = page - 1;
            BatchVotingVotersPageResultDto batchVotingVotersPageResultDto = userBatchVotingService.filterBatchVotingVoters(batchVotingId, registeredVoterStatus, voterName, page);
            result = BatchVotingVotersGridDto.successDto(batchVotingVotersPageResultDto);
        } catch (Exception e) {
            e.printStackTrace();
            result = BatchVotingVotersGridDto.failDto();
        }
        return result;
    }
}
