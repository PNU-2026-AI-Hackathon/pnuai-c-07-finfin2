package apptive.fin.auth;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerAuthorizationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void 공개_조회_API는_로그인없이_접근할수있다() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/search/products")
                        .queryParam("searchInput", "청년"))
                .andExpect(status().isOk());
    }

    @Test
    void 공개_검색_POST_API는_로그인없이_컨트롤러까지_도달한다() throws Exception {
        mockMvc.perform(post("/search/dynamic-form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/search/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/search/products/999999/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 보호_API는_비로그인시_401을_반환한다() throws Exception {
        mockMvc.perform(get("/providers/banks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));

        mockMvc.perform(delete("/user/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }

    @Test
    void 추천_API는_약관동의전_사용자에게_403을_반환한다() throws Exception {
        mockMvc.perform(get("/providers/banks")
                        .with(principal(UserRole.BEFORE_AGREED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("A002"));
    }

    @Test
    void 찜과_계산기_API는_약관동의전_사용자에게_403을_반환한다() throws Exception {
        mockMvc.perform(get("/favorites/count")
                        .with(principal(UserRole.BEFORE_AGREED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("A002"));

        mockMvc.perform(post("/calculator")
                        .with(principal(UserRole.BEFORE_AGREED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productPropertyId": 1,
                                  "productType": "DEPOSIT",
                                  "interestRateType": "SINGLE_INTEREST",
                                  "appliedRate": 1.0,
                                  "amount": 10000,
                                  "saveTrm": 12,
                                  "taxType": "GENERAL"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("A002"));
    }

    @Test
    void 추천사용자와_관리자는_추천_API에_접근할수있다() throws Exception {
        mockMvc.perform(get("/providers/banks")
                        .with(principal(UserRole.RECOMMENDATION)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/providers/banks")
                        .with(principal(UserRole.ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    void 약관동의전_사용자는_약관_API에_접근할수있다() throws Exception {
        mockMvc.perform(get("/term")
                        .with(principal(UserRole.BEFORE_AGREED)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(
            statements = "INSERT INTO users (id, name, provider, provider_id, user_role) VALUES (999999, '보안 테스트', 'TEST', 'security-test', 'BEFORE_AGREED')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            statements = "DELETE FROM users WHERE id = 999999",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void 약관동의전_사용자는_본인정보_API에_접근할수있다() throws Exception {
        mockMvc.perform(get("/user/me")
                        .with(principal(999999L, UserRole.BEFORE_AGREED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userRole").value("BEFORE_AGREED"));
    }

    @Test
    @Sql(
            statements = {
                    "INSERT INTO users (id, name, provider, provider_id, user_role) VALUES (999998, '탈퇴 테스트', 'TEST', 'delete-test', 'BEFORE_AGREED')",
                    "INSERT INTO user_profiles (user_id) VALUES (999998)",
                    "INSERT INTO refresh_tokens (user_id, token_hash, is_active, expires_at) VALUES (999998, 'delete-token-hash', true, CURRENT_TIMESTAMP + INTERVAL '1 day')"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            statements = "DELETE FROM users WHERE id = 999998",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void 약관동의전_사용자는_회원탈퇴하고_관련데이터를_파기할수있다() throws Exception {
        mockMvc.perform(delete("/user/me")
                        .with(principal(999998L, UserRole.BEFORE_AGREED)))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = 999998", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_profiles WHERE user_id = 999998", Integer.class)).isZero();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM refresh_tokens WHERE user_id = 999998", Integer.class)).isZero();
    }

    @Test
    void 프로필_API는_약관동의전_사용자를_차단한다() throws Exception {
        mockMvc.perform(get("/user/me/profile")
                        .with(principal(UserRole.BEFORE_AGREED))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("A002"));
    }

    @Test
    void 추천사용자와_관리자는_프로필_API에_접근할수있다() throws Exception {
        mockMvc.perform(get("/user/me/profile")
                        .with(principal(UserRole.RECOMMENDATION)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/user/me/profile")
                        .with(principal(UserRole.ADMIN)))
                .andExpect(status().isOk());
    }

    private RequestPostProcessor principal(UserRole role) {
        return principal(1L, role);
    }

    private RequestPostProcessor principal(Long userId, UserRole role) {
        return user(new AuthUserDetails(userId, role));
    }

}
