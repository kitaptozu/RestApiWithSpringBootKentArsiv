package com.kentarsivi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Indexed
@Entity
@Table(name = "tbl_document", indexes = { @Index(name = "idx_document", columnList = "code") })
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Field(analyzer = @Analyzer(impl = TurkishAnalyzer.class))
	@Column(columnDefinition = "TEXT", length = 3000)
	private String description;

	private String folderPath;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "archive_id", insertable = true, updatable = true, nullable = true)
	private Archive archive;

}
