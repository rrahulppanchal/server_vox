package com.emplmgt.employee_management.mappers;

import com.emplmgt.employee_management.dto.ContactLogsDTO;
import com.emplmgt.employee_management.dto.ContactsDTO;
import com.emplmgt.employee_management.entities.ContactsEntity;
import com.emplmgt.employee_management.entities.ContactsLogsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ContactMapper.class})
public interface ContactMapper {

    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    @Mapping(target = "entity.id")
    ContactsDTO toDTO(ContactsEntity contactsEntity);

    @Mapping(target = "entity.id")
    ContactsEntity toEntity(ContactsDTO contactsDTO);

    @Mapping(target = "entity.id")
    ContactLogsDTO toLogsDTO(ContactsLogsEntity contactsEntity);

    @Mapping(target = "entity.id")
    ContactsEntity toLogsEntity(ContactLogsDTO contactsDTO);

    List<ContactsDTO> toDTOs(List<ContactsEntity> contactsEntity);

    List<ContactsEntity> toEntities(List<ContactsDTO> contactsDTO);

    @Mapping(target = "entity.id")
    List<ContactLogsDTO> toLogsDTOs(List<ContactsLogsEntity> contactsEntity);

    @Mapping(target = "entity.id")
    List<ContactsEntity> toLogsEntities(List<ContactLogsDTO> contactsDTO);

}
