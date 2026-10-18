package apptive.fin.apicollector.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

// 테스트용 일회용 Postgres 컨테이너. @ServiceConnection이 접속 정보를 자동 주입해 dev DB 접속을 차단한다.
// PostgreSQLContainer가 pgvector/pgvector 이미지를 네이티브 지원한다.
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestContainerConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return new PostgreSQLContainer("pgvector/pgvector:pg16");
    }
}
