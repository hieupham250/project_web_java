package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.datatype.StatusEnrollment;
import ra.edu.entity.Enrollment;
import ra.edu.repository.EnrollmentRepository;

import java.util.List;

@Service
public class EnrollmentServiceImp implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<Enrollment> findAll(String keyword, StatusEnrollment status, int userId, int page, int size) {
        return enrollmentRepository.findAll(keyword, status, userId, page, size);
    }

    @Override
    public long countWithFilter(String keyword, StatusEnrollment status, int userId) {
        return enrollmentRepository.countWithFilter(keyword, status, userId);
    }

    @Override
    public boolean checkEnrollment(int userId, int courseId) {
        return enrollmentRepository.checkEnrollment(userId, courseId);
    }

    @Override
    public void create(Enrollment enrollment) {
        enrollmentRepository.create(enrollment);
    }
}
