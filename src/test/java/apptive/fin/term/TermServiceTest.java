package apptive.fin.term;

import apptive.fin.global.error.BusinessException;
import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.dto.UserTermRequestDto;
import apptive.fin.term.entity.Term;
import apptive.fin.term.entity.TermVersion;
import apptive.fin.term.entity.UserTermAgreement;
import apptive.fin.term.repository.TermRepository;
import apptive.fin.term.repository.TermVersionRepository;
import apptive.fin.term.repository.UserTermAgreementRepository;
import apptive.fin.term.service.TermService;
import apptive.fin.user.UserErrorCode;
import apptive.fin.user.UserRole;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TermServiceTest {

    @Mock
    private TermRepository termRepository;

    @Mock
    private TermVersionRepository termVersionRepository;

    @Mock
    private UserTermAgreementRepository userTermAgreementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TermService termService;

    @Captor
    private ArgumentCaptor<Iterable<UserTermAgreement>> agreementsCaptor;

    @Test
    void 활성_필수_약관_버전이_기존_동의보다_높으면_재동의가_필요하다() {
        Long userId = 1L;

        when(termRepository.getTermResponseDtosByUserId(userId)).thenReturn(List.of(
                new TermResponseDto(
                        10L,
                        20L,
                        "SERVICE_TERMS",
                        "서비스 이용약관 v2.0",
                        "content",
                        LocalDateTime.now(),
                        true,
                        false
                )
        ));

        assertThat(termService.didUserAgreeAllRequiredTerms(userId)).isFalse();
    }

    @Test
    void 더_높은_버전이_존재해도_활성화되지_않았다면_재동의가_필요하지_않다() {
        Long userId = 1L;

        when(termRepository.getTermResponseDtosByUserId(userId)).thenReturn(List.of(
                new TermResponseDto(
                        10L,
                        11L,
                        "SERVICE_TERMS",
                        "서비스 이용약관 v1.0",
                        "content",
                        LocalDateTime.now(),
                        true,
                        true
                )
        ));

        assertThat(termService.didUserAgreeAllRequiredTerms(userId)).isTrue();
    }

    @Test
    void saveTermAgreementResults는_versionId로_신규_동의_결과를_저장한다() {
        Long userId = 1L;
        User user = createUser(userId);
        Term requiredTerm = createTerm(10L, "SERVICE_TERMS", true);
        Term optionalTerm = createTerm(11L, "MARKETING_TERMS", false);
        TermVersion requiredVersion = createTermVersion(100L, requiredTerm, 1, 0, true);
        TermVersion optionalVersion = createTermVersion(101L, optionalTerm, 1, 0, true);

        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(requiredVersion.getId(), true),
                new UserTermRequestDto.TermAgreement(optionalVersion.getId(), false)
        ));

        doReturn(List.of(requiredVersion, optionalVersion))
                .when(termVersionRepository)
                .findAllById(any());
        doReturn(List.of(requiredVersion, optionalVersion))
                .when(termVersionRepository)
                .findByIsCurrentTrue();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userTermAgreementRepository.findAllByUserAndTermVersionIn(eq(user), anyList())).thenReturn(List.of());

        termService.saveTermAgreementResults(userId, request);

        verify(userTermAgreementRepository).saveAll(agreementsCaptor.capture());
        List<UserTermAgreement> savedAgreements = StreamSupport.stream(agreementsCaptor.getValue().spliterator(), false)
                .toList();

        assertThat(savedAgreements).hasSize(2);
        assertThat(savedAgreements)
                .extracting(agreement -> agreement.getTermVersion().getId())
                .containsExactly(100L, 101L);
        assertThat(savedAgreements)
                .extracting(UserTermAgreement::isAgreed)
                .containsExactly(true, false);
        assertThat(savedAgreements.get(0).getAgreedAt()).isNotNull();
        assertThat(savedAgreements.get(1).getAgreedAt()).isNull();
    }

    @Test
    void saveTermAgreementResults는_기존_동의_정보를_업데이트한다() {
        Long userId = 1L;
        User user = createUser(userId);
        Term term = createTerm(10L, "SERVICE_TERMS", true);
        TermVersion currentVersion = createTermVersion(100L, term, 2, 0, true);
        UserTermAgreement existingAgreement = UserTermAgreement.builder()
                .user(user)
                .termVersion(currentVersion)
                .agreed(false)
                .agreedAt(null)
                .build();

        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(currentVersion.getId(), true)
        ));

        doReturn(List.of(currentVersion))
                .when(termVersionRepository)
                .findAllById(any());
        doReturn(List.of(currentVersion))
                .when(termVersionRepository)
                .findByIsCurrentTrue();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userTermAgreementRepository.findAllByUserAndTermVersionIn(eq(user), anyList()))
                .thenReturn(List.of(existingAgreement));

        termService.saveTermAgreementResults(userId, request);

        verify(userTermAgreementRepository).saveAll(agreementsCaptor.capture());
        List<UserTermAgreement> savedAgreements = StreamSupport.stream(agreementsCaptor.getValue().spliterator(), false)
                .toList();

        assertThat(savedAgreements).containsExactly(existingAgreement);
        assertThat(existingAgreement.isAgreed()).isTrue();
        assertThat(existingAgreement.getAgreedAt()).isNotNull();
    }

    @Test
    void saveTermAgreementResults는_요청한_versionId와_조회된_버전_수가_다르면_예외를_던진다() {
        Long userId = 1L;
        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(100L, true),
                new UserTermRequestDto.TermAgreement(101L, false)
        ));
        TermVersion foundVersion = createTermVersion(100L, createTerm(10L, "SERVICE_TERMS", true), 1, 0, true);

        doReturn(List.of(foundVersion))
                .when(termVersionRepository)
                .findAllById(any());

        assertThatThrownBy(() -> termService.saveTermAgreementResults(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode()).isEqualTo(TermErrorCode.TERM_ID_MISMATCH)
                );

        verifyNoInteractions(userRepository, userTermAgreementRepository);
    }

    @Test
    void saveTermAgreementResults는_요청에_중복된_versionId가_있으면_예외를_던진다() {
        Long userId = 1L;
        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(100L, true),
                new UserTermRequestDto.TermAgreement(100L, false)
        ));

        assertThatThrownBy(() -> termService.saveTermAgreementResults(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode()).isEqualTo(TermErrorCode.DUPLICATE_TERM_VERSION_ID)
                );

        verifyNoInteractions(termVersionRepository, termRepository, userRepository, userTermAgreementRepository);
    }


    @Test
    void saveTermAgreementResults는_요청한_versionId가_조회된_최신버전에_없으면_예외를_던진다() {
        Long userId = 1L;
        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(105L, true)
        ));
        TermVersion foundVersion = createTermVersion(105L, createTerm(10L, "SERVICE_TERMS", true), 1, 0, true);

        doReturn(List.of(foundVersion))
                .when(termVersionRepository)
                .findAllById(any());

        doReturn(List.of(createTermVersion(100L, createTerm(10L, "SERVICE_TERMS", true), 1, 0, true)))
                .when(termVersionRepository).findByIsCurrentTrue();

        assertThatThrownBy(() -> termService.saveTermAgreementResults(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode()).isEqualTo(TermErrorCode.TERM_NOT_FOUND)
                );

        verifyNoInteractions(userRepository, userTermAgreementRepository);
    }

    @Test
    void saveTermAgreementResults는_사용자가_없으면_예외를_던진다() {
        Long userId = 1L;
        TermVersion version = createTermVersion(100L, createTerm(10L, "SERVICE_TERMS", true), 1, 0, true);
        UserTermRequestDto request = new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(version.getId(), true)
        ));

        doReturn(List.of(version))
                .when(termVersionRepository)
                .findAllById(any());
        doReturn(List.of(version))
                .when(termVersionRepository)
                .findByIsCurrentTrue();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> termService.saveTermAgreementResults(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND)
                );
    }

    private User createUser(Long id) {
        User user = User.builder()
                .name("tester")
                .email("tester@example.com")
                .provider("google")
                .providerId("provider-id")
                .userRole(UserRole.BEFORE_AGREED)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Term createTerm(Long id, String code, boolean required) {
        Term term = Term.builder()
                .code(code)
                .isRequired(required)
                .build();
        ReflectionTestUtils.setField(term, "id", id);
        return term;
    }

    private TermVersion createTermVersion(Long id, Term term, int majorVersion, int minorVersion, boolean current) {
        TermVersion termVersion = BeanUtils.instantiateClass(TermVersion.class);
        ReflectionTestUtils.setField(termVersion, "id", id);
        ReflectionTestUtils.setField(termVersion, "term", term);
        ReflectionTestUtils.setField(termVersion, "majorVersion", majorVersion);
        ReflectionTestUtils.setField(termVersion, "minorVersion", minorVersion);
        ReflectionTestUtils.setField(termVersion, "title", term.getCode() + " v" + majorVersion + "." + minorVersion);
        ReflectionTestUtils.setField(termVersion, "content", "content");
        ReflectionTestUtils.setField(termVersion, "isCurrent", current);
        ReflectionTestUtils.setField(termVersion, "effectiveFrom", LocalDateTime.of(2026, 3, 12, 0, 0));
        return termVersion;
    }
}
