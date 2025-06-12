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
    public List<Course> findByIds(List<Integer> ids) {
        return courseRepository.findByIds(ids);
    }

    @Override
    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    @Override
    public void create(Course course) {
        courseRepository.create(course);
    }

    @Override
    public void update(Course course) {
        courseRepository.update(course);
    }

    @Override
    public void delete(int id) {
        courseRepository.delete(id);
    }
}
