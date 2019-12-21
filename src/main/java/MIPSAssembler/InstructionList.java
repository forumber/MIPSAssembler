package MIPSAssembler;

import java.util.*;

public class InstructionList {
    
    public String instructionType;
    public Map<String, String> instruction;
    
    public InstructionList(String instructionType)
    {
        instruction = new HashMap<>();
        this.instructionType = instructionType;
    }
    
    public void add(String instructionName, String opCode)
    {
        instruction.put(instructionName, opCode);
    }

    
}
