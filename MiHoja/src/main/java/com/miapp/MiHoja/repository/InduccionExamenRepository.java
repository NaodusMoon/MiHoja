package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.InduccionExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InduccionExamenRepository extends JpaRepository<InduccionExamen, Long> {

    // Buscar por ID de la relación persona-cargo
    List<InduccionExamen> findByPersonaCargoLaboralId(Long personaCargoId);

    // Buscar uno por ID exacto de persona-cargo (opcional)
    Optional<InduccionExamen> findFirstByPersonaCargoLaboralId(Long personaCargoId);

    // Buscar todos los registros donde se hizo inducción
    List<InduccionExamen> findByInduccionTrue();

    // Buscar todos los registros donde se hizo examen de ingreso
    List<InduccionExamen> findByExamenIngresoTrue();

    // Buscar todos los registros donde se hizo inducción y examen de ingreso
    List<InduccionExamen> findByInduccionTrueAndExamenIngresoTrue();
}
