package ru.radom.kabinet.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.util.BatchVotingBehavior;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.model.votingtemplate.VotingAttributeTemplate;
import ru.radom.kabinet.utils.VarUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 13.10.2015.
 */
@Transactional
@Component
public class StandardBatchVotingBehavior implements BatchVotingBehavior {

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private CommonVotingService commonVotingService;

    @Autowired
    private BatchVotingEventListener batchVotingEventListener;

    @PostConstruct
    public void init() {
        batchVotingService.registerBatchVotingBehavior(BatchVotingConstants.STANDARD_BEHAVIOR_NAME, this);
    }

    @Override
    public boolean shouldFinishBatchVoting(Voting voting) {
        boolean valid = voting.getResult().getResultType() == VotingResultType.VALID;
        boolean deadHeat = voting.getResult().getResultType() == VotingResultType.INVALID_DEAD_HEAT;
        boolean finishedManually = voting.getAdditionalData().get(AdditionalDataConstants.FINISHED_MANUALLY_KEY) != null;
        boolean finishCauseOnlyAbstain = ((voting.getParameters().getVotingType().equals(VotingType.INTERVIEW)) && (!nonAbstainVariantExists(voting)));

        return /*(valid && isContra) || */(!valid && (!deadHeat || finishedManually)) || finishCauseOnlyAbstain;
    }

    @Override
    public boolean shouldFinishBatchVotingIfCanNotRestartVoting(Voting voting) {
        boolean stopBatchVotingOnFailResult = VarUtils.getBool(voting.getAdditionalData().get(BatchVotingConstants.STOP_BATCH_VOTING_ON_FAIL_RESULT), true);
        return stopBatchVotingOnFailResult;
    }
    @Override
    public boolean shouldUpdateVoting(Voting voting) throws VotingSystemException {
        if ((voting.getParameters().getVotingType().equals(VotingType.INTERVIEW)) && (!nonAbstainVariantExists(voting))){
            voting.getResult().setResultType(VotingResultType.INVALID_WRONG_RESULT);
            return true;
        }
        return false;
    }

    @Override
    public boolean notifyAboutFinishedVotings(Voting voting) {
        return voting.getResult().getResultType() == VotingResultType.VALID;
    }

    @Override
    public boolean shouldUpdateBatchVoting(BatchVoting batchVoting, Voting voting) throws VotingSystemException {
        boolean batchVotingUpdated = false;

        boolean needHandlePresidentVoting = batchVotingEventListener.isNeedHandlePresidentVoting(batchVoting);
        for (Voting v : batchVoting.getVotings()) {

            String selectFromType = v.getAdditionalData().get(BatchVotingConstants.SELECT_FROM_TYPE);
            Long sourceVotingIndex = VarUtils.getLong(v.getAdditionalData().get(BatchVotingConstants.SOURCE_VOTING_INDEX), null);
            boolean needUpdateVotingItems = voting.getIndex().equals(sourceVotingIndex);

            if (needUpdateVotingItems) {
                switch (selectFromType) {
                    case "genFromInterview":
                        extractVariantsToVotingFromInterview(voting,v);
                        break;
                    case "genFromWinnersOtherVoting":
                        extractVariantsToVotingFromWinnersItems(voting,v);
                        break;
                    case "genFromLosersOtherVoting":
                        extractVariantsToVotingFromLosersItems(voting,v);
                        break;
                }
                batchVotingUpdated = true;
            }

            /*
            if (v.getAdditionalData().containsKey(CommonVotingService.SOURCE_VOTING_INDEX)) {
                try {
                    Long sourceIndex = Long.parseLong(v.getAdditionalData().get(CommonVotingService.SOURCE_VOTING_INDEX));
                    if ((voting.getIndex().equals(sourceIndex))) {
                        extractVariantsToVotingFromInterview(voting,v);
                        batchVotingUpdated = true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new VotingSystemException(e.getMessage());
                }
            }*/
        }

        if (voting.getIndex() == BatchVotingConstants.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX && needHandlePresidentVoting) {
            try {
                removeCandidate(batchVoting.getVotings().get(BatchVotingConstants.VOTING_FOR_SECRETARY_OF_MEETING_INDEX),
                        batchVoting.getVotings().get(BatchVotingConstants.VOTING_FOR_PRESIDENT_OF_MEETING_INDEX).getVotingItems().get(0).getValue());

                batchVotingUpdated = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new VotingSystemException("candidate.id.incorrect");
            } catch (Exception e) {
                e.printStackTrace();
                throw new VotingSystemException(e.getMessage());
            }
        }
        return batchVotingUpdated;
    }

    private void extractVariantsToVotingFromInterview(Voting source, Voting destination) throws VotingSystemException{
        for (VotingItem votingItem : source.getVotingItems()) {
            if (!votingItem.getValue().equals(VotingType.ITEM_ABSTAIN)){
                destination.getVotingItems().add(new VotingItem(null, votingItem.getCreated(), votingItem.getOwnerId(), votingItem.getValue()));
            }
        }
    }

    private void extractVariantsToVotingFromWinnersItems(Voting source, Voting destination) throws VotingSystemException{
        List<VotingItem> winners = getWinners(source);
        for (VotingItem votingItem : winners) {
            destination.getVotingItems().add(new VotingItem(null, votingItem.getCreated(), votingItem.getOwnerId(), votingItem.getValue()));
        }
    }

    private void extractVariantsToVotingFromLosersItems(Voting source, Voting destination) throws VotingSystemException{
        List<VotingItem> losers = getLosers(source);
        for (VotingItem votingItem : losers) {
            destination.getVotingItems().add(new VotingItem(null, votingItem.getCreated(), votingItem.getOwnerId(), votingItem.getValue()));
        }
    }

    private List<VotingItem> getWinners(Voting voting) {
        return commonVotingService.getWinners(voting);
    }

    private List<VotingItem> getLosers(Voting voting) {
        List<VotingItem> result = new ArrayList<>();
        long percentForWin = VarUtils.getLong(voting.getAdditionalData().get(BatchVotingConstants.PERCENT_FOR_WIN), 51L);
        for (VotingItem votingItem : voting.getVotingItems()) {
            if (votingItem.getVotesPercent() < percentForWin && !votingItem.getValue().equals(VotingType.ITEM_ABSTAIN)) {
                result.add(votingItem);
            }
        }
        return result;
    }

    private void removeCandidate(Voting voting, String candidateId) {
        voting.getParameters().getCandidatesAllowed().remove(Long.valueOf(candidateId));
        int count = voting.getVotingItems().size();

        for (int i = count - 1; i > -1; i--) {
            if (voting.getVotingItems().get(i).getValue().equals(candidateId)) {
                voting.getVotingItems().remove(i);
            }
        }
    }

    @Override
    public boolean shouldRestartVoting(Voting voting) {
        if (voting.getAdditionalData().containsKey(VotingAttributeTemplate.NOT_RESTARTABLE)) return false;

        boolean deadHeat = voting.getResult().getResultType() == VotingResultType.INVALID_DEAD_HEAT;
        boolean finishedManually = voting.getAdditionalData().get(AdditionalDataConstants.FINISHED_MANUALLY_KEY) != null;

        int percentForWin = VarUtils.getInt(voting.getAdditionalData().get(BatchVotingConstants.PERCENT_FOR_WIN), 51);

        boolean restartByNotScoredPercent = true;
        if (VotingType.INTERVIEW.equals(voting.getParameters().getVotingType())) {
            restartByNotScoredPercent = false;
        } else {
            for (VotingItem votingItem : voting.getVotingItems()) {
                if (VotingType.PRO_CONTRA.equals(voting.getParameters().getVotingType()) && VotingType.ITEM_CONTRA.equals(votingItem.getValue())) {
                    //
                } else if (
                        VotingType.PRO_CONTRA.equals(voting.getParameters().getVotingType()) &&
                                VotingType.ITEM_PRO.equals(votingItem.getValue()) &&
                                votingItem.getVotesPercent().intValue() >= percentForWin) {
                    restartByNotScoredPercent = false;
                } else {
                    if (!VotingType.ITEM_ABSTAIN.equals(votingItem.getValue()) && votingItem.getVotesPercent().intValue() >= percentForWin) {
                        restartByNotScoredPercent = false;
                    }
                }
            }
        }

        boolean restartByNotAllowedWinnersCount = false;
        if ((VotingType.CANDIDATE.equals(voting.getParameters().getVotingType()) ||
                VotingType.MULTIPLE_SELECTION.equals(voting.getParameters().getVotingType())) &&
                voting.getParameters().isMultipleWinners() &&
                (
                        voting.getAdditionalData().containsKey(BatchVotingConstants.VOTING_MIN_WINNERS_COUNT_ATTR_NAME) ||
                        voting.getAdditionalData().containsKey(BatchVotingConstants.VOTING_MAX_WINNERS_COUNT_ATTR_NAME)
                )) {
            int minWinnersCount = VarUtils.getInt(voting.getAdditionalData().get(BatchVotingConstants.VOTING_MIN_WINNERS_COUNT_ATTR_NAME), -1);
            int maxWinnersCount = VarUtils.getInt(voting.getAdditionalData().get(BatchVotingConstants.VOTING_MAX_WINNERS_COUNT_ATTR_NAME), -1);

            int countWinners = getWinners(voting).size();
            if ((minWinnersCount != -1 && countWinners < minWinnersCount) ||
                    (maxWinnersCount != -1 && countWinners > maxWinnersCount)) {
                restartByNotAllowedWinnersCount = true;
            }
        }

        return restartByNotAllowedWinnersCount || restartByNotScoredPercent || deadHeat && !finishedManually;
    }

    @Override
    public VotingState restartInState(Voting voting) {
        return VotingState.PAUSED;
    }
    private boolean nonAbstainVariantExists(Voting voting) {
        for (VotingItem votingItem : voting.getVotingItems()) {
            if (!votingItem.getValue().equals(VotingType.ITEM_ABSTAIN)){
                return true;
            }
        }
        return false;
    }

}
