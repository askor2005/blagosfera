package ru.askor.blagosfera.data.jpa.repositories.field;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.radom.kabinet.model.fields.FieldPossibleValueEntity;

import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 10.06.2016.
 */
public interface FieldPossibleValueRepository extends JpaRepository<FieldPossibleValueEntity, Long> {

    List<FieldPossibleValueEntity> findAllByField_Id(Long fieldId);
}
