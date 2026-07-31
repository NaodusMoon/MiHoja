import { ExcelImportPanel } from "@/components/excel-import-panel";
import { PersonForm } from "@/components/person-form";
import { SimpleAppShell } from "@/components/simple-app-shell";
import { getCustomFields } from "@/lib/people-service";

export default async function InsertPage() {
  const customFields = await getCustomFields();

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
          <p>Completa los datos personales, laborales, de salud y contacto.</p>
        </header>
        <PersonForm customFields={customFields} />
      </section>
    </SimpleAppShell>
  );
}
