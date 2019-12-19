/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MIPSAssembler;

/**
 *
 * @author Kerem
 */
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
