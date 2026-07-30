import { ExcelImportPanel } from "@/components/excel-import-panel";
import { PersonForm } from "@/components/person-form";
import { SimpleAppShell } from "@/components/simple-app-shell";

export default function InsertPage() {
  return (
    <SimpleAppShell
      active="/insertar"
      description="Crea un registro individual o carga la plantilla de demostracion."
      title="Insertar personas"
    >
      <ExcelImportPanel />
      <section className="formSection">
        <header>
          <h2>Nuevo registro individual</h2>
          <p>Los campos principales se guardan directamente en Supabase.</p>
        </header>
        <PersonForm />
      </section>
    </SimpleAppShell>
  );
}
