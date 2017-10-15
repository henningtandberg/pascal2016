package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class IfStatm extends Statement {

    Expression  expr;
    Statement   ifStatm;
    Statement   elseStatm;

    public IfStatm(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("if-statment");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrintLn("");
        Main.log.prettyPrint("if ");
        expr.prettyPrint();
        Main.log.prettyPrintLn(" then");

        if( ifStatm instanceof CompoundStatm ) {
            ifStatm.prettyPrint();
        } else {
            Main.log.prettyIndent();
            ifStatm.prettyPrint();
            Main.log.prettyOutdent();
        }

        if( elseStatm != null ) {
            Main.log.prettyPrintLn();
            Main.log.prettyPrintLn("else");

            if( elseStatm instanceof CompoundStatm ) {
                elseStatm.prettyPrint();
            } else {
                Main.log.prettyIndent();
                elseStatm.prettyPrint();
                Main.log.prettyOutdent();
            }
        }
    }

    /*
    *   Checks that expression is of type boolean
    */
    @Override
    public void check(Block curScope, Library lib) {
        expr.check(curScope, lib);

        correctType(expr.type).checkType(lib.booleanType, "if-test", this,
                                                "If-test is not Boolean.");

        ifStatm.check(curScope, lib);
        if( elseStatm !=  null )
            elseStatm.check(curScope, lib);
    }

    /* Geneares code for if/else statm */
    @Override
    public void genCode(CodeFile f) {
        String ifLabel      = "",
               elseLabel    = "";

        // If statment
        f.genInstr("", "", "", "Start if");
        ifLabel = f.getLocalLabel();
        expr.genCode(f);
        f.genInstr("", "cmpl", "$0,%eax", "");
        f.genInstr("", "je", ifLabel, "");
        ifStatm.genCode(f);

        // Else statment
        if( elseStatm != null ) {
            elseLabel = f.getLocalLabel();
            f.genInstr("", "jmp", elseLabel, "");
            f.genInstr(ifLabel, "", "", "");
            elseStatm.genCode(f);
            f.genInstr(elseLabel, "", "", "End if/else");
        } else {
            f.genInstr(ifLabel, "", "", "End if");
        }
    }

    public static IfStatm parse(Scanner s) {
        enterParser("if-statment");

        IfStatm is = new IfStatm( s.curLineNum() );
        s.skip(ifToken);   is.expr      = Expression.parse(s);
        s.skip(thenToken); is.ifStatm   = Statement.parse(s);
        if( isElseStatm(s) ) {
            /*s.skip(semicolonToken);*/ s.skip(elseToken);
            is.elseStatm = Statement.parse(s);
        }

        leaveParser("if-statment");
        return is;
    }

    /**
        Returns true if there is a else statment.
    */
    private static boolean isElseStatm(Scanner s) {
        return (s.curToken.kind == semicolonToken &&
                s.nextToken.kind == elseToken) ||
               (s.curToken.kind == elseToken);
    }
}
