package com.quickcar.rent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByNameOrEmail(String username, String email);
	Optional<User> findByEmail(String email);
	User findByUserRole(UserRole userRole);
}
