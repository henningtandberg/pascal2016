package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class PrefixOperator extends Operator {

    String opr;

    public PrefixOperator(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("prefix operator");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(" " + opr + " ");
    }

    @Override   //No need for check
    public void check(Block curScope, Library lib) {}

    /* If opr = subtrackToken and instr i generated */
    @Override
    public void genCode(CodeFile f){
        if( opr.equals("-") )
            f.genInstr("", "negl", "%eax", "");
    }

    public static PrefixOperator parse(Scanner s) {
        if(!s.curToken.kind.isPrefixOpr())
            return null;

        enterParser("prefix operator");

        PrefixOperator po = new PrefixOperator(s.curLineNum());
        if( s.curToken.kind == addToken)
            po.opr = "+";
        else
            po.opr = "-";

        s.readNextToken();
        leaveParser("prefix operator");
        return po;
    }
}
