package com.intelligent_personal_trainer.data_persistence_service.repository;

import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FitnessDataJpaRepositoryTest {

    @Autowired
    private FitnessDataJpaRepository repository;

    @Test
    void testSaveAndFind() {
        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setUserId("user1");
        entity.setTimestamp(Instant.now());
        entity.setAverageHeartRate(120.0);

        WorkoutData workoutData = new WorkoutData();
        workoutData.setWorkoutType("running");
        workoutData.setAttributes(Map.of("duration", "30m"));
        entity.setWorkoutDataList(List.of(workoutData));

        FitnessDataEntity saved = repository.save(entity);

        FitnessDataEntity found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getUserId()).isEqualTo("user1");
        assertThat(found.getWorkoutDataList()).hasSize(1);
        assertThat(found.getWorkoutDataList().get(0).getWorkoutType()).isEqualTo("running");
        assertThat(found.getWorkoutDataList().get(0).getAttributes()).containsEntry("duration", "30m");
    }

    @Test
    void testFindByUserIdOrderByTimestampDesc() {
        FitnessDataEntity e1 = new FitnessDataEntity();
        e1.setUserId("user1");
        e1.setTimestamp(Instant.parse("2023-01-01T10:00:00Z"));
        repository.save(e1);

        FitnessDataEntity e2 = new FitnessDataEntity();
        e2.setUserId("user1");
        e2.setTimestamp(Instant.parse("2023-01-02T10:00:00Z"));
        repository.save(e2);

        List<FitnessDataEntity> results = repository.findByUserIdOrderByTimestampDesc("user1");
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTimestamp()).isEqualTo(e2.getTimestamp());
        assertThat(results.get(1).getTimestamp()).isEqualTo(e1.getTimestamp());
    }

    @Test
    void testFindByUserIdAndTimestampBetweenOrderByTimestampDesc() {
        FitnessDataEntity e1 = new FitnessDataEntity();
        e1.setUserId("user1");
        e1.setTimestamp(Instant.parse("2023-01-01T10:00:00Z"));
        repository.save(e1);

        FitnessDataEntity e2 = new FitnessDataEntity();
        e2.setUserId("user1");
        e2.setTimestamp(Instant.parse("2023-01-02T10:00:00Z"));
        repository.save(e2);

        FitnessDataEntity e3 = new FitnessDataEntity();
        e3.setUserId("user1");
        e3.setTimestamp(Instant.parse("2023-01-03T10:00:00Z"));
        repository.save(e3);

        List<FitnessDataEntity> results = repository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                "user1",
                Instant.parse("2023-01-01T12:00:00Z"),
                Instant.parse("2023-01-03T12:00:00Z")
        );

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTimestamp()).isEqualTo(e3.getTimestamp());
        assertThat(results.get(1).getTimestamp()).isEqualTo(e2.getTimestamp());
    }
}
