# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table friendship (
  friendship_id             bigint auto_increment not null,
  initiator_user_id         bigint,
  participant_user_id       bigint,
  status                    varchar(1),
  confirmation_code         varchar(6),
  approval_date             datetime,
  constraint ck_friendship_status check (status in ('S','P','A','D')),
  constraint pk_friendship primary key (friendship_id))
;

create table user (
  user_id                   bigint auto_increment not null,
  name                      varchar(50),
  email                     varchar(50),
  password                  varchar(255),
  last_known_location       varchar(255),
  creation_date             datetime,
  constraint pk_user primary key (user_id))
;

alter table friendship add constraint fk_friendship_initiator_1 foreign key (initiator_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_friendship_initiator_1 on friendship (initiator_user_id);
alter table friendship add constraint fk_friendship_participant_2 foreign key (participant_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_friendship_participant_2 on friendship (participant_user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table friendship;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

