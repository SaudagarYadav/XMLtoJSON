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
public class XMLToJson {

	private ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/jsonschema")
	public String jsonS() throws IOException {
		
		String json = getJson("xml");
		return outputAsString("Json", "No Desc", json);
	}

	@GetMapping("/json")
	public String testApi() throws IOException {
		return getJson("xml");
	}

	private String getJson(String xml) {
		String TEST_XML_STRING = xml;
		String jsonPrettyPrintString = null;

		try {
			JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
			jsonPrettyPrintString = xmlJSONObj.toString(4);
			System.out.println(jsonPrettyPrintString);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return jsonPrettyPrintString;
	}


	public String outputAsString(String title, String description, String json) throws IOException {
		return cleanup(outputAsString(title, description, json, null));
	}

	public void outputAsPOJO(String title, String description, String json, String packageName,
			String outputDirectory) throws IOException {
		String schema = outputAsString(title, title, json);

		File fDirectory = new File(outputDirectory);
		if (!fDirectory.exists())
			FileUtils.forceMkdir(fDirectory);

		JCodeModel codeModel = new JCodeModel();
		SchemaMapper mapper = new SchemaMapper();
		mapper.generate(codeModel, title, packageName, schema);
		codeModel.build(fDirectory);
	}

	private String outputAsString(String title, String description, String json, JsonNodeType type)
			throws IOException {
		JsonNode jsonNode = objectMapper.readTree(json);
		StringBuilder output = new StringBuilder();
		output.append("{");

		for (Iterator<String> iterator = jsonNode.fieldNames(); iterator.hasNext();) {
			String fieldName = iterator.next();

			JsonNodeType nodeType = jsonNode.get(fieldName).getNodeType();

			output.append(convertNodeToStringSchemaNode(jsonNode, nodeType, fieldName));
		}

		output.append("}");

		return output.toString();
	}

	private String convertNodeToStringSchemaNode(JsonNode jsonNode, JsonNodeType nodeType, String key)
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

	private String cleanup(String dirty) {
		return dirty.replace("},}", "}}");
	}
}
