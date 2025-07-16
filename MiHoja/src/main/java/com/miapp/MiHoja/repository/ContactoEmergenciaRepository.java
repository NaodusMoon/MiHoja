package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.ContactoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoEmergenciaRepository extends JpaRepository<ContactoEmergencia, Long> {

    // Buscar contactos por el ID de la persona
    List<ContactoEmergencia> findByPersonaId(Long personaId);

    // Buscar por nombre exacto del contacto
    List<ContactoEmergencia> findByNombreContactoEmergencia(String nombre);

    // Buscar por parentesco
    List<ContactoEmergencia> findByParentesco(String parentesco);

    // Buscar por número exacto
    List<ContactoEmergencia> findByTelefonoContactoEmergencia(String telefono);
}
