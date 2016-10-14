package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.data.jpa.entities.community.CommunityVisitLogEntity;

/**
 * Created by vtarasenko on 14.07.2016.
 */
public interface CommunityVisitLogRepository extends JpaRepository<CommunityVisitLogEntity,Long> {
}
