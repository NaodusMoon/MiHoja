package com.miapp.MiHoja.service;

import com.miapp.MiHoja.dto.PersonaCompletaDTO;
import com.miapp.MiHoja.model.Alergia;
import com.miapp.MiHoja.model.CargoLaboral;
import com.miapp.MiHoja.model.ContactoEmergencia;
import com.miapp.MiHoja.model.Enfermedad;
import com.miapp.MiHoja.model.InduccionExamen;
import com.miapp.MiHoja.model.Medicamento;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.model.PersonaCargoLaboral;
import com.miapp.MiHoja.model.RiesgoProcedencia;
import com.miapp.MiHoja.model.Salud;
import com.miapp.MiHoja.repository.AlergiaRepository;
import com.miapp.MiHoja.repository.CargoLaboralRepository;
import com.miapp.MiHoja.repository.ContactoEmergenciaRepository;
import com.miapp.MiHoja.repository.EnfermedadRepository;
import com.miapp.MiHoja.repository.FormacionRepository;
import com.miapp.MiHoja.repository.InduccionExamenRepository;
import com.miapp.MiHoja.repository.MedicamentoRepository;
import com.miapp.MiHoja.repository.PersonaCargoLaboralRepository;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.repository.RiesgoProcedenciaRepository;
import com.miapp.MiHoja.repository.SaludRepository;
import com.miapp.MiHoja.model.Formacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaDtoPersistenceService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaCargoLaboralRepository pclRepository;

    @Autowired
    private SaludRepository saludRepository;

    @Autowired
    private FormacionRepository formacionRepository;

    @Autowired
    private CargoLaboralRepository cargoLaboralRepository;

    @Autowired
    private AlergiaRepository alergiaRepository;

    @Autowired
    private EnfermedadRepository enfermedadRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private InduccionExamenRepository induccionExamenRepository;

    @Autowired
    private ContactoEmergenciaRepository contactoEmergenciaRepository;

    @Autowired
    private RiesgoProcedenciaRepository riesgoProcedenciaRepository;

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String cleanOrNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private void setIfProvided(java.util.function.Consumer<String> setter, String incomingValue) {
        if (incomingValue != null) {
            setter.accept(cleanOrNull(incomingValue));
        }
    }

    @Transactional
    public void guardarDTO(PersonaCompletaDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID del DTO es null, no se puede guardar.");
        }

        Persona persona = personaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Persona con id " + dto.getId() + " no encontrada"));

        setIfProvided(persona::setNombres, dto.getNombres());
        setIfProvided(persona::setApellidos, dto.getApellidos());
        setIfProvided(persona::setCedula, dto.getCedula());
        setIfProvided(persona::setLugarExpedicion, dto.getLugarExpedicion());
        if (dto.getFechaNacimiento() != null) persona.setFechaNacimiento(dto.getFechaNacimiento());
        setIfProvided(persona::setDireccion, dto.getDireccion());
        setIfProvided(persona::setSexo, dto.getSexo());
        if (dto.getNumero() != null) persona.setNumero(dto.getNumero());
        setIfProvided(persona::setCorreoInstitucional, dto.getCorreoInstitucional());
        setIfProvided(persona::setTelefonoInstitucional, dto.getTelefonoInstitucional());
        setIfProvided(persona::setEnlaceSigep, dto.getEnlaceSigep());
        setIfProvided(persona::setEstado, dto.getEstado());
        if (dto.getNumeroHijos() != null) persona.setNumeroHijos(dto.getNumeroHijos());
        setIfProvided(persona::setImagenUrl, dto.getImagenUrl());

        if (dto.getSalud() != null) {
            Salud salud = persona.getRegistrosSalud().stream().findFirst().orElse(new Salud());
            PersonaCompletaDTO.Salud sDTO = dto.getSalud();
            setIfProvided(salud::setDotacion, sDTO.getDotacion());
            setIfProvided(salud::setArl, sDTO.getArl());
            setIfProvided(salud::setEps, sDTO.getEps());
            setIfProvided(salud::setAfp, sDTO.getAfp());
            setIfProvided(salud::setCcf, sDTO.getCcf());
            setIfProvided(salud::setRh, sDTO.getRh());
            if (sDTO.getCarnetVacunacion() != null) {
                salud.setCarnetVacunacion(sDTO.getCarnetVacunacion());
            }
            salud.setPersona(persona);
            saludRepository.save(salud);
            if (!persona.getRegistrosSalud().contains(salud)) persona.getRegistrosSalud().add(salud);
        }

        if (dto.getFormacion() != null) {
            for (PersonaCompletaDTO.Formacion fDTO : dto.getFormacion()) {
                Formacion f = (fDTO.getIdFormacion() != null)
                        ? formacionRepository.findById(fDTO.getIdFormacion()).orElse(new Formacion())
                        : new Formacion();
                setIfProvided(f::setFormacionAcademica, fDTO.getFormacionAcademica());
                setIfProvided(f::setGrado, fDTO.getGrado());
                setIfProvided(f::setTitulo, fDTO.getTitulo());
                f.setPersona(persona);
                formacionRepository.save(f);
                if (!persona.getFormaciones().contains(f)) persona.getFormaciones().add(f);
            }
        }

        if (dto.getCargoLaboral() != null) {
            for (PersonaCompletaDTO.CargoLaboral cDTO : dto.getCargoLaboral()) {
                CargoLaboral c = (cDTO.getIdCargo() != null)
                        ? cargoLaboralRepository.findById(cDTO.getIdCargo()).orElse(new CargoLaboral())
                        : new CargoLaboral();
                setIfProvided(c::setCodigo, cDTO.getCodigo());
                setIfProvided(c::setCargo, cDTO.getCargo());
                setIfProvided(c::setDependencia, cDTO.getDependencia());
                cargoLaboralRepository.save(c);
            }
        }

        if (dto.getPersonaCargoLaboral() != null) {
            for (PersonaCompletaDTO.PersonaCargoLaboral pclDTO : dto.getPersonaCargoLaboral()) {
                PersonaCargoLaboral pcl = (pclDTO.getIdPcl() != null)
                        ? pclRepository.findById(pclDTO.getIdPcl()).orElse(new PersonaCargoLaboral())
                        : new PersonaCargoLaboral();
                pcl.setPersona(persona);

                CargoLaboral cargo = null;
                if (pclDTO.getCargoId() != null) {
                    cargo = cargoLaboralRepository.findById(pclDTO.getCargoId())
                            .orElseThrow(() -> new RuntimeException("Cargo no encontrado con id " + pclDTO.getCargoId()));
                } else if (pcl.getCargo() != null) {
                    cargo = pcl.getCargo();
                }
                if (cargo == null) {
                    continue;
                }

                pcl.setCargo(cargo);
                if (pclDTO.getFechaIngreso() != null) {
                    pcl.setFechaIngreso(pclDTO.getFechaIngreso());
                }
                if (pclDTO.getFechaFirmaContrato() != null) {
                    pcl.setFechaFirmaContrato(pclDTO.getFechaFirmaContrato());
                }
                if (pclDTO.getMesesExperiencia() != null) {
                    pcl.setMesesExperiencia(pclDTO.getMesesExperiencia());
                }
                pclRepository.save(pcl);
                if (!persona.getCargosLaborales().contains(pcl)) persona.getCargosLaborales().add(pcl);
            }
        }

        if (dto.getInduccionExamen() != null) {
            for (PersonaCompletaDTO.InduccionExamen ieDTO : dto.getInduccionExamen()) {
                if (ieDTO.getPersonaCargoId() == null) {
                    continue;
                }
                PersonaCargoLaboral pcl = pclRepository.findById(ieDTO.getPersonaCargoId()).orElse(null);
                if (pcl == null) {
                    continue;
                }

                InduccionExamen ie = (ieDTO.getIdInduccion() != null)
                        ? induccionExamenRepository.findById(ieDTO.getIdInduccion()).orElse(new InduccionExamen())
                        : new InduccionExamen();
                ie.setPersonaCargoLaboral(pcl);
                if (ieDTO.getInduccion() != null) {
                    ie.setInduccion(ieDTO.getInduccion());
                }
                if (ieDTO.getExamenIngreso() != null) {
                    ie.setExamenIngreso(ieDTO.getExamenIngreso());
                }
                if (ieDTO.getFechaEgreso() != null) {
                    ie.setFechaEgreso(ieDTO.getFechaEgreso());
                }
                induccionExamenRepository.save(ie);
                if (pcl.getInduccionesExamen() != null && !pcl.getInduccionesExamen().contains(ie)) {
                    pcl.getInduccionesExamen().add(ie);
                }
            }
        }

        if (dto.getEnfermedad() != null) {
            for (PersonaCompletaDTO.Enfermedad eDTO : dto.getEnfermedad()) {
                Enfermedad e = (eDTO.getIdEnfermedad() != null)
                        ? enfermedadRepository.findById(eDTO.getIdEnfermedad()).orElse(new Enfermedad())
                        : new Enfermedad();
                String nombre = cleanOrNull(eDTO.getNombre());
                if (nombre == null) {
                    if (e.getId() != null) {
                        if (e.getMedicamentos() != null) {
                            e.getMedicamentos().forEach(med -> med.getEnfermedades().remove(e));
                            e.getMedicamentos().clear();
                        }
                        persona.getEnfermedades().remove(e);
                        enfermedadRepository.delete(e);
                    }
                    continue;
                }
                e.setNombre(nombre);
                e.setPersona(persona);
                enfermedadRepository.save(e);
                if (!persona.getEnfermedades().contains(e)) persona.getEnfermedades().add(e);
            }
        }

        if (dto.getAlergia() != null) {
            for (PersonaCompletaDTO.Alergia aDTO : dto.getAlergia()) {
                Alergia a = (aDTO.getIdAlergia() != null)
                        ? alergiaRepository.findById(aDTO.getIdAlergia()).orElse(new Alergia())
                        : new Alergia();
                String nombre = cleanOrNull(aDTO.getNombre());
                if (nombre == null) {
                    if (a.getId() != null) {
                        persona.getAlergias().remove(a);
                        alergiaRepository.delete(a);
                    }
                    continue;
                }
                a.setNombre(nombre);
                a.setPersona(persona);
                alergiaRepository.save(a);
                if (!persona.getAlergias().contains(a)) persona.getAlergias().add(a);
            }
        }

        if (dto.getMedicamento() != null) {
            for (PersonaCompletaDTO.Medicamento mDTO : dto.getMedicamento()) {
                Medicamento m = (mDTO.getIdMedicamento() != null)
                        ? medicamentoRepository.findById(mDTO.getIdMedicamento()).orElse(new Medicamento())
                        : new Medicamento();
                String nombre = cleanOrNull(mDTO.getNombre());
                if (nombre == null) {
                    if (m.getId() != null) {
                        if (m.getEnfermedades() != null) {
                            m.getEnfermedades().forEach(enf -> enf.getMedicamentos().remove(m));
                            m.getEnfermedades().clear();
                        }
                        persona.getMedicamentos().remove(m);
                        medicamentoRepository.delete(m);
                    }
                    continue;
                }
                m.setNombre(nombre);
                m.setPersona(persona);
                medicamentoRepository.save(m);
                if (!persona.getMedicamentos().contains(m)) persona.getMedicamentos().add(m);
            }
        }

        if (dto.getRiesgoProcedencia() != null) {
            for (PersonaCompletaDTO.RiesgoProcedencia rpDTO : dto.getRiesgoProcedencia()) {
                RiesgoProcedencia rp = (rpDTO.getIdRiesgo() != null)
                        ? riesgoProcedenciaRepository.findById(rpDTO.getIdRiesgo()).orElse(new RiesgoProcedencia())
                        : new RiesgoProcedencia();
                setIfProvided(rp::setMedioTransporte, rpDTO.getMedioTransporte());
                setIfProvided(rp::setProcedenciaTrabajador, rpDTO.getProcedenciaTrabajador());
                setIfProvided(rp::setRiesgo, rpDTO.getRiesgo());
                rp.setPersona(persona);
                riesgoProcedenciaRepository.save(rp);
                if (!persona.getRiesgoProcedencias().contains(rp)) persona.getRiesgoProcedencias().add(rp);
            }
        }

        if (dto.getContactoEmergencia() != null) {
            for (PersonaCompletaDTO.ContactoEmergencia ceDTO : dto.getContactoEmergencia()) {
                ContactoEmergencia ce = (ceDTO.getIdContacto() != null)
                        ? contactoEmergenciaRepository.findById(ceDTO.getIdContacto()).orElse(new ContactoEmergencia())
                        : new ContactoEmergencia();
                setIfProvided(ce::setNombreContactoEmergencia, ceDTO.getNombreContactoEmergencia());
                setIfProvided(ce::setParentesco, ceDTO.getParentesco());
                setIfProvided(ce::setTelefonoContactoEmergencia, ceDTO.getTelefonoContactoEmergencia());
                ce.setPersona(persona);
                contactoEmergenciaRepository.save(ce);
                if (!persona.getContactosEmergencia().contains(ce)) persona.getContactosEmergencia().add(ce);
            }
        }

        personaRepository.save(persona);
    }
}
