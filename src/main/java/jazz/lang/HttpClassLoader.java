package jazz.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import jazz.util.HttpClient;

public class HttpClassLoader extends ClassLoader {

	private String[] classpaths = new String[]{"."};
	private ClassLoader parent;
	
	public HttpClassLoader(String classpath, ClassLoader parent) {
		super();
		if (classpath != null) {
			classpaths = classpath.split(";");
		}
		this.parent = parent;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			// First, check if the class has already been loaded
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				//long t0 = System.nanoTime();
				try {
					if (parent != null) {
						c = parent.loadClass(name);
					}
				} catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
				}
				
				if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
					//long t1 = System.nanoTime();
					c = findClass(name);
					
                    // this is the defining class loader; record the stats
                    //sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    //sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    //sun.misc.PerfCounter.getFindClasses().increment();
				}
			}
			return c;
		}
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return super.getResources(name);
	}
	
	@Override
	public URL getResource(String name) {
		return super.getResource(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		return super.getResourceAsStream(name);
	}
	
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		for (String cp : classpaths) {
			if (cp.toLowerCase().endsWith(".jar")) {
				byte[] b = getBytes(cp, name);
				if (b != null) {
					return defineClass(name, b, 0, b.length);
				}
			}
		}
		throw new ClassNotFoundException(name);
	}
	
	private byte[] getBytes(String url, String name) {
		HttpClient hc = new HttpClient();
		hc.setConnectionTimeout(5000);
		hc.setReadTimeout(20000);
		InputStream inputStream = hc.get(url);
		JarInputStream jis = null;
		try {
			jis = new JarInputStream(inputStream, true);
			JarEntry entry = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((entry = jis.getNextJarEntry()) != null) {
				String className = entry.getName().replace('\\', '.').replace('/', '.');
				if ((name + ".class").equals(className)) {
					byte[] buf = new byte[2048];
					int num;
					while ((num = jis.read(buf, 0, 2048)) != -1) {
						out.write(buf, 0, num);
					}
					return out.toByteArray();
				}
			}
		} catch (IOException e) {
			return null;
		} finally {
			try {jis.close();} catch (Exception e) {}
			try {inputStream.close();} catch (Exception e) {}
		}	
		return null;
	}	
}
