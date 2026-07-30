import Link from "next/link";

export default function NotFound() {
  return (
    <main className="notFoundPage">
      <span>404</span>
      <h1>Esta pagina no existe</h1>
      <p>El enlace puede estar desactualizado o el registro ya no esta disponible.</p>
      <Link className="primaryAction" href="/">
        Volver al panel
      </Link>
    </main>
  );
}
