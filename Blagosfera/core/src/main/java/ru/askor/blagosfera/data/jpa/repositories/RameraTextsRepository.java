package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.RameraTextEntity;

import java.util.List;

/**
 * Created by mnikitin on 15.06.2016.
 */
public interface RameraTextsRepository extends JpaRepository<RameraTextEntity, Long> {

    RameraTextEntity findOneByCode(String code);

    @Query( "select t from RameraTextEntity t where t.code in :codes" )
    List<RameraTextEntity> findByCodes(@Param("codes") List<String> codes);
}
