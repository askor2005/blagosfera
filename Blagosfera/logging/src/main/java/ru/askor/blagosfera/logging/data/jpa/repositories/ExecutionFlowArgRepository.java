package ru.askor.blagosfera.logging.data.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.askor.blagosfera.logging.data.jpa.entities.ExecutionFlowArgEntity;

public interface ExecutionFlowArgRepository extends JpaRepository<ExecutionFlowArgEntity, Long> {
}
