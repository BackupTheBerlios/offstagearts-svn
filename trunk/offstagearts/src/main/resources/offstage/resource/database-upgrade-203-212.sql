
-- nullifequal: if true, then a lack of a one-to-many relationship
-- (y rel x) implies the relationship (x rel x)
ALTER TABLE relids ADD COLUMN nullifequal boolean NOT NULL DEFAULT false;

update relids set nullifequal=true where name='headof';


-- Pays attention to nullifequal when inserting!!!
CREATE OR REPLACE FUNCTION w_rels_o2m_set(xrelid integer, xtemporalid integer, xentityid0 integer, xentityid1 integer)
  RETURNS void AS
'
DECLARE
	xnullifequal bool;
BEGIN
	xnullifequal := (select nullifequal
	from relids
	where relid = xrelid);

	execute w_rels_o2m_set(xrelid, xtemporalid, xentityid0, xentityid1, xnullifequal);
END
'
  LANGUAGE 'plpgsql' VOLATILE;