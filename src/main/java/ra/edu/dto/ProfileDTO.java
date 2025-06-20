package ra.edu.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ra.edu.validator.ValidDob;
import ra.edu.validator.ValidPhone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ProfileDTO {
    private int id;

    @NotBlank(message = "Họ tên không được để trống")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidPhone
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Boolean sex;

    @NotNull(message = "Ngày sinh không được để trống")
    @ValidDob
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
