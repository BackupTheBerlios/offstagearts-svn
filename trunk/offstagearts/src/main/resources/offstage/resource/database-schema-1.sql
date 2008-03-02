--
-- PostgreSQL database dump
--

SET client_encoding = 'UNICODE';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: 
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

--
-- Name: r_entities_idlist_name_ret; Type: TYPE; Schema: public; Owner: ballettheatre
--

CREATE TYPE r_entities_idlist_name_ret AS (
	entityid integer,
	name character varying
);


--
-- Name: drop_table(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION drop_table(character varying) RETURNS character varying
    AS '
DECLARE
tablename alias for $1;
cnt int4;
BEGIN
SELECT into cnt count(*) from pg_class where relname =
tablename::name;
if cnt > 0 then
execute ''DROP TABLE '' || tablename;
return tablename || '' DROPPED'';
end if;
return tablename || '' does not exist'';
END;'
    LANGUAGE plpgsql;


--
-- Name: dropsilent2(character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION dropsilent2(character varying) RETURNS integer
    AS 'DECLARE
   table alias for $1;
   rnn varchar;
   sql varchar;
BEGIN

SELECT into rnn relname FROM pg_class WHERE relname = table;
if (rnn is not null) then
     sql := ''drop table '' || table;
     EXECUTE sql;
end if;

END
'
    LANGUAGE plpgsql;


--
-- Name: entityname(integer); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION entityname(integer) RETURNS character varying
    AS 'DECLARE
	vrelname varchar;
	vname varchar;
BEGIN
	select into vrelname relname
	from entities e, pg_class c
	where e.tableoid = c.oid
	and e.entityid = $1;

	if vrelname = ''persons'' then
		select into vname
		case when lastname is null then '''' else lastname || '', '' end ||
		case when firstname is null then '''' else firstname || '' '' end ||
		case when middlename is null then '''' else middlename end
		from persons
		where entityid = $1;
	else
		select into vname name
		from organizations
		where entityid = $1;
	end if;
	return rtrim(vname);
END








'
    LANGUAGE plpgsql;


--
-- Name: money2numeric(money); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION money2numeric(money) RETURNS numeric
    AS ' SELECT (((get_byte(cash_send($1), 0) & 255) << 24) |((get_byte(cash_send($1), 1) & 255) << 16) |((get_byte(cash_send($1), 2) & 255) << 8) |((get_byte(cash_send($1), 3) & 255)))::numeric * 0.01; '
    LANGUAGE sql IMMUTABLE STRICT;


--
-- Name: plpgsql_call_handler(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_call_handler() RETURNS language_handler
    AS '$libdir/plpgsql', 'plpgsql_call_handler'
    LANGUAGE c;


--
-- Name: plpgsql_validator(oid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_validator(oid) RETURNS void
    AS '$libdir/plpgsql', 'plpgsql_validator'
    LANGUAGE c;


--
-- Name: r_entities_idlist_name(character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION r_entities_idlist_name(character varying) RETURNS SETOF int2vector
    AS 'DECLARE
        veqsql alias for $1;
        sql text;                      -- SQL we''ll put together to insert records
        r int;
BEGIN

-- Create temporary table of IDs for this mailing list
perform dropsilent(''_ids'');
create temporary table _ids (entityid int);
sql := ''insert into _ids (entityid) '' || veqsql;
execute sql;

// -------------------
for r in
(select o.entityid, ''organizations'' as relation, name as name
from organizations o, ids
where o.entityid = ids.entityid
  union
select p.entityid, ''persons'' as relation,
(case when lastname is null then '''' else lastname || '', '' end ||
case when firstname is null then '''' else firstname || '' '' end ||
case when middlename is null then '''' else middlename end) as name
from persons p, _ids
where p.entityid = ids.entityid)
order by relation, name

        return next r;

end loop;

// -------------------
drop table _ids;

return;

END
'
    LANGUAGE plpgsql;


--
-- Name: r_entities_idlist_name2(character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION r_entities_idlist_name2(character varying) RETURNS SETOF r_entities_idlist_name_ret
    AS 'DECLARE
        veqsql alias for $1;
        sql text;                      -- SQL we''ll put together to insert records
        r r_entities_idlist_name_ret;
BEGIN

-- Create temporary table of IDs for this mailing list
perform dropsilent(''_ids'');
create temporary table _ids (entityid int);
sql := ''insert into _ids (entityid) '' || veqsql;
execute sql;

-- -------------------
for r in
(select o.entityid, ''organizations'' as relation, name as name
from organizations o, _ids
where o.entityid = _ids.entityid
  union
select p.entityid, ''persons'' as relation,
(case when lastname is null then '''' else lastname || '', '' end ||
case when firstname is null then '''' else firstname || '' '' end ||
case when middlename is null then '''' else middlename end) as name
from persons p, _ids
where p.entityid = _ids.entityid)
order by relation, name

loop
        return next r;

end loop;

-- -------------------
--drop table _ids;

return;

END
'
    LANGUAGE plpgsql;


--
-- Name: r_entities_relname(integer); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION r_entities_relname(integer) RETURNS name
    AS 'select c.relname
from entities e, pg_class c
where entityid = $1
and e.tableoid = c.oid
'
    LANGUAGE sql;


--
-- Name: rfi_primaryentityid(); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION rfi_primaryentityid() RETURNS "trigger"
    AS '   DECLARE   BEGIN   IF NEW.primaryentityid IN (select entityid from entities)       OR NEW.primaryentityid = NEW.entityid THEN      RETURN NEW;   ELSE      RAISE EXCEPTION       ''insert or update on table "%" violates foreign key constraint for entities table'',TG_RELNAME;   END IF;   END;   '
    LANGUAGE plpgsql;


--
-- Name: rfi_primaryentityid_delete(); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION rfi_primaryentityid_delete() RETURNS "trigger"
    AS '   DECLARE   BEGIN   IF OLD.entityid NOT IN (select primaryentityid from entities where entityid <> OLD.entityid ) THEN      RETURN OLD;   ELSE      RAISE EXCEPTION       ''delete on table "%" violates foreign key constraint for entities table'', TG_RELNAME;   END IF;   END;   '
    LANGUAGE plpgsql;


--
-- Name: w_groupids_new(); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_groupids_new() RETURNS integer
    AS 'DECLARE
	vgroupid int;
begin
	select into vgroupid nextval(''groupids_groupid_seq'');
	insert into groupids (groupid) values (vgroupid);
	return vgroupid;
end




'
    LANGUAGE plpgsql;


--
-- Name: w_mailingids_create(text, text); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_mailingids_create(text, text) RETURNS integer
    AS 'DECLARE
        veqxml alias for $1;
        veqsql alias for $2;
        vgroupid int;               -- Resulting ID for Mailing List
        sql text;                      -- SQL we''ll put together to insert records
BEGIN

select into vgroupid nextval(''groupids_groupid_seq'');
insert into mailingids
(groupid, name, created, equery) values
(vgroupid, ''Mailing'', now(), veqxml);

perform dropsilent(''_ids'');
sql = ''
create temporary table _ids (entityid int);
insert into _ids (entityid) '' || veqsql;

EXECUTE sql;

sql = ''select vgroupid, entityid from _ids'';

-- Insert into Mailing List
insert into mailings (groupid, entityid) EXECUTE sql;
drop table _ids;

-- Return Mailing List ID we created.
return vgroupid;

END'
    LANGUAGE plpgsql;


--
-- Name: w_mailingids_create_old(text, text); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_mailingids_create_old(text, text) RETURNS integer
    AS 'DECLARE
        veqxml alias for $1;
        veqsql alias for $2;
        vgroupid int;               -- Resulting ID for Mailing List
        sql text;                      -- SQL we''ll put together to insert records
BEGIN

-- Create Mailing List
select into vgroupid nextval(''groupids_groupid_seq'');
insert into mailingids
(groupid, name, created, equery) values
(vgroupid, ''Mailing'', now(), veqxml);

-- Create temporary table of IDs for this mailing list
perform dropsilent(''_ids'');
create temporary table _ids (entityid int);
delete from _ids;
sql := ''insert into _ids (entityid) '' || veqsql;
execute sql;

-- Insert into Mailing List
insert into mailings (groupid, entityid) select vgroupid, entityid from _ids;
drop table _ids;

-- Return Mailing List ID we created.
return vgroupid;

END
'
    LANGUAGE plpgsql;


--
-- Name: w_mailings_correctlist(integer, boolean); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_mailings_correctlist(integer, boolean) RETURNS void
    AS 'DECLARE
	vgroupid alias for $1;			-- Mailing to work on
	vkeepnosend alias for $2;	-- Keep "no send" people from list?
BEGIN
	-- Clear...
	update mailings set addressto = null
	where groupid = vgroupid;

	-- Send to the primary
	update mailings
	set sendentityid = e.primaryentityid, ename = null
	from entities e
	where mailings.entityid = e.entityid
	and mailings.groupid = vgroupid;

	-- Eliminate duplicates
	update mailings
	set minoid = xx.minoid
	from (
		select sendentityid, min(oid) as minoid
		from mailings m
		where m.groupid = vgroupid
		group by sendentityid
	) xx
	where mailings.sendentityid = xx.sendentityid
	and groupid = vgroupid;

	delete from mailings
	where groupid = vgroupid
	and oid <> minoid;

	-- Keep "no send" people
	if (not vkeepnosend) then
		update mailings
		set groupid = -2
		from entities e
		where mailings.sendentityid = e.entityid
		and not e.sendmail
		and mailings.groupid = vgroupid;

		delete from mailings where groupid = -2;
	end if;
	
	-- ========= Set addressto from multiple sources
	-- Set addressto by custom address to
	update mailings
	set addressto = customaddressto
	from entities p
	where p.entityid = sendentityid
	and p.customaddressto is not null
	and addressto is null
	and mailings.groupid = vgroupid;

	-- Use pre-computed names
	update mailings
	set addressto = ename
	where addressto is null and ename is not null
	and groupid = vgroupid;

	-- Set addressto as name of person
	update mailings
	set addressto = 
		coalesce(p.firstname || '' '', '''') ||
		coalesce(p.middlename || '' '', '''') ||
		coalesce(p.lastname, '''')
	from entities p
	where mailings.sendentityid = p.entityid
	and mailings.groupid = vgroupid
	and addressto is null;

	-- Set addressto as name of organization
	update mailings
	set addressto2 = p.name
	from organizations p
	where mailings.sendentityid = p.entityid
	and mailings.groupid = vgroupid;
--	and addressto is null;


update mailings set addressto = addressto2
where addressto is null;

	-- ==================================

	-- Set the rest of the address
	update mailings
	set address1 = e.address1,
	address2 = e.address2,
	city = e.city,
	state = e.state,
	zip = e.zip,
	country = e.country
	from entities e
	where mailings.sendentityid = e.entityid
	and mailings.groupid = vgroupid;

	return;
END








'
    LANGUAGE plpgsql;


--
-- Name: w_multiqueryids_new(character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_multiqueryids_new(character varying) RETURNS integer
    AS 'DECLARE
	vname alias for $1;
	vgroupid int;
BEGIN
	select into vgroupid nextval(''groupids_groupid_seq'');
	insert into multiqueryids (groupid, name) values (vgroupid, vname);
	return vgroupid;
END




'
    LANGUAGE plpgsql;


--
-- Name: w_organizations_new(integer); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_organizations_new(integer) RETURNS integer
    AS 'DECLARE
	iprimaryentityid alias for $1;
	vprimaryentityid int;
	ventityid int;
begin
select into ventityid nextval(''entities_entityid_seq'');

if iprimaryentityid = 0 then
	vprimaryentityid = ventityid;
else
	vprimaryentityid = iprimaryentityid;
end if;

insert into organizations
(isquery, entityid, primaryentityid, relprimarytypeid)
values
(false, ventityid, vprimaryentityid, 0);

return ventityid;
end





'
    LANGUAGE plpgsql;


--
-- Name: w_payer_create(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_payer_create(payerid integer) RETURNS void
    AS '
BEGIN
    insert into entities_school (entityid, adultid) values
	(payerid, payerid);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
'
    LANGUAGE plpgsql;


--
-- Name: w_persons_new(integer); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_persons_new(integer) RETURNS integer
    AS 'DECLARE
	iprimaryentityid alias for $1;
	vprimaryentityid int;
	ventityid int;
begin
select into ventityid nextval(''entities_entityid_seq'');

if iprimaryentityid = 0 then
	vprimaryentityid = ventityid;
else
	vprimaryentityid = iprimaryentityid;
end if;

insert into persons
(isquery, entityid, primaryentityid, relprimarytypeid)
values
(false, ventityid, vprimaryentityid, 0);

return ventityid;
end








'
    LANGUAGE plpgsql;


--
-- Name: w_queries_new_entities(character varying, character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_queries_new_entities(character varying, character varying) RETURNS integer
    AS 'DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = ''From'';

select into toid relprimarytypeid
from relprimarytypes where name = ''To'';

select into vfromentityid nextval(''entities_entityid_seq'');
select into vtoentityid nextval(''entities_entityid_seq'');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into entities (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into entities (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end

'
    LANGUAGE plpgsql;


--
-- Name: w_queries_new_organizations(character varying, character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_queries_new_organizations(character varying, character varying) RETURNS integer
    AS 'DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = ''From'';

select into toid relprimarytypeid
from relprimarytypes where name = ''To'';

select into vfromentityid nextval(''entities_entityid_seq'');
select into vtoentityid nextval(''entities_entityid_seq'');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into organizations (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into organizations (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end

'
    LANGUAGE plpgsql;


--
-- Name: w_queries_new_persons(character varying, character varying); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_queries_new_persons(character varying, character varying) RETURNS integer
    AS '
DECLARE
vfromentityid int;
vtoentityid int;
fromid int;
toid int;
begin
select into fromid relprimarytypeid
from relprimarytypes where name = ''From'';

select into toid relprimarytypeid
from relprimarytypes where name = ''To'';

select into vfromentityid nextval(''entities_entityid_seq'');
select into vtoentityid nextval(''entities_entityid_seq'');

insert into queries (fromentityid, toentityid, dtime, username, name)
values (vfromentityid, vtoentityid, now(), $1, $2);

insert into persons (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vfromentityid, vfromentityid, fromid);

insert into persons (isquery, entityid, primaryentityid, relprimarytypeid) values
(true, vtoentityid, vfromentityid, toid);


return vfromentityid;
end



'
    LANGUAGE plpgsql;


--
-- Name: w_resource_create(character varying, integer, integer); Type: FUNCTION; Schema: public; Owner: ballettheatre
--

CREATE FUNCTION w_resource_create(xname character varying, xuversionid integer, xversion integer) RETURNS void
    AS '
BEGIN
    insert into resources (resourceid, uversionid, version) values
	((select resourceid from resourceids where name = xname),
	 xuversionid, xversion);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
'
    LANGUAGE plpgsql;


--
-- Name: w_resourceid_create(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_resourceid_create(xname character varying) RETURNS integer
    AS '
DECLARE id integer := 0;
BEGIN
	-- First try, if it''s already there!
	select into id resourceid from resourceids where name = xname;
	if id is not null then
		return id;
	end if;

	BEGIN
		insert into resourceids (name) values (xname);
	EXCEPTION WHEN unique_violation THEN
		-- do nothing
	END;
	id := 3;
	select into id resourceid from resourceids where name = xname;
	return id;
END;
'
    LANGUAGE plpgsql;


--
-- Name: w_student_create(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_student_create(studentid integer) RETURNS void
    AS '
BEGIN
    INSERT INTO entities_school(entityid, adultid) VALUES
	(studentid,
	(select primaryentityid from entities where entityid = studentid));
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
'
    LANGUAGE plpgsql;


--
-- Name: w_student_register(integer, integer, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION w_student_register(termid integer, studentid integer, xdtregistered date) RETURNS void
    AS '
BEGIN
    insert into termregs (groupid, entityid, dtregistered) values
	(termid, studentid, xdtregistered);
    EXCEPTION WHEN unique_violation THEN
            -- do nothing
    END;
'
    LANGUAGE plpgsql;


SET search_path = pg_catalog;

--
-- Name: CAST (money AS numeric); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (money AS numeric) WITH FUNCTION public.money2numeric(money) AS IMPLICIT;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: absences; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE absences (
    entityid integer NOT NULL,
    meetingid integer NOT NULL,
    dtparentreviewed timestamp without time zone,
    dtstaffreviewed timestamp without time zone,
    parentnotes text,
    staffnotes text,
    dtime timestamp without time zone,
    privatenotes text,
    dtparentnotified timestamp without time zone,
    parentemail character varying(50),
    "valid" boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE absences; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE absences IS 'Record whenever an absence is noted';


--
-- Name: COLUMN absences.dtparentreviewed; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.dtparentreviewed IS 'Date/Time parent first reviewed the absence';


--
-- Name: COLUMN absences.dtstaffreviewed; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.dtstaffreviewed IS 'Date/Time staff first reviewed the absence';


--
-- Name: COLUMN absences.parentnotes; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.parentnotes IS 'Notations entered by parent, visible to staff';


--
-- Name: COLUMN absences.staffnotes; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.staffnotes IS 'Notations entered by staff, visible to parent';


--
-- Name: COLUMN absences.dtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.dtime IS 'Date/Time absence was noted by system';


--
-- Name: COLUMN absences.privatenotes; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.privatenotes IS 'Private notes viewable by staff only';


--
-- Name: COLUMN absences.dtparentnotified; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.dtparentnotified IS 'Date/Time parent emailed about absence';


--
-- Name: COLUMN absences.parentemail; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences.parentemail IS 'Address to which parent notification was sent';


--
-- Name: COLUMN absences."valid"; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN absences."valid" IS 'Was this a real absence, or was there a glitch in the system?';


--
-- Name: acbal; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE acbal (
    entityid integer NOT NULL,
    actypeid integer NOT NULL,
    dtime timestamp without time zone DEFAULT now() NOT NULL,
    bal numeric(9,2)
);


--
-- Name: accounts; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE accounts (
    entityid integer NOT NULL,
    username character varying(50) NOT NULL,
    "password" character varying(15) NOT NULL
);


--
-- Name: TABLE accounts; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE accounts IS 'Userid and login for entities with login accounts';


--
-- Name: COLUMN accounts."password"; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN accounts."password" IS 'Not encrypted --- we might have to email it to parents';


--
-- Name: actrans; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE actrans (
    actransid serial NOT NULL,
    actranstypeid integer NOT NULL,
    entityid integer NOT NULL,
    actypeid integer NOT NULL,
    date date NOT NULL,
    datecreated date,
    amount numeric(9,2),
    description character varying(300),
    studentid integer,
    termid integer,
    cc_type character(1),
    cc_info character varying(255),
    py_name character varying(50),
    cc_last4 character varying(4),
    cc_expdate character varying(4),
    cc_batchid integer,
    ck_number character varying(15),
    py_phone character varying(30)
);


--
-- Name: COLUMN actrans.amount; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans.amount IS 'like a credit card; >0 means customer owes';


--
-- Name: COLUMN actrans.studentid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans.studentid IS 'Student for whom this is a tuition record';


--
-- Name: COLUMN actrans.py_name; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans.py_name IS 'Name on check or credit card --- or name check was written to.';


--
-- Name: COLUMN actrans.cc_batchid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans.cc_batchid IS 'Batch the credit card payment was processed';


--
-- Name: COLUMN actrans.py_phone; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans.py_phone IS 'Contact phone # on check or credit card.';


--
-- Name: actrans_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE actrans_old (
    entityid integer NOT NULL,
    actypeid integer NOT NULL,
    date date NOT NULL,
    amount numeric(9,2),
    description character varying(300),
    actransid serial NOT NULL,
    datecreated date
);


--
-- Name: COLUMN actrans_old.amount; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN actrans_old.amount IS 'like a credit card; >0 means customer owes';


--
-- Name: actranstypes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE actranstypes (
    actranstypeid serial NOT NULL,
    name character varying(30),
    description character varying(30)
);


--
-- Name: actypes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE actypes (
    actypeid serial NOT NULL,
    name character varying(20)
);


--
-- Name: adjpayments_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE adjpayments_old (
)
INHERITS (actrans_old);


--
-- Name: TABLE adjpayments_old; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE adjpayments_old IS 'Ad-hoc adjustments to account balance.';


--
-- Name: attendance; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE attendance (
    meetingid integer,
    entityid integer,
    dtime timestamp without time zone DEFAULT now()
);


--
-- Name: TABLE attendance; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE attendance IS 'Who actually attended what courses';


--
-- Name: COLUMN attendance.dtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN attendance.dtime IS 'Time attendance was noted';


--
-- Name: cashpayments_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE cashpayments_old (
)
INHERITS (actrans_old);


--
-- Name: ccbatches; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE ccbatches (
    ccbatchid serial NOT NULL,
    dtime timestamp without time zone DEFAULT now()
);


--
-- Name: TABLE ccbatches; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE ccbatches IS 'A batch of processed credit card transactions';


--
-- Name: ccpayments_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE ccpayments_old (
    cctype character(1),
    ccinfo character varying(255),
    ccname character varying(50),
    cclast4 character varying(4),
    ccexpdate character varying(4),
    ccbatchid integer
)
INHERITS (actrans_old);


--
-- Name: COLUMN ccpayments_old.ccbatchid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN ccpayments_old.ccbatchid IS 'Batch the credit card payment was processed in.';


--
-- Name: checkpayments_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE checkpayments_old (
    name character varying(50),
    checknumber character varying(15),
    phone character varying(30)
)
INHERITS (actrans_old);


--
-- Name: groups; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE groups (
    groupid integer NOT NULL,
    entityid integer NOT NULL
);


--
-- Name: classes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE classes (
    comments character varying(200)
)
INHERITS (groups);


--
-- Name: groupids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE groupids (
    groupid serial NOT NULL,
    name character varying(100) NOT NULL
);


--
-- Name: classids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE classids (
    comments character varying(200)
)
INHERITS (groupids);


--
-- Name: coursedeps; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE coursedeps (
    basecourseid integer NOT NULL,
    reqcourseid integer NOT NULL
);


--
-- Name: TABLE coursedeps; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE coursedeps IS 'Courses required if one is enrolling in other courses';


--
-- Name: COLUMN coursedeps.basecourseid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN coursedeps.basecourseid IS 'The course one WANTS to take';


--
-- Name: COLUMN coursedeps.reqcourseid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN coursedeps.reqcourseid IS 'The course one is REQUIRED to take';


--
-- Name: courseids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE courseids (
    courseid serial NOT NULL,
    name character varying(50),
    termid integer,
    dayofweek integer,
    enrolllimit integer,
    tstart time without time zone,
    tnext time without time zone,
    price numeric(9,2),
    locationid integer DEFAULT 1 NOT NULL
);


--
-- Name: COLUMN courseids.dayofweek; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN courseids.dayofweek IS 'Uses Java day-of-week convention';


--
-- Name: COLUMN courseids.enrolllimit; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN courseids.enrolllimit IS 'Max # of students to be enrolled (guideline)';


--
-- Name: courseroles; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE courseroles (
    courseroleid serial NOT NULL,
    name character(30),
    orderid integer
);


--
-- Name: TABLE courseroles; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE courseroles IS 'Types of enrollment in a course (student, teacher, etc)';


--
-- Name: coursesetids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE coursesetids (
    coursesetid serial NOT NULL,
    programid integer,
    name character varying(50)
);


--
-- Name: TABLE coursesetids; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE coursesetids IS 'Sets of courses --- used to give customers simple menus for enrollment.';


--
-- Name: COLUMN coursesetids.programid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN coursesetids.programid IS 'Program to which this course set belongs (all courses in it must have matching programid)';


--
-- Name: coursesets; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE coursesets (
    coursesetid integer NOT NULL,
    courseid integer NOT NULL
);


--
-- Name: daysofweek; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE daysofweek (
    javaid integer NOT NULL,
    shortname character varying,
    lettername character varying(2),
    longname character varying
);


--
-- Name: dblogingroupids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dblogingroupids (
)
INHERITS (groupids);


--
-- Name: dblogingroups; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dblogingroups (
)
INHERITS (groups);


--
-- Name: dblogins; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dblogins (
    username character varying(32) NOT NULL,
    entityid integer NOT NULL
);


--
-- Name: TABLE dblogins; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE dblogins IS 'Associates OS logins with database user (entityID)';


--
-- Name: dbversion; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dbversion (
    major integer DEFAULT 0 NOT NULL,
    minor integer DEFAULT 0 NOT NULL,
    rev integer DEFAULT 0 NOT NULL
);


--
-- Name: dtgroupids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dtgroupids (
)
INHERITS (groupids);


--
-- Name: donationids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE donationids (
    fiscalyear integer
)
INHERITS (dtgroupids);


--
-- Name: donations; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE donations (
    serialid serial NOT NULL,
    entityid integer NOT NULL,
    groupid integer NOT NULL,
    date date NOT NULL,
    amount numeric(9,2)
);


--
-- Name: dtgroups; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dtgroups (
    groupid integer,
    entityid integer,
    date date NOT NULL
)
INHERITS (groups);


--
-- Name: duedateids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE duedateids (
    duedateid serial NOT NULL,
    name character varying(30) NOT NULL,
    description character varying(200)
);


--
-- Name: TABLE duedateids; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE duedateids IS 'Dates various stuff in the term is due';


--
-- Name: COLUMN duedateids.name; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN duedateids.name IS 'The type of thing that''s due at this time --- this can be flexible, based on the billing policy of the school.  Billing algorithm matches against this.';


--
-- Name: COLUMN duedateids.description; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN duedateids.description IS 'Long version of name --- will be placed in the Description field in the account.';


--
-- Name: duedates; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE duedates (
    termid integer NOT NULL,
    duedateid integer NOT NULL,
    duedate date
);


--
-- Name: duedates_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE duedates_old (
    termid serial NOT NULL,
    name character varying(30) NOT NULL,
    duedate date,
    description character varying(200)
);


--
-- Name: TABLE duedates_old; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE duedates_old IS 'Dates various stuff in the term is due';


--
-- Name: COLUMN duedates_old.name; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN duedates_old.name IS 'The type of thing that''s due at this time --- this can be flexible, based on the billing policy of the school.';


--
-- Name: COLUMN duedates_old.duedate; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN duedates_old.duedate IS 'Date this thing is due';


--
-- Name: COLUMN duedates_old.description; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN duedates_old.description IS 'Long version of name --- will be placed in the Description field in the account.';


--
-- Name: dups; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE dups (
    entityid0 integer NOT NULL,
    "type" character(1) NOT NULL,
    entityid1 integer NOT NULL,
    score double precision NOT NULL,
    string0 character varying(200),
    string1 character varying(200)
);


--
-- Name: COLUMN dups."type"; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN dups."type" IS '''a''=address, ''o''=orgname, ''n''=name';


--
-- Name: COLUMN dups.string0; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN dups.string0 IS 'Stuff that was compared';


SET default_with_oids = true;

--
-- Name: enrollments; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE enrollments (
    courseid integer NOT NULL,
    entityid integer NOT NULL,
    courserole integer,
    dstart date,
    dend date,
    pplanid integer,
    dtapproved time without time zone,
    dtenrolled timestamp without time zone
);


--
-- Name: COLUMN enrollments.pplanid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN enrollments.pplanid IS 'Payment plan set up to pay for this (and other) enrollment';


--
-- Name: COLUMN enrollments.dtapproved; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN enrollments.dtapproved IS 'Date/Time principlal approved enrollment';


--
-- Name: COLUMN enrollments.dtenrolled; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN enrollments.dtenrolled IS 'Date/Time student enrolled';


SET default_with_oids = false;

--
-- Name: entities; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE entities (
    entityid serial NOT NULL,
    oldid integer,
    primaryentityid integer NOT NULL,
    address1 character varying(100),
    address2 character varying(100),
    city character varying(50),
    state character varying(20),
    zip character varying(11),
    country character varying(50),
    recordsource character varying(25),
    sourcekey integer,
    lastupdated timestamp without time zone,
    relprimarytypeid integer,
    sendmail boolean DEFAULT true,
    obsolete boolean DEFAULT false,
    created timestamp without time zone DEFAULT now(),
    title character varying(60),
    occupation character varying(60),
    salutation character varying(30),
    firstname character varying(50),
    middlename character varying(50),
    lastname character varying(50),
    customaddressto character varying(100),
    orgname character varying(100),
    isorg boolean DEFAULT false NOT NULL,
    mailprefid integer,
    py_name character varying(50),
    cc_type character(1),
    cc_last4 character varying(4),
    cc_info character varying(255),
    cc_expdate character varying(4),
    flag boolean DEFAULT false NOT NULL
);


--
-- Name: COLUMN entities.orgname; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities.orgname IS 'Name of organization (i.e. employer) to which this person belongs.';


--
-- Name: COLUMN entities.isorg; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities.isorg IS 'True if this is an organization (i.e. we''re more interested in keeping track of the orgname than the person involved.)';


--
-- Name: entities_school; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE entities_school (
    entityid integer NOT NULL,
    adultid integer NOT NULL,
    ngrade integer,
    ngradeasof abstime,
    billingtype character(1) DEFAULT 'y'::bpchar NOT NULL,
    parent2id integer,
    parentid integer
);


--
-- Name: COLUMN entities_school.adultid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities_school.adultid IS 'Should really be payerid --- this is the person who pays for the student.';


--
-- Name: COLUMN entities_school.ngrade; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities_school.ngrade IS 'Student''s grade level (see gradelevels)';


--
-- Name: COLUMN entities_school.ngradeasof; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities_school.ngradeasof IS 'Date of student''s grade level (typically the start date of a TermID; maybe grade level should be moved into a Term-Person table, later --- registrations)';


--
-- Name: COLUMN entities_school.billingtype; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities_school.billingtype IS '''q'' = quarterly, ''y'' = yearly, ''m'' = monthly.  Only used in payer entities_school record.';


--
-- Name: COLUMN entities_school.parent2id; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN entities_school.parent2id IS 'Secondary parent --- not responsible for contact, but it''s nice to know the name.';


--
-- Name: equeries; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE equeries (
    equeryid serial NOT NULL,
    equery text,
    lastmodified timestamp without time zone,
    lastaccessed timestamp without time zone,
    name character varying(100)
);


--
-- Name: eventids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE eventids (
    "comment" character varying(100)
)
INHERITS (groupids);


--
-- Name: events; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE events (
    "role" character varying(50)
)
INHERITS (groups);


--
-- Name: flagids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE flagids (
)
INHERITS (groupids);


--
-- Name: flags; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE flags (
    entityid integer NOT NULL,
    groupid integer NOT NULL,
    date date
);


--
-- Name: gradelevels; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE gradelevels (
    name character varying(20),
    ngrade integer NOT NULL
);


--
-- Name: COLUMN gradelevels.ngrade; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN gradelevels.ngrade IS 'Numeric representation of grade (K=0)';


--
-- Name: holidays; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE holidays (
    holidayid serial NOT NULL,
    termid integer DEFAULT -1 NOT NULL,
    firstday date NOT NULL,
    lastday date,
    description character varying(50)
);


--
-- Name: interestids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE interestids (
    "comment" character varying(200)
)
INHERITS (groupids);


--
-- Name: interests; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE interests (
    byperson boolean,
    referredby character varying(50),
    id serial NOT NULL,
    count integer,
    minid integer
)
INHERITS (groups);


SET default_with_oids = true;

--
-- Name: invoiceids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE invoiceids (
    invoiceid serial NOT NULL,
    "type" character(1) NOT NULL,
    amount numeric(9,2),
    dtime timestamp without time zone DEFAULT now() NOT NULL,
    entityid integer NOT NULL,
    remain numeric(9,2),
    ddue date
);


--
-- Name: COLUMN invoiceids."type"; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids."type" IS '''e'' = enrollment, ''p'' = pledge, ''t'' = ticket sale ''c''=class card, ''s'' = sub';


--
-- Name: COLUMN invoiceids.amount; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids.amount IS 'Can be calculated by enrollments, pledges or ticket sales associated with this invoice.';


--
-- Name: COLUMN invoiceids.dtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids.dtime IS 'Date/time invoice created';


--
-- Name: COLUMN invoiceids.entityid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids.entityid IS 'Person responsible for the invoice';


--
-- Name: COLUMN invoiceids.remain; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids.remain IS 'Amount remaining to be paid on invoice';


--
-- Name: COLUMN invoiceids.ddue; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN invoiceids.ddue IS 'Due date by which invoice must be paid';


SET default_with_oids = false;

--
-- Name: invoices; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE invoices (
    ddue date
)
INHERITS (actrans_old);


--
-- Name: locations; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE locations (
    locationid serial NOT NULL,
    name character varying(40)
);


--
-- Name: TABLE locations; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE locations IS 'Studio locations';


--
-- Name: mailingids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE mailingids (
    created timestamp without time zone DEFAULT now(),
    equery text
)
INHERITS (groupids);


SET default_with_oids = true;

--
-- Name: mailings; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE mailings (
    ename character varying(100),
    addressto character varying(100),
    address1 character varying(100),
    address2 character varying(100),
    city character varying(50),
    state character varying(50),
    zip character varying(30),
    sendentityid integer,
    minoid integer,
    country character varying(50),
    line1 character varying(100),
    line2 character varying(100),
    line3 character varying(100),
    isgood boolean,
    addressto2 character varying(100)
)
INHERITS (groups);


SET default_with_oids = false;

--
-- Name: mailprefids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE mailprefids (
    mailprefid serial NOT NULL,
    name character varying(30)
);


SET default_with_oids = true;

--
-- Name: meetings; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE meetings (
    meetingid serial NOT NULL,
    courseid integer NOT NULL,
    dtstart timestamp without time zone NOT NULL,
    dtnext timestamp without time zone NOT NULL
);


--
-- Name: COLUMN meetings.courseid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN meetings.courseid IS 'Course to which this meeting belongs';


SET default_with_oids = false;

--
-- Name: mergelog; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE mergelog (
    entityid0 integer NOT NULL,
    entityid1 integer NOT NULL,
    dupok boolean,
    dtime timestamp without time zone
);


--
-- Name: noteids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE noteids (
)
INHERITS (dtgroupids);


--
-- Name: notes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE notes (
    note text
)
INHERITS (dtgroups);


--
-- Name: offercodeids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE offercodeids (
    offercodeid serial NOT NULL,
    name character varying(30)
);


--
-- Name: organizations; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE organizations (
    name character varying(100)
)
INHERITS (entities);


SET default_with_oids = true;

--
-- Name: paymentallocs; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE paymentallocs (
    invoiceid integer NOT NULL,
    paymentid integer NOT NULL,
    amount numeric(9,2) NOT NULL
);


SET default_with_oids = false;

--
-- Name: paymentids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE paymentids (
    paymentid serial NOT NULL,
    entityid integer,
    amount numeric(9,2),
    dtime timestamp without time zone DEFAULT now() NOT NULL,
    date date NOT NULL,
    remain numeric(9,2) NOT NULL
);


--
-- Name: COLUMN paymentids.entityid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN paymentids.entityid IS 'Person doing the paying';


--
-- Name: COLUMN paymentids.dtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN paymentids.dtime IS 'Date/time payment was recorded in our system';


--
-- Name: COLUMN paymentids.date; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN paymentids.date IS 'Date payment received (as perceived by human taking payment)';


--
-- Name: COLUMN paymentids.remain; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN paymentids.remain IS 'Unallocated $$ remaining from payment';


SET default_with_oids = true;

--
-- Name: paymenttypeids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE paymenttypeids (
    paymenttypeid integer NOT NULL,
    "table" character varying(30) NOT NULL,
    name character varying(50)
);


--
-- Name: COLUMN paymenttypeids."table"; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN paymenttypeids."table" IS 'Table in DB that holdes this type of payment';


SET default_with_oids = false;

--
-- Name: perftypeids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE perftypeids (
    perftypeid serial NOT NULL,
    name character varying(30)
);


--
-- Name: persons; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE persons (
    gender character(1),
    dob date,
    email character varying(100),
    url character varying(200)
)
INHERITS (entities);


--
-- Name: phoneids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE phoneids (
    priority integer,
    letter character(1),
    code character varying(5)
)
INHERITS (groupids);


--
-- Name: COLUMN phoneids.priority; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN phoneids.priority IS 'More usual ways of contacting people have lower priority';


--
-- Name: COLUMN phoneids.letter; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN phoneids.letter IS 'A character used to identify phone type in reports';


--
-- Name: phones; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE phones (
    phone character varying(20),
    id serial NOT NULL,
    count integer,
    minid integer
)
INHERITS (groups);


SET default_with_oids = true;

--
-- Name: pplanids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE pplanids (
    pplanid serial NOT NULL,
    entityid integer NOT NULL,
    paymenttypeid integer NOT NULL,
    cctype character(1),
    ccnumber character varying(25),
    invaliddate date,
    name character varying(50),
    dtime timestamp without time zone DEFAULT now(),
    dtapproved timestamp without time zone,
    paymentplantypeid integer,
    remain numeric(9,2),
    amount numeric(9,2),
    termid integer
);


--
-- Name: COLUMN pplanids.cctype; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.cctype IS '''m'' or ''v'' for MasterCard/Visa';


--
-- Name: COLUMN pplanids.ccnumber; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.ccnumber IS 'Full CC #';


--
-- Name: COLUMN pplanids.dtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.dtime IS 'Date/Time payment method supplied by customer';


--
-- Name: COLUMN pplanids.dtapproved; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.dtapproved IS 'Date/Time this payment method was approved';


--
-- Name: COLUMN pplanids.paymentplantypeid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.paymentplantypeid IS 'Spacing of payments --- in full @ beginning, in quarterly installments, etc.';


--
-- Name: COLUMN pplanids.remain; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.remain IS 'Amount remaining on the payment plan --- or null if amount has not yet been determined.';


--
-- Name: COLUMN pplanids.amount; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.amount IS 'Amount of $$ for which payment plan is being set up';


--
-- Name: COLUMN pplanids.termid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN pplanids.termid IS 'Term for payment play --- determines schedule of payments';


--
-- Name: pplaninvoiceids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE pplaninvoiceids (
    pplanid integer,
    invoiceid integer
);


--
-- Name: TABLE pplaninvoiceids; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE pplaninvoiceids IS 'Invoices generated that are associated with a payment plan';


--
-- Name: pplantypeids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE pplantypeids (
    pplantypeid integer NOT NULL,
    "type" character varying(30) NOT NULL,
    name character varying(50) NOT NULL
);


--
-- Name: programids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE programids (
    programid serial NOT NULL,
    termid integer,
    name character varying(50),
    needselig boolean DEFAULT true NOT NULL,
    minage integer
);


--
-- Name: COLUMN programids.termid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN programids.termid IS 'Term with which this program is associated --- or no term';


--
-- Name: COLUMN programids.needselig; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN programids.needselig IS 'Is an eligibility record required to registe for this program?';


--
-- Name: COLUMN programids.minage; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN programids.minage IS 'Minimum age (in years) for eligibility for this open program';


SET default_with_oids = false;

--
-- Name: querylog; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE querylog (
    queryid serial NOT NULL,
    "type" character(1),
    dtime timestamp without time zone,
    dbtable character varying(50),
    loginid integer
);


--
-- Name: querylogcols; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE querylogcols (
    queryid integer NOT NULL,
    colname character varying(30) NOT NULL,
    iskey boolean,
    sqlval character varying(2000),
    oldval character varying(2000)
);


SET default_with_oids = true;

--
-- Name: regelig; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE regelig (
    programid integer NOT NULL,
    entityid integer NOT NULL,
    authdtime timestamp without time zone,
    expiredate date
);


--
-- Name: TABLE regelig; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE regelig IS 'Permission to register for programs';


--
-- Name: COLUMN regelig.authdtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN regelig.authdtime IS 'Date/time eligibility was authorized';


--
-- Name: COLUMN regelig.expiredate; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN regelig.expiredate IS 'Date eligibility to register expires';


--
-- Name: registrations; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE registrations (
    programid integer NOT NULL,
    entityid integer NOT NULL,
    regdtime timestamp without time zone,
    expiredate date
);


--
-- Name: COLUMN registrations.regdtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN registrations.regdtime IS 'Date/time of registration';


--
-- Name: COLUMN registrations.expiredate; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN registrations.expiredate IS 'Date this registration expires --- usually @ end of YDP term, or 1 yr. from open class registration.';


SET default_with_oids = false;

--
-- Name: relprimarytypes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE relprimarytypes (
    relprimarytypeid serial NOT NULL,
    name character varying(30)
);


--
-- Name: resourceids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE resourceids (
    resourceid serial NOT NULL,
    name character varying(200)
);


--
-- Name: resources; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE resources (
    resourceid integer NOT NULL,
    uversionid integer DEFAULT 0 NOT NULL,
    version integer NOT NULL,
    val bytea,
    lastmodified timestamp without time zone
);


--
-- Name: COLUMN resources.uversionid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN resources.uversionid IS 'Matches to a termid or showid or something, depending on the resource.  Many templates of the same iversion could be for different shows or terms...';


--
-- Name: COLUMN resources.version; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN resources.version IS 'Matches to FrontApp''s iversion';


--
-- Name: status; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE status (
)
INHERITS (groups);


--
-- Name: statusids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE statusids (
)
INHERITS (groupids);


SET default_with_oids = true;

--
-- Name: subs; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE subs (
    meetingid integer NOT NULL,
    entityid integer NOT NULL,
    subtype character(1) NOT NULL,
    courserole integer,
    dtapproved timestamp without time zone,
    enterdtime timestamp without time zone DEFAULT now()
);


--
-- Name: COLUMN subs.subtype; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN subs.subtype IS '''+'' or ''-''';


--
-- Name: COLUMN subs.courserole; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN subs.courserole IS 'If ''+'', role this person will play at this course meeting';


--
-- Name: COLUMN subs.dtapproved; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN subs.dtapproved IS 'Date/time office staff reviewed & approved the time --- initially set to null if parent reports absence automatically via website.';


--
-- Name: COLUMN subs.enterdtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN subs.enterdtime IS 'Date/time change first entered into system';


SET default_with_oids = false;

--
-- Name: termids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE termids (
    termtypeid integer NOT NULL,
    firstdate date NOT NULL,
    nextdate date NOT NULL,
    iscurrent boolean DEFAULT true NOT NULL,
    paymentdue date,
    billdtime timestamp without time zone,
    tuitionclass character varying(200),
    calctuition boolean DEFAULT true NOT NULL
)
INHERITS (groupids);


--
-- Name: TABLE termids; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE termids IS 'Terms over which courses run';


--
-- Name: COLUMN termids.paymentdue; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids.paymentdue IS 'Date payment for the term is due in full';


--
-- Name: COLUMN termids.billdtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids.billdtime IS 'Date this term is billed as of in the records';


--
-- Name: COLUMN termids.tuitionclass; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids.tuitionclass IS 'Java class to instantiate to calculate tuition for this term.  Must have no-arg constructor and be a subclass of offstage.school.TuitionCalc.';


--
-- Name: COLUMN termids.calctuition; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids.calctuition IS 'False if the system should NOT be touching the tuition records for this term.';


--
-- Name: termenrolls; Type: VIEW; Schema: public; Owner: ballettheatre
--

CREATE VIEW termenrolls AS
    SELECT DISTINCT cc.termid AS groupid, ee.entityid, ee.courserole, tt.name, tt.firstdate FROM enrollments ee, courseids cc, termids tt WHERE ((ee.courseid = cc.courseid) AND (cc.termid = tt.groupid)) ORDER BY cc.termid, ee.entityid, ee.courserole, tt.name, tt.firstdate;


SET default_with_oids = true;

--
-- Name: termids_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE termids_old (
    termid serial NOT NULL,
    termtypeid integer NOT NULL,
    name character varying(40),
    firstdate date NOT NULL,
    nextdate date NOT NULL,
    iscurrent boolean DEFAULT true NOT NULL,
    paymentdue date,
    billdtime timestamp without time zone
);


--
-- Name: TABLE termids_old; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE termids_old IS 'Terms over which courses run';


--
-- Name: COLUMN termids_old.paymentdue; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids_old.paymentdue IS 'Date payment for the term is due in full';


--
-- Name: COLUMN termids_old.billdtime; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termids_old.billdtime IS 'Date this term is billed as of in the records';


SET default_with_oids = false;

--
-- Name: termregs; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE termregs (
    groupid integer,
    entityid integer,
    tuition numeric(9,2),
    scholarship numeric(9,2) DEFAULT 0 NOT NULL,
    tuitionoverride numeric(9,2),
    dtsigned date,
    programid integer,
    dtregistered date NOT NULL,
    defaulttuition numeric(9,2)
)
INHERITS (groups);


--
-- Name: COLUMN termregs.tuition; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termregs.tuition IS 'Total tuition for the term for this student.';


--
-- Name: termregs_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE termregs_old (
    termid integer NOT NULL,
    entityid integer NOT NULL,
    tuition numeric(9,2),
    scholarship numeric(9,2) DEFAULT 0 NOT NULL,
    tuitionoverride numeric(9,2),
    regsigneddt date
);


--
-- Name: COLUMN termregs_old.tuition; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termregs_old.tuition IS 'Total tuition for the term for this student.';


SET default_with_oids = true;

--
-- Name: termtypes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE termtypes (
    termtypeid serial NOT NULL,
    name character varying(40) NOT NULL,
    orderid integer
);


--
-- Name: TABLE termtypes; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON TABLE termtypes IS 'Enumerate type for kinds of terms';


--
-- Name: COLUMN termtypes.orderid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN termtypes.orderid IS 'default ordering in dropdowns';


SET default_with_oids = false;

--
-- Name: ticketeventids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE ticketeventids (
)
INHERITS (groupids);


--
-- Name: ticketeventsales; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE ticketeventsales (
    numberoftickets integer,
    payment numeric(9,2),
    tickettypeid integer,
    date date,
    venueid integer,
    perftypeid integer,
    offercodeid integer
)
INHERITS (groups);


--
-- Name: tickettypes; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE tickettypes (
    tickettypeid serial NOT NULL,
    name character varying(20)
);


--
-- Name: tuitiontrans_old; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE tuitiontrans_old (
    studentid integer,
    termid integer
)
INHERITS (invoices);


--
-- Name: COLUMN tuitiontrans_old.studentid; Type: COMMENT; Schema: public; Owner: ballettheatre
--

COMMENT ON COLUMN tuitiontrans_old.studentid IS 'Student for whom this is a tuition record';


--
-- Name: venueids; Type: TABLE; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE TABLE venueids (
    venueid serial NOT NULL,
    name character varying(30) NOT NULL
);


--
-- Name: zztest; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE zztest (
    ddd abstime
);


--
-- Name: acbal_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY acbal
    ADD CONSTRAINT acbal_pkey PRIMARY KEY (entityid, actypeid);


--
-- Name: actrans_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY actrans_old
    ADD CONSTRAINT actrans_pkey PRIMARY KEY (actransid);


--
-- Name: actrans_pkey1; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY actrans
    ADD CONSTRAINT actrans_pkey1 PRIMARY KEY (actransid);


--
-- Name: actranstypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY actranstypes
    ADD CONSTRAINT actranstypes_pkey PRIMARY KEY (actranstypeid);


--
-- Name: actypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY actypes
    ADD CONSTRAINT actypes_pkey PRIMARY KEY (actypeid);


--
-- Name: adjpayments_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY adjpayments_old
    ADD CONSTRAINT adjpayments_pkey PRIMARY KEY (actransid);


--
-- Name: cashpayments_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY cashpayments_old
    ADD CONSTRAINT cashpayments_pkey PRIMARY KEY (actransid);


--
-- Name: ccbatch_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY ccbatches
    ADD CONSTRAINT ccbatch_pkey PRIMARY KEY (ccbatchid);


--
-- Name: ccpayments_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY ccpayments_old
    ADD CONSTRAINT ccpayments_pkey PRIMARY KEY (actransid);


--
-- Name: checkpayments_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY checkpayments_old
    ADD CONSTRAINT checkpayments_pkey PRIMARY KEY (actransid);


--
-- Name: classes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY classes
    ADD CONSTRAINT classes_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: classids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY classids
    ADD CONSTRAINT classids_pkey PRIMARY KEY (groupid);


--
-- Name: coursedeps_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY coursedeps
    ADD CONSTRAINT coursedeps_pkey PRIMARY KEY (basecourseid, reqcourseid);


--
-- Name: courseids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY courseids
    ADD CONSTRAINT courseids_pkey PRIMARY KEY (courseid);


--
-- Name: courseroles_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY courseroles
    ADD CONSTRAINT courseroles_pkey PRIMARY KEY (courseroleid);


--
-- Name: coursesetids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY coursesetids
    ADD CONSTRAINT coursesetids_pkey PRIMARY KEY (coursesetid);


--
-- Name: coursesets_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY coursesets
    ADD CONSTRAINT coursesets_pkey PRIMARY KEY (coursesetid, courseid);


--
-- Name: daysofweek_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY daysofweek
    ADD CONSTRAINT daysofweek_pkey PRIMARY KEY (javaid);


--
-- Name: dblogingroupids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dblogingroupids
    ADD CONSTRAINT dblogingroupids_pkey PRIMARY KEY (groupid);


--
-- Name: dblogingroups_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dblogingroups
    ADD CONSTRAINT dblogingroups_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: dblogins_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dblogins
    ADD CONSTRAINT dblogins_pkey PRIMARY KEY (username);


--
-- Name: dbversion_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dbversion
    ADD CONSTRAINT dbversion_pkey PRIMARY KEY (major, minor, rev);


--
-- Name: donationids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY donationids
    ADD CONSTRAINT donationids_pkey PRIMARY KEY (groupid);


--
-- Name: donations_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY donations
    ADD CONSTRAINT donations_pkey PRIMARY KEY (serialid);


--
-- Name: dtgroupids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dtgroupids
    ADD CONSTRAINT dtgroupids_pkey PRIMARY KEY (groupid);


--
-- Name: dtgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dtgroups
    ADD CONSTRAINT dtgroups_pkey PRIMARY KEY (groupid, entityid, date);


--
-- Name: duedateids_name_key; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY duedateids
    ADD CONSTRAINT duedateids_name_key UNIQUE (name);


--
-- Name: duedateids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY duedateids
    ADD CONSTRAINT duedateids_pkey PRIMARY KEY (duedateid);


--
-- Name: duedates_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY duedates
    ADD CONSTRAINT duedates_pkey PRIMARY KEY (termid, duedateid);


--
-- Name: dups_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY dups
    ADD CONSTRAINT dups_pkey PRIMARY KEY ("type", entityid0, entityid1);


--
-- Name: enrollments_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY enrollments
    ADD CONSTRAINT enrollments_pkey PRIMARY KEY (courseid, entityid);


--
-- Name: entities_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT entities_pkey PRIMARY KEY (entityid);


--
-- Name: equeries_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY equeries
    ADD CONSTRAINT equeries_pkey PRIMARY KEY (equeryid);


--
-- Name: eventids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY eventids
    ADD CONSTRAINT eventids_pkey PRIMARY KEY (groupid);


--
-- Name: events_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY events
    ADD CONSTRAINT events_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: flagids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY flagids
    ADD CONSTRAINT flagids_pkey PRIMARY KEY (groupid);


--
-- Name: flags_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY flags
    ADD CONSTRAINT flags_pkey PRIMARY KEY (entityid, groupid);


--
-- Name: gradelevels_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY gradelevels
    ADD CONSTRAINT gradelevels_pkey PRIMARY KEY (ngrade);


--
-- Name: groupids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY groupids
    ADD CONSTRAINT groupids_pkey PRIMARY KEY (groupid);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: holidays_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY holidays
    ADD CONSTRAINT holidays_pkey PRIMARY KEY (termid, firstday);


--
-- Name: interestids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY interestids
    ADD CONSTRAINT interestids_pkey PRIMARY KEY (groupid);


--
-- Name: interests_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY interests
    ADD CONSTRAINT interests_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: locations_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (locationid);


--
-- Name: mailingids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY mailingids
    ADD CONSTRAINT mailingids_pkey PRIMARY KEY (groupid);


--
-- Name: mailprefids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY mailprefids
    ADD CONSTRAINT mailprefids_pkey PRIMARY KEY (mailprefid);


--
-- Name: meetings_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY meetings
    ADD CONSTRAINT meetings_pkey PRIMARY KEY (meetingid);


--
-- Name: mergelog_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY mergelog
    ADD CONSTRAINT mergelog_pkey PRIMARY KEY (entityid0, entityid1);


--
-- Name: noteids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY noteids
    ADD CONSTRAINT noteids_pkey PRIMARY KEY (groupid);


--
-- Name: offercodes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY offercodeids
    ADD CONSTRAINT offercodes_pkey PRIMARY KEY (offercodeid);


--
-- Name: perftypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY perftypeids
    ADD CONSTRAINT perftypes_pkey PRIMARY KEY (perftypeid);


--
-- Name: persons_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY persons
    ADD CONSTRAINT persons_pkey PRIMARY KEY (entityid);


--
-- Name: phoneids_name_key; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY phoneids
    ADD CONSTRAINT phoneids_name_key UNIQUE (name);


--
-- Name: phoneids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY phoneids
    ADD CONSTRAINT phoneids_pkey PRIMARY KEY (groupid);


--
-- Name: phoneids_priority_key; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY phoneids
    ADD CONSTRAINT phoneids_priority_key UNIQUE (priority);


--
-- Name: phones_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY phones
    ADD CONSTRAINT phones_pkey PRIMARY KEY (groupid, entityid);


--
-- Name: querylog_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY querylog
    ADD CONSTRAINT querylog_pkey PRIMARY KEY (queryid);


--
-- Name: querylogcols_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY querylogcols
    ADD CONSTRAINT querylogcols_pkey PRIMARY KEY (queryid, colname);


--
-- Name: regelig_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY regelig
    ADD CONSTRAINT regelig_pkey PRIMARY KEY (programid, entityid);


--
-- Name: registrations_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY registrations
    ADD CONSTRAINT registrations_pkey PRIMARY KEY (programid, entityid);


--
-- Name: relprimarytypes_name_key; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY relprimarytypes
    ADD CONSTRAINT relprimarytypes_name_key UNIQUE (name);


--
-- Name: relprimarytypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY relprimarytypes
    ADD CONSTRAINT relprimarytypes_pkey PRIMARY KEY (relprimarytypeid);


--
-- Name: resourceids_name_key; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY resourceids
    ADD CONSTRAINT resourceids_name_key UNIQUE (name);


--
-- Name: resourceids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY resourceids
    ADD CONSTRAINT resourceids_pkey PRIMARY KEY (resourceid);


--
-- Name: resources_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY resources
    ADD CONSTRAINT resources_pkey PRIMARY KEY (resourceid, uversionid, version);


--
-- Name: students_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY entities_school
    ADD CONSTRAINT students_pkey PRIMARY KEY (entityid);


--
-- Name: subs_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY subs
    ADD CONSTRAINT subs_pkey PRIMARY KEY (meetingid, entityid);


--
-- Name: termduedates_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY duedates_old
    ADD CONSTRAINT termduedates_pkey PRIMARY KEY (termid, name);


--
-- Name: termids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY termids_old
    ADD CONSTRAINT termids_pkey PRIMARY KEY (termid);


--
-- Name: termids_pkey2; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY termids
    ADD CONSTRAINT termids_pkey2 PRIMARY KEY (groupid);


--
-- Name: termregs_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY termregs_old
    ADD CONSTRAINT termregs_pkey PRIMARY KEY (termid, entityid);


--
-- Name: termregs_pkey2; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY termregs
    ADD CONSTRAINT termregs_pkey2 PRIMARY KEY (groupid, entityid);


--
-- Name: termtypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY termtypes
    ADD CONSTRAINT termtypes_pkey PRIMARY KEY (termtypeid);


--
-- Name: ticketeventids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY ticketeventids
    ADD CONSTRAINT ticketeventids_pkey PRIMARY KEY (groupid);


--
-- Name: tickettypes_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY tickettypes
    ADD CONSTRAINT tickettypes_pkey PRIMARY KEY (tickettypeid);


--
-- Name: tuitiontrans_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY tuitiontrans_old
    ADD CONSTRAINT tuitiontrans_pkey PRIMARY KEY (actransid);


--
-- Name: venueids_pkey; Type: CONSTRAINT; Schema: public; Owner: ballettheatre; Tablespace: 
--

ALTER TABLE ONLY venueids
    ADD CONSTRAINT venueids_pkey PRIMARY KEY (venueid);


--
-- Name: zztest_ddd_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY zztest
    ADD CONSTRAINT zztest_ddd_key UNIQUE (ddd);


--
-- Name: entities_school_adultid; Type: INDEX; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE INDEX entities_school_adultid ON entities_school USING btree (adultid);


--
-- Name: entities_school_parent2id; Type: INDEX; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE INDEX entities_school_parent2id ON entities_school USING btree (parent2id);


--
-- Name: entities_school_parentid; Type: INDEX; Schema: public; Owner: ballettheatre; Tablespace: 
--

CREATE INDEX entities_school_parentid ON entities_school USING btree (parentid);


--
-- Name: entities_relprimarytypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ballettheatre
--

ALTER TABLE ONLY entities
    ADD CONSTRAINT entities_relprimarytypeid_fkey FOREIGN KEY (relprimarytypeid) REFERENCES relprimarytypes(relprimarytypeid);


--
-- Name: groups_entityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ballettheatre
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_entityid_fkey FOREIGN KEY (entityid) REFERENCES entities(entityid);


--
-- Name: groups_groupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ballettheatre
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_groupid_fkey FOREIGN KEY (groupid) REFERENCES groupids(groupid);


--
-- Name: ticketeventsales_tickettypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ballettheatre
--

ALTER TABLE ONLY ticketeventsales
    ADD CONSTRAINT ticketeventsales_tickettypeid_fkey FOREIGN KEY (tickettypeid) REFERENCES tickettypes(tickettypeid);


--
-- PostgreSQL database dump complete
--

