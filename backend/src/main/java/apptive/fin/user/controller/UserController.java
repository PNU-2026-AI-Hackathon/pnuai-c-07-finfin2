package apptive.fin.user.controller;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.user.dto.UserProfileRequestDto;
import apptive.fin.user.dto.UserProfileResponseDto;
import apptive.fin.user.dto.UserResponseDto;
import apptive.fin.user.service.UserProfileService;
import apptive.fin.user.service.UserService;
import apptive.fin.user.dto.UserUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserResponseDto getMyInfo(@AuthenticationPrincipal AuthUserDetails userDetails){
        return userService.getMyInfo(userDetails.getId());
    }

    @PatchMapping("/me")
    public void updateUser(
            @RequestBody UserUpdateRequestDto request,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ){
        userService.updateUser(userDetails.getId(), request);
    }

    @GetMapping("/me/profile")
    public UserProfileResponseDto getProfile(@AuthenticationPrincipal AuthUserDetails userDetails){
        return userProfileService.getProfile(userDetails.getId());
    }

    @PutMapping("/me/profile")
    public void updateProfile(
            @Valid @RequestBody UserProfileRequestDto request,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ){
        userProfileService.upsert(userDetails.getId(), request);
    }

    @DeleteMapping("/me/profile")
    public void deleteProfile(@AuthenticationPrincipal AuthUserDetails userDetails){
        userProfileService.deleteProfile(userDetails.getId());
    }

}
