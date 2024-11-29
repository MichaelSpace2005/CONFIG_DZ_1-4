package Tests;

import ConfigCompiler.JsonConverter;
import ConfigCompiler.Parser;
import com.sun.tools.javac.Main;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;



public class ComprehensiveTests {

    @Test
    void testJsonConverterList() {
        List<Object> list = new ArrayList<>();
        list.add("value1");
        list.add(123);
        list.add(new HashMap<>()); 
        String expectedJson = "[\"value1\",123,{}]";
        assertEquals(expectedJson, JsonConverter.toJson(list));
    }

    @Test
    void testJsonConverterMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        map.put("key3", new ArrayList<>()); // Empty List
        String expectedJson = "{\"key1\":\"value1\",\"key2\":123,\"key3\":[]}";
        assertEquals(expectedJson, JsonConverter.toJson(map));
    }

    @Test
    void testJsonConverterString() {
        String str = "test string";
        String expectedJson = "\"test string\"";
        assertEquals(expectedJson, JsonConverter.toJson(str));
    }


    @Test
    void testJsonConverterNumber() {
        Integer num = 42;
        String expectedJson = "42";
        assertEquals(expectedJson, JsonConverter.toJson(num));

    }


    @Test
    void testJsonConverterNull() {

        String expectedJson = "null";
        assertEquals(expectedJson, JsonConverter.toJson(null));

    }



    @Test
    void testParserConstDeclaration() {
        String input = "const MY_CONST = 10;";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(1, statements.size());  // One constant declaration
        assertNull(statements.get(0));    // parseConstDeclaration returns null

        assertEquals(10, parser.constants.get("MY_CONST"));
    }


    @Test
    void testParserString() {

        String input = "'stringValue';";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals("stringValue", statements.get(0));

    }

    @Test
    void testParserNumber() {
        String input = "12345;";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(12345, statements.get(0));

    }

    @Test
    void testParserDictionary() {
        String input = "@{'key1', 'value1', 'key2', 2};";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        Map<String, Object> expectedMap = new LinkedHashMap<>();
        expectedMap.put("key1", "value1");
        expectedMap.put("key2", 2);

        assertEquals(expectedMap, statements.get(0));
    }

    @Test
    void testParserArray() {
        String input = "![ (1, 2, 'three') ];";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        List<Object> expectedList = new ArrayList<>();
        expectedList.add(1);
        expectedList.add(2);
        expectedList.add("three");

        assertEquals(expectedList, statements.get(0));


    }

    @Test
    void testParserExpressionPlus() {
        String input = "(1+2);";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(3, statements.get(0));


    }


    @Test
    void testParserExpressionMinus() {
        String input = "(4-2);";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(2, statements.get(0));


    }

    @Test
    void testParserExpressionMult() {
        String input = "(4*2);";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(8, statements.get(0));


    }

    @Test
    void testParserExpressionDiv() {
        String input = "(4/2);";
        Parser parser = new Parser(input);
        List<Object> statements = parser.parseStatements();
        assertEquals(2, statements.get(0));


    }



    @Test
    void testParserInvalidInputMissingSemicolon() {
        String input = "10";
        Parser parser = new Parser(input);

        assertThrows(RuntimeException.class, parser::parseStatements);

    }

    @Test
    void testParserUndefinedConstant() {
        String input = "UNDEFINED_CONSTANT;";
        Parser parser = new Parser(input);

        assertThrows(RuntimeException.class, parser::parseStatements);

    }

    @Test
    void testMainInvalidInput(@TempDir Path tempDir) throws IOException {
        Path tempFile = Path.of(tempDir.resolve("output.json").toString());
        String outputPath = tempFile.toString();
        String input = "invalid input"; 

        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        String[] args = {"-o", outputPath};
        assertThrows(RuntimeException.class, () -> Main.main(args));
        assertFalse(Files.exists(tempFile));

    }
}