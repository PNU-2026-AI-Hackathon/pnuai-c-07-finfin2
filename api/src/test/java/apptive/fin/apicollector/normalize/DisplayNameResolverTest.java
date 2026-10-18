package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.DisplayNameResolver.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DisplayNameResolverTest {

    private final DisplayNameResolver resolver = new DisplayNameResolver();

    // 단독(겹치지 않는) FSS 상품: 끝 괄호는 떼고, 중간 브랜드 괄호는 보존한다.
    // (입력은 이미 collapseWhitespace 된 original_name이라 개행은 없다.)
    @ParameterizedTest
    @CsvSource({
            "JB 다이렉트적금(자유적립식),JB 다이렉트적금",
            "KB국민프리미엄적금(정액),KB국민프리미엄적금",
            "The든든예금(시즌2),The든든예금",
            "IBK평생한가족통장(실세금리정기예금),IBK평생한가족통장",
            "제주Dream 정기예금 (개인/만기 지급식),제주Dream 정기예금",
            "헤이(Hey)적금 (자유적립식),헤이(Hey)적금",
            "더(The) 특판 정기예금,더(The) 특판 정기예금",
            "헤이(Hey)정기예금,헤이(Hey)정기예금",
    })
    void stripsTrailingParenFromFssProductWithoutCollision(String original, String expectedDisplay) {
        Map<Long, String> result = resolver.resolve(List.of(new Item(1L, Source.FSS, original)));

        assertThat(result).containsEntry(1L, expectedDisplay);
    }

    @Test
    void keepsParenWhenTwoFssProductsCollapseToSameName() {
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.FSS, "A적금(자유적립식)"),
                new Item(2L, Source.FSS, "A적금(정액적립식)")
        ));

        assertThat(result)
                .containsEntry(1L, "A적금(자유적립식)")
                .containsEntry(2L, "A적금(정액적립식)");
    }

    @Test
    void keepsFssParenButLeavesManualNameWhenTheyCollide() {
        // FSS "A적금(자유)" 를 떼면 수기 "A적금" 과 겹친다 → FSS만 괄호 유지, 수기는 그대로.
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.FSS, "A적금(자유적립식)"),
                new Item(2L, Source.ONTONG, "A적금")
        ));

        assertThat(result)
                .containsEntry(1L, "A적금(자유적립식)")
                .containsEntry(2L, "A적금");
    }

    @Test
    void doesNotStripManualProductNames() {
        // FSS가 아닌 소스는 끝 괄호가 있어도 원본 그대로 둔다.
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.ONTONG, "청년내일저축계좌(2024)")
        ));

        assertThat(result).containsEntry(1L, "청년내일저축계좌(2024)");
    }

    @Test
    void keepsBaseWhenDuplicateOriginalsAreIdentical() {
        // 동일 원본 2건(중복 데이터): 괄호를 되돌려도 같으므로 그냥 base(괄호 제거) 유지.
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.FSS, "A적금(자유적립식)"),
                new Item(2L, Source.FSS, "A적금(자유적립식)")
        ));

        assertThat(result)
                .containsEntry(1L, "A적금")
                .containsEntry(2L, "A적금");
    }

    @Test
    void keepsParenOnlyForStrippedSideWhenCollidingWithBareName() {
        // "A적금(자유)"(FSS) 와 "A적금"(FSS, 괄호 없음)이 base "A적금" 으로 겹침.
        // 괄호가 실제로 떨어진 쪽만 원본 유지, 원래 괄호 없던 쪽은 그대로.
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.FSS, "A적금(자유적립식)"),
                new Item(2L, Source.FSS, "A적금")
        ));

        assertThat(result)
                .containsEntry(1L, "A적금(자유적립식)")
                .containsEntry(2L, "A적금");
    }

    @Test
    void handlesNullBlankAndEmptyOriginalNames() {
        Map<Long, String> result = resolver.resolve(List.of(
                new Item(1L, Source.FSS, ""),
                new Item(2L, Source.ONTONG, "   "),
                new Item(3L, Source.FSS, null)
        ));

        assertThat(result)
                .containsEntry(1L, "")
                .containsEntry(2L, "   ")
                .containsEntry(3L, null);
    }
}
