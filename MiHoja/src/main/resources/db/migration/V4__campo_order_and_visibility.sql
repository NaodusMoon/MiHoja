alter table if exists campo_base_meta
    add column if not exists orden_mostrar integer,
    add column if not exists oculto boolean not null default false;

alter table if exists campo_personalizado_meta
    add column if not exists orden_mostrar integer;
