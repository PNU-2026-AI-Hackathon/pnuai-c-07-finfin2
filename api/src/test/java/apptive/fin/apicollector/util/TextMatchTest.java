package apptive.fin.apicollector.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextMatchTest {

    @Test
    void containsAny_isCaseInsensitiveAndNullSafe() {
        assertThat(TextMatch.containsAny("Salary transfer", "salary")).isTrue();
        assertThat(TextMatch.containsAny("급여 이체", "월급", "급여")).isTrue();
        assertThat(TextMatch.containsAny("no match", "카드")).isFalse();
        assertThat(TextMatch.containsAny(null, "x")).isFalse();
        assertThat(TextMatch.containsAny("   ", "x")).isFalse();
    }
}
