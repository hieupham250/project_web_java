package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Course;
import ra.edu.repository.CourseRepository;

import java.util.List;

@Service
public class CourseServiceImp implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> findAll(String keyword, String sortDirection, int page, int size) {
        return courseRepository.findAll(keyword, sortDirection, page, size);
    }

    @Override
    public long countWithFilter(String keyword) {
        return courseRepository.countWithFilter(keyword);
    }

    @Override
    public Course findById(int id) {
        return courseRepository.findById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    @Override
    public boolean create(Course course) {
       return courseRepository.create(course);
    }

    @Override
    public boolean update(Course course) {
        return courseRepository.update(course);
    }

    @Override
    public boolean checkHasStudents(int courseId) {
        return courseRepository.checkHasStudents(courseId);
    }

    @Override
    public boolean delete(int id) {
         return courseRepository.delete(id);
    }
}
