package main;

import java.io.*; /* PrintWriter */
import java.text.SimpleDateFormat;
import java.util.Date;

public class CodeFile {

    private String codeFileName;
    private PrintWriter code;
    private int numLabels = 0;

    /*
        Takes a file name as param and trys to open
        the file with a PrintWriter (code).
    */
    CodeFile(String fName) {
    	codeFileName = fName;
    	try {
    	    code = new PrintWriter(fName);
    	} catch (FileNotFoundException e) {
    	    Main.error("Cannot create code file " + fName + "!");
    	}
    	code.println("# Code file created by Pascal2016 compiler " +
    	    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    } /* END Constructor */

    /* Cloese the file when finished. */
    void finish() {
        code.close();
    } /* END finish */

    /* Returns identifyer for opened file. */
    public String identify() {
        return "Code file named " + codeFileName;
    } /* END identify */


    public String getLabel(String origName) {
        return origName + "_" + (++numLabels);
    } /* END getLabel */

    public String getLocalLabel() {
        return String.format(".L%04d", ++numLabels);
    } /* END getLocalLabel */

    /* Genereates a intruction. */
    public void genInstr(String lab, String instr, String arg, String comment) {
        if (lab.length() > 0)
            code.println(lab + ":");
        if ((instr+arg+comment).length() > 0) {
            code.printf("        %-7s %-23s ", instr, arg);
            if (comment.length() > 0) {
        	code.print("# " + comment);
            }
            code.println();
        }
    } /* END genInstr */
}
