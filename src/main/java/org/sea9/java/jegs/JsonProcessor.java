package org.sea9.java.jegs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

/**
 * Process JSON data using the JsonParser from com.fasterxml.jackson.
 */
public class JsonProcessor {
	/**
	 * Runtime config for the JsonProcessor, parsed from command-line arguments.
	 */
	public static class Config {
		private static final String USAGE = 
				"JsonProcessor options:\n" +
				"   {-i FILE | --input-file=FILE}\n" +
				"   {-o FILE | --output-file=FILE}\n" +
				"   {-p PATH | --lookup-path=PATH}\n" +
				"   {-s | --do-not-skip}";

		private static final String OPT_EQ = "=";
		private static final String OPT_LD = "--";
		private static final String OPT_INP0 = "-i";
		private static final String OPT_INP1 = "--input-file=";
		private static final String OPT_OUT0 = "-o";
		private static final String OPT_OUT1 = "--output-file=";
		private static final String OPT_PTH0 = "-p";
		private static final String OPT_PTH1 = "--lookup-path=";
		private static final String OPT_SKP0 = "-s";
		private static final String OPT_SKP1 = "--do-not-skip";

		private String inFile;
		/**
		 * path of the input file.
		 */
		public String inputFile() {
			return inFile;
		}
		public void setInputFile(String file) {
			inFile = file;
		}

		private String outFile;
		/**
		 * path of the output file.
		 */
		public String outputFile() {
			return outFile;
		}
		public void setOutputFile(String file) {
			outFile = file;
		}

		private String[][] jsonPths;
		/**
		 * JSON paths of the items to lookup, traverse the entire input if null.
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
				lst = new ArrayList<>(Arrays.asList(this.jsonPths));
			}
			lst.add(path);
			jsonPths = lst.toArray(buf);
		}

		private boolean skipChild;
		/**
		 * skip processing of subtrees not in the lookup path, no effect if lookup is null.
		 */
		public boolean isSkipChild() {
			return skipChild;
		}
		public void shouldSkipChild(boolean skip) {
			skipChild = skip;
		}

		public Config() {
			skipChild = true;
		}

		public static final String usage() {
			return USAGE;
		}

		/**
		 * Parse the supplied command-line arguments into a `Config` object.
		 * @param args arguments from the command-line.
		 * @return the corresponding `Config` object.
		 */
		public static final Config parse(String[] args) {
			String key, str;
			int idx, len, cnt = 0;

			Config cfg = new Config();

			if (args.length < 1) {
				throw new RuntimeException(String.format("missing argument%n%s", usage()));
			}

			if (args.length < 2) {
				return cfg;
			}
			for (int i = 0; i < args.length; i ++) {
				len = args[i].length();
				idx = args[i].lastIndexOf(Config.OPT_EQ);
				key = (len >= 5 && args[i].startsWith(Config.OPT_LD) && idx >= 3 && idx < (len - 1)) ? args[i].substring(0, idx) : args[i];
				switch (key) {
					case Config.OPT_INP0:
						i ++;
						if (i >= args.length) throw new RuntimeException("missing input filename in the command-line arguments");
						cfg.inFile = args[i];
						cnt ++;
						break;
					case Config.OPT_INP1:
						cfg.inFile = args[i].substring(idx);
						cnt ++;
						break;
					case Config.OPT_OUT0:
						i ++;
						if (i >= args.length) throw new RuntimeException("missing output filename in the command-line arguments");
						cfg.outFile = args[i];
						cnt ++;
						break;
					case Config.OPT_OUT1:
						cfg.outFile = args[i].substring(idx);
						cnt ++;
						break;
					case Config.OPT_PTH0:
					case Config.OPT_PTH1:
						if (key.equals(Config.OPT_PTH0)) {
							i ++;
							if (i >= args.length) throw new RuntimeException("missing json lookup path in the command-line arguments");
							str = args[i];
						} else {
							str = args[i].substring(idx);
						}
						cfg.setJsonPath(str.split(JavaEgs.REGEX_COMMA));
						cnt ++;
						break;
					case Config.OPT_SKP0:
					case Config.OPT_SKP1:
						cfg.skipChild = false;
						cnt ++;
						break;
					default:
						throw new RuntimeException(String.format("unknown argument %s%n%s", args[i], usage()));
				}
			}
			if (cnt > 0) {
				return cfg;
			}
			return null;
		}
	}

	private static final String ERR_JSON_ROOT = "expecting root to be an object";
	private static final String ERR_INPUT_MISSING = "input file path missing";
	private static final String ERR_CGF_MISSING = "configuration missing";

	/**
	 * The functional interface to invoke on JSON elements received during parsing.
	 */
	public interface Action {
		/**
		 * The abstract function in the Action functional interface.
		 * @param index    index of the looked-up element.
		 * @param sequence number of the match sets.
		 * @param token    current json token from jackson.
		 * @param key      key of the current json item.
		 * @param current  path of keys of the current json item.
		 * @param lookup   path of keys of the looked-up json item.
		 * @param found    target json item reached if true.
		 * @param value    value of the current json item.
		 * @param out      stream to write processed output to.
		 */
		public void exec(
			int index,
			int sequence,
			JsonToken token,
			String key,
			String[] current,
			String[] lookup,
			boolean found,
			Object value,
			PrintStream out
		);
	}

	/**
	 * Process the given JSON data traversed by the given JsonParser.
	 * @param parser  parser to traverse the given JSON data.
	 * @param out     stream to write processed output to.
	 * @param lookups JSON paths of the items to lookup, traverse the entire JSON data if null.
	 * @param skip    skip processing of subtrees not in the lookup path, no effect if lookup is null.
	 * @param action  action to be taken on JSON elements received during parsing.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@SuppressWarnings("null")
	public static final void process(
		JsonParser parser,
		PrintStream out,
		String[][] lookups,
		boolean skip,
		Action action
	) throws JsonParseException, IOException {
		JsonToken token = parser.nextToken();
		if (token != JsonToken.START_OBJECT) {
			throw new RuntimeException(ERR_JSON_ROOT);
		}

		String[] buff = {};
		String curr;
		Stack<String> currPath = new Stack<>();
		List<Stack<String>> lookupPaths = new ArrayList<Stack<String>>();
		int[] heights;

		int idx, lookCnt = 0;
		if (lookups != null && lookups.length > 0) {
			lookCnt = lookups.length;
		}
		heights = new int[lookCnt];
		lookupPaths = new ArrayList<>();
		for (idx = 0; idx < lookCnt; idx ++)
			lookupPaths.add(new Stack<>());

		Stack<String> currArray = new Stack<>();
		Map<String, Integer> arrayIdx = new HashMap<>();
		boolean found = false;
		int seq = 0;

		while (token != null) {
			curr = parser.currentName(); // The method getCurrentName() from the type JsonParser is deprecated
			for (idx = 0; idx < lookCnt; idx ++)
				heights[idx] = lookupPaths.get(idx).size();

			boolean ontrack = false, matched = false;
			switch (token) {
				case START_OBJECT:
					if (lookCnt > 0) {
						for (idx = 0; idx < lookCnt; idx ++) {
							if (lookups[idx][heights[idx]].equals(curr)) {
								ontrack = true;
								if (heights[idx] == lookups[idx].length - 1) { // found!
									matched = true;

									// do not push to stacks here because after 'readValueAsTree', the corresponding END_OBJECT event is not fired
									action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.readValueAsTree(), out);
								} else {
									lookupPaths.get(idx).push(curr);
								}
							}
						}
						if (matched) {
							break;
						} else if (curr != null) {
							if (ontrack || !skip) {
								currPath.push(curr);
							} else {
								parser.skipChildren();
							}
						} else {
							if (!currArray.isEmpty()) {
								if (!arrayIdx.containsKey(currArray.peek())) {
									arrayIdx.put(currArray.peek(), 0);
								} else {
									arrayIdx.merge(currArray.peek(), 1, (a, b) -> a + b);
								}
							}
						}
					} else if (curr != null) {
						currPath.push(curr);
					}
					action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, null, out);
					break;
				case END_OBJECT:
					if (!currPath.isEmpty() && currPath.peek().equals(curr)) {
						currPath.pop();
					}
					for (idx = 0; idx < lookCnt; idx ++) {
						if (heights[idx] > 0 && lookupPaths.get(idx).peek().equals(curr)) {
							lookupPaths.get(idx).pop();
						}
					}
					if (found && curr == null) {
						seq ++;
						found = false;
					}
					break;
				case START_ARRAY:
					if (lookCnt > 0) {
						for (idx = 0; idx < lookCnt; idx ++) {
							if (curr != null && lookups[idx][heights[idx]].startsWith(curr) && lookups[idx][heights[idx]].endsWith("[]") && curr.length() == lookups[idx][heights[idx]].length() - 2) {
								ontrack = true;
								if (heights[idx] == lookups[idx].length - 1) { // found!
									matched = true;

									// do not push to stacks here because after 'readValueAsTree', the corresponding END_ARRAY event is not fired
									action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.readValueAsTree(), out);
								} else {
									lookupPaths.get(idx).push(curr);
								}
							}
						}
						if (matched) {
							break;
						} else if (curr != null) {
							if (ontrack || !skip) {
								currPath.push(curr);
								currArray.push(curr);
							} else {
								parser.skipChildren();
							}
						}
					} else if (curr != null) {
						currPath.push(curr);
					}
					action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, null, out);
					break;
				case END_ARRAY:
					if (!currPath.isEmpty() && currPath.peek().equals(curr)) {
						currPath.pop();
					}
					if (!currArray.isEmpty() && currArray.peek().equals(curr)) {
						arrayIdx.remove(currArray.pop());
					}
					for (idx = 0; idx < lookCnt; idx ++) {
						if (heights[idx] > 0 && lookupPaths.get(idx).peek().equals(curr)) {
							lookupPaths.get(idx).pop();
						}
					}
					break;
				case VALUE_STRING:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.getText(), out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, parser.getText(), out);
					break;
				case VALUE_TRUE:
				case VALUE_FALSE:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.getBooleanValue(), out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, parser.getBooleanValue(), out);
					break;
				case VALUE_NUMBER_INT:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.getLongValue(), out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, parser.getLongValue(), out);
					break;
				case VALUE_NUMBER_FLOAT:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.getDecimalValue(), out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, parser.getDecimalValue(), out);
					break;
				case VALUE_EMBEDDED_OBJECT:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, parser.getEmbeddedObject(), out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, parser.getEmbeddedObject(), out);
					break;
				case VALUE_NULL:
					for (idx = 0; idx < lookCnt; idx ++) {
						if (lookCnt > 0 && lookupPaths.get(idx).size() == (lookups[idx].length - 1) && lookups[idx][lookups[idx].length-1].equals(curr)) {
							matched = true;
							action.exec(idx, seq, token, curr, currPath.toArray(buff), lookupPaths.get(idx).toArray(buff), true, null, out);
						}
					}
					if (!matched) action.exec(-1, -1, token, curr, currPath.toArray(buff), null, false, null, out);
					break;
				// case FIELD_NAME:
				// 	break;
				// case NOT_AVAILABLE:
				// 	break;
				default:
				// 	break;
			}
			if (!found && matched) found = true;

			token = parser.nextToken();
		}
	}

	/**
	 * Process JSON from the given input file and potentially write to the given output file.
	 * @param input   path of the input file.
	 * @param output  path of the output file.
	 * @param lookups JSON paths of the items to lookup, traverse the entire input file if null.
	 * @param skip    skip processing of subtrees not in the lookup path, no effect if lookup is null.
	 * @param action  action to be taken on JSON elements received during parsing.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static final void process(
		String input,
		String output,
		String[][] lookups,
		boolean skip,
		Action action
	) throws JsonParseException, IOException {
		if (input == null) {
			throw new RuntimeException(ERR_INPUT_MISSING);
		}
		JsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createParser(new File(input));

		if (output != null) {
			if (Files.exists(Paths.get(output), LinkOption.NOFOLLOW_LINKS)) {
				throw new RuntimeException(String.format("\"%s\" already exists", output));
			}
			try (PrintStream out = new PrintStream(new FileOutputStream(output))) {
				process(parser, out, lookups, skip, action);
			}
		} else {
			process(parser, System.out, lookups, skip, action);
		}
	}

	/**
	 * Process JSON with inputs from command-line arguments.
	 * @param cfg    parsed command-line arguments.
	 * @param action action to be taken on JSON elements received during parsing.
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static final void process(
		JsonProcessor.Config cfg,
		Action action
	) throws JsonParseException, IOException {
		if (cfg == null) {
			throw new RuntimeException(ERR_CGF_MISSING);
		}

		process(
			cfg.inputFile(),
			cfg.outputFile(),
			cfg.jsonPaths(),
			cfg.isSkipChild(),
			action
		);
	}
}
