package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.h2.tools.Server;
import org.junit.Test;

public class DbMigrationTest {

	@Test
	public void testFywayMigration() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:migration-test");

		Flyway flyway = new Flyway();
		flyway.setLocations("classpath:/db/migration");
		flyway.setDataSource(dataSource);
		flyway.migrate();

		try {
			Server server = Server.createTcpServer().start();
			Connection conn = DriverManager.getConnection("jdbc:h2:mem:migration-test");
			Server.startWebServer(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDomainDefinitionValidation() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("nxpense");
		EntityManager em = emf.createEntityManager();
		em.close();
		emf.close();
	}
}	
