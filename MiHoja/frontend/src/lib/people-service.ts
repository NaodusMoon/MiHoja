import type {
  DashboardFilterOptions,
  DashboardMetric,
  DashboardOverview,
  DashboardPeopleResponse,
  DashboardPersonCard
} from "@/lib/types";
import { encodeFilter, supabaseRest } from "@/lib/supabase-rest";

export type PersonRecord = {
  n: number;
  numero: number | null;
  nombres: string | null;
  apellidos: string | null;
  cedula: string | null;
  correo_institucional: string | null;
  direccion: string | null;
  enlace_sigep: string | null;
  estado: string | null;
  fecha_nacimiento: string | null;
  imagen_url: string | null;
  lugar_expedicion: string | null;
  numero_hijos: number | null;
  sexo: string | null;
  telefono_institucional: string | null;
  formacion?: Array<{
    id_formacion: number;
    formacion_academica: string | null;
    grado: string | null;
    titulo: string | null;
  }>;
  persona_cargo_laboral?: Array<{
    id_pcl: number;
    fecha_ingreso: string | null;
    fecha_firma_contrato: string | null;
    meses_experiencia: number | null;
    cargo_laboral: {
      id_cargo: number;
      cargo: string | null;
      codigo: string | null;
      dependencia: string | null;
    } | null;
    induccion_examen?: Array<{
      id_induccion: number;
      induccion: boolean | null;
      examen_ingreso: boolean | null;
      fecha_egreso: string | null;
    }>;
  }>;
  contacto_emergencia?: Array<{
    id_contacto: number;
    nombre_contacto_emergencia: string | null;
    parentesco: string | null;
    telefono_contacto_emergencia: string | null;
  }>;
  riesgo_procedencia?: Array<{
    id_riesgo: number;
    medio_transporte: string | null;
    procedencia_trabajador: string | null;
    riesgo: string | null;
  }>;
  salud?: Array<{
    id_salud: number;
    afp: string | null;
    arl: string | null;
    carnet_vacunacion: boolean | null;
    ccf: string | null;
    dotacion: string | null;
    eps: string | null;
    rh: string | null;
  }>;
  alergia?: Array<{ id_alergia: number; nombre: string }>;
  enfermedad?: Array<{ id_enfermedad: number; nombre: string }>;
  medicamento?: Array<{ id_medicamento: number; nombre: string }>;
  persona_campo_valor?: Array<{
    valor: string;
    campo_id: number;
    campo_personalizado: { nombre: string } | null;
  }>;
};

export type CustomField = {
  id_campo: number;
  nombre: string;
  activo: boolean;
};

export type CompletePersonInput = {
  nombres?: string;
  apellidos?: string;
  cedula?: string;
  correoInstitucional?: string;
  telefonoInstitucional?: string;
  lugarExpedicion?: string;
  fechaNacimiento?: string;
  direccion?: string;
  sexo?: string;
  estado?: string;
  enlaceSigep?: string;
  numeroHijos?: string;
  numero?: string;
  imagenUrl?: string;
  cargo?: string;
  codigoCargo?: string;
  dependencia?: string;
  fechaIngreso?: string;
  fechaFirmaContrato?: string;
  mesesExperiencia?: string;
  induccion?: string;
  examenIngreso?: string;
  fechaEgreso?: string;
  formacionAcademica?: string;
  grado?: string;
  titulo?: string;
  riesgo?: string;
  medioTransporte?: string;
  procedenciaTrabajador?: string;
  dotacion?: string;
  arl?: string;
  eps?: string;
  afp?: string;
  ccf?: string;
  rh?: string;
  carnetVacunacion?: string;
  nombreEmergencia?: string;
  parentesco?: string;
  telefonoEmergencia?: string;
  alergias?: string;
  enfermedades?: string;
  medicamentos?: string;
  customFields?: Record<string, string>;
};

const personSelect = [
  "n",
  "numero",
  "nombres",
  "apellidos",
  "cedula",
  "correo_institucional",
  "direccion",
  "enlace_sigep",
  "estado",
  "fecha_nacimiento",
  "imagen_url",
  "lugar_expedicion",
  "numero_hijos",
  "sexo",
  "telefono_institucional",
  "formacion(id_formacion,formacion_academica,grado,titulo)",
  "persona_cargo_laboral(id_pcl,fecha_ingreso,fecha_firma_contrato,meses_experiencia,cargo_laboral(id_cargo,cargo,codigo,dependencia),induccion_examen(id_induccion,induccion,examen_ingreso,fecha_egreso))",
  "contacto_emergencia(id_contacto,nombre_contacto_emergencia,parentesco,telefono_contacto_emergencia)",
  "riesgo_procedencia(id_riesgo,medio_transporte,procedencia_trabajador,riesgo)",
  "salud(id_salud,afp,arl,carnet_vacunacion,ccf,dotacion,eps,rh)",
  "alergia(id_alergia,nombre)",
  "enfermedad(id_enfermedad,nombre)",
  "medicamento(id_medicamento,nombre)",
  "persona_campo_valor(valor,campo_id,campo_personalizado(nombre))"
].join(",");

function first<T>(items: T[] | undefined) {
  return items?.[0] ?? null;
}

export async function getAllPeople(): Promise<PersonRecord[]> {
  return supabaseRest<PersonRecord[]>(
    `persona?select=${encodeURIComponent(personSelect)}&order=numero.asc&limit=1000`
  );
}

export async function getPersonById(id: number): Promise<PersonRecord | null> {
  const rows = await supabaseRest<PersonRecord[]>(
    `persona?select=${encodeURIComponent(personSelect)}&n=eq.${encodeFilter(id)}&limit=1`
  );
  return rows[0] ?? null;
}

function toCard(person: PersonRecord): DashboardPersonCard {
  const cargo = first(person.persona_cargo_laboral)?.cargo_laboral;
  return {
    id: person.n,
    numero: person.numero,
    nombres: person.nombres ?? "SIN NOMBRE",
    apellidos: person.apellidos ?? "",
    cedula: person.cedula ?? "NO DISPONIBLE",
    cargo: cargo?.cargo ?? "NO DISPONIBLE",
    dependencia: cargo?.dependencia ?? "NO DISPONIBLE",
    correoInstitucional: person.correo_institucional,
    telefonoInstitucional: person.telefono_institucional,
    estado: person.estado ?? "NO DISPONIBLE",
    imagenUrl: person.imagen_url
  };
}

function unique(values: Array<string | null | undefined>) {
  return Array.from(new Set(values.filter((value): value is string => Boolean(value)))).sort((a, b) =>
    a.localeCompare(b)
  );
}

function getFilterOptions(people: PersonRecord[]): DashboardFilterOptions {
  return {
    sexo: unique(people.map((person) => person.sexo)),
    lugarExpedicion: unique(people.map((person) => person.lugar_expedicion)),
    formacion: unique(people.map((person) => first(person.formacion)?.formacion_academica)),
    dependencia: unique(people.map((person) => first(person.persona_cargo_laboral)?.cargo_laboral?.dependencia)),
    cargo: unique(people.map((person) => first(person.persona_cargo_laboral)?.cargo_laboral?.cargo))
  };
}

function buildMetrics(total: number, activeFilters: number, duplicateCount: number): DashboardMetric[] {
  return [
    { id: "total", label: "Registros visibles", value: String(total), tone: "positive" },
    { id: "selected", label: "Seleccionadas", value: "0", tone: "neutral" },
    { id: "filters", label: "Filtros activos", value: String(activeFilters), tone: "accent" },
    { id: "duplicates", label: "Duplicados detectados", value: String(duplicateCount), tone: "warning" }
  ];
}

function duplicateCount(people: PersonRecord[]) {
  const seen = new Set<string>();
  let duplicates = 0;
  for (const person of people) {
    const key = person.cedula?.trim().toLowerCase();
    if (!key) continue;
    if (seen.has(key)) duplicates += 1;
    seen.add(key);
  }
  return duplicates;
}

export async function getDashboardOverviewData(): Promise<DashboardOverview> {
  const people = await getAllPeople();
  return {
    metrics: buildMetrics(people.length, 0, duplicateCount(people)),
    highlights: [
      "Datos ficticios listos para demostraciones",
      "Busqueda y filtros conectados con Supabase",
      "Importacion de Excel disponible desde Insertar"
    ],
    recentPeople: people.slice(0, 6).map(toCard)
  };
}

export async function getDashboardPeopleData(params: URLSearchParams): Promise<DashboardPeopleResponse> {
  const people = await getAllPeople();
  const query = (params.get("query") ?? "").trim().toLocaleLowerCase();
  const sortBy = params.get("sortBy") ?? "name-asc";
  const page = Math.max(1, Number(params.get("page") ?? 1));
  const size = Math.min(200, Math.max(1, Number(params.get("size") ?? 6)));
  const filters = {
    sexo: params.getAll("sexo"),
    lugarExpedicion: params.getAll("lugarExpedicion"),
    formacion: params.getAll("formacion"),
    dependencia: params.getAll("dependencia"),
    cargo: params.getAll("cargo")
  };

  const activeFilterCount = Object.values(filters).filter((values) => values.length > 0).length;
  const filtered = people.filter((person) => {
    const cargo = first(person.persona_cargo_laboral)?.cargo_laboral;
    const formation = first(person.formacion)?.formacion_academica;
    const searchable = [
      person.nombres,
      person.apellidos,
      person.cedula,
      cargo?.cargo,
      cargo?.dependencia
    ]
      .filter(Boolean)
      .join(" ")
      .toLocaleLowerCase();

    return (
      (!query || searchable.includes(query)) &&
      (!filters.sexo.length || filters.sexo.includes(person.sexo ?? "")) &&
      (!filters.lugarExpedicion.length || filters.lugarExpedicion.includes(person.lugar_expedicion ?? "")) &&
      (!filters.formacion.length || filters.formacion.includes(formation ?? "")) &&
      (!filters.dependencia.length || filters.dependencia.includes(cargo?.dependencia ?? "")) &&
      (!filters.cargo.length || filters.cargo.includes(cargo?.cargo ?? ""))
    );
  });

  filtered.sort((a, b) => {
    if (sortBy === "number-asc") return (a.numero ?? 0) - (b.numero ?? 0);
    const aName = `${a.apellidos ?? ""} ${a.nombres ?? ""}`;
    const bName = `${b.apellidos ?? ""} ${b.nombres ?? ""}`;
    return sortBy === "name-desc" ? bName.localeCompare(aName) : aName.localeCompare(bName);
  });

  const total = filtered.length;
  const totalPages = Math.max(1, Math.ceil(total / size));
  const safePage = Math.min(page, totalPages);
  const start = (safePage - 1) * size;

  return {
    people: filtered.slice(start, start + size).map(toCard),
    metrics: buildMetrics(total, activeFilterCount, duplicateCount(people)),
    filterOptions: getFilterOptions(people),
    total,
    page: safePage,
    size,
    totalPages,
    duplicateCount: duplicateCount(people),
    activeFilterCount,
    query
  };
}

export async function upsertPeople(rows: Array<Record<string, unknown>>) {
  return supabaseRest<PersonRecord[]>("persona?on_conflict=cedula", {
    method: "POST",
    body: JSON.stringify(rows),
    prefer: "resolution=merge-duplicates,return=representation"
  });
}

export async function updatePerson(id: number, values: Record<string, unknown>) {
  const rows = await supabaseRest<PersonRecord[]>(`persona?n=eq.${encodeFilter(id)}`, {
    method: "PATCH",
    body: JSON.stringify(values),
    prefer: "return=representation"
  });
  return rows[0] ?? null;
}

function textOrNull(value: string | undefined, lowerCase = false) {
  const text = value?.trim();
  if (!text) return null;
  return lowerCase ? text.toLowerCase() : text.toUpperCase();
}

function dateOrNull(value: string | undefined) {
  return value?.trim() || null;
}

function booleanOrNull(value: string | undefined) {
  if (value === "true") return true;
  if (value === "false") return false;
  return null;
}

function numberOrNull(value: string | undefined) {
  if (!value?.trim()) return null;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
}

async function upsertRelatedRow<T extends Record<string, unknown>>(
  table: string,
  foreignKey: string,
  foreignValue: number,
  primaryKey: string,
  values: T
) {
  const current = await supabaseRest<Array<Record<string, unknown>>>(
    `${table}?select=${primaryKey}&${foreignKey}=eq.${encodeFilter(foreignValue)}&limit=1`
  );
  const id = current[0]?.[primaryKey];
  if (id !== undefined) {
    const rows = await supabaseRest<Array<Record<string, unknown>>>(
      `${table}?${primaryKey}=eq.${encodeFilter(String(id))}`,
      {
        method: "PATCH",
        body: JSON.stringify(values),
        prefer: "return=representation"
      }
    );
    return rows[0] ?? null;
  }

  const rows = await supabaseRest<Array<Record<string, unknown>>>(table, {
    method: "POST",
    body: JSON.stringify({ ...values, [foreignKey]: foreignValue }),
    prefer: "return=representation"
  });
  return rows[0] ?? null;
}

function splitList(value: string | undefined) {
  return Array.from(
    new Set(
      (value ?? "")
        .split(/[,;\n]/)
        .map((item) => item.trim().toUpperCase())
        .filter(Boolean)
    )
  );
}

async function syncNamedRows(
  table: "alergia" | "enfermedad" | "medicamento",
  primaryKey: string,
  personId: number,
  names: string[]
) {
  const current = await supabaseRest<Array<Record<string, unknown>>>(
    `${table}?select=${primaryKey},nombre&n=eq.${encodeFilter(personId)}&order=${primaryKey}.asc`
  );

  for (let index = 0; index < names.length; index += 1) {
    const rowId = current[index]?.[primaryKey];
    if (rowId !== undefined) {
      await supabaseRest(`${table}?${primaryKey}=eq.${encodeFilter(String(rowId))}`, {
        method: "PATCH",
        body: JSON.stringify({ nombre: names[index] }),
        prefer: "return=minimal"
      });
    } else {
      await supabaseRest(table, {
        method: "POST",
        body: JSON.stringify({ n: personId, nombre: names[index] }),
        prefer: "return=minimal"
      });
    }
  }

  for (const row of current.slice(names.length)) {
    const rowId = String(row[primaryKey]);
    if (table === "enfermedad") {
      await supabaseRest<void>(
        `enfermedad_medicamento?enfermedad_id=eq.${encodeFilter(rowId)}`,
        { method: "DELETE", prefer: "return=minimal" }
      );
    } else if (table === "medicamento") {
      await supabaseRest<void>(
        `enfermedad_medicamento?medicamento_id=eq.${encodeFilter(rowId)}`,
        { method: "DELETE", prefer: "return=minimal" }
      );
    }
    await supabaseRest<void>(`${table}?${primaryKey}=eq.${encodeFilter(rowId)}`, {
      method: "DELETE",
      prefer: "return=minimal"
    });
  }
}

export async function getCustomFields(): Promise<CustomField[]> {
  return supabaseRest<CustomField[]>(
    "campo_personalizado?select=id_campo,nombre,activo&activo=eq.true&order=id_campo.asc"
  );
}

export async function saveCompletePerson(personId: number | undefined, input: CompletePersonInput) {
  const personValues = {
    nombres: textOrNull(input.nombres),
    apellidos: textOrNull(input.apellidos),
    cedula: input.cedula?.trim() || null,
    correo_institucional: textOrNull(input.correoInstitucional, true),
    telefono_institucional: input.telefonoInstitucional?.trim() || null,
    lugar_expedicion: textOrNull(input.lugarExpedicion),
    fecha_nacimiento: dateOrNull(input.fechaNacimiento),
    direccion: textOrNull(input.direccion),
    sexo: textOrNull(input.sexo),
    estado: textOrNull(input.estado) ?? "ACTIVO",
    enlace_sigep: input.enlaceSigep?.trim() || null,
    numero_hijos: numberOrNull(input.numeroHijos) ?? 0,
    numero: numberOrNull(input.numero),
    imagen_url: input.imagenUrl?.trim() || null
  };

  const person = personId
    ? await updatePerson(personId, personValues)
    : (await upsertPeople([personValues]))[0];
  if (!person) throw new Error("No se pudo guardar la persona.");

  const id = person.n;
  await Promise.all([
    upsertRelatedRow("formacion", "n", id, "id_formacion", {
      formacion_academica: textOrNull(input.formacionAcademica),
      grado: textOrNull(input.grado),
      titulo: textOrNull(input.titulo)
    }),
    upsertRelatedRow("contacto_emergencia", "n", id, "id_contacto", {
      nombre_contacto_emergencia: textOrNull(input.nombreEmergencia),
      parentesco: textOrNull(input.parentesco),
      telefono_contacto_emergencia: input.telefonoEmergencia?.trim() || null
    }),
    upsertRelatedRow("riesgo_procedencia", "n", id, "id_riesgo", {
      riesgo: textOrNull(input.riesgo),
      medio_transporte: textOrNull(input.medioTransporte),
      procedencia_trabajador: textOrNull(input.procedenciaTrabajador)
    }),
    upsertRelatedRow("salud", "n", id, "id_salud", {
      dotacion: textOrNull(input.dotacion),
      arl: textOrNull(input.arl),
      eps: textOrNull(input.eps),
      afp: textOrNull(input.afp),
      ccf: textOrNull(input.ccf),
      rh: textOrNull(input.rh),
      carnet_vacunacion: booleanOrNull(input.carnetVacunacion)
    })
  ]);

  const hasJobData = [
    input.cargo,
    input.codigoCargo,
    input.dependencia,
    input.fechaIngreso,
    input.fechaFirmaContrato,
    input.mesesExperiencia
  ].some((value) => Boolean(value?.trim()));

  if (hasJobData) {
    const cargoValues = {
      cargo: textOrNull(input.cargo),
      codigo: textOrNull(input.codigoCargo),
      dependencia: textOrNull(input.dependencia)
    };
    const cargos = await supabaseRest<
      Array<{ id_cargo: number; cargo: string | null; codigo: string | null; dependencia: string | null }>
    >("cargo_laboral?select=id_cargo,cargo,codigo,dependencia&limit=1000");
    let cargo = cargos.find(
      (item) =>
        item.cargo === cargoValues.cargo &&
        item.codigo === cargoValues.codigo &&
        item.dependencia === cargoValues.dependencia
    );
    if (!cargo) {
      [cargo] = await supabaseRest<typeof cargos>("cargo_laboral", {
        method: "POST",
        body: JSON.stringify(cargoValues),
        prefer: "return=representation"
      });
    }

    const personJob = await upsertRelatedRow(
      "persona_cargo_laboral",
      "persona_id",
      id,
      "id_pcl",
      {
        cargo_id: cargo.id_cargo,
        fecha_ingreso: dateOrNull(input.fechaIngreso),
        fecha_firma_contrato: dateOrNull(input.fechaFirmaContrato),
        meses_experiencia: numberOrNull(input.mesesExperiencia)
      }
    );
    const personJobId = Number(personJob?.id_pcl);
    if (Number.isFinite(personJobId)) {
      await upsertRelatedRow("induccion_examen", "persona_cargo_id", personJobId, "id_induccion", {
        induccion: booleanOrNull(input.induccion),
        examen_ingreso: booleanOrNull(input.examenIngreso),
        fecha_egreso: dateOrNull(input.fechaEgreso)
      });
    }
  }

  await Promise.all([
    syncNamedRows("alergia", "id_alergia", id, splitList(input.alergias)),
    syncNamedRows("enfermedad", "id_enfermedad", id, splitList(input.enfermedades)),
    syncNamedRows("medicamento", "id_medicamento", id, splitList(input.medicamentos))
  ]);

  const customFields = input.customFields ?? {};
  await Promise.all(
    Object.entries(customFields).map(async ([fieldId, rawValue]) => {
      const campoId = Number(fieldId);
      if (!Number.isFinite(campoId)) return;
      const current = await supabaseRest<Array<{ id_valor: number }>>(
        `persona_campo_valor?select=id_valor&persona_id=eq.${encodeFilter(id)}&campo_id=eq.${encodeFilter(campoId)}&limit=1`
      );
      const value = rawValue.trim();
      if (current[0] && !value) {
        await supabaseRest<void>(
          `persona_campo_valor?id_valor=eq.${encodeFilter(current[0].id_valor)}`,
          { method: "DELETE", prefer: "return=minimal" }
        );
      } else if (current[0]) {
        await supabaseRest(
          `persona_campo_valor?id_valor=eq.${encodeFilter(current[0].id_valor)}`,
          { method: "PATCH", body: JSON.stringify({ valor: value }), prefer: "return=minimal" }
        );
      } else if (value) {
        await supabaseRest("persona_campo_valor", {
          method: "POST",
          body: JSON.stringify({ persona_id: id, campo_id: campoId, valor: value }),
          prefer: "return=minimal"
        });
      }
    })
  );

  return getPersonById(id);
}

export async function deletePeople(ids: number[]) {
  const cleanIds = Array.from(
    new Set(ids.map((id) => Number(id)).filter((id) => Number.isInteger(id) && id > 0))
  );
  if (cleanIds.length === 0) return;

  const filter = cleanIds.join(",");
  const deleteRows = async (table: string, where: string) => {
    await supabaseRest<void>(`${table}?${where}`, {
      method: "DELETE",
      prefer: "return=minimal"
    });
  };

  const [jobs, diseases, medicines] = await Promise.all([
    supabaseRest<Array<{ id_pcl: number }>>(
      `persona_cargo_laboral?select=id_pcl&persona_id=in.(${filter})`
    ),
    supabaseRest<Array<{ id_enfermedad: number }>>(
      `enfermedad?select=id_enfermedad&n=in.(${filter})`
    ),
    supabaseRest<Array<{ id_medicamento: number }>>(
      `medicamento?select=id_medicamento&n=in.(${filter})`
    )
  ]);

  const jobIds = jobs.map((row) => row.id_pcl).filter(Number.isInteger);
  const diseaseIds = diseases.map((row) => row.id_enfermedad).filter(Number.isInteger);
  const medicineIds = medicines.map((row) => row.id_medicamento).filter(Number.isInteger);

  if (diseaseIds.length > 0) {
    await deleteRows("enfermedad_medicamento", `enfermedad_id=in.(${diseaseIds.join(",")})`);
  }
  if (medicineIds.length > 0) {
    await deleteRows("enfermedad_medicamento", `medicamento_id=in.(${medicineIds.join(",")})`);
  }
  if (jobIds.length > 0) {
    await deleteRows("induccion_examen", `persona_cargo_id=in.(${jobIds.join(",")})`);
  }

  await Promise.all([
    deleteRows("persona_campo_valor", `persona_id=in.(${filter})`),
    deleteRows("formacion", `n=in.(${filter})`),
    deleteRows("riesgo_procedencia", `n=in.(${filter})`),
    deleteRows("salud", `n=in.(${filter})`),
    deleteRows("contacto_emergencia", `n=in.(${filter})`),
    deleteRows("alergia", `n=in.(${filter})`),
    deleteRows("enfermedad", `n=in.(${filter})`),
    deleteRows("medicamento", `n=in.(${filter})`)
  ]);

  if (jobIds.length > 0) {
    await deleteRows("persona_cargo_laboral", `persona_id=in.(${filter})`);
  }

  await deleteRows("persona", `n=in.(${filter})`);
}
