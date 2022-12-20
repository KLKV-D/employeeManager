package ru.klokov.employee_manager.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.klokov.employee_manager.entity.Position;
import ru.klokov.employee_manager.exception.ResourceNotFoundException;
import ru.klokov.employee_manager.repository.PositionRepository;
import ru.klokov.employee_manager.service.impl.PositionServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {
    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionServiceImpl positionService;

    private Position position;

    @BeforeEach
    public void setup() {
        position = Position.builder().id(1L).name("position").build();
    }

    @Test
    public void PositionService_AddPosition_ReturnAddedPosition() {
        when(positionRepository.save(Mockito.any(Position.class))).thenReturn(position);

        Position expectedPosition = positionService.addPosition(position);

        Assertions.assertThat(expectedPosition).isNotNull();
        Assertions.assertThat(expectedPosition.getName()).isEqualTo("position");
    }

    // TODO throw exception when add existing department

    @Test
    public void PositionService_GetAllPositions_ReturnAllPositions() {
        when(positionRepository.findAll()).thenReturn(List.of(position));

        List<Position> expectedPositions = positionService.getAllPositions();

        Assertions.assertThat(expectedPositions).isNotNull();
        Assertions.assertThat(expectedPositions.size()).isEqualTo(1);
        Assertions.assertThat(expectedPositions.get(0).getName()).isEqualTo("position");
    }

    @Test
    public void PositionService_GetPositionById_ReturnPositionById() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        Position expectedPosition = positionService.getPositionById(position.getId());

        Assertions.assertThat(expectedPosition).isNotNull();
        Assertions.assertThat(expectedPosition.getName()).isEqualTo("position");
    }

    @Test
    public void PositionService_GetPositionById_ThrowsResourceNotFoundException() {
        when(positionRepository.findById(position.getId()))
                .thenThrow(new ResourceNotFoundException("Position not found with id=" + position.getId()));

        Assertions.assertThatThrownBy(() -> positionService.getPositionById(position.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Position not found with id=" + position.getId());
    }

    @Test
    public void PositionService_UpdatePosition_ReturnUpdatedPosition() {
        Position newPosition = Position.builder().id(1L).name("newPosition").build();
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(newPosition);

        Position expectedUpdatedPosition = positionService.updatePosition(newPosition, 1L);

        Assertions.assertThat(expectedUpdatedPosition).isNotNull();
        Assertions.assertThat(expectedUpdatedPosition.getName()).isEqualTo(newPosition.getName());
    }

    @Test
    public void PositionService_DeleteById_ReturnNothing() {
        Long id = 1L;

        when(positionRepository.findById(id)).thenReturn(Optional.of(position));

        doNothing().when(positionRepository).deleteById(id);

        assertAll(() -> positionService.deletePosition(id));
    }

    @Test
    public void PositionService_GetNumberOfPositions_ReturnNumberOfPositions() {
        when(positionRepository.count()).thenReturn(1L);

        Long expectedNumberOfPositions = positionService.getNumberOfPositions();

        Assertions.assertThat(expectedNumberOfPositions).isEqualTo(1L);
    }

    @Test
    public void PositionService_GetTop3LastPositions_ReturnTop3LastAddedPositions() {
        Position position2 = Position.builder().id(2L).name("position2").build();
        Position position3 = Position.builder().id(3L).name("position3").build();
        Position position4 = Position.builder().id(4L).name("position4").build();

        when(positionRepository.findTop3ByOrderByIdDesc()).thenReturn(List.of(position4, position3, position2));

        List<Position> expectedPositions = positionService.get3LastPositions();

        Assertions.assertThat(expectedPositions).isNotNull();
        Assertions.assertThat(expectedPositions.size()).isEqualTo(3);
    }

    @Test
    public void PositionService_GetPositionsPaginated_ReturnPositionsPaginated() {
        Page<Position> page = Mockito.mock(Page.class);

        when(positionRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Position> expectedPage = positionService.getPositionsPaginated(1, 5, "id", "asc");

        Assertions.assertThat(expectedPage).isNotNull();
    }

    @Test
    public void PositionService_PositionAlreadyExists_CheckIfPositionWithGivenNameAlreadyExists() {
        when(positionRepository.exists(Mockito.any(Example.class))).thenReturn(true);

        Assertions.assertThat(positionService.positionAlreadyExists(position)).isTrue();
    }
}
