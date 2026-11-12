package org.sea9.java.jegs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Examples {
	private static final String JSON = "{\r\n\t\"searchCriteria\": {\r\n\t\t\"cmuMemberCode\": \"{{MEMBER_CODE}}\",        \r\n        \"cmuAccountSerialNumber\": \"001\"\r\n\t},\r\n\t\"timestamp\":\"{{TIMESTAMP}}\",\r\n\t\"reqId\": \"{{REQID}}\"\r\n}";

	protected static final void display() {
		System.out.println(JSON);
	}

	protected static final void serialize() throws JsonProcessingException {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("k00", "Hello");
		map1.put("k01", "There");

		List<Object> lst1 = new ArrayList<>();
		lst1.add("?");
		lst1.add("How");
		lst1.add("do");

		Map<String, Object> map0 = new HashMap<>();
		map0.put("key0", "Greetings");
		map0.put("key1", map1);
		map0.put("key2", lst1);

		ObjectMapper mapper = new ObjectMapper();
		String txt = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map0);
		System.out.println(txt);
	}

    protected static final void deserialize() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
		Map<String, Object> root = mapper.readValue(JSON, typeRef);
		System.out.println(root);
	}
}
