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

INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (1, 2, 'MOHW', '보건복지부', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (2, 2, 'BUSAN_CITY', '부산광역시', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (3, 2, 'KINFA', '서민금융진흥원', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (4, 2, 'GYEONGGI', '경기도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (5, 2, 'INCHEON', '인천광역시', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (6, 2, 'GANGWON', '강원특별자치도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (7, 2, 'GWANGJU', '광주광역시', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (8, 2, 'JEONNAM', '전라남도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (9, 2, 'GYEONGNAM', '경상남도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (10, 2, 'JEONBUK', '전북특별자치도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (11, 2, 'DAEGU', '대구광역시', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (12, 2, 'SEJONG', '세종특별자치시', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (13, 2, 'GYEONGBUK', '경상북도', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (14, 2, 'HAMAN', '함안군', NULL);
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (15, 1, '0010001', '우리은행', 'https://spot.wooribank.com/pot/Dream?withyou=PODEP0001');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (16, 1, '0010002', 'SC제일은행', 'https://www.standardchartered.co.kr/np/kr/pl/se/SavingList.jsp?ptfrm=HIN.KOR.INTRO.mega.korPerA1_1&id=list1');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (17, 1, '0010016', 'iM뱅크', 'https://www.imbank.co.kr/com_ebz_fpm_sub_main.jsp');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (18, 1, '0010017', '부산은행', 'https://www.busanbank.co.kr/ib20/mnu/FPM00001');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (19, 1, '0010019', '광주은행', 'https://www.kjbank.com/ib20/mnu/FPM0000000001');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (20, 1, '0010020', '제주은행', 'https://www.jejubank.co.kr/hmpg/prdGdnc/sid/mndp.do');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (21, 1, '0010022', '전북은행', 'https://www.jbbank.co.kr/');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (22, 1, '0010024', '경남은행', 'https://www.knbank.co.kr/ib20/mnu/FPM000000000001');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (23, 1, '0010026', 'IBK기업은행', 'https://mybank.ibk.co.kr/uib/jsp/guest/ntr/ntr00/ntr0000/PNTR000000_i.jsp?_linkFrmChk=Y');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (24, 1, '0010030', 'KDB산업은행', 'https://www.kdb.co.kr/index.jsp');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (25, 1, '0010927', 'KB국민은행', 'https://obank.kbstar.com/quics?page=C030037');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (26, 1, '0011625', '신한은행', 'https://bank.shinhan.com/index.jsp#020001000000');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (27, 1, '0013175', 'NH농협은행', 'https://smartmarket.nonghyup.com/servlet/BFBCW0001R.view');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (28, 1, '0013909', '하나은행', 'https://www.kebhana.com/cont/mall/index.jsp');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (29, 1, '0014674', '케이뱅크', 'https://www.kbanknow.com/web/product/info/list?tab=deposit');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (30, 1, '0014807', 'Sh수협은행', 'https://www.suhyup-bank.com/');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (31, 1, '0015130', '카카오뱅크', 'https://www.kakaobank.com/products/withdrawal');
INSERT INTO public.provider (id, source_id, code, name, apply_url) VALUES (32, 1, '0017801', '토스뱅크', 'https://www.tossbank.com/product-service/savings/time-deposit');


--
-- Name: provider_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.provider_id_seq', 32, true);


--
-- PostgreSQL database dump complete
--


