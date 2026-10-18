package apptive.fin.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto (

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    String name

    ){
}
