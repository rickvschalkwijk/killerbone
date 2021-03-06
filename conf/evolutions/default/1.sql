# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  event_id                  bigint auto_increment not null,
  eventful_id               varchar(255),
  title                     varchar(255),
  description               TEXT,
  start_date                datetime,
  end_date                  datetime,
  category_event_category_id bigint,
  latitude                  double,
  longitude                 double,
  is_free                   tinyint(1) default 0,
  price                     varchar(255),
  thumb_url                 varchar(255),
  image_url                 varchar(255),
  creation_timestamp        bigint,
  modified_timestamp        bigint,
  constraint uq_event_eventful_id unique (eventful_id),
  constraint pk_event primary key (event_id))
;

create table event_category (
  event_category_id         bigint auto_increment not null,
  title                     varchar(255),
  system_name               varchar(255),
  constraint pk_event_category primary key (event_category_id))
;

create table friendship (
  friendship_id             bigint auto_increment not null,
  initiator_user_id         bigint,
  participant_user_id       bigint,
  status                    varchar(1),
  request_date              datetime,
  approval_date             datetime,
  constraint ck_friendship_status check (status in ('S','P','E','A','D')),
  constraint uq_friendship_1 unique (initiator_user_id ,participant_user_id),
  constraint pk_friendship primary key (friendship_id))
;

create table friendship_location (
  friendship_location_id    bigint auto_increment not null,
  latitude                  double not null,
  longitude                 double not null,
  user_user_id              bigint,
  friendship_friendship_id  bigint,
  refresh_date              datetime,
  constraint pk_friendship_location primary key (friendship_location_id))
;

create table location (
  location_id               bigint auto_increment not null,
  title                     varchar(255) not null,
  description               TEXT,
  image_url                 varchar(255),
  latitude                  double not null,
  longitude                 double not null,
  creation_timestamp        bigint not null,
  modification_timestamp    bigint,
  category_location_category_id bigint,
  constraint uq_location_title unique (title),
  constraint pk_location primary key (location_id))
;

create table location_category (
  location_category_id      bigint auto_increment not null,
  title                     varchar(255),
  system_name               varchar(255),
  constraint pk_location_category primary key (location_category_id))
;

create table location_review (
  location_review_id        bigint auto_increment not null,
  location_location_id      bigint not null,
  user_user_id              bigint not null,
  rating                    integer,
  constraint pk_location_review primary key (location_review_id))
;

create table setting (
  setting_id                bigint auto_increment not null,
  setting_key               varchar(50),
  setting_value             varchar(255),
  constraint uq_setting_setting_key unique (setting_key),
  constraint pk_setting primary key (setting_id))
;

create table user (
  user_id                   bigint auto_increment not null,
  name                      varchar(50),
  email                     varchar(50),
  hashed_password           varchar(255),
  is_admin                  tinyint(1) default 0,
  is_activated              tinyint(1) default 0,
  last_activity_date        datetime,
  creation_date             datetime,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (user_id))
;

alter table event add constraint fk_event_category_1 foreign key (category_event_category_id) references event_category (event_category_id) on delete restrict on update restrict;
create index ix_event_category_1 on event (category_event_category_id);
alter table friendship add constraint fk_friendship_initiator_2 foreign key (initiator_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_friendship_initiator_2 on friendship (initiator_user_id);
alter table friendship add constraint fk_friendship_participant_3 foreign key (participant_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_friendship_participant_3 on friendship (participant_user_id);
alter table friendship_location add constraint fk_friendship_location_user_4 foreign key (user_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_friendship_location_user_4 on friendship_location (user_user_id);
alter table friendship_location add constraint fk_friendship_location_friendship_5 foreign key (friendship_friendship_id) references friendship (friendship_id) on delete restrict on update restrict;
create index ix_friendship_location_friendship_5 on friendship_location (friendship_friendship_id);
alter table location add constraint fk_location_category_6 foreign key (category_location_category_id) references location_category (location_category_id) on delete restrict on update restrict;
create index ix_location_category_6 on location (category_location_category_id);
alter table location_review add constraint fk_location_review_location_7 foreign key (location_location_id) references location (location_id) on delete restrict on update restrict;
create index ix_location_review_location_7 on location_review (location_location_id);
alter table location_review add constraint fk_location_review_user_8 foreign key (user_user_id) references user (user_id) on delete restrict on update restrict;
create index ix_location_review_user_8 on location_review (user_user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table event;

drop table event_category;

drop table friendship;

drop table friendship_location;

drop table location;

drop table location_category;

drop table location_review;

drop table setting;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

