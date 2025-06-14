package ra.edu.service;

import ra.edu.entity.Enrollment;

public interface EnrollmentService {
    boolean checkEnrollment(int userId, int courseId);
    void create(Enrollment enrollment);
}
