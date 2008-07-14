insert into entities (orgname, obsolete, sink, primaryentityid) values ('openclass', true, true, -1);
update entities set primaryentityid=entityid where sink;
