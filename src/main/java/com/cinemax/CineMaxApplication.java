package com.cinemax;

import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.repository.user.UserRoleRepository;
import com.cinemax.service.user.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CineMaxApplication implements CommandLineRunner {

    private final UserRoleService userRoleService;
    private final UserRoleRepository userRoleRepository;

    public CineMaxApplication(UserRoleService userRoleService, UserRoleRepository userRoleRepository) {
        this.userRoleService = userRoleService;
        this.userRoleRepository = userRoleRepository;
    }


	public static void main(String[] args) {
		SpringApplication.run(CineMaxApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {

        if (userRoleService.getAllUserRoles().isEmpty()){

            UserRole admin = new UserRole();
            admin.setRoleType(RoleType.ADMIN);
            admin.setName(RoleType.ADMIN.name());
            userRoleRepository.save(admin);

            UserRole manager = new UserRole();
            manager.setRoleType(RoleType.MANAGER);
            manager.setName(RoleType.MANAGER.name());
            userRoleRepository.save(manager);

            UserRole customer = new UserRole();
            customer.setRoleType(RoleType.CUSTOMER);
            customer.setName(RoleType.CUSTOMER.name());
            userRoleRepository.save(customer);

        }




    }

}
