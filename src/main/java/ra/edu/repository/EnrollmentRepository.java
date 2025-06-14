package ra.edu.repository;

import ra.edu.entity.Enrollment;

public interface EnrollmentRepository {
    boolean checkEnrollment(int userId, int courseId);
    void create(Enrollment enrollment);
}
