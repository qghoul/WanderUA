package com.khpi.wanderua.controller;

import com.khpi.wanderua.entity.TravelIdea;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PageController {

    private final UserService userService;

    @GetMapping("/catalog")
    public String catalogPage(@RequestParam(value = "city", required = false) String city,
                              @RequestParam(value = "category", required = false) String category,
                              Model model,
                              Authentication authentication) {
        log.info("Loading catalog page for city: {}, category: {}", city, category);

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            model.addAttribute("currentUser", user);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        model.addAttribute("selectedCity", city != null ? city : "");
        model.addAttribute("selectedCategory", category != null ? category : "");

        log.info("Catalog page loaded with city: {}, category: {}", city, category);
        return "catalog";
    }

    @GetMapping("/advertisement")
    public String advertisementDetailPage(@RequestParam(value = "id", required = true) Long id,
                                          Model model,
                                          Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            model.addAttribute("currentUser", user);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        model.addAttribute("advertisementId", id);

        log.info("Loading advertisement detail page for ID: {}", id);
        return "advertisement";
    }

    @GetMapping("/advertisements/{id}")
    public String advertisementDetailByPath(@PathVariable Long id,
                                            Model model,
                                            Authentication authentication) {
        log.info("Accessing advertisement detail page with ID: {}", id);

        if (id == null || id <= 0) {
            log.error("Invalid advertisement ID: {}", id);
            return "redirect:/advertisements?error=invalid_id";
        }

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            model.addAttribute("currentUser", user);
            model.addAttribute("isAuthenticated", true);
            log.info("User {} is viewing advertisement {}", user != null ? user.getUsername() : "unknown", id);
        } else {
            model.addAttribute("isAuthenticated", false);
            log.info("Anonymous user is viewing advertisement {}", id);
        }

        model.addAttribute("advertisementId", id);

        log.info("Loading advertisement detail page for ID: {}", id);
        return "advertisement";
    }

    @GetMapping({"/", "/main"})
    public String mainPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            model.addAttribute("currentUser", user);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "main";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Невірний email або пароль");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Ви успішно вийшли з системи");
        }

        if (registered != null) {
            model.addAttribute("successMessage", "Реєстрація успішна! Тепер ви можете увійти в систему");
        }

        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(@RequestParam(value = "error", required = false) String error,
                                   Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Помилка при реєстрації. Користувач з таким іменем або email вже існує");
        }

        return "registration";
    }

    @GetMapping({"/addAdvert", "/advertisements/create"})
    @PreAuthorize("hasAuthority('ROLE_BUSINESS')")
    public String createAdvertisementPage(Model model, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);

        if (user == null) {
            if (authentication != null && authentication.getName() != null) {
                log.info("Trying to find user by email: {}", authentication.getName());
                User userByEmail = userService.findUserByEmail(authentication.getName());
                log.info("User found by email: {}", userByEmail);

                if (userByEmail == null) {
                    log.info("Trying to find user by username: {}", authentication.getName());
                    User userByUsername = userService.findUserByUsername(authentication.getName()).orElse(null);
                    log.info("User found by username: {}", userByUsername);
                }
            }

            return "redirect:/main?error=user_not_found_in_database";
        }

        if (!user.isBusinessRepresentVerify()) {
            log.warn("User {} has ROLE_BUSINESS but not verified as business representative",
                    user.getUsername());
            return "redirect:/main?error=business_verification_required";
        }

        log.info("Access granted to user: {}", user.getUsername());
        model.addAttribute("currentUser", user);
        return "add-form";
    }

    @GetMapping("/help")
    public String helpPage() {
        return "help";
    }

    @GetMapping("/aboutProject")
    public String aboutProjectPage() {
        return "about-project";
    }

    @GetMapping("/forBussines")
    public String forBusinessPage() {
        return "for-business";
    }


    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        model.addAttribute("currentUser", user);
        return "profile";
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public String complaintCatalog(){ return "complaints-catalog";}
    @GetMapping("/my-complaints")
    public String myComplaintsCatalog() { return "my-complaints";}

    @GetMapping("/travel-ideas")
    public String getTravelIdeasPage() { return "travel-ideas";}

    @GetMapping("/travel-ideas/{id}")
    public String getTravelIdeaDetail() { return "travel-idea-detail";}

    @GetMapping("/business_info")
    public String getBusinessInfoPage(){ return "business-info";}
    @GetMapping("/business_verify")
    public String getBusinessVerifyForm(){ return "business-verify";}
    @GetMapping("/sustainability_verify")
    public String getSustainabilityVerifyForm(){ return "sustainability-verify";}

    @GetMapping("/verify-requests-catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public String verifyRequestsCatalogPage() {
        return "verify-request-catalog";
    }

    @GetMapping("/my-verify-requests")
    @PreAuthorize("isAuthenticated()")
    public String myVerifyRequestsPage() {
        return "my-verify-requests";
    }

}