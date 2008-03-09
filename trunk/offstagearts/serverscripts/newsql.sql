start transaction;

insert into actypes (name) values ('expense');
-- Table: resources

-- DROP TABLE resources;

create table resourceids (
	resourceid serial primary key,
	name varchar(200)
);
ALTER TABLE resourceids ADD UNIQUE (name);

CREATE TABLE resources
(
  resourceid int NOT NULL,
  uversionid integer NOT NULL DEFAULT 0, 
  version integer NOT NULL,
  lastmodified timestamp,
  val bytea,
  CONSTRAINT resources_pkey PRIMARY KEY (resourceid, uversionid, version)
) 
WITHOUT OIDS;
COMMENT ON COLUMN resources.version IS 'Matches to FrontApp''s iversion';
COMMENT ON COLUMN resources.uversionid IS 'Matches to a termid or showid or something, depending on the resource.  Many templates of the same iversion could be for different shows or terms...';


CREATE OR REPLACE FUNCTION w_resource_create(xname varchar, xuversionid int, xversion int)
  RETURNS void AS
$BODY$
BEGIN
    insert into resources (resourceid, uversionid, version) values
	((select resourceid from resourceids where name = xname),
	 xuversionid, xversion);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;


CREATE OR REPLACE FUNCTION w_resourceid_create(xname varchar)
  RETURNS int AS
$BODY$
DECLARE id integer := 0;
BEGIN
	-- First try, if it's already there!
	select into id resourceid from resourceids where name = xname;
	if id is not null then
		return id;
	end if;

	BEGIN
		insert into resourceids (name) values (xname);
	EXCEPTION WHEN unique_violation THEN
		-- do nothing
	END;
	select into id resourceid from resourceids where name = xname;
	return id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

ALTER TABLE termregs ADD COLUMN rbplan varchar(100);
COMMENT ON COLUMN termregs.rbplan IS 'Short name of rate/billing plan to use for this customer.  If null, use the default rate/billing plan for the term.  See RbPlanSet';

ALTER TABLE termids ADD COLUMN rbplansetclass varchar(200);
COMMENT ON COLUMN termids.rbplansetclass IS 'Java class of the RBPlanSet subclass to use for rate plans this term.';


alter table termids drop column tuitionclass;
alter table termids drop column calctuition;


commit;
