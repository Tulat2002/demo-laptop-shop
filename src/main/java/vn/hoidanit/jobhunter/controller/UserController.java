package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.*;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;


@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/user/create")
    public User createNewUser(@RequestBody User postManUser) {
        User user = this.userService.handleCreateUser(postManUser);
        return user;
    }

}
