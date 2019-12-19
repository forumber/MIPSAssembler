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
public class Constants {

    public static final String TYPE_R = "r";
    public static final String TYPE_I = "i";
    public static final String TYPE_J = "j";
    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_MEMORY = "memory";

    public static final String lookUpTableFileName = "instructionTable.txt";
    public static final long firstMIPSMemoryLocation = 0x8000100eL; // given in PDF

}
