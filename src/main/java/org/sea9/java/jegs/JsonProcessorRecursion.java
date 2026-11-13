package org.sea9.java.jegs;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

public class JsonProcessorRecursion implements JsonProcessor.Action {
	@Override
	public void exec(
		JsonToken token,
		String key,
		String[] current,
		String[] lookup,
		boolean found,
		Object value
	) {
		ArrayList<String> lstc = new ArrayList<>(Arrays.asList(current));
		ArrayList<String> lstl = new ArrayList<String>(Arrays.asList(lookup));
		lstc.removeAll(lstl);

		if (found && key != null && value != null && (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY)) {
			System.out.printf("%-9s %-21s %s %s (\"%s\")%n", key, token, lstl, lstc, value);

			// TODO: Testing!!!
			try {
				String[] look1 = {"branch100", "leaf1000"};
				String[] look2 = {"branch101", "leaf1013"};
				JsonProcessor._process(
					((TreeNode) value).traverse(), look1, true,
					(t, k, curr, look, fd, val) -> {
						if (fd) {
							System.out.printf("%s -> %s%n", Arrays.asList(look1), val);
						}
					}
				);
				JsonProcessor._process(
					((TreeNode) value).traverse(), look2, true,
					(t, k, curr, look, fd, val) -> {
						if (fd) {
							System.out.printf("%s -> %s%n", Arrays.asList(look2), val);
						}
					}
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
