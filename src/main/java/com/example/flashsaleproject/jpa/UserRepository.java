package com.example.flashsaleproject.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flashsaleproject.models.FlashUser;



public interface UserRepository extends JpaRepository<FlashUser, Long> {

	@EntityGraph(attributePaths="authorities")
	Optional<FlashUser> findOneWithAuthoritiesByEmail(String email);
	
	@EntityGraph(attributePaths="authorities")
	Optional<FlashUser> findOneWithAuthoritiesByUsername(String username);

}
