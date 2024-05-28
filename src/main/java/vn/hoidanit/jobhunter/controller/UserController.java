package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDto;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDto;
import vn.hoidanit.jobhunter.domain.dto.ResUserDto;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDto;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new User")
    public ResponseEntity<ResCreateUserDto> createUser(@Valid @RequestBody User user) throws IdInvalidException{
        boolean emailExists = this.userService.existsByEmail(user.getEmail());
        if (emailExists) {
            throw  new IdInvalidException("Email " + user.getEmail() + " da ton tai, vui long su dung email khac.");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User saveUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDto(saveUser));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDto> getAllUser(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch User By Id")
    public ResponseEntity<ResUserDto> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null){
            throw new IdInvalidException("User voi id " + id + " khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDto(fetchUser));
    }

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDto> updateUser(@RequestBody User updatedUser) throws IdInvalidException {
       User user = this.userService.handleUpdateUser(updatedUser);
       if (user == null){
           throw new IdInvalidException("User voi id " + user.getId() + " khong ton tai");
       }
       return ResponseEntity.ok(this.userService.convertToResUpdateUserDto(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null){
            throw new IdInvalidException("User voi id " + id + " khong ton tai");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

}
