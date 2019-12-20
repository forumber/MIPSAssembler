// Test at https://csfieldguide.org.nz/en/interactives/mips-assembler/

package MIPSAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MIPSAssembler {

    public static List<InstructionList> lookUpTable = new ArrayList<>();

    public static Scanner consoleInput = new Scanner(System.in);

    // TEST
    public static void printInstructionTable() {
        for (InstructionList i : lookUpTable) {
            System.out.println(i.instructionType + " type instructions;");
            i.instruction.entrySet().forEach(entry -> {
                System.out.println(entry.getKey() + " " + entry.getValue());
            });
            System.out.println("");
        }
    }

    public static void fillLookUpTable() throws FileNotFoundException {
        Scanner lookUpTableFile = new Scanner(new File(Constants.lookUpTableFileName));
        int lineCounter = 0;
        
        lookUpTable.add(new InstructionList(Constants.TYPE_I));
        lookUpTable.add(new InstructionList(Constants.TYPE_J));
        lookUpTable.add(new InstructionList(Constants.TYPE_R));
        lookUpTable.add(new InstructionList(Constants.TYPE_MEMORY));
        lookUpTable.add(new InstructionList(Constants.TYPE_REGISTER));

        while (lookUpTableFile.hasNextLine()) {
            boolean found = false;
            
            String nextLine = lookUpTableFile.nextLine();
            lineCounter++;
            
            // TODO: Change theInstruction variable name
            if (!(nextLine.startsWith("#") || nextLine.isEmpty())) {
                String[] theInstruction = nextLine.split(" ");
                
                for (InstructionList i: lookUpTable)
                    if (theInstruction[0].equals(i.instructionType))
                    {
                        found = true;
                        i.add(theInstruction[1], theInstruction[2]);
                    }
                
                if (!found)
                {
                    System.err.println("");
                    System.err.println("An error has occurred while reading " + Constants.lookUpTableFileName);
                    System.err.println("Line " + lineCounter + ": " + theInstruction[0] + " is not a valid instruction type!");
                    System.exit(1);
                }

            }
        }

        lookUpTableFile.close();
    }

    public static long assemble(String instructionToDecode) {
        String instructionToDecodeType = "";
        String instructionOpCode = "";
        
        
        String[] instrParts = instructionToDecode.split(" ");
        for(InstructionList i: lookUpTable){
            if(i.instruction.containsKey(instrParts[0]))
            {
                instructionToDecodeType = i.instructionType;
                instructionOpCode = i.instruction.get(instrParts[0]);
            }
        }
        
        if(instructionToDecodeType.isEmpty()){
            System.err.println("Instruction " + instrParts[0] + " is not a"
                    + "valid instruction!");
        }
        
        if(!instructionToDecodeType.equals(Constants.TYPE_J)){
           String[] instrOperands = instrParts[1].split(", ");
        }
        
        return Constants.firstMIPSMemoryLocation; // test
    }

    public static void batchMode() {
        System.out.println("BATCH MODE");

        String inputFileName, outputFileName;
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

        while (inputFile.hasNextLine()) {
            instructionsToAssemble.add(inputFile.nextLine());
        }

        // TEST
        while (inputFile.hasNextLine())
            try {
                outputFile.write(inputFile.nextLine());
                outputFile.write("\n");
            } catch (IOException ex) {
                System.out.println("Could not write to file " + outputFileName);
            }
        
        try {
            outputFile.close();
            System.out.println("File write operation has been completed successfully!");
        } catch (IOException ex) {
            System.out.println("Could not write to file " + outputFileName);
        }
        System.exit(0);
    }

    public static void interactiveMode() {
        System.out.println("INTERACTIVE MODE");

        while (true) {
            System.out.println("");
            System.out.print("Enter instruction (-1 to exit): ");
            String theInstructionFromTerminalInput = consoleInput.nextLine();

            if (theInstructionFromTerminalInput.equals("-1")) {
                System.exit(0);
            } else {
                System.out.println("0x" + Long.toHexString(assemble(theInstructionFromTerminalInput)));
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

        printInstructionTable();

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

}
