// Test at https://csfieldguide.org.nz/en/interactives/mips-assembler/
package MIPSAssembler;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class MIPSAssembler {
    
    public static long firstMIPSMemoryLocation = 0x0L;
    public static Map<String, Map<String, String>> lookUpTable = new HashMap<>();
    public static Map<String, List<String>> permittedOperands = new HashMap<>();
    public static Map<String, Map<String, Integer>> customOperands = new HashMap<>();
    public static Map<String, PseudoInstruction> pseudoInstructions = new HashMap<>();
    public static Map<String, Integer> labelIndex = new HashMap<>();
    public static Map<String, Long> labelAddress = new HashMap<>();
    public static Scanner consoleInput = new Scanner(System.in);
    public static boolean isRandomlyGeneratedFileCreated = false;

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

        permittedOperands.put(Constants.TYPE_I, new ArrayList<String>() {
            {
                add(Constants.OP_TYPE_IMM);
                add(Constants.OP_TYPE_RS);
                add(Constants.OP_TYPE_RT);
                add(Constants.OP_TYPE_LABEL);
            }
        });
        permittedOperands.put(Constants.TYPE_R, new ArrayList<String>() {
            {
                add(Constants.OP_TYPE_IMM);
                add(Constants.OP_TYPE_RS);
                add(Constants.OP_TYPE_RT);
                add(Constants.OP_TYPE_RD);
            }
        });

        while (lookUpTableFile.hasNextLine()) {

            String nextLine = lookUpTableFile.nextLine();
            if (nextLine.contains("#") && !nextLine.startsWith("#")) {
                nextLine = nextLine.substring(0, nextLine.indexOf("#"));
            }
            lineCounter++;

            // TODO: Change theInstruction variable name
            if (!(nextLine.startsWith("#") || nextLine.isEmpty())) {
                if (nextLine.startsWith(Constants.TYPE_FIRSTMIPSMEMORYLOCATION)) {
                    String[] memoryParts = nextLine.split(" ");

                    if (memoryParts.length == 2) {
                        try {
                            long test = Long.decode(memoryParts[1]);
                            firstMIPSMemoryLocation = Long.decode(memoryParts[1]);
                        } catch (NumberFormatException e) {
                            System.err.println("");
                            System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                            System.err.println("Line " + lineCounter + ": Please enter a valid hex variable for first MIPS memory location!");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("");
                        System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                        System.err.println("Line " + lineCounter + ": Please enter a valid hex variable for first MIPS memory location!");
                        System.exit(1);
                    }
                } else if (nextLine.startsWith(Constants.TYPE_PSEUDO)) {
                    PseudoInstruction temp = new PseudoInstruction(nextLine);
                    pseudoInstructions.put(temp.instructionName, temp);
                } else {
                    String[] theInstruction = nextLine.split(" ");

                    if (theInstruction.length == 3 || theInstruction.length == 4) {
                        if (lookUpTable.containsKey(theInstruction[0])) {
                            if (lookUpTable.get(theInstruction[0]).containsKey(theInstruction[1]) || lookUpTable.get(theInstruction[0]).containsValue(theInstruction[2])) {
                                System.err.println("");
                                System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                                System.err.println("Line " + lineCounter + ": The instruction or opcode/function is already being used!");
                                System.exit(1);
                            } else {
                                lookUpTable.get(theInstruction[0]).put(theInstruction[1], theInstruction[2]);
                                if (theInstruction.length == 4) {
                                    try {
                                        String[] customOperandsInLookUpTable = theInstruction[3].split(",");
                                        for (String i : customOperandsInLookUpTable) {
                                            if (!permittedOperands.get(theInstruction[0]).contains(i)) {
                                                System.err.println("");
                                                System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                                                System.err.println("Line " + lineCounter + ": The custom operand " + i + " is not allowed for type " + theInstruction[0]);
                                                System.exit(1);
                                            }
                                        }
                                        customOperands.put(theInstruction[1], new HashMap<String, Integer>() {
                                            {
                                                put(Constants.OP_TYPE_RD, Arrays.asList(customOperandsInLookUpTable).indexOf(Constants.OP_TYPE_RD) + 1);
                                                put(Constants.OP_TYPE_RS, Arrays.asList(customOperandsInLookUpTable).indexOf(Constants.OP_TYPE_RS) + 1);
                                                put(Constants.OP_TYPE_RT, Arrays.asList(customOperandsInLookUpTable).indexOf(Constants.OP_TYPE_RT) + 1);
                                                put(Constants.OP_TYPE_IMM, Arrays.asList(customOperandsInLookUpTable).indexOf(Constants.OP_TYPE_IMM) + 1);
                                                put(Constants.OP_TYPE_LABEL, Arrays.asList(customOperandsInLookUpTable).indexOf(Constants.OP_TYPE_LABEL) + 1);
                                            }
                                        });
                                    } catch (Exception e) {
                                        System.err.println("");
                                        System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                                        System.err.println("Line " + lineCounter + ": Custom operands are not allowed for type: " + theInstruction[0]);
                                        System.exit(1);
                                    }
                                }
                            }
                        } else {
                            System.err.println("");
                            System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                            System.err.println("Line " + lineCounter + ": " + theInstruction[0] + " is not a valid instruction type!");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("");
                        System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                        System.err.println("Line " + lineCounter + ": Entered field is not valid!");
                        System.exit(1);
                    }
                }
            }
        }
        if (firstMIPSMemoryLocation == 0x0L) {
            System.err.println("");
            System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
            System.err.println("Please enter a valid " + Constants.TYPE_FIRSTMIPSMEMORYLOCATION + " value to " + Constants.TYPE_FIRSTMIPSMEMORYLOCATION + "!");
            System.exit(1);
        }
        
        lookUpTableFile.close();
    }

    public static String assemble(String instructionToDecode, int currentInstrLine) {

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
                return iTypeAssemble(instrParts, currentInstrLine);
            case Constants.TYPE_R:
                return rTypeAssemble(instrParts);

            case Constants.TYPE_J:
                return jTypeAssemble(instrParts);

            case Constants.TYPE_MEMORY:
                return memoryTypeAssemble(instrParts);

            default:
                return null;

        }

    }

    public static List<String> removeBlanksAndComments(List<String> stringList) {
        List<String> toReturn = new ArrayList<>();

        for (String i : stringList) {
            String temp = i;
            
            while (temp.startsWith(String.valueOf((char) 32)) || temp.startsWith(String.valueOf((char) 9))) {
                temp = temp.substring(1);
            }
            
            if (!temp.isEmpty() && !temp.startsWith("#"))
                toReturn.add(temp);
        }

        return toReturn;
    }
    
    public static List<String> parseAndAddPseudoInstructions(List<String> instructionsToDecode) {
        List<String> toReturn = new ArrayList<>();
        
        for(String temp: instructionsToDecode) {
            String[] instrParts = temp.split(" ");
            
            if (pseudoInstructions.containsKey(instrParts[0])) {
                toReturn.addAll(PseudoInstruction.parse(pseudoInstructions.get(instrParts[0]), temp));
            } else {
                toReturn.add(temp);
            }
        }

        return toReturn;
    }

    public static List<String> assembleBatch(List<String> instructionsToDecode) {
        List<String> instructionsToDecodeWithoutBlanks = removeBlanksAndComments(instructionsToDecode);
        List<String> instructionsWithParsedPseudoInstructions = parseAndAddPseudoInstructions(instructionsToDecodeWithoutBlanks);
        List<String> labellessInstructionsToDecode = findAllLabelIndexesAndAddresses(instructionsWithParsedPseudoInstructions);
        List<String> assembledInstructionsAsBinary = new ArrayList<>();
        int instructionLineCounter = 0;
        for (String i : labellessInstructionsToDecode) {
            if (!i.isEmpty()) {
                String assembledInstruction = assemble(i, instructionLineCounter);
                instructionLineCounter++;
                if (assembledInstruction.startsWith(Constants.errorTag)) {
                    System.err.println("");
                    System.err.println("An error has occurred while assembling the instruction");
                    int lineNumber = 0;
                    for (int currLine = 0; currLine < instructionsToDecode.size(); currLine++) {
                        if (instructionsToDecode.get(currLine).contains(i)) {
                            lineNumber = currLine;
                            break;
                        }
                    }
                    System.err.println("Line " + (lineNumber + 1) + ": " + assembledInstruction.replace(Constants.errorTag, ""));
                    return null;
                } else {
                    assembledInstructionsAsBinary.add(assembledInstruction);
                }
            }

        }

        List<String> assembledAsHex = new ArrayList<>();

        for (String toConvert : assembledInstructionsAsBinary) {
            String temp = Long.toHexString(Long.parseLong(toConvert, 2));
            String oldString = "";
            for (int i = 0; i < 8 - temp.length(); i++) {
                oldString += "0";
            }
            temp = oldString + temp;

            assembledAsHex.add(temp);
        }

        return assembledAsHex;
    }

    public static void batchMode() {
        System.out.println("BATCH MODE");

        String inputFileName, outputFileName;
        List<String> instructionsToWrite;
        Scanner inputFile;
        FileWriter outputFile;

        List<String> instructionsToAssemble = new ArrayList<>();

        while (true) {
            if (isRandomlyGeneratedFileCreated)
            {
                System.out.println("");
                Constants.randomlyGeneratedFileMessage.forEach((theMessage) -> {
                    System.out.println(theMessage);
                });
            }
            System.out.println("");
            System.out.print("Enter name of source file: ");
            inputFileName = consoleInput.nextLine();
            if (!inputFileName.contains(".")) {
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
        
        inputFile.close();

        instructionsToWrite = assembleBatch(instructionsToAssemble);

        if (instructionsToWrite != null) {
            while (true) {
                System.out.print("Enter name of output file: ");
                outputFileName = consoleInput.nextLine();
                if (!outputFileName.contains(".")) {
                    outputFileName += ".obj";
                }

                try {
                    outputFile = new FileWriter(outputFileName);
                    break;
                } catch (IOException ex) {
                    System.err.println(outputFileName + " could not created!");
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
        System.out.println("Enter instructions (type assemble to start assembling):");

        while (true) {
            String theInstructionFromTerminalInput = consoleInput.nextLine();

            if (theInstructionFromTerminalInput.equals("assemble")) {
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

    public static String registerDecode(String registerToDecode) {
        if (registerToDecode.startsWith("$")) {
            if (lookUpTable.get(Constants.TYPE_REGISTER).containsKey(registerToDecode)) {
                return lookUpTable.get(Constants.TYPE_REGISTER).get(registerToDecode);
            } else {
                return Constants.errorTag + Constants.errorRegisterIsNotFoundMessage;
            }
        } else {
            return Constants.errorTag + Constants.errorOperandIsNotValidMessage;
        }
    }

    public static String iTypeAssemble(String[] instrParts, int currentInstrLine) {
        Map<String, Integer> operandDecodeOrder;

        if (!customOperands.containsKey(instrParts[0])) {
            operandDecodeOrder = new HashMap<String, Integer>() {
                {
                    put(Constants.OP_TYPE_RS, 2);
                    put(Constants.OP_TYPE_RT, 1);
                    put(Constants.OP_TYPE_IMM, 3);
                    put(Constants.OP_TYPE_LABEL, 0);
                }
            };
        } else {
            operandDecodeOrder = customOperands.get(instrParts[0]);
        }

        String bin32instr = "";
        String immediateField = "";
        String registerDecodeResult;

        // i type format: opcode (6) rs (5) rt (5) immediate (16)
        bin32instr += lookUpTable.get(Constants.TYPE_I).get(instrParts[0]); // opcode

        if (operandDecodeOrder.get(Constants.OP_TYPE_RS) != 0) {
            registerDecodeResult = registerDecode(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_RS)]); // rs
            if (!registerDecodeResult.startsWith(Constants.errorTag)) {
                bin32instr += registerDecodeResult;
            } else {
                return registerDecodeResult;
            }
        } else {
            bin32instr += "00000";
        }

        if (operandDecodeOrder.get(Constants.OP_TYPE_RT) != 0) {
            registerDecodeResult = registerDecode(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_RT)]); // rt
            if (!registerDecodeResult.startsWith(Constants.errorTag)) {
                bin32instr += registerDecodeResult;
            } else {
                return registerDecodeResult;
            }
        } else {
            bin32instr += "00000";
        }

        if (operandDecodeOrder.get(Constants.OP_TYPE_IMM) != 0) {
            if (Long.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)]) > 32767 || Long.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)]) < -32767)
                return Constants.errorTag + Constants.errorImmediateIsOutOfRangeMessage;
            
            try {
                immediateField = Integer.toBinaryString(Integer.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)])); // imm
            } catch (NumberFormatException ex) {
                return Constants.errorTag + Constants.errorImmediateFieldIsNotValidMessage;
            }
        } else if (operandDecodeOrder.get(Constants.OP_TYPE_LABEL) != 0) {
            try {
                immediateField = Integer.toBinaryString(labelIndex.get(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_LABEL)]) - currentInstrLine); // imm
            } catch (Exception ex) {
                return Constants.errorTag + Constants.errorLabelNotFoundMessage;
            }
        }

        if (immediateField.length() == 32 && operandDecodeOrder.get(Constants.OP_TYPE_IMM) != 0) {
            immediateField = immediateField.substring(16);
        }

        if (immediateField.length() > 16 && operandDecodeOrder.get(Constants.OP_TYPE_LABEL) != 0) {
            immediateField = immediateField.substring(16);
        }

        String oldString = "";
        for (int i = 0; i < 16 - immediateField.length(); i++) {
            oldString += "0";
        }
        immediateField = oldString + immediateField;

        bin32instr += immediateField;

        return bin32instr;
    }

    public static String rTypeAssemble(String[] instrParts) {
        Map<String, Integer> operandDecodeOrder;

        if (!customOperands.containsKey(instrParts[0])) {
            operandDecodeOrder = new HashMap<String, Integer>() {
                {
                    put(Constants.OP_TYPE_RS, 2);
                    put(Constants.OP_TYPE_RT, 3);
                    put(Constants.OP_TYPE_RD, 1);
                    put(Constants.OP_TYPE_IMM, 0);
                }
            };
        } else {
            operandDecodeOrder = customOperands.get(instrParts[0]);
        }

        // r type format: opcode (6) rs (5) rt (5) rd (5) shamt (5) funct (6)
        String bin32instr = "000000"; // opcode
        String registerDecodeResult;
        String shiftAmount = "00000";

        if (operandDecodeOrder.get(Constants.OP_TYPE_RS) != 0) {
            registerDecodeResult = registerDecode(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_RS)]); // rs
            if (!registerDecodeResult.startsWith(Constants.errorTag)) {
                bin32instr += registerDecodeResult;
            } else {
                return registerDecodeResult;
            }
        } else {
            bin32instr += "00000";
        }

        if (operandDecodeOrder.get(Constants.OP_TYPE_RT) != 0) {
            registerDecodeResult = registerDecode(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_RT)]); // rt
            if (!registerDecodeResult.startsWith(Constants.errorTag)) {
                bin32instr += registerDecodeResult;
            } else {
                return registerDecodeResult;
            }
        } else {
            bin32instr += "00000";
        }

        if (operandDecodeOrder.get(Constants.OP_TYPE_RD) != 0) {
            registerDecodeResult = registerDecode(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_RD)]); // rd
            if (!registerDecodeResult.startsWith(Constants.errorTag)) {
                bin32instr += registerDecodeResult;
            } else {
                return registerDecodeResult;
            }
        } else {
            bin32instr += "00000";
        }

        if (operandDecodeOrder.get(Constants.OP_TYPE_IMM) != 0) {
            if (Long.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)]) > 15 || Long.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)]) < -15)
                return Constants.errorTag + Constants.errorImmediateIsOutOfRangeMessage;
            
            try {
                shiftAmount = Integer.toBinaryString(Integer.valueOf(instrParts[operandDecodeOrder.get(Constants.OP_TYPE_IMM)])); // imm
            } catch (NumberFormatException ex) {
                return Constants.errorTag + Constants.errorImmediateFieldIsNotValidMessage;
            }
        }

        if (shiftAmount.length() == 32) {
            shiftAmount = shiftAmount.substring(27);
        }

        String oldString = "";
        for (int i = 0; i < 5 - shiftAmount.length(); i++) {
            oldString += "0";
        }
        shiftAmount = oldString + shiftAmount;

        bin32instr += shiftAmount;
        bin32instr += lookUpTable.get(Constants.TYPE_R).get(instrParts[0]); // funct

        return bin32instr;
    }

    public static List<String> findAllLabelIndexesAndAddresses(List<String> instructionsToDecode) {
        labelIndex.clear();
        labelAddress.clear();
        int instrLineCounter = 0;
        List<String> temp = new ArrayList<>();
        for (String instr : instructionsToDecode) {
            if (instr.endsWith(":")) {
                long theLabelAddress = firstMIPSMemoryLocation + ((long)4 * (long)instrLineCounter);
                labelIndex.put(instr.replace(":", ""), instrLineCounter);
                labelAddress.put(instr.replace(":", ""), theLabelAddress);
            } else if (instr.contains(":")) {
                long theLabelAddress = firstMIPSMemoryLocation + ((long)4 * (long)instrLineCounter);
                labelIndex.put(instr.substring(0, instr.indexOf(":")), instrLineCounter);
                labelAddress.put(instr.substring(0, instr.indexOf(":")), theLabelAddress);

                instrLineCounter++;
                String tempString = instr.substring(instr.indexOf(":") + 1);
                while (tempString.startsWith(" ")) {
                    tempString = tempString.substring(1);
                }
                temp.add(tempString);
            } else {
                instrLineCounter++;
                temp.add(instr);
            }
        }
        return temp;

    }

    public static String jTypeAssemble(String[] instrParts) {
        String temp = Long.toBinaryString(Long.parseLong(Long.toHexString(labelAddress.get(instrParts[1])), 16));

        String oldString = "";
        for (int i = 0; i < 32 - temp.length(); i++) {
            oldString += "0";
        }
        temp = oldString + temp;

        return lookUpTable.get(Constants.TYPE_J).get(instrParts[0])
                + temp.substring(4, 30);
    }

    public static String memoryTypeAssemble(String[] instrParts) {
        String theMemoryAndOffset = instrParts[2];
        theMemoryAndOffset = theMemoryAndOffset.replace("(", " ");
        theMemoryAndOffset = theMemoryAndOffset.replace(")", "");

        List<String> tempInstrPartsList = new LinkedList<String>(Arrays.asList(instrParts));
        tempInstrPartsList.remove(2);

        String[] theMemoryAndOffsetArray = theMemoryAndOffset.split(" ");
        tempInstrPartsList.add(theMemoryAndOffsetArray[0]);
        tempInstrPartsList.add(theMemoryAndOffsetArray[1]);

        instrParts = tempInstrPartsList.toArray(new String[tempInstrPartsList.size()]);

        String immediateField = Integer.toBinaryString(Integer.valueOf(instrParts[2]));
        String oldString = "";
        for (int i = 0; i < 16 - immediateField.length(); i++) {
            oldString += "0";
        }
        immediateField = oldString + immediateField;

        return lookUpTable.get(Constants.TYPE_MEMORY).get(instrParts[0])
                + lookUpTable.get(Constants.TYPE_REGISTER).get(instrParts[3])
                + lookUpTable.get(Constants.TYPE_REGISTER).get(instrParts[1])
                + immediateField;

    }
    
    public static void main(String[] args) {
        String answer;

        try {
            fillLookUpTable();
        } catch (FileNotFoundException ex) {
            System.err.println(Constants.lookUpTableFileName + " is not found!");
            System.exit(1);
        }
        
        try {
            Debug.createRandomSourceFile();
            isRandomlyGeneratedFileCreated = true;
        } catch (IOException e) {
        }

        while (true) {
            System.out.println("");
            System.out.println("Java MIPS Assembler");
            System.out.print("(I)nteraction mode / (B)atch mode / (E)xit: ");

            answer = consoleInput.nextLine();

            if (answer.equalsIgnoreCase("b")) {
                batchMode();
            } else if (answer.equalsIgnoreCase("i")) {
                interactiveMode();
            } else if (answer.equalsIgnoreCase("e")) {
                System.exit(0);
            } else {
                System.out.println("Wrong input!");
            }
        }
    }    

}
