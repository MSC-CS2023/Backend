create table customer
(
    id                   bigint          not null
        primary key,
    username             varchar(255)    not null,
    email                varchar(255)    not null,
    address              varchar(255)    null,
    tel                  varchar(255)    null,
    avatar_path          varchar(255)    null,
    avatar_timestamp     bigint          null,
    preference_vector    varchar(255)    null,
    preference_timestamp bigint          null,
    balance              float default 0 not null
);

create table customer_password
(
    id       bigint       not null
        primary key,
    password varchar(255) not null,
    constraint customer_password_ibfk_1
        foreign key (id) references customer (id)
);

create table service_pics
(
    id         bigint       not null
        primary key,
    service_id bigint       not null,
    pic_path   varchar(255) not null,
    timestamp  bigint       not null
);

create table service_provider
(
    id               bigint          not null
        primary key,
    username         varchar(255)    not null,
    email            varchar(255)    not null,
    address          varchar(255)    null,
    tel              varchar(255)    null,
    avatar_path      varchar(255)    null,
    avatar_timestamp bigint          null,
    balance          float default 0 null,
    mark             float default 0 not null
);

create table service
(
    id          bigint                  not null
        primary key,
    title       varchar(255)            not null,
    description varchar(255)            null,
    fee         float                   not null,
    provider_id bigint                  null,
    timestamp   bigint                  not null,
    tag         varchar(255) default '' not null,
    constraint service_ibfk_1
        foreign key (provider_id) references service_provider (id)
);

create table booking_order
(
    id                     bigint not null
        primary key,
    service_id             bigint not null,
    customer_id            bigint not null,
    creation_timestamp     bigint not null,
    start_timestamp        bigint not null,
    end_timestamp          bigint not null,
    is_confirmed           bit    not null,
    confirmation_timestamp bigint null,
    is_rejected            bit    not null,
    rejection_timestamp    bigint null,
    is_canceled            bit    not null,
    cancel_timestamp       bigint null,
    is_finished            bit    not null,
    finish_timestamp       bigint null,
    mark                   int    null,
    constraint booking_order_ibfk_1
        foreign key (service_id) references service (id),
    constraint booking_order_ibfk_2
        foreign key (customer_id) references customer (id)
);

create index customer_id
    on booking_order (customer_id);

create index service_id
    on booking_order (service_id);

create table customer_log
(
    id          bigint not null
        primary key,
    customer_id bigint not null,
    service_id  bigint not null,
    timestamp   bigint not null,
    constraint customer_log_ibfk_1
        foreign key (customer_id) references customer (id),
    constraint customer_log_ibfk_2
        foreign key (service_id) references service (id)
);

create index costomer_id
    on customer_log (customer_id);

create index service_id
    on customer_log (service_id);

create table favourite
(
    id          bigint not null
        primary key,
    service_id  bigint not null,
    customer_id bigint not null,
    timestamp   bigint not null,
    constraint favourite_ibfk_1
        foreign key (service_id) references service (id),
    constraint favourite_ibfk_2
        foreign key (customer_id) references customer (id)
);

create index customer_id
    on favourite (customer_id);

create index service_id
    on favourite (service_id);

create index provider_id
    on service (provider_id);

create table service_provider_password
(
    id       bigint       not null
        primary key,
    password varchar(255) null,
    constraint service_provider_password_ibfk_1
        foreign key (id) references service_provider (id)
);

create table session
(
    id             bigint           not null
        primary key,
    sender_id      bigint           not null,
    sender_type    varchar(255)     not null,
    recipient_id   bigint           not null,
    recipient_type varchar(255)     not null,
    message        varchar(255)     not null,
    timestamp      bigint           not null,
    unread         bit default b'1' not null
);

create table user
(
    id       varchar(10) charset ascii not null
        primary key,
    username varchar(10) charset ascii not null,
    password varchar(255)              null
);


