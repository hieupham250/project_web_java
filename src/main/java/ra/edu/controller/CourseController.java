package ra.edu.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.CourseConvertDTO;
import ra.edu.dto.CourseDTO;
import ra.edu.entity.Course;
import ra.edu.entity.User;
import ra.edu.service.CourseService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public String courses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            HttpSession session) {
        String result = checkAdminRole(session);
        if (result != null) return result;

        prepareCourseList(model, name, sortDirection, page, size);

        model.addAttribute("courseDTO", new CourseDTO());
        return "admin/admin";
    }

    @PostMapping
    public String saveOrUpdateCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                     BindingResult bindingResult,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String sortDirection,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        boolean isUpdate = courseDTO.getId() != null;

        if (!isUpdate && courseService.existsByName(normalizeName(courseDTO.getName()))) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khóa học đã tồn tại");
        }

        // Kiểm tra trùng tên khi cập nhật
        if (isUpdate) {
            Course oldCourse = courseService.findById(courseDTO.getId());
            if (!courseDTO.getName().equalsIgnoreCase(oldCourse.getName())
                    && courseService.existsByName(normalizeName(courseDTO.getName()))) {
                bindingResult.rejectValue("name", "error.courseDTO", "Tên khóa học đã tồn tại");
            }
        }

        if (!isUpdate && (courseDTO.getImageFile() == null || courseDTO.getImageFile().isEmpty())) {
            bindingResult.rejectValue("imageFile", "error.courseDTO", "Ảnh không được để trống");
        }

        if (bindingResult.hasErrors()) {
            name = null;
            prepareCourseList(model, name, sortDirection, page, size);
            model.addAttribute("showFormModal", true);
            return "admin/admin";
        }

        Course course = new Course();
        if (isUpdate) {
            Course oldCourse = courseService.findById(courseDTO.getId());
            course.setId(oldCourse.getId());
            course.setCreate_at(oldCourse.getCreate_at());
            course.setImage(oldCourse.getImage());
        } else {
            course.setCreate_at(LocalDate.now());
        }

        course.setName(courseDTO.getName());
        course.setDuration(courseDTO.getDuration());
        course.setInstructor(courseDTO.getInstructor());

        try {
            MultipartFile imageFile = courseDTO.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                course.setImage(uploadResult.get("url").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi upload ảnh", e);
        }

        if (isUpdate) {
            if (courseService.update(course)) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật khóa học thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cập nhật khóa học thất bại!");
            }

        } else {
            if (courseService.create(course)) {
                redirectAttributes.addFlashAttribute("success", "Thêm khóa học thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Thêm khóa học thất bại!");
            }
        }

        return "redirect:/admin/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        String result = checkAdminRole(session);
        if (result != null) return result;

        if (courseService.checkHasStudents(id)) {
            redirectAttributes.addFlashAttribute("error", "Khóa học đang có sinh viên, không thể xóa!");
            return "redirect:/admin/courses";
        }

        if (courseService.delete(id)) {
            redirectAttributes.addFlashAttribute("success", "Xóa khóa học thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Xóa khóa học thất bại!");
        }

        return "redirect:/admin/courses";
    }

    private void prepareCourseList(Model model, String name, String sortDirection, int page, int size) {
        List<Course> courses = courseService.findAll(name, sortDirection, page, size);
        long totalCourse = courseService.countWithFilter(name);
        int totalPages = (int) Math.ceil((double) totalCourse / size);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<CourseConvertDTO> newCourses = new ArrayList<>();
        for (Course course : courses) {
            CourseConvertDTO courseConvertDTO = new CourseConvertDTO();
            courseConvertDTO.setId(course.getId());
            courseConvertDTO.setName(course.getName());
            courseConvertDTO.setDuration(course.getDuration());
            courseConvertDTO.setInstructor(course.getInstructor());
            courseConvertDTO.setImage(course.getImage());
            courseConvertDTO.setCreate_at(formatter.format(course.getCreate_at()));
            newCourses.add(courseConvertDTO);
        }
        model.addAttribute("courses", newCourses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("content", "admin/listCourse");
    }

    private String normalizeName(String name) {
        if (name == null) return null;
        return name.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String checkAdminRole(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/auth/login";
        }

        return null;
    }
}
