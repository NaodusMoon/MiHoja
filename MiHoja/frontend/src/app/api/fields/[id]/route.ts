import { NextRequest, NextResponse } from "next/server";

import { encodeFilter, supabaseRest } from "@/lib/supabase-rest";

export async function PATCH(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  try {
    const { id } = await context.params;
    const body = (await request.json()) as { nombre?: string; activo?: boolean };
    const values: Record<string, unknown> = {};
    if (typeof body.activo === "boolean") values.activo = body.activo;
    if (body.nombre?.trim()) values.nombre = body.nombre.trim().toLowerCase().replace(/[^a-z0-9_]/g, "_");

    const fields = await supabaseRest<Array<Record<string, unknown>>>(
      `campo_personalizado?id_campo=eq.${encodeFilter(id)}`,
      {
        method: "PATCH",
        body: JSON.stringify(values),
        prefer: "return=representation"
      }
    );
    return NextResponse.json(fields[0]);
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo actualizar el campo." },
      { status: 500 }
    );
  }
}
