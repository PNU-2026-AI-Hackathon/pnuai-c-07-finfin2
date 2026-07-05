package apptive.fin.provider.controller;

import apptive.fin.provider.dto.BankProviderDto;
import apptive.fin.provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping("/banks")
    public List<BankProviderDto> getBankProviders() {
        return providerService.getBankProviders();
    }
}
