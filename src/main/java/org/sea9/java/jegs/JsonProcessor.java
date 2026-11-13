package org.sea9.java.jegs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

	/*
	 * Process the given input JSON file.
	 * @param input // 
	 * @param lookup // 
	 * @param operation // 
	 * @param skip // 
	 * @throws JsonParseException
	 * @throws IOException
	 */

public class JsonProcessor {
	public static class Config {
		private String inFile;
		/**
		 * path of the input file.
		 */
		public String inputFile() {
			return inFile;
		}
		public void setInputFile(String file) {
			this.inFile = file;
		}

		private String outFile;
		public String outputFile() {
			return outFile;
		}
		public void setOutputFile(String file) {
			this.outFile = file;
		}

		private String[][] jsonPths;
		/**
		 * JSON path of the item to lookup, run thru the entire file if null.
		 */
		public String[][] jsonPaths() {
			return jsonPths;
		}
		public void setJsonPaths(String[][] paths) {
			jsonPths = paths;
		}
		public void setJsonPath(String[] path) {
			List<String[]> lst;
			String[][] buf = {{}};
			if (this.jsonPths == null) {
				lst = new ArrayList<>();
			} else {
				lst = Arrays.asList(this.jsonPths);
			}
			lst.add(path);
			this.jsonPths = lst.toArray(buf);
		}

		private String oper;
		/**
		 * identifier of the operation.
		 */
		public String operation() {
			return oper;
		}
		public void setOperation(String ops) {
			this.oper = ops;
		}

		private boolean skipChild;
		/**
		 * skip processing of subtrees not in the lookup path, no effect if lookup is null.
		 */
		public boolean isSkipChild() {
			return skipChild;
		}
		public void shouldSkipChild(boolean skip) {
			this.skipChild = skip;
		}

		public Config() {
			skipChild = true;
			oper = "default";
		}

		public final String usage() {
			return
				"JsonProcessor options:\n" +
				"   {-i FILE | --input-file=FILE}\n" +
				"   {-p PATH | --lookup-path=PATH}\n" +
				"   {-o OPER | --operation=OPER}\n" +
				"   {-s | --do-not-skip}";
		}
	}

	public static final Config parse(String[] args) {
		String key, str;
		int idx, len, cnt = 0;

		Config cfg = new Config();
		for (int i = 0; i < args.length; i ++) {
			len = args[i].length();
			idx = args[i].lastIndexOf("=");
			key = (len >= 5 && args[i].startsWith("--") && idx >= 3 && idx < (len - 1)) ? args[i].substring(0, idx) : args[i];
			switch (key) {
				case "-i":
					i ++;
					if (i >= args.length) throw new RuntimeException("missing input filename in the command line arguments");
					cfg.inFile = args[i];
					cnt ++;
					break;
				case "--input-file=":
					cfg.inFile = args[i].substring(idx);
					cnt ++;
					break;
				case "-p":
				case "--lookup-path=":
					if (key.equals("-p")) {
						i ++;
						if (i >= args.length) throw new RuntimeException("missing json lookup path in the command line arguments");
						str = args[i];
					} else {
						str = args[i].substring(idx);
					}
					cfg.setJsonPath(str.split("[\\s]*[.][\\s]*"));
					cnt ++;
					break;
				case "-o":
					i ++;
					if (i >= args.length) throw new RuntimeException("missing operation identifier in the command line arguments");
					cfg.oper = args[i];
					cnt ++;
					break;
				case "--operation=":
					cfg.oper = args[i].substring(idx);
					cnt ++;
					break;
				case "-s":
				case "--do-not-skip":
					cfg.skipChild = false;
					cnt ++;
					break;
				default:
					throw new RuntimeException(String.format("unknown argument %s%n%s", args[i], cfg.usage()));
			}
		}
		if (cnt > 0) {
			return cfg;
		}
		return null;
	}

	public interface Action {
		public void exec(
			JsonToken token,  // current json token from jackson
			String key,       // key of the current json item
			String[] current, // path of keys of the current json item 
			String[] lookup,  // path of keys of the looked-up json item
			boolean found,    // target json item reached if true
			Object value      // value of the current json item
		);
	}

	private static final void parse(
		JsonParser parser,
		String[][] lkup,
		boolean skip,
		Action action
	) throws JsonParseException, IOException {
		JsonToken token = parser.nextToken();
		if (token != JsonToken.START_OBJECT) {
			throw new RuntimeException("expecting root to be an object");
		}

		String[] lookup = null;
		if (lkup != null && lkup.length > 0) {
			lookup = lkup[0];
		}

		String key;
		String[] keys = {};
		Stack<String> stckCurr = new Stack<>();
		Stack<String> stckLook = new Stack<>();
		boolean fnd;
		int idx;
		while (token != null) {
			key = parser.currentName(); // The method getCurrentName() from the type JsonParser is deprecated
			idx = stckLook.size();

			switch (token) {
				case START_OBJECT:
					if (lookup != null) {
						if (idx < lookup.length && lookup[idx].equals(key)) {
							stckCurr.push(key);
							stckLook.push(key);
						} else if (key != null) {
							if (skip) {
								parser.skipChildren();
							} else {
								stckCurr.push(key);
							}
						}
						fnd = (stckLook.size() == lookup.length);
					} else {
						if (key != null) {
							stckCurr.push(key);
						}
						fnd = false;
					}
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, fnd ? parser.readValueAsTree() : null);
					if (fnd) {
						// this is needed because after 'readValueAsTree', the corresponding END_OBJECT event is not fired
						// by (fnd == true), implicitly it means: (lookup != null), stckLook.size() == lookup.length
						if (!stckLook.isEmpty() && stckLook.peek().equals(key)) stckLook.pop();
						if (!stckCurr.isEmpty() && !skip && stckCurr.peek().equals(key)) stckCurr.pop();
					}
					break;
				case END_OBJECT:
					if (!stckCurr.isEmpty() && stckCurr.peek().equals(key)) {
						stckCurr.pop();
					}
					if (idx > 0 && stckLook.peek().equals(key)) {
						stckLook.pop();
					}
					break;
				case START_ARRAY:
					if (lookup != null) {
						if (key != null && idx < lookup.length && lookup[idx].startsWith(key) && lookup[idx].endsWith("[]")) {
							stckCurr.push(key);
							stckLook.push(key);
						} else if (key != null) {
							if (skip) {
								parser.skipChildren();
							} else {
								stckCurr.push(key);
							}
						}
						fnd = (stckLook.size() == lookup.length);
					} else {
						if (key != null) {
							stckCurr.push(key);
						}
						fnd = false;
					}
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, fnd ? parser.readValueAsTree() : null);
					if (fnd) {
						// this is needed because after 'readValueAsTree', the corresponding END_ARRAY event is not fired
						// same as START_OBJECT above
						if (!stckLook.isEmpty() && stckLook.peek().equals(key)) stckLook.pop();
						if (!stckCurr.isEmpty() && !skip && stckCurr.peek().equals(key)) stckCurr.pop();
					}
					break;
				case END_ARRAY:
					if (!stckCurr.isEmpty() && stckCurr.peek().equals(key)) {
						stckCurr.pop();
					}
					if (idx > 0 && stckLook.peek().equals(key)) {
						stckLook.pop();
					}
					break;
				case VALUE_STRING:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, parser.getText());
					break;
				case VALUE_TRUE:
				case VALUE_FALSE:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, parser.getBooleanValue());
					break;
				case VALUE_NUMBER_INT:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, parser.getLongValue());
					break;
				case VALUE_NUMBER_FLOAT:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, parser.getDecimalValue());
					break;
				case VALUE_EMBEDDED_OBJECT:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, parser.getEmbeddedObject());
					break;
				case VALUE_NULL:
					fnd = lookup != null && stckLook.size() == (lookup.length -1) && lookup[lookup.length-1].equals(key);
					action.exec(token, key, stckCurr.toArray(keys), stckLook.toArray(keys), fnd, null);
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

	private static class Result {
		public String key;
		public TreeNode out;
	};
	private static final Result run(
		JsonParser parser,
		String[][] lookup,
		boolean skip
	) {
		JsonProcessorOutput action = new JsonProcessorOutput();
		new Thread() {
			public final void run() {
				try {
					parse(parser, lookup, skip, action);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		int count = 19;
		while (!action.isReady() && count > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			count --;
		}

		if (!action.isReady()) {
			throw new RuntimeException("Parse failed");
		}
		Result rslt = new Result();
		rslt.key = action.keys();
		rslt.out = action.output();
		return rslt;
	}

	/**
	 * Process the given input JSON file.
	 * @param input // path of the input file.
	 * @param lookup // JSON path of the item to lookup, run thru the entire file if null.
	 * @param operation // identifier of the operation.
	 * @param skip // skip processing of subtrees not in the lookup path, no effect if lookup is null.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static final void process(
		String input,
		String output,
		String[][] lookup,
		String operation,
		boolean skip
	) throws JsonParseException, IOException {
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(new File(input));

		switch (operation) {
			case "output":
				PrintStream pnt;
				if (output != null) {
					pnt = new PrintStream(new FileOutputStream(output));
				} else {
					pnt = System.out;
				}

				Result target = run(parser, lookup, skip);
				if (lookup == null || lookup.length <= 1) {
					pnt.printf("\"%s\": ", target.key);
					ObjectMapper mapper = new ObjectMapper();
					String txt = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(target.out);
					pnt.println(txt);
				} else {
					pnt.println("Work In Progress!!!!"); // TODO HERE!!!
				}

				break;
			case "display":
				// default behaviour
			default:
				parse(
					parser, lookup, skip,
					(token, key, curr, look, found, val) -> {
						ArrayList<String> lstc = new ArrayList<>(Arrays.asList(curr));
						ArrayList<String> lstl = new ArrayList<String>(Arrays.asList(look));
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
		}
	}

	public static final void process(JsonProcessor.Config cfg) throws JsonParseException, IOException {
		if (cfg == null) {
			throw new RuntimeException("configuration missing");
		}

		process(
			cfg.inputFile(),
			cfg.outputFile(),
			cfg.jsonPaths(),
			cfg.operation(),
			cfg.isSkipChild()
		);
	}
}
