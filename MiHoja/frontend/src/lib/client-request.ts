export async function requestJson<T>(url: string, options?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(url, { ...options, signal: options?.signal ?? AbortSignal.timeout(90000) });
  } catch {
    throw new Error("No se pudo confirmar la operación. Revisa tu conexión y consulta los datos antes de volver a intentarlo.");
  }
  const result = await response.json().catch(() => null);
  if (!response.ok || result === null) {
    throw new Error(result?.message || `El servidor no pudo completar la solicitud (${response.status}). Inténtalo de nuevo.`);
  }
  return result as T;
}
