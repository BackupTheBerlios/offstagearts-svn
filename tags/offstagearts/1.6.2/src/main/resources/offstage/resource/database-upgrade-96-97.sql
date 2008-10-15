create table ocdiscids (
ocdiscid serial not null primary key,
name varchar(30),
teacherid int,		-- If not null, this discount only good for a particular teacher
dtstart date,		-- First day this discount is available (can be null)
dtend date			-- Last day it's available (can be null)
); -- inherits ( groupids);

create table ocdiscidsamt (
ocdiscid int not null,
ocdisccatid int not null,		-- category of this discount segment
dollars numeric(9,2),			-- amount ($) of discount in this ocdisccatid category
pct numeric(9,2),			-- amount (% of nominal price or teacher portion) of discount in this ocdisccatid category
primary key(ocdiscid, ocdisccatid)
);


-- The discount codes for which a student is eligible.  These match to
-- discount codes in the Java plug-in code (and are drawn from a drop-down
-- provided by it as well).
create table ocdiscs (
serialid serial primary key,
entityid int not null,
ocdiscid int not null,
--disccode varchar(30) not null,
dtstart date,
dtend date,
   FOREIGN KEY (ocdiscid)
      REFERENCES ocdiscids (ocdiscid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);



-- Vector of numeric pricing values stored for each open class sign-in
-- (in subs table)
create table ocdisccatids (
ocdisccatid serial primary key,
--assetid int not null,
name varchar(30),
description varchar(30));

insert into ocdisccatids (ocdisccatid, name,description) values (
0, 'price', 'Price');

insert into ocdisccatids (name,description) values (
'studio_disc', 'Studio Discount ($)');

insert into ocdisccatids (name,description) values (
'teacher_disc', 'Teacher Discount ($)');


