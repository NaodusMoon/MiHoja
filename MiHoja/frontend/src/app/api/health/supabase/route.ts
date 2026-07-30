import { NextResponse } from "next/server";

import { supabaseRest } from "@/lib/supabase-rest";

export async function GET() {
  try {
    const rows = await supabaseRest<Array<{ n: number }>>("persona?select=n&order=n.asc&limit=1");
    return NextResponse.json(
      { ok: true, database: "reachable", sampleRows: rows.length },
      { headers: { "Cache-Control": "no-store" } }
    );
  } catch (error) {
    return NextResponse.json(
      { ok: false, message: error instanceof Error ? error.message : "Database unavailable" },
      { status: 503, headers: { "Cache-Control": "no-store" } }
    );
  }
}
