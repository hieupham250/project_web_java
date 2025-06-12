package ra.edu.repository;

import ra.edu.entity.Course;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll(String keyword, String sortDirection, int page, int size);
    long countWithFilter(String keyword);
    Course findById(int id);
    List<Course> findByIds(List<Integer> ids);
    boolean existsByName(String name);
    void create(Course course);
    void update(Course course);
    void delete(int id);
}
