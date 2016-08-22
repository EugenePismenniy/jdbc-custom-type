package ua.home.jdbc.custom.type;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Evgeniy Pismenny 22.08.16 20:21
 */
public class Main {


    public static void main(String[] args) throws Exception {

        // Class.forName("org.postgresql.Driver");

        Class.forName("ua.home.jdbc.driver.warp.postgres.PostgresDriverProxyRegister");


        try(Connection connection = DriverManager
                .getConnection("jdbc:postgresql-proxy://localhost:5432/test_db", "test_user", "test_password");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM public.book")) {


            if (rs.next()) {

                //rs.get


                //rs.ge


                System.out.println(rs.getInt(1));

            }


            System.out.println(!connection.isClosed());
        }


    }









}
