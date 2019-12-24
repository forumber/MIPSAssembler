// Test at https://csfieldguide.org.nz/en/interact  ives/mips-assembler/
package MIPSAssembler;


import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class MIPSAssembler {

    public static Map<String, Map<String, String>> lookUpTable = new HashMap<>();

    public static Scanner consoleInput = new Scanner(System.in);

    // TEST
    public static void printLookUpTable() {
        lookUpTable.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " instructions: ");
            entry.getValue().entrySet().forEach(entryIn -> {
                System.out.println(entryIn.getKey() + " " + entryIn.getValue());
            });
        });
    }

    public static void fillLookUpTable() throws FileNotFoundException {
        Scanner lookUpTableFile = new Scanner(new File(Constants.lookUpTableFileName));
        int lineCounter = 0;

        lookUpTable.put(Constants.TYPE_I, new HashMap<>());
        lookUpTable.put(Constants.TYPE_J, new HashMap<>());
        lookUpTable.put(Constants.TYPE_R, new HashMap<>());
        lookUpTable.put(Constants.TYPE_MEMORY, new HashMap<>());
        lookUpTable.put(Constants.TYPE_REGISTER, new HashMap<>());

        while (lookUpTableFile.hasNextLine()) {

            String nextLine = lookUpTableFile.nextLine();
            lineCounter++;

            // TODO: Change theInstruction variable name
            if (!(nextLine.startsWith("#") || nextLine.isEmpty())) {
                String[] theInstruction = nextLine.split(" ");

                if (lookUpTable.containsKey(theInstruction[0])) {
                    if (lookUpTable.get(theInstruction[0]).containsKey(theInstruction[1]) || lookUpTable.get(theInstruction[0]).containsValue(theInstruction[2])) {
                        System.err.println("");
                        System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                        System.err.println("Line " + lineCounter + ": The instruction or opcode/function is already being used!");
                        System.exit(1);
                    } else {
                        lookUpTable.get(theInstruction[0]).put(theInstruction[1], theInstruction[2]);
                    }
                } else {
                    System.err.println("");
                    System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                    System.err.println("Line " + lineCounter + ": " + theInstruction[0] + " is not a valid instruction type!");
                    System.exit(1);
                }
            }
        }
        lookUpTableFile.close();
    }

    

    public static String assemble(String instructionToDecode) {
        String instructionToDecodeType = "";

        instructionToDecode = instructionToDecode.replace(", ", " ");
        instructionToDecode = instructionToDecode.replace(",", " ");

        String[] instrParts = instructionToDecode.split(" ");

        for (Entry<String, Map<String, String>> i : lookUpTable.entrySet()) {
            for (Entry<String, String> j : i.getValue().entrySet()) {
                if (j.getKey().equals(instrParts[0])) {
                    instructionToDecodeType = i.getKey();
                }
            }
        }

        if (instructionToDecodeType.isEmpty()) {
            return (Constants.errorTag + "Instruction " + instrParts[0] + " is not a valid instruction!");
        }

        switch (instructionToDecodeType) {
            case Constants.TYPE_I:
                return iTypeAssemble(instrParts);
            case Constants.TYPE_R:
                return rTypeAssemble(instrParts);

            /*case Constants.TYPE_J:
                jTypeAssemble(instrParts);
                break;
            case Constants.TYPE_MEMORY:
                memoryTypeAssemble(instrParts);
                break;*/
            default:
                return null;

        }

    }

    public static List<String> assembleBatch(List<String> instructionsToDecode) {
        List<String> assembledInstructionsToReturn = new ArrayList<>();

        int lineCounter = 0;

        for (String i : instructionsToDecode) {
            String assembledInstruction = assemble(i);
            lineCounter++;
            if (assembledInstruction.startsWith(Constants.errorTag)) {
                System.err.println("");
                System.err.println("An error has occurred while assembling the instruction");
                System.err.println("Line " + lineCounter + ": " + assembledInstruction.replace(Constants.errorTag, ""));
                return null;
            }else{
                assembledInstructionsToReturn.add(assembledInstruction);
            }
        }

        return assembledInstructionsToReturn;
    }

    public static void batchMode() {
        System.out.println("BATCH MODE");

        String inputFileName, outputFileName;
        List<String> instructionsToWrite;
        Scanner inputFile;
        FileWriter outputFile;

        List<String> instructionsToAssemble = new ArrayList<>();

        while (true) {
            System.out.println("");
            System.out.print("Enter name of source file: ");
            inputFileName = consoleInput.nextLine();
            if (!inputFileName.contains(".src")) {
                inputFileName += ".src";
            }

            try {
                inputFile = new Scanner(new File(inputFileName));
                break;
            } catch (FileNotFoundException ex) {
                System.err.println("File not found!");
            }
        }

        while (inputFile.hasNextLine()) {
            instructionsToAssemble.add(inputFile.nextLine());
        }

        instructionsToWrite = assembleBatch(instructionsToAssemble);

        if (instructionsToWrite != null) {
            while (true) {
                System.out.print("Enter name of output file: ");
                outputFileName = consoleInput.nextLine();
                if (!outputFileName.contains(".obj")) {
                    outputFileName += ".obj";
                }

                try {
                    outputFile = new FileWriter(outputFileName);
                    break;
                } catch (IOException ex) {
                    System.err.println(outputFileName + "could not created!");
                }
            }

            for (String i : instructionsToWrite) {
                try {
                    outputFile.write(i);
                    outputFile.write("\n");
                } catch (IOException ex) {
                    System.out.println("Could not write to file " + outputFileName);
                }
            }

            try {
                outputFile.close();
                System.out.println("File write operation has been completed successfully!");
            } catch (IOException ex) {
                System.out.println("Could not write to file " + outputFileName);
            }
        }

    }

    public static void interactiveMode() {
        System.out.println("INTERACTIVE MODE");

        List<String> instructionsToAssemble = new ArrayList<>();
        List<String> instructionsToPrint;

        System.out.println("");
        System.out.println("Enter instructions (-1 to start assembling):");

        while (true) {
            String theInstructionFromTerminalInput = consoleInput.nextLine();

            if (theInstructionFromTerminalInput.equals("-1")) {
                instructionsToPrint = assembleBatch(instructionsToAssemble);
                break;
            } else {
                instructionsToAssemble.add(theInstructionFromTerminalInput);
            }
        }

        if (instructionsToPrint != null) {
            System.out.println("");
            System.out.println("Assembled instructions:");
            for (String i : instructionsToPrint) {
                System.out.println(i);
            }
        }
    }

    public static void main(String[] args) {
        //System.out.println("First MIPS Memory Location is 0x" + Long.toHexString(firstMIPSMemoryLocation));
        String answer;

        try {
            fillLookUpTable();
        } catch (FileNotFoundException ex) {
            System.err.println(Constants.lookUpTableFileName + " is not found!");
            System.exit(1);
        }

        printLookUpTable();

        while (true) {
            System.out.println("");
            System.out.println("Java MIPS Assembler");
            System.out.print("(I)nteraction mode / (B)atch mode: ");

            answer = consoleInput.nextLine();

            if (answer.equalsIgnoreCase("b")) {
                batchMode();
            } else if (answer.equalsIgnoreCase("i")) {
                interactiveMode();
            } else {
                System.out.println("Wrong input!");
            }
        }
    }

    public static String registerDecode(String registerToDecode) {
        if (registerToDecode.startsWith("$")) {
            if (lookUpTable.get(Constants.TYPE_REGISTER).containsKey(registerToDecode)) {
                return lookUpTable.get(Constants.TYPE_REGISTER).get(registerToDecode);
            } else {
                return Constants.errorTag + Constants.errorRegisterIsNotFoundMessage;
            }
        } else {
            return Constants.errorTag + Constants.errorIsNotRegisterMessage;
        }
    }

    public static String iTypeAssemble(String[] instrParts) {
        String bin32instr = "";
        String immidieateField = null;
        String registerDecodeResult;
        
        // i type format: opcode (6) rs (5) rt (5) immediate (16)
        bin32instr += lookUpTable.get(Constants.TYPE_I).get(instrParts[0]); // opcode
        
        registerDecodeResult = registerDecode(instrParts[2]); // rs
        if (!registerDecodeResult.startsWith(Constants.errorTag))
            bin32instr += registerDecodeResult;
        else
            return registerDecodeResult;
        
        registerDecodeResult = registerDecode(instrParts[1]); // rt
        if (!registerDecodeResult.startsWith(Constants.errorTag))
            bin32instr += registerDecodeResult;
        else
            return registerDecodeResult;
        
        try {
            immidieateField = Integer.toBinaryString(Integer.valueOf(instrParts[3])); // imm
        } catch (NumberFormatException ex) {
            return Constants.errorTag + Constants.errorImmediateFieldIsNotValidMessage;
        }
        
        if (immidieateField.length() > 16)
            return Constants.errorTag + Constants.errorImmediateIsOutOfRangeMessage;
        
        String oldString = "";
        for(int i = 0; i < 16 - immidieateField.length(); i++){
            oldString += "0";
        }
        immidieateField = oldString + immidieateField;
        
        bin32instr += immidieateField;
       
        return bin32instr;
    }

    public static String rTypeAssemble(String[] instrParts) {
        // r type format: opcode (6) rs (5) rt (5) rd (5) shamt (5) funct (6)
        String bin32instr = "000000"; // opcode
        String registerDecodeResult;
        
        registerDecodeResult = registerDecode(instrParts[2]); // rs
        if (!registerDecodeResult.startsWith(Constants.errorTag))
            bin32instr += registerDecodeResult;
        else
            return registerDecodeResult;
        
        registerDecodeResult = registerDecode(instrParts[3]); // rt
        if (!registerDecodeResult.startsWith(Constants.errorTag))
            bin32instr += registerDecodeResult;
        else
            return registerDecodeResult;
        
        registerDecodeResult = registerDecode(instrParts[1]); // rd
        if (!registerDecodeResult.startsWith(Constants.errorTag))
            bin32instr += registerDecodeResult;
        else
            return registerDecodeResult;
        
        //TODO LOOK FOR SHIFTING INSTRUCTIONS TO ADD TO SHAMT
        bin32instr += "00000"; //if no shifting is present
        bin32instr += lookUpTable.get(Constants.TYPE_R).get(instrParts[0]); // funct
       
        return bin32instr;
    }

}
