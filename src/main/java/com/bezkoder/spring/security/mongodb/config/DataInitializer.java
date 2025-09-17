package com.bezkoder.spring.security.mongodb.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bezkoder.spring.security.mongodb.models.ERole;
import com.bezkoder.spring.security.mongodb.models.Role;
import com.bezkoder.spring.security.mongodb.repository.RoleRepository;

@Configuration
public class DataInitializer {

  @Bean
  CommandLineRunner seedRoles(RoleRepository roleRepository) {
    return args -> {
      if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
        roleRepository.save(new Role(ERole.ROLE_USER));
      }
      if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty()) {
        roleRepository.save(new Role(ERole.ROLE_MODERATOR));
      }
      if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
        roleRepository.save(new Role(ERole.ROLE_ADMIN));
      }
    };
  }
}


