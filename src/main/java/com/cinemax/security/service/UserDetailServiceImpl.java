package com.cinemax.security.service;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct; // ekledik

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MethodHelper methodHelper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Artık email üzerinden kullanıcı çekiyoruz
        User user = methodHelper.loadByEmail(email);

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),   // username yerine email
                user.getPassword(),
                user.getUserRole().getRoleType().getName()
        );
    }

}
