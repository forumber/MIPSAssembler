package MIPSAssembler;

public class Constants {

    public static final String TYPE_R = "r";
    public static final String TYPE_I = "i";
    public static final String TYPE_J = "j";
    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_MEMORY = "memory";

    public static final String lookUpTableFileName = "lookUpTable.txt";
    public static final long firstMIPSMemoryLocation = 0x8000100L; // given in PDF
    
    public static final String errorTag = "err: ";
    public static final String errorIsNotRegisterMessage = "Entered operand(s) are not valid!";
    public static final String errorRegisterIsNotFoundMessage = "Entered register is not found in application's register referance table!";
    public static final String errorImmediateIsOutOfRangeMessage = "Entered immediate value is out of range!";

}
