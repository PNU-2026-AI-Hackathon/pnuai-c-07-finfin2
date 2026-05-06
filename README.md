# FIN
청년들의 금융(Finance) 고민을 끝(Finish)내다.

---

## 1. 기술스택
- Backend : Spring Boot / OAuth2
- Database : PostgreSQL
- Infra : Docker / Docker Compose

## 2. 기능 요약
- OAuth2 로그인(Google, Kakao)
- JWT 기반 인증
- 사용자 / 약관 / 카테고리 관리 
- 동적 입력 로직
- 사용자 맞춤 금융 상품 추천

## 3. API 명세
### /auth
| 기능      | Method | API Path   |
| ------- | ------ | ---------- |
| 로그아웃    | POST   | `/logout`  |
| 토큰 리프레시 | POST   | `/refresh` |

### /oauth2
| 기능         | Method | API Path                |
| ---------- | ------ | ----------------------- |
| 구글 계정 로그인  | GET    | `/authorization/google` |
| 카카오 계정 로그인 | GET    | `/authorization/kakao`  |

### /user
| 기능       | Method | API Path |
| -------- | ------ | -------- |
| 유저 정보 조회 | GET    | `/me`    |
| 유저 정보 수정 | PATCH  | `/me`    |

### /term
| 기능       | Method | API Path |
| -------- | ------ | -------- |
| 약관 목록 조회 | GET    | `/`      |
| 약관 동의    | POST   | `/agree` |

### /category
| 기능            | Method | API Path        |
|---------------|--------|----------------|
| 카테고리 목록 조회 | GET    | `/api/categories` |

### /search
| 기능 | Method | API Path |
|------|--------|----------|
| 동적 폼 조회 | POST | `/search/dynamic-form` |
| 맞춤 상품 추천 | POST | `/search/products` |

## 4. 개발현황
Update : 2026/05/04

| 도메인       | 진행상황 | 비고 |
| -------- | ------ | -------- |
| Auth | 완료    | OAuth2 기반 SNS 로그인, JWT(Access Token + Refresh Token)     |
| User   | 완료   | 유저 정보 조회 및 수정 |
| Term   | 완료   | 약관 조회 및 약관 동의 기록 , 버전 관리 |
|Category   | 완료   | 키워드 데이터 저장 및 전달 |
| Search  | 진행 중 | 사용자 맞춤 적합도 계산 및 사용자 적합도 순과 금리 순 정렬|
| data | 진행 중 | 금감원, 온통청년 API 데이터 전처리 및 저장 |