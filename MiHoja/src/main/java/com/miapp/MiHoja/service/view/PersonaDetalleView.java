package com.miapp.MiHoja.service.view;

import com.miapp.MiHoja.model.CargoLaboral;
import com.miapp.MiHoja.model.ContactoEmergencia;
import com.miapp.MiHoja.model.Formacion;
import com.miapp.MiHoja.model.InduccionExamen;
import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.model.PersonaCargoLaboral;
import com.miapp.MiHoja.model.RiesgoProcedencia;
import com.miapp.MiHoja.model.Salud;

import java.util.List;

public record PersonaDetalleView(
        Persona persona,
        Formacion formacion,
        PersonaCargoLaboral personaCargoLaboral,
        CargoLaboral cargoLaboral,
        InduccionExamen induccionExamen,
        Salud salud,
        RiesgoProcedencia riesgoProcedencia,
        ContactoEmergencia contactoEmergencia,
        List<String> enfermedades,
        List<String> alergias,
        List<String> medicamentos
) {}
