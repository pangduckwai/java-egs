package org.sea9.java.jegs;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

public class JsonProcessorOutput implements JsonProcessor.Action {
	private boolean ready;
	public final boolean isReady() {
		return ready;
	}

	private String[] ks;
	public final String keys() {
		if (ks == null || ks.length < 1) {
			return null;
		}

		StringBuilder sb = new StringBuilder(ks[0]);
		for (int i = 1; i < ks.length; i ++) {
			sb.append(".").append(ks[i]);
		}
		return sb.toString();
	}

	private TreeNode out;
	public final TreeNode output() {
		return out;
	}

	public JsonProcessorOutput() {
		ready = false;
	}

	@Override
	public void exec(
		JsonToken token,
		String key,
		String[] current,
		String[] lookup,
		boolean found,
		Object value
	) {
		if (found && key != null && value != null && (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY)) {
			this.out = (TreeNode) value;
			this.ks = lookup;
			this.ready = true;
		}
	}
}
