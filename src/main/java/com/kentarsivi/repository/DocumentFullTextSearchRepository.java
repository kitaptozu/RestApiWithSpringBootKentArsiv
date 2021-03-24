package com.kentarsivi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kentarsivi.model.Document;

public interface DocumentFullTextSearchRepository {

	public Page<Document> findDocumentByDescriptionFuzzySearch(Pageable pageable, String searchText);

	public Page<Document> findDocumentByDescriptionPhraseSearch(Pageable pageable, String searchText);

	public Page<Document> findDocumentByDescriptionWildcardSearch(Pageable pageable, String searchText);
}
