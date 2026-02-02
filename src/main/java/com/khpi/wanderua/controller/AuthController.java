package com.khpi.wanderua.controller;

import com.khpi.wanderua.config.JwtUtil;
import com.khpi.wanderua.dto.LoginRequest;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.repository.UserRepository;
import com.khpi.wanderua.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String passwordConfirm,
                               RedirectAttributes redirectAttributes) {

        try {
            if (!user.getPassword().equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "Паролі не співпадають");
                return "redirect:/registration.html?error=true";
            }

            if (user.getPassword().length() < 5) {
                redirectAttributes.addFlashAttribute("error", "Пароль повинен містити мінімум 5 символів");
                return "redirect:/registration.html?error=true";
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email є обов'язковим");
                return "redirect:/registration.html?error=true";
            }

            boolean saved = userService.saveUser(user);

            if (saved) {
                log.info("User {} registered successfully", user.getUsername());
                return "redirect:/login.ftl?registered=true";
            } else {
                log.warn("Registration failed for user {}: user already exists", user.getUsername());
                return "redirect:/registration.html?error=true";
            }

        } catch (Exception e) {
            log.error("Registration error: ", e);
            return "redirect:/registration.html?error=true";
        }
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            log.info("User {} logged in successfully", authentication.getName());

            if (user != null && user.isBusinessVerified()) {
                return "redirect:/main.html";
            }
        }

        return "redirect:/main.html";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest,
                                   HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            Integer versionInDB = user.getJwtTokenVersion();
            if (versionInDB == null) {
                user.setJwtTokenVersion(1);
                userRepository.save(user);
                log.info("Initialized token version for user {}", user.getEmail());
            }

            String token = jwtUtil.generateToken(user);

            Cookie cookie = new Cookie("accessToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); //24 hours

            response.addCookie(cookie);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            log.info("User {} logged in and received JWT cookie", user.getEmail());

            return "redirect:/main";

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            return "redirect:/login?error=true";
        }
    }
}