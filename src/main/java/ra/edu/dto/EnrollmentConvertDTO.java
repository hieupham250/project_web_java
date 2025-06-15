package ra.edu.dto;

import ra.edu.datatype.StatusEnrollment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EnrollmentConvertDTO {
    private int id;
    private String courseName;
    private String instructor;
    private int duration;
    private String image;
    private String registeredAt;
    private StatusEnrollment status;
}
