delete from termtypes;
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (1, 'school', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (2, 'openclass', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (3, 'rehearsal', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (4, 'company', NULL);
INSERT INTO termtypes (termtypeid, name, orderid) VALUES (5, 'legacy', NULL);
ALTER SEQUENCE termtypes_termtypeid_seq RESTART WITH 6;


CREATE TABLE openclasscompids
(
-- Inherited:   groupid integer NOT NULL DEFAULT nextval('groupids_groupid_seq'::regclass),
-- Inherited:   name character varying(100) NOT NULL,
  PRIMARY KEY (groupid)
) INHERITS (groupids);

CREATE TABLE openclasscomps
(
  entityid integer NOT NULL,
  groupid integer NOT NULL,
  fromdate date,
  todate date,
  PRIMARY KEY (entityid, groupid)
);

insert into openclasscompids (name) values ('student');
insert into openclasscompids (name) values ('comp');

CREATE TABLE classpackages
(
   name character varying(50) primary key,
   price numeric(9, 2) NOT NULL, 
   assetid integer NOT NULL, 
   amount numeric(9, 2) NOT NULL,
begindate date,
enddate date
) WITHOUT OIDS;
