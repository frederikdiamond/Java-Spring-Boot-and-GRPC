package util;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseUtil {
    private static BasicDataSource dataSource;

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            prop.load(input);

            dataSource = new BasicDataSource();
            dataSource.setUrl(prop.getProperty("db.url"));
            dataSource.setUsername(prop.getProperty("db.username"));
            dataSource.setPassword(prop.getProperty("db.password"));
            dataSource.setDriverClassName(prop.getProperty("db.driverClassName", "org.postgresql.Driver"));

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError("Failed to initialize DataSource: " + ex.getMessage());
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
