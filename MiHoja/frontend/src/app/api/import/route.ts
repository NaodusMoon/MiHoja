import { NextRequest, NextResponse } from "next/server";
import { readSheet } from "read-excel-file/node";

import { saveCompletePerson, type CompletePersonInput } from "@/lib/people-service";

function normalizeHeader(value: unknown) {
  return String(value ?? "")
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .replace(/[^a-zA-Z0-9]/g, "")
    .toLowerCase();
}

function asText(value: unknown) {
  if (value === null || value === undefined || value === "") return "";
  return String(value).trim();
}

function asNumberText(value: unknown) {
  const text = asText(value);
  if (!text) return "";
  const number = Number(text.replace(",", "."));
  return Number.isFinite(number) ? String(number) : "";
}

function asDateText(value: unknown) {
  if (value instanceof Date && !Number.isNaN(value.getTime())) {
    return value.toISOString().slice(0, 10);
  }

  const text = asText(value);
  if (/^\d{4}-\d{2}-\d{2}$/.test(text)) return text;

  const match = text.match(/^(\d{1,2})[/-](\d{1,2})[/-](\d{4})$/);
  if (!match) return "";
  return `${match[3]}-${match[2].padStart(2, "0")}-${match[1].padStart(2, "0")}`;
}

function asBooleanText(value: unknown) {
  if (value === true || value === 1) return "true";
  if (value === false || value === 0) return "false";

  const normalized = asText(value).toLowerCase();
  if (["true", "si", "sí", "yes", "1"].includes(normalized)) return "true";
  if (["false", "no", "0"].includes(normalized)) return "false";
  return "";
}

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData();
    const file = formData.get("file");
    if (!(file instanceof File) || file.size === 0) {
      return NextResponse.json({ message: "Selecciona un archivo Excel valido." }, { status: 400 });
    }
    if (file.size > 8 * 1024 * 1024) {
      return NextResponse.json({ message: "El archivo supera el limite de 8 MB." }, { status: 413 });
    }

    const rows = await readSheet(Buffer.from(await file.arrayBuffer()));
    if (rows.length < 2) {
      return NextResponse.json({ message: "El archivo no contiene filas para importar." }, { status: 400 });
    }

    const headers = rows[0].map(normalizeHeader);
    const index = (name: string) => headers.indexOf(normalizeHeader(name));
    const value = (row: unknown[], name: string) => {
      const column = index(name);
      return column >= 0 ? row[column] : null;
    };

    if (index("cedula") < 0 || index("nombres") < 0 || index("apellidos") < 0) {
      return NextResponse.json(
        { message: "El Excel debe incluir las columnas nombres, apellidos y cedula." },
        { status: 400 }
      );
    }

    const validRows = rows
      .slice(1)
      .map((row, rowIndex) => ({ row, rowIndex: rowIndex + 2 }))
      .filter(({ row }) => row.some((cell) => cell !== null && cell !== ""));

    let processed = 0;
    const failedRows: Array<{ row: number; message: string }> = [];

    for (const { row, rowIndex } of validRows) {
      const input: CompletePersonInput = {
        numero: asNumberText(value(row, "numero")),
        nombres: asText(value(row, "nombres")),
        apellidos: asText(value(row, "apellidos")),
        cedula: asText(value(row, "cedula")),
        lugarExpedicion: asText(value(row, "lugarExpedicion")),
        fechaNacimiento: asDateText(value(row, "fechaNacimiento")),
        direccion: asText(value(row, "direccion")),
        sexo: asText(value(row, "sexo")),
        correoInstitucional: asText(value(row, "correoInstitucional")),
        telefonoInstitucional: asText(value(row, "telefonoInstitucional")),
        enlaceSigep: asText(value(row, "enlaceSigep")),
        estado: asText(value(row, "estado")),
        numeroHijos: asNumberText(value(row, "numeroHijos")),
        cargo: asText(value(row, "cargo")),
        codigoCargo: asText(value(row, "codigo")),
        dependencia: asText(value(row, "dependencia")),
        fechaIngreso: asDateText(value(row, "fechaIngreso")),
        fechaFirmaContrato: asDateText(value(row, "fechaFirmaContrato")),
        mesesExperiencia: asNumberText(value(row, "mesesExperiencia")),
        induccion: asBooleanText(value(row, "induccion")),
        examenIngreso: asBooleanText(value(row, "examen")),
        fechaEgreso: asDateText(value(row, "fechaEgreso")),
        formacionAcademica: asText(value(row, "formacionAcademica")),
        grado: asText(value(row, "grado")),
        titulo: asText(value(row, "titulo")),
        riesgo: asText(value(row, "riesgo")),
        medioTransporte: asText(value(row, "medioTransporte")),
        procedenciaTrabajador: asText(value(row, "procedencia")),
        dotacion: asText(value(row, "dotacion")),
        arl: asText(value(row, "arl")),
        eps: asText(value(row, "eps")),
        afp: asText(value(row, "afp")),
        ccf: asText(value(row, "ccf")),
        rh: asText(value(row, "rh")),
        carnetVacunacion: asBooleanText(value(row, "carnetVacunacion")),
        nombreEmergencia: asText(value(row, "nombreEmergencia")),
        parentesco: asText(value(row, "parentesco")),
        telefonoEmergencia: asText(value(row, "telefonoEmergencia")),
        enfermedades: asText(value(row, "enfermedades")),
        alergias: asText(value(row, "alergias")),
        medicamentos: asText(value(row, "medicamentos"))
      };

      if (!input.cedula || !input.nombres || !input.apellidos) {
        failedRows.push({ row: rowIndex, message: "Faltan nombres, apellidos o cedula." });
        continue;
      }

      try {
        await saveCompletePerson(undefined, input);
        processed += 1;
      } catch (error) {
        failedRows.push({
          row: rowIndex,
          message: error instanceof Error ? error.message : "No se pudo guardar la fila."
        });
      }
    }

    if (processed === 0) {
      return NextResponse.json(
        { message: "No se pudo importar ninguna fila.", processed, failedRows },
        { status: 400 }
      );
    }

    return NextResponse.json({
      message: failedRows.length
        ? `Importacion parcial: ${processed} fila(s) procesadas y ${failedRows.length} con errores.`
        : `Importacion completada: ${processed} fila(s) procesadas.`,
      processed,
      failedRows
    });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo procesar el archivo." },
      { status: 500 }
    );
  }
}
