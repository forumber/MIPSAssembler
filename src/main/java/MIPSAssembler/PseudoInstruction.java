package MIPSAssembler;

import java.util.*;

public class PseudoInstruction {

    public String instructionName;
    private Map<String, String> operandsAndTypes;
    private List<String> translatedOperations;

    public PseudoInstruction(String lookUpTableLine) {
        if (isItSafeToParse(lookUpTableLine))
            parsePseudoInstruction(lookUpTableLine);
        else
            throw new IllegalArgumentException("Pseudo line is not valid!");
    }
    
    private boolean isItSafeToParse(String lookUpTableLine) {
        if (!(lookUpTableLine.contains(" {") && lookUpTableLine.contains("}")))
            return false;
        
        if (!BalancedParan.areParenthesisBalanced(lookUpTableLine.toCharArray()))
            return false;
        
        if (!lookUpTableLine.endsWith("}"))
            return false;
        
        String translationPart = lookUpTableLine.substring(lookUpTableLine.indexOf("{"));
        String[] leftParts = lookUpTableLine.substring(0, lookUpTableLine.indexOf(" {")).split(" ");
        
        if (leftParts.length != 3)
            return false;
        
        return true;
    }

    public void parsePseudoInstruction(String lookUpTableLine) {
        this.translatedOperations = parseTranslatedPartFromLookUpTable(lookUpTableLine.substring(lookUpTableLine.indexOf("{")));
        this.operandsAndTypes = new HashMap<>();
        String[] linePartsWithoutTranslatedPart = lookUpTableLine.substring(0, lookUpTableLine.indexOf(" {")).split(" ");
        this.instructionName = linePartsWithoutTranslatedPart[1];
        String[] operandParts = linePartsWithoutTranslatedPart[2].split(",");

        for (String i : operandParts) {
            String tempInForEach = i;
            tempInForEach = tempInForEach.replace("(", " ");
            tempInForEach = tempInForEach.replace(")", "");
            String[] tempInForEachForMapping = tempInForEach.split(" ");
            operandsAndTypes.put(tempInForEachForMapping[1], tempInForEachForMapping[0]);
        }
    }

    private List<String> parseTranslatedPartFromLookUpTable(String translatedPart) {
        List<String> toReturn = new ArrayList<>();
        String temp = translatedPart;
        temp = temp.replace("{", "");
        temp = temp.replace("}", "");
        String[] theTranslatedInstructions = temp.split(";");

        for (String i : theTranslatedInstructions) {
            String tempInForEach = i;
            tempInForEach = tempInForEach.replace("(", "");
            tempInForEach = tempInForEach.replace(")", "");
            toReturn.add(tempInForEach);
        }

        return toReturn;
    }
    
    public static List<String> parse(PseudoInstruction p, String instructionToDecode) {
        List<String> toReturn = new ArrayList<>();
        
        instructionToDecode = instructionToDecode.replace(", ", " ");
        instructionToDecode = instructionToDecode.replace(",", " ");
        
        String[] instructionToDecodeOperands = instructionToDecode.replace(p.instructionName + " ", "").split(" ");
        
        if (instructionToDecodeOperands.length != p.operandsAndTypes.size())
            return null;
        
        List<String> availableOperandTypes = new ArrayList<>(p.operandsAndTypes.values());
        List<String> availableOperandVariables = new ArrayList<>(p.operandsAndTypes.keySet());
        
        for (int i = 0; i < p.operandsAndTypes.size(); i++) {
            String temp = instructionToDecodeOperands[i];
            try {
                if (!MIPSAssembler.lookUpTable.get(availableOperandTypes.get(i)).containsKey(temp)) {
                    return null;
                }
            } catch (Exception e) {
            }
        }
        
        Map<String, String> variables = new HashMap<>();
        
        for (int i = 0; i < availableOperandVariables.size(); i++) {
            variables.put(availableOperandVariables.get(availableOperandVariables.size() - i - 1), instructionToDecodeOperands[i]);
        }
        
        for (String s: p.translatedOperations)
        {
            String temp = s;
            String temp2 = s;
            
            temp = temp.replace(", ", " ");
            temp = temp.replace(",", " ");
            
            String[] variablesToChange = temp.split(" ");
            
            for (int i = 1; i < variablesToChange.length; i++) {
                if (variables.containsKey(variablesToChange[i])) {
                    temp2 = temp2.replace(variablesToChange[i], variables.get(variablesToChange[i]));
                }
            }
            
            toReturn.add(temp2);
        }
        
        return toReturn;
    }
}
