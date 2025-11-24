create table usuario
(
    uid        serial       not null primary key,
    uuid       UUID default gen_random_uuid(),
    name       varchar(100) not null,
    email      varchar(100) not null unique,
    pass       varchar(255) not null,
    photo      bytea,
    photo_type varchar(50)
);

create table cliente
(
    cid     serial       not null primary key,
    uuid    uuid default gen_random_uuid(),
    name    varchar(100) not null,
    phone   varchar(15)  not null,
    email   varchar(100) not null,
    address varchar(100) not null,
    u_id    integer      not null
);

create table servico
(
    sid         serial        not null primary key,
    uuid        uuid default gen_random_uuid(),
    service     varchar(50)   not null,
    description text          not null,
    value       numeric(5, 2) not null,
    u_id        integer       not null
);

create table ordem_servico
(
    osid        serial        not null primary key,
    uuid        uuid default gen_random_uuid(),
    c_id        integer       not null,
    device      varchar(50)   not null,
    opendate    timestamp     not null,
    closedate   timestamp,
    status      varchar(20)   not null,
    description text          not null,
    extras      numeric(6, 2) not null,
    discount    numeric(6, 2) not null,
    total       numeric(7, 2) not null,
    u_id        integer       not null
);

create table relacao_os
(
    os_id integer not null,
    s_id  integer not null,
    primary key (os_id, s_id),
    constraint fk_relacao_os foreign key (os_id) references ordem_servico (osid),
    constraint fk_relacao_servico foreign key (s_id) references servico (sid)
);

alter table cliente
    add constraint fk_cliente_usuario foreign key (u_id) references usuario (uid);
alter table servico
    add constraint fk_servico_usuario foreign key (u_id) references usuario (uid);
alter table ordem_servico
    add constraint fk_ordem_cliente foreign key (c_id) references cliente (cid);
alter table ordem_servico
    add constraint fk_ordem_usuario foreign key (u_id) references usuario (uid);
