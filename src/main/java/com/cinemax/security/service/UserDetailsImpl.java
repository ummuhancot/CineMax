package com.cinemax.security.service;

import com.cinemax.entity.concretes.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {


    private Long id;

    private String email;

    @JsonIgnore
    private String password;

    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();

        List<GrantedAuthority> authorities = new ArrayList<>();
        String roleName = user.getUserRole().getName();
        if("ROLE_ADMIN".equals(roleName)) {
            roleName = "Admin"; // PreAuthorize ile eşleşsin
        }
        authorities.add(new SimpleGrantedAuthority(roleName));
        this.authorities = new ArrayList<>(authorities);
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
