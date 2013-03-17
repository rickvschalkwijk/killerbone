# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  user_id                   bigint auto_increment not null,
  name                      varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  creation_date             datetime,
  constraint pk_user primary key (user_id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

