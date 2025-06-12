package ra.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CourseConvertDTO {
    private Integer id;

    private String name;

    private int duration;

    private String instructor;

    private String image;

    private String create_at;
}
