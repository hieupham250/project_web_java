package ra.edu.service;

import ra.edu.entity.Course;

import java.util.List;

public interface DashboardService {
    long countTotalCourses();
    long countTotalStudents();
    long countTotalEnrollments();
    List<Course> getAllCourses();
    List<Course> get5bestCourses();
}
