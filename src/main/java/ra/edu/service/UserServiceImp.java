package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.User;
import ra.edu.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll(String keyword, String sortDirection, int page, int size) {
        return userRepository.findAll(keyword, sortDirection, page, size);
    }

    @Override
    public long countWithFilter(String keyword) {
        return userRepository.countWithFilter(keyword);
    }

    @Override
    public boolean updateStatus(int id) {
        return userRepository.updateStatus(id);
    }

    @Override
    public boolean updateInfo(User user) {
        return userRepository.updateInfo(user);
    }
}
