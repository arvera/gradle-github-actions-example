-- USERS
--insert into USERS( username, password, enabled)values('admin','admin',1);
--insert into USERS( username, password, enabled)values('cash1','cash1',1);
--insert into USERS( username, password, enabled)values('cash2','cash2',1);
--insert into USERS( username, password, enabled)values('avera','avera',1);
--insert into USERS( username, password, enabled)values('nobody','nobody',1);
-- for deployment 
insert into USERS( username, password, enabled)values('admin','mud-jazz-denial',1);
insert into USERS( username, password, enabled)values('cash1','mud-jazz-denial-cash1',1);
insert into USERS( username, password, enabled)values('cash2','mud-jazz-denial-cash2',1);
insert into USERS( username, password, enabled)values('avera','mud-jazz-denial-',1);
insert into USERS( username, password, enabled)values('tim','mud-jazz-denial-tim',1);



-- ROLES FOR EACH USER (DETRERMINES WHAT A USER CAN ACCESS)
INSERT INTO AUTHORITIES(username,authority) VALUES('admin','ROLE_TELLER');
INSERT INTO AUTHORITIES( username,authority) VALUES('admin','ROLE_ADMIN');
INSERT INTO AUTHORITIES( username,authority) VALUES('cash1','ROLE_TELLER');
INSERT INTO AUTHORITIES( username,authority) VALUES('cash2','ROLE_TELLER');
INSERT INTO AUTHORITIES( username,authority) VALUES('avera','ROLE_TELLER');
INSERT INTO AUTHORITIES( username,authority) VALUES('avera','ROLE_ADMIN');


-- DEFAULT TYPE OF VENDORS
INSERT INTO VENDORTYPE(vendortype_id, vendortype_name, commission, minCatEntryCost, minComission, listItemsOnCheque)
				values(1,'Public',19,5,2,1);
INSERT INTO VENDORTYPE(vendortype_id, vendortype_name, commission, minCatEntryCost, minComission, listItemsOnCheque)
                values(2,'Ski Patrollers - KZ',0,0,0,1);
INSERT INTO VENDORTYPE(vendortype_id, vendortype_name, commission, minCatEntryCost, minComission, listItemsOnCheque)
                values(3,'Vendor - Dealer',17,10,2,1);
                

-- DEFAULT TAXES - ONTARIO
INSERT INTO TAXENTRY (Name,REGISTRATION,PERCENTAGE,INCLUDEINREFUND)
				values ('HST','0',(0.13),true);

-- DEFAULT TAXES FOR VENDORS
INSERT INTO vendortype_tax(vendorType_id,taxentry_id)
				values(1,1);
INSERT INTO vendortype_tax(vendorType_id,taxentry_id)
				values(2,1);
INSERT INTO vendortype_tax(vendorType_id,taxentry_id)
				values(3,1);

				
                
                
-- TYPE OF ITEMS
-- VALUES(ID,NAME);
INSERT INTO catentrytype( CATENTRYTYPE_ID,CATENTRYTYPE_NAME) VALUES(1,'DH Boots');
INSERT INTO catentrytype( CATENTRYTYPE_ID,CATENTRYTYPE_NAME) VALUES(2,'DH Ski');
INSERT INTO catentrytype( CATENTRYTYPE_ID,CATENTRYTYPE_NAME) VALUES(3,'Snowboard');
INSERT INTO catentrytype( CATENTRYTYPE_ID,CATENTRYTYPE_NAME) VALUES(4,'Snowboard Boot');



-- PAYMENT METHOD
INSERT INTO paymenttype(NAME,COMISSION,IS_TRANS) VALUES('CASH',0,false);
INSERT INTO paymenttype(NAME,COMISSION,IS_TRANS) VALUES('DEBIT',0,true);
INSERT INTO paymenttype(NAME,COMISSION,IS_TRANS) VALUES('VISA',0,false);
INSERT INTO paymenttype(NAME,COMISSION,IS_TRANS) VALUES('MC',0,false);
INSERT INTO paymenttype(NAME,COMISSION,IS_TRANS) VALUES('AMEX',0,false);

