create table if not exists campo_base_meta (
    nombre_campo varchar(255) primary key,
    etiqueta varchar(255),
    tipo_dato varchar(50) not null default 'texto',
    actualizado_en timestamp not null default now()
);
