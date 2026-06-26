package apptive.fin.search;

import apptive.fin.search.dto.BankProviderDto;
import apptive.fin.search.entity.Provider;
import apptive.fin.search.repository.ProviderRepository;
import apptive.fin.search.service.ProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    void 은행_provider_목록은_FSS_provider_code와_이름을_반환한다() {
        given(providerRepository.findBySource_CodeOrderByNameAsc("FSS"))
                .willReturn(List.of(provider("0010927", "국민은행"), provider("0011625", "신한은행")));

        List<BankProviderDto> result = providerService.getBankProviders();

        assertThat(result).containsExactly(
                new BankProviderDto("0010927", "국민은행"),
                new BankProviderDto("0011625", "신한은행")
        );
    }

    private Provider provider(String code, String name) {
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "code", code);
        ReflectionTestUtils.setField(provider, "name", name);
        return provider;
    }
}
