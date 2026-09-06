"use client";

import Link from "next/link";
import { SimpleAppShell } from "@/components/simple-app-shell";

export default function ErrorPage({ reset }: { error: Error; reset: () => void }) {
  return <SimpleAppShell active="/" title="No pudimos cargar esta pantalla" description="Intenta nuevamente en unos momentos.">
    <section className="emptyStateCard" role="alert">
      <h2>La información no está disponible temporalmente</h2>
      <p>Puedes volver a intentar la carga o regresar a la consulta.</p>
      <div className="simplePageActions">
        <button className="primaryAction" onClick={reset}>Reintentar</button>
        <Link className="secondaryAction" href="/">Volver al panel</Link>
      </div>
    </section>
  </SimpleAppShell>;
}
