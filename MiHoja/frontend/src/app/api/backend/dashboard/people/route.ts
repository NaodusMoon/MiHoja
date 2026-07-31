import { NextRequest, NextResponse } from "next/server";

import { deletePeople, getDashboardPeopleData } from "@/lib/people-service";

export async function GET(request: NextRequest) {
  try {
    return NextResponse.json(await getDashboardPeopleData(request.nextUrl.searchParams));
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudieron cargar las personas." },
      { status: 503 }
    );
  }
}

export async function DELETE(request: NextRequest) {
  try {
    const body = (await request.json()) as { ids?: number[] };
    const ids = Array.isArray(body.ids)
      ? Array.from(new Set(body.ids.map(Number).filter((id) => Number.isInteger(id) && id > 0)))
      : [];
    if (ids.length === 0) {
      return NextResponse.json({ message: "No se recibieron registros válidos para eliminar." }, { status: 400 });
    }
    await deletePeople(ids);
    return NextResponse.json({
      message: `${ids.length} registro(s) eliminado(s).`,
      deletedCount: ids.length,
      failedIds: []
    });
  } catch (error) {
    console.error("[dashboard/people DELETE] failed", {
      message: error instanceof Error ? error.message : String(error),
      stack: error instanceof Error ? error.stack : undefined
    });
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudieron eliminar los registros." },
      { status: 500 }
    );
  }
}
