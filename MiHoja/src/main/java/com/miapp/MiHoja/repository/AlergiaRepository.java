package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Alergia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlergiaRepository extends JpaRepository<Alergia, Long> {

    // Buscar todas las alergias asociadas a una persona por su ID
    List<Alergia> findByPersonaId(Long personaId);

    // Buscar por nombre exacto de alergia (opcional)
    List<Alergia> findByNombre(String nombre);
}
