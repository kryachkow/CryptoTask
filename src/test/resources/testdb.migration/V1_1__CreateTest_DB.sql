create table if not exists `application_user` (
    `id` bigint not null auto_increment primary key,
    `user_name` varchar(255) not null unique,
    `password` varchar(255) not null
);

create table if not exists `role` (
    `id` bigint not null auto_increment primary key,
    `role_name` varchar(255) not null unique
);

create table if not exists `authority` (
    `id` bigint not null auto_increment  primary key,
    `authority_name` varchar(255) not null unique
);

create table if not exists `role_authority` (
    `role_id` bigint not null,
    `authority_id` bigint not null,
    constraint fk_role_to_authority_id foreign key ("role_id") references "role"("id") on update no action on delete no action,
    constraint fk_authority_to_role_id foreign key ("authority_id") references "authority"("id") on update no action on delete no action
);

create table if not exists `application_user_role` (
    `application_user_id` bigint not null,
    `role_id` bigint not null,
    constraint fk_role_to_application_user_id foreign key ("role_id") references "role"("id") on update no action on delete no action,
    constraint fk_appliction_user_to_role_id foreign key ("application_user_id") references "application_user"("id") on update no action on delete no action
);

insert into `authority` ("authority_name") VALUES ('AUTHORITY_READ'), ('AUTHORITY_WRITE');
insert into `role` ("role_name") VALUES ('ROLE_USER'), ('ROLE_ADMIN');
insert into `application_user` ("user_name", "password") VALUES('testUser', '$2a$10$CYgt7ph3F6Uw53Rn2365luOs8FKV77Zj./VxeGNWYDqoikD/xhSb2'), ('testAdmin', '$2a$10$u/NVskqrBxNtAWheaApmf.HbR2VtNB85efIxPubIO6BUgFCoDfTIW');

insert into `role_authority` ("role_id", "authority_id")  VALUES (1,1), (2,2), (2,1);
insert into `application_user_role` ("application_user_id", "role_id") VALUES (1,1), (2,2);


