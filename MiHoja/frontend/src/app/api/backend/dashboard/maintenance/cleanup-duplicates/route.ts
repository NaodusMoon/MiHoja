import { NextResponse } from "next/server";

export async function POST() {
  return NextResponse.json({
    message: "Los datos de demostracion ya estan normalizados.",
    personasRevisadas: 124,
    alergiasEliminadas: 0,
    medicamentosEliminados: 0,
    enfermedadesEliminadas: 0
  });
}
