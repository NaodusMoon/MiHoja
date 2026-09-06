"use client";

import { useRef, useState } from "react";
import { Download, FileSpreadsheet, Upload } from "lucide-react";
import { requestJson } from "@/lib/client-request";

export function ExcelImportPanel() {
  const inputRef = useRef<HTMLInputElement>(null);
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [failedRows, setFailedRows] = useState<Array<{ row: number; message: string }>>([]);

  const upload = async () => {
    if (uploading) return;
    if (!file) {
      inputRef.current?.click();
      return;
    }

    setUploading(true);
    setMessage(null);
    setFailedRows([]);
    const body = new FormData();
    body.set("file", file);
    try {
      const result = await requestJson<{ message?: string; failedRows?: Array<{ row: number; message: string }> }>("/api/import", { method: "POST", body });
      setMessage(result.message ?? "Importación completada.");
      setFailedRows(result.failedRows ?? []);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "No se pudo importar.");
    } finally {
      setUploading(false);
    }
  };

  return (
    <section className="importBand">
      <div className="importBandCopy">
        <span className="importIcon">
          <FileSpreadsheet aria-hidden="true" />
        </span>
        <div>
          <h2>Importar desde Excel</h2>
          <p>Usa la plantilla con 124 registros ficticios o selecciona otro archivo compatible.</p>
        </div>
      </div>
      <div className="importActions">
        <a className="secondaryAction" download href="/plantilla-mihoja-datos-ficticios.xlsx">
          <Download aria-hidden="true" />
          <span>Descargar plantilla</span>
        </a>
        <input
          accept=".xlsx"
          aria-label="Archivo Excel"
          disabled={uploading}
          className="visuallyHidden"
          onChange={(event) => setFile(event.target.files?.[0] ?? null)}
          ref={inputRef}
          type="file"
        />
        <button className="secondaryAction" disabled={uploading} onClick={() => inputRef.current?.click()} type="button">
          <FileSpreadsheet aria-hidden="true" />
          <span>{file?.name ?? "Seleccionar Excel"}</span>
        </button>
        <button className="primaryAction" disabled={uploading} onClick={upload} type="button">
          <Upload aria-hidden="true" />
          <span>{uploading ? "Importando..." : "Importar"}</span>
        </button>
      </div>
      {message ? <p className="importMessage" role="status">{message}</p> : null}
      {failedRows.length ? <ul className="importErrors">{failedRows.map((failure) => <li key={failure.row}>Fila {failure.row}: {failure.message}</li>)}</ul> : null}
    </section>
  );
}
