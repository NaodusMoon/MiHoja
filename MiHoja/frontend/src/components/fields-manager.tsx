"use client";

import { useEffect, useState } from "react";
import { Plus, RefreshCw } from "lucide-react";

type CustomField = {
  id_campo: number;
  nombre: string;
  activo: boolean;
  creado_en: string;
};

export function FieldsManager() {
  const [fields, setFields] = useState<CustomField[]>([]);
  const [name, setName] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    const response = await fetch("/api/fields", { cache: "no-store" });
    const result = await response.json();
    setFields(Array.isArray(result) ? result : []);
    setMessage(Array.isArray(result) ? null : result.message ?? "No se pudieron cargar los campos.");
    setLoading(false);
  };

  useEffect(() => {
    void load();
  }, []);

  const create = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const response = await fetch("/api/fields", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombre: name })
    });
    const result = (await response.json()) as CustomField & { message?: string };
    if (response.ok) {
      setFields((current) => [...current, result]);
      setName("");
      setMessage("Campo creado.");
    } else {
      setMessage(result.message ?? "No se pudo crear el campo.");
    }
  };

  const toggle = async (field: CustomField) => {
    const response = await fetch(`/api/fields/${field.id_campo}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ activo: !field.activo })
    });
    if (response.ok) {
      setFields((current) =>
        current.map((item) => (item.id_campo === field.id_campo ? { ...item, activo: !item.activo } : item))
      );
    }
  };

  return (
    <section className="fieldsWorkspace">
      <form className="fieldCreator" onSubmit={create}>
        <label>
          <span>Nombre del nuevo campo</span>
          <input
            onChange={(event) => setName(event.target.value)}
            placeholder="Ej. certificacion_adicional"
            required
            value={name}
          />
        </label>
        <button className="primaryAction" type="submit">
          <Plus aria-hidden="true" />
          <span>Agregar campo</span>
        </button>
      </form>

      <div className="fieldListHeader">
        <div>
          <h2>Campos personalizados</h2>
          <p>{fields.length} campo(s) configurado(s)</p>
        </div>
        <button aria-label="Actualizar campos" className="iconAction" onClick={load} type="button">
          <RefreshCw aria-hidden="true" />
        </button>
      </div>

      {loading ? <p className="inlineStatus">Cargando campos...</p> : null}
      {!loading && fields.length === 0 ? <p className="inlineStatus">No hay campos personalizados.</p> : null}

      <div className="fieldRows">
        {fields.map((field) => (
          <article className="fieldRow" key={field.id_campo}>
            <div>
              <strong>{field.nombre}</strong>
              <span>ID {field.id_campo}</span>
            </div>
            <label className="switchControl">
              <input checked={field.activo} onChange={() => toggle(field)} type="checkbox" />
              <span>{field.activo ? "Activo" : "Inactivo"}</span>
            </label>
          </article>
        ))}
      </div>
      {message ? <p className="formMessage">{message}</p> : null}
    </section>
  );
}
