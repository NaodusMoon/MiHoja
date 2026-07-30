"use client";

import { useState } from "react";
import { Save } from "lucide-react";
import { useRouter } from "next/navigation";

import type { CustomField } from "@/lib/people-service";

export type FormValues = {
  nombres: string;
  apellidos: string;
  cedula: string;
  correoInstitucional: string;
  telefonoInstitucional: string;
  lugarExpedicion: string;
  fechaNacimiento: string;
  direccion: string;
  sexo: string;
  estado: string;
  enlaceSigep: string;
  numeroHijos: string;
  numero: string;
  imagenUrl: string;
  cargo: string;
  codigoCargo: string;
  dependencia: string;
  fechaIngreso: string;
  fechaFirmaContrato: string;
  mesesExperiencia: string;
  induccion: string;
  examenIngreso: string;
  fechaEgreso: string;
  formacionAcademica: string;
  grado: string;
  titulo: string;
  riesgo: string;
  medioTransporte: string;
  procedenciaTrabajador: string;
  dotacion: string;
  arl: string;
  eps: string;
  afp: string;
  ccf: string;
  rh: string;
  carnetVacunacion: string;
  nombreEmergencia: string;
  parentesco: string;
  telefonoEmergencia: string;
  alergias: string;
  enfermedades: string;
  medicamentos: string;
};

const emptyValues: FormValues = {
  nombres: "",
  apellidos: "",
  cedula: "",
  correoInstitucional: "",
  telefonoInstitucional: "",
  lugarExpedicion: "",
  fechaNacimiento: "",
  direccion: "",
  sexo: "",
  estado: "ACTIVO",
  enlaceSigep: "",
  numeroHijos: "0",
  numero: "",
  imagenUrl: "",
  cargo: "",
  codigoCargo: "",
  dependencia: "",
  fechaIngreso: "",
  fechaFirmaContrato: "",
  mesesExperiencia: "",
  induccion: "",
  examenIngreso: "",
  fechaEgreso: "",
  formacionAcademica: "",
  grado: "",
  titulo: "",
  riesgo: "",
  medioTransporte: "",
  procedenciaTrabajador: "",
  dotacion: "",
  arl: "",
  eps: "",
  afp: "",
  ccf: "",
  rh: "",
  carnetVacunacion: "",
  nombreEmergencia: "",
  parentesco: "",
  telefonoEmergencia: "",
  alergias: "",
  enfermedades: "",
  medicamentos: ""
};

function BooleanSelect({
  label,
  value,
  onChange
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <label>
      <span>{label}</span>
      <select value={value} onChange={(event) => onChange(event.target.value)}>
        <option value="">Sin especificar</option>
        <option value="true">Si</option>
        <option value="false">No</option>
      </select>
    </label>
  );
}

export function PersonForm({
  personId,
  initialValues,
  customFields = [],
  initialCustomFields = {}
}: {
  personId?: number;
  initialValues?: Partial<FormValues>;
  customFields?: CustomField[];
  initialCustomFields?: Record<string, string>;
}) {
  const router = useRouter();
  const [values, setValues] = useState<FormValues>({ ...emptyValues, ...initialValues });
  const [customValues, setCustomValues] = useState<Record<string, string>>(initialCustomFields);
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const update = (key: keyof FormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }));
  };

  const submit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setMessage(null);

    const response = await fetch(personId ? `/api/people/${personId}` : "/api/people", {
      method: personId ? "PATCH" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...values, customFields: customValues })
    });
    const result = (await response.json()) as { message?: string; person?: { n?: number } };

    setSaving(false);
    setMessage(result.message ?? (response.ok ? "Registro guardado." : "No se pudo guardar."));
    if (response.ok) {
      const id = personId ?? result.person?.n;
      router.push(id ? `/muestra/${id}` : "/");
      router.refresh();
    }
  };

  return (
    <form className="dataForm" onSubmit={submit}>
      <fieldset className="formGroup">
        <legend>Datos personales</legend>
        <div className="formGrid">
          <label>
            <span>Numero</span>
            <input min="1" type="number" value={values.numero} onChange={(event) => update("numero", event.target.value)} />
          </label>
          <label>
            <span>Nombres</span>
            <input required value={values.nombres} onChange={(event) => update("nombres", event.target.value)} />
          </label>
          <label>
            <span>Apellidos</span>
            <input required value={values.apellidos} onChange={(event) => update("apellidos", event.target.value)} />
          </label>
          <label>
            <span>Cedula</span>
            <input required value={values.cedula} onChange={(event) => update("cedula", event.target.value)} />
          </label>
          <label>
            <span>Lugar de expedicion</span>
            <input value={values.lugarExpedicion} onChange={(event) => update("lugarExpedicion", event.target.value)} />
          </label>
          <label>
            <span>Fecha de nacimiento</span>
            <input type="date" value={values.fechaNacimiento} onChange={(event) => update("fechaNacimiento", event.target.value)} />
          </label>
          <label>
            <span>Direccion</span>
            <input value={values.direccion} onChange={(event) => update("direccion", event.target.value)} />
          </label>
          <label>
            <span>Sexo</span>
            <select value={values.sexo} onChange={(event) => update("sexo", event.target.value)}>
              <option value="">Seleccionar</option>
              <option value="FEMENINO">Femenino</option>
              <option value="MASCULINO">Masculino</option>
              <option value="OTRO">Otro</option>
            </select>
          </label>
          <label>
            <span>Estado</span>
            <select value={values.estado} onChange={(event) => update("estado", event.target.value)}>
              <option value="ACTIVO">Activo</option>
              <option value="INACTIVO">Inactivo</option>
            </select>
          </label>
          <label>
            <span>Numero de hijos</span>
            <input min="0" type="number" value={values.numeroHijos} onChange={(event) => update("numeroHijos", event.target.value)} />
          </label>
          <label>
            <span>Correo institucional</span>
            <input type="email" value={values.correoInstitucional} onChange={(event) => update("correoInstitucional", event.target.value)} />
          </label>
          <label>
            <span>Telefono institucional</span>
            <input value={values.telefonoInstitucional} onChange={(event) => update("telefonoInstitucional", event.target.value)} />
          </label>
          <label>
            <span>Enlace SIGEP</span>
            <input type="url" value={values.enlaceSigep} onChange={(event) => update("enlaceSigep", event.target.value)} />
          </label>
          <label>
            <span>URL de imagen</span>
            <input type="url" value={values.imagenUrl} onChange={(event) => update("imagenUrl", event.target.value)} />
          </label>
        </div>
      </fieldset>

      <fieldset className="formGroup">
        <legend>Cargo e ingreso</legend>
        <div className="formGrid">
          <label><span>Cargo</span><input value={values.cargo} onChange={(event) => update("cargo", event.target.value)} /></label>
          <label><span>Codigo del cargo</span><input value={values.codigoCargo} onChange={(event) => update("codigoCargo", event.target.value)} /></label>
          <label><span>Dependencia</span><input value={values.dependencia} onChange={(event) => update("dependencia", event.target.value)} /></label>
          <label><span>Fecha de ingreso</span><input type="date" value={values.fechaIngreso} onChange={(event) => update("fechaIngreso", event.target.value)} /></label>
          <label><span>Fecha de firma del contrato</span><input type="date" value={values.fechaFirmaContrato} onChange={(event) => update("fechaFirmaContrato", event.target.value)} /></label>
          <label><span>Meses de experiencia</span><input min="0" type="number" value={values.mesesExperiencia} onChange={(event) => update("mesesExperiencia", event.target.value)} /></label>
          <BooleanSelect label="Induccion realizada" value={values.induccion} onChange={(value) => update("induccion", value)} />
          <BooleanSelect label="Examen de ingreso" value={values.examenIngreso} onChange={(value) => update("examenIngreso", value)} />
          <label><span>Fecha de egreso</span><input type="date" value={values.fechaEgreso} onChange={(event) => update("fechaEgreso", event.target.value)} /></label>
        </div>
      </fieldset>

      <fieldset className="formGroup">
        <legend>Formacion</legend>
        <div className="formGrid">
          <label><span>Formacion academica</span><input value={values.formacionAcademica} onChange={(event) => update("formacionAcademica", event.target.value)} /></label>
          <label><span>Grado</span><input value={values.grado} onChange={(event) => update("grado", event.target.value)} /></label>
          <label><span>Titulo</span><input value={values.titulo} onChange={(event) => update("titulo", event.target.value)} /></label>
        </div>
      </fieldset>

      <fieldset className="formGroup">
        <legend>Salud</legend>
        <div className="formGrid">
          <label><span>Dotacion</span><input value={values.dotacion} onChange={(event) => update("dotacion", event.target.value)} /></label>
          <label><span>ARL</span><input value={values.arl} onChange={(event) => update("arl", event.target.value)} /></label>
          <label><span>EPS</span><input value={values.eps} onChange={(event) => update("eps", event.target.value)} /></label>
          <label><span>AFP</span><input value={values.afp} onChange={(event) => update("afp", event.target.value)} /></label>
          <label><span>Caja de compensacion</span><input value={values.ccf} onChange={(event) => update("ccf", event.target.value)} /></label>
          <label><span>RH</span><input value={values.rh} onChange={(event) => update("rh", event.target.value)} /></label>
          <BooleanSelect label="Carnet de vacunacion" value={values.carnetVacunacion} onChange={(value) => update("carnetVacunacion", value)} />
          <label className="formWide"><span>Alergias</span><textarea placeholder="Separadas por comas" value={values.alergias} onChange={(event) => update("alergias", event.target.value)} /></label>
          <label className="formWide"><span>Enfermedades</span><textarea placeholder="Separadas por comas" value={values.enfermedades} onChange={(event) => update("enfermedades", event.target.value)} /></label>
          <label className="formWide"><span>Medicamentos</span><textarea placeholder="Separados por comas" value={values.medicamentos} onChange={(event) => update("medicamentos", event.target.value)} /></label>
        </div>
      </fieldset>

      <fieldset className="formGroup">
        <legend>Contacto y procedencia</legend>
        <div className="formGrid">
          <label><span>Contacto de emergencia</span><input value={values.nombreEmergencia} onChange={(event) => update("nombreEmergencia", event.target.value)} /></label>
          <label><span>Parentesco</span><input value={values.parentesco} onChange={(event) => update("parentesco", event.target.value)} /></label>
          <label><span>Telefono de emergencia</span><input value={values.telefonoEmergencia} onChange={(event) => update("telefonoEmergencia", event.target.value)} /></label>
          <label><span>Riesgo</span><input value={values.riesgo} onChange={(event) => update("riesgo", event.target.value)} /></label>
          <label><span>Medio de transporte</span><input value={values.medioTransporte} onChange={(event) => update("medioTransporte", event.target.value)} /></label>
          <label><span>Procedencia del trabajador</span><input value={values.procedenciaTrabajador} onChange={(event) => update("procedenciaTrabajador", event.target.value)} /></label>
        </div>
      </fieldset>

      {customFields.length > 0 ? (
        <fieldset className="formGroup">
          <legend>Campos personalizados</legend>
          <div className="formGrid">
            {customFields.map((field) => (
              <label key={field.id_campo}>
                <span>{field.nombre.replaceAll("_", " ")}</span>
                <input
                  value={customValues[String(field.id_campo)] ?? ""}
                  onChange={(event) =>
                    setCustomValues((current) => ({
                      ...current,
                      [String(field.id_campo)]: event.target.value
                    }))
                  }
                />
              </label>
            ))}
          </div>
        </fieldset>
      ) : null}

      <div className="formFooter">
        {message ? <p className="formMessage">{message}</p> : <span />}
        <button className="primaryAction" disabled={saving} type="submit">
          <Save aria-hidden="true" />
          <span>{saving ? "Guardando..." : "Guardar registro completo"}</span>
        </button>
      </div>
    </form>
  );
}
