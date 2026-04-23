create table if not exists campo_personalizado_meta (
    campo_id bigint primary key,
    etiqueta varchar(255),
    tipo_dato varchar(50) not null default 'texto',
    actualizado_en timestamp not null default now()
);
