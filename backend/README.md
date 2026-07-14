# Y-FIN.
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
| 기능       | Method | API Path |
|----------|--------|----------|
| 동적 폼 조회  | POST   | `/search/dynamic-form` |
| 맞춤 상품 추천 | POST   | `/search/products` |
| 상품명 검색   | GET    | `/search/products` |

### /favorites
| 기능       | Method | API Path |
|----------|--------|----------|
| 찜 목록 조회 (프로필 기반) | POST   | `/favorites/list` |
| 찜 목록 조회 (기본) | GET    | `/favorites` |
| 찜 추가     | POST   | `/favorites` |
| 찜 삭제     | DELETE | `/favorites/{productPropertyId}` |
| 찜 여부 확인  | GET    | `/favorites/{productPropertyId}/status` |
| 찜 개수 조회  | GET    | `/favorites/count` |

### /calculator
| 기능              | Method | API Path |
|-----------------|--------|----------|
| 적금/예금 최종 수익 계산기 | POST   | `/calculator` |


## 4. 개발현황
Update : 2026/07/13

| 도메인       | 진행상황 | 비고 |
| -------- |------| -------- |
| Auth | 완료   | OAuth2 기반 SNS 로그인, JWT(Access Token + Refresh Token)     |
| User   | 완료   | 유저 정보 조회 및 수정 |
| Term   | 완료   | 약관 조회 및 약관 동의 기록 , 버전 관리 |
| Category   | 완료   | 키워드 데이터 저장 및 전달 |
| Search  | 완료   | 사용자 맞춤 적합도 계산 및 사용자 적합도 순과 금리 순 정렬|
| Calculator | 완료   | 프로필 기반 금리/적합도 재계산, 정부·은행 상품별 수익률 산출 |
| MyFin | 완료   | 찜 목록 관리 (추가/삭제/조회), 최신순 정렬, 최대 20개 제한 |
| data | 진행 중 | 금감원, 온통청년 API 데이터 전처리 및 저장 |
