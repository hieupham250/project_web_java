package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.datatype.Role;
import ra.edu.datatype.StatusEnrollment;
import ra.edu.dto.ChangePasswordDTO;
import ra.edu.dto.EnrollmentConvertDTO;
import ra.edu.dto.ProfileDTO;
import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;
import ra.edu.entity.User;
import ra.edu.service.AuthService;
import ra.edu.service.CourseService;
import ra.edu.service.EnrollmentService;
import ra.edu.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    // ====== Phần xem tất cả khóa học ==========
    @GetMapping("/courses")
    public String courses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {
        List<Course> courses = courseService.findAll(name, sortDirection, page, size);
        long totalCourse = courseService.countWithFilter(name);
        int totalPages = (int) Math.ceil((double) totalCourse / size);

        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);
        model.addAttribute("size", size);
        model.addAttribute("sortDirection", sortDirection);

        User user = checkLogin(session);
        List<Integer> registeredCourseIds = new ArrayList<>();
        if (user != null) {
            registeredCourseIds = enrollmentService.findAllCourseIdsByUserId(user.getId());
        }
        model.addAttribute("registeredCourseIds", registeredCourseIds);

        model.addAttribute("content", "courses");
        return "home";
    }

    @PostMapping("/register")
    public String registerCourse(@RequestParam int courseId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = checkLogin(session);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }

        boolean isRegistered = enrollmentService.checkEnrollment(user.getId(), courseId);

        if (isRegistered) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đăng ký khóa học này!");
            return "redirect:/home/courses";
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(courseService.findById(courseId));
        enrollment.setUser(user);
        enrollment.setRegistered_at(LocalDateTime.now());
        enrollment.setStatus(StatusEnrollment.WAITING);

        if(enrollmentService.create(enrollment)) {
            redirectAttributes.addFlashAttribute("success", "Đăng ký khóa học thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng ký khóa học!");
        }

        return "redirect:/home/courses";
    }

    // ====== Phần xem lịch sử đăng ký ==========
    @GetMapping("enrollments")
    public String enrollments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) StatusEnrollment status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = checkLogin(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }
        int userId = 0; // mặc định admin
        if (user.getRole() != Role.ADMIN) {
            userId = user.getId();
        }

        List<Enrollment> enrollments = enrollmentService.findAll(name, status, userId, page, size);
        long totalEnrollment = enrollmentService.countWithFilter(name, status, userId);
        int totalPages = (int) Math.ceil((double) totalEnrollment / size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        List<EnrollmentConvertDTO> newEnrollments = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            EnrollmentConvertDTO enrollmentConvertDTO = new EnrollmentConvertDTO();
            enrollmentConvertDTO.setId(enrollment.getId());
            enrollmentConvertDTO.setCourseName(enrollment.getCourse().getName());
            enrollmentConvertDTO.setInstructor(enrollment.getCourse().getInstructor());
            enrollmentConvertDTO.setDuration(enrollment.getCourse().getDuration());
            enrollmentConvertDTO.setImage(enrollment.getCourse().getImage());
            enrollmentConvertDTO.setRegisteredAt(formatter.format(enrollment.getRegistered_at()));
            enrollmentConvertDTO.setStatus(enrollment.getStatus());
            newEnrollments.add(enrollmentConvertDTO);
        }

        model.addAttribute("enrollments", newEnrollments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);
        model.addAttribute("size", size);
        model.addAttribute("status", status);
        model.addAttribute("statusList", StatusEnrollment.values());
        model.addAttribute("content", "enrollments");

        return "home";
    }

    @PostMapping("enrollments/cancel/{id}")
    public String cancelEnrollment(
            @PathVariable("id") int enrollmentId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User user = checkLogin(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }

        if(enrollmentService.updateStatus(enrollmentId, StatusEnrollment.CANCEL)) {
            redirectAttributes.addFlashAttribute("success", "Hủy đăng ký thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Hủy đăng ký thất bại!");
        }

        return "redirect:/home/enrollments";
    }

    // ====== Phần profile ==========
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = checkLogin(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(user.getId());
        profileDTO.setName(user.getName());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setSex(user.isSex());
        profileDTO.setDob(user.getDob());

        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        model.addAttribute("content", "profile");
        return "home";
    }

    @PostMapping("/update")
    public String updateProfileInfo(
            @Valid @ModelAttribute("profileDTO") ProfileDTO profileDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User user = checkLogin(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }

        if (!profileDTO.getPhone().equals(user.getPhone()) && authService.existsByPhone(profileDTO.getPhone())) {
            bindingResult.rejectValue("phone", "error.profileDTO", "Số điện thoại đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("profileDTO", profileDTO);
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
            model.addAttribute("content", "profile");
            return "home";
        }

        user.setName(profileDTO.getName());
        user.setPhone(profileDTO.getPhone());
        user.setSex(profileDTO.getSex());
        user.setDob(profileDTO.getDob());

        if(userService.updateInfo(user)) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thông tin thất bại!");
        }

        return "redirect:/home/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        User user = checkLogin(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng nhập!");
            return "redirect:/home/courses";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("profileDTO", new ProfileDTO());
            model.addAttribute("content", "profile");
            model.addAttribute("showChangePasswordModal", true);
            return "home";
        }

        // Kiểm tra mật khẩu cũ
        if (!user.getPassword().equals(changePasswordDTO.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "error.changePasswordDTO", "Mật khẩu cũ không đúng");
            model.addAttribute("profileDTO", new ProfileDTO());
            model.addAttribute("content", "profile");
            model.addAttribute("showChangePasswordModal", true);
            return "home";
        }

        // Kiểm tra confirm password
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.changePasswordDTO", "Xác nhận mật khẩu không khớp");
            model.addAttribute("profileDTO", new ProfileDTO());
            model.addAttribute("content", "profile");
            model.addAttribute("showChangePasswordModal", true);
            return "home";
        }

        // Cập nhật mật khẩu
        user.setPassword(changePasswordDTO.getNewPassword());
        if (userService.updateInfo(user)) {
            session.removeAttribute("user");
            redirectAttributes.addFlashAttribute("success", "Thay đổi mật khẩu thành công!");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Thay đổi mật khẩu thất bại!");
        }

        return "redirect:/home/profile";
    }

    private User checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return null;
        return user;
    }
}
