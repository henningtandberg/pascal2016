package parser;

import main.*;
import scanner.*;

public abstract class PascalSyntax {
    public int lineNum;

    public PascalSyntax(int n) {
    	lineNum = n;
    } /* END  Constructor */

    public boolean isInLibrary() {
    	return lineNum < 0;
    } /* END isInLibrary */

    abstract void check(Block curScope, Library lib);     //Del 3
    abstract void genCode(CodeFile f);                    //Del 4
    abstract public String identify();
    abstract void prettyPrint();                          //Del 2

    /** Make identify-methods faster to write */
    protected String identify(String identifyer) {
        if( lineNum >= 0 )
            return "<" + identifyer + ">" + " on line " + lineNum;
        else
            return "<" + identifyer + ">" + " in the library";
    }

    /**
    *   Used to check if type is of types.ArrayType.
    *   If so, the elemType of types.ArrayType
    *   is returned.
    */
    protected types.Type correctType(types.Type t) {
        if( t instanceof types.ArrayType ) {
            types.ArrayType tmp = (types.ArrayType)t;
            return tmp.elemType;
        } else {
            return t;
        }
    }

    public void error(String message) {
    	Main.error("Error at line " + lineNum + ": " + message);
    } /* END error */

    static void enterParser(String nonTerm) {
    	Main.log.enterParser(nonTerm);
    } /* END enterParser */

    static void leaveParser(String nonTerm) {
    	Main.log.leaveParser(nonTerm);
    } /* END leaveParser */
}
