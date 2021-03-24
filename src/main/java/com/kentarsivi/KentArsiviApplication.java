package com.kentarsivi;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KentArsiviApplication {

	@Value("${archive.root-folder-path}")
	private String archiveRootFolderPath;

	public static void main(String[] args) {
		SpringApplication.run(KentArsiviApplication.class, args);
	}

	@Bean
	public LuceneIndexServiceBean luceneIndexServiceBean(EntityManagerFactory EntityManagerFactory) {
		LuceneIndexServiceBean luceneIndexServiceBean = new LuceneIndexServiceBean(EntityManagerFactory);
		luceneIndexServiceBean.triggerIndexing();
		return luceneIndexServiceBean;
	}

	@Bean
	public String archiveRootFolderPath() {

		return this.archiveRootFolderPath;
	}
	

	// Set maxPostSize of embedded tomcat server to 10 megabytes (default is 2 MB, not large enough to support file uploads > 1.5 MB)
//	@Bean
//	private void EmbeddedServletContainerCustomizer()  throws Exception {
//	    return (ConfigurableEmbeddedServletContainer container) -> {
//	        if (container instanceof TomcatEmbeddedServletContainerFactory) {
//	            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
//	            tomcat.addConnectorCustomizers(
//	                (connector) -> {
//	                    connector.setMaxPostSize(10000000); // 10 MB
//	                }
//	            );
//	        }
//	    };
//	}

}
