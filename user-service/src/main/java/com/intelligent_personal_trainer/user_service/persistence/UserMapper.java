package com.intelligent_personal_trainer.user_service.persistence;

import com.intelligent_personal_trainer.user_common.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User dto) {
        if (dto == null)
            return null;

        return UserEntity.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .surname(dto.getSurname())
                .age(dto.getAge())
                .lifestyle(dto.getLifestyle())
                .dataSourceId(dto.getDataSourceId())
                .externalSourceUserId(dto.getExternalSourceUserId())
                .diseases(dto.getDiseases())
                .build();
    }

    public User toDto(UserEntity entity) {
        if (entity == null)
            return null;

        return User.builder()
                .userId(entity.getUserId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .age(entity.getAge())
                .lifestyle(entity.getLifestyle())
                .dataSourceId(entity.getDataSourceId())
                .externalSourceUserId(entity.getExternalSourceUserId())
                .diseases(entity.getDiseases())
                .build();
    }

    public void updateEntityFromDto(UserEntity entity, User dto) {
        if (dto.getName() != null)
            entity.setName(dto.getName());

        if (dto.getSurname() != null)
            entity.setSurname(dto.getSurname());

        if (dto.getAge() != null)
            entity.setAge(dto.getAge());

        if (dto.getLifestyle() != null)
            entity.setLifestyle(dto.getLifestyle());

        if (dto.getDataSourceId() != null)
            entity.setDataSourceId(dto.getDataSourceId());

        if (dto.getExternalSourceUserId() != null)
            entity.setExternalSourceUserId(dto.getExternalSourceUserId());

        if (dto.getDiseases() != null)
            entity.setDiseases(dto.getDiseases());
    }
}
