package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Course;
import ra.edu.repository.DashboardRepository;

import java.util.List;

@Service
public class DashboardServiceImp implements DashboardService {
    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public long countTotalCourses() {
        return dashboardRepository.countTotalCourses();
    }

    @Override
    public long countTotalStudents() {
        return dashboardRepository.countTotalStudents();
    }

    @Override
    public long countTotalEnrollments() {
        return dashboardRepository.countTotalEnrollments();
    }

    @Override
    public List<Course> getAllCourses() {
        return dashboardRepository.getAllCourses();
    }

    @Override
    public List<Course> get5bestCourses() {
        return dashboardRepository.get5bestCourses();
    }
}
