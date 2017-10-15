package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class InnerExpr extends Factor {

    Expression expr;

    public InnerExpr(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("inner-expression");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("( ");
        expr.prettyPrint();
        Main.log.prettyPrint(" )");
    }

    @Override //Saves typ of expr
    public void check(Block curScope, Library lib) {
        expr.check(curScope, lib);
        type = expr.type;
    }

    @Override
    public void genCode(CodeFile f){
        expr.genCode(f);
    }

    public static InnerExpr parse(Scanner s) {
        enterParser("inner-expression");

        InnerExpr ie = new InnerExpr( s.curLineNum() );
        s.skip(leftParToken);
        ie.expr = Expression.parse(s);
        s.skip(rightParToken);

        leaveParser("inner-expression");
        return ie;
    }

}
