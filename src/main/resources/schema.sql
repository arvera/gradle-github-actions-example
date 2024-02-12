-- drop table authorities;
-- drop table users;
-- drop table vendortype;
-- drop table Taxentry;
-- drop table vendortype_tax;
-- drop table vendor;
-- drop table catentrytype;
-- drop table inventory;
-- drop table catentry;
-- drop table orders;
-- drop table paymenttype;
-- drop table discounts;
-- drop table payment;
-- drop table orderitems;
-- drop table CNSfiles;
-- drop table audit;

create table users (
    username varchar(50) primary key not null,
    password varchar(120) not null,
    enabled int not null
);


create table authorities (
	username varchar(50) references users (username),
	authority varchar(50) not null
);

create unique index ix_auth_username on authorities (username,authority);



-- VENDOR



create table vendortype (
	vendortype_id integer primary key not null,
  	vendortype_name varchar(500),
  	commission int not null,
  	minCatEntryCost int not null,
  	minComission int not null,
  	listItemsOnCheque int not null
);



create table taxentry (
     id int primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
     name varchar(500),
     registration varchar(500),
     percentage decimal(5,2) not null,
     includeInRefund boolean
);

create table vendortype_tax (
     vendortype_id integer references vendortype(vendortype_id),
     taxentry_id integer references taxentry(id)
);  

create table vendor (
	id integer primary key not null,
  	firstname varchar(100),
  	lastname varchar(100),
  	address1 varchar(200),
  	address2 varchar(200),
  	city varchar(50),
  	province varchar(50),
  	postalCode varchar(10),
  	country varchar(50),
  	email varchar(100),
  	phone varchar(100),
  	comment varchar(5000),
  	vendortype_id integer references vendortype (vendortype_id)
);


-- ITEM / CATENTRY

create table catentrytype (
	CATENTRYTYPE_ID integer primary key not null,
	CATENTRYTYPE_NAME varchar(50) not null
);


create table Inventory (
	ID integer  primary key not null, -- GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
	status varchar(50) not null,
	qty integer not null
);

create table catentry (
	ID varchar(50) primary key not null, -- CSV:BARCODE
	NAME varchar(50) not null, -- CSV:NAME
	DESCRIPTION varchar(300), -- DESCRIPTION 
	SIZE varchar (10), -- CSV: SIZE 
	NEW boolean, -- CSV: New
	PRICE Bigint not null, -- CSV: Price
	CATENTRYTYPE integer references catentrytype(CATENTRYTYPE_ID), -- CSV: TYPE
	Vendor integer references vendor(id), -- 
	Inventory integer references Inventory(ID), --
	comment varchar(5000)
);


-- TRANSACTION --
-- Based on this article I decided to use decimal in derby and bigdecimal in java 
-- Storing money in a decimal column - what precision and scale?: 
--      https://stackoverflow.com/questions/224462/storing-money-in-a-decimal-column-what-precision-and-scale
-- Supported Data Types: 
--      https://docs.oracle.com/cd/E19501-01/819-3659/gcmaz/

create table orders (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  discount_id int,
  total decimal(19,3),
  subtotal decimal(19,3),
  status varchar(10), -- COMPLETED,PENDING,REFUNDED
  trans_time timestamp,
  trans_username varchar(50) references users (username),
  comment varchar(5000)
);

create table taxinstructions (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  ORDERSID integer references orders(id),
  NAME varchar(50) not null,
  TOTAL decimal(19,3) not null
);

create table paymenttype (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  name varchar(50) not null,
  COMISSION int not null,
  IS_TRANS boolean not null 
);


create table discounts (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  auth_username varchar(50),
  comment varchar(300),
  discount_type int not null,
  total decimal(19,3) not null
);

create table payment (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  ordersId integer references orders(id),
  total decimal(19,3) not null,
  auth varchar(50) not null,
  paymenttype_id integer references paymenttype(id)
);



create table orderitems (
  id integer primary key GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), 
  catentry_id varchar(50) not null,
  orders_id integer references orders(id),
  name varchar(50) not null,
  description varchar(300),
  size  varchar(10) not null,
  type  varchar(50) not null,
  unitprice  decimal(19,3) not null,
  discount_id integer references discounts(id),
  comment varchar(5000)
);




-- STORAGE


create table CNSFiles (
	filename varchar(1000),
	cnsType varchar(5),
	url varchar(1000),
	uploadedTime timestamp,
	size bigint,
	HRSize varchar(50),
	recordsProcessed int,
	totalOfRecords int
);

-- AUDITING

create table audit (
	id integer  primary key not null,
	tablename varchar (50),
	columnname varchar (50),
	oldvalue varchar (5000),
	newvalue varchar (5000),
	byuser varchar(100),
	comment varchar(5000),
	actiontime timestamp
);