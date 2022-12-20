package ru.klokov.employee_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.klokov.employee_manager.entity.Position;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findTop3ByOrderByIdDesc();
}
