package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.CargoLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoLaboralRepository extends JpaRepository<CargoLaboral, Long> {

    // Buscar por nombre exacto del cargo
    List<CargoLaboral> findByCargo(String cargo);

    // Buscar cargos por dependencia exacta
    List<CargoLaboral> findByDependencia(String dependencia);

    // Buscar por coincidencia parcial en nombre de cargo (ignora mayúsculas/minúsculas)
    List<CargoLaboral> findByCargoContainingIgnoreCase(String cargo);

    // Buscar por coincidencia parcial en dependencia (ignora mayúsculas/minúsculas)
    List<CargoLaboral> findByDependenciaContainingIgnoreCase(String dependencia);

    // Buscar por código exacto
    CargoLaboral findByCodigo(String codigo);
}
