package ru.radom.kabinet.web.communities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка для участника из поля
 * Created by vgusev on 23.03.2016.
 */
@Data
@AllArgsConstructor
public class ParticipantsListDto {

    private Long id;

    private String name;

    public static List<ParticipantsListDto> toListDto(List<User> participants) {
        List<ParticipantsListDto> result = null;
        if (participants != null) {
            result = new ArrayList<>();
            for (User participant : participants) {
                ParticipantsListDto participantDto = new ParticipantsListDto(
                        participant.getId(),
                        participant.getFullName()
                );
                result.add(participantDto);
            }
        }
        return result;
    }

}
