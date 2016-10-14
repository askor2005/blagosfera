package ru.radom.kabinet.services.batchVoting.dto;

import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import ru.radom.kabinet.model.votingtemplate.VoterAllowedTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 30.05.2016.
 */
public class VoterAllowedTemplateDto {

    public Long id;

    public Long voterId;

    public boolean isSignProtocol;

    public static VoterAllowedTemplateDto toDto(VoterAllowedTemplate voterAllowed) {
        VoterAllowedTemplateDto result = new VoterAllowedTemplateDto();
        result.id = voterAllowed.getId();
        result.voterId = voterAllowed.getVoterId();
        result.isSignProtocol = BooleanUtils.toBooleanDefaultIfNull(voterAllowed.getSignProtocol(), false);
        return result;
    }

    public static List<VoterAllowedTemplateDto> toListDto(List<VoterAllowedTemplate> votersAllowed) {
        List<VoterAllowedTemplateDto> result = new ArrayList<>();
        if (votersAllowed != null && !votersAllowed.isEmpty()) {
            for (VoterAllowedTemplate voterAllowed : votersAllowed) {
                result.add(toDto(voterAllowed));
            }
        }
        return result;
    }

    public static VoterAllowedTemplate toDomain(VoterAllowedTemplateDto voterAllowedDto) {
        VoterAllowedTemplate result = new VoterAllowedTemplate();
        result.setId(voterAllowedDto.id);
        result.setVoterId(voterAllowedDto.voterId);
        result.setSignProtocol(voterAllowedDto.isSignProtocol);
        return result;
    }

    public static List<VoterAllowedTemplate> toListDomain(List<VoterAllowedTemplateDto> votersAllowedDto) {
        List<VoterAllowedTemplate> result = null;
        if (votersAllowedDto != null && !votersAllowedDto.isEmpty()) {
            result = new ArrayList<>();
            for (VoterAllowedTemplateDto voterAllowedDto : votersAllowedDto) {
                result.add(toDomain(voterAllowedDto));
            }
        }
        return result;
    }

}
