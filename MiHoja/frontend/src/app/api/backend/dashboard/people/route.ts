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
    const ids = Array.isArray(body.ids) ? body.ids : [];
    await deletePeople(ids);
    return NextResponse.json({
      message: `${ids.length} registro(s) eliminado(s).`,
      deletedCount: ids.length,
      failedIds: []
    });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudieron eliminar los registros." },
      { status: 500 }
    );
  }
}
