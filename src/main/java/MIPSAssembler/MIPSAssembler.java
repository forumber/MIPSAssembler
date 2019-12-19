package MIPSAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MIPSAssembler {

    public static List<Instruction> instructionTable = new ArrayList<>();

    public static Scanner consoleInput = new Scanner(System.in);

    // TEST
    public static void printInstructionTable() {
        for (Instruction i : instructionTable) {
            System.out.println(i.instructionType + " " + i.instructionName
                    + " " + Long.toHexString(i.theCode));
        }
    }

    public static void fillInstructionTable() throws FileNotFoundException {
        Scanner lookUpTableFile = new Scanner(new File(Constants.lookUpTableFileName));

        while (lookUpTableFile.hasNextLine()) {
            String nextLine = lookUpTableFile.nextLine();

            if (!nextLine.startsWith("#")) {
                String[] theInstruction = nextLine.split(" ");

                instructionTable.add(new Instruction(theInstruction[0],
                        theInstruction[1], Long.decode(theInstruction[2])));
            }
        }

        lookUpTableFile.close();
    }

    public static long assemble(String instructionToDecode) {
        Instruction instructionToDecodeType = null;
        
        String[] instrParts = instructionToDecode.split(" ");
        for(Instruction i: instructionTable){
            if(i.instructionName.equals(instrParts[0]))
                instructionToDecodeType = i;
        }
        if(instructionToDecodeType == null){
            System.err.println("Instruction " + instrParts[0] + " is not a"
                    + "valid instruction!");
        }
        
        if(!instructionToDecodeType.instructionType.equals(Constants.TYPE_J)){
           String[] instrOperands = instrParts[1].split(",");
           
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
                System.out.println("File not found!");
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
                System.out.println(outputFileName + "could not created!");
            }
        }

        while (inputFile.hasNextLine()) {
            instructionsToAssemble.add(inputFile.nextLine());
        }

        // TEST
        /*
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
        }*/
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
            fillInstructionTable();
        } catch (FileNotFoundException ex) {
            System.out.println(Constants.lookUpTableFileName + " is not found!");
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
