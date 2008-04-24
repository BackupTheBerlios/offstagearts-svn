#!/bin/sh
# Usage: initdb name

psql $1 -U $1 <initdb.sql
