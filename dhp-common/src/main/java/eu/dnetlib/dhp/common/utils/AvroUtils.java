package eu.dnetlib.dhp.common.utils;

import java.lang.reflect.Field;

import org.apache.avro.Schema;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public final class AvroUtils {
    
	public final static String primitiveTypePrefix = "org.apache.avro.Schema.Type.";
	
	
	//------------------------ CONSTRUCTORS -------------------
	
	
	private AvroUtils() {}
	
	
	//------------------------ LOGIC --------------------------
	
	
	/**
	 * For a given name of a class generated from Avro schema return 
	 * a JSON schema.
	 * 
	 * Apart from a name of a class you can also give a name of one of enums
	 * defined in {@link org.apache.avro.Schema.Type}; in such case an
	 * appropriate primitive type will be returned.
	 * 
	 * @param typeName fully qualified name of a class generated from Avro schema, 
	 * e.g. {@code eu.dnetlib.dhp.common.avro.Person},
	 * or a fully qualified name of enum defined by 
	 * {@link org.apache.avro.Schema.Type},
	 * e.g. {@link org.apache.avro.Schema.Type.STRING}.
	 * @return JSON string
	 */
	public static Schema toSchema(String typeName) {
		Schema schema = null;
		if(typeName.startsWith(primitiveTypePrefix)){
			String shortName = typeName.substring(
					primitiveTypePrefix.length(), typeName.length());
			schema = getPrimitiveTypeSchema(shortName);
		} else {
			schema = getAvroClassSchema(typeName);
		}
		return schema;
	}
	
	private static Schema getPrimitiveTypeSchema(String shortName){
		Schema.Type type = Schema.Type.valueOf(shortName);
		return Schema.create(type);
	}
	
	private static Schema getAvroClassSchema(String className){
		try {
			Class<?> avroClass = Class.forName(className);
			Field f = avroClass.getDeclaredField("SCHEMA$");
			return (Schema) f.get(null);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Class \""+className+"\" does not exist", e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
