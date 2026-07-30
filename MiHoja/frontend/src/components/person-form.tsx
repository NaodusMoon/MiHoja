"use client";

import { useState } from "react";
import { Save } from "lucide-react";
import { useRouter } from "next/navigation";

type FormValues = {
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
  numeroHijos: "0"
};

export function PersonForm({
  personId,
  initialValues
}: {
  personId?: number;
  initialValues?: Partial<FormValues>;
}) {
  const router = useRouter();
  const [values, setValues] = useState<FormValues>({ ...emptyValues, ...initialValues });
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const update = (key: keyof FormValues, value: string) => {
    setValues((current) => ({ ...current, [key]: value }));
  };

  const submit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setMessage(null);

    const editPayload = {
      nombres: values.nombres.trim().toUpperCase(),
      apellidos: values.apellidos.trim().toUpperCase(),
      cedula: values.cedula.trim(),
      correo_institucional: values.correoInstitucional.trim().toLowerCase() || null,
      telefono_institucional: values.telefonoInstitucional.trim() || null,
      lugar_expedicion: values.lugarExpedicion.trim().toUpperCase() || null,
      fecha_nacimiento: values.fechaNacimiento || null,
      direccion: values.direccion.trim().toUpperCase() || null,
      sexo: values.sexo || null,
      estado: values.estado || "ACTIVO",
      enlace_sigep: values.enlaceSigep.trim() || null,
      numero_hijos: Number(values.numeroHijos) || 0
    };

    const response = await fetch(personId ? `/api/people/${personId}` : "/api/people", {
      method: personId ? "PATCH" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(personId ? editPayload : values)
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
      <div className="formGrid">
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
          <span>Correo institucional</span>
          <input
            type="email"
            value={values.correoInstitucional}
            onChange={(event) => update("correoInstitucional", event.target.value)}
          />
        </label>
        <label>
          <span>Telefono institucional</span>
          <input
            value={values.telefonoInstitucional}
            onChange={(event) => update("telefonoInstitucional", event.target.value)}
          />
        </label>
        <label>
          <span>Lugar de expedicion</span>
          <input
            value={values.lugarExpedicion}
            onChange={(event) => update("lugarExpedicion", event.target.value)}
          />
        </label>
        <label>
          <span>Fecha de nacimiento</span>
          <input
            type="date"
            value={values.fechaNacimiento}
            onChange={(event) => update("fechaNacimiento", event.target.value)}
          />
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
          <input
            min="0"
            type="number"
            value={values.numeroHijos}
            onChange={(event) => update("numeroHijos", event.target.value)}
          />
        </label>
        <label>
          <span>Enlace SIGEP</span>
          <input value={values.enlaceSigep} onChange={(event) => update("enlaceSigep", event.target.value)} />
        </label>
      </div>

      <div className="formFooter">
        {message ? <p className="formMessage">{message}</p> : <span />}
        <button className="primaryAction" disabled={saving} type="submit">
          <Save aria-hidden="true" />
          <span>{saving ? "Guardando..." : "Guardar registro"}</span>
        </button>
      </div>
    </form>
  );
}
