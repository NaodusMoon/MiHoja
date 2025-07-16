package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Salud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaludRepository extends JpaRepository<Salud, Long> {

    // Buscar todos los registros de salud de una persona
    List<Salud> findByPersonaId(Long personaId);

    // Buscar por EPS (por nombre parcial, insensible a mayúsculas)
    List<Salud> findByEpsContainingIgnoreCase(String eps);

    // Buscar por ARL
    List<Salud> findByArlContainingIgnoreCase(String arl);

    // Buscar por AFP
    List<Salud> findByAfpContainingIgnoreCase(String afp);

    // Buscar por Caja de Compensación Familiar
    List<Salud> findByCcfContainingIgnoreCase(String ccf);

    // Buscar por tipo de sangre (RH)
    List<Salud> findByRh(String rh);

    // Buscar por estado de carnet de vacunación
    List<Salud> findByCarnetVacunacion(boolean carnetVacunacion);
}
