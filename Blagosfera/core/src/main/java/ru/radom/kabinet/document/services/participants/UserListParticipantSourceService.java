package ru.radom.kabinet.document.services.participants;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.ParticipantsTypes;

/**
 *
 * Created by vgusev on 11.04.2016.
 */
@Service
@Transactional
public class UserListParticipantSourceService extends UserParticipantSourceService implements DocumentParticipantSourceService {

    @Override
    public ParticipantsTypes getType() {
        return ParticipantsTypes.INDIVIDUAL_LIST;
    }

    @Override
    public boolean isListDataSource() {
        return true;
    }

}
