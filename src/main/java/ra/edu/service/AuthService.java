package ra.edu.service;

import ra.edu.entity.User;

public interface AuthService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean register(User user);
    User login(String username, String password);
}
