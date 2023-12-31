package com.felipe.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.felipe.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByUserName(@Param("userName") String userName);
	// User findByEmail(@Param("email") String email);
}
