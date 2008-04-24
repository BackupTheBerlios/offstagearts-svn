#!/bin/sh
# Usage: newuser name password

psql template1 -U postgres <<EOF
CREATE ROLE $1 LOGIN PASSWORD '$2'
   VALID UNTIL 'infinity'
   CONNECTION LIMIT 50
   IN ROLE offstageusers;

CREATE DATABASE $1
  WITH ENCODING='UTF8'
       OWNER=$1;
EOF

psql $1 -U postgres <<EOF
CREATE PROCEDURAL LANGUAGE plpgsql;
ALTER SCHEMA public OWNER TO $1;
EOF
