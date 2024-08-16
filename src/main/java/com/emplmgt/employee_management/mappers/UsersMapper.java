package com.emplmgt.employee_management.mappers;

import com.emplmgt.employee_management.dto.UsersDTO;
import com.emplmgt.employee_management.entities.UsersEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = {UsersMapper.class})
public interface UsersMapper {

    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    @Mapping(target = "entity.id")
    UsersDTO toDTO(UsersEntity usersEntity);

    @Mapping(target = "entity.id")
    UsersEntity toEntity(UsersDTO usersDTO);
}
