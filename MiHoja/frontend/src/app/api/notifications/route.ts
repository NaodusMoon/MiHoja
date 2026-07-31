import { NextResponse } from "next/server";

import { getDashboardOverviewData } from "@/lib/people-service";

export async function GET() {
  try {
    const overview = await getDashboardOverviewData();
    const duplicateMetric = overview.metrics.find((metric) => metric.id === "duplicates");
    const visibleMetric = overview.metrics.find((metric) => metric.id === "total");

    const notifications = [
      {
        id: "summary-visible",
        title: "Registros disponibles",
        description: `${visibleMetric?.value ?? "0"} registros ficticios visibles en el tablero.`,
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
        description: `${person.cargo ?? "Sin cargo"} - ${person.dependencia ?? "Sin dependencia"}`,
        tone: "neutral"
      }))
    ];

    return NextResponse.json({ notifications, unreadCount: notifications.length });
  } catch (error) {
    return NextResponse.json({
      notifications: [],
      unreadCount: 0,
      message: error instanceof Error ? error.message : "No se pudieron cargar las notificaciones."
    });
  }
}
