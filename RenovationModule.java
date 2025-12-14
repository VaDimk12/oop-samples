import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RenovationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("jdbcUrl")).toInstance("jdbc:sqlite:target/renovation.db");
        bind(ClientFactory.class);
    }

    @Provides
    @Singleton
    public Connection provideConnection(@Named("jdbcUrl") String jdbcUrl) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl);
        createTableIfNotExists(conn);
        return conn;
    }

    private void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY," +
                "date TEXT," +
                "status TEXT," +
                "deposit REAL" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}