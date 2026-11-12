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

		System.out.printf("1. %s\n", s1.equals(s2)); // false
		System.out.printf("2. %s\n", s1.equals(s3)); // true
		System.out.printf("3. %s\n", s1.equals(s4)); // false
	}
}
