create table U_USER
(
    ID BIGINT auto_increment primary key,
    USERNAME CHARACTER VARYING(48) not null unique,
    PASSWORD CHARACTER VARYING(48),
    NAME     CHARACTER VARYING(24)
);

create table G_CHAT
(
    ID   BIGINT auto_increment primary key,
    NAME CHARACTER VARYING(48)
);

create table H_MSG
(
    ID        BIGINT auto_increment primary key,
    SENDER    BIGINT  not null,
    RECEIVER  BIGINT  not null,
    TIMESTAMP BIGINT  not null,
    TYPE      INTEGER not null,
    STATUS    INTEGER not null,
    MSG       CHARACTER VARYING(2048)
);

create table U_GROUP
(
    ID       BIGINT auto_increment primary key,
    USER_ID  BIGINT not null,
    GROUP_ID BIGINT not null
);