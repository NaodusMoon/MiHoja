type RestOptions = RequestInit & {
  prefer?: string;
};

function getSupabaseConfig() {
  const url = process.env.SUPABASE_URL;
  const key = process.env.SUPABASE_PUBLISHABLE_KEY;

  if (!url || !key) {
    throw new Error("Faltan SUPABASE_URL o SUPABASE_PUBLISHABLE_KEY.");
  }

  return { key, url: url.replace(/\/$/, "") };
}

export async function supabaseRest<T>(path: string, options: RestOptions = {}): Promise<T> {
  const { key, url } = getSupabaseConfig();
  const headers = new Headers(options.headers);

  headers.set("apikey", key);
  headers.set("Authorization", `Bearer ${key}`);
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (options.prefer) {
    headers.set("Prefer", options.prefer);
  }

  const response = await fetch(`${url}/rest/v1/${path}`, {
    ...options,
    headers,
    cache: "no-store"
  });

  if (!response.ok) {
    const detail = await response.text();
    throw new Error(`Supabase respondio ${response.status}: ${detail.slice(0, 300)}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export function encodeFilter(value: string | number) {
  return encodeURIComponent(String(value));
}
