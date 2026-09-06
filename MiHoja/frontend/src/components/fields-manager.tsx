"use client";

import { useEffect, useState } from "react";
import { Plus, RefreshCw } from "lucide-react";
import { requestJson } from "@/lib/client-request";

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
  const [busy, setBusy] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const result = await requestJson<CustomField[]>("/api/fields", { cache: "no-store" });
      if (!Array.isArray(result)) throw new Error("No se pudieron cargar los campos.");
      setFields(result);
      setMessage(null);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "No se pudieron cargar los campos.");
    } finally { setLoading(false); }
  };

  useEffect(() => {
    void load();
  }, []);

  const create = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (busy || !name.trim()) return;
    setBusy(true);
    try {
    const result = await requestJson<CustomField>("/api/fields", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombre: name.trim() })
    });
      setFields((current) => [...current, result]);
      setName("");
      setMessage("Campo creado.");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "No se pudo crear el campo.");
    } finally { setBusy(false); }
  };

  const toggle = async (field: CustomField) => {
    if (busy) return;
    setBusy(true);
    try {
    await requestJson(`/api/fields/${field.id_campo}`, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ activo: !field.activo })
    });
      setFields((current) =>
        current.map((item) => (item.id_campo === field.id_campo ? { ...item, activo: !item.activo } : item))
      );
      setMessage("Campo actualizado.");
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "No se pudo actualizar el campo.");
    } finally { setBusy(false); }
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
        <button className="primaryAction" disabled={busy || loading || !name.trim()} type="submit">
          <Plus aria-hidden="true" />
          <span>Agregar campo</span>
        </button>
      </form>

      <div className="fieldListHeader">
        <div>
          <h2>Campos personalizados</h2>
          <p>{fields.length} campo(s) configurado(s)</p>
        </div>
        <button aria-label="Actualizar campos" className="iconAction" disabled={loading || busy} onClick={load} type="button">
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
              <input disabled={busy || loading} aria-label={`Activar ${field.nombre}`} checked={field.activo} onChange={() => toggle(field)} type="checkbox" />
              <span>{field.activo ? "Activo" : "Inactivo"}</span>
            </label>
          </article>
        ))}
      </div>
      {message ? <p className="formMessage" role="status">{message}</p> : null}
    </section>
  );
}
