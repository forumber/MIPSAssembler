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
    
    public boolean add(String instructionName, String opCode)
    {
        if (instruction.containsKey(instructionName) || instruction.containsValue(opCode))
            return true;
        else
        {
            instruction.put(instructionName, opCode);
            return false;
        }
    }

    
}
