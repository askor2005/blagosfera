package ru.askor.blagosfera.data.jpa.repositories.field;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.fields.FieldsGroupEntity;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface FieldsGroupRepository extends JpaRepository<FieldsGroupEntity, Long>, JpaSpecificationExecutor<FieldsGroupEntity> {

    List<FieldsGroupEntity> findAllByObjectType(String objectType);
}
