import { DashboardShell } from "@/components/dashboard-shell";
import { getDashboardOverview } from "@/lib/api";
import type { DashboardOverview } from "@/lib/types";

const fallbackOverview: DashboardOverview = {
  metrics: [
    { id: "total", label: "Registros visibles", value: "124", tone: "positive" },
    { id: "selected", label: "Seleccionadas", value: "0", tone: "neutral" },
    { id: "filters", label: "Filtros activos", value: "3", tone: "accent" },
    { id: "duplicates", label: "Duplicados detectados", value: "12", tone: "warning" }
  ],
  highlights: [
    "Busqueda inteligente por nombre, cedula y cargo",
    "Vista dual entre tarjetas amplias y modo compacto",
    "Animaciones suaves para interacciones y carga"
  ],
  recentPeople: [
    {
      id: 1,
      numero: 1,
      nombres: "Leyly Zuliana",
      apellidos: "Alfonso Pinzon",
      cedula: "1048848951",
      cargo: "Profesional universitario",
      dependencia: "Secretaria general y de gobierno",
      correoInstitucional: "leyly.alfonso@mihoja.gov.co",
      telefonoInstitucional: "320 100 2101",
      estado: "Activo",
      imagenUrl: null
    },
    {
      id: 2,
      numero: 2,
      nombres: "Clara Ines",
      apellidos: "Avila Diaz",
      cedula: "33677037",
      cargo: "Auxiliar administrativo",
      dependencia: "Secretaria general y de gobierno - inspeccion de policia",
      correoInstitucional: "clara.avila@mihoja.gov.co",
      telefonoInstitucional: "320 100 2102",
      estado: "Activo",
      imagenUrl: null
    },
    {
      id: 3,
      numero: 3,
      nombres: "Daniel Eduardo",
      apellidos: "Avila Becerra",
      cedula: "1052413131",
      cargo: "Secretario de cultura",
      dependencia: "No disponible",
      correoInstitucional: "daniel.avila@mihoja.gov.co",
      telefonoInstitucional: "320 100 2103",
      estado: "Activo",
      imagenUrl: null
    },
    {
      id: 4,
      numero: 4,
      nombres: "Rosa Cecilia",
      apellidos: "Avila Gonzalez",
      cedula: "23606579",
      cargo: "Auxiliar administrativo",
      dependencia: "Secretaria general y de gobierno",
      correoInstitucional: "rosa.avila@mihoja.gov.co",
      telefonoInstitucional: "320 100 2104",
      estado: "Activo",
      imagenUrl: null
    },
    {
      id: 5,
      numero: 5,
      nombres: "Angie Celene",
      apellidos: "Avila Montanez",
      cedula: "33677704",
      cargo: "Servicios generales",
      dependencia: "Secretaria general y de gobierno",
      correoInstitucional: "angie.avila@mihoja.gov.co",
      telefonoInstitucional: "320 100 2105",
      estado: "Activo",
      imagenUrl: null
    },
    {
      id: 6,
      numero: 6,
      nombres: "Norma Constanza",
      apellidos: "Bacca Vacca",
      cedula: "1049625301",
      cargo: "Inspector de policia",
      dependencia: "Secretaria general y de gobierno",
      correoInstitucional: "norma.bacca@mihoja.gov.co",
      telefonoInstitucional: "320 100 2106",
      estado: "Activo",
      imagenUrl: null
    }
  ]
};

async function loadOverview() {
  try {
    return await getDashboardOverview();
  } catch {
    return fallbackOverview;
  }
}

export default async function HomePage() {
  const overview = await loadOverview();

  return <DashboardShell overview={overview} />;
}
