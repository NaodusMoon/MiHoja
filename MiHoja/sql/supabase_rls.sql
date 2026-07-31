-- Ejecutar manualmente en Supabase SQL Editor para endurecer acceso a API PostgREST.
-- Nota: este backend Java se conecta por JDBC directo y normalmente usa credencial de DB,
-- por lo que RLS aplica principalmente para accesos por API de Supabase.

alter table if exists persona enable row level security;
alter table if exists cargo_laboral enable row level security;
alter table if exists alergia enable row level security;
alter table if exists contacto_emergencia enable row level security;
alter table if exists enfermedad enable row level security;
alter table if exists formacion enable row level security;
alter table if exists medicamento enable row level security;
alter table if exists persona_cargo_laboral enable row level security;
alter table if exists induccion_examen enable row level security;
alter table if exists riesgo_procedencia enable row level security;
alter table if exists salud enable row level security;
alter table if exists campo_personalizado enable row level security;
alter table if exists persona_campo_valor enable row level security;

drop policy if exists "read_persona_authenticated" on persona;
create policy "read_persona_authenticated" on persona
    for select to authenticated
    using (true);

drop policy if exists "write_persona_authenticated" on persona;
create policy "write_persona_authenticated" on persona
    for all to authenticated
    using (true)
    with check (true);
