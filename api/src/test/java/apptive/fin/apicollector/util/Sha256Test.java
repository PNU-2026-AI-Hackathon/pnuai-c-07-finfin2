package apptive.fin.apicollector.util;

import org.junit.jupiter.api.Test;

import apptive.fin.apicollector.global.util.Sha256;

import static org.assertj.core.api.Assertions.assertThat;

class Sha256Test {

    @Test
    void hex_matchesKnownVectorAndIsStable() {
        // 표준 SHA-256("") 벡터
        assertThat(Sha256.hex(""))
                .isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        assertThat(Sha256.hex("abc"))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertThat(Sha256.hex("hello")).isEqualTo(Sha256.hex("hello"));
        assertThat(Sha256.hex("hello")).hasSize(64);
    }
}
