package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Enfermedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnfermedadRepository extends JpaRepository<Enfermedad, Long> {

    // Buscar todas las enfermedades asociadas a una persona por su ID
    List<Enfermedad> findByPersonaId(Long personaId);

    // Buscar enfermedad por nombre exacto (retorna solo una o null)
    Enfermedad findByNombre(String nombre);

    // Buscar enfermedades por coincidencia parcial en el nombre (ignora mayúsculas/minúsculas)
    List<Enfermedad> findByNombreContainingIgnoreCase(String nombre);

    // 🔹 Buscar la primera coincidencia por nombre ignorando mayúsculas/minúsculas
    Enfermedad findFirstByNombreIgnoreCase(String nombre);
}
