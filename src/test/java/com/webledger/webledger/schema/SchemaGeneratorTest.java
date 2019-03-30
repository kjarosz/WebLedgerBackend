package com.webledger.webledger.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SchemaGeneratorTest {
	public static class ClassWithPrimitives {
		int primitive;
	}
	
	public final static String CLASS_WITH_PRIMITIVES_SCHEMA = "";

	@Test
	public void testClassWithPrimitives() {
		SchemaGenerator generator = new SchemaGenerator();
		String schema = generator.getSchemaFor(ClassWithPrimitives.class);
		assertEquals(schema, "");
	}
}
