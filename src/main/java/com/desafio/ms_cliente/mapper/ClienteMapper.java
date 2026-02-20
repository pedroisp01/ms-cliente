package com.desafio.ms_cliente.mapper;

import com.desafio.ms_cliente.dto.ClienteDTO;
import com.desafio.ms_cliente.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteDTO toDTO(Cliente cliente);

    Cliente toEntity(ClienteDTO clienteDTO);
}