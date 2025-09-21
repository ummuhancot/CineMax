package com.cinemax.builtin;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.repository.user.UserRoleRepository;
import com.cinemax.service.user.UserRoleService;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class BuiltInInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();  //  roller
        initializeUsers();  //  kullanıcılar
    }

    private void initializeRoles() {
        if (userRoleRepository.count() == 0) {//role table bos ise
            userRoleRepository.save(UserRole.builder()
                    .roleType(RoleType.ADMIN)
                    .name("Admin")
                    .build());

            userRoleRepository.save(UserRole.builder()
                    .roleType(RoleType.MANAGER)
                    .name("Manager")
                    .build());

            userRoleRepository.save(UserRole.builder()
                    .roleType(RoleType.CUSTOMER)
                    .name("Customer")
                    .build());
        }
    }


    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Admin
            User admin = User.builder()
                    .name("Admin")
                    .surname("User")
                    .email("admin@example.com")
                    .phoneNumber("(123) 456-7890")
                    .password(passwordEncoder.encode("Admin123!"))
                    .birthDate(LocalDate.of(1985, 1, 1))
                    .userRole(userRoleService.getByRoleType(RoleType.ADMIN))
                    .builtIn(true)
                    .build();
            userRepository.save(admin);

            // Manager
            User manager = User.builder()
                    .name("Manager")
                    .surname("User")
                    .email("manager@example.com")
                    .phoneNumber("(123) 456-7891")
                    .password(passwordEncoder.encode("Manager123!"))
                    .birthDate(LocalDate.of(1988, 5, 10))
                    .userRole(userRoleService.getByRoleType(RoleType.MANAGER))
                    .builtIn(true)
                    .build();
            userRepository.save(manager);

            // Customer
            User customer = User.builder()
                    .name("Customer")
                    .surname("User")
                    .email("customer@example.com")
                    .phoneNumber("(123) 456-7892")
                    .password(passwordEncoder.encode("Customer123!"))
                    .birthDate(LocalDate.of(1995, 8, 15))
                    .userRole(userRoleService.getByRoleType(RoleType.CUSTOMER))
                    .builtIn(true)
                    .build();
            userRepository.save(customer);
        }
    }


}
