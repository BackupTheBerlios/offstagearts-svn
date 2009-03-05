-- Simplified database versioning for all hokserver applications

CREATE TABLE dbbversions
(
   schemaname character varying(50) NOT NULL primary key, 
   "version" integer NOT NULL, 
   lastupdated timestamp without time zone NOT NULL DEFAULT now()
) WITH (OIDS=FALSE)
;
