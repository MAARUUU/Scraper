package database;

import java.sql.*;
import java.util.Properties;

public class DBWorker {
    private final String dbUrl;

    public DBWorker(String dbHost, int port, String dbName) {
        dbUrl = "jdbc:postgresql://" + dbHost + ":" + port + "/" + dbName;
    }

    private Connection getConnection(String user, String password) throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("ssl", "false");
        Connection conn = DriverManager.getConnection(dbUrl, props);
        return conn;
    }

    public void Query() throws SQLException {
        var conn = getConnection("postgres", "snakemaster3000");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM test");
        while (rs.next()) {
            System.out.print(rs.getString(1) + " - ");
            System.out.println(rs.getString(2));
        }
        rs.close();
        st.close();
    }
}
