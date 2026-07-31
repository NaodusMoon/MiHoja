import { notFound } from "next/navigation";

import { PersonForm } from "@/components/person-form";
import { SimpleAppShell } from "@/components/simple-app-shell";
import { getCustomFields, getPersonById } from "@/lib/people-service";

export default async function EditPersonPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const [person, customFields] = await Promise.all([
    getPersonById(Number(id)),
    getCustomFields()
  ]);
  if (!person) notFound();
  const job = person.persona_cargo_laboral?.[0];
  const cargo = job?.cargo_laboral;
  const induction = job?.induccion_examen?.[0];
  const formation = person.formacion?.[0];
  const risk = person.riesgo_procedencia?.[0];
  const health = person.salud?.[0];
  const contact = person.contacto_emergencia?.[0];
  const booleanValue = (value: boolean | null | undefined) =>
    value === true ? "true" : value === false ? "false" : "";
  const initialCustomFields = Object.fromEntries(
    (person.persona_campo_valor ?? []).map((value) => [String(value.campo_id), value.valor])
  );

  return (
    <SimpleAppShell
      active="/"
      description={`Actualiza el registro ficticio #${person.numero ?? person.n}.`}
      title="Editar persona"
    >
      <section className="formSection">
        <PersonForm
          customFields={customFields}
          initialCustomFields={initialCustomFields}
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
            numeroHijos: String(person.numero_hijos ?? 0),
            numero: person.numero === null ? "" : String(person.numero),
            imagenUrl: person.imagen_url ?? "",
            cargo: cargo?.cargo ?? "",
            codigoCargo: cargo?.codigo ?? "",
            dependencia: cargo?.dependencia ?? "",
            fechaIngreso: job?.fecha_ingreso ?? "",
            fechaFirmaContrato: job?.fecha_firma_contrato ?? "",
            mesesExperiencia:
              job?.meses_experiencia === null || job?.meses_experiencia === undefined
                ? ""
                : String(job.meses_experiencia),
            induccion: booleanValue(induction?.induccion),
            examenIngreso: booleanValue(induction?.examen_ingreso),
            fechaEgreso: induction?.fecha_egreso ?? "",
            formacionAcademica: formation?.formacion_academica ?? "",
            grado: formation?.grado ?? "",
            titulo: formation?.titulo ?? "",
            riesgo: risk?.riesgo ?? "",
            medioTransporte: risk?.medio_transporte ?? "",
            procedenciaTrabajador: risk?.procedencia_trabajador ?? "",
            dotacion: health?.dotacion ?? "",
            arl: health?.arl ?? "",
            eps: health?.eps ?? "",
            afp: health?.afp ?? "",
            ccf: health?.ccf ?? "",
            rh: health?.rh ?? "",
            carnetVacunacion: booleanValue(health?.carnet_vacunacion),
            nombreEmergencia: contact?.nombre_contacto_emergencia ?? "",
            parentesco: contact?.parentesco ?? "",
            telefonoEmergencia: contact?.telefono_contacto_emergencia ?? "",
            alergias: (person.alergia ?? []).map((item) => item.nombre).join(", "),
            enfermedades: (person.enfermedad ?? []).map((item) => item.nombre).join(", "),
            medicamentos: (person.medicamento ?? []).map((item) => item.nombre).join(", ")
          }}
        />
      </section>
    </SimpleAppShell>
  );
}
