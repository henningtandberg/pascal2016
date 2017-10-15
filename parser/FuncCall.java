package parser;

import main.*;
import types.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class FuncCall extends Factor {

    ArrayList<Expression> exprs;
    String                funcName;
    FuncDecl              fRef;

    public FuncCall(int lNum) {
        super(lNum);
        exprs = new ArrayList<Expression>();
    }

    @Override
    public String identify() {
        return identify("function-call");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(funcName);
        if( exprs.size() > 0 ) {
            Main.log.prettyPrint("( ");

            // If more then one expression
            if( exprs.size() > 1) {
                exprs.get(0).prettyPrint();
                for(int i = 1; i < exprs.size(); i++) {
                    Main.log.prettyPrint(", ");
                    exprs.get(i).prettyPrint();
                }
            } else {
                for(int i = 0; i < exprs.size(); i++)
                    exprs.get(i).prettyPrint();
            }

            Main.log.prettyPrint(" )");
        }
    }

    /* Generates code for params if any. Calculates offset.
    * Generates code for func call
    * reset esp if params
    */
    @Override
    public void genCode(CodeFile f) {
        int paramOffset = 0;

        f.genInstr("", "", "", "Start call " + fRef.label);
        for( int i = (exprs.size()-1); i >= 0 && exprs.size() > 0; i-- ) {
            exprs.get(i).genCode(f);
            f.genInstr("", "pushl", "%eax", "");
            paramOffset += 4;
        }

        f.genInstr("", "call", fRef.label, "");
        if( paramOffset > 0 )
        { f.genInstr("", "addl", "$"+paramOffset+",%esp", ""); }
        f.genInstr("", "", "", "End call " + fRef.label);
    }

    /**
    *   Saves pointer to refrenceDecl.
    *   Saves type of refrenceDecl.
    *   Checks that param usage is correct
    */
    @Override
    public void check(Block curScope, Library lib) {
        fRef = (FuncDecl)curScope.findDecl(funcName, this);
        type = fRef.type;

        if( fRef.pDeclList == null && exprs.size() > 0 )
            error(funcName + " does not take any parameters!");

        if( exprs.size() != fRef.pDeclList.pDeclList.size() )
            error("Worng amout of parameters!");

        for( int i = 0; i < exprs.size(); i++ ) {
            exprs.get(i).check(curScope, lib);
            exprs.get(i).type.checkType(fRef.pDeclList.pDeclList.get(i).type,
            "param #" + (i+1), this, "Param #" + (i+1) + " is of different type then decl!");
        }
    }

    public static FuncCall parse(Scanner s) {
        enterParser("function-call");

        FuncCall fc = new FuncCall( s.curLineNum() );
        fc.funcName = s.curToken.id;
        s.skip(nameToken);

        if( s.curToken.kind == leftParToken ) {
            s.skip(leftParToken);
            while( s.curToken.kind != rightParToken ) {
                if( s.curToken.kind == commaToken )
                    s.skip(commaToken);

                fc.exprs.add( Expression.parse(s) );
                //make sure to catch error in expression if
                //there is noe expression after commaToken.
            }
            s.skip(rightParToken);
        }


        leaveParser("function-call");
        return fc;
    }

}
