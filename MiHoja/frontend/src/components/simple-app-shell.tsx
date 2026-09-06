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
      <aside className="simpleSidebar">
        <Link className="simpleBrand" href="/">
          <span className="simpleBrandMark">
            <Image alt="MiHoja" height={54} priority src="/logo-mihoja.png" width={54} />
          </span>
          <span className="simpleBrandCopy">
            <strong>MiHoja</strong>
            <small>Datos de personal</small>
          </span>
        </Link>
        <nav className="simpleNav" aria-label="Navegacion principal">
          <span className="simpleNavLabel">MENU PRINCIPAL</span>
          {links.map((link) => {
            const Icon = link.icon;
            return (
              <Link aria-current={active === link.href ? "page" : undefined} className={active === link.href ? "isActive" : ""} href={link.href} key={link.href}>
                <Icon aria-hidden="true" />
                <span>{link.label}</span>
              </Link>
            );
          })}
        </nav>
        <div className="simpleSidebarNote">
          <span>MiHoja</span>
          <small>Gestion de personal</small>
        </div>
      </aside>

      <section className="simpleWorkspace">
        <header className="simpleTopbar">
          <div className="simpleTopbarIdentity">
            <span className="simpleTopbarAvatar"><UserRound aria-hidden="true" /></span>
            <span>
              <strong>Hola, Sandra</strong>
              <small>Administradora</small>
            </span>
          </div>
          <span className="simpleTopbarSection">{title}</span>
        </header>
        <section className="simplePage">
          <header className="simplePageHeader">
            <div>
              <span className="simpleEyebrow">MIHOJA / {active === "/" ? "CONSULTAR" : active.slice(1).replaceAll("-", " ").toUpperCase()}</span>
              <h1>{title}</h1>
              <p>{description}</p>
            </div>
            {actions ? <div className="simplePageActions">{actions}</div> : null}
          </header>
          {children}
        </section>
      </section>
    </main>
  );
}
