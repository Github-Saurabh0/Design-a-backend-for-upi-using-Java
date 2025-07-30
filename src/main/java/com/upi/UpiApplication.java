package com.upi;

import com.upi.model.Role;
import com.upi.model.Role.ERole;
import com.upi.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UpiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpiApplication.class, args);
    }

    /**
     * Initialize roles in the database
     */
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Check if roles already exist
            if (roleRepository.count() == 0) {
                // Create roles
                Role userRole = new Role();
                userRole.setName(ERole.ROLE_USER);
                
                Role adminRole = new Role();
                adminRole.setName(ERole.ROLE_ADMIN);
                
                Role bankAdminRole = new Role();
                bankAdminRole.setName(ERole.ROLE_BANK_ADMIN);

                // Save roles
                roleRepository.save(userRole);
                roleRepository.save(adminRole);
                roleRepository.save(bankAdminRole);

                System.out.println("Roles initialized successfully");
            }
        };
    }
}