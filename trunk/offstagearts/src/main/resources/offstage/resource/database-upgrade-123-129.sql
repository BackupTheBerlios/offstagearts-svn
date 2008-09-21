-- Allow for different "databases" within the system.
-- dbid=0 is the main database.
-- dbid>0 is for imports and merges from other sources.

alter table entities add column dbid int default 0;
create table dbids (dbid serial, name varchar(30), description varchar(200));
insert into dbids (dbid, name, description) values (0, 'default', 'Main Database');
--insert into dbids (name, description) values ('import', 'Imported records, may need to be merged with main database');

--create table dupids (dupid serial not null, dtime timestamp default now() not null,
--	name varchar(100) default 'Duplicates', dbid0 int not null, dbid1 int not null);
--insert into dupids (name, dbid0, dbid1) values ('Basic Duplicates', 0, 0);

alter table dups add column dbid0 int default 0;
alter table dups add column dbid1 int default 0;

alter table entities add column dobapprox bool default false;	-- true if we have just an approximate date of birth (eg, within a month or so)
alter table holidays add column entityid int default null;
