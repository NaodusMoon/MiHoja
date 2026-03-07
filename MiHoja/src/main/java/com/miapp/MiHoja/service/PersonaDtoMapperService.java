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
import com.miapp.MiHoja.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PersonaDtoMapperService {

    @Autowired
    private PersonaRepository personaRepository;

    public PersonaCompletaDTO convertirADTO(Persona persona) {
        if (persona == null) return null;

        PersonaCompletaDTO dto = new PersonaCompletaDTO();
        dto.setId(persona.getId());
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setCedula(persona.getCedula());
        dto.setLugarExpedicion(persona.getLugarExpedicion());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setDireccion(persona.getDireccion());
        dto.setSexo(persona.getSexo());
        dto.setNumero(persona.getNumero());
        dto.setCorreoInstitucional(persona.getCorreoInstitucional());
        dto.setTelefonoInstitucional(persona.getTelefonoInstitucional());
        dto.setEnlaceSigep(persona.getEnlaceSigep());
        dto.setEstado(persona.getEstado());
        dto.setNumeroHijos(persona.getNumeroHijos());
        dto.setImagenUrl(persona.getImagenUrl());

        if (persona.getFormaciones() != null) {
            dto.setFormacion(persona.getFormaciones().stream().map(f -> {
                PersonaCompletaDTO.Formacion fDTO = new PersonaCompletaDTO.Formacion();
                fDTO.setIdFormacion(f.getIdFormacion());
                fDTO.setFormacionAcademica(f.getFormacionAcademica());
                fDTO.setGrado(f.getGrado());
                fDTO.setTitulo(f.getTitulo());
                return fDTO;
            }).toList());
        }

        if (persona.getCargosLaborales() != null) {
            dto.setCargoLaboral(persona.getCargosLaborales().stream().map(pcl -> {
                PersonaCompletaDTO.CargoLaboral cDTO = new PersonaCompletaDTO.CargoLaboral();
                CargoLaboral cargo = pcl.getCargo();

                cDTO.setIdCargo(cargo.getId());
                cDTO.setCodigo(cargo.getCodigo());
                cDTO.setCargo(cargo.getCargo());
                cDTO.setDependencia(cargo.getDependencia());

                return cDTO;
            }).toList());
        }

        if (persona.getRegistrosSalud() != null && !persona.getRegistrosSalud().isEmpty()) {
            Salud registro = persona.getRegistrosSalud().iterator().next();
            PersonaCompletaDTO.Salud sDTO = new PersonaCompletaDTO.Salud();
            sDTO.setIdSalud(registro.getIdSalud());
            sDTO.setDotacion(registro.getDotacion());
            sDTO.setArl(registro.getArl());
            sDTO.setEps(registro.getEps());
            sDTO.setAfp(registro.getAfp());
            sDTO.setCcf(registro.getCcf());
            sDTO.setRh(registro.getRh());
            sDTO.setCarnetVacunacion(registro.getCarnetVacunacion());
            dto.setSalud(sDTO);
        }

        if (persona.getEnfermedades() != null && !persona.getEnfermedades().isEmpty()) {
            dto.setEnfermedad(
                persona.getEnfermedades().stream()
                    .map(e -> {
                        PersonaCompletaDTO.Enfermedad eDTO = new PersonaCompletaDTO.Enfermedad();
                        eDTO.setIdEnfermedad(e.getId());
                        eDTO.setNombre(e.getNombre());
                        return eDTO;
                    })
                    .toList()
            );
        }

        if (persona.getAlergias() != null && !persona.getAlergias().isEmpty()) {
            dto.setAlergia(
                persona.getAlergias().stream()
                    .map(a -> {
                        PersonaCompletaDTO.Alergia aDTO = new PersonaCompletaDTO.Alergia();
                        aDTO.setIdAlergia(a.getId());
                        aDTO.setNombre(a.getNombre());
                        return aDTO;
                    })
                    .toList()
            );
        }

        if (persona.getMedicamentos() != null && !persona.getMedicamentos().isEmpty()) {
            dto.setMedicamento(
                persona.getMedicamentos().stream()
                    .map(m -> {
                        PersonaCompletaDTO.Medicamento mDTO = new PersonaCompletaDTO.Medicamento();
                        mDTO.setIdMedicamento(m.getId());
                        mDTO.setNombre(m.getNombre());
                        return mDTO;
                    })
                    .toList()
            );
        }

        if (persona.getContactosEmergencia() != null && !persona.getContactosEmergencia().isEmpty()) {
            dto.setContactoEmergencia(
                persona.getContactosEmergencia().stream()
                    .map(c -> {
                        PersonaCompletaDTO.ContactoEmergencia cDTO = new PersonaCompletaDTO.ContactoEmergencia();
                        cDTO.setIdContacto(c.getIdContacto());
                        cDTO.setNombreContactoEmergencia(c.getNombreContactoEmergencia());
                        cDTO.setTelefonoContactoEmergencia(c.getTelefonoContactoEmergencia());
                        cDTO.setParentesco(c.getParentesco());
                        return cDTO;
                    })
                    .toList()
            );
        }

        if (persona.getRiesgoProcedencias() != null && !persona.getRiesgoProcedencias().isEmpty()) {
            dto.setRiesgoProcedencia(
                persona.getRiesgoProcedencias().stream()
                    .map(r -> {
                        PersonaCompletaDTO.RiesgoProcedencia rDTO = new PersonaCompletaDTO.RiesgoProcedencia();
                        rDTO.setIdRiesgo(r.getIdRiesgo());
                        rDTO.setRiesgo(r.getRiesgo());
                        rDTO.setMedioTransporte(r.getMedioTransporte());
                        rDTO.setProcedenciaTrabajador(r.getProcedenciaTrabajador());
                        return rDTO;
                    })
                    .toList()
            );
        }

        if (persona.getCargosLaborales() != null && !persona.getCargosLaborales().isEmpty()) {
            dto.setInduccionExamen(persona.getCargosLaborales().stream()
                .filter(pcl -> pcl.getInduccionesExamen() != null && !pcl.getInduccionesExamen().isEmpty())
                .flatMap(pcl -> pcl.getInduccionesExamen().stream())
                .map(i -> {
                    PersonaCompletaDTO.InduccionExamen iDTO = new PersonaCompletaDTO.InduccionExamen();
                    iDTO.setIdInduccion(i.getIdInduccion());
                    iDTO.setInduccion(i.getInduccion());
                    iDTO.setExamenIngreso(i.getExamenIngreso());
                    iDTO.setFechaEgreso(i.getFechaEgreso());
                    return iDTO;
                })
                .toList());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public PersonaCompletaDTO buscarDTOporId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        java.util.Set<com.miapp.MiHoja.model.Formacion> formacionesPersona =
                java.util.Optional.ofNullable(persona.getFormaciones()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.PersonaCargoLaboral> cargosPersona =
                java.util.Optional.ofNullable(persona.getCargosLaborales()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.Salud> saludPersona =
                java.util.Optional.ofNullable(persona.getRegistrosSalud()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.Alergia> alergiasPersona =
                java.util.Optional.ofNullable(persona.getAlergias()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.Enfermedad> enfermedadesPersona =
                java.util.Optional.ofNullable(persona.getEnfermedades()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.Medicamento> medicamentosPersona =
                java.util.Optional.ofNullable(persona.getMedicamentos()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.ContactoEmergencia> contactosPersona =
                java.util.Optional.ofNullable(persona.getContactosEmergencia()).orElse(java.util.Collections.emptySet());
        java.util.Set<com.miapp.MiHoja.model.RiesgoProcedencia> riesgosPersona =
                java.util.Optional.ofNullable(persona.getRiesgoProcedencias()).orElse(java.util.Collections.emptySet());

        PersonaCompletaDTO dto = new PersonaCompletaDTO();
        dto.setId(persona.getId());
        dto.setNombres(persona.getNombres());
        dto.setApellidos(persona.getApellidos());
        dto.setCedula(persona.getCedula());
        dto.setLugarExpedicion(persona.getLugarExpedicion());
        dto.setFechaNacimiento(persona.getFechaNacimiento());
        dto.setDireccion(persona.getDireccion());
        dto.setSexo(persona.getSexo());
        dto.setNumero(persona.getNumero());
        dto.setCorreoInstitucional(persona.getCorreoInstitucional());
        dto.setTelefonoInstitucional(persona.getTelefonoInstitucional());
        dto.setEnlaceSigep(persona.getEnlaceSigep());
        dto.setEstado(persona.getEstado());
        dto.setNumeroHijos(persona.getNumeroHijos());
        dto.setImagenUrl(persona.getImagenUrl());

        dto.setFormacion(formacionesPersona.stream().map(f -> {
            PersonaCompletaDTO.Formacion fDTO = new PersonaCompletaDTO.Formacion();
            fDTO.setIdFormacion(f.getIdFormacion());
            fDTO.setN(f.getPersona().getNumero().longValue());
            fDTO.setFormacionAcademica(f.getFormacionAcademica());
            fDTO.setGrado(f.getGrado());
            fDTO.setTitulo(f.getTitulo());
            return fDTO;
        }).toList());

        if (!saludPersona.isEmpty()) {
            Salud s = saludPersona.stream().findFirst().orElse(null);
            if (s != null) {
                PersonaCompletaDTO.Salud sDTO = new PersonaCompletaDTO.Salud();
                sDTO.setIdSalud(s.getIdSalud());
                sDTO.setDotacion(s.getDotacion());
                sDTO.setArl(s.getArl());
                sDTO.setEps(s.getEps());
                sDTO.setAfp(s.getAfp());
                sDTO.setCcf(s.getCcf());
                sDTO.setRh(s.getRh());
                sDTO.setCarnetVacunacion(s.getCarnetVacunacion());
                dto.setSalud(sDTO);
            }
        }

        dto.setCargoLaboral(cargosPersona.stream()
                .map(pcl -> {
                    PersonaCompletaDTO.CargoLaboral cDTO = new PersonaCompletaDTO.CargoLaboral();
                    cDTO.setIdCargo(pcl.getCargo().getId());
                    cDTO.setCargo(pcl.getCargo().getCargo());
                    cDTO.setCodigo(pcl.getCargo().getCodigo());
                    cDTO.setDependencia(pcl.getCargo().getDependencia());
                    return cDTO;
                }).toList());

        dto.setAlergia(alergiasPersona.stream().map(a -> {
            PersonaCompletaDTO.Alergia aDTO = new PersonaCompletaDTO.Alergia();
            aDTO.setIdAlergia(a.getId());
            aDTO.setN(persona.getNumero().longValue());
            aDTO.setNombre(a.getNombre());
            return aDTO;
        }).toList());

        dto.setEnfermedad(enfermedadesPersona.stream().map(e -> {
            PersonaCompletaDTO.Enfermedad eDTO = new PersonaCompletaDTO.Enfermedad();
            eDTO.setIdEnfermedad(e.getId());
            eDTO.setN(persona.getNumero().longValue());
            eDTO.setNombre(e.getNombre());
            return eDTO;
        }).toList());

        dto.setMedicamento(medicamentosPersona.stream().map(m -> {
            PersonaCompletaDTO.Medicamento mDTO = new PersonaCompletaDTO.Medicamento();
            mDTO.setIdMedicamento(m.getId());
            mDTO.setNombre(m.getNombre());
            return mDTO;
        }).toList());

        dto.setContactoEmergencia(contactosPersona.stream().map(c -> {
            PersonaCompletaDTO.ContactoEmergencia cDTO = new PersonaCompletaDTO.ContactoEmergencia();
            cDTO.setIdContacto(c.getIdContacto());
            cDTO.setNombreContactoEmergencia(c.getNombreContactoEmergencia());
            cDTO.setTelefonoContactoEmergencia(c.getTelefonoContactoEmergencia());
            cDTO.setParentesco(c.getParentesco());
            return cDTO;
        }).toList());

        dto.setRiesgoProcedencia(riesgosPersona.stream().map(r -> {
            PersonaCompletaDTO.RiesgoProcedencia rDTO = new PersonaCompletaDTO.RiesgoProcedencia();
            rDTO.setIdRiesgo(r.getIdRiesgo());
            rDTO.setRiesgo(r.getRiesgo());
            rDTO.setMedioTransporte(r.getMedioTransporte());
            rDTO.setProcedenciaTrabajador(r.getProcedenciaTrabajador());
            return rDTO;
        }).toList());

        dto.setInduccionExamen(cargosPersona.stream()
                .filter(pcl -> pcl.getInduccionesExamen() != null && !pcl.getInduccionesExamen().isEmpty())
                .flatMap(pcl -> pcl.getInduccionesExamen().stream().map(i -> {
                    PersonaCompletaDTO.InduccionExamen iDTO = new PersonaCompletaDTO.InduccionExamen();
                    iDTO.setIdInduccion(i.getIdInduccion());
                    iDTO.setPersonaCargoId(pcl.getId());
                    iDTO.setInduccion(i.getInduccion());
                    iDTO.setExamenIngreso(i.getExamenIngreso());
                    iDTO.setFechaEgreso(i.getFechaEgreso());
                    return iDTO;
                }))
                .collect(Collectors.toList()));

        dto.setPersonaCargoLaboral(cargosPersona.stream()
            .map(pcl -> {
                PersonaCompletaDTO.PersonaCargoLaboral pclDTO = new PersonaCompletaDTO.PersonaCargoLaboral();
                pclDTO.setIdPcl(pcl.getId());
                pclDTO.setPersonaId(pcl.getPersona() != null ? pcl.getPersona().getId() : null);
                pclDTO.setCargoId(pcl.getCargo() != null ? pcl.getCargo().getId() : null);
                pclDTO.setFechaIngreso(pcl.getFechaIngreso());
                pclDTO.setFechaFirmaContrato(pcl.getFechaFirmaContrato());
                pclDTO.setMesesExperiencia(pcl.getMesesExperiencia());
                return pclDTO;
            }).toList());

        return dto;
    }
}
