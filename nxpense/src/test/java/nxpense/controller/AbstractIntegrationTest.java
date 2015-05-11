package nxpense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nxpense.domain.User;
import nxpense.security.CustomUserDetails;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;

public class AbstractIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    protected static final String USER_PASSWORD = "secret";
    protected static final String USER_EMAIL = "test@test.com";

    protected static final ObjectMapper om = new ObjectMapper();

    @Autowired
    protected DataSource datasource;

    @Autowired
    protected WebApplicationContext wac;

    protected DefaultMockMvcBuilder mockMvcBuilder;

    protected MockMvc mockMvc;

    protected IDatabaseConnection getDBConnection() throws SQLException, DatabaseUnitException {
        return new DatabaseConnection(java.sql.DriverManager.getConnection("jdbc:h2:mem:nxpense;DB_CLOSE_DELAY=-1;user=sa;password=sa"));
    }

    protected IDataSet loadDataSet(String pathToDataset) throws DataSetException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(pathToDataset);
        return new FlatXmlDataSetBuilder().build(inputStream);
    }

    protected void mockAuthenticatedUser(int mockUserId) {
        User mockUser = new User();

        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockUser, mockUserId);
            idField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Attempt to access a field that does not exist.", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Attempt to access a non-accessible field.", e);
        }

        mockUser.setEmail(USER_EMAIL);
        mockUser.setPassword(USER_PASSWORD.toCharArray());

        UserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, USER_PASSWORD, Collections.<GrantedAuthority>emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
