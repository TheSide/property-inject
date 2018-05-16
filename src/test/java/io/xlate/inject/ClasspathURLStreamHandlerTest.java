package io.xlate.inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.enterprise.inject.InjectionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class ClasspathURLStreamHandlerTest {

	ClasspathURLStreamHandler cut;


	@BeforeEach
	public void setup() {
		cut = new ClasspathURLStreamHandler(getClass().getClassLoader());
	}

	@Test
	public void testResourceUrlResolvesNull() throws MalformedURLException {
		URL target = new URL(null, "classpath:does/not/exist.txt", cut);
        @SuppressWarnings("unused")
		InjectionException ex = assertThrows(InjectionException.class, () -> {
			target.openConnection();
        });
	}

	@Test
	public void testResourceUrlFound() throws IOException {
		URL target = new URL(null, "classpath:META-INF/beans.xml", cut);
		URLConnection connection = target.openConnection();
		assertNotNull(connection);
	}

}
