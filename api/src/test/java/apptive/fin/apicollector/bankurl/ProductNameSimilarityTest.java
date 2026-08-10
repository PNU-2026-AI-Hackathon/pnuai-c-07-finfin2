package apptive.fin.apicollector.bankurl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProductNameSimilarityTest {

    private final ProductNameSimilarity similarity = new ProductNameSimilarity();

    @Test
    void identicalNamesHaveFullSimilarity() {
        assertThat(similarity.score("청년내일적금", "청년내일적금")).isEqualTo(1.0);
    }

    @ParameterizedTest
    @MethodSource("similarityCases")
    void matchesPythonThresholdClassification(String query, String candidate, boolean expected) {
        assertThat(similarity.score(query, candidate) >= 0.80).isEqualTo(expected);
    }

    private static Stream<Arguments> similarityCases() {
        return Stream.of(
                Arguments.of("청년내일적금", "청년 내일 적금", true),
                Arguments.of("청년내일적금", "KB 청년 내일 적금", true),
                Arguments.of("청년내일적금", "신한 청년내일적금", true),
                Arguments.of("청년내일적금", "청년내일적금(우대형)", true),
                Arguments.of("청년내일적금", "청년내일 적금 상품", true),
                Arguments.of("청년내일적금", "NH청년내일적금", true),
                Arguments.of("청년도약계좌", "청년 도약 계좌", true),
                Arguments.of("청년도약계좌", "KB청년도약계좌", true),
                Arguments.of("청년희망적금", "신한 청년희망 적금", true),
                Arguments.of("내일채움공제", "내일 채움 공제", true),
                Arguments.of("청년내일적금", "청년희망적금", false),
                Arguments.of("청년내일적금", "청년도약계좌", false),
                Arguments.of("청년내일적금", "청년내일저축계좌", false),
                Arguments.of("청년내일적금", "내일채움공제", false),
                Arguments.of("청년희망적금", "청년도약계좌", false),
                Arguments.of("청년내일적금", "주택청약종합저축", false),
                Arguments.of("청년내일적금", "직장인신용대출", false),
                Arguments.of("청년도약계좌", "정기예금", false),
                Arguments.of("청년희망적금", "마이너스통장대출", false),
                Arguments.of("청년내일적금", "청년우대형청약통장", false),
                Arguments.of("청년내일적금", "내일을위한정기예금", false),
                Arguments.of("청년도약계좌", "청년전세자금대출", false),
                Arguments.of("청년희망적금", "희망플러스신용대출", false),
                Arguments.of("", "청년내일적금", false),
                Arguments.of("청년내일적금", "", false),
                Arguments.of("", "", false)
        );
    }
}
