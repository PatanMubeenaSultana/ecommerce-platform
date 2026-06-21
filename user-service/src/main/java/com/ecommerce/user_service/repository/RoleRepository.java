package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Optional<Role> findByName(String name);
}
