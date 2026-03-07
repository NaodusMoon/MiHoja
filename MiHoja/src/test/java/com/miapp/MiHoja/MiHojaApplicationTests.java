package com.miapp.MiHoja;

import com.miapp.MiHoja.model.Persona;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MiHojaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonaRepository personaRepository;

    private Long personaId;

    @BeforeEach
    void setUp() {
        Persona persona = new Persona();
        persona.setNumero(1);
        persona.setNombres("Ana");
        persona.setApellidos("Test");
        persona.setCedula("CC-TEST-123");
        persona.setLugarExpedicion("Bogota");
        persona.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        persona.setDireccion("Calle 1");
        persona.setSexo("F");
        persona.setCorreoInstitucional("ana.test@example.com");
        persona.setTelefonoInstitucional("3000000000");
        persona.setEnlaceSigep("");
        persona.setEstado("Activo");
        persona.setNumeroHijos(0);

        personaId = personaRepository.save(persona).getId();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void muestraConEditTrueRenderizaFormularioEmbebido() throws Exception {
        mockMvc.perform(get("/muestra/{id}", personaId).param("edit", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Edicion directa")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<iframe"))));
    }

    @Test
    void guardarEdicionMantieneFiltrosEnRedirect() throws Exception {
        String returnQuery = "nombre=Ana&cargo=Analista";

        mockMvc.perform(post("/editar/{id}", personaId)
                        .param("nombres", "Ana Maria")
                        .param("apellidos", "Test")
                        .param("cedula", "CC-TEST-123")
                        .param("returnQuery", returnQuery))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/muestra/" + personaId + "?" + returnQuery + "&edit=false"));
    }
}
