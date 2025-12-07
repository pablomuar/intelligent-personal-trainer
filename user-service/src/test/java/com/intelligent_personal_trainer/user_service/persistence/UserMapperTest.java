package com.intelligent_personal_trainer.user_service.persistence;

import com.intelligent_personal_trainer.user_common.Lifestyle;
import com.intelligent_personal_trainer.user_common.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toEntity_shouldMapAllFields_whenDtoIsValid() {
        User dto = User.builder()
                .userId("123")
                .name("John")
                .surname("Doe")
                .age(30)
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .dataSourceId("source1")
                .externalSourceUserId("ext123")
                .diseases(List.of("flu"))
                .build();

        UserEntity entity = userMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getUserId(), entity.getUserId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getSurname(), entity.getSurname());
        assertEquals(dto.getAge(), entity.getAge());
        assertEquals(dto.getLifestyle(), entity.getLifestyle());
        assertEquals(dto.getDataSourceId(), entity.getDataSourceId());
        assertEquals(dto.getExternalSourceUserId(), entity.getExternalSourceUserId());
        assertEquals(dto.getDiseases(), entity.getDiseases());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void toDto_shouldMapAllFields_whenEntityIsValid() {
        UserEntity entity = UserEntity.builder()
                .userId("123")
                .name("John")
                .surname("Doe")
                .age(30)
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .dataSourceId("source1")
                .externalSourceUserId("ext123")
                .diseases(List.of("flu"))
                .build();

        User dto = userMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getUserId(), dto.getUserId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSurname(), dto.getSurname());
        assertEquals(entity.getAge(), dto.getAge());
        assertEquals(entity.getLifestyle(), dto.getLifestyle());
        assertEquals(entity.getDataSourceId(), dto.getDataSourceId());
        assertEquals(entity.getExternalSourceUserId(), dto.getExternalSourceUserId());
        assertEquals(entity.getDiseases(), dto.getDiseases());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void updateEntityFromDto_shouldUpdateOnlyNonNullFields() {
        UserEntity entity = UserEntity.builder()
                .userId("123")
                .name("OldName")
                .surname("OldSurname")
                .age(30)
                .lifestyle(Lifestyle.SEDENTARY)
                .build();

        User dto = User.builder()
                .name("NewName")
                .age(31)
                // Other fields null
                .build();

        userMapper.updateEntityFromDto(entity, dto);

        assertEquals("NewName", entity.getName());
        assertEquals("OldSurname", entity.getSurname()); // Should match old value
        assertEquals(31, entity.getAge());
        assertEquals(Lifestyle.SEDENTARY, entity.getLifestyle()); // Should match old value
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllFields_whenDtoIsFull() {
        UserEntity entity = new UserEntity();
        User dto = User.builder()
                .name("Name")
                .surname("Surname")
                .age(25)
                .lifestyle(Lifestyle.MODERATELY_ACTIVE)
                .dataSourceId("ds")
                .externalSourceUserId("ext")
                .diseases(List.of("d1"))
                .build();

        userMapper.updateEntityFromDto(entity, dto);

        assertEquals("Name", entity.getName());
        assertEquals("Surname", entity.getSurname());
        assertEquals(25, entity.getAge());
        assertEquals(Lifestyle.MODERATELY_ACTIVE, entity.getLifestyle());
        assertEquals("ds", entity.getDataSourceId());
        assertEquals("ext", entity.getExternalSourceUserId());
        assertEquals(List.of("d1"), entity.getDiseases());
    }
}
