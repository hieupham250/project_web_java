package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.CourseConvertDTO;
import ra.edu.dto.StudentConvertDTO;
import ra.edu.entity.Course;
import ra.edu.entity.User;
import ra.edu.service.UserService;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/students")
public class StudentController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String students(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session
    ) {
        String result = checkAdminRole(session);
        if (result != null) return result;

        prepareStudentList(model, name, sortDirection, page, size);

        return "admin/admin";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean result = userService.updateStatus(id);
        if (result) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cập nhật trạng thái thất bại.");
        }
        return "redirect:/admin/students";
    }

    private void prepareStudentList(Model model, String name, String sortDirection, int page, int size) {
        List<User> users = userService.findAll(name, sortDirection, page, size);
        long totalUser = userService.countWithFilter(name);
        int totalPages = (int) Math.ceil((double) totalUser / size);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<StudentConvertDTO> newStudents = new ArrayList<>();
        for (User user : users) {
            StudentConvertDTO studentConvertDTO = new StudentConvertDTO();
            studentConvertDTO.setId(user.getId());
            studentConvertDTO.setUsername(user.getUsername());
            studentConvertDTO.setName(user.getName());
            studentConvertDTO.setDob(formatter.format(user.getDob()));
            studentConvertDTO.setSex(user.isSex());
            studentConvertDTO.setPhone(user.getPhone());
            studentConvertDTO.setEmail(user.getEmail());
            studentConvertDTO.setStatus(user.getStatus());
            newStudents.add(studentConvertDTO);
        }
        model.addAttribute("students", newStudents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);
        model.addAttribute("size", size);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("content", "admin/listStudent");
    }

    private String checkAdminRole(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/auth/login";
        }

        return null;
    }
}
