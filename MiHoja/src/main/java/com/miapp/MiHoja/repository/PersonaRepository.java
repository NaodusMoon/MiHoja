package com.miapp.MiHoja.repository;

import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.dto.PersonaConCargo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // Búsqueda parcial por nombre
    List<Persona> findByNombresContainingIgnoreCase(String nombres);

    // Búsqueda parcial por apellido
    List<Persona> findByApellidosContainingIgnoreCase(String apellidos);

    // Búsqueda parcial combinada por nombre y apellido
    List<Persona> findByNombresContainingIgnoreCaseAndApellidosContainingIgnoreCase(String nombres, String apellidos);

    // Búsqueda exacta por cédula
    List<Persona> findByCedula(String cedula);

    // Búsqueda parcial por lugar de expedición
    List<Persona> findByLugarExpedicionContainingIgnoreCase(String lugarExpedicion);

    // Búsqueda parcial por dirección
    List<Persona> findByDireccionContainingIgnoreCase(String direccion);

    // Búsqueda exacta por sexo
    List<Persona> findBySexo(String sexo);

    // Búsqueda exacta por correo institucional
    List<Persona> findByCorreoInstitucional(String correoInstitucional);

    // Búsqueda exacta por teléfono institucional
    List<Persona> findByTelefonoInstitucional(String telefonoInstitucional);

    // Búsqueda parcial por enlace SIGEP
    List<Persona> findByEnlaceSigepContainingIgnoreCase(String enlaceSigep);

    // Obtener registros con número mayor al dado, ordenados
    List<Persona> findByNumeroGreaterThanOrderByNumeroAsc(Integer numero);

    // Disminuir en 1 todos los números mayores a cierto número
    @Modifying
    @Query("UPDATE Persona p SET p.numero = p.numero - 1 WHERE p.numero > :numeroEliminado")
    void decrementarNumerosPosteriores(@Param("numeroEliminado") int numeroEliminado);

        // ✅ Cargar persona con todas las relaciones importantes
    @EntityGraph(attributePaths = {
            "formaciones",
            "contactosEmergencia",
            "riesgoProcedencias",
            "registrosSalud",
            "alergias",
            "enfermedades",
            "enfermedades.medicamentos",
            "cargosLaborales",
            "cargosLaborales.cargo",
            "cargosLaborales.induccionesExamen"
    })
    @Query("SELECT p FROM Persona p WHERE p.id = :id")
    Optional<Persona> findByIdWithAllRelations(@Param("id") Long id);

        // ✅ Consulta personalizada de personas con su cargo más reciente + todos los campos necesarios
    @Query(value = """
        SELECT 
            p.n AS id,
            p.nombres AS nombres,
            p.apellidos AS apellidos,
            p.cedula AS cedula,
            p.numero AS numero,
            p.lugar_expedicion AS lugarExpedicion,
            p.direccion AS direccion,
            p.sexo AS sexo,
            p.correo_institucional AS correoInstitucional,
            p.telefono_institucional AS telefonoInstitucional,
            p.enlace_sigep AS enlaceSigep,

            -- ✅ Cargo más reciente
            cl.cargo AS cargo,
            cl.dependencia AS dependencia,

            -- ✅ Formación (última)
            f.formacion_academica AS formacion,
            f.grado AS grado,

            -- ✅ Salud
            s.rh AS rh,
            s.eps AS eps,
            s.afp AS afp,
            s.dotacion AS dotacion,
            CASE 
                WHEN s.carnet_vacunacion = TRUE THEN 'SI'
                ELSE 'NO'
            END AS carnetVacunacion,

            -- ✅ Riesgo y procedencia
            rp.riesgo AS riesgo,
            rp.medio_transporte AS medioTransporte,
            rp.procedencia_trabajador AS procedencia,

            -- ✅ Inducción y examen
            CASE 
                WHEN ie.induccion = TRUE THEN 'SI'
                ELSE 'NO'
            END AS induccion,
            CASE 
                WHEN ie.examen_ingreso = TRUE THEN 'SI'
                ELSE 'NO'
            END AS examen,

            -- ✅ Meses de experiencia
            pcl.meses_experiencia AS mesesExperiencia

        FROM persona p

        -- Último cargo (LATERAL)
        LEFT JOIN LATERAL (
            SELECT DISTINCT ON (persona_id) *
            FROM persona_cargo_laboral
            WHERE persona_id = p.n
            ORDER BY persona_id, fecha_ingreso DESC NULLS LAST, id_pcl DESC
        ) pcl ON TRUE

        LEFT JOIN cargo_laboral cl ON pcl.cargo_id = cl.id_cargo

        -- Última formación (LATERAL)
        LEFT JOIN LATERAL (
            SELECT DISTINCT ON (n) *
            FROM formacion
            WHERE n = p.n
            ORDER BY n, id_formacion DESC
        ) f ON TRUE

        -- Última salud (LATERAL)
        LEFT JOIN LATERAL (
            SELECT DISTINCT ON (n) *
            FROM salud
            WHERE n = p.n
            ORDER BY n, id_salud DESC
        ) s ON TRUE

        -- Último riesgo (LATERAL)
        LEFT JOIN LATERAL (
            SELECT DISTINCT ON (n) *
            FROM riesgo_procedencia
            WHERE n = p.n
            ORDER BY n, id_riesgo DESC
        ) rp ON TRUE

        -- Última inducción/examen (LATERAL)
        LEFT JOIN LATERAL (
            SELECT DISTINCT ON (persona_cargo_id) *
            FROM induccion_examen
            WHERE persona_cargo_id = pcl.id_pcl
            ORDER BY persona_cargo_id, id_induccion DESC
        ) ie ON TRUE
        """, nativeQuery = true)
    List<PersonaConCargo> consultarPersonasConCargo();
}
