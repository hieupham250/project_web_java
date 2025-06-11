package ra.edu.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DobValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDob {
    String message() default "Ngày sinh phải nhỏ hơn ngày hiện tại";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}