package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class WhileStatm extends Statement {
    Expression  expr;
    Statement   body;

    public WhileStatm(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("while-statment");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrintLn("");
        Main.log.prettyPrint("while ");
        expr.prettyPrint();
        Main.log.prettyPrintLn(" do");

        if( body instanceof CompoundStatm ) {
            body.prettyPrint();
        } else {
            Main.log.prettyIndent();
            body.prettyPrint();
            Main.log.prettyOutdent();
        }
    }

    /**
    *   Chekcs that expr is of type boolean
    */
    @Override
    public void check(Block curScope, Library lib) {
        expr.check(curScope, lib);
        correctType(expr.type).checkType(lib.booleanType, "while-test", this,
                                                "While-test is not Boolean.");

        body.check(curScope, lib);
    }

    /* Generates code for while statm */
    @Override
    public void genCode(CodeFile f){
        String testLabel = f.getLocalLabel(),
               endLabel  = f.getLocalLabel();

        f.genInstr(testLabel, "", "", "Start while-statment");
        expr.genCode(f);
        f.genInstr("", "cmpl", "$0,%eax", "");
        f.genInstr("", "je", endLabel, "");
        body.genCode(f);
        f.genInstr("", "jmp", testLabel, "");
        f.genInstr(endLabel, "", "", "End while-statment");
    }

    public static WhileStatm parse(Scanner s) {
        enterParser("while-statment");

        WhileStatm ws = new WhileStatm( s.curLineNum() );
        s.skip(whileToken); ws.expr = Expression.parse(s);
        s.skip(doToken);    ws.body = Statement.parse(s);

        leaveParser("while-statment");
        return ws;
    }
}
