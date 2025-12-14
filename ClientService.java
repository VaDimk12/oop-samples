import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientService {
    private final Connection connection;

    @Inject
    public ClientService(Connection connection) {
        this.connection = connection;
    }

    public void saveOrder(Order order) {
        String sql = "INSERT INTO orders (id, date, status, deposit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getId());
            stmt.setString(2, order.getDate());
            stmt.setString(3, order.getStatus());
            stmt.setDouble(4, order.getDeposit());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}