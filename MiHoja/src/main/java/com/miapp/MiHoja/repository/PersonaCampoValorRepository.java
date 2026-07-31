package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.PersonaCampoValor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaCampoValorRepository extends JpaRepository<PersonaCampoValor, Long> {
    List<PersonaCampoValor> findByPersonaId(Long personaId);
    Optional<PersonaCampoValor> findFirstByPersonaIdAndCampoId(Long personaId, Long campoId);
    void deleteByCampoId(Long campoId);
}
