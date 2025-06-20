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
import ra.edu.dto.EnrollmentConvertDTO;
import ra.edu.entity.Enrollment;
import ra.edu.entity.User;
import ra.edu.service.EnrollmentService;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("admin/enrollments")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public String enrollments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) StatusEnrollment status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {
        String result = checkAdminRole(session);
        if (result != null) return result;

        prepareEnrollmentList(name, status, page, size, model);
        return "admin/admin";
    }

    @PostMapping
    public String updateStatus(
            @RequestParam int enrollmentId,
            @RequestParam StatusEnrollment newStatus,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String result = checkAdminRole(session);
        if (result != null) return result;

        if (enrollmentService.updateStatus(enrollmentId, newStatus)) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhật trạng thái thất bại!");
        }

        return "redirect:/admin/enrollments";
    }

    private void prepareEnrollmentList(String name, StatusEnrollment status, int page, int size,Model model) {
        int userId = 0; // mặc định admin

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
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        model.addAttribute("statusList", StatusEnrollment.values());
        model.addAttribute("content", "admin/listEnrollment");
    }

    private String checkAdminRole(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/auth/login";
        }

        return null;
    }
}
