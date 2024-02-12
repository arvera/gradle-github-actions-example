# File created for DERBY 10.1

drop table authorities;
drop table users; 


create table users (
    users_id int primary key  not null,
    username varchar(50) not null,
    password varchar(120) not null,
    enabled int not null
);


create table authorities (
	authority_id int primary key not null,
	users_id int references users (users_id),
	authority varchar(50) not null
);


insert into USERS(users_id, username, password, enabled)values(1,'admin','admin',1);
insert into USERS(users_id, username, password, enabled)values(2,'cash1','cash1',1);
insert into USERS(users_id, username, password, enabled)values(3,'cash2','cash2',1);
insert into USERS(users_id, username, password, enabled)values(4,'avera','avera',1);
insert into USERS(users_id, username, password, enabled)values(9999,'nobody','nobody',1);


INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(1,1,'ROLE_TELLER');
INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(2,1,'ROLE_ADMIN');
INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(3,2,'ROLE_TELLER');
INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(4,3,'ROLE_TELLER');
INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(5,4,'ROLE_TELLER');
INSERT INTO AUTHORITIES(authority_id, users_id,authority) VALUES(6,4,'ROLE_ADMIN');
