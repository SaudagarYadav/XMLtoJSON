package com.api.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.jsonschema2pojo.SchemaMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.sun.codemodel.JCodeModel;

@RestController
public class TestController {

	@GetMapping("/test")
	public String jsonS() throws IOException {
		
		String json = "{\"sectors\": [{\"times\":[{\"intensity\":30," +
                "\"start\":{\"hour\":8,\"minute\":30},\"end\":{\"hour\":17,\"minute\":0}}," +
                "{\"intensity\":10,\"start\":{\"hour\":17,\"minute\":5},\"end\":{\"hour\":23,\"minute\":55}}]," +
                "\"id\":\"dbea21eb-57b5-44c9-a953-f61816fd5876\"}]}";
		TestController.outputAsString("Json", "No Desc", json);
		return "Test";
	}

	@GetMapping("/")
	public String testApi() throws IOException {

		int PRETTY_PRINT_INDENT_FACTOR = 4;
		String TEST_XML_STRING = "<note>\r\n" + "<to>Tove</to>\r\n" + "<from>Jani</from>\r\n"
				+ "<heading>Reminder</heading>\r\n" + "<body>Don't forget me this weekend!</body>\r\n" + "</note>";
		String jsonPrettyPrintString = null;

		try {
			JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
			jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
			System.out.println(jsonPrettyPrintString);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return jsonPrettyPrintString;
	}

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Map<String, JsonNodeType> map = new HashMap<>();

	public static String outputAsString(String title, String description, String json) throws IOException {
		return cleanup(outputAsString(title, description, json, null));
	}

	public static void outputAsFile(String title, String description, String json, String filename) throws IOException {
		FileUtils.writeStringToFile(new File(filename), cleanup(outputAsString(title, description, json)), "utf8");
	}

	public static void outputAsPOJO(String title, String description, String json, String packageName,
			String outputDirectory) throws IOException {
		String schema = TestController.outputAsString(title, title, json);

		File fDirectory = new File(outputDirectory);
		if (!fDirectory.exists())
			FileUtils.forceMkdir(fDirectory);

		JCodeModel codeModel = new JCodeModel();
		SchemaMapper mapper = new SchemaMapper();
		mapper.generate(codeModel, title, packageName, schema);
		codeModel.build(fDirectory);
	}

	private static String outputAsString(String title, String description, String json, JsonNodeType type)
			throws IOException {
		JsonNode jsonNode = objectMapper.readTree(json);
		StringBuilder output = new StringBuilder();
		output.append("{");

		if (type == null)
			output.append("\"title\": \"" + title + "\", \"description\": \"" + description
					+ "\", \"type\": \"object\", \"properties\": {");

		for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext();) {
			String fieldName = iterator.next();

			JsonNodeType nodeType = jsonNode.get(fieldName).getNodeType();

			output.append(convertNodeToStringSchemaNode(jsonNode, nodeType, fieldName));
		}

		if (type == null)
			output.append("}");

		output.append("}");

		return output.toString();
	}

	private static String convertNodeToStringSchemaNode(JsonNode jsonNode, JsonNodeType nodeType, String key)
			throws IOException {
		StringBuilder result = new StringBuilder("\"" + key + "\": { \"type\": \"");

		JsonNode node = null;
		switch (nodeType) {
		case ARRAY:
			node = jsonNode.get(key).get(0);
			result.append("array\", \"items\": { \"properties\":");
			result.append(outputAsString(null, null, node.toString(), JsonNodeType.ARRAY));
			result.append("}},");
			break;
		case BOOLEAN:
			result.append("boolean\" },");
			break;
		case NUMBER:
			result.append("number\" },");
			break;
		case OBJECT:
			node = jsonNode.get(key);
			result.append("object\", \"properties\": ");
			result.append(outputAsString(null, null, node.toString(), JsonNodeType.OBJECT));
			result.append("},");
			break;
		case STRING:
			result.append("string\" },");
			break;
		}

		return result.toString();
	}

	private static String cleanup(String dirty) {
		JSONObject rawSchema = new JSONObject(new JSONTokener(dirty));
		org.everit.json.schema.Schema schema = SchemaLoader.load(rawSchema);
		return schema.toString();
	}
}
