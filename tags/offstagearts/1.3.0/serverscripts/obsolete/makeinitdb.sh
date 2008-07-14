pg_dump --encoding=UNICODE --disable-dollar-quoting --schema-only --no-owner --no-privileges -U postgres ballettheatre >../src/main/resources/offstage/resource/database-schema-$1.sql
