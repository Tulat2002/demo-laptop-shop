package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDto;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDto;
import vn.hoidanit.jobhunter.domain.response.ResUserDto;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDto;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService){
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user){
        //check company
        if (user.getCompany() != null){
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        //check role
        if (user.getRole() != null){
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
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
        ResultPaginationDto.Meta mt = new ResultPaginationDto.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDto> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDto(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public User handleUpdateUser(User reqUser){
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null){
            currentUser.setName(reqUser.getName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());

            //check company
            if (reqUser.getCompany() != null){
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            //check role
            if (reqUser.getRole() != null){
                Role role = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(role != null ? role : null);
            }
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
        ResCreateUserDto.CompanyUser companyUser = new ResCreateUserDto.CompanyUser();

        resCreateUserDto.setId(user.getId());
        resCreateUserDto.setEmail(user.getEmail());
        resCreateUserDto.setName(user.getName());
        resCreateUserDto.setAge(user.getAge());
        resCreateUserDto.setAddress(user.getAddress());
        resCreateUserDto.setGender(user.getGender());
        resCreateUserDto.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resCreateUserDto.setCompany(companyUser);
        }
        return resCreateUserDto;
    }

    public ResUserDto convertToResUserDto(User user){
        ResUserDto resUserDto = new ResUserDto();
        ResUserDto.CompanyUser companyUser = new ResUserDto.CompanyUser();
        ResUserDto.RoleUser roleUser = new ResUserDto.RoleUser();

        if (user.getCompany() != null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUserDto.setCompany(companyUser);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            resUserDto.setRole(roleUser);
        }

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
        ResUpdateUserDto res = new ResUpdateUserDto();
        ResUpdateUserDto.CompanyUser com = new ResUpdateUserDto.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email){
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
