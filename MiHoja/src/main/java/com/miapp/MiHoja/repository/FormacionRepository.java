package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Formacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormacionRepository extends JpaRepository<Formacion, Long> {

    // Buscar formaciones por ID de persona
    List<Formacion> findByPersonaId(Long personaId);

    // Buscar por nivel de formación académica exacto
    List<Formacion> findByFormacionAcademica(String formacionAcademica);

    // Buscar por grado exacto
    List<Formacion> findByGrado(String grado);

    // Buscar por título exacto
    List<Formacion> findByTitulo(String titulo);

    // Buscar por coincidencia parcial en el título
    List<Formacion> findByTituloContainingIgnoreCase(String titulo);
}
