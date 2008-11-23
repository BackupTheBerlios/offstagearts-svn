CREATE TABLE emailingids
(
   emailingid serial primary key, 
   dtime timestamp default now() not null,
   emailproviderid integer not null, 
   emailingtype character(1) NOT NULL, 
   equeryid int,
   equery text,
   campaignname character varying(50), 
   groupname character varying(50)
) WITH (OIDS=FALSE)
;

COMMENT ON COLUMN emailingids.emailproviderid IS 'Back-end email provider';
COMMENT ON COLUMN emailingids.emailingtype IS '''c'' for customer service, ''m'' for mass email';
COMMENT ON COLUMN emailingids.campaignname IS 'Provider-assigned id identifying this mailing';
COMMENT ON COLUMN emailingids.groupname IS 'Self-assigned name of a group set up for this mailing.  For some providers, it will be null.';
COMMENT ON COLUMN emailingids.equeryid IS 'The Offstage EQuery used to generate this mailing.  Can be null if not EQuery-driven';

create table emailproviderids (emailproviderid serial primary key,
name varchar(30), url varchar(300));
insert into emailproviderids (name, url) values
	('JangoMail', 'http://www.jangomail.com');
insert into emailproviderids (name, url) values
	('SMTP', null);

create table emailings (
emailingid int,
entityid int,
primary key(emailingid, entityid));
