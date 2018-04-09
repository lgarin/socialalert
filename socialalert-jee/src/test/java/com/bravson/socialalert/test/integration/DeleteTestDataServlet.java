package com.bravson.socialalert.test.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;

@WebServlet("/unitTest/deleteData")
@Transactional
public class DeleteTestDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Inject
	Logger logger;
	
	@PersistenceContext(unitName = "socialalert")
    EntityManager em;
    
    @Resource(name="mediaBaseDirectory")
	String mediaBaseDirectory;

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			logger.info("Deleting database...");
			deleteDatabase();
		
			logger.info("Deleting media files...");
			deleteMediaFiles();
			
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	private void deleteMediaFiles() throws IOException {
		if (Files.notExists(Paths.get(mediaBaseDirectory))) {
			return;
		}
		Files.walk(Paths.get(mediaBaseDirectory))
	      .sorted(Comparator.reverseOrder())
	      .map(Path::toFile)
	      .forEach(File::delete);
	}

	private void deleteDatabase() {
		FullTextEntityManager entityManager = Search.getFullTextEntityManager(em);
		entityManager.createNativeQuery("MATCH (n) DETACH DELETE n").executeUpdate();
		for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {
			if (entityType.getJavaType().getAnnotation(Indexed.class) != null) {
				entityManager.purgeAll(entityType.getJavaType());
			}
		}
		entityManager.flushToIndexes();
	}
}
