# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table client (
  id                            bigint not null,
  user_id                       bigint,
  nif                           varchar(255),
  name                          varchar(255),
  lastname                      varchar(255),
  phone                         varchar(255),
  city                          varchar(255),
  address                       varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint uq_client_user_id unique (user_id),
  constraint pk_client primary key (id)
);
create sequence client_seq;

create table expedient (
  id                            bigint not null,
  client_exp_id                 bigint,
  code                          varchar(255),
  mark                          float,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_expedient primary key (id)
);
create sequence expedient_seq;

create table inspector (
  id                            bigint not null,
  user_id                       bigint,
  nif                           varchar(255),
  name                          varchar(255),
  lastname                      varchar(255),
  phone                         varchar(255),
  city                          varchar(255),
  address                       varchar(255),
  inspector_code                varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint uq_inspector_user_id unique (user_id),
  constraint pk_inspector primary key (id)
);
create sequence inspector_seq;

create table inspector_expedient (
  inspector_id                  bigint not null,
  expedient_id                  bigint not null,
  constraint pk_inspector_expedient primary key (inspector_id,expedient_id)
);

create table plot (
  id                            bigint not null,
  expedient_id                  bigint,
  enclosure                     integer,
  surface                       float,
  product                       varchar(255),
  harvest                       float,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_plot primary key (id)
);
create sequence plot_seq;

create table qualification (
  id                            bigint not null,
  expedient_id                  bigint,
  mark                          float,
  mark_date                     timestamp,
  inspector                     varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_qualification primary key (id)
);
create sequence qualification_seq;

create table user (
  id                            bigint not null,
  username                      varchar(255),
  password                      varchar(255),
  authtoken                     varchar(255),
  token_date                    timestamp,
  type                          varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_updated                  timestamp not null,
  constraint pk_user primary key (id)
);
create sequence user_seq;

alter table client add constraint fk_client_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table expedient add constraint fk_expedient_client_exp_id foreign key (client_exp_id) references client (id) on delete restrict on update restrict;
create index ix_expedient_client_exp_id on expedient (client_exp_id);

alter table inspector add constraint fk_inspector_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table inspector_expedient add constraint fk_inspector_expedient_inspector foreign key (inspector_id) references inspector (id) on delete restrict on update restrict;
create index ix_inspector_expedient_inspector on inspector_expedient (inspector_id);

alter table inspector_expedient add constraint fk_inspector_expedient_expedient foreign key (expedient_id) references expedient (id) on delete restrict on update restrict;
create index ix_inspector_expedient_expedient on inspector_expedient (expedient_id);

alter table plot add constraint fk_plot_expedient_id foreign key (expedient_id) references expedient (id) on delete restrict on update restrict;
create index ix_plot_expedient_id on plot (expedient_id);

alter table qualification add constraint fk_qualification_expedient_id foreign key (expedient_id) references expedient (id) on delete restrict on update restrict;
create index ix_qualification_expedient_id on qualification (expedient_id);


# --- !Downs

alter table client drop constraint if exists fk_client_user_id;

alter table expedient drop constraint if exists fk_expedient_client_exp_id;
drop index if exists ix_expedient_client_exp_id;

alter table inspector drop constraint if exists fk_inspector_user_id;

alter table inspector_expedient drop constraint if exists fk_inspector_expedient_inspector;
drop index if exists ix_inspector_expedient_inspector;

alter table inspector_expedient drop constraint if exists fk_inspector_expedient_expedient;
drop index if exists ix_inspector_expedient_expedient;

alter table plot drop constraint if exists fk_plot_expedient_id;
drop index if exists ix_plot_expedient_id;

alter table qualification drop constraint if exists fk_qualification_expedient_id;
drop index if exists ix_qualification_expedient_id;

drop table if exists client;
drop sequence if exists client_seq;

drop table if exists expedient;
drop sequence if exists expedient_seq;

drop table if exists inspector;
drop sequence if exists inspector_seq;

drop table if exists inspector_expedient;

drop table if exists plot;
drop sequence if exists plot_seq;

drop table if exists qualification;
drop sequence if exists qualification_seq;

drop table if exists user;
drop sequence if exists user_seq;

