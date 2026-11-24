package org.sea9.java.jegs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

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

	private static final int matchArrayKey(String key, String path) {
		if (key == null) return -5;
		if (!path.startsWith(key)) return -4;
		if (!path.endsWith("]")) return -3;

		int len = path.length();
		int idx = path.indexOf("[");
		if (idx != key.length()) return -2; // includes the case "[" not found

		if (idx == (len-2)) return 0; // "[]" means all items within the array

		try {
			return Integer.parseInt(path.substring(idx + 1, len - 1)) + 1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Test
	public void matchTest() throws Exception {
		String[] keys = {"there", null, "hello"};
		String[] paths = {"hello", "hello[", "hell[]", "hello[]", "helloo[]", "hello]", "hello[49]", "hello[X]"};
		int cnt = 0, rst;
		for (String key : keys) {
			for (String path : paths) {
				rst = matchArrayKey(key, path);
				// System.out.printf("%6s <- %-9s => %d%n", key == null ? "<null>" : key, path, rst);
				if (cnt < 8) {
					assertEquals(-4, rst);
				} else if (cnt < 16) {
					assertEquals(-5, rst);
				} else {
					switch (cnt) {
						case 16:
							assertEquals(-3, rst);
							break;
						case 17:
							assertEquals(-3, rst);
							break;
						case 18:
							assertEquals(-4, rst);
							break;
						case 19:
							assertEquals(0, rst);
							break;
						case 20:
							assertEquals(-2, rst);
							break;
						case 21:
							assertEquals(-2, rst);
							break;
						case 22:
							assertEquals(50, rst);
							break;
						case 23:
							assertEquals(-1, rst);
							break;
					}
				}
				cnt ++;
			}
		}
	}

	private static final String getPath(Stack<String> stack) {
		try {
			Iterator<String> itr = stack.iterator();

			StringBuilder sb = new StringBuilder(itr.next());
			for (; itr.hasNext();) {
				sb.append(JavaEgs.PERIOD).append(itr.next());
			}

			return sb.toString();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	@Test
	public void stackTest() throws Exception {
		Stack<String> stack = new Stack<>();
		stack.push("this");
		stack.push("is");
		stack.push("a");
		stack.push("test");
		stack.push("only");

		String pop, pth;
		int cnt = 0;
		while (!stack.isEmpty()) {
			pop = stack.pop();
			pth = getPath(stack);
			switch (cnt) {
				case 0:
					assertEquals("only", pop);
					assertEquals("this.is.a.test", pth);
					break;
				case 1:
					assertEquals("test", pop);
					assertEquals("this.is.a", pth);
					break;
				case 2:
					assertEquals("a", pop);
					assertEquals("this.is", pth);
					break;
				case 3:
					assertEquals("is", pop);
					assertEquals("this", pth);
					break;
				case 4:
					assertEquals("this", pop);
					assertNull(pth);
					break;
			}
			cnt ++;
		}
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
		"            \"struc\": {\r\n" + //
		"              \"value0\": \"001\",\r\n" + //
		"              \"value1\": {\r\n" + //
		"                \"msg\": \"msg1\"\r\n" + //
		"              },\r\n" + //
		"              \"value2\": {\r\n" + //
		"                \"msg\": \"hello/how/are/you\"\r\n" + //
		"              },\r\n" + //
		"              \"value3\": \"warning 0\"\r\n" + //
		"            }\r\n" + //
		"          }, {\r\n" + //
		"            \"id\": \"002\",\r\n" + //
		"            \"struc\": {\r\n" + //
		"              \"value1\": \"msg2\",\r\n" + //
		"              \"value2\": {\r\n" + //
		"                \"msg\": \"i'm/fine/thanks\"\r\n" + //
		"              },\r\n" + //
		"              \"value3\": \"testing one two three\",\r\n" + //
		"              \"value4\": \"testing four five six\"\r\n" + //
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
		"            \"struc\": {\r\n" + //
		"              \"value0\": \"003\",\r\n" + //
		"              \"value1\": \"msg3\",\r\n" + //
		"              \"value2\": {\r\n" + //
		"                \"msg\": \"what/about/you\"\r\n" + //
		"              },\r\n" + //
		"              \"value4\": \"huh?\"\r\n" + //
		"            }\r\n" + //
		"          }, {\r\n" + //
		"            \"id\": \"004\",\r\n" + //
		"            \"struc\": {\r\n" + //
		"              \"value0\": \"004\",\r\n" + //
		"              \"value1\": \"msg4\",\r\n" + //
		"              \"value2\": {\r\n" + //
		"                \"msg\": \"i'm/fine/too\"\r\n" + //
		"              },\r\n" + //
		"              \"value3\": \"this is a test\",\r\n" + //
		"              \"value4\": \"this is also a test\"\r\n" + //
		"            }\r\n" + //
		"          }, {\r\n" + //
		"            \"id\": \"005\",\r\n" + //
		"            \"struc\": {\r\n" + //
		"              \"value0\": \"005\",\r\n" + //
		"              \"value1\": \"msg5\",\r\n" + //
		"              \"value3\": \"may day\",\r\n" + //
		"              \"value4\": \"may day may day\"\r\n" + //
		"            }\r\n" + //
		"          }\r\n" + //
		"        ]\r\n" + //
		"      }\r\n" + //
		"    }, {\r\n" + //
		"      \"key\": \"TESTER\",\r\n" + //
		"      \"value\": \"XXXXXX\",\r\n" + //
		"      \"enabled\": true,\r\n" + //
		"      \"note\": \"here!!!\",\r\n" + //
		"      \"config\": {\r\n" + //
		"        \"env\": \"uat\",\r\n" + //
		"        \"hosts\": [\r\n" + //
		"          {\r\n" + //
		"            \"id\": \"006\",\r\n" + //
		"            \"struc\": {\r\n" + //
		"              \"value0\": \"006\",\r\n" + //
		"              \"value2\": {\r\n" + //
		"                \"msg\": \"good/to/know!\"\r\n" + //
		"              },\r\n" + //
		"              \"value3\": \"this is a drill\",\r\n" + //
		"              \"value4\": \"this is not a drill\"\r\n" + //
		"            }\r\n" + //
		"          }\r\n" + //
		"        ]\r\n" + //
		"      }\r\n" + //
		"    }\r\n" + //
		"  ],\r\n" + //
		"  \"_scope\": \"environment\",\r\n" + //
		"  \"_at\": \"2025-09-17T03:49:12.773Z\",\r\n" + //
		"  \"_using\": \"Postman/0.4.1\"\r\n" + //
		"}";

	@Test
	public void parseTest1a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"tree", "branch1", "branch11", "leaf110"}};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("Nice", val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("Nice", val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isObject());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isObject());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest6a() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "config", "hosts[]", "struc", "value2"}};
		String[] expects = {
			"{\"msg\":\"hello/how/are/you\"}",
			"{\"msg\":\"i'm/fine/thanks\"}",
			"{\"msg\":\"what/about/you\"}",
			"{\"msg\":\"i'm/fine/too\"}",
			"{\"msg\":\"good/to/know!\"}"
		};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					assertTrue(node.isObject());
					result.add(node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
				}
			}
		);
		assertArrayEquals(expects, result.toArray(conv));
	}

	@Test
	public void parseTest6b() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {{"values[]", "config", "hosts[]", "struc", "value2"}};
		String[] expects = {
			"{\"msg\":\"hello/how/are/you\"}",
			"{\"msg\":\"i'm/fine/thanks\"}",
			"{\"msg\":\"what/about/you\"}",
			"{\"msg\":\"i'm/fine/too\"}",
			"{\"msg\":\"good/to/know!\"}"
		};
		String[] conv = {};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					assertTrue(node.isObject());
					result.add(node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					JsonNode node = (JsonNode) val;
					result.add(node.toString());
					assertTrue(node.isArray());
					assertEquals(expects, node.toString());
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("here!!!", val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
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
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					result.add((String) val);
					assertEquals("here!!!", val);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
				}
			}
		);
		assertTrue(result.size() > 0);
	}

	@Test
	public void parseTest9() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {
			{"values[]", "config", "hosts[]", "struc", "value1"},
			{"values[]", "config", "hosts[]", "struc", "value2", "msg"}
		};
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, true,
			(idx, seq, token, key, curr, look, found, val, out) -> {
				if (found) {
					if (idx == 0) result.add(key);
					switch (seq) {
						case 0:
							if (idx == 0) {
								JsonNode node = (JsonNode) val;
								assertEquals("{\"msg\":\"msg1\"}", node.toString());
							} else {
								assertEquals("hello/how/are/you", val);
							}
							break;
						case 1:
							assertEquals(idx == 0 ? "msg2" : "i'm/fine/thanks", val);
							break;
						case 2:
							assertEquals(idx == 0 ? "msg3" : "what/about/you", val);
							break;
						case 3:
							assertEquals(idx == 0 ? "msg4" : "i'm/fine/too", val);
							break;
						case 4:
							assertEquals(0, idx);
							break;
						case 5:
							assertEquals(1, idx);
							break;
						default:
							assertTrue(false);
					}
				}
			}
		);
		assertEquals(5, result.size());
	}

	@Test
	public void parseTest() throws Exception {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(JSON);
		String[][] lookups = {
			{"values[]", "config", "hosts[]", "struc", "value2", "msg"},
			{"values[]", "config", "hosts[]", "struc", "value1"},
			{"values[]", "config", "hosts[]", "struc", "value3"},
			{"values[]", "config", "hosts[]", "struc", "value4"}
		};
		// String[][] lookups = {{"values[]", "config", "hosts[]", "struc", "value2", "msg"}};
		// String[][] lookups = {
		// 	{"values[]", "config", "hosts[]", "struc", "value2", "msg"},
		// 	{"values[]", "config", "hosts[]", "struc", "value1"},
		// 	{"tree", "branch1", "branch10", "branch101", "leaf1013"},
		// 	{"values[]", "config", "hosts[]", "struc", "value0"}
		// };
		ArrayList<String> result = new ArrayList<>();
		JsonProcessor.process(
			parser, null, lookups, false,
			(idx, seq, token, key, curr, look, found, val, out) -> {
				StringBuilder path = new StringBuilder(JavaEgs.ROOT);
				if (curr.length > 0)
					path.append(JavaEgs.COLON).append(curr[0]);
				for (int i = 1; i < curr.length; i ++)
					path.append(JavaEgs.PERIOD).append(curr[i]);

				String find = JavaEgs.EMPTY;
				if (found) {
					assertArrayEquals(look, curr);
					assertNotEquals(-1, idx);
					assertNotEquals(-1, seq);
					if (idx == 0) result.add(key);
					find = String.format(JavaEgs.FOUND, seq, idx);
					path.append(JavaEgs.COLON).append(key);
				} else if (key != null && token != JsonToken.START_OBJECT && token != JsonToken.START_ARRAY) {
					path.append(JavaEgs.COLON).append(key);
				}

				if (val != null) {
					System.out.printf("%-9s %-21s %-49s (\"%s\")%s%n", key == null ? "<null>" : key, token, path, val, find);
				} else {
					System.out.printf("%-9s %-21s %-49s%s%n", key == null ? "<null>" : key, token, path, find);
				}
			}
		);
		assertTrue(result.size() > 0);
	}
}
