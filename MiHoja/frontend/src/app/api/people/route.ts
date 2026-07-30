import { NextRequest, NextResponse } from "next/server";

import { upsertPeople } from "@/lib/people-service";

function cleanText(value: unknown) {
  return typeof value === "string" && value.trim() ? value.trim().toUpperCase() : null;
}

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as Record<string, unknown>;
    const cedula = cleanText(body.cedula);
    if (!cedula) {
      return NextResponse.json({ message: "La cedula es obligatoria." }, { status: 400 });
    }

    const rows = await upsertPeople([
      {
        nombres: cleanText(body.nombres),
        apellidos: cleanText(body.apellidos),
        cedula,
        correo_institucional: cleanText(body.correoInstitucional)?.toLowerCase() ?? null,
        telefono_institucional: cleanText(body.telefonoInstitucional),
        lugar_expedicion: cleanText(body.lugarExpedicion),
        fecha_nacimiento: body.fechaNacimiento || null,
        direccion: cleanText(body.direccion),
        sexo: cleanText(body.sexo),
        estado: cleanText(body.estado) ?? "ACTIVO",
        enlace_sigep: typeof body.enlaceSigep === "string" ? body.enlaceSigep.trim() || null : null,
        numero_hijos: Number.isFinite(Number(body.numeroHijos)) ? Number(body.numeroHijos) : 0
      }
    ]);

    return NextResponse.json({ message: "Registro guardado.", person: rows[0] }, { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo guardar el registro." },
      { status: 500 }
    );
  }
}
