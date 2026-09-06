import Link from "next/link";
import { notFound } from "next/navigation";
import { Pencil } from "lucide-react";

import { SimpleAppShell } from "@/components/simple-app-shell";
import { getPersonById } from "@/lib/people-service";

function display(value: unknown) {
  if (value === null || value === undefined || value === "") return "Sin registrar";
  if (typeof value === "boolean") return value ? "SI" : "NO";
  return String(value);
}

export default async function PersonDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!/^\d+$/.test(id) || !Number.isSafeInteger(Number(id)) || Number(id) < 1) notFound();
  const person = await getPersonById(Number(id));
  if (!person) notFound();

  const cargo = person.persona_cargo_laboral?.[0];
  const formation = person.formacion?.[0];
  const health = person.salud?.[0];
  const contact = person.contacto_emergencia?.[0];
  const risk = person.riesgo_procedencia?.[0];

  const groups = [
    {
      title: "Datos personales",
      items: [
        ["Numero", person.numero],
        ["Cedula", person.cedula],
        ["Fecha de nacimiento", person.fecha_nacimiento],
        ["Sexo", person.sexo],
        ["Lugar de expedicion", person.lugar_expedicion],
        ["Direccion", person.direccion],
        ["Correo", person.correo_institucional],
        ["Telefono", person.telefono_institucional],
        ["Estado", person.estado]
        , ["Número de hijos", person.numero_hijos]
        , ["Enlace SIGEP", person.enlace_sigep]
      ]
    },
    {
      title: "Cargo y formacion",
      items: [
        ["Cargo", cargo?.cargo_laboral?.cargo],
        ["Codigo", cargo?.cargo_laboral?.codigo],
        ["Dependencia", cargo?.cargo_laboral?.dependencia],
        ["Fecha de ingreso", cargo?.fecha_ingreso],
        ["Firma del contrato", cargo?.fecha_firma_contrato],
        ["Inducción", cargo?.induccion_examen?.[0]?.induccion],
        ["Examen de ingreso", cargo?.induccion_examen?.[0]?.examen_ingreso],
        ["Fecha de egreso", cargo?.induccion_examen?.[0]?.fecha_egreso],
        ["Meses de experiencia", cargo?.meses_experiencia],
        ["Formacion", formation?.formacion_academica],
        ["Grado", formation?.grado],
        ["Titulo", formation?.titulo]
      ]
    },
    {
      title: "Salud y contacto",
      items: [
        ["EPS", health?.eps],
        ["ARL", health?.arl],
        ["AFP", health?.afp],
        ["Caja de compensacion", health?.ccf],
        ["RH", health?.rh],
        ["Dotación", health?.dotacion],
        ["Carnet de vacunación", health?.carnet_vacunacion],
        ["Alergias", person.alergia?.map((item) => item.nombre).join(", ")],
        ["Enfermedades", person.enfermedad?.map((item) => item.nombre).join(", ")],
        ["Medicamentos", person.medicamento?.map((item) => item.nombre).join(", ")],
        ["Contacto de emergencia", contact?.nombre_contacto_emergencia],
        ["Parentesco", contact?.parentesco],
        ["Telefono de emergencia", contact?.telefono_contacto_emergencia],
        ["Riesgo", risk?.riesgo],
        ["Transporte", risk?.medio_transporte]
        , ["Procedencia", risk?.procedencia_trabajador]
      ]
    }
  ];

  if (person.persona_campo_valor?.length) groups.push({
    title: "Información adicional",
    items: person.persona_campo_valor.map((field) => [field.campo_personalizado?.nombre.replaceAll("_", " ") ?? `Campo ${field.campo_id}`, field.valor])
  });

  return (
    <SimpleAppShell
      active="/"
      actions={
        <Link className="primaryAction" href={`/editar/${person.n}`}>
          <Pencil aria-hidden="true" />
          <span>Editar</span>
        </Link>
      }
      description={`Ficha de personal · Registro #${person.numero ?? person.n}`}
      title={`${person.nombres ?? ""} ${person.apellidos ?? ""}`.trim()}
    >
      <div className="detailGroups">
        {groups.map((group) => (
          <section className="detailGroup" key={group.title}>
            <h2>{group.title}</h2>
            <dl>
              {group.items.map(([label, value]) => (
                <div key={String(label)}>
                  <dt>{label}</dt>
                  <dd>{display(value)}</dd>
                </div>
              ))}
            </dl>
          </section>
        ))}
      </div>
    </SimpleAppShell>
  );
}
