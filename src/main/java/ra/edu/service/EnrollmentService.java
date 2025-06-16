package ra.edu.service;

import ra.edu.datatype.StatusEnrollment;
import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    List<Enrollment> findAll(String keyword, StatusEnrollment status, int userId, int page, int size);
    long countWithFilter(String keyword, StatusEnrollment status, int userId);
    boolean checkEnrollment(int userId, int courseId);
    List<Integer> findAllCourseIdsByUserId(int userId);
    void create(Enrollment enrollment);
}
