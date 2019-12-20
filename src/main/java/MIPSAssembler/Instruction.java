package MIPSAssembler;

public class Instruction {
    
    public String instructionType;
    public String instructionName;
    public long theCode;
    
    public Instruction(String instructionType, String instructionName, long theCode)
    {
        this.instructionType = instructionType;
        this.instructionName = instructionName;
        this.theCode = theCode;
    }

    
}
