package ra.edu.repository;

import ra.edu.datatype.StatusEnrollment;
import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentRepository {
    List<Enrollment> findAll(String keyword, StatusEnrollment status, int userId, int page, int size);
    long countWithFilter(String keyword, StatusEnrollment status, int userId);
    boolean checkEnrollment(int userId, int courseId);
    List<Integer> findAllCourseIdsByUserId(int userId);
    boolean create(Enrollment enrollment);
}
