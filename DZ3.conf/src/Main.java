import ConfigCompiler.JsonConverter;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        try {
            String outputPath = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-o") && i + 1 < args.length) {
                    outputPath = args[i + 1];
                    i++;
                }
            }

            if (outputPath == null) {
                System.err.println("Usage: java Main -o <output_file>");
                System.exit(1);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            StringBuilder inputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                inputBuilder.append(line).append("\n");
            }
            String input = inputBuilder.toString();

            ConfigCompiler.Parser parser = new ConfigCompiler.Parser(input);
            Object parsedObject = parser.parseStatements();

            String jsonOutput = JsonConverter.toJson(parsedObject);


            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(jsonOutput);
            writer.close();

            System.out.println("Conversion successful. Output written to: " + outputPath);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}