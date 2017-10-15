package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class Expression extends PascalSyntax {

    SimpleExpr  sExpr1, sExpr2;
    RelOperator relOpr;
    types.Type  type;

    public Expression(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("expression");
    }

    @Override
    public void prettyPrint() {
        sExpr1.prettyPrint();

        if( relOpr != null && sExpr2 != null ) {
            relOpr.prettyPrint();
            sExpr2.prettyPrint();
        }
    }

    /**
    *   Does typecheks of simple expressions.
    *   Uses correctType() incase of an arrayType.
    *   If nothing fails then type is set to booltype.
    */
    @Override
    public void check(Block curScope, Library lib) {
        sExpr1.check(curScope, lib);
        type = sExpr1.type;

        if( relOpr != null && sExpr2 != null ) {
            sExpr2.check(curScope, lib);
            correctType(type).checkType(correctType(sExpr2.type), relOpr.img+" operands",
            this, "Operands to " + relOpr.img + " are of different type!");
            type = lib.booleanType;
        }
    }

    @Override
    public void genCode(CodeFile f){
        sExpr1.genCode(f); //Beregn e1 med eax

        if( relOpr != null && sExpr2 != null ) {
            f.genInstr("", "pushl", "%eax", "");
            sExpr2.genCode(f);
            f.genInstr("", "popl", "%ecx", "");
            f.genInstr("", "cmpl", "%eax,%ecx", "");
            f.genInstr("", "movl", "$0,%eax", "");
            relOpr.genCode(f);
        }
    }

    public static Expression parse(Scanner s) {
        enterParser("expression");

        Expression expr = new Expression( s.curLineNum() );
        expr.sExpr1 = SimpleExpr.parse(s);

        if( s.curToken.kind.isRelOpr() ) {
            expr.relOpr = RelOperator.parse(s);
            expr.sExpr2 = SimpleExpr.parse(s);
        }

        leaveParser("expression");
        return expr;
    }

}
