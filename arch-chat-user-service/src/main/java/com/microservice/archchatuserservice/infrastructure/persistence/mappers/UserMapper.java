package com.microservice.archchatuserservice.infrastructure.persistence.mappers;

import com.microservice.archchatuserservice.domain.User;
import com.microservice.archchatuserservice.infrastructure.persistence.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(User domain);
    User toDomain (UserEntity entity);
}
