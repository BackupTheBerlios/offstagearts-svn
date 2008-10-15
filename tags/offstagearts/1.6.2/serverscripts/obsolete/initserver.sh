psql template1 -U postgres <<EOF

CREATE ROLE offstageusers
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;

CREATE DATABASE offstagetemplate1
  WITH ENCODING='UTF8';
EOF

psql offstagetemplate1 <offstagetemplate1.sql
