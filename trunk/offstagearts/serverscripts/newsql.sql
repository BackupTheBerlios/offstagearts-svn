insert into actypes (name) values ('expense');
-- Table: resources

-- DROP TABLE resources;

CREATE TABLE resources
(
  name character varying(30) NOT NULL,
  uversionid integer NOT NULL DEFAULT 0, -- Matches to a termid or showid or something, depending on the resource.  Many templates of the same iversion could be for different shows or terms...
  version integer NOT NULL, -- Matches to FrontApp's iversion
  val bytea,
  CONSTRAINT resources_pkey PRIMARY KEY (name, uversionid)
) 
WITHOUT OIDS;
COMMENT ON COLUMN resources.version IS 'Matches to FrontApp''s iversion';
COMMENT ON COLUMN resources.uversionid IS 'Matches to a termid or showid or something, depending on the resource.  Many templates of the same iversion could be for different shows or terms...';


CREATE OR REPLACE FUNCTION w_resource_create(xname varchar, xuversionid int, xversion int)
  RETURNS void AS
$BODY$
BEGIN
    insert into resources (name, uversionid, version) values
	(xname, xuversionid, xversion);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;



