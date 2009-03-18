-- Codified relationships


-- Different KINDS of relationships treated differently
create table relcategoryids
(
relcategoryid serial primary key,
name varchar(30)
);

-- General family relationships can sprawl;
-- It is OK if they are orphaned at some point.
insert into relcategoryids (name) values ('family');


CREATE TABLE relids
(
   relid serial,
   "name" character varying(30) NOT NULL, 
   relcategoryid int not null,
   reltype int not null default 0,	-- 0=m2m, 1=o2m  NOTE USED: 2=m2o, 3=o2o
   sym boolean not null default false,	-- symmetrical?
   explicit boolean not null default true,	-- true if we add this epxlicitly to the rels table, rather than seeing it in a view
--   temporal boolean not null default false,	-- true if this relationship is good for one term or show or something
--   temptable varchar(50),	-- Table of temporal relationship (eg 'termids')
	req0 boolean not null default true,	-- Is entityid0 required to be non-obsolete?
	req1 boolean not null default true,-- Is entityid1 required to be non-obsolete?
description varchar(200),
    UNIQUE ("name"), 
    PRIMARY KEY (relid)
);


-- Devel relationships are fundamental to the integrity of the development
-- database, they must not be orphaned
insert into relcategoryids (name) values ('devel');
insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('headof', 1, false,  true, false,
	(select relcategoryid from relcategoryids where name='devel'));
insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('parent1of', 1, false,  true, false,
	(select relcategoryid from relcategoryids where name='devel'));
insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('parent2of',  1, false,  true, false,
	(select relcategoryid from relcategoryids where name='devel'));

-- Term relationships are good for one term of the school only.
insert into relcategoryids (name) values ('term');
insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('payerof', 1, false,  true, false,
	(select relcategoryid from relcategoryids where name='term'));

-- many-to-many relationships
CREATE TABLE rels
(
   relid integer NOT NULL, 
   entityid0 integer NOT NULL, 
   entityid1 integer NOT NULL,
   temporalid integer not null default -1,		-- Matches to a termid or showid or something (-1 if forever)
primary key(relid,temporalid,entityid0,entityid1)
);
create index rels_entityid0 on rels (temporalid,entityid0);
create index rels_entityid1 on rels (temporalid,entityid1);

-- One-to-many relationships
create table rels_o2m (
primary key(relid,temporalid,entityid1)
) inherits (rels);



-- Function: w_rels_o2m_set(integer, integer, integer, integer)

-- DROP FUNCTION w_rels_o2m_set(integer, integer, integer, integer, bool);

CREATE OR REPLACE FUNCTION w_rels_o2m_set(xrelid integer, xtemporalid integer,
xentityid0 integer, xentityid1 integer, nullifequal bool)
  RETURNS void AS
'
BEGIN
	if (nullifequal and xentityid0 = xentityid1) or xentityid0 is null then
		delete from rels where relid = xrelid and temporalid = xtemporalid and entityid1 = xentityid1;
	elseif xentityid1 is null then
		delete from rels where relid = xrelid and temporalid = xtemporalid and entityid0 = xentityid0;
	else
		BEGIN
		    insert into rels_o2m (relid,temporalid,entityid0,entityid1) values
			(xrelid,xtemporalid,xentityid0,xentityid1);
	    EXCEPTION WHEN unique_violation THEN
			update rels_o2m set entityid0=xentityid0
			where relid=xrelid
			and temporalid=xtemporalid
			and entityid1=xentityid1;
		END;
	end if;
END
'
  LANGUAGE 'plpgsql' VOLATILE;


-- Modify old version of this function
-- DROP FUNCTION w_student_register(integer, integer, date);

CREATE OR REPLACE FUNCTION w_student_register(xtermid integer, studentid integer, xdtregistered date)
  RETURNS void AS
'

BEGIN
    BEGIN
		insert into termregs (groupid, entityid, dtregistered) values
			(xtermid, studentid, xdtregistered);
		execute w_rels_o2m_set(
			(select relid from relids where name = ''payerof''), xtermid,
			studentid, studentid, false);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;


END;
'
  LANGUAGE 'plpgsql' VOLATILE;


-- We don't need these (for now)
--create table rels_m2o (
--primary key(relid,temporalid,entityid0)
--) inherits (rels);


-- NOTE: This is slow.  We really need to convert our relationships
-- to all be the explicit form
--CREATE OR REPLACE VIEW allrels AS
--select relid,temporalid,entityid0,entityid1, true as inrels
--from rels
--	UNION
--select relids.relid, -1, e.primaryentityid, e.entityid, false
--from entities e, relids
--where relids.name='headof'
--and e.primaryentityid is not null
--    UNION
--select relids.relid, -1, e.parent1id, e.entityid, false
--from entities e, relids
--where relids.name='parent1of'
--and e.parent1id is not null
--    UNION
--select relids.relid, -1, e.parent2id, e.entityid, false
--from entities e, relids
--where relids.name='parent2of'
--and e.parent2id is not null
--	UNION
--select relids.relid, tr.groupid as temporalid,tr.payerid as entityid0,tr.entityid as entityid1, false
--from termregs tr, relids
--where relids.name='payerof'
--and tr.payerid is not null
--;

-- Orphan Relationships
CREATE OR REPLACE VIEW orphanrels AS
select rid.name,rid.reltype,ar.*,e0.obsolete as obsolete0, e1.obsolete as obsolete1
from rels ar, entities e0, entities e1, relids rid
where ar.relid = rid.relid
and ar.entityid0 = e0.entityid and ar.entityid1 = e1.entityid
and (
	(rid.req0 and e0.obsolete and not e1.obsolete)
		or
	(rid.req1 and e1.obsolete and not e0.obsolete))
-- NOTE: Double-obsolete references are ignored
-- This also implies that "Orphaned" self-references are ignored:
-- and ar.entityid0 <> ar.entityid1
;

--CREATE OR REPLACE VIEW orphanrels AS
--select relids.name,ar.*,e0.obsolete as obsolete0, e1.obsolete as obsolete1
--from rels ar, entities e0, entities e1, relids
--where ar.relid = relids.relid
--and ar.entityid0 = e0.entityid and ar.entityid1 = e1.entityid
--and ((e0.obsolete and not e1.obsolete) or (e1.obsolete and not e0.obsolete))
--;

-- Merges that have been executed;
-- These are dug out of the mergelog
CREATE OR REPLACE VIEW finalmerges AS
select entityid0,entityid1 from mergelog
where action=2 and not provisional
	UNION
select entityid1,entityid0 from mergelog
where action=1 and not provisional
;

-- Transfer primaryentityid into rels table
insert into rels_o2m (relid,temporalid,entityid0,entityid1)
select relids.relid, -1, e.primaryentityid, e.entityid
from entities e, relids
where relids.name='headof'
and e.primaryentityid is not null
and e.primaryentityid <> e.entityid;

insert into rels_o2m (relid,temporalid,entityid0,entityid1)
select relids.relid, -1, e.parent1id, e.entityid
from entities e, relids
where relids.name='parent1of'
and e.parent1id is not null;
--and e.parent1id <> e.entityid;

insert into rels_o2m (relid,temporalid,entityid0,entityid1)
select relids.relid, -1, e.parent2id, e.entityid
from entities e, relids
where relids.name='parent2of'
and e.parent2id is not null;
--and e.parent2id <> e.entityid;

insert into rels_o2m (relid,temporalid,entityid0,entityid1)
select relids.relid, tr.groupid, tr.payerid, tr.entityid
from termregs tr,relids
where relids.name='payerof'
and tr.payerid is not null;
--and tr.payerid <> e.entityid;

-- Drop primaryentityid from the schema
drop function rfi_primaryentityid();
drop function rfi_primaryentityid_delete();
drop function w_organizations_new(integer);
drop function w_persons_new(integer);
drop function w_queries_new_entities(character varying, character varying);
drop function w_queries_new_organizations(character varying, character varying);
drop function w_queries_new_persons(character varying, character varying);
--drop function w_student_create();

ALTER TABLE entities DROP COLUMN primaryentityid;
ALTER TABLE entities DROP COLUMN relprimarytypeid;
drop table relprimarytypes;
ALTER TABLE entities DROP COLUMN parent1id;
ALTER TABLE entities DROP COLUMN parent2id;
ALTER TABLE termregs DROP COLUMN payerid;





-- ===================================================================
-- Everything after the following line will be ignored
-- ==EOF==

-- Some test data
insert into rels_o2m (relid, entityid0, entityid1) values (
(select relid from relids where name='parent1of'),
1,2);
insert into rels_o2m values (
(select relid from relids where name='parent1of'),
1,3);

-- Find all relationships for entityid=12633
select relids.name,ar.*
from rels ar, relids
where ar.temporalid=-1
and (ar.entityid0=12633 or ar.entityid1=12633)
and ar.relid = relids.relid;


-- Find others with same relationship as 12633
-- Used for "household" or "payer group" type of box
-- as a detail for one row of above query
select * from rels where entityid0=12633 and relid=3;

-- Suggest changes to un-orphan currently-orphaned relationships

-- This makes sure the "one" side of a one-to-many relationship
-- is valid.  It also makes sure both sides of a many-to-many relationship
-- are valid.
select orr.*,fm.entityid1 as new_entityid0
from orphanrels orr, finalmerges fm
where orr.entityid0=fm.entityid0

select orr.*,fm.entityid1 as new_entityid1
from orphanrels orr, finalmerges fm
where orr.entityid1=fm.entityid0
and reltype <> 1	-- Don't worry if the "many" side of a one-to-many relationship is obsolete

-- Easy-to-use table to join, to find primaryentityid
create view peids as
select e.entityid,
(case when r.entityid0 is null then e.entityid else r.entityid0 end) as primaryentityid
from entities e
left outer join rels r on (e.entityid = r.entityid1)
left outer join relids on (r.relid = relids.relid)
where relids.name='headof' or r.relid is null
;





-- Suggest changes to un-orphan currently-orphaned relationships
-- (slightly faster version)
--select orr.*,ml.entityid1 as new_entityid0
--from orphanrels orr, mergelog ml
--where orr.entityid0=ml.entityid0 and ml.action=2
--and not ml.provisional
--	UNION
--select orr.*,ml.entityid0 as new_entityid0
--from orphanrels orr, mergelog ml
--where orr.entityid0=ml.entityid1 and ml.action=1
--and not ml.provisional
