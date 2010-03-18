insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('grandparent-of', 1, false,  true, false,
	(select relcategoryid from relcategoryids where name='devel'));

insert into relids (name, reltype, explicit, req0, req1, relcategoryid) values
	('boss-of', 1, false,  true, false,
	(select relcategoryid from relcategoryids where name='devel'));


insert into relids (name, reltype, sym, explicit, req0, req1, relcategoryid) values
	('friend', 0, true, false,  false, false,
	(select relcategoryid from relcategoryids where name='devel'));

insert into relids (name, reltype, sym, explicit, req0, req1, relcategoryid) values
	('sibling', 0, true, false,  false, false,
	(select relcategoryid from relcategoryids where name='devel'));

insert into relids (name, reltype, sym, explicit, req0, req1, relcategoryid) values
	('cousin', 0, true, false,  false, false,
	(select relcategoryid from relcategoryids where name='devel'));

insert into relids (name, reltype, sym, explicit, req0, req1, relcategoryid) values
	('business', 0, true, false,  false, false,
	(select relcategoryid from relcategoryids where name='devel'));

insert into relids (name, reltype, sym, explicit, req0, req1, relcategoryid) values
	('coworker', 0, true, false,  false, false,
	(select relcategoryid from relcategoryids where name='devel'));


CREATE TABLE rels_m2m (
	PRIMARY KEY (relid, temporalid, entityid0, entityid1)
)
INHERITS (rels)
WITH (
  OIDS=FALSE
);


CREATE OR REPLACE FUNCTION w_rels_m2m_set(xrelid integer, xtemporalid integer,
xentityid0 integer, xentityid1 integer)
  RETURNS void AS
'
BEGIN
	if xentityid0 is null then
--		delete from rels where relid = xrelid and temporalid = xtemporalid and entityid1 = xentityid1;
	elseif xentityid1 is null then
--		delete from rels where relid = xrelid and temporalid = xtemporalid and entityid0 = xentityid0;
	else
		BEGIN
		    insert into rels_m2m (relid,temporalid,entityid0,entityid1) values
			(xrelid,xtemporalid,xentityid0,xentityid1);
	    EXCEPTION WHEN unique_violation THEN
-- Relationship is already there, nothing to do!
		END;
	end if;
END
'
  LANGUAGE 'plpgsql' VOLATILE;



-- Pays attention to nullifequal when inserting!!!
CREATE OR REPLACE FUNCTION w_rels_set(xrelid integer, xtemporalid integer, xentityid0 integer, xentityid1 integer)
  RETURNS void AS
'
DECLARE
	xreltype int;
BEGIN
	xreltype := (select reltype from relids where relid = xrelid);

	if xreltype = 0 then		-- many-to-many
		execute w_rels_m2m_set(xrelid, xtemporalid, xentityid0, xentityid1);
	elseif reltype = 1 then		-- one-to-many
		execute w_rels_o2m_set(xrelid, xtemporalid, xentityid0, xentityid1);
	end if;
END
'
  LANGUAGE 'plpgsql' VOLATILE;
