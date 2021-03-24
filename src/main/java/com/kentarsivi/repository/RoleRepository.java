package com.kentarsivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kentarsivi.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{

	public Role findByRole(String role);
	
	@Query("SELECT count(r) FROM Role r WHERE r.role =:role")
	public Long findRoleCountByRoleName(@Param("role") String role);
	
	
	
}
