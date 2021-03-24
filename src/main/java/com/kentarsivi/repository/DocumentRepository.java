package com.kentarsivi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kentarsivi.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	public Page<Document> findByCodeStartingWithIgnoreCaseAndFolderPathNotNull(String code, Pageable pageable);

	public Document findByCode(String code);

	@Query(value = "select d.folderPath from Document d where d.id = ?1")
	public String findDocumentPathById(Long id);

	@Query("SELECT count(d) FROM Document d WHERE d.code =:code")
	public Long findDocumentCountByCode(@Param("code") String code);
	
	@Modifying
	@Transactional
	@Query("UPDATE Document document SET document.folderPath =:folderPath WHERE document.code =:code")
	public int updateDocumentFolderPath(@Param("code") String code, @Param("folderPath") String folderPath);


}
