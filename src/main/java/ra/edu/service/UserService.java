package ra.edu.service;

import ra.edu.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll(String keyword, String sortDirection, int page, int size);
    long countWithFilter(String keyword);
    boolean updateStatus(int id);
    boolean updateInfo(User user);
}
