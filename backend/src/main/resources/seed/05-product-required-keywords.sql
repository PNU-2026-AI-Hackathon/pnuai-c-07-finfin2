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

INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (1, 19, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (2, 20, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (3, 21, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (4, 22, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (5, 23, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (6, 24, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (7, 29, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (8, 42, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (9, 43, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (10, 44, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (11, 45, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (12, 46, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (13, 53, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (14, 54, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (15, 55, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (16, 56, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (17, 63, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (18, 64, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (19, 65, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (20, 66, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (21, 67, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (22, 68, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (23, 72, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (24, 73, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (25, 74, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (26, 75, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (27, 91, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (28, 92, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (29, 93, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (30, 94, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (31, 95, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (32, 96, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (33, 97, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (34, 98, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (35, 99, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (36, 100, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (37, 101, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (38, 108, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (39, 109, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (40, 110, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (41, 111, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (42, 112, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (43, 113, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (44, 118, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (45, 119, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (46, 120, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (47, 121, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (48, 122, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (49, 123, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (50, 124, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (51, 125, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (52, 126, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (53, 127, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (54, 128, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (55, 129, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (56, 130, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (57, 131, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (58, 132, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (59, 133, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (60, 134, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (61, 135, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (62, 136, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (63, 137, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (64, 138, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (65, 139, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (66, 140, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (67, 141, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (68, 142, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (69, 143, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (70, 144, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (71, 145, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (72, 146, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (73, 147, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (74, 148, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (75, 149, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (76, 150, 'STATUS_UNEMPLOYED', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (77, 151, 'STATUS_UNEMPLOYED', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (78, 152, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (79, 153, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (80, 154, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (81, 155, 'STATUS_PART_TIME', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (82, 156, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (83, 157, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (84, 158, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (85, 159, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (86, 160, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (87, 161, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (88, 162, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (89, 163, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (90, 164, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (91, 165, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (92, 166, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (93, 167, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (94, 168, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (95, 169, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (96, 170, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (97, 171, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (98, 172, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (99, 173, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (100, 174, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (101, 175, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (102, 176, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (103, 177, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (104, 178, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (105, 179, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (106, 180, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (107, 181, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (108, 196, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (109, 205, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (110, 206, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (111, 207, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (112, 208, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (113, 209, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (114, 210, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (115, 211, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (116, 212, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (117, 213, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (118, 214, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (119, 215, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (120, 216, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (121, 220, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (122, 221, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (123, 222, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (124, 223, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (125, 224, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (126, 225, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (127, 234, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (128, 238, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (129, 239, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (130, 240, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (131, 241, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (132, 242, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (133, 243, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (134, 244, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (135, 245, 'STATUS_PART_TIME', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (136, 246, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (137, 247, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (138, 248, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (139, 249, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (140, 250, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (141, 251, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (142, 252, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (143, 253, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (144, 254, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (145, 261, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (146, 262, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (147, 263, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (148, 264, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (149, 265, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (150, 266, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (151, 267, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (152, 268, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (153, 269, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (154, 270, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (155, 271, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (156, 272, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (157, 273, 'STATUS_SME_WORKER', 'EXCLUDE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (158, 286, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (159, 287, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (160, 288, 'STATUS_SME_WORKER', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (161, 292, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (162, 293, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (163, 294, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (164, 295, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (165, 296, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (166, 297, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (167, 298, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (168, 299, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (169, 300, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (170, 301, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (171, 302, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (172, 315, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (173, 316, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (174, 317, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (175, 318, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (176, 319, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (177, 320, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (178, 321, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (179, 327, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (180, 328, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (181, 329, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (182, 330, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (183, 331, 'STATUS_PART_TIME', 'REQUIRE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (184, 332, 'STATUS_UNEMPLOYED', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (185, 332, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (186, 333, 'STATUS_UNEMPLOYED', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (187, 333, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (188, 334, 'STATUS_UNEMPLOYED', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (189, 334, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (190, 335, 'STATUS_UNEMPLOYED', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (191, 335, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (192, 338, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (193, 339, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (194, 340, 'STATUS_SME_WORKER', 'EXCLUDE', 'LOW');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (195, 341, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (196, 342, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (197, 343, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (198, 344, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (199, 345, 'STATUS_PART_TIME', 'REQUIRE', 'HIGH');
INSERT INTO public.product_property_required_keyword (id, product_property_id, keyword_code, effect, confidence) VALUES (200, 346, 'STATUS_UNEMPLOYED', 'EXCLUDE', 'HIGH');


--
-- Name: product_property_required_keyword_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.product_property_required_keyword_id_seq', 200, true);


--
-- PostgreSQL database dump complete
--


