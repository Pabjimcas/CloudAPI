# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table client (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  nif                           varchar(255),
  name                          varchar(255),
  lastname                      varchar(255),
  phone                         varchar(255),
  city                          varchar(255),
  address                       varchar(255),
  constraint uq_client_user_id unique (user_id),
  constraint pk_client primary key (id)
);

create table expedient (
  id                            bigint auto_increment not null,
  client_exp_id                 bigint,
  code                          varchar(255),
  average_mark                  float,
  constraint pk_expedient primary key (id)
);

create table inspector (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  nif                           varchar(255),
  name                          varchar(255),
  lastname                      varchar(255),
  phone                         varchar(255),
  city                          varchar(255),
  address                       varchar(255),
  inspector_code                varchar(255),
  constraint uq_inspector_user_id unique (user_id),
  constraint pk_inspector primary key (id)
);

create table inspector_expedient (
  inspector_id                  bigint not null,
  expedient_id                  bigint not null,
  constraint pk_inspector_expedient primary key (inspector_id,expedient_id)
);

create table plot (
  id                            bigint auto_increment not null,
  expedient_id                  bigint,
  enclosure                     integer,
  surface                       float,
  product                       varchar(255),
  harvest                       float,
  constraint pk_plot primary key (id)
);

create table qualification (
  id                            bigint auto_increment not null,
  expedient_id                  bigint,
  mark                          float,
  inspector                     varchar(255),
  constraint pk_qualification primary key (id)
);

create table user (
  id                            bigint auto_increment not null,
  username                      varchar(255),
  password                      varchar(255),
  authtoken                     varchar(255),
  type                          varchar(255),
  constraint pk_user primary key (id)
);

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

alter table client drop foreign key fk_client_user_id;

alter table expedient drop foreign key fk_expedient_client_exp_id;
drop index ix_expedient_client_exp_id on expedient;

alter table inspector drop foreign key fk_inspector_user_id;

alter table inspector_expedient drop foreign key fk_inspector_expedient_inspector;
drop index ix_inspector_expedient_inspector on inspector_expedient;

alter table inspector_expedient drop foreign key fk_inspector_expedient_expedient;
drop index ix_inspector_expedient_expedient on inspector_expedient;

alter table plot drop foreign key fk_plot_expedient_id;
drop index ix_plot_expedient_id on plot;

alter table qualification drop foreign key fk_qualification_expedient_id;
drop index ix_qualification_expedient_id on qualification;

drop table if exists client;

drop table if exists expedient;

drop table if exists inspector;

drop table if exists inspector_expedient;

drop table if exists plot;

drop table if exists qualification;

drop table if exists user;

