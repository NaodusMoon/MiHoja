package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.RiesgoProcedencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiesgoProcedenciaRepository extends JpaRepository<RiesgoProcedencia, Long> {

    // Buscar todos los registros de riesgo por persona
    List<RiesgoProcedencia> findByPersonaId(Long personaId);

    // Buscar por tipo de riesgo (parcial)
    List<RiesgoProcedencia> findByRiesgoContainingIgnoreCase(String riesgo);

    // Buscar por medio de transporte
    List<RiesgoProcedencia> findByMedioTransporteContainingIgnoreCase(String medioTransporte);

    // Buscar por procedencia del trabajador
    List<RiesgoProcedencia> findByProcedenciaTrabajadorContainingIgnoreCase(String procedenciaTrabajador);
}
