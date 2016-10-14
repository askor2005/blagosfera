package ru.askor.blagosfera.data.jpa.repositories.field;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.fields.FieldEntity;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 04.03.2016.
 */
public interface FieldRepository extends JpaRepository<FieldEntity, Long>, JpaSpecificationExecutor<FieldEntity> {

    List<FieldEntity> findAllByFieldsGroup_ObjectType(String objectType);

    FieldEntity findOneByInternalName(String internalName);
}
