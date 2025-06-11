package ra.edu.repository;

import ra.edu.entity.User;

public interface AuthRepository {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    void register(User user);
    User login(String username, String password);
}
