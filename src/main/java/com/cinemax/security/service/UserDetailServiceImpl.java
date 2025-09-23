package com.cinemax.security.service;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.service.helper.MethodHelper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MethodHelper methodHelper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // email üzerinden kullanıcı çekiyoruz
        User user = methodHelper.loadByEmail(email);

        return new UserDetailsImpl(user);
    }

}
