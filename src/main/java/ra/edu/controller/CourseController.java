package ra.edu.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.CourseConvertDTO;
import ra.edu.dto.CourseDTO;
import ra.edu.entity.Course;
import ra.edu.service.CourseService;

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
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        prepareCourseList(model, name, sortDirection, page, size);

        model.addAttribute("courseDTO", new CourseDTO());
        return "admin";
    }

//    @PostMapping
//    public String addCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
//                            BindingResult bindingResult,
//                            @RequestParam(required = false) String name,
//                            @RequestParam(defaultValue = "asc") String sortDirection,
//                            @RequestParam(defaultValue = "1") int page,
//                            @RequestParam(defaultValue = "5") int size,
//                            Model model) {
//        if (courseService.existsByName(courseDTO.getName())) {
//            bindingResult.rejectValue("name", "error.courseDTO", "Tên khóa học đã tồn tại");
//        }
//
//        if (bindingResult.hasErrors()) {
//            prepareCourseList(model, name, sortDirection, page, size);
//
//            model.addAttribute("showAddModal", true); // flag để mở modal
//            model.addAttribute("courseDTO", courseDTO);
//
//            return "admin";
//        }
//
//        Course course = new Course();
//        course.setName(courseDTO.getName());
//        course.setDuration(courseDTO.getDuration());
//        course.setInstructor(courseDTO.getInstructor());
//        course.setImage(courseDTO.getImage());
//        course.setCreate_at(LocalDate.now());
//
//        try {
//            MultipartFile imageFile = courseDTO.getImageFile();
//            if (imageFile != null && !imageFile.isEmpty()) {
//                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
//                course.setImage(uploadResult.get("url").toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Upload ảnh thất bại", e);
//        }
//
//        courseService.create(course);
//
//        return "redirect:/admin/courses";
//    }

    @PostMapping
    public String saveOrUpdateCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                     BindingResult bindingResult,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(defaultValue = "asc") String sortDirection,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     Model model) {
        boolean isUpdate = courseDTO.getId() != null;

        if (!isUpdate && courseService.existsByName(courseDTO.getName())) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khóa học đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            prepareCourseList(model, name, sortDirection, page, size);
            model.addAttribute("showFormModal", true);
            return "admin";
        }

        Course course = new Course();
        if (isUpdate) {
            course.setId(courseDTO.getId());
        }
        course.setName(courseDTO.getName());
        course.setDuration(courseDTO.getDuration());
        course.setInstructor(courseDTO.getInstructor());
        if (!isUpdate) course.setCreate_at(LocalDate.now());

        try {
            MultipartFile imageFile = courseDTO.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                course.setImage(uploadResult.get("url").toString());
            } else if (isUpdate) {
                Course oldCourse = courseService.findById(courseDTO.getId());
                course.setImage(oldCourse.getImage());
                course.setCreate_at(oldCourse.getCreate_at());// giữ ảnh cũ
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi upload ảnh", e);
        }

        if (isUpdate) {
            courseService.update(course);
        } else {
            courseService.create(course);
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
        model.addAttribute("content", "listCourse");
    }
}
