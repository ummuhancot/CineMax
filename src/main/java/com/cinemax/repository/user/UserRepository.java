package com.cinemax.repository.user;

import com.cinemax.entity.concretes.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    User findByUsername(String username);
}
