package com.khpi.wanderua.config;

import com.khpi.wanderua.entity.RoleConstants;
import com.khpi.wanderua.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable csrf for TEST
                .csrf(csrf -> csrf.disable())

                // Authorization settings
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/business_info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/catalog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/advertisements/**").permitAll()
                        // API adverts - GET fot all users, POST/PUT/DELETE for business represents
                        .requestMatchers(HttpMethod.GET, "/api/advertisements/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/advertisements/**")
                        .hasAuthority(RoleConstants.ROLE_BUSINESS)
                        .requestMatchers(HttpMethod.PUT, "/api/advertisements/**")
                        .hasAuthority(RoleConstants.ROLE_BUSINESS)
                        .requestMatchers(HttpMethod.DELETE, "/api/advertisements/**")
                        .hasAnyAuthority(RoleConstants.ROLE_BUSINESS, RoleConstants.ROLE_ADMIN)

                        // API reviews - GET fot all users, POST - checked on back-end (permit for authenticated)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").permitAll() // may be should be .authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()
                        // API complaints
                        .requestMatchers(HttpMethod.GET, "/api/complaints/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/complaints/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/complaints/**").authenticated()
                        // API Travel ideas
                        .requestMatchers("/api/travel-ideas/**").authenticated()

                        .requestMatchers("/login", "/registration").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/registration").permitAll()

                        .requestMatchers("/addAdvert", "/advertisements/create")
                        .hasAuthority(RoleConstants.ROLE_BUSINESS)

                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        .requestMatchers("/", "/main", "/help", "/aboutProject", "/forBussines", "/Tourists").permitAll()

                        .anyRequest().authenticated()
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}