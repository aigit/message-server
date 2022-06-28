package com.qlk.message.server.utils;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  JacksonMapper
 *
 *  @author guoyongxiang
 */
public class JacksonMapper {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JacksonMapper() {
	}

	public static ObjectMapper getInstance() {
		return MAPPER;
	}

	/**
	 * Object to Json String
	 * 
	 * @param obj
	 * @return
	 * @throws JsonProcessingException 
	 */
	public static String toJson(Object obj) throws JsonProcessingException {

		ObjectMapper mapper = JacksonMapper.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		mapper.setDateFormat(df);
		return mapper.writeValueAsString(obj);
	}

	/**
	 * Json to List
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @param mapper
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static <T> List<T> toList(String json, Class<T> clazz, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		List<T> list = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
		return list;
	}
	
	/**
	 * Json to List
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = JacksonMapper.getInstance();
		return toList(json, clazz, mapper);
	}

	/**
	 * Json String to Object<br>
	 * String json = "..."; ObjectMapper mapper = JacksonMapper.getInstance();<br>
	 * YourBean bean = mapper.readValue(json, new YourBean().getClass());
	 * 
	 * @param json
	 * @param clazz
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static <T> T toObj(String json, Class<T> clazz, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(json, clazz);
	}
	
	/**
	 * Json String to Object<br>
	 * String json = "..."; ObjectMapper mapper = JacksonMapper.getInstance();<br>
	 * YourBean bean = mapper.readValue(json, new YourBean().getClass());
	 * 
	 * @param json
	 * @param clazz
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static <T> T toObj(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = JacksonMapper.getInstance();
		return toObj(json, clazz, mapper);
	}
	
	/**
	 * JsonNode to Object
	 * @param node
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toObj(JsonNode node, Class<T> clazz, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(node.toString(), clazz);
	}
	
	/**
	 * JsonNode to Object
	 * @param node
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toObj(JsonNode node, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = JacksonMapper.getInstance();
		return toObj(node.toString(), clazz, mapper);
	}
	
	/**
	 * Json String to JsonNode
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static JsonNode toNode(String json, ObjectMapper mapper) throws JsonProcessingException, IOException{
		return mapper.readTree(json);
	}
	
	/**
	 * Json String to JsonNode
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static JsonNode toNode(String json) throws JsonProcessingException, IOException{
		ObjectMapper mapper = JacksonMapper.getInstance();
		return toNode(json, mapper);
	}
	
	/**
	 * Object to JsonNode
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static JsonNode toNode(Object obj) throws JsonProcessingException, IOException{
		String json = toJson(obj);
		return toNode(json);
	}

	/**
	 * 判断一个字符串是否是有效的json
	 *
	 * @param jsonString
	 *
	 * @return true or false
	 *
	 * @author ldl
	 */
	public static boolean isJSONValid(String jsonString) {
		try {
			MAPPER.readTree(jsonString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
