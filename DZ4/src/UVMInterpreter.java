import java.io.*;

public class UVMInterpreter {

    public static void interpret(String inputFile, String outputFile, int startAddress, int endAddress) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            byte[] memory = new byte[256];
            int accumulator = 0;

            while (inputStream.available() > 0) {
                int opcode = inputStream.readUnsignedByte();
                inputStream.readUnsignedByte(); // Skip unused byte
                inputStream.readUnsignedByte(); // Skip unused byte
                inputStream.readUnsignedByte(); // Skip unused byte
                int operand = inputStream.readUnsignedByte();

                switch (opcode) {
                    case 0x13: // LOAD
                        accumulator = operand;
                        break;
                    case 0x00: // READ
                        accumulator = memory[accumulator + operand] & 0xFF;
                        break;
                    case 0xFE: // WRITE
                        memory[operand] = (byte) accumulator;
                        break;
                    case 0x05: // SGN
                        int value = memory[operand] & 0xFF;
                        accumulator = (value > 0) ? 1 : (value < 0) ? -1 : 0;
                        memory[operand] = (byte) accumulator;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown opcode: " + opcode);
                }
            }

            for (int i = startAddress; i <= endAddress; i++) {
                writer.println(i + "," + (memory[i] & 0xFF));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("Usage: UVMInterpreter <input_file> <output_file> <start_address> <end_address>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];
        int startAddress = Integer.parseInt(args[2]);
        int endAddress = Integer.parseInt(args[3]);

        interpret(inputFile, outputFile, startAddress, endAddress);
    }
}