import { NextRequest, NextResponse } from "next/server";
import { readSheet } from "read-excel-file/node";

import { upsertPeople } from "@/lib/people-service";

function normalizeHeader(value: unknown) {
  return String(value ?? "")
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .replace(/[^a-zA-Z0-9]/g, "")
    .toLowerCase();
}

function asText(value: unknown) {
  if (value === null || value === undefined || value === "") return null;
  return String(value).trim();
}

function asDate(value: unknown) {
  if (value instanceof Date) return value.toISOString().slice(0, 10);
  const text = asText(value);
  return text && /^\d{4}-\d{2}-\d{2}$/.test(text) ? text : null;
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

    const people = rows
      .slice(1)
      .filter((row) => row.some((cell) => cell !== null && cell !== ""))
      .map((row) => ({
        numero: Number(value(row, "numero")) || null,
        nombres: asText(value(row, "nombres"))?.toUpperCase(),
        apellidos: asText(value(row, "apellidos"))?.toUpperCase(),
        cedula: asText(value(row, "cedula")),
        lugar_expedicion: asText(value(row, "lugarExpedicion"))?.toUpperCase(),
        fecha_nacimiento: asDate(value(row, "fechaNacimiento")),
        direccion: asText(value(row, "direccion"))?.toUpperCase(),
        sexo: asText(value(row, "sexo"))?.toUpperCase(),
        correo_institucional: asText(value(row, "correoInstitucional"))?.toLowerCase(),
        telefono_institucional: asText(value(row, "telefonoInstitucional")),
        enlace_sigep: asText(value(row, "enlaceSigep")),
        numero_hijos: Number(value(row, "numeroHijos")) || 0,
        imagen_url: null
      }))
      .filter((person) => person.cedula && person.nombres && person.apellidos);

    if (people.length === 0) {
      return NextResponse.json({ message: "No se encontraron filas validas." }, { status: 400 });
    }

    const saved = await upsertPeople(people);
    return NextResponse.json({
      message: `Importacion completada: ${saved.length} registro(s) creados o actualizados.`,
      processed: saved.length
    });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo procesar el archivo." },
      { status: 500 }
    );
  }
}
