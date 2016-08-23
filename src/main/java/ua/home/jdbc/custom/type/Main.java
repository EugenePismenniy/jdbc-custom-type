package ua.home.jdbc.custom.type;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws Exception {

        Class.forName("ua.home.jdbc.driver.warp.postgres.PostgresDriverProxyRegister");

        try(Connection connection = DriverManager
                // используем отличный от оригинала прификс к url 'jdbc:postgresql-proxy:', что бы именно наш драйвер грузился
                .getConnection("jdbc:postgresql-proxy://localhost:5432/test_db", "test_user", "test_password");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT 12.65456161 as testDecimal, '  tEsTsTrinG    ' as testString")) {

            if (rs.next()) {
                System.out.printf("testDecimal = '%s'\n", rs.getBigDecimal("testDecimal"));
                System.out.printf("testString = '%s'\n", rs.getString("testString"));
            }

            /* output in console
                    testDecimal = '12.65'
                    testString = 'TESTSTRING'
            */

        }
    }









}
