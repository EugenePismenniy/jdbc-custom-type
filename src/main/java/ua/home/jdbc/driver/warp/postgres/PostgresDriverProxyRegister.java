package ua.home.jdbc.driver.warp.postgres;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;

/**
 * @author Evgeniy Pismenny 22.08.16 21:56
 */
public class PostgresDriverProxyRegister implements InvocationHandler {

    private static InvocationHandler handler = new PostgresDriverProxyRegister();



    static {
        try {
            DriverManager.registerDriver((Driver) newProxy(new org.postgresql.Driver()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static Object newProxy(Object target) {

        Class<?> clazz = defineInterface(target);

        return clazz != null ? Proxy.newProxyInstance(Driver.class.getClassLoader()
                , new Class[]{clazz}, handler) : target;
    }


    public static Object invokeAndProxy(Object target, Method method, Object[] args) throws Throwable {
        Object returnValue = method.invoke(target, args);
        return newProxy(returnValue);
    }

    public static Class<?> defineInterface(Object o) {

        if (o == null)
            return null;

        if (o instanceof Driver)
            return Driver.class;
        if (o instanceof Connection)
            return Connection.class;
        if (o instanceof Statement)
            return Statement.class;
        if (o instanceof PreparedStatement)
            return PreparedStatement.class;
        if (o instanceof ResultSet)
            return ResultSet.class;

        return null;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        System.out.println(methodName);

        if (proxy instanceof Driver) {
            if ("acceptsURL".equals(methodName) || "connect".equals(methodName)) {

                String url = (String) args[0];
                if (url.startsWith("jdbc:postgresql-proxy:")) {
                    args[0] = url.replace("jdbc:postgresql-proxy:", "jdbc:postgresql:");
                }
            }
        }


        return invokeAndProxy(proxy, method, args);
    }




}


