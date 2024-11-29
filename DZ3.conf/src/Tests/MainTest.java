package Tests;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

    private final InputStream systemIn = System.in;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(System.out);
    }


    @Test
    public void testMainWithValidInput(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("output.json");
        String outputPath = tempFile.toString();
        String input = "statement1;\nstatement2;";

        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        String[] args = {"-o", outputPath};
        try {
            Main.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String expectedOutput = "Conversion successful. Output written to: " + outputPath + System.lineSeparator();
        assertEquals(expectedOutput, testOut.toString());

        assertTrue(Files.exists(tempFile));
    }

    @Test
    public void testMainWithNoArguments() {
        String[] args = {};
        try {
            Main.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String expectedOutput = "Usage: java Main -o <output_file>" + System.lineSeparator();
        assertEquals(expectedOutput, testOut.toString());
    }


    @Test
    public void testMainWithInvalidOutputPath(@TempDir Path tempDir) throws IOException{
        String outputPath = tempDir.resolve("nonexistent_directory").resolve("output.json").toString();

        String input = "statement1;\nstatement2;";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        String[] args = {"-o", outputPath};

        try {
            Main.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String expectedOutputStart = "Error: "; // Expecting a file not found error.
        assertTrue(testOut.toString().startsWith(expectedOutputStart));
    }
}