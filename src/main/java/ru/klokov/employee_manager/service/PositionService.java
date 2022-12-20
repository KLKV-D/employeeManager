package ru.klokov.employee_manager.service;

import org.springframework.data.domain.Page;
import ru.klokov.employee_manager.entity.Position;

import java.util.List;

public interface PositionService {
    List<Position> getAllPositions();
    Position getPositionById(Long id);
    Position addPosition(Position position);
    Position updatePosition(Position position, Long id);
    void deletePosition(Long id);
    Long getNumberOfPositions();
    List<Position> get3LastPositions();
    Page<Position> getPositionsPaginated(int pageNumber, int pageSize, String sortField, String sortDirection);
    Boolean positionAlreadyExists(Position position);
}
