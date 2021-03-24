package com.kentarsivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kentarsivi.model.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

	public Archive findByName(String name);
	
	@Query("SELECT count(a) FROM Archive a WHERE a.name =:name")
	public Long findArchiveCountByName(@Param("name") String name);

}
