package ra.edu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDTO {
    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    private String oldPassword;
    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    private String newPassword;
    @NotBlank(message = "Vui lòng nhập xác nhận mật khẩu mới")
    private String confirmPassword;
}
