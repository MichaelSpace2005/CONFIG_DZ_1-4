package Tests;

import ConfigCompiler.JsonConverter;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonConverterTest {

    @Test
    void testStringToJson() {
        assertEquals("\"hello\"", JsonConverter.toJson("hello"));
    }

    @Test
    void testIntegerToJson() {
        assertEquals("123", JsonConverter.toJson(123));
    }

    @Test
    void testDoubleToJson() {
        assertEquals("3.14", JsonConverter.toJson(3.14));
    }

    @Test
    void testBooleanToJson() {
        assertEquals("true", JsonConverter.toJson(true));
    }

    @Test
    void testNullToJson() {
        assertEquals("null", JsonConverter.toJson(null));
    }


    @Test
    void testListToJson_empty() {
        List<String> list = new ArrayList<>();
        assertEquals("[]", JsonConverter.toJson(list));
    }

    @Test
    void testListToJson_singleElement() {
        List<String> list = new ArrayList<>();
        list.add("hello");
        assertEquals("[\"hello\"]", JsonConverter.toJson(list));
    }

    @Test
    void testListToJson_multipleElements() {
        List<Object> list = new ArrayList<>();
        list.add("hello");
        list.add(123);
        list.add(true);
        assertEquals("[\"hello\",123,true]", JsonConverter.toJson(list));
    }


    @Test
    void testMapToJson_empty() {
        Map<String, String> map = new HashMap<>();
        assertEquals("{}", JsonConverter.toJson(map));
    }

    @Test
    void testMapToJson_singleEntry() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "John");
        assertEquals("{\"name\":\"John\"}", JsonConverter.toJson(map));
    }
}