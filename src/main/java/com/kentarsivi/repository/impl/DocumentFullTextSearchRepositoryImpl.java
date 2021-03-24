package com.kentarsivi.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kentarsivi.model.Document;
import com.kentarsivi.repository.DocumentFullTextSearchRepository;

@Repository
public class DocumentFullTextSearchRepositoryImpl implements DocumentFullTextSearchRepository {

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional
	public Page<Document> findDocumentByDescriptionFuzzySearch(Pageable pageable, String searchText) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Document.class).get();

		Query luceneQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
				.onFields("description"). matching(searchText.trim()).createQuery();

		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Document.class);

		int page = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		fullTextQuery.setFirstResult(page * pageSize);
		fullTextQuery.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Document> documents = fullTextQuery.getResultList();

		return new PageImpl<>(documents, pageable, fullTextQuery.getResultSize());
	}

	@Override
	@Transactional
	public Page<Document> findDocumentByDescriptionPhraseSearch(Pageable pageable, String searchText) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Document.class).get();

		Query luceneQuery = queryBuilder.phrase().onField("description").sentence(searchText.trim()).createQuery();

		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Document.class);

		int page = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		fullTextQuery.setFirstResult(page * pageSize);
		fullTextQuery.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Document> documents = fullTextQuery.getResultList();

		return new PageImpl<>(documents, pageable, fullTextQuery.getResultSize());
	}

	@Override
	public Page<Document> findDocumentByDescriptionWildcardSearch(Pageable pageable, String searchText) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(Document.class).get();

		Query luceneQuery = queryBuilder.keyword().wildcard().onField("description").matching(searchText.trim())
				.createQuery();

		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Document.class);

		int page = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		fullTextQuery.setFirstResult(page * pageSize);
		fullTextQuery.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Document> documents = fullTextQuery.getResultList();

		return new PageImpl<>(documents, pageable, fullTextQuery.getResultSize());
	}

}
