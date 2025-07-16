package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Medicamento;
import com.miapp.MiHoja.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    // Buscar medicamentos por nombre exacto
    List<Medicamento> findByNombre(String nombre);

    // Buscar medicamentos por coincidencia parcial (ignora mayúsculas)
    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);

    // Buscar medicamento específico por nombre y persona (si tienes la relación directa en Medicamento)
    Optional<Medicamento> findByNombreAndPersona(String nombre, Persona persona);

    // Buscar todos los medicamentos relacionados con una persona a través de sus enfermedades
    @Query("SELECT DISTINCT m FROM Medicamento m " +
           "JOIN m.enfermedades e " +
           "WHERE e.persona = :persona")
    List<Medicamento> findByPersonaRelacionada(@Param("persona") Persona persona);
}
