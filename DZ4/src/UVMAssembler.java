import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UVMAssembler {

    private static final Map<String, Integer> OPCODES = new HashMap<>();

    static {
        OPCODES.put("LOAD", 0x13);
        OPCODES.put("READ", 0x00);
        OPCODES.put("WRITE", 0xFE);
        OPCODES.put("SGN", 0x05);
    }

    public static void assemble(String inputFile, String outputFile, String logFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile));
             PrintWriter logWriter = new PrintWriter(new FileWriter(logFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                String command = parts[0];
                int operand = Integer.parseInt(parts[1]);

                int opcode = OPCODES.get(command);
                switch (command) {
                    case "LOAD":
                        outputStream.writeByte(opcode);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(operand);
                        break;
                    case "READ":
                        outputStream.writeByte(opcode);
                        outputStream.writeByte(operand);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        break;
                    case "WRITE":
                        outputStream.writeByte(opcode);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(operand);
                        break;
                    case "SGN":
                        outputStream.writeByte(opcode);
                        outputStream.writeByte(0x06); // Fixed value for SGN
                        outputStream.writeByte(0);
                        outputStream.writeByte(0);
                        outputStream.writeByte(operand);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown command: " + command);
                }

                logWriter.println(command + "," + operand);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: UVMAssembler <input_file> <output_file> <log_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        String logFile = args[2];

        assemble(inputFile, outputFile, logFile);
    }
}