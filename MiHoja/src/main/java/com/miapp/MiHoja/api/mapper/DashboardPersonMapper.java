package com.miapp.MiHoja.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.miapp.MiHoja.api.dto.DashboardPersonCardResponse;
import com.miapp.MiHoja.dto.PersonaConCargo;

@Mapper(componentModel = "spring")
public interface DashboardPersonMapper {

    @Mapping(target = "cargo", source = "cargo")
    @Mapping(target = "dependencia", source = "dependencia")
    DashboardPersonCardResponse toCard(PersonaConCargo persona);
}
