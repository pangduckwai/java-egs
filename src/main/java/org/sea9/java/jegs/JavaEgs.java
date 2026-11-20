package org.sea9.java.jegs;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private static final String APP_NAME = "JavaEgs: Java Examples";
	private static final String APP_BEAN = "Bean names:";
	private static final String CMD_BEAN = "beans";
	private static final String CMD_DISP = "display";
	private static final String CMD_SRLZ = "serialize";
	private static final String CMD_DSLZ = "deserialize";
	private static final String CMD_READ = "read";

	protected static final String ROOT = "root";
	protected static final String EMPTY = "";
	protected static final String PERIOD = ".";
	protected static final String COLON = ":";
	protected static final String FOUND = " ***** FOUND %3d %3d *****";

	protected static final String REGEX_COMMA = "[\\s]*[.][\\s]*";

	public static void main(String[] args) {
		SpringApplication.run(JavaEgs.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			if (args.length < 1) {
				System.out.println(APP_NAME);
				return;
			}
			try {
				switch (args[0].toLowerCase()) {
					case CMD_BEAN:
						System.out.println(APP_BEAN);
						String[] names = ctx.getBeanDefinitionNames();
						Arrays.sort(names);
						for (String name : names) {
							System.out.println(name);
						}
						break;
					case CMD_DISP:
						Examples.display();
						break;
					case CMD_SRLZ:
						Examples.serialize();
						break;
					case CMD_DSLZ:
						Examples.deserialize();
						break;
					case "test":
						JsonProcessor.Config tmp = JsonProcessor.Config.parse(args);
						tmp.usage();
						break;
					case CMD_READ:
						if (args.length < 2) {
							System.out.println("JavaEgs:\n Usage: java -jar target\\jegs.jar read {command} [option1 [option2 ...]]");
							return;
						}

						ObjectMapper mapper = new ObjectMapper();

						JsonProcessor.process(
							JsonProcessor.Config.parse(Arrays.copyOfRange(args, 1, args.length)),
							(idx, seq, token, key, curr, look, found, val, out) -> {
								StringBuilder path = new StringBuilder(ROOT);
								if (curr.length > 0)
									path.append(COLON).append(curr[0]);
								for (int i = 1; i < curr.length; i ++)
									path.append(PERIOD).append(curr[i]);

								String find = EMPTY;
								if (found) {
									find = String.format(FOUND, seq, idx);
									path.append(COLON).append(key);
								} else if (key != null && token != JsonToken.START_OBJECT && token != JsonToken.START_ARRAY) {
									path.append(COLON).append(key);
								}

								if (val != null) {
									if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
										try {
											System.out.printf(
												"%-9s %-21s %-49s%s%n%s%n", key == null ? "<null>" : key, token, path, find,
												mapper.writerWithDefaultPrettyPrinter().writeValueAsString(val)
											);
										} catch (JsonProcessingException e) {
											e.printStackTrace();
										}
									} else {
										System.out.printf("%-9s %-21s %-49s (\"%s\")%s%n", key == null ? "<null>" : key, token, path, val, find);
									}
								} else {
									System.out.printf("%-9s %-21s %-49s%s%n", key == null ? "<null>" : key, token, path, find);
								}
							}
						);
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
