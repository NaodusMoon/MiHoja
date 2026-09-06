import Link from "next/link";
import { SimpleAppShell } from "@/components/simple-app-shell";

export default function NotFound() {
  return (
    <SimpleAppShell active="/" title="No encontramos esta página" description="El enlace puede haber cambiado o el registro ya no está disponible.">
    <section className="emptyStateCard">
      <span>404</span>
      <h2>Volvamos a tus registros</h2>
      <p>El enlace puede estar desactualizado o el registro ya no esta disponible.</p>
      <Link className="primaryAction" href="/">
        Volver al panel
      </Link>
    </section>
    </SimpleAppShell>
  );
}
