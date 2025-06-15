package ra.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.datatype.StatusAccount;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentConvertDTO {
    private int id;

    private String username;

    private String name;

    private String dob;

    private Boolean sex;

    private String phone;

    private String email;

    private StatusAccount status;
}
