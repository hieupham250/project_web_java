package ra.edu.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CourseDTO {
    private Integer id;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 100, message = "Tên khóa học không vượt quá 100 ký tự")
    private String name;

    @Min(value = 1, message = "Thời lượng phải lớn hơn hoặc bằng 1")
    private int duration;

    @NotBlank(message = "Giảng viên không được để trống")
    private String instructor;

    private MultipartFile imageFile;

    private String image;
}
