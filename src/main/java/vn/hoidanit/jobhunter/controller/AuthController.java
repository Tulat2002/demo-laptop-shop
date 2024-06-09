package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDto;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDto;
import vn.hoidanit.jobhunter.domain.response.RestLoginDto;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RestLoginDto> login(@Valid @RequestBody ReqLoginDto loginDto) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //set thong tin nguoi dung dang nhap vao context(co the su dung sau nay)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RestLoginDto res = new RestLoginDto();
        User currentUserDb = this.userService.handleGetUserByUsername(loginDto.getUsername());
        if (currentUserDb != null){
            RestLoginDto.UserLogin userLogin = new RestLoginDto.UserLogin(
                    currentUserDb.getId(),
                    currentUserDb.getEmail(),
                    currentUserDb.getName(),
                    currentUserDb.getRole()
            );
            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        //create RefreshToken
        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

        //updateUser
        this.userService.updateUserToken(refresh_token, loginDto.getUsername());

        //setCookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDto> register(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.existsByEmail(user.getEmail());
        if (isEmailExist){
            throw new IdInvalidException("Email " + user.getEmail() + " da ton tai vui long su dung email khac");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User user1 = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDto(user1));
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<RestLoginDto.UserGetAccount> getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDb = this.userService.handleGetUserByUsername(email);
        RestLoginDto.UserLogin userLogin = new RestLoginDto.UserLogin();
        RestLoginDto.UserGetAccount userGetAccount = new RestLoginDto.UserGetAccount();
        if (currentUserDb != null){
            userLogin.setId(currentUserDb.getId());
            userLogin.setName(currentUserDb.getName());
            userLogin.setEmail(currentUserDb.getEmail());
            userLogin.setRole(currentUserDb.getRole());

            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<RestLoginDto> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {
        if (refresh_token.equals("abc")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        RestLoginDto res = new RestLoginDto();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            RestLoginDto.UserLogin userLogin = new RestLoginDto.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
