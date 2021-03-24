package com.kentarsivi.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kentarsivi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByUsername(String username);

	public Page<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);
	
	@Query("SELECT u.username FROM User u WHERE u.username =:username")
	public Object checkUsername(@Param("username") String username);
	
	@Query("SELECT DISTINCT count(u) FROM User u JOIN u.roles r WHERE r.role=:role")
	public Long findUserCountByRoleName(@Param("role") String role);
	
	@Query("SELECT count(u) FROM User u WHERE u.id=:id")
	public Long findUserCountById(@Param("id") Long id);
	
	
}
