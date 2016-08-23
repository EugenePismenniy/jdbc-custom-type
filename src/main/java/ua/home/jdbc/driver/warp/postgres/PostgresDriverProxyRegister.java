package ua.home.jdbc.driver.warp.postgres;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Base64;

public final class PostgresDriverProxyRegister implements InvocationHandler {

	static {
		try {
			DriverManager.registerDriver((Driver) newProxy(new org.postgresql.Driver()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static Object newProxy(Object target) {
		Class<?> clazz = defineInterface(target);
		return clazz == null ? target
				: Proxy.newProxyInstance(Driver.class.getClassLoader(), new Class[]{clazz}, new PostgresDriverProxyRegister(target));
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


	private final Object target;

	private PostgresDriverProxyRegister(Object target) {
		this.target = target;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		String methodName = method.getName();

		if (proxy instanceof ResultSet) {
			Object invokeResult = method.invoke(target, args);
			if (invokeResult != null) {
				if ("getBigDecimal".equals(methodName)) {
					// округляем до 2-х знаков после запятой
					BigDecimal bigDecimal = (BigDecimal) invokeResult;
					invokeResult = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else if ("getString".equals(methodName)) {
					// убираем пробелы в начале и конце строки, делаем строку в верхнем регистре
					String s = (String) invokeResult;
					invokeResult = s.trim().toUpperCase();
				}
			}

			return invokeResult;
		}

		if (proxy instanceof Driver) {
			if ("acceptsURL".equals(methodName) || "connect".equals(methodName)) {
				// меняем префикс на оригинал
				String url = (String) args[0];
				if (url.startsWith("jdbc:postgresql-proxy:")) {
					args[0] = url.replace("jdbc:postgresql-proxy:", "jdbc:postgresql:");
				}
			}
		}

		return invokeAndProxy(method, args);
	}

	public Object invokeAndProxy(Method method, Object[] args) throws Throwable {
		Object returnValue = method.invoke(target, args);
		return newProxy(returnValue);
	}
}


