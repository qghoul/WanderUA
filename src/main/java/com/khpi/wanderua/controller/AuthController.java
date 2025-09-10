package com.khpi.wanderua.controller;

import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

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
}