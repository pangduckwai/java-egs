package org.sea9.java.jegs;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * jegs
 * To run:
 * - mvn exec:java -Dexec.mainClass="org.sea9.java.jegs.JavaEgs" -Dexec.args="input.txt"
 * or
 * - mvn clean install
 * - java -jar target/jegs-X.X.X.jar
 *
 * Resolve dependencies: 
 * - mvn dependency:resolve
 * 
 * Build without install:
 * - mvn clean package
 */
@SpringBootApplication
public class JavaEgs {
	// private static final String FILE = "temp/P1B_OpenAPI_Nov2025Release.postman_collection.json";
	// private static final String[] KEYS = {"item[]", "request", "url", "raw"};
	// private static final String FILE = "temp/P1B_UAT7 PenetrationTest2025.postman_environment.json";
	// private static final String FILE = "temp/test.json";
	// private static final String[] KEYS = {"tree", "branch1", "branch11"};
	// private static final String[] KEYS = {"tree", "branch3", "leaf30[]"};
	// private static final String[] KEYS = {"tree", "branch1", "branch10", "branch101", "leaf1012"};
	// private static final String[] KEYS = {"values[]", "config", "hosts[]", "mtree", "mbranch1", "msg"};
	// private static final String[] KEYS = {"values[]", "config", "hosts[]"};

	public static void main(String[] args) {
		SpringApplication.run(JavaEgs.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			if (args.length < 1) {
				System.out.println("JavaEgs: Java Examples");
				return;
			}
			try {
				switch (args[0].toLowerCase()) {
					case "beans":
						System.out.println("Bean names:");
						String[] names = ctx.getBeanDefinitionNames();
						Arrays.sort(names);
						for (String name : names) {
							System.out.println(name);
						}
						break;
					case "display":
						Examples.display();
						break;
					case "serialize":
						Examples.serialize();
						break;
					case "deserialize":
						Examples.deserialize();
						break;
					case "read":
						if (args.length < 2) {
							System.out.println("JavaEgs:\n Usage: java -jar target\\jegs.jar read {command} [option1 [option2 ...]]");
							return;
						}
						JsonProcessor.process(JsonProcessor.parse(Arrays.copyOfRange(args, 1, args.length)));
						break;
					default:
						System.out.printf("Unknown command '%s'%n", args[0]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
