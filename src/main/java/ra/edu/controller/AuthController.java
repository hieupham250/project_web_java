package ra.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.datatype.Role;
import ra.edu.datatype.StatusAccount;
import ra.edu.dto.UserDTO;
import ra.edu.entity.User;
import ra.edu.service.AuthService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult,  RedirectAttributes redirectAttributes) {
        if (authService.existsByUsername(userDTO.getUsername())) {
            bindingResult.rejectValue("username", "error.userDTO", "Tên đăng nhập đã tồn tại");
        }

        if (authService.existsByEmail(userDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.userDTO", "Email đã tồn tại");
        }

        if (authService.existsByPhone(userDTO.getPhone())) {
            bindingResult.rejectValue("phone", "error.userDTO", "Số điện thoại đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setDob(userDTO.getDob());
        user.setSex(userDTO.getSex());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCreate_at(LocalDate.now());

        if (authService.register(user)) {
            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Đăng ký tài khoản thất bại!");
        }

        return "redirect:/auth/register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if ((username == null || username.trim().isEmpty()) && (password == null || password.trim().isEmpty())) {
            model.addAttribute("usernameError", "Tên đăng nhập không được để trống!");
            model.addAttribute("passwordError", "Mật khẩu không được để trống!");
            return "login";
        }

        User user = authService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "Tài khoản mật khẩu không chính xác!");
            return "login";
        }

        if (user.getStatus() == StatusAccount.ACTIVE) {
            session.setAttribute("user", user);
            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin";
            } else {
                return "redirect:/home/courses";
            }
        } else if (user.getStatus() == StatusAccount.INACTIVE) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản của bạn đã bị khóa!");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!");
        return "redirect:/home/courses";
    }
}
