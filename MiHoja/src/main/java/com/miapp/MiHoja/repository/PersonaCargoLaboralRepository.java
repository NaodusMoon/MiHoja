package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.PersonaCargoLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaCargoLaboralRepository extends JpaRepository<PersonaCargoLaboral, Long> {

    // Buscar todos los cargos asignados a una persona específica
    List<PersonaCargoLaboral> findByPersona_Id(Long personaId);

    // Buscar todos los registros donde se asignó un cargo específico
    List<PersonaCargoLaboral> findByCargo_Id(Long cargoId);

    // Buscar por persona y cargo específicos
    Optional<PersonaCargoLaboral> findByPersona_IdAndCargo_Id(Long personaId, Long cargoId);

    // Buscar por combinación exacta de persona y fechas
    Optional<PersonaCargoLaboral> findByPersona_IdAndFechaIngresoAndFechaFirmaContrato(
            Long personaId, LocalDate fechaIngreso, LocalDate fechaFirmaContrato
    );
}
