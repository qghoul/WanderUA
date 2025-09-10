package com.khpi.wanderua.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        log.error("Unhandled exception for request {}: ", requestURI, ex);

        if (requestURI.startsWith("/api/")) {
            return "error/api-error";
        }

        model.addAttribute("error", "Виникла непередбачена помилка");
        model.addAttribute("message", ex.getMessage());
        return "error/general-error";
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handleAccessDeniedException(Exception ex, Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        log.warn("Access denied for request {}: {}", requestURI, ex.getMessage());

        if (requestURI.startsWith("/api/")) {
            return "error/access-denied-api";
        }

        model.addAttribute("error", "Доступ заборонений");
        model.addAttribute("message", "У вас немає прав для виконання цієї операції");
        return "error/access-denied";
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public String handleEntityNotFoundException(Exception ex, Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        log.warn("Entity not found for request {}: {}", requestURI, ex.getMessage());

        if (requestURI.startsWith("/api/")) {
            return "error/not-found-api";
        }

        model.addAttribute("error", "Ресурс не знайдений");
        model.addAttribute("message", ex.getMessage());
        return "error/not-found";
    }
}