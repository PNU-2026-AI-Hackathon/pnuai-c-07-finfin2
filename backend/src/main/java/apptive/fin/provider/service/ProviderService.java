package apptive.fin.provider.service;

import apptive.fin.provider.BankDisplayRegistry;
import apptive.fin.provider.dto.BankProviderDto;
import apptive.fin.provider.repository.ProviderRepository;
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
    private final BankDisplayRegistry bankDisplayRegistry;

    public List<BankProviderDto> getBankProviders() {
        return providerRepository.findBySource_CodeOrderByNameAsc(FSS_SOURCE).stream()
                .map(provider -> new BankProviderDto(
                        provider.getCode(),
                        bankDisplayRegistry.displayNameOrFallback(provider.getCode(), provider.getName()),
                        bankDisplayRegistry.categoryOrFallback(provider.getCode()),
                        bankDisplayRegistry.region(provider.getCode())
                ))
                .toList();
    }
}
