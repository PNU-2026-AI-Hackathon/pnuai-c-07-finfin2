package apptive.fin.devtools;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class ResetDatabase {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5678/database";
    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "password";

    private static final List<String> SQL_FILES = List.of(
            "schema.sql",
            "data.sql",
            "seed/01-providers.sql",
            "seed/02-products.sql",
            "seed/03-product-properties.sql",
            "seed/04-product-keywords.sql",
            "seed/05-product-required-keywords.sql",
            "seed/06-product-preferential-rates.sql"
    );

    public static void main(String[] args) throws Exception {
        String url = getSetting("SPRING_DATASOURCE_URL", DEFAULT_URL);
        String username = getSetting("SPRING_DATASOURCE_USERNAME", DEFAULT_USERNAME);
        String password = getSetting("SPRING_DATASOURCE_PASSWORD", DEFAULT_PASSWORD);
        List<String> argList = List.of(args);
        boolean noSeed = argList.contains("--no-seed");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(true);
            
            

            for (String sqlFile : SQL_FILES) {
                if (noSeed && sqlFile.startsWith("seed"))
                    continue;
                System.out.println("Running " + sqlFile);
                ScriptUtils.executeSqlScript(
                        connection,
                        new EncodedResource(new ClassPathResource(sqlFile), "UTF-8")
                );
            }
        }

        System.out.println("Database reset completed.");
    }

    private static String getSetting(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
