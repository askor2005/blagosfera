package ru.radom.kabinet.web;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.askor.voting.domain.Voting;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.askor.voting.mvc.dto.ResponseDto;
import ru.askor.voting.mvc.dto.VotesDto;
import ru.askor.voting.mvc.dto.VotingItemDto;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.utils.Roles;

import javax.annotation.Resource;

@RestController("votingController")
public class VotingController extends ru.askor.voting.mvc.controller.VotingControllerImpl {
    private static final Logger logger = LoggerFactory.createLogger(VotingController.class);
    @Resource(name = "votingController")
    private VotingController self;

    public VotingController() {
    }

    @Override
    protected Long getUserId() throws Exception {
        UserDetailsImpl currentUser = SecurityUtils.getUserDetails();
        if (currentUser == null) throw new Exception("not authorised");
        return currentUser.getUser().getId();
    }

    @Override
    protected boolean isAdmin() throws Exception {
        UserDetailsImpl currentUser = SecurityUtils.getUserDetails();
        if (currentUser == null) throw new Exception("not authorised");
        return currentUser.hasRole(Roles.ROLE_ADMIN);
    }

    @Override
    @RequestMapping(value = "addVotes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto addVotes(@RequestBody VotesDto votesDto) throws Exception {
        Long votingItemId = votesDto.votes.get(0).votingItemId;
        Voting voting = votingService.getVotingByItemId(votingItemId, false, false);

        if (useBiometricIdentification(voting)) {
            return self.addVotesF(votesDto);
        } else {
            return super.addVotes(votesDto);
        }
    }

    @TokenProtected
    @RequestMapping(value = "addVotesF", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto addVotesF(@RequestBody VotesDto votesDto) throws Exception {
        return super.addVotes(votesDto);
    }

    @Override
    @RequestMapping(value = "addVotingItem", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto addVotingItem(@RequestBody VotingItemDto votingItemDto) throws Exception {
        Voting voting = votingService.getVoting(votingItemDto.votingId, false, false);

        if (useBiometricIdentification(voting)) {
            return self.addVotingItemF(votingItemDto);
        } else {
            return super.addVotingItem(votingItemDto);
        }
    }

    @TokenProtected
    @RequestMapping(value = "addVotingItemF", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto addVotingItemF(@RequestBody VotingItemDto votingItemDto) throws Exception {
        return super.addVotingItem(votingItemDto);
    }
    @RequestMapping(value = "deleteVotingItem", method = RequestMethod.POST)
    public ResponseDto deleteVotingItem(@RequestBody DeleteVotingItemDto deleteVotingItemDto) throws Exception {
            Voting voting = votingService.getVoting(deleteVotingItemDto.getVotingId(), false, false);
            if (useBiometricIdentification(voting)) {
                return self.deleteVotingItemF(deleteVotingItemDto);
            } else {
                return super.deleteVotingItem(deleteVotingItemDto.getVotingItemId());
            }

    }
    @TokenProtected
    @RequestMapping(value = {"deleteVotingItemF"}, method = {RequestMethod.POST})
    public ResponseDto deleteVotingItemF(@RequestBody DeleteVotingItemDto deleteVotingItemDto) throws Exception {
        return super.deleteVotingItem(deleteVotingItemDto.getVotingItemId());
    }
    private boolean useBiometricIdentification(Voting voting) throws VotingSystemException {
        return voting.getAdditionalData().containsKey(VotingAttributeTemplate.USE_BIOMETRIC_IDENTIFICATION);
    }
}
