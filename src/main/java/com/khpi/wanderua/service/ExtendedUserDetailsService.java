package com.khpi.wanderua.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ExtendedUserDetailsService extends UserDetailsService {
    UserDetails loadById(Long id);
}
