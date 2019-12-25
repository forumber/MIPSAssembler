package MIPSAssembler;

public class Constants {

    public static final String TYPE_R = "r";
    public static final String TYPE_I = "i";
    public static final String TYPE_J = "j";
    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_MEMORY = "memory";
    
    public static final String OP_TYPE_RS = "rs";
    public static final String OP_TYPE_RT = "rt";
    public static final String OP_TYPE_RD = "rd";
    public static final String OP_TYPE_IMM = "imm";
    public static final String OP_TYPE_LABEL = "label";

    public static final String lookUpTableFileName = "lookUpTable.txt";
    public static final long firstMIPSMemoryLocation = 0x80400024L; // QtSpim
    //public static final long firstMIPSMemoryLocation = 0x80001000L; // given in PDF
    
    public static final String errorTag = "err: ";
    public static final String errorOperandIsNotValidMessage = "Entered operand(s) are not valid!";
    public static final String errorRegisterIsNotFoundMessage = "Entered register is not found in application's register referance table!";
    public static final String errorImmediateIsOutOfRangeMessage = "Entered immediate value is out of range!";
    public static final String errorImmediateFieldIsNotValidMessage = "Entered immediate value is not valid!";
    public static final String errorLabelNotFoundMessage = "Entered label is not found in the code!";

}
