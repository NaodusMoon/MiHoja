import Image from "next/image";
import Link from "next/link";
import type { ReactNode } from "react";
import { FilePlus2, Grid2x2, UserRound } from "lucide-react";

const links = [
  { href: "/", label: "Consultar", icon: UserRound },
  { href: "/insertar", label: "Insertar", icon: FilePlus2 },
  { href: "/configuracion-campos", label: "Campos", icon: Grid2x2 }
];

export function SimpleAppShell({
  active,
  title,
  description,
  actions,
  children
}: {
  active: string;
  title: string;
  description: string;
  actions?: ReactNode;
  children: ReactNode;
}) {
  return (
    <main className="simpleRoot">
      <header className="simpleTopbar">
        <Link className="simpleBrand" href="/">
          <Image alt="MiHoja" height={44} priority src="/logo-mihoja.png" width={44} />
          <span>
            <strong>MiHoja</strong>
            <small>Datos de personal</small>
          </span>
        </Link>
        <nav className="simpleNav" aria-label="Navegacion principal">
          {links.map((link) => {
            const Icon = link.icon;
            return (
              <Link className={active === link.href ? "isActive" : ""} href={link.href} key={link.href}>
                <Icon aria-hidden="true" />
                <span>{link.label}</span>
              </Link>
            );
          })}
        </nav>
      </header>

      <section className="simplePage">
        <header className="simplePageHeader">
          <div>
            <h1>{title}</h1>
            <p>{description}</p>
          </div>
          {actions ? <div className="simplePageActions">{actions}</div> : null}
        </header>
        {children}
      </section>
    </main>
  );
}
