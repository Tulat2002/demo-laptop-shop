package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.*;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
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

    public ResultPaginationDto fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDto rs = new ResultPaginationDto();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDto> lisUser = pageUser.getContent()
                        .stream().map(item -> new ResUserDto(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getAge(),
                        item.getGender(),
                        item.getAddress(),
                        item.getUpdatedAt(),
                        item.getCreatedAt())).collect(Collectors.toList());

        rs.setResult(lisUser);

        return rs;
    }

    public User handleUpdateUser(User reqUser){
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null){
            currentUser.setName(reqUser.getName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
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

    public boolean existsByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDto convertToResCreateUserDto(User user){
        ResCreateUserDto resCreateUserDto = new ResCreateUserDto();
        resCreateUserDto.setId(user.getId());
        resCreateUserDto.setEmail(user.getEmail());
        resCreateUserDto.setName(user.getName());
        resCreateUserDto.setAge(user.getAge());
        resCreateUserDto.setAddress(user.getAddress());
        resCreateUserDto.setGender(user.getGender());
        resCreateUserDto.setCreatedAt(user.getCreatedAt());
        return resCreateUserDto;
    }

    public ResUserDto convertToResUserDto(User user){
        ResUserDto resUserDto = new ResUserDto();
        resUserDto.setId(user.getId());
        resUserDto.setEmail(user.getEmail());
        resUserDto.setName(user.getName());
        resUserDto.setAge(user.getAge());
        resUserDto.setAddress(user.getAddress());
        resUserDto.setGender(user.getGender());
        resUserDto.setCreatedAt(user.getCreatedAt());
        resUserDto.setUpdatedAt(user.getUpdatedAt());
        return resUserDto;
    }

    public ResUpdateUserDto convertToResUpdateUserDto(User user){
        ResUpdateUserDto resUpdateUserDto = new ResUpdateUserDto();
        resUpdateUserDto.setId(user.getId());
        resUpdateUserDto.setName(user.getName());
        resUpdateUserDto.setAge(user.getAge());
        resUpdateUserDto.setAddress(user.getAddress());
        resUpdateUserDto.setGender(user.getGender());
        resUpdateUserDto.setUpdatedAt(user.getUpdatedAt());
        return resUpdateUserDto;
    }

}
