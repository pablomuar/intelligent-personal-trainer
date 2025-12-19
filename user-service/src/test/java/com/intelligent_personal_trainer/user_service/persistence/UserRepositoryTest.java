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
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.datasource.driver-class-name=org.h2.Driver"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void testSaveAndFind() {
        UserEntity user = UserEntity.builder()
                .userId("user123")
                .username("john.doe")
                .password("password")
                .name("John")
                .surname("Doe")
                .age(30)
                .gender("Male")
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .diseases(List.of("Asthma"))
                .build();

        repository.save(user);

        UserEntity found = repository.findById("user123").orElseThrow();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getUsername()).isEqualTo("john.doe");
        assertThat(found.getSurname()).isEqualTo("Doe");
        assertThat(found.getLifestyle()).isEqualTo(Lifestyle.MODERATELY_ACTIVE);
        assertThat(found.getDiseases()).containsExactly("Asthma");
    }

    @Test
    void testFindByUsername() {
        UserEntity user = UserEntity.builder()
                .userId("user789")
                .username("jane.smith")
                .password("password")
                .name("Jane")
                .surname("Smith")
                .age(28)
                .gender("Female")
                .lifestyle(Lifestyle.LIGHTLY_ACTIVE)
                .build();

        repository.save(user);

        UserEntity found = repository.findByUsername("jane.smith").orElseThrow();
        assertThat(found.getUsername()).isEqualTo("jane.smith");
        assertThat(found.getName()).isEqualTo("Jane");
    }

    @Test
    void testUpdateUser() {
        UserEntity user = UserEntity.builder()
                .userId("user456")
                .username("jane.doe")
                .password("password")
                .name("Jane")
                .surname("Doe")
                .age(25)
                .gender("Female")
                .lifestyle(Lifestyle.SEDENTARY)
                .build();

        repository.save(user);

        user.setLifestyle(Lifestyle.VERY_ACTIVE);
        repository.save(user);

        UserEntity updated = repository.findById("user456").orElseThrow();
        assertThat(updated.getLifestyle()).isEqualTo(Lifestyle.VERY_ACTIVE);
    }
}
