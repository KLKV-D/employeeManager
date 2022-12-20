package ru.klokov.employee_manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceAlreadyExistsException;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.PositionRepository;
import ru.klokov.employee_manager.service.PositionService;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;

    @Override
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    @Override
    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id=" + id));
    }

    @Override
    public Position addPosition(Position position) {
        if (positionAlreadyExists(position)) throw new ResourceAlreadyExistsException(
                "Position with name " + position.getName() + " already exists!");

        return positionRepository.save(position);
    }

    @Override
    public Position updatePosition(Position position, Long id) {
        if (positionAlreadyExists(position)) throw new ResourceAlreadyExistsException(
                "Position with name " + position.getName() + " already exists!");

        Position positionToUpdate = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id=" + id));

        positionToUpdate.setName(position.getName());
        positionToUpdate.setEmployees(position.getEmployees());
        return positionRepository.save(positionToUpdate);
    }

    @Override
    public void deletePosition(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id=" + id));
        positionRepository.deleteById(id);
    }

    @Override
    public Long getNumberOfPositions() {
        return positionRepository.count();
    }

    @Override
    public List<Position> get3LastPositions() {
        return positionRepository.findTop3ByOrderByIdDesc();
    }

    @Override
    public Page<Position> getPositionsPaginated(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        return positionRepository.findAll(pageable);
    }

    @Override
    public Boolean positionAlreadyExists(Position position) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("name", ignoreCase());
        Example<Position> example = Example.of(position, matcher);
        return positionRepository.exists(example);
    }
}