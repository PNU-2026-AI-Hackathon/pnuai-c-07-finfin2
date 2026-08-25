# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

This is **Y-FIN** (청년 금융상품 추천 서비스). Three top-level modules:

- `backend/` — the main Spring Boot **API server** (`apptive.fin`). OAuth2/JWT auth, product search & recommendation, calculator, favorites (myfin). **This is the primary application.**
- `api/` — a Spring **Batch data collector** (`apptive.fin.apicollector`, gradle description `api-collector`). Fetches raw financial products from external sources (금감원 FSS 공시, 정부 정책상품), normalizes them (optionally via an LLM), and writes them into the products tables the backend reads.
- `frontend/` — React + Vite web client. This guide covers the `backend/` and `api/` Java modules.

`backend/` and `api/` are **separate Gradle projects** (each has its own `gradlew`, `build.gradle`, `settings.gradle`) but **share one PostgreSQL database**. They are two halves of one system: `api/` populates the product catalog, `backend/` serves it.

## Build / test / run

Both modules use the Gradle wrapper (Java 21 toolchain, Spring Boot 4.x). On Windows run `./gradlew.bat` from inside `backend/` or `api/`. All commands below are per-module — `cd` into the module first.

```bash
# backend/  — main API server
./gradlew.bat bootRun            # run the server
./gradlew.bat test               # full test suite
./gradlew.bat test --tests "apptive.fin.search.service.MatchScoreServiceTest"   # single test class
./gradlew.bat test --tests "*.MatchScoreServiceTest.someMethod"                 # single test method
./gradlew.bat resetDb            # devtool: drop+recreate local dev DB schema from schema.sql + seed (custom JavaExec task)

# api/  — collector
./gradlew.bat bootRun            # runs the batch job on startup (batch.job.enabled=true)
./gradlew.bat test

# 은행 상품 URL 수집용 Chromium 설치
./gradlew.bat playwrightInstall

# 기존 Python 스크래퍼와 Java 결과(URL + PASS/WARN/FAIL) 동등성 검증
./gradlew.bat bankUrlParityTest -PpythonScraperDir="C:\\path\\to\\fin_web_scrape"
```

### Local database (required for both)

Both `application-dev.yml` files point at `jdbc:postgresql://localhost:5678/database` (user `user` / pw `password`). Start it from `backend/docker-compose.yml` — the image is `pgvector/pgvector:pg16` (vector extension, PostgreSQL 16). Note: **no volume is mounted**, so data is lost on container removal.

Each module reads secrets from a git-ignored `.env` (Spring `config.import: optional:file:.env[.properties]`). `backend/.env` needs the OAuth client IDs/secrets and `JWT_SECRET`; `api/.env` needs `FSS_API_KEY`, `GEMINI_API_KEY`, etc.

### Schema management is manual — do not expect auto-DDL

Both apps run JPA with `ddl-auto: validate` (they never create tables). Schema comes from `schema.sql`:
- **`api/`** dev profile uses `sql.init.mode: always` → it re-runs `schema.sql` on boot.
- **`backend/`** dev profile uses `sql.init.mode: never` → it validates entities against whatever is already in the DB. **If you change `backend/src/main/resources/schema.sql`, you must apply the change to the running `localhost:5678` DB by hand (or via `./gradlew.bat resetDb`), or `contextLoads()` will fail on startup.** The two modules' `schema.sql` files describe the same shared tables — keep entity changes consistent across both.

## Backend architecture (`backend/`)

Package-by-feature under `apptive.fin`: `auth`, `user`, `term`, `category`, `search`, `calculator`, `myfin` (favorites/찜), `provider`, plus `global` (cross-cutting). Each feature package is layered `controller / service / dto / entity / repository`.

- **Auth**: OAuth2 login (Google, Kakao) → issues JWT (access + refresh). `global/config/SecurityConfig` is stateless (no session); `JwtAuthFilter` runs before the username/password filter. Public paths: `/auth/**`, `/search/**`, `/oauth2/**`, `POST /users`; everything else requires auth. Swagger (`/swagger-ui/**`, `/v3/api-docs/**`) is permitted **only** under `dev`/`test` profiles and denied otherwise. CORS allowed origin comes from `app.frontend.url`.
- **Search (recommendation core)**: `SearchService.search()` orchestrates a pipeline of focused services — `ResolveKeywordService` (request options → `ResolvedKeywords`), `EligibilityFilterService` (who can subscribe), `MatchScoreService` (relevance score per product/property), `RateCalculatorService` (achievable interest rate). Results are split into government vs. bank products and returned as two rankings (match-score order + rate order), gated by tab availability (`tabB` needs a logged-in user with detailed profile options). Products are collapsed to the best `(Product, ProductProperty)` pair per product. `BankMaxInterestPolicy` owns the pure top-rate threshold and qualification rules; `collapseToBestPerProduct` remains a static ranking helper for unit testing.
- **Errors**: every domain defines an `ErrorCode` enum (e.g. `SearchErrorCode`, `AuthErrorCode`) implementing the `global/error/ErrorCode` interface (`codePrefix + errNum`). Throw `BusinessException(errorCode)`; `GlobalExceptionHandler` (`@RestControllerAdvice`) maps it to `ErrorResponseDto`. Add new failure modes as enum constants, not ad-hoc exceptions.
- **Seed data**: `data.sql` (median incomes) and `seed/*.sql` (providers, products, keywords, rates) populate reference/product data.

### Tests (`backend/`)

Integration tests extend `apptive.fin.support.IntegrationTestSupport` (`@SpringBootTest`, `@ActiveProfiles({"dev","test"})`, `@Import(PostgresTestContainerConfig)`). They run against a **Testcontainers Postgres** — the `test` profile overrides only DB keys, inheriting the rest from `dev`, so the local dev DB is never touched. SQL fixtures live in `src/test/resources/sql/`. Pure-logic services also have plain JUnit unit tests (no Spring context).

## Collector architecture (`api/`)

A single Spring Batch job `financialProductSyncJob` runs on boot. It is driven entirely by the `collector.*` config (`CollectorProperties`, a `@ConfigurationProperties` record) in `application-dev.yml`:

- **`source`** (`FSS` | `ONTONG` | `ALL`) — `SourceDecider` branches the job into `fssSyncFlow`, `ontongYouthSyncFlow`, or `allSyncFlow`. Note: the 온통청년 API is deprecated; `ONTONG`/manual products now come from `FetchManualRawTasklet` reading `manual-products.json`.
- **`mode`** (`SYNC` | `NORMALIZE_ONLY`) — `NORMALIZE_ONLY` skips all `Fetch*RawTasklet`s and re-normalizes existing raw rows without hitting external APIs or deactivating missing products.
- Each flow is: **fetch raw → normalize → deactivate-missing**. Fetch tasklets store `ProductRaw`; the normalize step reads raw rows and runs them through source-specific `ProductNormalizer`s (`FssProductNormalizer`, `OntongYouthProductNormalizer`, `ManualProductNormalizer`) → `ProductDraft` → written to `product`/`product_property`/keyword/rate tables.
- FSS/ALL flows then run `bankProductUrlStep`. It uses headless Chromium to resolve product-specific URLs for active FSS bank products and updates only PASS results. WARN/FAIL keep the previous URL and do not fail the job. Configure it with `BANK_PRODUCT_URL_ENABLED`, `BANK_URL_CONCURRENCY`, `BANK_URL_TIMEOUT_SECONDS`, and `BANK_URL_RETRIES`.
- **LLM enrichment**: when `collector.llm.enabled=true`, the FSS normalize step switches to an **async chunked** pipeline (`AsyncProductItemProcessor`/`Writer` on a fixed thread pool sized by `llm.max-concurrency`) that calls Gemini (`GeminiLlmProviderClient`) to enrich drafts. Results are cached in an `llm_enrichment_cache` table keyed by prompt/schema version. When disabled, normalization is synchronous.

### Cache/version invalidation knobs (important for re-processing)

Because normalized output is persisted, code changes to normalization logic **do not** re-apply to existing rows automatically. To force reprocessing:
- **`collector.normalizer-version`** — bump it to re-run normalization over the existing DB.
- **`collector.llm.prompt-version` / `llm.schema-version`** — gate the LLM enrichment cache; bump to invalidate cached LLM results.
- Typical re-normalize run: bump `normalizer-version`, then `bootRun` with `mode=NORMALIZE_ONLY` and `llm.enabled=true`.

## Conventions

- Java 21, Lombok, records for DTOs/config, package-by-feature. Both modules share the `apptive.fin[.apicollector]` root and the same layered structure.
- Korean is used freely in comments, commit messages, and user-facing error messages — match the surrounding style.
- Commit message style in history: `type : 한국어 설명` (e.g. `fix : 대출상품 관심 키워드 부착 제외`, `feat : ...`, `test : ...`).
