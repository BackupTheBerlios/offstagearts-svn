--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
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
-- Name: money2numeric(money); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION money2numeric(money) RETURNS numeric
    AS $_$ SELECT (((get_byte(cash_send($1), 0) & 255) << 24) |((get_byte(cash_send($1), 1) & 255) << 16) |((get_byte(cash_send($1), 2) & 255) << 8) |((get_byte(cash_send($1), 3) & 255)))::numeric * 0.01; $_$
    LANGUAGE sql IMMUTABLE STRICT;


ALTER FUNCTION public.money2numeric(money) OWNER TO postgres;

--
-- Name: plpgsql_call_handler(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_call_handler() RETURNS language_handler
    AS '$libdir/plpgsql', 'plpgsql_call_handler'
    LANGUAGE c;


ALTER FUNCTION public.plpgsql_call_handler() OWNER TO postgres;

--
-- Name: plpgsql_validator(oid); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION plpgsql_validator(oid) RETURNS void
    AS '$libdir/plpgsql', 'plpgsql_validator'
    LANGUAGE c;


ALTER FUNCTION public.plpgsql_validator(oid) OWNER TO postgres;

SET search_path = pg_catalog;

--
-- Name: CAST (money AS numeric); Type: CAST; Schema: pg_catalog; Owner: 
--

CREATE CAST (money AS numeric) WITH FUNCTION public.money2numeric(money) AS IMPLICIT;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

