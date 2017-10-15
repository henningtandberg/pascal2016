package parser;

import main.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

class ProcCallStatm extends Statement {

    ArrayList<Expression>   exprs;
    String                  procName;
    ProcDecl                pRef;

    public ProcCallStatm(int lNum) {
        super(lNum);
        exprs = new ArrayList<Expression>();
    }

    @Override
    public String identify() {
        return identify("procedure-call");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(procName);
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

    /*
    *   Saves pointer to refrenceDecl.
    *   Checks if write has been predifined.
    *   Checks that useage of parameters is correct.
    *   Typechecks parameters.
    */
    @Override
    public void check(Block curScope, Library lib) {
        pRef = (ProcDecl)curScope.findDecl(procName, this);
        pRef.checkWhetherProcedure(this);

        if( procName.equals("write") && !isRedefined(curScope, lib) ) {
            for( Expression e : exprs )
                e.check(curScope, lib);

            procName = "_write";
        } else if( pRef.pDeclList == null && exprs.size() > 0 ) {
            error(procName + " does not take any parameters!");
        } else if( pRef.pDeclList != null ) {

            if( exprs.size() != pRef.pDeclList.pDeclList.size() )
                error("Worng amout of parameters!");

            for( int i = 0; i < exprs.size(); i++ ) {
                exprs.get(i).check(curScope, lib);
                exprs.get(i).type.checkType(pRef.pDeclList.pDeclList.get(i).type,
                "param #" + (i+1), this, "Param #" + (i+1) + " is of different type then decl!");
            }
        }
    }

    /* If procName is _write the standard write will be used.
    * Else the custom proc is called.
    */
    @Override
    public void genCode(CodeFile f){
        int paramOffset = 0;

        if( procName.equals("_write") ) {
            f.genInstr("", "", "", "Start call std write");

            for( Expression e : exprs ) {
                e.genCode(f);

                f.genInstr("", "pushl", "%eax", "Push param");

                if( e.type instanceof types.CharType )
                        f.genInstr("", "call", "write_char", "");
                else if( e.type instanceof types.IntType )
                        f.genInstr("", "call", "write_int", "");
                else if( e.type instanceof types.BoolType )
                        f.genInstr("", "call", "write_bool", "");

                f.genInstr("", "addl", "$"+e.type.size()+",%esp", "Pop param");
            }
            f.genInstr("", "", "", "End call std write");
            return;
        }

        f.genInstr("", "", "", "Start call " + pRef.label);
        for( int i = (exprs.size()-1); i >= 0 && exprs.size() > 0; i-- ) {
            exprs.get(i).genCode(f);
            f.genInstr("", "pushl", "%eax", "");
            paramOffset += 4;
        }

        f.genInstr("", "call", pRef.label, "");
        if( paramOffset > 0 )
        { f.genInstr("", "addl", "$"+paramOffset+",%esp", ""); }
        f.genInstr("", "", "", "End call " + pRef.label);
    }

    public static ProcCallStatm parse(Scanner s) {
        enterParser("procedure-call");

        ProcCallStatm pcs = new ProcCallStatm( s.curLineNum() );
        pcs.procName = s.curToken.id;
        s.skip(nameToken);

        // If expressions
        if( s.curToken.kind == leftParToken ) {
            s.skip(leftParToken);
            while( s.curToken.kind != rightParToken ) {
                if( s.curToken.kind == commaToken )
                    s.skip(commaToken);

                pcs.exprs.add( Expression.parse(s) );
            }
            s.skip(rightParToken);
        }

        leaveParser("procedure-call");
        return pcs;
    }

    /**
    *   Checks if predifined proc "wite" has been redefined.
    */
    private boolean isRedefined(Block curScope, Library lib) {
        if( curScope == lib )
            return false;

        if( curScope.decls.get("write") != null )
            return true;
        else
            return isRedefined(curScope.outerScope, lib);

    }
}
