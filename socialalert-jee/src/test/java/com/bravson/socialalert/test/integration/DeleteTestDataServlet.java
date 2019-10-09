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

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
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
		SearchSession entityManager = Search.session(em);
		for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
			em.createNativeQuery("db." + entityType.getName() + ".drop()").executeUpdate();
		}
		entityManager.writer().purge();
		entityManager.writer().flush();
	}
}
