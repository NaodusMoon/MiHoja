import { NextRequest, NextResponse } from "next/server";

import { saveCompletePerson, type CompletePersonInput } from "@/lib/people-service";

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as CompletePersonInput;
    if (!body.cedula?.trim() || !body.nombres?.trim() || !body.apellidos?.trim()) {
      return NextResponse.json({ message: "La cedula es obligatoria." }, { status: 400 });
    }

    const person = await saveCompletePerson(undefined, body);

    return NextResponse.json({ message: "Registro guardado.", person }, { status: 201 });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo guardar el registro." },
      { status: 500 }
    );
  }
}
