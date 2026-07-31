"use client";

import { useRef, useState } from "react";
import { Download, FileSpreadsheet, Upload } from "lucide-react";

export function ExcelImportPanel() {
  const inputRef = useRef<HTMLInputElement>(null);
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);

  const upload = async () => {
    if (!file) {
      inputRef.current?.click();
      return;
    }

    setUploading(true);
    setMessage(null);
    const body = new FormData();
    body.set("file", file);
    const response = await fetch("/api/import", { method: "POST", body });
    const result = (await response.json()) as { message?: string };
    setMessage(result.message ?? (response.ok ? "Importacion completada." : "No se pudo importar."));
    setUploading(false);
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
          className="visuallyHidden"
          onChange={(event) => setFile(event.target.files?.[0] ?? null)}
          ref={inputRef}
          type="file"
        />
        <button className="secondaryAction" onClick={() => inputRef.current?.click()} type="button">
          <FileSpreadsheet aria-hidden="true" />
          <span>{file?.name ?? "Seleccionar Excel"}</span>
        </button>
        <button className="primaryAction" disabled={uploading} onClick={upload} type="button">
          <Upload aria-hidden="true" />
          <span>{uploading ? "Importando..." : "Importar"}</span>
        </button>
      </div>
      {message ? <p className="importMessage">{message}</p> : null}
    </section>
  );
}
