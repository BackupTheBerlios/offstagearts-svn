ALTER TABLE termregs ADD COLUMN scholarshippct double precision;

-- Different kinds of assets that can be kept track of in the account
-- CurrencyID=0 means cash.

create table assetids (assetid serial primary key, name varchar(30) not null);
insert into assetids (assetid, name) values (0, 'USD');
insert into assetids (name) values ('openclass');

--ALTER TABLE actrans ADD COLUMN assetid integer not null default 0;
--ALTER TABLE actrans ADD COLUMN dualactransid integer;

--ALTER TABLE acbal DROP CONSTRAINT acbal_pkey;
--ALTER TABLE acbal ADD COLUMN assetid integer not null default 0;
--ALTER TABLE acbal ADD CONSTRAINT acbal_pkey PRIMARY KEY (entityid, actypeid, assetid);

CREATE TABLE actrans2
(
  actransid serial NOT NULL PRIMARY KEY,
  cr_entityid integer NOT NULL,
  cr_actypeid integer NOT NULL,
  db_entityid integer NOT NULL,
  db_actypeid integer NOT NULL,
  actranstypeid integer NOT NULL,

  date date NOT NULL,
  datecreated date,
--  amount numeric(9,2), -- like a credit card; >0 means customer owes
  description character varying(300),
  studentid integer, -- Student for whom this is a tuition record
  termid integer,
  cc_type character(1),
  cc_info character varying(255),
  py_name character varying(50), -- Name on check or credit card --- or name check was written to.
  cc_last4 character varying(4),
  cc_expdate character varying(4),
  cc_batchid integer, -- Batch the credit card payment was processed
  ck_number character varying(15),
  py_phone character varying(30) -- Contact phone # on check or credit card.
);

CREATE TABLE actrans2amt
(
  actransid int NOT NULL REFERENCES actrans2 ON DELETE CASCADE,
  assetid int NOT NULL,
  amount numeric(9,2) not null,
  PRIMARY KEY(actransid, assetid)
);

CREATE TABLE acbal2
(
  entityid integer NOT NULL,
  actypeid integer NOT NULL,
  acbalid serial NOT NULL,
  dtime timestamp without time zone NOT NULL DEFAULT now(),
--  bal numeric(9,2),
  PRIMARY KEY (entityid, actypeid), UNIQUE (acbalid)
);

CREATE TABLE acbal2amt
(
  acbalid integer NOT NULL REFERENCES acbal2(acbalid) ON DELETE CASCADE,
  assetid int NOT NULL,
  bal numeric(9,2),
  PRIMARY KEY (acbalid, assetid)
);

--create table acsinks (
--  entityid integer NOT NULL,
--  name varchar(20) NOT NULL,
--  primary key(entityid), unique(name)
--);

ALTER TABLE entities
   ADD COLUMN sink boolean;
update entities set sink=false;
ALTER TABLE entities
   ALTER COLUMN sink SET NOT NULL;
ALTER TABLE entities
   ALTER COLUMN sink SET DEFAULT false;


insert into entities (orgname, obsolete, sink, primaryentityid) values ('received', true, true, -1);
insert into entities (orgname, obsolete, sink, primaryentityid) values ('billed', true, true, -1);
update entities set primaryentityid=entityid where sink;

--insert into acsinks (entityid, name) values (
--	select entityid from entities where orgname='received' and sink, 'received');
--insert into acsinks (entityid, name) values (
--	select entityid from entities where orgname='billed' and sink, 'billed');

--insert into acsinks (entityid, name) values (-1, 'received');
--update acsinks
--set entityid = e.entityid
--from entities e
--where e.orgname = 'received' and e.sink
--and acsinks.entityid = -1;
--
--insert into acsinks (entityid, name) values (-1, 'billed');
--update acsinks
--set entityid = e.entityid
--from entities e
--where e.orgname = 'billed' and e.sink
--and acsinks.entityid = -1;



