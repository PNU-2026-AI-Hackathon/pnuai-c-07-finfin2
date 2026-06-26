package apptive.fin.search.service;

import apptive.fin.search.dto.BankProviderDto;
import apptive.fin.search.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderService {

    private static final String FSS_SOURCE = "FSS";

    private final ProviderRepository providerRepository;

    public List<BankProviderDto> getBankProviders() {
        return providerRepository.findBySource_CodeOrderByNameAsc(FSS_SOURCE).stream()
                .map(provider -> new BankProviderDto(provider.getCode(), provider.getName()))
                .toList();
    }
}
