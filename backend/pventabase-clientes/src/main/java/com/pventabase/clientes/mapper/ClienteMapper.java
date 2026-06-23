package com.pventabase.clientes.mapper;

import com.pventabase.clientes.dto.ClienteRequestDTO;
import com.pventabase.clientes.dto.ClienteResponseDTO;
import com.pventabase.clientes.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Cliente toEntity(ClienteRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntity(ClienteRequestDTO dto, @MappingTarget Cliente entity);

    ClienteResponseDTO toResponseDto(Cliente entity);
}
