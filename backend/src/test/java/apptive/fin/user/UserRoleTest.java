package apptive.fin.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void 추천기능은_추천사용자와_관리자만_사용할수있다() {
        assertThat(UserRole.BEFORE_AGREED.canUseRecommendation()).isFalse();
        assertThat(UserRole.RECOMMENDATION.canUseRecommendation()).isTrue();
        assertThat(UserRole.ADMIN.canUseRecommendation()).isTrue();
    }
}
