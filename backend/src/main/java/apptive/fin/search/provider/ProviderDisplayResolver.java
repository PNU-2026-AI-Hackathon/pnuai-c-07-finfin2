package apptive.fin.search.provider;

import apptive.fin.search.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
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
