package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.edu.entity.Course;
import ra.edu.service.CourseService;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private CourseService courseService;
    @GetMapping
    public String admin() {
        List<Course> courses = courseService.findAll("","asc", 1,5);
        return "admin";
    }
}
