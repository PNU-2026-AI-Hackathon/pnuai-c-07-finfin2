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
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: user
--

INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (13, 2, 'POLICY', 'POLICY011', '청년 디딤돌 2배 적금 (강원도)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 36개월 = 360만 원 | 매칭 기여금: 기업 5만+도·시 5만 = 월 10만(1:1) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '강원특별자치도 경제진흥원', NULL, '2026-07-14 18:01:59.91881+09', '2026-07-14 18:01:59.944608+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (14, 2, 'POLICY', 'POLICY012', '전북청년 함께 두배적금 (전북)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 24개월 = 240만 원 | 매칭 기여금: 도·시군 240만 원(1:1) | 정부기여금 환산수익률: 연 50.0% (2년)', NULL, NULL, NULL, '전북. 14개 시군 명의 통장', NULL, '2026-07-14 18:01:59.951504+09', '2026-07-14 18:01:59.978217+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (3, 2, 'POLICY', 'POLICY001', '청년내일저축계좌', '매칭 유형: 정액 | 본인 부담금: 월 10만~50만 원 본인 저축 | 매칭 기여금: 정부 근로소득장려금 중위소득 50% 이하 월 30만 원 정액(1:3) | 정부기여금 환산수익률: 연 100.0% (3년, 본인 월10만 기준)', NULL, NULL, '만 15~39세, 기준 중위소득 50% 이하', '전국 단일사업. 2026년부터 차상위 초과자 신규모집 중단', NULL, '2026-07-14 15:05:32.933199+09', '2026-07-14 15:22:56.076324+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (4, 2, 'POLICY', 'POLICY003', '부산청년 기쁨두배통장', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원, 24/36개월 | 매칭 기여금: 부산시 1:1 동일액 | 정부기여금 환산수익률: 연 50.0%(2년) / 33.3%(3년)', NULL, NULL, '만 18~39세, 부산 거주 청년', '부산시·부산은행 협약 (boogi2.kr)', NULL, '2026-07-14 15:05:33.026141+09', '2026-07-14 15:22:56.175465+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (5, 2, 'POLICY', 'POLICY002', '청년 자산형성 지원 (청년미래적금)', '매칭 유형: 정률 (월 납입 비례, 만기 누적) | 본인 부담금: 월 최대 50만 원 자유 납입, 3년 | 매칭 기여금: 정부기여금 일반형 108만 원 / 우대형 216만 원 | 정부기여금 환산수익률: 일반형 연 2.0% / 우대형 연 4.0%', NULL, NULL, '만 19~34세, 소득 조건에 따라 일반형 또는 우대형 적용', '서민금융진흥원. 2026년 6월 출시 예정 (청년도약계좌 대체). 기본 5% + 기관별 우대 2~3%', NULL, '2026-07-14 16:27:53.772237+09', '2026-07-14 16:27:53.830265+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (6, 2, 'POLICY', 'POLICY004', '청년 노동자 통장 (경기도)', '매칭 유형: 정률 1:1 + 지역화폐 | 본인 부담금: 월 10만 원, 24개월 | 매칭 기여금: 경기도 1:1(현금 240만) + 지역화폐 100만 | 정부기여금 환산수익률: 연 50.0%(현금) / 70.8%(지역화폐 포함)', NULL, NULL, NULL, '경기도미래세대재단 운영', NULL, '2026-07-14 18:01:59.630641+09', '2026-07-14 18:01:59.698748+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (7, 2, 'POLICY', 'POLICY005', '드림For 청년통장 (인천시)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 15만 원 x 36회 = 540만 원 | 매칭 기여금: 인천시 540만 원(3년 만기 일괄) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '인천시. 취급: 신한 청년DREAM적금', NULL, '2026-07-14 18:01:59.710344+09', '2026-07-14 18:01:59.739698+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (8, 2, 'POLICY', 'POLICY006', '청년발달장애인 자산형성지원 (행복씨앗통장, 인천)', '매칭 유형: 정액 | 본인 부담금: 월 15만 원 x 36개월 = 540만 원 | 매칭 기여금: 지원금 월 15만 원 정액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '인천시, 청년 발달장애인 대상', NULL, '2026-07-14 18:01:59.746844+09', '2026-07-14 18:01:59.768175+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (9, 2, 'POLICY', 'POLICY007', '청년 재가 중증장애인 자산형성 지원 (강원도)', '매칭 유형: 정액 | 본인 부담금: 월 15만 원 이상, 3년 | 매칭 기여금: 지원금 월 15만 원 정액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '강원도, 재가 중증장애 청년 대상', NULL, '2026-07-14 18:01:59.77651+09', '2026-07-14 18:01:59.802047+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (10, 2, 'POLICY', 'POLICY008', '광주 청년13(일+삶)통장', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 10개월 = 100만 원 | 매칭 기여금: 광주시 100만 원(만기 일괄) | 정부기여금 환산수익률: 연 120.0% (10개월)', NULL, NULL, NULL, '광주청년통합플랫폼 운영 (단기 10개월)', NULL, '2026-07-14 18:01:59.810502+09', '2026-07-14 18:01:59.841458+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (11, 2, 'POLICY', 'POLICY009', '청년 희망디딤돌 통장 (전남)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 36개월 = 360만 원 | 매칭 기여금: 전남도(40%)+시군(60%) 동일액(1:1) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '전남. 도·시군 운영분 통합(구 9·19번)', NULL, '2026-07-14 18:01:59.849195+09', '2026-07-14 18:01:59.878699+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (12, 2, 'POLICY', 'POLICY010', '모다드림 청년통장 (경남)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 20만 원 x 24개월 = 480만 원 | 매칭 기여금: 경남도·시군 480만 원(1:1) | 정부기여금 환산수익률: 연 50.0% (2년)', NULL, NULL, NULL, '경남. BNK경남은행. 도·시군 운영분 통합(구 10·20번)', NULL, '2026-07-14 18:01:59.885516+09', '2026-07-14 18:01:59.911292+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (15, 2, 'POLICY', 'POLICY013', '대구 청년희망적금', '매칭 유형: 정률 1:1 | 본인 부담금: 월 10만 원 x 12개월 = 120만 원 | 매칭 기여금: 대구시 120만 원(1:1) | 정부기여금 환산수익률: 연 100.0% (1년)', NULL, NULL, NULL, '대구시. 8개월 이상 근로 조건 (단기 1년)', NULL, '2026-07-14 18:01:59.986583+09', '2026-07-14 18:02:00.022165+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (16, 2, 'POLICY', 'POLICY014', '세종 청년미래적금 (청년희망적금)', '매칭 유형: 정률 1:1 | 본인 부담금: 월 15만 원 x 36개월 = 540만 원 | 매칭 기여금: 세종시 540만 원(1:1, 만기 일시지급) | 정부기여금 환산수익률: 연 33.3% (3년)', NULL, NULL, NULL, '세종시. 금융위 상품과 별개 지자체 사업', NULL, '2026-07-14 18:02:00.032406+09', '2026-07-14 18:02:00.05956+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (17, 2, 'POLICY', 'POLICY015', '청년 사랑채움사업 (경북 내 22개 시·군)', '매칭 유형: 정률 1:1 + 결혼축하금 | 본인 부담금: 월 20만 원 x 24회 = 480만 원 | 매칭 기여금: 지자체 480만 원(1:1) + 결혼축하금 120만(조건부) | 정부기여금 환산수익률: 연 50.0% (2년, 축하금 환산 제외)', NULL, NULL, NULL, '경북도경제진흥원 통합사업(구 15·16·17번)', NULL, '2026-07-14 18:02:00.06656+09', '2026-07-14 18:02:00.09612+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (47, 1, 'SAVING', 'FSS:SAVING:020000:0010030:03700', 'KDB 자유적금', '영업점,인터넷,스마트폰

* 만기후 1년 이내 : 만기일 현재 고시된 일반 정기적금 해당예금기간 기본이율의 1/2
* 만기후 1년 초과 : 만기일 현재 고시된 보통예금 이율

해당없음

개인, 개인사업자, 임의단체

해당없음', '가입방법: 영업점, 인터넷, 스마트폰
유의사항: 만기후 1년 이내에는 만기일 현재 고시된 일반 정기적금 해당 예금기간 기본이율의 1/2, 만기후 1년 초과 시에는 만기일 현재 고시된 보통예금 이율 적용.', '영업점,인터넷,스마트폰', '개인, 개인사업자, 임의단체', '해당없음', NULL, '2026-07-14 19:59:26.116855+09', '2026-07-24 00:21:06.393867+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (18, 2, 'POLICY', 'POLICY016', '함안정착 청년통장 (함안군)', '매칭 유형: 정률 1:1.5 | 본인 부담금: 월 20만 원 x 36개월 = 720만 원 | 매칭 기여금: 함안군 월 30만 x 36개월 = 1,080만 원(1.5배) | 정부기여금 환산수익률: 연 50.0% (3년)', NULL, NULL, NULL, '경남 함안군. 2026년 청년·군 3년 적립으로 개편', NULL, '2026-07-14 18:02:00.104757+09', '2026-07-14 18:02:00.140614+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (24, 1, 'SAVING', 'FSS:SAVING:020000:0010017:01020400510001', '저탄소 실천 적금', '영업점,인터넷,스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

조건 달성여부에 따라 우대금리 적용
*개인형 우대이율: 최대0.5%
*기업형(개인사업자 및 법인)우대이율: 최대0.4%

제한없음

1. 가입한도: 월 1만원 이상 1천만원 이하 원단위 (월 1천만원 이하 불입 가능)
2. 자유적립식', '가입방법: 영업점, 인터넷, 스마트폰
유의사항: 만기후 1년이내 일반정기적금 기본이율의 50% 적용, 만기후 1년초과 일반정기적금 기본이율의 20% 적용', '영업점,인터넷,스마트폰', '제한없음', '1. 가입한도: 월 1만원 이상 1천만원 이하 원단위 (월 1천만원 이하 불입 가능)
2. 자유적립식', NULL, '2026-07-14 19:58:52.765866+09', '2026-07-24 00:20:27.001423+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (93, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0063-0000', '내맘 쏙 정기예금', '인터넷,스마트폰

만기후 1개월 이하 : 만기일 현재 계약기간별 정기예금 실행이율 1/2
만기후 1개월 초과 : 연 0.01%

1. 마케팅동의고객 0.10%

실명의 개인 및 개인사업자

가입금액 : 계좌당 10만원 이상
예금의 신규 : 모바일뱅킹, 모바일Web
예금의 해지 : 모바일뱅킹, 인터넷뱅킹, 영업점
계약기간 : 1개월이상 12개월이내(월단위)', '가입방법: 인터넷, 스마트폰
우대조건: 마케팅 동의 시 0.10%p 우대
가입대상: 실명의 개인 및 개인사업자
유의사항: 가입금액 10만원 이상, 계약기간 1개월 이상 12개월 이내(월단위)', '인터넷,스마트폰', '실명의 개인 및 개인사업자', '가입금액 : 계좌당 10만원 이상
예금의 신규 : 모바일뱅킹, 모바일Web
예금의 해지 : 모바일뱅킹, 인터넷뱅킹, 영업점
계약기간 : 1개월이상 12개월이내(월단위)', NULL, '2026-07-14 20:01:18.565355+09', '2026-07-24 00:23:15.929309+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (20, 1, 'SAVING', 'FSS:SAVING:020000:0010001:WR0001L', 'WON적금', '스마트폰,전화(텔레뱅킹)

만기 후
- 1개월이내 : 만기시점약정이율×50%
- 1개월초과 6개월이내: 만기시점약정이율×30%
- 6개월초과 : 만기시점약정이율×20%

※ 만기시점 약정이율 : 일반정기적금 금리

1. 아래 각 항(가, 나)의 조건을 충족하는 경우 합산 최대 연 0.2%p 우대
가. 이 적금을 우리꿈통장, WON통장에 연결하여 가입하는 경우 : 0.1%p
나. 우리 오픈뱅킹 서비스에 타행계좌가 등록되어 있는 경우 : 연 0.1%p

실명의 개인

1. 가입기간 : 1년
2. 가입금액 : 월 50만원 이내', NULL, '스마트폰,전화(텔레뱅킹)', '실명의 개인', '1. 가입기간 : 1년
2. 가입금액 : 월 50만원 이내', NULL, '2026-07-14 19:58:52.579489+09', '2026-07-24 00:20:26.89194+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (32, 1, 'SAVING', 'FSS:SAVING:020000:0010020:220002501', 'MZ 플랜적금', '영업점,인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기적금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기적금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

①매월 1회이상 지속적 납입시:1년제: 0.50%
② 목표 금액 달성:0.50%
③ 신용카드,체크카드 합산 사용액 월10만원 이상 사용: 0.50%
* 청년이니까응원합니다 이벤트 : 0.50%

개인 및 개인사업자

월 납입한도 30만원 이하', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 매월 1회 이상 지속적 납입 시, 목표 금액 달성 시, 신용카드/체크카드 합산 월 10만원 이상 사용 시, 청년이니까응원합니다 이벤트 참여 시
가입대상: 개인 및 개인사업자
유의사항: 월 납입한도 30만원 이하, 만기 후 이자율은 일반 정기적금 기본 이자율의 50%~25% 적용 (최저 0.1%)', '영업점,인터넷,스마트폰', '개인 및 개인사업자', '월 납입한도 30만원 이하', NULL, '2026-07-14 19:59:09.153666+09', '2026-07-24 00:20:46.350569+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (30, 1, 'SAVING', 'FSS:SAVING:020000:0010019:TD11330031000', 'VIP플러스적금', '영업점,인터넷,스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 고시금리의 1/2 
*만기후 1개월 초과: 0.1%

▶ 최고우대금리 0.50%p  
①이 예금가입시 VIP고객이거나 가입일 이후부터 만기일전일까지 VIP고객에 선정된 경험이 있는 경우 : 0.3%p 
②이 예금 가입일에 정기예금 500만원이상(만기 1년이상) 가입하고 만기일 전일까지 유지한 경우 : 0.2%p

실명의 개인

1. 가입기간 : 1년제
2. 가입금액 : 월10만원 이상 5백만원 이하 (1인1계좌)', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: VIP고객 선정 경험, 정기예금 500만원 이상 가입 및 유지
가입대상: 실명의 개인
유의사항: 만기 후 이자율 적용, 월 납입액 및 가입금액 제한', '영업점,인터넷,스마트폰', '실명의 개인', '1. 가입기간 : 1년제
2. 가입금액 : 월10만원 이상 5백만원 이하 (1인1계좌)', NULL, '2026-07-14 19:59:09.088376+09', '2026-07-24 00:20:46.29132+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (72, 1, 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0006', '카카오뱅크 한달적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

매일/보너스 우대금리 제공 : 최고 연 5.50%p
- 제공조건
① 매일 우대금리 : 매 입금 시 마다 연 0.10%p 제공(최대 연 3.10%p)
② 보너스 우대금리 : 누적하여 5/10/15/20/25/31회 입금 시 해당 우대금리 제공(최대 연 2.40%p)
- 유의사항 : 만기 해지하는 경우에만 제공

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 납입금액 : 1회 100원 이상 3만원 이하(원단위)
3. 가입기간 : 31일
4. 직접 납입을 통해서 1일 1회만 입금 가능하며, 그 외의 입금은 모두 제한됨', '가입방법: 스마트폰
납입금액: 1회 100원 이상 3만원 이하
가입기간: 31일
직접 납입을 통해서 1일 1회만 입금 가능
만기 해지 시 우대금리 제공', '스마트폰', '만 14세 이상의 실명의 개인', '1. 가입방법 : 스마트폰
2. 납입금액 : 1회 100원 이상 3만원 이하(원단위)
3. 가입기간 : 31일
4. 직접 납입을 통해서 1일 1회만 입금 가능하며, 그 외의 입금은 모두 제한됨', NULL, '2026-07-14 20:00:14.605101+09', '2026-07-24 00:22:38.248479+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (34, 1, 'SAVING', 'FSS:SAVING:020000:0010022:10-01-30-031-0018-0000', 'JB 다이렉트적금(자유적립식)', '인터넷,스마트폰

√만기후 1개월이내 경과분 : 만기일 현재 계약기간별 정기적금(자유적립식) 실행이율의 1/2
√만기후 1개월초과 경과분 : 연 0.1%

추가우대금리 :
당행 계좌간 자동이체를 통해 이 예금으로 자동이체 된 금액에 0.1% 금리 우대

실명의 개인 및 개인사업자(임의단체 제외)

1. 초회불입금 1만원이상, 1인당 월별 최고 1천만원이내
2. 만기직전 1개월간 적립합계는 이전기간 적립금액을 초과할 수 없음
3. 인터넷뱅킹/스마트폰뱅킹 가입상품', '가입방법: 인터넷, 스마트폰
우대조건: 당행 계좌간 자동이체 시 0.1% 우대
가입대상: 실명의 개인 및 개인사업자
유의사항: 초회불입금 1만원 이상, 월별 최고 1천만원 이내 납입 가능. 만기직전 1개월간 적립합계는 이전 기간 적립금액 초과 불가.', '인터넷,스마트폰', '실명의 개인 및 개인사업자(임의단체 제외)', '1. 초회불입금 1만원이상, 1인당 월별 최고 1천만원이내
2. 만기직전 1개월간 적립합계는 이전기간 적립금액을 초과할 수 없음
3. 인터넷뱅킹/스마트폰뱅킹 가입상품', NULL, '2026-07-14 19:59:09.219762+09', '2026-07-24 00:20:46.415286+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (73, 1, 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0007', '카카오뱅크 우리아이적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

자동이체시 우대금리 제공 : 연 4.00%p
 - 제공조건 : 전체 계약월수의 1/2이상을 자동이체로 납입하고 만기 해지하는 경우
 - 유의사항 : 만기 자동연장된 원리금은 우대금리를 제공하지 않음

우리아이통장을 보유한 만 0세 이상 만 17세 미만의 실명의 개인

1. 가입방법: 스마트폰 
2. 가입기간 : 12개월 
3. 월 적립한도: 1천원 이상 월 20만원 이하 원단위(단, 자동연장된 원리금은 월 적립한도에 포함되지 않음)
4. 우리아이서비스를 통해 법정대리인으로 확인된 부 또는 모가 본인을 대리하여 가입 가능', '스마트폰으로 가입 가능하며, 12개월 만기 적금입니다. 월 1천원 이상 20만원 이하로 납입 가능합니다. 법정대리인(부 또는 모)이 대리 가입할 수 있습니다.', '스마트폰', '우리아이통장을 보유한 만 0세 이상 만 17세 미만의 실명의 개인', '1. 가입방법: 스마트폰 
2. 가입기간 : 12개월 
3. 월 적립한도: 1천원 이상 월 20만원 이하 원단위(단, 자동연장된 원리금은 월 적립한도에 포함되지 않음)
4. 우리아이서비스를 통해 법정대리인으로 확인된 부 또는 모가 본인을 대리하여 가입 가능', NULL, '2026-07-14 20:00:14.632377+09', '2026-07-24 00:22:38.258965+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (76, 1, 'SAVING', 'FSS:SAVING:020000:0017801:1001303001004', '토스뱅크 자유 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 적금 가입 시 설정되는 월 단위 자동이체를 모두 성공하는 경우 : 연 0.50% 제공

· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인

· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원 이상 300만원 이하
· 우대금리는 만기 해지하는 경우에만 제공됨', '스마트폰으로 가입 가능하며, 토스뱅크 통장 또는 서브 통장을 보유한 실명의 개인이 가입 대상입니다. 1인 1계좌만 가능하며, 가입금액은 0원 이상 300만원 이하입니다. 우대금리는 만기 해지 시 제공됩니다.', '스마트폰', '· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인', '· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원 이상 300만원 이하
· 우대금리는 만기 해지하는 경우에만 제공됨', NULL, '2026-07-14 20:00:14.727167+09', '2026-07-24 00:22:38.295247+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (35, 1, 'SAVING', 'FSS:SAVING:020000:0010022:10-01-30-031-0036', 'JB 다이렉트적금(정액적립식)', '인터넷,스마트폰

√만기후 1개월이내 경과분 : 만기일 현재 계약기간별 정기적금(정액적립식) 실행이율의 1/2
√만기후 1개월초과 경과분 : 연 0.1%

추가우대금리 :
당행 계좌간 자동이체를 통해 6회이상 입금한 경우 
연 0.1% 금리우대

실명의 개인 및 개인사업자(임의단체 제외)

1. 초회불입금 1만원이상, 1인당 월별 최고 5백만원이내
2. 인터넷뱅킹/스마트폰뱅킹 가입상품', '가입방법: 인터넷, 스마트폰
우대조건: 당행 계좌간 자동이체를 통해 6회이상 입금 시 연 0.1% 우대
가입대상: 실명의 개인 및 개인사업자
유의사항: 만기후 1개월이내 경과분은 만기이율의 1/2, 1개월 초과분은 연 0.1% 적용', '인터넷,스마트폰', '실명의 개인 및 개인사업자(임의단체 제외)', '1. 초회불입금 1만원이상, 1인당 월별 최고 5백만원이내
2. 인터넷뱅킹/스마트폰뱅킹 가입상품', NULL, '2026-07-14 19:59:09.246939+09', '2026-07-24 00:20:46.436363+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (106, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0013175:10-003-1384-0001', 'NH올원e예금', '인터넷,스마트폰

만기 후 3개월 : 기본금리의 50%
만기 후 6개월 : 기본금리의 20%
만기 후  6개월 초과 : 기본금리의 10%

* 기본금리 : 만기시점의 큰만족실세예금 계약기간별 금리

없음

개인

1. 10만원 이상 10억원 이내 가입', '가입방법: 인터넷, 스마트폰
유의사항: 10만원 이상 10억원 이내 가입', '인터넷,스마트폰', '개인', '1. 10만원 이상 10억원 이내 가입', NULL, '2026-07-14 20:01:32.780889+09', '2026-07-14 23:22:45.88628+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (110, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0014807:10120114300011', 'Sh해양플라스틱Zero!예금 (만기일시지급식)', '영업점,인터넷,스마트폰

* 만기후 
-1개월 이내: 만기당시 일반정기예금(월이자지급식) 계약기간별 기본금리 1/2
-1개월초과~3개월 이내: 만기당시 일반정기예금(월이자지급식) 기본금리의 1/4
- 3개월 초과: 만기당시 보통예금 기본금리

* 최대우대금리:0.35%
1. 해양플라스틱감축서약 : 0.1% (신규시) 
2. 봉사활동 또는 상품홍보 : 0.15% (만기시) 
3. 입출금통장 최초신규 : 0.1% (만기시)
4. 자동이체 출금실적 : 0.1% (만기시)
 - 수협신용카드 / 당행 펀드 또는 적금 / 수협체크카드

실명의 개인

- 1인 다계좌 가능 
  단,  합산금액 5억원 이내 
- 최저 100만원 이상', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 해양플라스틱감축서약, 봉사활동 또는 상품홍보, 입출금통장 최초신규, 자동이체 출금실적
가입대상: 실명의 개인
유의사항: 1인 다계좌 가능 (합산금액 5억원 이내), 최저 100만원 이상', '영업점,인터넷,스마트폰', '실명의 개인', '- 1인 다계좌 가능 
  단,  합산금액 5억원 이내 
- 최저 100만원 이상', NULL, '2026-07-14 20:01:41.558822+09', '2026-07-21 16:39:12.330801+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (48, 1, 'SAVING', 'FSS:SAVING:020000:0010927:010200100051', 'KB국민프리미엄적금(정액)', '영업점,인터넷,스마트폰,전화(텔레뱅킹)

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

① 단체가입/나라사랑/쿠폰 우대이율: 
    1년: 연 0.6%p, 2년: 연 0.7%p,
    3년: 연 0.9%p, 5년: 연 1.0%p 
   (중복적용되지 않음, 계약기간별차등적용)
② 교차거래 우대이율: 연 0.3%p

실명의 개인

1인 1계좌', '영업점, 인터넷, 스마트폰, 전화(텔레뱅킹)를 통해 가입 가능합니다. 우대 조건으로는 단체가입/나라사랑/쿠폰 우대이율(계약기간별 차등 적용) 및 교차거래 우대이율이 있습니다. 유의사항으로 1인 1계좌만 가입 가능합니다.', '영업점,인터넷,스마트폰,전화(텔레뱅킹)', '실명의 개인', '1인 1계좌', NULL, '2026-07-14 19:59:26.154838+09', '2026-07-24 00:21:06.436056+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (37, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21001116', 'BNK더조은자유적금', '인터넷,스마트폰

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①오픈뱅킹서비스 가입 0.10%(만기까지 유지)
②당행 입출금통장에서 자동이체시 입금건별 0.10%
③이 상품 신규시 금리우대쿠폰을 등록할 경우 0.30%
④마케팅동의 0.20%

거래대상자는 제한을 두지 아니한다. 다만, 국가 및 지방자치단체는 이 예금을 거래할 수 없다.

1. 계약기간은 6개월 이상 2년 이내 월단위로 한다.
2. 초입금 1만원 이상 월별 500만원 이내에서 자유롭게 저축
3. 최대 저축횟수는 999회 이내', '가입방법: 인터넷, 스마트폰
우대조건: 오픈뱅킹서비스 가입, 당행 입출금통장에서 자동이체, 금리우대쿠폰 등록, 마케팅동의
가입대상: 제한 없음 (국가 및 지방자치단체 제외)
유의사항: 만기 후 1개월 이내/초과 시 이율 차등 적용', '인터넷,스마트폰', '거래대상자는 제한을 두지 아니한다. 다만, 국가 및 지방자치단체는 이 예금을 거래할 수 없다.', '1. 계약기간은 6개월 이상 2년 이내 월단위로 한다.
2. 초입금 1만원 이상 월별 500만원 이내에서 자유롭게 저축
3. 최대 저축횟수는 999회 이내', NULL, '2026-07-14 19:59:09.306048+09', '2026-07-24 00:20:46.516869+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (40, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21001259', '오늘도세이브적금', '인터넷,스마트폰,기타

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①마케팅동의 0.15%
②목돈마련 축하이율 0.30~1.00%
③친구 추천번호 0.30~0.60%

실명의 개인 및 개인사업자

1. 계약기간은 1개월 이상 6개월 이내 월단위로 한다.
2. 초입금 일 1천원 이상 10만원 이하 자유롭게 저축
3. 최대 저축횟수는 999회 이내', '가입방법: 인터넷, 스마트폰, 기타
우대조건: 마케팅 동의 시 0.15%, 목돈 마련 축하 시 0.30~1.00%, 친구 추천 시 0.30~0.60%
가입대상: 실명의 개인 및 개인사업자
유의사항: 계약기간 1~6개월 월단위, 초입금 1천원 이상 10만원 이하, 최대 저축 999회 이내', '인터넷,스마트폰,기타', '실명의 개인 및 개인사업자', '1. 계약기간은 1개월 이상 6개월 이내 월단위로 한다.
2. 초입금 일 1천원 이상 10만원 이하 자유롭게 저축
3. 최대 저축횟수는 999회 이내', NULL, '2026-07-14 19:59:25.853986+09', '2026-07-24 00:21:06.275852+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (38, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21001199', 'BNK 위더스자유적금', '영업점,인터넷,스마트폰,기타

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①ESG 실천 우대금리 1.00%
②신규고객 우대금리 1.00%
- 당행 1년 이내예적금(청약포함)신규해지 이력미보유
③마케팅동의우대금리 0.50%

실명의 개인 및 개인사업자

1.계약기간은 1개월 이상 36개월 이하 월단위로 한다.
2..1인 1계좌로 가입가능
2.매월 최소 1만원 이상, 최고 월 100만원 이하 (천원 단위)', '가입방법: 영업점, 인터넷, 스마트폰, 기타
우대조건: ESG 실천, 신규고객(당행 1년 이내 예적금 신규해지 이력 미보유), 마케팅 동의
가입대상: 실명의 개인 및 개인사업자
유의사항: 1인 1계좌 가입 가능', '영업점,인터넷,스마트폰,기타', '실명의 개인 및 개인사업자', '1.계약기간은 1개월 이상 36개월 이하 월단위로 한다.
2..1인 1계좌로 가입가능
2.매월 최소 1만원 이상, 최고 월 100만원 이하 (천원 단위)', NULL, '2026-07-14 19:59:09.339104+09', '2026-07-14 19:59:09.3875+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (114, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0017801:1001202000002', '토스뱅크 먼저 이자 받는 정기예금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

우대조건 없음

토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 만 17세 이상 실명의 개인

· 계약기간 : 3개월, 6개월, 12개월 
· 가입금액 : 최소 1백만원 최대 10억원', '가입방법: 스마트폰
유의사항: 만기 후 1개월 이내 기본금리 50%, 1개월 초과 3개월 이내 기본금리 20%, 3개월 초과 연 0.10% 지급', '스마트폰', '토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 만 17세 이상 실명의 개인', '· 계약기간 : 3개월, 6개월, 12개월 
· 가입금액 : 최소 1백만원 최대 10억원', NULL, '2026-07-14 20:01:41.726031+09', '2026-07-24 00:24:44.699804+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (112, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0014807:10120116100011', 'Sh첫만남우대예금', '인터넷,스마트폰

* 만기후 
-1개월 이내: 만기당시 일반정기예금(월이자지급식) 계약기간별 기본금리 1/2
-1개월초과~3개월 이내: 만기당시 일반정기예금(월이자지급식) 기본금리의 1/4
- 3개월 초과: 만기당시 보통예금 기본금리

* 최대우대금리:1.05%
1. 첫거래우대 : 1.0% (신규시) 
  - 최근 1년간 수협은행 예적금 활동계좌 미보유 고객포함
2. 마케팅전체동의 : 0.05%(신규시) 
3. 스마트폰뱅킹의 상품알리기 : 0.80%(만기시)

실명의 개인

-1인 1계좌
-최저 100만원 이상', '가입방법: 인터넷, 스마트폰
우대조건: 첫거래 우대, 마케팅 전체 동의, 스마트폰뱅킹 상품 알림
가입대상: 실명의 개인
유의사항: 1인 1계좌, 최저 100만원 이상', '인터넷,스마트폰', '실명의 개인', '-1인 1계좌
-최저 100만원 이상', NULL, '2026-07-14 20:01:41.635023+09', '2026-07-24 00:24:44.662356+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (53, 1, 'SAVING', 'FSS:SAVING:020000:0013175:10-047-1360-0002', 'NH올원e 미니적금', '스마트폰

만기후 1년 이내 : 만기시점 계약기간별 자유로우대적금 기본금리의 1/2
만기후 1년 초과 : 보통예금 금리

1. 자동이체 입금횟수 우대금리 : 최고 0.5%p
 - 5회이상 : 0.2%p, 10회이상 : 0.3%p, 15회이상 0.5%p
2. 목표금액 달성 축하금리  : 0.5%p
3. MZ세대(만 19~34세)) 우대금리 : 0.5%p
4. 직전 1년간 당행 예적금(청약포함) 미보유 고객 : 0.2%p

개인

가입기간 1개월 이상 6개월 이하(일 단위)

초입금 및 매일 1천원이상  5만원이내(천원 단위)', '스마트폰으로 가입 가능하며, 가입기간은 1개월 이상 6개월 이하입니다. 초입금 및 매일 1천원 이상 5만원 이내로 납입 가능합니다.', '스마트폰', '개인', '가입기간 1개월 이상 6개월 이하(일 단위)

초입금 및 매일 1천원이상  5만원이내(천원 단위)', NULL, '2026-07-14 19:59:43.465459+09', '2026-07-22 01:38:08.592675+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (23, 1, 'SAVING', 'FSS:SAVING:020000:0010017:01020400490002', '펫 적금', '영업점,스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

*우대이율 6개월제 최대 0.55%, 12개월제 최대 0.90%

실명의 개인고객(1인 1계좌)

1. 가입한도: 월 1만원 이상 50만원 이하 원단위
2. 정기적립식', '가입방법: 영업점, 스마트폰
우대조건: 만기후 1년이내 기본이율 x 50%, 만기후 1년초과 기본이율 x 20%
가입대상: 실명의 개인고객 (1인 1계좌)
유의사항: 월 1만원 이상 50만원 이하 원단위', '영업점,스마트폰', '실명의 개인고객(1인 1계좌)', '1. 가입한도: 월 1만원 이상 50만원 이하 원단위
2. 정기적립식', NULL, '2026-07-14 19:58:52.735904+09', '2026-07-24 00:20:26.96718+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (25, 1, 'SAVING', 'FSS:SAVING:020000:0010017:01020400530001', 'BNK내맘대로 적금', '영업점,인터넷,스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

*우대이율 최대 0.2%
- 신규시 우대이율 0.05% 및 해지시 우대이율 최대 0.15%

제한없음

1. 가입금액 :  1천원 이상
2. 가입기간 :  6개월 이상 60개월 이하(일단위)
3. 자유적립식', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 신규 시 우대이율 0.05%, 해지 시 우대이율 최대 0.15%
가입대상: 제한없음
유의사항: 만기 후 1년 이내에는 기본이율의 50%, 1년 초과 시에는 20% 적용', '영업점,인터넷,스마트폰', '제한없음', '1. 가입금액 :  1천원 이상
2. 가입기간 :  6개월 이상 60개월 이하(일단위)
3. 자유적립식', NULL, '2026-07-14 19:58:52.814083+09', '2026-07-24 00:20:27.037692+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (27, 1, 'SAVING', 'FSS:SAVING:020000:0010017:01020400700001', 'Only One 주거래 우대적금', '영업점,스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

* 우대이율 최대 2.5%
- 공통 우대이율 최대 2%
- 가입자격별 우대이율 최대 0.5%

만14세 이상 실명의 개인(1인 1계좌)

1.가입금(적립)금액 : 월 1천원 이상 50만원 이하
2. 가입기간 : 12개월', NULL, '영업점,스마트폰', '만14세 이상 실명의 개인(1인 1계좌)', '1.가입금(적립)금액 : 월 1천원 이상 50만원 이하
2. 가입기간 : 12개월', NULL, '2026-07-14 19:58:52.913907+09', '2026-07-24 00:20:27.087357+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (28, 1, 'SAVING', 'FSS:SAVING:020000:0010019:TD11330029000', '해피라이프_여행스케치적금V', '영업점,스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 고시금리의 1/2 
*만기후 1개월 초과: 0.1%

▶ 최고우대금리 1.20%p  
①해피라이프_여행스케치외화적금V 동일자가입 0.5%p 
②해지원금 기준 500만원이상 : 최고0.2%p
③신용(체크)카드사용실적300만원이상:최고 0.3%p
④개인(신용)정보 동의: 0.2%p

제한없음(단,국가및지방자치단체제외)

1. 가입기간 : 6개월이상 3년제
2. 가입금액 : 월5만원 이상 5백만원 이하 (1인1계좌)', '가입방법: 영업점, 스마트폰
우대조건: 해피라이프_여행스케치외화적금V 동일자가입, 해지원금 기준 500만원이상, 신용(체크)카드사용실적 300만원이상, 개인(신용)정보 동의
가입대상: 제한없음 (단, 국가 및 지방자치단체 제외)
유의사항: 만기후 1개월 이내: 만기일 당시 최초 가입 기간별 고시금리의 1/2, 만기후 1개월 초과: 0.1%', '영업점,스마트폰', '제한없음(단,국가및지방자치단체제외)', '1. 가입기간 : 6개월이상 3년제
2. 가입금액 : 월5만원 이상 5백만원 이하 (1인1계좌)', NULL, '2026-07-14 19:58:52.935396+09', '2026-07-24 00:20:27.152341+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (59, 1, 'SAVING', 'FSS:SAVING:020000:0013909:53', '내맘적금', '영업점,인터넷,스마트폰,전화(텔레뱅킹)

1개월 이내 : 지급당시 해당기간별 일반정기적금 기본금리 1/2
1개월 초과 : 지급당시 해당기간별 일반정기적금 기본금리 1/4

하나은행 통장에서 계약기간의 1/2이상 월부금 자동이체실적 충족 시 연 0.50%

실명의 개인
또는 개인사업자(1인 다계좌 가능)

1. 1인 다계좌 가능
2. 가입금액
   - 1천원 이상~1,000만원 이하(원단위)
3. 적립한도
    - 자유적립식 : 매월 1원 이상~1,000만원 이하(원단위)
    - 정액적립식 : 매월 1천원 이상~1,000만원 이하(원단위)', '가입방법: 영업점, 인터넷, 스마트폰, 전화(텔레뱅킹)
우대조건: 하나은행 통장에서 계약기간의 1/2이상 월부금 자동이체실적 충족 시 연 0.50%
가입대상: 실명의 개인 또는 개인사업자
유의사항: 1인 다계좌 가능, 가입금액 1천원 이상~1,000만원 이하, 자유적립식 월 1원 이상~1,000만원 이하, 정액적립식 월 1천원 이상~1,000만원 이하', '영업점,인터넷,스마트폰,전화(텔레뱅킹)', '실명의 개인
또는 개인사업자(1인 다계좌 가능)', '1. 1인 다계좌 가능
2. 가입금액
   - 1천원 이상~1,000만원 이하(원단위)
3. 적립한도
    - 자유적립식 : 매월 1원 이상~1,000만원 이하(원단위)
    - 정액적립식 : 매월 1천원 이상~1,000만원 이하(원단위)', NULL, '2026-07-14 19:59:59.510744+09', '2026-07-22 01:38:25.985669+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (85, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300031000', '스마트모아Dream정기예금', '인터넷,스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 1년미만, 1천만원이상 0.10%p
▶ 1년이상, 1천만원이상 0.20%p

개인 및 개인사업자

1. 가입기간 : 1개월이상 3년제
2. 최소가입금액 : 100만원이상', '가입방법: 인터넷, 스마트폰
우대조건: 1년 미만, 1천만원 이상 가입 시 0.10%p, 1년 이상, 1천만원 이상 가입 시 0.20%p
가입대상: 개인 및 개인사업자
유의사항: 만기 후 1개월 이내에는 만기 당시 일반정기예금 금리의 1/2, 1개월 초과 시 0.01%의 이율이 적용됩니다.', '인터넷,스마트폰', '개인 및 개인사업자', '1. 가입기간 : 1개월이상 3년제
2. 최소가입금액 : 100만원이상', NULL, '2026-07-14 20:01:02.468698+09', '2026-07-24 00:22:58.10923+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (78, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010001:WR0001B', 'WON플러스예금', '인터넷,스마트폰,전화(텔레뱅킹)

만기 후
- 1개월이내 : 만기시점약정이율×50%
- 1개월초과 6개월이내: 만기시점약정이율×30%
- 6개월초과 : 만기시점약정이율×20%

※ 만기시점 약정이율 : 일반정기예금 금리

해당사항 없음

실명의 개인

- 가입기간: 1~36개월
- 최소가입금액: 1만원 이상
- 만기일을 일,월 단위로 자유롭게 선택 가능
- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용', '가입방법: 인터넷, 스마트폰, 전화(텔레뱅킹)
유의사항: 만기 후 이율은 만기 시점 약정 이율의 20~50% 적용. 만기일을 일, 월 단위로 자유롭게 선택 가능. 최소 가입금액 1만원 이상.', '인터넷,스마트폰,전화(텔레뱅킹)', '실명의 개인', '- 가입기간: 1~36개월
- 최소가입금액: 1만원 이상
- 만기일을 일,월 단위로 자유롭게 선택 가능
- 만기해지 시 신규일 당시 영업점과 인터넷 홈페이지에 고시된 계약기간별 금리 적용', NULL, '2026-07-14 20:00:14.829455+09', '2026-07-24 00:22:38.338251+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (62, 1, 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000005', '데굴데굴 농장', '스마트폰

만기 후
- 1개월 이내: 만기시점 기본금리 X 50%
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

금리우대 코드를 입력하는 경우 우대금리 적용

만 17세 이상 실명의 개인 및 개인사업자

가입금액: 1만원 이상 1천만원 이하
가입기간: 6개월 , 1년
(1인 최대1계좌)', '가입방법: 스마트폰
우대조건: 금리우대 코드를 입력하는 경우 우대금리 적용
가입대상: 만 17세 이상 실명의 개인 및 개인사업자
유의사항: 만기 후 이자율 적용 조건 있음', '스마트폰', '만 17세 이상 실명의 개인 및 개인사업자', '가입금액: 1만원 이상 1천만원 이하
가입기간: 6개월 , 1년
(1인 최대1계좌)', NULL, '2026-07-14 19:59:59.645652+09', '2026-07-22 01:38:26.139891+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (36, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21000111', '행복 DREAM 적금', '영업점,인터넷,스마트폰

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①신규일로부터 6개월전까지 당행 적금 미보유 0.2%
②월부금이 50만원 이상인 경우 0.2%
③이 예금을 자동이체로 납입하는 경우 0.2%
④신규일로부터 익월 말일까지 신용(체크)카드를 최초로 발급하고 동 기간내에 10만원이상 이용실적이 있는경우 0.2%
⑤이 예금 가입전 경남은행 마케팅동의가 되어있는경우 0.2%

실명의 개인 및 개인사업자

1. 계약기간은 1년 이상 3년 이하 월단위로 한다.
2. 적립금액은 5만원 이상, 최고금액은 제한없음', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 신규일로부터 6개월 전까지 당행 적금 미보유, 월부금 50만원 이상, 자동이체 납입, 신용(체크)카드 최초 발급 및 10만원 이상 이용, 경남은행 마케팅 동의
가입대상: 실명의 개인 및 개인사업자
유의사항: 계약기간 1년 이상 3년 이하 월단위, 적립금액 5만원 이상, 최고금액 제한 없음', '영업점,인터넷,스마트폰', '실명의 개인 및 개인사업자', '1. 계약기간은 1년 이상 3년 이하 월단위로 한다.
2. 적립금액은 5만원 이상, 최고금액은 제한없음', NULL, '2026-07-14 19:59:09.270495+09', '2026-07-24 00:20:46.482044+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (43, 1, 'SAVING', 'FSS:SAVING:020000:0010026:01211210121', 'IBK탄소제로적금(자유적립식)', '스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 2.00%p
1. 에너지 절감 우대금리 : 최대 연 1.00%p
2. 최초거래고객 우대금리 : 연 0.50%p
3. 지로 또는 공과금 자동이체 우대금리 : 연 0.50%p

실명의 개인
(개인사업자 제외)

1인당 1계좌 가입 가능하며, 계좌당 최소 1만원 이상 1백만원까지 납입 가능', '가입방법: 스마트폰
우대조건: 에너지 절감, 최초거래, 지로/공과금 자동이체
가입대상: 실명의 개인 (개인사업자 제외)
유의사항: 만기후금리 적용, 1인 1계좌, 계좌당 최소 1만원 이상 1백만원까지 납입 가능', '스마트폰', '실명의 개인
(개인사업자 제외)', '1인당 1계좌 가입 가능하며, 계좌당 최소 1만원 이상 1백만원까지 납입 가능', NULL, '2026-07-14 19:59:26.010967+09', '2026-07-24 00:21:06.326475+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (87, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300036000', 'The플러스예금', '영업점,스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 해당사항없음

개인 및 법인(단,국가 지자체 및 금융기관 제외)

1. 가입기간 : 3개월,6개월,1년제
2. 가입금액 : 10백만원이상 고객당 10억원한도', '가입방법: 영업점, 스마트폰
유의사항: 만기후 1개월 이내에는 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2, 만기후 1개월 초과 시에는 0.01%의 금리가 적용됩니다.', '영업점,스마트폰', '개인 및 법인(단,국가 지자체 및 금융기관 제외)', '1. 가입기간 : 3개월,6개월,1년제
2. 가입금액 : 10백만원이상 고객당 10억원한도', NULL, '2026-07-14 20:01:02.562475+09', '2026-07-24 00:22:58.143178+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (67, 1, 'SAVING', 'FSS:SAVING:020000:0014807:10141109800021', 'Sh월복리자유적금', '영업점,인터넷,스마트폰

* 만기후 1년 이내
 - 만기당시 상호부금
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

*최대우대금리:0.7%
-첫거래고객:0.3%
-카드거래:최대0.3%
-복수거래:0.1%
-요구불거래:최대0.2%
-인터넷뱅킹고객:0.1%
-자동이체실적:0.1%

실명의 개인 및 개인사업자

- 1인 1계좌
- 월 가입한도 : 100만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 첫거래고객, 카드거래, 복수거래, 요구불거래, 인터넷뱅킹고객, 자동이체실적
가입대상: 실명의 개인 및 개인사업자
유의사항: 1인 1계좌, 월 가입한도 100만원', '영업점,인터넷,스마트폰', '실명의 개인 및 개인사업자', '- 1인 1계좌
- 월 가입한도 : 100만원', NULL, '2026-07-14 19:59:59.809674+09', '2026-07-22 01:38:26.355033+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (66, 1, 'SAVING', 'FSS:SAVING:020000:0014807:10140114700041', '헤이(Hey)적금 (정액적립식)', '스마트폰

* 만기 후 1년이내
 - 만기당시 일반정기적금 
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

*최대우대금리  :0.9%
1. 마케팅동의 : 0.1% (신규시)
2. 자동이체 납입 : 0.8% (만기시)

실명의 개인

- 1인 1계좌 
- 월 가입한도 : 100만원', '가입방법: 스마트폰
우대조건: 마케팅 동의 시 0.1%, 자동이체 납입 시 0.8%
가입대상: 실명의 개인
유의사항: 1인 1계좌, 월 가입한도 100만원', '스마트폰', '실명의 개인', '- 1인 1계좌 
- 월 가입한도 : 100만원', NULL, '2026-07-14 19:59:59.779049+09', '2026-07-21 16:37:55.433319+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (69, 1, 'SAVING', 'FSS:SAVING:020000:0014807:10141114700041', '헤이(Hey)적금 (자유적립식)', '스마트폰

*만기 후 1년이내
 - 만기당시 상호부금 
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

*최대우대금리 : 0.9%
1. 마케팅동의 : 0.1% (신규시)
2. 자동이체 납입 : 0.8% (만기시)

실명의개인

- 1인 1계좌 
- 월 가입한도 : 100만원', '가입방법: 스마트폰
우대조건: 마케팅 동의 시 0.1%, 자동이체 납입 시 0.8%
가입대상: 실명의 개인
유의사항: 1인 1계좌, 월 가입한도 100만원', '스마트폰', '실명의개인', '- 1인 1계좌 
- 월 가입한도 : 100만원', NULL, '2026-07-14 20:00:14.472386+09', '2026-07-21 16:38:12.71+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (52, 1, 'SAVING', 'FSS:SAVING:020000:0011625:230-0119-85', '신한 알.쏠 적금', '영업점,스마트폰

-1개월 이하:(일반) 정기적금 기본금리 1/2
(단, 최저금리 0.10%)
-1개월 초과~6개월 이하: (일반) 정기적금 기본금리의 1/4
(단, 최저금리 0.10%)
-6개월 초과 0.10%

※가산금리 최고 연 1.30%
- 소득이체 : 연 0.6%
- 카드이용 : 연 0.3%
- 오픈뱅킹 : 연 0.6%
- 청약보유 : 연 0.3%
- 마케팅동의 : 연 0.1%
※ 우대금리 항목별 자세한 적용 조건은 상품설명서 참조

제한없음

1. 가입한도: 
월300만원 이하
2. 1인 다계좌', '영업점, 스마트폰으로 가입 가능합니다. 월 300만원 이하로 가입 가능하며, 1인 다계좌 개설이 가능합니다.', '영업점,스마트폰', '제한없음', '1. 가입한도: 
월300만원 이하
2. 1인 다계좌', NULL, '2026-07-14 19:59:43.404814+09', '2026-07-24 00:21:53.38254+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (86, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300035000', '굿스타트예금', '스마트폰

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 최고우대금리 0.5% 
 ① 첫예금거래 : 0.4% -최근1년동안 정기예금 계좌 신규 또는 해지이력이 없는경우
 ② 개인(신용)정보 수집이용동의 : 0.1% -만기일전일까지 유지시

개인 및 개인사업자

1. 가입기간 : 1년제
2. 가입금액 : 1백만원이상 최고 1억원(1인1계좌)', '가입방법: 스마트폰
우대조건: 첫예금거래, 개인(신용)정보 수집이용동의
가입대상: 개인 및 개인사업자
유의사항: 만기후 1개월 이내/초과 시 이율 상이', '스마트폰', '개인 및 개인사업자', '1. 가입기간 : 1년제
2. 가입금액 : 1백만원이상 최고 1억원(1인1계좌)', NULL, '2026-07-14 20:01:02.539265+09', '2026-07-24 00:22:58.13062+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (88, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010020:101272000006', '제주Dream 정기예금 (개인/만기 지급식)', '영업점,인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기예금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기예금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

최고 연 0.1%p(항목별 0.1%p)
①급여이체
②적립식예금 잔액 10만원 이상 보유
③탑스, 주거래 고객
④결제계좌(가맹점) 전월 입금액 10만원 이상 
⑤비과세종합저축 대상 고객
⑥다자녀(3인이상 자녀)가정
⑦탐나는 J연금통장 가입고객
⑧국민연금안심통장 가입고객
⑨공무원연금안심통장 가입고객

제한없음

가입금액 : 1백만원 이상', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 최고 연 0.1%p (항목별 0.1%p)
가입대상: 제한없음
유의사항: 만기후 이자율은 기본이자의 50%~25% 적용되며, 최저 0.1% 적용. 만기후 3개월 초과 시 0.1% 적용.', '영업점,인터넷,스마트폰', '제한없음', '가입금액 : 1백만원 이상', NULL, '2026-07-14 20:01:02.596594+09', '2026-07-24 00:22:58.172997+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (74, 1, 'SAVING', 'FSS:SAVING:020000:0017801:1001303001001', '토스뱅크 키워봐요 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 적금 가입 시 설정되는 주 단위 자동이체를 통하여 25회 이상 적립한 경우 : 연 2.00% 제공

· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인

· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원
· 우대금리는 우대조건을 달성하고 만기 해지하는 경우에만 제공됨', '가입방법: 스마트폰
우대조건: 주 단위 자동이체를 통해 25회 이상 적립 시 연 2.00% 제공
가입대상: 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인
유의사항: 1인 1계좌, 가입금액 0원, 우대금리는 만기 해지 시 제공', '스마트폰', '· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인', '· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원
· 우대금리는 우대조건을 달성하고 만기 해지하는 경우에만 제공됨', NULL, '2026-07-14 20:00:14.663266+09', '2026-07-14 20:00:14.690051+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (89, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010020:101272000057', 'J정기예금 (만기지급식)', '영업점,인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기예금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기예금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

- 아래의 우대요건 충족시 최고 0.5%p 추가 우대 
①비대면 채널 가입시 0.3%제공(신규시제공)  
(단, 이벤트시 디지털 채널에 고시한 우대금리를 추가 적용할 수 있음)
②신규일로부터 만기달 제외한 계약기간의 1/2이상 매월 Jbank로그인 시 0.2%제공(만기시제공)

실명의 
개인 및 
개인사업자

가입금액 : 30만원 이상', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 비대면 채널 가입 시 0.3%p, 만기달 제외 계약기간의 1/2 이상 매월 Jbank 로그인 시 0.2%p
가입대상: 실명의 개인 및 개인사업자
유의사항: 가입금액 30만원 이상', '영업점,인터넷,스마트폰', '실명의 
개인 및 
개인사업자', '가입금액 : 30만원 이상', NULL, '2026-07-14 20:01:18.425662+09', '2026-07-24 00:23:15.846921+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (56, 1, 'SAVING', 'FSS:SAVING:020000:0013175:10-047-1387-0001', 'NH고향사랑기부적금', '영업점,인터넷,스마트폰

만기후 1년 이내 : 만기시점 계약기간별 기본금리의 1/2
만기후 1년 초과 : 보통예금 금리

1. 고향사랑기부금 납부고객 우대 : 0.5%p

2-1. 만 65세 이상 고령자 우대 : 0.1%p
2-2. 만 19~34세 MZ고객 우대 : 0.3%p

3. 고향사랑특별금리 : 0.05%p(금리시장상황에 따라 변동 가능)
 - 고향사랑특별금리는 상품 가입고객 모두에게 적용

개인

1.초입금1만원 이상 및 매회 1천원 이상, 매월50만원이내

2. 고향사랑기부금 우대금리는 농협은행/농축협 영업점 또는 고향사랑e음 홈페이지를 통한 고향사랑기부금 납부실적이 확인되는 경우 제공

3. 연간 판매액의 0.1% 공익기금 적립

※자세한 사항은 상품설명서 참조', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 고향사랑기부금 납부고객, 만 65세 이상 고령자, 만 19~34세 MZ고객
가입대상: 개인
유의사항: 만기후 금리 변동, 고향사랑특별금리 변동 가능성 있음', '영업점,인터넷,스마트폰', '개인', '1.초입금1만원 이상 및 매회 1천원 이상, 매월50만원이내

2. 고향사랑기부금 우대금리는 농협은행/농축협 영업점 또는 고향사랑e음 홈페이지를 통한 고향사랑기부금 납부실적이 확인되는 경우 제공

3. 연간 판매액의 0.1% 공익기금 적립

※자세한 사항은 상품설명서 참조', NULL, '2026-07-14 19:59:43.632149+09', '2026-07-24 00:21:53.532007+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (92, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0059-0000', 'JB 123 정기예금 (만기일시지급식)', '인터넷,스마트폰

만기후 1개월 이하 : 만기일 현재 계약기간별 정기예금 실행이율 1/2
만기후 1개월 초과 : 연 0.01%

자동재예치 우대이율
1회차 0.1%,
2회차 0.2%,
3회차 0.3%

(이벤트우대이율)
2026.5.28~12.31 까지
1)가입일직전 6개월 동안 당행 원화 정기예금 보유이력이 없는 경우 0.50%
2.개인(신용)정보 수집/이용 동의한 경우(단, 상품서비스 안내수단 전체 동의한 경우에 한함) 0.10%

실명의 개인 또는 개인사업자 (1인 다계좌 가입 가능함)

예금의 신규 : 인터넷뱅킹, 모바일뱅킹, 모바일웹, BDT
예금의 해지 : 인터넷뱅킹, 모바일뱅킹, 영업점
가입금액 최저 1백만원이상', '가입방법: 인터넷뱅킹, 모바일뱅킹, 모바일웹, BDT
우대조건: 자동재예치 우대이율(1회차 0.1%, 2회차 0.2%, 3회차 0.3%), 이벤트 우대이율(직전 6개월 당행 원화 정기예금 미보유 시 0.50%, 개인(신용)정보 수집/이용 동의 시 0.10%)
가입대상: 실명의 개인 또는 개인사업자
유의사항: 만기후 1개월 이하 이율은 만기일 현재 계약기간별 정기예금 실행이율의 1/2, 만기후 1개월 초과는 연 0.01% 적용', '인터넷,스마트폰', '실명의 개인 또는 개인사업자 (1인 다계좌 가입 가능함)', '예금의 신규 : 인터넷뱅킹, 모바일뱅킹, 모바일웹, BDT
예금의 해지 : 인터넷뱅킹, 모바일뱅킹, 영업점
가입금액 최저 1백만원이상', NULL, '2026-07-14 20:01:18.540025+09', '2026-07-24 00:23:15.900937+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (101, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010927:010300100335', 'KB Star 정기예금', '인터넷,스마트폰

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

해당무

실명의 개인 또는 개인사업자

- 가입금액 : 1백만원 이상', '가입방법: 인터넷, 스마트폰
우대조건: 해당무
가입대상: 실명의 개인 또는 개인사업자
유의사항: 가입금액 1백만원 이상', '인터넷,스마트폰', '실명의 개인 또는 개인사업자', '- 가입금액 : 1백만원 이상', NULL, '2026-07-14 20:01:32.591301+09', '2026-07-22 01:39:33.546405+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (100, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010030:06492', 'KDB 정기예금', '영업점,인터넷,스마트폰

* 만기후 1년 이내 : 만기일 현재 고시된 일반 정기예금 해당기간 기본이율의 1/2
* 만기후 1년 초과 : 만기일 현재 고시된 보통예금 이자율

해당없음

제한없음

해당없음', '가입방법: 영업점, 인터넷, 스마트폰
유의사항: 만기후 1년 이내에는 만기일 현재 고시된 일반 정기예금 해당기간 기본이율의 1/2, 만기후 1년 초과 시에는 만기일 현재 고시된 보통예금 이자율 적용.', '영업점,인터넷,스마트폰', '제한없음', '해당없음', NULL, '2026-07-14 20:01:32.551468+09', '2026-07-21 16:39:02.238158+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (90, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010020:101272000058', '스마일드림 정기예금 (개인/선이자 지급식)', '영업점,스마트폰

- 만기후 1개월 이내 : (일반)정기예금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기예금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

-아래의 우대요건 충족시 최고0.3% 추가우대(신규시제공)
①김만덕나눔적금 보유 또는 김만적 나눔적금 만기 해지고객 0.2%우대(가입시제공)
②예금가입시 탐나는전 체크카드 보유고객 0.1%우대(가입시제공)
(단, 이벤트시 영업점,디지털채널에 고시한 우대금리를 추가 적용할 수 있음)

실명의
개인 및 
개인사업자

가입금액 : 1백만원 이상', '가입방법: 영업점, 스마트폰
우대조건: 김만덕나눔적금 보유 또는 만기 해지 고객 (0.2%), 탐나는전 체크카드 보유 고객 (0.1%)
가입대상: 실명의 개인 및 개인사업자
유의사항: 가입금액 1백만원 이상', '영업점,스마트폰', '실명의
개인 및 
개인사업자', '가입금액 : 1백만원 이상', NULL, '2026-07-14 20:01:18.481476+09', '2026-07-22 01:39:17.403103+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (91, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010022:10-01-20-024-0046-0000', 'JB 다이렉트예금통장 (만기일시지급식)', '인터넷,스마트폰

만기후 1개월 이하 : 만기일 현재 계약기간별 정기예금 실행이율 1/2
만기후 1개월 초과 : 연 0.01%

우대조건
없음

실명의 개인(임의단체 제외

가입금액 1계좌당 1백만원이상
인터넷/스마트폰뱅킹 가입상품', '가입방법: 인터넷, 스마트폰
가입대상: 실명의 개인(임의단체 제외)
유의사항: 가입금액 1계좌당 1백만원 이상', '인터넷,스마트폰', '실명의 개인(임의단체 제외', '가입금액 1계좌당 1백만원이상
인터넷/스마트폰뱅킹 가입상품', NULL, '2026-07-14 20:01:18.512884+09', '2026-07-22 01:39:17.41655+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (94, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010024:21001115', 'BNK더조은정기예금', '인터넷,스마트폰

만기 후 1개월 이내: 일반정기예금 기본이율 Ⅹ50%
만기 후 1개월 초과: 일반정기예금 기본이율 Ⅹ20%

①신규시 가입(재예치)금액 20백만원 이상인 경우 0.10%
②신규시 금리우대쿠폰을 등록한 경우 0.20% 
③경남은행 오픈뱅킹 서비스를 가입한 경우 0.10%
(만기시까지 해당서비스 유지하는 경우)
④자동재예치를 통해 가입한 경우 0.05%
(금리우대쿠폰과 중복적용 불가)
⑤신규(재예치)시 마케팅동의 및 모바일메시지 수신동의 0.10%

거래대상자는 제한을 두지 아니한다. 다만, 국가 및 지방자치단체는 이 예금을 거래할 수 없다.

1. 이 예금의 계약기간은 3개월 이상 2년 이내 월단위로 한다.
2. 가입금액은 1인당 최소 100만원 이상 5억원 이하이다.', '가입방법: 인터넷, 스마트폰
우대조건: 신규시 가입(재예치)금액 20백만원 이상, 금리우대쿠폰 등록, 경남은행 오픈뱅킹 서비스 가입, 자동재예치, 마케팅동의 및 모바일메시지 수신동의
가입대상: 제한 없음 (단, 국가 및 지방자치단체 제외)
유의사항: 만기 후 1개월 이내/초과 시 이율 차등 적용, 금리우대쿠폰과 자동재예치 중복 불가', '인터넷,스마트폰', '거래대상자는 제한을 두지 아니한다. 다만, 국가 및 지방자치단체는 이 예금을 거래할 수 없다.', '1. 이 예금의 계약기간은 3개월 이상 2년 이내 월단위로 한다.
2. 가입금액은 1인당 최소 100만원 이상 5억원 이하이다.', NULL, '2026-07-14 20:01:18.608174+09', '2026-07-24 00:23:15.971571+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (103, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0011625:207-0135-12', '쏠편한 정기예금', '인터넷,스마트폰

-1개월 이하: (일반) 정기예금 기본금리 1/2
(단, 최저금리 0.10%)
-1개월 초과~6개월 이하: (일반) 정기예금 기본금리의 1/4
(단, 최저금리 0.10%)
-6개월 초과:  0.10%

해당사항없음

만14세이상 개인고객

1. 가입한도 :
 1만원 이상', '가입방법: 인터넷, 스마트폰
유의사항: 1만원 이상 가입 가능', '인터넷,스마트폰', '만14세이상 개인고객', '1. 가입한도 :
 1만원 이상', NULL, '2026-07-14 20:01:32.665885+09', '2026-07-24 00:23:35.867518+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (102, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0011625:207-0134-16', '신한My플러스 정기예금', '영업점,스마트폰

-1개월 이하: (일반) 정기예금 기본금리 1/2
(단, 최저금리 0.10%)
-1개월 초과~6개월 이하: (일반) 정기예금 기본금리의 1/4
(단, 최저금리 0.10%)
-6개월 초과:  0.10%

※가산금리 최고 연 0.2%
- 정기예금 미보유 : 연 0.1%
- 소득이체 : 연 0.1%

개인고객

1. 가입한도 :
 50만원 이상 1억원 이내', '가입방법: 영업점, 스마트폰
우대조건: 정기예금 미보유 시 연 0.1%, 소득이체 시 연 0.1%
가입대상: 개인고객
유의사항: 가입한도 50만원 이상 1억원 이내', '영업점,스마트폰', '개인고객', '1. 가입한도 :
 50만원 이상 1억원 이내', NULL, '2026-07-14 20:01:32.629629+09', '2026-07-14 20:01:32.662113+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (84, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010019:TD11300027000', '미즈월복리정기예금', '영업점,인터넷,스마트폰,기타

*만기후 1개월 이내: 만기일 당시 최초 가입 기간별 일반정기예금 고시금리의 1/2 
*만기후 1개월 초과: 0.01%

▶ 최고우대금리 0.2% 
 ① 요구불평잔 : 0.2% -300만원이상 0.1%, 500만원이상 0.2%
 ② 신용(체크)카드결제실적 : 0.1% -전월결제금 300만원이상 0.05%, 500만원이상 0.1%

만18세이상 여성으로 실명의 개인 및 개인사업자

1. 가입기간 : 1년이상 3년제
2. 가입금액 : 5백만원이상 최고 50백만원', '가입방법: 영업점, 인터넷, 스마트폰, 기타
우대조건: 요구불평잔, 신용(체크)카드 결제실적
가입대상: 만 18세 이상 여성, 실명의 개인 및 개인사업자
유의사항: 만기 후 이자율은 별도 명시됨', '영업점,인터넷,스마트폰,기타', '만18세이상 여성으로 실명의 개인 및 개인사업자', '1. 가입기간 : 1년이상 3년제
2. 가입금액 : 5백만원이상 최고 50백만원', NULL, '2026-07-14 20:01:02.421106+09', '2026-07-24 00:22:58.081781+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (115, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010016:10511008001278000', 'iM스마트예금', '인터넷,스마트폰

만기 후 1개월 미만 경과 : 약정이자율 x 50%
만기 후 3개월 미만 경과 : 약정이자율 x 25% 
만기 후 3개월 이상 경과 : 약정이자율 x 10%

* 최고우대금리 : 연0.25%p
- 가입일(재예치일)로부터 3개월 이내 아래 1가지 이상 요건 충족시
① 당행 주택청약종합저축 보유
② 당행 신용(체크)카드 결제실적 보유(결제금액 출금기준)
* 해당 상품을 인터넷/모바일뱅킹을 통해 가입 : 연0.05%p

실명의 개인

계좌당 가입 최저한도 : 100만원', NULL, '인터넷,스마트폰', '실명의 개인', '계좌당 가입 최저한도 : 100만원', NULL, '2026-07-21 15:44:30.824088+09', '2026-07-24 00:24:44.728257+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (99, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010026:01211310142', 'IBK굴리기통장(정기예금)', '영업점,스마트폰

만기일 당시 정기예금 만기후 이자율 적용 -1개월 이내: 만기일 당시 계약기간별 고시금리×50% -1월 초과 6개월 이내: 만기일 당시 계약기간별 고시금리×30% -6개월 초과: 만기일 당시 계약기간별 고시금리×20%

없음

실명의 개인
(개인사업자 제외)

계좌 수 제한 없으며, 최소 1백만원 이상 통합한도 3억원 이내 가입 가능', '가입방법: 영업점, 스마트폰
가입대상: 실명의 개인 (개인사업자 제외)
유의사항: 만기 후 이자율은 만기일 당시 고시금리의 50%~20% 적용', '영업점,스마트폰', '실명의 개인
(개인사업자 제외)', '계좌 수 제한 없으며, 최소 1백만원 이상 통합한도 3억원 이내 가입 가능', NULL, '2026-07-14 20:01:32.519719+09', '2026-07-24 00:23:35.802215+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (107, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0013175:10-003-1387-0001', 'NH고향사랑기부예금', '영업점,인터넷,스마트폰

만기 후 3개월 : 기본금리의 50%
만기 후 6개월 : 기본금리의 20%
만기 후  6개월 초과 : 기본금리의 10%

* 기본금리 : 만기시점의 큰만족실세예금 계약기간별 금리

1. 고향사랑기부금 납부고객 우대 : 0.3%p

2-1. 만65세 이상 고령자 우대 : 0.1%p
2-2. 만 19~34세 MZ고객 우대 : 0.1%p

3. 고향사랑 특별금리 : 0.05%p (금리시장상황에 따라 변동 가능)
 - 고향사랑 특별금리는 상품 가입고객 모두에게 적용

개인

1. 100만원 이상 가입
2. 고향사랑기부금 우대금리는 농협은행/농축협 영업점 또는 고향사랑e음 홈페이지를 통한 고향사랑기부금 납부실적이 확인되는 경우 제공
3. 연간 판매액의 0.1% 공익기금 적립

※ 우대조건 관련 자세한 사항은 상품설명서 참조', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 고향사랑기부금 납부고객, 만 65세 이상 고령자, 만 19~34세 MZ고객, 고향사랑 특별금리 적용
가입대상: 개인
유의사항: 100만원 이상 가입, 고향사랑기부금 우대금리는 납부실적 확인 시 제공, 연간 판매액의 0.1% 공익기금 적립', '영업점,인터넷,스마트폰', '개인', '1. 100만원 이상 가입
2. 고향사랑기부금 우대금리는 농협은행/농축협 영업점 또는 고향사랑e음 홈페이지를 통한 고향사랑기부금 납부실적이 확인되는 경우 제공
3. 연간 판매액의 0.1% 공익기금 적립

※ 우대조건 관련 자세한 사항은 상품설명서 참조', NULL, '2026-07-14 20:01:32.834467+09', '2026-07-24 00:23:36.002295+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (108, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0013909:4', '하나의정기예금', '스마트폰

1개월이내 : 지급당시 해당기간 일반정기예금 월이자지급식 기본금리 1/2
1개월초과 : 지급당시 해당기간 일반정기예금 월이자지급식 기본금리 1/4

해당사항없음

실명의 개인 또는 개인사업자

1. 가입금액: 1백만원이상
2. 1인 최대가입한도 : 제한 없음', '가입방법: 스마트폰
우대조건: 해당사항 없음
가입대상: 실명의 개인 또는 개인사업자
유의사항: 가입금액 1백만원 이상, 1인 최대 가입한도 제한 없음', '스마트폰', '실명의 개인 또는 개인사업자', '1. 가입금액: 1백만원이상
2. 1인 최대가입한도 : 제한 없음', NULL, '2026-07-14 20:01:32.861894+09', '2026-07-24 00:50:16.593352+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (26, 1, 'SAVING', 'FSS:SAVING:020000:0010017:01020400660001', '부산이라 좋다 Big적금', '스마트폰

- 만기후 1년이내:가입기간별 일반정기적금 기본이율 x 50%
- 만기후 1년초과:가입기간별 일반정기적금 기본이율 x 20%

*우대이율 6개월 미만 최대2.00%, 6개월 이상 2.20%

만 14세이상 실명의 개인고객(1인 1계좌)

1. 가입한도: 월 1천원 이상 100만원 이하 원단위
2. 자유적립식', '스마트폰으로 가입 가능하며, 만기 후 이율이 적용됩니다. 우대이율은 6개월 미만 최대 2.00%, 6개월 이상 2.20%입니다.', '스마트폰', '만 14세이상 실명의 개인고객(1인 1계좌)', '1. 가입한도: 월 1천원 이상 100만원 이하 원단위
2. 자유적립식', NULL, '2026-07-14 19:58:52.859163+09', '2026-07-27 15:56:36.173045+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (31, 1, 'SAVING', 'FSS:SAVING:020000:0010020:220002101', 'jbank 저금통적금', '인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기적금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기적금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

* 거래조건에 따라 최고 2.1%p 우대금리 적용
① 자투리 출금계좌 평잔 50만원 이상 유지 : 0.8%p
② 첫거래고객 or JBANK저금통적금 1개월 내 재신규 : 0.5%p
③ 신규가입 시점에서 적금 목표금액 최소 30만원 이상 설정하고, 적금 신규일로부터 3개월 내 잔액이 목표금액 이상인 경우 :0.5%p
④ 추천인 우대금리 : 0.30%p

개인 및 개인사업자

월 납입한도 50만원 이하', '가입방법: 인터넷, 스마트폰
우대조건: 거래조건에 따라 최고 2.1%p 우대금리 적용 (자투리 출금계좌 평잔 50만원 이상 유지, 첫거래고객 또는 1개월 내 재신규, 적금 목표금액 설정 및 잔액 유지, 추천인 우대금리)
가입대상: 개인 및 개인사업자
유의사항: 만기 후 이자율 적용, 월 납입한도 50만원 이하', '인터넷,스마트폰', '개인 및 개인사업자', '월 납입한도 50만원 이하', NULL, '2026-07-14 19:59:09.107763+09', '2026-07-24 00:20:46.322787+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (46, 1, 'SAVING', 'FSS:SAVING:020000:0010030:03101', 'KDB 기업정기적금', '영업점,인터넷

* 만기후 1년 이내 : 만기일 현재 고시된 일반 정기적금 해당예금기간 기본이율의 1/2
* 만기후 1년 초과 : 만기일 현재 고시된 보통예금 이율

해당없음

개인사업자, 조합(비영리법인 포함), 법인

해당없음', '가입방법: 영업점, 인터넷
유의사항: 만기후 1년 이내에는 기본이율의 1/2, 1년 초과 시 보통예금 이율 적용', '영업점,인터넷', '개인사업자, 조합(비영리법인 포함), 법인', '해당없음', NULL, '2026-07-14 19:59:26.082844+09', '2026-07-24 00:21:06.37099+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (104, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0013175:10-003-1225-0001', 'NH왈츠회전예금 II', '영업점,인터넷,스마트폰

만기 후 3개월 : 기본금리의 50%
만기 후 6개월 : 기본금리의 20%
만기 후 6개월 초과 : 기본금리의 10%

* 기본금리 : 만기시점의 일반정기예금 계약기간별 금리

1. 급여이체실적(50만원 이상)이 있는 경우 : 0.1%p
2. 트리플 회전 우대이율 :  4회전기간부터 0.1%p

개인

기타유의사항없음.', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 급여이체실적(50만원 이상) 시 0.1%p, 트리플 회전 우대이율(4회전기간부터) 시 0.1%p
가입대상: 개인
유의사항: 만기 후 3개월 기본금리 50%, 6개월 20%, 6개월 초과 10% 적용', '영업점,인터넷,스마트폰', '개인', '기타유의사항없음.', NULL, '2026-07-14 20:01:32.703221+09', '2026-07-24 00:23:35.914857+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (33, 1, 'SAVING', 'FSS:SAVING:020000:0010020:220002701', '사이버우대매일부금', '인터넷,스마트폰

- 만기후 1개월 이내 : (일반)정기적금 기본이자율의 50%
(단, 최저금리 0.1%)
- 만기후 1개월 초과 3개월 이내 : (일반)정기적금 기본이자율의 25%
(단, 최저금리 0.1%)
- 만기후 3개월 초과 : 0.1%

최고 0.4%p 추가 우대금리 제공
1. 비대면채널 신규시 0.1%p
2. 아래의 조건 충족시 0.3%p 추가 우대금리 제공
①탐나는 J 직장인통장
②탐나는 J 주거래통장
가입고객이 기본우대 요건 충족 후 이 상품 가입시 0.1%p
③달리자 파킹통장 가입 고객이 이 상품 가입시 0.3%p
(단, ①, ②, ③항은 중복적용 불가)

개인 및 개인사업자

가입 최소금액 1천원 이상,
매 입금별 1천원 이상', '인터넷, 스마트폰으로 가입 가능하며, 가입 최소 금액은 1천원 이상입니다. 만기 후 경과 기간에 따라 이자율이 달라지며, 비대면 채널 신규 가입 시 우대금리를 제공합니다.', '인터넷,스마트폰', '개인 및 개인사업자', '가입 최소금액 1천원 이상,
매 입금별 1천원 이상', NULL, '2026-07-14 19:59:09.17694+09', '2026-07-24 00:20:46.389531+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (21, 1, 'SAVING', 'FSS:SAVING:020000:0010002:00266451', '퍼스트가계적금', '영업점,인터넷,스마트폰

만기 후 1개월: 약정이율의 50%
만기 후 1개월 초과 1년 이내: 약정이율의 30%
만기 후 1년 초과: 약정이율의 10%

없음

개인(개인사업자 포함)

해당없음', '가입방법: 영업점, 인터넷, 스마트폰
유의사항: 만기 후 이율은 약정 이율의 10%~50% 적용', '영업점,인터넷,스마트폰', '개인(개인사업자 포함)', '해당없음', NULL, '2026-07-14 19:58:52.614542+09', '2026-07-24 00:20:26.925197+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (111, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0014807:10120114700011', '헤이(Hey)정기예금', '인터넷,스마트폰

* 만기후 
-1개월 이내: 만기당시 일반정기예금(월이자지급식) 계약기간별 기본금리 1/2
-1개월초과~3개월 이내: 만기당시 일반정기예금(월이자지급식) 기본금리의 1/4
- 3개월 초과: 만기당시 보통예금 기본금리

없음

실명의 개인

-1인 다계좌 가능
 단, 합산금액 최대 10억원 이내
-최저 10만원 이상', '가입방법: 인터넷, 스마트폰
유의사항: 1인 다계좌 가능, 합산금액 최대 10억원 이내, 최저 10만원 이상', '인터넷,스마트폰', '실명의 개인', '-1인 다계좌 가능
 단, 합산금액 최대 10억원 이내
-최저 10만원 이상', NULL, '2026-07-14 20:01:41.603448+09', '2026-07-24 00:24:44.646289+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (44, 1, 'SAVING', 'FSS:SAVING:020000:0010026:01211210122', 'IBK중기근로자우대적금 (자유적립식)', '영업점,스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 2.20%p
1. 가입시점 중소기업 근로자로 확인된 경우 : 재직기간에 따라 최고 연 1.2%p
2. 당행 급여이체 실적(월50만원 이상) 6개월 이상
   인 경우 : 연 1.0%p

중소기업에서 근무하는
실명의 개인
(개인사업자 제외)

1인당 1계좌 가입 가능하며, 계좌당 100만원까지 납입 가능', '가입방법: 영업점, 스마트폰
우대조건: 가입시점 중소기업 근로자 확인 시 재직기간에 따라 최고 연 1.2%p, 당행 급여이체 실적(월50만원 이상) 6개월 이상 시 연 1.0%p
가입대상: 중소기업에서 근무하는 실명의 개인 (개인사업자 제외)
유의사항: 1인당 1계좌 가입 가능, 계좌당 100만원까지 납입 가능. 만기 후 금리 적용 조건 있음.', '영업점,스마트폰', '중소기업에서 근무하는
실명의 개인
(개인사업자 제외)', '1인당 1계좌 가입 가능하며, 계좌당 100만원까지 납입 가능', NULL, '2026-07-14 19:59:26.028034+09', '2026-07-24 00:21:06.339186+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (63, 1, 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000006', '마이키즈 적금', '스마트폰

만기 후
- 1개월 이내: 만기시점 기본금리 X 50%
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

1. 입금실적에 따라 우대금리 적용
2. 금리쿠폰을 입력시 우대금리 적용

만 17세 미만의 실명의 개인

가입금액: 0원 / 최대 납입금액 : 월 30만원 
가입기간 : 1년,2년,3년,4년,5년
 ㅇ 4년 금리: 최저 연 3.3% ~ 최고 연 8.3%
 ㅇ 5년 금리: 최저 연 3.5% ~ 최고 연 8.5%', '가입방법: 스마트폰
우대조건: 입금실적, 금리쿠폰 입력
가입대상: 만 17세 미만의 실명의 개인
유의사항: 만기 후 이자율 적용', '스마트폰', '만 17세 미만의 실명의 개인', '가입금액: 0원 / 최대 납입금액 : 월 30만원 
가입기간 : 1년,2년,3년,4년,5년
 ㅇ 4년 금리: 최저 연 3.3% ~ 최고 연 8.3%
 ㅇ 5년 금리: 최저 연 3.5% ~ 최고 연 8.5%', NULL, '2026-07-14 19:59:59.66558+09', '2026-07-24 00:22:15.036332+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (68, 1, 'SAVING', 'FSS:SAVING:020000:0014807:10141114300011', 'Sh해양플라스틱Zero!적금 (자유적립식)', '영업점,인터넷,스마트폰

* 만기후 1년 이내
 - 만기당시 상호부금 
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

* 최대우대금리:0.5%
1. 해양플라스틱감축서약 : 0.1% (신규시) 
2. 봉사활동 또는 상품홍보 : 0.2% (만기시) 
3. 입출금통장 최초신규 : 0.2% (만기시)
4. 자동이체 출금실적 : 0.2% (만기시)
 - 수협신용카드 / 당행 펀드 / 수협체크카드

실명의 개인

- 1인 1계좌 
- 월 가입한도 : 20만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 해양플라스틱감축서약, 봉사활동 또는 상품홍보, 입출금통장 최초신규, 자동이체 출금실적
가입대상: 실명의 개인
유의사항: 1인 1계좌, 월 가입한도 20만원', '영업점,인터넷,스마트폰', '실명의 개인', '- 1인 1계좌 
- 월 가입한도 : 20만원', NULL, '2026-07-14 19:59:59.861703+09', '2026-07-22 01:38:26.426242+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (22, 1, 'SAVING', 'FSS:SAVING:020000:0010016:10521001001166004', 'iM함께적금', '영업점,인터넷,스마트폰

만기 후 1개월 미만 경과: 약정이자율 x 50%
만기 후 3개월 미만 경과: 약정이자율 x 25%
만기 후 3개월 이상 경과: 약정이자율 x 10%

*최고우대금리:연0.85%p
-전월 총수신 평잔 30만원 이상 또는 첫만남플러스통장 보유:연0.10%p
-당행 주택청약상품 보유 
-신규일 "iM함께예금" 동시 가입 및 만기 보유 
각 연0.20%p
-당행 오픈뱅킹서비스에 다른 은행 계좌 등록:연0.30%p                       
*해당 상품을 인터넷/모바일뱅킹을 통해 가입:연0.05%p

실명의 개인 및 개인사업자

계좌당 가입 최저한도 : 10만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 최고 연0.85%p 우대금리 제공 (전월 총수신 평잔 30만원 이상 또는 첫만남플러스통장 보유, 당행 주택청약상품 보유, 신규일 "iM함께예금" 동시 가입 및 만기 보유, 당행 오픈뱅킹서비스에 다른 은행 계좌 등록, 인터넷/모바일뱅킹 가입 시)
가입대상: 실명의 개인 및 개인사업자
유의사항: 만기 후 경과 기간에 따라 약정이자율의 50%, 25%, 10% 적용', '영업점,인터넷,스마트폰', '실명의 개인 및 개인사업자', '계좌당 가입 최저한도 : 10만원', NULL, '2026-07-14 19:58:52.710685+09', '2026-07-24 00:20:26.944506+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (55, 1, 'SAVING', 'FSS:SAVING:020000:0013175:10-047-1381-0001', 'NH내가Green초록세상적금', '영업점,인터넷,스마트폰

만기후 1년 이내 : 만기시점 계약기간별 기본금리의 1/2
만기후 1년 초과 : 보통예금 금리

※ 우대금리 최대한도 : 1.0%p(연%, 세전)
1. 온실가스 줄이기 실천서약서 동의 : 0.1%p
2. 통장미발급 : 0.3%p
3. 손하나로인증 서비스 등록 : 0.2%p
4. 대중교통이용 : 0.2%p
5. NH내가Green초록세상예금 동시 보유 : 0.2%p

개인

초입금5만원 이상 및 매회 1만원 이상, 매월50만원이내
(단, 만기일 전 3개월 이내에는 이전 적립금 합계액을 초과하여 입금불가)

※자세한 사항은 상품설명서 참조', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 온실가스 줄이기 실천서약서 동의, 통장미발급, 손하나로인증 서비스 등록, 대중교통이용, NH내가Green초록세상예금 동시 보유
가입대상: 개인
유의사항: 만기후 1년 이내 기본금리 1/2, 만기후 1년 초과 보통예금 금리 적용. 만기일 전 3개월 이내 이전 적립금 합계액 초과 입금 불가.', '영업점,인터넷,스마트폰', '개인', '초입금5만원 이상 및 매회 1만원 이상, 매월50만원이내
(단, 만기일 전 3개월 이내에는 이전 적립금 합계액을 초과하여 입금불가)

※자세한 사항은 상품설명서 참조', NULL, '2026-07-14 19:59:43.576663+09', '2026-07-27 15:57:42.378453+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (79, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010002:00320342', 'e-그린세이브예금', '인터넷,스마트폰

만기 후 1개월: 약정이율의 50%
만기 후 1개월 초과 1년 이내: 약정이율의 30%
만기 후 1년 초과: 약정이율의 10%

1.SC제일은행 최초 거래 신규고객 우대이율 제공(보너스이율0.2%)                     
2.만기일에 아래의 조건을 모두 충족하는 경우보너스이율 제공
-6~12개월제(만기일시지급식) 5천만원 이상 가입
-만기일 기준 전전월 마지막 영업일에 수익증권(펀드)*을 3천만원 이상 보유 
(가입기간: 6개월~1년제/ 보너스이율:0.1% / 만기해약하는 경우에 한해 보너스이율을 적용함)

개인(개인사업자 포함)

디지털채널 전용상품 (인터넷, 모바일뱅킹)', '가입방법: 인터넷, 스마트폰
우대조건: SC제일은행 최초 거래 신규고객 우대이율 제공(보너스이율0.2%), 만기일에 6~12개월제 5천만원 이상 가입 및 만기일 기준 전전월 마지막 영업일에 수익증권(펀드) 3천만원 이상 보유 시 보너스이율 0.1% 제공
가입대상: 개인(개인사업자 포함)
유의사항: 디지털채널 전용상품, 만기 후 이율 적용 조건 명시', '인터넷,스마트폰', '개인(개인사업자 포함)', '디지털채널 전용상품 (인터넷, 모바일뱅킹)', NULL, '2026-07-14 20:01:02.181356+09', '2026-07-24 00:22:57.947862+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (83, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010017:01030500600002', '더(The) 레벨업 정기예금', '스마트폰

- 만기후1년내: 가입기간별 일반정기예금이율 월이자지급식 x 50%,
- 만기후1년초과:가입기간별 일반정기예금이율월 이자지급식 x 20%

*우대이율(최대 0.90%p)
가. 모바일뱅킹 금융정보 및 혜택알림 동의 우대이율 : 0.10%p
나. 비대면 정기예금 재예치 우대이율  : 0.80%

실명의 개인

1. 가입금액 : 1백만원 이상 제한없음 (원단위)
2. 가입기간 : 6개월, 1년
3. 이자지급방식 : 만기일시지급식', '가입방법: 스마트폰
우대조건: 모바일뱅킹 금융정보 및 혜택알림 동의, 비대면 정기예금 재예치
가입대상: 실명의 개인
유의사항: 만기후 1년 내 일반정기예금이율의 50%, 만기후 1년 초과 시 20% 적용', '스마트폰', '실명의 개인', '1. 가입금액 : 1백만원 이상 제한없음 (원단위)
2. 가입기간 : 6개월, 1년
3. 이자지급방식 : 만기일시지급식', NULL, '2026-07-14 20:01:02.384112+09', '2026-07-24 00:22:58.061631+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (97, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010026:01211310121', 'IBK평생한가족통장(실세금리정기예금)', '영업점,인터넷,스마트폰

만기일 당시 정기예금 만기후 이자율 적용 -1개월 이내: 만기일 당시 계약기간별 고시금리×50% -1월 초과 6개월 이내: 만기일 당시 계약기간별 고시금리×30% -6개월 초과: 만기일 당시 계약기간별 고시금리×20%

최고 연 0.20%p

-고객별 우대 : 최고 연 0.05%p
 1. 최초신규고객 : 연 0.05%p
 2. 재예치고객 : 연 0.05%p
 3. 장기거래고객 : 연 0.05%p

-주거래우대 : 연 0.15%p

실명의 개인
(개인사업자 제외)

계좌 수 제한 없으며, 최소 1백만원 이상 통합한도 1억원 이내 가입 가능', '가입방법: 영업점, 인터넷, 스마트폰
유의사항: 만기 후 이자율 적용 조건 확인 필요', '영업점,인터넷,스마트폰', '실명의 개인
(개인사업자 제외)', '계좌 수 제한 없으며, 최소 1백만원 이상 통합한도 1억원 이내 가입 가능', NULL, '2026-07-14 20:01:18.769163+09', '2026-07-24 00:23:16.067938+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (109, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0014674:01013000110000000001', '코드K 정기예금', '스마트폰

만기 후 
- 1개월 이내 : 만기시점 기본금리 X 50%
- 1개월 초과~6개월 이내 : 만기시점 기본금리 X 30%
- 6개월 초과 : 연 0.20%

우대조건 없음

만 17세 이상 실명의 개인 및 개인사업자

가입금액 : 1백만원 이상
가입기간 : 1개월~36개월', '가입방법: 스마트폰
유의사항: 만기 후 이자율 적용 조건 상이', '스마트폰', '만 17세 이상 실명의 개인 및 개인사업자', '가입금액 : 1백만원 이상
가입기간 : 1개월~36개월', NULL, '2026-07-14 20:01:41.499999+09', '2026-07-24 00:24:44.623108+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (42, 1, 'SAVING', 'FSS:SAVING:020000:0010026:01211210113', 'IBK D-day적금(자유적립식)', '스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 1.5%p
1. 목표달성 축하금리 : 연 1.0%p
  - 당행 입출금식 계좌에서 이 적금으로 자동이체를 통해 3회 이상 납입하고 만기일 전일까지 목표금액(신규 시 직접 설정) 이상 납입하는 경우
2. 최초거래고객 우대금리 : 연 0.5%p

실명의 개인
(개인사업자 제외)

1인당 3계좌 가입 가능하며, 계좌당 20만원 이내 납입 가능', '스마트폰으로 가입 가능하며, 만기 후에는 약정금리의 20~50%가 적용됩니다. 목표금액 이상 납입 및 자동이체 3회 이상 시 1.0%p, 최초 거래 시 0.5%p의 우대금리가 제공됩니다.', '스마트폰', '실명의 개인
(개인사업자 제외)', '1인당 3계좌 가입 가능하며, 계좌당 20만원 이내 납입 가능', NULL, '2026-07-14 19:59:25.96441+09', '2026-07-24 00:21:06.312913+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (105, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0013175:10-003-1381-0001', 'NH내가Green초록세상예금', '영업점,인터넷,스마트폰

만기 후 3개월 : 기본금리의 50%
만기 후 6개월 : 기본금리의 20%
만기 후  6개월 초과 : 기본금리의 10%

* 기본금리 : 만기시점의 일반정기예금 계약기간별 금리

※ 우대금리 최대한도 : 0.4%p(연%, 세전)
1. 온실가스 줄이기 실천서약서 동의 : 0.1%p
2. 통장미발급 : 0.1%p
3. 손하나로인증 서비스 등록 : 0.1%p
4. NH내가Green초록세상적금 동시 보유 : 0.1%p

개인

1. 300만원이상 가입
2. 온실가스 줄이기 실천서약서 동의시 가입가능
3. 신규가입 계좌당 2천원씩 녹색환경기금 적립
※ 자세한 사항은 상품설명서 참조', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 온실가스 줄이기 실천서약서 동의, 통장미발급, 손하나로인증 서비스 등록, NH내가Green초록세상적금 동시 보유
가입대상: 개인
유의사항: 300만원 이상 가입 가능, 온실가스 줄이기 실천서약서 동의 시 가입 가능, 신규 가입 계좌당 2천원씩 녹색환경기금 적립', '영업점,인터넷,스마트폰', '개인', '1. 300만원이상 가입
2. 온실가스 줄이기 실천서약서 동의시 가입가능
3. 신규가입 계좌당 2천원씩 녹색환경기금 적립
※ 자세한 사항은 상품설명서 참조', NULL, '2026-07-14 20:01:32.742965+09', '2026-07-24 00:23:35.95661+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (29, 1, 'SAVING', 'FSS:SAVING:020000:0010019:TD11330030000', '여행스케치_남도투어적금', '영업점,스마트폰

만기후 1개월 이내: 만기일 당시 최초 가입 기간별 고시금리의 1/2 
만기후 1개월 초과: 0.1%

▶ 최고우대금리 1.9%p 
①예금가입일~만기일전일까지 당행이선정한 전라남도 관광지 방문 인증시 : 최고 1.5%p
②신용(체크)카드사용실적300만원이상:최고 0.3%p
③개인(신용)정보 동의: 0.1%p

만14세이상 개인 및 개인사업자

1. 가입기간 :12개월제,18개월제
2. 가입금액 : 월 5만원 이상 1백만원 이하 (1인1계좌)
※ 18개월 정액식 기본금리 3.3%, 최고금리 5.2%', '가입방법: 영업점, 스마트폰
우대조건: 전라남도 관광지 방문 인증 시 최고 1.5%p, 신용(체크)카드 사용실적 300만원 이상 시 최고 0.3%p, 개인(신용)정보 동의 시 0.1%p
가입대상: 만 14세 이상 개인 및 개인사업자
유의사항: 만기 후 1개월 이내에는 만기일 당시 최초 가입 기간별 고시금리의 1/2, 1개월 초과 시 0.1%의 금리가 적용됩니다.', '영업점,스마트폰', '만14세이상 개인 및 개인사업자', '1. 가입기간 :12개월제,18개월제
2. 가입금액 : 월 5만원 이상 1백만원 이하 (1인1계좌)
※ 18개월 정액식 기본금리 3.3%, 최고금리 5.2%', NULL, '2026-07-14 19:59:09.064276+09', '2026-07-27 15:56:41.254476+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (19, 1, 'SAVING', 'FSS:SAVING:020000:0010001:WR0001F', '우리SUPER주거래적금', '영업점,인터넷,스마트폰,전화(텔레뱅킹)

만기 후
- 1개월이내 : 만기시점약정이율×50%
- 1개월초과 6개월이내: 만기시점약정이율×30%
- 6개월초과 : 만기시점약정이율×20%

※ 만기시점 약정이율 : 일반정기적금 금리

1.우리은행 입출식 계좌에서 각 항목별 실적 월 수가 계약기간의 1/2이상인 경우
가.급여/연금 이체:연 0.7%p
나.공과금 자동이체 출금: 0.3%p
다.우리카드사 신용/체크카드 결제 10만원 이상: 연 0.3%p
2.상품서비스 마케팅 동의 항목 중 전화(휴대폰) 및 SMS항목을 모두 동의 후 만기까지 유지 : 연 0.1%p
3.금리쿠폰을 적용

실명의 개인

1. 가입기간 : 1년/2년/3년
2. 가입금액 : 월 50만원 이내', NULL, '영업점,인터넷,스마트폰,전화(텔레뱅킹)', '실명의 개인', '1. 가입기간 : 1년/2년/3년
2. 가입금액 : 월 50만원 이내', NULL, '2026-07-14 19:58:52.426385+09', '2026-07-24 00:20:26.854303+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (54, 1, 'SAVING', 'FSS:SAVING:020000:0013175:10-047-1365-0001', 'NH1934월복리적금', '영업점,인터넷,스마트폰

만기후 1년 이내 : 만기시점 계약기간별 기본금리의 1/2
만기후 1년 초과 : 보통예금 금리

1. 급여실적 : 1.0%p (①/② 중복 적용 불가)
 ① 급여이체 실적 : 1.0%p
 ② 개인사업자 계좌 실적 :1.0%p
2. 비대면 채널 이체 실적 : 0.3%p
3. 마케팅 동의 : 0.2%p
4. 농업계고 및 청년농부사관학교 졸업자 : 2.0%p

만19세~만34세 개인 및 개인사업자

초입금 및 매회 1만원 이상, 월 50만원 이내 자유적립

급여이체 실적과 개인사업자 계좌 실적 우대금리는 중복 적용 불가

 * 급여입금실적 인정기준
  - 가입기간 12개월 이하 : 3개월 이상 급여이체
  - 가입기간 24개월 이하 : 12개월 이상 급여이체', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 급여이체 실적, 개인사업자 계좌 실적, 비대면 채널 이체 실적, 마케팅 동의, 농업계고 및 청년농부사관학교 졸업자
가입대상: 만 19세~만 34세 개인 및 개인사업자
유의사항: 급여이체 실적과 개인사업자 계좌 실적 우대금리는 중복 적용 불가', '영업점,인터넷,스마트폰', '만19세~만34세 개인 및 개인사업자', '초입금 및 매회 1만원 이상, 월 50만원 이내 자유적립

급여이체 실적과 개인사업자 계좌 실적 우대금리는 중복 적용 불가

 * 급여입금실적 인정기준
  - 가입기간 12개월 이하 : 3개월 이상 급여이체
  - 가입기간 24개월 이하 : 12개월 이상 급여이체', NULL, '2026-07-14 19:59:43.518485+09', '2026-07-22 01:38:08.640376+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (64, 1, 'SAVING', 'FSS:SAVING:020000:0014674:01012000210000000000', '주거래우대 자유적금', '스마트폰

만기 후 
- 1개월 이내: 만기시점 기본금리 X 50% 
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

급여이체 또는 통신비 자동이체, 체크카드 고객에게 우대금리 제공 (최고 연 0.6%)

만 17세 이상 실명의 개인 및 개인사업자

가입금액: 1천원 이상 300만원 이하
가입기간: 6개월 ~ 36개월
(1인 최대 3계좌)', '스마트폰으로 가입 가능하며, 만기 후 이자율이 차등 적용됩니다. 급여이체 또는 통신비 자동이체, 체크카드 고객에게 우대금리가 제공됩니다.', '스마트폰', '만 17세 이상 실명의 개인 및 개인사업자', '가입금액: 1천원 이상 300만원 이하
가입기간: 6개월 ~ 36개월
(1인 최대 3계좌)', NULL, '2026-07-14 19:59:59.69218+09', '2026-07-24 00:22:15.065663+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (39, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21001236', '주거래 프리미엄 적금', '영업점,인터넷,스마트폰

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

①주거래우대 0.5%
②공과금 자동이체 0.4~0.6%
③신규고객 0.2%
④주택청약종합저축 보유 0.1%
⑤ 전자명함을 통한 신규 시 0.2%

실명의 개인 및 개인사업자

1.계악기간은 1년제, 2년제, 3년제로 한다.
2. 적립금액은 매월 1만원이상, 최고금액은 제한없음', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 주거래우대, 공과금 자동이체, 신규고객, 주택청약종합저축 보유, 전자명함 신규
가입대상: 실명의 개인 및 개인사업자
유의사항: 계약기간 1년, 2년, 3년제. 매월 1만원 이상 납입 가능, 최고 금액 제한 없음. 만기 후 1개월 이내 일반정기예금 기본이율의 50%, 1개월 초과 시 20% 적용.', '영업점,인터넷,스마트폰', '실명의 개인 및 개인사업자', '1.계악기간은 1년제, 2년제, 3년제로 한다.
2. 적립금액은 매월 1만원이상, 최고금액은 제한없음', NULL, '2026-07-14 19:59:25.818273+09', '2026-07-24 00:21:06.251176+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (58, 1, 'SAVING', 'FSS:SAVING:020000:0013909:52', '주거래하나 월복리적금', '영업점,인터넷,스마트폰

1개월 이내 : 지급당시 해당기간별 일반정기적금 기본금리 1/2
1개월 초과 : 지급당시 해당기간별 일반정기적금 기본금리 1/4

최고 연1.0%
- 주거래하나우대(연 0.5%) : 적금만기 전전월말기준 본인명의 당행입출금통장을 통해 계약기간 1/2이상 이체된 주거래실적 1종  - 주거래플러스우대(연 0.9%) : 주거래 하나우대와 동일요건의 거래실적 2종이상 경우 
- 온라인.재예치우대 연 최대 0.1%

실명의 개인
또는 개인사업자

1. 1인 1계좌만 가능
(급여하나월복리적금,연금하나 월복리적금과 중복가입 불가)
2. 가입금액 
 - 최저1만원~300만원이하
3. 적립한도
 분기당 1만원이상 300만원
 (자유적립식)', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 주거래 실적 1종 이상 시 연 0.5%, 2종 이상 시 연 0.9% 추가. 온라인/재예치 시 최대 연 0.1% 추가.
가입대상: 실명의 개인 또는 개인사업자
유의사항: 1인 1계좌만 가능. 가입금액 최저 1만원 ~ 300만원 이하. 분기당 1만원 이상 300만원까지 적립 가능.', '영업점,인터넷,스마트폰', '실명의 개인
또는 개인사업자', '1. 1인 1계좌만 가능
(급여하나월복리적금,연금하나 월복리적금과 중복가입 불가)
2. 가입금액 
 - 최저1만원~300만원이하
3. 적립한도
 분기당 1만원이상 300만원
 (자유적립식)', NULL, '2026-07-14 19:59:43.745265+09', '2026-07-24 00:21:53.592132+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (61, 1, 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000004', '궁금한 적금', '스마트폰

만기 후 
- 1개월 이내: 만기시점 기본금리 X 50% 
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

입금할 때마다 우대금리를 랜덤으로 제공하며, 입금 시 제공되는 우대금리를 누적으로 합산하여 만기 해지 시 적용 (최고 연 6.0%)

만 17세 이상 실명의 개인 및 개인사업자

가입금액: 0원
가입기간: 31일
(1인 최대 1계좌)
적립방법 : 연결계좌를 통한 직접 입금만 가능하며, 입금은 신규일부터 만기일 전일까지 1일 1회 가능
납입금액 : 1일(1회) 최소 100원, 최대 5만원', '가입방법: 스마트폰
우대조건: 입금 시마다 랜덤 우대금리 제공, 누적 합산하여 만기 적용 (최고 연 6.0%)
가입대상: 만 17세 이상 실명의 개인 및 개인사업자
유의사항: 1인 최대 1계좌, 연결계좌 통한 직접 입금만 가능, 신규일부터 만기일 전일까지 1일 1회 입금 가능', '스마트폰', '만 17세 이상 실명의 개인 및 개인사업자', '가입금액: 0원
가입기간: 31일
(1인 최대 1계좌)
적립방법 : 연결계좌를 통한 직접 입금만 가능하며, 입금은 신규일부터 만기일 전일까지 1일 1회 가능
납입금액 : 1일(1회) 최소 100원, 최대 5만원', NULL, '2026-07-14 19:59:59.628377+09', '2026-07-24 00:22:15.019404+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (41, 1, 'SAVING', 'FSS:SAVING:020000:0010024:21001292', '오면우대! 하면우대! 정기적금', '영업점,인터넷,스마트폰

만기후 1개월 이내: 일반정기예금 기본이율의 50%
만기후 1개월 초과: 일반정기예금 기본이율의 20%

신규고객
①적금가입시3.0%
②상품가입 전 마케팅동의시0.1%
③이 적금 신규월 포함 3개월 동안 10만원 이상 경남은행 카드 대금결제시2.0%
기존고객
①급여 또는 연금입금시1.5%
②공과금 자동이체시 2.0%
③경남은행 카드이용시(8회이상&10만원이상 카드대금 결제보유)1.5%
④상품가입 전 마케팅동의시0.1%

실명의 개인

1.계악기간은 1년제로 한다.
2. 적립금액은 매월 10만원이상, 50만원 이하(1만원 이상)', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 신규고객 적금가입 시 3.0%, 상품가입 전 마케팅 동의 시 0.1%, 신규월 포함 3개월 동안 10만원 이상 경남은행 카드 대금 결제 시 2.0%. 기존고객 급여 또는 연금 입금 시 1.5%, 공과금 자동이체 시 2.0%, 경남은행 카드 이용 시 1.5%, 상품가입 전 마케팅 동의 시 0.1%.
가입대상: 실명의 개인
유의사항: 계약기간은 1년제이며, 적립금액은 매월 10만원 이상 50만원 이하입니다.', '영업점,인터넷,스마트폰', '실명의 개인', '1.계악기간은 1년제로 한다.
2. 적립금액은 매월 10만원이상, 50만원 이하(1만원 이상)', NULL, '2026-07-14 19:59:25.912177+09', '2026-07-24 00:21:06.292528+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (51, 1, 'SAVING', 'FSS:SAVING:020000:0010927:010200100104', 'KB 특★한 적금', '스마트폰

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

항목별 적용 조건 충족시, 최고 연 4.0%p
① 목표달성 축하 우대이율: 최고 연 1.0%p
    50만원 이하: 연 0.5%p, 50만원 초과: 연 1.0%p 
② 별 모으기 우대이율 : 최고 연 1.0%p
    10개: 연 0.5%p, 20개: 연 1.0%p
③ 함께해요 우대이율: 최고 연 2.0%p

실명의 개인

개인사업자, 임의단체 및
공동명의 가입 불가
(1인 최대 3계좌)', '스마트폰으로 가입 가능하며, 가입 대상은 실명의 개인입니다. 개인사업자, 임의단체, 공동명의는 가입할 수 없습니다. 1인당 최대 3계좌까지 개설 가능합니다.', '스마트폰', '실명의 개인', '개인사업자, 임의단체 및
공동명의 가입 불가
(1인 최대 3계좌)', NULL, '2026-07-14 19:59:43.366263+09', '2026-07-24 00:21:53.350307+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (65, 1, 'SAVING', 'FSS:SAVING:020000:0014807:10140114300011', 'Sh해양플라스틱Zero!적금 (정액적립식)', '영업점,인터넷,스마트폰

* 만기후 1년 이내
 - 만기당시 일반정기적금 
계약기간별 기본금리 1/2
* 만기후 1년 초과
 - 만기당시 보통예금 기본금리

* 최대우대금리:0.5%
1. 해양플라스틱감축서약 : 0.1% (신규시) 
2. 봉사활동 또는 상품홍보 : 0.2% (만기시) 
3. 입출금통장 최초신규 : 0.2% (만기시)
4. 자동이체 출금실적 : 0.2% (만기시)
 - 수협신용카드 / 당행 펀드 / 수협체크카드

실명의 개인

- 1인 1계좌 
- 월 가입한도 : 100만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 해양플라스틱감축서약, 봉사활동 또는 상품홍보, 입출금통장 최초신규, 자동이체 출금실적
가입대상: 실명의 개인
유의사항: 1인 1계좌, 월 가입한도 100만원', '영업점,인터넷,스마트폰', '실명의 개인', '- 1인 1계좌 
- 월 가입한도 : 100만원', NULL, '2026-07-14 19:59:59.733497+09', '2026-07-22 01:38:26.272521+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (45, 1, 'SAVING', 'FSS:SAVING:020000:0010026:01211210129', 'IBK모으기통장(자유적립식)', '영업점,스마트폰

만기일 당시 정기적금 만기후금리 적용
- 1개월 이내: 만기일 당시 약정금리x50%
- 1월 초과 6개월 이내: 만기일 당시 약정금리x30%
- 6개월 초과: 만기일 당시 약정금리x20%

최고 연 0.20%p
1. 자동이체 우대금리 : 연 0.20%p
- 6개월 이상 12개월 미만 : 3회
 12개월 이상 24개월 미만 : 6회
 24개월 이상 36개월 미만 : 12회
 36개월 : 18회

실명의 개인
(개인사업자 제외)

1인당 5계좌 가입 가능하며, 계좌당 최소 1천원 이상 3백만원까지 납입 가능', '영업점, 스마트폰에서 가입 가능합니다. 만기 후 금리 적용 시 만기일 당시 약정금리의 50%~20%가 적용됩니다. 자동이체 시 최대 연 0.20%p 우대금리가 적용됩니다. 실명의 개인만 가입 가능하며, 개인사업자는 제외됩니다. 1인당 5계좌까지 가입 가능하며, 계좌당 최소 1천원 이상 3백만원까지 납입 가능합니다.', '영업점,스마트폰', '실명의 개인
(개인사업자 제외)', '1인당 5계좌 가입 가능하며, 계좌당 최소 1천원 이상 3백만원까지 납입 가능', NULL, '2026-07-14 19:59:26.047523+09', '2026-07-24 00:21:06.355813+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (49, 1, 'SAVING', 'FSS:SAVING:020000:0010927:010200100070', 'KB내맘대로적금', '인터넷,스마트폰

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

신규 시 다음의 9가지 우대이율 항목 중 6가지를 자유롭게 선택하고, 아래 우대이율 적용조건 충족 시 항목 당 각 연0.1%p의 우대이율 적용
(최고 연0.6%p)
 - 우대이율 항목 : 급여이체, 카드결제계좌, 자동이체 저축, 아파트관리비 이체, KB스타뱅킹 이체, 장기거래, 첫 거래, 주택청약종합저축, 소중한 날

실명의 개인 또는 개인사업자

인터넷뱅킹/KB스타뱅킹 전용상품', '가입방법: 인터넷, 스마트폰
우대조건: 9가지 우대이율 항목 중 6가지 선택 시 항목당 연 0.1%p 가산 (최고 연 0.6%p)
가입대상: 실명의 개인 또는 개인사업자
유의사항: 인터넷뱅킹/KB스타뱅킹 전용상품', '인터넷,스마트폰', '실명의 개인 또는 개인사업자', '인터넷뱅킹/KB스타뱅킹 전용상품', NULL, '2026-07-14 19:59:43.273591+09', '2026-07-24 00:21:53.26781+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (50, 1, 'SAVING', 'FSS:SAVING:020000:0010927:010200100084', 'KB맑은하늘적금', '영업점,인터넷,스마트폰

- 1개월 이내 : 기본이율 X 50%
- 1개월 초과  ~ 3개월 이내 : 기본이율 X 30%
- 3개월 초과 : 0.1%

맑은하늘을 위한 미션별 제공조건을 달성하는 경우 각 미션별 우대이율 제공
 - 1년제 최고 연 0.8%p, 2년제 최고 연 0.9%p, 3년제 최고 연 1.0%p
① 종이통장 줄이기 미션: 연 0.1%p
② 종이서식 줄이기 미션: 연 0.2%p
③ 대중교통 미션: 1년제 연 0.4%p, 2년제 연 0.5%p, 3년제 연 0.6%p
④ 퀴즈미션: 연 0.1%p

실명의 개인

공동명의 불가
(1인 최대 3계좌)', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 맑은하늘 미션 달성 시 우대이율 제공 (종이통장 줄이기, 종이서식 줄이기, 대중교통 이용, 퀴즈 미션 등)
가입대상: 실명의 개인
유의사항: 공동명의 불가, 1인 최대 3계좌', '영업점,인터넷,스마트폰', '실명의 개인', '공동명의 불가
(1인 최대 3계좌)', NULL, '2026-07-14 19:59:43.327348+09', '2026-07-27 15:57:42.195424+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (71, 1, 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0005', '카카오뱅크 26주적금', '스마트폰

- 만기 후 1개월 이내 : 가입시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

자동이체 연속 성공 우대금리 제공 : 최고 연 3.00%p
- 제공조건
① 7주차까지 자동이체 납입을 연속 성공하고 만기해지 하는 경우 연 1.00%p 제공
② 26주차까지 자동이체 납입을 연속 성공하고 만기해지 하는 경우 연 2.00%p 추가 제공
- 유의사항 : 자동이체 실패한 주차를 빈자리채우기 하여도 성공으로 인정되지 않음

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 가입금액 : 1천원, 2천원, 3천원, 5천원, 1만원
3. 가입기간 : 6개월
4. 26주적금서비스(자동이체)를 통해서 납입이 가능하며, 그 외의 입금은 모두 제한됨
(단, 26주적금 서비스를 통한 납입 실패 시 빈자리 채우기로 납입 가능)', '가입방법: 스마트폰. 가입금액: 1천원, 2천원, 3천원, 5천원, 1만원. 가입기간: 6개월. 26주적금서비스(자동이체)를 통해서 납입 가능하며, 그 외 입금 제한됨 (단, 26주적금 서비스 납입 실패 시 빈자리 채우기 가능).', '스마트폰', '만 14세 이상의 실명의 개인', '1. 가입방법 : 스마트폰
2. 가입금액 : 1천원, 2천원, 3천원, 5천원, 1만원
3. 가입기간 : 6개월
4. 26주적금서비스(자동이체)를 통해서 납입이 가능하며, 그 외의 입금은 모두 제한됨
(단, 26주적금 서비스를 통한 납입 실패 시 빈자리 채우기로 납입 가능)', NULL, '2026-07-14 20:00:14.575151+09', '2026-07-24 00:22:38.237301+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (57, 1, 'SAVING', 'FSS:SAVING:020000:0013175:10-059-1264-0001', 'NH직장인월복리적금', '영업점,인터넷,스마트폰

만기후 3개월 이내 : 만기시점 국고채 1년물 금리
만기후 1년 이내 : 만기시점 채움적금 계약기간별 고시금리의 50%
만기후 1년 초과 : 만기시점 보통예금 금리

1. 급여입금실적 보유 고객 중
 - 가입기간 중 3개월 이상 급여이체시 : 0.3%p
 - 주택청약종합저축 또는 펀드가입 : 0.2%p
 - NH채움카드 결제실적 1백만원 이상 : 0.2%p
2. 인터넷(스마트)뱅킹 또는 올원뱅크로 가입 : 0.1%p

만18세이상 개인

초입금 및 매회 입금 1만원 이상 원 단위,  1인당 분기별 3백만원 이내 적립 가능
(단,계약기간 3/4 경과 후 적립할 수 있는 금액은 이전 적립누계액의 1/2이내)', NULL, '영업점,인터넷,스마트폰', '만18세이상 개인', '초입금 및 매회 입금 1만원 이상 원 단위,  1인당 분기별 3백만원 이내 적립 가능
(단,계약기간 3/4 경과 후 적립할 수 있는 금액은 이전 적립누계액의 1/2이내)', NULL, '2026-07-14 19:59:43.683712+09', '2026-07-24 00:21:53.561377+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (60, 1, 'SAVING', 'FSS:SAVING:020000:0014674:01012000200000000003', '코드K 자유적금', '스마트폰

만기 후 
- 1개월 이내: 만기시점 기본금리 X 50% 
- 1개월 초과 ~ 6개월 이내: 만기시점 기본금리 X 30%
- 6개월 초과: 연 0.20%

금리우대 코드를 입력하는 경우 우대금리 적용

만 17세 이상 실명의 개인 및 개인사업자

가입금액: 1만원 이상 30만원 이하
가입기간: 6개월, 1년, 2년, 3년 
(1인 최대 15계좌)', '가입방법: 스마트폰
우대조건: 금리우대 코드를 입력하는 경우 우대금리 적용
가입대상: 만 17세 이상 실명의 개인 및 개인사업자
유의사항: 만기 후 1개월 이내에는 만기 시점 기본금리의 50%, 1개월 초과 6개월 이내에는 30%가 적용되며, 6개월 초과 시 연 0.20%가 적용됩니다.', '스마트폰', '만 17세 이상 실명의 개인 및 개인사업자', '가입금액: 1만원 이상 30만원 이하
가입기간: 6개월, 1년, 2년, 3년 
(1인 최대 15계좌)', NULL, '2026-07-14 19:59:59.576111+09', '2026-07-24 00:22:15.002682+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (70, 1, 'SAVING', 'FSS:SAVING:020000:0015130:10-01-30-355-0002', '카카오뱅크 자유적금', '스마트폰

- 만기 후 1개월 이내 : 가입(또는 자동연장)시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입(또는 자동연장)시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

자동이체시 우대금리 제공 : 연 0.20%p
 - 제공조건 : 전체 계약월수의 1/2이상을 자동이체로 납입하고 만기 해지하는 경우
 - 유의사항 : 만기 자동연장된 원리금은 우대금리를 제공하지 않음

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 가입금액 : 1천원 이상(원단위)
3. 가입기간 : 6개월 이상 ~ 36개월 이하(월, 일단위 지정 가능)
4. 월 적립한도 : 1천원 이상 월 300만원 이하 원단위(단, 자동연장된 원리금은 월 적립한도에 포함되지 않음)', '스마트폰으로 가입 가능하며, 1천원 이상 월 300만원 이하로 납입할 수 있습니다. 만기 후 일정 기간 내 해지 시 기본금리의 일부만 적용됩니다.', '스마트폰', '만 14세 이상의 실명의 개인', '1. 가입방법 : 스마트폰
2. 가입금액 : 1천원 이상(원단위)
3. 가입기간 : 6개월 이상 ~ 36개월 이하(월, 일단위 지정 가능)
4. 월 적립한도 : 1천원 이상 월 300만원 이하 원단위(단, 자동연장된 원리금은 월 적립한도에 포함되지 않음)', NULL, '2026-07-14 20:00:14.516167+09', '2026-07-24 00:22:38.22281+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (77, 1, 'SAVING', 'FSS:SAVING:020000:0017801:1001303001005', '토스뱅크 아이 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 적금 가입 시 설정되는 월 단위 자동이체를 모두 성공하는 경우 : 연 2.50% 제공

· 토스뱅크 아이 통장을 보유한 15세 이하 실명의 개인

· 1인 1계좌 
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', '가입방법: 스마트폰
우대조건: 월 단위 자동이체 성공 시 연 2.50% 제공
가입대상: 토스뱅크 아이 통장을 보유한 15세 이하 실명의 개인
유의사항: 1인 1계좌, 가입금액 0원, 우대금리는 만기 해지 시 제공', '스마트폰', '· 토스뱅크 아이 통장을 보유한 15세 이하 실명의 개인', '· 1인 1계좌 
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', NULL, '2026-07-14 20:00:14.801828+09', '2026-07-21 16:38:12.935002+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (75, 1, 'SAVING', 'FSS:SAVING:020000:0017801:1001303001003', '토스뱅크 굴비 적금', '스마트폰

· 만기 후 1개월 이내 : 만기시점 기본금리 X 50% 
· 만기 후 1개월 초과 3개월 이내 : 만기시점 기본금리 X 20% 
· 만기 후 3개월 초과 : 연 0.10%

· 만기 해지 시 : 연 2.50% 제공

· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인

· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', '가입방법: 스마트폰
우대조건: 만기 해지 시 연 2.50% 제공
가입대상: 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인
유의사항: 1인 1계좌 (이벤트 등으로 추가 개설 가능), 가입금액 0원, 우대금리는 만기 해지 시 제공됨', '스마트폰', '· 토스뱅크 통장 또는 토스뱅크 서브 통장을 보유한 실명의 개인', '· 1인 1계좌 (단, 이벤트 등으로 토스뱅크가 복수의 계좌개설을 허용하는 경우 추가 개설 가능)
· 가입금액 : 0원
· 우대금리는 만기 해지하는 경우에만 제공됨', NULL, '2026-07-14 20:00:14.697741+09', '2026-07-22 01:38:42.119616+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (81, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010017:01030500510002', 'LIVE정기예금', '영업점,인터넷

- 만기후1년내: 가입기간별 일반정기예금이율 월이자지급식 x 50%,
- 만기후1년초과:가입기간별 일반정기예금이율 월이자지급식 x 20%

*우대이율
가. 3~5개월 특판우대이율 : 0.70%
나. 6~11개월 특판 우대이율: 0.60%
다. 12개월 특판 우대이율 : 0.45%

제한없음

1. 가입금액 :
   1천만원 이상
2. 가입기간 : 
1개월 이상 60개월 이하(일단위)
3. 월이자지급식/만기일시지급식', '가입금액 1천만원 이상, 가입기간 1개월 이상 60개월 이하. 월이자지급식 또는 만기일시지급식으로 가입 가능합니다.', '영업점,인터넷', '제한없음', '1. 가입금액 :
   1천만원 이상
2. 가입기간 : 
1개월 이상 60개월 이하(일단위)
3. 월이자지급식/만기일시지급식', NULL, '2026-07-14 20:01:02.243546+09', '2026-07-24 00:22:57.996317+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (80, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010016:10511008001166004', 'iM함께예금', '영업점,인터넷,스마트폰

만기 후 1개월 미만 경과 : 약정이자율 x 50%
만기 후 3개월 미만 경과 : 약정이자율 x 25% 
만기 후 3개월 이상 경과 : 약정이자율 x 10%

* 최고우대금리: 연0.45%p
- 전월 총수신 평잔 30만원 이상 또는 상품 가입 전 첫만남플러스통장 보유
- 상품 가입 전 당행 주택청약상품 보유
- 신규일 "iM함께적금" 동시 가입 및 만기(12회 불입) 보유 
- 당행 오픈뱅킹서비스에 다른 은행 계좌 등록
각 연0.10%p                       
* 해당 상품을 인터넷/모바일뱅킹을 통해 가입: 연0.05%p

실명의 개인 및 개인사업자

계좌당 가입 최저한도 : 100만원', '가입방법: 영업점, 인터넷, 스마트폰
우대조건: 전월 총수신 평잔 30만원 이상 또는 첫만남플러스통장 보유, 주택청약상품 보유, iM함께적금 동시 가입 및 만기 보유, 오픈뱅킹서비스에 다른 은행 계좌 등록 시 각 연0.10%p, 인터넷/모바일뱅킹 가입 시 연0.05%p
가입대상: 실명의 개인 및 개인사업자
유의사항: 만기 후 경과 기간에 따라 약정 이자율의 50%, 25%, 10% 적용', '영업점,인터넷,스마트폰', '실명의 개인 및 개인사업자', '계좌당 가입 최저한도 : 100만원', NULL, '2026-07-14 20:01:02.226028+09', '2026-07-24 00:22:57.965385+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (82, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010017:01030500560002', '더(The) 특판 정기예금', '인터넷,스마트폰

- 만기후1년내: 가입기간별 일반정기예금이율 월이자지급식x 50%,
- 만기후1년초과:가입기간별 일반정기예금이율 월이자지급식 x 20%

* 우대이율 (최대 1.35%p)
가. 모바일뱅킹 금융정보 및 혜택알림 동의 우대이율 : 0.10%p
나. 신규고객 또는 정기예금 중도해지고객 우대이율 0.75%p
다. 특판우대이율 : 0.50%p(24개월 0.85%p)

실명의 개인

1. 가입금액 : 1백만원 이상 제한없음 (원단위)
2. 가입기간 : 1개월, 3개월, 6개월, 1년, 2년, 3년
3. 이자지급방식 : 만기일시지급식', '가입방법: 인터넷, 스마트폰
우대조건: 모바일뱅킹 금융정보 및 혜택알림 동의, 신규고객 또는 정기예금 중도해지고객, 특판우대이율
가입대상: 실명의 개인
유의사항: 만기후1년내: 가입기간별 일반정기예금이율 월이자지급식x 50%, 만기후1년초과:가입기간별 일반정기예금이율 월이자지급식 x 20%', '인터넷,스마트폰', '실명의 개인', '1. 가입금액 : 1백만원 이상 제한없음 (원단위)
2. 가입기간 : 1개월, 3개월, 6개월, 1년, 2년, 3년
3. 이자지급방식 : 만기일시지급식', NULL, '2026-07-14 20:01:02.299349+09', '2026-07-24 00:22:58.039288+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (113, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0015130:10-01-20-388-0002', '카카오뱅크 정기예금', '스마트폰

- 만기 후 1개월 이내 : 가입(또는 자동연장)시점 기본금리x50%
- 만기 후 1개월초과 3개월 이내 : 가입(또는 자동연장)시점 기본금리x30%
- 만기 후 3개월 초과 : 0.20%

※복잡한 우대조건 없이 가입가능한 정기예금

만 14세 이상의 실명의 개인

1. 가입방법 : 스마트폰
2. 가입금액 : 100만원 이상(원단위)
3. 가입기간 : 1개월 이상 ~ 36개월 이하(월, 일단위 지정 가능)', NULL, '스마트폰', '만 14세 이상의 실명의 개인', '1. 가입방법 : 스마트폰
2. 가입금액 : 100만원 이상(원단위)
3. 가입기간 : 1개월 이상 ~ 36개월 이하(월, 일단위 지정 가능)', NULL, '2026-07-14 20:01:41.657885+09', '2026-07-24 00:24:44.681099+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (95, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010024:21001280', 'The든든예금(시즌2)', '스마트폰

만기 후 1개월 이내: 일반정기예금 기본이율 Ⅹ50%
만기 후 1개월 초과: 일반정기예금 기본이율 Ⅹ20%

①마케팅동의 및 모바일메시지 수신동의 0.05%
②신규고객 우대(최근 12개월 신규이력·해지이력 미보유) 0.10%
③이벤트금리(비대면금리) 최대 1.40% (3개월 0.55% / 6, 9개월 0.90% / 12개월 1.40%)

개인

1. 이 예금의 계약기간은 3개월, 6개월, 9개월, 12개월로 한다.
2. 가입좌수 제한없으며, 가입금액은 1인당 최소 100만원 이상 10억원 이하이다.', '가입방법: 스마트폰
우대조건: 마케팅동의 및 모바일메시지 수신동의, 신규고객 우대, 이벤트금리(비대면금리)
가입대상: 개인
유의사항: 만기 후 1개월 이내 일반정기예금 기본이율의 50% 지급, 만기 후 1개월 초과 시 20% 지급. 가입금액은 1인당 최소 100만원 이상 10억원 이하.', '스마트폰', '개인', '1. 이 예금의 계약기간은 3개월, 6개월, 9개월, 12개월로 한다.
2. 가입좌수 제한없으며, 가입금액은 1인당 최소 100만원 이상 10억원 이하이다.', NULL, '2026-07-14 20:01:18.674256+09', '2026-07-24 00:23:16.011657+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (96, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010024:21001285', 'The파트너예금', '스마트폰

만기 후 1개월 이내: 일반정기예금 기본이율 Ⅹ50%
만기 후 1개월 초과: 일반정기예금 기본이율 Ⅹ20%

①경남은행 거래기간 5년이상 + 마케팅동의 고객 0.20%
②급여, 연금, 가맹점대금 입금 시 0.10%
③당행 카드 결제실적 보유 시 0.10%

개인

1. 이 예금의 계약기간은 3개월, 6개월, 12개월, 24개월로 한다.
2. 가입좌수 제한없으며, 가입금액은 1인당 최소 100만원 이상 10억원 이하이다.', '가입방법: 스마트폰
우대조건: 경남은행 거래기간 5년 이상 + 마케팅 동의 시 0.20%, 급여/연금/가맹점대금 입금 시 0.10%, 당행 카드 결제 실적 보유 시 0.10%
가입대상: 개인
유의사항: 만기 후 1개월 이내 기본이율의 50%, 1개월 초과 시 20% 적용. 가입금액은 100만원 이상 10억원 이하.', '스마트폰', '개인', '1. 이 예금의 계약기간은 3개월, 6개월, 12개월, 24개월로 한다.
2. 가입좌수 제한없으며, 가입금액은 1인당 최소 100만원 이상 10억원 이하이다.', NULL, '2026-07-14 20:01:18.714577+09', '2026-07-24 00:23:16.041195+09');
INSERT INTO public.product (id, source_id, type, product_code, product_name, content, content_summary, join_method, eligibility_text, caution_text, recruitment_period, created_at, updated_at) VALUES (98, 1, 'DEPOSIT', 'FSS:DEPOSIT:020000:0010026:01211310130', 'IBK더굴리기통장(실세금리정기예금)', '인터넷,스마트폰

만기일 당시 정기예금 만기후 이자율 적용 -1개월 이내: 만기일 당시 계약기간별 고시금리×50% -1월 초과 6개월 이내: 만기일 당시 계약기간별 고시금리×30% -6개월 초과: 만기일 당시 계약기간별 고시금리×20%

없음

실명의 개인
(개인사업자 제외)

계좌 수 제한 없으며, 최소 1백만원 이상 납입한도 제한 없음', '가입방법: 인터넷, 스마트폰
유의사항: 만기 후 이자율 적용 조건 상세 확인 필요', '인터넷,스마트폰', '실명의 개인
(개인사업자 제외)', '계좌 수 제한 없으며, 최소 1백만원 이상 납입한도 제한 없음', NULL, '2026-07-14 20:01:18.822448+09', '2026-07-24 00:23:16.094537+09');


--
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.product_id_seq', 115, true);


--
-- PostgreSQL database dump complete
--
