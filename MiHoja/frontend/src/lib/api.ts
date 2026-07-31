import type {
  DashboardCleanupResponse,
  DashboardDeleteResponse,
  DashboardFilters,
  DashboardNotificationsResponse,
  DashboardPeopleResponse
} from "@/lib/types";

const clientApiBaseUrl = "/api/backend";

function buildPeopleQuery(params: {
  query?: string;
  filters?: DashboardFilters;
  sortBy?: string;
  page?: number;
  size?: number;
}) {
  const searchParams = new URLSearchParams();

  if (params.query?.trim()) {
    searchParams.set("query", params.query.trim());
  }

  if (params.sortBy) {
    searchParams.set("sortBy", params.sortBy);
  }

  if (params.page) {
    searchParams.set("page", String(params.page));
  }

  if (params.size) {
    searchParams.set("size", String(params.size));
  }

  const filters = params.filters;
  if (filters) {
    Object.entries(filters).forEach(([key, values]) => {
      values.forEach((value) => searchParams.append(key, value));
    });
  }

  return searchParams.toString();
}

async function ensureOk(response: Response, errorLabel: string) {
  if (!response.ok) {
    let detail = "";
    try {
      const body = (await response.clone().json()) as { message?: string };
      detail = body.message?.trim() ?? "";
    } catch {
      // Keep the generic message when the server does not return JSON.
    }
    throw new Error(`${errorLabel} (${response.status})${detail ? `: ${detail}` : ""}`);
  }
}

export async function getDashboardPeople(params: {
  query?: string;
  filters?: DashboardFilters;
  sortBy?: string;
  page?: number;
  size?: number;
}): Promise<DashboardPeopleResponse> {
  const queryString = buildPeopleQuery(params);
  const response = await fetch(`${clientApiBaseUrl}/dashboard/people?${queryString}`, {
    cache: "no-store"
  });

  await ensureOk(response, "Dashboard people request failed");
  return response.json() as Promise<DashboardPeopleResponse>;
}

export async function deleteDashboardPeople(ids: number[]): Promise<DashboardDeleteResponse> {
  const response = await fetch(`${clientApiBaseUrl}/dashboard/people`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ ids })
  });

  await ensureOk(response, "Dashboard delete request failed");
  return response.json() as Promise<DashboardDeleteResponse>;
}

export async function cleanupDashboardDuplicates(): Promise<DashboardCleanupResponse> {
  const response = await fetch(`${clientApiBaseUrl}/dashboard/maintenance/cleanup-duplicates`, {
    method: "POST"
  });

  await ensureOk(response, "Dashboard cleanup request failed");
  return response.json() as Promise<DashboardCleanupResponse>;
}

export async function getDashboardNotifications(): Promise<DashboardNotificationsResponse> {
  const response = await fetch("/api/notifications", {
    cache: "no-store"
  });

  await ensureOk(response, "Dashboard notifications request failed");
  return response.json() as Promise<DashboardNotificationsResponse>;
}
