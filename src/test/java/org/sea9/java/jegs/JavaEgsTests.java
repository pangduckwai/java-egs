package org.sea9.java.jegs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JavaEgsTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void nullTest() throws Exception {
		String s1 = "Hello";
		String s2 = null;
		String s3 = "Hello";
		String s4 = "There";

		System.out.printf("nullTest() - 1. %s%n", s1.equals(s2)); // false
		System.out.printf("nullTest() - 2. %s%n", s1.equals(s3)); // true
		System.out.printf("nullTest() - 3. %s%n", s1.equals(s4)); // false
	}

	@Test
	public void splitTest() throws Exception {
		String str = "hello, there ,how, are , you, doing?";
		String[] lst = str.split("[\\s]*,[\\s]*");
		for (String itm : lst) {
			System.out.printf("splitTest() - \"%s\"%n", itm);
		}
	}
}
