package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class Variable extends Factor {

    Expression  expr;
    String      name;
    PascalDecl  vRef;

    public Variable(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("variable " + name);
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);

        if( expr != null ) {
            Main.log.prettyPrint("[ ");
            expr.prettyPrint();
            Main.log.prettyPrint(" ]");
        }
    }

    /*
    *   Saves pointer to refrenceDecl.
    *   Saves type of refrenceDecl.
    *   If any expression, check that indexation
    *   is been done correctly.
    */
    @Override
    public void check(Block curScope, Library lib) {
        vRef = curScope.findDecl(name, this);
        type = vRef.type;

        if( expr != null ) {
            types.ArrayType at = (types.ArrayType)type;
            expr.check(curScope, lib);
            at.indexType.checkType( expr.type, "array-index", this,
            "Expression type is different to variable type!");
            type = at;
        }
    }

    @Override
    public void genCode(CodeFile f) {
        if( expr != null ) {
            types.ArrayType tmp = (types.ArrayType)vRef.type;
            expr.genCode(f);
            if( tmp.loLim > 0 )
            { f.genInstr("", "subl", "$"+tmp.loLim+",%eax", ""); }
            f.genInstr("", "movl", (-4*vRef.declLevel)+"(%ebp),%edx", "");
            f.genInstr("", "leal", vRef.declOffset+"(%edx),%edx", "");
            f.genInstr("", "movl", "(%edx,%eax,4),%eax", "");
        } else if ( vRef instanceof ConstDecl ) {
            vRef.genCode(f);
        } else {
            f.genInstr("", "movl", (-4*vRef.declLevel)+"(%ebp),%edx", "");
            f.genInstr("", "movl", vRef.declOffset+"(%edx),%eax", name);
        }
        //System.out.println(vRef.identify() + "dl:" + vRef.declLevel + " do:" + vRef.declOffset);
    }

    public static Variable parse(Scanner s) {
        enterParser("variable");

        Variable v = new Variable( s.curLineNum() );
        /* Save name if needed later */
        v.name = s.curToken.id; s.skip(nameToken);

        if(s.curToken.kind == leftBracketToken ) {
            s.skip(leftBracketToken);
            v.expr = Expression.parse(s);
            s.skip(rightBracketToken);
        }

        leaveParser("variable");
        return v;
    }

 }
