package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.*;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;


@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User postManUser) {
        User user = this.userService.handleCreateUser(postManUser);
        return user;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return this.userService.fetchAllUsers();
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable("id") long id) {
        return this.userService.fetchUserById(id);
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User updatedUser) {
        User user = this.userService.handleUpdateUser(updatedUser);
        return user;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return "Deleted user successfully";
    }

}
