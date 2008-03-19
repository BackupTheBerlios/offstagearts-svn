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




--CREATE TABLE payerterms
--(
--termid int not null,
--entityid int not null,
--  rbplan varchar(30), -- Short name of rate/billing plan to use for this customer.  If null, use the default rate/billing plan for the term.  See RbPlanSet
-- PRIMARY KEY (termid, entityid)
--)
--WITHOUT OIDS;
--COMMENT ON COLUMN termregs.rbplan IS 'Short name of rate/billing plan to use for this customer.  If null, use the default rate/billing plan for the term.  See RbPlanSet';


ALTER TABLE termregs ADD COLUMN payerid int4;
COMMENT ON COLUMN termregs.payerid IS 'entityid of person promising to pay this term bill.';
update termregs set payerid = entityid;
ALTER TABLE termregs
   ALTER COLUMN payerid SET NOT NULL;

CREATE TABLE payertermregs
(
  termid int4 NOT NULL,
  entityid int4 NOT NULL,
  rbplan varchar(30), -- Tuition payment plan to use for this term.
  CONSTRAINT payertermregs_pkey PRIMARY KEY (termid, entityid)
) 
WITHOUT OIDS;
COMMENT ON COLUMN payertermregs.rbplan IS 'Tuition payment plan to use for this term.';

ALTER TABLE entities ADD COLUMN parent1id int4;
ALTER TABLE entities ADD COLUMN parent2id int4;

DROP FUNCTION w_student_create(studentid int4);
DROP FUNCTION w_payer_create(payerid int4);
DROP FUNCTION w_student_register(termid int4, studentid int4, xdtregistered date);

CREATE OR REPLACE FUNCTION w_student_register(xtermid int4, studentid int4, xdtregistered date) RETURNS void AS
$BODY$
BEGIN
    BEGIN
    insert into termregs (groupid, entityid, payerid, dtregistered) values
	(xtermid, studentid, studentid, xdtregistered);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION w_payer_register(xtermid int4, xpayerid int4) RETURNS void AS
$BODY$
BEGIN
    BEGIN
    insert into payertermregs (termid, entityid) values (xtermid, xpayerid);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;


-- drop table entities_school


-- Transfer over billing records
update termregs
set rbplan = es1.billingtype
from entities_school es0, entities_school es1
where termregs.entityid = es0.entityid
and es0.adultid = es1.entityid



commit;
