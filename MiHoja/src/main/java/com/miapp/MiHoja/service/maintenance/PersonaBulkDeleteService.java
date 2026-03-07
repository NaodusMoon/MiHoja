package com.miapp.MiHoja.service.maintenance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonaBulkDeleteService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public DeletionSummary eliminarVariosConResumen(List<Long> ids) {
        DeletionSummary summary = new DeletionSummary();
        if (ids == null || ids.isEmpty()) {
            return summary;
        }

        try {
            eliminarLoteRapido(ids);
            summary.setEliminados(ids.size());
            return summary;
        } catch (Exception exception) {
            for (Long id : ids) {
                try {
                    eliminarUno(id);
                    summary.setEliminados(summary.getEliminados() + 1);
                } catch (Exception innerException) {
                    summary.getFallidos().add(id);
                }
            }
            return summary;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void eliminarUno(Long id) {
        eliminarLoteRapido(List.of(id));
    }

    private void eliminarLoteRapido(List<Long> ids) {
        entityManager.createNativeQuery(
                        "DELETE FROM enfermedad_medicamento " +
                                "WHERE enfermedad_id IN (SELECT id_enfermedad FROM enfermedad WHERE n IN (:ids)) " +
                                "OR medicamento_id IN (SELECT id_medicamento FROM medicamento WHERE n IN (:ids))")
                .setParameter("ids", ids)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM induccion_examen WHERE persona_cargo_id IN " +
                        "(SELECT id_pcl FROM persona_cargo_laboral WHERE persona_id IN (:ids))")
                .setParameter("ids", ids)
                .executeUpdate();
        entityManager.createNativeQuery("DELETE FROM persona_cargo_laboral WHERE persona_id IN (:ids)")
                .setParameter("ids", ids)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM formacion WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM riesgo_procedencia WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM salud WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM contacto_emergencia WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM alergia WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM enfermedad WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM medicamento WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM persona_campo_valor WHERE persona_id IN (:ids)").setParameter("ids", ids).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM persona WHERE n IN (:ids)").setParameter("ids", ids).executeUpdate();
    }

    public static class DeletionSummary {
        private int eliminados;
        private final List<Long> fallidos = new ArrayList<>();

        public int getEliminados() {
            return eliminados;
        }

        public void setEliminados(int eliminados) {
            this.eliminados = eliminados;
        }

        public List<Long> getFallidos() {
            return fallidos;
        }
    }
}
