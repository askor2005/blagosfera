package ru.askor.blagosfera.logging.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.logging.data.jpa.entities.ExecutionFlowResultEntity;

public interface ExecutionFlowResultRepository extends JpaRepository<ExecutionFlowResultEntity, Long> {
}
