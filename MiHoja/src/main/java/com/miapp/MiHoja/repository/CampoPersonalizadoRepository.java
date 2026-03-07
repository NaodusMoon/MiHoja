package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.CampoPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampoPersonalizadoRepository extends JpaRepository<CampoPersonalizado, Long> {
    Optional<CampoPersonalizado> findFirstByNombreIgnoreCase(String nombre);
    List<CampoPersonalizado> findByActivoTrueOrderByNombreAsc();
    List<CampoPersonalizado> findAllByOrderByNombreAsc();
}
