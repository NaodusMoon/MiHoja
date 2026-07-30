import { notFound } from "next/navigation";

import { PersonForm } from "@/components/person-form";
import { SimpleAppShell } from "@/components/simple-app-shell";
import { getPersonById } from "@/lib/people-service";

export default async function EditPersonPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const person = await getPersonById(Number(id));
  if (!person) notFound();

  return (
    <SimpleAppShell
      active="/"
      description={`Actualiza el registro ficticio #${person.numero ?? person.n}.`}
      title="Editar persona"
    >
      <section className="formSection">
        <PersonForm
          personId={person.n}
          initialValues={{
            nombres: person.nombres ?? "",
            apellidos: person.apellidos ?? "",
            cedula: person.cedula ?? "",
            correoInstitucional: person.correo_institucional ?? "",
            telefonoInstitucional: person.telefono_institucional ?? "",
            lugarExpedicion: person.lugar_expedicion ?? "",
            fechaNacimiento: person.fecha_nacimiento ?? "",
            direccion: person.direccion ?? "",
            sexo: person.sexo ?? "",
            estado: person.estado ?? "ACTIVO",
            enlaceSigep: person.enlace_sigep ?? "",
            numeroHijos: String(person.numero_hijos ?? 0)
          }}
        />
      </section>
    </SimpleAppShell>
  );
}
