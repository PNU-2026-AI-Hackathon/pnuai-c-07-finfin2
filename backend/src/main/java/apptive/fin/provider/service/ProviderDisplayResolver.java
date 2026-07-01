package apptive.fin.provider.service;

import apptive.fin.provider.BankDisplayRegistry;
import apptive.fin.provider.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderDisplayResolver {

    private final BankDisplayRegistry registry;

    public String resolveName(Provider provider) {
        if (provider == null) {
            return null;
        }
        return registry.displayNameOrFallback(provider.getCode(), provider.getName());
    }
}
