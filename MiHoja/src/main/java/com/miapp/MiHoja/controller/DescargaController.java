package com.miapp.MiHoja.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.miapp.MiHoja.model.*;
import com.miapp.MiHoja.repository.PersonaRepository;
import com.miapp.MiHoja.service.CampoPersonalizadoService;
import com.miapp.MiHoja.service.view.PersonaDetalleView;
import com.miapp.MiHoja.service.view.PersonaViewService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/descargar")
public class DescargaController {

    private static final DateTimeFormatter CO_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private CampoPersonalizadoService campoPersonalizadoService;

    @Autowired
    private PersonaViewService personaViewService;

    private String txt(String value) {
        if (value == null || value.trim().isEmpty()) return "NO DISPONIBLE";
        return value.trim();
    }

    private String txtDate(LocalDate date) {
        return date == null ? "NO DISPONIBLE" : date.format(CO_DATE);
    }

    private String txtBool(Boolean value) {
        if (value == null) return "NO DISPONIBLE";
        return value ? "SI" : "NO";
    }

    private List<String[]> buildRows(Persona p) {
        List<String[]> rows = new ArrayList<>();
        PersonaDetalleView detalle = personaViewService.construirDetalle(p);

        Formacion f = detalle.formacion();
        PersonaCargoLaboral pcl = detalle.personaCargoLaboral();
        CargoLaboral cl = detalle.cargoLaboral();
        InduccionExamen ie = detalle.induccionExamen();
        RiesgoProcedencia rp = detalle.riesgoProcedencia();
        Salud s = detalle.salud();
        ContactoEmergencia ce = detalle.contactoEmergencia();

        String enfermedades = detalle.enfermedades().stream().map(this::txt).collect(Collectors.joining(", "));
        if (enfermedades.isBlank()) enfermedades = "NO DISPONIBLE";

        String alergias = detalle.alergias().stream().map(this::txt).collect(Collectors.joining(", "));
        if (alergias.isBlank()) alergias = "NO DISPONIBLE";

        String medicamentos = detalle.medicamentos().stream().map(this::txt).distinct().collect(Collectors.joining(", "));
        if (medicamentos.isBlank()) medicamentos = "NO DISPONIBLE";

        rows.add(new String[]{"ID", p.getId() == null ? "NO DISPONIBLE" : String.valueOf(p.getId())});
        rows.add(new String[]{"N", p.getNumero() == null ? "NO DISPONIBLE" : String.valueOf(p.getNumero())});
        rows.add(new String[]{"NOMBRES", txt(p.getNombres())});
        rows.add(new String[]{"APELLIDOS", txt(p.getApellidos())});
        rows.add(new String[]{"CEDULA", txt(p.getCedula())});
        rows.add(new String[]{"LUGAR EXPEDICION", txt(p.getLugarExpedicion())});
        rows.add(new String[]{"FECHA NACIMIENTO", txtDate(p.getFechaNacimiento())});
        rows.add(new String[]{"DIRECCION", txt(p.getDireccion())});
        rows.add(new String[]{"SEXO", txt(p.getSexo())});
        rows.add(new String[]{"CORREO", txt(p.getCorreoInstitucional())});
        rows.add(new String[]{"TELEFONO", txt(p.getTelefonoInstitucional())});
        rows.add(new String[]{"ENLACE SIGEP", txt(p.getEnlaceSigep())});
        rows.add(new String[]{"ESTADO", txt(p.getEstado())});
        rows.add(new String[]{"NUMERO HIJOS", p.getNumeroHijos() == null ? "NO DISPONIBLE" : String.valueOf(p.getNumeroHijos())});

        rows.add(new String[]{"FORMACION", f == null ? "NO DISPONIBLE" : txt(f.getFormacionAcademica())});
        rows.add(new String[]{"GRADO", f == null ? "NO DISPONIBLE" : txt(f.getGrado())});
        rows.add(new String[]{"TITULO", f == null ? "NO DISPONIBLE" : txt(f.getTitulo())});

        rows.add(new String[]{"CARGO", cl == null ? "NO DISPONIBLE" : txt(cl.getCargo())});
        rows.add(new String[]{"CODIGO", cl == null ? "NO DISPONIBLE" : txt(cl.getCodigo())});
        rows.add(new String[]{"DEPENDENCIA", cl == null ? "NO DISPONIBLE" : txt(cl.getDependencia())});
        rows.add(new String[]{"FECHA INGRESO", pcl == null ? "NO DISPONIBLE" : txtDate(pcl.getFechaIngreso())});
        rows.add(new String[]{"FECHA FIRMA CONTRATO", pcl == null ? "NO DISPONIBLE" : txtDate(pcl.getFechaFirmaContrato())});
        rows.add(new String[]{"MESES EXPERIENCIA", (pcl == null || pcl.getMesesExperiencia() == null) ? "NO DISPONIBLE" : String.valueOf(pcl.getMesesExperiencia())});

        rows.add(new String[]{"INDUCCION", ie == null ? "NO DISPONIBLE" : txtBool(ie.getInduccion())});
        rows.add(new String[]{"EXAMEN INGRESO", ie == null ? "NO DISPONIBLE" : txtBool(ie.getExamenIngreso())});
        rows.add(new String[]{"FECHA EGRESO", ie == null ? "NO DISPONIBLE" : txtDate(ie.getFechaEgreso())});

        rows.add(new String[]{"RIESGO", rp == null ? "NO DISPONIBLE" : txt(rp.getRiesgo())});
        rows.add(new String[]{"MEDIO TRANSPORTE", rp == null ? "NO DISPONIBLE" : txt(rp.getMedioTransporte())});
        rows.add(new String[]{"PROCEDENCIA", rp == null ? "NO DISPONIBLE" : txt(rp.getProcedenciaTrabajador())});

        rows.add(new String[]{"DOTACION", s == null ? "NO DISPONIBLE" : txt(s.getDotacion())});
        rows.add(new String[]{"ARL", s == null ? "NO DISPONIBLE" : txt(s.getArl())});
        rows.add(new String[]{"EPS", s == null ? "NO DISPONIBLE" : txt(s.getEps())});
        rows.add(new String[]{"AFP", s == null ? "NO DISPONIBLE" : txt(s.getAfp())});
        rows.add(new String[]{"CCF", s == null ? "NO DISPONIBLE" : txt(s.getCcf())});
        rows.add(new String[]{"RH", s == null ? "NO DISPONIBLE" : txt(s.getRh())});
        rows.add(new String[]{"CARNET VACUNACION", s == null ? "NO DISPONIBLE" : txtBool(s.getCarnetVacunacion())});

        rows.add(new String[]{"CONTACTO EMERGENCIA", ce == null ? "NO DISPONIBLE" : txt(ce.getNombreContactoEmergencia())});
        rows.add(new String[]{"PARENTESCO", ce == null ? "NO DISPONIBLE" : txt(ce.getParentesco())});
        rows.add(new String[]{"TELEFONO EMERGENCIA", ce == null ? "NO DISPONIBLE" : txt(ce.getTelefonoContactoEmergencia())});

        rows.add(new String[]{"ENFERMEDADES", enfermedades});
        rows.add(new String[]{"ALERGIAS", alergias});
        rows.add(new String[]{"MEDICAMENTOS", medicamentos});

        Map<String, String> custom = campoPersonalizadoService.mapaValoresPorNombre(p.getId());
        for (Map.Entry<String, String> entry : custom.entrySet()) {
            rows.add(new String[]{"CUSTOM - " + entry.getKey().toUpperCase(), txt(entry.getValue())});
        }

        return rows;
    }

    private ResponseEntity<Resource> toPdf(List<Persona> personas, String filename) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = new Font(Font.HELVETICA, 15, Font.BOLD);
            Font subtitle = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font text = new Font(Font.HELVETICA, 11);

            doc.add(new Paragraph("Registros de MiHoja", title));
            doc.add(new Paragraph(" "));

            for (Persona p : personas) {
                String numero = p.getNumero() == null ? "NO DISPONIBLE" : String.valueOf(p.getNumero());
                doc.add(new Paragraph("Persona N " + numero + " - " + txt(p.getNombres()) + " " + txt(p.getApellidos()), subtitle));
                for (String[] row : buildRows(p)) {
                    doc.add(new Paragraph(row[0] + ": " + row[1], text));
                }
                doc.add(new Paragraph(" "));
            }

            doc.close();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }

        Resource res = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(out.size())
                .body(res);
    }

    private ResponseEntity<Resource> toWord(List<Persona> personas, String filename) {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph title = doc.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText("Registros de MiHoja");

            for (Persona p : personas) {
                XWPFParagraph personTitle = doc.createParagraph();
                XWPFRun personRun = personTitle.createRun();
                personRun.setBold(true);
                String numero = p.getNumero() == null ? "NO DISPONIBLE" : String.valueOf(p.getNumero());
                personRun.setText("Persona N " + numero + " - " + txt(p.getNombres()) + " " + txt(p.getApellidos()));

                for (String[] row : buildRows(p)) {
                    XWPFParagraph para = doc.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setText(row[0] + ": " + row[1]);
                }
            }

            doc.write(out);
            Resource res = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .contentLength(out.size())
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<Resource> toExcel(List<Persona> personas, String filename) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Registros");
            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("N");
            header.createCell(1).setCellValue("Campo");
            header.createCell(2).setCellValue("Valor");

            for (Persona p : personas) {
                for (String[] data : buildRows(p)) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(p.getNumero() == null ? "NO DISPONIBLE" : String.valueOf(p.getNumero()));
                    row.createCell(1).setCellValue(data[0]);
                    row.createCell(2).setCellValue(data[1]);
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            workbook.write(out);

            Resource res = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(out.size())
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> descargarPDF(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        return toPdf(List.of(p), "persona_" + id + ".pdf");
    }

    @GetMapping("/{id}/word")
    public ResponseEntity<Resource> descargarWord(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        return toWord(List.of(p), "persona_" + id + ".docx");
    }

    @GetMapping("/{id}/excel")
    public ResponseEntity<Resource> descargarExcel(@PathVariable Long id) {
        Persona p = personaRepository.findByIdWithAllRelations(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        return toExcel(List.of(p), "persona_" + id + ".xlsx");
    }

    @GetMapping("/todos/pdf")
    public ResponseEntity<Resource> descargarTodosPdf() {
        List<Persona> personas = personaRepository.findAllWithAllRelations();
        return toPdf(personas, "mihoja_todos.pdf");
    }

    @GetMapping("/todos/word")
    public ResponseEntity<Resource> descargarTodosWord() {
        List<Persona> personas = personaRepository.findAllWithAllRelations();
        return toWord(personas, "mihoja_todos.docx");
    }

    @GetMapping("/todos/excel")
    public ResponseEntity<Resource> descargarTodosExcel() {
        List<Persona> personas = personaRepository.findAllWithAllRelations();
        return toExcel(personas, "mihoja_todos.xlsx");
    }
}
