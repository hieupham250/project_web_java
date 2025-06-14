package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Enrollment;
import ra.edu.repository.EnrollmentRepository;

@Service
public class EnrollmentServiceImp implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public boolean checkEnrollment(int userId, int courseId) {
        return enrollmentRepository.checkEnrollment(userId, courseId);
    }

    @Override
    public void create(Enrollment enrollment) {
        enrollmentRepository.create(enrollment);
    }
}
