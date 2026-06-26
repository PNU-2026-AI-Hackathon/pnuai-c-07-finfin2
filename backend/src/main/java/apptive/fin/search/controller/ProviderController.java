package apptive.fin.search.controller;

import apptive.fin.search.dto.BankProviderDto;
import apptive.fin.search.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping("/banks")
    public List<BankProviderDto> getBankProviders() {
        return providerService.getBankProviders();
    }
}
