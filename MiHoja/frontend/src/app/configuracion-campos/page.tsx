import { FieldsManager } from "@/components/fields-manager";
import { SimpleAppShell } from "@/components/simple-app-shell";

export default function FieldsPage() {
  return (
    <SimpleAppShell
      active="/configuracion-campos"
      description="Agrega campos sin modificar las columnas existentes de la base."
      title="Configuracion de campos"
    >
      <FieldsManager />
    </SimpleAppShell>
  );
}
