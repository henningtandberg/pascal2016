package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class Constant extends PascalSyntax {
    PrefixOperator      operator;
    UnsignedConstant    uConst;
    types.Type          type;

    public Constant(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("constant");
    }

    @Override
    public void prettyPrint() {
        if(operator != null)
            operator.prettyPrint();

        uConst.prettyPrint();
    }

    /* Checks and saves type of uConst */
    @Override
    public void check(Block curScope, Library lib) {
        uConst.check(curScope, lib);
        type = uConst.type;
    }

    /*  Generates code for uConst
    *   Generates code for opr if any
    */
    @Override
    public void genCode(CodeFile f){
        uConst.genCode(f);

        if( operator != null )
            operator.genCode(f);
    }

    public static Constant parse(Scanner s) {
        enterParser("constant");

        Constant c  = new Constant(s.curLineNum());
        c.operator  = PrefixOperator.parse(s);
        c.uConst    = UnsignedConstant.parse(s);

        leaveParser("constant");
        return c;
    }

}
