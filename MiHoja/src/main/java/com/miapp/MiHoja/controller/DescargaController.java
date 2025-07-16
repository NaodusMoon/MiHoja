package com.miapp.MiHoja.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.PersonaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/descargar")
public class DescargaController {

    @Autowired
    private PersonaRepository personaRepository;

    private <T> T getPrimero(Set<T> set) {
        return (set != null && !set.isEmpty()) ? set.iterator().next() : null;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> descargarPDF(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font norm = new Font(Font.HELVETICA, 12);

            doc.add(new Paragraph("Datos de la Persona", title));
            doc.add(new Paragraph(" "));

            addParagraph(doc, "ID: " + p.getId(), norm);
            addParagraph(doc, "Nombre: " + p.getNombres() + " " + p.getApellidos(), norm);
            addParagraph(doc, "Cédula: " + p.getCedula(), norm);
            addParagraph(doc, "Lugar Expedición: " + p.getLugarExpedicion(), norm);
            addParagraph(doc, "Fecha Nac.: " + p.getFechaNacimiento(), norm);
            addParagraph(doc, "Dirección: " + p.getDireccion(), norm);
            addParagraph(doc, "Sexo: " + p.getSexo(), norm);
            addParagraph(doc, "Número: " + p.getNumero(), norm);
            addParagraph(doc, "Correo: " + p.getCorreoInstitucional(), norm);
            addParagraph(doc, "Teléfono: " + p.getTelefonoInstitucional(), norm);
            addParagraph(doc, "Enlace SIGEP: " + p.getEnlaceSigep(), norm);

            PersonaCargoLaboral pcl = getPrimero(p.getCargosLaborales());
            if (pcl != null && pcl.getCargo() != null) {
                CargoLaboral cl = pcl.getCargo();
                addParagraph(doc, "", norm);
                addParagraph(doc, "=== Cargo Laboral ===", title);
                addParagraph(doc, "Cargo: " + cl.getCargo(), norm);
                addParagraph(doc, "Código: " + cl.getCodigo(), norm);
                addParagraph(doc, "Dependencia: " + cl.getDependencia(), norm);
            }

            ContactoEmergencia ce = getPrimero(p.getContactosEmergencia());
            if (ce != null) {
                addParagraph(doc, "", norm);
                addParagraph(doc, "=== Contacto Emergencia ===", title);
                addParagraph(doc, "Nombre: " + ce.getNombreContactoEmergencia(), norm);
                addParagraph(doc, "Parentesco: " + ce.getParentesco(), norm);
                addParagraph(doc, "Teléfono: " + ce.getTelefonoContactoEmergencia(), norm);
            }

            Formacion f = getPrimero(p.getFormaciones());
            if (f != null) {
                addParagraph(doc, "", norm);
                addParagraph(doc, "=== Formación ===", title);
                addParagraph(doc, "Formación Acad.: " + f.getFormacionAcademica(), norm);
                addParagraph(doc, "Grado: " + f.getGrado(), norm);
                addParagraph(doc, "Título: " + f.getTitulo(), norm);
            }

            RiesgoProcedencia rp = getPrimero(p.getRiesgoProcedencias());
            if (rp != null) {
                addParagraph(doc, "", norm);
                addParagraph(doc, "=== Riesgo & Procedencia ===", title);
                addParagraph(doc, "Riesgo: " + rp.getRiesgo(), norm);
                addParagraph(doc, "Medio transporte: " + rp.getMedioTransporte(), norm);
                addParagraph(doc, "Procedencia Trabajador: " + rp.getProcedenciaTrabajador(), norm);
            }

            Salud s = getPrimero(p.getRegistrosSalud());
            if (s != null) {
                addParagraph(doc, "", norm);
                addParagraph(doc, "=== Salud ===", title);
                addParagraph(doc, "Dotación: " + s.getDotacion(), norm);
                addParagraph(doc, "ARL: " + s.getArl(), norm);
                addParagraph(doc, "EPS: " + s.getEps(), norm);
                addParagraph(doc, "AFP: " + s.getAfp(), norm);
                addParagraph(doc, "CCF: " + s.getCcf(), norm);
                addParagraph(doc, "RH: " + s.getRh(), norm);
                addParagraph(doc, "Carnet Vac.: " + s.getCarnetVacunacion(), norm);
            }

            List<String> enfermedades = p.getEnfermedades().stream().map(Enfermedad::getNombre).toList();
            List<String> alergias = p.getAlergias().stream().map(Alergia::getNombre).toList();
            List<String> medicamentos = p.getEnfermedades().stream()
                    .flatMap(e -> e.getMedicamentos().stream())
                    .map(Medicamento::getNombre)
                    .distinct()
                    .toList();

            addParagraph(doc, "", norm);
            addParagraph(doc, "=== Condiciones Médicas ===", title);
            addParagraph(doc, "Enfermedades: " + String.join(", ", enfermedades), norm);
            addParagraph(doc, "Alergias: " + String.join(", ", alergias), norm);
            addParagraph(doc, "Medicamentos: " + String.join(", ", medicamentos), norm);

            doc.close();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }

        Resource res = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=persona_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(out.size())
                .body(res);
    }

    private void addParagraph(Document doc, String text, Font font) throws Exception {
        doc.add(new Paragraph(text, font));
    }

    @GetMapping("/{id}/word")
    public ResponseEntity<Resource> descargarWord(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setBold(true);
            run.setFontSize(16);
            run.setText("Datos de la Persona");

            addRun(doc, "Nombre: " + p.getNombres() + " " + p.getApellidos());
            addRun(doc, "Cédula: " + p.getCedula());
            addRun(doc, "Dirección: " + p.getDireccion());
            addRun(doc, "Correo: " + p.getCorreoInstitucional());
            addRun(doc, "Teléfono: " + p.getTelefonoInstitucional());

            doc.write(out);
            Resource res = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=persona_" + id + ".docx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .contentLength(out.size())
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private void addRun(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(text);
    }

    @GetMapping("/{id}/excel")
    public ResponseEntity<Resource> descargarExcel(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Persona");
            int rowIdx = 0;

            String[][] datos = {
                    {"Nombre", p.getNombres() + " " + p.getApellidos()},
                    {"Cédula", p.getCedula()},
                    {"Dirección", p.getDireccion()},
                    {"Correo", p.getCorreoInstitucional()},
                    {"Teléfono", p.getTelefonoInstitucional()},
                    {"Sexo", p.getSexo()},
                    {"Enlace SIGEP", p.getEnlaceSigep()}
            };

            for (String[] fila : datos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(fila[0]);
                row.createCell(1).setCellValue(fila[1]);
            }

            workbook.write(out);
            Resource res = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=persona_" + id + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(out.size())
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
