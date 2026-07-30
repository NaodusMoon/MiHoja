import { NextResponse } from "next/server";

import { getDashboardOverviewData } from "@/lib/people-service";

export async function GET() {
  try {
    return NextResponse.json(await getDashboardOverviewData());
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo cargar el resumen." },
      { status: 503 }
    );
  }
}
