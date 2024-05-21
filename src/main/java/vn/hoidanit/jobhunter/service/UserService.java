package vn.hoidanit.jobhunter.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User handleCreateUser(User user){
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    public List<User> fetchAllUsers(){
        return this.userRepository.findAll();
    }

    public User handleUpdateUser(User reqUser){
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null){
            currentUser.setName(reqUser.getName());
            currentUser.setEmail(reqUser.getEmail());
            currentUser.setPassword(reqUser.getPassword());
            //update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public void handleDeleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmail(username);
    }

}
