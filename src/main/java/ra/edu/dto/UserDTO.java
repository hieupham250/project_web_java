package ra.edu.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.validator.ValidDob;
import ra.edu.validator.ValidPassword;
import ra.edu.validator.ValidPhone;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class UserDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Họ tên không được để trống")
    private String name;

    @NotNull(message = "Ngày sinh không được để trống")
    @ValidDob
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Boolean sex;

    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidPhone
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @ValidPassword
    private String password;
}