package jazz;

import java.lang.reflect.Method;

import jazz.lang.HttpClassLoader;

public class Main {

	public static void main(String[] args) throws Exception {
		String cp = System.getProperty("http.classpath");
		HttpClassLoader classLoader = new HttpClassLoader(cp, Main.class.getClassLoader());
		String className = args[0];
		Class<?> clazz = classLoader.loadClass(className);
		Method main = clazz.getMethod("main", String[].class);
		String[] params = new String[args.length - 1];
		for (int i = 1; i < args.length; i ++) {
			params[i - 1] = args[i];
		}
		main.invoke(null, new Object[]{params});
	}
}
