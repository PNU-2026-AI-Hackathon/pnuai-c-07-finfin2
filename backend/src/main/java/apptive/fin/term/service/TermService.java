package apptive.fin.term.service;

import apptive.fin.global.error.BusinessException;
import apptive.fin.term.TermErrorCode;
import apptive.fin.term.entity.TermVersion;
import apptive.fin.term.repository.TermRepository;
import apptive.fin.term.entity.UserTermAgreement;
import apptive.fin.term.repository.TermVersionRepository;
import apptive.fin.term.repository.UserTermAgreementRepository;
import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.dto.UserTermRequestDto;
import apptive.fin.user.UserErrorCode;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {

    private final TermRepository termRepository;
    private final TermVersionRepository termVersionRepository;
    private final UserTermAgreementRepository userTermAgreementRepository;
    private final UserRepository userRepository;

    public List<TermResponseDto> getTermsForUser(Long userId) {
        return termRepository.getTermResponseDtosByUserId(userId);
    }

    public boolean didUserAgreeAllRequiredTerms(Long userId) {
        long notAgreedNum = termRepository.getTermResponseDtosByUserId(userId)
                .stream()
                .filter((t)-> t.isRequired() && !t.agreed())
                .count();

        return notAgreedNum == 0;
    }

    @Transactional
    public void saveTermAgreementResults(Long userId, UserTermRequestDto request) {

        Map<Long, Boolean> agreementRequestMap = request.agreements().stream()
                .collect(Collectors.toMap(
                        UserTermRequestDto.TermAgreement::versionId,
                        UserTermRequestDto.TermAgreement::agreed,
                        (oldValue, newValue) -> {
                            throw new BusinessException(TermErrorCode.DUPLICATE_TERM_VERSION_ID);
                        }
                ));

        List<TermVersion> termsToAgree = termVersionRepository.findAllById(agreementRequestMap.keySet());


        if (termsToAgree.size() != agreementRequestMap.size()) {
            throw new BusinessException(TermErrorCode.TERM_ID_MISMATCH);
        }

        Set<Long> availableTermVersionIds = termVersionRepository.findByIsCurrentTrue()
                .stream().map(TermVersion::getId).collect(Collectors.toSet());

        if (!availableTermVersionIds.
                containsAll(termsToAgree.stream().map(TermVersion::getId).toList())
        ) {
            throw new BusinessException(TermErrorCode.TERM_NOT_FOUND);
        }

        User user = userRepository.findById(userId).
                orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Map<Long, UserTermAgreement> existingAgreementsMap = userTermAgreementRepository
                .findAllByUserAndTermVersionIn(user, termsToAgree).stream()
                .collect(Collectors.toMap(
                        uta -> uta.getTermVersion().getId(),
                        uta -> uta
                ));

        List<UserTermAgreement> finalAgreements = new ArrayList<>();
        for (TermVersion termVersion : termsToAgree) {
            Boolean agreed = agreementRequestMap.get(termVersion.getId());

            UserTermAgreement agreement = existingAgreementsMap.getOrDefault(
                    termVersion.getId(),
                    UserTermAgreement.builder().
                            user(user)
                            .termVersion(termVersion)
                            .agreed(false)
                            .agreedAt(null)
                            .build()
            );

            if (agreed) agreement.agree();
            else agreement.disagree();

            finalAgreements.add(agreement);
        }

        userTermAgreementRepository.saveAll(finalAgreements);
    }

}