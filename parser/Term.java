package parser;

import main.*;
import types.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class Term extends PascalSyntax {

    ArrayList<Factor>           factors;
    ArrayList<FactorOperator>   factOprs;
    types.Type                  type;

    public Term(int lNum) {
        super(lNum);
        factors  = new ArrayList<>();
        factOprs = new ArrayList<>();
    }

    @Override
    public String identify() {
        return identify("term");
    }

    @Override
    public void prettyPrint() {
        //If more then one factor
        if(factOprs.size() > 0) {
            factors.get(0).prettyPrint();
            for(int i = 0; i < factOprs.size(); i++) {
                factOprs.get(i).prettyPrint();
                factors.get(i+1).prettyPrint();
            }
        } else {
            factors.get(0).prettyPrint();
        }
    }

    /**
    *   Does typecheks of simple expressions.
    *   Uses correctType() incase of an arrayType.
    *   Type is set to the type of term.
    */
    @Override
    public void check(Block curScope, Library lib) {

        factors.get(0).check(curScope, lib);
        type = factors.get(0).type;

        if(factOprs.size() > 0) {
            for(int i = 0; i < factOprs.size(); i++) {
                String factImg = factOprs.get(i).img;
                factors.get(i+1).check(curScope, lib);
                correctType(type).checkType(correctType(factors.get(i+1).type),
                                                        factImg + " oprands", this,
                            "Operands to " + factImg + " are of different types!");
                type = factors.get(i+1).type;
            }
        }
    }

    /* Generates code for factors and oprators */
    @Override
    public void genCode(CodeFile f){
        factors.get(0).genCode(f);

        if( factOprs.size() > 0 ) {
            for(int i = 0; i < factOprs.size(); i++) {
                f.genInstr("", "pushl", "%eax", "");
                factors.get(i+1).genCode(f);
                f.genInstr("", "movl", "%eax,%ecx", "");
                f.genInstr("", "popl", "%eax", "");
                factOprs.get(i).genCode(f);
            }
        }
    }

    public static Term parse(Scanner s) {
        enterParser("term");

        Term t = new Term( s.curLineNum() );

        //While there are more more factors
        Boolean moreFactors = true;
        while( moreFactors ) {
            moreFactors = false;
            t.factors.add( Factor.parse(s) );
            if( s.curToken.kind.isFactorOpr() ) {
                moreFactors = true;
                t.factOprs.add( FactorOperator.parse(s) );
            }
        }

        leaveParser("term");
        return t;
    }
}
