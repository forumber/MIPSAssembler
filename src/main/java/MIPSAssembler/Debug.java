package MIPSAssembler;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Debug {

    private static String[] typesToBeginWith = {Constants.TYPE_I, Constants.TYPE_J, Constants.TYPE_R, Constants.TYPE_MEMORY};
    private static Random rand = new Random();
    private static String[] availableRegisters = MIPSAssembler.lookUpTable.get(Constants.TYPE_REGISTER).keySet().toArray(new String[MIPSAssembler.lookUpTable.get(Constants.TYPE_REGISTER).size()]);
    
    // It is definitely not copied directly from the internet.
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static List<String> putLabelsIn(List<String> generatedLabelsSoFar, List<String> stringsToWrite) {
        List<String> toReturn = new ArrayList<>();
        int currentLine = 0;
        int initialSize = generatedLabelsSoFar.size();
        
        while (!generatedLabelsSoFar.isEmpty()) {
            int theRandomIndex = rand.nextInt(generatedLabelsSoFar.size());
            String theLabel = generatedLabelsSoFar.get(theRandomIndex);
            generatedLabelsSoFar.remove(theRandomIndex);
            int insertLineAfter = (stringsToWrite.size() / (initialSize)); // TODO: Find a real algorithm!
            for (int i = 0; i < insertLineAfter; i++) {
                toReturn.add(stringsToWrite.get(currentLine));
                currentLine++;
            }
            toReturn.add("");
            toReturn.add(theLabel + ":");
        }

        for (int i = currentLine; i < stringsToWrite.size(); i++) {
            toReturn.add(stringsToWrite.get(currentLine));
            currentLine++;
        }

        return toReturn;

    }

    private static String generateRandomLabel(List<String> generatedLabelsSoFar) {
        while (true) {
            boolean isAlreadyExist = false;

            String theLabel = "label";
            theLabel += String.valueOf(rand.nextInt(500));
            if (generatedLabelsSoFar.isEmpty()) {
                return theLabel;
            } else {
                for (String i : generatedLabelsSoFar) {
                    if (i.equals(theLabel)) {
                        isAlreadyExist = true;
                    }
                }

                if (!isAlreadyExist) {
                    return theLabel;
                }
            }
        }

    }

    public static void createRandomSourceFile() throws IOException {
        FileWriter outputFile = new FileWriter(Constants.randomlyGeneratedFileName);

        List<String> stringsToWrite = new ArrayList<>();

        List<String> generatedLabelsSoFar = new ArrayList<>();

        int linesToWrite = rand.nextInt(500);

        for (int i = 0; i < linesToWrite; i++) {

            String theTypeToBeginWith = typesToBeginWith[rand.nextInt(typesToBeginWith.length)];
            String[] instrsToBeginWith = MIPSAssembler.lookUpTable.get(theTypeToBeginWith).keySet().toArray(new String[MIPSAssembler.lookUpTable.get(theTypeToBeginWith).size()]);
            String theInstrToBeginWith = instrsToBeginWith[rand.nextInt(instrsToBeginWith.length)];

            String builtString = theInstrToBeginWith + " ";

            switch (theTypeToBeginWith) {
                case Constants.TYPE_I:
                    if (!MIPSAssembler.customOperands.containsKey(theInstrToBeginWith)) {
                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                        builtString += ", ";
                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                        builtString += ", ";
                        builtString += String.valueOf(rand.nextInt(16000));
                    } else {
                        for (int j = 1; j <= 4; j++) {
                            try {
                                switch (getKeyByValue(MIPSAssembler.customOperands.get(theInstrToBeginWith), j)) {
                                    case Constants.OP_TYPE_RS:
                                    case Constants.OP_TYPE_RT:
                                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                                        break;
                                    case Constants.OP_TYPE_IMM:
                                        builtString += String.valueOf(rand.nextInt(16000));
                                        break;
                                    case Constants.OP_TYPE_LABEL:
                                        generatedLabelsSoFar.add(generateRandomLabel(generatedLabelsSoFar));
                                        builtString += generatedLabelsSoFar.get(generatedLabelsSoFar.size() - 1);
                                        break;
                                    default:
                                        break;
                                }
                                builtString += ", ";
                            } catch (Exception e) {
                            }
                        }
                    }   break;
                case Constants.TYPE_R:
                    if (!MIPSAssembler.customOperands.containsKey(theInstrToBeginWith)) {
                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                        builtString += ", ";
                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                        builtString += ", ";
                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                    } else {
                        for (int j = 1; j <= 4; j++) {
                            try {
                                switch (getKeyByValue(MIPSAssembler.customOperands.get(theInstrToBeginWith), j)) {
                                    case Constants.OP_TYPE_RS:
                                    case Constants.OP_TYPE_RD:
                                    case Constants.OP_TYPE_RT:
                                        builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                                        break;
                                    case Constants.OP_TYPE_IMM:
                                        builtString += String.valueOf(rand.nextInt(32));
                                        break;
                                    default:
                                        break;
                                }
                                builtString += ", ";
                            } catch (Exception e) {
                            }
                        }
                    }   break;
                case Constants.TYPE_J:
                    generatedLabelsSoFar.add(generateRandomLabel(generatedLabelsSoFar));
                    builtString += generatedLabelsSoFar.get(generatedLabelsSoFar.size() - 1);
                    break;
                case Constants.TYPE_MEMORY:
                    builtString += availableRegisters[rand.nextInt(availableRegisters.length)];
                    builtString += ", ";
                    builtString += String.valueOf(rand.nextInt(10) * 4) + "(" + availableRegisters[rand.nextInt(availableRegisters.length)] + ")";
                    break;
                default:
                    break;
            }

            while (builtString.endsWith(" ") || builtString.endsWith(",")) {
                builtString = builtString.substring(0, builtString.length() - 1);
            }
            stringsToWrite.add(builtString);
        }
        
        stringsToWrite = putLabelsIn(generatedLabelsSoFar, stringsToWrite);

        for (String theString : stringsToWrite) {
            outputFile.write(theString);
            outputFile.write("\n");
        }
        outputFile.close();
    }

}
