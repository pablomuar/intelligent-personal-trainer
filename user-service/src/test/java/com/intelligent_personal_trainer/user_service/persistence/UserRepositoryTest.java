package com.intelligent_personal_trainer.user_service.persistence;

import com.intelligent_personal_trainer.user_common.Lifestyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void testSaveAndFind() {
        UserEntity user = UserEntity.builder()
                .userId("user123")
                .name("John")
                .surname("Doe")
                .age(30)
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Asthma"))
                .build();

        repository.save(user);

        UserEntity found = repository.findById("user123").orElseThrow();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getSurname()).isEqualTo("Doe");
        assertThat(found.getLifestyle()).isEqualTo(Lifestyle.MODERATELY_ACTIVE);
        assertThat(found.getDiseases()).containsExactly("Asthma");
    }

    @Test
    void testUpdateUser() {
        UserEntity user = UserEntity.builder()
                .userId("user456")
                .name("Jane")
                .surname("Doe")
                .age(25)
                .lifestyle(Lifestyle.SEDENTARY)
                .build();

        repository.save(user);

        user.setLifestyle(Lifestyle.VERY_ACTIVE);
        repository.save(user);

        UserEntity updated = repository.findById("user456").orElseThrow();
        assertThat(updated.getLifestyle()).isEqualTo(Lifestyle.VERY_ACTIVE);
    }
}
