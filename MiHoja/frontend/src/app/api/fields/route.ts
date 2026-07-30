import { NextRequest, NextResponse } from "next/server";

import { supabaseRest } from "@/lib/supabase-rest";

type CustomField = {
  id_campo: number;
  nombre: string;
  activo: boolean;
  creado_en: string;
};

export async function GET() {
  try {
    const fields = await supabaseRest<CustomField[]>(
      "campo_personalizado?select=id_campo,nombre,activo,creado_en&order=id_campo.asc"
    );
    return NextResponse.json(fields);
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudieron cargar los campos." },
      { status: 503 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as { nombre?: string };
    const nombre = body.nombre?.trim().toLowerCase().replace(/[^a-z0-9_]/g, "_");
    if (!nombre) {
      return NextResponse.json({ message: "Escribe un nombre para el campo." }, { status: 400 });
    }

    const fields = await supabaseRest<CustomField[]>("campo_personalizado", {
      method: "POST",
      body: JSON.stringify({ nombre, activo: true, creado_en: new Date().toISOString() }),
      prefer: "return=representation"
    });
    return NextResponse.json(fields[0], { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo crear el campo." },
      { status: 500 }
    );
  }
}
