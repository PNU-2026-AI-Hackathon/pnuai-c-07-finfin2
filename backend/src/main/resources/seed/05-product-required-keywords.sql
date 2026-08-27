--
-- PostgreSQL database dump
--


-- Dumped from database version 16.13 (Debian 16.13-1.pgdg12+1)
-- Dumped by pg_dump version 16.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: product_property_required_keyword; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (31, 103, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (32, 16, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');


--
-- Name: product_property_required_keyword_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.product_property_required_keyword_id_seq', 32, true);


--
-- PostgreSQL database dump complete
--


