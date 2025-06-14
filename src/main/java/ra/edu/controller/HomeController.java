package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.datatype.StatusEnrollment;
import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;
import ra.edu.entity.User;
import ra.edu.service.CourseService;
import ra.edu.service.EnrollmentService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/courses")
    public String courses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {
        List<Course> courses = courseService.findAll(name, sortDirection, page, size);
        long totalCourse = courseService.countWithFilter(name);
        int totalPages = (int) Math.ceil((double) totalCourse / size);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);

        model.addAttribute("content", "courses");
        return "home";
    }

    @PostMapping("/register")
    public String registerCourse(@RequestParam int courseId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = checkLogin(session);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập");
            return "redirect:/home/courses";
        }

        boolean isRegistered = enrollmentService.checkEnrollment(user.getId(), courseId);

        if (isRegistered) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đăng ký khóa học này");
            return "redirect:/home/courses";
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(courseService.findById(courseId));
        enrollment.setUser(user);
        enrollment.setRegistered_at(LocalDateTime.now());
        enrollment.setStatus(StatusEnrollment.WAITING);
        enrollmentService.create(enrollment);

        redirectAttributes.addFlashAttribute("success", "Đăng ký khóa học thành công");

        return "redirect:/home/courses";
    }

    private User checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return null;
        return user;
    }
}
