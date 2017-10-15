package parser;

import main.*;
import types.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class SimpleExpr extends PascalSyntax {

    PrefixOperator          preOpr;
    ArrayList<Term>         terms;
    ArrayList<TermOperator> termOprs;
    types.Type              type;

    public SimpleExpr(int lNum) {
        super(lNum);
        terms    = new ArrayList<>();
        termOprs = new ArrayList<>();
    }

    @Override
    public String identify() {
        return identify("simple-expression");
    }

    @Override
    public void prettyPrint() {
        if(preOpr != null)
            preOpr.prettyPrint();

        // if more then one term operator
        if(termOprs.size() > 0) {
            terms.get(0).prettyPrint();
            for(int i = 0; i < termOprs.size(); i++) {
                termOprs.get(i).prettyPrint();
                terms.get(i+1).prettyPrint();
            }
        } else {
            terms.get(0).prettyPrint();
        }
    }

    /**
    *   Does typecheks of simple expressions.
    *   Uses correctType() incase of an arrayType.
    *   Type is set to the type of expression.
    */
    @Override
    public void check(Block curScope, Library lib) {
        terms.get(0).check(curScope, lib);
        type = terms.get(0).type;

        if(termOprs.size() > 0) {
            for(int i = 0; i < termOprs.size(); i++) {
                String termImg = termOprs.get(i).img;
                terms.get(i+1).check(curScope, lib);
                correctType(type).checkType(correctType(terms.get(i+1).type),
                                                    termImg + " oprands", this,
                        "Operands to " + termImg + " are of different types!");
            }
        }
    }

    /* Generates code for terms and operators */
    @Override
    public void genCode(CodeFile f){
        terms.get(0).genCode(f);

        if(termOprs.size() > 0) {
            for(int i = 0; i < termOprs.size(); i++) {
                f.genInstr("", "pushl", "%eax", "");
                terms.get(i+1).genCode(f);
                f.genInstr("", "movl", "%eax,%ecx", "");
                f.genInstr("", "popl", "%eax", "");
                termOprs.get(i).genCode(f);
            }
        }

        if( preOpr != null ) { preOpr.genCode(f); }
    }

    public static SimpleExpr parse(Scanner s) {
        enterParser("simple-expression");

        SimpleExpr se = new SimpleExpr( s.curLineNum() );

        if( s.curToken.kind.isPrefixOpr() ) {
            se.preOpr = PrefixOperator.parse(s);

            if( isNotValue(s) )
                simpleExprError(s);
        }

        // While expression is not finished
        Boolean exprIsNotOver = true;
        while( exprIsNotOver ) {
            exprIsNotOver = false;
            se.terms.add( Term.parse(s) );

            if( s.curToken.kind.isTermOpr() ) {
                exprIsNotOver = true;
                se.termOprs.add( TermOperator.parse(s) );
            }
        } // make simpler code???

        leaveParser("simple-expression");
        return se;
    }

    /**
        Error occurs if for example double preOpr.
    */
    private static void simpleExprError(Scanner s) {
        Main.error(s.curLineNum(),
    	           "Expected a value but found a "
                   + s.curToken.kind + "!");
    }

    /**
        Checks if preOpr if is followed by value.
    */
    private static boolean isNotValue(Scanner s) {
        return !( s.curToken.kind == nameToken ||
                 s.curToken.kind == intValToken );
    }
}
