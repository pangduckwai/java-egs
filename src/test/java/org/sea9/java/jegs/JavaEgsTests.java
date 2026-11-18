package org.sea9.java.jegs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

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
		String s5 = null;

		assertFalse(s1.equals(s2));
		assertTrue(s1.equals(s3));
		assertFalse(s1.equals(s4));
		assertThrows(NullPointerException.class, () -> s1.startsWith(s5));
	}

	@Test
	public void splitTest() throws Exception {
		String[] xpt = {"hello", "there", "how", "are", "you", "doing?"};
		String str = "hello, there ,how, are , you, doing?";
		String[] lst = str.split("[\\s]*,[\\s]*");
		assertArrayEquals(xpt, lst);
	}

	private static final String JSON =
		"{\r\n" + //
		"  \"id\": \"8cdb6b7b-fb76-42de-881f-abc44022c5d8\",\r\n" + //
		"  \"name\": \"Test\",\r\n" + //
		"  \"tree\": {\r\n" + //
		"    \"branch0\": {\r\n" + //
		"      \"branch00\": {\r\n" + //
		"        \"leaf000\": \"Hello\",\r\n" + //
		"        \"leaf001\": \"there\",\r\n" + //
		"        \"leaf002\": \"how\"\r\n" + //
		"      },\r\n" + //
		"      \"branch01\": {\r\n" + //
		"        \"leaf010\": \"are\",\r\n" + //
		"        \"leaf011\": \"you\",\r\n" + //
		"        \"leaf012\": \"doing\",\r\n" + //
		"        \"leaf013\": \"?\"\r\n" + //
		"      }\r\n" + //
		"    },\r\n" + //
		"    \"branch1\": {\r\n" + //
		"      \"branch10\": {\r\n" + //
		"        \"branch100\": {\r\n" + //
		"          \"leaf1000\": \"I'm\",\r\n" + //
		"          \"leaf1001\": \"fine\",\r\n" + //
		"          \"leaf1002\": \"thanks\"\r\n" + //
		"        },\r\n" + //
		"        \"branch101\": {\r\n" + //
		"          \"leaf1010\": \"what\",\r\n" + //
		"          \"leaf1011\": \"about\",\r\n" + //
		"          \"leaf1012\": \"you\",\r\n" + //
		"          \"leaf1013\": \"?\"\r\n" + //
		"        }\r\n" + //
		"      },\r\n" + //
		"      \"branch11\": {\r\n" + //
		"        \"leaf110\": \"Nice\",\r\n" + //
		"        \"leaf111\": \",\",\r\n" + //
		"        \"leaf112\": \"good\"\r\n" + //
		"      },\r\n" + //
		"      \"branch12\": {\r\n" + //
		"        \"leaf120\": \"to\",\r\n" + //
		"        \"leaf121\": \"know\"\r\n" + //
		"      }\r\n" + //
		"    },\r\n" + //
		"    \"branch2\": {\r\n" + //
		"      \"leaf20\": \"I'm\",\r\n" + //
		"      \"leaf21\": \"fine\",\r\n" + //
		"      \"leaf22\": \"too\",\r\n" + //
		"      \"leaf23\": \"thanks\"\r\n" + //
		"    },\r\n" + //
		"    \"branch3\": {\r\n" + //
		"      \"leaf30\": [{\r\n" + //
		"        \"id\": 301,\r\n" + //
		"        \"msg\": \"Let's\"\r\n" + //
		"      }, {\r\n" + //
		"        \"id\": 302,\r\n" + //
		"        \"msg\": \"drink\"\r\n" + //
		"      }, {\r\n" + //
		"        \"id\": 303,\r\n" + //
		"        \"msg\": \"tea\"\r\n" + //
		"      }],\r\n" + //
		"      \"leaf31\": [\r\n" + //
		"        \"when\", \"free\"\r\n" + //
		"      ]\r\n" + //
		"    }\r\n" + //
		"  },\r\n" + //
		"  \"keys\": [\r\n" + //
		"    \"id\", \"name\", \"scope\"\r\n" + //
		"  ],\r\n" + //
		"  \"values\": [\r\n" + //
		"    {\r\n" + //
		"      \"key\": \"MEMBER_CODE\",\r\n" + //
		"      \"value\": \"LCHB\",\r\n" + //
		"      \"enabled\": false,\r\n" + //
		"      \"config\": {\r\n" + //
		"        \"env\": \"test\",\r\n" + //
		"        \"hosts\": [\r\n" + //
		"          {\r\n" + //
		"            \"id\": \"001\",\r\n" + //
		"            \"mtree\": {\r\n" + //
		"              \"mbranch0\": {\r\n" + //
		"                \"msg\": \"msg1\"\r\n" + //
		"              },\r\n" + //
		"              \"mbranch1\": {\r\n" + //
		"                \"msg\": \"hello/how/are/you\"\r\n" + //
		"              }\r\n" + //
		"            }\r\n" + //
		"          }, {\r\n" + //
		"            \"id\": \"002\",\r\n" + //
		"            \"mtree\": {\r\n" + //
		"              \"mbranch0\": \"msg2\",\r\n" + //
		"              \"mbranch1\": {\r\n" + //
		"                \"msg\": \"i'm/fine/thanks\"\r\n" + //
		"              }\r\n" + //
		"            }\r\n" + //
		"          }\r\n" + //
		"        ]\r\n" + //
		"      }\r\n" + //
		"    }, {\r\n" + //
		"      \"key\": \"HOST\",\r\n" + //
		"      \"value\": \"api-uat-p1.cmu.org.hk/u7\",\r\n" + //
		"      \"enabled\": true\r\n" + //
		"    }, {\r\n" + //
		"      \"key\": \"CLIENT_ID\",\r\n" + //
		"      \"value\": \"7HG2ABIKSSNT8LGHI7OBLAFYNZ1DZWUZ\",\r\n" + //
		"      \"enabled\": false,\r\n" + //
		"      \"config\": {\r\n" + //
		"        \"env\": \"prod\",\r\n" + //
		"        \"hosts\": [\r\n" + //
		"          {\r\n" + //
		"            \"id\": \"003\",\r\n" + //
		"            \"mtree\": {\r\n" + //
		"              \"mbranch0\": \"msg3\",\r\n" + //
		"              \"mbranch1\": {\r\n" + //
		"                \"msg\": \"what/about/you\"\r\n" + //
		"              }\r\n" + //
		"            }\r\n" + //
		"          }, {\r\n" + //
		"            \"id\": \"004\",\r\n" + //
		"            \"mtree\": {\r\n" + //
		"              \"mbranch0\": \"msg4\",\r\n" + //
		"              \"mbranch1\": {\r\n" + //
		"                \"msg\": \"i'm/fine/too\"\r\n" + //
		"              }\r\n" + //
		"            }\r\n" + //
		"          }\r\n" + //
		"        ]\r\n" + //
		"      }\r\n" + //
		"    }, {\r\n" + //
		"      \"key\": \"TESTER\",\r\n" + //
		"      \"value\": \"XXXXXX\",\r\n" + //
		"      \"enabled\": true,\r\n" + //
		"      \"note\": \"here!!!\"\r\n" + //
		"    }\r\n" + //
		"  ],\r\n" + //
		"  \"_scope\": \"environment\",\r\n" + //
		"  \"_at\": \"2025-09-17T03:49:12.773Z\",\r\n" + //
		"  \"_using\": \"Postman/0.0.3\"\r\n" + //
		"}";

	@Test
	public void parseTest1a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch11", "leaf110"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("Nice", val);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest1b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch11", "leaf110"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("Nice", val);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest2a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"keys[]"}};
		String expects = "[\"id\",\"name\",\"scope\"]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest2b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"keys[]"}};
		String expects = "[\"id\",\"name\",\"scope\"]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest3a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch10"}};
		String expects = "{\"branch100\":{\"leaf1000\":\"I'm\",\"leaf1001\":\"fine\",\"leaf1002\":\"thanks\"},\"branch101\":{\"leaf1010\":\"what\",\"leaf1011\":\"about\",\"leaf1012\":\"you\",\"leaf1013\":\"?\"}}";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isObject());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest3b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch10"}};
		String expects = "{\"branch100\":{\"leaf1000\":\"I'm\",\"leaf1001\":\"fine\",\"leaf1002\":\"thanks\"},\"branch101\":{\"leaf1010\":\"what\",\"leaf1011\":\"about\",\"leaf1012\":\"you\",\"leaf1013\":\"?\"}}";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isObject());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest4a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf30[]", "msg"}};
		String[] expects = {"Let's", "drink", "tea"};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
				}
			}
		);
		assertArrayEquals(expects, result.toArray(conv));
	}

	@Test
	public void parseTest4b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf30[]", "msg"}};
		String[] expects = {"Let's", "drink", "tea"};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
				}
			}
		);
		assertArrayEquals(expects, result.toArray(conv));
	}

	@Test
	public void parseTest5a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf30[]"}};
		String expects = "[{\"id\":301,\"msg\":\"Let's\"},{\"id\":302,\"msg\":\"drink\"},{\"id\":303,\"msg\":\"tea\"}]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest5b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf30[]"}};
		String expects = "[{\"id\":301,\"msg\":\"Let's\"},{\"id\":302,\"msg\":\"drink\"},{\"id\":303,\"msg\":\"tea\"}]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest6a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "config", "hosts[]", "mtree", "mbranch1"}};
		String[] expects = {
			"{\"msg\":\"hello/how/are/you\"}",
			"{\"msg\":\"i'm/fine/thanks\"}",
			"{\"msg\":\"what/about/you\"}",
			"{\"msg\":\"i'm/fine/too\"}"
		};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					assertTrue(node.isObject());
					result.add(node.toString());
				}
			}
		);
		assertArrayEquals(expects, result.toArray(conv));
	}

	@Test
	public void parseTest6b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "config", "hosts[]", "mtree", "mbranch1"}};
		String[] expects = {
			"{\"msg\":\"hello/how/are/you\"}",
			"{\"msg\":\"i'm/fine/thanks\"}",
			"{\"msg\":\"what/about/you\"}",
			"{\"msg\":\"i'm/fine/too\"}"
		};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					assertTrue(node.isObject());
					result.add(node.toString());
				}
			}
		);
		assertArrayEquals(expects, result.toArray(conv));
	}

	@Test
	public void parseTest7a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf31[]"}};
		String expects = "[\"when\",\"free\"]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest7b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch3", "leaf31[]"}};
		String expects = "[\"when\",\"free\"]";
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest8a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "note"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("here!!!", val);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest8b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "note"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("here!!!", val);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch10", "branch101", "leaf1013"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(token, key, curr, look, found, val, out) -> {
				ArrayList<String> lstc = new ArrayList<>(Arrays.asList(curr));
				ArrayList<String> lstl = new ArrayList<String>(Arrays.asList(look));
				if (key != null) {
					if (found) {
						lstl.add(key);
						result.add(key);
					} else if (token != JsonToken.START_OBJECT && token != JsonToken.START_ARRAY) {
						lstc.add(key);
					}
				}
				lstc.removeAll(lstl);
				String fnd = found ? " ***** FOUND! *****" : "";
				if (key != null && val != null) {
					System.out.printf("%-9s %-21s %s %s (\"%s\")%s%n", key, token, lstl, lstc, val, fnd);
				} else if (key != null) {
					System.out.printf("%-9s %-21s %s %s%s%n", key, token, lstl, lstc, fnd);
				} else if (val != null) {
					System.out.printf("no key    %-21s %s %s (\"%s\")%s%n", token, lstl, lstc, val, fnd);
				}
			}
		);
		assertTrue(result.size() > 0);
	}
}
