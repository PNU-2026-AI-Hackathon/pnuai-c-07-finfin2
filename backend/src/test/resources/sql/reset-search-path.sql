-- seed/*.sql(pg_dump)이 set_config('search_path', '', false)로 세션 search_path를 비워두므로,
-- 같은 @Sql 커넥션에서 복원해 풀에 반환한다. 미복원 시 이후 비정규화 쿼리(Hibernate 등)가 깨진다.
SET search_path TO public;
