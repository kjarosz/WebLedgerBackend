package com.webledger.webledger.schema;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.webledger.webledger.account.Account;

@RestController
public class SchemaController {

	@GetMapping("/schema")
	public JsonSchema structureSchema(@RequestParam("className") String className)
		throws JsonMappingException
	{
		ObjectMapper mapper = new ObjectMapper();
		JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(mapper);
		JsonSchema schema = schemaGenerator.generateSchema(Account.class);
		return schema;
	}
}
