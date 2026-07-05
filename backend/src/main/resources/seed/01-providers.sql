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
-- Data for Name: provider; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.provider (id, source_id, code, name) VALUES (1, 2, 'MOHW', '보건복지부');
INSERT INTO public.provider (id, source_id, code, name) VALUES (2, 2, 'KINFA', '서민금융진흥원');
INSERT INTO public.provider (id, source_id, code, name) VALUES (3, 2, 'BUSAN_CITY', '부산광역시');
INSERT INTO public.provider (id, source_id, code, name) VALUES (4, 2, 'GYEONGGI', '경기도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (5, 2, 'INCHEON', '인천광역시');
INSERT INTO public.provider (id, source_id, code, name) VALUES (6, 2, 'GANGWON', '강원특별자치도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (7, 2, 'GWANGJU', '광주광역시');
INSERT INTO public.provider (id, source_id, code, name) VALUES (8, 2, 'JEONNAM', '전라남도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (9, 2, 'GYEONGNAM', '경상남도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (10, 2, 'JEONBUK', '전북특별자치도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (11, 2, 'DAEGU', '대구광역시');
INSERT INTO public.provider (id, source_id, code, name) VALUES (12, 2, 'SEJONG', '세종특별자치시');
INSERT INTO public.provider (id, source_id, code, name) VALUES (13, 2, 'GYEONGBUK', '경상북도');
INSERT INTO public.provider (id, source_id, code, name) VALUES (14, 2, 'HAMAN', '함안군');
INSERT INTO public.provider (id, source_id, code, name) VALUES (15, 1, '0010001', '우리은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (16, 1, '0010002', 'SC제일은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (17, 1, '0010016', 'iM뱅크');
INSERT INTO public.provider (id, source_id, code, name) VALUES (18, 1, '0010017', '부산은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (19, 1, '0010019', '광주은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (20, 1, '0010020', '제주은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (21, 1, '0010022', '전북은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (22, 1, '0010024', '경남은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (23, 1, '0010026', 'IBK기업은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (24, 1, '0010030', 'KDB산업은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (25, 1, '0010927', 'KB국민은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (26, 1, '0011625', '신한은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (27, 1, '0013175', 'NH농협은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (28, 1, '0013909', '하나은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (29, 1, '0014674', '케이뱅크');
INSERT INTO public.provider (id, source_id, code, name) VALUES (30, 1, '0014807', 'Sh수협은행');
INSERT INTO public.provider (id, source_id, code, name) VALUES (31, 1, '0015130', '카카오뱅크');
INSERT INTO public.provider (id, source_id, code, name) VALUES (32, 1, '0017801', '토스뱅크');


--
-- Name: provider_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.provider_id_seq', 32, true);


--
-- PostgreSQL database dump complete
--


