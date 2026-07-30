import { NextRequest, NextResponse } from "next/server";

import {
  getPersonById,
  saveCompletePerson,
  type CompletePersonInput
} from "@/lib/people-service";

export async function GET(_: NextRequest, context: { params: Promise<{ id: string }> }) {
  const { id } = await context.params;
  const person = await getPersonById(Number(id));
  return person
    ? NextResponse.json(person)
    : NextResponse.json({ message: "Registro no encontrado." }, { status: 404 });
}

export async function PATCH(request: NextRequest, context: { params: Promise<{ id: string }> }) {
  try {
    const { id } = await context.params;
    const body = (await request.json()) as CompletePersonInput;
    const person = await saveCompletePerson(Number(id), body);
    return person
      ? NextResponse.json({ message: "Registro actualizado.", person })
      : NextResponse.json({ message: "Registro no encontrado." }, { status: 404 });
  } catch (error) {
    return NextResponse.json(
      { message: error instanceof Error ? error.message : "No se pudo actualizar el registro." },
      { status: 500 }
    );
  }
}
