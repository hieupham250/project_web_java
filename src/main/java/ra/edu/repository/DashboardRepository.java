package ra.edu.repository;

import ra.edu.entity.Course;

import java.util.List;

public interface DashboardRepository {
    long countTotalCourses();
    long countTotalStudents();
    long countTotalEnrollments();
    List<Course> getAllCourses();
    List<Course> get5bestCourses();
}
