import { NextResponse } from "next/server";

const backendBaseUrl =
  process.env.API_BASE_URL ??
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080";

type DashboardMetric = {
  id: string;
  label: string;
  value: string;
  tone: string;
};

type DashboardPerson = {
  id: number;
  nombres: string;
  apellidos: string;
  cargo: string | null;
  dependencia: string | null;
};

type DashboardOverview = {
  metrics: DashboardMetric[];
  recentPeople: DashboardPerson[];
  highlights: string[];
};

export async function GET() {
  try {
    const response = await fetch(`${backendBaseUrl}/api/dashboard/overview`, {
      cache: "no-store"
    });

    if (!response.ok) {
      throw new Error(`Notifications request failed (${response.status})`);
    }

    const overview = (await response.json()) as DashboardOverview;
    const duplicateMetric = overview.metrics.find((metric) => metric.id === "duplicates");
    const visibleMetric = overview.metrics.find((metric) => metric.id === "visible");

    const notifications = [
      {
        id: "summary-visible",
        title: "Registros disponibles",
        description: `${visibleMetric?.value ?? "0"} hojas visibles en el tablero.`,
        tone: "neutral"
      },
      {
        id: "summary-duplicates",
        title: "Control de duplicados",
        description:
          Number(duplicateMetric?.value ?? "0") > 0
            ? `Hay ${duplicateMetric?.value} posibles duplicados por revisar.`
            : "No se detectaron duplicados en la vista actual.",
        tone: Number(duplicateMetric?.value ?? "0") > 0 ? "warning" : "positive"
      },
      ...overview.recentPeople.slice(0, 3).map((person) => ({
        id: `person-${person.id}`,
        title: `${person.apellidos} ${person.nombres}`.trim(),
        description: `${person.cargo ?? "Sin cargo"} · ${person.dependencia ?? "Sin dependencia"}`,
        tone: "neutral"
      }))
    ];

    return NextResponse.json({
      notifications,
      unreadCount: notifications.length
    });
  } catch (error) {
    return NextResponse.json(
      {
        notifications: [],
        unreadCount: 0,
        message: error instanceof Error ? error.message : "No se pudieron cargar las notificaciones."
      },
      { status: 200 }
    );
  }
}
