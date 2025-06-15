package ra.edu.service;

import ra.edu.entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> findAll(String keyword, String sortDirection, int page, int size);
    long countWithFilter(String keyword);
    Course findById(int id);
    List<Course> findByIds(List<Integer> ids);
    boolean existsByName(String name);
    boolean create(Course course);
    boolean update(Course course);
    boolean checkHasStudents(int courseId);
    boolean delete(int id);
}
