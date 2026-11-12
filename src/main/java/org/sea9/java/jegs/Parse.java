package org.sea9.java.jegs;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class Parse {
	public interface ParseAction {
		public void exec(JsonParser p, JsonToken t, String key, String[] stack, Object value);
	}

	// private static final int LOOK_NOOP = 0;
	// private static final int LOOK_FOUND = 1;
	// private static final int LOOK_CONTN = 2;
	// private static final int LOOK_ARRYS = 3;
	// private static final int LOOK_ARRYE = 4;

	// private static final int look(JsonToken token, String[] lookup, int idx, String key) {
	// 	if (idx >= lookup.length) {
	// 		throw new RuntimeException("index out of range"); // should not happen
	// 	}
	// 	if (key == null) {
	// 		return LOOK_NOOP;
	// 	}

	// 	switch (token) {
	// 		case START_OBJECT:
	// 			if (key.equals(lookup[idx])) {
	// 				if (idx == (lookup.length - 1)) {
	// 					return LOOK_FOUND;
	// 				} else {
	// 					return LOOK_CONTN;
	// 				}
	// 			}
	// 			return LOOK_NOOP;
	// 		case START_ARRAY:
	// 			if (key.startsWith(lookup[idx]) && lookup[idx].endsWith("[]")) {
	// 				if (idx == (lookup.length - 1)) {
	// 					return -1; // found
	// 				} else {
	// 					return 1; // key in lookup path, continue
	// 				}
	// 			}
	// 			return 0; // noop, continue
	// 		case VALUE_STRING:
	// 		case VALUE_TRUE:
	// 		case VALUE_FALSE:
	// 		case VALUE_NUMBER_INT:
	// 		case VALUE_NUMBER_FLOAT:
	// 		case VALUE_NULL:
	// 		case VALUE_EMBEDDED_OBJECT:
	// 			if (key.equals(lookup[idx])) {
	// 				if (idx == (lookup.length - 1)) {
	// 					return -1; // found
	// 				} else {
	// 					return 1; // key in lookup path, continue
	// 				}
	// 			}
	// 			return LOOK_NOOP;
	// 		// case FIELD_NAME:
	// 		// 	break;
	// 		// case END_OBJECT:
	// 		// case END_ARRAY:
	// 		// case NOT_AVAILABLE:
	// 		// 	break;
	// 		default:
	// 			return LOOK_NOOP;
	// 	}
	// }

	// values[].config.hosts[].tree.branch1.msg
	protected static final void run(
		String input, String[] lookup,
		ParseAction action
	) throws JsonParseException, IOException {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(new File(input));

		JsonToken token = parser.nextToken();
		if (token != JsonToken.START_OBJECT) {
			throw new RuntimeException("expecting root to be an object");
		}

		String key;
		String[] keys = {};
		Stack<String> stack = new Stack<>();
		int idx;
		while (token != null) {
			key = parser.getCurrentName();
			idx = stack.size();

			switch (token) {
				case START_OBJECT:
					if (idx < lookup.length && lookup[idx].equals(key)) {
						stack.push(key);
					}
					action.exec(parser, token, key, stack.toArray(keys), null);
					break;
				case END_OBJECT:
					if (idx > 0 && stack.peek().equals(key)) {
						stack.pop();
					}
					action.exec(parser, token, key, stack.toArray(keys), null);
					break;
				case START_ARRAY:
					if (key != null && idx < lookup.length && key.startsWith(lookup[idx]) && lookup[idx].endsWith("[]")) {
						stack.push(key);
					}
					action.exec(parser, token, key, stack.toArray(keys), null);
					break;
				case END_ARRAY:
					if (idx > 0 && stack.peek().equals(key)) {
						stack.pop();
						System.out.printf("%s <- %s\n", key, stack);
					}
					action.exec(parser, token, key, stack.toArray(keys), null);
					break;
				case VALUE_STRING:
					action.exec(parser, token, key, stack.toArray(keys), parser.getText());
					break;
				case VALUE_TRUE:
				case VALUE_FALSE:
					action.exec(parser, token, key, stack.toArray(keys), parser.getBooleanValue());
					break;
				case VALUE_NUMBER_INT:
					action.exec(parser, token, key, stack.toArray(keys), parser.getLongValue());
					break;
				case VALUE_NUMBER_FLOAT:
					action.exec(parser, token, key, stack.toArray(keys), parser.getDecimalValue());
					break;
				case VALUE_EMBEDDED_OBJECT:
					action.exec(parser, token, key, stack.toArray(keys), parser.getEmbeddedObject());
					break;
				case VALUE_NULL:
					action.exec(parser, token, key, stack.toArray(keys), null);
					break;
				// case FIELD_NAME:
				// 	break;
				// case NOT_AVAILABLE:
				// 	break;
				default:
				// 	break;
			}

			token = parser.nextToken();
		}
	}
}
