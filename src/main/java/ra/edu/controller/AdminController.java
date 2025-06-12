package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.edu.entity.User;
import ra.edu.service.CourseService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private CourseService courseService;
    @GetMapping
    public String admin(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/auth/login";
        }
        return "admin";
    }
}
