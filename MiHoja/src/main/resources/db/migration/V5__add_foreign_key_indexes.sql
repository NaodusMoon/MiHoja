create index if not exists idx_alergia_n
    on alergia (n);

create index if not exists idx_contacto_emergencia_n
    on contacto_emergencia (n);

create index if not exists idx_enfermedad_n
    on enfermedad (n);

create index if not exists idx_enfermedad_medicamento_medicamento_id
    on enfermedad_medicamento (medicamento_id);

create index if not exists idx_formacion_n
    on formacion (n);

create index if not exists idx_induccion_examen_persona_cargo_id
    on induccion_examen (persona_cargo_id);

create index if not exists idx_medicamento_n
    on medicamento (n);

create index if not exists idx_persona_campo_valor_campo_id
    on persona_campo_valor (campo_id);

create index if not exists idx_persona_cargo_laboral_cargo_id
    on persona_cargo_laboral (cargo_id);

create index if not exists idx_persona_cargo_laboral_persona_id
    on persona_cargo_laboral (persona_id);

create index if not exists idx_riesgo_procedencia_n
    on riesgo_procedencia (n);

create index if not exists idx_salud_n
    on salud (n);
