package apptive.fin.user.service;

import apptive.fin.global.error.BusinessException;
import apptive.fin.user.*;
import apptive.fin.user.dto.UserResponseDto;
import apptive.fin.user.dto.UserUpdateRequestDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        // ++ 유렬님 코드 참고해서 나중에 에러코드만 따로 뽑아내서 정의하기

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUserRole().toString()
        );

    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if(request.email() != null){
            if(userRepository.existsByEmail(request.email())){
                throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.updateEmail(request.email());
        }

        if(request.name() != null){
            user.updateName(request.name());
        }

    }

}
