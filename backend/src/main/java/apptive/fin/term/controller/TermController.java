package apptive.fin.term.controller;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.term.dto.TermAgreementResponseDto;
import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.service.TermService;
import apptive.fin.term.dto.UserTermRequestDto;
import apptive.fin.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/term")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    // 로그인 유저 기준 약관 조회
    @GetMapping
    public List<TermResponseDto> getTerms(@AuthenticationPrincipal AuthUserDetails authUserDetails){
        return termService.getTermsForUser(authUserDetails.getId());
    }

    // 약관 동의
    @PostMapping
    public TermAgreementResponseDto saveTermAgreementResults(@AuthenticationPrincipal AuthUserDetails authUserDetails,
                           @Valid @RequestBody UserTermRequestDto request){
        UserRole userRole = termService.saveTermAgreementResults(authUserDetails.getId(), request);
        return new TermAgreementResponseDto(userRole);
    }


}
