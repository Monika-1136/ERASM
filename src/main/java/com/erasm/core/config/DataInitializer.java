package com.erasm.core.config;

import com.erasm.core.entity.*;
import com.erasm.core.enums.*;
import com.erasm.core.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               EmployeeRepository employeeRepository,
                               SkillRepository skillRepository,
                               ProjectRepository projectRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Roles
            for (RoleName roleName : RoleName.values()) {
                if (roleRepository.findByRoleName(roleName).isEmpty()) {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    roleRepository.save(role);
                }
            }

            // Seed Admin User
            if (userRepository.findByEmail("admin@erasm.com").isEmpty()) {
                Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN).orElse(null);
                User admin = new User();
                admin.setFullName("System Admin");
                admin.setEmail("admin@erasm.com");
                admin.setPassword(passwordEncoder.encode("admin12345"));
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            // Seed Skills
            String[][] defaultSkills = {
                {"Java", "Backend", "Core Java and Enterprise Edition"},
                {"Spring Boot", "Backend", "Spring Boot Framework microservices"},
                {"React", "Frontend", "React Web Library UI"},
                {"Angular", "Frontend", "Angular Framework UI"},
                {"AWS", "Cloud", "Amazon Web Services cloud architecture"},
                {"Azure", "Cloud", "Microsoft Azure cloud services"}
            };

            for (String[] s : defaultSkills) {
                if (!skillRepository.existsBySkillName(s[0])) {
                    Skill skill = new Skill();
                    skill.setSkillName(s[0]);
                    skill.setCategory(s[1]);
                    skill.setDescription(s[2]);
                    skillRepository.save(skill);
                }
            }
        };
    }
}
