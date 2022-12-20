package ru.klokov.employee_manager.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.klokov.employee_manager.entity.Position;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PositionRepositoryTest {
    @Autowired
    private PositionRepository positionRepository;

    @Test
    public void PositionRepository_FindTop3ByOrderByIdDesc_ReturnTop3PositionsOrderedByIdDesc() {
        Position position1 = Position.builder().name("position1").build();
        Position position2 = Position.builder().name("position2").build();
        Position position3 = Position.builder().name("position3").build();
        Position position4 = Position.builder().name("position4").build();

        positionRepository.save(position1);
        positionRepository.save(position2);
        positionRepository.save(position3);
        positionRepository.save(position4);

        List<Position> expectedPositions = positionRepository.findTop3ByOrderByIdDesc();

        Assertions.assertThat(expectedPositions.size()).isEqualTo(3);
        Assertions.assertThat(expectedPositions.get(0).getName()).isEqualTo("position4");
        Assertions.assertThat(expectedPositions.get(1).getName()).isEqualTo("position3");
        Assertions.assertThat(expectedPositions.get(2).getName()).isEqualTo("position2");
    }
}
